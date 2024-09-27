package com.goormdari.domain.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goormdari.domain.common.BaseEntity;
import com.goormdari.domain.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String profileUrl;

    private String routinImg1;
    private String routinImg2;
    private String routinImg3;
    private String routinImg4;

    private String role;
  
    private String goal;
    private LocalDate deadLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
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

    @Builder
    public User(String nickname, String username, String password, String role) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
