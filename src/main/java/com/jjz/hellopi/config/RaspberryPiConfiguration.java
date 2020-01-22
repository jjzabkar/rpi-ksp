package com.jjz.hellopi.config;

import com.jjz.hellopi.service.pi.MockGpioController;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.system.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class RaspberryPiConfiguration {

    @Bean
    @ConditionalOnExpression("${pi.enabled:true}")
    public GpioController gpioController() throws IOException, InterruptedException {
        log.info("<--Pi4J--> GPIO Control Example ... started.");
        GpioController result = GpioFactory.getInstance();
        log.info("boardType = {}", SystemInfo.getBoardType());
        return result;
    }

    @Bean
    @ConditionalOnExpression("!${pi.enabled:true}")
    public GpioController mockGpioController() {
        log.info("<--Pi4J--> GPIO Control Example ... MOCKITO-MOCKED.");
        return new MockGpioController();
    }

}
