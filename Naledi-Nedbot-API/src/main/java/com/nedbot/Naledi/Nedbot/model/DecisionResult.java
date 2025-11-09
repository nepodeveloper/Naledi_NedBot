package com.nedbot.Naledi.Nedbot.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class DecisionResult {
    private String decisionId;
    private String applicationId;
    private String decision; // APPROVED, REJECTED, MANUAL_REVIEW
    private String reason;
    private Double riskScore;
    private Double creditScore;
    private Map<String, Object> decisionFactors;
    private LocalDateTime decisionDate;
    private String decisionMaker; // AUTOMATED or agent ID
}