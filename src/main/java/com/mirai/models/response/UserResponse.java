package com.mirai.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private String designation;
    private String linkedInProfile;
    private String type;
}
