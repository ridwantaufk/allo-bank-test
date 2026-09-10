package com.ridwantaufik.allobank.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceChargeCalculatorTest {

    private final ServiceChargeCalculator calculator = new ServiceChargeCalculator();

    @Test
    void shouldCalculateServiceChargePercentageFromGithubUsername() {
        int percentage = calculator.calculatePercentage();

        assertEquals(4, percentage);
    }

    @Test
    void shouldCalculateServiceChargeAmount() {
        BigDecimal totalExpense = new BigDecimal("100000.00");

        BigDecimal serviceCharge = calculator.calculateAmount(totalExpense);

        assertEquals(
                new BigDecimal("4000.00"),
                serviceCharge);
    }

    @Test
    void shouldCalculateZeroServiceChargeForZeroExpense() {
        BigDecimal totalExpense = BigDecimal.ZERO;

        BigDecimal serviceCharge = calculator.calculateAmount(totalExpense);

        assertEquals(
                new BigDecimal("0.00"),
                serviceCharge);
    }
}