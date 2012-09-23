package com.axen.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.text.TextUtils;

public class Utils {
	
	private static final int COMPARE_NUM = 7;
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static String getEncryptString(String plainText) {
		String encrypt = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (md != null) {
			md.update(plainText.getBytes());
		}
		encrypt = new String(md.digest());
		return encrypt;
	}
	
	public static boolean phoneNumberEqual(String no1, String no2) {
		int len1 = 0;
		int len2 = 0;
		String shorter;
		String longer;
		if(no1 == null && no2 == null) {
			return true;
		}
		
		if(no1 != null) {
			len1 = no1.length();
		}
		
		if(no2 != null) {
			len2 = no2.length();
		}
		
		if(len1 < len2) {
			shorter = no1;
			longer = no2;
		}else {
			shorter = no2;
			longer = no1;
		}
		
		if(shorter.length() > COMPARE_NUM) {
			shorter = shorter.substring(shorter.length() - COMPARE_NUM);
		}else {
			/*
			 * 不够7个字符，则不算号码
			 */
			return false;
		}
		
		return longer.endsWith(shorter);
	}
	/**
	 * 把含有true或false的字条转换成boolean型
	 * @param s
	 * @return 只有字符里含有true的字符才转换为true.忽略大小写。
	 */
	public static boolean parseBooleanString(String s) {
		if(TextUtils.isEmpty(s)) {
			return false;
		}else if(s.toLowerCase().contains("true")) {
			return true;
		}
		return false;
	}
	
	public static int parseIntString(String s) {
		int ret = 0;
		if(!TextUtils.isEmpty(s)) {
			try {
				ret = Integer.parseInt(s);
			}catch(NumberFormatException e) {
				
			}
		}
		return ret;
	}
}
