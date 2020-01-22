package com.jjz.rpiksp.event.flight;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Slf4j
public class SasButtonEvent extends ApplicationEvent {
    @Getter
    private final Boolean sas;

    public SasButtonEvent(Object source, Boolean sas) {
        super(source);
        this.sas = sas;
        log.info("sas toggled: {}", sas);
    }
}
