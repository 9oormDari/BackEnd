package com.goormdari.domain.calendar.application;

import com.goormdari.domain.calendar.domain.repository.CalendarRepository;
import com.goormdari.domain.calendar.dto.response.CheckGoalProgressResponse;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;


    public CheckGoalProgressResponse searchCheckGoalProgress(String username, YearMonth date) {
        System.out.println("username = " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        CheckGoalProgressResponse checkGoalProgressResponse = calendarRepository.findByIdAndDate(user.getId(), date);

        return checkGoalProgressResponse;
    }
}
