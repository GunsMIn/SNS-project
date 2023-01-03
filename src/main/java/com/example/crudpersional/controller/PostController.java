package com.example.crudpersional.controller;

import com.example.crudpersional.domain.dto.Response;
import com.example.crudpersional.domain.dto.alarm.AlarmResponse;
import com.example.crudpersional.domain.dto.comment.*;
import com.example.crudpersional.domain.dto.post.*;
import com.example.crudpersional.domain.dto.user.UserDeleteRequest;
import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.service.PostService;
import com.example.crudpersional.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    //인증 필요 없음
    @ApiOperation(value = "글 단건 조회", notes = "post ID를 통해 글 정보를 조회한다.")
    @GetMapping("/api/v1/posts/{postId}")
    public Response<PostSelectResponse> get(@PathVariable Long postId) {
        PostSelectResponse postSelectResponse = postService.getPost(postId);
        return Response.success(postSelectResponse);
    }

    //인증 필요 없음
    //post list 페이징 처리 1. 20개 , 2. 최신순
    @ApiOperation(value = "글 전체 조회", notes = "post 전체 조회(1. 20개 페이징, 2.최신순 정렬)")
    @GetMapping("/api/v1/posts")
    public Response<PageImpl<PostSelectResponse>> getAll(@PageableDefault(size = 20, sort ="registeredAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        List<PostSelectResponse> posts = postService.getPosts(pageable);
        return Response.success(new PageImpl<>(posts));
    } //

    @ApiOperation(value = "글 등록", notes = "정상적인 JWT토큰 발급 받은 사용자만 글 등록 가능")
    @PostMapping("/api/v1/posts")
    public Response<PostAddResponse> add(@RequestBody PostAddRequest postAddRequest, @ApiIgnore Authentication authentication) {
        //여기 아래에서 jwt토큰을 사용한 userName을 가져와서 service단에서 처리
        PostAddResponse postAddResponse = postService.addPost(postAddRequest,authentication.getName());
        return Response.success(postAddResponse);
    }

    @ApiOperation(value = "글 수정", notes = "정상적인 JWT토큰 발급 받은 사용자만 글 수정 가능")
    @PutMapping("/api/v1/posts/{id}")
    public Response<PostUpdateResponse> update(@PathVariable Long id, @RequestBody PostUpdateRequest postUpdateRequest,@ApiIgnore Authentication authentication) {
        log.info("수정 controller :{}", postUpdateRequest);
        PostUpdateResponse postUpdateResponse = postService.updatePost(id, postUpdateRequest,authentication.getName());
        return Response.success(postUpdateResponse);
    }

    @ApiOperation(value = "글 삭제", notes = "정상적인 JWT토큰 발급 받은 사용자만 글 삭제 가능")
    @DeleteMapping("/api/v1/posts/{id}")
    public Response<PostDeleteResponse> delete(@PathVariable Long id,@ApiIgnore Authentication authentication) {
        PostDeleteResponse deletePost = postService.deletePost(id,authentication.getName());
        return Response.success(deletePost);
    }



    @ApiOperation(value = "나의 글 보기", notes = "내가 쓴 포스트 보는 API")
    @GetMapping("/api/v1/posts/my")
    public Response<Page<PostMineDto>> my(@PageableDefault(size = 20, sort ="registeredAt",
            direction = Sort.Direction.DESC) Pageable pageable,@ApiIgnore Authentication authentication) {
        Page<PostMineDto> myPost = postService.getMyPost(authentication.getName(),pageable);
        return Response.success(myPost);
    }

    @ApiOperation(value = "알림 페이징 조회(최신순)", notes = "내가 쓴 포스트 보는 API")
    @GetMapping("/api/v1/posts/alarms")
    public Response<Page<AlarmResponse>> getAlarms(@PageableDefault(size = 20, sort ="registeredAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AlarmResponse> alarmResponses = postService.getAlarms(pageable);
        return Response.success(alarmResponses);
    }


}
