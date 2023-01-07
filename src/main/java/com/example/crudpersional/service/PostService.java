package com.example.crudpersional.service;

import com.example.crudpersional.domain.dto.alarm.AlarmResponse;
import com.example.crudpersional.domain.dto.comment.CommentResponse;
import com.example.crudpersional.domain.dto.comment.CommentUpdateResponse;
import com.example.crudpersional.domain.dto.comment.PostMineDto;
import com.example.crudpersional.domain.dto.post.*;
import com.example.crudpersional.domain.entity.*;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.LikeException;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.mvc.dto.PostForm;
import com.example.crudpersional.mvc.dto.PostResponse;
import com.example.crudpersional.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.crudpersional.domain.entity.alarm.AlarmType.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeEntityRepository;
    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;

    /**글 단건 조회**/
    @Transactional(readOnly = true)
    public PostSelectResponse getPost(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        //해당 포스트를 찾지 못했을 때
        Post post = postOptional
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND, postId+"번의 글을 찾을 수 없습니다."));

        PostSelectResponse postSelectResponse = new PostSelectResponse(post);
        return postSelectResponse;
    }

    /**글 전체 조회**/
    @Transactional(readOnly = true)
    public List<PostSelectResponse> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        //stream을 이용해서 엔티티를 응답객체로 변경
        List<PostSelectResponse> postSelectResponseList =
                posts.stream().map(p -> new PostSelectResponse(p)).collect(Collectors.toList());
        return postSelectResponseList;
    }

    /**글 제목으로 조회**/
    @Transactional(readOnly = true)
    public List<PostSelectResponse> getPostsByTitle (Pageable pageable,String title) {
        Page<Post> posts = postRepository.findByTitleContaining(pageable, title);
        List<PostSelectResponse> postSelectResponseList =
                posts.stream().map(p -> new PostSelectResponse(p)).collect(Collectors.toList());
        return postSelectResponseList;
    }

    /**글 등록**/                                                 //인증으로 들어온 userName
    public PostAddResponse addPost(PostAddRequest postAddRequest, String userName) {
        //userName으로 해당 User엔티티 찾아옴
        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, "회원가입 후 작성해주세요"));

        Post post = postAddRequest.toEntity(user);
        //save를 할때는 JpaRepository<Article,Long>를 사용해야 하기때문에
        //articleRequestDto -> 를 Article 타입으로 바꿔줘야한다.
        Post savedPost = postRepository.save(post);
        PostAddResponse postAddResponse = new PostAddResponse("포스트 등록 완료",savedPost.getId());
        return postAddResponse;
    }

    /**글 수정과 삭제에서 사용 될 권한 체크 메서드**/
    /**관리자와 해당 포스트 작성회원만 삭제 수정 가능**/
    private void check(Post post, User user) {
        if (user.getRole() != UserRole.ADMIN && user.getId() != post.getUser().getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION, user.getUsername()+ "님은"
                    + post.getId()+"글을 수정.삭제 할 수 있는 권한이없습니다");
        }
    }

    /**글 수정**/
    public PostUpdateResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest,String userName) {

        Post findPost =
                postRepository.findById(postId).orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND,"해당 글 없습니다"));
        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s not founded", userName)));
        // 수정 권한 확인
        check(findPost, user);
        //변경감지 수정 메서드
        findPost.update(postUpdateRequest.getTitle(),postUpdateRequest.getBody());

        PostUpdateResponse postUpdateResponse = new PostUpdateResponse("포스트 수정 완료", findPost.getId());
        return postUpdateResponse;

    }
    /**글 삭제**/
    public PostDeleteResponse deletePost(Long postId, String userName) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Post post =
                optionalPost.orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND, postId + "번 글은 존재하지 않아서 삭제할 수 없습니다."));

        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s not founded", userName)));

        //글을 쓴 유저가 아닌 다른 사람이 해당 글을 지우려고 할 때 예외 + admin은 허용
        check(post, user);
        //위의 check 메서드 통과 시 글 삭제
        postRepository.delete(post);
        PostDeleteResponse deleteResponse = new PostDeleteResponse("포스트 삭제 완료", post.getId());
        return deleteResponse;
    }

    /**내가 쓴 post 보기**/
    @Transactional(readOnly = true)
    public Page<PostMineDto> getMyPeed(String userName, Pageable pageable) {
        User user = userRepository.findOptionalByUserName(userName).orElseThrow(() ->
                new UserException(ErrorCode.USERNAME_NOT_FOUND,String.format("%s not founded",userName)));

        Page<Post> postsByUser = postRepository.findPostsByUser(user, pageable);
        //아래의 map()의 과정은 Page<Post> => Page<PostMineDto> 로 변환과정
        return postsByUser.map(PostMineDto::fromEntity);
    }

    /**알람 페이징 조회 20개 **/
    @Transactional(readOnly = true)
    public Page<AlarmResponse> getAlarms(String userName,Pageable pageable) {
        User user = userRepository.findOptionalByUserName(userName).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
        Page<AlarmEntity> alarmEntities = alarmRepository.findByUser(user,pageable);
        Page<AlarmResponse> alarmResponses = AlarmResponse.toResponse(alarmEntities);
        return alarmResponses;
    }




    /**MVC Service🔽 (not RestApi Service)**/
    /***********************************************MVC********************************************************/
    public Post addMvcPost(PostForm postForm, String userName) {
        log.info("서비스 userName:{}",userName);
        //userName으로 해당 User엔티티 찾아옴
        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, "회원가입 후 작성해주세요"));

        Post post = postForm.toEntity(user);
        //save를 할때는 JpaRepository<Article,Long>를 사용해야 하기때문에
        //articleRequestDto -> 를 Article 타입으로 바꿔줘야한다.
        Post savedPost = postRepository.save(post);
        return savedPost;
    }


    public void updateMvcPost(Long id,PostForm postForm) {

        Post post = postRepository.findById(id).orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND, "해당 포스트는 없습니다"));
        post.setTitle(postForm.getTitle());
        post.setBody(postForm.getBody());
    }

    public void deleteMvcPost(Long id) {
        postRepository.deleteById(id);
    }

    public Page<PostResponse> getViewPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return PostResponse.toDtoList(posts);
    }

    public Page<PostResponse> searchByTitle(Pageable pageable,String title) {
        Page<Post> posts = postRepository.findByTitleContaining(pageable, title);
        return PostResponse.toDtoList(posts);
    }



}
