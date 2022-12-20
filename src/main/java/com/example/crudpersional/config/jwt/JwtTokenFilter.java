package com.example.crudpersional.config.jwt;

import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //헤더에서 토큰 꺼내기
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorizationHeader:{}", authorizationHeader);

        // 토큰이 없거나
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // token분리
        String token;

        try {
            //Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmFtZSI6Imt5ZW9uZ3JvazUiLCJpYXQiOjE2Njk2NT ~~~
            //형태로 들어오므로 .split(“ “)로 token을 분리 한다.
            token = authorizationHeader.split(" ")[1];
        } catch (Exception e) {
            log.error("token 추출에 실패 했습니다.");
            filterChain.doFilter(request, response);
            return;
        }
        // Token이 만료 되었는지 Check
        if(JwtTokenUtil.isExpired(token, secretKey)){
            filterChain.doFilter(request, response);
            return;
        };


        // Token에서 Claim에서 UserName꺼내기
        String userName = JwtTokenUtil.getUserName(token, secretKey);
        log.info("userName:{}", userName);

// UserDetail가져오기
        User user = userService.getUserByUserName(userName);
        log.info("userRole:{}", user.getRole());

//문 열어주기, Role 바인딩
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority(user.getRole().name()))    );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 권한 부여
        filterChain.doFilter(request, response);

    }
}