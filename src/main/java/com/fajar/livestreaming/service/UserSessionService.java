package com.fajar.livestreaming.service;

import static com.fajar.livestreaming.runtimerepo.SessionRepository.SESSION_ATTR_SESS_DATA;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.config.LogProxyFactory;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.runtimerepo.ActiveCallsRepository;
import com.fajar.livestreaming.runtimerepo.SessionRepository;
import com.fajar.livestreaming.util.Encryptions;
import com.fajar.livestreaming.util.MapUtil;
import com.fajar.livestreaming.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSessionService {

	private static final String HEADER_REQUEST_ID = "request-id";
	private static final String HEADER_REQUEST_KEY = "request_key";

	@Autowired
	private SessionRepository sessionRepository; 

	@Autowired
	private ActiveCallsRepository activeCallsRepository; 

	@Autowired
	private RealtimeService realtimeService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
 
	public void registerNewSession(RegisteredRequest request) {  
		sessionRepository.registerNewSession(request);
	}

	public RegisteredRequest getRegisteredRequestById(String requestId) {
		return sessionRepository.getRequest(requestId);
	}

	private void removeSessionById(String requestId) {
		sessionRepository.remove(requestId);
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

	public List<RegisteredRequest> getAvaliableRequests() {
		Map<String, RegisteredRequest> registeredApps = sessionRepository.getData().getRegisteredApps();
		return MapUtil.mapToList(registeredApps);
	}

	public boolean isRegistered(HttpServletRequest request) {
		return getRegisteredRequest(request) != null;
	}

	public void setActiveSession(String requestId, boolean active) {
		RegisteredRequest existingSession = getRegisteredRequestById(requestId);
		if (null == existingSession) {
			return;
		}
//		SESSION_MAP.get(SESSION_TRIAL_ONE).getRequest(requestId).setActive(active);
		sessionRepository.setActive(requestId, active);
		if (active) {
			activeCallsRepository.put(requestId, new Date());
		}

		realtimeService.sendUpdateSessionStatus(existingSession);
	}

	public RegisteredRequest getRegisteredRequest(HttpServletRequest request) {
		
		String requestId = null;
		RegisteredRequest storedInHttpSession = null;
		try {
			storedInHttpSession = (RegisteredRequest) request.getSession(false)
					.getAttribute(SESSION_ATTR_SESS_DATA);
		} catch (Exception e) { }
		
		if (null != storedInHttpSession) {
			requestId = storedInHttpSession.getRequestId();
		}else {
			requestId = decodeRawRequestId(request.getHeader(HEADER_REQUEST_KEY));
		}
		
		if(null != requestId) {
			return getRegisteredRequestById(requestId);
		}

		return null;
	}

	public void removeSessioon(HttpServletRequest request) {
		RegisteredRequest currentRequest = getRegisteredRequest(request);
		if (null != currentRequest) {
			removeRegisteredRequest(request);
			currentRequest.setExist(false);
			realtimeService.sendUpdateSessionExistance(currentRequest, false);
		}
	}

	public RegisteredRequest registerSession(WebRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		RegisteredRequest newRegisteredRequest = createNewRequest(request.getUsername(), httpRequest);
		
		setSessionAttributeSessionData(httpRequest, newRegisteredRequest);
		
		registerNewSession(newRegisteredRequest);
		realtimeService.sendUpdateSessionExistance(newRegisteredRequest, true);
		
		httpResponse.addHeader("request_key", newRegisteredRequest.getEncodedRequestId());

		return newRegisteredRequest;
	}

	private static void setSessionAttributeSessionData(HttpServletRequest request, RegisteredRequest newRequest) {
		request.getSession(true).setAttribute(SESSION_ATTR_SESS_DATA, newRequest);
	}

	private RegisteredRequest createNewRequest(String username, HttpServletRequest httpRequest) {
		final String requestId = randomRequestId();
		RegisteredRequest registeredRequest = RegisteredRequest.newSession(username, requestId, httpRequest);
		registeredRequest.setEncodedRequestId(encodeRequestId(requestId));
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
	
	public WebResponse clearSessionById(String sessionID) {
		try {
			removeSessionById(sessionID);
		}catch (Exception e) {
			WebResponse.failed(e.getMessage());
		}
		return new WebResponse();
	}

	public WebResponse leavecall(HttpServletRequest httpRequest) {
		RegisteredRequest userSession = getRegisteredRequest(httpRequest);
		if (null != userSession) {
			activeCallsRepository.remove(userSession.getRequestId());
		}
		return new WebResponse();
	}

	public HashMap<String, Object> getActiveCalls() {
		return activeCallsRepository.getMap();
	}

	public boolean isInActiveCall(String requestId) {
		return requestId != null && activeCallsRepository.containsKey(requestId);
	}

	public void clearActiveCalls() {
		activeCallsRepository.clearAll();
	}
	
	private String decodeRawRequestId(String raw) {	
		try {
			return Encryptions.decodeBase64(raw);
		}catch (Exception e) {
			return null;
		}
	}
	
	private String encodeRequestId(String requestId) {
		try {
			return Encryptions.encodeBase64(requestId);
		}catch (Exception e) {
			return null;
		}
	}

}
