package com.goormdari.domain.routine.application;

import com.amazonaws.services.kms.model.NotFoundException;
import com.goormdari.domain.routine.domain.Routine;
import com.goormdari.domain.routine.dto.request.CompleteRoutineRequest;
import com.goormdari.domain.routine.domain.repository.RoutineRepository;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.repository.UserRepository;
import com.goormdari.global.config.s3.S3Service;
import com.goormdari.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
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
    public Message deleteRoutineByUserIdAndRoutineIndex (Long userId, Long routineIndex) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("User Not Found"));
        Routine routine = routineRepository.findByRoutineIndexAndUserId(userId,routineIndex);
        routineRepository.deleteById(routine.getId());
        user.updateCurrentStep(user.getCurrentStep()-1);
        return Message.builder()
                .message("루틴 삭제 성공")
                .build();
    }

    @Transactional
    public List<Routine> findAllRoutineByUserId (Long userId) {
        return routineRepository.findAllByUserId(userId);
    }
}
