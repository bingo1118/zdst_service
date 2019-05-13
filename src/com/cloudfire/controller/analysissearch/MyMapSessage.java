package com.cloudfire.controller.analysissearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.MapControlDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.MapControlDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.WriteJson;

/**
 * @author cheng
 *2017-3-17
 *ÏÂÎç5:08:20
 */

@Controller
public class MyMapSessage {
	private LoginDao mLoginDao;
	private AlarmInfoDao mAlarmInfoDao;
	
	
	@RequestMapping(value="goToMap.do",method=RequestMethod.GET)
	public void toToMap(String currentId,HttpServletRequest request,HttpServletResponse response,String devMac,String alarmTime,Model model) {

		
		currentId =(String) request.getSession().getAttribute("userId");
		 //devMac = (String)request.getAttribute("devMac");
		if (StringUtils.isBlank(currentId)) {
			/*modelAndView.setViewName("/WEB-INF/page/login/login");*/
			model.addAttribute("/WEB-INF/page/login/login");
		}else {
			mLoginDao = new LoginDaoImpl();
			LoginEntity mLoginEntity = mLoginDao.login(currentId);
			if (mLoginEntity != null) {
				String privilege = mLoginEntity.getPrivilege()+"";
				mAlarmInfoDao = new AlarmInfoDaoImpl();
				//List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfo(areIds);
				List<AlarmInfoEntity> lists = mAlarmInfoDao.getAlarmInfoByDev(devMac,alarmTime);
				if (lists!=null && lists.size()>0) {
					WriteJson writeJson = new WriteJson();
					String object = writeJson.getJsonData(lists);
					try {
						response.getWriter().write(object);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
				}
			}
			model.addAttribute("mapInfo.do");
		}
		
	}
}
