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
import com.fajar.livestreaming.service.RealtimeService;

@CrossOrigin
@RestController
public class WebSocketController extends BaseController {
	Logger log = LoggerFactory.getLogger(WebSocketController.class);

	@Autowired
	private RealtimeService realtimeUserService; 

	public WebSocketController() {
		log.info("------------------WebSocketController #1-----------------");
	}

	@PostConstruct
	public void init() {// LogProxyFactory.setLoggers(this);

	}

	/////////////////////////////////////// WEbsocket
	/////////////////////////////////////// //////////////////////////////////////////

	@MessageMapping("/stream")
	public WebResponse stream(WebRequest request) throws IOException {

		return realtimeUserService.stream(request);
	}

	@MessageMapping("/audiostream")
	public WebResponse audiostream(WebRequest request) throws IOException {

		return realtimeUserService.audioStream(request);
	}

}
