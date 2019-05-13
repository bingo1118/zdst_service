package com.cloudfire.controller;

import java.io.IOException;
import java.util.ArrayList;
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

import com.cloudfire.dao.AllElectricInfoDao;
import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.DeviceStateDao;
import com.cloudfire.dao.ElectricPCDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.AllElectricInfoDaoImpl;
import com.cloudfire.dao.impl.DeviceStateDaoImpl;
import com.cloudfire.dao.impl.DevicesDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.dao.query.SearchElectricQuery;
import com.cloudfire.dao.query.impl.SearchElectricQueryImpl;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.entity.query.FaultInfoEntityQuery;
import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.myservice.CompanyService;
import com.cloudfire.myservice.ElectricInfoService;
import com.cloudfire.myservice.impl.CompanyServiceImpl;
import com.cloudfire.myservice.impl.ElectricInfoServiceImpl;
import com.cloudfire.until.Constant;
import com.cloudfire.until.WriteJson;

import common.page.Pagination;

@Controller
public class DeviceStateController {
	private static final StringBuilder params = null;
	private AllElectricInfoDao allElectricInfoDao;
	private SmartControlDao mSmartControlDao;
	private ElectricInfoService electricInfoService;
	private ElectricPCDao mElectricPCDao; 

	@RequestMapping(value="/deviceState.do",method=RequestMethod.POST)
	public ModelAndView deviceState2(HttpServletRequest request,String currentId){
		ModelAndView ma = deviceState(request,currentId);
		return ma;
	}
	
	
	@RequestMapping(value="/deviceState.do",method=RequestMethod.GET)
	public ModelAndView deviceState(HttpServletRequest request,String currentId){
		ModelAndView modelAndView =  new ModelAndView();
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		
		if(areaIds!=null&&areaIds.size()>0){
			DeviceStateDao dsd = new DeviceStateDaoImpl();
			
			Map<String,Map<Integer,String>> deviceMap = dsd.getDeviceStateToMap(areaIds);
			
			modelAndView.addObject("deviceSystem", deviceMap);
			
		}
		
		modelAndView.setViewName("/WEB-INF/page/main/deviceState");
		return modelAndView;
	} 
	
	
	@RequestMapping(value="/showAllMessage.do",method=RequestMethod.GET)
	public ModelAndView shwoAllMessage(HttpServletRequest request,ModelMap model){
		ModelAndView modelAndView =  new ModelAndView();
		SearchElectricQuery seq = new SearchElectricQueryImpl();
		String mac = request.getParameter("mac");
		SearchElectricInfo searchInfo = null;
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		
		if(areaIds!=null&&areaIds.size()>0){
			searchInfo = new SearchElectricInfo();
			searchInfo = seq.queryDeviceTypeByMac(mac,areaIds);
			model.addAttribute("searchInfo", searchInfo);
		}
		modelAndView.setViewName("/WEB-INF/page/devicestates/showAllInfomation");
		return modelAndView;
	} 
	
	@RequestMapping(value="/deviceState_list_electrical.do",method=RequestMethod.GET)
	public ModelAndView deviceState_list_items(HttpServletRequest request,Integer pageNo,SearchElectricInfo query){
		electricInfoService = new ElectricInfoServiceImpl();
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
		}if (StringUtils.isNotBlank(status)) {
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
		if (StringUtils.isNotBlank(query.getType())) {
			params.append("&devType=").append(query.getType());
			request.setAttribute("devType", query.getType());
		}
		
		mSmartControlDao = new SmartControlDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		
		if(areaIds!=null&&areaIds.size()>0){
			InfoManagerDao infoDao = new InfoManagerDaoImpl();
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			allElectricInfoDao = new AllElectricInfoDaoImpl();
			Pagination pagination = electricInfoService.getElectricListWithPage(areaIds, query);
			pagination.pageView("/fireSystem/deviceState_list_electrical.do", params.toString());
			
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType( areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelMap.put("deviceNetState", deviceNetState);
			modelAndView.addAllObjects(modelMap);
			modelAndView.addObject("pagination", pagination);
		}
		modelAndView.setViewName("/WEB-INF/page/main/deviceState_list_electrical");
		return modelAndView;
	} 
	
	@RequestMapping(value="/deviceState_NB_electrical.do",method=RequestMethod.GET)
	public ModelAndView deviceState_NB_electrical(HttpServletRequest request,Integer pageNo,SearchElectricInfo query){
		electricInfoService = new ElectricInfoServiceImpl();
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
		}if (StringUtils.isNotBlank(status)) {
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
		}if(StringUtils.isNotBlank(query.getType())){
			params.append("&devType = ").append(query.getType());
			request.setAttribute("devType", query.getType());
		}
		
		mSmartControlDao = new SmartControlDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			InfoManagerDao infoDao = new InfoManagerDaoImpl();
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			allElectricInfoDao = new AllElectricInfoDaoImpl();
			Pagination pagination = electricInfoService.get_NB_ElectricList(areaIds, query);
			pagination.pageView("/fireSystem/deviceState_NB_electrical.do", params.toString());
			
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType( areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelMap.put("deviceNetState", deviceNetState);
			modelAndView.addAllObjects(modelMap);
			modelAndView.addObject("pagination", pagination);
		}
		modelAndView.setViewName("/WEB-INF/page/devicestates/deviceState_NB_electrical");
		return modelAndView;
	}
	
