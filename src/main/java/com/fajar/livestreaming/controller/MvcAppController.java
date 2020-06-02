package com.fajar.livestreaming.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
		 
		try { 
			
			setTitle(model, "Welcome"); 
			setPageUrl(model, "pages/main-menu");
			
		}catch (Exception e) {
			setTitle(model, "Invalid Session");
			model.addAttribute("message", "Invalid Session");
		}
		return basePage;
	}

	 

}
