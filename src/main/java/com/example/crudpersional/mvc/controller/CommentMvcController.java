package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.domain.dto.Response;
import com.example.crudpersional.domain.dto.comment.CommentResponse;
import com.example.crudpersional.domain.dto.comment.PostCommentRequest;
import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.mvc.dto.*;
import com.example.crudpersional.repository.CommentRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import com.example.crudpersional.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentMvcController {
    private final PostService postService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;


    @PostMapping("/comments/write")
    @ResponseBody
    public RsData<Comment> write(@RequestBody CommentForm commentForm) {
        log.info("들어온다다잗자닺ㄷㅈㄷ");
        log.info("댓글 작성자:{} , 내용 :{}",commentForm.getId(),commentForm.getContent());
        log.info("글 번호:{}",commentForm.getPostId());
        User user = userRepository.findById(commentForm.getId()).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        CommentResponse commentResponse = postService.writeComment(commentForm.getPostId(), commentForm.getContent(), user.getUsername());
        return new RsData(
                "S-1",
                "메세지가 작성되었습니다.",
                new WriteMessageResponse(commentResponse.getId())
        );
    }

    /**내용을 가져옴**/
    @ResponseBody
    @GetMapping("/chat/messages")
    public RsData<MessagesResponse> messages(Long fromId,Long postId) {

        Post post = postRepository.findById(postId).get();
        //메세지 리스트
        log.debug("들어오자 !!! req:{}",fromId);
        List<Comment> chatMessages = commentRepository.findAllByPost(post);
        //subList될 저장소
        List<Comment> messagesEntity = chatMessages;
        List<CommentResponse> messages = messagesEntity.stream().map(c -> new CommentResponse(c.getId(),
                c.getComment(), c.getUser().getUsername(),
                c.getPost().getId(), c.getRegisteredAt(),
                c.getUpdatedAt())).collect(Collectors.toList());
        log.info("메세지 리스트 : {}",messages);
        //req.getFromId 에서의 fromId값으로 !
        if (fromId != null) {
            // 0 부터 messageList의 size만큼의 int 생성
            // 해당하는 인덱스를 찾고 안나오면 -1 반환
            int index = IntStream.range(0, messages.size()) // 0부터 messageList의 size만큼 반복
                    .filter(i -> chatMessages.get(i).getId() == fromId)
                    .findFirst() // 찾으면 멈춘다.
                    .orElse(-1);
            //해당 인덱스를 찾았다면
            if (index != -1) {
                //찾은 index 전을 날리는 것이다. 찾은 index후부터 list 에 담긴다.
                messages = messages.subList(index + 1, messages.size());
                // 0 1 2 3 4 ->5개
                // 1 2 3 4 5
            }
        }
        return new RsData<>(
                "S-1",
                "성공",
                new MessagesResponse(messages,messages.size())
        );
    }

    /**댓글 달기**/
    @ApiOperation(value = "해당 포스트 댓글 달기 ", notes = "postId로 들어온 Post글 댓글 달기 API")
    @ResponseBody
    @PostMapping("/api/v1/posts/mvc/{id}/comments")
    public Response<CommentResponse> commentFromPost2(@PathVariable Long id, @RequestBody PostCommentRequest postCommentRequest) {
        log.info("postCommentRequest:{}",postCommentRequest);
        CommentResponse commentResponse = postService.writeComment(id, postCommentRequest.getComment(), postCommentRequest.getName());
        return Response.success(commentResponse);
    }

    @GetMapping("/toDoList")
    public String goTodoList() {
        return "toDoList";
    }
}
