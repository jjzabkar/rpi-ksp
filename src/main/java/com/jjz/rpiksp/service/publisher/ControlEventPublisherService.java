package com.jjz.rpiksp.service.publisher;

import com.jjz.rpiksp.event.ActiveVesselEvent;
import com.jjz.rpiksp.event.ControlEvent;
import com.jjz.rpiksp.event.flight.RcsButtonEvent;
import com.jjz.rpiksp.event.flight.SasButtonEvent;
import krpc.client.Stream;
import krpc.client.services.SpaceCenter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ControlEventPublisherService {
    private final ApplicationContext ctx;

    @Async
    @EventListener(ActiveVesselEvent.class)
    public void publishControlEvent(ActiveVesselEvent event) {
        try {
            Stream<SpaceCenter.Control> controlStream = event.getKrpcConnection().addStream(event.getVessel(), "getControl");
            controlStream.addCallback(x -> ctx.publishEvent(new ControlEvent(this, x, event.getKrpcConnection())));

            SpaceCenter.Control control = controlStream.get();
            if (control != null) {
                ctx.publishEvent(new ControlEvent(this, control, event.getKrpcConnection()));

                Stream<Boolean> rcsStream = event.getKrpcConnection().addStream(control, "getRCS");
                rcsStream.addCallback(x -> ctx.publishEvent(new RcsButtonEvent(this, x)));
                log.info("start stream: RcsButtonEvent publisher");
                rcsStream.start();

                Stream<Boolean> sasStream = event.getKrpcConnection().addStream(control, "getSAS");
                sasStream.addCallback(x -> ctx.publishEvent(new SasButtonEvent(this, x)));
                log.info("start stream: SasButtonEvent publisher");
                sasStream.start();
            }

            log.info("start stream: ControlEvent publisher");
            controlStream.start();
        } catch (Exception e) {
            log.error("Unable to publish Control events: " + e.toString(), e);
        }
    }
}
