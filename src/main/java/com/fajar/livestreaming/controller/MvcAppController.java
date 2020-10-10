package com.fajar.livestreaming.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.livestreaming.annotation.CustomRequestInfo;

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

	@RequestMapping(value = { "/" , "/index.html"})
	@CustomRequestInfo(pageUrl = "pages/main-menu")
	public String sessionlist(Model model,
			HttpServletRequest request, HttpServletResponse response)  {
		model.addAttribute("title", applicationHeaderLabel);
		return basePage;
	}

	 

}
