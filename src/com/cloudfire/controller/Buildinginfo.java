package com.cloudfire.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.BuildingDao;
import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.KeepSystemDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.BuildingDaoImpl;
import com.cloudfire.dao.impl.DevicesDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.AreaParentEntity;
import com.cloudfire.entity.RepeaterLevelUp;
import com.cloudfire.entity.WorkingTime;
import com.cloudfire.thread.WorkingTimeThread;
import com.cloudfire.until.IfStopAlarm;
import com.cloudfire.until.WriteJson;


@Controller
public class Buildinginfo {
	private KeepSystemDao mKeepSystemDao;
	private AreaDao mAreaDao;
	private LoginDao mLoginDao;
	
	/**
	 * @author lzo
	 * @param JSON根据设备类型获取处理记录
	 * @throws IOException 
	 */
	@RequestMapping(value = "/lookdisp.do" ,method = RequestMethod.GET)
	public void lookdisp(HttpServletRequest request,HttpServletResponse response,String currentId) throws IOException{
		String devMac = request.getParameter("devMac");
		BuildingDao dd = new BuildingDaoImpl();
		List<AlarmInfoEntity_PC> alarmList = dd.getAlarmInfo(devMac);
		if(alarmList !=null && alarmList.size()>0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(alarmList);
			response.getWriter().write(jObject);
		}
	}
	
	//批量升级
	@RequestMapping(value = "/batchUpgrading.do",method=RequestMethod.GET)
	public ModelAndView batchUpgrading(HttpServletResponse response,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		mLoginDao = new LoginDaoImpl();
		List<String> areaids = (List<String>)request.getSession().getAttribute("areaIds");
		mAreaDao = new AreaDaoImpl();
		List<AreaParentEntity> areaLists = mAreaDao.getParentList(areaids);
		request.setAttribute("areaBean", areaLists);
		mv.setViewName("/WEB-INF/page/repeater/repeaterLeveUpState");
		return mv;
	}
	
	//批量升级
	@RequestMapping(value = "/batchRepeaterInfo.do",method=RequestMethod.GET)
	public ModelAndView batchRepeaterInfo(HttpServletResponse response,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		mAreaDao = new AreaDaoImpl();
		String areaid = request.getParameter("areaid");
		String parentid = request.getParameter("parentid");
//		Map<String,String> rMap = mAreaDao.getRepeaterList(areaid);
//		request.setAttribute("repeaterMap", rMap);
		mAreaDao = new AreaDaoImpl();
		List<RepeaterLevelUp> areaLists = mAreaDao.getParentList(areaid,parentid);
		request.setAttribute("areaBean", areaLists);
		mv.setViewName("/WEB-INF/page/repeater/repeaterInfo");
		return mv;
	}
	
	@RequestMapping(value = "/levenUp.do",method=RequestMethod.GET)
	public ModelAndView levenUp(HttpServletResponse response,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		request.getSession().removeAttribute("levelMessage");
		mv.setViewName("/WEB-INF/page/util/levenUp");
		return mv;
	}
	
	@RequestMapping(value="/notify.do",method=RequestMethod.GET)
	public ModelAndView infoweb(HttpServletRequest request,HttpServletResponse response,String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		Map<String, List<?>> modelMap = new HashMap<String, List<?>>();
		DeviceDao dev = new DevicesDaoImpl();
		List<WorkingTime> wlist = dev.getWorkingTime(currentId);
		modelMap.put("wlist", wlist);
		modelAndView.addAllObjects(modelMap);
		modelAndView.setViewName("/WEB-INF/page/main/notify");
		return modelAndView;
	}
	
	@RequestMapping(value="/resetalarmtime.do",method=RequestMethod.GET)
	public ModelAndView resetalarmtime(HttpServletRequest request,String currentId){
		ModelAndView modelAndView =  new ModelAndView();
		String resetTime = request.getParameter("resetTime");
		if(StringUtils.isNotBlank(resetTime)){
			System.out.println("timeReset:===="+resetTime);
			long timeReset = Integer.parseInt(resetTime)*60000;
			IfStopAlarm.cycleTime = timeReset;
		}
		request.getSession().setAttribute("resetTime",resetTime);
		modelAndView.setViewName("/WEB-INF/page/devicestates/resetalarmtime");
		return modelAndView;
	} 
	
	/**
	 * @author lzo
	 * @param 修改上班时间
	 */
	@RequestMapping(value = "/updaTimeWorking.do" ,method = RequestMethod.GET)
	public void updaTimeWorking(HttpServletRequest request,HttpServletResponse response,String currentId){
		currentId = (String) request.getSession().getAttribute("userId");
		String workingTime = request.getParameter("workingTime");
		String workingUser = request.getParameter("workingUser");
		DeviceDao dev = new DevicesDaoImpl();
		dev.updateUserInfo(workingTime, workingUser);
		WorkingTime w=dev.selectParentIdByUserId(workingUser);
		if(w!=null&&w.getSuperUserId()==currentId){
			List<WorkingTime> wlist = dev.getWorkingTime(currentId);
			Timer timer = new Timer();
			timer.schedule(new WorkingTimeThread(),1000,1000*60*60*24);
			if(wlist !=null && wlist.size()>0){
				WriteJson writeJson = new WriteJson();
				String jObject = writeJson.getJsonData(wlist);
				try {
					response.getWriter().write(jObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
