package com.cloudfire.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.OperationDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.OperationDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.Operation;
import com.cloudfire.entity.OperationQuery;
import com.cloudfire.entity.OperationType;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.entity.query.SmokeQuery;
import com.cloudfire.myservice.OperationService;
import com.cloudfire.myservice.impl.OperationServiceImpl;
import com.cloudfire.myservice.impl.SmokeServiceImpl;
import com.cloudfire.until.Constant;
import common.page.Pagination;

@Controller
public class OperationController {
	@RequestMapping(value="/allOperation.do",method=RequestMethod.GET)
	public ModelAndView allOperation(HttpServletRequest request,OperationQuery query,ModelMap model,Integer pageNo){
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String operator = request.getParameter("operator");
		String object = request.getParameter("object");
		String opt = request.getParameter("opt");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		
		//构建跳转到下一页参数
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(operator)) {
			query.setOperator(operator);
			params.append("operator=").append(operator);
			model.addAttribute("operator", operator);
		}if (StringUtils.isNotBlank(object)) {
			query.setObject(object);
			params.append("&object=").append(object);
			model.addAttribute("object",object);
		}if (StringUtils.isNotBlank(opt)) {
			query.setOpt(opt);
			params.append("&opt=").append(opt);
			model.addAttribute("opt", opt);
		}if (StringUtils.isNotBlank(startTime)) {
			query.setStartTime(startTime+" 00:00:00");
			params.append("&startTime=").append(startTime);
			model.addAttribute("startTime", startTime);
		}if (StringUtils.isNotBlank(endTime)) {
			query.setEndTime(endTime+" 23:59:59");
			params.append("&endTime=").append(endTime);
			model.addAttribute("endTime", endTime);
		}
		
		OperationDao od = new OperationDaoImpl();
		List<OperationType> lstop= od.getAllType();
		OperationService service = new OperationServiceImpl();
		Pagination pagination = service.getOperations(query);
		pagination.pageView("/"+Constant.ContextPath+"/allOperation.do", params.toString());
		model.addAttribute("pagination", pagination);
		Map<String,List<?>> modelMap = new HashMap<String, List<?>>();
		modelMap.put("lstop", lstop);
		modelAndView.addAllObjects(modelMap);
		modelAndView.setViewName("/operation_record");
		return modelAndView;
	}
	
	
}
