package com.jjz.hellopi.service.pi;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.system.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * This example code demonstrates how to perform simple state
 * control of a GPIO pin on the Raspberry Pi.
 *
 * @author Robert Savage
 */
//@Component
@Slf4j
public class ControlGpioExample {

    public static final int SLEEP_MILLIS = 1000;
    private static final Pin OUTPUT_PIN = RaspiPin.GPIO_15;
    GpioPinDigitalOutput pin;
    Pin[] OUTPUT_PINS = {
//            RaspiPin.GPIO_02, // NOT actual gpio2/3/4 nor pins 3/5/7
//            RaspiPin.GPIO_03, // NOT actual gpio2/3/4 nor pins 3/5/7
//            RaspiPin.GPIO_04, // NOT actual gpio2/3/4 nor pins 3/5/7

            RaspiPin.GPIO_08, // ACTUAL button1 @pin 3 (expected GPIO2)
//            RaspiPin.GPIO_10, // NOT actual gpio2/3/4 nor pins 3/5/7
//            RaspiPin.GPIO_14, // NOT actual gpio2/3/4 nor pins 3/5/7
//            RaspiPin.GPIO_15, // NOT actual gpio2/3/4 nor pins 3/5/7
//            RaspiPin.GPIO_01,
//            RaspiPin.GPIO_05,
//            RaspiPin.GPIO_06,
            RaspiPin.GPIO_07, // ACTUAL red LED @pin7 (expected GPIO4)
            RaspiPin.GPIO_09, // ACTUAL button2 @pin5 (expected GPIO3)
//            RaspiPin.GPIO_11,
//            RaspiPin.GPIO_12,
//            RaspiPin.GPIO_13,
//            RaspiPin.GPIO_16,
//            RaspiPin.GPIO_17,
//            RaspiPin.GPIO_18,
//            RaspiPin.GPIO_19,
//            RaspiPin.GPIO_20,
//            RaspiPin.GPIO_21,
//            RaspiPin.GPIO_22,
//            RaspiPin.GPIO_23,
//            RaspiPin.GPIO_24,
//            RaspiPin.GPIO_25,
//            RaspiPin.GPIO_26,
//            RaspiPin.GPIO_27,
//            RaspiPin.GPIO_28,
//            RaspiPin.GPIO_29,
//            RaspiPin.GPIO_30,
//            RaspiPin.GPIO_31
    };

    Pin[] INPUT_PINS = {
            RaspiPin.GPIO_02, // NOT actual gpio2/3/4 nor pins 3/5/7
            RaspiPin.GPIO_03, // NOT actual gpio2/3/4 nor pins 3/5/7
            RaspiPin.GPIO_04, // NOT actual gpio2/3/4 nor pins 3/5/7
            RaspiPin.GPIO_10, // NOT actual gpio2/3/4 nor pins 3/5/7
            RaspiPin.GPIO_14, // NOT actual gpio2/3/4 nor pins 3/5/7
            RaspiPin.GPIO_15, // NOT actual gpio2/3/4 nor pins 3/5/7
            RaspiPin.GPIO_01,
            RaspiPin.GPIO_05,
            RaspiPin.GPIO_06,
            RaspiPin.GPIO_11,
            RaspiPin.GPIO_12,
            RaspiPin.GPIO_13,
            RaspiPin.GPIO_16,
            RaspiPin.GPIO_17,
            RaspiPin.GPIO_18,
            RaspiPin.GPIO_19,
            RaspiPin.GPIO_20,
            RaspiPin.GPIO_21,
            RaspiPin.GPIO_22,
            RaspiPin.GPIO_23,
            RaspiPin.GPIO_24,
            RaspiPin.GPIO_25,
            RaspiPin.GPIO_26,
            RaspiPin.GPIO_27,
            RaspiPin.GPIO_28,
            RaspiPin.GPIO_29,
            RaspiPin.GPIO_30,
            RaspiPin.GPIO_31
    };


