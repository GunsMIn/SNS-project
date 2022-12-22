package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.domain.dto.user.UserJoinRequest;
import com.example.crudpersional.mvc.dto.MemberForm;
import com.example.crudpersional.mvc.service.UserMvcService;
import com.example.crudpersional.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserMvcController {

    private final UserMvcService userService;

    @GetMapping("/members/joinForm")
    public String goJoinForm(@ModelAttribute MemberForm memberForm) {
        return "/members/join";
    }

    @PostMapping("/members/joinForm")
    public String doJoinForm(@ModelAttribute MemberForm memberForm) {
        log.info("유저:{}",memberForm);
        userService.join(memberForm);
        return "redirect:/members/joinForm";
    }

}
