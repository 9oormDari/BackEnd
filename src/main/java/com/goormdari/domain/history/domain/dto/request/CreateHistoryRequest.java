package com.goormdari.domain.history.domain.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateHistoryRequest(
        String goal,
        String routine1,
        String routine2,
        String routine3,
        String routine4,
        List<Long> routineIds
//        Long teamId
) {
}
