package com.example.aws.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("\n🚀 AWS Event-Driven Orchestrator is running!");
        System.out.println("API Entry Point -> http://localhost:8080/api/tickets");
    }
}
