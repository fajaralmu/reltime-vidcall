package com.fajar.livestreaming.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.ChattingData;
import com.fajar.livestreaming.dto.Message;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.WebRequest;
import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.runtimerepo.AccountSessionRepository;
import com.fajar.livestreaming.runtimerepo.ChatMessageRepository;
import com.fajar.livestreaming.util.ThreadUtil;

@Service
public class ChattingService {

	@Autowired
	private RealtimeService realtimeService;
	@Autowired
	private ChatMessageRepository chatMessageRepository;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private AccountSessionRepository accountSessionRepository;

	public WebResponse sendMessage(WebRequest webRequest, String receiverId, HttpServletRequest httpRequest) {

		RegisteredRequest sender = userSessionService.getRegisteredRequest(httpRequest);
		RegisteredRequest receiver = userSessionService.getRegisteredRequestById(receiverId);

		if (null == sender || receiver == null) {
			return null;
		}
		Message storedMessage = chatMessageRepository.storeMessage(sender, receiver, webRequest.getMessage());
		WebResponse response = WebResponse.builder().chatMessage(storedMessage).build();
		response.setRequestId(sender.getRequestId());

		realtimeService.convertAndSend("/wsResp/newchatting/" + receiverId, response);
		updateReceiverPartnerOrder(receiver, sender);
		return response;
	}

	private void updateReceiverPartnerOrder(RegisteredRequest receiver, RegisteredRequest sender) {
		ThreadUtil.run(() -> {
			receiver.setChattingPartnerFirstOrder(sender.getRequestId());
			accountSessionRepository.update(receiver);
		});
	}

	public List<Message> getChatMessagesBetween(RegisteredRequest sender, RegisteredRequest partner) {
		ChattingData chatMessageData = chatMessageRepository.getChattingData(sender, partner);
		userSessionService.addChatHistory(sender.getRequestId(), partner.getRequestId());
		return chatMessageData.getMessages();
	}

	public WebResponse getChatMessagesBetween(String partnerId, HttpServletRequest httpServletRequest) {
		RegisteredRequest sender = userSessionService.getRegisteredRequest(httpServletRequest);
		RegisteredRequest partner = userSessionService.getRegisteredRequestById(partnerId);
		List<Message> messages = getChatMessagesBetween(sender, partner);
		return WebResponse.builder().resultList(messages).messageList(messages).build();
	}

	public WebResponse getPartnerInfo(String partnerId, HttpServletResponse response) {
		RegisteredRequest partner = userSessionService.getRegisteredRequestById(partnerId);

		if (null == partner) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return WebResponse.failed("not found");
		}
		return WebResponse.builder().registeredRequest(partner).build();
	}

	public WebResponse sendTypingStatus(WebRequest request) {
		WebResponse response = WebResponse.builder().typing(request.isTyping()).build();
		String origin = request.getOriginId(), destination = request.getDestination();
		realtimeService.convertAndSend("/wsResp/typingstatus/" + origin + "/" + destination, response);
		return response;
	}

	public WebResponse getChattingList(HttpServletRequest httpRequest) {
		RegisteredRequest registeredRequest = userSessionService.getRegisteredRequest(httpRequest);

		List<String> partnerIds = registeredRequest.getChattingPartnerList();
		List<RegisteredRequest> chattingPartners = new ArrayList<RegisteredRequest>();

		for (String partnerId : partnerIds) {
			try {
				RegisteredRequest partner = userSessionService.getRegisteredRequestById(partnerId);
				Date lastMessageDate = chatMessageRepository.getLastMessageDate(registeredRequest, partner);
				// TODO: move to appropriate field
				partner.setCreated(lastMessageDate);
				chattingPartners.add(partner);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return WebResponse.builder().resultList(chattingPartners).chattingPartnerList(chattingPartners).build();

	}

	public List<RegisteredRequest> getChattingPartners(RegisteredRequest registeredRequest) {
		List<String> partnerIds = registeredRequest.getChattingPartnerList();
		List<RegisteredRequest> chattingPartners = new ArrayList<RegisteredRequest>();

		for (String partnerId : partnerIds) {
			try {
				RegisteredRequest partner = userSessionService.getRegisteredRequestById(partnerId);
				Date lastMessageDate = chatMessageRepository.getLastMessageDate(registeredRequest, partner);
				// TODO: move to appropriate field
				partner.setCreated(lastMessageDate);
				chattingPartners.add(partner);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return chattingPartners;
	}

	public WebResponse getChattingData(HttpServletRequest httpRequest) {
		RegisteredRequest registeredRequest = userSessionService.getRegisteredRequest(httpRequest);
		List<ChattingData> chattingDataList = getChattingDataList(registeredRequest);
		WebResponse webResponse = new WebResponse();
		webResponse.setChattingDataList(chattingDataList);
		return webResponse;
	}

	public List<ChattingData> getChattingDataList(RegisteredRequest registeredRequest) {
		List<String> partnerIds = registeredRequest.getChattingPartnerList();
		List<ChattingData> chattingDataList = new ArrayList<ChattingData>();
		List<RegisteredRequest> chattingPartners = getChattingPartners(registeredRequest);

		for (RegisteredRequest chattingPartner : chattingPartners) {

			ChattingData chattindData = chatMessageRepository.getChattingData(registeredRequest, chattingPartner);
			chattingDataList.add(chattindData);

		}
		return chattingDataList;
	}

	public void markMessageAsRead(WebRequest request) {
		ThreadUtil.run(()->{
			String partnerId = request.getPartnerId();
			String requestId = request.getOriginId();
			RegisteredRequest sender = userSessionService.getRegisteredRequestById(requestId);
			RegisteredRequest receiver = userSessionService.getRegisteredRequestById(partnerId);
			if (null != sender && receiver != null) {
				chatMessageRepository.markMessageAsRead(sender, receiver);
			}
		});
	}

}
