package com.mirai.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtils implements Serializable {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    private Set<String> invalidatedTokens = new HashSet<>();

    public String getIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getRoleFromUsers(String role) {
        Claims claims;
        claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(role).getBody();
        Claims claim = claims;
        return getRoleFromUsers(String.valueOf(claim));
    }

    public Date getExpirationDateFromToken(String token) {

        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String userId, String mail) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", mail);
        return doGenerateToken(claims, userId);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean validateToken(String token, String userId) {
        String id = getIdFromToken(token);
        return (id.equals(userId) && !isTokenExpired(token)) && !invalidatedTokens.contains(token);
    }

    public void invalidateToken(String token) {
      //  token = token.substring(7);
        invalidatedTokens.add(token);
    }
}
