package com.example.crudpersional.repository;

import com.example.crudpersional.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findAll(Pageable pageable);
}
