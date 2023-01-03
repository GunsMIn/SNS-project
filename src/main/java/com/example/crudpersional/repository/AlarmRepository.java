package com.example.crudpersional.repository;

import com.example.crudpersional.domain.entity.AlarmEntity;
import com.example.crudpersional.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<AlarmEntity,Long> {

    Page<AlarmEntity> findByUser(User user, Pageable pageable);

    Page<AlarmEntity> findAll(Pageable pageable);
}
