package com.goormdari.domain.team.domain;

import com.goormdari.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String goal;

    private LocalDate deadLine;

    private String routine1;
    private String routine2;
    private String routine3;
    private String routine4;

    private String joinCode;


    @Builder
    public Team(String goal, LocalDate deadLine, String routine1, String routine2, String routine3, String routine4, String joinCode) {
        this.goal = goal;
        this.deadLine = deadLine;
        this.routine1 = routine1;
        this.routine2 = routine2;
        this.routine3 = routine3;
        this.routine4 = routine4;
        this.joinCode = joinCode;
    }
}
