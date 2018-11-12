package com.war4.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class MD5Util {
	private static char hexs[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
		'b', 'c', 'd', 'e', 'f' };
	
	public static String encode(String source) {
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");

			byte[] sbs = source.getBytes("UTF8");
			digester.update(sbs);
			byte[] rbs = digester.digest();
			int j = rbs.length;
			char result[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = rbs[i];
				result[k++] = hexs[b >>> 4 & 0xf];
				result[k++] = hexs[b & 0xf];
			}
			return new String(result);
		} catch (Exception e) {
			return null;
		}
	}
	public static void main(String[] rags) throws UnsupportedEncodingException {
		System.out.println(MD5Util.encode("p@ssw0rd.com$#@!"));
		
	}
}
