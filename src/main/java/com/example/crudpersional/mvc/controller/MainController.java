
package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.mvc.dto.MemberForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
@Slf4j
public class MainController {

    //guns shop 메인화면
    @RequestMapping("/main")
    public String main() {
        return "main";
    }


}
