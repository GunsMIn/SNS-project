package com.example.crudpersional.mvc.dto;

import com.example.crudpersional.domain.dto.alarm.AlarmResponse;
import com.example.crudpersional.domain.entity.AlarmEntity;
import com.example.crudpersional.domain.entity.alarm.AlarmType;
import org.springframework.data.domain.Page;

public class AlarmsResponse {

    private Long id;
    private AlarmType alarmType;
    private Long fromUserId;
    private Long targetId;
    private String text;
    private String createdAt;
    private String userName;

    /**map() 을 사용하여 알람엔티티의 페이징응답을 알람DTO페이징으로 변환**/
    public static Page<AlarmResponse> toResponse(Page<AlarmEntity> alarm){

        Page<AlarmResponse> responses =
                alarm.map(a -> AlarmResponse.builder()
                        .id(a.getId())
                        .alarmType(a.getAlarmType())
                        .fromUserId(a.getFromUserId())
                        .targetId(a.getTargetId())
                        .text(a.getAlarmType().getAlarmText())
                        .createdAt(a.getRegisteredAt())

                        .build());

        return responses;
    }

}
