package com.ridwantaufik.allobank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseShareRequest(

        @NotNull(message = "Participant ID is required") UUID participantId,

        @NotNull(message = "Share amount is required") @DecimalMin(value = "0.01", message = "Share amount must be greater than zero") BigDecimal amount) {
}