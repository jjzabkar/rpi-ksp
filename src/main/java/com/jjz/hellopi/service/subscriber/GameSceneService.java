package com.jjz.hellopi.service.subscriber;

import com.jjz.hellopi.event.GameSceneEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GameSceneService {

    @Async
    @EventListener(GameSceneEvent.class)
    public void onGameSceneEvent(GameSceneEvent event) {
        log.info("scene = {}", event.getGameScene());
    }

}
