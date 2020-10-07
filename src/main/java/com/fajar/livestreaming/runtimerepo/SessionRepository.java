package com.fajar.livestreaming.runtimerepo;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.SessionData;
import com.fajar.livestreaming.runtime.TempSessionService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SessionRepository {

	@Autowired
	private TempSessionService tempSessionService;
	public static final String SESSION_ATTR_SESS_DATA = "session-data";
	public static final String SESSION_TRIAL_ONE = "1";

	@PostConstruct
	public void init() {
		try {
			tempSessionService.put(SESSION_ATTR_SESS_DATA, new SessionData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean put(String key, RegisteredRequest value) {

		if (getData() == null) {
			init();
		}
		SessionData sessionData = getData();
		sessionData.getRegisteredApps().put(key, value);
		return updateData(sessionData);
	}

	public synchronized boolean updateData(SessionData sessionData) {
		try {
			tempSessionService.put(SESSION_ATTR_SESS_DATA, sessionData);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public synchronized SessionData getData() {

		try {
			return tempSessionService.get(SESSION_ATTR_SESS_DATA, SessionData.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		log.debug("getSessionData return null");
		return null;
	}

	public synchronized boolean addNewApp(RegisteredRequest request) {

		SessionData sessionData = getData();
		if (null == sessionData) {
			return false;
		}
		sessionData.addNewApp(request);
		return updateData(sessionData);
	}

	public boolean setActive(String requestId, boolean active) {
		try {
			SessionData sessionData = getData();
			sessionData.getRequest(requestId).setActive(true);
			return updateData(sessionData);
		} catch (Exception e) {
			return false;
		}
	}

	public RegisteredRequest getRequest(String requestId) {
		try {
			SessionData sessionData = getData();
			return sessionData.getRequest(requestId);
		} catch (Exception e) {

		}
		return null;
	}

	public boolean removeRequest(String requestId) {
		try {
			SessionData sessionData = getData();
			sessionData.remove(requestId);
			return updateData(sessionData);
		} catch (Exception e) {
			return false;
		}

	}
}
