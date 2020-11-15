package com.fajar.livestreaming.runtimerepo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.service.runtime.TempSessionService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class ChatMessageRepository implements BaseRuntimeRepo<ChatMessageRepository.ChatMessageData> {

	@Autowired
	private TempSessionService tempSessionService;

	@PostConstruct
	public void init() {

	}

	@Override
	public ChatMessageData get(String messageDataKey) {

		ChatMessageData roomData = null;
		try {
			roomData = tempSessionService.get(messageDataKey, ChatMessageData.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roomData == null ? null : roomData;
	}
	
	public synchronized Message storeMessage(RegisteredRequest sender, RegisteredRequest receiver, String body) {
		ChatMessageData chatMessageData = getChatMessage(sender, receiver);
		chatMessageData.addMessage(sender, receiver, body);
		
		try {
			tempSessionService.put(chatMessageData.getKey(), chatMessageData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chatMessageData == null ? null : chatMessageData.getLatestMessage();
	}
	
	public synchronized ChatMessageData getChatMessage(RegisteredRequest sender, RegisteredRequest receiver) {
		String senderId = sender.getRequestId();
		String receiverId = receiver.getRequestId();
		
		
		ChatMessageData chatMessageData = get(senderId+"_"+receiverId);
		if(null == chatMessageData) {
			chatMessageData  = get(receiverId+"_"+senderId);
		}
		
		if(null == chatMessageData) {
			chatMessageData = new ChatMessageData();
			chatMessageData.setKey(senderId+"_"+receiverId);
		}
		
		
		return chatMessageData;
	} 

	public boolean remove(String messageDataKey) {
		try {
			tempSessionService.remove(messageDataKey, ChatMessageData.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	} 

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ChatMessageData implements Serializable {
		/**
		* 
		*/
		private static final long serialVersionUID = 5112666284732804863L;
		@Builder.Default
		private Date date = new Date();
		private String key;
		@Default
		private List<Message> messages = new java.util.ArrayList<>();
		private Message latestMessage;
		
		public void addMessage(RegisteredRequest sender, RegisteredRequest receiver, String body) {
			setLatestMessage(Message.create(sender, receiver, body));
			messages.add(getLatestMessage());
		}
		 
	}

	@Override
	public List<ChatMessageData> getAll() {

		return tempSessionService.getAllFiles(ChatMessageData.class);
	}

	@Override
	public boolean deleteByKey(String key) {

		return remove(key);
	}

	@Override
	public boolean clearAll() {
		List<ChatMessageData> rooms = getAll();
		for (ChatMessageData activeRoomData : rooms) {
			deleteByKey(activeRoomData.getKey());
		}
		return false;
	}

}
