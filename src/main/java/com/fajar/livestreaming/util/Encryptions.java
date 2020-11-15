package com.fajar.livestreaming.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.cipher.CryptoCipherFactory.CipherProvider;
import org.apache.commons.crypto.utils.Utils;

public class Encryptions {
	static final SecretKeySpec key = new SecretKeySpec(getUTF8Bytes("1234567890123456"), "AES");
	static final IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes("1234567890123456"));
	static final String sampleInput = "hello world!";
	static final String transform = "AES/CBC/PKCS5Padding";
	static final CryptoCipher encipher = encipher();
	
	public static String encodeBase64(String input) {
		String encoded = Base64.getEncoder().encodeToString(input.getBytes());
		return encoded;  
	}
	
	
	public static String decodeBase64(String encodedBase64) {
		String decoded = new String(Base64.getDecoder().decode(encodedBase64.getBytes()));
		return decoded;
	}

	public static void main2(final String[] args) throws Exception {

		// Creates a CryptoCipher instance with the transformation and properties.
		System.out.println("Cipher:  " + encipher.getClass().getCanonicalName());
		System.out.println("input:  " + sampleInput);

		final byte[] input = getUTF8Bytes(sampleInput);
		final byte[] output = new byte[32];

		// Initializes the cipher with ENCRYPT_MODE, key and iv.
		encipher.init(Cipher.ENCRYPT_MODE, key, iv);
		// Continues a multiple-part encryption/decryption operation for byte array.
		final int updateBytes = encipher.update(input, 0, input.length, output, 0);
		System.out.println(updateBytes);
		// We must call doFinal at the end of encryption/decryption.
		final int finalBytes = encipher.doFinal(input, 0, 0, output, updateBytes);
		System.out.println(finalBytes);
		// Closes the cipher.
		encipher.close();

		System.out.println(Arrays.toString(Arrays.copyOf(output, updateBytes + finalBytes)));
		System.out.println("updated bytes: " + updateBytes);
		System.out.println("final bytes: " + finalBytes);
		System.out.println("input length: " + (updateBytes + finalBytes));
		decrypt(output, updateBytes + finalBytes);
	}

	static final byte[] sample = new byte[] { -49, 93, 117, 64, -46, -117, -62, 74, -55, 124, -49, 121, -110, 43, -77,
			-107 };

	public static void main(String[] args) throws Exception {
		decrypt(sample, 16);
		String encoded = Base64.getEncoder().encodeToString("Hello".getBytes());
		System.out.println(encoded); // Outputs "SGVsbG8="

		String decoded = new String(Base64.getDecoder().decode(encoded.getBytes()));
		System.out.println(decoded); // Outputs "Hello"
	}

	private static CryptoCipher encipher() {
		try {
			CryptoCipher result = Utils.getCipherInstance(transform, encipherProperties());
			return result;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private static Properties encipherProperties() {
		final Properties properties = new Properties();
		properties.setProperty(CryptoCipherFactory.CLASSES_KEY, CipherProvider.OPENSSL.getClassName());
		return properties;
	}

	static void decrypt(final byte[] output, int inputLength) throws Exception {
		final Properties dechipderProperties = new Properties();
		dechipderProperties.setProperty(CryptoCipherFactory.CLASSES_KEY, CipherProvider.JCE.getClassName());
		final CryptoCipher decipher = Utils.getCipherInstance(transform, dechipderProperties);
		System.out.println("Cipher:  " + encipher.getClass().getCanonicalName());

		decipher.init(Cipher.DECRYPT_MODE, key, iv);
		final byte[] decoded = new byte[32];
		decipher.doFinal(output, 0, inputLength, decoded, 0);

		System.out.println("output: " + new String(decoded, StandardCharsets.UTF_8));
	}

	/**
	 * Converts String to UTF8 bytes
	 *
	 * @param input the input string
	 * @return UTF8 bytes
	 */
	private static byte[] getUTF8Bytes(final String input) {
		return input.getBytes(StandardCharsets.UTF_8);
	}
}
