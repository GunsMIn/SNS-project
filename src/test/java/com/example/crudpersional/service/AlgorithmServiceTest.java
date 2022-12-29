package com.example.crudpersional.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmServiceTest {

    // Spring을 안쓰고 테스트 하기 때문에 new를 이용해 초기화를 해줍니다.
    // Pojo방식을 최대한 활용합니다.
    AlgorithmService algorithmService = new AlgorithmService();

    @Test
    @DisplayName("자릿수 합 service test")
    void sumOfDigit() {
        assertEquals(21, algorithmService.solution(687));
        assertEquals(22, algorithmService.solution(787));
        assertEquals(0, algorithmService.solution(0));
        assertEquals(5, algorithmService.solution(11111));
    }

}