package com.jjz.hellopi.event.flight;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Slf4j
public class RcsButtonEvent extends ApplicationEvent {
    @Getter
    private final Boolean rcs;

    public RcsButtonEvent(Object source, Boolean rcs) {
        super(source);
        this.rcs = rcs;
        log.info("rcs toggled: {}", rcs);
    }
}
