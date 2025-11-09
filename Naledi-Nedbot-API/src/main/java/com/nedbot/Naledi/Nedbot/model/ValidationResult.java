package com.nedbot.Naledi.Nedbot.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ValidationResult {
    private String validationId;
    private String applicationId;
    private String field;
    private String status; // PASS, FAIL, WARNING
    private String message;
    private LocalDateTime validationDate;
    private String validationType; // DOCUMENT, IDENTITY, ADDRESS, INCOME, FRAUD
}