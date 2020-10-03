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
	@Builder.Default
	private HashMap<String, Date> members = new HashMap();
	@Builder.Default
	private Date createdDate = new Date();
	@Builder.Default
	private List<Message> chatMessages = new ArrayList<Message>();

}
