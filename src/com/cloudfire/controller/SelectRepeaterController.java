package com.cloudfire.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;

import com.cloudfire.dao.KeepSystemDao;
import com.cloudfire.dao.impl.KeepSystemDaoImpl;
import com.cloudfire.entity.KeepEntity;
import com.gexin.fastjson.JSON;

@Controller
public class SelectRepeaterController{
	private KeepSystemDao mKeepSystemDao;

	
	@RequestMapping(value = "/selectItems.do", method = RequestMethod.GET)
	public void queryItem(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		
		String repeater = request.getParameter("repeaterMac").trim();
		String smokeMac = request.getParameter("smokeMac").trim();
		
		mKeepSystemDao = new KeepSystemDaoImpl();
		List<KeepEntity> listItems = mKeepSystemDao.getRepeaterData(repeater,smokeMac, areaIds);
        PrintWriter out = null;  
        response.setContentType("application/json");
        Object obj = JSON.toJSON(listItems);
        try {  
            out = response.getWriter();
            if(listItems!=null){
            	out.write(obj.toString());
            }else{
            	out.write("[]");
            }
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
//		//返回ModelAndView
//		ModelAndView modelAndView =  new ModelAndView();
//		//相当 于request的setAttribut，在jsp页面中通过itemsList取数据
//		modelAndView.addObject(obj.toString());
//		//指定视图
//		modelAndView.setViewName("selectItems.do");

//		return modelAndView;
        
	}

}
