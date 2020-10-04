package com.fajar.livestreaming.runtimerepo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.ConferenceData;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.service.runtime.TempSessionService;

@Service
public class ConferenceDataRepository implements RuntimeRepository {

	@Autowired
	private TempSessionService tempSessionService;

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateData(Object data) {
		// TODO Auto-generated method stub
		return false;
	}

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
			conferenceData.getMembers().put(requestId, new Date());
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

}
