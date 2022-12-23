
package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.domain.dto.post.PostAddRequest;
import com.example.crudpersional.domain.dto.post.PostSelectResponse;
import com.example.crudpersional.domain.entity.Post;
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


    @GetMapping("/posts/form")
    public String goWriteForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginMember, @ModelAttribute PostForm postForm, Model model) {
        log.info("글 작성 뷰");
        String userName = loginMember.getUserName();

        log.info("userName : {}", userName);
        postForm.setUserName(userName);
        return "writePost";
    }

    @PostMapping("/posts/doForm")
    public String doWriteForm(@ModelAttribute PostAddRequest postAddRequest,String userName) {
        log.info("제목과 내용 : {} ",postAddRequest);
        log.info("이름 : {} ",userName);
        postService.addPost(postAddRequest,userName);
        return "redirect:/";
    }


    @GetMapping("/posts/list")
    public String getPostList(@PageableDefault(page = 0 ,size = 10, sort ="registeredAt",
            direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        log.info("list에는 들어오나?");
        Page<Post> posts = postService.getViewPosts(pageable);
        //페이지블럭 처리
        //1을 더해주는 이유는 pageable은 0부터라 1을 처리하려면 1을 더해서 시작해주어야 한다.
        int nowPage = posts.getPageable().getPageNumber() + 1;
        //-1값이 들어가는 것을 막기 위해서 max값으로 두 개의 값을 넣고 더 큰 값을 넣어주게 된다.
        int startPage =  Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage+9, posts.getTotalPages());
        log.info("list:{}",posts);
        model.addAttribute("posts", posts);
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "post/postList";
    }



    //포스트 상세보기
    @GetMapping("/post/getOne/{id}")
    public String getPost(@PathVariable Long id) {
        log.info("id :{}" ,id);
        PostSelectResponse post = postService.getPost(id);
        return "post/list";
    }





}

