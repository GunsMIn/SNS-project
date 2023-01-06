package com.example.crudpersional.controller;

import com.example.crudpersional.domain.dto.Response;
import com.example.crudpersional.domain.dto.post.LikeResponse;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.mvc.dto.LikeRequest;
import com.example.crudpersional.mvc.service.LikeService;
import com.example.crudpersional.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class LikeController {
    private final PostService postService;
    private final LikeService likeService;



    @ApiOperation(value = "해당 글 좋아요", notes = "정상적인 JWT토큰 발급 받은 사용자만 해당 글 좋아요 가능")
    @PostMapping("/{postId}/likes")
    public Response<String> like(@PathVariable Long postId, @ApiIgnore Authentication authentication) {
        LikeResponse like = postService.like(postId, authentication.getName());
        return Response.successToMessage(like.getMessage());
    }

    @ApiOperation(value = "해당 글 좋아요 갯수", notes = "해당 postId에 해당하는 글의 좋아요 count 구하는 API")
    @GetMapping("/{postId}/likes")
    public Response<String> getLikeCount(@PathVariable Long postId) {
        Integer likeCount = postService.getLikeCount(postId);
        return Response.successToMessage(String.format("%s번 게시글의 좋아요 개수 : %d", postId, likeCount));
    }


/*    @ApiOperation(value = "해당 글 좋아요", notes = "정상적인 JWT토큰 발급 받은 사용자만 해당 글 좋아요 가능")
    @PostMapping("/mvc/{postId}/likes")
    public Response<String> likeMvc(@PathVariable Long postId, @SessionAttribute(name = "loginMember", required = false) User loginMember) {
       log.info("좋아요 버튼 클릭 후 값 :{},,{}",postId,loginMember);
        LikeResponse like = postService.like(postId, loginMember.getUsername());
        return Response.successToMessage(like.getMessage());
    }*/

    @ApiOperation(value = "해당 글 좋아요", notes = "정상적인 JWT토큰 발급 받은 사용자만 해당 글 좋아요 가능")
    @PostMapping("/mvc/likes")
    public Response likeMvc(@RequestBody LikeRequest request, @SessionAttribute(name = "loginMember", required = false) User loginMember, HttpServletResponse response) throws Exception {
        log.info("좋아요 버튼 클릭 후 값 :{},,{}",request.getPostId(),loginMember);
            //세션에 저장된 user의 정보
        likeService.AddLike(request.getPostId(), loginMember.getUsername());
        return Response.success("좋아요를 눌렀습니다.");
    }






}
