package com.effectivemobile.socialmedia.controllers;

import com.effectivemobile.socialmedia.dto.friendRequest.FriendRequestDTO;
import com.effectivemobile.socialmedia.dto.friendRequest.FriendRequestResponseDTO;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityResponseDTO;
import com.effectivemobile.socialmedia.service.userEntity.UserEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FriendRequestController {

    private final UserEntityService userEntityService;

    public FriendRequestController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    @PostMapping("/users/{userEntityId}/friend-request")
    public ResponseEntity<?> sendFriendRequest(@PathVariable Long userEntityId,
                                               @Valid @RequestBody FriendRequestDTO friendRequestDTO) {
        if (!userEntityId.equals(friendRequestDTO.getSenderId())) {
            return new ResponseEntity<>("User IDs didn't equal", HttpStatus.BAD_REQUEST);
        }

        userEntityService.sendFriendRequest(userEntityId, friendRequestDTO);

        return new ResponseEntity<>("Success send friend request", HttpStatus.OK);
    }

    @PutMapping("/users/{userEntityId}/accept-friend-request/{userEntitySenderId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long userEntityId, @PathVariable Long userEntitySenderId,
                                                 @RequestParam(value = "choice") String choice) {
        if ("accept".equals(choice)) {
            userEntityService.acceptFriendRequest(userEntityId, userEntitySenderId);
            return ResponseEntity.ok().body("Friend request accepted");
        } else if ("decline".equals(choice)) {
            userEntityService.deleteFriendRequest(userEntityId, userEntitySenderId);
            return ResponseEntity.ok().body("Friend request declined");
        } else
            return ResponseEntity.badRequest().body("Invalid choice");
    }

    @GetMapping("/users/{userEntityId}/friend-requests")
    public ResponseEntity<List<FriendRequestResponseDTO>> getAllFriendsRequests(@PathVariable Long userEntityId) {

        return ResponseEntity.ok(userEntityService.getAllFriendRequestByUserEntityId(userEntityId));
    }

    @DeleteMapping("/users/{userEntityId}/delete-friend-request/{userEntityReceiverId}")
    public ResponseEntity<?> deleteFriendRequest(@PathVariable Long userEntityId, @PathVariable Long userEntityReceiverId) {
        userEntityService.deleteFriendRequest(userEntityId, userEntityReceiverId);

        return ResponseEntity.ok().body("Friend deleted");
    }

    @DeleteMapping("/users/{userEntityId}/unsubscribe-request/{userEntityReceiverId}")
    public ResponseEntity<?> unsubscribeRequest(@PathVariable Long userEntityId, @PathVariable Long userEntityReceiverId) {
        userEntityService.sendUnsubscribeAndRecallFriendRequest(userEntityId, userEntityReceiverId);

        return ResponseEntity.ok().body("Successfully unsubscribed");
    }

    @GetMapping("/users/{userEntityId}/friends")
    public ResponseEntity<List<UserEntityResponseDTO>> getAllFriends(@PathVariable Long userEntityId) {
        List<UserEntityResponseDTO> friendsList = userEntityService.getAllFriends(userEntityId);

        return ResponseEntity.ok().body(friendsList);
    }

}
