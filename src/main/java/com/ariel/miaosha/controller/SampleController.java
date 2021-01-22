package com.ariel.miaosha.controller;

import com.ariel.miaosha.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ariel.miaosha.entity.User;
import com.ariel.miaosha.redis.RedisService;
import com.ariel.miaosha.redis.UserKey;
import com.ariel.miaosha.result.CodeMsg;
import com.ariel.miaosha.result.Result;
import com.ariel.miaosha.service.UserService;

@Controller
@RequestMapping("/demo")
public class SampleController {

	@Autowired
	UserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
    MQSender mqSender;

//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq() {
//        mqSender.send("JY LOVE YJ");
//        return Result.success("Hello，world");
//    }
//
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> topic() {
//        mqSender.sendTopic("JY LOVE YJ");
//        return Result.success("Hello，world");
//    }
//
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public Result<String> fanout() {
//        mqSender.sendFanout("JY LOVE YJ");
//        return Result.success("Hello，world");
//    }
//
//    @RequestMapping("/mq/headers")
//    @ResponseBody
//    public Result<String> headers() {
//        mqSender.sendHeader("JY LOVE YJ");
//        return Result.success("Hello，world");
//    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> home() {
        return Result.success("Hello，world");
    }
    
    @RequestMapping("/error")
    @ResponseBody
    public Result<String> error() {
        return Result.error(CodeMsg.SESSION_ERROR);
    }
    
    @RequestMapping("/hello/themaleaf")
    public String themaleaf(Model model) {
        model.addAttribute("name", "Joshua");
        return "hello";
    }
    
    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
    	User user = userService.getById(1);
        return Result.success(user);
    }
    
    
    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {
    	userService.tx();
        return Result.success(true);
    }
    
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
    	User  user  = redisService.get(UserKey.getById, ""+1, User.class);
        return Result.success(user);
    }
    
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
    	User user  = new User();
    	user.setId(1);
    	user.setName("2222");
    	redisService.set(UserKey.getById, ""+1, user);//UserKey:id1
        return Result.success(true);
    }
    
    
}
