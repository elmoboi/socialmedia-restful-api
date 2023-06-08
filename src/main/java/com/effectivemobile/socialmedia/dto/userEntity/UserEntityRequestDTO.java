package com.effectivemobile.socialmedia.dto.userEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserEntityRequestDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 3, max = 30)
    private String userName;

    private String lastName;

    @NotBlank
    @Size(min = 6, max = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
