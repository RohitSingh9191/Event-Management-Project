package com.mirai.models.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRequest {
    private String name;

    private String email;

    private String phone;

    private String company;

    private String designation;

    private String linkedInProfile;

    private String type;

    private String isPolicyAcepted;
}
