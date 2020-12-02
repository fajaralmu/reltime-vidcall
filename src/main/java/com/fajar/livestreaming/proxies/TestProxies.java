package com.fajar.livestreaming.proxies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.spi.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate; 

import lombok.extern.slf4j.Slf4j;

public class TestProxies {

	static RestTemplate restTemplate = null;
	static void buildRestTemplate() {
		if (null != restTemplate) {
			return;
		}
		restTemplate = new RestTemplate();
	    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
	    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
	    restTemplate.getMessageConverters().add(converter);
	    

	}

	public static void main(String[] args) {
		buildRestTemplate();
		getUser();
	}
	
	private static void getUser() {
		String endpoint = "https://developmentmode.000webhostapp.com/api/accountdashboard/user";
		String token = "12a590ebd4b4d71646613fac5b41d4d47277361ffdf3c038133b894a7fe7b7aca";
		ResponseEntity<String> response = restTemplate.postForEntity(endpoint, null, String.class);
		System.out.println(response.getStatusCodeValue());
		System.out.println(response.getBody());
		
		if (response.getStatusCodeValue() == 302) {
			String html = response.getBody();
			String redirectedUrl = TestString.getRedirectingUrl(html);
			System.out.println("Redirecting to: "+redirectedUrl);
			response = restTemplate.getForEntity(redirectedUrl, String.class);
			System.out.println(response.getStatusCodeValue());
			System.out.println(response.getBody());
		}
	}

	public static void generateRequestId() {
		String endpoint = "https://developmentmode.000webhostapp.com/api/account/requestid";
		ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
		System.out.println(response.getStatusCodeValue());
		System.out.println(response.getBody());
	}
	
	public static <T> HttpEntity<T> httpEntityWithAuthorization(T payload, String token){ 
        Map<String , String> map = new HashMap<>();
        map.put("Authorization", "Bearer "+ token);
        return httpEntity(payload,map);
    }
	
	public static void login() {
		String endpoint = "https://developmentmode.000webhostapp.com/api/account/login";
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> user = new HashMap<>();
		user.put("email", "admin@gmail.com");
		user.put("password", "123");
		payload.put("user", user);
		String requestJson = "{\"user\":{\"email\":\"admin@gmail.com\", \"password\": \"123\"}}";
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(endpoint, httpEntity(requestJson, null), String.class);
			HttpHeaders headers = response.getHeaders();
			System.out.println("API_TOKEN: " +headers.get("api_token"));
			System.out.println(response.getStatusCodeValue());
			System.out.println(response.getBody());
		} catch (HttpServerErrorException errorException) {
			 String responseBody = errorException.getResponseBodyAsString();
			System.out.println(responseBody);
		}
	}
	
	public static <T> HttpEntity<T> httpEntity(T payload, Map<String, String> header) {
        HttpHeaders headers = new HttpHeaders();
        headers.setConnection("close");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.add("Accept-Encoding", "identity");
        headers.add("Accept", "application/json");
        if(null != header){
            for (Map.Entry<String, String> entry:
                    header.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }
//        headers.add("user-agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<T> entity = new HttpEntity<T>( payload,headers);
        return entity;

    }
	
	@Slf4j
	static public class LoggingInterceptor implements ClientHttpRequestInterceptor {

	    

	    @Override
	    public ClientHttpResponse intercept(
	      HttpRequest req, byte[] reqBody, ClientHttpRequestExecution ex) throws IOException {
	        log.debug("Request body: {}", new String(reqBody, StandardCharsets.UTF_8));
	        ClientHttpResponse response = ex.execute(req, reqBody);
	        InputStreamReader isr = new InputStreamReader(
	          response.getBody(), StandardCharsets.UTF_8);
	        String body = new BufferedReader(isr).lines()
	            .collect(Collectors.joining("\n"));
	        log.debug("Response body: {}", body);
	        return response;
	    }
	}
}
