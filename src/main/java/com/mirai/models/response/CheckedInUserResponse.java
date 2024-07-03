package com.mirai.models.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CheckedInUserResponse {
    private Integer id;
    private String name;
    private String company;
    private String designation;
    private String imageUrl;
    private String phone;
    private String status;
    private Date checkIn;
    private Date checkOut;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
}
