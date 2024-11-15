package com.mirai.config;

import com.mirai.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class JwtHelper {

    private final HttpServletRequest httpServletRequest;

    private final JwtTokenUtils jwtTokenUtils;

    public String getUserId() {
        String token = httpServletRequest.getHeader("Authorization");
        token = token.substring(7);
        String userId = null;
        try {
            userId = jwtTokenUtils.getIdFromToken(token);
        } catch (IllegalArgumentException e) {
            log.warn("Unable to get JWT Token");

        } catch (ExpiredJwtException e) {
            log.warn("JWT Token has expired");
        }
        return userId;
    }
}
