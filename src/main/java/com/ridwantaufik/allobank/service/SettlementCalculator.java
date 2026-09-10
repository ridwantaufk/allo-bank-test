package com.ridwantaufik.allobank.service;

import com.ridwantaufik.allobank.dto.SettlementTransactionResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class SettlementCalculator {

    public List<SettlementTransactionResponse> calculate(
            List<ParticipantBalance> balances) {
        List<Balance> creditors = balances.stream()
                .filter(balance -> balance.netBalance().compareTo(BigDecimal.ZERO) > 0)
                .map(balance -> new Balance(
                        balance.participantId(),
                        balance.participantName(),
                        balance.netBalance()))
                .sorted(
                        Comparator.comparing(
                                Balance::amount).reversed())
                .collect(ArrayList::new, List::add, List::addAll);

        List<Balance> debtors = balances.stream()
                .filter(balance -> balance.netBalance().compareTo(BigDecimal.ZERO) < 0)
                .map(balance -> new Balance(
                        balance.participantId(),
                        balance.participantName(),
                        balance.netBalance().abs()))
                .sorted(
                        Comparator.comparing(
                                Balance::amount).reversed())
                .collect(ArrayList::new, List::add, List::addAll);

        List<SettlementTransactionResponse> settlements = new ArrayList<>();

        int creditorIndex = 0;
        int debtorIndex = 0;

        while (creditorIndex < creditors.size()
                && debtorIndex < debtors.size()) {
            Balance creditor = creditors.get(creditorIndex);
            Balance debtor = debtors.get(debtorIndex);

            BigDecimal settlementAmount = creditor.amount().min(debtor.amount());

            settlements.add(
                    new SettlementTransactionResponse(
                            debtor.participantId(),
                            debtor.participantName(),
                            creditor.participantId(),
                            creditor.participantName(),
                            settlementAmount));

            BigDecimal remainingCreditor = creditor.amount()
                    .subtract(settlementAmount);

            BigDecimal remainingDebtor = debtor.amount()
                    .subtract(settlementAmount);

            creditors.set(
                    creditorIndex,
                    new Balance(
                            creditor.participantId(),
                            creditor.participantName(),
                            remainingCreditor));

            debtors.set(
                    debtorIndex,
                    new Balance(
                            debtor.participantId(),
                            debtor.participantName(),
                            remainingDebtor));

            if (remainingCreditor.compareTo(BigDecimal.ZERO) == 0) {
                creditorIndex++;
            }

            if (remainingDebtor.compareTo(BigDecimal.ZERO) == 0) {
                debtorIndex++;
            }
        }

        return settlements;
    }

    public record ParticipantBalance(
            UUID participantId,
            String participantName,
            BigDecimal netBalance) {
    }

    private record Balance(
            UUID participantId,
            String participantName,
            BigDecimal amount) {
    }
}