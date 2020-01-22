package com.jjz.rpiksp.event;

import krpc.client.Connection;
import krpc.client.services.SpaceCenter;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SpaceCenterEvent extends ApplicationEvent {
    private final SpaceCenter spaceCenter;
    private final Connection krpcConnection;

    public SpaceCenterEvent(Object source, SpaceCenter spaceCenter, Connection krpcConnection) {
        super(source);
        this.spaceCenter = spaceCenter;
        this.krpcConnection = krpcConnection;
    }
}
