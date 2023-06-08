package com.effectivemobile.socialmedia.dto.userEntity;

import lombok.Data;

@Data
public class UserEntityResponseDTO {
    private Long id;
    private String email;
    private String userName;
    private String lastName;
}
