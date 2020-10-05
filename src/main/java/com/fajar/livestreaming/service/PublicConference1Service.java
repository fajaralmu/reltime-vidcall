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
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.runtimerepo.ActiveRoomsRepository;
import com.fajar.livestreaming.runtimerepo.ConferenceDataRepository;
import com.fajar.livestreaming.util.StringUtil;

@Service
public class PublicConference1Service {

//	private final HashMap<String, String> activeRoomId = new HashMap<>();
	@Autowired
	private ActiveRoomsRepository activeRoomId;
//	private final HashMap<String, ConferenceData> conferenceDataRepository = new HashMap<>();
	@Autowired
	private ConferenceDataRepository conferenceDataRepository;
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
		String newRoomId = StringUtil.generateRandomNumber(1) + StringUtil.generateRandomChar(4).toLowerCase();
		String oldRoomId = activeRoomId.get(session.getRequestId());
		activeRoomId.put(session.getRequestId(), newRoomId);
		updateconferenceDataRepository(session.getRequestId(), oldRoomId, newRoomId);

		return WebResponse.builder().message(newRoomId).build();
	}

	public synchronized void updateconferenceDataRepository(String creatorId, String oldRoomId, String newRoomId) {
		if (null != oldRoomId && conferenceDataRepository.containsKey(oldRoomId)) {
			conferenceDataRepository.remove(oldRoomId);
			nofityRoomInvalidated(oldRoomId);
		}

		if (newRoomId != null) {
			putNewconferenceDataRepository(creatorId, newRoomId);

		} else if (conferenceDataRepository.containsKey(oldRoomId)) {
			conferenceDataRepository.remove(oldRoomId);
			// nofityRoomInvalidated(oldRoomId);
		}

	}

	private void putNewconferenceDataRepository(String creatorId, String newRoomId) {

		conferenceDataRepository.put(newRoomId,
				ConferenceData.builder().creatorRequestId(creatorId).members(new HashMap<>()).build());
	}

	public WebResponse invalidateRoom(HttpServletRequest httpRequest, WebRequest request) {
		RegisteredRequest session = userSessionService.getRegisteredRequest(httpRequest);
		if (session == null) {
			return null;
		}
		final String roomId = request.getRoomId();
		if (validateCode(roomId)) {
			updateconferenceDataRepository(session.getRequestId(), roomId, null);
		}

		activeRoomId.remove(session.getRequestId());

		return new WebResponse();
	}

	private void nofityRoomInvalidated(String roomId) {
		realtimeService.convertAndSend("/wsResp/roominvalidated/" + roomId, WebResponse.success());
	}

	public boolean isRoomOwner(HttpServletRequest httpRequest, String roomId) {
		RegisteredRequest session = userSessionService.getRegisteredRequest(httpRequest);
		if (session == null || validateCode(roomId) == false) {
			return false;
		}
		RegisteredRequest roomOwner = getRoomOwner(roomId);

		return roomOwner != null && roomOwner.getRequestId().equals(session.getRequestId());
	}

	public RegisteredRequest getRoomOwner(String roomId) {
		if (!validateCode(roomId)) {
			return null;
		}
		ConferenceData conferenceData = conferenceDataRepository.get(roomId);
		String creatorId = conferenceData.getCreatorRequestId();

		RegisteredRequest session = userSessionService.getRequestFromSessionMap(creatorId);
		return session;

	}

	public boolean validateCode(String roomId) {
		return activeRoomId.validateCode(roomId);
//		for (String key : activeRoomId.keySet()) {
//			if (activeRoomId.get(key).equals(roomId)) {
//				return true;
//			}
//		}
//		return false;
	}

	public synchronized WebResponse leaveRoom(WebRequest request) {
		String roomId = request.getRoomId();
		String requestId = request.getOriginId();
		RegisteredRequest registeredRequest = userSessionService.getRequestFromSessionMap(requestId);

		if (!validateCode(roomId) || registeredRequest == null) {
			return WebResponse.failed();
		}

//		conferenceDataRepository.get(roomId).getMembers().remove(requestId);
		conferenceDataRepository.removeMember(roomId, requestId);
		realtimeService.convertAndSend("/wsResp/leaveroom/" + roomId, WebResponse.builder()
				.username(registeredRequest.getUsername()).date(new Date()).requestId(requestId).build());
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
		if (conferenceDataRepository.get(roomId).getMembers().get(requestId) != null) {
			return new WebResponse();
		}
		if (conferenceDataRepository.get(roomId).getCreatorRequestId().equals(requestId)) {
			roomCreator = true;
		}
//		conferenceDataRepository.get(roomId).getMembers().put(requestId, new Date());
		conferenceDataRepository.addNewMember(roomId, requestId);

		WebResponse response = WebResponse.builder().username(registeredRequest.getUsername()).date(new Date())
				.requestId(requestId).roomCreator(roomCreator).build();

		realtimeService.convertAndSend("/wsResp/joinroom/" + roomId, response);
		return new WebResponse();
	}

	public List<RegisteredRequest> getMemberList(String roomId) {
		List<RegisteredRequest> members = new ArrayList<>();

		if (!validateCode(roomId)) {
			return members;
		}
		ConferenceData conferenceData = conferenceDataRepository.get(roomId);
		HashMap<String, Date> memberIds = conferenceData.getMembers();
		for (Entry<String, Date> entry : memberIds.entrySet()) {
			RegisteredRequest memberSession = userSessionService.getRequestFromSessionMap(entry.getKey());
			if (memberSession.getRequestId().equals(conferenceData.getCreatorRequestId())) {
				memberSession.setRoomCreator(true);
			}
			memberSession.setCreated(entry.getValue());
			members.add(memberSession);
		}

		return members;
	}

	////////////////////////////// HANDSHAKE
	////////////////////////////// ///////////////////////////////////////////////////

	public WebResponse handshakeWebRtc(WebRequest request) {
		String roomId = request.getRoomId();
		String originId = request.getOriginId();
		String eventId = request.getEventId();
		String destination = request.getDestination();

		WebResponse response = WebResponse.builder().requestId(originId).eventId(eventId)
				.webRtcObject(request.getWebRtcObject()).build();
		realtimeService.convertAndSend("/wsResp/webrtcpublicconference/" + roomId + "/" + destination, response);
		return response;
	}

	//////////////////////////// CHAT MESSAGE
	//////////////////////////// ///////////////////////////////////////////////////

	public List<Message> getChatMessages(String roomId) {
		List<Message> chatMessages = new ArrayList<>();

		if (validateCode(roomId) == false) {
			return chatMessages;
		}

		ConferenceData conferenceData = conferenceDataRepository.get(roomId);
		chatMessages = conferenceData.getChatMessages();
		return chatMessages;
	}

	public WebResponse sendMessage(WebRequest request) {
		// String requestId, String roomId, String body) {
		String roomId = request.getRoomId();
		String body = request.getMessage();
		String requestId = request.getOriginId();
		if (validateCode(roomId) == false) {
			return null;
		}
		Message newMessage = constructMessage(requestId, body);
		if (null == newMessage) {
			return null;
		}

//		conferenceDataRepository.get(roomId).getChatMessages().add(newMessage);
		conferenceDataRepository.addChatMessage(roomId, newMessage);
		WebResponse response = WebResponse.builder().chatMessage(newMessage).build();
		realtimeService.convertAndSend("/wsResp/newchat/" + roomId, response);
		return new WebResponse();
	}

	public Message constructMessage(String requestId, String body) {
		RegisteredRequest session = userSessionService.getRequestFromSessionMap(requestId);
		if (null == session) {
			return null;
		}

		Message message = new Message();
		message.setBody(body);
		message.setRequestId(requestId);
		message.setUsername(session.getUsername());
		return message;
	}

	public WebResponse togglePeerStream(WebRequest request) {
		String originId = request.getOriginId();
		String roomId = request.getRoomId();
		boolean streamEnabled = request.isStreamEnabled();
		WebResponse response = WebResponse.builder().streamEnabled(streamEnabled).requestId(originId).build();
		realtimeService.convertAndSend("/wsResp/togglepeerstream/" + roomId, response);
		return response ;
	}

	public boolean isJoined(HttpServletRequest httpRequest, String roomId) {
		RegisteredRequest session = userSessionService.getRegisteredRequest(httpRequest);
		if (null == session) {
			return false;
		}
		
		ConferenceData conferenceData = conferenceDataRepository.get(roomId);
		return conferenceData != null &&
				conferenceData.getMembers().get(session.getRequestId()) != null;
		 
	}

}
