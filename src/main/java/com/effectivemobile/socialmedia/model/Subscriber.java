package com.effectivemobile.socialmedia.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "subscriber_table")
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "subscription_status")
    private Boolean subscriptionStatus = true;

    @Column(name = "is_friend")
    private Boolean isFriend = false;

    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private UserEntity subscriber;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "subscription_date")
    private LocalDateTime subscriptionDate;
}
