package com.fajar.livestreaming.runtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.config.LogProxyFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TempSessionService {

	private final FlatFileAccessorv2 flatFileAccessor;
	private ObjectMapper objectMapper = new ObjectMapper();
	private final String applicationId;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public TempSessionService(FlatFileAccessorv2 fileAccessor) {
		this.applicationId = RandomStringUtils.randomAlphabetic(7);
		this.flatFileAccessor = fileAccessor;
		objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

	}

	public <T> T get(String key, Class<T> _class) throws Exception {

		String json = flatFileAccessor.getFileContent(prefix(_class) + key);
		log.debug("TempSessionService get JSON: {}", json);
		if (null == json) {
			return null;
		}
		return objectMapper.readValue(json, _class);
	}

	public void put(String key, Serializable data) throws Exception {

		String json = objectMapper.writeValueAsString(data);
		log.debug("TempSessionService set JSON: {}", json);
		flatFileAccessor.putKeyValue(prefix(data.getClass()) + key, json);
	}

	public void remove(String key, Class<?> _class) throws Exception {

		flatFileAccessor.removeLineWithKey(prefix(_class) + key);
	}

	private String prefix(Class<?> _class) {
		return applicationId + "_" + _class.getSimpleName() + "_";
	}

	public <T> List<T> getAllFiles(Class<T> _class) {
		String[] fileNames = flatFileAccessor.getFileNames(applicationId+"_"+_class.getSimpleName());

		List<T> result = new ArrayList<T>();
		if (fileNames != null)
			for (String fileName : fileNames) {
				String json = flatFileAccessor.getFileContent(fileName, true);
				log.info("name: {} => JSON: {}", fileName, json);
				try {
					if(json != null) result.add(objectMapper.readValue(json, _class));
				} catch ( Exception e) { 
					e.printStackTrace();
				}
			}
		return result;
	}

}
