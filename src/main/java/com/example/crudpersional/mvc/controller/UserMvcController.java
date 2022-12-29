
package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.domain.dto.user.UserJoinRequest;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.mvc.dto.LoginForm;
import com.example.crudpersional.mvc.dto.MemberForm;
import com.example.crudpersional.mvc.dto.SessionConst;
import com.example.crudpersional.mvc.service.UserMvcService;
import com.example.crudpersional.repository.UserRepository;
import com.example.crudpersional.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.net.http.HttpRequest;
import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserMvcController {

    private final UserMvcService userService;
    private final UserRepository userRepository;


    @GetMapping("/members/joinUser")
    public String joinUser(@ModelAttribute MemberForm memberForm){
        return "members/join";
    }

    @PostMapping("/members/doJoinForm")
    public String doJoin(@Validated @ModelAttribute MemberForm memberForm , BindingResult result, HttpServletResponse response)  throws Exception{
        if (result.hasErrors()) {
            return "members/join";
        }
        log.info("유저:{}", memberForm);
        userService.join(memberForm);
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script>alert('반갑습니다🤗, 이제 로그인 후 글을 작성하실 수 있습니다.저희 사이트는 더 개발을 진행중이니 기대해주세요😎');location.assign('/');</script>");
        out.flush();
        return "/";
    }

    @GetMapping("/members/loginForm")
    public String goLogin(@ModelAttribute LoginForm loginForm) {
        return "members/login";
    }

    @PostMapping("/members/doLoginForm")
    public String doLogin(@Validated @ModelAttribute LoginForm loginForm, BindingResult result,Model model,HttpServletRequest request) {
        if (result.hasErrors()) {
            return "members/login";
        }
        //세션 로그인 사용
        User user = userService.loginMvc(loginForm.getUserName(), loginForm.getPassword());

        if(user==null){
            return "members/loginForm";
        }

        HttpSession session = request.getSession(true); // 세션이 없다면 새로운 세션 생성
        session.setAttribute(SessionConst.LOGIN_MEMBER,user);
        log.info("로그인 user :{} ",user);
        log.info("세션 저장 된 user :{}",session.getAttribute(SessionConst.LOGIN_MEMBER));
        model.addAttribute("member", user);
        return "redirect:/members/loginIndex";
    }

   @GetMapping("/members/loginIndex")
    public String goLoginIndex(@SessionAttribute(name = "loginMember", required = false) User loginMember,@ModelAttribute LoginForm loginForm,Model model) {
        model.addAttribute("member", loginMember);
        return "loginindex";
    }


    @PostMapping("/members/logout")
    public String logOut(HttpServletRequest request , HttpServletResponse response) throws Exception {
        //HttpSession이 존재하면 현재 HttpSession을 반환하고 존재하지 않으면 새로이 생성하지 않고 그냥 null을 반환합니다
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('로그아웃 성공! 다음에 또 찾아주세요🤗');location.assign('/');</script>");
            out.flush();
        }
        return "/";
    }







}


