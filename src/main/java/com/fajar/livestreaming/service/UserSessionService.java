package com.fajar.livestreaming.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.config.LogProxyFactory;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.SessionData;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.util.MapUtil;
import com.fajar.livestreaming.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSessionService {

	private static final Map<String, SessionData> SESSION_MAP = new LinkedHashMap<>();
	private static final String SESSION_TRIAL_ONE = "1";
	private static final String SESSION_ATTR_SESS_DATA = "session-data";
	private static final String HEADER_REQUEST_ID = "request-id";
	
	private final HashMap<String, Object> activeCalls = new HashMap<>();

	@Autowired
	private RealtimeService realtimeService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	private SessionData getSessionData(String key) {
		if (null == SESSION_MAP.get(key)) {
			SESSION_MAP.put(key, new SessionData());
		}

		return SESSION_MAP.get(key);
	}

	public void addRequestId(RegisteredRequest request) {
		getSessionData(SESSION_TRIAL_ONE);

		SESSION_MAP.get(SESSION_TRIAL_ONE).addNewApp(request);
	}

	public RegisteredRequest getRequestFromSessionMap(String requestId) {
		return SESSION_MAP.get(SESSION_TRIAL_ONE).getRequest(requestId);
	}

	private void removeSessionById(String requestId) {
		SESSION_MAP.get(SESSION_TRIAL_ONE).remove(requestId);
	}

	public void removeRegisteredRequest(HttpServletRequest request) {

		try {
			RegisteredRequest registeredRequest = getRegisteredRequest(request);

			removeSessionById(registeredRequest.getRequestId());

		} catch (Exception e) {

			log.error("Error Removing Session");
			e.printStackTrace();
		} finally {
			request.getSession().invalidate();
		}

	}

	public RegisteredRequest getAvailableSession(String requestId) {
		List<RegisteredRequest> requestList = getAvaliableRequests();
		for (RegisteredRequest registeredRequest : requestList) {
			if (registeredRequest.getRequestId().equals(requestId)) {
				return registeredRequest;
			}
		}
		return null;
	}

	public List<RegisteredRequest> getAvaliableRequests() {
		Map<String, RegisteredRequest> registeredApps = getSessionData(SESSION_TRIAL_ONE).getRegisteredApps();
		return MapUtil.mapToList(registeredApps);
	}

	public boolean isRegistered(HttpServletRequest request) {
		return getRegisteredRequest(request) != null;
	}

	public void setActiveSession(String requestId, boolean active) {
		RegisteredRequest existingReqId = getAvailableSession(requestId);
		if (null == existingReqId) {
			return;
		}
		SESSION_MAP.get(SESSION_TRIAL_ONE).getRequest(requestId).setActive(active);
		if (active) {
			activeCalls.put(requestId, new Date());
		}

		realtimeService.sendUpdateSessionStatus(existingReqId);
	}

	public RegisteredRequest getRegisteredRequest(HttpServletRequest request) {
		try {
			RegisteredRequest storedInHttpSession = (RegisteredRequest) request.getSession(false)
					.getAttribute(SESSION_ATTR_SESS_DATA);
			if (null != storedInHttpSession) {
				RegisteredRequest storedInSessionMap = getRequestFromSessionMap(storedInHttpSession.getRequestId());
				return storedInSessionMap;
			}

		} catch (Exception e) {
		}
		
		//check by header
		try {
			RegisteredRequest session = getRequestFromSessionMap(request.getHeader(HEADER_REQUEST_ID));
			return session;
		} catch (Exception e) {
			
		}
		
		return null;
	}

	public void removeSessioon(HttpServletRequest request) {
		RegisteredRequest currentRequest = getRegisteredRequest(request);
		if (null != currentRequest) {
			removeRegisteredRequest(request);
			currentRequest.setExist(false);
			realtimeService.sendUpdateSessionExistance(currentRequest);
		}
	}

	public RegisteredRequest registerSession(WebRequest request, HttpServletRequest httpRequest) {
		RegisteredRequest newRegisteredRequest = createNewRequest(request.getUsername(), httpRequest);
		setSessionAttributeSessionData(httpRequest, newRegisteredRequest);
		addRequestId(newRegisteredRequest);
		realtimeService.sendUpdateSessionExistance(newRegisteredRequest);

		return newRegisteredRequest;
	}

	private static void setSessionAttributeSessionData(HttpServletRequest request, RegisteredRequest newRequest) {
		request.getSession(true).setAttribute(SESSION_ATTR_SESS_DATA, newRequest);
	}

	private RegisteredRequest createNewRequest(String username, HttpServletRequest httpRequest) {
		final String requestId = randomRequestId();
		RegisteredRequest registeredRequest = new RegisteredRequest();
		registeredRequest.setUsername(username + "_" + requestId);
		registeredRequest.setActive(false);
		registeredRequest.setCreated(new Date());
		registeredRequest.setUserAgent(httpRequest.getHeader("user-agent"));
		registeredRequest.setRequestId(requestId);
		return registeredRequest;
	}

	private String randomRequestId() {
		return StringUtil.generateRandomChar(8);
	}

	public WebResponse clearAllSession(HttpServletRequest request) {
		try {
			List<RegisteredRequest> requests = (List<RegisteredRequest>) SerializationUtils
					.clone((Serializable) getAvaliableRequests());
			for (RegisteredRequest registeredRequest : requests) {
				removeSessionById(registeredRequest.getRequestId());
			}
			return new WebResponse();
		} catch (Exception e) {

			return WebResponse.failed(e.getMessage());
		}
	}

	public WebResponse leavecall(HttpServletRequest httpRequest) {
		RegisteredRequest userSession = getRegisteredRequest(httpRequest);
		if (null != userSession) {
			activeCalls.remove(userSession.getRequestId());
		}
		return new WebResponse();
	}

	public HashMap<String, Object> getActiveCalls() {
		return activeCalls;
	}

	public boolean isInActiveCall(String requestId) {
		return requestId != null && activeCalls.containsKey(requestId);
	}

	public void clearActiveCalls() {
		activeCalls.clear();
	}
	
	

}
