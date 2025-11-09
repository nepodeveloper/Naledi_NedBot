package com.nedbot.Naledi.Nedbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AzureAgentService {

    private final RestTemplate restTemplate;

    @Value("${nodeServerUrl}")
    private String nodeServerUrl; // Node.js server URL

    public AzureAgentService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Send a message to the Node.js AI server and return the first assistant response as a String
     */
    public String sendMessageToAgent(String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    nodeServerUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("messages")) {
                return "No response from agent";
            }

            List<Map<String, Object>> messages = (List<Map<String, Object>>) body.get("messages");

            // Return the first assistant message, or fallback if none
            return messages.stream()
                    .filter(m -> "assistant".equals(m.get("role")))
                    .map(m -> (String) m.get("text"))
                    .findFirst()
                    .orElse("No assistant response found");

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to communicate with Node.js AI server: " + e.getMessage();
        }
    }
}
