package com.ariel.miaosha.vo;

import com.ariel.miaosha.entity.Goods;

import java.util.Date;

public class GoodsVo extends Goods {

    private double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
    public Integer getStockCount() {

        return stockCount;
    }

    public double getMiaoshaPrice() {
        return miaoshaPrice;
    }

    public void setMiaoshaPrice(double miaoshaPrice) {
        this.miaoshaPrice = miaoshaPrice;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}
