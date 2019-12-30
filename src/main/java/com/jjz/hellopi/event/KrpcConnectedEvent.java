package com.jjz.hellopi.event;

import krpc.client.Connection;
import krpc.client.services.KRPC;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class KrpcConnectedEvent extends ApplicationEvent {
    private final KRPC krpc;
    private final Connection krpcConnection;

    public KrpcConnectedEvent(Object source, Connection krpcConnection, KRPC krpc) {
        super(source);
        this.krpcConnection = krpcConnection;
        this.krpc = krpc;
    }
}
