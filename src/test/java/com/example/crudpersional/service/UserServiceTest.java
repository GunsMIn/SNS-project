package com.example.crudpersional.service;

import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.UserRole;
import com.example.crudpersional.repository.LikeRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
@RequiredArgsConstructor
class UserServiceTest {

    UserService userService;
    private final BCryptPasswordEncoder encoder;

    UserRepository userRepository = Mockito.mock(UserRepository.class);




    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository,encoder);
    }

    @Data
    public static class UserTest{
        private Long userId;
        private String userName;
        private String password;


        public PostServiceTest.PostAndUser getDto() {
            PostServiceTest.PostAndUser p = new PostServiceTest.PostAndUser();
            p.setPostId(1L);
            p.setUserId(1L);
            p.setUserName("test");
            p.setPassword("1234");
            p.setTitle("테스트 제목");
            p.setBody("테스트 내용");
            return p;
        }
    }



    @Data
    public static class UserTestEntity {
        public static User get(String userName, String password) {
            User entity = new User();
            entity.setId(1L);
            entity.setUserName(userName);
            entity.setPassword(password);
            entity.setRole(UserRole.USER);
            return entity;
        }
    }

}