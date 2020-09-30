package com.fajar.livestreaming.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebRtcService {

	@Autowired
	private RealtimeService realtimeService;
	@Autowired
	private StreamingService streamingService; 
	@Autowired
	private UserSessionService sessionService;

	public WebResponse handshakeWebRtc(WebRequest request) {
		String partner = request.getPartnerId();
		WebResponse response = WebResponse.builder().webRtcObject(request.getWebRtcObject()).build();
		realtimeService.convertAndSend("/wsResp/webrtc/" + partner, response); 
		return response;
	}

	public WebResponse responseCall(WebRequest request) {
		log.info("acceptcall: {} ", request);
		String origin = request.getOriginId();
		WebResponse response = WebResponse.builder().accept(request.isAccept()).message(request.getMessage()).build();
		realtimeService.convertAndSend("/wsResp/partneracceptcall/"+request.getDestination()+"/" + origin, response);
		return response;
	}

	public WebResponse notityCallPartner(WebRequest webRequest, HttpServletRequest httpRequest) throws Exception {
		try {
			RegisteredRequest partnerSession = streamingService.getPartnerSession(webRequest.getDestination());
			
			if(sessionService.isInActiveCall(partnerSession.getRequestId())) {
				throw new Exception("Partner is busy!");
			}
			
			streamingService.notifyCallingPartner(httpRequest, partnerSession);
			return new WebResponse();
		}catch (Exception e) {
			return WebResponse.failed(e.getMessage());
		}
	}

}
