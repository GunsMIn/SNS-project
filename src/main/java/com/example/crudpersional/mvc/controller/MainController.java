package com.example.crudpersional.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    //guns shop 메인화면
    @RequestMapping("/main")
    public String main() {
        return "main";
    }
}