package com.mirai.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class UserAuthRequest {
    
    private String userid;

    private String name;

    private String email;

    private String password;

    private String about;
}
