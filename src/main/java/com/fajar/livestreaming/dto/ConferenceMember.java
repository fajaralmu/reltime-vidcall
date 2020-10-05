package com.fajar.livestreaming.dto;

import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceMember implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 4510609918221122470L;
	@Builder.Default
	private Date date = new Date();
	private String requestId;
	private boolean streamEnabled;
}
