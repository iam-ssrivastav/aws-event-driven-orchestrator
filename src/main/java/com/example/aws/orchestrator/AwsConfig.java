package com.example.aws.orchestrator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;

import java.net.URI;

@Configuration
public class AwsConfig {

    private static final URI LOCALSTACK_URI = URI.create("http://localhost:4566");
    private static final Region REGION = Region.US_EAST_1;
    private static final StaticCredentialsProvider CREDENTIALS = StaticCredentialsProvider.create(
            AwsBasicCredentials.create("fake", "fake"));

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .endpointOverride(LOCALSTACK_URI)
                .region(REGION)
                .credentialsProvider(CREDENTIALS)
                .build();
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .endpointOverride(LOCALSTACK_URI)
                .region(REGION)
                .credentialsProvider(CREDENTIALS)
                .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(LOCALSTACK_URI)
                .region(REGION)
                .credentialsProvider(CREDENTIALS)
                .forcePathStyle(true)
                .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(LOCALSTACK_URI)
                .region(REGION)
                .credentialsProvider(CREDENTIALS)
                .build();
    }

    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()
                .endpointOverride(LOCALSTACK_URI)
                .region(REGION)
                .credentialsProvider(CREDENTIALS)
                .build();
    }

    @Bean
    public CloudWatchClient cloudWatchClient() {
        return CloudWatchClient.builder()
                .endpointOverride(LOCALSTACK_URI)
                .region(REGION)
                .credentialsProvider(CREDENTIALS)
                .build();
    }

    @Bean
    public software.amazon.awssdk.services.lambda.LambdaClient lambdaClient() {
        return software.amazon.awssdk.services.lambda.LambdaClient.builder()
                .endpointOverride(LOCALSTACK_URI)
                .region(REGION)
                .credentialsProvider(CREDENTIALS)
                .build();
    }
}
