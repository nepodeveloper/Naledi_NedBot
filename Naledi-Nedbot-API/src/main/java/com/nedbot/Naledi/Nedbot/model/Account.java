package com.nedbot.Naledi.Nedbot.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Account {
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String customerNumber;
    private String status;
}