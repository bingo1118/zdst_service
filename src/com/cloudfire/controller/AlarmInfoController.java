package com.cloudfire.controller;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.QP_FaultInfoDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.QP_FaultInfoDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.QP_FaultInfos;
import com.cloudfire.myservice.AlarmInfoService;
import com.cloudfire.myservice.impl.AlarmInfoServiceImpl;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;
import com.cloudfire.until.WriteJson;

import common.page.Pagination;

@Controller
public class AlarmInfoController {
	private AlarmInfoDao mAlarmInfoDao;
	private LoginDao mLoginDao;
	
	@RequestMapping(value="/flashAlarm.do",method=RequestMethod.GET)
	public void flashAlarm(HttpServletRequest request,HttpServletResponse response) {
		//返回ModelAndView
		ModelAndView modelAndView =  new ModelAndView();
		String methods = request.getParameter("methods");
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		List<AlarmInfoEntity> lists = new ArrayList<AlarmInfoEntity>();
		if(Utils.isNullStr(methods)&&methods.equals("fault")){
			lists = mAlarmInfoDao.getAlarmInfoByFault(areaIds);
		}else{
			lists = mAlarmInfoDao.getAlarmInfo(areaIds);
		}
		modelAndView.addObject("alarmInfos", lists);
		WriteJson writeJson = new WriteJson();
        String jObject = writeJson.getJsonData(lists);
		try {
			response.getWriter().write(jObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/getAlarmInfo.do",method=RequestMethod.GET)
	public ModelAndView showAlarmInfo(HttpServletRequest request,HttpServletResponse response, AlarmInfoEntity_PC query, Integer pageNo) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>) request.getSession().getAttribute("areaIds");
		
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		String repeater = request.getParameter("repeaterId");
		String mac=request.getParameter("mac");
		String J_xl_1 = request.getParameter("J_xl_1");
		String J_xl_2 = request.getParameter("J_xl_2");
		
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if(StringUtils.isNotBlank(mac)){
			query.setRepeaterMac(mac);
			params.append("&mac=").append(mac);
			request.setAttribute("mac", query.getRepeaterMac());
		}
		if(StringUtils.isNotBlank(repeater)){
			query.setRepeaterMac(repeater);
			params.append("&repeaterId=").append(repeater);
			request.setAttribute("repeater", query.getRepeaterMac());
		}
		if(StringUtils.isNotBlank(J_xl_1)){
			query.setBegintime(J_xl_1+" 00:00:00");
			params.append("&J_xl_1=").append(J_xl_1);
			request.setAttribute("J_xl_1", J_xl_1);
		}
		if(StringUtils.isNotBlank(J_xl_2)){
			query.setEndtime(J_xl_2+" 23:59:59");
			params.append("&J_xl_2=").append(J_xl_2);
			request.setAttribute("J_xl_2", J_xl_2);
		}
		
		AlarmInfoService service = new AlarmInfoServiceImpl();
		Pagination pagination = service.selectAlarmInfoListByMac(query, mac,areaIds);
		pagination.pageView("/fireSystem/getAlarmInfo.do", params.toString());
		request.getSession().setAttribute("mac", mac);
		modelAndView.addObject("repeaterMac",mac);
		modelAndView.addObject("pagination", pagination);
		modelAndView.setViewName("/WEB-INF/page/devicestates/showAlarmInfo");
		
		return modelAndView;
	}
	
	
	
	@RequestMapping(value="/faultAlarm.do",method=RequestMethod.GET)
	public void faultAlarm(HttpServletRequest request,HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("GBK");
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//返回ModelAndView
		//ModelAndView modelAndView =  new ModelAndView();
		String currentId = (String) request.getSession().getAttribute("userId");
		mLoginDao = new LoginDaoImpl();
		LoginEntity mLoginEntity = mLoginDao.login(currentId);
		if(mLoginEntity!=null){
			String repeaterMac = request.getParameter("repeaterMac");
			String faultCode = request.getParameter("faultCode");
			String faultTime = request.getParameter("alarmTime");
			
			String dealUser = request.getParameter("dealUser");
			String dealText = request.getParameter("dealText");
			try {
				if(Utils.isNullStr(dealUser)){
					byte[] c = request.getParameter("dealUser").getBytes("ISO-8859-1");
					for(int i = 0;i<c.length;i++){
						byte b = c[i];
						if(b == 63)
							break;
						else if(b > 0 )
							continue;
						else if(b <0){
							dealUser = new String(dealUser.getBytes("ISO-8859-1") , "UTF-8");
							break;
						}
					}
				}
				if(Utils.isNullStr(dealText)){
					byte[] c = request.getParameter("dealText").getBytes("ISO-8859-1");
					for(int i = 0;i<c.length;i++){
						byte b = c[i];
						if(b == 63)
							break;
						else if(b > 0 )
							continue;
						else if(b <0){
							dealText = new String(dealText.getBytes("ISO-8859-1") , "UTF-8");
							break;
						}
					}
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mAlarmInfoDao = new AlarmInfoDaoImpl();
			boolean flag = false;
			flag = mAlarmInfoDao.updateFaultInfo(dealUser, dealText, repeaterMac, faultCode, faultTime);
			
			
		}
	}
	


	@RequestMapping(value="dealAlarm.do",method=RequestMethod.GET)
	public void dealAlarm(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("GBK");
		request.setCharacterEncoding("utf-8");
		
		String currentId = (String) request.getSession().getAttribute("userId");
		if(StringUtils.isNotBlank(currentId)){
			String repeaterMac = request.getParameter("repeaterMac");
			String alarmTime = request.getParameter("alarmTime");//根据报警时间来判断处理的是那条报警记录
			String dealUser = request.getParameter("dealUser");
			String dealText = request.getParameter("dealText");
			String alarmType=request.getParameter("alarmType");
		
			if(Utils.isNullStr(dealUser)){
				byte[] c = request.getParameter("dealUser").getBytes("ISO-8859-1");
				for(int i = 0;i<c.length;i++){
					byte b = c[i];
					if(b == 63)
						break;
					else if(b > 0 )
						continue;
					else if(b <0){
						dealUser = new String(dealUser.getBytes("ISO-8859-1") , "UTF-8");
						break;
					}
				}
			}
			if(Utils.isNullStr(dealText)){
				byte[] c = request.getParameter("dealText").getBytes("ISO-8859-1");
				for(int i = 0;i<c.length;i++){
					byte b = c[i];
					if(b == 63)
						break;
					else if(b > 0 )
						continue;
					else if(b <0){
						dealText = new String(dealText.getBytes("ISO-8859-1") , "UTF-8");
						break;
					}
				}
			}
			
			mAlarmInfoDao = new AlarmInfoDaoImpl();
			boolean flag = false;
			flag = mAlarmInfoDao.updateAlarmInfo(repeaterMac,dealText,dealUser,alarmTime,alarmType);
			/*modelAndView.addObject("alarmInfos", true);*/
			WriteJson writeJson = new WriteJson();
	        String jObject = writeJson.getJsonStr(flag);
			response.getWriter().write(jObject);
		
		}else{
			request.getRequestDispatcher("/WEB-INF/page/login/login.jsp").forward(request, response);
		}
		
	}
}
