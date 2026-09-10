package com.ridwantaufik.allobank.dto;

import java.util.UUID;

public record ParticipantResponse(
        UUID id,
        String name) {
}