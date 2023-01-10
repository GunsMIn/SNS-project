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
                .setIssuedAt(new Date(System.currentTimeMillis())) // 오늘 날짜 넣어줌
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs)) // 지금 현재 시간에서 + expiredTimeMs
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }


    /**토큰과 secretKey로 userName 꺼내오는 메서드**/
    public static String getUserName(String token, String key) {
        return extractClaims(token, key).get("userName").toString();
    }

    /**Claims 반환 메서드**/
    private static Claims extractClaims(String token, String key) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    /**토큰 만료확인 메서드**/
    public static boolean isExpired(String token, String secretkey) {
        // expire timestamp를 return함
        Date expiredDate = extractClaims(token, secretkey).getExpiration();
        //expire Date 가 지금보다 전이면 만료 된 것이다(true 반환 시 만료된 토큰)
        return expiredDate.before(new Date());
    }


}
