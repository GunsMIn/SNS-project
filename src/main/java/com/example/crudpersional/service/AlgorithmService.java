package com.example.crudpersional.service;

import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {

    public int solution(int n) {
        int answer = 0;

        while(n > 0){
            answer += n%10;
            n/=10;
        }
        return answer;
    }

}
