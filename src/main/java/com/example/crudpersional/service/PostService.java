package com.example.crudpersional.service;

import com.example.crudpersional.domain.dto.post.*;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public PostSelectResponse getPost(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Post post = postOptional.orElseThrow(() -> new RuntimeException("해당 글 없습니다"));
        PostSelectResponse postSelectResponse =
                new PostSelectResponse(post.getId(), post.getTitle(),
                        post.getBody(), post.getUser().getUserName(),
                        post.getRegisteredAt(), post.getUpdatedAt());
        return postSelectResponse;
    }

    public List<PostSelectResponse> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        List<PostSelectResponse> postSelectResponseList =
                posts.stream().map(p -> new PostSelectResponse(p)).collect(Collectors.toList());

        return postSelectResponseList;
    }

    public PostAddResponse addPost(PostAddRequest postAddRequest) {

        User user = userRepository.findById(postAddRequest.getUserId()).orElseThrow(() -> new RuntimeException("can't find the author"));

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


    public PostUpdateResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest) {

        log.info("수정 요청 dto :{}", postUpdateRequest);

        Post findPost =
                postRepository.findById(postId).orElseThrow(() -> new RuntimeException("해당 글이 없습니다"));
        //변경감지
        findPost.setTitle(postUpdateRequest.getTitle());
        findPost.setBody(postUpdateRequest.getBody());

        PostUpdateResponse postUpdateResponse = new PostUpdateResponse("포스트 수정 완료", findPost.getId());
        return postUpdateResponse;

    }

    public PostDeleteResponse deletePost(Long postId) {
        postRepository.deleteById(postId);
        PostDeleteResponse deleteResponse = new PostDeleteResponse("포스트 삭제 완료", postId);
        return deleteResponse;
    }
}
