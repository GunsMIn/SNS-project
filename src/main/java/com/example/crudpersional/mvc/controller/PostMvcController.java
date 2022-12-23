
package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.domain.dto.post.PostAddRequest;
import com.example.crudpersional.domain.dto.post.PostSelectResponse;
import com.example.crudpersional.mvc.dto.PostForm;
import com.example.crudpersional.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostMvcController {

    private final PostService postService;


    @GetMapping("/posts/form")
    public String goWriteForm(@ModelAttribute PostAddRequest postAddRequest) {
        log.info("뷰는 들어오는거야?");
        return "/post/writePost";
    }

    @PostMapping("/posts/form")
    public String doWriteForm(@ModelAttribute PostAddRequest postAddRequest) {
        log.info("제목과 내용 : {} ",postAddRequest);
        postService.addPost(postAddRequest);
        return "redirect:/";
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


}

