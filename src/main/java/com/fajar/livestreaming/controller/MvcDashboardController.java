package com.fajar.livestreaming.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.livestreaming.annotation.Authenticated;
import com.fajar.livestreaming.annotation.CustomRequestInfo;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.service.PublicConference1Service;
import com.fajar.livestreaming.service.StreamingService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("dashboard")
@Authenticated
public class MvcDashboardController extends BaseController {

	@Autowired
	private StreamingService streamingService;
	@Autowired
	private PublicConference1Service publicConference1Service;

	public MvcDashboardController() {
		log.info("-----------------Mvc App Controller------------------");
	}

	@RequestMapping(value = { "/" })
	@CustomRequestInfo(title = "Dashboard", pageUrl = "pages/dashboard/index")
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) {

		model.addAttribute("roomId", publicConference1Service.getRoomIdOfUser(request));
		return basePage;
	}

	@RequestMapping(value = { "/sessionlist" })
	@CustomRequestInfo(title = "Session List", pageUrl = "pages/dashboard/session-list")
	public String sessionlist(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<RegisteredRequest> sessions = streamingService.getSessionList(request);

		model.addAttribute("sessions", sessions);

		return basePage;
	}

}
