package com.fajar.livestreaming.controller;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.service.RealtimeService;

@CrossOrigin
@RestController
public class RestAppController extends BaseController{
	Logger log = LoggerFactory.getLogger(RestAppController.class); 
	
	@Autowired
	private RealtimeService realtimeUserService; 
	 
	
	public RestAppController() {
		log.info("------------------RestAppController #1-----------------");
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
	public WebResponse invalidate(HttpServletRequest httpRequest,	HttpServletResponse httpResponse) { 
		userSessionService.removeSessioon(httpRequest);
		return new WebResponse();
	}  
	
	/////////////////////////////////////////// Utility //////////////////////////////////////////////
	@GetMapping(value = "/api/util/clearsession", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse clearsession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		return userSessionService.clearAllSession( httpRequest);
	}
	@GetMapping(value = "/api/util/activecalls", produces = MediaType.APPLICATION_JSON_VALUE)
	public HashMap<String, Object> activecalls(HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		return userSessionService.getActiveCalls();
	}
	@GetMapping(value = "/api/util/clearactivecalls", produces = MediaType.APPLICATION_JSON_VALUE)
	public HashMap<String, Object> clearactivecalls(HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		userSessionService.clearActiveCalls();
		return userSessionService.getActiveCalls();
	}
	
}
