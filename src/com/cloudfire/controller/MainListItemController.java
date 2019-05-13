package com.cloudfire.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainListItemController {
	
	@RequestMapping(value="/mainListItem.do",method=RequestMethod.GET)
	public ModelAndView mainListItem(HttpServletRequest request) {
		//·µ»ØModelAndView
		ModelAndView modelAndView =  new ModelAndView();
		
		
		modelAndView.setViewName("/WEB-INF/page/main/main_list_items");

		return modelAndView;
	}

}
