package com.example.crudpersional.domain.dto.alarm;

import com.example.crudpersional.domain.dto.comment.CommentResponse;
import com.example.crudpersional.domain.entity.AlarmEntity;
import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.domain.entity.alarm.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmResponse {

    private Long id;
    private AlarmType alarmType;
    private Long fromUserId;
    private Long targetId;
    private String text;
    private String createdAt;

    /**map() 을 사용하여 알람엔티티의 페이징응답을 알람DTO페이징으로 변환**/
    public static Page<AlarmResponse> toResponse(Page<AlarmEntity> alarm){

        Page<AlarmResponse> responses = alarm.map(a -> AlarmResponse
                .builder()
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
