package com.nedbot.Naledi.Nedbot.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Card {
    private String cardId;
    
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    @NotBlank(message = "Card type is required")
    private String cardType; // DEBIT, CREDIT

    @Pattern(regexp = "\\d{4}", message = "Last 4 digits should be exactly 4 numbers")
    private String lastFourDigits;
    
    private String status;
    private LocalDate expiryDate;
    private boolean contactless;
    private boolean blocked;
    private String replacementReason;
}