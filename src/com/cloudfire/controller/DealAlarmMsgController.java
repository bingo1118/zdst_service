package com.cloudfire.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.DealAlarmDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.DealAlarmDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.DealOkAlarmEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.query.AlarmInfoQuery;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.entity.query.DeviceType;
import com.cloudfire.myservice.AlarmInfoService;
import com.cloudfire.myservice.ControllerService;
import com.cloudfire.myservice.impl.AlarmInfoServiceImpl;
import com.cloudfire.myservice.impl.ControllerServiceImpl;
import com.cloudfire.push.RBMQClient;
import com.cloudfire.until.Constant;
import com.cloudfire.until.WriteJson;
import common.page.Pagination;

@Controller
public class DealAlarmMsgController {
	private DealAlarmDao mDealAlarmDao;
	private AlarmInfoDao mAlarmInfoDao;
	private SmartControlDao mSmartControlDao;
	private AlarmInfoService alarmInfoService;
	private ControllerService service;
	/**
	 * 根据烟感的mac和报警时间查询烟感的信息
	 * @param request
	 * @param currentId 当前用户的id
	 * @param devMac 前台传过来的烟感的mac
	 * @param alarmTime 点击的那条信息的烟感报警时间
	 * @param response 响应给前台的json数据
	 * @throws IOException 
	 */
	@RequestMapping(value="/searchByDevMac.do",method=RequestMethod.GET)
	public void searchByDevMac(HttpServletRequest request,String currentId,String devMac,String alarmTime,HttpServletResponse response) throws IOException{
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if (areaIds != null) {
			mAlarmInfoDao = new AlarmInfoDaoImpl();
			//List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areIds);
			List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfoByDev(devMac,alarmTime);
			if (lists!=null && lists.size()>0) {
				WriteJson writeJson = new WriteJson();
				String object = writeJson.getJsonData(lists);
				response.getWriter().write(object);
			}else {
				response.getWriter().write("[]");
			}
		} else{
			response.getWriter().write("[]");
		}
			
	}	
	
