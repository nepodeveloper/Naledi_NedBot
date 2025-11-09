package com.nedbot.Naledi.Nedbot.controller;

import com.nedbot.Naledi.Nedbot.model.Card;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/banking/cards")
@Tag(name = "Card Management", description = "Endpoints for managing bank cards")
public class CardController {

    @Operation(summary = "Request a new card")
    @PostMapping("/request")
    public ResponseEntity<Card> requestCard(@Valid @RequestBody Card card) {
        // Simulate card creation
        card.setCardId(UUID.randomUUID().toString());
        card.setStatus("PENDING_ACTIVATION");
        card.setExpiryDate(LocalDate.now().plusYears(5));
        card.setContactless(true);
        card.setBlocked(false);
        return ResponseEntity.ok(card);
    }

    @Operation(summary = "Get all cards for an account")
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Card>> getAccountCards(@PathVariable String accountNumber) {
        List<Card> cards = new ArrayList<>();
        
        // Simulate fetching cards
        Card debitCard = new Card();
        debitCard.setCardId(UUID.randomUUID().toString());
        debitCard.setAccountNumber(accountNumber);
        debitCard.setCardType("DEBIT");
        debitCard.setLastFourDigits("1234");
        debitCard.setStatus("ACTIVE");
        debitCard.setExpiryDate(LocalDate.now().plusYears(4));
        cards.add(debitCard);
        
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Block a card")
    @PostMapping("/{cardId}/block")
    public ResponseEntity<Card> blockCard(
            @PathVariable String cardId,
            @RequestParam(required = false) String reason) {
        
        // Simulate blocking card
        Card card = new Card();
        card.setCardId(cardId);
        card.setStatus("BLOCKED");
        card.setBlocked(true);
        return ResponseEntity.ok(card);
    }

    @Operation(summary = "Request card replacement")
    @PostMapping("/{cardId}/replace")
    public ResponseEntity<Card> requestReplacement(
            @PathVariable String cardId,
            @RequestParam String reason) {
        
        // Simulate card replacement
        Card newCard = new Card();
        newCard.setCardId(UUID.randomUUID().toString());
        newCard.setStatus("PENDING_ACTIVATION");
        newCard.setExpiryDate(LocalDate.now().plusYears(5));
        newCard.setReplacementReason(reason);
        return ResponseEntity.ok(newCard);
    }

    @Operation(summary = "Activate a card")
    @PostMapping("/{cardId}/activate")
    public ResponseEntity<Card> activateCard(@PathVariable String cardId) {
        // Simulate card activation
        Card card = new Card();
        card.setCardId(cardId);
        card.setStatus("ACTIVE");
        card.setBlocked(false);
        return ResponseEntity.ok(card);
    }
}