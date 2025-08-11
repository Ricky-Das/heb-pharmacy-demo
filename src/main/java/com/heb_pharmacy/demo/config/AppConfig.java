package com.heb_pharmacy.demo.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@ConfigurationPropertiesScan(basePackages = "com.heb_pharmacy.demo")
public class AppConfig {
    @Bean
    public Clock clock() { return Clock.systemUTC(); }
} 