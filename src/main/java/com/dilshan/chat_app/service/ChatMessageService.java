package com.dilshan.chat_app.service;

import com.dilshan.chat_app.dto.ChatMessageDTO;
import com.dilshan.chat_app.dto.UserProfileDTO;
import com.dilshan.chat_app.entity.ChatMessage;
import com.dilshan.chat_app.entity.ChatRoom;
import com.dilshan.chat_app.entity.User;
import com.dilshan.chat_app.exception.UserNotFoundException;
import com.dilshan.chat_app.repository.ChatMessageRepository;
import com.dilshan.chat_app.repository.ChatRoomRepository;
import com.dilshan.chat_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ChatMessageDTO saveMessage(String chatRoomId, String senderPhoneNumber, String content) throws UserNotFoundException {
        ChatRoom chatRoom = chatRoomRepository.findByChatId(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found with ID: " + chatRoomId));

        User sender = userRepository.findByPhoneNumber(senderPhoneNumber)
                .orElseThrow(() -> new UserNotFoundException("Sender not found with phone number: " + senderPhoneNumber));

        if (!chatRoom.getParticipants().contains(sender)) {
            throw new IllegalArgumentException("Sender is not a participant of this chat room.");
        }

        ChatMessage message = new ChatMessage(
                chatRoom,
                sender,
                content
        );

        message = chatMessageRepository.save(message);

        chatRoom.setUpdatedAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);

        return convertToDto(message);
    }

    public List<ChatMessageDTO> getMessagesByChatRoomId(String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatId(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found with ID: " + chatRoomId));

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderByTimestampAsc(chatRoom);
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ChatMessageDTO convertToDto(ChatMessage message) {
        return new ChatMessageDTO(
                message.getId(),
                message.getChatRoom().getChatId(),
                new UserProfileDTO(
                        message.getSender().getId(),
                        message.getSender().getName(),
                        message.getSender().getPhoneNumber(),
                        message.getSender().getAbout(),
                        message.getSender().getProfileImageUrl(),
                        message.getSender().isVerified()
                ),
                message.getContent(),
                message.getTimestamp(),
                message.getStatus()
        );
    }

    @Transactional
    public void updateMessageStatus(Long messageId, ChatMessage.MessageStatus newStatus) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with ID: " + messageId));
        message.setStatus(newStatus);
        chatMessageRepository.save(message);
    }
}
