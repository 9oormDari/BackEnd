package com.goormdari.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateUserRequest {
    private String nickname;
    private String username;
    private String password;
    private String profileUrl;
    private String email;
    private String currentPassword;
}
