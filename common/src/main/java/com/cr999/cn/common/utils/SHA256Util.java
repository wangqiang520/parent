package com.cr999.cn.common.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 王强
 * SHA256加密
 *
 */
public class SHA256Util {
	
	public static String getSha256(String str) {
		MessageDigest messageDigest;
		String encodeStr="";
		try {
			messageDigest=MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
			encodeStr=byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        return encodeStr;
	}
	
	private static String byte2Hex(byte[] bytes) {
		StringBuffer stringBuffer=new StringBuffer();
		String temp=null;
		int length=bytes.length;
		for(int i=0;i<length;i++) {
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if(temp.length()==1) {
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}
	public static void main(String[] args) {
		String a=getSha256("测试加密1");
		System.out.println(a);
		a=EncryptionUtils.encodeSha256("测试加密");
		System.out.println(a);
	}
}
