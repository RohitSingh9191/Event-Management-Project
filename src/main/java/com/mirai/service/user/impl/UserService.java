package com.mirai.service.user.impl;

import com.mirai.data.entities.UserAuth;
import com.mirai.data.repos.UserAuthRepository;
import com.mirai.models.request.UserAuthRequest;
import com.mirai.service.user.UserInterface;

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
    public UserAuth createUser(UserAuthRequest user) {
        UserAuth userAuth = new UserAuth();
        userAuth.setName(user.getName());
        userAuth.setEmail(user.getEmail());
        userAuth.setUserid(UUID.randomUUID().toString());
        userAuth.setPassword(passwordEncoder.encode(user.getPassword()));
        userAuth.setAbout(user.getAbout());
        return userRepository.save(userAuth);
    }
}
