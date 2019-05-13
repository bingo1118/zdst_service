package com.cloudfire.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;

@Controller
public class NoContentController {
	
	@RequestMapping(value="/noContent.do",method=RequestMethod.GET)
	public ModelAndView deviceState(HttpServletRequest request,String currentId){
		ModelAndView modelAndView =  new ModelAndView();
		@SuppressWarnings("unchecked")
		List<String> areaIds =(List<String>) request.getSession().getAttribute("areaIds");
		AlarmInfoDaoImpl mAlarmInfoDao = new AlarmInfoDaoImpl();
		List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areaIds);
		
		Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
		modelMap.put("alarmInfos", lists);
		modelAndView.addAllObjects(modelMap);
		
		modelAndView.setViewName("/WEB-INF/page/main/nocontent");
		return modelAndView;
	} 
}
