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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.service.UtilityService;

@CrossOrigin
@RestController 
public class RestUtilityController extends BaseController{
	Logger log = LoggerFactory.getLogger(RestUtilityController.class); 
	
	@Autowired
	private UtilityService utilityService;
	
	public RestUtilityController() {
		log.info("------------------RestAppController #1-----------------");
	}
	
	@PostConstruct
	public void init() {
//		LogProxyFactory.setLoggers(this);
	}
	 
	@PostMapping(value = "/api/util/clearsession", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse clearAllSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		return userSessionService.clearAllSession( httpRequest);
	}
	@PostMapping(value = "/api/util/clearsessionbyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse clearSessionById(@PathVariable(name="id")String id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		return userSessionService.clearSessionById(id);
	}
	@PostMapping(value = "/api/util/activecalls", produces = MediaType.APPLICATION_JSON_VALUE)
	public HashMap<String, Object> activecalls(HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		return userSessionService.getActiveCalls();
	}
	@PostMapping(value = "/api/util/clearactivecalls", produces = MediaType.APPLICATION_JSON_VALUE)
	public HashMap<String, Object> clearactivecalls(HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		userSessionService.clearActiveCalls();
		return userSessionService.getActiveCalls();
	}
	
	@PostMapping(value = "/api/runtimedata/getall/{repoName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getAllRuntimeData(@PathVariable(name="repoName")String repoName, HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		 
		return utilityService.getAll(repoName);
	}
	@PostMapping(value = "/api/runtimedata/clear/{repoName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse clearAllData(@PathVariable(name="repoName")String repoName,HttpServletRequest httpRequest, HttpServletResponse httpResponse) { 
		 
		return utilityService.clearAll(repoName);
	} 
	
}
