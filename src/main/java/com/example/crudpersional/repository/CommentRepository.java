package com.example.crudpersional.repository;

import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //해당 post의 comment를 paging
    Page<Comment> findAllByPost(Post post, Pageable pageable);

}
