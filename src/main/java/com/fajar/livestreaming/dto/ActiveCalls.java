package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveCalls implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 2862260452570216446L;

	@Builder.Default
	private HashMap<String, Object> data = new HashMap<String, Object>();
	
	
}
