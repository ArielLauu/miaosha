package com.ariel.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix{

	public static final int TOKEN_EXPIRE = 3600*24 * 2;
	private MiaoshaUserKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE, "token");
	//对象缓存永久有效
	public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");
}
