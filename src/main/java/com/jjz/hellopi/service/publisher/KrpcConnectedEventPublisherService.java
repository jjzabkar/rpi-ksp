package com.jjz.hellopi.service.publisher;

import com.jjz.hellopi.event.KrpcConnectedEvent;
import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.KRPC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@Slf4j
public class KrpcConnectedEventPublisherService {
    @Value("${krpc.name}")
    private String name;
    @Value("${krpc.dns}")
    private String dns;
    @Value("${krpc.rpc-port}")
    private int rpcPort;
    @Value("${krpc.http-test-port}")
    private int httpTestPort;
    @Value("${krpc.stream-port}")
    private int streamPort;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ApplicationContext ctx;

    private Connection krpcConnection;
    private boolean isConnected = false;
    private KRPC krpc;

    @Scheduled(fixedDelay = 5000L)
    public void pollToPublishKrpcConnectedEvent() {
        try {
            if (isConnected == false) {
                this.publishKrpcConnectedEvent();
            }
        } catch (Exception e) {
            this.isConnected = false;
            log.warn(e.toString());
        }
    }

    @Retryable
    public void publishKrpcConnectedEvent() {
        log.info("publishKrpcConnectedEvent");
        try {
            createConnection();
            this.isConnected = true;
            ctx.publishEvent(new KrpcConnectedEvent(this, this.krpcConnection, this.krpc));
        } catch (Exception e) {
            log.warn(e.toString());
            this.isConnected = false;
            throw new RuntimeException(e);
        }
        if (this.isConnected == false || this.krpcConnection == null) {
            this.isConnected = false;
            throw new RuntimeException("unable to connect to KSP");
        }
    }

    private Connection createConnection() throws IOException, RPCException, URISyntaxException {
        try {
            log.info("connect to http {}:{}", dns, httpTestPort);
            String result = this.restTemplate.getForObject(new URI("http://" + dns + ":" + httpTestPort + "/test.html"), String.class);
            log.info("http test = '{}'", result);
        } catch (Exception e) {
            log.warn("unable to connect to test.html: {}", e.toString());
        }

        log.info("connect to kRPC '{}' on {}:{} (stream={})...", name, dns, rpcPort, streamPort);
        long startTime = System.currentTimeMillis();
        Connection connection = Connection.newInstance(name, dns, rpcPort, streamPort);
        long connectionTime = System.currentTimeMillis() - startTime;
        KRPC krpc = KRPC.newInstance(connection);
        long krpcInstantiationTime = System.currentTimeMillis() - connectionTime;
        log.info("Connected to kRPC version={} clientId={}. connectionTime={}ms, krpcInstantiationTime={}ms", krpc.getStatus().getVersion(), krpc.getClientID(), connectionTime, krpcInstantiationTime);

        for (krpc.schema.KRPC.Service service : krpc.getServices().getServicesList()) {
            log.info("\t service: '{}'", service.getName());
        }

        this.krpcConnection = connection;
        this.krpc = krpc;

        return connection;
    }
}
