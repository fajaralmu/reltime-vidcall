package com.fajar.livestreaming.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class WebSocketEventListener {

//    @Autowired
//    private SimpMessageSendingOperations messagingTemplate;

    Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    	log.info("Received a new web socket connection");
    	log.info("message: {}",event.getMessage());
    	log.info("source : {}",event.getSource());
    	}

//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String username = (String) headerAccessor.getSessionAttributes().get("username");
//        if(username != null) {
//        	log.info("User Disconnected : " + username);
//
//            ChatMessage chatMessage = new ChatMessage();
//            chatMessage.setType(ChatMessage.MessageType.LEAVE);
//            chatMessage.setSender(username);
//
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);
//        }
//    }
}
