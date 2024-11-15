package com.mirai.data.entities;

import jakarta.persistence.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "Users")
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Email")
    private String email;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Company")
    private String company;

    @Column(name = "Designation")
    private String designation;

    @Column(name = "LinkedInProfile")
    private String linkedInProfile;

    @Column(name = "Type")
    private String type;

    @Column(name = "IsPolicyAccept")
    private Boolean isPolicyAccept;

    @Column(name = "Image")
    private String image;

    @Column(name = "Status")
    private String status;

    @Column(name = "CreatedAt")
    private Date createdAt;

    @Column(name = "ModifiedAt")
    private Date modifiedAt;

    @Column(name = "Field1")
    private String field1;

    @Column(name = "Field2")
    private String field2;

    @Column(name = "Field3")
    private String field3;

    @Column(name = "Field4")
    private String field4;
}
