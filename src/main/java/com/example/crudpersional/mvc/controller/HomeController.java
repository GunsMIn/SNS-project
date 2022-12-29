
package com.example.crudpersional.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
@RequiredArgsConstructor
public class HomeController {

    @RequestMapping("/")
    public String goHome() {
        return "index";
    }

}

