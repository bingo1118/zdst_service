package com.cloudfire.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.MapControlDao;
import com.cloudfire.dao.NeedSmokeDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.DevicesDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.MapControlDaoImpl;
import com.cloudfire.dao.impl.NeedSmokeDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.AreaBeanEntity;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.DealOkAlarmEntity;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.NFCInfoEntity;
import com.cloudfire.entity.NFC_infoEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.query.DeviceType;
import com.cloudfire.until.Utils;
import com.cloudfire.until.WriteJson;

@Controller
public class MapController {
	private AreaDao mAreaDao;
	private LoginDao mLoginDao;
	private AlarmInfoDao mAlarmInfoDao;
	private MapControlDao mMapControlDao;
	private SmartControlDao mSmartControlDao;
	private SmokeDao smokeDao;

	@RequestMapping(value = "/mapInfo.do", method = RequestMethod.POST)
	public ModelAndView mapInfo2(HttpServletRequest request, String currentId,
			HttpServletResponse response) {
		ModelAndView ma = mapInfo(request, currentId, response);
		return ma;
	}

	@RequestMapping(value = "/mapInfo.do", method = RequestMethod.GET)
	public ModelAndView mapInfo(HttpServletRequest request, String currentId,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		
		
		mAreaDao = new AreaDaoImpl();
		mSmartControlDao = new SmartControlDaoImpl();

		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserIdLg(currentId, privilege);

		Map<String, List> modelMap = new HashMap<String, List>();
//				List<SmokeBean> listSmokes=new ArrayList<SmokeBean>();
//				if (mAreaAndRepeater != null) {
//					if (privilege == "4" || "4".equals(privilege)) {
//						currentId = null;
//					}
//					listSmokes = mMapControlDao.getAreaSmoke(
//							mAreaAndRepeater.getAreaName(),
//							mAreaAndRepeater.getAreaId(), currentId);
//				}
//
//				CountValue cv = new CountValue();
//				DeviceDao dd = new DevicesDaoImpl();
//				if (privilege == "4" || "4".equals(privilege)) {
//					cv = dd.getCount(null);
//				} else {
//					cv = dd.getCountByPrivilege(currentId, null);
//				}
//
//				String longitude = request.getParameter("longitude");
//				String latitude = request.getParameter("latitude");
//				String devMac = request.getParameter("devMac");
//				List<SmokeBean> slist = new ArrayList<SmokeBean>();
//				SmokeBean smokeBean = new SmokeBean();
//				smokeBean.setLongitude(longitude);
//				smokeBean.setLatitude(latitude);
//				smokeBean.setMac(devMac);
//				smokeBean.setCv(cv);
//				slist.add(smokeBean);

		List<DeviceType> devicesList =  mSmartControlDao.getAllDeviceTypeAndName(areaIds);

		modelMap.put("devicesList", devicesList);
//				modelMap.put("alarmInfos", lists);
		modelMap.put("areaLists", areaLists);
//				modelMap.put("smokeList", slist);
//				modelMap.put("listSmokes", listSmokes);  //设备状态统计
//				modelAndView.addObject("cv", cv);
		modelAndView.addAllObjects(modelMap);
		modelAndView.setViewName("/WEB-INF/page/main/map");
		return modelAndView;
	}

