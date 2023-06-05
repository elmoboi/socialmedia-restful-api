package com.effectivemobile.socialmedia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(min = 6, max = 100)
    @Column(length = 100)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Subscriber> subscriberList;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL)
    private List<Friends> friendsList;

    @OneToMany(mappedBy = "postOwner", cascade = CascadeType.ALL)
    private List<Post> postList;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Message> messageList;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
