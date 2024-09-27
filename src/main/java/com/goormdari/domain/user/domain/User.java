package com.goormdari.domain.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private int currentStep;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    private String profileUrl;

    private String routinImg1;
    private String routinImg2;
    private String routinImg3;
    private String routinImg4;

    private String role;

    @Builder
    public User(String nickname, String username, String password, String role) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
