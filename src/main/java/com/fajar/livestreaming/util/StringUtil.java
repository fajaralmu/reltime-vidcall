package com.fajar.livestreaming.util;

import java.util.Random;

public class StringUtil {

	static final Random rand = new Random();
	public static final char[] CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' , '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
 
	public static final String[] GREEK_NUMBER = new String[] {
			"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"
	};
	
	public static final int CHARS_LENGTH = CHARS.length;
	
	public static String generateRandomChar(int length) {
		StringBuilder randomString = new StringBuilder();
		
		if (length < 1) {
			length = 1;
		}
		for (int i = 0; i < length; i++) {

			Integer n = rand.nextInt(CHARS_LENGTH);
			randomString.append(CHARS[n]);
		}
		return randomString.toString();
	}
	
	public static String generateRandomNumber(int length) {

		String random = "";
		if (length < 1) {
			length = 1;
		}
		
		for (int i = 0; i < length; i++) {

			Integer n = rand.nextInt(9);
			random += n;
		}
		return random;
	}

	public static void main(String[] xxx) {

		 System.out.println(extractCamelCase("fajarAmKkkk"));
	}

	public static String addZeroBefore(Integer number) {
		return number < 10 ? "0" + number : number.toString();
	}

	public static String buildString(String... strings) {
		
		StringBuilder stringBuilder = new StringBuilder();

		for (String string : strings) {
			stringBuilder.append(" ").append(string);
		}

		return stringBuilder.toString();
	}
	
	public static String buildTableColumnDoubleQuoted(String tableName, String columnName) {
		return buildString(doubleQuoteMysql(tableName), ".", doubleQuoteMysql(columnName));
	}

	public static String doubleQuoteMysql(String str) {
		return " `".concat(str).concat("` ");
	}

	public static Object beautifyNominal(Long valueOf) {
		// TODO Auto-generated method stub
		return null;
	}
	
	static boolean isUpperCase(char _char) {
		StringBuilder str = new StringBuilder();
		str.append(_char);
		return str.toString().equals(str.toString().toUpperCase());
	}
	
	public static String extractCamelCase(String camelCased) {
		
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < camelCased.length(); i++) {
			char _char = camelCased.charAt(i);
			if(isUpperCase(_char)) {
				result.append(" ");
			}
			result.append(_char);
		}
		
		return result.toString();
	}

}
