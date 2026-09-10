package com.ridwantaufik.allobank.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseShareResponse(
        UUID participantId,
        BigDecimal amount) {
}