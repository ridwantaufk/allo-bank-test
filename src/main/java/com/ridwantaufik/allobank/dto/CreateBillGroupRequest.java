package com.ridwantaufik.allobank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateBillGroupRequest(

        @NotBlank(message = "Group name is required") @Size(max = 150, message = "Group name must not exceed 150 characters") String name,

        @NotEmpty(message = "At least one participant is required") List<@NotBlank(message = "Participant name is required") @Size(max = 100, message = "Participant name must not exceed 100 characters") String> participants) {
}