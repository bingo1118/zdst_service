/**
 * 下午3:29:21
 */
package com.cloudfire.controller.water;

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

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.OperationDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.OperationDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.Operation;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.Water;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.entity.query.WaterQuery;
import com.cloudfire.myservice.WaterService;
import com.cloudfire.myservice.impl.WaterServiceImpl;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

import common.page.Pagination;

/**
 * @author cheng
 *2017-5-10
 *下午3:29:21
 */
@Controller
public class WaterInfoController {
	private AreaDao mAreaDao;
	private LoginDao mLoginDao;
	private SmartControlDao mSmartControlDao;
	private AlarmInfoDao mAlarmInfoDao;
	private WaterService waterService;
	
	
	@RequestMapping(value="/waterInfo_list_items.do",method=RequestMethod.GET)
	public ModelAndView waterInfo_list_items(HttpServletRequest request,HttpServletResponse response,String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		if(StringUtils.isBlank(currentId)) {
			//modelAndView.setViewName("/WEB-INF/page/login/login");  
			request.getRequestDispatcher("/WEB-INF/page/login/login.jsp").forward(request, response);
		}else{
			request.getSession().setAttribute("userId", currentId);
			modelAndView.setViewName("/WEB-INF/page/water/waterViewData");
		}
		return modelAndView;
	}	
	
	@RequestMapping(value="/water.do",method=RequestMethod.GET)
	public ModelAndView waterInfo(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		waterService = new WaterServiceImpl();
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
		String devType = request.getParameter("devType");
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if(StringUtils.isNotBlank(devType)){
			query.setType(devType);
			params.append("&devType=").append(query.getType());
			request.setAttribute("devType", query.getType());
		}
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
	
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			Pagination pagination = waterService.selectAllWaterListWithPage(areaIds, query);
			pagination.pageView("/fireSystem/water.do", params.toString());
			InfoManagerDao infoDao = new InfoManagerDaoImpl();
//				List<ShopTypeEntity> shopList = mSmartControlDao.getPlaceType(type, areaIds);
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
//				List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeater(type, areaIds);
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			model.addAttribute("pagination", pagination);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelMap.put("deviceNetState", deviceNetState);
			modelAndView.addAllObjects(modelMap);
		}
		if("10".equals(type)||"42".equals(type)||"125".equals(type)||(StringUtils.isNotBlank(query.getType())&&query.getType().equals("8"))){ //水压
			modelAndView.setViewName("/WEB-INF/page/water/waterView");
		}else if("18".equals(type)||(StringUtils.isNotBlank(query.getType())&&query.getType().equals("10"))){ //喷淋
			modelAndView.setViewName("/WEB-INF/page/water/waterView_Sprinkler");
		}else if("19".equals(type)||"124".equals(type)||(StringUtils.isNotBlank(query.getType())&&query.getType().equals("9"))){ //水位
			modelAndView.setViewName("/WEB-INF/page/water/waterLeve");
		} else if("15".equals(type)||(StringUtils.isNotBlank(query.getType())&&query.getType().equals("11"))){ //水浸
			modelAndView.setViewName("/WEB-INF/page/water/waterLeach");
		} else {
			modelAndView.setViewName("/WEB-INF/page/water/waterView");
		}
		return modelAndView;
	}
	
	//查询温湿度设备列表
	@RequestMapping(value="/thinfo.do",method=RequestMethod.GET)
	public ModelAndView thinfo(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		waterService = new WaterServiceImpl();
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
		String devType = request.getParameter("devType");
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if(StringUtils.isNotBlank(devType)){
			query.setType(devType);
			params.append("&devType=").append(query.getType());
			request.setAttribute("devType", query.getType());
		}
		
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
		}
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			Pagination pagination = waterService.selectAllThListWithPage(areaIds, query);
			pagination.pageView("/fireSystem/thinfo.do", params.toString());
			InfoManagerDao infoDao = new InfoManagerDaoImpl();
