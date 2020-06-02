package com.fajar.livestreaming.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.config.LogProxyFactory;
import com.fajar.livestreaming.dto.RegisteredRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StreamingService {
	
	@Autowired
	private UserSessionService userSessionService;
	 
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this); 
	}
	public RegisteredRequest getPartnerSession(String partnerId) throws Exception {
		RegisteredRequest partnerSession = userSessionService.getAvailableSession(partnerId);
		
		if(null == partnerSession) {
			throw new Exception("Invalid request ID");
		} 
		
		return partnerSession;
	}

	public void setActive(HttpServletRequest request) {
 
		RegisteredRequest registeredRequest = userSessionService.getRegisteredRequest(request);
		userSessionService.setActiveSession(registeredRequest.getRequestId(), true);
		 
	}

	public List<RegisteredRequest> getSessionList(HttpServletRequest request) throws Exception {
		 
		RegisteredRequest currentRequest = userSessionService.getRegisteredRequest(request);
		if(null == currentRequest) {
			throw new Exception("invalid current sssion");
		}
		List<RegisteredRequest> sessionList = userSessionService.getAvaliableRequests();
		
		log.info("sessionList size: {}", sessionList.size());
		for (RegisteredRequest registeredRequest : sessionList) {
			if(registeredRequest.getRequestId().equals(currentRequest.getRequestId())) {
 				 
			}
		}
		return sessionList;
	}

}
