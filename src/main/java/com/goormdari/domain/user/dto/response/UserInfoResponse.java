package com.goormdari.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserInfoResponse {
    private String nickname;
    private String username;
    private String email;
    private String profileUrl;
    private String goal;
    private LocalDate deadline;
    private Long teamId;
}
