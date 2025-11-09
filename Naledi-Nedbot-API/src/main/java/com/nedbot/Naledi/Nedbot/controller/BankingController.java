package com.nedbot.Naledi.Nedbot.controller;

import com.nedbot.Naledi.Nedbot.model.Account;
import com.nedbot.Naledi.Nedbot.model.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/banking")
public class BankingController {

    // Dummy account endpoint
    @GetMapping("/accounts/{customerNumber}")
    public ResponseEntity<List<Account>> getAccounts(@PathVariable String customerNumber) {
        List<Account> accounts = new ArrayList<>();
        
        // Simulate fetching accounts
        Account savingsAccount = new Account();
        savingsAccount.setAccountNumber("1234567890");
        savingsAccount.setAccountType("SAVINGS");
        savingsAccount.setBalance(new BigDecimal("5000.00"));
        savingsAccount.setCustomerNumber(customerNumber);
        savingsAccount.setStatus("ACTIVE");
        
        Account currentAccount = new Account();
        currentAccount.setAccountNumber("0987654321");
        currentAccount.setAccountType("CURRENT");
        currentAccount.setBalance(new BigDecimal("15000.00"));
        currentAccount.setCustomerNumber(customerNumber);
        currentAccount.setStatus("ACTIVE");
        
        accounts.add(savingsAccount);
        accounts.add(currentAccount);
        
        return ResponseEntity.ok(accounts);
    }

    // Get account balance
    @GetMapping("/accounts/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNumber) {
        // Simulate fetching balance
        BigDecimal balance = new BigDecimal("5000.00");
        return ResponseEntity.ok(balance);
    }

    // Transfer money
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(
            @RequestParam String fromAccount,
            @RequestParam String toAccount,
            @RequestParam BigDecimal amount) {
        
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAccountNumber(fromAccount);
        transaction.setType("TRANSFER");
        transaction.setAmount(amount);
        transaction.setDescription("Transfer to account " + toAccount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("COMPLETED");
        
        return ResponseEntity.ok(transaction);
    }

    // Get transaction history
    @GetMapping("/accounts/{accountNumber}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory(
            @PathVariable String accountNumber,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate) {
        
        List<Transaction> transactions = new ArrayList<>();
        
        // Simulate transaction history
        Transaction t1 = new Transaction();
        t1.setTransactionId(UUID.randomUUID().toString());
        t1.setAccountNumber(accountNumber);
        t1.setType("DEBIT");
        t1.setAmount(new BigDecimal("100.00"));
        t1.setDescription("Coffee Shop");
        t1.setTimestamp(LocalDateTime.now().minusDays(1));
        t1.setStatus("COMPLETED");
        
        Transaction t2 = new Transaction();
        t2.setTransactionId(UUID.randomUUID().toString());
        t2.setAccountNumber(accountNumber);
        t2.setType("CREDIT");
        t2.setAmount(new BigDecimal("5000.00"));
        t2.setDescription("Salary Deposit");
        t2.setTimestamp(LocalDateTime.now().minusDays(2));
        t2.setStatus("COMPLETED");
        
        transactions.add(t1);
        transactions.add(t2);
        
        return ResponseEntity.ok(transactions);
    }

    // Pay beneficiary
    @PostMapping("/payments/beneficiary")
    public ResponseEntity<Transaction> payBeneficiary(
            @RequestParam String fromAccount,
            @RequestParam String beneficiaryAccount,
            @RequestParam String beneficiaryName,
            @RequestParam BigDecimal amount,
            @RequestParam String reference) {
        
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAccountNumber(fromAccount);
        transaction.setType("PAYMENT");
        transaction.setAmount(amount);
        transaction.setDescription("Payment to " + beneficiaryName + " - " + reference);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("COMPLETED");
        
        return ResponseEntity.ok(transaction);
    }

    // Buy prepaid services (airtime, electricity, etc.)
    @PostMapping("/prepaid/{serviceType}")
    public ResponseEntity<Transaction> buyPrepaidService(
            @PathVariable String serviceType,
            @RequestParam String accountNumber,
            @RequestParam String recipientNumber,
            @RequestParam BigDecimal amount) {
        
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAccountNumber(accountNumber);
        transaction.setType("PREPAID_" + serviceType.toUpperCase());
        transaction.setAmount(amount);
        transaction.setDescription(serviceType.toUpperCase() + " purchase for " + recipientNumber);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("COMPLETED");
        
        return ResponseEntity.ok(transaction);
    }
}