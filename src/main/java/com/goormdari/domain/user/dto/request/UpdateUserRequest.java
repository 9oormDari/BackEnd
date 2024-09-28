package com.goormdari.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class UpdateUserRequest {
    private String nickname;
    private String username;
    private String password;
    private MultipartFile file;
    private String email;
    private String currentPassword;
}
