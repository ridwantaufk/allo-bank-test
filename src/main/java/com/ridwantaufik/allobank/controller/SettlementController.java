package com.ridwantaufik.allobank.controller;

import com.ridwantaufik.allobank.dto.SettlementResponse;
import com.ridwantaufik.allobank.service.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bill-groups/{groupId}/settlement")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(
            SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping
    public ResponseEntity<SettlementResponse> getSettlement(
            @PathVariable UUID groupId) {
        SettlementResponse response = settlementService.calculateSettlement(groupId);

        return ResponseEntity.ok(response);
    }
}