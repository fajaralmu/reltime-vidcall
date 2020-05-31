package com.fajar.livestreaming.controller;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fajar.livestreaming.service.UserSessionService;
@Controller 
public class BaseController {
	
	protected String basePage = "BASE_PAGE";
	@Autowired
	protected UserSessionService userSessionService;
	
	@ModelAttribute("requestId")
	public String getPublicRequestId(HttpServletRequest request) {
		Cookie cookie = getCookie("JSESSSIONID", request.getCookies());
		String cookieValue = cookie == null ? UUID.randomUUID().toString():cookie.getValue();
		return	cookieValue;
		 
	}
	
	@ModelAttribute("contextPath")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}
	
	/**
	 * ======================================================
	 * 				     	Statics
	 * ======================================================
	 * 
	 */
	
	public static Cookie getCookie(String name, Cookie[] cookies) {
		try {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals(name)) { return cookie; }
			}
		}catch(Exception ex) { ex.printStackTrace(); }
		return null;
	}
	
	/**
	 * send to login page URL
	 * @param request
	 * @param response
	 */
	public static void sendRedirectLogin(HttpServletRequest request, HttpServletResponse response) {
		sendRedirect(response, request.getContextPath() + "/account/login");
	}
	
	/**
	 * send to specified URL
	 * @param response
	 * @param url
	 */
	public static void sendRedirect(HttpServletResponse response ,String url)  {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
