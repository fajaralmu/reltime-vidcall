package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data  
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class WebResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8345271799535134609L;
	@Builder.Default
	private Date date = new Date(); 
	@Builder.Default
	private String code = "00";
	@Builder.Default
	private String message = "success";
	private String requestId;
	private String username;
	private String imageData;
	private String audioData;
	private String eventId;
	
	private boolean onlineStatus;
	private boolean typing;
	private boolean accept;
	private boolean roomCreator;
	private boolean streamEnabled;
	
	private RegisteredRequest registeredRequest;
	
	private WebRtcObject webRtcObject;
	private Message chatMessage;
	
	private Integer counter;
	
	private List resultList;
	@Default
	private List<ChattingData> chattingDataList = new ArrayList<>();
	@Default
	private List<RegisteredRequest> chattingPartnerList = new ArrayList<>();
	@Default
	private List<Message> messageList = new ArrayList<>();
 
	 
	public static WebResponse failedResponse() {
		return new WebResponse("01","INVALID REQUEST");
	}
	public WebResponse(String code, String message) {
		this.code = code;
		this.message = message;
		this.date = new Date();
	}
	public static WebResponse failed() {
		return   failed("INVALID REQUEST");
	}
	
	public static WebResponse failed(String msg) {
		return new WebResponse("01", msg);
	} 

	public static WebResponse success() {
		return new WebResponse("00", "SUCCESS");
	}
	public static WebResponse invalidSession() { 
		return new WebResponse("02","Invalid Session");
	}
}
