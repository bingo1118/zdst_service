package com.cloudfire.controller;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.Electic_NB_DTUDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.SmokeSummaryDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.DevicesDaoImpl;
import com.cloudfire.dao.impl.Electic_NB_DTUDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.SmokeSummaryDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.BQMacInfo;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.SearchDto;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.query.BqMacType;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.entity.query.ElectricDTUQuery;
import com.cloudfire.interceptor.NorthqinHandler2;
import com.cloudfire.myservice.BqService;
import com.cloudfire.myservice.impl.BqServiceImpl;
import com.cloudfire.until.Constant;
import com.cloudfire.until.PageBean;
import com.cloudfire.until.WriteJson;
import com.sun.deploy.net.HttpRequest;
import com.sun.mail.iap.Response;
import common.page.Pagination;

import freemarker.template.utility.StringUtil;

@Controller
public class NorthController {
	private AreaDao mAreaDao;
	private LoginDao mLoginDao;
	private DeviceDao deviceDao;
	private BqService bqService;
	
	
	/**
	 * @author lzo add by 2017-6-5
	 * @return
	 */
	@RequestMapping(value = "/electric_Need_DTU_data.do",method = RequestMethod.GET)
	public ModelAndView electric_Need_DTU_data(HttpServletRequest request,HttpServletResponse response){
		String electricMac = request.getParameter("mac");
		String eleType = request.getParameter("type");
		Electic_NB_DTUDao dao = new Electic_NB_DTUDaoImpl();
		List<ElectricDTUQuery> eleList = dao.getNeedNBDTU(electricMac, eleType);
		if(eleList!=null &&eleList.size()>0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(eleList);
			try {
				response.getWriter().write(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	/**
	 * @author lzo add by 2017-6-3
	 * @return
	 */
	@RequestMapping(value = "/electric_Need_DTU.do",method = RequestMethod.GET)
	public ModelAndView electric_Need_DTU(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView();
		String electricMac = request.getParameter("electricMac");
		String eleType = request.getParameter("eleType");
		request.getSession().setAttribute("mac", electricMac);
		request.getSession().setAttribute("type", eleType);
		modelAndView.setViewName("/WEB-INF/page/bq/electrical_Need_DTU");
		return modelAndView;
	}
	
	
	/**
	 * @author lzo
	 * @param  beiqin DTU shuju 2017-6-2
	 * @return
	 */
	@RequestMapping(value = "/electricDTU.do", method = RequestMethod.GET)
	public ModelAndView electricDTU(HttpServletRequest request,ElectricDTUQuery query,Integer pageNo,ModelMap model){
		ModelAndView modelAndView = new ModelAndView();
		String electicMac = request.getParameter("electicMac");
		if(electicMac != null){
			request.getSession().setAttribute("electicMac", electicMac);
		}
			
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(electicMac)) {
			query.setSmokeMac(electicMac);
			params.append("&electicMac").append(electicMac);
			model.addAttribute("electicMac", electicMac);
		}
		bqService = new BqServiceImpl();
		Pagination pagination = bqService.getElectriDTUListPage(query);
		pagination.pageView("/fireSystem/electricDTU.do", params.toString());
		model.addAttribute("pagination", pagination);
		modelAndView.setViewName("/WEB-INF/page/bq/electricalDTU");
		return modelAndView;
	}
	
	@RequestMapping(value = "/bqEletric.do", method = RequestMethod.GET)
	public ModelAndView bqEletric(HttpServletRequest request,BQMacEntity query,Integer pageNo,ModelMap model) {
		String currentId = (String)request.getSession().getAttribute("userId");
		ModelAndView modelAndView = new ModelAndView();
		//获得设备类型
		String deviceId = request.getParameter("deviceId");
		if (deviceId!=null) {
			request.getSession().setAttribute("deviceId", deviceId);
		}
		if (deviceId == null) {
			deviceId = (String) request.getSession().getAttribute("deviceId");
		}
		
		//String projectName = 
		//String deviceName = request.getParameter("deviceName");
		String type = request.getParameter("t");
		String statusStr = request.getParameter("statusStr");	
		String begintime = request.getParameter("J_xl_1");
		String endtime = request.getParameter("J_xl_2");
		if (StringUtils.isNotBlank(begintime) && StringUtils.isNotBlank(endtime) && begintime.equals(endtime)) {
			begintime = begintime+" 00:00:00";
			endtime = endtime + " 23:59:59";
		}

		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(deviceId)) {
			query.setDeviceId(deviceId);
			params.append("&deviceId").append(deviceId);
			model.addAttribute("deviceId", deviceId);
		}
		if (StringUtils.isNotBlank(type)) {
			query.setDevicetype(type);
			params.append("&t=").append(type);
			model.addAttribute("t",type);
		}
		
		/*if (StringUtils.isNotBlank(deviceName)) {
			query.setNamed(deviceName);
			params.append("&deviceName=").append(query.getNamed());
			model.addAttribute("deviceName", query.getNamed());
		}*/
		
		if (StringUtils.isNotBlank(statusStr)) {
			query.setStatusStr(statusStr);
			params.append("&statusStr=").append(query.getStatusStr());
			model.addAttribute("statusStr", query.getStatusStr());
		}if (StringUtils.isNotBlank(begintime)) {
			query.setBegintime(begintime);
			if (begintime.contains(" 00:00:00")) {
				begintime = (String) begintime.substring(0, 10);
			}
			params.append("&J_xl_1=").append(begintime);
			model.addAttribute("J_xl_1", begintime);
		}
		if (StringUtils.isNotBlank(endtime)) {
			query.setEndtime(endtime);			
			params.append("&J_xl_2=").append(query.getEndtime());
			if (endtime.contains(" 23:59:59")) {
				endtime = (String) endtime.substring(0, 10);
			}
			model.addAttribute("J_xl_2", endtime);
		}
		
		
		bqService = new BqServiceImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			deviceDao = new DevicesDaoImpl();
			List<BqMacType> bqMacTypes = deviceDao.getBqMacType();
			List<DeviceNetState> deviceNetState = deviceDao.getBqMacStatus(deviceId);
			Pagination pagination = bqService.getBqListPage(query, areaIds);
			pagination.pageView("/fireSystem/bqEletric.do", params.toString());
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			model.addAttribute("pagination", pagination);
			
			//List<BqMacType> t = deviceDao.getBqMacType();
			//modelAndView.addObject("status", status);
			//modelAndView.addObject("type",t);
			model.addAttribute("deviceNetState", deviceNetState);
			model.addAttribute("type",bqMacTypes);
			modelAndView.addAllObjects(modelMap);
		}
		modelAndView.setViewName("/WEB-INF/page/main/northqin_deviceState_list_electrical");
		return modelAndView;
	}

	@RequestMapping(value = "/bqEletricData.do", method = RequestMethod.GET)
	public ModelAndView bqEletricData(HttpServletRequest request,
			HttpServletResponse response) {
		SmokeSummaryDao smokeDao = new SmokeSummaryDaoImpl();
		String deviceId = request.getParameter("deviceId");
		String type = request.getParameter("type");
		String url = "http://www.northqin.com/hanrun/connection/realtime/info";
		if (!StringUtils.isBlank(deviceId)) {
			String result = new NorthqinHandler2().sendPost(url,
					"auth=$apr1$&deviceId=" + deviceId);
			if (!StringUtils.isBlank(result)) {
				JSONObject object = JSONObject.fromObject(result);
				JSONObject realtimeMap = object.getJSONObject("data")
						.getJSONObject("equipInfo")
						.getJSONObject("realtimeMap");
				//System.out.println(realtimeMap);
				String time = realtimeMap.getJSONObject("voltage1").getString(
						"time");
				String voltage1 = realtimeMap.getJSONObject("voltage1")
						.getString("nodeValue");
				String voltage2 = realtimeMap.getJSONObject("voltage2")
						.getString("nodeValue");
				String voltage3 = realtimeMap.getJSONObject("voltage3")
						.getString("nodeValue");
				String temperature1 = realtimeMap.getJSONObject("temperature1")
						.getString("nodeValue");
				String temperature2 = realtimeMap.getJSONObject("temperature2")
						.getString("nodeValue");
				String temperature3 = realtimeMap.getJSONObject("temperature3")
						.getString("nodeValue");
				String temperature4 = realtimeMap.getJSONObject("temperature4")
						.getString("nodeValue");
				String eleCurrent1 = realtimeMap.getJSONObject("eleCurrent1")
						.getString("nodeValue");
				String eleCurrent2 = realtimeMap.getJSONObject("eleCurrent2")
						.getString("nodeValue");
				String eleCurrent3 = realtimeMap.getJSONObject("eleCurrent3")
						.getString("nodeValue");
				String leakEleCurrent1 = realtimeMap.getJSONObject(
						"leakEleCurrent1").getString("nodeValue");
				BQMacInfo info = new BQMacInfo();
				info.setBqEleMac(deviceId);
				info.setTemperature1(temperature1);
				info.setTemperature2(temperature2);
				info.setTemperature3(temperature3);
				info.setTemperature4(temperature4);
				info.setEleCurrent1(eleCurrent1);
				info.setEleCurrent2(eleCurrent2);
				info.setEleCurrent3(eleCurrent3);
				info.setCreateTimeString(time);
				info.setLeakEleCurrent1(leakEleCurrent1);
				info.setVoltage1(voltage1);
				info.setVoltage2(voltage2);
				info.setVoltage3(voltage3);

				boolean flag = smokeDao.selectEleInfoByTime(deviceId, time);
				if (flag == false) {
					smokeDao.insertNowData(info);
				}
			}
			List<BQMacInfo> dataList = smokeDao.getDataById(deviceId);
			JSONArray array = JSONArray.fromObject(dataList);
			response.setContentType("text/html;charset=utf-8");
			try {
				response.getWriter().write(array.toString());
			} catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;

	}
	
	@RequestMapping(value="/northqin_deviceState_list_electrical_one.do",method=RequestMethod.GET)
	public ModelAndView bqListOne(HttpServletRequest request,String currentId){
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		String deviceId = request.getParameter("deviceId");
		bqService = new BqServiceImpl() ;
//		@SuppressWarnings("unchecked")
//		List<String> areIds =(List<String>) request.getSession().getAttribute("areaIds");
//		AlarmInfoDaoImpl mAlarmInfoDao = new AlarmInfoDaoImpl();
//		List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areIds);
		BQMacEntity entity = bqService.getOnetBqMacEntity(deviceId);
		modelAndView.addObject("entity", entity);
	
		modelAndView.setViewName("/WEB-INF/page/bq/northqin_deviceState_list_electrical_one");
		return modelAndView;
		
	}
}
