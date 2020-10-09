package com.fajar.livestreaming.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.livestreaming.annotation.Authenticated;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.service.PublicConference1Service;

@CrossOrigin
@RestController 
@Authenticated
public class RestWebRTCRoomController extends BaseController{
	Logger log = LoggerFactory.getLogger(RestWebRTCRoomController.class); 
	 
	@Autowired
	private PublicConference1Service publicConference1Service;
	 
	
	public RestWebRTCRoomController() {
		log.info("------------------RestWebRTCRoomController #1-----------------");
	}
	
	@PostConstruct
	public void init() {
//		LogProxyFactory.setLoggers(this);
	}
	 
	@PostMapping(value = "/api/webrtcroom/generateroomid", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse generateroomid( HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		return publicConference1Service.generateRoomId( httpRequest);
	}
	
	@PostMapping(value = "/api/webrtcroom/invalidate", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse invalidateRoom(@RequestBody WebRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		return publicConference1Service.invalidateRoom(httpRequest, request);
	}
	
	@PostMapping(value = "/api/webrtcroom/startrecording", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse startRecordingPeer(@RequestBody WebRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		return publicConference1Service.startRecordingPeer(httpRequest, request);
	}
	
	 
	
}
