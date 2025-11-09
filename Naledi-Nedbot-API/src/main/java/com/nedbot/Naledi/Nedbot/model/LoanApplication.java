package com.nedbot.Naledi.Nedbot.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanApplication {
    private String applicationId;
    
    @NotBlank(message = "Customer number is required")
    private String customerNumber;
    
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000.0", message = "Minimum loan amount is R1000")
    private BigDecimal amount;
    
    @NotNull(message = "Loan term is required")
    private Integer termMonths;
    
    @NotBlank(message = "Loan type is required")
    private String loanType; // PERSONAL, HOME, VEHICLE
    
    private String status;
    private LocalDate applicationDate;
    private BigDecimal interestRate;
    private BigDecimal monthlyPayment;
}