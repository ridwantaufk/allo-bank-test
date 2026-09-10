package com.ridwantaufik.allobank.repository;

import com.ridwantaufik.allobank.entity.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, UUID> {

    List<ExpenseShare> findAllByParticipantId(UUID participantId);

    List<ExpenseShare> findAllByExpenseId(UUID expenseId);
}