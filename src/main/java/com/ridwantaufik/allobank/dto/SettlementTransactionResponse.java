package com.ridwantaufik.allobank.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SettlementTransactionResponse(
        UUID fromParticipantId,
        String fromParticipantName,
        UUID toParticipantId,
        String toParticipantName,
        BigDecimal amount) {
}