package com.dilshan.chat_app.repository;

import com.dilshan.chat_app.entity.ChatMessage;
import com.dilshan.chat_app.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

    // You might also need to find messages for a chat room with pagination for large histories
    //Page<ChatMessage> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom, Pageable pageable);

}
