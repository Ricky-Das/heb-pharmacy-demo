package com.heb_pharmacy.demo.service;

import com.heb_pharmacy.demo.domain.Outbox;
import com.heb_pharmacy.demo.domain.repo.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@EnableScheduling
public class OutboxDrainer {
    private static final Logger log = LoggerFactory.getLogger(OutboxDrainer.class);
    private final OutboxRepository repo;

    public OutboxDrainer(OutboxRepository repo) { this.repo = repo; }

    @Scheduled(fixedDelay = 5000)
    public void drain() {
        // naive: publish all rows with null published_at
        List<Outbox> pending = repo.findAll().stream().filter(o -> o.getPublishedAt() == null).toList();
        for (Outbox o : pending) {
            log.info("Publishing event {} for {}#{} payload={}", o.getEventType(), o.getAggregateType(), o.getAggregateId(), o.getPayloadJson());
            o.setPublishedAt(OffsetDateTime.now());
            repo.save(o);
        }
    }
} 