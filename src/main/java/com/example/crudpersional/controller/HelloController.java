package com.example.crudpersional.controller;

import com.example.crudpersional.service.AlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class HelloController {

    private final AlgorithmService algorithmService;

    @GetMapping(value = "/api/v1/hello", produces = "application/json; charset=utf8")
    public String hello() {
        return "김건우";
    }

    @GetMapping("/{num}")
    public Integer divide(Integer num) {
        return algorithmService.sum(num);
    }

}
