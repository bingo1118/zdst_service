package com.cloudfire.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.ToolDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.ToolDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.LoginEntity;

@Controller
public class ConstructorDetail {
	private AreaDao mAreaDao;
	private LoginDao mLoginDao;
	private AlarmInfoDao mAlarmInfoDao;

	@RequestMapping(value="updateUserInfo.do",method=RequestMethod.GET)
	public ModelAndView updateUserInfo2(HttpServletRequest request,String currentId,HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView();
		updateUserInfo(request,currentId,response);
		return modelAndView;
	}
	@RequestMapping(value="updateUserInfo.do",method=RequestMethod.POST)
	public ModelAndView updateUserInfo(HttpServletRequest request,String currentId,HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String)request.getSession().getAttribute("userId");
		String message = "";
		if(StringUtils.isBlank(currentId)){
			modelAndView.setViewName("/welcome");
		}else{
			mLoginDao = new LoginDaoImpl();
			LoginEntity mLoginEntity = mLoginDao.login(currentId);
			if(mLoginEntity!=null){
				String principal1=request.getParameter("principal1");
				String principal1Phone=request.getParameter("principal1Phone");
				String principal2=request.getParameter("principal2");
				String principal2Phone=request.getParameter("principal2Phone");
				String repeater = request.getParameter("repeater");
				ToolDao tdao = new ToolDaoImpl();
				if(StringUtils.isNotBlank(repeater)){
					if(StringUtils.isNotBlank(principal1)&&StringUtils.isNotBlank(principal2)){
						tdao.updateSmokePrincipal(repeater, principal1, principal1Phone, principal2, principal2Phone);
						message = "设置成功！";
					}else if(StringUtils.isNotBlank(principal1)){
						tdao.updateSmokePrincipal(repeater, principal1Phone, principal1Phone);
						message = "设置成功！";
					}else if(StringUtils.isNotBlank(principal2)){
						tdao.updateSmokePrincipal2(repeater, principal2, principal2Phone);
						message = "设置成功！";
					}
					
				}else{
					message = "设置失败！";
				}
			}
			modelAndView.setViewName("/resetTelUser");
		}
		modelAndView.addObject("message", message);
		return modelAndView;
	}
	
	@RequestMapping(value="/constructorDetail.do",method=RequestMethod.GET)
	public ModelAndView statisticAnalysis(HttpServletRequest request, HttpServletResponse response, String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areaIds);
		modelAndView.addObject("alarmInfos", lists);
		modelAndView.setViewName("/WEB-INF/page/main/smartControl_list_items");
		return modelAndView;
	} 
}
