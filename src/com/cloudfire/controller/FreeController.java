package com.cloudfire.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.FreeDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.FreeDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.PayRecord;
import com.cloudfire.entity.Recharge;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.SmokePay;
import com.cloudfire.entity.UserEntity;
import com.cloudfire.entity.query.DeviceType;
import com.cloudfire.entity.query.DevicesFee;
import com.cloudfire.entity.query.SmokeQuery;
import com.cloudfire.myservice.FreeService;
import com.cloudfire.myservice.SmokeService;
import com.cloudfire.myservice.impl.FreeServiceImpl;
import com.cloudfire.myservice.impl.SmokeServiceImpl;
import com.cloudfire.until.Constant;
import com.cloudfire.until.DateUtil;

import common.page.Pagination;

@Controller
public class FreeController {
	
	private AreaDao mAreaDao;
	private LoginDao mLoginDao;
	private SmartControlDao mSmartControlDao;
	private SmokeService service;
	private FreeDao freeDao;
	private FreeService freeService;
	
	@RequestMapping(value="/smokeList.do",method=RequestMethod.GET)
	public ModelAndView allSmoke(HttpServletRequest request,HttpServletResponse response, SmokeQuery query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		service = new SmokeServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();

		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		ModelAndView modelAndView = new ModelAndView();
		
		String mac = request.getParameter("mac");
		String placeTypeId = request.getParameter("placeTypeId");
		String areaId = request.getParameter("areaId");
		String deviceType = request.getParameter("deviceType");
	
		//������ת����һҳ����
		StringBuilder params = new StringBuilder();
		params.append("1=1");
        if (StringUtils.isNotBlank(mac)) {
        	query.setMac(mac);
			params.append("&mac=").append(query.getMac());
			model.addAttribute("mac", mac);
		}if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
			params.append("&deviceType=").append(deviceType);
			model.addAttribute("deviceType", deviceType);
		}
	    if (StringUtils.isNotBlank(areaId)) {
	    	query.setAreaName(areaId);
			params.append("&areaId=").append(areaId);
			model.addAttribute("areaId", query.getAreaName());
		}if (StringUtils.isNotBlank(placeTypeId)) {
			query.setPlaceType(placeTypeId);
			params.append("&placeTypeId=").append(placeTypeId);
			model.addAttribute("placeTypeId", placeTypeId);
		}
		
