package com.dilshan.chat_app.repository;

import com.dilshan.chat_app.entity.ChatMessage;
import com.dilshan.chat_app.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

    // Custom query to eagerly fetch ChatRoom and Sender entities
    @Query("SELECT cm FROM ChatMessage cm " +
            "JOIN FETCH cm.chatRoom " +
            "JOIN FETCH cm.sender " +
            "WHERE cm.id = :messageId")
    Optional<ChatMessage> findByIdWithChatRoomAndSender(@Param("messageId") Long messageId);
}