package com.fajar.livestreaming.service;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.SessionData;
import com.fajar.livestreaming.util.MapUtil;

@Service
public class UserSessionService {

	private static final Map<String, SessionData> SESSION_MAP = new LinkedHashMap<>();
	private static final String SESS_1 = "1";
	private static final String SESSION_DATA = "session-data";

	private SessionData getSessionData(String key) {
		if (null == SESSION_MAP.get(key)) {
			SESSION_MAP.put(key, new SessionData());
		}

		return SESSION_MAP.get(key);
	}

	public void addRequestId(RegisteredRequest request) {
		getSessionData(SESS_1);

		SESSION_MAP.get(SESS_1).addNewApp(request);
	}

	public RegisteredRequest getAvailableSession(String requestId) {
		List<RegisteredRequest> requestList = getAvaliableRequests();
		for (RegisteredRequest registeredRequest : requestList) {
			if(registeredRequest.getRequestId().equals(requestId)) {
				return registeredRequest;
			}
		}
		return null;
	}
	
	public List<RegisteredRequest> getAvaliableRequests(){
		Map<String, RegisteredRequest> registeredApps = getSessionData(SESS_1).getRegisteredApps();
		return MapUtil.mapToList(registeredApps);
	}
	
	public boolean isRegistered(HttpServletRequest request) {
		return getRegisteredRequest(request) != null;
	}

	public void setActiveSession(String requestId, boolean active) {
		RegisteredRequest existingReqId = getAvailableSession(requestId);
		if(null == existingReqId) {
			return;
		}
		SESSION_MAP.get(SESS_1).getRequest(requestId).setActive(active);
	}
	
	public RegisteredRequest getRegisteredRequest(HttpServletRequest request) {
		try {
			return (RegisteredRequest) request.getSession(false).getAttribute(SESSION_DATA);
		}catch (Exception e) {
			 
			return null;
		}
	}
	
	public void removeSessioon(HttpServletRequest request) {
		request.getSession().invalidate();
	}
	
	public RegisteredRequest registerSession(HttpServletRequest request) {
		RegisteredRequest newRequest = createNewRequest();
		request.getSession(true).setAttribute(SESSION_DATA, newRequest);
		addRequestId(newRequest);
		return newRequest;
	}

	private RegisteredRequest createNewRequest() {
		RegisteredRequest registeredRequest = new RegisteredRequest();
		registeredRequest.setActive(false);
		registeredRequest.setCreated(new Date());
		registeredRequest.setRequestId(UUID.randomUUID().toString());
		return registeredRequest;
	}

}
