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
public class AlarmEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알람을 받은 사람
    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private Long fromUserId; //어떤 회원에게 알람이 왔는지 발신자 id
    private Long targetId; // 내가 쓴 글의 postId(알림주체)
    private String text;

    public static AlarmEntity of(User userEntity, AlarmType alarmType, Long fromUserId, Long targetId) {
        AlarmEntity entity = AlarmEntity.builder()
                .user(userEntity)
                .alarmType(alarmType)
                .fromUserId(fromUserId)
                .targetId(targetId)
                .build();
        return entity;
    }
}
