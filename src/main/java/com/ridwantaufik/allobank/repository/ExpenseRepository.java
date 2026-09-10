package com.ridwantaufik.allobank.repository;

import com.ridwantaufik.allobank.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findAllByBillGroupId(UUID billGroupId);
}