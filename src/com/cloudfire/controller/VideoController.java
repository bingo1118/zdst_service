package com.cloudfire.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.VideoDao;
import com.cloudfire.dao.impl.VideoDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.until.IpUtils;
import com.cloudfire.until.WriteJson;

@Controller
public class VideoController {
	
	
	@RequestMapping(value = "/videoList.do" ,method = RequestMethod.GET)
	public ModelAndView videoList(HttpServletRequest request,String currentId){
		ModelAndView modelAndView =  new ModelAndView();
		modelAndView.setViewName("/WEB-INF/page/video/demo");
		return modelAndView;
	}
	
	@RequestMapping(value = "/addVideo.do" ,method = RequestMethod.GET)
	public void addVideo(HttpServletRequest request,HttpServletResponse response){
		String indexCode = request.getParameter("cameraIndexCode");
		String videoName = request.getParameter("videoName");
		String areaId = request.getParameter("areaId");
		VideoDao vd = new VideoDaoImpl();
		boolean  result=vd.updateVideo(indexCode,videoName,areaId);
		HttpRsult hr = new HttpRsult();
		if (result){
			hr.setErrorCode(0);
			hr.setError("添加成功");
		} else {
			hr.setErrorCode(1);
			hr.setError("添加失败");
		}
		
		JSONObject jsonObject=new JSONObject(result);
		try {
			response.getWriter().write(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value = "/clickgetip.do" ,method = RequestMethod.GET)
	public void clickgetip(HttpServletRequest request,String currentId,HttpServletResponse response){
		String ipstr = request.getParameter("clickgetip");
		List<String> list = new ArrayList<String>();
		list = IpUtils.getAllIpList(ipstr);
		WriteJson writeJson = new WriteJson();
		String jObject = writeJson.getJsonData(list);
		try {
			//System.out.println(jObject);
			response.getWriter().write(jObject);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value = "/getBindVideo.do" , method = RequestMethod.GET)
	public ModelAndView getBindVideo(HttpServletRequest request,HttpServletResponse response,@RequestParam String mac) {
		ModelAndView mav = new ModelAndView();
		VideoDao vd = new VideoDaoImpl();
		String videoIndex = vd.getVideoByMac(mac);
		if (StringUtils.isBlank(videoIndex)){
			mav.addObject("error_message", "该设备未绑定摄像头");
			mav.setViewName("/WEB-INF/page/error/novideo");
		} else {
			mav.addObject("videoIndex", videoIndex);
			mav.setViewName("/WEB-INF/page/video/preview");
		}
		return mav;
	}
	
	@RequestMapping(value = "/getVideoList.do" , method = RequestMethod.GET)
	public ModelAndView getVideoList(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/WEB-INF/page/main/video_detail");
		return mav;
	}
	
	@RequestMapping(value = "/bindVideo.do" , method = RequestMethod.GET)
	public void bindVideo(HttpServletRequest request,HttpServletResponse response){
		String cameraIndexCode = request.getParameter("cameraIndexCode");
		String smokeMac = request.getParameter("smokeMac");
		String areaId = request.getParameter("areaId");
		
		VideoDao vd = new VideoDaoImpl();
		boolean result = vd.bindVideo(cameraIndexCode,smokeMac,areaId);
		
		HttpRsult hr = new HttpRsult();
		if (result){
			hr.setErrorCode(0);
			hr.setError("绑定成功");
		} else {
			hr.setErrorCode(1);
			hr.setError("绑定失败");
		}
		
		JSONObject jsonObject=new JSONObject(result);
		try {
			response.getWriter().write(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
