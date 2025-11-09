package com.nedbot.Naledi.Nedbot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/keys")
@Tag(name = "API Key Management", description = "Endpoints for managing API keys")
public class ApiKeyController {

    @Operation(summary = "Generate a new API key")
    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateApiKey() {
        String apiKey = UUID.randomUUID().toString();
        Map<String, String> response = new HashMap<>();
        response.put("apiKey", apiKey);
        response.put("message", "Store this API key safely. It won't be shown again.");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verify an API key")
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verifyApiKey(
            @RequestHeader("X-API-KEY") String apiKey) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", true);
        return ResponseEntity.ok(response);
    }
}