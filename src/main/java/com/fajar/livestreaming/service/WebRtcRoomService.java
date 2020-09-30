package com.fajar.livestreaming.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.WebResponse;

@Service
public class WebRtcRoomService {

	
	@Autowired
	private UserSessionService userSessionService;
	
	public WebResponse generateRoomId(HttpServletRequest httpRequest) {
		String roomId = userSessionService.generateRoomId(httpRequest);
		return WebResponse.builder().message(roomId).build();
	}
	
	

}
