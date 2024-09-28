package com.goormdari.domain.team.dto.request;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegenerateTeamRequest(
        String goal,
        LocalDate deadline,
        String routine1,
        String routine2,
        String routine3,
        String routine4
) {
}
