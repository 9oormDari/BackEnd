package com.goormdari.domain.history.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponse {
    private int dDay;
    private String goal;
    private String[] routineList;
    private String result;
}
