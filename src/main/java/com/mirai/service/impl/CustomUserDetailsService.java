package com.mirai.service.impl;

import com.mirai.data.entities.UserAuth;
import com.mirai.data.repos.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserAuthRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth user =
                userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not Found...."));

        return user;
    }
}
