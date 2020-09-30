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
import com.fajar.livestreaming.service.PublicConference1Service;

@CrossOrigin
@RestController
public class WebSocketPublicConf1Controller extends BaseController {
	Logger log = LoggerFactory.getLogger(WebSocketPublicConf1Controller.class);

	@Autowired
	private PublicConference1Service publicConference1Service; 

	public WebSocketPublicConf1Controller() {
		log.info("------------------WebSocketPublicConf1Controller #1-----------------");
	}

	@PostConstruct
	public void init() {// LogProxyFactory.setLoggers(this);

	}

	/////////////////////////////////////// WEbsocket
	/////////////////////////////////////// //////////////////////////////////////////
	
	@MessageMapping("/publicconf1/webrtc")
	public WebResponse webrtc(WebRequest request) throws IOException {
		return publicConference1Service.handshakeWebRtc(request);
	}

	@MessageMapping("/publicconf1/join")
	public WebResponse join(WebRequest request) throws IOException { 
		return publicConference1Service.joinRoom(request);
	}

	@MessageMapping("/publicconf1/leave")
	public WebResponse leave(WebRequest request) throws IOException {
		return publicConference1Service.leaveRoom(request);
	}

}
