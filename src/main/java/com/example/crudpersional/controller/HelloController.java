package com.example.crudpersional.controller;

import com.example.crudpersional.service.AlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping
@RequiredArgsConstructor
public class HelloController {

    private final AlgorithmService algorithmService;

    @GetMapping(value = "/api/v1/hello", produces = "application/json; charset=utf8")
    public String hello() {
        return "김건우";
    }

    @GetMapping("/api/v1/hello/{num}")
    public Integer divide(@PathVariable Integer num) {
        return algorithmService.solution(num);
    }


    /**Security test용 api 주소**/
    @GetMapping("/api/v1/hello/api-auth-test")
    public String authTest() {
        return "ok";
    }


}
