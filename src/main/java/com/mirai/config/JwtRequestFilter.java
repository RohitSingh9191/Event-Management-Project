package com.mirai.config;

import com.mirai.data.entities.UserAuth;
import com.mirai.service.auth.AuthService;
import com.mirai.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;

    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getMethod().equals("OPTIONS")) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } else {
            final String requestTokenHeader = request.getHeader("Authorization");
            String userId = null;
            String jwtToken = null;
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    String tokenUserId = jwtTokenUtils.getIdFromToken(jwtToken);
                    UserAuth userAuth = authService.getById(tokenUserId);
                    if (userAuth != null) {
                        userId = jwtTokenUtils.getIdFromToken(jwtToken);
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                    }

                } catch (IllegalArgumentException e) {
                    log.error("Unable to get JWT Token");
                } catch (ExpiredJwtException e) {
                    log.error("JWT Token has expired");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    filterChain.doFilter(request, response);
                }
            } else {
                jwtToken = null;
                log.error("JWT Token does not begin with Bearer String");
            }

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserAuth userResponse = authService.getById(userId);
                if (jwtTokenUtils
                        .validateToken(jwtToken, userResponse.getUserid())
                        .equals(true)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userResponse, null, null);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    log.error("Validation fails");
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
