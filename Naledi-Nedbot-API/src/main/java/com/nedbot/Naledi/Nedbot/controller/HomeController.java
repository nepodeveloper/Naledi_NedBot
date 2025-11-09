package com.nedbot.Naledi.Nedbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "running");
        response.put("message", "Nedbot API is running!");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Business Verification", "/api/business");
        endpoints.put("Account Opening", "/api/onboarding/applications");
        endpoints.put("Loan Application", "/api/banking/loans/apply");
        endpoints.put("Card Request", "/api/banking/cards/request");
        endpoints.put("Get Accounts", "/api/banking/accounts/{customerNumber}");
        endpoints.put("Check Balance", "/api/banking/accounts/{accountNumber}/balance");
        endpoints.put("Transfer Money", "/api/banking/transfer");
        endpoints.put("Transaction History", "/api/banking/accounts/{accountNumber}/transactions");
        endpoints.put("Pay Beneficiary", "/api/banking/payments/beneficiary");
        endpoints.put("Buy Prepaid Services", "/api/banking/prepaid/{serviceType}");
        
        response.put("available_endpoints", endpoints);
        
        return ResponseEntity.ok(response);
    }
}