package com.cloudfire.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.until.WriteJson;

@Controller
public class PushController {
	private AlarmInfoDao mAlarmInfoDao;
	
	@RequestMapping(value="/push.do",method=RequestMethod.POST)
	public void push2(HttpServletRequest request,String currentId,HttpServletResponse response){
		push(request,currentId,response);
	}
	@RequestMapping(value="/push.do",method=RequestMethod.GET)
	public void push(HttpServletRequest request,String currentId,HttpServletResponse response){
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(StringUtils.isBlank(currentId)){
			currentId = (String) request.getSession().getAttribute("userId");
		}
		if(currentId.equals("abcdefg001")){//预留该账号不显示所有报警数据
			try {
				response.getWriter().write("[]");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			mAlarmInfoDao = new AlarmInfoDaoImpl();
			List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areaIds);
			//后台分页
			request.setAttribute("alarmInfos", lists);
			WriteJson writeJson = new WriteJson();
			if (lists != null && lists.size() > 0) {
				String jObject = writeJson.getJsonData(lists);
				try {
					response.getWriter().write(jObject);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				String jObject = "[]";
				try {
					response.getWriter().write(jObject);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*public void push(HttpServletRequest request,String currentId,HttpServletResponse response){
		ModelAndView modelAndView =  new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		if(StringUtils.isBlank(currentId)) {
			modelAndView.setViewName("/WEB-INF/page/login/login");
		}else{
			mLoginDao = new LoginDaoImpl();
			LoginEntity mLoginEntity = mLoginDao.login(currentId);
			if(mLoginEntity!=null){
				String privilege = mLoginEntity.getPrivilege()+"";
				mAreaDao = new AreaDaoImpl();
				List<String> areIds = mAreaDao.getAreaStr(currentId, privilege);
				mAlarmInfoDao = new AlarmInfoDaoImpl();
				List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areIds);
				request.setAttribute("alarmInfos", lists);
				if (lists != null && lists.size() > 0) {
					WriteJson writeJson = new WriteJson();
					String jObject = writeJson.getJsonData(lists);
					try {
						response.getWriter().write(jObject);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					WriteJson writeJson = new WriteJson();
					String jObject = "[]";
					try {
						response.getWriter().write(jObject);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}*/
}
