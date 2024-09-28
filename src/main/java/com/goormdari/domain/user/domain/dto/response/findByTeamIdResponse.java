package com.goormdari.domain.user.domain.dto.response;

import lombok.Builder;

@Builder
public record findByTeamIdResponse(Long id, String username) {
}
