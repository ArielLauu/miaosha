package com.ariel.miaosha.controller;

import com.ariel.miaosha.entity.MiaoshaUser;
import com.ariel.miaosha.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

	
    @RequestMapping("/info")
	@ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
        return Result.success(user);
    }
    
}
