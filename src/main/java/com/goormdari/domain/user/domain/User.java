package com.goormdari.domain.user.domain;

import com.goormdari.domain.common.BaseEntity;
import com.goormdari.domain.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String nickname;
    private String username;
    private String password;
    private String profileUrl;

    private int currentStep;

    private String routineImg1;
    private String routineImg2;
    private String routineImg3;
    private String routineImg4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public void updateTeam(Team team) {
        this.team = team;
    }
}
