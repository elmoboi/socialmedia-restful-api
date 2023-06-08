package com.effectivemobile.socialmedia.dto.post;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PostUpdateRequest {
    private String title;
    private String text;
    private String imgUrl;
}
