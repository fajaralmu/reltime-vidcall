package com.fajar.livestreaming.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.fajar.livestreaming.dto.KeyValue;
import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.service.UserSessionService;
import com.fajar.livestreaming.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BaseController {

	private static final String MODEL_ATTR_TITLE = "title";
	private static final String MODEL_ATTR_PAGE_URL = "pageUrl";
	
	protected String basePage = "BASE_PAGE";
	
	@Autowired
	protected UserSessionService userSessionService;
	
	@Value("${app.header.label}")
	private String applicationHeaderLabel;
	@Value("${app.footer.label}")
	private String applicationFooterLabel;
	
	@ModelAttribute("applicationHeaderLabel")
	public String applicationHeaderLabel(HttpServletRequest request) {
		
		return applicationHeaderLabel;
	}
	@ModelAttribute("applicationFooterLabel")
	public String applicationFooterLabel(HttpServletRequest request) {
		
		return applicationFooterLabel;
	}
	
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

	@ModelAttribute("inActiveCall")
	public boolean inActiveCall(HttpServletRequest request) {
		RegisteredRequest session = getPublicRequestId(request);
		if (session != null) {
			return userSessionService.isInActiveCall(session.getRequestId());
		}
		return false;
	}

	@ModelAttribute("year")
	public int getCurrentYear(HttpServletRequest request) {
		return DateUtil.getCalendarItem(new Date(), Calendar.YEAR);
	}

	@ModelAttribute("ipAndPort")
	public String getIpAddressAndPort(HttpServletRequest request) {

		String remoteAddress = request.getRemoteAddr();
		int port = request.getServerPort();

		return remoteAddress + ":" + port;
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
	
	private static void addResourcePaths(ModelAndView modelAndView, String resourceName, String... paths) {
		List<KeyValue<String, String>> resoucePaths = new ArrayList<>();
		for (int i = 0; i < paths.length; i++) {
			KeyValue<String, String> keyValue = new KeyValue<String, String>();
			keyValue.setValue(paths[i]);

			resoucePaths.add(keyValue);
			log.info("{}. Add {} to {} , value: {}", i, resourceName, modelAndView.getViewName(), paths[i]);
		}
		setModelAttribute(modelAndView, resourceName, resoucePaths);
	}

	private static void setModelAttribute(ModelAndView modelAndView, String attrName, Object attrValue) {
		if (null == attrValue) {
			return;
		}
		modelAndView.getModel().put(attrName, attrValue);
	}

	public static void addStylePaths(ModelAndView modelAndView, String... paths) {
		if (null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalStylePaths", paths);
	}

	
	public static void addJavaScriptResourcePaths(ModelAndView modelAndView, String... paths) {
		if (null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalScriptPaths", paths);
	}

	public static void addTitle(ModelAndView modelAndView, String title) {
		if (null == title || title.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "title", title);
	}

	public static void addPageUrl(ModelAndView modelAndView, String pageUrl) {
		if (null == pageUrl || pageUrl.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "pageUrl", pageUrl);
	}
}
