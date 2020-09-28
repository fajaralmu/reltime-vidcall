package com.fajar.livestreaming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebRtcService {

	@Autowired
	private SimpMessagingTemplate webSocket;

	public WebResponse handshakeWebRtc(WebRequest request) {
		String partner = request.getPartnerId();
		WebResponse response = WebResponse.builder().webRtcObject(request.getWebRtcObject()).build();
		webSocket.convertAndSend("/wsResp/webrtc/" + partner, response); 
		return response;
	}

	public WebResponse acceptCall(WebRequest request) {
		log.info("acceptcall: {} ", request);
		String origin = request.getOriginId();
		WebResponse response = WebResponse.builder().accept(request.isAccept()).build();
		webSocket.convertAndSend("/wsResp/partneracceptcall/" + origin, response);
		return response;
	}

}
