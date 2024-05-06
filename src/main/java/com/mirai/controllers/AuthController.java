package com.mirai.controllers;

import com.mirai.data.entities.UserAuth;
import com.mirai.models.request.JWTRequest;
import com.mirai.models.request.UserAuthRequest;
import com.mirai.models.response.JWTResponse;
import com.mirai.service.auth.AuthService;
import com.mirai.service.user.UserInterface;
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

    private UserInterface userInterface;
    private final AuthService authService;

    /**
     * Endpoint for user login.
     * @param request JwtRequest object containing user credentials.
     * @return ResponseEntity with JwtResponse object containing JWT token upon successful authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody JWTRequest request) {
        JWTResponse response = authService.adminlogin(request);
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
     * Endpoint for creating a new user.
     * @param user UserAuth object containing user details.
     * @return Created UserAuth object.
     */
    @PostMapping("/createUser")
    public UserAuth createUser(@RequestBody UserAuthRequest user) {
        return userInterface.createUser(user);
    }
}
