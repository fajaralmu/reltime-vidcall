package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
 
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
	
	@JsonIgnore
	private String encodedRequestId;
	 
	private boolean active;
	
	@Builder.Default
	private boolean exist = true;
	private boolean roomCreator;
	
	private ConferenceMember conferenceMemberData;
	
	@Default
	private List<String> chattingPartnerList = new ArrayList<>();

	public static RegisteredRequest newSession(String username, String requestId, HttpServletRequest httpRequest) {
		RegisteredRequest registeredRequest = new RegisteredRequest();
		registeredRequest.setUsername(username + "_" + requestId);
		registeredRequest.setActive(false);
		registeredRequest.setCreated(new Date());
		registeredRequest.setUserAgent(httpRequest.getHeader("user-agent"));
		registeredRequest.setRequestId(requestId);
		return registeredRequest;
	}
	
	public void addChattingPartner(String partnerId) {
		if(chattingPartnerList.indexOf(partnerId)<0) {
			chattingPartnerList.add(partnerId);
		}
	}
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("ABC");
		list.add("EBC");
		list.add("EBC");
		list.add("FBC");
		
		System.out.println(list.indexOf("EBC"));
	}
	
}
