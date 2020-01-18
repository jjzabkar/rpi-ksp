package com.jjz.hellopi.service.pi;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.system.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Slf4j
public class ControlGpioExample2 {

    public static final int SLEEP_MILLIS = 1000;
    public static final int DEBOUNCE_MILLIS = 20;

    private final Pin[] OUTPUT_PINS = {
            // See: https://pinout.xyz/pinout/wiringpi
            RaspiPin.GPIO_08, // ACTUAL button1 @pin 3 (expected GPIO2)
            RaspiPin.GPIO_09, // ACTUAL button2 @pin5 (expected GPIO3)
            RaspiPin.GPIO_07, // ACTUAL red LED @pin7 (expected GPIO4)
    };

    private final Pin[] INPUT_PINS = {
            // See: https://pinout.xyz/pinout/wiringpi
            RaspiPin.GPIO_15, // ACTUAL pin 8
            RaspiPin.GPIO_16, // ACTUAL pin 10
    };
    private GpioController gpio;

    @PostConstruct
    public void postConstruct() throws InterruptedException {
        try {
            log.info("<--Pi4J--> GPIO Control Example ... started.");
            this.gpio = GpioFactory.getInstance();
            log.info("boardType = {}", SystemInfo.getBoardType());
            provisionOutputPins(gpio);
            provisionInputPins(gpio, PinPullResistance.PULL_UP);
            this.addListener(gpio, RaspiPin.GPIO_15, RaspiPin.GPIO_08);
            this.addListener(gpio, RaspiPin.GPIO_16, RaspiPin.GPIO_09);

            for (Pin p : INPUT_PINS) {
                GpioPin pp = gpio.getProvisionedPin(p);
                final GpioPinListener listener = (GpioPinListenerDigital) event
                        -> log.info("event on pin {}: {}. Edge={}.", p, event.getState(), event.getEdge());
                pp.addListener(listener);
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    @PreDestroy
    public void unprovisionInputPins() {
        log.warn("unprovision {} pins", INPUT_PINS.length);
        for (Pin p : INPUT_PINS) {
            GpioPin pp = gpio.getProvisionedPin(p);
            log.info("remove all listeners on pin {}", p);
            pp.removeAllListeners();
        }
    }

    private void provisionInputPins(GpioController gpio, PinPullResistance defaultState) {
        for (Pin p : INPUT_PINS) {
            log.info("provision pin {} to {} with {}ms debounce", p, defaultState, DEBOUNCE_MILLIS);
            try {
                GpioPinDigitalInput input = gpio.provisionDigitalInputPin(p, defaultState);
                input.setDebounce(DEBOUNCE_MILLIS);
            } catch (Exception e) {
                log.error("provision in error: {}: {}", p, e.toString());
            }
        }
    }

    private void provisionOutputPins(GpioController gpio) {
        for (Pin p : OUTPUT_PINS) {
            try {
                GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(p, "MyLED", PinState.HIGH);
                log.info("initialize output pin {} to LOW (off), but provision as HIGH", p);
                pin.setState(PinState.LOW); // initializes lights to OFF
            } catch (Exception e) {
                log.error("provision out error {}: {}", p, e.toString());
            }
        }
    }

    private void addListener(final GpioController gpio, final Pin inputPin, final Pin outputPin) {
        GpioPin pp = gpio.getProvisionedPin(inputPin);
        log.info("add listener to pin {}", inputPin);
        final GpioPinListener listener = new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(final GpioPinDigitalStateChangeEvent event) {
                log.info("event on pin {}: {}. Edge={}. Pin to set={}", inputPin, event.getState(), event.getEdge(), outputPin);
                GpioPinDigitalOutput out = (GpioPinDigitalOutput) gpio.getProvisionedPin(outputPin);
                out.setState(event.getState());
            }
        };
        pp.addListener(listener);
    }

}
