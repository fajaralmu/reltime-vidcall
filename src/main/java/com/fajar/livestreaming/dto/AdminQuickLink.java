package com.fajar.livestreaming.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminQuickLink implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4379467118342474217L;
	private String id;
	private String link;
	private String label;
	private boolean hasPathVariable;
	
	@Setter(value = AccessLevel.NONE)
	private String pathVariableName;
	
	@Builder.Default
	private List<KeyValue<String, String>> pathVariableNameList = new ArrayList<>();
	
	public void setPathVariableName(String pathVariableName) {
		pathVariableNameList.clear();
		this.pathVariableName = pathVariableName;
		if(pathVariableName.contains(",")) {
			String[] values = pathVariableName.split(",");
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				if(null!= value && !value.isEmpty()) {
					 
					pathVariableNameList.add(KeyValue.identical(value) );
				}
			}
		}
	}
}
