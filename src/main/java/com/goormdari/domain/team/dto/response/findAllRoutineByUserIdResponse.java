package com.goormdari.domain.team.dto.response;

import lombok.Builder;

@Builder
public record findAllRoutineByUserIdResponse(String routine1, String routine2, String routine3, String routine4) {
}
