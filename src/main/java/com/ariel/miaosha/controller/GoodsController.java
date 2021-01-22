package com.ariel.miaosha.controller;

import com.ariel.miaosha.redis.GoodsKey;
import com.ariel.miaosha.result.Result;
import com.ariel.miaosha.service.GoodsService;
import com.ariel.miaosha.vo.GoodsDetailVo;
import com.ariel.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ariel.miaosha.entity.MiaoshaUser;
import com.ariel.miaosha.redis.RedisService;
import com.ariel.miaosha.service.MiaoshaUserService;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;

	@Autowired
	ApplicationContext applicationContext;
	
    @RequestMapping(value="/to_list",produces = "text/html")
	@ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response,Model model, MiaoshaUser user) {
    	model.addAttribute("user", user);
		//取缓存
		String html=redisService.get(GoodsKey.getGoodsList,"",String.class);
		if(!StringUtils.isEmpty(html)){
			return html;
		}
    	//查询商品
        List<GoodsVo> goodsList=goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsList);

		WebContext ctx = new WebContext(request, response,
				request.getServletContext(),
				request.getLocale(), model.asMap());
		//手动渲染
		html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
		if (!Strings.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsList, "", html);
		}
		return html;
    }

	@RequestMapping(value="/detail2/{goodsId}",produces = "text/html")
	@ResponseBody
	public String detail(HttpServletRequest request, HttpServletResponse response,Model model,MiaoshaUser user,
		@PathVariable("goodsId")long goodsId) {
		model.addAttribute("user", user);
		//取缓存
		String html=redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
		if(!StringUtils.isEmpty(html)){
			return html;
		}
		//手动渲染
		GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods",goods);

		long startAt=goods.getStartDate().getTime();
		long endAt=goods.getEndDate().getTime();
		long now=System.currentTimeMillis();

		int miaoshaStatus=0;
		int remainSeconds=0;
		if(now<startAt){
			//秒杀还没开始，倒计时
			miaoshaStatus=0;
			remainSeconds = (int)((startAt - now )/1000);
		}else if(now>endAt){
			//秒杀已经结束
			miaoshaStatus=2;
			remainSeconds=-1;
		}else{
			//秒杀进行中
			miaoshaStatus=1;
			remainSeconds=0;
		}
		model.addAttribute("miaoshaStatus",miaoshaStatus);
		model.addAttribute("remainSeconds",remainSeconds);
//		return "goods_detail";
		WebContext context=new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
		html=thymeleafViewResolver.getTemplateEngine().process("goods_detail",context);
		if(!StringUtils.isEmpty(html)){
			redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
		}
		return html;
	}

	//详情页，页面静态化
	@RequestMapping(value="/detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> detail2(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
										@PathVariable("goodsId")long goodsId) {
		GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);

		long startAt=goods.getStartDate().getTime();
		long endAt=goods.getEndDate().getTime();
		long now=System.currentTimeMillis();

		int miaoshaStatus=0;
		int remainSeconds=0;
		if(now<startAt){
			//秒杀还没开始，倒计时
			miaoshaStatus=0;
			remainSeconds = (int)((startAt - now )/1000);
		}else if(now>endAt){
			//秒杀已经结束
			miaoshaStatus=2;
			remainSeconds=-1;
		}else{
			//秒杀进行中
			miaoshaStatus=1;
			remainSeconds=0;
		}
		GoodsDetailVo vo=new GoodsDetailVo();
		vo.setGoods(goods);
		vo.setMiaoshaStatus(miaoshaStatus);
		vo.setRemainSeconds(remainSeconds);
		vo.setUser(user);
		return Result.success(vo);
	}
    
}

