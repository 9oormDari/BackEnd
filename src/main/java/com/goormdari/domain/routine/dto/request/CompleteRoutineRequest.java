package com.goormdari.domain.routine.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record CompleteRoutineRequest (

        Long routineIndex,

        String routineName,

        MultipartFile file

        ) {

}
