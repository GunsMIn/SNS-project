package com.example.crudpersional.controller;

import com.example.crudpersional.domain.dto.Response;
import com.example.crudpersional.domain.dto.post.LikeResponse;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.mvc.dto.LikeRequest;
import com.example.crudpersional.service.LikeService;
import com.example.crudpersional.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Api(tags = "Like(좋아요 API)")
@Slf4j
public class LikeController {
    private final PostService postService;
    private final LikeService likeService;


    @ApiOperation(value = "해당 글 좋아요", notes = "정상적인 JWT토큰 발급 받은 사용자만 해당 글 좋아요 가능")
    @PostMapping("/{postId}/likes")
    public Response<String> like(@PathVariable(name = "postId") Long postId, @ApiIgnore Authentication authentication) {
        LikeResponse response = likeService.likes(postId, authentication.getName());
        return Response.successToMessage(response.getMessage());
    }

    @ApiOperation(value = "해당 글 좋아요 갯수", notes = "해당 postId에 해당하는 글의 좋아요 count 구하는 API")
    @GetMapping("/{postId}/likes")
    public Response<Integer> getLikeCount(@PathVariable(name = "postId") Long postId) {
        Integer likeCount = likeService.getLikeCount(postId);
        return Response.success(likeCount);
    }










}
