package com.example.crudpersional.controller;

import com.example.crudpersional.domain.dto.Response;
import com.example.crudpersional.domain.dto.comment.*;
import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class CommentController {
    private final PostService postService;

    /**댓글 달기**/
    @ApiOperation(value = "해당 포스트 댓글 달기 ", notes = "postId로 들어온 Post글 댓글 달기 API")
    @PostMapping("/{postId}/comments")
    public Response<CommentResponse> commentFromPost(@PathVariable Long postId, @RequestBody PostCommentRequest postCommentRequest,@ApiIgnore Authentication authentication) {
        CommentResponse commentResponse = postService.writeComment(postId, postCommentRequest.getComment(), authentication.getName());
        return Response.success(commentResponse);
    }



    /**댓글 수정**/
    @ApiOperation(value = "해당 포스트 댓글 수정하기 ", notes = "댓글 수정 하기 API")
    @PutMapping("/{postId}/comments/{commentId}")
    public Response<CommentUpdateResponse> updateComment(@PathVariable Long postId,@PathVariable Long commentId, @RequestBody CommentModifyRequest commentModifyRequest, @ApiIgnore Authentication authentication) {
        CommentUpdateResponse response = postService.modifyComment(postId, commentId,commentModifyRequest.getComment(), authentication.getName());
        return Response.success(response);
    }

    /**댓글 삭제**/
    @ApiOperation(value = "해당 포스트 댓글 삭제하기 ", notes = "댓글 삭제 하기 API")
    @DeleteMapping("/{postId}/comments/{commentId}")
    public Response<CommentDeleteResponse> deleteComment(@PathVariable Long postId,@PathVariable Long commentId ,@ApiIgnore Authentication authentication) {
        postService.deleteComment(postId,commentId,authentication.getName());
        return Response.success(new CommentDeleteResponse("댓글 삭제 완료",postId));
    }

    /**해당 포스트 댓글 조회(최신순)**/
    @ApiOperation(value = "해당 포스트 댓글 최신순 조회", notes = "id : 포스트 번호(@PathVariable) , paging : 20개 , 최신순 정렬 API")
    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> getComments(@PathVariable Long postId,
                                                       @PageableDefault(size = 10,
                                                               sort = "registeredAt",
                                                               direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CommentResponse> commentResponses = postService.getComments(postId, pageable);
        return Response.success(commentResponses);
    }



}
