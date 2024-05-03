package com.mirai.service.user;

import com.mirai.data.entities.UserAuth;
import java.util.List;

import com.mirai.models.request.UserAuthRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserInterface {
    public List<UserAuth> getUsers();

    public UserAuth createUser(UserAuthRequest user);
}
