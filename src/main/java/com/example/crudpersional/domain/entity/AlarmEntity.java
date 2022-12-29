/*
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
    private Integer id;

    // 알람을 받은 사람
    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private Integer fromUserId;
    private Integer targetId;
    private String text;

    public static AlarmEntity of(User userEntity, AlarmType alarmType, Integer fromUserId, Integer targetId) {
        AlarmEntity entity = AlarmEntity.builder()
                .user(userEntity)
                .alarmType(alarmType)
                .fromUserId(fromUserId)
                .targetId(targetId)
                .build();
        return entity;
    }
}
*/
