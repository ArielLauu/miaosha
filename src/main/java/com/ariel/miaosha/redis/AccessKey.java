package com.ariel.miaosha.redis;

public class AccessKey extends BasePrefix {

	public static final int HTML_EXPIRE = 5;
	public AccessKey(int expireSeconds, String prefix) {
		super(expireSeconds,prefix);
	}

	//访问控制
	public static AccessKey withExpire(int expireSeconds){
		return new AccessKey(expireSeconds,"acess");
	}

}
