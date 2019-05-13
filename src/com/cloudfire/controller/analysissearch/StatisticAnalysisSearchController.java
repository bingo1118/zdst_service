package com.cloudfire.controller.analysissearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.dao.query.SearchAnalysisQuery;
import com.cloudfire.dao.query.impl.SearchAnalysisQueryImpl;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.query.SearchAnalysisInfo;
import com.cloudfire.entity.query.SearchAnalysisinfo1;
import com.cloudfire.until.WriteJson;

/**
 * @author cheng
 *2017-4-13
 *下午2:28:43
 */

@Controller
public class StatisticAnalysisSearchController {
	private LoginDao mLoginDao;
	private AreaDao mAreaDao;
	private SmartControlDao mSmartControlDao;
	private SearchAnalysisQuery mSearchAnalysisQuery;
	
	//火灾自动报警系统各种报警数据统计 by LG
	@RequestMapping(value="/searchAnalysisByareaIdAndyearlg.do",method=RequestMethod.POST)
	public void searchAnalysisByareaIdAndyearLg(HttpServletRequest request,HttpServletResponse response){
		//获取设备类型
		//String deviceType="1";
		//获得当前用户的id
		String currentId = (String)request.getSession().getAttribute("userId");
		//获得设备类型
		String devId = (String)request.getSession().getAttribute("devId");
		//区域的id
		String areId = request.getParameter("areaId");
		int areaId = 0;
		if(StringUtils.isNotBlank(areId)){
			areaId = Integer.parseInt(areId);
		}
		String year= request.getParameter("year");
		//如果传过来的年份为null，则设置为当前的年
		if (StringUtils.isBlank(year)) {
			year = Calendar.getInstance().get(Calendar.YEAR) +"";
		}
		mLoginDao = new LoginDaoImpl();
		LoginEntity loginUser = mLoginDao.login(currentId);
		String privilege = loginUser.getPrivilege()+"";
		mAreaDao = new AreaDaoImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		
		List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
		mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
		//List<Integer> info = new ArrayList<>();
		List<List<Integer>> listInfo=new ArrayList<List<Integer>>();
		switch(devId){
		case "1":
			List<Integer> listi202=new ArrayList<Integer>();
			listi202.add(1);
			listi202.add(202);
			List<String> lists202=new ArrayList<String>();
			lists202.add("202");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount202 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists202, devId, query);
				listi202.add(alarmCount202);
			}
			List<Integer> listi193=new ArrayList<Integer>();
			listi193.add(1);
			listi193.add(193);
			List<String> lists193=new ArrayList<String>();
			lists193.add("193");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount193 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists193, devId, query);
				listi193.add(alarmCount193);
			}
			listInfo.add(listi202);
			listInfo.add(listi193);
			break;
		case "2":
			List<Integer> listi2202=new ArrayList<Integer>();
			listi2202.add(2);
			listi2202.add(202);
			List<String> lists2202=new ArrayList<String>();
			lists2202.add("202");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount2202 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists2202, devId, query);
				listi2202.add(alarmCount2202);
			}
			listInfo.add(listi2202);
			break;
		case "5":
			List<Integer> listi43=new ArrayList<Integer>();
			listi43.add(5);
			listi43.add(43);
			List<String> lists143=new ArrayList<String>();
			lists143.add("43");
			lists143.add("143");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount43 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists143, devId, query);
				listi43.add(alarmCount43);
			}
			List<Integer> listi44=new ArrayList<Integer>();
			listi44.add(5);
			listi44.add(44);
			List<String> lists144=new ArrayList<String>();
			lists144.add("44");
			lists144.add("144");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount44 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists144, devId, query);
				listi44.add(alarmCount44);
			}
			List<Integer> listi45=new ArrayList<Integer>();
			listi45.add(5);
			listi45.add(45);
			List<String> lists145=new ArrayList<String>();
			lists145.add("45");
			lists145.add("145");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount45 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists145, devId, query);
				listi45.add(alarmCount45);
			}
			List<Integer> listi46=new ArrayList<Integer>();
			listi46.add(5);
			listi46.add(46);
			List<String> lists146=new ArrayList<String>();
			lists146.add("46");
			lists146.add("146");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount46 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists146, devId, query);
				listi46.add(alarmCount46);
			}
			List<Integer> listi47=new ArrayList<Integer>();
			listi47.add(5);
			listi47.add(47);
			List<String> lists147=new ArrayList<String>();
			lists147.add("47");
			lists147.add("147");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount47 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists147, devId, query);
				listi47.add(alarmCount47);
			}
			List<Integer> listi36=new ArrayList<Integer>();
			listi36.add(5);
			listi36.add(36);
			List<String> lists136=new ArrayList<String>();
			lists136.add("36");
			lists136.add("136");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount36 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists136, devId, query);
				listi36.add(alarmCount36);
			}
			List<Integer> listi48=new ArrayList<Integer>();
			listi48.add(5);
			listi48.add(48);
			List<String> lists148=new ArrayList<String>();
			lists148.add("48");
			lists148.add("148");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount48 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists148, devId, query);
				listi48.add(alarmCount48);
			}
			listInfo.add(listi43);
			listInfo.add(listi44);
			listInfo.add(listi45);
			listInfo.add(listi46);
			listInfo.add(listi47);
			listInfo.add(listi48);
			listInfo.add(listi36);

			break;
		case "7":
			List<Integer> listi203=new ArrayList<Integer>();
			listi203.add(7);
			listi203.add(203);
			List<String> lists203=new ArrayList<String>();
			lists203.add("203");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount203 = mSearchAnalysisQuery.getAlarmCountLg(areaIds,lists203, devId, query);
				listi203.add(alarmCount203);
			}
			listInfo.add(listi203);
			break;
		case "8":
			List<Integer> listi8202=new ArrayList<Integer>();
			listi8202.add(8);
			listi8202.add(202);
			List<String> list8202=new ArrayList<String>();
			list8202.add("202");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount8202 = mSearchAnalysisQuery.getAlarmCountLg(areaIds,list8202, devId, query);
				listi8202.add(alarmCount8202);
			}
			listInfo.add(listi8202);
			break;
		case "10":
			List<Integer> listi217=new ArrayList<Integer>();
			listi217.add(10);
			listi217.add(217);
			List<String> lists217=new ArrayList<String>();
			lists217.add("217");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount217 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists217, devId, query);
				listi217.add(alarmCount217);
			}
			List<Integer> listi210=new ArrayList<Integer>();
			listi210.add(10);
			listi210.add(210);
			List<String> lists210=new ArrayList<String>();
			lists210.add("210");
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount210 = mSearchAnalysisQuery.getAlarmCountLg(areaIds, lists210, devId, query);
				listi210.add(alarmCount210);
			}
			listInfo.add(listi217);
			listInfo.add(listi210);
		default:
			break;
		}
		
		if (listInfo.size()>0 && listInfo!=null) {
			WriteJson json = new WriteJson();
			String object = json.getJsonData(listInfo);
			try {
				response.getWriter().write(object);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		else {
		}
	}

		
		/** 设备1,2,5,7,8 */
		@RequestMapping(value="/searchAnalysisData.do",method=RequestMethod.POST)
		public void getanalysisAll(HttpServletRequest request,HttpServletResponse response,String currentId){
			//定义视图
			ModelAndView mv = new ModelAndView();
			currentId = (String)request.getSession().getAttribute("userId");
			String deviceType = (String) request.getSession().getAttribute("devId");
			int devId = Integer.parseInt(deviceType);
			if (StringUtils.isBlank(currentId)) {
				mv.setViewName("/WEB-INF/page/login/login");
			}else {
				mLoginDao =new LoginDaoImpl();
				mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
				LoginEntity entity = mLoginDao.login(currentId);
				if (entity!=null) {
					String privilege = entity.getPrivilege()+"";
					mAreaDao = new AreaDaoImpl();
					List<String> areIds = mAreaDao.getAreaStr(currentId, privilege);
					switch (devId) {
					case 1:
					case 2:
					case 7:
					case 8:
						List<SearchAnalysisInfo> info = mSearchAnalysisQuery.getAnalysisInfoData(areIds, deviceType);
						if (info.size()>0 && info !=null) {
							WriteJson json = new WriteJson();
							String object = json.getJsonData(info);
							try {
								response.getWriter().write(object);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						break;
					case 5:
						List<SearchAnalysisInfo> info5 = mSearchAnalysisQuery.getAnalysisInfoData(areIds, deviceType);
						if (info5.size()>0 && info5 !=null) {
							WriteJson json = new WriteJson();
							String object = json.getJsonData(info5);
							try {
								response.getWriter().write(object);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						break;
						
					default:
						break;
					}
				}
			}
		}
		
		//默认加载当前的区域来显示一年的报警数量（查的是火警）
		@RequestMapping(value="/searchAnalysisData1.do",method=RequestMethod.POST)
		public void getSearchAnalysisData1(HttpServletRequest request,HttpServletResponse response){
			String currentId = (String)request.getSession().getAttribute("userId");
			String devId = (String)request.getSession().getAttribute("devId");
			String year= request.getParameter("year");
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
			Integer areaId = areaAndName.get(0).getAreaId();
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount202 = mSearchAnalysisQuery.getAlarmCount202(areaIds, devId, query);
				info.add(alarmCount202);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
			}
		}
		
		//默认加载当前的区域来显示一年的报警数量（查的是低电压报警的次数）
		@RequestMapping(value="/searchAnalysisData2.do",method=RequestMethod.POST)
		public void getSearchAnalysisData2(HttpServletRequest request,HttpServletResponse response){
			String currentId = (String)request.getSession().getAttribute("userId");
			String devId = (String)request.getSession().getAttribute("devId");
			String year= request.getParameter("year");
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
			Integer areaId = areaAndName.get(0).getAreaId();
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				int alarmCount193 = mSearchAnalysisQuery.getAlarmCount193(devId, query);						
				info.add(alarmCount193);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
					//System.out.println(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
			}
		}
		
		@RequestMapping(value="/searchAnalysisByareaIdAndyear.do",method=RequestMethod.POST)
		public void searchAnalysisByareaIdAndyear(HttpServletRequest request,HttpServletResponse response){
			//获得当前用户的id
			String currentId = (String)request.getSession().getAttribute("userId");
			//获得设备类型
			String devId = (String)request.getSession().getAttribute("devId");
			//区域的id
			String areId = request.getParameter("areaId");
			int areaId = Integer.parseInt(areId);
			String year= request.getParameter("year");
			//如果传过来的年份为null，则设置为当前的年
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount202 = mSearchAnalysisQuery.getAlarmCount202(areaIds, devId, query);
				info.add(alarmCount202);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			else {
			}
		}
	
		
		@RequestMapping(value="/searchAnalysisByareaIdAndyear1.do",method=RequestMethod.POST)
		public void searchAnalysisByareaIdAndyear1(HttpServletRequest request,HttpServletResponse response){
			//获得当前用户的id
			String currentId = (String)request.getSession().getAttribute("userId");
			//获得设备类型
			String devId = (String)request.getSession().getAttribute("devId");
			//区域的id
			String areId = request.getParameter("areaId");
			int areaId = Integer.parseInt(areId);
			String year= request.getParameter("year");
			//如果传过来的年份为null，则设置为当前的年
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount193 = mSearchAnalysisQuery.getAlarmCount193(devId, query);
				info.add(alarmCount193);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			else {
			}
		}
		
		//默认加载当前的区域来显示一年的报警数量（查的是故障）
		@RequestMapping(value="/searchAnalysisData36.do",method=RequestMethod.POST)
		public void getSearchAnalysisData36(HttpServletRequest request,HttpServletResponse response){
			String currentId = (String)request.getSession().getAttribute("userId");
			String devId = (String)request.getSession().getAttribute("devId");
			String year= request.getParameter("year");
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
			Integer areaId = areaAndName.get(0).getAreaId();
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount36 = mSearchAnalysisQuery.getAlarmCount36(devId, query);
				info.add(alarmCount36);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
			}
		}
		
		@RequestMapping(value="/searchAnalysisData43.do",method=RequestMethod.POST)
		public void getSearchAnalysisData43(HttpServletRequest request,HttpServletResponse response){
			String currentId = (String)request.getSession().getAttribute("userId");
			String devId = (String)request.getSession().getAttribute("devId");
			String year= request.getParameter("year");
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
			Integer areaId = areaAndName.get(0).getAreaId();
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				int alarmCount43 = mSearchAnalysisQuery.getAlarmCount43(devId, query);						
				info.add(alarmCount43);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
					//System.out.println(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
			}
		}
		
		//欠压报警
		@RequestMapping(value="/searchAnalysisData44.do",method=RequestMethod.POST)
		public void getSearchAnalysisData44(HttpServletRequest request,HttpServletResponse response){
			String currentId = (String)request.getSession().getAttribute("userId");
			String devId = (String)request.getSession().getAttribute("devId");
			String year= request.getParameter("year");
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
			Integer areaId = areaAndName.get(0).getAreaId();
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				int alarmCount44 = mSearchAnalysisQuery.getAlarmCount44(devId, query);						
				info.add(alarmCount44);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
					//System.out.println(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
			}
		}
		
		//过流报警
		@RequestMapping(value="/searchAnalysisData45.do",method=RequestMethod.POST)
		public void getSearchAnalysisData45(HttpServletRequest request,HttpServletResponse response){
			String currentId = (String)request.getSession().getAttribute("userId");
			String devId = (String)request.getSession().getAttribute("devId");
			String year= request.getParameter("year");
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
			Integer areaId = areaAndName.get(0).getAreaId();
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				int alarmCount45 = mSearchAnalysisQuery.getAlarmCount45(devId, query);						
				info.add(alarmCount45);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
					//System.out.println(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
			}
		}
		
		//漏电流报警
		@RequestMapping(value="/searchAnalysisData46.do",method=RequestMethod.POST)
		public void getSearchAnalysisData46(HttpServletRequest request,HttpServletResponse response){
			String currentId = (String)request.getSession().getAttribute("userId");
			String devId = (String)request.getSession().getAttribute("devId");
			String year= request.getParameter("year");
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
			Integer areaId = areaAndName.get(0).getAreaId();
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				int alarmCount46 = mSearchAnalysisQuery.getAlarmCount46(devId, query);						
				info.add(alarmCount46);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
					//System.out.println(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
			}
		}
		
		//温度报警
		@RequestMapping(value="/searchAnalysisData47.do",method=RequestMethod.POST)
		public void getSearchAnalysisData47(HttpServletRequest request,HttpServletResponse response){
			String currentId = (String)request.getSession().getAttribute("userId");
			String devId = (String)request.getSession().getAttribute("devId");
			String year= request.getParameter("year");
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
			Integer areaId = areaAndName.get(0).getAreaId();
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				int alarmCount47 = mSearchAnalysisQuery.getAlarmCount47(devId, query);						
				info.add(alarmCount47);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
					//System.out.println(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
			}
		}
		
		@RequestMapping(value="/searchAnalysisByareaIdAndyear36.do",method=RequestMethod.POST)
		public void searchAnalysisByareaIdAndyear36(HttpServletRequest request,HttpServletResponse response){
			//获得当前用户的id
			String currentId = (String)request.getSession().getAttribute("userId");
			//获得设备类型
			String devId = (String)request.getSession().getAttribute("devId");
			//区域的id
			String areId = request.getParameter("areaId");
			int areaId = Integer.parseInt(areId);
			String year= request.getParameter("year");
			//如果传过来的年份为null，则设置为当前的年
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				int alarmCount36 = mSearchAnalysisQuery.getAlarmCount36(devId, query);
				//Integer alarmCount202 = mSearchAnalysisQuery.getAlarmCount36(areaIds, devId, query);
				info.add(alarmCount36);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			else {
			}
		}
		
		@RequestMapping(value="/searchAnalysisByareaIdAndyear43.do",method=RequestMethod.POST)
		public void searchAnalysisByareaIdAndyear43(HttpServletRequest request,HttpServletResponse response){
			//获得当前用户的id
			String currentId = (String)request.getSession().getAttribute("userId");
			//获得设备类型
			String devId = (String)request.getSession().getAttribute("devId");
			//区域的id
			String areId = request.getParameter("areaId");
			int areaId = Integer.parseInt(areId);
			String year= request.getParameter("year");
			//如果传过来的年份为null，则设置为当前的年
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount43 = mSearchAnalysisQuery.getAlarmCount43(devId, query);
				info.add(alarmCount43);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			else {
			}
		}
		
		@RequestMapping(value="/searchAnalysisByareaIdAndyear44.do",method=RequestMethod.POST)
		public void searchAnalysisByareaIdAndyear44(HttpServletRequest request,HttpServletResponse response){
			//获得当前用户的id
			String currentId = (String)request.getSession().getAttribute("userId");
			//获得设备类型
			String devId = (String)request.getSession().getAttribute("devId");
			//区域的id
			String areId = request.getParameter("areaId");
			int areaId = Integer.parseInt(areId);
			String year= request.getParameter("year");
			//如果传过来的年份为null，则设置为当前的年
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount44 = mSearchAnalysisQuery.getAlarmCount44( devId, query);
				info.add(alarmCount44);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			else {
			}
		}
		
		@RequestMapping(value="/searchAnalysisByareaIdAndyear45.do",method=RequestMethod.POST)
		public void searchAnalysisByareaIdAndyear45(HttpServletRequest request,HttpServletResponse response){
			//获得当前用户的id
			String currentId = (String)request.getSession().getAttribute("userId");
			//获得设备类型
			String devId = (String)request.getSession().getAttribute("devId");
			//区域的id
			String areId = request.getParameter("areaId");
			int areaId = Integer.parseInt(areId);
			String year= request.getParameter("year");
			//如果传过来的年份为null，则设置为当前的年
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount45 = mSearchAnalysisQuery.getAlarmCount45( devId, query);
				info.add(alarmCount45);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			else {
			}
		}
		
		@RequestMapping(value="/searchAnalysisByareaIdAndyear46.do",method=RequestMethod.POST)
		public void searchAnalysisByareaIdAndyear46(HttpServletRequest request,HttpServletResponse response){
			//获得当前用户的id
			String currentId = (String)request.getSession().getAttribute("userId");
			//获得设备类型
			String devId = (String)request.getSession().getAttribute("devId");
			//区域的id
			String areId = request.getParameter("areaId");
			int areaId = Integer.parseInt(areId);
			String year= request.getParameter("year");
			//如果传过来的年份为null，则设置为当前的年
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount46 = mSearchAnalysisQuery.getAlarmCount46(devId, query);
				info.add(alarmCount46);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			else {
			}
		}
		
		@RequestMapping(value="/searchAnalysisByareaIdAndyear47.do",method=RequestMethod.POST)
		public void searchAnalysisByareaIdAndyear47(HttpServletRequest request,HttpServletResponse response){
			//获得当前用户的id
			String currentId = (String)request.getSession().getAttribute("userId");
			//获得设备类型
			String devId = (String)request.getSession().getAttribute("devId");
			//区域的id
			String areId = request.getParameter("areaId");
			int areaId = Integer.parseInt(areId);
			String year= request.getParameter("year");
			//如果传过来的年份为null，则设置为当前的年
			if (StringUtils.isBlank(year)) {
				year = Calendar.getInstance().get(Calendar.YEAR) +"";
			}
			mLoginDao = new LoginDaoImpl();
			LoginEntity loginUser = mLoginDao.login(currentId);
			String privilege = loginUser.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			mSmartControlDao = new SmartControlDaoImpl();
			
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
			List<Integer> info = new ArrayList<>();
			//Map<String, List<Integer>> map = new HashedMap();
			for(int i=1;i<=12;i++){
				SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
				query.setYear(year);
				query.setMonth(i+"");
				query.setAreaId(areaId);
				Integer alarmCount47 = mSearchAnalysisQuery.getAlarmCount47(devId, query);
				info.add(alarmCount47);
			
			}
			if (info.size()>0 && info!=null) {
				WriteJson json = new WriteJson();
				String object = json.getJsonData(info);
				try {
					response.getWriter().write(object);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
			else {
			}
		}
		
		/** *******************默认加载的各个的平均值****************************/
		//欠压报警
				@RequestMapping(value="/searchAnalysisData43and44avg.do",method=RequestMethod.POST)
				public void getSearchAnalysisData43and44avg(HttpServletRequest request,HttpServletResponse response){
					String currentId = (String)request.getSession().getAttribute("userId");
					String devId = (String)request.getSession().getAttribute("devId");
					String year= request.getParameter("year");
					if (StringUtils.isBlank(year)) {
						year = Calendar.getInstance().get(Calendar.YEAR) +"";
					}
					mLoginDao = new LoginDaoImpl();
					LoginEntity loginUser = mLoginDao.login(currentId);
					String privilege = loginUser.getPrivilege()+"";
					mAreaDao = new AreaDaoImpl();
					mSmartControlDao = new SmartControlDaoImpl();
					
					List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
					List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
					Integer areaId = areaAndName.get(0).getAreaId();
					mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
					List<Float> info = new ArrayList<>();
					//Map<String, List<Integer>> map = new HashedMap();
					for(int i=1;i<=12;i++){
						SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
						query.setYear(year);
						query.setMonth(i+"");
						query.setAreaId(areaId);
						float alarmCount43and44avg = mSearchAnalysisQuery.getAlarmCount43and44avg(devId, query);						
						info.add(alarmCount43and44avg);
					
					}
					if (info.size()>0 && info!=null) {
						WriteJson json = new WriteJson();
						String object = json.getJsonData(info);
						try {
							response.getWriter().write(object);
							//System.out.println(object);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
					}
				}
				
				//过流报警
				@RequestMapping(value="/searchAnalysisData45avg.do",method=RequestMethod.POST)
				public void getSearchAnalysisData45avg(HttpServletRequest request,HttpServletResponse response){
					String currentId = (String)request.getSession().getAttribute("userId");
					String devId = (String)request.getSession().getAttribute("devId");
					String year= request.getParameter("year");
					if (StringUtils.isBlank(year)) {
						year = Calendar.getInstance().get(Calendar.YEAR) +"";
					}
					mLoginDao = new LoginDaoImpl();
					LoginEntity loginUser = mLoginDao.login(currentId);
					String privilege = loginUser.getPrivilege()+"";
					mAreaDao = new AreaDaoImpl();
					mSmartControlDao = new SmartControlDaoImpl();
					
					List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
					List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
					Integer areaId = areaAndName.get(0).getAreaId();
					mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
					List<Float> info = new ArrayList<>();
					//Map<String, List<Integer>> map = new HashedMap();
					for(int i=1;i<=12;i++){
						SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
						query.setYear(year);
						query.setMonth(i+"");
						query.setAreaId(areaId);
						Float alarmCount45avg = mSearchAnalysisQuery.getAlarmCount45avg(devId, query);						
						info.add(alarmCount45avg);
					
					}
					if (info.size()>0 && info!=null) {
						WriteJson json = new WriteJson();
						String object = json.getJsonData(info);
						try {
							response.getWriter().write(object);
							//System.out.println(object);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
					}
				}
				
				//漏电流报警
				@RequestMapping(value="/searchAnalysisData46avg.do",method=RequestMethod.POST)
				public void getSearchAnalysisData46avg(HttpServletRequest request,HttpServletResponse response){
					String currentId = (String)request.getSession().getAttribute("userId");
					String devId = (String)request.getSession().getAttribute("devId");
					String year= request.getParameter("year");
					if (StringUtils.isBlank(year)) {
						year = Calendar.getInstance().get(Calendar.YEAR) +"";
					}
					mLoginDao = new LoginDaoImpl();
					LoginEntity loginUser = mLoginDao.login(currentId);
					String privilege = loginUser.getPrivilege()+"";
					mAreaDao = new AreaDaoImpl();
					mSmartControlDao = new SmartControlDaoImpl();
					
					List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
					List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
					Integer areaId = areaAndName.get(0).getAreaId();
					mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
					List<Float> info = new ArrayList<>();
					//Map<String, List<Integer>> map = new HashedMap();
					for(int i=1;i<=12;i++){
						SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
						query.setYear(year);
						query.setMonth(i+"");
						query.setAreaId(areaId);
						Float alarmCount46avg = mSearchAnalysisQuery.getAlarmCount46avg(devId, query);						
						info.add(alarmCount46avg);
					
					}
					if (info.size()>0 && info!=null) {
						WriteJson json = new WriteJson();
						String object = json.getJsonData(info);
						try {
							response.getWriter().write(object);
							//System.out.println(object);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
					}
				}
				
				//温度报警
				@RequestMapping(value="/searchAnalysisData47avg.do",method=RequestMethod.POST)
				public void getSearchAnalysisData47avg(HttpServletRequest request,HttpServletResponse response){
					String currentId = (String)request.getSession().getAttribute("userId");
					String devId = (String)request.getSession().getAttribute("devId");
					String year= request.getParameter("year");
					if (StringUtils.isBlank(year)) {
						year = Calendar.getInstance().get(Calendar.YEAR) +"";
					}
					mLoginDao = new LoginDaoImpl();
					LoginEntity loginUser = mLoginDao.login(currentId);
					String privilege = loginUser.getPrivilege()+"";
					mAreaDao = new AreaDaoImpl();
					mSmartControlDao = new SmartControlDaoImpl();
					
					List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
					List<AreaAndRepeater> areaAndName = mSmartControlDao.getAreaAndRepeater(devId,areaIds );
					Integer areaId = areaAndName.get(0).getAreaId();
					mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
					List<Float> info = new ArrayList<>();
					//Map<String, List<Integer>> map = new HashedMap();
					for(int i=1;i<=12;i++){
						SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
						query.setYear(year);
						query.setMonth(i+"");
						query.setAreaId(areaId);
						Float alarmCount47avg = mSearchAnalysisQuery.getAlarmCount47avg(devId, query);						
						info.add(alarmCount47avg);
					
					}
					if (info.size()>0 && info!=null) {
						WriteJson json = new WriteJson();
						String object = json.getJsonData(info);
						try {
							response.getWriter().write(object);
							//System.out.println(object);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
					}
				}
				
				@RequestMapping(value="/searchAnalysisByareaIdAndyear43and44avg.do",method=RequestMethod.POST)
				public void searchAnalysisByareaIdAndyear43and44avg(HttpServletRequest request,HttpServletResponse response){
					//获得当前用户的id
					String currentId = (String)request.getSession().getAttribute("userId");
					//获得设备类型
					String devId = (String)request.getSession().getAttribute("devId");
					//区域的id
					String areId = request.getParameter("areaId");
					int areaId = Integer.parseInt(areId);
					String year= request.getParameter("year");
					//如果传过来的年份为null，则设置为当前的年
					if (StringUtils.isBlank(year)) {
						year = Calendar.getInstance().get(Calendar.YEAR) +"";
					}
					mLoginDao = new LoginDaoImpl();
					LoginEntity loginUser = mLoginDao.login(currentId);
					String privilege = loginUser.getPrivilege()+"";
					mAreaDao = new AreaDaoImpl();
					mSmartControlDao = new SmartControlDaoImpl();
					
					List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
					mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
					List<Float> info = new ArrayList<>();
					//Map<String, List<Integer>> map = new HashedMap();
					for(int i=1;i<=12;i++){
						SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
						query.setYear(year);
						query.setMonth(i+"");
						query.setAreaId(areaId);
						Float alarmCount43and44 = mSearchAnalysisQuery.getAlarmCount43and44avg( devId, query);
						info.add(alarmCount43and44);
					
					}
					if (info.size()>0 && info!=null) {
						WriteJson json = new WriteJson();
						String object = json.getJsonData(info);
						try {
							response.getWriter().write(object);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}	
					else {
					}
				}
				
				@RequestMapping(value="/searchAnalysisByareaIdAndyear45avg.do",method=RequestMethod.POST)
				public void searchAnalysisByareaIdAndyear45avg(HttpServletRequest request,HttpServletResponse response){
					//获得当前用户的id
					String currentId = (String)request.getSession().getAttribute("userId");
					//获得设备类型
					String devId = (String)request.getSession().getAttribute("devId");
					//区域的id
					String areId = request.getParameter("areaId");
					int areaId = Integer.parseInt(areId);
					String year= request.getParameter("year");
					//如果传过来的年份为null，则设置为当前的年
					if (StringUtils.isBlank(year)) {
						year = Calendar.getInstance().get(Calendar.YEAR) +"";
					}
					mLoginDao = new LoginDaoImpl();
					LoginEntity loginUser = mLoginDao.login(currentId);
					String privilege = loginUser.getPrivilege()+"";
					mAreaDao = new AreaDaoImpl();
					mSmartControlDao = new SmartControlDaoImpl();
					
					List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
					mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
					List<Float> info = new ArrayList<>();
					//Map<String, List<Integer>> map = new HashedMap();
					for(int i=1;i<=12;i++){
						SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
						query.setYear(year);
						query.setMonth(i+"");
						query.setAreaId(areaId);
						Float alarmCount45 = mSearchAnalysisQuery.getAlarmCount45avg( devId, query);
						info.add(alarmCount45);
					
					}
					if (info.size()>0 && info!=null) {
						WriteJson json = new WriteJson();
						String object = json.getJsonData(info);
						try {
							response.getWriter().write(object);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}	
					else {
					}
				}
				
				@RequestMapping(value="/searchAnalysisByareaIdAndyear46avg.do",method=RequestMethod.POST)
				public void searchAnalysisByareaIdAndyear46avg(HttpServletRequest request,HttpServletResponse response){
					//获得当前用户的id
					String currentId = (String)request.getSession().getAttribute("userId");
					//获得设备类型
					String devId = (String)request.getSession().getAttribute("devId");
					//区域的id
					String areId = request.getParameter("areaId");
					int areaId = Integer.parseInt(areId);
					String year= request.getParameter("year");
					//如果传过来的年份为null，则设置为当前的年
					if (StringUtils.isBlank(year)) {
						year = Calendar.getInstance().get(Calendar.YEAR) +"";
					}
					mLoginDao = new LoginDaoImpl();
					LoginEntity loginUser = mLoginDao.login(currentId);
					String privilege = loginUser.getPrivilege()+"";
					mAreaDao = new AreaDaoImpl();
					mSmartControlDao = new SmartControlDaoImpl();
					
					List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
					mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
					List<Float> info = new ArrayList<>();
					//Map<String, List<Integer>> map = new HashedMap();
					for(int i=1;i<=12;i++){
						SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
						query.setYear(year);
						query.setMonth(i+"");
						query.setAreaId(areaId);
						Float alarmCount46 = mSearchAnalysisQuery.getAlarmCount46avg(devId, query);
						info.add(alarmCount46);
					
					}
					if (info.size()>0 && info!=null) {
						WriteJson json = new WriteJson();
						String object = json.getJsonData(info);
						try {
							response.getWriter().write(object);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}	
					else {
					}
				}
				
				@RequestMapping(value="/searchAnalysisByareaIdAndyear47avg.do",method=RequestMethod.POST)
				public void searchAnalysisByareaIdAndyear47avg(HttpServletRequest request,HttpServletResponse response){
					//获得当前用户的id
					String currentId = (String)request.getSession().getAttribute("userId");
					//获得设备类型
					String devId = (String)request.getSession().getAttribute("devId");
					//区域的id
					String areId = request.getParameter("areaId");
					int areaId = Integer.parseInt(areId);
					String year= request.getParameter("year");
					//如果传过来的年份为null，则设置为当前的年
					if (StringUtils.isBlank(year)) {
						year = Calendar.getInstance().get(Calendar.YEAR) +"";
					}
					mLoginDao = new LoginDaoImpl();
					LoginEntity loginUser = mLoginDao.login(currentId);
					String privilege = loginUser.getPrivilege()+"";
					mAreaDao = new AreaDaoImpl();
					mSmartControlDao = new SmartControlDaoImpl();
					
					List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
					mSearchAnalysisQuery = new SearchAnalysisQueryImpl();
					List<Float> info = new ArrayList<>();
					//Map<String, List<Integer>> map = new HashedMap();
					for(int i=1;i<=12;i++){
						SearchAnalysisinfo1 query = new SearchAnalysisinfo1();
						query.setYear(year);
						query.setMonth(i+"");
						query.setAreaId(areaId);
						Float alarmCount47 = mSearchAnalysisQuery.getAlarmCount47avg(devId, query);
						info.add(alarmCount47);
					
					}
					if (info.size()>0 && info!=null) {
						WriteJson json = new WriteJson();
						String object = json.getJsonData(info);
						try {
							response.getWriter().write(object);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}	
					else {
					}
				}		
				
		
}	
