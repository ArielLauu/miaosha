package com.ariel.miaosha.redis;

public class GoodsKey extends BasePrefix {

	public static final int HTML_EXPIRE = 60;
	public GoodsKey(int expireSeconds,String prefix) {
		super(expireSeconds,prefix);
	}

	//页面缓存
	public static GoodsKey getGoodsList = new GoodsKey(HTML_EXPIRE ,"goodsList");
	public static GoodsKey getGoodsDetail = new GoodsKey(HTML_EXPIRE ,"goodsDetail");
	public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0,"goodsStock");
}
