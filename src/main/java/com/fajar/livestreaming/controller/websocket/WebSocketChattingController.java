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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class WebSocketChattingController extends BaseController {
	
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
	public WebResponse typingstatus(WebRequest request) throws IOException {
		log.info("websocket send typingstatus");
		return realChatService.sendTypingStatus(request);
	}
	
	@MessageMapping("/chatting/markmessageasread")
	public WebResponse markmessageasread(WebRequest request) throws IOException {
		log.info("websocket send markmessageasread");
		realChatService.markMessageAsRead(request);
		return null;
	}

}
