package com.ridwantaufik.allobank.service;

import com.ridwantaufik.allobank.dto.AddExpenseRequest;
import com.ridwantaufik.allobank.dto.ExpenseShareRequest;
import com.ridwantaufik.allobank.entity.BillGroup;
import com.ridwantaufik.allobank.entity.Expense;
import com.ridwantaufik.allobank.entity.ExpenseShare;
import com.ridwantaufik.allobank.entity.Participant;
import com.ridwantaufik.allobank.repository.BillGroupRepository;
import com.ridwantaufik.allobank.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ridwantaufik.allobank.repository.ExpenseRepository;
import com.ridwantaufik.allobank.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

        private final BillGroupRepository billGroupRepository;
        private final ParticipantRepository participantRepository;
        private final ExpenseRepository expenseRepository;

        public ExpenseService(
                        BillGroupRepository billGroupRepository,
                        ParticipantRepository participantRepository,
                        ExpenseRepository expenseRepository) {
                this.billGroupRepository = billGroupRepository;
                this.participantRepository = participantRepository;
                this.expenseRepository = expenseRepository;
        }

        @Transactional
        public Expense addExpense(
                        UUID groupId,
                        AddExpenseRequest request) {
                BillGroup billGroup = billGroupRepository.findById(groupId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Bill group not found: " + groupId));

                Participant paidBy = participantRepository
                                .findByIdAndBillGroupId(
                                                request.paidByParticipantId(),
                                                groupId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Paid-by participant does not belong to this bill group"));

                validateShares(request);

                validateAmountScale(request.amount());

                for (ExpenseShareRequest share : request.shares()) {
                        validateAmountScale(share.amount());
                }

                Map<UUID, Participant> participantsById = participantRepository
                                .findAllByBillGroupId(groupId)
                                .stream()
                                .collect(Collectors.toMap(
                                                Participant::getId,
                                                Function.identity()));

                Expense expense = new Expense();
                expense.setBillGroup(billGroup);
                expense.setPaidBy(paidBy);
                expense.setAmount(request.amount());
                expense.setDescription(request.description().trim());

                for (ExpenseShareRequest shareRequest : request.shares()) {
                        Participant participant = participantsById.get(shareRequest.participantId());

                        if (participant == null) {
                                throw new IllegalArgumentException(
                                                "Share participant does not belong to this bill group: "
                                                                + shareRequest.participantId());
                        }

                        ExpenseShare share = new ExpenseShare();
                        share.setExpense(expense);
                        share.setParticipant(participant);
                        share.setAmount(shareRequest.amount());

                        expense.getShares().add(share);
                }

                return expenseRepository.save(expense);
        }

        private void validateAmountScale(BigDecimal amount) {
                if (amount.scale() > 2) {
                        throw new IllegalArgumentException(
                                        "Monetary amounts must have at most 2 decimal places");
                }
        }

        private void validateShares(AddExpenseRequest request) {
                long uniqueParticipantCount = request.shares()
                                .stream()
                                .map(ExpenseShareRequest::participantId)
                                .distinct()
                                .count();

                if (uniqueParticipantCount != request.shares().size()) {
                        throw new IllegalArgumentException(
                                        "A participant can only appear once in expense shares");
                }

                BigDecimal totalShares = request.shares()
                                .stream()
                                .map(ExpenseShareRequest::amount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalShares.compareTo(request.amount()) != 0) {
                        throw new IllegalArgumentException(
                                        "Total share amount must equal expense amount");
                }
        }
}