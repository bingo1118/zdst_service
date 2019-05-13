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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.controller.water.WaterInfoController;
import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.entity.query.DeviceType;
import com.cloudfire.entity.query.FaultInfoEntityQuery;
import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.entity.query.SmokeQuery;
import com.cloudfire.entity.query.WaterQuery;
import com.cloudfire.myservice.ElectricInfoService;
import com.cloudfire.myservice.SmokeService;
import com.cloudfire.myservice.impl.ElectricInfoServiceImpl;
import com.cloudfire.myservice.impl.SmokeServiceImpl;
import com.cloudfire.until.Constant;

import common.page.Pagination;

@Controller
public class SmartControlController {
	private SmartControlDao mSmartControlDao;
	private SmokeService service;
	private DeviceStateController dsController;
	private WaterInfoController wiController;
	
	@RequestMapping(value="/smartControl.do",method=RequestMethod.GET)
	public ModelAndView infoweb(HttpServletRequest request,HttpServletResponse response,String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		modelAndView.setViewName("/WEB-INF/page/main/smartControl");
		return modelAndView;
	}
	
	
	@RequestMapping(value="/deviceCost.do",method=RequestMethod.GET)
	public ModelAndView deviceCost(HttpServletRequest request,String currentId){
		ModelAndView modelAndView =  new ModelAndView();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		mSmartControlDao = new SmartControlDaoImpl();
		List<DeviceType> deviceAndName = mSmartControlDao. getAllDeviceTypeAndName(areaIds);
		modelAndView.addObject("deviceAndName", deviceAndName);
		modelAndView.setViewName("/WEB-INF/page/main/deviceCost");
		return modelAndView;
	}
	
	
	@RequestMapping(value="/allSmokes.do",method=RequestMethod.GET)
	public ModelAndView allSmokes(HttpServletRequest request,HttpServletResponse response, SmokeQuery query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		service = new SmokeServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		ModelAndView modelAndView = new ModelAndView();
		
		String mac = request.getParameter("mac");
		String placeTypeId = request.getParameter("placeTypeId");
		String areaId = request.getParameter("areaId");
		String status = request.getParameter("status");
		String deviceType = request.getParameter("deviceType");
		
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac);
			params.append("&mac=").append(query.getMac());
			model.addAttribute("mac", mac);
		}if (StringUtils.isNotBlank(placeTypeId)) {
			query.setPlaceType(placeTypeId);
			params.append("&placeTypeId=").append(query.getPlaceType());
			model.addAttribute("placeTypeId", placeTypeId);
		}if (StringUtils.isNotBlank(areaId)) {
			query.setAreaName(areaId);
			params.append("&areaId=").append(query.getAreaName());
			model.addAttribute("areaId", areaId);
		}if (StringUtils.isNotBlank(status)) {
			query.setNetState(status);
			params.append("&status=").append(query.getNetState());
			model.addAttribute("status", status);
		}if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
			params.append("&deviceType=").append(query.getDeviceType());
			model.addAttribute("deviceType", deviceType);
		}

		InfoManagerDao infoDao = new InfoManagerDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			Pagination pagination = service.selectAllSmokesListWithPage(areaIds, query);
			pagination.pageView("/fireSystem/allSmokes.do", params.toString());
			//List<ShopTypeEntity> shopList = mSmartControlDao.getPlaceTypeNotType(areaIds);
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
			List<DeviceType> deviceAndName = mSmartControlDao. getAllDeviceTypeAndName(areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			model.addAttribute("pagination", pagination);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);				
			modelAndView.addObject("deviceAndName", deviceAndName);
			modelAndView.addObject("deviceNetState",deviceNetState);
			modelAndView.addAllObjects(modelMap);
		}
		modelAndView.setViewName("/WEB-INF/page/main/deviceCost_list_items");
		return modelAndView;
	}
	
	
	@RequestMapping(value="/allSmoke.do",method=RequestMethod.GET)
	public ModelAndView allSmoke(HttpServletRequest request,HttpServletResponse response,SmokeQuery query,Integer pageNo) throws ServletException, IOException{
		ModelAndView modelAndView = new ModelAndView();
		pageNo = Pagination.cpn(pageNo);
		
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		String mac = request.getParameter("mac");
		String placeTypeId = request.getParameter("placeTypeId");
		String areaId = request.getParameter("areaId");
		String status = request.getParameter("status");
		String deviceType = request.getParameter("deviceType");
		
		//锟斤拷锟斤拷询锟斤拷锟斤拷锟斤拷装锟斤拷query锟斤拷锟襟，癸拷锟斤拷锟斤拷转锟斤拷锟斤拷一页锟斤拷锟斤拷
		StringBuilder params = new StringBuilder(); 
		params.append("1=1");
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac.trim());
			params.append("&mac=").append(query.getMac());
			request.setAttribute("mac", mac);
		}if (StringUtils.isNotBlank(placeTypeId)) {
			query.setPlaceType(placeTypeId);
			params.append("&placeTypeId=").append(query.getPlaceType());
			request.setAttribute("placeTypeId", placeTypeId);
		}if (StringUtils.isNotBlank(areaId)) {
			query.setAreaName(areaId);
			params.append("&areaId=").append(query.getAreaName());
			request.setAttribute("areaId", areaId);
		}if (StringUtils.isNotBlank(status)) {
			query.setNetState(status);
			params.append("&status=").append(query.getNetState());
			request.setAttribute("status", status);
		}if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
			params.append("&deviceType=").append(query.getDeviceType());
			request.setAttribute("deviceType", deviceType);
		}
	
