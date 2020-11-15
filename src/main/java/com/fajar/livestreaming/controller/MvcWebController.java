package com.fajar.livestreaming.controller;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Controller
@RequestMapping("web")
@Slf4j
public class MvcWebController extends BaseController {

	public MvcWebController() {
		log.info("-----------------MvcUtilController------------------");
	}
	 

	@GetMapping(value = "notfound")
	public String halamanNotFound(Model model) throws IOException {
		model.addAttribute("pesan", "Halaman tidak ditemukan");
		return "error/notfound";
	}

	@ExceptionHandler({ RuntimeException.class })
	public String databaseError() {
		// Nothing to do. Returns the logical view name of an error page, passed
		// to the view-resolver(s) in usual way.
		// Note that the exception is NOT available to this view (it is not added
		// to the model) but see "Extending ExceptionHandlerExceptionResolver"
		// below.
		return "error/notfound";
	}

	@RequestMapping(value = "app-error", method = RequestMethod.GET)
	public ModelAndView renderErrorPage(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		ModelAndView errorPage = new ModelAndView("error/errorPage");

		int httpErrorCode = getErrorCode(httpRequest);

		if (200 == httpErrorCode) {
			httpResponse.sendRedirect(httpRequest.getContextPath()+"/index");
			return null;
		}

		errorPage.addObject("errorCode", httpErrorCode);
		errorPage.addObject("errorMessage", getAttribute(httpRequest, "javax.servlet.error.exception"));
		printHttpRequestAttrs(httpRequest);
		return errorPage;
	}

	private void printHttpRequestAttrs(HttpServletRequest httpRequest) {
		Enumeration<String> attrNames = httpRequest.getAttributeNames();
		log.debug("========= error request http attrs ========");
		int number = 1;
		while (attrNames.hasMoreElements()) {
			String attrName = attrNames.nextElement();
			Object attributeValue = httpRequest.getAttribute(attrName);
			log.debug(number + ". " + attrName + " : " + attributeValue + " || TYPE: "
					+ (attributeValue == null ? "" : attributeValue.getClass()));
			number++;
		}
		log.debug("===== ** end ** ====");
	}

	private int getErrorCode(HttpServletRequest httpRequest) {
		if (null == httpRequest.getAttribute("javax.servlet.error.status_code")) {
			return 200;
		}
		try {
			Integer status_code = (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
			log.debug("status_code:{}", status_code);
			return status_code;
		} catch (Exception e) {

			return 500;
		}
	}

	private Object getAttribute(HttpServletRequest httpServletRequest, String name) {
		return httpServletRequest.getAttribute(name);
	}

	@GetMapping(value = "noaccess")
	public String halamanNotAccessable(Model model) throws IOException {
		model.addAttribute("pesan", "Halaman tidak dapat diakses");
		return "error/notfound";
	}

//	/**
//	 * Realtime
//	 */
//	@GetMapping(value = "test-chatv1")
//	public String testChat(Model model) {
//		return "websocket/chat";
//	}
//
//	@GetMapping(value = "test-chatv2")
//	public String testChat2(Model model) {
//		return "websocket/chat2";
//	}

}
