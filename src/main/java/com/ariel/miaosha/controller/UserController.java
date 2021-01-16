package com.ariel.miaosha.controller;

import com.ariel.miaosha.domain.MiaoshaUser;
import com.ariel.miaosha.redis.RedisService;
import com.ariel.miaosha.result.Result;
import com.ariel.miaosha.service.GoodsService;
import com.ariel.miaosha.service.MiaoshaUserService;
import com.ariel.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

	
    @RequestMapping("/info")
	@ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
        return Result.success(user);
    }
    
}
