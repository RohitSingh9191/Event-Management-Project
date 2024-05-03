package com.mirai.data.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
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
    private Boolean IsPolicyAccept;

    @Column(name = "PasswordHash")
    private String passwordHash;

    @Column(name = "PasswordSalt")
    private String passwordSalt;

    @Column(name = "date")
    private LocalDate date;
}
