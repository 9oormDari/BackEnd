package com.goormdari.domain.routine.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goormdari.domain.common.BaseEntity;
import com.goormdari.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Routine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "routine_img")
    private String routineImg;

    @Column(name = "routine_index")
    private Long routineIndex;

    @Column(name = "routine_name")
    private String routineName;


    @Builder
    public Routine(User user, String routineImg, Long routineIndex, String routineName) {
        this.user=user;
        this.routineImg=routineImg;
        this.routineIndex=routineIndex;
        this.routineName=routineName;
    }

}
