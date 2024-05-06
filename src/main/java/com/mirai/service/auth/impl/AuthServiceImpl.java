package com.mirai.service.auth.impl;

import com.mirai.exception.customException.ApplicationErrorCode;
import com.mirai.exception.customException.MiraiException;
import com.mirai.models.request.JWTRequest;
import com.mirai.models.response.JWTResponse;
import com.mirai.security.JwtHelper;
import com.mirai.service.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private JwtHelper helper;
    private AuthenticationManager manager;
    private UserDetailsService userDetailsService;

    @Override
    public JWTResponse adminlogin(JWTRequest request) {
        this.doAuthenticate(request.getEmail(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        JWTResponse response = JWTResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername())
                .build();
        return response;
    }

    /**
     * Authenticates user credentials.
     * @param email User email.
     * @param password User password.
     */
    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new MiraiException(ApplicationErrorCode.USERNAME_NOT_VALID);
        }
    }
}
