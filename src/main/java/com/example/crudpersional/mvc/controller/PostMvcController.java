package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.domain.dto.post.PostAddRequest;
import com.example.crudpersional.mvc.dto.PostForm;
import com.example.crudpersional.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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


}
