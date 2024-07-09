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
@Table(name = "Checkin")
@NoArgsConstructor
public class Checkin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "UserId")
    private Integer userId;

    @Column(name = "CheckinTime")
    private Date checkinTime;

    @Column(name = "CheckoutTime")
    private Date checkoutTime;

    @Column(name = "Status")
    private String status;
}
