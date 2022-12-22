package com.example.crudpersional.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private String registeredAt;

    @LastModifiedDate
    private String updatedAt;

    @PrePersist
    public void onPrePersist(){
        this.registeredAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        this.updatedAt = this.registeredAt;
    }

    @PreUpdate
    public void onPreUpdate(){
        this.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
 /*

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime registeredAt;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @LastModifiedDate
    private LocalDateTime updatedAt;*/

    //https://thalals.tistory.com/302
    //삭제 시점
   /* private LocalDateTime deletedAt;*/
}
