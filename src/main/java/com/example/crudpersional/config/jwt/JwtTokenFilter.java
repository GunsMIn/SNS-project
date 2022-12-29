package com.example.crudpersional.config.jwt;

import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
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
    public static final String BEARER = "Bearer ";


    private final UserService userService;
    private final String secretKey;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // http header에 있는 AUTHORIZATION 정보를 받아옵니다.
        final String token = getToken(request);
        log.info("token : {}", token);

        // AUTHORIZATION에 있는 Token을 꺼냅니다.
        try {
            String userName = JwtTokenUtil.getUserName(token, secretKey);
            log.info("userName = {}", userName);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER")));

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException e) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            request.setAttribute("exception", ErrorCode.INVALID_PERMISSION.name());
        } catch (Exception e) {
            log.error("JwtFilter - doFilterInternal() 오류발생");
            log.error("token : {}", token);
            log.error("Exception Message : {}", e.getMessage());
            log.error("Exception StackTrace : {");
            e.printStackTrace();
            log.error("}");
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(token) && token.startsWith(BEARER)) {
            return token.substring(7);
        }
        return null;
    }
   /* @Override
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
        log.info("userName:{}",userName);

        // UserDetail가져오기
        User user = userService.getUserByUserName(userName);
        log.info("userRole:{}", user.getRole());

        //문 열어주기, Role 바인딩
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority(user.getRole().name()))    );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 권한 부여
        filterChain.doFilter(request, response);

    }*/
}