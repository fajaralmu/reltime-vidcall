package com.fajar.livestreaming.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.runtimerepo.ChatMessageRepository;
import com.fajar.livestreaming.runtimerepo.ChatMessageRepository.ChatMessageData;

@Service
public class RealChatService {
	
	@Autowired
	private RealtimeService realtimeService;
	@Autowired
	private ChatMessageRepository chatMessageRepository;
	@Autowired
	private UserSessionService userSessionService;

	public WebResponse sendMessage(WebRequest webRequest, String receiverId, HttpServletRequest httpRequest) {
		
		RegisteredRequest sender = userSessionService.getRegisteredRequest(httpRequest);
		RegisteredRequest receiver = userSessionService.getRegisteredRequestById(receiverId);
		
		if(null == sender || receiver == null) {
			return null;
		}
		Message storedMessage = chatMessageRepository.storeMessage(sender, receiver, webRequest.getMessage());
		WebResponse response = WebResponse.builder().chatMessage(storedMessage).build();
		response.setRequestId(sender.getRequestId());
		realtimeService.convertAndSend("/wsResp/newchatting/"+receiverId, response);
		return response;
	}

	public List<Message> getChatMessagesBetween(RegisteredRequest sender, RegisteredRequest partner) {
		ChatMessageData chatMessageData = chatMessageRepository.getChatMessage(sender, partner);
		return chatMessageData.getMessages();
	}

}
