package com.fajar.livestreaming.config.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer  {
 
	Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
	public WebSocketConfig() {
		log.info("====================Web Socket Config=====================");
	}
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//    	org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport c = null;
//    	c.messageBrokerTaskScheduler()
    	log.info("configureMessageBroker");
      //  config.enableSimpleBroker("/topic");
        config.enableSimpleBroker("/wsResp");
        config.setApplicationDestinationPrefixes("/app");
       
    }
    
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    	registration.setMessageSizeLimit(Integer.MAX_VALUE);
//    	registration.setMessageSizeLimit(500 * 1024);
        registration.setSendBufferSizeLimit(Integer.MAX_VALUE);
        registration.setSendTimeLimit(Integer.MAX_VALUE);
    	//super.configureWebSocketTransport(registration);
    }
 
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	log.info(". . . . . . . . . register Stomp Endpoints . . . . . . . . . . ");
      //   registry.addEndpoint("/chat");
//         registry.addEndpoint("/random").setAllowedOrigins("*").withSockJS();
//         registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
         registry.addEndpoint("/realtime-app").setAllowedOrigins("*").withSockJS();
         registry.addEndpoint("/socket").setAllowedOrigins("*");
         
         
    }

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		 
		registry.addHandler(new CustomWebsocketHandler(), "/socket")
        .setAllowedOrigins("*");
		
	}
    
    
}