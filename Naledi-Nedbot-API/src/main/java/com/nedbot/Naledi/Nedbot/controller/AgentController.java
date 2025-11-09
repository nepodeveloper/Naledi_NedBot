package com.nedbot.Naledi.Nedbot.controller;

import com.nedbot.Naledi.Nedbot.model.BusinessRequest;
import com.nedbot.Naledi.Nedbot.model.BusinessResponse;
//import com.nedbot.Naledi.Nedbot.service.AgentVerificationService;
import com.nedbot.Naledi.Nedbot.service.AzureAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AgentController {

    @Autowired
    private AzureAgentService azureAgentService;

    @PostMapping("/agentMessage")
    public ResponseEntity<Map<String, Object>> agentMessage(@RequestBody String requestBody) {
        String message = requestBody;
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing 'message' in request body"));
        }

        String assistantResponse = azureAgentService.sendMessageToAgent(message);

        return ResponseEntity.ok(Map.of("assistant", assistantResponse));
    }


}