	@RequestMapping(value="/mapCount.do",method=RequestMethod.GET)
	public void mapCount(HttpServletRequest request,String currentId,HttpServletResponse response){
		ModelAndView modelAndView =  new ModelAndView();
//		currentId = (String) request.getSession().getAttribute("userId");
//		String privilege = request.getSession().getAttribute("privilege")+"";
//		mAreaDao = new AreaDaoImpl();
//		List<String> areIds = mAreaDao.getAreaStr(currentId, privilege);
//		AlarmInfoDaoImpl mAlarmInfoDao = new AlarmInfoDaoImpl();
//		List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areIds);				
		Map<String,List<?>> modelMap = new HashMap<String,List<?>>();	

		List<CountValue> cvList = new ArrayList<CountValue>();
		DeviceDao dd = new DevicesDaoImpl();
		cvList = dd.getCountNum(null);
		modelMap.put("cvList", cvList);
		if(cvList !=null && cvList.size()>0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(cvList);
			try {
				response.getWriter().write(jObject);
				//System.out.println(jObject);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		modelAndView.addAllObjects(modelMap);
		//modelAndView.addObject("alarmInfos", lists);
	} 
	
	@RequestMapping(value="/faultinfo.do",method=RequestMethod.GET)
	public ModelAndView faultinfo(HttpServletRequest request,HttpServletResponse response, String currentId,FaultInfoEntityQuery query,ModelMap model, Integer pageNo ) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		String repeaterMac = request.getParameter("repeaterId");
		String J_xl_1 = request.getParameter("J_xl_1");
		String J_xl_2 = request.getParameter("J_xl_2");
		String devType = request.getParameter("devType");
		String status = request.getParameter("status");
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if(StringUtils.isNotBlank(devType)){
			query.setType(devType);
			params.append("&devType=").append(query.getType());
			request.setAttribute("devType", query.getType());
		}
		if(StringUtils.isNotBlank(status)){
			query.setStatus(status);
			params.append("&status=").append(query.getStatus());
			request.setAttribute("status", query.getStatus());
		}
		if (StringUtils.isNotBlank(J_xl_1)&& StringUtils.isNotBlank(J_xl_2)) {
			J_xl_1 = J_xl_1+" 00:00:00";
			J_xl_2 = J_xl_2+" 23:59:59";
		}
		
		
		if(StringUtils.isNotBlank(repeaterMac)){
			query.setRepeaterMac(repeaterMac.trim());
			params.append("&repeaterId=").append(repeaterMac.trim());
			request.setAttribute("repeaterId", query.getRepeaterMac());
		}
		if(StringUtils.isNotBlank(J_xl_1)){
			query.setJ_xl_1(J_xl_1);
			params.append("&J_xl_1=").append(J_xl_1);
			if (J_xl_1.contains(" 00:00:00")) {
				J_xl_1 = (String) J_xl_1.substring(0, 10);
			}
			request.setAttribute("J_xl_1", J_xl_1);
		}
		if(StringUtils.isNotBlank(J_xl_2)){
			query.setJ_xl_2(J_xl_2);
			params.append("&J_xl_2=").append(J_xl_2);
			if (J_xl_2.contains(" 23:59:59")) {
				J_xl_2 = (String) J_xl_2.substring(0, 10);
			}
			request.setAttribute("J_xl_2", J_xl_2);
		}
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds != null && areaIds.size() > 0){
			CompanyService service = new CompanyServiceImpl();
			Pagination pagination = service.selectFaultinfo(query,areaIds);
			pagination.pageView("/fireSystem/faultinfo.do", params.toString());
			model.addAttribute("pagination", pagination);
			modelAndView.addAllObjects(model);
		}
		
		modelAndView.setViewName("/WEB-INF/page/main/faultinfo_list_items");
		return modelAndView;
	}
	
