package com.ridwantaufik.allobank.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AddExpenseResponse(
        UUID id,
        UUID groupId,
        UUID paidByParticipantId,
        BigDecimal amount,
        String description,
        List<ExpenseShareResponse> shares,
        Instant createdAt) {
}