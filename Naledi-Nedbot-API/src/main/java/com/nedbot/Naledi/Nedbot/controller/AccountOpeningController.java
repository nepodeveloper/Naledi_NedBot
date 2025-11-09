package com.nedbot.Naledi.Nedbot.controller;

import com.nedbot.Naledi.Nedbot.model.AccountApplication;
import com.nedbot.Naledi.Nedbot.model.DecisionResult;
import com.nedbot.Naledi.Nedbot.model.Document;
import com.nedbot.Naledi.Nedbot.model.ValidationResult;
import com.nedbot.Naledi.Nedbot.service.AccountOpeningService;
import com.nedbot.Naledi.Nedbot.service.AutomatedDecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/onboarding")
@Tag(name = "Account Opening", description = "Endpoints for new account applications")
public class AccountOpeningController {

    @Autowired
    private AccountOpeningService accountOpeningService;

    @Autowired
    private AutomatedDecisionService automatedDecisionService;

    @Operation(summary = "Start new account application")
    @PostMapping("/applications")
    public ResponseEntity<AccountApplication> startApplication(@RequestBody AccountApplication application) {
        application.setApplicationId(UUID.randomUUID().toString());
        application.setStatus("INITIATED");
        application.setApplicationDate(LocalDateTime.now().toLocalDate());
        application.setDocuments(new ArrayList<>());
        application.setValidationResults(new ArrayList<>());
        
        accountOpeningService.updateAuditTrail(application, "Application initiated");
        
        return ResponseEntity.ok(application);
    }

    @Operation(summary = "Get required documents")
    @GetMapping("/required-documents")
    public ResponseEntity<List<String>> getRequiredDocuments(
            @RequestParam(defaultValue = "CURRENT") String accountType) {
        return ResponseEntity.ok(accountOpeningService.getRequiredDocuments(accountType));
    }

    @Operation(summary = "Upload document")
    @PostMapping(value = "/applications/{applicationId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> uploadDocument(
            @PathVariable String applicationId,
            @RequestParam String documentType,
            @RequestParam MultipartFile file) throws IOException {
        
        Document document = new Document();
        document.setDocumentId(UUID.randomUUID().toString());
        document.setApplicationId(applicationId);
        document.setDocumentType(documentType);
        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setContent(file.getBytes());
        document.setStatus("PENDING");
        document.setUploadDate(LocalDateTime.now());
        
        // Validate document
        ValidationResult validationResult = accountOpeningService.validateDocument(document);
        
        // Update document status based on validation
        document.setStatus(validationResult.getStatus());
        document.setVerificationNotes(validationResult.getMessage());
        
        return ResponseEntity.ok(document);
    }

    @Operation(summary = "Validate application")
    @PostMapping("/applications/{applicationId}/validate")
    public ResponseEntity<Map<String, Object>> validateApplication(
            @PathVariable String applicationId,
            @RequestBody AccountApplication application) {
        
        List<ValidationResult> validationResults = accountOpeningService.validateApplication(application);
        
        // Determine overall status
        boolean isValid = validationResults.stream()
                .noneMatch(result -> "FAIL".equals(result.getStatus()));
        
        Map<String, Object> response = new HashMap<>();
        response.put("applicationId", applicationId);
        response.put("validationResults", validationResults);
        response.put("isValid", isValid);
        
        // Update audit trail
        accountOpeningService.updateAuditTrail(application, 
            "Application validated - Status: " + (isValid ? "PASS" : "FAIL"));
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Submit application for processing")
    @PostMapping("/applications/{applicationId}/submit")
    public ResponseEntity<Map<String, Object>> submitApplication(
            @PathVariable String applicationId,
            @RequestBody AccountApplication application) {
        
        List<ValidationResult> validationResults = accountOpeningService.validateApplication(application);
        boolean isValid = validationResults.stream()
                .noneMatch(result -> "FAIL".equals(result.getStatus()));
        
        if (!isValid) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Application validation failed");
            errorResponse.put("validationResults", validationResults);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Make automated decision
        DecisionResult decisionResult = automatedDecisionService.evaluateApplication(application, validationResults);
        
        // Update application status based on decision
        switch (decisionResult.getDecision()) {
            case "APPROVED":
                application.setStatus("APPROVED");
                accountOpeningService.updateAuditTrail(application, 
                    "Application automatically approved - Risk Score: " + decisionResult.getRiskScore() + 
                    ", Credit Score: " + decisionResult.getCreditScore());
                break;
            case "REJECTED":
                application.setStatus("REJECTED");
                accountOpeningService.updateAuditTrail(application, 
                    "Application automatically rejected - Reason: " + decisionResult.getReason());
                return ResponseEntity.ok(Map.of(
                    "applicationId", applicationId,
                    "status", "REJECTED",
                    "reason", decisionResult.getReason(),
                    "decisionFactors", decisionResult.getDecisionFactors()
                ));
            case "MANUAL_REVIEW":
                application.setStatus("PENDING_REVIEW");
                application.setAssignedAgent("PENDING_ASSIGNMENT");
                accountOpeningService.updateAuditTrail(application, 
                    "Application flagged for manual review - Reason: " + decisionResult.getReason());
                break;
        }

        String onboardingEmail = accountOpeningService.generateOnboardingEmail(application);
        
        Map<String, Object> response = new HashMap<>();
        response.put("applicationId", applicationId);
        response.put("status", application.getStatus());
        response.put("decisionResult", decisionResult);
        response.put("onboardingEmail", onboardingEmail);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check application status")
    @GetMapping("/applications/{applicationId}/status")
    public ResponseEntity<Map<String, Object>> checkStatus(@PathVariable String applicationId) {
        String status = accountOpeningService.getApplicationStatus(applicationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("applicationId", applicationId);
        response.put("status", status);
        
        return ResponseEntity.ok(response);
    }
}