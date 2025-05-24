package com.dilshan.chat_app.controller;

import com.dilshan.chat_app.dto.ChatMessageDTO;
import com.dilshan.chat_app.dto.MessageRequestDTO;
import com.dilshan.chat_app.entity.ChatMessage;
import com.dilshan.chat_app.exception.UserNotFoundException;
import com.dilshan.chat_app.service.ChatMessageService;
import com.dilshan.chat_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequestDTO messageRequestDTO, Principal principal) {
        try {
            String senderPhoneNumber = principal.getName();

            ChatMessageDTO savedMessage = chatMessageService.saveMessage(
                    messageRequestDTO.getChatRoomId(),
                    senderPhoneNumber,
                    messageRequestDTO.getContent()
            );

            // Broadcast the newly saved message to all participants in the chat room
            simpMessagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getChatRoomId(), savedMessage);
            System.out.println("Backend: Sent message ID " + savedMessage.getId() + " with status " + savedMessage.getStatus() + " to chat room " + savedMessage.getChatRoomId());

        } catch (UserNotFoundException | IllegalArgumentException e) {
            System.err.println("Error sending message: " + e.getMessage());
            // Consider sending an error message back to the sender if needed,
            // e.g., to a user-specific queue.
        } catch (Exception e) {
            System.err.println("Unexpected error during message sending: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/chat.markAsDelivered")
    public void markAsDelivered(@Payload ChatMessageDTO messageDTO, Principal principal) {
        try {
            // Validate if the principal (recipient) is a participant in the chat and the message is for them.
            // For simplicity, we assume the messageDTO.getId() is valid and the backend handles permissions.
            // The service method should only update if the status is SENT or PENDING to DELIVERED.
            chatMessageService.updateMessageStatus(messageDTO.getId(), ChatMessage.MessageStatus.DELIVERED);

            // Fetch the updated message to ensure all fields are correct for broadcasting
            ChatMessageDTO updatedMessage = chatMessageService.getChatMessageById(messageDTO.getId());

            // Broadcast the updated message to all participants in the chat room
            simpMessagingTemplate.convertAndSend("/topic/chat/" + updatedMessage.getChatRoomId(), updatedMessage);
            System.out.println("Backend: Marked message ID " + updatedMessage.getId() + " as DELIVERED and broadcasted.");

        } catch (IllegalArgumentException e) {
            System.err.println("Error marking message as delivered: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during marking message as delivered: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/chat.markAsRead")
    public void markAsRead(@Payload ChatMessageDTO messageDTO, Principal principal) {
        try {
            // Validate if the principal (recipient) is a participant in the chat and the message is for them.
            // The service method should only update if the status is SENT, PENDING, or DELIVERED to READ.
            chatMessageService.updateMessageStatus(messageDTO.getId(), ChatMessage.MessageStatus.READ);

            // Fetch the updated message to ensure all fields are correct for broadcasting
            ChatMessageDTO updatedMessage = chatMessageService.getChatMessageById(messageDTO.getId());

            // Broadcast the updated message to all participants in the chat room
            simpMessagingTemplate.convertAndSend("/topic/chat/" + updatedMessage.getChatRoomId(), updatedMessage);
            System.out.println("Backend: Marked message ID " + updatedMessage.getId() + " as READ and broadcasted.");

        } catch (IllegalArgumentException e) {
            System.err.println("Error marking message as read: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during marking message as read: " + e.getMessage());
            e.printStackTrace();
        }
    }
}