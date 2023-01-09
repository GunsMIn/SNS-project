//
//package com.example.crudpersional.service;
//import com.example.crudpersional.domain.dto.post.*;
//import com.example.crudpersional.domain.entity.Post;
//import com.example.crudpersional.domain.entity.User;
//import com.example.crudpersional.exceptionManager.ErrorCode;
//import com.example.crudpersional.exceptionManager.PostException;
//import com.example.crudpersional.exceptionManager.UserException;
//import com.example.crudpersional.fixture.AllFixture;
//import com.example.crudpersional.fixture.PostEntityFixture;
//import com.example.crudpersional.fixture.UserEntityFixture;
//import com.example.crudpersional.repository.LikeRepository;
//import com.example.crudpersional.repository.PostRepository;
//import com.example.crudpersional.repository.UserRepository;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.security.test.context.support.WithMockUser;
//import com.example.crudpersional.domain.entity.Post;
//import com.example.crudpersional.domain.entity.User;
//import com.example.crudpersional.domain.entity.UserRole;
//import com.example.crudpersional.repository.LikeRepository;
//import com.example.crudpersional.repository.PostRepository;
//import com.example.crudpersional.repository.UserRepository;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class UserServiceTest {
//
//    @InjectMocks
//    UserService userService;
//
//    @Mock
//    UserRepository userRepository ;
//    @Mock
//    BCryptPasswordEncoder encoder;
//
//
//
//    @Test
//    @DisplayName("회원가입 서비스")
//    void check () throws Exception {
//        //postAndUser의 객체
//        AllFixture allFixture = new AllFixture();
//        AllFixture dto = allFixture.getDto();
//        //User 픽스처로 생성된 user엔티티(매개변수는 postAndUser픽스처의 값)
//        User user = UserEntityFixture.get(dto.getUserName(), dto.getPassword());
//
//        when(userRepository.find)
//
//
//
//
//
//    }
//
//
//}
