package com.effectivemobile.socialmedia.dto.post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponseDTO {
    private Long id;
    private String title;
    private String text;
    private String imgUrl;
    private LocalDateTime sendDate;
    private LocalDateTime modifiedDate;
    private Long senderId;
}
