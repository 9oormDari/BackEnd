package com.goormdari.domain.calendar.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CalendarController {

    @GetMapping("/test")
    public void test() {
        System.out.println("test");
    }

}
