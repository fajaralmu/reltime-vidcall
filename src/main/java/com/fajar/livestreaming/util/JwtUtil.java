package com.fajar.livestreaming.util;

import java.io.Serializable;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.apache.commons.codec.digest.DigestUtils;

import com.fajar.livestreaming.dto.RegisteredRequest;
import com.fajar.livestreaming.dto.SessionData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtUtil {
	
	static ObjectMapper mapper = new ObjectMapper();
	static Decoder decoder = Base64.getUrlDecoder();
	static Encoder encoder = Base64.getUrlEncoder();
	public static String toJson(Serializable serializable) {
		try {
			return mapper.writeValueAsString(serializable);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String generateJWT(SessionData sessionData, RegisteredRequest account) {
		
		byte[] sessionDataEncoded = encoder.encode(toJson(sessionData).getBytes());
		byte[] accountEncoded = encoder.encode(toJson(account).getBytes());
		String headerAndPayload = new String(sessionDataEncoded)+"."+new String(accountEncoded);
		
		String headerAndPayloadHashed = sha256HexMatch(headerAndPayload);
		
		return headerAndPayload+"."+new String(headerAndPayloadHashed);
		
	}
	
	public static RegisteredRequest getRegisteredRequest(String jwt) {
		if(validateJWT(jwt) == false) {
			return null;
		}
		String payload = jwt.split("\\.")[1];
		String payloadDecoded = new String(decoder.decode(payload));
		try {
			return mapper.readValue(payloadDecoded, RegisteredRequest.class);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private static String sha256HexMatch(String raw) {
		return DigestUtils.sha256Hex(raw);
	}
	
	
	
	public static boolean validateJWT(String jwt) {
		try {
			
			String[] splitted = jwt.split("\\.");
			String header = splitted[0];
			String payload = splitted[1];
			String signature = splitted[2];
			
			if(sha256HexMatch(header+"."+payload).equals(signature) == false) {
				System.out.println("signature not match");
				return false;
			}
			byte[] headerDecoded = decoder.decode(header);
			byte[] payloadDecoded = decoder.decode(payload);
			
			System.out.println("header: "+ new String(headerDecoded));
			System.out.println("payload: "+ new String(payloadDecoded));
			return true;
		}catch (Exception e) { 
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		String jwt = generateJWT(new SessionData(), new RegisteredRequest());
		System.out.println(jwt);
		System.out.println(getRegisteredRequest(jwt));
	}
	

}
