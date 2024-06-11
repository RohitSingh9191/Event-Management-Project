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

    private Boolean adminWeb;

    private String field1;

    private String field2;

    private String field3;

    private String field4;

}
