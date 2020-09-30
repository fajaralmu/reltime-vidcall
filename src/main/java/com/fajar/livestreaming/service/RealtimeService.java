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
	
	public void sendOnlineStatus(RegisteredRequest registeredRequest, boolean online) {
		try {
			WebResponse payload = WebResponse.builder().onlineStatus(online).build();
			convertAndSend("/wsResp/partneronlineinfo/"+registeredRequest.getRequestId(), payload );
		}catch (Exception e) {
			// TODO: handle exception
		}
	}


	public boolean sendUpdateSessionExistance(RegisteredRequest registeredRequest) {
 
		convertAndSend("/wsResp/sessions", WebResponse.builder().registeredRequest(registeredRequest).build());

		return true;
	}

	public void convertAndSend(String url, WebResponse payload) {
		webSocket.convertAndSend(url, payload); 
		
	}

	public void sendMessageChat(WebResponse response) {
		convertAndSend("/wsResp/messages", response); 
	}
	
	private void sendLiveStramResponse(WebResponse response) {
		convertAndSend("/wsResp/videostream/"+response.getRequestId(), response);
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
		 
		convertAndSend("/wsResp/sessionstatus", WebResponse.builder().registeredRequest(registeredRequest).build());

	}


	public WebResponse audioStream(WebRequest request) {
		WebResponse response = new WebResponse();
		
		response.setAudioData(request.getAudioData());
		response.setRequestId(request.getOriginId());
		
		sendLiveAudioStramResponse(response);
		return response;
	}


	private void sendLiveAudioStramResponse(WebResponse response) {
		 
		convertAndSend("/wsResp/audiostream/"+response.getRequestId(), response);
	}

	public void notifyCallingPartner(RegisteredRequest userRequest, RegisteredRequest partnerSession) {
		WebResponse response = WebResponse.builder().requestId(userRequest.getRequestId()).username(userRequest.getUsername()).build();
		convertAndSend("/wsResp/notifycall/"+partnerSession.getRequestId(), response);
	}
	

}
