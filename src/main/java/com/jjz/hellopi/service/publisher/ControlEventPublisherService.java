package com.jjz.hellopi.service.publisher;

import com.jjz.hellopi.event.ActiveVesselEvent;
import com.jjz.hellopi.event.ControlEvent;
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
//            SpaceCenter.ReferenceFrame refFrame = event.getVessel().getSurfaceReferenceFrame();
//            SpaceCenter.Flight flight = event.getVessel().flight(refFrame);
//            Stream<Double> meanAltitudeStream = event.getKrpcConnection().addStream(flight, "getMeanAltitude");
//            meanAltitudeStream.addCallback(x -> log.info("flight meanAltitude =    {}m", x));
//            meanAltitudeStream.start();

            Stream<SpaceCenter.Control> controlStream = event.getKrpcConnection().addStream(event.getVessel(), "getControl");
            controlStream.addCallback(x -> ctx.publishEvent(new ControlEvent(this, x, event.getKrpcConnection())));
            SpaceCenter.Control control = controlStream.get();
            if (control != null) {
                ctx.publishEvent(new ControlEvent(this, control, event.getKrpcConnection()));
            }

//            Stream<Double> apoapsisAltitudeStream = event.getKrpcConnection().addStream(event.getVessel().getOrbit(), "getApoapsisAltitude");
//            apoapsisAltitudeStream.addCallback(x -> log.info("orbit apoapsisAltitude = {}m", x));
//            apoapsisAltitudeStream.start();

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }
}
