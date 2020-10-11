package com.fajar.livestreaming.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.livestreaming.dto.WebResponse;
import com.fajar.livestreaming.runtimerepo.ActiveCallsRepository;
import com.fajar.livestreaming.runtimerepo.ActiveRoomsRepository;
import com.fajar.livestreaming.runtimerepo.BaseRuntimeRepo;
import com.fajar.livestreaming.runtimerepo.ConferenceDataRepository;
import com.fajar.livestreaming.runtimerepo.SessionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UtilityService {

	@Autowired
	private PublicConference1Service publicConference1Service;
	@Autowired
	private ActiveCallsRepository activeCallsRepository;
	@Autowired
	private ActiveRoomsRepository activeRoomsRepository;
	@Autowired
	private ConferenceDataRepository conferenceDataRepository;
	@Autowired
	private SessionRepository sessionRepository;
	
	final Map<String, BaseRuntimeRepo> runtimeRepo = new HashMap<String, BaseRuntimeRepo>();
	
	@PostConstruct
	public void init() {
		runtimeRepo.clear();
		runtimeRepo.put("activecall", this.activeCallsRepository);
		runtimeRepo.put("activeroom", this.activeRoomsRepository);
		runtimeRepo.put("session", this.sessionRepository);
		runtimeRepo.put("conferencedata", this.conferenceDataRepository);
	}
	
	
	public WebResponse getAll(String repoName) {
		log.info("get all data from : {}", repoName);
		
		BaseRuntimeRepo baseRuntimeRepo = runtimeRepo.get(repoName);
		if(null == baseRuntimeRepo) {
			return WebResponse.failed("invalid repoName");
		}
		List list = baseRuntimeRepo.getAll();
		return WebResponse.builder().resultList(list).message("GET_ALL_DATA").build();
	}


	public WebResponse clearAll(String repoName) {
		log.info("clear all data from : {}", repoName);
		
		BaseRuntimeRepo baseRuntimeRepo = runtimeRepo.get(repoName);
		if(null == baseRuntimeRepo) {
			return WebResponse.failed("invalid repoName");
		}
		baseRuntimeRepo.clearAll();
		WebResponse response = getAll(repoName);
		response.setMessage("CLEAR_ALL_DATA");
		return response;
	}
}
