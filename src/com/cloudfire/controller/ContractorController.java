
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
	
	//�ҵ��豸��ҳ
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
	
	//�ҵ��豸�첽��ѯ
	@RequestMapping(value="/getDeviceByInfo.do",method=RequestMethod.GET)
	public void getDeviceByInfo(HttpServletRequest request,String currentId, HttpServletResponse response ){
		currentId = (String) request.getSession().getAttribute("userId");
		String currentPageStr = request.getParameter("pageNum");	//��ȡ��ǰҳ
		String totalPageStr = request.getParameter("totalPage");		//��ȡ��ҳ��
		String totalCountStr = request.getParameter("totalCount");		//��ȡ�ܼ�¼����
		String areaId = request.getParameter("areaId");
		
		String deviceNameType = request.getParameter("deviceNameType");
		String deviceMac = request.getParameter("deviceMac");
		String deviceLossUp = request.getParameter("deviceLossUp");
		String J_xl_1 = request.getParameter("J_xl_1");
		String J_xl_2 = request.getParameter("J_xl_2");
		
		ConditionEntity ce = new ConditionEntity();
		if(currentPageStr != null && !"".equals(currentPageStr)){
			int currentPage = Integer.parseInt(currentPageStr); //��ǰҳ
			int totalPage = Integer.parseInt(totalPageStr);		//��ҳ��
			int totalCount = Integer.parseInt(totalCountStr);	//�ܼ�¼��
			if(currentPage<=0) currentPage = 1;
			if(currentPage > totalPage) currentPage = totalPage;  //ҳ������
			ce.setCurrentPage(currentPage); //��ǰҳ
			ce.setLimit(currentPage);		//���õ�ǰҳ
			ce.setTotalCount(totalCount);	//�����ܼ�¼��
			ce.setTotalPage(totalPage);		//������ҳ��
		}else {
			int currentPage = 1; //��ǰҳ
			ce.setCurrentPage(currentPage); //��ǰҳ
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
	
	//�״η����ҵ��豸����ʹ�����ַ�ҳ��ʽ��
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
	
	//�״η����ҵ��豸����ʹ�����ַ�ҳ��ʽ��
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
				zongshu = mMyDevices.getCountByUserId(currentId);	//��ȡ�ܼ�¼��
			}
			pageBean.setTotalCount(zongshu); //�����ܼ�¼��
			int totalPage = 0;
			if(zongshu%10==0){
				totalPage = zongshu/10;
			}else totalPage = zongshu/10 +1;				//��ȡ��ҳ��
			pageBean.setTotalPage(totalPage);				//������ҳ��
			if(pageNameCount == null){
				if(currentPageStr != null){
					int currentPage = Integer.parseInt(currentPageStr); //��ȡ��ǰҳ
					if(currentPage<=0) currentPage = 1;
					if(currentPage > pageBean.getTotalPage()) currentPage = pageBean.getTotalPage();
					pageBean.setCurrentPage(currentPage); //��ǰҳ
				}else {
					int currentPage = 1; //��ǰҳ
					pageBean.setCurrentPage(currentPage); //��ǰҳ
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
	
	//�ҵ��豸��ҳ
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
	
	//������Ϣ
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
	
	//��Ƶ���
	@RequestMapping(value="/contractvoid.do",method=RequestMethod.GET)
	public ModelAndView contractvoid(HttpServletRequest request,HttpServletResponse response, String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_shipinjiankong");
		return modelAndView;
	}
	
	//��ת�������Ϣ����ҳ�棬��Խ���������һ�ε�¼����Ҫע���µ���Ϣ��ע��ɹ�����ת�ҵ�ҳ�档
	@RequestMapping(value="/contractaddinfo.do",method=RequestMethod.GET)
	public ModelAndView contractaddinfo(HttpServletRequest request,HttpServletResponse response, String currentId) throws ServletException, IOException{
		ModelAndView modelAndView =  new ModelAndView();
		List<CompanyEntity> companyList = new ArrayList<CompanyEntity>();
		modelAndView.addObject("comList", companyList);
		modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_tianjiaxinxi");
		return modelAndView;
	}
	
	//��ת��ͳ�Ʒ���ҳ�棬��ʼ����ʱ����AJAX�������ȡ����
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
			int alarmState = bd.getAlarmTypeByUserId(currentId);//��
			int lossup = bd.getNoLineByUserId(currentId); //����
			int faultNub = bd.getFaultByUserId(currentId);//�͵�ѹ
			ca.setAlarmState(alarmState);
			ca.setFaultNub(faultNub);
			ca.setLossup(lossup);
			ca.setDevName("�豸");
			countList.add(ca);
		}else{
			int alarmState = bd.getAlarmNumThBy(deviceName, J_xl_1, J_xl_2, currentId);//��
			int lossup = bd.getDevLossThBy(deviceName, J_xl_1, J_xl_2, currentId); //����
			int faultNub = bd.getDevErrNumThBy(deviceName, J_xl_1, J_xl_2, currentId);//�͵�ѹ
			ca.setAlarmState(alarmState);
			ca.setFaultNub(faultNub);
			ca.setLossup(lossup);
			if(deviceName == 0){
				ca.setDevName("�豸");
			}else if(deviceName == 1){
				ca.setDevName("�̸�");
			}else if(deviceName == 2){
				ca.setDevName("ȼ��");
			}else if(deviceName == 5){
				ca.setDevName("����");
			}else if(deviceName == 7){
				ca.setDevName("����");
			}else if(deviceName == 8){
				ca.setDevName("�ֶ�");
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













