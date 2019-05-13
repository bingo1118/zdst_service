package com.cloudfire.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.KeepSystemDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.StyleDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.KeepSystemDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.dao.impl.StyleDaoImpl;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.AllAreaEntity;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.Repeater;
import com.cloudfire.entity.RepeaterBean;
//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.entity.Style;
import com.cloudfire.myservice.DevStateService;
import com.cloudfire.myservice.impl.DevStateServiceImpl;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;
import com.cloudfire.until.WriteJson;

import common.page.Pagination;

import org.springframework.stereotype.Controller;

import redis.clients.jedis.Jedis;

@Controller
public class RepeaterController {
	private KeepSystemDao mKeepSystemDao;
	private AreaDao mAreaDao;
	private StyleDao styleDao;

	public RepeaterController() {
		mKeepSystemDao = new KeepSystemDaoImpl();
		mAreaDao = new AreaDaoImpl();
		styleDao = new StyleDaoImpl();
	}

	@RequestMapping(value = "/queryItems_test.do", method = RequestMethod.POST)
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String cuserid = (String) request.getSession().getAttribute("userId");
		request.getSession().removeAttribute("style");
		if (styleDao.isExistUser(cuserid)) {
			Style style = styleDao.getStyle(cuserid);
			request.getSession().setAttribute("style", style);
		}
		ModelAndView ma = handleRequest2(request, response, cuserid);
		return ma;
	}

	@RequestMapping(value = "/queryItems_test.do", method = RequestMethod.GET)
	public ModelAndView handleRequest2(HttpServletRequest request, HttpServletResponse response, String currentId)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		SmokeDao smokeDao = new SmokeDaoImpl();
		String userId = (String) request.getSession().getAttribute("userId");
		if (StringUtils.isBlank(userId)||userId.equals("gzhr")||userId.equals("15986438401")) {
			modelAndView.setViewName("/welcome");
		} else {
			String privilege = (String) request.getSession().getAttribute("privilege");
			request.getSession().setAttribute("userId", userId);
			List<AreaAndRepeater> itemsList = null;
			int ifShow = 0;
			if (privilege.equals("4")) {
				ifShow = smokeDao.selectIfShow();
				itemsList = mKeepSystemDao.getAreaAndRepeater(null);
			} else {
				itemsList = mKeepSystemDao.getAreaAndRepeater(userId);
			}
			// 相当 于request的setAttribute，在jsp页面中通过itemsList取数据
			modelAndView.addObject("itemsList", itemsList);
			modelAndView.setViewName("/items");
		}
		return modelAndView;
	}

	@RequestMapping(value = "/queryRepeaterInfo.do", method = RequestMethod.GET)
	public ModelAndView queryRepeaterInfo(HttpServletRequest request, HttpServletResponse response, String currentId,
			RepeaterBean query, Integer pageNo, ModelMap model) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		String repeaterMac = request.getParameter("repeaterMac");
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(repeaterMac)) {
			query.setRepeaterMac(repeaterMac.trim());
			params.append("&repeaterMac=").append(query.getRepeaterMac());
			request.setAttribute("repeaterMac", repeaterMac);
		}
		
		/*RePeaterDataDao reDao = new RePeaterDataDaoImpl();
		List<RepeaterBean> repeaterList = new ArrayList<RepeaterBean>();
		repeaterList = reDao.queryRepeaterInfo(query);
		modelAndView.addObject("repeaterList", repeaterList);
		modelAndView.setViewName("/repeaterInfo");
		return modelAndView;*/
		
		DevStateService devStateService = new DevStateServiceImpl();
		Pagination pagination = devStateService.getRepeaterInfoPage(query);
		modelAndView.addObject("pagination", pagination);
		pagination.pageView("/fireSystem/queryRepeaterInfo.do", params.toString());
		modelAndView.setViewName("/repeaterInfo");
		return modelAndView;
	}

	@RequestMapping(value = "/addUser.do")
	public ModelAndView addUser() {
		/*
		 * mAreaDao = new AreaDaoImpl(); Map<Integer,String> parentIds =
		 * mAreaDao.getParentAll(); List<AreaBean> lists = mAreaDao.getAll();
		 */

		ModelAndView modelAndView = new ModelAndView();
		/*
		 * modelAndView.addObject("plist", parentIds);
		 * modelAndView.addObject("lists", lists);
		 */
		// 指定视图
		modelAndView.setViewName("/tianjia");

		return modelAndView;
	}

	@RequestMapping(value = "/tianjiatojson.do", method = RequestMethod.POST)
	public void tianjiatojson2(HttpServletRequest request, HttpServletResponse response) {
		tianjiatojson(request, response);
		// 指定视图
	}

	@RequestMapping(value = "/tianjiatojson.do", method = RequestMethod.GET)
	public void tianjiatojson(HttpServletRequest request, HttpServletResponse response) {
		mAreaDao = new AreaDaoImpl();
		Map<Integer, String> parentIds = mAreaDao.getParentAll();
		List<AreaBean> lists = mAreaDao.getAll();
		AllAreaEntity entity = new AllAreaEntity();
		entity.setAreaBean(lists);
		entity.setParentIds(parentIds);
		List<AllAreaEntity> elist = new ArrayList<AllAreaEntity>();
		elist.add(entity);
		WriteJson writeJson = new WriteJson();
		String jObject = writeJson.getJsonData(elist);
		try {
			response.getWriter().write(jObject);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 指定视图
	}
	
	
	@RequestMapping(value="/getRepeaterState.do",method=RequestMethod.GET)
	public ModelAndView comRepeaterState(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView();
		List<RepeaterBean> repeatersByRedis = new ArrayList<>();
		List<RepeaterBean> repeaters = new ArrayList<>();
		Jedis jedis = RedisConnection.getJedis();
		Set<String> repeaterMacs = RedisOps.getRepeaters(jedis);
		Iterator<String> iterator = repeaterMacs.iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			String repeaterMac=key.substring(1,key.length());
			Repeater rep =(Repeater) RedisOps.getObject(jedis,key);
			RepeaterBean rb = RePeaterDataDaoImpl.getRepeaterState(repeaterMac);
			repeatersByRedis.add(Repeater2RepeaterBean(rep));
			repeaters.add(rb);
		}
		
		mav.addObject("repeatersByRedis", repeatersByRedis);
		mav.addObject("repeaters", repeaters);
		mav.setViewName("/repeaterStateCompare");
		return mav;
	}

	private RepeaterBean Repeater2RepeaterBean(Repeater rep) {
		RepeaterBean rb = new RepeaterBean();
		rb.setRepeaterMac(rep.getRepeaterMac());
		rb.setNetstate(rep.getNetState());
		if (rep.getHeartime()==0) {
			rb.setHeartime("暂无心跳");
		} else {
			rb.setHeartime(GetTime.getTimeByLong(rep.getHeartime()));
		}
		rb.setHoststate(rep.getPowerState());
		if (rep.getPowerChangeTime() ==0){
			rb.setRepeaterTime("暂无电源状态改变");
		} else {
			rb.setRepeaterTime(GetTime.getTimeByLong(rep.getPowerChangeTime()));
		}
		return rb;
	}
	
//	@RequestMapping(value="/getOffMacsByRepeaterMac.do",method=RequestMethod.GET)
//	public ModelAndView getOffMacsByRepeaterMac(HttpServletRequest request,HttpServletResponse response){
//		String repeaterMac = request.getParameter("repeaterMac");
//		ModelAndView mav = new ModelAndView();
//		List<String> offMacs = Utils.getOffMacs(repeaterMac);
//		mav.addObject("offMacs", offMacs);
//		mav.setViewName("offMacs");
//		return mav;
//	}

}
