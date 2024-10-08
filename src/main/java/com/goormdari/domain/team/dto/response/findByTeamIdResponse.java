package com.goormdari.domain.team.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record findByTeamIdResponse(Long id, String username, String profileUrl) {
}
