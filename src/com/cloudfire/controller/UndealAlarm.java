package com.cloudfire.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.query.DeviceType;
import com.cloudfire.myservice.AlarmInfoService;
import com.cloudfire.myservice.impl.AlarmInfoServiceImpl;
import com.cloudfire.until.Constant;
import common.page.Pagination;

@Controller
public class UndealAlarm {
	private SmartControlDao mSmartControlDao;
	private AlarmInfoService alarmInfoService;
	
	@RequestMapping(value="/unDeal.do",method=RequestMethod.GET)
	public ModelAndView deviceState(HttpServletRequest request,Integer pageNo,AlarmInfoEntity_PC query){
		alarmInfoService = new AlarmInfoServiceImpl();
		ModelAndView modelAndView =  new ModelAndView();
		
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		String mac = request.getParameter("mac");
		String deviceType = request.getParameter("deviceType");
		String alarmType = request.getParameter("alarmType");
		String begintime = request.getParameter("J_xl_1");
		String endtime = request.getParameter("J_xl_2");
		
		//设置查询的参数
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(mac)) {
			query.setDevMac(mac.trim());
			params.append("&mac=").append(query.getMac().trim());
			request.setAttribute("mac", query.getMac());
		}
		if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
			params.append("&deviceType=").append(query.getDeviceType());
			request.setAttribute("deviceType", query.getDeviceType());
		}
		if (StringUtils.isNotBlank(alarmType)) {
			query.setAlarmType(alarmType);
			params.append("&alarmType=").append(query.getAlarmType());
			request.setAttribute("alarmType", query.getAlarmType());
		}
		if (StringUtils.isNotBlank(begintime)) {
			query.setBegintime(begintime+" 00:00:00");
			params.append("&J_xl_1=").append(query.getBegintime());
			request.setAttribute("J_xl_1", begintime);
		}
		if (StringUtils.isNotBlank(endtime)) {
			query.setEndtime(endtime+ " 23:59:59");
			params.append("&J_xl_2=").append(query.getEndtime());
			request.setAttribute("J_xl_2", endtime);
		}
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds != null){
			mSmartControlDao = new SmartControlDaoImpl();
			Pagination pagination = alarmInfoService.selectAlarmInfoListByPage(areaIds, query);
			List<DeviceType> deviceAndName = mSmartControlDao.getAllDeviceTypeAndName(areaIds);
			pagination.pageView("/fireSystem/unDeal.do",params.toString());
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();				
			modelMap.put("deviceAndName", deviceAndName);
			modelAndView.addAllObjects(modelMap);
			modelAndView.addObject("pagination", pagination);
		}
			
		modelAndView.setViewName("/WEB-INF/page/main/undeal");
		return modelAndView;
	} 
	
}
