package com.ridwantaufik.allobank.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SettlementResponse(
        UUID groupId,
        String groupName,
        BigDecimal totalExpense,
        int serviceChargePct,
        BigDecimal serviceChargeAmount,
        BigDecimal totalWithServiceCharge,
        List<SettlementTransactionResponse> settlements) {
}