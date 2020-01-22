package com.jjz.rpiksp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemplateConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RetryTemplate retryTemplate(
            @Value("${retry.initial-interval:30}") long initalInterval,
            @Value("${retry.max-interval:5000}") long maxInterval,
            @Value("${retry.multiplier:1.3}") double multiplier
    ) {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(initalInterval);
        policy.setMaxInterval(maxInterval);
        policy.setMultiplier(multiplier);
        retryTemplate.setBackOffPolicy(policy);
        return retryTemplate;
    }

}
