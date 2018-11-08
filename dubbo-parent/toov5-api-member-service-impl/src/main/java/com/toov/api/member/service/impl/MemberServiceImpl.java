package com.toov.api.member.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.toov.api.member.service.MemberService;

@Service
public class MemberServiceImpl implements MemberService {

	public String getUser() {
		System.out.println("订单服务调用会员服务");
		return "订单服务调用会员服务成功";
	}
	  
}
