package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.domain.dto.Response;
import com.example.crudpersional.domain.dto.comment.CommentDeleteResponse;
import com.example.crudpersional.domain.dto.comment.CommentResponse;
import com.example.crudpersional.domain.dto.comment.PostCommentRequest;
import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.mvc.dto.*;
import com.example.crudpersional.mvc.ssr.SseEmitters;
import com.example.crudpersional.repository.CommentRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import com.example.crudpersional.service.CommentService;
import com.example.crudpersional.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@ApiIgnore
@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentMvcController {
    private final CommentService commentService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final SseEmitters sseEmitters;


    @PostMapping("/comments/write")
    @ResponseBody
    public RsData<Comment> write(@RequestBody CommentForm commentForm) {
        User user = userRepository.findById(commentForm.getId()).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        CommentResponse commentResponse = commentService.writeComment(commentForm.getPostId(), commentForm.getContent(), user.getUsername());

        /**?????? ??? ????????? SSE ????????? ??? / ????????? ?????? ?????? **/
        sseEmitters.noti("chat__messageAdded");
        return new RsData(
                "S-1",
                "???????????? ?????????????????????.",
                new WriteMessageResponse(commentResponse.getId())
        );
    }

    /**????????? ?????????**/
    @ResponseBody
    @GetMapping("/chat/messages")
    public RsData<MessagesResponse> messages(Long fromId,Long postId) {
        Post post = postRepository.findById(postId).get();
        //????????? ?????????
        List<Comment> chatMessages = commentRepository.findAllByPost(post);
        //subList??? ?????????
        List<Comment> messagesEntity = chatMessages;
        List<CommentResponse> messages = messagesEntity.stream().map(c -> new CommentResponse(c.getId(),
                c.getComment(), c.getUser().getUsername(),
                c.getPost().getId(), c.getRegisteredAt(),
                c.getUpdatedAt())).collect(Collectors.toList());
        log.info("????????? ????????? : {}",messages);
        if (fromId != null) {
            // 0 ?????? messageList??? size????????? int ??????
            // ???????????? ???????????? ?????? ???????????? -1 ??????
            int index = IntStream.range(0, messages.size()) // 0?????? messageList??? size?????? ??????
                    .filter(i -> chatMessages.get(i).getId() == fromId)
                    .findFirst() // ????????? ?????????.
                    .orElse(-1);
            //?????? ???????????? ????????????
            if (index != -1) {
                //?????? index ?????? ????????? ?????????. ?????? index????????? list ??? ?????????.
                messages = messages.subList(index + 1, messages.size());
            }
        }
        return new RsData<>(
                "S-1",
                "??????",
                new MessagesResponse(messages,messages.size())
        );
    }


    /**?????? ??????**/
    @ApiOperation(value = "?????? ????????? ?????? ?????? ", notes = "postId??? ????????? Post??? ?????? ?????? API")
    @ResponseBody
    @PostMapping("/api/v1/posts/mvc/{id}/comments")
    public Response<CommentResponse> commentFromPost2(@PathVariable Long id, @RequestBody PostCommentRequest postCommentRequest, @SessionAttribute(name = "loginMember", required = false) User loginMember) {
        log.info("postCommentRequest:{}",postCommentRequest);
        CommentResponse commentResponse = commentService.writeComment(id, postCommentRequest.getComment(),loginMember.getUsername());
        return Response.success(commentResponse);
    }


    /**?????? ??????**/
    @ResponseBody
    @PostMapping("/post/getOne/api/v1/posts/{postId}/commentMvc/{commentId}/mvc")
    public Response<CommentDeleteResponse> deleteCommentMVC(@PathVariable Long postId,@PathVariable Long commentId , @RequestBody UserNameDto userNameDto,@SessionAttribute(name = "loginMember", required = false) User loginMember) {
        log.info("2??? postId:{} / commentId:{} / userName :{} ", postId, commentId,loginMember.getUsername());
        if (loginMember == null) {
            throw new UserException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage());
        }
        commentService.deleteComment(postId,commentId,loginMember.getUsername());
        return Response.success(new CommentDeleteResponse("?????? ?????? ??????",postId));
    }

    @GetMapping("/toDoList")
    public String goTodoList() {
        return "toDoList";
    }
}
