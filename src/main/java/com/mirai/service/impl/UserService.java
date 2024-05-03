package com.mirai.service.impl;

import com.mirai.data.entities.UserAuth;
import com.mirai.data.repos.UserAuthRepository;
import com.mirai.service.UserInterface;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserInterface {
    @Autowired
    private UserAuthRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserAuth> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserAuth createUser(UserAuth user) {
        user.setUserid(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
