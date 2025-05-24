package com.dilshan.chat_app.config;

import com.dilshan.chat_app.security.JwtTokenProvider;
import com.dilshan.chat_app.service.UserService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public WebSocketAuthInterceptor(JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Remove "Bearer " prefix

                try {

                    if (jwtTokenProvider.isTokenValid(token)) {

                        String phoneNumber = jwtTokenProvider.extractPhoneNumber(token);

                        if (phoneNumber != null) {

                            UserDetails userDetails = userService.getUserByPhoneNumber(phoneNumber);

                            // Create a custom principal that returns the phone number
                            Principal customPrincipal = new Principal() {
                                @Override
                                public String getName() {
                                    return phoneNumber; // Return phone number directly
                                }
                            };


                            // Set the custom principal
                            accessor.setUser(customPrincipal);

                            // Also set up the authentication for security context
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            System.out.println("WebSocket authenticated user: " + phoneNumber);

                        } else {
                            System.err.println("JWT token is valid but no phone number found in claims.");
                        }
                    } else {
                        System.err.println("Invalid JWT token during WebSocket connection attempt.");
                    }
                } catch (Exception e) {
                    System.err.println("Error processing JWT for WebSocket: " + e.getMessage());

                }
            } else {
                System.err.println("No or invalid Authorization header in WebSocket CONNECT frame.");
            }
        }
        return message;
    }
}
