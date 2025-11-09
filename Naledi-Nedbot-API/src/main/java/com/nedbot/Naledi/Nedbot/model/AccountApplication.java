package com.nedbot.Naledi.Nedbot.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AccountApplication {
    private String applicationId;
    private String customerNumber;
    private String applicantName;
    private String idNumber;
    private String emailAddress;
    private String mobileNumber;
    private LocalDate dateOfBirth;
    private String employmentStatus;
    private String occupation;
    private Double monthlyIncome;
    private String residentialAddress;
    private String accountType;
    private String status;
    private LocalDate applicationDate;
    private List<Document> documents;
    private List<ValidationResult> validationResults;
    private String assignedAgent;
    private String auditTrail;
}