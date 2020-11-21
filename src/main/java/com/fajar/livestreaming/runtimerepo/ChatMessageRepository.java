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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

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
		ChattingData senderChattingData = getChattingData(sender, receiver);
		ChattingData receiverChattingData = getChattingData(receiver, sender);
		receiverChattingData.addUnreadMessage();
		
		Message message = Message.create(sender, receiver, body);
		
		senderChattingData.addMessage(message);
		receiverChattingData.addMessage(message);
		 
		boolean result1 = updateChattingData(senderChattingData);
		boolean result2 = updateChattingData(receiverChattingData);
		 
		return result1 && result2 ? senderChattingData.getLatestMessage():null; 
	}
	
	public boolean updateChattingData(ChattingData chattingData) {
		try {
			tempSessionService.put(chattingData.getKey(), chattingData);
			return true;
		}catch (Exception e) {
			log.error("ERROR updateChattingData: {}", e);
			return false;
		}
	}

	public synchronized ChattingData getChattingData(RegisteredRequest sender, RegisteredRequest receiver) {
		String senderId = sender.getRequestId();
		String receiverId = receiver.getRequestId();
		final String key = "SENDER_"+senderId + "_RECEIVER_" + receiverId;
		ChattingData chatMessageData = get(key);
		
		if (null == chatMessageData) {
			chatMessageData = new ChattingData();
			chatMessageData.setKey(key);
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
		log.info("markMessageAsRead sender: {}, partner: {}", sender.getUsername(), receiver.getUsername());
		ChattingData chatMessageData = getChattingData(sender, receiver);
		if (null == chatMessageData) {
			log.info("markMessageAsRead Chat message not found");
			return false;
		}
		chatMessageData.removeUnreadMessage();
		try {
			tempSessionService.put(chatMessageData.getKey(), chatMessageData);
			log.info("marking message as read success");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
