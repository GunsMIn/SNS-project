package com.example.crudpersional.domain.entity;


import com.example.crudpersional.domain.entity.alarm.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
//@Table(name = "\"alarm\"")
public class AlarmEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알람을 받은 사람 (수신자)
    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private Long fromUserId; //발신자 user id

    private Long targetId; // 내가 쓴 글의 postId(알림주체)

    private String text;
    //                           알림수신자 ,         알림 타입 ,     알림발신자 id ,  알림 주체 포스트 id
    public static AlarmEntity of(User user, AlarmType alarmType,Long fromUserId, Long targetId) {
        AlarmEntity entity = AlarmEntity.builder()
                .user(user)
                .alarmType(alarmType)
                .text(alarmType.getAlarmText())
                .fromUserId(fromUserId)
                .targetId(targetId)
                .build();
        return entity;
    }
}
