package com.goormdari.domain.history.domain;

import com.goormdari.domain.common.BaseEntity;
import com.goormdari.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

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

    @CreatedDate
    private LocalDateTime createAt;

    private int dDayPlus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // user_id로 User와 연결
    private User user;

    @Builder
    public History(String goal, String routine1, String routine2, String routine3, String routine4, User user, Boolean isSuccess, int dDayPlus) {
        this.goal = goal;
        this.routine1 = routine1;
        this.routine2 = routine2;
        this.routine3 = routine3;
        this.routine4 = routine4;
        this.user = user;
        this.isSuccess = isSuccess;
        this.dDayPlus = dDayPlus;
    }

    public void incrementDDayPlus() {
        this.dDayPlus++;  // dDayPlus 증가
    }

}
