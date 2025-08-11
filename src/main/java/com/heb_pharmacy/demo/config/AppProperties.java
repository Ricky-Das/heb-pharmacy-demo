package com.heb_pharmacy.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private double tooSoonPercent = 0.75;
    private boolean gcpPubSubEnabled = false;
    private String topicName = "refill-events";

    public double getTooSoonPercent() { return tooSoonPercent; }
    public void setTooSoonPercent(double v) { this.tooSoonPercent = v; }

    public boolean isGcpPubSubEnabled() { return gcpPubSubEnabled; }
    public void setGcpPubSubEnabled(boolean v) { this.gcpPubSubEnabled = v; }

    public String getTopicName() { return topicName; }
    public void setTopicName(String v) { this.topicName = v; }
} 