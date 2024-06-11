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
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private String designation;
    private String linkedInProfile;
    private String type;
    private Boolean isPolicyAccept;
    private String status;
    private Date date;
    private String imageUrl;
    private Boolean checkIN;
    private String final1;
    private String final2;
    private String final3;
    private String final4;
}
