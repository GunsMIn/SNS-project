package com.example.crudpersional.controller;

import com.example.crudpersional.domain.dto.Response;
import com.example.crudpersional.domain.dto.post.*;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.service.PostService;
import com.example.crudpersional.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

/*  "userId":"2",
    "title":"박두식",
   "body":"1234"*/
    @GetMapping("/api/v1/posts/{postId}")
    public Response<PostSelectResponse> get(@PathVariable Long postId) {

        PostSelectResponse postSelectResponse = postService.getPost(postId);
        return Response.success(postSelectResponse);
    }

    @GetMapping("/api/v1/posts")
    public Page<PostSelectResponse> getAll(@PageableDefault(size = 20, sort ="registeredAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        List<PostSelectResponse> posts = postService.getPosts(pageable);

        return new PageImpl<>(posts);
    }


    @PostMapping("/api/v1/posts")
    public Response<PostAddResponse> add(@RequestBody PostAddRequest postAddRequest) {
        log.info("postAddRequest : {}", postAddRequest);

        PostAddResponse postAddResponse = postService.addPost(postAddRequest);
        return Response.success(postAddResponse);
    }

    @PutMapping("/api/v1/posts/{postId}")
    public Response<PostUpdateResponse> update(@PathVariable Long postId, @RequestBody PostUpdateRequest postUpdateRequest) {
        log.info("수정 controller :{}", postUpdateRequest);
        PostUpdateResponse postUpdateResponse = postService.updatePost(postId, postUpdateRequest);
        return Response.success(postUpdateResponse);
    }

    @DeleteMapping("/api/v1/posts/{postId}/{userId}")
    public Response<PostDeleteResponse> delete(@PathVariable Long postId,@PathVariable Long userId) {
        PostDeleteResponse deletePost = postService.deletePost(postId,userId);
        return Response.success(deletePost);
    }
}
