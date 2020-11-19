package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChattingData implements Serializable {
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
	private RegisteredRequest partner;

	public void addMessage(Message message) {
		setLatestMessage(message);
		messages.add(message);
	}

	private int unreadMessages;

	public void addUnreadMessage() {
		setUnreadMessages(unreadMessages + 1);

	}

	public void removeUnreadMessage() {
		setUnreadMessages(0);
	}

	public boolean hasUnreadMessage() {
		return unreadMessages > 0;
	}

}
