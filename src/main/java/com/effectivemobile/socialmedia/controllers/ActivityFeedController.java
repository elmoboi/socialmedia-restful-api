package com.effectivemobile.socialmedia.controllers;

import com.effectivemobile.socialmedia.dto.post.PostResponseDTO;
import com.effectivemobile.socialmedia.service.post.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ActivityFeedController {

    private final PostService postService;

    public ActivityFeedController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/users/{userIntityId}/feed")
    public ResponseEntity<List<PostResponseDTO>> getUserEntityFeed(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                   @RequestParam(name = "size", defaultValue = "5") int size,
                                                                   @PathVariable Long userIntityId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());

        return ResponseEntity.ok(postService.getPagedAndSortedUserEntityActivityFeed(userIntityId, pageable).getContent());
    }
}
