package com.mirai.controllers;

import com.mirai.Security.JwtHelper;
import com.mirai.data.entities.UserAuth;
import com.mirai.models.request.JWTRequest;
import com.mirai.models.request.UserAuthRequest;
import com.mirai.models.response.JWTResponse;
import com.mirai.service.user.UserInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@Slf4j
@RequestMapping("/v1/auth")
public class AuthController {

    private UserDetailsService userDetailsService;
    private UserInterface userInterface;
    private AuthenticationManager manager;
    private JwtHelper helper;

    /**
     * Endpoint for user login.
     * @param request JwtRequest object containing user credentials.
     * @return ResponseEntity with JwtResponse object containing JWT token upon successful authentication.
     */
    @PostMapping("/login")
    @CrossOrigin
    public ResponseEntity<JWTResponse> login(@RequestBody JWTRequest request) {
        this.doAuthenticate(request.getEmail(), request.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        JWTResponse response = JWTResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
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
            throw new BadCredentialsException("Invalid Username or Password!!");
        }
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
