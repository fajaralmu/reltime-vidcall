package com.fajar.livestreaming.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 110411933791444017L; 
	private String destination;
	private String username;
	private String value; 
	
	private String imageData;
	private String audioData;
	private String partnerId;
	private String originId; 
	private String message;
	
	private String roomId;
	private String eventId;
	
	private WebRtcObject webRtcObject;
	
	private boolean accept;
	private boolean streamEnabled;
	private boolean typing;

}
