package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionData implements Remote, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1210492423406561769L;
	private String requestId;
	@Default
	private Date registeredDate = new Date();
	private boolean valid;
}
