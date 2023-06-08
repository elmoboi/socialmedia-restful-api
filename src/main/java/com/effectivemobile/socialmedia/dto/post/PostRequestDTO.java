package com.effectivemobile.socialmedia.dto.post;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PostRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    @Size(max = 1500)
    private String text;

    private String imgUrl;

}
