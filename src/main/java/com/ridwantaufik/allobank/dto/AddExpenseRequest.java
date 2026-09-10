package com.ridwantaufik.allobank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AddExpenseRequest(

                @NotNull(message = "Paid by participant is required") UUID paidByParticipantId,

                @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,

                @NotBlank(message = "Description is required") @Size(max = 200, message = "Description must not exceed 200 characters") String description,

                @NotNull(message = "Shares are required") @Size(min = 1, message = "At least one participant must share the expense") List<@Valid ExpenseShareRequest> shares) {
}