//				List<ShopTypeEntity> shopList = mSmartControlDao.getPlaceType(type, areaIds);
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
//				List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeater(type, areaIds);
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
			List<DeviceNetState> deviceNetState = mSmartControlDao.getDeviceNetState(areaIds);
			mAlarmInfoDao = new AlarmInfoDaoImpl();
			Map<String,List> modelMap = new HashMap<String,List>();
			model.addAttribute("pagination", pagination);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);
			modelMap.put("deviceNetState", deviceNetState);
			modelAndView.addAllObjects(modelMap);
		}
		if("25".equals(type)||(StringUtils.isNotBlank(query.getType())&&query.getType().equals("12"))){
			modelAndView.setViewName("/WEB-INF/page/water/thView");
		}else if("26".equals(type)||(StringUtils.isNotBlank(query.getType())&&query.getType().equals("13"))){
			modelAndView.setViewName("/WEB-INF/page/water/thView_Self");
		}
			
		return modelAndView;
	}
	
	/*@RequestMapping(value="/waterRecord1111111.do",method=RequestMethod.GET)
	public ModelAndView waterRecord1111(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		waterService = new WaterServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		if (pageNo == null) {
			pageNo = Pagination.cpn(pageNo);
		}
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String mac = request.getParameter("mac");
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac);
		}
		SmokeDao sd = new SmokeDaoImpl();
		String heartTime = sd.getHeartTimeByMac(mac);
		int deviceType =sd.getDeviceTypeByMac(mac);
	
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(query.getMac())) {
			params.append("&mac=").append(query.getMac());
			model.addAttribute("mac", query.getMac());
		}		
		
		Pagination pagination = waterService.selectAllWaterRecordWithPage(query);
		pagination.pageView("/fireSystem/waterRecord.do", params.toString());
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		model.addAttribute("pagination", pagination);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("heartTime", heartTime);
		modelAndView.setViewName("/WEB-INF/page/water/waterRecord");
		return modelAndView;
	}*/
	//无线水压探测器详情
	@RequestMapping(value="/waterRecord.do",method=RequestMethod.GET)
	public ModelAndView waterRecord(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		waterService = new WaterServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		if (pageNo == null) {
			pageNo = Pagination.cpn(pageNo);
		}
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String mac = request.getParameter("mac");
		String startTime = request.getParameter("J_xl_1");
		String endTime = request.getParameter("J_xl_2");
		String status = request.getParameter("status");
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac);
		}
		if (StringUtils.isNotBlank(startTime)) {
			query.setStartTime(startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			query.setEndTime(endTime);
		}
		if (StringUtils.isNotBlank(status)) {
			query.setDevictTypeName(status);
		}
		SmokeDao sd = new SmokeDaoImpl();
		String heartTime = sd.getHeartTimeByMac(mac);
		int deviceType =sd.getDeviceTypeByMac(mac);
	
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(query.getMac())) {
			params.append("&mac=").append(query.getMac());
			model.addAttribute("mac", query.getMac());
		}
		if (StringUtils.isNotBlank(query.getStartTime())) {
			params.append("&startTime=").append(query.getStartTime());
			model.addAttribute("startTime", query.getStartTime());
		}
		if (StringUtils.isNotBlank(query.getEndTime())) {
			params.append("&endTime=").append(query.getEndTime());
			model.addAttribute("endTime", query.getEndTime());
		}
		if (StringUtils.isNotBlank(query.getDevictTypeName())) {
			params.append("&status=").append(query.getDevictTypeName());
			model.addAttribute("status", query.getDevictTypeName());
		}
		Pagination pagination = waterService.selectAllWaterRecordWithPage(query);
		pagination.pageView("/fireSystem/waterRecord.do", params.toString());
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		model.addAttribute("pagination", pagination);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("heartTime", heartTime);
		modelAndView.setViewName("/WEB-INF/page/water/waterRecord");
		return modelAndView;
	}
	
	@RequestMapping(value="/thRecord.do",method=RequestMethod.GET)
	public ModelAndView thRecord(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		waterService = new WaterServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		if (pageNo == null) {
			pageNo = Pagination.cpn(pageNo);
		}
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String mac = request.getParameter("mac");
		String startTime = request.getParameter("J_xl_1");
		String endTime = request.getParameter("J_xl_2");
		
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac);
		}
		if (StringUtils.isNotBlank(startTime)) {
			query.setStartTime(startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			query.setEndTime(endTime);
		}
		
		SmokeDao sd = new SmokeDaoImpl();
		String heartTime = sd.getHeartTimeByMac(mac);
		int deviceType =sd.getDeviceTypeByMac(mac);
	
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(query.getMac())) {
			params.append("&mac=").append(query.getMac());
			model.addAttribute("mac", query.getMac());
		}		
		if (StringUtils.isNotBlank(query.getStartTime())) {
			params.append("&startTime=").append(query.getStartTime());
			model.addAttribute("startTime", query.getStartTime());
		}
		
		if (StringUtils.isNotBlank(query.getEndTime())) {
			params.append("&endTime=").append(query.getEndTime());
			model.addAttribute("endTime", query.getEndTime());
		}
		Pagination pagination = waterService.selectAllThRecordWithPage(query);
		pagination.pageView("/fireSystem/thRecord.do", params.toString());
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		model.addAttribute("pagination", pagination);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("heartTime", heartTime);
		modelAndView.setViewName("/WEB-INF/page/water/thRecord");
		return modelAndView;
	}
	
	@RequestMapping(value="/thRecord2.do",method=RequestMethod.GET)
	public ModelAndView thRecord2(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		waterService = new WaterServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		if (pageNo == null) {
			pageNo = Pagination.cpn(pageNo);
		}
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String mac = request.getParameter("mac");
		String startTime = request.getParameter("J_xl_1");
		String endTime = request.getParameter("J_xl_2");
		
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac);
		}
		if (StringUtils.isNotBlank(startTime)) {
			query.setStartTime(startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			query.setEndTime(endTime);
		}
		
		SmokeDao sd = new SmokeDaoImpl();
		String heartTime = sd.getHeartTimeByMac(mac);
		int deviceType =sd.getDeviceTypeByMac(mac);
	
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(query.getMac())) {
			params.append("&mac=").append(query.getMac());
			model.addAttribute("mac", query.getMac());
		}		
		if (StringUtils.isNotBlank(query.getStartTime())) {
			params.append("&startTime=").append(query.getStartTime());
			model.addAttribute("startTime", query.getStartTime());
		}
		
		if (StringUtils.isNotBlank(query.getEndTime())) {
			params.append("&endTime=").append(query.getEndTime());
			model.addAttribute("endTime", query.getEndTime());
		}
		Pagination pagination = waterService.selectAllThRecordWithPage(query);
		pagination.pageView("/fireSystem/thRecord.do", params.toString());
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		model.addAttribute("pagination", pagination);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("heartTime", heartTime);
		modelAndView.setViewName("/WEB-INF/page/water/thRecord2");
		return modelAndView;
	}
	
	@RequestMapping(value="/waterViewRecord.do",method=RequestMethod.POST)
	public ModelAndView waterViewRecord2(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView = waterViewRecord(request,query,model,pageNo);
		return modelAndView;
	}
	
	//无线水位探测器详情
	@RequestMapping(value="/waterViewRecord.do",method=RequestMethod.GET)
	public ModelAndView waterViewRecord(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		waterService = new WaterServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		if (pageNo == null) {
			pageNo = Pagination.cpn(pageNo);
		}
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String mac = request.getParameter("mac");
		String startTime = request.getParameter("J_xl_1");
		String endTime = request.getParameter("J_xl_2");
		String status = request.getParameter("status");
		
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac);
		}
		if (StringUtils.isNotBlank(startTime)) {
			query.setStartTime(startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			query.setEndTime(endTime);
		}
		if (StringUtils.isNotBlank(status)) {
			query.setDevictTypeName(status);
		}
		
		SmokeDao sd = new SmokeDaoImpl();
		String heartTime = sd.getHeartTimeByMac(mac);
		int deviceType =sd.getDeviceTypeByMac(mac);
		
		
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(query.getMac())) {
			params.append("&mac=").append(query.getMac());
			model.addAttribute("mac", query.getMac());
		}		
		if (StringUtils.isNotBlank(query.getStartTime())) {
			params.append("&startTime=").append(query.getStartTime());
			model.addAttribute("startTime", query.getStartTime());
		}
		
		if (StringUtils.isNotBlank(query.getEndTime())) {
			params.append("&endTime=").append(query.getEndTime());
			model.addAttribute("endTime", query.getEndTime());
		}
		
		if (StringUtils.isNotBlank(query.getDevictTypeName())) {
			params.append("&status=").append(query.getDevictTypeName());
			model.addAttribute("status", query.getDevictTypeName());
		}
		//Pagination pagination = waterService.selectAllWaterRecordWithPage(query);
		Pagination pagination = waterService.waterViewRecordPage(query);
		pagination.pageView("/fireSystem/waterViewRecord.do", params.toString());
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		model.addAttribute("pagination", pagination);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("heartTime", heartTime);
		modelAndView.setViewName("/WEB-INF/page/water/waterViewRecord");
		return modelAndView;
	}
	
	@RequestMapping(value="/waterRecord.do",method=RequestMethod.POST)
	public ModelAndView waterRecord2(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView = waterRecord(request,query,model,pageNo);
		return modelAndView;
	}
	
	@RequestMapping(value="/waterLeveRecord.do",method=RequestMethod.GET)
	public ModelAndView waterLeveRecord(HttpServletRequest request,HttpServletResponse response,WaterQuery query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		waterService = new WaterServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		if (pageNo == null) {
			pageNo = Pagination.cpn(pageNo);
		}
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String mac = request.getParameter("mac");
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac);
		}
		
		SmokeDao sd = new SmokeDaoImpl();
		String heartTime = sd.getHeartTimeByMac(mac);
		int deviceType =sd.getDeviceTypeByMac(mac);
		
		//将类型放到session域中
		//当前用户的id
		String currentId = (String) request.getSession().getAttribute("userId");
		//构建跳转到下一页参数
	
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(query.getMac())) {
			params.append("&mac=").append(query.getMac());
			model.addAttribute("mac", query.getMac());
		}		
		
		String privilege = (String) request.getSession().getAttribute("privilege");
		mAreaDao = new AreaDaoImpl();
		Pagination pagination = waterService.selectAllWaterRecordWithPage(query);
		pagination.pageView("/fireSystem/waterLeveRecord.do", params.toString());
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		Map<String,List> modelMap = new HashMap<String,List>();
		model.addAttribute("pagination", pagination);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("heartTime", heartTime);
		modelAndView.addAllObjects(modelMap);
		modelAndView.setViewName("/WEB-INF/page/water/waterLeveRecord");
		return modelAndView;
	}
	
	
	/**
	 * 跳转到设置温湿度阈值功能
	 * @return
	 */
	@RequestMapping(value="/thSettings.do",method=RequestMethod.GET)
	public ModelAndView thSettings(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		waterService = new WaterServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String mac = request.getParameter("mac");
		SmokeDao sd = new SmokeDaoImpl();
		SmokeBean smokeBean = new SmokeBean();
		Water water = new Water();
		if (StringUtils.isNotBlank(mac)) {
			smokeBean = sd.getSmokeByMac2(mac);
		}
		modelAndView.addObject("smoke", smokeBean);
		modelAndView.setViewName("/WEB-INF/page/water/thView_Setting");
		return modelAndView;
	}
	
	@RequestMapping(value="/waterLeveRecord.do",method=RequestMethod.POST)
	public ModelAndView waterLeveRecord2(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView = waterRecord(request,query,model,pageNo);
		return modelAndView;
	}
	
	@RequestMapping(value="/waterRecord_Sprinkler.do",method=RequestMethod.GET)
	public ModelAndView waterRecord_Sprinkler(HttpServletRequest request,HttpServletResponse response,WaterQuery query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		waterService = new WaterServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		if (pageNo == null) {
			pageNo = Pagination.cpn(pageNo);
		}
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String mac = request.getParameter("mac");
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac);
		}
		SmokeDao sd = new SmokeDaoImpl();
		String heartTime = sd.getHeartTimeByMac(mac);
		//将类型放到session域中
		//当前用户的id
		String currentId = (String) request.getSession().getAttribute("userId");
		//构建跳转到下一页参数
	
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(query.getMac())) {
			params.append("&mac=").append(query.getMac());
			model.addAttribute("mac", query.getMac());
		}		
		
		//如果用户未登陆，则跳转到登陆页面
		if(StringUtils.isBlank(currentId)) {
			//modelAndView.setViewName("/WEB-INF/page/login/login"); 
			request.getRequestDispatcher("/WEB-INF/page/login/login.jsp").forward(request, response);
		}else{
			mLoginDao = new LoginDaoImpl();
			LoginEntity mLoginEntity = mLoginDao.login(currentId);
			String privilege = mLoginEntity.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			Pagination pagination = waterService.selectAllWaterRecordWithPage(query);
			pagination.pageView("/fireSystem/waterRecord.do", params.toString());
			mAlarmInfoDao = new AlarmInfoDaoImpl();
			Map<String,List> modelMap = new HashMap<String,List>();
			model.addAttribute("pagination", pagination);
			model.addAttribute("heartTime", heartTime);
			modelAndView.addAllObjects(modelMap);
			modelAndView.setViewName("/WEB-INF/page/water/waterRecord_Sprinkler");
		}
		return modelAndView;
	}
	
	/**
	 * 跳转到设置水压阈值功能
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping(value="/waterWave_Value.do",method=RequestMethod.GET)
	public ModelAndView waterWave_Value(HttpServletRequest request,HttpServletResponse response,WaterQuery query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		waterService = new WaterServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String mac = request.getParameter("mac");
		SmokeDao sd = new SmokeDaoImpl();
		SmokeBean smokeBean = new SmokeBean();
		if (StringUtils.isNotBlank(mac)) {
			smokeBean = sd.getSmokeByMac(mac);
		}
		List<SmokeBean> smokeList = new ArrayList<SmokeBean>();
		smokeList.add(smokeBean);
		String currentId = (String) request.getSession().getAttribute("userId");
	
		//如果用户未登陆，则跳转到登陆页面
		if(StringUtils.isBlank(currentId)) {
			//modelAndView.setViewName("/WEB-INF/page/login/login"); 
			request.getRequestDispatcher("/WEB-INF/page/login/login.jsp").forward(request, response);
		}else{
			modelAndView.addObject("smokeBeans", smokeList);
			modelAndView.setViewName("/WEB-INF/page/water/waterSettings");
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/NBWaterDetail.do",method=RequestMethod.GET)//水压
	public ModelAndView NBWaterDetail(HttpServletRequest request,WaterQuery query,ModelMap model,Integer pageNo){
		waterService = new WaterServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		if (pageNo == null) {
			pageNo = Pagination.cpn(pageNo);
		}
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String mac = request.getParameter("mac");
		String deviceType = request.getParameter("type");
		String startTime = request.getParameter("J_xl_1");
		String endTime = request.getParameter("J_xl_2");
		String status = request.getParameter("status");
		
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac);
		}
		if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
		}
		if (StringUtils.isNotBlank(startTime)) {
			query.setStartTime(startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			query.setEndTime(endTime);
		}
		if (StringUtils.isNotBlank(status)) {
			query.setDevictTypeName(status);
		}
		
		SmokeDao sd = new SmokeDaoImpl();
		String heartTime = sd.getHeartTimeByMac(mac);
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(query.getMac())) {
			params.append("&mac=").append(query.getMac());
			model.addAttribute("mac", query.getMac());
		}
		if (StringUtils.isNotBlank(query.getDeviceType())) {
			params.append("&type=").append(query.getDeviceType());
			model.addAttribute("type", query.getDeviceType());
		}
		if (StringUtils.isNotBlank(query.getStartTime())) {
			params.append("&startTime=").append(query.getStartTime());
			model.addAttribute("startTime", query.getStartTime());
		}
		if (StringUtils.isNotBlank(query.getEndTime())) {
			params.append("&endTime=").append(query.getEndTime());
			model.addAttribute("endTime", query.getEndTime());
		}
		if (StringUtils.isNotBlank(query.getDevictTypeName())) {
			params.append("&status=").append(query.getDevictTypeName());
			model.addAttribute("status", query.getDevictTypeName());
		}
		Pagination pagination = waterService.NBWaterDetail(query);
		pagination.pageView("/fireSystem/NBWaterDetail.do", params.toString());
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		model.addAttribute("pagination", pagination);
		model.addAttribute("deviceType", deviceType);
		model.addAttribute("heartTime", heartTime);
		int devType1 = Integer.valueOf(query.getDeviceType());
		switch (devType1) {   
			case 42:
				modelAndView.setViewName("/WEB-INF/page/devicestates/NBWaterDetail");//水压
			break;
			case 46:
				modelAndView.setViewName("/WEB-INF/page/devicestates/NBWaterDetail1");//水位
			break;
		}
		return modelAndView;
	}
	
	
	
	/**
	 * 对单个万科温湿度设备的参数进行设置。
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/updateWaterSettings2.do",method=RequestMethod.GET)
	public void updateWaterSettings2(HttpServletRequest request,HttpServletResponse response) {
		String waterMac = request.getParameter("waterMac");
		//int deviceType = Integer.parseInt(request.getParameter("deviceType"));
		int waterType = Integer.parseInt( request.getParameter("waterType"));
		String waterValueStr = request.getParameter("watervalue");
		//SmokeDao sd = new SmokeDaoImpl();
		//SmokeBean smokeBean = sd.getSmokeByMac2(waterMac);
		int waveTime =0; 
		int waveValue=0; 
		String highGage = "";
		String lowGage = "";
		String highGage2 = "";
		String lowGage2 = "";
		String userid =  (String) request.getSession().getAttribute("userId");
		
		WaterInfoDao wid = new WaterInfoDaoImpl();
		OperationDao od = new OperationDaoImpl();
		
		//创建操作对象记录操作
    	Operation operation = new Operation();
    	operation.setOperator(userid);
    	operation.setObject(waterMac);
    	operation.setTime(GetTime.ConvertTimeByLong());
    	operation.setContent(waterValueStr); //存入新操作的内容
    	int optType = 0;
    	
    	String result = "设置失败，请重新设置";
    	int state = 0 ; //操作状态，默认为0，失败；1为成功
    	boolean bool = false;
		switch(waterType){ //0:温度最高值;1:温度最低值;2:湿度最高值;3:湿度最小值;
		case 0:
			highGage = waterValueStr;
			optType = 11;
			if(highGage != null && highGage != ""){
				
				//将操作入库
				operation.setOptype(optType);
				od.saveOperation(operation);
				
				bool = Utils.updateSettingsByMac(waterMac, waveTime, waveValue,(int)(Float.parseFloat(highGage)*10),32767,32767,32767);
				if(bool){
					od.updateOperationState(waterMac, 1);
				}
				//延时
				delay();
				//获取操作数据库中的状态0，待定 1，成功  2，失败
				state = od.getState(waterMac,optType);
				if (state == 1) {
					wid.updateGage(highGage,waterMac,308); //更新高温阈值
					result = "设置成功,请刷新页面";
				}else if(state == 0) { //等待后操作状态还是待定，则更新这次操作为失败
					od.updateOperationState(waterMac, 2);
				}
			}
			break;
		case 1:
			lowGage =  waterValueStr;
			optType = 12;
			if(lowGage != null && lowGage != ""){
				
				//将操作入库
				operation.setOptype(optType);
				od.saveOperation(operation);
				bool = Utils.updateSettingsByMac(waterMac, waveTime, waveValue,32767,(int)(Float.parseFloat(lowGage)*10),32767,32767);
				if(bool){
					od.updateOperationState(waterMac, 1);
				}
				//延时
				delay();
				//获取操作数据库中的状态0，待定 1，成功  2，失败
				state = od.getState(waterMac,optType);
				if (state == 1) {
					wid.updateGage(lowGage,waterMac,307); //更新低温阈值
					result = "设置成功,请刷新页面";
				}else if(state == 0) { //等待后操作状态还是待定，则更新这次操作为失败
					od.updateOperationState(waterMac, 2);
				}
			}
			break;
		case 2:
			highGage2= waterValueStr;
			optType = 13;
			if(highGage2 != null && highGage2 != ""){
				
				//将操作入库
				operation.setOptype(optType);
				od.saveOperation(operation);
				bool = Utils.updateSettingsByMac(waterMac, waveTime, waveValue,32767,32767,(int)(Float.parseFloat(highGage2)*10),32767);
				if(bool){
					od.updateOperationState(waterMac, 1);
				}
				//延时
				delay();
				//获取操作数据库中的状态0，待定 1，成功  2，失败
				state = od.getState(waterMac,optType);
				if (state == 1) {
					wid.updateGage(highGage2,waterMac,408); //高湿度
					result = "设置成功,请刷新页面";
				}else if(state == 0) { //等待后操作状态还是待定，则更新这次操作为失败
					od.updateOperationState(waterMac, 2);
				}
			}
			break;
		case 3:
			lowGage2 =  waterValueStr;
			optType = 14;
			if(lowGage2 != null && lowGage2 != ""){
				
				//将操作入库
				operation.setOptype(optType);
				od.saveOperation(operation);
				bool = Utils.updateSettingsByMac( waterMac, waveTime, waveValue,32767,32767,32767,(int)(Float.parseFloat(lowGage2)*10));
				if(bool){
					od.updateOperationState(waterMac, 1);
				}
				//延时
				delay();
				//获取操作数据库中的状态0，待定 1，成功  2，失败
				state = od.getState(waterMac,optType);
				if (state == 1) {
					wid.updateGage(lowGage2,waterMac,407); //低湿度
					result = "设置成功,请刷新页面";
				}else if(state == 0) { //等待后操作状态还是待定，则更新这次操作为失败
					od.updateOperationState(waterMac, 2);
				}
			}
			break;
		}
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//当前线程延时5s再去查操作记录表的状态
		public void delay(){
			try {
				Thread.currentThread().sleep(8000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
}
