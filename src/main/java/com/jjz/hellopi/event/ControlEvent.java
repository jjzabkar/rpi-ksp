package com.jjz.hellopi.event;

import krpc.client.Connection;
import krpc.client.services.SpaceCenter;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Created by jjzabkar on 2019-12-29.
 */
public class ControlEvent extends ApplicationEvent {
    @Getter
    private final SpaceCenter.Control control;
    @Getter
    private final Connection krpcConnection;

    public ControlEvent(Object source, SpaceCenter.Control control, Connection krpcConnection) {
        super(source);
        this.control = control;
        this.krpcConnection = krpcConnection;
    }
}
