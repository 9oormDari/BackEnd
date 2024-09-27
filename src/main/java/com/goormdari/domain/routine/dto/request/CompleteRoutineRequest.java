package com.goormdari.domain.routine.dto.request;

import lombok.Builder;

@Builder
public record CompleteRoutineRequest (

        Long routineIndex,

        String routineName

        ) {

}
