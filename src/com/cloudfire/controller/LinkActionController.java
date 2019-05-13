package com.cloudfire.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.LinkActionDao;
import com.cloudfire.dao.impl.LinkActionDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LinkAction;
import com.cloudfire.myservice.LinkActionService;
import com.cloudfire.myservice.impl.LinkActionServiceImpl;
import com.cloudfire.until.Constant;
import com.cloudfire.until.WriteJson;

import common.page.Pagination;

@Controller
public class LinkActionController {

	private LinkActionDao linkActionDao;
	private LinkActionService linkActionService;

	public LinkActionController() {
		linkActionDao = new LinkActionDaoImpl();
		linkActionService = new LinkActionServiceImpl();
	}

	@RequestMapping(value = "/getTypeByMac.do", method = RequestMethod.POST)
	public void getTypeByMac(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String alarmMac = request.getParameter("alarmMac");
		String responseMac = request.getParameter("responseMac");
		HttpRsult hr = new HttpRsult();
		if (alarmMac != null && !alarmMac.equals("")) {
			if (linkActionDao.getTypeByMac(alarmMac.trim()) == null) {
				hr.setErrorCode(0);
				hr.setError("���豸������");
			} else {
				LinkAction linkAction = linkActionDao.getTypeByMac(alarmMac.trim());
				hr.setErrorCode(linkAction.getDeviceType1());
				hr.setError(linkAction.getAlarmMacType());
			}
		} else if (responseMac != null && !responseMac.equals("")) {
			if (linkActionDao.getTypeByMac(responseMac.trim()) == null) {
				hr.setErrorCode(0);
				hr.setError("���豸������");
			} else {
				LinkAction linkAction = linkActionDao.getTypeByMac(responseMac.trim());
				if (linkAction.getDeviceType1() == 75 || linkAction.getDeviceType1() == 73) {// Ϊ75����Ӳ��ɹ���smoke���У�imei_device���ޣ�������ȥimei_device���Ƿ����
					if (linkActionDao.getImeiByNJ(responseMac.trim()) == null || linkActionDao.getImeiByNJ(responseMac.trim()).equals("")) {
						hr.setErrorCode(0);
						hr.setError("���豸δ��ӳɹ�");
						System.out.println("���豸δ��ӳɹ�");
					} else {
						hr.setErrorCode(linkAction.getDeviceType1());
						hr.setError(linkAction.getAlarmMacType());
						System.out.println("���豸��ӳɹ�");
					}
				} else {
					hr.setErrorCode(linkAction.getDeviceType1());
					hr.setError(linkAction.getAlarmMacType());
				}
			}
		} else {
			hr.setErrorCode(0);
			hr.setError("�豸��Ϊ��");
		}
		JSONObject jObject = new JSONObject(hr);
		response.getWriter().write(jObject.toString());
	}

	@RequestMapping(value = "/checkType.do", method = RequestMethod.POST)
	public void checkType(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
		String deviceType1 = request.getParameter("deviceType1");
		int type1 = Integer.valueOf(deviceType1);
		String deviceType2 = request.getParameter("deviceType2");
		int type2 = Integer.valueOf(deviceType2);
		String alarmMac = request.getParameter("alarmMac");
		String responseMac = request.getParameter("responseMac");
		
		String alarmAreaId = linkActionDao.getAreaIdByMac(alarmMac);
		String responseAreaId = linkActionDao.getAreaIdByMac(responseMac);
		System.out.println("alarmAreaId:"+alarmAreaId+"responseAreaId:"+responseAreaId);
		int result = linkActionDao.checkMac(alarmMac.trim(), responseMac.trim());
		if(alarmAreaId.equals(responseAreaId)) {
			if (result == 0) {// ���Ƿ��ظ�������֤,0Ϊ���ظ�
				if (type1 == 1 && type2 == 52) { // ������1Ϊ�̸б�����������2ΪLora�����豸�����ش�������Ϊ�𾯣�ִ�в���Ϊ�ر�lora����
					// String array[] = {"��"};
					// String array1[] = {"�ر�lora����"};
					/*
					 * String array[] = {"������"}; JSONArray alarmType =
					 * JSONArray.fromObject(array); String array1[] = {"�ر�lora����"};
					 * JSONArray action = JSONArray.fromObject(array1);
					 * response.getWriter().write(alarmType.toString());
					 * response.getWriter().write(action.toString());
					 */
					ArrayList<LinkAction> list = new ArrayList<LinkAction>();
					LinkAction la = new LinkAction();
					la.setId(1);
					la.setAlarmType("������");
					la.setAction("�ر�lora����");
					list.add(la);
					WriteJson json = new WriteJson();
					String object = json.getJsonData(list);
					response.getWriter().write(object);
				} else if (type1 == 1 && type2 == 75) {
					ArrayList<LinkAction> list = new ArrayList<LinkAction>();
					LinkAction la = new LinkAction();
					la.setId(1);
					la.setAlarmType("������");
					la.setAction("�ر�NB����");
					list.add(la);
					WriteJson json = new WriteJson();
					String object = json.getJsonData(list);
					response.getWriter().write(object);
				}else if (type1 == 1 && type2 == 73) {
					ArrayList<LinkAction> list = new ArrayList<LinkAction>();
					LinkAction la = new LinkAction();
					la.setId(1);
					la.setAlarmType("������");
					la.setAction("�ر�7020NBȼ��");
					list.add(la);
					WriteJson json = new WriteJson();
					String object = json.getJsonData(list);
					response.getWriter().write(object);
				} else {
					response.getWriter().write("{\"error\":\"�������豸��������\"}");
				}
			} else {
				response.getWriter().write("{\"error\":\"�������豸�Ѿ���\"}");
			}
		}else {
			response.getWriter().write("{\"error\":\"�������豸��������һ��\"}");
		}
	}

