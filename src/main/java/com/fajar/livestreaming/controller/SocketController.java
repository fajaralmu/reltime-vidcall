package com.fajar.livestreaming.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.service.RealtimeService;
import com.fajar.livestreaming.service.WebRtcService;

@CrossOrigin
@RestController
public class SocketController extends BaseController{
	Logger log = LoggerFactory.getLogger(SocketController.class);
//	@Autowired
//	private SimpMessagingTemplate webSocket;
	@Autowired
	RealtimeService realtimeUserService;
	@Autowired
	private WebRtcService webRtcService;
	 
	
	public SocketController() {
		log.info("------------------SOCKET CONTROLLER #1-----------------");
	}
	
	@PostConstruct
	public void init() {
//		LogProxyFactory.setLoggers(this);
	}
	
	@PostMapping(value = "/api/stream/disconnect", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse disconnect(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) { 
		realtimeUserService.disconnectLiveStream(request);
		return new WebResponse();
	}
	@PostMapping(value = "/api/stream/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse register(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) { 
		
		RegisteredRequest registeredRequest = userSessionService.registerSession(request, httpRequest);;
		return WebResponse.builder().registeredRequest(registeredRequest).build();
	}
	@PostMapping(value = "/api/stream/invalidate", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse invalidate( HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) { 
		userSessionService.removeSessioon(httpRequest);
		return new WebResponse();
	}
	
	@GetMapping(value = "/api/stream/clearsession", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse clearsession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		return userSessionService.clearAllSession( httpRequest);
	}
	
	@PostMapping(value = "/api/stream/callpartner", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse clearsession(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		return webRtcService.callPartner(webRequest, httpRequest);
	}
	 
	/////////////////////////////////////// WEbsocket //////////////////////////////////////////
	
	@MessageMapping("/stream") 
	public WebResponse stream( WebRequest request) throws IOException {
		
		return realtimeUserService.stream(request);
	}
	@MessageMapping("/audiostream") 
	public WebResponse audiostream( WebRequest request) throws IOException {
		
		return realtimeUserService.audioStream(request);
	}
	
	@MessageMapping("/webrtc") 
	public WebResponse webRtc( WebRequest request) throws IOException {
		
		return webRtcService.handshakeWebRtc(request);
	}
	
	@MessageMapping("/acceptcall") 
	public WebResponse acceptCall( WebRequest request) throws IOException {
		
		return webRtcService.acceptCall(request);
	}
	
	
}