	@RequestMapping(value = "/mapSmoke.do", method = RequestMethod.GET)
	public void mapSmoke(HttpServletRequest request, String currentId,
			HttpServletResponse response) throws IOException {
		currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserIdLg(currentId, privilege);
		int count = 0;
		try{
			if(areaLists == null || areaLists.size() == 0) {
				response.getWriter().write("{\"check\":\"1\"}");
			}else{
				List<String> listAreaId = areaLists.get(0).getListEaraId();
				mMapControlDao = new MapControlDaoImpl();
				if (privilege == "4" || "4".equals(privilege)) {
					smokeDao  = new SmokeDaoImpl();
					count = smokeDao.queryCountCustomerSmoke();
					currentId = null;
				}
				List<SmokeBean> listSmokes = mMapControlDao.getAreaSmokeLgNum(null,listAreaId,currentId,privilege);
				if (listSmokes != null && listSmokes.size() > 0) {
					if(privilege.equals("4")){
						listSmokes.get(0).getCv().setMacNum(listSmokes.get(0).getCv().getMacNum()+count);
						listSmokes.get(0).getCv().setNetStaterNum(listSmokes.get(0).getCv().getNetStaterNum()+count);
					}
					WriteJson writeJson = new WriteJson();
					String jObject = writeJson.getJsonData(listSmokes);
					try {
						response.getWriter().write(jObject);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}catch(NullPointerException e) {
			response.getWriter().write("{\"check\":\"1\"}");
		}
	}

	@RequestMapping(value = "/mapPatrolRecord.do", method = RequestMethod.GET)
	public void mapPatrolRecord(HttpServletRequest request, String currentId,
			HttpServletResponse response) {
		String areaId = request.getParameter("areaId");
		String time_start = request.getParameter("J_xl_1");
		String time_end = request.getParameter("J_xl_2");
		NFC_infoEntity nfc_web = new NFC_infoEntity();
		nfc_web.setAreaId(areaId);
		nfc_web.setEndTime_1(time_start);
		nfc_web.setEndTime_2(time_end);
		mAreaDao = new AreaDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		mMapControlDao = new MapControlDaoImpl();
		if(areaIds != null) {
			NeedSmokeDao nsd = new NeedSmokeDaoImpl();
			List<NFC_infoEntity> listSmokes = nsd.getAllNFCInfo_map(areaIds,
					nfc_web);
			if (listSmokes != null && listSmokes.size() > 0) {
				WriteJson writeJson = new WriteJson();
				String jObject = writeJson.getJsonData(listSmokes);
//					System.out.println(jObject);
				try {
					response.getWriter().write(jObject);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				String jObject = "[]";
				try {
					response.getWriter().write(jObject);
					// System.out.println(jObject);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@RequestMapping(value = "/mapPatrolInfo.do", method = RequestMethod.GET)
	public void mapPatrolInfo(HttpServletRequest request, String currentId,
			HttpServletResponse response) {
			currentId = (String) request.getSession().getAttribute("userId");
			String uuid = request.getParameter("uuid");
			NeedSmokeDao nsd = new NeedSmokeDaoImpl();
			List<NFCInfoEntity> listNfc = new ArrayList<NFCInfoEntity>();
			NFCInfoEntity nfcEntity = nsd.getNFC_Info_Entity(uuid);
			listNfc.add(nfcEntity);
			if (listNfc != null && listNfc.size() > 0) {
				WriteJson writeJson = new WriteJson();
				String jObject = writeJson.getJsonData(listNfc);
				System.out.println(jObject);
				try {
					response.getWriter().write(jObject);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				String jObject = "[]";
				try {
					response.getWriter().write(jObject);
					// System.out.println(jObject);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	

	@RequestMapping(value = "/updateMapInfo.do", method = RequestMethod.GET)
	public void updateMapInfo(HttpServletRequest request, String currentId,
			HttpServletResponse response) {
		currentId = (String) request.getSession().getAttribute("userId");
		mLoginDao = new LoginDaoImpl();
		String smokeMac = request.getParameter("smokeMac");
		if (Utils.isNullStr(smokeMac)) {
			mAreaDao = new AreaDaoImpl();
			List<SmokeBean> smokeList = mAreaDao.getSmokeInfo(smokeMac);
			if (smokeList != null && smokeList.size() > 0) {
				WriteJson writeJson = new WriteJson();
				String jObject = writeJson.getJsonData(smokeList);
				try {
					response.getWriter().write(jObject);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@RequestMapping(value = "/saveMapInfo.do", method = RequestMethod.GET)
	public void saveMapInfo(HttpServletRequest request, String currentId,
			HttpServletResponse response) {
		currentId = (String) request.getSession().getAttribute("userId");
		String smokeMac = request.getParameter("smokeMac");
		String longitude = request.getParameter("longitude");
		String latitude = request.getParameter("latitude");
		if (Utils.isNullStr(smokeMac)) {
			mAreaDao = new AreaDaoImpl();
			List<SmokeBean> smokeList = mAreaDao.saveSmokeInfo(smokeMac,
					longitude, latitude);
			if (smokeList != null && smokeList.size() > 0) {
				WriteJson writeJson = new WriteJson();
				String jObject = writeJson.getJsonData(smokeList);
				try {
					response.getWriter().write(jObject);
					// System.out.println(jObject);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@RequestMapping(value = "/mapSmokeByLongitu.do", method = RequestMethod.GET)
	public void mapSmokeByLongitu(HttpServletRequest request, String currentId,
			HttpServletResponse response) {
		currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		String longitude = request.getParameter("longitude");
		String latitude = request.getParameter("latitude");
		String smokeMac = request.getParameter("smokeMac");
//		mAreaDao = new AreaDaoImpl();
//		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserId(currentId,
//				privilege);
//		mMapControlDao = new MapControlDaoImpl();
//		AreaAndRepeater mAreaAndRepeater = areaLists.get(0);
//		String areaName = mAreaAndRepeater.getAreaName();
		List<SmokeBean> listSmokes = mMapControlDao
				.getAreaSmokeByLongitu("", longitude, latitude,
						smokeMac, "");
		if (listSmokes != null && listSmokes.size() > 0) {
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(listSmokes);
			try {
				response.getWriter().write(jObject);
				System.out.println(jObject);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
