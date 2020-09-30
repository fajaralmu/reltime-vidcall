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

import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.service.WebRtcService;

@CrossOrigin
@RestController 
public class RestWebRTC2Controller extends BaseController{
	Logger log = LoggerFactory.getLogger(RestWebRTC2Controller.class); 
	 
	@Autowired
	private WebRtcService webRtcService;
	 
	
	public RestWebRTC2Controller() {
		log.info("------------------RestAppController #1-----------------");
	}
	
	@PostConstruct
	public void init() {
//		LogProxyFactory.setLoggers(this);
	}
	 
	@PostMapping(value = "/api/webrtc2/callpartner", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse callPartner(@RequestBody WebRequest webRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		return webRtcService.notityCallPartner(webRequest, httpRequest);
	}
	
	@PostMapping(value = "/api/webrtc2/leavecall", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse leavecall(HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		return userSessionService.leavecall( httpRequest);
	}
	
}
