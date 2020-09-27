package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebRtcObject implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 4555511857945709127L;

	private String event;
	private Map<String, Object> data;
}
