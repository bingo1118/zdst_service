package com.cloudfire.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.PlanDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.PlanDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.AreaBeanEntity;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.Plan;
import com.cloudfire.entity.PlanPoint;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.WriteJson;

@Controller
public class PlanController {
	private PlanDao pd;
	private SmokeDao sd;
	
	
	@RequestMapping(value = "/editPlan.do",method = RequestMethod.GET) 
	public ModelAndView editPlan(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		pd = new PlanDaoImpl();
		sd = new SmokeDaoImpl();
		
		//获取用户的id和权限等级
		String userid = (String)request.getSession().getAttribute("userId");
		String privilege = (String)request.getSession().getAttribute("privilege");
		
		//根据用户id和权限查询 地区id和地区名
		AreaDaoImpl mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaAndRepeaters = mAreaDao.getAllAreaByUserId(userid, privilege); 
		int planArea =0;
		if(areaAndRepeaters.size()>0){
			planArea = areaAndRepeaters.get(0).getAreaId(); 
		}
		
		//默认查询第一个地区的平面图信息(平面图地址+设备坐标位置信息)
		Plan plan = pd.getPlan(planArea);
		List<SmokeBean> lstSmoke = sd.getSmokesByAreaid(planArea);
		for (PlanPoint pp : plan.getLstpp()) {
			if (lstSmoke == null) {
				break;
			}
			//获取不在平面图内的设备列表
			Iterator<SmokeBean> it =lstSmoke.iterator(); 
			while(it.hasNext()){
				if (pp.getMac() != null &&pp.getMac().equals(it.next().getMac())){
					it.remove();
				}
			}
		}
		 //获取区域id和区域名字
		mv.addObject("areaAndRepeaters", areaAndRepeaters); //将地区列表areaid和area存入mv
		mv.addObject("plan",plan);  //将平面图的信息存入mv
		mv.addObject("lstSmoke",lstSmoke); //将不在平面图的设备列表存入mv
		mv.setViewName("/addsb");
		return mv;
	}
	
	
	
	@RequestMapping(value = "/getPlan.do",method = {RequestMethod.POST,RequestMethod.GET}) 
	public ModelAndView getPlan(HttpServletRequest request,HttpServletResponse response,ModelAndView mv) {
		pd = new PlanDaoImpl();
		sd = new SmokeDaoImpl();
		int planArea = Integer.parseInt(request.getParameter("planArea"));
		Plan plan = pd.getPlan(planArea);
		List<SmokeBean> lstSmoke = sd.getSmokesByAreaid(planArea);
		for (PlanPoint pp : plan.getLstpp()) {
			if (lstSmoke == null) {
				break;
			}
			
			//获取不在平面图内的设备列表
			Iterator<SmokeBean> it =lstSmoke.iterator(); 
			while(it.hasNext()){
				if (pp.getMac() != null &&pp.getMac().equals(it.next().getMac())){
					it.remove();
				}
			}
			
		}
		
		String userid = (String)request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		
		AreaDaoImpl mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaAndRepeaters = mAreaDao.getAllAreaByUserId(userid, privilege);
		mv.addObject("areaAndRepeaters", areaAndRepeaters);
		mv.addObject("plan", plan);
		mv.addObject("lstSmoke", lstSmoke);
		mv.setViewName("/addsb");
		return mv;
	}
	
	@RequestMapping(value="getPlan2.do")
	public void getPlan2(HttpServletRequest request,HttpServletResponse response) {
		pd = new PlanDaoImpl();
		String mac = request.getParameter("mac");
		Plan plan = pd.getPlanByMac(mac);
		if (plan != null) {
			JSONObject jObject = JSONObject.fromObject(plan);
			try {
				response.getWriter().write(jObject.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				response.getWriter().write("");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		
	}
	
}
