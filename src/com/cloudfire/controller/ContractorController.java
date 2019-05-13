
package com.cloudfire.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.BuildingDao;
import com.cloudfire.dao.CompanyDao;
import com.cloudfire.dao.IMyDevices;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.BuildingDaoImpl;
import com.cloudfire.dao.impl.CompanyDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.MyDeviceMsgImpl;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.CompanyEntity;
import com.cloudfire.entity.ConditionEntity;
import com.cloudfire.entity.CountextAnalyze;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.MyDevicesEntityQuery;
import com.cloudfire.entity.MyDevicesVo;
import com.cloudfire.entity.PageBeanEntity;
import com.cloudfire.entity.query.MainIndexEntityQuery;
import com.cloudfire.myservice.CompanyService;
import com.cloudfire.myservice.impl.CompanyServiceImpl;
import com.cloudfire.until.Constant;
import com.cloudfire.until.Utils;
import com.cloudfire.until.WriteJson;
import common.page.Pagination;

@Controller
public class ContractorController {
	private LoginDao mLoginDao;
	private AreaDao mAreaDao;
	private IMyDevices mMyDevices;
	
	//我的设备主页
	@RequestMapping(value="/mydevices.do",method=RequestMethod.GET)
	public ModelAndView mydevices(HttpServletRequest request,HttpServletResponse response, String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserId(currentId,privilege);
		modelAndView.addObject("areaBean", areaLists);
		modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_wodeshebei");
		return modelAndView;
	}
	
