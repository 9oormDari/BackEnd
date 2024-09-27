package com.goormdari.domain.routine.application;

import com.amazonaws.services.kms.model.NotFoundException;
import com.goormdari.domain.routine.domain.Routine;
import com.goormdari.domain.routine.dto.request.CompleteRoutineRequest;
import com.goormdari.domain.routine.domain.repository.RoutineRepository;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.repository.UserRepository;
import com.goormdari.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineService {

    final RoutineRepository routineRepository;
    final UserRepository userRepository;

    @Transactional
    public Message completeRoutine (Long userId, CompleteRoutineRequest completeRoutineRequest, String imgURL) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("User Not Found"));
        Routine routine = Routine.builder()
                .user(user)
                .routineImg(imgURL)
                .routineIndex(completeRoutineRequest.routineIndex())
                .routineName(completeRoutineRequest.routineName())
                .build();

        user.updateCurrentStep(user.getCurrentStep()+1);
        routineRepository.save(routine);

        return Message.builder()
                .message("루틴 완수 성공")
                .build();
    }

    @Transactional
    public Message deleteRoutine (Long userId, Long routineIndex) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("User Not Found"));

        return Message.builder()
                .message("루틴 삭제 성공")
                .build();
    }
}
