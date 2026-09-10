package com.ridwantaufik.allobank.controller;

import com.ridwantaufik.allobank.dto.CreateBillGroupRequest;
import com.ridwantaufik.allobank.dto.CreateBillGroupResponse;
import com.ridwantaufik.allobank.service.BillGroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bill-groups")
public class BillGroupController {

    private final BillGroupService billGroupService;

    public BillGroupController(BillGroupService billGroupService) {
        this.billGroupService = billGroupService;
    }

    @PostMapping
    public ResponseEntity<CreateBillGroupResponse> createGroup(
            @Valid @RequestBody CreateBillGroupRequest request) {
        CreateBillGroupResponse response = billGroupService.createGroup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}