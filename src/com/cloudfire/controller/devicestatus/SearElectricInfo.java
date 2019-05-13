package com.cloudfire.controller.devicestatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.OneElectricInfoDao;
import com.cloudfire.dao.impl.OneElectricInfoDaoImpl;
import com.cloudfire.dao.query.SearchElectricQuery;
import com.cloudfire.dao.query.impl.SearchElectricQueryImpl;
import com.cloudfire.entity.OneElectricEntity;
import com.cloudfire.entity.ThreePhaseElectricEntity;
import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.until.WriteJson;

/**
 * @author cheng
 *2017-4-1
 *下午4:16:51
 */
@Controller
public class SearElectricInfo {
	private SearchElectricQuery searchElectricQuery;
	private OneElectricInfoDao mOneElectricInfoDao;
	private SearchElectricQuery electricDao;
	
	/**电气火灾设备的查询 */
	@RequestMapping(value = "/searchElectricForm1.do",method = RequestMethod.POST)
	public void addUserResult(HttpServletRequest request,HttpServletResponse response){
		
		String mac = request.getParameter("mac");
		String status = request.getParameter("status");
		String begintime = request.getParameter("begintime");
		String endtime = (String)request.getParameter("endtime");
		String placeTypeId = request.getParameter("placeTypeId");
		String areaId = request.getParameter("areaId");

		SearchElectricInfo searchElectricInfo = new SearchElectricInfo();
		searchElectricInfo.setMac(mac);
		searchElectricInfo.setNetState(status);
		searchElectricInfo.setAreaName(areaId);
		searchElectricInfo.setPlaceType(placeTypeId);
		searchElectricInfo.setAlarmBeginTime(begintime);
		searchElectricInfo.setAlarmEndTime(endtime);

		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		searchElectricQuery = new SearchElectricQueryImpl();
		//List<SearchElectricInfo> list = mSearchElectricQuery.searchElectricInfo(mac,devicestatus,begintime,endtime,areaList);
		if (areaIds!=null && areaIds.size()>0) {
			List<SearchElectricInfo> list = searchElectricQuery.searchElectricInfo(searchElectricInfo,areaIds);
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(list);
			try {
				response.getWriter().write(jObject);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else {
		}
		

	}	
	
	
	/**点击查看最近一次 */
	@RequestMapping(value="/getmyOneElectricInfo.do",method=RequestMethod.GET)
	public ModelAndView deviceState(HttpServletRequest request,String currentId){
		ModelAndView modelAndView =  new ModelAndView();
		String smokeMac = request.getParameter("devId");
		String DeviceType = request.getParameter("deviceType");
		int deviceType = -1;
		if(StringUtils.isNotBlank(DeviceType)){
			deviceType = Integer.parseInt(DeviceType);
		}
		if(deviceType == 81){
			modelAndView = getThreeElectricInfo(request, currentId);
		}else{
			//获取设备的数据列表
			mOneElectricInfoDao = new OneElectricInfoDaoImpl();
			OneElectricEntity oee  = mOneElectricInfoDao.getOneElectricInfo(smokeMac);
			modelAndView.addObject("oee", oee);
			
			//获取设备的信息
			electricDao = new SearchElectricQueryImpl();
			SearchElectricInfo deviceDetail = electricDao.getDeviceDetail(smokeMac);
			modelAndView.addObject("deviceDetail", deviceDetail);
			
			modelAndView.setViewName("/WEB-INF/page/devicestates/oneElectric");
		}
		return modelAndView;
	} 
	
	/**点击查看最近一次 */
	@RequestMapping(value="/getThreeElectricInfo.do",method=RequestMethod.GET)
	public ModelAndView getThreeElectricInfo(HttpServletRequest request,String currentId){
		ModelAndView modelAndView =  new ModelAndView();
		
		SearchElectricInfo query = new SearchElectricInfo();
		String smokeMac = request.getParameter("devId");
		if (StringUtils.isNotBlank(smokeMac)) {
			query.setMac(smokeMac);
		}
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		
		mOneElectricInfoDao = new OneElectricInfoDaoImpl();
		ThreePhaseElectricEntity oee  = mOneElectricInfoDao.getThreePhaseElectricEntity(smokeMac);
		
		Map<Integer, List<String>> thresholdMap = mOneElectricInfoDao.getThresholdValues(smokeMac);
		if (oee!=null) {
			oee.setThresholdV(thresholdMap.get(6).get(0));
			oee.setThresholdA(thresholdMap.get(7).get(0));
			oee.setThresholdT(thresholdMap.get(8).get(0));
			modelAndView.addObject("oee", oee);
		}
		
		electricDao = new SearchElectricQueryImpl();
		List<SearchElectricInfo> list = electricDao.searchNBElectricInfo(query, areaIds);
		
		Map<String, List<?>> modelMap = new HashMap<>();
		modelMap.put("entity", list);
		modelAndView.addAllObjects(modelMap);
		modelAndView.setViewName("/WEB-INF/page/devicestates/threeElectric");
		return modelAndView;
	} 
}

