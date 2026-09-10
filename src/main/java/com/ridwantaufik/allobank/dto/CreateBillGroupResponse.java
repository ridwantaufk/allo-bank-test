package com.ridwantaufik.allobank.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateBillGroupResponse(
        UUID id,
        String name,
        List<ParticipantResponse> participants,
        Instant createdAt) {
}