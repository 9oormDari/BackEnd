package com.goormdari.domain.team.exception;

public class TeamAlreadyExistException extends RuntimeException {

    public TeamAlreadyExistException() {
        super("해당 유저의 팀이 이미 존재합니다.");
    }
}
