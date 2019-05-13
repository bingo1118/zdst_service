package com.cloudfire.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.BuildingDao;
import com.cloudfire.dao.QP_FaultInfoDao;
import com.cloudfire.dao.impl.BuildingDaoImpl;
import com.cloudfire.dao.impl.QP_FaultInfoDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.QP_FaultInfos;
import com.cloudfire.until.WriteJson;


@Controller
public class faultInfoController {
	
	/**
	 * @author lzo
	 * @param JSON根据设备类型获取处理记录
	 */
	@RequestMapping(value = "/lookdispsss.do" ,method = RequestMethod.GET)
	public void lookdispsss(HttpServletRequest request,HttpServletResponse response,String currentId){
		String devMac = request.getParameter("devMac");
		BuildingDao dd = new BuildingDaoImpl();
		List<AlarmInfoEntity_PC> alarmList = dd.getAlarmInfo(devMac);
		if(alarmList !=null && alarmList.size()>0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(alarmList);
			try {
				//System.out.println(jObject);
				response.getWriter().write(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value = "/buildinfo.do",method=RequestMethod.GET)
	public ModelAndView buildinfo(HttpServletResponse response,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		QP_FaultInfoDao qfa = new QP_FaultInfoDaoImpl();
		request.getSession().removeAttribute("messageCode");
		String faultMac = request.getParameter("repeaterMac");
		QP_FaultInfos faultEntity = new QP_FaultInfos();
		faultEntity = qfa.getFaultInfos(faultMac);
		List<QP_FaultInfos> olist = new ArrayList<QP_FaultInfos>();
		olist.add(faultEntity);
		mv.addObject("faultList",olist);
		mv.setViewName("/WEB-INF/page/builds/buildingInfos");
		return mv;
	}
	
	@RequestMapping(value = "/buildinfo.do",method=RequestMethod.POST)
	public ModelAndView buildinfoAction(HttpServletResponse response,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		QP_FaultInfoDao qfa = new QP_FaultInfoDaoImpl();
//		request.getSession().removeAttribute("messageCode");
		String faultMac = request.getParameter("repeaterMac");
		QP_FaultInfos faultEntity = new QP_FaultInfos();
		faultEntity = qfa.getFaultInfos(faultMac);
		List<QP_FaultInfos> olist = new ArrayList<QP_FaultInfos>();
		olist.add(faultEntity);
		mv.addObject("faultList",olist);
		mv.setViewName("/WEB-INF/page/builds/buildingInfos");
		return mv;
	}
	
	@RequestMapping(value = "/ModifyCompanyInfo.do",method=RequestMethod.GET)
	public ModelAndView ModifyCompanyInfo(HttpServletResponse response,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		QP_FaultInfoDao qfa = new QP_FaultInfoDaoImpl();
		String faultMac = request.getParameter("repeaterMac");
		QP_FaultInfos faultEntity = new QP_FaultInfos();
		faultEntity = qfa.getFaultInfos(faultMac);
		List<QP_FaultInfos> olist = new ArrayList<QP_FaultInfos>();
		olist.add(faultEntity);
		mv.addObject("faultList",olist);
		mv.setViewName("/WEB-INF/page/builds/buildingInfos");
		return mv;
	}
	
}
