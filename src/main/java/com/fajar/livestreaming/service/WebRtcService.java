package com.fajar.livestreaming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;

@Service
public class WebRtcService {

	@Autowired
	private SimpMessagingTemplate webSocket;

	public WebResponse webRtc(WebRequest request) {
		String partner = request.getPartnerId();
		WebResponse response = WebResponse.builder().webRtcObject(request.getWebRtcObject()).build();
		webSocket.convertAndSend("/wsResp/webrtc/" + partner, response);
//		webSocket.convertAndSend("/wsResp/webrtc", response);
		return response;
	}

}
