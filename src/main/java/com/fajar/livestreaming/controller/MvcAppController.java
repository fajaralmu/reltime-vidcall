package com.fajar.livestreaming.controller;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.livestreaming.annotation.CustomRequestInfo;
import com.fajar.livestreaming.dto.AdminQuickLink;
import com.fajar.livestreaming.runtime.FlatFileAccessorv2;

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
	 

	@Value("${app.admin.pass}")
	private String adminPass;
	@Autowired
	private FlatFileAccessorv2 fileAccessorv2;
	
	private List<AdminQuickLink> adminQuickLinks;
	
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

	
	@RequestMapping(value = { "/admin/{pass}"})
	@CustomRequestInfo(pageUrl = "pages/admin/admin-data", title="ADMIN DATA")
	public String adminData(@PathVariable(name="pass") String path, Model model,
			HttpServletRequest request, HttpServletResponse response)  { 
		
		if(adminPass.equals(path) == false) {
			throw new IllegalAccessError("NOT ALLOWED");
		}
		model.addAttribute("runtimeData", fileAccessorv2.getRuntimePath());
		List<AdminQuickLink> quickLinks = getAdminQuickLinks();
		model.addAttribute("quickLinks", quickLinks);
		return basePage;
	}

	
	
	private List<AdminQuickLink> getAdminQuickLinks() {
		if(null != adminQuickLinks) {
			return adminQuickLinks;
		}
		
		Class<RestUtilityController> controller = RestUtilityController.class;
		List<AdminQuickLink> list = new ArrayList<AdminQuickLink>();
		Method[] methods = controller.getMethods();
		
		for (Method method : methods) {
			try {
				AdminQuickLink adminQuickLink = generateQuickLink(method);
				if(null != adminQuickLink) { list.add(adminQuickLink); }
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		adminQuickLinks = list;
		return list ;
	}

	/**
	 * currently supports one path variable
	 * @param method
	 * @return
	 */
	private AdminQuickLink generateQuickLink(Method method) {
		PostMapping postMapping = method.getAnnotation(PostMapping.class);
		if(null == postMapping) {
			return null;
		}

		boolean hasVariable = false;
		String link = postMapping.value()[0];
		StringBuilder pathVariableName = new StringBuilder();
		
	 
		Parameter[] methodParams = method.getParameters();
		for (Parameter parameter : methodParams) {
			PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
			if(pathVariable != null) {
				String name = pathVariable.name().equals("") ? parameter.getName() : pathVariable.name();
				pathVariableName.append(name+",");
				hasVariable = true;
				 
			}
		}
		 
		
		AdminQuickLink adminQuickLink = new AdminQuickLink();
		adminQuickLink.setLabel(method.getName());
		adminQuickLink.setId(com.fajar.livestreaming.util.StringUtil.generateRandomChar(4));
		adminQuickLink.setLink(link);
		adminQuickLink.setHasPathVariable(hasVariable);
		adminQuickLink.setPathVariableName(pathVariableName.toString());
		
		return adminQuickLink;
	}
	 

}
