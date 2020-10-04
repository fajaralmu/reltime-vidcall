package com.fajar.livestreaming.runtimerepo;

import java.io.Serializable;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.ActiveCalls;
import com.fajar.livestreaming.runtime.TempSessionService;

@Service
public class ActiveCallsRepository implements RuntimeRepository<ActiveCalls>{

	@Autowired
	private TempSessionService tempSessionService;
	public static final String ACTIVE_CALLS = "active-calls";

	@PostConstruct
	public void init() {
		try {
			tempSessionService.put(ACTIVE_CALLS, new ActiveCalls());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean put(String key, Serializable value) {

		if (getData() == null) {
			init();
		}
		ActiveCalls activeCalls = getData();
		activeCalls.getData().put(key, value);
		return updateData(activeCalls);
	}
	
	@Override
	public synchronized boolean updateData(ActiveCalls activeCalls) {
		try {
			tempSessionService.put(ACTIVE_CALLS, activeCalls);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public synchronized ActiveCalls getData() {

		try {
			return tempSessionService.get(ACTIVE_CALLS, ActiveCalls.class);
		} catch (Exception e) {
			 
			e.printStackTrace();
		}
		return null;
	}

	public synchronized boolean remove(String key) {
		try {
			ActiveCalls activeCalls = getData();
			activeCalls.getData().remove(key);
			updateData(activeCalls);
			return true;
		} catch (Exception e) {

			e.printStackTrace();
		}
		return false;
	}

	public boolean containsKey(String key) {
		if (getData() == null) {
			init();
		}
		ActiveCalls activeCalls = getData();
		return activeCalls.getData().containsKey(key);
	}

	public boolean clear() {

		if (getData() == null) {
			init();
		}
		ActiveCalls activeCalls = getData();
		activeCalls.getData().clear();
		return updateData(activeCalls);
	}

	public HashMap<String, Object> getMap() {
		try {
			return getData().getData();
		}catch (Exception e) {
			return null;
		}
	}

}
