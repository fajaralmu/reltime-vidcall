package com.fajar.livestreaming.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.service.UserSessionService;

@Controller
public class BaseController {

	private static final String SESSION_ATTR_TITLE = "page-title";

	private static final String SESSION_ATTR_PAGE_URL = "page-url";

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

	@ModelAttribute("title")
	public String getTitle(HttpServletRequest request) {
		try {
			return request.getSession().getAttribute(SESSION_ATTR_TITLE).toString();
		} catch (Exception e) {
			 
			return "Realtime VidCall";
		}
	}
	
	@ModelAttribute("pageUrl")
	public String getPageUrl(HttpServletRequest request) {
		try {
			return request.getSession().getAttribute(SESSION_ATTR_PAGE_URL).toString();
		} catch (Exception e) {
			 
			return "Realtime VidCall";
		}
	}
	
	protected static void setSessionAttribute(HttpServletRequest httpServletRequest, String attributeName, Object value) {
		httpServletRequest.getSession().setAttribute(attributeName, value);
	}
	
	protected static void setTitle(HttpServletRequest httpServletRequest, String title) {
		setSessionAttribute(httpServletRequest, SESSION_ATTR_TITLE, title);
	}
	protected static void setPageUrl(HttpServletRequest httpServletRequest, String title) {
		setSessionAttribute(httpServletRequest, SESSION_ATTR_PAGE_URL, title);
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
