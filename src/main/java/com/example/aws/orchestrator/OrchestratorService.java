package com.example.aws.orchestrator;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class OrchestratorService {

    private final DynamoDbClient dynamoDbClient;
    private final OutboxRepository outboxRepository;

    public OrchestratorService(DynamoDbClient dynamoDbClient, OutboxRepository outboxRepository) {
        this.dynamoDbClient = dynamoDbClient;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public String processTicket(String userId, String eventName) {
        String ticketId = UUID.randomUUID().toString();

        System.out.println("Starting Orchestration for Ticket: " + ticketId);

        // 1. DYNAMODB: Save Read Model State (Reserved)
        Map<String, AttributeValue> item = Map.of(
                "ticketId", AttributeValue.builder().s(ticketId).build(),
                "userId", AttributeValue.builder().s(userId).build(),
                "event", AttributeValue.builder().s(eventName).build(),
                "status", AttributeValue.builder().s("RESERVED").build());
        dynamoDbClient.putItem(PutItemRequest.builder().tableName("Tickets").item(item).build());
        System.out.println(" -> [DynamoDB] State Saved (Reserved)");

        // 2. OUTBOX: Save event to PostgreSQL (Atomic with business logic)
        String payload = String.format("{\"ticketId\":\"%s\", \"userId\":\"%s\", \"event\":\"%s\"}", ticketId, userId,
                eventName);
        OutboxEvent event = OutboxEvent.builder()
                .aggregateId(ticketId)
                .eventType("TICKET_CREATED")
                .payload(payload)
                .processed(false)
                .createdAt(LocalDateTime.now())
                .build();

        outboxRepository.save(event);
        System.out.println(" -> [PostgreSQL] Event saved to Outbox. Background poller will publish to AWS.");

        return ticketId;
    }
}
