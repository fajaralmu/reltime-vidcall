package com.fajar.livestreaming.runtimerepo;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.ChattingData;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.service.runtime.TempSessionService;

@Service
public class ChatMessageRepository implements BaseRuntimeRepo<ChattingData> {

	@Autowired
	private TempSessionService tempSessionService;

	@PostConstruct
	public void init() {

	}

	@Override
	public ChattingData get(String messageDataKey) {

		ChattingData roomData = null;
		try {
			roomData = tempSessionService.get(messageDataKey, ChattingData.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roomData == null ? null : roomData;
	}

	public synchronized Message storeMessage(RegisteredRequest sender, RegisteredRequest receiver, String body) {
		ChattingData chatMessageData = getChattingData(sender, receiver);
		Message message = Message.create(sender, receiver, body);
		chatMessageData.addUnreadMessage();
		chatMessageData.addMessage(message);

		try {
			tempSessionService.put(chatMessageData.getKey(), chatMessageData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chatMessageData == null ? null : chatMessageData.getLatestMessage();
	}

	public synchronized ChattingData getChattingData(RegisteredRequest sender, RegisteredRequest receiver) {
		String senderId = sender.getRequestId();
		String receiverId = receiver.getRequestId();

		ChattingData chatMessageData = get(senderId + "_" + receiverId);
		if (null == chatMessageData) {
			chatMessageData = get(receiverId + "_" + senderId);
		}

		if (null == chatMessageData) {
			chatMessageData = new ChattingData();
			chatMessageData.setKey(senderId + "_" + receiverId);
		}
		chatMessageData.setPartner(receiver);

		return chatMessageData;
	}

	public boolean remove(String messageDataKey) {
		try {
			tempSessionService.remove(messageDataKey, ChattingData.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<ChattingData> getAll() {

		return tempSessionService.getAllFiles(ChattingData.class);
	}

	@Override
	public boolean deleteByKey(String key) {

		return remove(key);
	}

	@Override
	public boolean clearAll() {
		List<ChattingData> rooms = getAll();
		for (ChattingData activeRoomData : rooms) {
			deleteByKey(activeRoomData.getKey());
		}
		return false;
	}

	public Date getLastMessageDate(RegisteredRequest sender, RegisteredRequest partner) {
		ChattingData chatMessageData = getChattingData(sender, partner);
		if (null == chatMessageData) {
			return new Date();
		}
		return chatMessageData.getLatestMessage() == null ? new Date() : chatMessageData.getLatestMessage().getDate();
	}

	public boolean markMessageAsRead(RegisteredRequest sender, RegisteredRequest receiver) {
		ChattingData chatMessageData = getChattingData(sender, receiver);
		if (null == chatMessageData) {
			return false;
		}
		chatMessageData.removeUnreadMessage();
		try {
			tempSessionService.put(chatMessageData.getKey(), chatMessageData);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
