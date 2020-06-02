package com.fajar.livestreaming.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebPageData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4250445003165442956L;
	private String pageUrl;
	private String title;

}
