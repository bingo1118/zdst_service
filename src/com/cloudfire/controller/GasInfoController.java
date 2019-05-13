package com.cloudfire.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.entity.query.GpsQuery;
import com.cloudfire.myservice.GpsService;
import com.cloudfire.myservice.impl.GpsServiceImpl;
import com.cloudfire.until.Constant;
import common.page.Pagination;

@Controller
public class GasInfoController {
	@RequestMapping(value="/getOneGasInfo.do",method=RequestMethod.GET)
	public ModelAndView getOneGasInfo(HttpServletRequest request,GpsQuery query,ModelMap model,Integer pageNo){
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		ModelAndView modelAndView = new ModelAndView();
		
		//获得设备类型
		String smokeMac = request.getParameter("mac");
		String startTime = request.getParameter("J_xl_1");
		String endTime = request.getParameter("J_xl_2");
		String status = request.getParameter("status");
		String status7020 = request.getParameter("status7020");
		SmokeDao sd = new SmokeDaoImpl();
	
		int devType = 0;
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(smokeMac)) {
			query.setGasMac(smokeMac);
			params.append("&mac=").append(smokeMac);
			model.addAttribute("mac", smokeMac);
			devType = sd.getDeviceTypeByMac(smokeMac);
		}
		if (StringUtils.isNotBlank(startTime)) {
			query.setStartTime(startTime);
			params.append("&startTime=").append(startTime);
			model.addAttribute("startTime",startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			query.setEndTime(endTime);
			params.append("&endTime=").append(endTime);
			model.addAttribute("endTime", endTime);
		}
		if (StringUtils.isNotBlank(status)) {
			query.setStatus(status);
			params.append("&status=").append(status);
			model.addAttribute("status", status);
		}
		
		if (StringUtils.isNotBlank(status7020)) {
			query.setStatus7020(status7020);
			params.append("&status7020=").append(status7020);
			model.addAttribute("status7020", status7020);
		}
		
		GpsService gpsService = new GpsServiceImpl();
		Pagination pagination = gpsService.getGpsRecordInfo(query);
		pagination.pageView("/fireSystem/getOneGasInfo.do", params.toString());
		model.addAttribute("pagination", pagination);
		switch(devType){
		case 73:
			modelAndView.setViewName("/WEB-INF/page/devicestates/gasRecord7020");
			break;
		default:
			modelAndView.setViewName("/WEB-INF/page/devicestates/gpsRecord");
			break;
		}
		
		return modelAndView;
	}
}
