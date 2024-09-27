package com.goormdari.domain.routine.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CompleteRoutineRequest (

        Long routineIndex,

        String routineName

        ) {

}
