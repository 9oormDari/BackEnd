package com.goormdari.domain.user.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UpdateUserRequest {
    private String nickname;
    private String username;
    private String password;
    private MultipartFile file;
    private String email;
    private String currentPassword;
}
