package com.jjz.hellopi.service;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpaceCenterService {
    private SpaceCenter spaceCenter;

    @Autowired
    public SpaceCenterService(Connection krpcConnection) throws RPCException {
        this.spaceCenter = SpaceCenter.newInstance(krpcConnection);
    }


    @Scheduled(fixedRate = 5000L)
    public void scheduled() {
        try {
            log.info("ping for active vessel");
            SpaceCenter.Vessel vessel = spaceCenter.getActiveVessel();
            log.info("active vessel name = '{}'", vessel.getName());
        } catch (Exception e) {
            log.error(e.toString().replace('\n', ' '));
        }
    }

}
