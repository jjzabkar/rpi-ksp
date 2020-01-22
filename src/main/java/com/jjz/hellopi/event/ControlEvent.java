package com.jjz.hellopi.event;

import krpc.client.Connection;
import krpc.client.services.SpaceCenter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Slf4j
public class ControlEvent extends ApplicationEvent {
    @Getter
    private final SpaceCenter.Control control;
    @Getter
    private final Connection krpcConnection;

    public ControlEvent(Object source, SpaceCenter.Control control, Connection krpcConnection) {
        super(source);
        log.debug("ControlEvent: {}", control);
        this.control = control;
        this.krpcConnection = krpcConnection;
    }
}
