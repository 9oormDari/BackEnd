package com.goormdari.domain.team.dto.response;

import lombok.Builder;

@Builder
public record CreateTeamResponse(
        String joinCode
) {
}
