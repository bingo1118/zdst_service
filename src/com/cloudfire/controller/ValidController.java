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
		
		String flag = "" ;  //Ҫ���ص�ֵ
		
		if (!(Utils.isNullStr(innerIp) && Utils.isNullStr(outerIp) && Utils.isNullStr(registerCode))){ 
			flag = "1";
		} else {
			ExpireDao ed = new ExpireDaoImpl();
			Expire exp = ed.getExpireByRegisterCode(registerCode); //����ע�����ȡ����
			
			if(exp == null){ //��ע���벻�������ݿ⣬ֱ�ӷ���
				flag = "1";
			} else { //��ע������������ݿ�
				int expirestate = exp.getState(); //��ȡ����״̬
				switch(expirestate){
				case 0:  //����ǰһ����ǰ
				case 1:  //����ǰһ����֮��
					if(exp.getServers() == 0) { //ֻ��һ���û�
						if (innerIp.equals(exp.getInnerIp())&&outerIp.equals(exp.getOuterIp())) { //ע����һ����������Ipһ��
							flag = "3";
						} else { //ע����һ����������Ip��һ��
							flag = "2";
						}
						ed.updateExpire(innerIp, outerIp, GetTime.ConvertTimeByLong(), registerCode);
//						ed.updateValidTime(GetTime.ConvertTimeByLong(), registerCode);
					} else { //����û�
						if (innerIp.equals(exp.getInnerIp())&&outerIp.equals(exp.getOuterIp())) { //ע����һ����������Ipһ��
							flag = "5";
							ed.updateExpire(innerIp, outerIp, GetTime.ConvertTimeByLong(), registerCode);
						} else { //ע����һ����������Ip��һ��
							if (innerIp.equals(exp.getCur_innerIp()) && outerIp.equals(exp.getCur_outerIp())) { //�����һ����֤��������Ipһ��
								flag = "2";
								ed.updateExpire(innerIp, outerIp, GetTime.ConvertTimeByLong(), registerCode);
							} else {
								flag = "4";
							}
						}
					}
					break;
				case 2: //����
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
