package com.example.crudpersional.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {

    //토큰 생성 메서드
    public static String generateToken(String userName, String key, long expiredTimeMs) {
        Claims claims = Jwts.claims(); //일종의 map
        claims.put("userName", userName); // Token에 담는 정보를 Claim이라고 함
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }


    //토큰에서 userName 꺼내오는 메서드
    public static String getUserName(String token, String key) {
        return extractClaims(token, key).get("userName").toString();
    }

    private static Claims extractClaims(String token, String key) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    //토큰 만료확인 메서드
    public static boolean isExpired(String token, String secretkey) {
        // expire timestamp를 return함
        Date expiredDate = extractClaims(token, secretkey).getExpiration();
        return expiredDate.before(new Date());
    }
}
