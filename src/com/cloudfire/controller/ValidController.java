package com.cloudfire.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import com.cloudfire.dao.ExpireDao;
import com.cloudfire.dao.impl.ExpireDaoImpl;
import com.cloudfire.entity.Expire;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

@Controller
public class ValidController {
	/**
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/validExpireTime.do",method=RequestMethod.GET)
	public void validExpireTime(HttpServletRequest request,HttpServletResponse response){
		String innerIp = request.getParameter("innerIp");
		String outerIp = request.getParameter("outerIp");
		String registerCode = request.getParameter("registerCode");
		
		String flag = "" ;  //要返回的值
		
		if (!(Utils.isNullStr(innerIp) && Utils.isNullStr(outerIp) && Utils.isNullStr(registerCode))){ 
			flag = "1";
		} else {
			ExpireDao ed = new ExpireDaoImpl();
			Expire exp = ed.getExpireByRegisterCode(registerCode); //根据注册码获取对象
			
			if(exp == null){ //若注册码不存在数据库，直接返回
				flag = "1";
			} else { //该注册码存在于数据库
				int expirestate = exp.getState(); //获取过期状态
				switch(expirestate){
				case 0:  //过期前一个月前
				case 1:  //过期前一个月之内
					if(exp.getServers() == 0) { //只有一个用户
						if (innerIp.equals(exp.getInnerIp())&&outerIp.equals(exp.getOuterIp())) { //注册码一致且内外网Ip一致
							flag = "3";
						} else { //注册码一致且内外网Ip不一致
							flag = "2";
						}
						ed.updateExpire(innerIp, outerIp, GetTime.ConvertTimeByLong(), registerCode);
//						ed.updateValidTime(GetTime.ConvertTimeByLong(), registerCode);
					} else { //多个用户
						if (innerIp.equals(exp.getInnerIp())&&outerIp.equals(exp.getOuterIp())) { //注册码一致且内外网Ip一致
							flag = "5";
							ed.updateExpire(innerIp, outerIp, GetTime.ConvertTimeByLong(), registerCode);
						} else { //注册码一致且内外网Ip不一致
							if (innerIp.equals(exp.getCur_innerIp()) && outerIp.equals(exp.getCur_outerIp())) { //与最近一次验证的内外网Ip一致
								flag = "2";
								ed.updateExpire(innerIp, outerIp, GetTime.ConvertTimeByLong(), registerCode);
							} else {
								flag = "4";
							}
						}
					}
					break;
				case 2: //过期
					flag = "1";
					break;
				}
				
			}
		}
		
		try {
			response.getWriter().write(flag);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
