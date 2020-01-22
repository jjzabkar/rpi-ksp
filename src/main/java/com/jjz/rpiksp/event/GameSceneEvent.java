package com.jjz.rpiksp.event;

import krpc.client.Connection;
import krpc.client.services.KRPC;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GameSceneEvent extends ApplicationEvent {
    private final KRPC.GameScene gameScene;
    private final Connection krpcConnection;

    public GameSceneEvent(Object source, KRPC.GameScene gameScene, Connection krpcConnection) {
        super(source);
        this.gameScene = gameScene;
        this.krpcConnection = krpcConnection;
    }
}
