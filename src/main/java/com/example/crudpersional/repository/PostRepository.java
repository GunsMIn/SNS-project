package com.example.crudpersional.repository;

import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post,Long> {

    @EntityGraph(attributePaths = {"likes"})
    Page<Post> findAll(Pageable pageable);
    @EntityGraph(attributePaths = {"likes"})
    Page<Post> findByTitleContaining(Pageable pageable,String title);

    Page<Post> findPostsByUser(User user, Pageable pageable);

  /*  @Query("UPDATE ")
    Integer addLikeCount(Post post);*/
 }
