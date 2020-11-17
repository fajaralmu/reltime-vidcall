package com.fajar.livestreaming.dto;

import static com.fajar.livestreaming.util.CollectionUtil.setFirstOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.SerializationUtils;

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
	private LinkedList<String> chattingPartnerList = new LinkedList<>();

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
	
	public void setChattingPartnerFirstOrder(String partnerId) {
		if(chattingPartnerList.indexOf(partnerId)<0) {
			return;
		}
		setFirstOrder(partnerId, chattingPartnerList);
	}
	

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("A");
		list.add("C");
		list.add("B");
		list.add("D");
		list.add("E");
		list.add("K");
		list.add("R");
		System.out.println(list);
		setFirstOrder("B", list);
		System.out.println(list);
	}
	
}
