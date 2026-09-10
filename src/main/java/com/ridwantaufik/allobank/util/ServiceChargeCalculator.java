package com.ridwantaufik.allobank.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ServiceChargeCalculator {

    private static final String GITHUB_USERNAME = "ridwantaufk";

    public int calculatePercentage() {
        return GITHUB_USERNAME
                .chars()
                .sum() % 10;
    }

    public BigDecimal calculateAmount(BigDecimal totalExpense) {
        int percentage = calculatePercentage();

        return totalExpense
                .multiply(BigDecimal.valueOf(percentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public String getGithubUsername() {
        return GITHUB_USERNAME;
    }
}