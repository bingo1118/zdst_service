/**
 * 下午2:16:18
 */
package com.cloudfire.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AddUserDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.impl.AddUserDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.City;
import com.cloudfire.entity.ParentArea;
import com.cloudfire.entity.Province;
import com.cloudfire.entity.SecondArea;
import com.cloudfire.entity.Town;
import com.cloudfire.myservice.AreaService;
import com.cloudfire.myservice.impl.AreaServiceImpl;
import com.gexin.fastjson.JSON;

/**
 * @author cheng
 *2017-6-7
 *下午2:16:18
 */

@Controller
public class AddAreaController {
	private AreaService areaService;
	private AreaDao mAreaDao;
	
	@RequestMapping(value = "/lookvedio.do",method=RequestMethod.GET)
	public ModelAndView lookvedio(HttpServletResponse response,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		
		String cameraId = request.getParameter("cameraId");
		String cameraName = request.getParameter("cameraName");
		String cameraPwd = request.getParameter("cameraPwd");
		String position = request.getParameter("position");
		String size = request.getParameter("size");
		String cameraChannel = request.getParameter("cameraChannel");
		
		request.getSession().setAttribute("cameraId", cameraId);
		request.getSession().setAttribute("cameraName", cameraName);
		request.getSession().setAttribute("cameraPwd", cameraPwd);
		request.getSession().setAttribute("position", position);
		request.getSession().setAttribute("size", size);
		request.getSession().setAttribute("cameraChannel", cameraChannel);
		
		mv.setViewName("/lookvedio");
		return mv;
	}
	
	
	@RequestMapping(value = "/toAdd.do",method=RequestMethod.GET)
	public ModelAndView toAdd(){
		ModelAndView mv = new ModelAndView();
		areaService = new AreaServiceImpl();
		List<Province> listProvince = areaService.getAllPrivinceList();
		mv.addObject("province", listProvince);
		mv.setViewName("/addParentArea");
		return mv;
	}
	
	@RequestMapping(value = "/toAdd2.do",method=RequestMethod.GET)
	public ModelAndView toAdd2(){
		ModelAndView mv = new ModelAndView();
//		areaService = new AreaServiceImpl();
//		List<Province> listProvince = areaService.getAllPrivinceList();
//		List<ParentArea> listParentAreas=areaService.getParentAreaByCityCode("");
//		Map<Integer,String> areaMap = new HashMap<Integer,String>();
//		mv.addObject("province", listProvince);
//		mv.addObject("listParentAreas",listParentAreas);
		mv.setViewName("/addSecondArea");
		return mv;
	}
	
	@RequestMapping(value="getAllProvince.do")
	public void getAllProvince(HttpServletResponse response){
		areaService=new AreaServiceImpl();
		List<Province> listProvince=areaService.getAllPrivinceList();
		Object json=JSON.toJSON(listProvince);
		try {
			response.getWriter().write(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/selectCity.do")
	public void selectCity(String provinceCode,HttpServletResponse response){
		if (StringUtils.isNotBlank(provinceCode)) {
			areaService = new AreaServiceImpl();
			List<City> cityList = areaService.getCityByProviceCode(provinceCode);
			Object json = JSON.toJSON(cityList);
			try {
				response.getWriter().write(json.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value="/selectTown.do")
	public void selectTown(String cityCode,HttpServletResponse response){
		if (StringUtils.isNotBlank(cityCode)) {
			areaService = new AreaServiceImpl();
			List<Town> townList = areaService.getTownByCityCode(cityCode);
			Object object = JSON.toJSON(townList);
			try {
				response.getWriter().write(object.toString());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value="/selectParentCode.do")
	public void selectParentCode(String townCode,HttpServletResponse response){
		if (StringUtils.isNotBlank(townCode)) {
			areaService = new AreaServiceImpl();
			List<ParentArea> parentAreaList = areaService.getParentAreaByCityCode(townCode);
			Object object = JSON.toJSON(parentAreaList);
			try {
				response.getWriter().write(object.toString());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** 添加一级区域 */
	@RequestMapping(value="/addParentArea.do")
	public ModelAndView addParentArea(HttpServletRequest request,
			HttpServletResponse response,ParentArea parentArea) {
		String userId = (String)request.getSession().getAttribute("userId");
		ModelAndView modelAndView =  new ModelAndView();
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("GBK");
		
		parentArea.setCurrentId(userId);
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		mAreaDao = new AreaDaoImpl();
		
		String message = "";
		List<String> lists = new ArrayList<String>();
		if(mAreaDao.addParentArea(parentArea)){
			message = "添加成功";
		}else{
			message = "添加失败,此区域可能已经存在！";
		}
		request.setAttribute("message", message);
		lists.add(message);
		modelAndView.addObject("lists", lists);
		//指定视图
		modelAndView.setViewName("/addParentArea");

		return modelAndView;
	}
	
	/** 添加二级区域 */
	@RequestMapping(value="/addSecondArea.do")
	public ModelAndView addSecondArea(HttpServletRequest request,
			HttpServletResponse response,SecondArea secondArea) {
		String userId = (String)request.getSession().getAttribute("userId");
		ModelAndView modelAndView =  new ModelAndView();
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("GBK");
//		String parentAreaName=request.getParameter("parentAreaName");
		secondArea.setCurrentId(userId);
		
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		mAreaDao = new AreaDaoImpl();
		AddUserDao addDao = new AddUserDaoImpl();
		
		
		String message = "";
		List<String> lists = new ArrayList<String>();
		if(secondArea!=null&&StringUtils.isNotEmpty(secondArea.getSecondAreaName().trim())){
			if(!StringUtils.isNotEmpty(secondArea.getParentAreaName())){  //一级区域不能为空
				message = "添加失败,请先选择一级区域";
			}else{
				int line = mAreaDao.insertSecondArea(secondArea);
				if(line>0){
					message = "添加成功";
					addDao.addUserArea(userId, line+"");
				}else{
					message = "添加失败,此区域可能已经存在！";
				}
			}
		}else{
			message = "添加失败,请输入正确的名称！";
		}
		
		request.setAttribute("message", message);
		lists.add(message);
		modelAndView.addObject("lists", lists);
		//指定视图
		modelAndView.setViewName("/addSecondArea");

		return modelAndView;
	}
	
	
	@RequestMapping(value = "/updateParent.do",method=RequestMethod.GET)
	public ModelAndView updateParent(){
		ModelAndView mv = new ModelAndView();
		
		mAreaDao = new AreaDaoImpl();
		Map<Integer,String> parentIds = mAreaDao.getParentAll();
		List<AreaBean> lists = mAreaDao.getAll();
		
		mv.addObject("parentIds", parentIds);
		mv.addObject("lists", lists);
		mv.setViewName("/updateParend");
		return mv;
	}
	
	@RequestMapping(value = "/getParentIds.do",method=RequestMethod.POST)
	public void getParentIds2(String townCode,HttpServletResponse response){
		getParentIds(null,response);
	}
	
	@RequestMapping(value = "/getParentIds.do",method=RequestMethod.GET)
	public void getParentIds(String townCode,HttpServletResponse response){
		mAreaDao = new AreaDaoImpl();
		List<ParentArea> parentIds = mAreaDao.getParentAreaByTownCode(null);
		Object object = JSON.toJSON(parentIds);
		try {
			response.getWriter().write(object.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