//		String areaIdsStr = (String) request.getSession().getAttribute("areaIdsStr");
//		if(StringUtils.isNotBlank(areaIdsStr)){
//			mSmartControlDao = new SmartControlDaoImpl();
//			List<ShopTypeEntity> shopList = mSmartControlDao.getPlaceTypeNotType(areaIdsStr);
//			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIdsStr);
//			List<DeviceType> deviceAndName = mSmartControlDao. getAllDeviceTypeAndName(areaIdsStr);
//			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIdsStr);
////		}
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
//			InfoManagerDao infoDao = new InfoManagerDaoImpl();
//			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
			mSmartControlDao = new SmartControlDaoImpl();
			List<ShopTypeEntity> shopList = mSmartControlDao.getPlaceTypeNotType(areaIds);
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
			List<DeviceType> deviceAndName = mSmartControlDao. getAllDeviceTypeAndName(areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);	
			modelMap.put("deviceAndName", deviceAndName);
			modelMap.put("deviceNetState", deviceNetState);
			
			service = new SmokeServiceImpl();
			Pagination pagination = service.selectAllSmokeListWithPage(areaIds, query);
			pagination.pageView("/fireSystem/allSmoke.do", params.toString());
			modelAndView.addObject("pagination",pagination);
			modelAndView.addAllObjects(modelMap);
		}
		
		modelAndView.setViewName("/WEB-INF/page/main/deviceState_list_items");
		return modelAndView;
	}
	
	@RequestMapping(value="devStateToPage.do",method=RequestMethod.GET)
	public ModelAndView devStateToPage(HttpServletRequest request,HttpServletResponse response,Object query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		ModelAndView modelAndView = new ModelAndView();
		SearchElectricInfo equery = null;
		WaterQuery wquery = null;
		SmokeQuery squery = null;
		String devType = request.getParameter("devType");
		String currentId = (String) request.getSession().getAttribute("userId");
		if(StringUtils.isNotBlank(devType)){
			switch(devType){
			case "1"://无线感烟火灾探测报警器	---- smoke.do?devType
			case "2"://无线可燃气体探测器
			case "3"://无线手动报警按钮
			case "4"://无线声光报警器
				squery = new SmokeQuery();
				modelAndView = smoke(request, response, squery, model, pageNo,devType);
				break;
			case "5"://无线电气火灾探测器---deviceState_list_electrical.do
				dsController = new DeviceStateController();
				equery = new SearchElectricInfo();
				equery.setType(devType);
				modelAndView = dsController.deviceState_list_items(request, pageNo, equery);
				break;
			case "6"://无线电弧探测器----deviceState_list_electr_arc.do
				dsController = new DeviceStateController();
				equery = new SearchElectricInfo();
				equery.setType(devType);
				modelAndView = dsController.deviceState_list_electr_arc(request, pageNo, equery);
				break;
			case "7"://用户信息传输装置----faultinfo.do
				dsController = new DeviceStateController();
				FaultInfoEntityQuery fequery = new FaultInfoEntityQuery();
				fequery.setType(devType);
				modelAndView = dsController.faultinfo(request, response, currentId, fequery, model, pageNo);
				break;
			case "8"://无线水压探测器-----water.do
			case "9"://无线水位设备
			case "10"://喷淋设备
			case "11"://水浸探测器
				wiController = new WaterInfoController();
				wquery = new WaterQuery();
				wquery.setType(devType);
				modelAndView = wiController.waterInfo(request, wquery, model, pageNo);
				break;
			case "12"://温湿度探测器-----thinfo.do
			case "13"://双向温湿度探测器
				wiController = new WaterInfoController();
				wquery = new WaterQuery();
				wquery.setType(devType);
				modelAndView = wiController.thinfo(request, wquery, model, pageNo);
				break;
			case "14"://NB烟感探测器-----nbsmoke.do
			case "16"://NB气感探测器
			case "17"://NB燃气探测器
				squery = new SmokeQuery();
				squery.setType(devType);
				modelAndView = nbsmoke(request, squery, model, pageNo);
				break;
			case "15"://NB电气火灾探测器---deviceState_NB_electrical.do
				dsController = new DeviceStateController();
				equery = new SearchElectricInfo();
				equery.setType(devType);
				modelAndView = dsController.deviceState_NB_electrical(request, pageNo, equery);
				break;
			case "18"://NB水压水位探测器-----nbWater.do
				squery = new SmokeQuery();
				squery.setType(devType);
				modelAndView = nbWater(request, response, squery, model, pageNo);
				break;
			case "19"://NB三相电气探测器---nbThreeElectric.do
				equery = new SearchElectricInfo();
				equery.setType(devType);
				modelAndView = nbThreeElectric(request, response, equery, model, pageNo);
				break;
			case "20"://红外线
			case "21"://电梯
			case "22"://门磁
			case "23"://环境探测器
			case "24"://GPS定位器
			case "25"://无线模块
				squery = new SmokeQuery();
				modelAndView = smoke(request, response, squery, model, pageNo,devType);
				break;
			case "26"://未知
				squery = new SmokeQuery();
				modelAndView = unknown(request, response,squery, model, pageNo, devType);
				break;
			}
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/unknown.do",method=RequestMethod.GET)
	public ModelAndView unknown(HttpServletRequest request, HttpServletResponse response, SmokeQuery query, ModelMap model, Integer pageNo, String devType) throws ServletException, IOException{
		service = new SmokeServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String type = request.getParameter("type");
		String mac = request.getParameter("mac");
		String placeTypeId = request.getParameter("placeTypeId");
		String areaId = request.getParameter("areaId");
		String status = request.getParameter("status");
		
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(type)) {
			query.setDeviceType(type);
			params.append("&type=").append(Integer.parseInt(type));
			request.setAttribute("type", type);
		}
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac.trim());
			params.append("&mac=").append(query.getMac());
			request.setAttribute("mac", mac);
		}if (StringUtils.isNotBlank(placeTypeId)) {
			query.setPlaceType(placeTypeId);
			params.append("&placeTypeId=").append(query.getPlaceType());
			request.setAttribute("placeTypeId", placeTypeId);
		}if (StringUtils.isNotBlank(areaId)) {
			query.setAreaName(areaId);
			params.append("&areaId=").append(query.getAreaName());
			request.setAttribute("areaId", areaId);
		}if (StringUtils.isNotBlank(status)) {
			query.setNetState(status);
			params.append("&status=").append(query.getNetState());
			request.setAttribute("status", status);
		}
		if (StringUtils.isNotBlank(devType)) {
			query.setType(devType);
			params.append("&devType=").append(query.getType());
			request.setAttribute("devType", devType);
		}
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			Pagination pagination = service.selectUnknownPage(areaIds, query);
			pagination.pageView("/fireSystem/unknown.do", params.toString());
			List<ShopTypeEntity> shopList = mSmartControlDao.getPlaceType(type, devType, areaIds);
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeater(areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			model.addAttribute("pagination", pagination);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelMap.put("deviceNetState", deviceNetState);
			modelAndView.addAllObjects(modelMap);
		}
		
		modelAndView.setViewName("/WEB-INF/page/devicestates/unknown_list");
		return modelAndView;
	}
	
	@RequestMapping(value="/smoke.do",method=RequestMethod.GET)
	public ModelAndView smoke(HttpServletRequest request,HttpServletResponse response,SmokeQuery query,ModelMap model,Integer pageNo,String devType) throws ServletException, IOException{
		service = new SmokeServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		ModelAndView modelAndView = new ModelAndView();
		String type = request.getParameter("type");
		String mac = request.getParameter("mac");
		String placeTypeId = request.getParameter("placeTypeId");
		String areaId = request.getParameter("areaId");
		String status = request.getParameter("status");
		if(StringUtils.isBlank(devType)){
			devType = request.getParameter("devType");
		}
		System.out.println("smoke中的type:"+type);
		
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(type)) {
			query.setDeviceType(type);
			params.append("&type=").append(Integer.parseInt(type));
			request.setAttribute("type", type);
		}if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac.trim());
			params.append("&mac=").append(query.getMac());
			request.setAttribute("mac", mac);
		}if (StringUtils.isNotBlank(placeTypeId)) {
			query.setPlaceType(placeTypeId);
			params.append("&placeTypeId=").append(query.getPlaceType());
			request.setAttribute("placeTypeId", placeTypeId);
		}if (StringUtils.isNotBlank(areaId)) {
			query.setAreaName(areaId);
			params.append("&areaId=").append(query.getAreaName());
			request.setAttribute("areaId", areaId);
		}if (StringUtils.isNotBlank(status)) {
			query.setNetState(status);
			params.append("&status=").append(query.getNetState());
			request.setAttribute("status", status);
		}if (StringUtils.isNotBlank(devType)) {
			query.setType(devType);
			params.append("&devType=").append(query.getType());
			request.setAttribute("devType", devType);
		}
		
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			Pagination pagination = service.selectSmokeListWithPage(areaIds, query);
			pagination.pageView("/fireSystem/smoke.do", params.toString());
			List<ShopTypeEntity> shopList = mSmartControlDao.getPlaceType(type,devType, areaIds);
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeater(areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			model.addAttribute("pagination", pagination);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelMap.put("deviceNetState", deviceNetState);
			modelAndView.addAllObjects(modelMap);
		}
		
		modelAndView.setViewName("/WEB-INF/page/devicestates/deviceState_list_items_smoke");
		return modelAndView;
	}
	
	@RequestMapping(value="/nbsmoke.do",method=RequestMethod.GET)
	public ModelAndView nbsmoke(HttpServletRequest request,SmokeQuery query,ModelMap model,Integer pageNo){
		service = new SmokeServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		ModelAndView modelAndView = new ModelAndView();
		String type = request.getParameter("type");
		String mac = request.getParameter("mac");
		String placeTypeId = request.getParameter("placeTypeId");
		
		String areaId = request.getParameter("areaId");
		String status = request.getParameter("status");
		String devType = request.getParameter("devType");
		if(StringUtils.isNotBlank(devType)){
			query.setType(devType);
		}
		
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(type)) {
			query.setDeviceType(type);
			params.append("&type=").append(Integer.parseInt(type));
			request.setAttribute("type", type);
		}if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac.trim());
			params.append("&mac=").append(query.getMac());
			request.setAttribute("mac", mac);
		}if (StringUtils.isNotBlank(placeTypeId)) {
			query.setPlaceType(placeTypeId);
			params.append("&placeTypeId=").append(query.getPlaceType());
			request.setAttribute("placeTypeId", placeTypeId);
		}if (StringUtils.isNotBlank(areaId)) {
			query.setAreaName(areaId);
			params.append("&areaId=").append(query.getAreaName());
			request.setAttribute("areaId", areaId);
		}if (StringUtils.isNotBlank(status)) {
			query.setNetState(status);
			params.append("&status=").append(query.getNetState());
			request.setAttribute("status", status);
		}if(StringUtils.isNotBlank(query.getType())){
			params.append("&devType=").append(query.getType());
			request.setAttribute("devType", query.getType());
		}
		
		InfoManagerDao infoDao = new InfoManagerDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			Pagination pagination = service.selectNBSmokeList(areaIds, query);
			pagination.pageView("/fireSystem/nbsmoke.do", params.toString());
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			model.addAttribute("pagination", pagination);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelMap.put("deviceNetState", deviceNetState);
			modelAndView.addAllObjects(modelMap);
		}
		
		if(StringUtils.isBlank(type)) type = "0";
		if("72".equals(type)||"73".equals(type)||StringUtils.equals("17", query.getType())){
			modelAndView.setViewName("/WEB-INF/page/devicestates/deviceState_NB_gas");
		}else{
			modelAndView.setViewName("/WEB-INF/page/devicestates/deviceState_NB_smoke");
		}
		return modelAndView;
	}
	@RequestMapping(value="/nbThreeElectric.do",method=RequestMethod.POST)
	public ModelAndView nbThreeElectric2(HttpServletRequest request,HttpServletResponse response,SearchElectricInfo query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		ModelAndView modelAndView = new ModelAndView();
		nbThreeElectric(request, response, query, model, pageNo);
		
		return modelAndView;
	}
	
	/**
	 * 锟斤拷询NB锟斤拷锟斤拷锟教革拷锟斤拷锟斤拷
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping(value="/nbThreeElectric.do",method=RequestMethod.GET)
	public ModelAndView nbThreeElectric(HttpServletRequest request,HttpServletResponse response,SearchElectricInfo query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		ElectricInfoService electricInfoService = new ElectricInfoServiceImpl();
		ModelAndView modelAndView =  new ModelAndView();

		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		String mac = request.getParameter("mac");
		String status = request.getParameter("status");
		String begintime = request.getParameter("begintime");
		String endtime = (String)request.getParameter("endtime");
		String placeTypeId = request.getParameter("placeTypeId");
		String areaId = request.getParameter("areaId");
		String devType = request.getParameter("devType");
		
		if(StringUtils.isNotBlank(devType)){
			query.setType(devType);
		}
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac.trim());
			params.append("&mac=").append(query.getMac());
			request.setAttribute("mac", query.getMac());
		}if(StringUtils.isNotBlank(query.getType())){
			params.append("&devType=").append(query.getType());
			request.setAttribute("devType", query.getType());
		}
		if (StringUtils.isNotBlank(status)) {
			query.setNetState(status);
			params.append("&status=").append(status);
			request.setAttribute("status", query.getNetState());
		}
		if (StringUtils.isNotBlank(begintime)) {
			query.setAlarmBeginTime(begintime);
			params.append("&begintime=").append(begintime);
			request.setAttribute("begintime", begintime);
		}if (StringUtils.isNotBlank(endtime)) {
			query.setAlarmEndTime(endtime);
			params.append("&endtime=").append(endtime);
			request.setAttribute("endtime", endtime);
		}
		if (StringUtils.isNotBlank(placeTypeId)) {
			query.setPlaceType(placeTypeId);
			params.append("&placeTypeId=").append(placeTypeId);
			request.setAttribute("placeTypeId", placeTypeId);
		}if (StringUtils.isNotBlank(areaId)) {
			query.setAreaName(areaId);
			params.append("&areaId=").append(areaId);
			request.setAttribute("areaId", areaId);
		}
//		query.setDevictTypeName("76");//锟斤拷锟斤拷锟斤拷锟�
		query.setDevictTypeName("in (76,77,80,81)");//锟斤拷锟斤拷锟斤拷锟�
		
		
		mSmartControlDao = new SmartControlDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			InfoManagerDao infoDao = new InfoManagerDaoImpl();
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			Pagination pagination = electricInfoService.get_NB_ElectricList(areaIds, query);
			pagination.pageView("/fireSystem/nbThreeElectric.do", params.toString());
			
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType( areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelMap.put("deviceNetState", deviceNetState);
			modelAndView.addAllObjects(modelMap);
			modelAndView.addObject("pagination", pagination);
		}
		modelAndView.setViewName("/WEB-INF/page/devicestates/NB_three_electrical");
		return modelAndView;
	}
	
	/**
	 * 锟斤拷询NB锟斤拷锟斤拷锟教革拷锟斤拷锟斤拷
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping(value="/nbWater.do",method=RequestMethod.GET)
	public ModelAndView nbWater(HttpServletRequest request,HttpServletResponse response,SmokeQuery query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		ModelAndView modelAndView = new ModelAndView();
		
		String mac = request.getParameter("mac");//锟借备ID
		String placeTypeId = request.getParameter("placeTypeId");//锟斤拷业锟斤拷锟斤拷
		String areaId = request.getParameter("areaId");//锟斤拷锟斤拷
		String status = request.getParameter("status");//锟借备状态
		String devType = request.getParameter("devType");
		if(StringUtils.isNotBlank(devType)){
			query.setType(devType);
		}
		//锟斤拷锟斤拷锟斤拷转锟斤拷锟斤拷一页锟斤拷锟斤拷
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac.trim());
			params.append("&mac=").append(query.getMac());
			request.setAttribute("mac", mac);
		}
		if (StringUtils.isNotBlank(placeTypeId)) {
			query.setPlaceType(placeTypeId);
			params.append("&placeTypeId=").append(query.getPlaceType());
			request.setAttribute("placeTypeId", placeTypeId);
		}
		if (StringUtils.isNotBlank(areaId)) {
			query.setAreaName(areaId);
			params.append("&areaId=").append(query.getAreaName());
			request.setAttribute("areaId", areaId);
		}
		if (StringUtils.isNotBlank(status)) {
			query.setNetState(status);
			params.append("&status=").append(query.getNetState());
			request.setAttribute("status", status);
		}
		if(StringUtils.isNotBlank(query.getType())){
			params.append("&devType=").append(query.getType());
			request.setAttribute("devType", query.getType());
		}
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		
		if(areaIds!=null && areaIds.size()>0){
			service = new SmokeServiceImpl();
			Pagination pagination = service.getNBWater(areaIds, query);
			pagination.pageView("/fireSystem/nbWater.do", params.toString());
			InfoManagerDao infoDao = new InfoManagerDaoImpl();
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
			mSmartControlDao = new SmartControlDaoImpl();
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			model.addAttribute("pagination", pagination);
			modelMap.put("shopList", shopList);//锟斤拷业锟斤拷锟斤拷
			modelMap.put("areaAndRepeaters", areaAndRepeaters);		//锟斤拷锟斤拷		+锟斤拷锟斤拷状态
			modelAndView.addAllObjects(modelMap);
		}
		modelAndView.setViewName("/WEB-INF/page/devicestates/NBwater");
		return modelAndView;
	}
	
	@RequestMapping(value="/recentAlarmDetail.do",method=RequestMethod.GET)
	public ModelAndView recentAlarmDetai(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		ModelAndView mav = new ModelAndView();
		String mac = request.getParameter("mac");
		
		AlarmInfoDao aid = new AlarmInfoDaoImpl();
		AlarmInfoEntity aie = aid.getRecentAlarmInfo(mac);
		
		mav.addObject("aie", aie);
		mav.setViewName("/WEB-INF/page/main/recentAlarmDetail");
		return mav;
	}
	
}
