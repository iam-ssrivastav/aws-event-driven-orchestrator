package com.example.aws.orchestrator;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

@Component
public class InfrastructureInitializer {

    private final SqsClient sqsClient;
    private final SnsClient snsClient;
    private final S3Client s3Client;
    private final DynamoDbClient dynamoDbClient;

    public InfrastructureInitializer(SqsClient sqsClient, SnsClient snsClient, S3Client s3Client,
            DynamoDbClient dynamoDbClient) {
        this.sqsClient = sqsClient;
        this.snsClient = snsClient;
        this.s3Client = s3Client;
        this.dynamoDbClient = dynamoDbClient;
    }

    @PostConstruct
    public void init() {
        System.out.println("🛠 Initializing AWS Infrastructure...");

        try {
            // 1. SQS
            sqsClient.createQueue(CreateQueueRequest.builder().queueName("ticket-queue").build());
            System.out.println(" -> SQS Queue Created");

            // 2. SNS
            snsClient.createTopic(CreateTopicRequest.builder().name("ticket-notifications").build());
            System.out.println(" -> SNS Topic Created");

            // 3. S3
            s3Client.createBucket(CreateBucketRequest.builder().bucket("ticket-receipts").build());
            System.out.println(" -> S3 Bucket Created");

            // 4. DynamoDB
            dynamoDbClient.createTable(CreateTableRequest.builder()
                    .tableName("Tickets")
                    .keySchema(KeySchemaElement.builder().attributeName("ticketId").keyType(KeyType.HASH).build())
                    .attributeDefinitions(AttributeDefinition.builder().attributeName("ticketId")
                            .attributeType(ScalarAttributeType.S).build())
                    .provisionedThroughput(
                            ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                    .build());
            System.out.println(" -> DynamoDB Table Created");

        } catch (Exception e) {
            System.out.println("Infrastructure partially already exists or error: " + e.getMessage());
        }
    }
}
