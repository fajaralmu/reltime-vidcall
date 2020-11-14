package com.fajar.livestreaming.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.livestreaming.annotation.Authenticated;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.service.RealChatService;

@CrossOrigin
@RestController 
@Authenticated
public class RestChatMessageController extends BaseController{
	Logger log = LoggerFactory.getLogger(RestChatMessageController.class); 
	  
	@Autowired
	private RealChatService realChatService;
	 
	
	public RestChatMessageController() {
		log.info("------------------RestAppController #1-----------------");
	}
	
	@PostConstruct
	public void init() {
//		LogProxyFactory.setLoggers(this);
	}
	 
	@PostMapping(value = "/api/chatting/send/{receiverId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse sendMessage(@RequestBody WebRequest webRequest, @PathVariable(name = "receiverId") String receiverId, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		return realChatService.sendMessage(webRequest, receiverId, httpRequest);
	}
	
	 
	
}
