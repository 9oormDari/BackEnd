package com.goormdari.domain.history.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponse {
    private String dDay; // "D-20", "D+5", "D-Day"
    private String goal;
    private String[] routineList;
    private String result; // "성공", "실패", "진행 중"
}
