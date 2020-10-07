package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredRequest implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -2584171097698972770L; 
	 
	private String requestId;
	
	private String username;
	 
	private String value;
	 
	@JsonFormat(pattern = "dd-MM-yyyy' 'hh:mm:ss")
	private Date created;
	 
	private String referrer;
	 
	private String userAgent;
	 
	private String ipAddress; 
	 
//	private  List<? extends BaseEntity> messages;
	 
	private boolean active;
	
	@Builder.Default
	private boolean exist = true;
	private boolean roomCreator;
	
	private ConferenceMember conferenceMemberData;

	public static RegisteredRequest newSession(String username, String requestId, HttpServletRequest httpRequest) {
		RegisteredRequest registeredRequest = new RegisteredRequest();
		registeredRequest.setUsername(username + "_" + requestId);
		registeredRequest.setActive(false);
		registeredRequest.setCreated(new Date());
		registeredRequest.setUserAgent(httpRequest.getHeader("user-agent"));
		registeredRequest.setRequestId(requestId);
		return registeredRequest;
	}
	
}
