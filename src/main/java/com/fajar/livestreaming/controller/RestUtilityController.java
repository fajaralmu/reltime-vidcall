package com.fajar.livestreaming.controller;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.livestreaming.dto.WebResponse;

@CrossOrigin
@RestController 
public class RestUtilityController extends BaseController{
	Logger log = LoggerFactory.getLogger(RestUtilityController.class); 
	
	public RestUtilityController() {
		log.info("------------------RestAppController #1-----------------");
	}
	
	@PostConstruct
	public void init() {
//		LogProxyFactory.setLoggers(this);
	}
	 
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