		InfoManagerDao infoDao = new InfoManagerDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null&&areaIds.size()>0){
			Pagination pagination = service.getSmokeListPage(areaIds, query);
			pagination.pageView("/fireSystem/smokeList.do", params.toString());
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
			List<DeviceType> deviceAndName = mSmartControlDao. getAllDeviceTypeAndName(areaIds);
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			model.addAttribute("pagination", pagination);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);				
			modelAndView.addObject("deviceAndName", deviceAndName);
			modelAndView.addAllObjects(modelMap);
		}
		modelAndView.setViewName("/recharge_pay");
		return modelAndView;
	}
	
	
	@RequestMapping(value="/detailList.do",method=RequestMethod.GET)
	public ModelAndView detailList(HttpServletRequest request,HttpServletResponse response, SmokeQuery query,ModelMap model,Integer pageNo) throws ServletException, IOException{
		mSmartControlDao = new SmartControlDaoImpl();
		freeDao = new FreeDaoImpl();
		freeService = new FreeServiceImpl();

		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		ModelAndView modelAndView = new ModelAndView();
		String mac = request.getParameter("mac");//�豸ID
		String placeTypeId = request.getParameter("placeTypeId");//��ҵ����
		String areaId = request.getParameter("areaId");//����
		String deviceType = request.getParameter("deviceType");//�豸����
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(mac)) {
			query.setMac(mac);
			params.append("&mac=").append(mac);
			model.addAttribute("mac", mac);
		}if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
			params.append("&deviceType=").append(deviceType);
			model.addAttribute("deviceType", deviceType);
		}
	    if (StringUtils.isNotBlank(areaId)) {
	    	query.setAreaName(areaId);
			params.append("&areaId=").append(areaId);
			model.addAttribute("areaId", areaId);
		}if (StringUtils.isNotBlank(placeTypeId)) {
			query.setPlaceType(placeTypeId);
			params.append("&placeTypeId=").append(placeTypeId);
			model.addAttribute("placeTypeId", placeTypeId);
		}
		
		InfoManagerDao infoDao = new InfoManagerDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if(areaIds!=null){
			Pagination pagination = freeService.getDetailPage(areaIds, query);
			pagination.pageView("/fireSystem/detailList.do", params.toString());
			List<ShopTypeEntity> shopList = infoDao.getShopTypeEntity();
			List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
			List<DeviceType> deviceAndName = mSmartControlDao. getAllDeviceTypeAndName(areaIds);
			Map<String,List<?>> modelMap = new HashMap<String,List<?>>();
			model.addAttribute("pagination", pagination);
			modelMap.put("shopList", shopList);
			modelMap.put("areaAndRepeaters", areaAndRepeaters);				
			modelAndView.addObject("deviceAndName", deviceAndName);
			modelAndView.addAllObjects(modelMap);
		}
		modelAndView.setViewName("/recharge_record");
		return modelAndView;
	}
	
	@RequestMapping(value="updateFree.do",method=RequestMethod.GET)
	public void updateFree(HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException {
		String userId = (String)request.getSession().getAttribute("userId");
		String Macs = request.getParameter("smokeMacs");
		String[] smokeMacs = Macs.split(",");
		freeDao = new FreeDaoImpl();
		String now = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(new Date(System.currentTimeMillis()));
		//һ���ʱ��
		int result = 0;
		PayRecord payRecord = new PayRecord();
		for (int i = 0; i < smokeMacs.length; i++) {
			Recharge recharge = freeDao.getRechargeByMac(smokeMacs[i]);
			if(recharge.getSetFee() == null || recharge.getSetFee().equals("")) {//���ĳ���豸δ�����շѽ�Ĭ��Ϊ10Ԫ
				recharge.setSetFee( new BigDecimal("10.00"));
			}
			if(recharge.getStopTime() == null || recharge.getStopTime().equals("") || (now.compareTo(recharge.getStopTime())) >= 0) {//����ʱ����ڻ����ڵ���ʱ��    ����ʱ��Ϊ��
				//ԭ�н��Ϊ�ջ�0��ֱ�ӱ������õĽ��
				if(recharge.getBeforeFee() == null || recharge.getBeforeFee().equals("") || recharge.getBeforeFee().equals("0.00")) {
					
					result = freeDao.updateFree(smokeMacs[i], recharge.getSetFee(), now, DateUtil.oneYear());//��ֵ����
					payRecord.setStopTime(DateUtil.oneYear());
				}else{//ԭ�н�Ϊ�ջ�0����ԭ�н��Ļ���������
					result = freeDao.updateFree(smokeMacs[i], recharge.getBeforeFee().add(recharge.getSetFee()), now, DateUtil.oneYear());//��ֵ����
				}
			}else{//δ���ڳ�ֵ��ֱ����ԭ�еĽ�������ӣ�ԭ�����ݿ�ĵ���ʱ�����һ��ʱ�䣨���Ժ�����ȡ��ʱ�乤���ࣩ
				String addTime = DateUtil.addDate(recharge.getStopTime(), 365);
				result = freeDao.updateFree(smokeMacs[i], recharge.getBeforeFee().add(recharge.getSetFee()), now, addTime);//��ֵ����
				payRecord.setStopTime(addTime);
				System.out.println("δ���ڳ�ֵ��ֱ����ԭ�еĽ�������ӣ�ԭ�����ݿ�ĵ���ʱ�����һ��ʱ��");
			}
			//�����¼
			payRecord.setMac(smokeMacs[i]);
			payRecord.setFree(recharge.getSetFee());
			payRecord.setPayTime(now);
			payRecord.setUserId(userId);
			freeDao.addPayRecord(payRecord);
		}
		if(result == 1) {
			HttpRsult hr = new HttpRsult();
			hr.setError("��ֵ�ɹ�");
			hr.setErrorCode(1);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}else{
			HttpRsult hr = new HttpRsult();
			hr.setError("��ֵʧ��");
			hr.setErrorCode(0);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}
	}
	
	@RequestMapping(value="list.do",method=RequestMethod.GET)
	public void list(HttpServletRequest request,HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		freeDao = new FreeDaoImpl();
		List<DevicesFee> DevicesFeeList = freeDao.deviceList(null);
		int count = freeDao.countDeviceType(null);
		System.out.println("����:"+count);
		for(int i=0; i<DevicesFeeList.size(); i++) {
			System.out.println("DevicesFee����"+DevicesFeeList.get(i).getDeviceName());
		}
		JSONObject jObject = new JSONObject(count);
		System.out.print("������"+jObject.toString());
		response.getWriter().write(jObject.toString());
		
		JSONObject jObject1 = new JSONObject(DevicesFeeList);
		System.out.print("ȫ����"+jObject1.toString());
		response.getWriter().write(jObject1.toString());
		
	}
	
	@RequestMapping(value="testDao.do",method=RequestMethod.GET)
	public void testImpl(HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		freeDao = new FreeDaoImpl();
		
		List<SmokePay> smokePays = freeDao.getUserAllList();
		List<String> stopTimes = new ArrayList<String>();
		List<String> userIdAlls = new ArrayList<String>();
		List<String> userIds = new ArrayList<String>();
		for(int i=0; i<smokePays.size(); i++) {
			userIdAlls.add(smokePays.get(i).getUserId());
			if(smokePays.get(i).getIsTxt() != 0) {
				userIds.add(smokePays.get(i).getUserId());
			}
			userIdAlls.add(smokePays.get(i).getUserId());
			stopTimes.add(smokePays.get(i).getStopTime());
		}
		List<String> newAllUserIds = DateUtil.removeRepeat(userIdAlls);//ȥ�غ�ȫ���û�id
		List<String> newUserIds = DateUtil.removeRepeat(userIds);//ȥ�غ�ȫ���û�id
		List<String> newStopTimes = DateUtil.removeRepeat(stopTimes);//ȥ�غ���ʱ��
		
		System.out.println("ȥ�غ�ȫ���û�id��"+newAllUserIds);
		System.out.println("ȥ�غ�ͨ�˶��ŵ绰��id��"+newUserIds);
		System.out.println("ȥ�غ���ʱ�䣺"+newStopTimes);
		JSONObject jObject1 = new JSONObject(smokePays);
		response.getWriter().write(jObject1.toString());
	}
	
	
	@RequestMapping(value="test.do",method=RequestMethod.GET)
	public void test(HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException {
		freeDao = new FreeDaoImpl();
		List<Recharge> reachargeList = freeDao.listRecharge();//���ڳ�ֵ��¼�еģ�����ֵ����
		
		for(int i=0; i<reachargeList.size(); i++) {
			if(freeDao.recordExistMac(reachargeList.get(i).getMac())){//ƥ���ֵ��¼Mac����ֵ��¼��mac�ģ��������Ǯ
				//���ڿɸ��ݳ�ֵʱ��Ϊ�մ�������ʱ��Ϊ�մ������Ϊ0.00�ж�(���ǵ����ݿ��������Ҳ�ֵ���ɼ��ϳ�ֵ��¼һ���ж�)
				if(!reachargeList.get(i).getStopTime().equals("") && !reachargeList.get(i).getFeetime().equals("") && !reachargeList.get(i).getBeforeFee().equals("0.00")) {//δ����
					//֪ͨ���������ǰһ���£�10�죬�ѵ���
					//֪ͨ�ˣ��豸�ű������list��Ա
					//֪ͨ��ʽ�����򣬷����ţ��ʼ�֪ͨ
					//���ڲ�������ֵʱ�䣬��ֵ������ʱ����գ���֪ͨ��ͣ�豸
					//���ڴ������ֵʱ�䣬����ʱ�䣬����ֶ�Ϊ0
					
					
					//�������������ʱ����ͬ��
					
					
					//����ǰ10��
					try {
						int day = DateUtil.countDay(DateUtil.formatDate(DateUtil.now()), DateUtil.formatDate(reachargeList.get(i).getStopTime()));
						if(day == 10 || day == 3 || day == 0) {//����ǰ10�죬3�죬�ѵ���
								
							List<String> userIds = freeDao.getUserIdsByMac(reachargeList.get(i).getMac());//�豸�ű������ȫ��list��Ա
							List<UserEntity> users = freeDao.getUsersByMac(reachargeList.get(i).getMac());//�豸�ű�����Ŀ�ͨ�˶��Ż�绰��list��Ա
							for(int a=0; a<userIds.size(); a++) {
								for(int b=0; b<users.size(); b++) {
									if(userIds.get(a).equals(users.get(a).getUserId())) {//��ͨ�˶��Ŷ̻��ģ��跢�Ͷ��ŵ绰
										if(users.get(b).getIstxt() == 1) {//����
												
										}else if(users.get(b).getIstxt() == 2) {//�绰
												
										}else if(users.get(b).getIstxt() == 3) {//���ź͵绰
													
										}
									}
								}
								System.out.println("ȫ���ģ�Ӧ������userIds���飺"+userIds.get(a));
									
									
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else{//�ѵ��ڣ���һ������
					JSONObject jObject1 = new JSONObject(reachargeList.get(i));
					System.out.print("����ʱ��Ϊ�գ�"+jObject1.toString());
					
					
				}
			}else{
				
			}
		}
	}
	
	@RequestMapping(value="/deviceList.do",method=RequestMethod.GET)
	public ModelAndView deviceList(HttpServletRequest request,HttpServletResponse response,DevicesFee query,ModelMap model,Integer pageNo) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		freeService = new FreeServiceImpl();

		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		ModelAndView modelAndView = new ModelAndView();
		String deviceName = request.getParameter("deviceName");
		String devSystemName = request.getParameter("devSystemName");
		
		//������ת����һҳ����
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(deviceName)) {
			query.setDeviceName(deviceName.trim());
			params.append("&deviceName=").append(deviceName);
			model.addAttribute("deviceName", deviceName);
		}if (StringUtils.isNotBlank(devSystemName)) {
			query.setDevSystemName(devSystemName);
			params.append("&devSystemName=").append(devSystemName);
			model.addAttribute("devSystemName", devSystemName);
		}
		
		Pagination pagination = freeService.getDevicesPage(query);
		pagination.pageView("/fireSystem/deviceList.do", params.toString());
		model.addAttribute("pagination", pagination);
		modelAndView.setViewName("/deviceList");
		return modelAndView;
	}
	
	@RequestMapping(value="/updateMoney.do", method=RequestMethod.GET)
	public void updateMoney(HttpServletRequest request, HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "textml;charset=UTF-8");
		freeDao = new FreeDaoImpl();
		String deviceId = request.getParameter("deviceId");
		String money = request.getParameter("money");
		HttpRsult hr = new HttpRsult();
		if(deviceId == null || deviceId.equals("") || money == null || money.equals("")) {
			hr.setError("�豸�Ż�۸�Ϊ��");
			hr.setErrorCode(0);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}
		System.out.println("�۸�"+money+"deviceId:"+deviceId);
		int a = freeDao.updateMoney(deviceId, money);
		if(a == 1) {
			hr.setError("���óɹ�");
			hr.setErrorCode(1);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}else{
			hr.setError("����ʧ��");
			hr.setErrorCode(0);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}
	}
	
}