	@RequestMapping(value = "/addLinkAction.do", method = RequestMethod.POST)
	public void addLinkAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String alarmMac = request.getParameter("alarmMac");
		String deviceType1 = request.getParameter("deviceType1");
		String alarmMacType = request.getParameter("alarmMacType");
		String responseMac = request.getParameter("responseMac");
		String deviceType2 = request.getParameter("deviceType2");
		String responseMacType = request.getParameter("responseMacType");
		String alarmType = request.getParameter("alarmType");
		String action = request.getParameter("action");
		String userid = (String) request.getSession().getAttribute("userId");
		String now = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(new Date(System.currentTimeMillis()));
		LinkAction linkAction = new LinkAction();
		linkAction.setAlarmMac(alarmMac.trim());
		linkAction.setDeviceType1(Integer.valueOf(deviceType1));
		linkAction.setAlarmMacType(alarmMacType);
		linkAction.setResponseMac(responseMac.trim());
		linkAction.setDeviceType2(Integer.valueOf(deviceType2));
		linkAction.setResponseMacType(responseMacType);
		linkAction.setAlarmType(alarmType);
		linkAction.setAction(action);
		linkAction.setTime(now);
		linkAction.setUserid(userid);
		int result = linkActionDao.addLinkAction(linkAction);
		HttpRsult hr = new HttpRsult();
		if (result == 1) {
			hr.setErrorCode(1);
			hr.setError("��ӳɹ�");
		} else {
			hr.setErrorCode(0);
			hr.setError("���ʧ��");
		}
		JSONObject jObject = new JSONObject(hr);
		response.getWriter().write(jObject.toString());
	}

	@RequestMapping(value = "/linkActionList.do", method = RequestMethod.GET)
	public ModelAndView listLinkAction(HttpServletRequest request, HttpServletResponse response, LinkAction query,
			ModelMap model, Integer pageNo) throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		StringBuilder params = new StringBuilder();
		ModelAndView modelAndView = new ModelAndView();
		Pagination pagination = linkActionService.getLinkActionPage(query);
		model.addAttribute("pagination", pagination);
		pagination.pageView("/fireSystem/linkActionList.do", params.toString());
		modelAndView.setViewName("/bindLinkage");
		return modelAndView;
	}

	@RequestMapping(value = "/removeById.do", method = RequestMethod.POST)
	public void removeById(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String linkid = request.getParameter("linkid");
		int result = linkActionDao.removeById(Integer.valueOf(linkid));
		HttpRsult hr = new HttpRsult();
		if (result == 1) {
			hr.setErrorCode(1);
			hr.setError("�Ƴ��ɹ�");
		} else {
			hr.setErrorCode(0);
			hr.setError("�쳣ʧ��");
		}
		JSONObject jObject = new JSONObject(hr);
		response.getWriter().write(jObject.toString());
	}

}
