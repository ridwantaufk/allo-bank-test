package com.ridwantaufik.allobank.service;

import com.ridwantaufik.allobank.dto.SettlementResponse;
import com.ridwantaufik.allobank.dto.SettlementTransactionResponse;
import com.ridwantaufik.allobank.entity.BillGroup;
import com.ridwantaufik.allobank.entity.Expense;
import com.ridwantaufik.allobank.entity.ExpenseShare;
import com.ridwantaufik.allobank.entity.Participant;
import com.ridwantaufik.allobank.repository.BillGroupRepository;
import com.ridwantaufik.allobank.repository.ExpenseRepository;
import com.ridwantaufik.allobank.util.ServiceChargeCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ridwantaufik.allobank.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SettlementService {

        private final BillGroupRepository billGroupRepository;
        private final ExpenseRepository expenseRepository;
        private final SettlementCalculator settlementCalculator;
        private final ServiceChargeCalculator serviceChargeCalculator;

        public SettlementService(
                        BillGroupRepository billGroupRepository,
                        ExpenseRepository expenseRepository,
                        SettlementCalculator settlementCalculator,
                        ServiceChargeCalculator serviceChargeCalculator) {
                this.billGroupRepository = billGroupRepository;
                this.expenseRepository = expenseRepository;
                this.settlementCalculator = settlementCalculator;
                this.serviceChargeCalculator = serviceChargeCalculator;
        }

        @Transactional(readOnly = true)
        public SettlementResponse calculateSettlement(UUID groupId) {

                BillGroup billGroup = billGroupRepository.findById(groupId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Bill group not found: " + groupId));

                List<Expense> expenses = expenseRepository.findAllByBillGroupId(groupId);

                Map<UUID, BigDecimal> paidAmounts = new HashMap<>();
                Map<UUID, BigDecimal> owedAmounts = new HashMap<>();

                for (Participant participant : billGroup.getParticipants()) {
                        paidAmounts.put(
                                        participant.getId(),
                                        BigDecimal.ZERO);

                        owedAmounts.put(
                                        participant.getId(),
                                        BigDecimal.ZERO);
                }

                BigDecimal totalExpense = BigDecimal.ZERO;

                for (Expense expense : expenses) {

                        totalExpense = totalExpense.add(
                                        expense.getAmount());

                        UUID paidById = expense.getPaidBy().getId();

                        paidAmounts.merge(
                                        paidById,
                                        expense.getAmount(),
                                        BigDecimal::add);

                        for (ExpenseShare share : expense.getShares()) {

                                UUID participantId = share.getParticipant().getId();

                                owedAmounts.merge(
                                                participantId,
                                                share.getAmount(),
                                                BigDecimal::add);
                        }
                }

                List<SettlementCalculator.ParticipantBalance> balances = new ArrayList<>();

                for (Participant participant : billGroup.getParticipants()) {

                        BigDecimal paid = paidAmounts.getOrDefault(
                                        participant.getId(),
                                        BigDecimal.ZERO);

                        BigDecimal owed = owedAmounts.getOrDefault(
                                        participant.getId(),
                                        BigDecimal.ZERO);

                        BigDecimal netBalance = paid.subtract(owed);

                        balances.add(
                                        new SettlementCalculator.ParticipantBalance(
                                                        participant.getId(),
                                                        participant.getName(),
                                                        netBalance));
                }

                List<SettlementTransactionResponse> settlements = settlementCalculator.calculate(balances);

                int serviceChargePct = serviceChargeCalculator.calculatePercentage();

                BigDecimal serviceChargeAmount = serviceChargeCalculator.calculateAmount(
                                totalExpense);

                BigDecimal totalWithServiceCharge = totalExpense.add(serviceChargeAmount);

                return new SettlementResponse(
                                billGroup.getId(),
                                billGroup.getName(),
                                totalExpense,
                                serviceChargePct,
                                serviceChargeAmount,
                                totalWithServiceCharge,
                                settlements);
        }
}