package com.fajar.livestreaming.controller;

import java.io.IOException;

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
@RequestMapping("app")
public class MvcAppController extends BaseController{  
	 

	public MvcAppController() {
		log.info("-----------------Mvc App Controller------------------");
	}

	@RequestMapping(value = { "/" })
	public String sessionlist(Model model,
			HttpServletRequest request, HttpServletResponse response)  {
		
		RegisteredRequest currentRequest = userSessionService.getRegisteredRequest(request);
		try {
			model.addAttribute("currentRequest", currentRequest);
			model.addAttribute("title", "Welcome"); 
			model.addAttribute("pageUrl", "pages/main-menu");
		}catch (Exception e) {
			model.addAttribute("title", "Invalid Session");
		}
		return basePage;
	}

	 

}
