package com.fajar.livestreaming.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.fajar.livestreaming.proxies.ProxyGateway;

import lombok.extern.slf4j.Slf4j;
@CrossOrigin
@Controller
@RequestMapping("gateway")
@Slf4j
public class ProxiesController {
	
	String host = "https://developmentmode.000webhostapp.com/";

	@Autowired
	private ProxyGateway gateway;
	@RequestMapping(value = { "/{path}/**"}, method = RequestMethod.POST)
	public void post(@PathVariable(name="path") String path, HttpServletRequest httpRequest, 
			HttpServletResponse httpServletResponse) throws IOException  {
		String fullPath = getFullPath(path, httpRequest);
		httpServletResponse.setHeader("Access-Control-Allow-Origin"	, "*");
		gateway.proxyPost(host+fullPath, httpRequest, httpServletResponse);
	}
	
	private String getFullPath(String path, HttpServletRequest httpServletRequest) {
		final String hiddenPath =
				httpServletRequest.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
	    final String bestMatchingPattern =
	    		httpServletRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();

	    String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, hiddenPath);

	    String fullPath;
	    if (null != arguments && !arguments.isEmpty()) {
	    	fullPath = path + '/' + arguments;
	    } else {
	    	fullPath = path;
	    }
	    log.info("PATH: {}", fullPath);
	    return fullPath;
	}
	
	@RequestMapping(value = { "/{path}/**"}, method = RequestMethod.GET)
	public void get(@PathVariable(name="path") String path, HttpServletRequest httpRequest, 
			HttpServletResponse httpServletResponse) throws IOException  {
		String fullPath = getFullPath(path, httpRequest);
		httpServletResponse.setHeader("Access-Control-Allow-Origin"	, "*");
		gateway.proxyGet(host+fullPath, httpRequest, httpServletResponse);
		
	}
	
	@RequestMapping(value = { "/{path}/**"}, method = RequestMethod.OPTIONS)
	public void option(@PathVariable(name="path") String path, HttpServletRequest httpRequest, 
			HttpServletResponse httpServletResponse) throws IOException  {
		
		String fullPath = getFullPath(path, httpRequest);
		
		log.info("_________________ METHOD OPTIONS ________________:{}", fullPath);
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
         
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		httpServletResponse.setHeader("Access-Control-Allow-Origin"	, "*");
		httpServletResponse.setHeader("Access-Control-Allow-Headers", "X-PINGOTHER,Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
         
//		gateway.proxyGet(host+fullPath, httpRequest, httpServletResponse);
		
	}
	
}