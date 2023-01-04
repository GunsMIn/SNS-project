package com.example.crudpersional.fixture;

import com.example.crudpersional.domain.entity.AlarmEntity;
import com.example.crudpersional.domain.entity.LikeEntity;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.alarm.AlarmType;

public class AlarmFixture {

    //서비스 테스트에서 필요한 alarmEntity 생성 클래스와 메서드
    public static AlarmEntity get(Post post,User user, AlarmType alarmType) {
        return AlarmEntity.builder()
                .id(1l)
                .user(post.getUser()) // 알림 수신자
                .alarmType(alarmType)
                .text(alarmType.getAlarmText())
                .fromUserId(user.getId()) // 알림 발신자 id
                .targetId(post.getId())
                .build();

    }
}
