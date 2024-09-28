package com.goormdari.domain.calendar.application;

import com.goormdari.domain.calendar.domain.repository.CalendarRepository;
import com.goormdari.domain.calendar.dto.response.CheckGoalProgressResponse;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.repository.UserRepository;
import com.goormdari.domain.validation.annotation.ExistUser;
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


    public CheckGoalProgressResponse searchCheckGoalProgress(@ExistUser final String username, final YearMonth date) {
        User user = userRepository.findByUsername(username).get();

        CheckGoalProgressResponse checkGoalProgressResponse = calendarRepository.findByIdAndDate(user.getId(), date);

        return checkGoalProgressResponse;
    }
}
