package com.jjz.rpiksp.service.publisher;

import com.jjz.rpiksp.event.GameSceneEvent;
import com.jjz.rpiksp.event.KrpcConnectedEvent;
import krpc.client.Stream;
import krpc.client.services.KRPC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameSceneEventPublisherService {
    private final ApplicationContext ctx;

    @Async
    @EventListener(KrpcConnectedEvent.class)
    public void publishGameSceneEvent(KrpcConnectedEvent event) {
        try {
            Stream<KRPC.GameScene> currentGameSceneStream = event.getKrpcConnection().addStream(KRPC.class, "getCurrentGameScene");
            currentGameSceneStream.addCallback(x -> ctx.publishEvent(new GameSceneEvent(this, x, event.getKrpcConnection())));
            log.info("start stream: GameSceneEvent publisher");
            currentGameSceneStream.start();
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }
}
