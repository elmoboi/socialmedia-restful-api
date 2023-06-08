package com.effectivemobile.socialmedia.dto.friendRequest;

import lombok.Data;

@Data
public class FriendRequestResponseDTO {
    private Long senderId;
    private String senderUserName;
    private String email;
}
