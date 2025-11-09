package com.nedbot.Naledi.Nedbot.service;

import com.nedbot.Naledi.Nedbot.model.AccountApplication;
import com.nedbot.Naledi.Nedbot.model.DecisionResult;
import com.nedbot.Naledi.Nedbot.model.ValidationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AutomatedDecisionService {
    
    @Value("${app.decision.min-credit-score:650}")
    private int minimumCreditScore;
    
    @Value("${app.decision.min-income:5000}")
    private double minimumIncome;
    
    @Value("${app.decision.min-age:18}")
    private int minimumAge;
    
    @Value("${app.decision.max-age:65}")
    private int maximumAge;

    public DecisionResult evaluateApplication(AccountApplication application, List<ValidationResult> validations) {
        DecisionResult decision = new DecisionResult();
        decision.setDecisionId(UUID.randomUUID().toString());
        decision.setApplicationId(application.getApplicationId());
        decision.setDecisionDate(LocalDateTime.now());
        decision.setDecisionMaker("AUTOMATED");
        
        Map<String, Object> factors = new HashMap<>();
        boolean automaticApproval = true;
        StringBuilder reasonBuilder = new StringBuilder();
        
        // 1. Validation Results Check
        boolean hasFailedValidations = validations.stream()
                .anyMatch(v -> "FAIL".equals(v.getStatus()));
        factors.put("validationsPassed", !hasFailedValidations);
        if (hasFailedValidations) {
            automaticApproval = false;
            reasonBuilder.append("Failed validations. ");
        }
        
        // 2. Age Check
        int age = Period.between(application.getDateOfBirth(), LocalDateTime.now().toLocalDate()).getYears();
        factors.put("age", age);
        if (age < minimumAge || age > maximumAge) {
            automaticApproval = false;
            reasonBuilder.append("Age criteria not met. ");
        }
        
        // 3. Income Assessment
        factors.put("monthlyIncome", application.getMonthlyIncome());
        if (application.getMonthlyIncome() < minimumIncome) {
            automaticApproval = false;
            reasonBuilder.append("Income below minimum requirement. ");
        }
        
        // 4. Calculate Risk Score (0-100, lower is better)
        double riskScore = calculateRiskScore(application);
        decision.setRiskScore(riskScore);
        factors.put("riskScore", riskScore);
        if (riskScore > 70) {
            automaticApproval = false;
            reasonBuilder.append("High risk score. ");
        }
        
        // 5. Calculate Credit Score (300-850)
        double creditScore = calculateCreditScore(application);
        decision.setCreditScore(creditScore);
        factors.put("creditScore", creditScore);
        if (creditScore < minimumCreditScore) {
            automaticApproval = false;
            reasonBuilder.append("Credit score below minimum requirement. ");
        }

        // Store all decision factors
        decision.setDecisionFactors(factors);
        
        // Make final decision
        if (automaticApproval) {
            decision.setDecision("APPROVED");
            decision.setReason("All criteria met for automatic approval");
        } else if (riskScore > 90 || creditScore < 500) {
            decision.setDecision("REJECTED");
            decision.setReason("Application rejected: " + reasonBuilder.toString());
        } else {
            decision.setDecision("MANUAL_REVIEW");
            decision.setReason("Requires manual review: " + reasonBuilder.toString());
        }
        
        return decision;
    }
    
    private double calculateRiskScore(AccountApplication application) {
        double score = 0;
        
        // Employment status factor
        if ("UNEMPLOYED".equals(application.getEmploymentStatus())) {
            score += 40;
        } else if ("SELF_EMPLOYED".equals(application.getEmploymentStatus())) {
            score += 20;
        } else if ("EMPLOYED".equals(application.getEmploymentStatus())) {
            score += 10;
        }
        
        // Income factor (higher income = lower risk)
        if (application.getMonthlyIncome() > 50000) {
            score += 0;
        } else if (application.getMonthlyIncome() > 20000) {
            score += 10;
        } else if (application.getMonthlyIncome() > 10000) {
            score += 20;
        } else {
            score += 30;
        }
        
        // Age factor
        int age = Period.between(application.getDateOfBirth(), LocalDateTime.now().toLocalDate()).getYears();
        if (age < 25) {
            score += 20;
        } else if (age > 55) {
            score += 15;
        }
        
        return score;
    }
    
    private double calculateCreditScore(AccountApplication application) {
        // Simulated credit score calculation
        // In production, this would call a credit bureau API
        double baseScore = 650;
        
        if (application.getMonthlyIncome() > 30000) {
            baseScore += 100;
        } else if (application.getMonthlyIncome() > 15000) {
            baseScore += 50;
        }
        
        if ("EMPLOYED".equals(application.getEmploymentStatus())) {
            baseScore += 50;
        }
        
        // Cap the score at 850
        return Math.min(850, baseScore);
    }
}