package com.jjz.rpiksp.service.publisher;

import com.jjz.rpiksp.event.KrpcConnectedEvent;
import com.jjz.rpiksp.event.SpaceCenterEvent;
import krpc.client.RPCException;
import krpc.client.StreamException;
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
public class SpaceCenterEventPublisherService {
    private final ApplicationContext ctx;

    @Async
    @EventListener(KrpcConnectedEvent.class)
    public void publishSpaceCenterEvent(KrpcConnectedEvent event) throws RPCException, StreamException {
        try {
            SpaceCenter spaceCenter = SpaceCenter.newInstance(event.getKrpcConnection());
            ctx.publishEvent(new SpaceCenterEvent(this, spaceCenter, event.getKrpcConnection()));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
