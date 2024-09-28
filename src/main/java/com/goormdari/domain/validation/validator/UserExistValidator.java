package com.goormdari.domain.validation.validator;

import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.repository.UserRepository;
import com.goormdari.domain.validation.annotation.ExistUser;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserExistValidator implements ConstraintValidator<ExistUser, String> {

    private final UserRepository userRepository;


    @Override
    public void initialize(ExistUser constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {

        boolean isValid = userRepository.existsByUsername(username);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("User not found").addConstraintViolation();
        }

        return isValid;
    }
}
