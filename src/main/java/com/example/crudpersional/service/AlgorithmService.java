package com.example.crudpersional.service;

import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {

    public Integer sum(Integer num) {
        int sum = 0;
        while (num > 0) {
            sum += num % 10; // 맨 끝수 +
            num = num / 10; // 자릿수 진행
        }
        return sum;
    }

}
