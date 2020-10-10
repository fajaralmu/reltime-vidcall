package com.fajar.livestreaming.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.ConferenceData;
import com.fajar.livestreaming.dto.ConferenceMember;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.runtimerepo.ActiveRoomsRepository;
import com.fajar.livestreaming.runtimerepo.ConferenceDataRepository;
import com.fajar.livestreaming.util.DateUtil;
import com.fajar.livestreaming.util.SchedulerUtil;
import com.fajar.livestreaming.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PublicConference1Service {

	@Autowired
	private ActiveRoomsRepository activeRoomsRepository;
	@Autowired
	private ConferenceDataRepository conferenceDataRepository;
	
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private RealtimeService realtimeService; 
	
	@Value("${app.streaming.maxRecordingTime}")
	private Integer maxRecordingTime;

	public String getRoomIdOfUser(HttpServletRequest httpRequest) {
		RegisteredRequest session = userSessionService.getRegisteredRequest(httpRequest);
		if (session == null) {
			return null;
		}

		return activeRoomsRepository.get(session.getRequestId());
	}

	public synchronized WebResponse generateRoomId(HttpServletRequest httpRequest) {
		RegisteredRequest session = userSessionService.getRegisteredRequest(httpRequest);
		if (session == null) {
			return null;
		}
		String newRoomId = StringUtil.generateRandomNumber(1) + StringUtil.generateRandomChar(4).toLowerCase();
		String oldRoomId = activeRoomsRepository.get(session.getRequestId());
		activeRoomsRepository.put(session.getRequestId(), newRoomId);
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

		conferenceDataRepository.put(newRoomId, ConferenceData.newRegisteredRoom(creatorId, newRoomId));
	}

	public WebResponse invalidateRoom(HttpServletRequest httpRequest, WebRequest request) {
		RegisteredRequest session = userSessionService.getRegisteredRequest(httpRequest);
		if (session == null) {
			return null;
		}
		
		String roomId = request.getRoomId();
		if (validateCode(roomId)) {
			updateconferenceDataRepository(session.getRequestId(), roomId, null);
		}

		activeRoomsRepository.remove(session.getRequestId());

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

		RegisteredRequest session = userSessionService.getRegisteredRequestById(creatorId);
		return session;

	}

	public boolean validateCode(String roomId) {
		return activeRoomsRepository.validateCode(roomId);
	}

	public synchronized WebResponse leaveRoom(WebRequest request) {
		String roomId = request.getRoomId();
		String requestId = request.getOriginId();
		RegisteredRequest registeredRequest = userSessionService.getRegisteredRequestById(requestId);

		if (!validateCode(roomId) || registeredRequest == null) {
			return WebResponse.failed();
		}

//		conferenceDataRepository.get(roomId).getMembers().remove(requestId);
		conferenceDataRepository.removeMember(roomId, requestId);
		WebResponse response = WebResponse.builder()
				.username(registeredRequest.getUsername()).date(new Date()).requestId(requestId).build();
		
		realtimeService.convertAndSend("/wsResp/leaveroom/" + roomId, response);
		return response;
	}

	public synchronized WebResponse joinRoom(WebRequest request) {
		String roomId = request.getRoomId();
		String requestId = request.getOriginId();
		boolean roomCreator = false;
		RegisteredRequest registeredRequest = userSessionService.getRegisteredRequestById(requestId);

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
		HashMap<String, ConferenceMember> memberIds = conferenceData.getMembers();
		
		for (Entry<String, ConferenceMember> entry : memberIds.entrySet()) {
			
			RegisteredRequest memberSession = userSessionService.getRegisteredRequestById(entry.getKey());
			
			if (memberSession.getRequestId().equals(conferenceData.getCreatorRequestId())) {
				memberSession.setRoomCreator(true);
			}
			
			ConferenceMember memberData = entry.getValue();
			memberSession.setConferenceMemberData(memberData);
			
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
		RegisteredRequest session = userSessionService.getRegisteredRequestById(requestId);
		if (null == session) {
			return null;
		}

		Message message = Message.create(session, body);
		return message;
	}

	public WebResponse togglePeerStream(WebRequest request) {
		String originId = request.getOriginId();
		String roomId = request.getRoomId();
		boolean streamEnabled = request.isStreamEnabled();
		WebResponse response = WebResponse.builder().streamEnabled(streamEnabled).requestId(originId).build();
		
		conferenceDataRepository.updateEnableStream(roomId, originId, streamEnabled);
		
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

	public WebResponse peerconfirm(WebRequest request) {
		String roomId = request.getRoomId();
		String peerId = request.getDestination();
		WebResponse response = WebResponse.builder().requestId(request.getOriginId()).build();
		
		realtimeService.convertAndSend("/wsResp/peerconfirm/" + roomId+"/"+peerId, response);
		return response;
	}

	public WebResponse startRecordingPeer(HttpServletRequest httpRequest, WebRequest request) {
		
		RegisteredRequest userSession = userSessionService.getRegisteredRequest(httpRequest);
		String roomId = request.getRoomId();
		String peerId = request.getDestination();
		String userRequestId = userSession.getRequestId();
		String schedulerId = roomId+peerId+userRequestId;
		
		SchedulerUtil.SchedulerCallback callback = new SchedulerUtil.SchedulerCallback() {

			boolean running = true;
			
			@Override
			public void action(int counter) {
				WebResponse response = WebResponse.builder().counter(counter).message(DateUtil.secondToTimeString(counter)).requestId(peerId).build();
				realtimeService.convertAndSend("/wsResp/recordingtimer/" + roomId+"/"+userRequestId, response);
			}
			
			@Override
			public void end(String cause, int counter) {
				WebResponse response = WebResponse.builder().code(DateUtil.secondToTimeString(counter)).message(cause).requestId(peerId).build();
				realtimeService.convertAndSend("/wsResp/recordingtimer/" + roomId+"/"+userRequestId, response);
				removeSchedulerCallback(this.getId());
			}
 
			public String getId() { return schedulerId; } 
			public int getMaxTime() { return maxRecordingTime; } 
			public void stop() { running = false; } 
			public boolean isRunning() { return running; }
		};
		
//		SchedulerUtil.registerScheduler(callback);
		
		return WebResponse.builder().message(schedulerId).build();
	}
	
	public WebResponse stopRecording(HttpServletRequest httpRequest, String schedulerId) {
		 
		SchedulerUtil.SchedulerCallback schedulerCallback = SchedulerUtil.getScheduler(schedulerId);
		
		if(schedulerCallback == null) {
			return WebResponse.builder().message("schedulerCallback NOT FOUND").build();
		}
//		schedulerCallback.stop();
//		removeSchedulerCallback(schedulerId);
		
		return new WebResponse();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void removeSchedulerCallback(String id) {
		SchedulerUtil.removeScheduler(id);
	}

	public static void main(String[] args) {
 
	}
}
