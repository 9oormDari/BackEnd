package com.goormdari.domain.user.dto.response;

import lombok.Builder;

@Builder
public record findCurrentStepResponse (int currentStep) {
}
