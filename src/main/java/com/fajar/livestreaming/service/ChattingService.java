package com.fajar.livestreaming.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.runtimerepo.ChatMessageRepository;
import com.fajar.livestreaming.runtimerepo.ChatMessageRepository.ChatMessageData;

@Service
public class ChattingService {
	
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
	
	public WebResponse getPartnerInfo(String partnerId, HttpServletResponse response) {
		RegisteredRequest partner = userSessionService.getRegisteredRequestById(partnerId);
		
		if(null == partner) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return WebResponse.failed("not found");
		}
		return WebResponse.builder().registeredRequest(partner).build();
	}

	public WebResponse sendTypingStatus(WebRequest request) {
		WebResponse response = WebResponse.builder().typing(request.isTyping()).build();
		String origin = request.getOriginId(),
				destination = request.getDestination();
		realtimeService.convertAndSend("/wsResp/typingstatus/"+origin+"/"+destination, response);
		return response;
	}

}
