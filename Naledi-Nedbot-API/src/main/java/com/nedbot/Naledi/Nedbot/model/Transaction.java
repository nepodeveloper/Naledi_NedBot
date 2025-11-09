package com.nedbot.Naledi.Nedbot.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private String transactionId;
    private String accountNumber;
    private String type;  // DEBIT, CREDIT
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private String status;
}