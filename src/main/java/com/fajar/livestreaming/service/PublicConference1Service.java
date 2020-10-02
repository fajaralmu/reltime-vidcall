package com.fajar.livestreaming.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.ConferenceData;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.util.StringUtil;

@Service
public class PublicConference1Service {

	private final HashMap<String, String> activeRoomId = new HashMap<>();
	private final HashMap<String, ConferenceData> roomMembers = new HashMap<>();
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
		String newRoomId = StringUtil.generateRandomNumber(1)+StringUtil.generateRandomChar(4).toLowerCase();
		String oldRoomId = activeRoomId.get(session.getRequestId());
		activeRoomId.put(session.getRequestId(), newRoomId);
		updateRoomMembers(session.getRequestId(), oldRoomId, newRoomId);		

		return WebResponse.builder().message(newRoomId).build();
	}
	
	public synchronized void updateRoomMembers(String creatorId, String oldRoomId, String newRoomId) {
		if(null != oldRoomId && roomMembers.containsKey(oldRoomId)) {
			roomMembers.remove(oldRoomId);
		}
		if(newRoomId != null) {
			putNewRoomMembers(creatorId, newRoomId);
		}else if(roomMembers.containsKey(newRoomId)) {
			roomMembers.remove(oldRoomId);
		}
		
	}
	
	private void putNewRoomMembers(String creatorId, String newRoomId) {
		 
		roomMembers.put(newRoomId, ConferenceData.builder().creatorRequestId(creatorId).members(new HashMap<>()).build());
	}

	public WebResponse invalidateRoom(HttpServletRequest httpRequest, WebRequest request) {
		RegisteredRequest session = userSessionService.getRegisteredRequest(httpRequest);
		if (session == null) {
			return null;
		}
		final String roomId = request.getRoomId();
		if(validateCode(roomId)) {
			updateRoomMembers(session.getRequestId(), roomId, null);
		}
		
		activeRoomId.remove(session.getRequestId());
		realtimeService.convertAndSend("/wsResp/roominvalidated/"+roomId, WebResponse.success());
		return new WebResponse();
	}
	
	public boolean isRoomOwner(HttpServletRequest httpRequest, String roomId) {
		RegisteredRequest session = userSessionService.getRegisteredRequest(httpRequest);
		if (session == null) {
			return false;
		}
		
		String activeRoom = activeRoomId.get(session.getRequestId()) ;
		return activeRoom != null && activeRoom.equals(roomId);
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
		 
		roomMembers.get(roomId).getMembers().remove(requestId );
		realtimeService.convertAndSend("/wsResp/leaveroom/"+roomId, WebResponse.builder().username(registeredRequest.getUsername()).date(new Date()).requestId(requestId).build());
		return new WebResponse();
	}

	public synchronized WebResponse joinRoom(WebRequest request) {
		String roomId = request.getRoomId();
		String requestId = request.getOriginId();
		boolean roomCreator = false;
		RegisteredRequest registeredRequest = userSessionService.getRequestFromSessionMap(requestId);

		if (!validateCode(roomId) || registeredRequest == null) {
			return WebResponse.failed();
		}
		if(roomMembers.get(roomId).getMembers().get(requestId)!=null) {
			return new WebResponse();
		}
		if(roomMembers.get(roomId).getCreatorRequestId().equals(requestId)) {
			roomCreator = true;
		}
		roomMembers.get(roomId).getMembers().put(requestId, new Date());
		
		WebResponse response = WebResponse.builder()
				.username(registeredRequest.getUsername())
				.date(new Date())
				.requestId(requestId)
				.roomCreator(roomCreator)
				.build();
		
		realtimeService.convertAndSend("/wsResp/joinroom/"+roomId, response);
		return new WebResponse();
	}
	
	public List<RegisteredRequest> getMemberList(String roomId){
		List<RegisteredRequest> members = new ArrayList<>();
		
		if(!validateCode(roomId)) {
			return members;
		}
		ConferenceData conferenceData = roomMembers.get(roomId);
		HashMap<String, Date> memberIds = conferenceData.getMembers();
		for (Entry<String, Date> entry : memberIds.entrySet()) {
			RegisteredRequest memberSession = userSessionService.getRequestFromSessionMap(entry.getKey());
			if(memberSession.getRequestId().equals(conferenceData.getCreatorRequestId())){
				memberSession.setRoomCreator(true);
			}
			memberSession.setCreated(entry.getValue());
			members.add(memberSession);
		}
		
		return members;
	}

	////////////////////////////// HANDSHAKE ///////////////////////////////////////////////////
	
	public WebResponse handshakeWebRtc(WebRequest request) {
		String roomId = request.getRoomId();
		String originId = request.getOriginId();
		String eventId = request.getEventId();
		String destination = request.getDestination();
		
		WebResponse response = WebResponse.builder().requestId(originId).eventId(eventId).webRtcObject(request.getWebRtcObject()).build();
		realtimeService.convertAndSend("/wsResp/webrtcpublicconference/"+roomId+"/"+destination , response); 
		return response;
	}

}
