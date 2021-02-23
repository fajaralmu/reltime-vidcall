package com.fajar.livestreaming.service;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TurnServerProperty implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2296350290749308473L;
	private String url;
	private String username;
	private String password;

}
