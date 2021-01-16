package com.ariel.miaosha.controller;

import com.ariel.miaosha.access.AccessLimit;
import com.ariel.miaosha.domain.MiaoshaOrder;
import com.ariel.miaosha.domain.MiaoshaUser;
import com.ariel.miaosha.domain.OrderInfo;
import com.ariel.miaosha.rabbitmq.MQSender;
import com.ariel.miaosha.rabbitmq.MiaoshaMessage;
import com.ariel.miaosha.redis.AccessKey;
import com.ariel.miaosha.redis.GoodsKey;
import com.ariel.miaosha.redis.MiaoshaKey;
import com.ariel.miaosha.redis.RedisService;
import com.ariel.miaosha.result.CodeMsg;
import com.ariel.miaosha.result.Result;
import com.ariel.miaosha.service.GoodsService;
import com.ariel.miaosha.service.MiaoshaUserService;
import com.ariel.miaosha.service.OrderService;
import com.ariel.miaosha.service.MiaoshaService;
import com.ariel.miaosha.util.MD5Util;
import com.ariel.miaosha.util.UUIDUtil;
import com.ariel.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.MD5;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	OrderService orderService;

	@Autowired
	MiaoshaService miaoshaService;

	@Autowired
	MQSender sender;

	private Map<Long,Boolean> localOverMap=new HashMap<Long, Boolean>();

	/**
	 *系统初始化,商品库存加入缓存
	 **/
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList=goodsService.listGoodsVo();
		if(goodsList==null){
			return;
		}
		for(GoodsVo goods:goodsList){
			redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(),goods.getStockCount());
			localOverMap.put(goods.getId(),false);
		}
	}

//	//秒杀静态化
//	@RequestMapping(value="/do_miaosha",method = RequestMethod.POST)
//	@ResponseBody
//	public Result<Integer> miaosha(Model model, MiaoshaUser user,
//						 @RequestParam("goodsId")long goodsId) {
//		model.addAttribute("user",user);
//		if(user==null){
//			return Result.error(CodeMsg.SESSION_ERROR);
//		}
//		//预减少缓存库存
//		long stock=redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
//		if(stock<0){
//			return Result.error(CodeMsg.MIAO_SHA_OVER);
//		}
//		//判断是否已经秒杀过了
//		MiaoshaOrder order=orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
//		if(order!=null){
//			return Result.error(CodeMsg.REPEATE_MIAOSHA);
//		}
//		//未秒杀过，入队
//		MiaoshaMessage mm=new MiaoshaMessage();
//		mm.setGoodsId(goodsId);
//		mm.setUser(user);
//		sender.sendMiaoshaMessage(mm);
//		//排队中
//		return Result.success(0);
//
//
////		//判断是否已经秒杀过了
////		MiaoshaOrder order=orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
////		if(order!=null){
////			return Result.error(CodeMsg.REPEATE_MIAOSHA);
////		}
////		//减库存，下订单，写入秒杀订单（事务）
////		OrderInfo orderInfo=miaoshaService.miaosha(user,goods);
////		if(orderInfo==null){
////			return Result.error(CodeMsg.MIAO_SHA_OVER);
////		}
////		return Result.success(orderInfo);
//	}

    /**
     * 1000*10 130QPS=>608QPS
     */
    @RequestMapping(value="/{path}/do_miaosha", method=RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model,MiaoshaUser user,
                                   @RequestParam("goodsId")long goodsId,
                                   @PathVariable("path") String path) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check=miaoshaService.checkPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over=localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);//10
        if(stock < 0) {
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);
        return Result.success(0);//排队中
    	/*
    	//判断库存
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
    	int stock = goods.getStockCount();
    	if(stock <= 0) {
    		return Result.error(CodeMsg.MIAO_SHA_OVER);
    	}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
    	}
    	//减库存 下订单 写入秒杀订单
    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        return Result.success(orderInfo);
        */
    }


    /**
     * 客户端轮询接口
     *orderId：秒杀成功
     *-1：秒杀失败
     * 0：排队中
     **/
    @AccessLimit(seconds=5,maxCount=10,needLogin=true)
    @RequestMapping(value="/result",method = RequestMethod.GET )
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user,
                                  @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //查询是否生成订单
        long result=miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds=5,maxCount=5,needLogin=true)
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoshaUser user,HttpServletRequest request,
                                   @RequestParam("goodsId")long goodsId,
                                   @RequestParam(value="verifyCode",defaultValue = "0")int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证码检查
        boolean check=miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path=miaoshaService.createMiaoshaPath(user,goodsId);
        return Result.success(path);
    }

    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, MiaoshaUser user,
                                               @RequestParam("goodsId")long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image=miaoshaService.createVerifyCode(user,goodsId);
        try{
            OutputStream out=response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }

        return null;
    }
}
