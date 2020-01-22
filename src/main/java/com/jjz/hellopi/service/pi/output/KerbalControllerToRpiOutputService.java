package com.jjz.hellopi.service.pi.output;

import com.jjz.hellopi.event.flight.RcsButtonEvent;
import com.jjz.hellopi.event.flight.SasButtonEvent;
import com.pi4j.io.gpio.*;
import krpc.client.RPCException;
import krpc.client.StreamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Responds to state changes IN KSP, not in controller
 *
 * @see {@link GpioPinDigitalOutput#setState(boolean)}
 * @see {@link GpioPinDigitalOutput#setState(PinState)}
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KerbalControllerToRpiOutputService {
    private static final Pin OUTPUT_RCS_LED_PIN = RaspiPin.GPIO_08;
    private static final Pin OUTPUT_SAS_LED_PIN = RaspiPin.GPIO_09;
    private final GpioController gpio;

    @PostConstruct
    public void postConstruct() {
        final Pin[] pins = {
                OUTPUT_RCS_LED_PIN,
                OUTPUT_SAS_LED_PIN};
        this.initializePins(pins);
    }

    private void initializePins(Pin[] pins) {
        for (Pin p : pins) {
            GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(p, "MyLED", PinState.HIGH);
            log.info("initialize output pin {} to LOW (off), but provision as HIGH", p);
            pin.setState(PinState.LOW); // initializes lights to OFF
        }
    }

    @Async
    @EventListener(RcsButtonEvent.class)
    public void onRcsButtonEvent(RcsButtonEvent event) throws RPCException, StreamException {
        log.info("set output RCS (pin {}) = {}", OUTPUT_RCS_LED_PIN, event.getRcs());
        GpioPinDigitalOutput out = (GpioPinDigitalOutput) gpio.getProvisionedPin(OUTPUT_RCS_LED_PIN);
        out.setState(event.getRcs());
    }

    @Async
    @EventListener(SasButtonEvent.class)
    public void onRcsButtonEvent(SasButtonEvent event) throws RPCException, StreamException {
        log.info("set output SAS (pin {}) = {}", OUTPUT_SAS_LED_PIN, event.getSas());
        GpioPinDigitalOutput out = (GpioPinDigitalOutput) gpio.getProvisionedPin(OUTPUT_SAS_LED_PIN);
        out.setState(event.getSas());
    }

}
