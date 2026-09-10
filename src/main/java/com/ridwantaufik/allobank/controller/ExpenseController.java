package com.ridwantaufik.allobank.controller;

import com.ridwantaufik.allobank.dto.AddExpenseRequest;
import com.ridwantaufik.allobank.dto.AddExpenseResponse;
import com.ridwantaufik.allobank.dto.ExpenseShareResponse;
import com.ridwantaufik.allobank.entity.Expense;
import com.ridwantaufik.allobank.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bill-groups/{groupId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<AddExpenseResponse> addExpense(
            @PathVariable UUID groupId,
            @Valid @RequestBody AddExpenseRequest request) {
        Expense expense = expenseService.addExpense(
                groupId,
                request);

        AddExpenseResponse response = new AddExpenseResponse(
                expense.getId(),
                groupId,
                expense.getPaidBy().getId(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getShares()
                        .stream()
                        .map(share -> new ExpenseShareResponse(
                                share.getParticipant().getId(),
                                share.getAmount()))
                        .toList(),
                expense.getCreatedAt());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}