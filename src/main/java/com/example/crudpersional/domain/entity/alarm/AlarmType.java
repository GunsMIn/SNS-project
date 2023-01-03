
package com.example.crudpersional.domain.entity.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {
    NEW_COMMENT_ON_POST("New Comment!"),
    NEW_LIKE_ON_POST("New Like!");

    private final String alarmText;
}
