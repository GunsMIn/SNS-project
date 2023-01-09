package com.example.crudpersional.repository;

import com.example.crudpersional.domain.entity.AlarmEntity;
import com.example.crudpersional.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<AlarmEntity,Long> {

    Page<AlarmEntity> findByUser(User user, Pageable pageable);

    List<AlarmEntity> findListByUser(User user, Pageable pageable);

    Page<AlarmEntity> findAll(Pageable pageable);
}
