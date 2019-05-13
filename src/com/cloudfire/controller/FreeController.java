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
	
		//构建跳转到下一页参数
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
		String mac = request.getParameter("mac");//设备ID
		String placeTypeId = request.getParameter("placeTypeId");//行业类型
		String areaId = request.getParameter("areaId");//区域
		String deviceType = request.getParameter("deviceType");//设备类型
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
		//一年后时间
		int result = 0;
		PayRecord payRecord = new PayRecord();
		for (int i = 0; i < smokeMacs.length; i++) {
			Recharge recharge = freeDao.getRechargeByMac(smokeMacs[i]);
			if(recharge.getSetFee() == null || recharge.getSetFee().equals("")) {//如果某种设备未设置收费金额，默认为10元
				recharge.setSetFee( new BigDecimal("10.00"));
			}
			if(recharge.getStopTime() == null || recharge.getStopTime().equals("") || (now.compareTo(recharge.getStopTime())) >= 0) {//现在时间等于或晚于到期时间    到期时间为空
				//原有金额为空或0，直接保存设置的金额
				if(recharge.getBeforeFee() == null || recharge.getBeforeFee().equals("") || recharge.getBeforeFee().equals("0.00")) {
					
					result = freeDao.updateFree(smokeMacs[i], recharge.getSetFee(), now, DateUtil.oneYear());//充值操作
					payRecord.setStopTime(DateUtil.oneYear());
				}else{//原有金额不为空或0，在原有金额的基础上增加
					result = freeDao.updateFree(smokeMacs[i], recharge.getBeforeFee().add(recharge.getSetFee()), now, DateUtil.oneYear());//充值操作
				}
			}else{//未到期充值，直接在原有的金额上增加，原来数据库的到期时间加上一年时间（若以后按月收取改时间工具类）
				String addTime = DateUtil.addDate(recharge.getStopTime(), 365);
				result = freeDao.updateFree(smokeMacs[i], recharge.getBeforeFee().add(recharge.getSetFee()), now, addTime);//充值操作
				payRecord.setStopTime(addTime);
				System.out.println("未到期充值，直接在原有的金额上增加，原来数据库的到期时间加上一年时间");
			}
			//保存记录
			payRecord.setMac(smokeMacs[i]);
			payRecord.setFree(recharge.getSetFee());
			payRecord.setPayTime(now);
			payRecord.setUserId(userId);
			freeDao.addPayRecord(payRecord);
		}
		if(result == 1) {
			HttpRsult hr = new HttpRsult();
			hr.setError("充值成功");
			hr.setErrorCode(1);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}else{
			HttpRsult hr = new HttpRsult();
			hr.setError("充值失败");
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
		System.out.println("数量:"+count);
		for(int i=0; i<DevicesFeeList.size(); i++) {
			System.out.println("DevicesFee对象："+DevicesFeeList.get(i).getDeviceName());
		}
		JSONObject jObject = new JSONObject(count);
		System.out.print("数量："+jObject.toString());
		response.getWriter().write(jObject.toString());
		
		JSONObject jObject1 = new JSONObject(DevicesFeeList);
		System.out.print("全部："+jObject1.toString());
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
		List<String> newAllUserIds = DateUtil.removeRepeat(userIdAlls);//去重后全部用户id
		List<String> newUserIds = DateUtil.removeRepeat(userIds);//去重后全部用户id
		List<String> newStopTimes = DateUtil.removeRepeat(stopTimes);//去重后到期时间
		
		System.out.println("去重后全部用户id："+newAllUserIds);
		System.out.println("去重后开通了短信电话的id："+newUserIds);
		System.out.println("去重后到期时间："+newStopTimes);
		JSONObject jObject1 = new JSONObject(smokePays);
		response.getWriter().write(jObject1.toString());
	}
	
	
	@RequestMapping(value="test.do",method=RequestMethod.GET)
	public void test(HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException {
		freeDao = new FreeDaoImpl();
		List<Recharge> reachargeList = freeDao.listRecharge();//存在充值记录中的，即充值过的
		
		for(int i=0; i<reachargeList.size(); i++) {
			if(freeDao.recordExistMac(reachargeList.get(i).getMac())){//匹配充值记录Mac，充值记录有mac的，表明冲过钱
				//到期可根据充值时间为空串，到期时间为空串，金额为0.00判断(考虑到数据库中有人乱插值，可加上充值记录一起判断)
				if(!reachargeList.get(i).getStopTime().equals("") && !reachargeList.get(i).getFeetime().equals("") && !reachargeList.get(i).getBeforeFee().equals("0.00")) {//未到期
					//通知情况：到期前一个月，10天，已到期
					//通知人：设备号本区域的list成员
					//通知方式：弹框，发短信，邮件通知
					//到期操作：充值时间，充值金额，到期时间清空，发通知，停设备
					//到期处理，清充值时间，到期时间，金额字段为0
					
					
					//批量情况，到期时间相同，
					
					
					//到期前10天
					try {
						int day = DateUtil.countDay(DateUtil.formatDate(DateUtil.now()), DateUtil.formatDate(reachargeList.get(i).getStopTime()));
						if(day == 10 || day == 3 || day == 0) {//到期前10天，3天，已到期
								
							List<String> userIds = freeDao.getUserIdsByMac(reachargeList.get(i).getMac());//设备号本区域的全部list成员
							List<UserEntity> users = freeDao.getUsersByMac(reachargeList.get(i).getMac());//设备号本区域的开通了短信或电话的list成员
							for(int a=0; a<userIds.size(); a++) {
								for(int b=0; b<users.size(); b++) {
									if(userIds.get(a).equals(users.get(a).getUserId())) {//开通了短信短话的，需发送短信电话
										if(users.get(b).getIstxt() == 1) {//短信
												
										}else if(users.get(b).getIstxt() == 2) {//电话
												
										}else if(users.get(b).getIstxt() == 3) {//短信和电话
													
										}
									}
								}
								System.out.println("全部的，应弹窗的userIds数组："+userIds.get(a));
									
									
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else{//已到期，发一次提醒
					JSONObject jObject1 = new JSONObject(reachargeList.get(i));
					System.out.print("到期时间为空："+jObject1.toString());
					
					
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
		
		//构建跳转到下一页参数
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
			hr.setError("设备号或价格为空");
			hr.setErrorCode(0);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}
		System.out.println("价格："+money+"deviceId:"+deviceId);
		int a = freeDao.updateMoney(deviceId, money);
		if(a == 1) {
			hr.setError("设置成功");
			hr.setErrorCode(1);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}else{
			hr.setError("设置失败");
			hr.setErrorCode(0);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}
	}
	
}