	@RequestMapping(value="/dealAlarmMsg.do",method=RequestMethod.GET)
	public void dealAlarmMsg(HttpServletRequest request,String currentId,String devMac,String dealPeople,HttpServletResponse response){
		currentId =(String) request.getSession().getAttribute("userId");
		String dealDetail = request.getParameter("dealDetail");
		String alarmTruth = request.getParameter("alarmTruth");
		String alarmType1 = request.getParameter("alarmType1");
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		 //devMac = (String)request.getAttribute("devMac");
		try {
			//真实报警推给国瑞和伊特诺
			if ("4".equals(alarmTruth)) {
				RBMQClient.PushToDianXin(devMac,Integer.parseInt(alarmType1));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
		try {
			mDealAlarmDao = new DealAlarmDaoImpl();
			mDealAlarmDao.dealAlarm(currentId,dealPeople,devMac,dealDetail,alarmTruth);
			//System.out.println(result);
			mAlarmInfoDao = new AlarmInfoDaoImpl();
			List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areaIds);
			
			if (lists != null && lists.size() > 0) {
				WriteJson writeJson = new WriteJson();
				String jObject = writeJson.getJsonData(lists);
				response.getWriter().write(jObject);
			}else {
//				WriteJson writeJson = new WriteJson();
				String jObject = "[]";
				response.getWriter().write(jObject);
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 该方法对应于智能监控下-->设备处理记录-->报警记录 
	 *  */
	@RequestMapping(value="/selectAllAlarmMsg.do",method=RequestMethod.GET)
	public ModelAndView selectAllAlarmMsgListByPage(HttpServletRequest request,Integer pageNo,AlarmInfoQuery query){
		alarmInfoService = new AlarmInfoServiceImpl();
		ModelAndView modelAndView =  new ModelAndView();
		
		String mac = request.getParameter("mac");
		String deviceType = request.getParameter("deviceType");
		String areaId = request.getParameter("areaId");
		String alarmType = request.getParameter("alarmType");
		String status = request.getParameter("status");
		String begintime = request.getParameter("J_xl_1");
		String endtime = request.getParameter("J_xl_2");
		
		//设置查询的参数
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(mac)) {
			query.setDevMac(mac.trim());
			params.append("&mac=").append(query.getDevMac());
			request.setAttribute("mac", query.getDevMac());
		}
		if (StringUtils.isNotBlank(areaId)) {
			query.setAreaName(areaId);
			params.append("&areaId=").append(query.getAreaName());
			request.setAttribute("areaId", query.getAreaName());
		}
		if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
			params.append("&deviceType=").append(query.getDeviceType());
			request.setAttribute("deviceType", query.getDeviceType());
		}
		if (StringUtils.isNotBlank(status)) {
			query.setNetState(status);
			params.append("&status=").append(status);
			request.setAttribute("status", query.getNetState());
		}
		if (StringUtils.isNotBlank(alarmType)) {
			query.setAlarmType(alarmType);
			params.append("&alarmType=").append(query.getAlarmType());
			request.setAttribute("alarmType", query.getAlarmType());
		}
		if (StringUtils.isNotBlank(begintime)) {
			query.setBegintime(begintime+" 00:00:00");
			params.append("&J_xl_1=").append(begintime);
			request.setAttribute("J_xl_1", begintime);
		}
		if (StringUtils.isNotBlank(endtime)) {
			query.setEndtime(endtime+" 23:59:59");
			params.append("&J_xl_2=").append(endtime);
			request.setAttribute("J_xl_2", endtime);
		}
		
		pageNo = Pagination.cpn(pageNo); // return (pageNo == null || pageNo < 1) ? 1 : pageNo;
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		mSmartControlDao = new SmartControlDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if (areaIds != null ){
			Pagination pagination = alarmInfoService.selectAllAlarmInfoListByPage(areaIds, query);
			List<DeviceType> deviceAndName = mSmartControlDao.getAllDeviceTypeAndName(areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
			pagination.pageView("/fireSystem/selectAllAlarmMsg.do",params.toString());
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();	
			modelMap.put("deviceNetState", deviceNetState);
			modelMap.put("deviceAndName", deviceAndName);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelAndView.addAllObjects(modelMap);
			modelAndView.addObject("pagination", pagination);
		}
			modelAndView.setViewName("/WEB-INF/page/devicestates/allalarmmsg");
		
		return modelAndView;
	} 
	
	@RequestMapping(value="/exportAllAlarmMsg.do",method=RequestMethod.GET)
	public ModelAndView exportAllAlarmMsg(HttpServletRequest request,Integer pageNo,AlarmInfoQuery query){
		alarmInfoService = new AlarmInfoServiceImpl();
		ModelAndView modelAndView =  new ModelAndView();
	
		String mac = request.getParameter("mac");
		String deviceType = request.getParameter("deviceType");
		String areaId = request.getParameter("areaId");
		String alarmType = request.getParameter("alarmType");
		String status = request.getParameter("status");
		String begintime = request.getParameter("J_xl_1");
		String endtime = request.getParameter("J_xl_2");
		
		//设置查询的参数
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(mac)) {
			query.setDevMac(mac.trim());
			params.append("&mac=").append(query.getDevMac());
			request.setAttribute("mac", query.getDevMac());
		}
		if (StringUtils.isNotBlank(areaId)) {
			query.setAreaName(areaId);
			params.append("&areaId=").append(query.getAreaName());
			request.setAttribute("areaId", query.getAreaName());
		}
		if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
			params.append("&deviceType=").append(query.getDeviceType());
			request.setAttribute("deviceType", query.getDeviceType());
		}
		if (StringUtils.isNotBlank(status)) {
			query.setNetState(status);
			params.append("&status=").append(status);
			request.setAttribute("status", query.getNetState());
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
			query.setEndtime(endtime+" 23:59:59");
			params.append("&J_xl_2=").append(query.getEndtime());
			request.setAttribute("J_xl_2", endtime);
		}
		
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(1000);
	
		mSmartControlDao = new SmartControlDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds != null){
			Pagination pagination = alarmInfoService.selectAllAlarmInfoListByPage(areaIds, query);
			List<DeviceType> deviceAndName = mSmartControlDao.getAllDeviceTypeAndName(areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
			pagination.pageView("/fireSystem/exportAllAlarmMsg.do",params.toString());
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();	
			modelMap.put("deviceNetState", deviceNetState);
			modelMap.put("deviceAndName", deviceAndName);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelAndView.addAllObjects(modelMap);
			modelAndView.addObject("pagination", pagination);
		}
		
		modelAndView.setViewName("/WEB-INF/page/devicestates/exportallalarmmsg");
		return modelAndView;
	} 
	
	/**
	 * 未处理记录一键处理(权限为4才可以操作)
	 */
	@RequestMapping(value="/oneKeyAllAlarm.do",method=RequestMethod.GET)
	public ModelAndView oneKeyAllAlarm(HttpServletRequest request,String current,Integer pageNo,AlarmInfoEntity_PC query){
		alarmInfoService = new AlarmInfoServiceImpl();
		ModelAndView modelAndView =  new ModelAndView();
	
		String currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		
		StringBuilder params = new StringBuilder();
		
		//设置查询的参数
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds != null){
			mDealAlarmDao = new DealAlarmDaoImpl();
			
			HttpRsult result = new HttpRsult();
			if(privilege.equals("4")){
				result = mDealAlarmDao.dealAlarm(currentId);
				mAlarmInfoDao = new AlarmInfoDaoImpl();
				List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areaIds);
				Map<String,List<?>> modelMap = new HashMap<String,List<?>>();	
				modelMap.put("lists", lists);
				modelAndView.addAllObjects(modelMap);
			}else{
				result.setError("您权限不够");
				mSmartControlDao = new SmartControlDaoImpl();
				Pagination pagination = alarmInfoService.selectAlarmInfoListByPage(areaIds, query);
				List<DeviceType> deviceAndName = mSmartControlDao.getAllDeviceTypeAndName(areaIds);
				pagination.pageView("/fireSystem/unDeal.do",params.toString());
				Map<String,List<?>> modelMap = new HashMap<String,List<?>>();				
				modelMap.put("deviceAndName", deviceAndName);
				modelAndView.addAllObjects(modelMap);
				modelAndView.addObject("pagination", pagination);
			}
			request.setAttribute("HttpRsult", result);
		}
			
