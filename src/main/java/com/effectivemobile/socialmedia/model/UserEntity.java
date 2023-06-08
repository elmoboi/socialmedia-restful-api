package com.effectivemobile.socialmedia.model;

import javax.persistence.*;

import com.effectivemobile.socialmedia.enums.Sex;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "user_table")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(length = 60, unique = true)
    private String email;

    @NotBlank
    @Size(min = 3, max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String userName;

    @Column(length = 60)
    private String lastName;

    private Sex sex;

    @NotBlank
    @Size(min = 6, max = 100)
    @Column(length = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_subscribers",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "subscriber_id")})
    @Nullable
    private List<UserEntity> subscriberList;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_signer",
            joinColumns = {@JoinColumn(name = "signer_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    @Nullable
    private List<UserEntity> singerList;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_friends",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "friend_id")})
    @Nullable
    private List<UserEntity> friendsList;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "received_friend_requests",
            joinColumns = @JoinColumn(name = "receiver_id"),
            inverseJoinColumns = @JoinColumn(name = "sender_id"))
    private List<UserEntity> receiverFriendRequests;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

}
