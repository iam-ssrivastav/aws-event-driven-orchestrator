package com.example.aws.orchestrator;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateId;
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private boolean processed;
    private LocalDateTime createdAt;

    public OutboxEvent() {
    }

    public OutboxEvent(String aggregateId, String eventType, String payload, boolean processed,
            LocalDateTime createdAt) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.processed = processed;
        this.createdAt = createdAt;
    }

    // Standard Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Static Builder equivalent
    public static OutboxEventBuilder builder() {
        return new OutboxEventBuilder();
    }

    public static class OutboxEventBuilder {
        private String aggregateId;
        private String eventType;
        private String payload;
        private boolean processed;
        private LocalDateTime createdAt;

        public OutboxEventBuilder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public OutboxEventBuilder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public OutboxEventBuilder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public OutboxEventBuilder processed(boolean processed) {
            this.processed = processed;
            return this;
        }

        public OutboxEventBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OutboxEvent build() {
            return new OutboxEvent(aggregateId, eventType, payload, processed, createdAt);
        }
    }
}
