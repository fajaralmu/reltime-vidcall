package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceData implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -4909578318437777004L;
	
	private String creatorRequestId;
	private String roomId;
	@Builder.Default
	private HashMap<String, ConferenceMember> members = new HashMap();
	@Builder.Default
	private Date createdDate = new Date();
	@Builder.Default
	private List<Message> chatMessages = new ArrayList<Message>();
	
	public static ConferenceData newRegisteredRoom(String creatorId, String roomId) {
		 
		return builder().roomId(roomId).creatorRequestId(creatorId).members(new HashMap<>()).build();
	}

}
