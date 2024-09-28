package com.goormdari.domain.user.domain;

import java.util.Random;

public enum DefaultProfileUrl {
    A("https://goormdari.s3.ap-northeast-2.amazonaws.com/394624e7-8fe3-4c95-8141-3935df511922-KakaoTalk_Photo_2024-09-28-20-30-53%20004.png"),
    B("https://goormdari.s3.ap-northeast-2.amazonaws.com/7e0b113d-e904-45e7-9ac0-64f0d177e41a-KakaoTalk_Photo_2024-09-28-20-30-53%20003.png"),
    C("https://goormdari.s3.ap-northeast-2.amazonaws.com/e8555819-8da6-43c2-947b-c032e4de931d-KakaoTalk_Photo_2024-09-28-20-30-53%20002.png"),
    D("https://goormdari.s3.ap-northeast-2.amazonaws.com/7e7085f0-3fef-4348-b726-422a50b1d1d4-KakaoTalk_Photo_2024-09-28-20-30-52%20001.png");

    private String profileUrl;

    DefaultProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public static String getRandomProfileUrl() {
        DefaultProfileUrl[] values = DefaultProfileUrl.values();

        // 랜덤 인덱스 생성
        Random random = new Random();
        int index = random.nextInt(values.length); // 0에서 배열의 길이만큼 랜덤 숫자 생성

        // 랜덤으로 선택된 문자 출력
        return values[index].getProfileUrl();
    }

}
