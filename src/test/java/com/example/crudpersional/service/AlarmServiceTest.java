package com.example.crudpersional.service;
import com.example.crudpersional.domain.dto.alarm.AlarmResponse;
import com.example.crudpersional.domain.dto.comment.CommentResponse;
import com.example.crudpersional.domain.dto.comment.CommentUpdateResponse;
import com.example.crudpersional.domain.dto.comment.PostMineDto;
import com.example.crudpersional.domain.entity.AlarmEntity;
import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.alarm.AlarmType;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.fixture.*;
import com.example.crudpersional.repository.LikeRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import com.example.crudpersional.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.crudpersional.repository.AlarmRepository;
import com.example.crudpersional.repository.CommentRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AlarmServiceTest {

    @InjectMocks
    PostService postService;
    @Mock
    PostRepository postRepository ;
    @Mock
    UserRepository userRepository ;
    @Mock
    CommentRepository commentRepository;
    @Mock
    AlarmRepository alarmRepository;


    @Test
    @DisplayName("알람 페이징 조회 20개 성공")
    void 알람_페이징_성공() throws Exception {

        AllFixture all = AllFixture.getDto();
        User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
        Post post = PostEntityFixture.get(user);
        PageRequest request = PageRequest.of(0, 10, Sort.Direction.DESC, "registeredAt");

        /**repositoty에 성공 로직 가정**/
        when(userRepository.findOptionalByUserName(any())).thenReturn(Optional.of(user));
        when(alarmRepository.findByUser(any(), any())).thenReturn(Page.empty());

        Page<AlarmResponse> alarms = postService.getAlarms(user.getUsername(), request);
        assertDoesNotThrow(()->alarms);
    }

    @Test
    @DisplayName("알람 페이징 조회 20개 실패 : 유저 없음")
    void 알람_페이징_실패() throws Exception {

        AllFixture all = AllFixture.getDto();
        User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
        Post post = PostEntityFixture.get(user);
        PageRequest request = PageRequest.of(0, 10, Sort.Direction.DESC, "registeredAt");

        /**회원이 없는 상황 가정**/
        when(userRepository.findOptionalByUserName(any())).thenReturn(Optional.empty());
        when(alarmRepository.findByUser(any(), any())).thenReturn(Page.empty());


        UserException userException = assertThrows(UserException.class, () -> postService.getAlarms(user.getUsername(), request));

        assertThat(userException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        assertThat(userException.getErrorCode().getStatus()).isEqualTo(ErrorCode.USER_NOT_FOUND.getStatus());
        assertThat(userException.getErrorCode().getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
    }



}
