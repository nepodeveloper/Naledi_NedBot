package com.nedbot.Naledi.Nedbot.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Document {
    private String documentId;
    private String applicationId;
    private String documentType; // ID_DOCUMENT, PROOF_OF_RESIDENCE, BANK_STATEMENT, PAYSLIP
    private String fileName;
    private String contentType;
    private byte[] content;
    private String status; // PENDING, VERIFIED, REJECTED
    private LocalDateTime uploadDate;
    private String verificationNotes;
}