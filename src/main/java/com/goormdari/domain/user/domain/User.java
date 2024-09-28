package com.goormdari.domain.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goormdari.domain.common.BaseEntity;
import com.goormdari.domain.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

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

    @Column(name = "email")
    private String email;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "role")
    private String role;

    @Column(name = "goal")
    private String goal;

    @Column(name = "dead_line")
    private LocalDate deadLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnore
    private Team team;

    public void updateTeam(Team team) {
        this.team = team;
    }

    public void updateGoal(String goal) {
        this.goal = goal;
    }

    public void updateDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    public void updateCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    @Builder
    public User(String nickname, String username, String password, String role) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
