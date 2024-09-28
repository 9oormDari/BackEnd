package com.goormdari.domain.team.domain;

import com.goormdari.domain.common.BaseEntity;
import com.goormdari.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "goal")
    private String goal;

    @Column(name = "dead_line")
    private LocalDate deadLine;

    @Column(name = "routine1")
    private String routine1;

    @Column(name = "routine2")
    private String routine2;

    @Column(name = "routine3")
    private String routine3;

    @Column(name = "routine4")
    private String routine4;

    @Column(name = "join_code")
    private String joinCode;

    @Builder
    public Team(String name, String goal, LocalDate deadLine, String routine1, String routine2, String routine3, String routine4, String joinCode) {
        this.name = name;
        this.goal = goal;
        this.deadLine = deadLine;
        this.routine1 = routine1;
        this.routine2 = routine2;
        this.routine3 = routine3;
        this.routine4 = routine4;
        this.joinCode = joinCode;
    }

    public void updateForRegenerateObject(String goal, LocalDate deadLine, String routine1, String routine2, String routine3, String routine4) {
        this.goal = goal;
        this.deadLine = deadLine;
        this.routine1 = routine1;
        this.routine2 = routine2;
        this.routine3 = routine3;
        this.routine4 = routine4;
    }
}
