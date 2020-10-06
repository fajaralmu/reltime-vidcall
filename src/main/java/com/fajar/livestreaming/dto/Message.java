package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3400367607860353523L;
	
	@Builder.Default
	private Date date = new Date();
	private String requestId;
	private String username;
	private String body;
	
	public static Message create(RegisteredRequest session, String body) {
		Message message =new Message();
		message.setBody(body);
		message.setRequestId(session.getRequestId());
		message.setUsername(session.getUsername());
		
		return message;
		
	}

}
