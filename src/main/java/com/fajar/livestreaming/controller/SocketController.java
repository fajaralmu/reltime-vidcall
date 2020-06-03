package com.fajar.livestreaming.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.service.RealtimeService;

@CrossOrigin
@RestController
public class SocketController extends BaseController{
	Logger log = LoggerFactory.getLogger(SocketController.class);
//	@Autowired
//	private SimpMessagingTemplate webSocket;
	@Autowired
	RealtimeService realtimeUserService;
	 
	
	public SocketController() {
		log.info("------------------SOCKET CONTROLLER #1-----------------");
	}
	
	@PostConstruct
	public void init() {
//		LogProxyFactory.setLoggers(this);
	}
	
	@PostMapping(value = "/api/stream/disconnect")
	public WebResponse disconnect(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) { 
		realtimeUserService.disconnectLiveStream(request);
		return new WebResponse();
	}
	@PostMapping(value = "/api/stream/register")
	public WebResponse register(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) { 
		
		RegisteredRequest registeredRequest = userSessionService.registerSession(httpRequest);;
		return WebResponse.builder().registeredRequest(registeredRequest).build();
	}
	@PostMapping(value = "/api/stream/invalidate")
	public WebResponse invalidate( HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) { 
		userSessionService.removeSessioon(httpRequest);
		return new WebResponse();
	}
	
	//@MessageMapping("/move")
//	//@SendTo("/wsResp/players")
//	public RealtimeResponse join2( RealtimeResponse response) throws IOException {
//		webSocket.convertAndSend("/wsResp/players", response);
//		return response;
//	}
//	
//	@MessageMapping("/addUser")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse join( RealtimeRequest request) throws IOException {
//		
//		return realtimeUserService.connectUser(request);
//	}
//	
//	@MessageMapping("/addEntity")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse addEntity( RealtimeRequest request) throws IOException {
//		
//		return realtimeUserService.addEntity(request);
//	}
//	
//	@MessageMapping("/move")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse move( RealtimeRequest request) throws IOException {
//		log.info("MOVE: {},",request);
//		return realtimeUserService.move(request);
//	}
//	
//	@MessageMapping("/leave")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse leave( RealtimeRequest request) throws IOException {
//		
//		return realtimeUserService.disconnectUser(request);
//	}
	
	
	
//	@MessageMapping("/chat")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse send(Message message){
//		RealtimeResponse response = new RealtimeResponse();
//		System.out.println("Message > "+message);
//	    String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
//	    OutputMessage msg =new  OutputMessage(message.getSender(), message.getText(), time);
//	    System.out.println("Output > "+msg);
//	    response.setMessage(msg);
//	    return response;
//	}
	@MessageMapping("/stream") 
	public WebResponse stream( WebRequest request) throws IOException {
		
		return realtimeUserService.stream(request);
	}
	@MessageMapping("/audiostream") 
	public WebResponse audiostream( WebRequest request) throws IOException {
		
		return realtimeUserService.audioStream(request);
	}
	
	
}
