package com.fajar.livestreaming.runtimerepo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.ConferenceData;
import com.fajar.livestreaming.dto.ConferenceMember;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.service.runtime.TempSessionService;

@Service
public class ConferenceDataRepository implements BaseRuntimeRepo<ConferenceData> {

	@Autowired
	private TempSessionService tempSessionService;

	public boolean containsKey(String oldRoomId) {
		ConferenceData object = null;
		try {
			object = tempSessionService.get(oldRoomId, ConferenceData.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object != null;
	}

	public synchronized boolean remove(String oldRoomId) {

		try {
			tempSessionService.remove(oldRoomId, ConferenceData.class);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public synchronized ConferenceData put(String newRoomId, ConferenceData data) {
		// TODO Auto-generated method stub
		try {
			tempSessionService.put(newRoomId, data);
			return data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public ConferenceData get(String roomId) {
		try {
			return tempSessionService.get(roomId, ConferenceData.class);
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized void addNewMember(String roomId, String requestId) {
		try {
			ConferenceData conferenceData = get(roomId);
			conferenceData.getMembers().put(requestId, ConferenceMember.builder().requestId(requestId).build());
			put(roomId, conferenceData);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public synchronized void removeMember(String roomId, String requestId) {
		try {
			ConferenceData conferenceData = get(roomId);
			conferenceData.getMembers().remove(requestId);
			put(roomId, conferenceData);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public synchronized void addChatMessage(String roomId, Message newMessage) {

		try {
			ConferenceData conferenceData = get(roomId);
			conferenceData.getChatMessages().add(newMessage);
			put(roomId, conferenceData);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public synchronized void updateEnableStream(String roomId, String originId, boolean streamEnabled) {

		try {
			ConferenceData conferenceData = get(roomId);
			conferenceData.getMembers().get(originId).setStreamEnabled(streamEnabled);
			put(roomId, conferenceData);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public List<ConferenceData> getAll() {
		 
		return tempSessionService.getAllFiles(ConferenceData.class);
	}

	@Override
	public boolean deleteByKey(String key) {
		 
		return remove(key);
	}

	@Override
	public boolean clearAll() {
		List<ConferenceData> rooms = getAll();
		for (ConferenceData conferenceData : rooms) {
			deleteByKey(conferenceData.getRoomId());
		}
		return false;
	}

}