		modelAndView.setViewName("/WEB-INF/page/main/undeal");
		return modelAndView;
	} 
	
	
	
	
	
	
	/**
	 * 该方法对应于智能监控下-->设备处理记录-->报警记录 -->已处理
	 * @throws IOException 
	 * @throws ServletException 
	 *  */
	@RequestMapping(value="/okDeal.do",method=RequestMethod.GET)
	public ModelAndView okDealAlarmMsg(HttpServletResponse response,HttpServletRequest request,Integer pageNo,DealOkAlarmEntity query) throws ServletException, IOException{
		service = new ControllerServiceImpl();
		ModelAndView modelAndView = new ModelAndView();
		
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		String address = request.getParameter("address");
		String deviceType = request.getParameter("deviceType");
		String mac = request.getParameter("mac");
		String alarmType = request.getParameter("alarmType");
		
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(address )) {
			query.setAlarmAddress(address);
			params.append("&address=").append(address );
			request.setAttribute("address", address);
		}
		if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
			params.append("&deviceType=").append(deviceType);
			request.setAttribute("deviceType", deviceType);
		}if (StringUtils.isNotBlank(mac)) {
			query.setDevMac(mac.trim());
			params.append("&mac=").append(mac);
			request.setAttribute("mac", query.getDevMac());
		}
		if (StringUtils.isNotBlank(alarmType )) {
			query.setAlarmAddress(alarmType);
			params.append("&alarmType=").append(alarmType);
			request.setAttribute("alarmType", alarmType);
		}
		
		mSmartControlDao = new SmartControlDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if (areaIds != null) {
			Map<String, List<?>> modelMap = new HashMap<String, List<?>>();
			Pagination pagination = service.selectControllerListWithPage(areaIds, query);
			pagination.pageView("/fireSystem/okDeal.do",params.toString());
			//List<DealOkAlarmEntity> deviceAndName = mSmartControlDao.getDeviceTypeAndName(areaIds);
			List<DeviceType> deviceAndName = mSmartControlDao.getAllDeviceTypeAndName(areaIds);
			modelMap.put("deviceAndName", deviceAndName);
			modelAndView.addAllObjects(modelMap);
			modelAndView.addObject("pagination", pagination);
		}
		
		modelAndView.setViewName("/WEB-INF/page/main/dealOkAlarmMsg");
		return modelAndView;
	}
	
	
}