    @Scheduled(fixedDelay = 10_000L)
    public void postConstruct() throws InterruptedException {
        try {
            log.info("<--Pi4J--> GPIO Control Example ... started.");

            // create gpio controller
            final GpioController gpio = GpioFactory.getInstance();

            log.info("boardType = {}", SystemInfo.getBoardType());


            provisionOutputPins(gpio);
//            testOutputPins(gpio);
            PinPullResistance[] states = {PinPullResistance.PULL_UP, PinPullResistance.PULL_DOWN};

            for (PinPullResistance state : states) {
                provisionInputPins(gpio, state);

                for (Pin p : INPUT_PINS) {
                    log.info("add listeners to input pin {}", p);
                    GpioPin pp = gpio.getProvisionedPin(p);
                    final GpioPinListener listener = new GpioPinListenerDigital() {
                        @Override
                        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                            log.info("event on pin {}: {}. Edge={}", p, event.getState(), event.getEdge());
//                        for (Pin p : OUTPUT_PINS) {
//                            // provision gpio pin as an output pin and turn on
////                pin = gpio.provisionDigitalOutputPin(p, "MyLED", PinState.HIGH);
//                            GpioPinDigitalOutput out = (GpioPinDigitalOutput) gpio.getProvisionedPin(p);
//                            log.info("set pin {} state to {}", p, event.getState());
//                            out.setState(event.getState());
//                        }
                        }
                    };
                    pp.addListener(listener);
//                Thread.sleep(SLEEP_MILLIS * 4L);
//                log.info("remove all listeners on pin {}", p);
//                pp.removeAllListeners();
                }


                log.info("sleep for 5s");
                Thread.sleep(5000);

                unprovisionInputPins(gpio);
            }

//            log.info("Exiting ControlGpioExample");
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    private void unprovisionInputPins(GpioController gpio) {
        for (Pin p : INPUT_PINS) {
            GpioPin pp = gpio.getProvisionedPin(p);
            log.info("remove all listeners on pin {}", p);
            pp.removeAllListeners();
        }
    }

    private void provisionInputPins(GpioController gpio, PinPullResistance defaultState) {
        for (Pin p : INPUT_PINS) {
            log.info("provision pin {} to {}", p, defaultState);
            try {
                GpioPinDigitalInput input = gpio.provisionDigitalInputPin(p, defaultState);
                input.setDebounce(20);
            } catch (Exception e) {
                log.error("provision in error: {}: {}", p, e.toString());
            }
        }
    }

    private void provisionOutputPins(GpioController gpio) {
        for (Pin p : OUTPUT_PINS) {
            try {
                pin = gpio.provisionDigitalOutputPin(p, "MyLED", PinState.HIGH);
            } catch (Exception e) {
                log.error("provision out error {}: {}", p, e.toString());
            }
        }
    }

    private void testOutputPins(GpioController gpio) {
        for (Pin p : OUTPUT_PINS) {
            // provision gpio pin as an output pin and turn on
//                pin = gpio.provisionDigitalOutputPin(p, "MyLED", PinState.HIGH);
            try {
                pin = (GpioPinDigitalOutput) gpio.getProvisionedPin(p);

                // set shutdown state for this pin
                // pin.setShutdownOptions(true, PinState.LOW);

                log.info("--> GPIO state for pin {} should be: hi", p);

                Thread.sleep(SLEEP_MILLIS);

                // turn off gpio pin
                pin.low();
                log.info("--> GPIO state for pin {} should be: low", p);

                Thread.sleep(SLEEP_MILLIS);

                // toggle the current state of gpio pin #01 (should turn on)
                pin.toggle();
                log.info("--> GPIO state should be: toggled for pin {}", p);

                Thread.sleep(SLEEP_MILLIS);

                // toggle the current state of gpio pin #01  (should turn off)
                pin.toggle();
                log.info("--> GPIO state should be: toggled for pin {}", p);

                Thread.sleep(SLEEP_MILLIS);

                // turn on gpio pin #01 for 1 second and then off
                log.info("--> GPIO state should be: ON for only {} second", SLEEP_MILLIS / 1000L);
                pin.pulse(SLEEP_MILLIS, true); // set second argument to 'true' use a blocking call

                // stop all GPIO activity/threads by shutting down the GPIO controller
                // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
//            gpio.shutdown();
            } catch (Exception e) {
                log.error("error at pin {}: {}", p, e.toString());
            }
        }

    }
}
//END SNIPPET: control-gpio-snippet