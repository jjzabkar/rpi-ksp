package com.jjz.hellopi.service.publisher;

import com.jjz.hellopi.event.ActiveVesselEvent;
import com.jjz.hellopi.event.SpaceCenterEvent;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ActiveVesselEventPublisherService {
    private final ApplicationContext ctx;

    @Async
    @EventListener(SpaceCenterEvent.class)
    public void publishActiveVesselEvent(SpaceCenterEvent event) throws RPCException, StreamException, InterruptedException {
        try {
            Stream<SpaceCenter.Vessel> vesselStream = event.getKrpcConnection().addStream(SpaceCenter.class, "getActiveVessel");
            log.info("start Stream<Vessel> and publish ActiveVesselEvent");
            vesselStream.addCallback(vessel -> ctx.publishEvent(new ActiveVesselEvent(this, vessel, event.getKrpcConnection())));
            vesselStream.start();
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }
}
