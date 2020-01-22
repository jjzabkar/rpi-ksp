package com.jjz.rpiksp.service.subscriber;

import com.jjz.rpiksp.event.ControlEvent;
import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class TelemetryService {
    private static final long NEXT_OK_MILLIS_CONST = 1000L;
    private AtomicLong nextOkMillis = new AtomicLong(0L);
    private AtomicReference<Float> yaw = new AtomicReference<Float>();
    private AtomicReference<Float> pitch = new AtomicReference<Float>();
    private AtomicReference<Float> roll = new AtomicReference<Float>();
    private AtomicLong events = new AtomicLong(0L);
    private long startMillis;
    private Flux<Float> yawFlux;
    private Flux<Float> rollFlux;
    private Flux<Float> pitchFlux;

    @Async // TODO: UNCOMMENT
    @EventListener(ControlEvent.class) // TODO: UNCOMMENT
    public void onControlEvent(ControlEvent event) {
        try {
            this.yawFlux = this.registerAndStartStream("getYaw", this.yaw, event.getKrpcConnection(), event.getControl());
            this.pitchFlux = this.registerAndStartStream("getPitch", this.pitch, event.getKrpcConnection(), event.getControl());
            this.rollFlux = this.registerAndStartStream("getRoll", this.roll, event.getKrpcConnection(), event.getControl());


//            Flux<Float> allFlux = this.yawFlux.distinctUntilChanged()
//                    .mergeWith(this.pitchFlux)
//                    .mergeWith(this.rollFlux);

            //            Stream<Float> yawStream = event.getKrpcConnection().addStream(event.getControl(), "getYaw");
//            Stream<Float> pitchStream = event.getKrpcConnection().addStream(event.getControl(), "getPitch");
//            Stream<Float> rollStream = event.getKrpcConnection().addStream(event.getControl(), "getRoll");

//            yawStream.addCallback(x -> {
//                this.yaw.set(x);
//                printTelemetry();
//            });

//            pitchStream.addCallback(x -> {
//                this.pitch.set(x);
//                printTelemetry();
//            });
            //.addCallback(x -> log.info("vessel control pitch = {}", x));

//            rollStream.addCallback(x -> {
//                this.roll.set(x);
//                printTelemetry();
//            });
            // .addCallback(x -> log.info("vessel control roll  = {}", x));

//            yawStream.start(); //works!
//            pitchStream.start(); //works!
//            rollStream.start(); //works!

            log.info("subscribed to vessel control streams. report every {}ms", NEXT_OK_MILLIS_CONST);
            startMillis = System.currentTimeMillis();

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    private Flux<Float> registerAndStartStream(String methodName, AtomicReference<Float> reference, Connection krpcConnection, SpaceCenter.Control control) throws RPCException, StreamException {
        Stream<Float> floatStream = krpcConnection.addStream(control, methodName);

//        EmitterProcessor<Float> emitterProcessor = EmitterProcessor.create();
//        Flux<Float> autoConnect = emitterProcessor.publish().autoConnect();
//        final FluxSink<Float> emitter = emitterProcessor.sink();

        final Flux flux = Flux.<Float>create(emitter2 -> {
            log.info("create stream and add callback for '{}'", methodName);
            floatStream.addCallback(x -> {
                reference.set(x);
                emitter2.next(x);
                printTelemetry();
            });
        });

//        // formerly:
//        floatStream.addCallback(x -> {
//            reference.set(x);
//            emitter.next(x);
//            printTelemetry();
//        });
        log.info("start floatstream callback '{}'", methodName);
        floatStream.start();

        log.info("subscribe to flux '{}'", methodName);
        flux.subscribe();
        return flux;
    }

    private void printTelemetry() {
        events.incrementAndGet();
        synchronized (nextOkMillis) {
            if (System.currentTimeMillis() > nextOkMillis.get()) {
                double rate = 1000.0 * ((double) events.get()) / ((double) (System.currentTimeMillis() - startMillis));
                log.info(String.format("yaw = %.3f \t pitch= %.3f \t roll = %.3f \t events=%s \t rate=%.2f/sec", this.yaw.get(), this.pitch.get(), this.roll.get(), events.get(), rate));
                final long time = Math.max(System.currentTimeMillis(), nextOkMillis.get());
                nextOkMillis.set(time + NEXT_OK_MILLIS_CONST);
            }
        }
    }
}