	@RequestMapping(value="/faultinfo2.do",method=RequestMethod.GET)
	public ModelAndView faultinfo2(HttpServletRequest request,HttpServletResponse response,String currentId,FaultInfoEntityQuery query,ModelMap model, Integer pageNo ) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		
		pageNo = Pagination.cpn(pageNo);
		//repeater=09101620&faultCode=00101000
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		String repeater = request.getParameter("repeaterId");
		String repeaterMac = request.getParameter("repeaterMac");
		String faultCode = request.getParameter("faultCode");
		String J_xl_1 = request.getParameter("J_xl_1");
		String J_xl_2 = request.getParameter("J_xl_2");
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if(StringUtils.isNotBlank(faultCode)){
			query.setFaultCode(faultCode);
			params.append("&faultCode=").append(faultCode);
			request.setAttribute("faultCode",faultCode);
		}
		if(StringUtils.isNotBlank(repeaterMac)){
			query.setRepeaterMac(repeaterMac);
			params.append("&repeaterId=").append(repeaterMac);
			request.setAttribute("repeaterMac", repeaterMac);
		}
		if(StringUtils.isNotBlank(repeater)){
			query.setRepeaterMac(repeater);
			params.append("&repeaterId=").append(repeater);
			request.setAttribute("repeater", repeater);
		}
		if(StringUtils.isNotBlank(J_xl_1)){
			query.setJ_xl_1(J_xl_1+" 00:00:00");
			params.append("&J_xl_1=").append(J_xl_1);
			request.setAttribute("J_xl_1", J_xl_1);
		}
		if(StringUtils.isNotBlank(J_xl_2)){
			query.setJ_xl_2(J_xl_2+" 23:59:59");
			params.append("&J_xl_2=").append(J_xl_2);
			request.setAttribute("J_xl_2", J_xl_2);
		}
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds !=null ) {
			CompanyService service = new CompanyServiceImpl();
			Pagination pagination = service.selectFaultinfo2(query,areaIds,repeaterMac,faultCode);
			pagination.pageView("/fireSystem/faultinfo2.do", params.toString());
			model.addAttribute("pagination", pagination);
			modelAndView.addAllObjects(model);
		}
		modelAndView.setViewName("/WEB-INF/page/devicestates/faultinfo_list_two");
		return modelAndView;
	}
	
	@RequestMapping(value="/faultinfo3.do",method=RequestMethod.GET)
	public ModelAndView faultinfo3(HttpServletRequest request,String currentId,FaultInfoEntityQuery query,ModelMap model, Integer pageNo ){
		ModelAndView modelAndView =  new ModelAndView();
		
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		String repeaterMac = request.getParameter("repeaterMac");
		String J_xl_1 = request.getParameter("J_xl_1");
		String J_xl_2 = request.getParameter("J_xl_2");
		String faultCode = request.getParameter("faultCode");
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if(StringUtils.isNotBlank(faultCode)){
			query.setFaultCode(faultCode);
			params.append("&faultCode=").append(faultCode);
			request.setAttribute("faultCode", query.getFaultCode());
		}
		if(StringUtils.isNotBlank(repeaterMac)){
			query.setRepeaterMac(repeaterMac);
			params.append("&repeaterMac=").append(repeaterMac);
			request.removeAttribute("repeaterMac");
			request.setAttribute("repeaterMac", query.getRepeaterMac());
		}
		if(StringUtils.isNotBlank(J_xl_1)){
			query.setJ_xl_1(J_xl_1+" 00:00:00");
			params.append("&J_xl_1=").append(J_xl_1);
			request.setAttribute("J_xl_1", J_xl_1);
		}
		if(StringUtils.isNotBlank(J_xl_2)){
			query.setJ_xl_2(J_xl_2+" 23:59:59");
			params.append("&J_xl_2=").append(J_xl_2);
			request.setAttribute("J_xl_2", J_xl_2);
		}
		
		CompanyService service = new CompanyServiceImpl();
		Pagination pagination = service.selectFaultinfo3(query,repeaterMac,faultCode);
		pagination.pageView("/fireSystem/faultinfo3.do", params.toString());
		model.addAttribute("pagination", pagination);
		modelAndView.addAllObjects(model);
		modelAndView.setViewName("/WEB-INF/page/devicestates/faultinfo_list_three");
		return modelAndView;
	}
	
	@RequestMapping(value="/deviceState_list_electr_arc.do",method=RequestMethod.GET)
	public ModelAndView deviceState_list_electr_arc(HttpServletRequest request,Integer pageNo,SearchElectricInfo query){
		electricInfoService = new ElectricInfoServiceImpl();
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
		StringBuilder params = new StringBuilder();
		if(StringUtils.isNotBlank(devType)){
			query.setType(devType);
			params.append("&devType=").append(query.getType());
			request.setAttribute("devType", query.getType());
		}
		
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac.trim());
			params.append("&mac=").append(query.getMac());
			request.setAttribute("mac", query.getMac());
		}if (StringUtils.isNotBlank(status)) {
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
		mSmartControlDao = new SmartControlDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			InfoManagerDao infoDao = new InfoManagerDaoImpl();
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			
			allElectricInfoDao = new AllElectricInfoDaoImpl();
			Pagination pagination = electricInfoService.getElectricHuListWithPage(areaIds, query);
			pagination.pageView("/fireSystem/deviceState_list_electr_arc.do", params.toString());
			
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType( areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelMap.put("deviceNetState", deviceNetState);
			modelAndView.addAllObjects(modelMap);
			modelAndView.addObject("pagination", pagination);
		}
		modelAndView.setViewName("/WEB-INF/page/devicestates/deviceState_list_electr_arc");
		return modelAndView;
	} 
	
}
