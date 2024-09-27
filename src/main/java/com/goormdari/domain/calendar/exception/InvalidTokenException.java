package com.goormdari.domain.calendar.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("토큰이 비어있습니다.");
    }
}
