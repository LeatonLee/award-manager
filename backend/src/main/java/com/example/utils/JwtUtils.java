package com.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    private static String signKey = "itheima";
    private static Long expire = 60L * 60L * 1000L; // 设置JWT过期时间为1小时

    /**
     * 生成JWT令牌
     * @param claims JWT第二部分负载 payload 中存储的内容
     * @return
     */
    public static String generateJwt(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
        return jwt;
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }

    /**
     * 从JWT令牌中获取用户ID
     * @param jwt JWT令牌
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.get("studentId", Long.class);
    }

    /**
     * 检查JWT是否过期
     * @param jwt JWT令牌
     * @return true 表示过期，false 表示未过期
     */
    public static boolean isTokenExpired(String jwt) {
        Claims claims = parseJWT(jwt);
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
