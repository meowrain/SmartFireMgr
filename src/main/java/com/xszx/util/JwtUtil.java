package com.xszx.util;

import com.xszx.common.exceptions.ServiceException;
import com.xszx.dao.entity.AdminDAO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.xszx.common.errorcode.BaseErrorCode.AUTH_ERROR;

public class JwtUtil {
    private static final long EXPIRATION_TIME = 86400L; // 1 hour in milliseconds
    // 更新为256位（32字节）的密钥以满足HS256算法要求
    public static final String JWT_SECRET = "japmzpefctvbbjaxojgdijkwlygbwyfbFireManagerSecretKey2024SmartFire";
    public static final String ISS = "firemanager";
    public static final String TOKEN_PREFIX = "Bearer ";
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static SecretKeySpec createKey(String rawSecret) {
        return new SecretKeySpec(rawSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * 生成用户TOKEN
     */
    public static String generateAccessToken(AdminDAO adminDao) {
        SecretKeySpec key = createKey(JWT_SECRET);
        Map<String, Object> adminMap = new HashMap<>();
        adminMap.put("username", adminDao.getName());
        adminMap.put("id", adminDao.getId());
        String jwtToken = Jwts.builder()
                .setIssuer(ISS) // 设置发行者
                .setSubject("admin") // 设置主题
                .setClaims(adminMap)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME * 1000))
                .setIssuedAt(new Date()) // 设置签发时间
                .signWith(key)
                .compact();
        return TOKEN_PREFIX + jwtToken;
    }

    public static Claims validateToken(String token) {
        try {
            // 去掉 "Bearer " 前缀，解析 JWT
            String pureToken = token.replace(TOKEN_PREFIX, "").trim();
            // 创建正确的密钥
            SecretKeySpec key = createKey(JWT_SECRET);
            // 解析并获取 JWT 的 Jws<Claims> 部分
            Jws<Claims> jwsClaims = Jwts.parserBuilder()
                    .setSigningKey(key) // 设置签名密钥
                    .build()
                    .parseClaimsJws(pureToken); // 验证并解析 Token
            Claims claims = jwsClaims.getBody();
            // 检查 Token 是否过期
            if (claims.getExpiration().before(new Date())) {
                log.warn("token 已过期");
                throw new ServiceException(AUTH_ERROR.message(), AUTH_ERROR);
            }
            return claims;
        } catch (Exception e) {
            log.warn("token 验证失败: {}", e.getMessage());
            throw new ServiceException(AUTH_ERROR.message(), AUTH_ERROR);
        }

    }

}
