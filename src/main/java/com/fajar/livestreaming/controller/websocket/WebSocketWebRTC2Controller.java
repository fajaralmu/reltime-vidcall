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
import com.fajar.livestreaming.service.WebRtcService;

@CrossOrigin
@RestController
public class WebSocketWebRTC2Controller extends BaseController {
	Logger log = LoggerFactory.getLogger(WebSocketWebRTC2Controller.class);

	@Autowired
	private WebRtcService webRtcService;

	public WebSocketWebRTC2Controller() {
		log.info("------------------WebSocketWebRTC2Controller #1-----------------");
	}

	@PostConstruct
	public void init() {// LogProxyFactory.setLoggers(this); 
	}

	@MessageMapping("/webrtc")
	public WebResponse webrtc(WebRequest request) throws IOException {

		return webRtcService.handshakeWebRtc(request);
	}

	@MessageMapping("/acceptcall")
	public WebResponse responseCall(WebRequest request) throws IOException {

		return webRtcService.responseCall(request);
	}

}
