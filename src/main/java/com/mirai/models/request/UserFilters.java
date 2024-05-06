package com.mirai.models.request;

import lombok.Data;

@Data
public class UserFilters {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private String designation;
    private String type;
    private String policyType;
    private String sortBy;
    private String orderBy;
    private String status;
    private String limit;
    private String offset;
}
