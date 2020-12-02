package com.fajar.livestreaming.proxies;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		Map<String, String> headers = extractHeader(httpRequest);
		String payload = getPayload(httpRequest);
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(path, httpEntity(payload, headers),
					String.class);

			if (302 == response.getStatusCodeValue()) {
				redirect(response, httpRequest, httpServletResponse);
				return;
			}

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

	private void redirect(ResponseEntity<String> response, HttpServletRequest httpRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		String html = response.getBody();
		String redirectedUrl = TestString.getRedirectingUrl(html);
		log.info("Redirecting to: " + redirectedUrl);
		proxyGet(redirectedUrl, httpRequest, httpServletResponse);

	}

	static void mapResponseHeader(HttpHeaders responseHeaders, HttpServletResponse httpServletResponse) {
		responseHeaders.forEach((name, values) -> {
			String[] valueAsArray = values.toArray(new String[values.size()]);
//			responseHeaderMap.put(name, String.join(",", valueAsArray));
			httpServletResponse.setHeader(name, String.join(",", valueAsArray));
		});
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

	public static <T> HttpEntity<T> httpEntity(T payload, Map<String, String> header) {
		HttpHeaders headers = new HttpHeaders();
		headers.setConnection("close");
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
////        headers.add("Accept-Encoding", "identity");
//        headers.add("Accept", "application/json");
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
