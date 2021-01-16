package com.ariel.miaosha.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ValidatorUtil {

	public static final Pattern mobile_Pattern=Pattern.compile("1\\d{10}");

	public static boolean isMobile(String str){
		if(StringUtils.isEmpty(str)){
			return false;
		}
		Matcher matcher=mobile_Pattern.matcher(str);
		return matcher.matches();
	}
	
//	public static void main(String[] args) {
//			System.out.println(isMobile("18912341234"));
//			System.out.println(isMobile("1891234123"));
//	}
}
