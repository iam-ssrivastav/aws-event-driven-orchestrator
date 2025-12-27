package com.example.aws.orchestrator;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import jakarta.transaction.Transactional;
import java.util.List;

@Component
public class OutboxPoller {

    private final OutboxRepository outboxRepository;
    private final SqsClient sqsClient;
    private final SnsClient snsClient;
    private final S3Client s3Client;

    public OutboxPoller(OutboxRepository outboxRepository, SqsClient sqsClient, SnsClient snsClient,
            S3Client s3Client) {
        this.outboxRepository = outboxRepository;
        this.sqsClient = sqsClient;
        this.snsClient = snsClient;
        this.s3Client = s3Client;
    }

    @Scheduled(fixedDelay = 5000) // Poll every 5 seconds
    @Transactional
    public void pollAndPublish() {
        List<OutboxEvent> events = outboxRepository.findByProcessedFalse();
        if (events.isEmpty())
            return;

        System.out.println("📦 Poller: Found " + events.size() + " events to publish...");

        for (OutboxEvent event : events) {
            try {
                publishToAws(event);
                event.setProcessed(true);
                outboxRepository.save(event);
                System.out.println(" ✅ Published & Marked Processed: " + event.getAggregateId());
            } catch (Exception e) {
                System.err.println(" ❌ Failed to publish event " + event.getId() + ": " + e.getMessage());
            }
        }
    }

    private void publishToAws(OutboxEvent event) {
        String ticketId = event.getAggregateId();

        // 1. Publish to SQS
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl("http://localhost:4566/000000000000/ticket-queue")
                .messageBody("Process Ticket: " + ticketId)
                .build());

        // 2. Publish to SNS
        snsClient.publish(PublishRequest.builder()
                .topicArn("arn:aws:sns:us-east-1:000000000000:ticket-notifications")
                .message("Event Published via Outbox Polling: " + event.getPayload())
                .build());

        // 3. Archive to S3
        s3Client.putObject(PutObjectRequest.builder()
                .bucket("ticket-receipts")
                .key("receipts/" + ticketId + ".txt")
                .build(), RequestBody.fromString("Receipt for " + ticketId + "\nPayload: " + event.getPayload()));
    }
}
