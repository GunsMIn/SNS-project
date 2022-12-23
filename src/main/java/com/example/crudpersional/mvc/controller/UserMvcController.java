
package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.domain.dto.user.UserJoinRequest;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.mvc.dto.LoginForm;
import com.example.crudpersional.mvc.dto.MemberForm;
import com.example.crudpersional.mvc.dto.SessionConst;
import com.example.crudpersional.mvc.service.UserMvcService;
import com.example.crudpersional.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpRequest;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserMvcController {

    private final UserMvcService userService;


    @GetMapping("/members/joinUser")
    public String joinUser(@ModelAttribute MemberForm memberForm) {
        return "members/join";
    }

    @PostMapping("/members/doJoinForm")
    public String doJoin(@ModelAttribute MemberForm memberForm) {
        log.info("유저:{}", memberForm);
        userService.join(memberForm);
        return "redirect:/";
    }

    @GetMapping("/members/loginForm")
    public String goLogin(@ModelAttribute LoginForm loginForm) {
        return "members/login";
    }

    @PostMapping("/members/doLoginForm")
    public String doLogin(@ModelAttribute LoginForm loginForm, Model model,HttpServletRequest request) {
        userService.login(loginForm.getUserName(), loginForm.getPassword());
        User user = loginForm.toEntity();

        if(user==null){
            return "members/loginForm";
        }

        HttpSession session = request.getSession(true); // 세션이 없다면 새로운 세션 생성
        session.setAttribute(SessionConst.LOGIN_MEMBER,user);
        model.addAttribute("member", user);
        return "loginIndex";
    }

  @GetMapping("/members/loginIndex")
    public String goLoginIndex(@SessionAttribute(name = "loginMember", required = false) User loginMember,@ModelAttribute LoginForm loginForm,Model model) {
        model.addAttribute("member", loginMember);
        return "loginindex";
    }




/*@GetMapping("/members/loginIndex")
    public String goLoginIndex(@ModelAttribute LoginForm loginForm,Model model) {
        model.addAttribute("member", loginMember);
        return "/loginindex";
    }*/





   /* @PostMapping("/members/loginForm")
    public String doLogin(@ModelAttribute LoginForm loginForm, Model model, HttpServletRequest request) {
        userService.login(loginForm.getUserName(), loginForm.getPassword());
        User loginMember = loginForm.toEntity();
        //로그인 성공 시 session 처리
        HttpSession session = request.getSession(true); // 세션이 없다면 새로운 세션 생성
        session.setAttribute("loginMember",loginMember);
        return "loginIndex";
    }*/




}


