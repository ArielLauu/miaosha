package com.ariel.miaosha.controller;

import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.ariel.miaosha.domain.MiaoshaUser;
import com.ariel.miaosha.domain.OrderInfo;
import com.ariel.miaosha.redis.RedisService;
import com.ariel.miaosha.result.CodeMsg;
import com.ariel.miaosha.result.Result;
import com.ariel.miaosha.service.GoodsService;
import com.ariel.miaosha.service.MiaoshaUserService;
import com.ariel.miaosha.service.OrderService;
import com.ariel.miaosha.vo.GoodsDetailVo;
import com.ariel.miaosha.vo.GoodsVo;
import com.ariel.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	MiaoshaUserService userService;

	@Autowired
	RedisService redisService;

	@Autowired
	OrderService orderService;

	@Autowired
	GoodsService goodsService;

	@RequestMapping("/detail")
	@ResponseBody
	public Result<OrderDetailVo> info(Model model,MiaoshaUser user,
									  @RequestParam("orderId") long orderId) {
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		OrderInfo order = orderService.getOrderById(orderId);
		if(order == null) {
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}
		long goodsId = order.getGoodsId();
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		OrderDetailVo vo = new OrderDetailVo();
		vo.setOrder(order);
		vo.setGoods(goods);
		return Result.success(vo);
	}

}

