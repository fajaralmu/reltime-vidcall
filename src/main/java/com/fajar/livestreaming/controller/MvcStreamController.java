package com.fajar.livestreaming.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.service.StreamingService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("stream")
public class MvcStreamController extends BaseController{ 
	
	@Autowired
	private StreamingService streamingService; 

	public MvcStreamController() {
		log.info("-----------------Mvc Stram Controller------------------");
	}

	@RequestMapping(value = { "/sessionlist" })
	public String sessionlist(Model model, HttpServletRequest request, HttpServletResponse response)  {
		try {
			List<RegisteredRequest> sessions = streamingService.getSessionList(request);
			
			setTitle(model,  "Session List");
			setPageUrl(model, "pages/session-list");
			
			model.addAttribute("sessions", sessions);
			
		}catch (Exception e) {
			e.printStackTrace();
			setTitle(model,  "Invalid Session");
			model.addAttribute("message", "Invalid Session");
		}
		return basePage;
	}

	@RequestMapping(value = { "/videocall/{partnerId}" })
	public String videocall(Model model, @PathVariable String partnerId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException { 
		
		
		RegisteredRequest partnerSession;
		try {
			partnerSession = streamingService.getPartnerSession(partnerId); 
			
			streamingService.setActive(request);
		 
			model.addAttribute("partnerId", partnerId);
			
			setTitle(model,  "Video Call");
			setPageUrl(model,  "pages/video-call");
			
			model.addAttribute("partnerInfo", partnerSession);
			
		} catch (Exception e) { 
			 model.addAttribute("message", "Invalid Session");
		}
		return basePage;
	} 

}
