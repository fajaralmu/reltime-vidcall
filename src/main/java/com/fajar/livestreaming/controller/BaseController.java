package com.fajar.livestreaming.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.service.UserSessionService;
import com.fajar.livestreaming.util.DateUtil;

@Controller
public class BaseController {

	private static final String MODEL_ATTR_TITLE = "title";

	private static final String MODEL_ATTR_PAGE_URL = "pageUrl";

	protected String basePage = "BASE_PAGE";
	@Autowired
	protected UserSessionService userSessionService;

	@ModelAttribute("registeredRequest")
	public RegisteredRequest getPublicRequestId(HttpServletRequest request) {
		try {
			return userSessionService.getRegisteredRequest(request);
		} catch (Exception e) {
			return null;
		}
	}

	@ModelAttribute("contextPath")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}
	
	@ModelAttribute("year")
	public int getCurrentYear(HttpServletRequest request) {
		return DateUtil.getCalendarItem(new Date(), Calendar.YEAR);
	}
	
	@ModelAttribute("ipAndPort")
	public String getIpAddressAndPort(HttpServletRequest request) {
		
		String remoteAddress = request.getRemoteAddr();
		int port = request.getServerPort();
		
		return remoteAddress +":"+ port;
	}
	 
	protected static void setTitle(Model model, String title) {
		model.addAttribute(MODEL_ATTR_TITLE, title);
	}
	protected static void setPageUrl(Model model, String pageUrl) {
		model.addAttribute(MODEL_ATTR_PAGE_URL, pageUrl);
	}

	/**
	 * ====================================================== Statics
	 * ======================================================
	 * 
	 */

	public static Cookie getCookie(String name, Cookie[] cookies) {
		try {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * send to login page URL
	 * 
	 * @param request
	 * @param response
	 */
	public static void sendRedirectLogin(HttpServletRequest request, HttpServletResponse response) {
		sendRedirect(response, request.getContextPath() + "/account/login");
	}

	/**
	 * send to specified URL
	 * 
	 * @param response
	 * @param url
	 */
	public static void sendRedirect(HttpServletResponse response, String url) {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
