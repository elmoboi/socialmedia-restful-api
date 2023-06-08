package com.effectivemobile.socialmedia.repository;

import com.effectivemobile.socialmedia.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> getPostsByPostOwner_Id(Long postOwnerId);
}