	//我的设备异步查询
	@RequestMapping(value="/getDeviceByInfo.do",method=RequestMethod.GET)
	public void getDeviceByInfo(HttpServletRequest request,String currentId, HttpServletResponse response ){
		currentId = (String) request.getSession().getAttribute("userId");
		String currentPageStr = request.getParameter("pageNum");	//获取当前页
		String totalPageStr = request.getParameter("totalPage");		//获取总页数
		String totalCountStr = request.getParameter("totalCount");		//获取总记录条数
		String areaId = request.getParameter("areaId");
		
		String deviceNameType = request.getParameter("deviceNameType");
		String deviceMac = request.getParameter("deviceMac");
		String deviceLossUp = request.getParameter("deviceLossUp");
		String J_xl_1 = request.getParameter("J_xl_1");
		String J_xl_2 = request.getParameter("J_xl_2");
		
		ConditionEntity ce = new ConditionEntity();
		if(currentPageStr != null && !"".equals(currentPageStr)){
			int currentPage = Integer.parseInt(currentPageStr); //当前页
			int totalPage = Integer.parseInt(totalPageStr);		//总页数
			int totalCount = Integer.parseInt(totalCountStr);	//总记录数
			if(currentPage<=0) currentPage = 1;
			if(currentPage > totalPage) currentPage = totalPage;  //页的总数
			ce.setCurrentPage(currentPage); //当前页
			ce.setLimit(currentPage);		//设置当前页
			ce.setTotalCount(totalCount);	//设置总记录数
			ce.setTotalPage(totalPage);		//设置总页数
		}else {
			int currentPage = 1; //当前页
			ce.setCurrentPage(currentPage); //当前页
			ce.setLimit(currentPage);
		}
		ce.setDeviceType(deviceNameType);
		ce.setMac(deviceMac);
		ce.setEndDate(J_xl_2);
		ce.setStartDate(J_xl_1);
		ce.setUserId(currentId);
		ce.setAreaId(areaId);
		if(Utils.isNullStr(deviceLossUp)){
			ce.setNetState(Integer.parseInt(deviceLossUp));
		}
		BuildingDao bd = new BuildingDaoImpl();
		List<MyDevicesVo> list = bd.getDevicesByC(ce);
		if(list != null && list.size()>0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(list);
			try {
				//System.out.println(jObject);
				response.getWriter().write(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//首次访问我的设备，则使用这种分页方式。
	@RequestMapping(value="/mydevicesPageQuery.do",method=RequestMethod.GET)
	public ModelAndView mydevicesPageQuery(HttpServletRequest request,HttpServletResponse response, String currentId,MyDevicesEntityQuery query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		String areaId = request.getParameter("areaId");
		if(StringUtils.isNotBlank(areaId)){
			query.setAreaId(areaId);
			params.append("&areaId=").append(areaId);
			request.setAttribute("areaId",query.getAreaId());
		}
		String deviceNameType = request.getParameter("deviceNameType");
		if(StringUtils.isNotBlank(deviceNameType)){
			query.setDevictType(deviceNameType);
			params.append("&deviceNameType=").append(deviceNameType);
			request.setAttribute("deviceNameType", query.getDevictType());
		}
		
		String deviceMac = request.getParameter("deviceMac");
		if(StringUtils.isNotBlank(deviceMac)){
			query.setDevMac(deviceMac);
			params.append("&deviceMac=").append(deviceMac);
			request.setAttribute("deviceMac", query.getDevMac());
		}
		String deviceLossUp = request.getParameter("deviceLossUp");
		if(StringUtils.isNotBlank(deviceLossUp)){
			query.setNetStates(deviceLossUp);
			params.append("&deviceLossUp=").append(deviceLossUp);
			request.setAttribute("deviceLossUp", query.getNetStates());
		}
		String J_xl_1 = request.getParameter("J_xl_1");
		String J_xl_2 = request.getParameter("J_xl_2");
		if(StringUtils.isNotBlank(J_xl_1)){
			query.setJ_xl_1(J_xl_1);
			params.append("&J_xl_1=").append(J_xl_1);
			request.setAttribute("J_xl_1", query.getJ_xl_1());
			if(StringUtils.isNotBlank(J_xl_2)){
				query.setJ_xl_2(J_xl_2);
				params.append("&J_xl_2=").append(J_xl_2);
				request.setAttribute("J_xl_2", query.getJ_xl_2());
			}else{
				query.setJ_xl_2(J_xl_1);
			}
		}else if(StringUtils.isNotBlank(J_xl_2)){
			query.setJ_xl_1(J_xl_2);
			query.setJ_xl_2(J_xl_2);
			params.append("&J_xl_2=").append(J_xl_2);
			request.setAttribute("J_xl_2", query.getJ_xl_2());
		}
		
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if (areaIds != null) {
			mMyDevices = new MyDeviceMsgImpl();
			CompanyService service = new CompanyServiceImpl();
			Pagination pagination = service.selectBudinginfo(query, areaIds);
			pagination.pageView("/fireSystem/mydevicesPageQuery.do", params.toString());
			model.addAttribute("pagination", pagination);
		}
		modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_wodeshebei_one");
		return modelAndView;
	}
	
	//首次访问我的设备，则使用这种分页方式。
	@RequestMapping(value="/mydevicesPage.do",method=RequestMethod.GET)
	public ModelAndView mydevicesPage(HttpServletRequest request,String currentId){
		ModelAndView modelAndView =  new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		String currentPageStr = request.getParameter("currentPage");
		String pageNameCount = request.getParameter("pageNameCount");
//		String method = request.getParameter("getArea");
		String areaIdstr = request.getParameter("areaId"); 
		mLoginDao = new LoginDaoImpl();
		LoginEntity mLoginEntity = mLoginDao.login(currentId);
		if (mLoginEntity!=null) {
			mAreaDao = new AreaDaoImpl();
			mMyDevices = new MyDeviceMsgImpl();
			PageBeanEntity pageBean = new PageBeanEntity();
			pageBean.setAreaId(areaIdstr);
			int zongshu = 0;
			if(Utils.isNullStr(areaIdstr)){
				zongshu = mMyDevices.getCountByUserIdAreaId(currentId,areaIdstr);
			}else{
				zongshu = mMyDevices.getCountByUserId(currentId);	//获取总记录数
			}
			pageBean.setTotalCount(zongshu); //设置总记录数
			int totalPage = 0;
			if(zongshu%10==0){
				totalPage = zongshu/10;
			}else totalPage = zongshu/10 +1;				//获取总页数
			pageBean.setTotalPage(totalPage);				//设置总页数
			if(pageNameCount == null){
				if(currentPageStr != null){
					int currentPage = Integer.parseInt(currentPageStr); //获取当前页
					if(currentPage<=0) currentPage = 1;
					if(currentPage > pageBean.getTotalPage()) currentPage = pageBean.getTotalPage();
					pageBean.setCurrentPage(currentPage); //当前页
				}else {
					int currentPage = 1; //当前页
					pageBean.setCurrentPage(currentPage); //当前页
				}
			}else{
				int currentPage = Integer.parseInt(pageNameCount);
				if(currentPage >pageBean.getTotalPage()) currentPage = pageBean.getTotalPage();
				if(currentPage < 1) currentPage = 1;
				pageBean.setCurrentPage(currentPage);
			}
			List<PageBeanEntity> pageList = new ArrayList<PageBeanEntity>();
			pageList.add(pageBean);
			List<MyDevicesVo> list = new ArrayList<MyDevicesVo>();
			if(Utils.isNullStr(areaIdstr)){
				list = mMyDevices.getMyDevicesByareaIdPageAreaId(areaIdstr,currentId, pageBean.getCurrentPage());
			}else{
				list = mMyDevices.getMyDevicesByareaIdPage(currentId, pageBean.getCurrentPage());
			}
			modelAndView.addObject("pageList",pageList);
			modelAndView.addObject("myDevicesVo",list);
		}
		modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_wodeshebei_one");
		return modelAndView;
	}
	
	//我的设备子页
	@RequestMapping(value="/mydevicesone.do",method=RequestMethod.GET)
	public ModelAndView mydevicesone(HttpServletRequest request,HttpServletResponse response, String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if (areaIds!=null) {
			mMyDevices = new MyDeviceMsgImpl();
			List<MyDevicesVo> list = mMyDevices.getMyDevicesByareaId(areaIds);
			modelAndView.addObject("myDevicesVo",list);
			modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_wodeshebei_one");
		}
		return modelAndView;
	}
	
	//基本信息
	@RequestMapping(value="/contractinfoweb.do",method=RequestMethod.GET)
	public ModelAndView contractinfoweb(HttpServletRequest request,HttpServletResponse response, String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		String infomethod = request.getParameter("methods");
		try {
			request.setCharacterEncoding("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		
		CompanyDao  companyDao = new CompanyDaoImpl();
		List<CompanyEntity> companyList = new ArrayList<CompanyEntity>();
		companyList = companyDao.getAllCompanyBy(currentId);
		
		modelAndView.addObject("comList", companyList);
		
		if("Modifiers".equals(infomethod)||infomethod=="Modifiers"){
			AreaDao ad = new AreaDaoImpl();
			List<AreaBean> alist = new ArrayList<AreaBean>();
			alist = ad.getAll();
			modelAndView.addObject("alist", alist);
		}
		modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_jibenxinxi");
		return modelAndView;
	}
	
	//视频监控
	@RequestMapping(value="/contractvoid.do",method=RequestMethod.GET)
	public ModelAndView contractvoid(HttpServletRequest request,HttpServletResponse response, String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_shipinjiankong");
		return modelAndView;
	}
	
	//跳转到添加信息资料页面，针对建筑机构第一次登录，需要注册新的信息，注册成功则跳转我的页面。
	@RequestMapping(value="/contractaddinfo.do",method=RequestMethod.GET)
	public ModelAndView contractaddinfo(HttpServletRequest request,HttpServletResponse response, String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		List<CompanyEntity> companyList = new ArrayList<CompanyEntity>();
		modelAndView.addObject("comList", companyList);
		modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_tianjiaxinxi");
		return modelAndView;
	}
	
	//跳转到统计分析页面，初始化的时候由AJAX来请求获取数据
	@RequestMapping(value="/contractcount.do",method=RequestMethod.GET)
	public ModelAndView Contractcount(HttpServletRequest request,String currentId,HttpServletResponse response) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_tongjifenxi");
		return modelAndView;
	}
	
	@RequestMapping(value="/selectContractcount.do",method=RequestMethod.GET)
	public void selectContractcount(HttpServletRequest request,String currentId,HttpServletResponse response) throws ServletException, IOException{
		String deviceNameStr = request.getParameter("deviceName");
		int deviceName = 0;
		if(Utils.isNullStr(deviceNameStr)) deviceName = Integer.parseInt(deviceNameStr);
		String J_xl_1 = request.getParameter("J_xl_1");
		String J_xl_2 = request.getParameter("J_xl_2");
		BuildingDao bd = new BuildingDaoImpl();
		CountextAnalyze ca = new CountextAnalyze();
		List<CountextAnalyze> countList = new ArrayList<CountextAnalyze>();
		if(deviceName == 0&&!Utils.isNullStr(J_xl_1)&&!Utils.isNullStr(J_xl_2)){
			int alarmState = bd.getAlarmTypeByUserId(currentId);//火警
			int lossup = bd.getNoLineByUserId(currentId); //掉线
			int faultNub = bd.getFaultByUserId(currentId);//低电压
			ca.setAlarmState(alarmState);
			ca.setFaultNub(faultNub);
			ca.setLossup(lossup);
			ca.setDevName("设备");
			countList.add(ca);
		}else{
			int alarmState = bd.getAlarmNumThBy(deviceName, J_xl_1, J_xl_2, currentId);//火警
			int lossup = bd.getDevLossThBy(deviceName, J_xl_1, J_xl_2, currentId); //掉线
			int faultNub = bd.getDevErrNumThBy(deviceName, J_xl_1, J_xl_2, currentId);//低电压
			ca.setAlarmState(alarmState);
			ca.setFaultNub(faultNub);
			ca.setLossup(lossup);
			if(deviceName == 0){
				ca.setDevName("设备");
			}else if(deviceName == 1){
				ca.setDevName("烟感");
			}else if(deviceName == 2){
				ca.setDevName("燃气");
			}else if(deviceName == 5){
				ca.setDevName("电气");
			}else if(deviceName == 7){
				ca.setDevName("声光");
			}else if(deviceName == 8){
				ca.setDevName("手动");
			}
			countList.add(ca);
		}
		if(countList!=null && countList.size()>0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(countList);
			
			try {
				response.getWriter().write(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}













