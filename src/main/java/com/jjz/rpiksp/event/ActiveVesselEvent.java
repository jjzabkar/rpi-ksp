package com.jjz.rpiksp.event;

import krpc.client.Connection;
import krpc.client.services.SpaceCenter;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ActiveVesselEvent extends ApplicationEvent {
    @Getter
    private final SpaceCenter.Vessel vessel;
    @Getter
    private final Connection krpcConnection;

    public ActiveVesselEvent(Object source, SpaceCenter.Vessel vessel, Connection krpcConnection) {
        super(source);
        this.vessel = vessel;
        this.krpcConnection = krpcConnection;
    }
}
