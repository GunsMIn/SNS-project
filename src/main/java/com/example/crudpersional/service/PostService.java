package com.example.crudpersional.service;

import com.example.crudpersional.domain.dto.post.*;
import com.example.crudpersional.domain.dto.user.UserDeleteRequest;
import com.example.crudpersional.domain.entity.LikeEntity;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.LikeException;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.mvc.dto.PostForm;
import com.example.crudpersional.repository.CommentRepository;
import com.example.crudpersional.repository.LikeRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeEntityRepository;
    //private final CommentRepository commentRepository;

    /**글 단건 조회**/
    public PostSelectResponse getPost(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Post post = postOptional.orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND,"해당 글 없습니다"));

        PostSelectResponse postSelectResponse =
                new PostSelectResponse(post.getId(), post.getTitle(),
                        post.getBody(), post.getUser().getUsername(),
                        post.getRegisteredAt(), post.getUpdatedAt());

        return postSelectResponse;
    }

    /**글 전체 조회**/
    public List<PostSelectResponse> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        List<PostSelectResponse> postSelectResponseList =
                posts.stream().map(p -> new PostSelectResponse(p)).collect(Collectors.toList());

        return postSelectResponseList;
    }

    /**글 제목으로 조회**/
    public List<PostSelectResponse> getPostsByTitle (Pageable pageable,String title) {
        Page<Post> posts = postRepository.findByTitleContaining(pageable, title);
        List<PostSelectResponse> postSelectResponseList =
                posts.stream().map(p -> new PostSelectResponse(p)).collect(Collectors.toList());
        return postSelectResponseList;
    }

    /**글 등록**/                                                 //인증으로 들어온 userName
    public PostAddResponse addPost(PostAddRequest postAddRequest, String userName) {
        log.info("서비스 userName:{}",userName);
        //userName으로 해당 User엔티티 찾아옴
        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, "회원가입 후 작성해주세요"));

        Post post = postAddRequest.toEntity(user);
        //save를 할때는 JpaRepository<Article,Long>를 사용해야 하기때문에
        //articleRequestDto -> 를 Article 타입으로 바꿔줘야한다.
        Post savedPost = postRepository.save(post);
        if (savedPost.getId() == null) {
            throw new RuntimeException("해당 파일은 존재하지 않습니다");
        }
        PostAddResponse postAddResponse = new PostAddResponse("포스트 등록 완료",savedPost.getId());
        return postAddResponse;
    }

    /**글 수정**/
    public PostUpdateResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest,String userName) {
        log.info("수정 요청 dto :{}", postUpdateRequest);
        Post findPost =
                postRepository.findById(postId).orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND,"해당 글 없습니다"));

        log.info("userName:{}",userName);

        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s not founded", userName)));
        Long userId = user.getId();
        // 수정 권한 확인
        if (userId != findPost.getUser().getId()) {
            throw new PostException(ErrorCode.INVALID_PERMISSION, "해당 회원은 수정할 권한이 없습니다");
        }
        //변경감지 수정 메서드
        findPost.update(postUpdateRequest.getTitle(),postUpdateRequest.getBody());

        PostUpdateResponse postUpdateResponse = new PostUpdateResponse("포스트 수정 완료", findPost.getId());
        return postUpdateResponse;

    }
    /**글 삭제**/
    public PostDeleteResponse deletePost(Long postId, String userName) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Post post =
                optionalPost.orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않아서 삭제할 수 없습니다."));

        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(()
                        -> new UserException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s not founded", userName)));

        //글을 쓴 유저가 아닌 다른 사람이 해당 글을 지우려고 할 때 예외
        if (user.getId() != post.getUser().getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION, "당신을 글을 지울 수 있는 권한이없습니다");
        }

        postRepository.delete(post);
        PostDeleteResponse deleteResponse = new PostDeleteResponse("포스트 삭제 완료", post.getId());
        return deleteResponse;
    }

    /**like**/
    public void like(Long postId,String userName) {
        //해당 글 찾음
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다"));
        //해당 유저 찾음
        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s님은 존재하지 않습니다.", userName)));

        //글(post) 회원(user) 찾음으로 like 눌렀는지 확인
        //ifPresent() 메소드 = 값을 가지고 있는지 확인 후 예외처리 / 값이 존재한다면 예외처리 진행
        likeEntityRepository.findByUserAndPost(user,post)
                .ifPresent(item -> new LikeException(ErrorCode.ALREADY_LIKED, String.format("이미 %d번 글의 좋아요를 눌렀습니다",postId)));

        LikeEntity like = LikeEntity.of(user, post);
        likeEntityRepository.save(like);
    }

    /**
     * 해당 글 좋아요 개수
     * @PathVarable로 들어오는 postId로 post entity조회 후 좋아요 count 계산 후 반환
     * */
    public Integer getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다."));

        Integer postLikeCount = likeEntityRepository.countByPost(post);
        return postLikeCount;
    }









    /***********************************************MVC********************************************************/
    public Post addMvcPost(PostForm postAddRequest, String userName) {
        log.info("서비스 userName:{}",userName);
        //userName으로 해당 User엔티티 찾아옴
        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, "회원가입 후 작성해주세요"));

        Post post = postAddRequest.toEntity(user);
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

    public Page<Post> getViewPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> searchByTitle(Pageable pageable,String title) {
        return postRepository.findByTitleContaining(pageable,title);
    }

}
