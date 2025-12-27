package com.example.aws.orchestrator;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final OrchestratorService orchestratorService;

    public TicketController(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping
    public Map<String, String> bookTicket(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String eventName = request.get("eventName");

        String ticketId = orchestratorService.processTicket(userId, eventName);

        return Map.of(
                "status", "Success",
                "message", "Ticket orchestrated across AWS services",
                "ticketId", ticketId);
    }
}
