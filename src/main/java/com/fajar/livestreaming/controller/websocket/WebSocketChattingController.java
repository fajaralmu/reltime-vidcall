package com.fajar.livestreaming.controller.websocket;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.livestreaming.controller.BaseController;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.service.ChattingService;

@CrossOrigin
@RestController
public class WebSocketChattingController extends BaseController {
	Logger log = LoggerFactory.getLogger(WebSocketChattingController.class);

	@Autowired
	private ChattingService realChatService; 

	public WebSocketChattingController() {
		log.info("------------------WebSocketChattingController-----------------");
	}

	@PostConstruct
	public void init() {// LogProxyFactory.setLoggers(this);

	}

	/////////////////////////////////////// WEbsocket
	/////////////////////////////////////// //////////////////////////////////////////

	@MessageMapping("/chatting/typingstatus")
	public WebResponse stream(WebRequest request) throws IOException {
		
		return realChatService.sendTypingStatus(request);
	}

}
