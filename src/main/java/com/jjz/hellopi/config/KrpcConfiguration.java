package com.jjz.hellopi.config;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.KRPC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Configuration
@Slf4j
public class KrpcConfiguration {

    @Bean
    public Connection krpcConnection(
            @Value("${krpc.name}") String name,
            @Value("${krpc.dns}") String dns,
            @Value("${krpc.rpc-port}") int rpcPort,
            @Value("${krpc.stream-port}") int streamPort
    ) throws IOException, RPCException {
        log.info("connnect to kRPC '{}' on {}:{} (stream={})...", name, dns, rpcPort, streamPort);
        Connection connection = Connection.newInstance(name, dns, rpcPort, streamPort);
        KRPC krpc = KRPC.newInstance(connection);
        log.info("Connected to kRPC version " + krpc.getStatus().getVersion());
        return connection;
    }
}
