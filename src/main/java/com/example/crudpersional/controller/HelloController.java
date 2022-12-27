package com.example.crudpersional.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HelloController {

    @GetMapping("/api/v1/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().body("kimgunwoo");
    }
}
