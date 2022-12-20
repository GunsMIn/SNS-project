package com.example.crudpersional.repository;

import com.example.crudpersional.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findByUserName(String username);

    Optional<User> findUserByUserName(String username);
}
