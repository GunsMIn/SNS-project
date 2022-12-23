
package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.domain.dto.post.PostAddRequest;
import com.example.crudpersional.domain.dto.post.PostSelectResponse;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.mvc.dto.PostForm;
import com.example.crudpersional.mvc.dto.SessionConst;
import com.example.crudpersional.repository.UserRepository;
import com.example.crudpersional.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostMvcController {

    private final PostService postService;
    private final UserRepository userRepository;

  /*  @GetMapping("/posts/form")
    public String goWriteForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginMember, @ModelAttribute PostAddRequest postAddRequest, Model model) {
        log.info("글 작성 뷰");
        String userName = loginMember.getUserName();


        model.addAttribute("userName", userName);
        User user = userRepository.findOptionalByUserName(userName)
                .get();
        model.addAttribute("userId", user.getId());
        log.info("userid : {}" ,user.getId());
        return "/post/writePost";
    }*/

    @GetMapping("/posts/form")
    public String goWriteForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginMember, @ModelAttribute PostForm postForm, Model model) {
        log.info("글 작성 뷰");
        String userName = loginMember.getUserName();

        log.info("userName : {}", userName);
        postForm.setUserName(userName);
        return "/post/writePost";
    }



    @GetMapping("/posts/list")
    public String getPostList(@PageableDefault(size = 20, sort ="registeredAt",
            direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        log.info("list에는 들어오나?");
        List<PostSelectResponse> posts = postService.getPosts(pageable);
        log.info("list:{}",posts);
        model.addAttribute("posts", posts);
        return "post/postList";
    }



    //포스트 상세보기
    @GetMapping("/post/getOne/{id}")
    public String getPost(@PathVariable Long id) {
        log.info("id :{}" ,id);
        PostSelectResponse post = postService.getPost(id);
        return "/posts/list";
    }



        @PostMapping("/posts/form")
    public String doWriteForm(@ModelAttribute PostAddRequest postAddRequest,String userName) {
        log.info("제목과 내용 : {} ",postAddRequest);
        log.info("이름 : {} ",userName);
        postService.addPost(postAddRequest,userName);
        return "redirect:/";
    }

}

