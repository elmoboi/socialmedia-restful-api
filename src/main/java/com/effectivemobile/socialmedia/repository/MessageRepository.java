package com.effectivemobile.socialmedia.repository;

import com.effectivemobile.socialmedia.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllBySenderIdAndRecipientId(Long senderId, Long recipientId);
}
