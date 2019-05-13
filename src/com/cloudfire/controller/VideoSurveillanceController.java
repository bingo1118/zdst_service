package com.cloudfire.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.util.JSONUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.VideoDao;
import com.cloudfire.dao.impl.VideoDaoImpl;
import com.cloudfire.entity.VideosInArea;


@Controller
public class VideoSurveillanceController {
	
	@RequestMapping(value="/videoSurveillance.do",method=RequestMethod.POST)
	public ModelAndView videoSurveillance2(HttpServletRequest request,String currentId,HttpServletResponse response){
		ModelAndView ma = videoSurveillance(request,currentId,response);
		return ma;
	}
	@RequestMapping(value="/videoSurveillance.do",method=RequestMethod.GET)
	public ModelAndView videoSurveillance(HttpServletRequest request,String currentId,HttpServletResponse response){
		ModelAndView modelAndView =  new ModelAndView();
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>) request.getSession().getAttribute("areaIds");
		if (areaIds == null || areaIds.size() == 0 ) {
			modelAndView.addObject("error_message", "你还没有开通区域，请联系管理员");
			modelAndView.setViewName("/WEB-INF/page/error/no_area");
		} else {
			VideoDao vd = new VideoDaoImpl();
			List<VideosInArea> lstvideo =  vd.getVideosInArea(areaIds);
			if (lstvideo.size() == 0) {
				modelAndView.addObject("error_message", "你的区域里面没有摄像头，请联系管理员");
				modelAndView.setViewName("/WEB-INF/page/error/no_camera");
			} else {
				modelAndView.addObject("lstvideo", lstvideo);
				modelAndView.setViewName("/WEB-INF/page/main/videoSurveillance");
			}
		}
		return modelAndView;
	}
	
	@RequestMapping(value="getname")
	@ResponseBody
	public String getName(HttpServletRequest request,HttpServletResponse respoonse){
		String callback = request.getParameter("callback");
		String callRes = "";
		try {
			String hrsstName = (String)request.getSession().getAttribute("hrsstName");
			if(StringUtils.isBlank(hrsstName)){
				return callback+"()";
			}
			callRes = callback+"("+JSONUtils.isString(hrsstName)+")";
		} catch (Exception e) {
			callRes = callback+"()";
			e.printStackTrace();
		}
		return callRes;
	}
	
	@RequestMapping(value="setname")
	@ResponseBody
	public String setName(HttpServletRequest request,HttpServletResponse respoonse){
		 String callRes = "";
        //获取回调函数名
        String callback = request.getParameter("callback");
        String data = request.getParameter("data");
        if(StringUtils.isBlank(data)){
            request.getSession().invalidate();
            return callback + "("+JSONUtils.isString("ok")+")";
        }
		try {
	            request.getSession().setAttribute("username", data);
	            callRes = callback + "("+JSONUtils.isString("ok")+")";
	        } catch (Exception e) {
	            callRes = callback + "()";
	            request.getSession().invalidate();
	            e.printStackTrace();
	        }
	        return callRes;
	}
}
