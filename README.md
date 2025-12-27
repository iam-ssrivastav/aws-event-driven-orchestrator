# AWS Event-Driven Cloud Orchestrator

This project is a high-performance orchestration engine built using **Spring Boot** and the **AWS Java SDK (v2)**. It implements an asynchronous, decoupled architecture using the "Core Nine" AWS services, providing a production-grade blueprint for cloud-native applications.

## 🏛 Architecture
The system uses an **Event-Driven Resilience** pattern with a **Transactional Outbox**:
1. **API Gateway** acts as the secured entry point.
2. **Lambda** performs initial serverless validation.
3. **Transactional Outbox**: Business logic (DynamoDB) and Events (Postgres) are saved in a single atomic transaction.
4. **Polling Publisher**: A background worker polls the outbox every 5 seconds and publishes confirmed events to AWS.
5. **SQS** buffers requests to handle traffic spikes.
6. **SNS** fan-out triggers multi-service actions.
7. **S3** archives transaction artifacts (e-receipts).
8. **CloudWatch** provides 360-degree observability.
9. **Secrets Manager** secures automated credential rotation.

### 📊 System Flow Diagram
```mermaid
graph TD
    User((User)) -->|POST /tickets| API[API Gateway]
    API -->|Route| SB[Spring Boot Service - EC2]
    
    subgraph "Atomic Transaction"
        SB -->|Save Read Model| DDB[(DynamoDB)]
        SB -->|Save Event| RDS[(PostgreSQL - RDS Outbox)]
    end
    
    subgraph "Reliable Publisher"
        Poller[Outbox Poller] -->|Read Unprocessed| RDS
        Poller -->|Publish| SQS[SQS Queue]
        Poller -->|Broadcast| SNS[SNS Topic]
        Poller -->|Archive| S3[S3 Bucket]
        Poller -->|Trigger| L[Lambda]
        Poller -->|Update Status| RDS
    end
    
    classDef aws fill:#ff9900,stroke:#232f3e,stroke-width:2px,color:white;
    classDef compute fill:#3b48cc,stroke:#232f3e,stroke-width:2px,color:white;
    class API,SQS,SNS,S3,DDB,L aws;
    class SB,Poller compute;
```

## 💎 Resume Highlights
- **Distributed Async Processing**: Leveraged SQS as a durable buffer between microservices to prevent data loss.
- **Serverless Integration**: Integrated AWS Lambda for event-triggered validation and lightweight compute tasks.
- **High-Scale Storage**: Designed optimized NoSQL data models in DynamoDB for rapid state transitions.
- **DevSecOps**: Standardized credential security using AWS Secrets Manager and fine-grained IAM resource policies.

## 🛠 Local Setup
The entire stack is verified locally using **Localstack**.

1. **Start Infrastructure**:
   ```bash
   docker-compose up -d
   ```

2. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```

---
**Designed by Shivam Srivastav**
