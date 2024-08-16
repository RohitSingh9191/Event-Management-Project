package com.mirai.controllers;

import com.mirai.models.request.JWTRequest;
import com.mirai.models.request.UserAuthRequest;
import com.mirai.models.response.AuthResponse;
import com.mirai.models.response.JWTResponse;
import com.mirai.service.auth.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@Slf4j
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for user login.
     * @param request JwtRequest object containing user credentials.
     * @return ResponseEntity with JwtResponse object containing JWT token upon successful authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody JWTRequest request) {
        log.info("Login attempt for user with email: '{}'", request.getEmail());
        JWTResponse response = authService.adminlogin(request);
        log.info("Login successful for user with email: '{}'", request.getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Exception handler for BadCredentialsException.
     * @return Error message for invalid credentials.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid!";
    }

    /**
     * Creates a new user with the provided user authentication request.
     *
     * @param user The user authentication request containing user details.
     * @return ResponseEntity containing the authentication response.
     */
    @PostMapping("/admin")
    public ResponseEntity<AuthResponse> addAdmin(@RequestBody UserAuthRequest user) {
        log.info("Received request to create user: {}", user);
        AuthResponse authResponse = authService.createUser(user);
        log.info("Created user successfully. Response: {}", authResponse);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public void logout() {
        log.info("Received request to logout");
        authService.logout();
    }
}
