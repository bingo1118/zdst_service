package com.cloudfire.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.StatisticDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AllSmokeDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.DevicesDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.dao.impl.StatisticDaoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.dao.query.SearchAnalysisQuery;
import com.cloudfire.dao.query.impl.SearchAnalysisQueryImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.DealOkAlarmEntity;
import com.cloudfire.entity.Devices;
import com.cloudfire.entity.FireBean;
import com.cloudfire.entity.IndexBean;
import com.cloudfire.entity.SearchDto;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.StatisticAnalysisEntity;
import com.cloudfire.entity.StatisticBean;
import com.cloudfire.entity.Water;
import com.cloudfire.entity.WaterBean;
import com.cloudfire.entity.WaterEntity;
import com.cloudfire.entity.query.SearchAnalysisInfo;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;
import com.cloudfire.until.WriteJson;

@Controller
public class StatisticAnalysis_list {
	
	
	private AreaDao mAreaDao;
	private AlarmInfoDao mAlarmInfoDao;
	private SmartControlDao mSmartControlDao;
	private SearchAnalysisQuery mSearchAnalysisQuery;
	private SmokeDao smokeDao;
	
	@RequestMapping(value="/statisticAnalysis_list_items.do",method=RequestMethod.GET)
	public ModelAndView statisticAnalysis_list_items(HttpServletRequest request,String currentId){
		ModelAndView modelAndView =  new ModelAndView();
		modelAndView.setViewName("/WEB-INF/page/main/statisticAnalysis_list_items");
		return modelAndView;
	} 
	@RequestMapping(value="/statisticAnalysisSearch_list_items.do",method=RequestMethod.GET)
	public ModelAndView statisticAnalysisSearch(HttpServletRequest request,String currentId,HttpServletResponse response){
			CountValue cv = new CountValue();
			DeviceDao dd = new DevicesDaoImpl();
			SearchDto dto=new SearchDto();
			dto.setCompanyName(request.getParameter("companyName"));
			dto.setFloor1(request.getParameter("floor1"));
			dto.setFloor2(request.getParameter("floor2"));
			dto.setFire1(request.getParameter("fire1"));
			dto.setFire2(request.getParameter("fire2"));
			dto.setMacStatus(request.getParameter("macStatus"));
			cv=dd.getCountByMacSearch(dto);
			JSONObject object=JSONObject.fromObject(cv);
			try {
				response.setContentType("text/html;charset=utf-8");
				response.getWriter().write(object.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			return null;
	}
	
	
	@RequestMapping(value="/getStatisticAnalysis_list_items.do",method=RequestMethod.GET)
	public ModelAndView getStatisticAnalysis_list_items(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		mSmartControlDao = new SmartControlDaoImpl();
		
		String devTypestr = request.getParameter("devId");
		//List<DeviceType> deviceTypes = mSmartControlDao.getDeviceTypeAndNameByType(devTypestr);
		String deviceName = mSmartControlDao.getDeviceNameByType(devTypestr);
//		if(deviceTypes!=null&&deviceTypes.size()>0){
//			deviceName = deviceTypes.get(0).getDevName();
//		}
		request.getSession().setAttribute("devName", deviceName);
		request.getSession().setAttribute("devId", devTypestr);
		
		//F67BA5D4   73200002   72250467
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
		
		List<DealOkAlarmEntity> deviceTypeAndName = mSmartControlDao.getDeviceTypeAndName(areaIds);
//				List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeaterAndSmokeNumber(areaIds, devTypestr);
		List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeaterNoType( areaIds);
		/*List<SearchAnalysisInfo> info = mSearchAnalysisQuery.getAnalysisInfoData(areaIds, devTypestr);*/
		List<String> allYear = mSearchAnalysisQuery.getAllYear();
		List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areaIds);				
		modelAndView.addObject("alarmInfos", lists);
	
		/** 设备的状态和名字 */
		modelAndView.addObject("deviceAndName", deviceTypeAndName);
		
		/** 添加区域 */
		modelAndView.addObject("areaAndName", areaAndName);
		
		/**获取所有的年份 */
		modelAndView.addObject("allYear", allYear);
		
		
		modelAndView.setViewName("/WEB-INF/page/analysis/statisticAnalysis_list_items1");
		return modelAndView;
	}
	
	@RequestMapping(value="/waterAnalysis.do",method=RequestMethod.GET)
	public ModelAndView waterAnalysis(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modelAndView =  new ModelAndView();
		String currentId = (String) request.getSession().getAttribute("userId");
		String privilege =(String) request.getSession().getAttribute("privilege");
		
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaIds = mAreaDao.getAllWaterAreaByUserId(currentId, privilege);
		
		modelAndView.addObject("areaAndName", areaIds);
		modelAndView.setViewName("/WEB-INF/page/analysis/waterAnalysis");
		return modelAndView;
	}
	
	@RequestMapping(value="/waterAnalysis_list_items.do",method=RequestMethod.GET)
	public ModelAndView waterAnalysis_list_items(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modelAndView =  new ModelAndView();
		String currentId = (String) request.getSession().getAttribute("userId");
		String privilege =(String) request.getSession().getAttribute("privilege");
		
		//获取用户管理的水压设备所在区域列表
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areas =  mAreaDao.getWaterAreaByUserId(currentId, privilege);
		
		//获取第一个区域的设备列表
		WaterInfoDao wid = new WaterInfoDaoImpl();
		List<WaterEntity>  waters = null;
		if (areas != null && areas.size() > 0)
			waters = wid.getWatersByAreaid(areas.get(0).getAreaId()+"");
		
		long endTime = System.currentTimeMillis();
		long startTime = endTime-24*60*60*1000;
		
		List<Water> records = null;
		if(waters!=null && waters.size() > 0)
			records = wid.getWaterRecords(GetTime.getTimeByLong(startTime), GetTime.getTimeByLong(endTime),waters.get(0).getWaterMac());
		
		
		Map<String,List<?>> modelMap = new HashMap<String, List<?>>();
		
		modelMap.put("areaAndName", areas);
		modelMap.put("waters", waters);
		modelMap.put("records", records);
		modelAndView.addAllObjects(modelMap);
		modelAndView.setViewName("/WEB-INF/page/analysis/waterAnalysis_list_items");
		return modelAndView;
	}
	
	@RequestMapping(value="/waterLeveAnalysis_list_items.do",method=RequestMethod.GET)
	public ModelAndView waterLeveAnalysis_list_items(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modelAndView =  new ModelAndView();
		String currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater>	areaIds = mAreaDao.getWaterLeveAreaByUserId(currentId, privilege);
		
		//获取第一个区域的设备列表
		WaterInfoDao wid = new WaterInfoDaoImpl();
		List<WaterEntity>  waters = null;
		if (areaIds != null && areaIds.size() > 0)
			waters = wid.getWaterLevelsByAreaid(areaIds.get(0).getAreaId()+"");
		
		long endTime = System.currentTimeMillis();
		long startTime = endTime-24*60*60*1000;
		
		List<Water> records = null;
		if(waters!=null && waters.size() > 0)
			records = wid.getWaterRecords(GetTime.getTimeByLong(startTime), GetTime.getTimeByLong(endTime),waters.get(0).getWaterMac());
		
		
		Map<String,List<?>> modelMap = new HashMap<String, List<?>>();
		
		modelMap.put("areaAndName", areaIds);
		modelMap.put("waters", waters);
		modelMap.put("records", records);
		
		modelAndView.addAllObjects(modelMap);
		modelAndView.setViewName("/WEB-INF/page/analysis/waterLeveAnalysis_list_items");
		return modelAndView;
	}
	
	@RequestMapping(value="/waterStatistic.do",method=RequestMethod.GET)
	public void waterStatistic(HttpServletRequest request,HttpServletResponse response){
		String areaId = request.getParameter("areaid");
		String startTime = request.getParameter("start_Time")+" 00:00:00";
		String endTime = request.getParameter("end_Time")+" 00:00:00";
		if(areaId == null || "0".equals(areaId)){
			areaId = "";
			String userId = (String) request.getSession().getAttribute("userId");
			String privilege =  (String) request.getSession().getAttribute("privilege");
			List<AreaAndRepeater> areaIds = mAreaDao.getAllWaterAreaByUserId(userId,privilege);
			if(areaIds != null){
				for(int i=0;i<areaIds.size() - 1;i++){
					areaId += areaIds.get(i).getAreaId()+",";
				}
				areaId+=areaIds.get(areaIds.size() - 1).getAreaId();
			}
		}
		WaterInfoDao wid = new WaterInfoDaoImpl();
		List<WaterBean>  lstWater = wid.getWaterStatistic2(startTime, endTime,areaId);
		WriteJson json = new WriteJson();
		String object = json.getJsonData(lstWater);
		try {
			response.getWriter().write(object.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/waterInfoAnalysis.do",method=RequestMethod.GET)
	public void waterInfoAnalysis(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String waterMac = request.getParameter("waterMac");
		String start_Time = request.getParameter("start_Time");
		String end_Time = request.getParameter("end_Time");
		String areaid = request.getParameter("areaid");
		long timel = System.currentTimeMillis();
		long nowtime = timel-24*60*60*1000;
		if(StringUtils.isBlank(start_Time)){
			start_Time = GetTime.getTimeByLong(nowtime);
		} else {
			start_Time += " 00:00:00";
		}
		if(StringUtils.isBlank(end_Time)){
			end_Time = GetTime.getTimeByLong(timel);
		} else {
			end_Time += " 24:00:00";
		}
		
		String currentId = (String) request.getSession().getAttribute("userId");
		String privilege =(String) request.getSession().getAttribute("privilege");
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areas =  mAreaDao.getWaterAreaByUserId(currentId, privilege);
		if(StringUtils.isBlank(areaid) || "0".equals(areaid)){
			areaid = areas.get(0).getAreaId()+"";
		}
		WaterInfoDao wid = new WaterInfoDaoImpl();
		//List<WaterEntity>  waters = wid.getWatersByAreaid(areaid);
		if(wid.getWatersByAreaid(areaid) == null) {
			response.getWriter().write("{\"error\":\"0\"}");
		}else{
			List<WaterEntity>  waters = wid.getWatersByAreaid(areaid);
			if(StringUtils.isBlank(waterMac))
				waterMac = waters.get(0).getWaterMac();
			
			mSmartControlDao = new SmartControlDaoImpl();
			List<Water> records = mSmartControlDao.getWaterInfo(waterMac, start_Time, end_Time);
//			List<WaterEntity> waterEntityList =  mSmartControlDao.getWaterInfoAll(areaid,waterMac,start_Time,end_Time);
			waters.get(0).setWaterList(records);
			WriteJson json = new WriteJson();
			String object = json.getJsonData(waters);
			response.getWriter().write(object);
		}
		
	}
	
	@RequestMapping(value="/waterRecordInfo.do",method=RequestMethod.GET)
	public void waterRecordInfo(HttpServletRequest request,HttpServletResponse response){
		String waterMac = request.getParameter("waterMac");
		String start_Time = request.getParameter("start_Time");
		String end_Time = request.getParameter("end_Time");
		
		long timel = System.currentTimeMillis();
		long nowtime = timel-24*60*60*1000;
		if(StringUtils.isBlank(start_Time)){
			start_Time = GetTime.getTimeByLong(nowtime);
		} else {
			start_Time += " 00:00:00";
		}
		if(StringUtils.isBlank(end_Time)){
			end_Time = GetTime.getTimeByLong(timel);
		} else {
			end_Time += " 24:00:00";
		}
		
		mSmartControlDao = new SmartControlDaoImpl();
		List<Water> records = mSmartControlDao.getWaterInfo(waterMac, start_Time, end_Time);
		
		WriteJson json = new WriteJson();
		String object = json.getJsonData(records);
		try {
			response.getWriter().write(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/waterLeveInfoAnalysis.do",method=RequestMethod.GET)
	public void waterLeveInfoAnalysis(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String waterMac = request.getParameter("waterMac");
		String start_Time = request.getParameter("start_Time");
		String end_Time = request.getParameter("end_Time");
		String areaid = request.getParameter("areaid");
		
		long timel = System.currentTimeMillis();
		long nowtime = timel-24*60*60*1000;
		if(StringUtils.isBlank(start_Time)){
			start_Time = GetTime.getTimeByLong(nowtime);
		} else {
			start_Time += " 00:00:00";
		}
		if(StringUtils.isBlank(end_Time)){
			end_Time = GetTime.getTimeByLong(timel);
		} else {
			end_Time += " 24:00:00";
		}
		
		String currentId = (String) request.getSession().getAttribute("userId");
		String privilege =(String) request.getSession().getAttribute("privilege");
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areas =  mAreaDao.getWaterLeveAreaByUserId(currentId, privilege);
		if(StringUtils.isBlank(areaid) || "0".equals(areaid)){
			areaid = areas.get(0).getAreaId()+"";
		}

		WaterInfoDao wid = new WaterInfoDaoImpl();
		//List<WaterEntity>  waters = wid.getWaterLevelsByAreaid(areaid);
		if(wid.getWaterLevelsByAreaid(areaid) == null) {
			response.getWriter().write("{\"error\":\"0\"}");
		}else{
			List<WaterEntity>  waters = wid.getWaterLevelsByAreaid(areaid);
			if(StringUtils.isBlank(waterMac)) {
				waterMac = waters.get(0).getWaterMac();
			}
			mSmartControlDao = new SmartControlDaoImpl();
			List<Water> records = mSmartControlDao.getWaterInfo(waterMac, start_Time, end_Time);
			waters.get(0).setWaterList(records);
			WriteJson json = new WriteJson();
			String object = json.getJsonData(waters);
			response.getWriter().write(object);
		}
	}
	
	
	@RequestMapping(value="/getStatisticAnalysis5_list_items.do",method=RequestMethod.GET)
	public ModelAndView getStatisticAnalysis5_list_items(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modelAndView =  new ModelAndView();
		mSmartControlDao = new SmartControlDaoImpl();
		
		String devTypestr = request.getParameter("devId");
		String deviceName= mSmartControlDao.getDeviceNameByType(devTypestr);
		request.getSession().setAttribute("devName", deviceName);
		request.getSession().setAttribute("devId", devTypestr);
		
		//F67BA5D4   73200002   72250467
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
		
		if (areaIds !=null){
			List<DealOkAlarmEntity> deviceTypeAndName = mSmartControlDao.getDeviceTypeAndName(areaIds);
			List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeaterAndSmokeNumber(areaIds, devTypestr);
			/*List<SearchAnalysisInfo> info = mSearchAnalysisQuery.getAnalysisInfoData(areaIds, devTypestr);*/
			List<String> allYear = mSearchAnalysisQuery.getAllYear();
			List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areaIds);				
			modelAndView.addObject("alarmInfos", lists);
		
			/** 设备的状态和名字 */
			modelAndView.addObject("deviceAndName", deviceTypeAndName);
			
			/** 添加区域 */
			modelAndView.addObject("areaAndName", areaAndName);
			
			/**获取所有的年份 */
			modelAndView.addObject("allYear", allYear);
		}
		
		modelAndView.setViewName("/WEB-INF/page/analysis/electricAndfireAnalysis");
		return modelAndView;
	}
	
	//返回了所有的在线统计，以及按区域的在线统计。
	@RequestMapping(value="/getAnalysis.do",method=RequestMethod.GET)
	public void getAnalysis(String userId,HttpServletRequest request,HttpServletResponse response) throws IOException {
		userId = (String) request.getSession().getAttribute("userId");
		SearchAnalysisQuery dao = new SearchAnalysisQueryImpl();
		List<SearchAnalysisInfo> list = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		String privilege = (String) request.getSession().getAttribute("privilege");
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		if(areaIds==null) {
			response.getWriter().write("{\"analysisCheck\":\"1\"}");
		}else{
			/*CountValue cv = new CountValue();
			DeviceDao dd = new DevicesDaoImpl();
			if(privilege == "4"||"4".equals(privilege)){
				cv = dd.getCount(null);
			}else {
				cv = dd.getCountByPrivilege(userId,null);
			}*/
			
			list = dao.getAnalysisInfoData(areaIds);
			if (list.size()>0 && list !=null) {
				smokeDao = new SmokeDaoImpl();
				int count = smokeDao.queryCountCustomerSmoke();
				if(privilege.equals("4")){
					for (int i = 0; i < list.size(); i++) {
						list.get(i).setSmokeNumber(list.get(i).getSmokeNumber()+count);
						list.get(i).setOnLineNumber(list.get(i).getOnLineNumber()+count);
					}
				}
				WriteJson json = new WriteJson();
				String object = json.getJsonData(list);
				System.out.println("==>>>"+object);
				try {
					response.setContentType("text/html;charset=utf-8");
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		
	}
	
	
	/**
	 * 报警统计数据
	 * 1.按区域的报警统计数据
	 * 2.电气的报警统计数据
	 * 3.火警的报警统计
	 * 4.水系统的报警统计数据
	 */
	
	@RequestMapping(value = "getAlarmStatistic.do", method = RequestMethod.GET)
	public void getAlarmStatistic(HttpServletRequest request, HttpServletResponse response) {
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		IndexBean  index = new IndexBean();
		//根据区域id获取报警类型及报警次数统计
		List<StatisticBean> lstSb = new ArrayList<StatisticBean>();
		StatisticDao sd = new StatisticDaoImpl();
		
//		for (String areaId : areaIds) {
//			int area = Integer.parseInt(areaId);
			
//			StatisticBean sb = sd.getStatistic2(areaId);
//			lstSb.add(sb);
//		}
		
		
		int[] waters = {10,15,18,19}; //水系统的设备类型id
		//根据区域id获取报警类型及报警次数统计
		List<StatisticBean> lstSb2 = new ArrayList<StatisticBean>();
//		StatisticDao sd = new StatisticDaoImpl();
		for(int water : waters ){
			StatisticBean sb = sd.getStatistic(water,areaIds);
			lstSb2.add(sb);
		}
		
		//电气近两天按时间段统计的报警次数
		Calendar calendar = Calendar.getInstance();
		List<Integer> today = sd.getStatistic3(calendar,1, areaIds);
		calendar.add(Calendar.DATE, -1);
		List<Integer> yesterday = sd.getStatistic3(calendar, 0,areaIds);
		
//		DeviceDao dd = new DevicesDaoImpl();
//		List<Integer> fires = dd.getAllFire(areaIds);
		//根据区域id获取报警类型及报警次数统计
		List<FireBean> lstFb =sd.getAlarmCountByDeviceType(areaIds);
//		for(int fire : fires ){
//			FireBean sb = sd.getAlarmCountByDeviceType(fire,areaIds);
//			lstFb.add(sb);
//		}
		
		index.setAreaSta(lstSb);
		index.setWaterSta(lstSb2);
		index.setToday(today);
		index.setYesterday(yesterday);
		index.setFireSta(lstFb);
		//将统计结果以json字符串的形式返回到前端
//		WriteJson wj = new WriteJson();
//		String jsonStr = wj.getJsonData(lstSb); //参数是一个List对象
		
		JSONObject object = JSONObject.fromObject(index); //参数是一个objec对象,不能是集合对象
//		JSONArray array = JSONArray.fromObject(areaIds); //参数是一个List对象，
//		String jsonStr2 = array.toString();
		
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(object.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "getWaterStatistic.do", method = RequestMethod.GET)
	public void getWaterStatistic(HttpServletRequest request, HttpServletResponse response) {
		String userId = (String)request.getSession().getAttribute("userId");
//		String userId = request.getParameter("userId");
//		String privilege = login.getPrivilege() + "";
		String privilege = (String)request.getSession().getAttribute("privilege");
//		if(login.getPrivilege()==4){
//			privilege = "10";
//		}else {
//		}
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		
		SmokeDao sd2 = new SmokeDaoImpl();
		List<Integer> waters = sd2.getDevices(userId, Integer.parseInt(privilege));
//		int[] waters = {10,15,18,19,1,2,5,7,8,9,11,12,13,14,16,17,20,21,31,41,42,124,125,126}; //水系统的设备类型id
		
		//根据区域id获取报警类型及报警次数统计
		List<StatisticBean> lstSb = new ArrayList<StatisticBean>();
		StatisticDao sd = new StatisticDaoImpl();
		for(int water : waters ){
			StatisticBean sb = sd.getStatistic(water,areaIds);
			lstSb.add(sb);
		}
		
		//将统计结果以json字符串的形式返回到前端
		WriteJson wj = new WriteJson();
		String jsonStr = wj.getJsonData(lstSb); //参数是一个List对象
		
//		JSONObject object = JSONObject.fromObject(areaIds); //参数是一个objec对象,不能是集合对象
//		JSONArray array = JSONArray.fromObject(areaIds); //参数是一个List对象，
//		String jsonStr2 = array.toString();
		
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "getElectricStatistic.do", method = RequestMethod.GET)
	public void getElectricStatistic(HttpServletRequest request, HttpServletResponse response) {
		String privilege =  (String)request.getSession().getAttribute("privilege");
		if(privilege=="4"){
			privilege = "10";
		}
		String flag = request.getParameter("flag");
		
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		
//		List<StatisticBean> lstSb = new ArrayList<StatisticBean>();
		StatisticDao sd = new StatisticDaoImpl();
		
		Calendar calendar = Calendar.getInstance();
		List<Integer> today = null;
		if (!"y".equals(flag)) 
			today = sd.getStatistic3(calendar,1, areaIds);
		else {
			calendar.add(Calendar.DATE, -1);
			today =  sd.getStatistic3(calendar, 0,areaIds);
		}
		
		
		//将统计结果以json字符串的形式返回到前端
		WriteJson wj = new WriteJson();
		String jsonStr = wj.getJsonData(today); //参数是一个List对象
//		String jsonStr = "{'today':"+today+",yesterday:"+yesterday+"}";
		
//		JSONObject object = JSONObject.fromObject(today); //参数是一个objec对象,不能是集合对象
//		JSONArray array = JSONArray.fromObject(areaIds); //参数是一个List对象，
//		String jsonStr2 = array.toString();
//		String jsonStr2 = object.toString();
		
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(jsonStr);
//			Date d3 = new Date();
//			System.out.println("eleccostTime2:"+(d3.getTime()-d0.getTime()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "getFireStatistic.do", method = RequestMethod.GET)
	public void getFireStatistic(HttpServletRequest request, HttpServletResponse response) {
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		
//		DeviceDao dd = new DevicesDaoImpl();
//		List<Integer> fires = dd.getAllFire(areaIds);
		//根据区域id获取报警类型及报警次数统计
		StatisticDao sd = new StatisticDaoImpl();
		List<FireBean> lstFb =sd.getAlarmCountByDeviceType(areaIds);
//		for(int fire : fires ){
//			FireBean sb = sd.getAlarmCountByDeviceType(fire,areaIds);
//			lstFb.add(sb);
//		}

		//将统计结果以json字符串的形式返回到前端
		WriteJson wj = new WriteJson();
		String jsonStr = wj.getJsonData(lstFb); //参数是一个List对象
		
//		JSONObject object = JSONObject.fromObject(areaIds); //参数是一个objec对象,不能是集合对象
//		JSONArray array = JSONArray.fromObject(areaIds); //参数是一个List对象，
//		String jsonStr2 = array.toString();
		
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(jsonStr);
//			Date d3 = new Date();
//			System.out.println("FirecostTime2:"+(d3.getTime()-d0.getTime()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//按地区统计设备的在线数，离线数。
	@RequestMapping(value = "getAreaStatistic.do", method = RequestMethod.GET)
	public void getAreaStatistic(HttpServletRequest request, HttpServletResponse response) {
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		
		List<StatisticAnalysisEntity> sae =null;
		if (areaIds != null) {  //只在用户拥有区域的时候查询
			StatisticDao sd = new StatisticDaoImpl();
			 sae = sd.getStatistNum2(areaIds);
		}
		/*List<StatisticBean> lstSb = new ArrayList<StatisticBean>();
		
		
		for (String areaId : areaIds) {
			StatisticBean sb = sd.getStatistic2(areaId);
			lstSb.add(sb);
		}*/
		//将统计结果以json字符串的形式返回到前端
		WriteJson wj = new WriteJson();
		String jsonStr = wj.getJsonData(sae); //参数是一个List对象
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 用于右饼图跳转
	 * 根据地区id和设备状态返回设备列表
	 */
	@RequestMapping(value="getSmokeList.do",method=RequestMethod.GET)
	public void getSmokeList(HttpServletRequest request, HttpServletResponse response){
		String areaId = request.getParameter("areaId");
		String state = request.getParameter("state");
		AllSmokeDao asd = new AllSmokeDaoImpl();
		List<SmokeBean> listSmokes = null;
		if(state!=null&&!"".equals(state))
			listSmokes = asd.getSmokeList(areaId, state);
		else
			listSmokes = asd.getSmokeList(areaId);
		WriteJson wj = new WriteJson();
		String jsonStr = wj.getJsonData(listSmokes); //参数是一个List对象
		
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 用于首页左二表跳转
	 * 根据设备类型和设备状态返回设备列表 fg 0表示为空，1表示在线，2表示异常，即离线+报警
	 */
	@RequestMapping(value="getSmokeLists.do",method=RequestMethod.GET)
	public void getSmokeLists(HttpServletRequest request, HttpServletResponse response){
		String deviceType = request.getParameter("deviceType");
		int dt = 0, fg = 0;
		if(deviceType!=null&&deviceType.length() > 0){
			dt = Integer.parseInt(deviceType); 
		}
		String flag = request.getParameter("flag");
		if (flag!=null&&flag.length()>0) {
			fg = Integer.parseInt(flag);
		}
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		AllSmokeDao asd = new AllSmokeDaoImpl();
		List<SmokeBean> listSmokes = asd.getSmokeList(dt, fg,areaIds);
		
		WriteJson wj = new WriteJson();
		String jsonStr = wj.getJsonData(listSmokes); //参数是一个List对象
		
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * 用于获取所有设备信息
	 */
	@RequestMapping(value="getAllSmokeList.do",method=RequestMethod.GET)
	public void getAllSmokeList(HttpServletRequest request, HttpServletResponse response){
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		List<SmokeBean> listSmokes = null;
		if (areaIds != null){
			AllSmokeDao asd = new AllSmokeDaoImpl();
			listSmokes = asd.getSmokeList(0, 0, areaIds);
		}
		
		WriteJson wj = new WriteJson();
		String jsonStr = wj.getJsonData(listSmokes); //参数是一个List对象
		
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	//新的首页数据接口
	//1.getDeviceNumOfDeviceType   获取各类型的设备数量
	//2.getAlarmNumOfDeviceType  获取各类型的真实报警数
	//3.getAllDeviceState.do  获取各状态的设备数量统计，及低电压的设备数量，故障主机数量
	@RequestMapping(value="getDeviceNumOfDeviceType.do",method=RequestMethod.GET)
	public void getDeviceNumOfDeviceType(HttpServletRequest request, HttpServletResponse response){
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		
		DeviceDao dd = new DevicesDaoImpl();
		List<Devices> deviceNums = dd.getDeviceNums(areaIds);
		
		WriteJson wj = new WriteJson();
		String jsonStr = wj.getJsonData(deviceNums); //参数是一个List对象
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value="getAlarmNumOfDeviceType.do",method=RequestMethod.GET)
	public void getAlarmNumOfDeviceType(HttpServletRequest request, HttpServletResponse response){
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		
		DeviceDao dd = new DevicesDaoImpl();
		List<Devices> deviceNums = dd.getAlarmNums(areaIds);
		
		WriteJson wj = new WriteJson();
		String jsonStr = wj.getJsonData(deviceNums); //参数是一个List对象
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value="getAllDeviceState.do",method=RequestMethod.GET)
	public void getAllDeviceState(HttpServletRequest request, HttpServletResponse response) throws IOException{
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		String privilege = (String) request.getSession().getAttribute("privilege");
		
		if(areaIds== null) {
			response.getWriter().write("{\"areaCheck\":\"1\"}");
		}else{
			StatisticDao sd  = new StatisticDaoImpl();
			CountValue cv = sd.getStatistic(areaIds);
			if(cv == null) {
				response.getWriter().write("{\"areaCheck\":\"1\"}");
			}else{
				if(privilege.equals("4")){//权限为4的才显示客户平台数据
					//客户平台数据总数，并全部显示正常状态
					smokeDao = new SmokeDaoImpl();
					int count = smokeDao.queryCountCustomerSmoke();
					cv.setMacNum(cv.getMacNum()+count);
					cv.setNetStaterNum(cv.getNetStaterNum()+count);
				}
				
				JSONObject object=JSONObject.fromObject(cv);
				try {
					response.setContentType("text/html;charset=utf-8");
					response.getWriter().write(object.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	@RequestMapping(value="getStaticsByDeviceType.do",method=RequestMethod.GET)
	public void getStaticsByDeviceType(HttpServletRequest request, HttpServletResponse response){
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		
		DeviceDao dd = new DevicesDaoImpl();
		List<Devices> lstdev = dd.getStaticsByDeviceType(areaIds);
		
		WriteJson wj = new WriteJson();
		String jsonStr = wj.getJsonData(lstdev); //参数是一个List对象
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * 根据mac和设备类型获取最近一天的数据
	 */
	@RequestMapping(value="getRecentData.do",method=RequestMethod.GET)
	public void getRecentData(HttpServletRequest request,HttpServletResponse response) {
		String mac = request.getParameter("mac");
		String deviceType = request.getParameter("deviceType");
		
//		WriteJson wj = new WriteJson();
//		String jsonStr = "";
		JSONObject object = null;
		WaterEntity waterEntity = null;
		
		if (Utils.isNullStr(deviceType) && Utils.isNullStr(mac)){
			mSmartControlDao = new SmartControlDaoImpl();
			switch(deviceType){
			case "1": //烟感
			case "21"://LoraWan烟感
			case "31"://三江烟感
			case "41"://NB烟感
			case "5"://电气
				waterEntity =  mSmartControlDao.getAlarmRecord(mac);
				break;
			case "10"://水压
			case "42"://NB水压
			case "125"://DTU水压
			case "19"://水位
			case "124"://DTU水位
				waterEntity =  mSmartControlDao.getWaterOfToday(mac);
				break;
			default:
				waterEntity =  mSmartControlDao.getAlarmRecord(mac);
				break;
			}
			
			if (waterEntity != null){
				waterEntity.setDeviceType(deviceType);
			}
			
		}
		
		try {
			object = JSONObject.fromObject(waterEntity);
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(object.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
