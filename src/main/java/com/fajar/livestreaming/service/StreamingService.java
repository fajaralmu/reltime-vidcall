package com.fajar.livestreaming.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.controller.BaseController;
import com.fajar.livestreaming.dto.RegisteredRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StreamingService {
	
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private BaseController baseController;
	@Autowired
	private RealtimeService2 realtimeService2;
	
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
		for (RegisteredRequest registeredRequest : sessionList) {
			if(registeredRequest.getRequestId().equals(currentRequest.getRequestId())) {
				sessionList.remove(registeredRequest);
				break;
			}
		}
		return sessionList;
	}

}
