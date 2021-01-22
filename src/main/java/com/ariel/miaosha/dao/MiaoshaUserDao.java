package com.ariel.miaosha.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.ariel.miaosha.entity.MiaoshaUser;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface MiaoshaUserDao {

	@Select("select * from user where id=#{id}")
	public MiaoshaUser getById(@Param("id") long id);

	@Update("update user set password=#{password} where id=#{id}")
    public void update(MiaoshaUser toBeUpdate);
}
