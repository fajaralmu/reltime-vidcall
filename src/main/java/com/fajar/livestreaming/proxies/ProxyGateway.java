package com.fajar.livestreaming.proxies;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProxyGateway {
	private RestTemplate restTemplate = null;

	@PostConstruct
	public void init() {
		buildRestTemplate();
	} 

	public void proxyPost(String path, HttpServletRequest httpRequest, HttpServletResponse httpServletResponse)
			throws IOException {
		System.out.println("Post to: "+path);
		StringBuilder recorder = new StringBuilder();
		
		Map<String, String> headers = extractHeader(httpRequest);
		String payload = getPayload(httpRequest);
		
		recorder.append("Endpoint: "+path+"\n");
		recorder.append("Method: "+httpRequest.getMethod()+"\n");
		recorder.append("========= REQUEST ==========\n");
		recorder.append(printMap(headers));
		recorder.append("========= payload ==========\n");
		recorder.append(payload);
		Object responsePayload = null;
		int statusCode = 200;
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(path, httpEntity(payload, headers),
					String.class);

			if (302 == response.getStatusCodeValue()) {
				redirect(response, httpRequest, httpServletResponse);
				return;
			}

			HttpHeaders responseHeaders = response.getHeaders();
			mapResponseHeader(responseHeaders, httpServletResponse);
			responsePayload = response.getBody();
			
		} catch (HttpClientErrorException e) {
			statusCode = (e.getRawStatusCode());
			mapResponseHeader(e.getResponseHeaders(), httpServletResponse);
			responsePayload = e.getResponseBodyAsString();
			
		} catch (HttpServerErrorException e) {
			statusCode = (e.getRawStatusCode());
			mapResponseHeader(e.getResponseHeaders(), httpServletResponse);
			responsePayload = e.getResponseBodyAsString();
			
		} catch (Exception e) {
			statusCode = (500);
			httpServletResponse.setContentType("application/json");
			responsePayload = "{\"message\":\""+e.getMessage()+"\"}";
			
		} finally {
			httpServletResponse.setStatus(statusCode);
			httpServletResponse.getWriter().write(String.valueOf(responsePayload));
			Collection<String> headerNames = httpServletResponse.getHeaderNames();
			Map headerMap = new HashMap<>();
			headerNames.forEach(name->{
				headerMap.put(name, httpServletResponse.getHeader(name));
			});
			
			recorder.append("========= RESPONSE ==========\n");
			recorder.append("Status: "+statusCode+"\n");
			recorder.append(printMap(headerMap));
			recorder.append("========= palyload ==========\n");
			recorder.append(String.valueOf(responsePayload)+"\n");
			writeRequestRecord(path, recorder.toString());
		}
	}
	static void writeRequestRecord(String path, String data) {
		if (path.startsWith("https://")) { return; }
		path = path.replace('/', '_').replace(':', '-');
		try {
			File file = new File("D:\\request_record\\"+new Date().getTime()+ path+".txt");
			FileUtils.writeStringToFile(file, data);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	private void redirect(ResponseEntity<String> response, HttpServletRequest httpRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		String html = response.getBody();
		String redirectedUrl = TestString.getRedirectingUrl(html);
		log.info("Redirecting to: " + redirectedUrl);
		proxyGet(redirectedUrl, httpRequest, httpServletResponse);

	}

	static Map mapResponseHeader(HttpHeaders responseHeaders, HttpServletResponse httpServletResponse) {
		Map responseHeaderMap = new HashMap<>();
		responseHeaders.forEach((name, values) -> {
			String[] valueAsArray = values.toArray(new String[values.size()]);
//			responseHeaderMap.put(name, String.join(",", valueAsArray));
			httpServletResponse.setHeader(name, String.join(",", valueAsArray));
		});
		return responseHeaderMap;
	}

	public void proxyGet(String path, HttpServletRequest httpRequest, HttpServletResponse httpServletResponse)
			throws IOException {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(path, String.class);
			HttpHeaders responseHeaders = response.getHeaders();
			mapResponseHeader(responseHeaders, httpServletResponse);

			httpServletResponse.setStatus(response.getStatusCodeValue());
			httpServletResponse.getWriter().write(response.getBody());

		} catch (HttpClientErrorException e) {
			httpServletResponse.setStatus(e.getRawStatusCode());
			mapResponseHeader(e.getResponseHeaders(), httpServletResponse);
			httpServletResponse.getWriter().write(e.getResponseBodyAsString());
		} catch (HttpServerErrorException e) {
			httpServletResponse.setStatus(e.getRawStatusCode());
			mapResponseHeader(e.getResponseHeaders(), httpServletResponse);
			httpServletResponse.getWriter().write(e.getResponseBodyAsString());
		}
	}

	void buildRestTemplate() {
		if (null != restTemplate) {
			return;
		}
		restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
		restTemplate.getMessageConverters().add(converter);

	}

	private String getPayload(HttpServletRequest httpRequest) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = httpRequest.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	private Map<String, String> extractHeader(HttpServletRequest httpRequest) {
		log.info("==========HEADERS {}===========", httpRequest.getRequestURI());
		Map<String, String> headers = new HashMap<>();
		Enumeration<String> headerNames = httpRequest.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String name = headerNames.nextElement();
				String value = httpRequest.getHeader(name);
				if (name.toLowerCase().equals("accept-encoding")) {
					log.info("!!! header is skipped: {}", name);
					continue;
				}
				headers.put(name, value);
				log.info("{}:{}", name, value);
			}
		}
		log.info("===============");
		return headers;
	}
	
	private String printMap(Map map) {
		StringBuilder sb = new StringBuilder();
		for (Object key : map.keySet()) {
			sb.append(key+" : "+map.get(key)+"\n");
		}
		return sb.toString();
	}

	public static <T> HttpEntity<T> httpEntity(T payload, Map<String, String> header) {
		HttpHeaders headers = new HttpHeaders();
		headers.setConnection("close");
		if (null != header) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				headers.add(entry.getKey(), entry.getValue());
			}
		}
//        headers.add("user-agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		HttpEntity<T> entity = new HttpEntity<T>(payload, headers);
		return entity;

	}
}
