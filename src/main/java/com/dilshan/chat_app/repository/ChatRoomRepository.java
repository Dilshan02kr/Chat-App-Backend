package com.dilshan.chat_app.repository;

import com.dilshan.chat_app.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByChatId(String chatId);

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants p WHERE cr.type = 'SINGLE' AND p.id IN :userIds GROUP BY cr HAVING COUNT(p.id) = 2")
    Optional<ChatRoom> findSingleChatRoomByParticipantIds(@Param("userIds") Set<Long> userIds);

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants p WHERE p.id = :userId")
    List<ChatRoom> findChatRoomsByParticipantId(@Param("userId") Long userId);
}
