package com.goormdari.domain.calendar.domain;

import com.goormdari.domain.common.BaseEntity;
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
public class Calendar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "success_count")
    private int success_count;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "goal")
    private String goal;
}
