package com.goormdari.domain.history.domain;

import com.goormdari.domain.common.BaseEntity;
import com.goormdari.domain.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String goal;

    private String routine1;
    private String routine2;
    private String routine3;
    private String routine4;

    private Boolean isSuccess;

    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder
    public History(String goal, String routine1, String routine2, String routine3, String routine4, Team team) {
        this.goal = goal;
        this.routine1 = routine1;
        this.routine2 = routine2;
        this.routine3 = routine3;
        this.routine4 = routine4;
        this.team = team;
        this.createAt = LocalDateTime.now();
        this.isSuccess = false;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

}
