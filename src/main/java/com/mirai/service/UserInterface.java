package com.mirai.service;

import com.mirai.data.entities.UserAuth;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface UserInterface {
    public List<UserAuth> getUsers();

    public UserAuth createUser(UserAuth user);
}
