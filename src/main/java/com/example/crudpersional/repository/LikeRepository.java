package com.example.crudpersional.repository;

import com.example.crudpersional.domain.entity.LikeEntity;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity,Long> {


    Optional<LikeEntity> findByUserAndPost(User user, Post post);

    List<LikeEntity> findAllByPost(Post post);

    //해당 post의 like count를 수하는 쿼리메소드
    @Query(value = "SELECT COUNT(*) FROM LikeEntity e WHERE e.post = :post")
    Integer countByPost(@Param("post") Post post);

    //좋아요 눌럿는지 안눌럿는지 check 눌렀다면 1 안눌렀다면 0
    @Query(value = "SELECT COUNT(*) FROM LikeEntity e WHERE e.post = :post and  e.user = :user")
    Integer countByPostAndUser(@Param("post") Post post,@Param("user") User user);
}
