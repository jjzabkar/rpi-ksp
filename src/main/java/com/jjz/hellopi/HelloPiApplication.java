package com.jjz.hellopi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableRetry
@EnableScheduling
@Slf4j
public class HelloPiApplication {
	public static final String PI4J_DEBUG = "pi4j.debug";
	public static final String PI4J_LINKING = "pi4j.linking";

	public static void main(String[] args) {
		log.info("set pi4j properties: {} {}", PI4J_DEBUG, PI4J_LINKING);

		System.setProperty(PI4J_DEBUG, "");
		// see https://raspberrypi.stackexchange.com/questions/57501/wiringpi-error-etates-im-not-using-an-rpi
		// see https://github.com/Pi4J/pi4j/issues/319
		System.setProperty(PI4J_LINKING, "dynamic");

		SpringApplication.run(HelloPiApplication.class, args);
	}
}
