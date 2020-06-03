package com.fajar.livestreaming.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.config.LogProxyFactory;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;

@Service
public class RealtimeService {
	Logger log = LoggerFactory.getLogger(RealtimeService.class);

	@Autowired
	private SimpMessagingTemplate webSocket;  

	public RealtimeService() {
		LogProxyFactory.setLoggers(this);
		log.info("=======================REALTIME SERVICE 2======================="); 
	}


	public boolean sendUpdateSessionExistance(RegisteredRequest registeredRequest) {
 
		webSocket.convertAndSend("/wsResp/sessions", WebResponse.builder().registeredRequest(registeredRequest).build());

		return true;
	}

	 

	public void sendMessageChat(WebResponse response) {
		webSocket.convertAndSend("/wsResp/messages", response); 
	}
	
	private void sendLiveStramResponse(WebResponse response) {
		webSocket.convertAndSend("/wsResp/videostream/"+response.getRequestId(), response);
	}


	public WebResponse stream(WebRequest request) {
		WebResponse response = new WebResponse();
		
		response.setImageData(request.getImageData());
		response.setRequestId(request.getOriginId());
		
		sendLiveStramResponse(response);
		return response;
	}


	public void disconnectLiveStream(WebRequest request) {
		 
//		userSessionService.setActiveSession(request.getOriginId(), false);
//		WebResponse response = WebResponse.builder().code("01").requestId(request.getOriginId()).build();
//		sendLiveStramResponse(response );
	}


	public void sendUpdateSessionStatus(RegisteredRequest registeredRequest) {
		 
		webSocket.convertAndSend("/wsResp/sessionstatus", WebResponse.builder().registeredRequest(registeredRequest).build());

	}


	public WebResponse audioStream(WebRequest request) {
		WebResponse response = new WebResponse();
		
		response.setAudioData(request.getAudioData());
		response.setRequestId(request.getOriginId());
		
		sendLiveAudioStramResponse(response);
		return response;
	}


	private void sendLiveAudioStramResponse(WebResponse response) {
		 
		webSocket.convertAndSend("/wsResp/audiostream/"+response.getRequestId(), response);
	}

}
