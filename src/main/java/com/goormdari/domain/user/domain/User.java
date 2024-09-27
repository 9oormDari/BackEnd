package com.goormdari.domain.user.domain;

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

    private String email;

    private String nickname;
    private String username;
    private String password;
    private String profileUrl;

    private int currentStep;

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

    public void updateCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }
}
