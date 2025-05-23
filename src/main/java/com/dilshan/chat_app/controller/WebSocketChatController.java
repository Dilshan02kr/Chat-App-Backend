package com.dilshan.chat_app.controller;

import com.dilshan.chat_app.dto.ChatMessageDTO;
import com.dilshan.chat_app.dto.MessageRequestDTO;
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

            simpMessagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getChatRoomId(), savedMessage);

        } catch (UserNotFoundException | IllegalArgumentException e) {

            System.err.println("Error sending message: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Unexpected error during message sending: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * (Optional) Example of handling user joining a chat.
     * Clients can send messages to "/app/chat.addUser" when they join a room.
     * This could be used for presence tracking or initial setup.
     *
     * @param principal The authenticated user's principal.
     * @param headerAccessor Accessor for STOMP headers, useful for getting session ID.
     */
    // @MessageMapping("/chat.addUser")
    // public void addUser(Principal principal, SimpMessageHeaderAccessor headerAccessor) {
    //     // Add user to WebSocket session attributes
    //     // You might use this to track active users in a chat room,
    //     // or to set up private queues for user-specific notifications.
    //     System.out.println("User added: " + principal.getName() + " to session: " + headerAccessor.getSessionId());
    //     // headerAccessor.getSessionAttributes().put("username", principal.getName());
    //
    //     // You might also broadcast a "user joined" message to the room if needed
    //     // messagingTemplate.convertAndSend("/topic/public", principal.getName() + " joined!");
    // }
}
