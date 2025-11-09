package com.nedbot.Naledi.Nedbot.service;

import com.nedbot.Naledi.Nedbot.model.AccountApplication;
import com.nedbot.Naledi.Nedbot.model.Document;
import com.nedbot.Naledi.Nedbot.model.ValidationResult;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccountOpeningService {
    
    public List<String> getRequiredDocuments(String applicationType) {
        List<String> documents = new ArrayList<>();
        documents.add("ID_DOCUMENT");
        documents.add("PROOF_OF_RESIDENCE");
        documents.add("BANK_STATEMENT");
        documents.add("PAYSLIP");
        return documents;
    }
    
    public ValidationResult validateDocument(Document document) {
        ValidationResult result = new ValidationResult();
        result.setValidationId(UUID.randomUUID().toString());
        result.setApplicationId(document.getApplicationId());
        result.setValidationType("DOCUMENT");
        result.setValidationDate(LocalDateTime.now());
        
        // Simulate document validation
        if (document.getContent() == null || document.getContent().length == 0) {
            result.setStatus("FAIL");
            result.setMessage("Document is empty");
            return result;
        }
        
        // Simulate content validation based on document type
        switch (document.getDocumentType()) {
            case "ID_DOCUMENT":
                result.setStatus("PASS");
                result.setMessage("ID document verified successfully");
                break;
            case "PROOF_OF_RESIDENCE":
                result.setStatus("PASS");
                result.setMessage("Address verified successfully");
                break;
            default:
                result.setStatus("WARNING");
                result.setMessage("Document requires manual verification");
        }
        
        return result;
    }
    
    public List<ValidationResult> validateApplication(AccountApplication application) {
        List<ValidationResult> results = new ArrayList<>();
        
        // Validate ID Number
        ValidationResult idValidation = new ValidationResult();
        idValidation.setValidationId(UUID.randomUUID().toString());
        idValidation.setApplicationId(application.getApplicationId());
        idValidation.setField("idNumber");
        idValidation.setValidationType("IDENTITY");
        idValidation.setValidationDate(LocalDateTime.now());
        
        if (application.getIdNumber() != null && application.getIdNumber().length() == 13) {
            idValidation.setStatus("PASS");
            idValidation.setMessage("ID number format is valid");
        } else {
            idValidation.setStatus("FAIL");
            idValidation.setMessage("Invalid ID number format");
        }
        results.add(idValidation);
        
        // Validate Age
        ValidationResult ageValidation = new ValidationResult();
        ageValidation.setValidationId(UUID.randomUUID().toString());
        ageValidation.setApplicationId(application.getApplicationId());
        ageValidation.setField("dateOfBirth");
        ageValidation.setValidationType("IDENTITY");
        ageValidation.setValidationDate(LocalDateTime.now());
        
        if (application.getDateOfBirth() != null && 
            application.getDateOfBirth().plusYears(18).isBefore(LocalDate.now())) {
            ageValidation.setStatus("PASS");
            ageValidation.setMessage("Applicant is over 18");
        } else {
            ageValidation.setStatus("FAIL");
            ageValidation.setMessage("Applicant must be over 18");
        }
        results.add(ageValidation);
        
        // Add more validations as needed
        return results;
    }
    
    public String generateOnboardingEmail(AccountApplication application) {
        StringBuilder email = new StringBuilder();
        email.append("Dear ").append(application.getApplicantName()).append(",\n\n");
        email.append("Welcome to NedBot Banking! Your account application has been processed successfully.\n\n");
        email.append("Application Details:\n");
        email.append("Application ID: ").append(application.getApplicationId()).append("\n");
        email.append("Account Type: ").append(application.getAccountType()).append("\n\n");
        email.append("Next Steps:\n");
        email.append("1. Visit your nearest branch with your original ID document\n");
        email.append("2. Complete biometric verification\n");
        email.append("3. Receive your debit card\n\n");
        email.append("If you have any questions, please contact us at support@nedbot.com\n\n");
        email.append("Best regards,\nThe NedBot Team");
        
        return email.toString();
    }
    
    public String getApplicationStatus(String applicationId) {
        // Simulate status check
        return "IN_PROGRESS";
    }
    
    public void updateAuditTrail(AccountApplication application, String action) {
        String auditEntry = LocalDateTime.now() + " - " + action + "\n";
        String currentAudit = application.getAuditTrail();
        application.setAuditTrail(currentAudit == null ? auditEntry : currentAudit + auditEntry);
    }
}