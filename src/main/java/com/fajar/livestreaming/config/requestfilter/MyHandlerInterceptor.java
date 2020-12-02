package com.fajar.livestreaming.config.requestfilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * this class is registered in the xml configuration
 * 
 * @author Republic Of Gamers
 *
 */
@Slf4j
public class MyHandlerInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private InterceptorProcessor interceptorProcessor;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		log.info("[preHandle][" + request + "]" + "[" + request.getMethod() + "]");
		
		if (request.getMethod().toLowerCase().equals("options")) {
			return true;
		}
		
		HandlerMethod handlerMethod = interceptorProcessor.getHandlerMethod(request);

		if (handlerMethod != null && interceptorProcessor.isApi(handlerMethod)) {
			return interceptorProcessor.interceptApiRequest(request, response, handlerMethod);
		} else if (handlerMethod != null) {
			return interceptorProcessor.interceptWebPageRequest(request, response, handlerMethod);
		}

		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		if (handler instanceof HandlerMethod) {
			interceptorProcessor.postHandle(request, response, (HandlerMethod) handler, modelAndView);

		}

		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		super.afterCompletion(request, response, handler, ex);
	}

}
