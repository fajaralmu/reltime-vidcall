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

import com.fajar.livestreaming.annotation.Authenticated;
import com.fajar.livestreaming.annotation.CustomRequestInfo;
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
@Authenticated
public class MvcConferenceController extends BaseController {

	@Autowired
	private StreamingService streamingService;

	public MvcConferenceController() {
		log.info("-----------------Mvc Conference Controller------------------");
	}

	@RequestMapping(value = { "/videocall/{partnerId}" })
	@CustomRequestInfo(title = "Video Call", pageUrl = "pages/videocall/video-call")
	public String videocall(Model model, @PathVariable String partnerId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		RegisteredRequest partnerSession = streamingService.getPartnerSession(partnerId);
		streamingService.setActive(request);

		model.addAttribute("partnerId", partnerId);

		model.addAttribute("partnerInfo", partnerSession);

		return basePage;
	}

	@RequestMapping(value = { "/videocallv2/{partnerId}" })
	@CustomRequestInfo(title = "Video Call v2", pageUrl = "pages/videocall/video-call-v2")
	public String videocallv2(Model model, @PathVariable String partnerId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		RegisteredRequest partnerSession = streamingService.getPartnerSession(partnerId);
		streamingService.setActive(request);

//			boolean notAnsweringCall = request.getParameter("referrer") == null || !request.getParameter("referrer").equals("calling");
//			if(notAnsweringCall) {
//				streamingService.notifyCallingPartner(request, partnerSession);
//			}

		model.addAttribute("partnerId", partnerId);
		model.addAttribute("partnerUsername", partnerSession.getUsername());

		model.addAttribute("partnerInfo", partnerSession);
		return basePage;
	}
	
	@RequestMapping(value = { "/publicconference/{roomId}" })
	@CustomRequestInfo(title = "Video Call v2", pageUrl = "pages/videocall/public-conference-v1")
	public String publicconference(Model model, @PathVariable(name="roomId") String roomId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		boolean codeIsValid = userSessionService.validateCode(roomId);
		if(!codeIsValid) {
			throw new Exception("Invalid Code");
		}
		model.addAttribute("roomId", roomId);
		return basePage;
	}

}
