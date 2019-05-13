/**
 * 上午8:53:59
 */
package com.cloudfire.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.KeepSystemDao;
import com.cloudfire.dao.OperationDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.SmokeSummaryDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.KeepSystemDaoImpl;
import com.cloudfire.dao.impl.OperationDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.dao.impl.SmokeSummaryDaoImpl;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.KeepEntity;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;
import com.gexin.fastjson.JSON;

/**
 * @author cheng
 *2017-3-20
 *上午8:53:59
 */
@Controller
public class DeleteOneSmokeBySmokeMac {
	private SmokeSummaryDao mSmokeSummaryDao;
	private KeepSystemDao mKeepSystemDao;
	private SmartControlDao mSmartControlDao;
	private AreaDao mAreaDao;
	
	@RequestMapping(value="deleteOneSmokeByMac.do",method=RequestMethod.GET)
	public ModelAndView deleteOneSmokeBySmokeMac(String userId,String smokeMac,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		mSmokeSummaryDao = new SmokeSummaryDaoImpl();
		mSmokeSummaryDao.deleteBysmokeMac(smokeMac);
		mv.setViewName("list_items");
		return mv;
	}
	
	
	@RequestMapping(value="deleteBuyerByIds.do",method=RequestMethod.GET)
	public void deleteOneSmokeBySmokeMac(String[] smokeMacs,HttpServletRequest request,HttpServletResponse response){
		mSmokeSummaryDao = new SmokeSummaryDaoImpl();
		String repeater = request.getParameter("repeater");
		if(StringUtils.isBlank(repeater)){
			repeater = mSmokeSummaryDao.getRepeaterMacBySmokeMac(smokeMacs[0]);
		}
		String userId = (String)request.getSession().getAttribute("userId");
		mSmokeSummaryDao.deleteBysmokeMacBate(smokeMacs);
		mSmokeSummaryDao.addDelDevMac(smokeMacs, userId, request);
		Utils.sendRepeaterList(repeater);
		HttpRsult rs = new HttpRsult();
		rs.setError("删除成功");
		rs.setErrorCode(0);
		Object object = JSON.toJSON(rs);
		try {
			response.getWriter().write(object.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value="findSmokeBySmokeMac.do",method=RequestMethod.GET)
	public ModelAndView findSmokeBySmokeMac(String smokeMacs, HttpServletRequest request, HttpServletResponse response){
		ModelAndView mv = new ModelAndView();
		mKeepSystemDao = new KeepSystemDaoImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		KeepEntity entity = mKeepSystemDao.findSmokeBySmokeMac(smokeMacs);
//		List<DeviceNetState> netStates = mSmartControlDao.getDeviceNetState();	
		//List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType();
		String userId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		System.out.println("privilege:"+privilege+"userId:"+userId);
		List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaByUser(userId, privilege);
		List<ShopTypeEntity> placeTypeIdPlaceTypeName = mSmartControlDao.getPlaceType();
//		mv.addObject("deviceNetState", netStates);
		mv.addObject("areaAndRepeaters", areaAndRepeaters);
		mv.addObject("placeTypeIdPlaceTypeName",placeTypeIdPlaceTypeName);
		mv.addObject("entity", entity);
		mv.setViewName("/updatesmokebysmokemac");
		return mv;
		
	}
	
	@RequestMapping(value="/updateSmokeByMac.do", method=RequestMethod.POST)
	public ModelAndView updateSmokeByMac (KeepEntity entity,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		mKeepSystemDao = new KeepSystemDaoImpl();
		if(entity.getLineState().equals("在线")||entity.getLineState()=="在线"){
			entity.setLineState("1");
		}else if(entity.getLineState().equals("离线")||entity.getLineState()=="离线"){
			entity.setLineState("0");
		}
		int result = mKeepSystemDao.updateSmokeBySmokeMac(entity);
		String message = "";
		if (result > 0) {
			message = "修改成功";
		}if (result==0) {
			message = "修改失败";
		}
		request.setAttribute("message", message);
		
		mSmartControlDao = new SmartControlDaoImpl();
//		List<DeviceNetState> netStates = mSmartControlDao.getDeviceNetState();	
		List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType();
		List<ShopTypeEntity> placeTypeIdPlaceTypeName = mSmartControlDao.getPlaceType();
		mv.addObject("entity", entity);
//		mv.addObject("deviceNetState", netStates);
		mv.addObject("areaAndRepeaters", areaAndRepeaters);
		mv.addObject("placeTypeIdPlaceTypeName", placeTypeIdPlaceTypeName);
		mv.setViewName("/updatesmokebysmokemac");
		return mv;	
	}
	
	@RequestMapping(value="findAreaByareaId.do")
	public ModelAndView findAreaByareaId(String address){
		ModelAndView mv = new ModelAndView();
		mAreaDao = new AreaDaoImpl();
		int areaId = Integer.parseInt(address);
		String areaName = mAreaDao.getAreaNameById(areaId);
		AreaBean bean = new AreaBean();
		bean.setAreaId(address);
		bean.setAreaName(areaName);
		mv.addObject("bean", bean);
		mv.setViewName("/updatearea");
		return mv;
	}
	
	@RequestMapping(value="updateAreaByareaId.do",method = RequestMethod.POST)
	public ModelAndView updateAreaByareaId(AreaBean bean,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		mAreaDao = new AreaDaoImpl();
		int result = mAreaDao.updateAreaByareaId(bean);
		String message = "";
		if (result > 0) {
			message = "修改成功";
		}if (result==0) {
			message = "修改失败";
		}
		request.setAttribute("message", message);
		mv.addObject("bean", bean);
		mv.setViewName("/updatearea");		
		return mv;
		
	}
	
	@RequestMapping(value="/deleteareabyareaid.do",method=RequestMethod.GET)
	public void deleteareabyareaid(HttpServletRequest request,HttpServletResponse response){
		String userId = (String) request.getSession().getAttribute("userId");
		mAreaDao = new AreaDaoImpl();
		String areaId = request.getParameter("areaId");
		int result = mAreaDao.deleteAreaById(areaId);
		
		//保存操作记录
		int status = 0;
		if (result == 0){
			status = 2;
		} else {
			status = 1;
		}
		OperationDao od = new OperationDaoImpl();
		od.saveOperationRecord(1, userId, areaId, GetTime.ConvertTimeByLong(), status);
		
		HttpRsult rs = new HttpRsult();
		rs.setError("请求成功");
		rs.setErrorCode(result);
		Object object = JSON.toJSON(rs);
		try {
			response.getWriter().write(object.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
