package com.ridwantaufik.allobank.service;

import com.ridwantaufik.allobank.dto.SettlementTransactionResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SettlementCalculatorTest {

    private final SettlementCalculator calculator = new SettlementCalculator();

    @Test
    void shouldCalculateSimpleSettlement() {
        UUID ridwanId = UUID.randomUUID();
        UUID naniId = UUID.randomUUID();
        UUID evaId = UUID.randomUUID();

        List<SettlementCalculator.ParticipantBalance> balances = List.of(
                new SettlementCalculator.ParticipantBalance(
                        ridwanId,
                        "Ridwan",
                        new BigDecimal("100000.00")),
                new SettlementCalculator.ParticipantBalance(
                        naniId,
                        "Nani",
                        new BigDecimal("-50000.00")),
                new SettlementCalculator.ParticipantBalance(
                        evaId,
                        "Eva",
                        new BigDecimal("-50000.00")));

        List<SettlementTransactionResponse> result = calculator.calculate(balances);

        assertEquals(2, result.size());

        assertEquals(naniId, result.get(0).fromParticipantId());
        assertEquals(ridwanId, result.get(0).toParticipantId());
        assertEquals(
                new BigDecimal("50000.00"),
                result.get(0).amount());

        assertEquals(evaId, result.get(1).fromParticipantId());
        assertEquals(ridwanId, result.get(1).toParticipantId());
        assertEquals(
                new BigDecimal("50000.00"),
                result.get(1).amount());
    }

    @Test
    void shouldMinimizeNumberOfTransactions() {
        UUID aId = UUID.randomUUID();
        UUID bId = UUID.randomUUID();
        UUID cId = UUID.randomUUID();
        UUID dId = UUID.randomUUID();

        List<SettlementCalculator.ParticipantBalance> balances = List.of(
                new SettlementCalculator.ParticipantBalance(
                        aId,
                        "A",
                        new BigDecimal("150000.00")),
                new SettlementCalculator.ParticipantBalance(
                        bId,
                        "B",
                        new BigDecimal("50000.00")),
                new SettlementCalculator.ParticipantBalance(
                        cId,
                        "C",
                        new BigDecimal("-100000.00")),
                new SettlementCalculator.ParticipantBalance(
                        dId,
                        "D",
                        new BigDecimal("-100000.00")));

        List<SettlementTransactionResponse> result = calculator.calculate(balances);

        assertEquals(3, result.size());

        assertEquals(
                new BigDecimal("100000.00"),
                result.get(0).amount());

        assertEquals(
                new BigDecimal("50000.00"),
                result.get(1).amount());

        assertEquals(
                new BigDecimal("50000.00"),
                result.get(2).amount());
    }

    @Test
    void shouldReturnEmptySettlementWhenEveryoneIsBalanced() {
        UUID ridwanId = UUID.randomUUID();
        UUID naniId = UUID.randomUUID();

        List<SettlementCalculator.ParticipantBalance> balances = List.of(
                new SettlementCalculator.ParticipantBalance(
                        ridwanId,
                        "Ridwan",
                        BigDecimal.ZERO),
                new SettlementCalculator.ParticipantBalance(
                        naniId,
                        "Nani",
                        BigDecimal.ZERO));

        List<SettlementTransactionResponse> result = calculator.calculate(balances);

        assertEquals(0, result.size());
    }
}