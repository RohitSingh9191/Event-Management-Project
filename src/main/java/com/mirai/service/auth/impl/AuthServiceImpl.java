package com.mirai.service.auth.impl;

import com.mirai.data.entities.UserAuth;
import com.mirai.data.repos.UserAuthRepository;
import com.mirai.exception.ApplicationErrorCode;
import com.mirai.exception.MiraiException;
import com.mirai.mapper.AuthMapper;
import com.mirai.models.request.JWTRequest;
import com.mirai.models.request.UserAuthRequest;
import com.mirai.models.response.AuthResponse;
import com.mirai.models.response.JWTResponse;
import com.mirai.security.JwtHelper;
import com.mirai.service.auth.AuthService;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private JwtHelper helper;
    private AuthenticationManager manager;
    private UserDetailsService userDetailsService;
    private UserAuthRepository userAuthRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public JWTResponse adminlogin(JWTRequest request) {
        this.doAuthenticate(request.getEmail(), request.getPassword());
        UserDetails userDetails = loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        JWTResponse response = JWTResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername())
                .build();
        return response;
    }

    private UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth user =
                userAuthRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not Found...."));
        return user;
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

    /**
     * Creates a new user with the provided user authentication request.
     *
     * @param user The user authentication request containing user details.
     * @return AuthResponse containing the authentication response.
     * @throws MiraiException If the email provided already exists in the database.
     */
    @Override
    public AuthResponse createUser(UserAuthRequest user) {
        log.info("Creating user with email: '{}'", user.getEmail());
        userAuthRepository.findByEmail(user.getEmail()).ifPresent(existingAuth -> {
            existingAuth.setModifiedAt(new Date());
            userAuthRepository.save(existingAuth);
            log.error("User creation failed: Email '{}' already exists.", user.getEmail());
            throw new MiraiException(ApplicationErrorCode.EMAIL_ALREADY_EXISTS);
        });
        String password = passwordEncoder.encode(user.getPassword());
        UserAuth userAuth = AuthMapper.mapAuthRequestToUserAuth(user, password);
        userAuth = userAuthRepository.save(userAuth);
        log.info("User created successfully with email: '{}'", user.getEmail());
        return AuthMapper.mapAuthResponse(userAuth);
    }
}
