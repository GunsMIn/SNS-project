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
        // http header에 있는 AUTHORIZATION 정보 토큰 반환
        final String token = getToken(request);
        log.info("token : {}", token);

        try {
            String userName = JwtTokenUtil.getUserName(token, secretKey);
            log.info("userName = {}", userName);

            //인증 시도 uesr의 권한 name()
            String role = userService.findRoleByUserName(userName).name();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority(role)));

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
            //substring(7) 해주는 이유 => Bearer 를 해주기위해 (Bearer_)
            return token.substring(7);
        }
        // null 처리로  권한부여 아예 안해줌
        return null;
    }
}