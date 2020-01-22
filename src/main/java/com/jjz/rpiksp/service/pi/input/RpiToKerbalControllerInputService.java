package com.jjz.rpiksp.service.pi.input;

import com.jjz.rpiksp.event.ActiveVesselEvent;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.pi4j.io.gpio.PinEdge.FALLING;

/**
 * Adds listeners to pins/inputs on the Raspberry Pi Controller.
 *
 * @see {@link GpioPin#addListener(GpioPinListener...)}
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RpiToKerbalControllerInputService {
    public static final int DEBOUNCE_MILLIS = 20;
    public static final PinPullResistance DEFAULT_INPUT_PIN_STATE = PinPullResistance.PULL_UP;
    public static final Pin INPUT_RCS_BUTTON_PIN = RaspiPin.GPIO_15;
    public static final Pin INPUT_SAS_BUTTON_PIN = RaspiPin.GPIO_16;
    private final GpioController gpio;
    private SpaceCenter.Control control;

    @Async
    @EventListener(ActiveVesselEvent.class)
    public void onActiveVesselEvent(ActiveVesselEvent event) throws RPCException {
        log.info("onActiveVesselEvent: set SpaceCenter.Control");
        this.control = event.getVessel().getControl();
    }

    @PostConstruct
    public void postConstruct() {
        log.info("add listener to RCS input at pin {}", INPUT_RCS_BUTTON_PIN);
        idempotentProvisionInputPin(INPUT_RCS_BUTTON_PIN)
                .addListener((GpioPinListenerDigital) event -> toggleRcsState(event));
        log.info("add listener to SAS input at pin {}", INPUT_SAS_BUTTON_PIN);
        idempotentProvisionInputPin(INPUT_SAS_BUTTON_PIN)
                .addListener((GpioPinListenerDigital) event -> toggleSasState(event));
    }

    private void toggleSasState(GpioPinDigitalStateChangeEvent event) {
        try {
            if (event.getEdge().equals(FALLING)) {
                log.info("toggleSasState from event.state={} event.edge={}", event.getState(), event.getEdge());
                control.setSAS(!control.getSAS());
            }
        } catch (NullPointerException e) {
            log.warn("Unable to set input SAS: SpaceCenter.Control not initialized");
        } catch (Exception e) {
            log.error("Unable to set input SAS: {}", e.toString(), e);
        }
    }


    public void toggleRcsState(final GpioPinDigitalStateChangeEvent event) {
        try {
            if (event.getEdge().equals(FALLING)) {
                log.info("toggleRcsState from event.state={} event.edge={}", event.getState(), event.getEdge());
                boolean newState = !control.getRCS();
                control.setRCS(newState);
            }
        } catch (NullPointerException e) {
            log.warn("Unable to set input RCS: SpaceCenter.Control not initialized");
        } catch (Exception e) {
            log.error("Unable to set input RCS: {}", e.toString(), e);
        }
    }

    private GpioPin idempotentProvisionInputPin(Pin inputPin) {
        GpioPin result;
        try {
            GpioPinDigitalInput input = gpio.provisionDigitalInputPin(inputPin, DEFAULT_INPUT_PIN_STATE);
            input.setDebounce(DEBOUNCE_MILLIS);
            result = input;
        } catch (Exception e) {
            log.warn("provision in error: {}: {}", inputPin, e.toString());
            result = gpio.getProvisionedPin(inputPin);
        }
        return result;
    }

}
