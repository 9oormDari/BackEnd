package com.goormdari.domain.history.domain;

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
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "goal")
    private String goal;

    @Column(name = "routine1")
    private String routine1;

    @Column(name = "routine2")
    private String routine2;

    @Column(name = "routine3")
    private String routine3;

    @Column(name = "routine4")
    private String routine4;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

}
