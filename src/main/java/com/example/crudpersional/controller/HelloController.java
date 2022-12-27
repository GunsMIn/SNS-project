package com.example.crudpersional.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HelloController {

    @GetMapping(value = "/api/v1/hello", produces = "application/json; charset=utf8")
    public String hello() {
        return "김건우";
    }

}
