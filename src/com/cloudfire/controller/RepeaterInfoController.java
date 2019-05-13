package com.cloudfire.controller;

import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;

import com.cloudfire.dao.KeepSystemDao;
import com.cloudfire.dao.impl.KeepSystemDaoImpl;
import com.cloudfire.entity.KeepEntity;


@Controller
public class RepeaterInfoController{
	private KeepSystemDao mKeepSystemDao;
	
	@RequestMapping(value = "/queryItems.do", method = RequestMethod.GET)
	public ModelAndView listItems(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView =  new ModelAndView();	
		String repeater = request.getParameter("repeater");
		String areaid = request.getParameter("areaid");
		mKeepSystemDao = new KeepSystemDaoImpl();
//				List<KeepEntity> listItems = mKeepSystemDao.getRepeaterData(repeater,null);
		List<KeepEntity> listItems = mKeepSystemDao.getRepeaterData(repeater,null,areaid);
		modelAndView.addObject("listItems", listItems);
		modelAndView.addObject("repeater", repeater);
		modelAndView.setViewName("/list_items");
		return modelAndView;
	}
	@RequestMapping(value = "/smokeItems.do", method = RequestMethod.GET)
	public ModelAndView smokeItems(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView =  new ModelAndView();	
		String currentId = (String) request.getSession().getAttribute("userId");
		String areaid = request.getParameter("areaid");
		String smokeName = request.getParameter("smokeMac");
		String repeater = request.getParameter("repeater");
		if(StringUtils.isBlank(currentId)) {
			//modelAndView.setViewName("/welcome");
			request.getRequestDispatcher("welcome.jsp").forward(request, response);
		}else{
			mKeepSystemDao = new KeepSystemDaoImpl();
//				List<KeepEntity> listItems = mKeepSystemDao.getRepeaterData(repeater,null);
			List<KeepEntity> listItems = mKeepSystemDao.getRepeaterDataBySmoke(smokeName,repeater,areaid);
			modelAndView.addObject("repeater", repeater);
			modelAndView.addObject("listItems", listItems);
			modelAndView.setViewName("/list_items");
		}
		return modelAndView;
	}

}
