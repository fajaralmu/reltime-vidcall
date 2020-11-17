package com.fajar.livestreaming.runtimerepo;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.service.runtime.TempSessionService;

@Service
public class AccountSessionRepository implements BaseRuntimeRepo<RegisteredRequest> {

	@Autowired
	private TempSessionService tempSessionService;

	@PostConstruct
	public void init() {
		
	}

	@Override
	public RegisteredRequest get(String requestId) {

		RegisteredRequest roomData = null;
		try {
			roomData = tempSessionService.get(requestId, RegisteredRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roomData == null ? null : roomData;
	}
	 

	public boolean remove(String requestId) {
		try {
			tempSessionService.remove(requestId, RegisteredRequest.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}  

	@Override
	public List<RegisteredRequest> getAll() {

		return tempSessionService.getAllFiles(RegisteredRequest.class);
	}

	@Override
	public boolean deleteByKey(String key) {

		return remove(key);
	}

	@Override
	public boolean clearAll() {
		List<RegisteredRequest> rooms = getAll();
		for (RegisteredRequest activeRoomData : rooms) {
			deleteByKey(activeRoomData.getRequestId());
		}
		return false;
	}

	public boolean update(RegisteredRequest request) {
		try {
			tempSessionService.put(request.getRequestId(), request);
			return true;
		} catch (Exception e) { 
			e.printStackTrace();
			return false;
		}
		
	}

	public void setActive(String requestId, boolean active) {
		try {
			RegisteredRequest request = get(requestId);
			request.setActive(active);
			update(request);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void addChattingPartner(String requestId, String partnerId) {
		try {
			RegisteredRequest accountSession = get(requestId);
			RegisteredRequest partnerSession = get(partnerId);
			
			accountSession.addChattingPartner(partnerId);			
			partnerSession.addChattingPartner(requestId);
			
			update(partnerSession);
			update(accountSession);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
