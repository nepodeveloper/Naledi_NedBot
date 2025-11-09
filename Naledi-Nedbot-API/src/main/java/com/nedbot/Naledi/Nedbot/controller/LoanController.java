package com.nedbot.Naledi.Nedbot.controller;

import com.nedbot.Naledi.Nedbot.model.LoanApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/banking/loans")
@Tag(name = "Loan Management", description = "Endpoints for managing loan applications and status")
public class LoanController {

    @Operation(summary = "Apply for a new loan")
    @PostMapping("/apply")
    public ResponseEntity<LoanApplication> applyForLoan(@Valid @RequestBody LoanApplication application) {
        // Simulate loan processing
        application.setApplicationId(UUID.randomUUID().toString());
        application.setApplicationDate(LocalDate.now());
        application.setStatus("UNDER_REVIEW");
        application.setInterestRate(new BigDecimal("12.5"));
        
        // Calculate monthly payment (simplified)
        BigDecimal monthlyRate = application.getInterestRate().divide(new BigDecimal("1200"));
        BigDecimal term = new BigDecimal(application.getTermMonths());
        BigDecimal monthlyPayment = application.getAmount()
                .multiply(monthlyRate.multiply(BigDecimal.ONE.add(monthlyRate).pow(application.getTermMonths())))
                .divide(BigDecimal.ONE.add(monthlyRate).pow(application.getTermMonths()).subtract(BigDecimal.ONE), 2);
        
        application.setMonthlyPayment(monthlyPayment);
        
        return ResponseEntity.ok(application);
    }

    @Operation(summary = "Get loan application status")
    @GetMapping("/{applicationId}")
    public ResponseEntity<LoanApplication> getLoanStatus(@PathVariable String applicationId) {
        // Simulate fetching loan application
        LoanApplication application = new LoanApplication();
        application.setApplicationId(applicationId);
        application.setStatus("APPROVED");
        application.setApplicationDate(LocalDate.now().minusDays(5));
        return ResponseEntity.ok(application);
    }

    @Operation(summary = "Get all loan applications for a customer")
    @GetMapping("/customer/{customerNumber}")
    public ResponseEntity<List<LoanApplication>> getCustomerLoans(@PathVariable String customerNumber) {
        List<LoanApplication> loans = new ArrayList<>();
        
        LoanApplication personalLoan = new LoanApplication();
        personalLoan.setApplicationId(UUID.randomUUID().toString());
        personalLoan.setCustomerNumber(customerNumber);
        personalLoan.setLoanType("PERSONAL");
        personalLoan.setAmount(new BigDecimal("50000.00"));
        personalLoan.setStatus("ACTIVE");
        loans.add(personalLoan);
        
        return ResponseEntity.ok(loans);
    }

    @Operation(summary = "Calculate loan repayment schedule")
    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateLoan(
            @RequestParam BigDecimal amount,
            @RequestParam Integer termMonths,
            @RequestParam(defaultValue = "12.5") BigDecimal interestRate) {
        
        Map<String, Object> calculation = new HashMap<>();
        
        // Simple loan calculation
        BigDecimal monthlyRate = interestRate.divide(new BigDecimal("1200"));
        BigDecimal monthlyPayment = amount
                .multiply(monthlyRate.multiply(BigDecimal.ONE.add(monthlyRate).pow(termMonths)))
                .divide(BigDecimal.ONE.add(monthlyRate).pow(termMonths).subtract(BigDecimal.ONE), 2);
        
        calculation.put("loanAmount", amount);
        calculation.put("termMonths", termMonths);
        calculation.put("interestRate", interestRate);
        calculation.put("monthlyPayment", monthlyPayment);
        calculation.put("totalPayment", monthlyPayment.multiply(new BigDecimal(termMonths)));
        
        return ResponseEntity.ok(calculation);
    }
}