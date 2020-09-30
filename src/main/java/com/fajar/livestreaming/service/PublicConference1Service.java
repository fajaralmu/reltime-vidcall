package com.fajar.livestreaming.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.util.StringUtil;

@Service
public class PublicConference1Service {

	private final HashMap<String, String> activeRoomId = new HashMap<>();
	private final HashMap<String, HashMap<String, Date>> roomMembers = new HashMap<>();
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private RealtimeService realtimeService;

	public String getRoomIdOfUser(HttpServletRequest httpRequest) {
		RegisteredRequest session = userSessionService.getRegisteredRequest(httpRequest);
		if (session == null) {
			return null;
		}

		return activeRoomId.get(session.getRequestId());
	}

	public synchronized WebResponse generateRoomId(HttpServletRequest httpRequest) {
		RegisteredRequest session = userSessionService.getRegisteredRequest(httpRequest);
		if (session == null) {
			return null;
		}
		String newRoomId = StringUtil.generateRandomChar(4).toLowerCase();
		String oldRoomId = activeRoomId.get(session.getRequestId());
		activeRoomId.put(session.getRequestId(), newRoomId);
		updateRoomMembers(oldRoomId, newRoomId);		

		return WebResponse.builder().message(newRoomId).build();
	}
	
	public synchronized void updateRoomMembers(String oldRoomId, String newRoomId) {
		if(null != oldRoomId && roomMembers.containsKey(oldRoomId)) {
			roomMembers.remove(oldRoomId);
		}
		roomMembers.put(newRoomId, new HashMap<String, Date>());
	}

	public boolean validateCode(String roomId) {

		for (String key : activeRoomId.keySet()) {
			if (activeRoomId.get(key).equals(roomId)) {
				return true;
			}
		}
		return false;
	}

	public synchronized WebResponse leaveRoom(WebRequest request) {
		String roomId = request.getRoomId();
		String requestId = request.getOriginId();
		RegisteredRequest registeredRequest = userSessionService.getRequestFromSessionMap(requestId);

		if (!validateCode(roomId) || registeredRequest == null) {
			return WebResponse.failed();
		}
		 
		roomMembers.get(roomId).remove(requestId );
		realtimeService.convertAndSend("/wsResp/leaveroom/"+roomId, WebResponse.builder().username(registeredRequest.getUsername()).date(new Date()).requestId(requestId).build());
		return new WebResponse();
	}

	public synchronized WebResponse joinRoom(WebRequest request) {
		String roomId = request.getRoomId();
		String requestId = request.getOriginId();
		RegisteredRequest registeredRequest = userSessionService.getRequestFromSessionMap(requestId);

		if (!validateCode(roomId) || registeredRequest == null) {
			return WebResponse.failed();
		}
		if(roomMembers.get(roomId).get(requestId)!=null) {
			return new WebResponse();
		}
		roomMembers.get(roomId).put(requestId, new Date());
		realtimeService.convertAndSend("/wsResp/joinroom/"+roomId, WebResponse.builder().username(registeredRequest.getUsername()).date(new Date()).requestId(requestId).build());
		return new WebResponse();
	}
	
	public List<RegisteredRequest> getMemberList(String roomId){
		List<RegisteredRequest> members = new ArrayList<>();
		
		if(!validateCode(roomId)) {
			return members;
		}
		
		HashMap<String, Date> memberIds = roomMembers.get(roomId);
		for (Entry<String, Date> entry : memberIds.entrySet()) {
			RegisteredRequest memberSession = userSessionService.getRequestFromSessionMap(entry.getKey());
			memberSession.setCreated(entry.getValue());
			members.add(memberSession);
		}
		
		return members;
	}

}
