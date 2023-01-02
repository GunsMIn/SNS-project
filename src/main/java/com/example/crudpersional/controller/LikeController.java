package com.example.crudpersional.controller;

import com.example.crudpersional.domain.dto.Response;
import com.example.crudpersional.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final PostService postService;

    @ApiOperation(value = "해당 글 좋아요", notes = "정상적인 JWT토큰 발급 받은 사용자만 해당 글 좋아요 가능")
    @PostMapping("/api/v1/{postId}/likes")
    public Response<Void> like(@PathVariable Long postId, @ApiIgnore Authentication authentication) {
        String authenticationName = authentication.getName();
        postService.like(postId,authenticationName);
        return Response.success(String.format("%s번의 글 좋아요(Like) 성공",postId));
    }

    @ApiOperation(value = "해당 글 좋아요 갯수", notes = "해당 postId에 해당하는 글의 좋아요 count 구하는 API")
    @GetMapping("/api/v1/{postId}/likes")
    public Response<String> getLikeCount(@PathVariable Long postId) {
        Integer likeCount = postService.getLikeCount(postId);
        return Response.successToMessage(String.format("%s번 게시글의 좋아요 개수 : %d", postId, likeCount));
    }
}
