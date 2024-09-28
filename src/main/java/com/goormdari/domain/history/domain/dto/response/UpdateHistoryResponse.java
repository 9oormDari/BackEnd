package com.goormdari.domain.history.domain.dto.response;

import lombok.Builder;

@Builder
public record UpdateHistoryResponse(
        String goal,
        String routine1,
        String routine2,
        String routine3,
        String routine4
) {
}
