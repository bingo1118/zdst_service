package com.cloudfire.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.MapControlDao;
import com.cloudfire.dao.NeedSmokeDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.MapControlDaoImpl;
import com.cloudfire.dao.impl.NeedSmokeDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.BuildingBean;
import com.cloudfire.entity.NFC_infoEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.WriteJson;

@Controller
public class MapInfoConroller {
	private AreaDao mAreaDao;
//	private AlarmInfoDao mAlarmInfoDao;
	private MapControlDao mMapControlDao;

	@RequestMapping(value = "/searchMapSmoke1.do", method = RequestMethod.GET)
	public void searchMapSmoke1(HttpServletRequest request, String currentId,
			HttpServletResponse response, String areaId) {
		currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
//		String aa = (String) request.getSession().getAttribute("areaId");
		String areaIdString = areaId;
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserId(currentId, privilege);
		mMapControlDao = new MapControlDaoImpl();
		AreaAndRepeater mAreaAndRepeater = areaLists.get(Integer.parseInt(areaIdString) - 1);
		String cuser = currentId;
		if (privilege == "4" || "4".equals(privilege)) {
			cuser = null;
		}
		List<SmokeBean> listSmokes = mMapControlDao.getAreaSmoke(mAreaAndRepeater.getAreaName(),mAreaAndRepeater.getAreaId(), cuser);
		if (listSmokes != null && listSmokes.size() > 0) {
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(listSmokes);
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@RequestMapping(value = "/searchMapSmoke.do", method = RequestMethod.GET)
	public void searchMapSmoke(HttpServletRequest request, String currentId,HttpServletResponse response, String areaId,String com) throws IOException {
		currentId = (String) request.getSession().getAttribute("userId");
		String deviceId = request.getParameter("deviceId");
		if(StringUtils.isNotBlank(com)){
			com = URLDecoder.decode(com, "UTF-8");
		}
		String[] areaIds= areaId.split(",");
		
		mMapControlDao = new MapControlDaoImpl();
		List<SmokeBean> listSmokes =  mMapControlDao.getAreaSmokeLggNum(null, com,areaIds, deviceId, null);
		
		WriteJson writeJson = new WriteJson();
		String jObject  = writeJson.getJsonData(listSmokes);
		response.getWriter().write(jObject);
	}
	
	@RequestMapping(value = "/searchMapBuilding.do", method = RequestMethod.GET)
	public void searchMapBuilding(HttpServletRequest request, String currentId,HttpServletResponse response, String areaId,String com) throws IOException {
		currentId = (String) request.getSession().getAttribute("userId");
		String deviceId = request.getParameter("deviceId");
		if(StringUtils.isNotBlank(com)){
			com = URLDecoder.decode(com, "UTF-8");
		}
		String[] areaIds= areaId.split(",");
		
		mMapControlDao = new MapControlDaoImpl();
//		List<SmokeBean> listSmokes =  mMapControlDao.getAreaSmokeLggNum(null, comName,areaIds, deviceId, null);
		List<BuildingBean> lstBuilding = mMapControlDao.getBuildingStatics(deviceId,com,areaIds);
		
		WriteJson writeJson = new WriteJson();
		String jObject  = writeJson.getJsonData(lstBuilding);
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().write(jObject);
	}
	
	@RequestMapping(value = "/smokesByBuilding.do", method = RequestMethod.GET)
	public void smokesByBuilding(HttpServletRequest request,HttpServletResponse response,String buildingname) throws IOException {
		SmokeDao sd = new SmokeDaoImpl();
		List<SmokeBean> lstSmoke = sd.getSmokesByBuilding(buildingname);
		
		WriteJson writeJson = new WriteJson();
		String jObject  = writeJson.getJsonData(lstSmoke);
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().write(jObject);
	}

	@RequestMapping(value = "/searchMapTenace.do", method = RequestMethod.GET)
	public void searchMapTenace(HttpServletRequest request, String currentId,
			HttpServletResponse response) throws IOException {
		currentId = (String) request.getSession().getAttribute("userId");
		String areaId = request.getParameter("areaId");
		String J_xl_1 = request.getParameter("J_xl_1");
		String J_xl_2 = request.getParameter("J_xl_2");

		NFC_infoEntity nfc_web = new NFC_infoEntity();
		nfc_web.setAreaId(areaId);
		nfc_web.setEndTime_1(J_xl_1);
		nfc_web.setEndTime_2(J_xl_2);
		
		NeedSmokeDao nsd = new NeedSmokeDaoImpl();
		List<NFC_infoEntity> listSmokes = nsd.getAllNFCInfo_web(null, nfc_web);
		
		if (listSmokes != null && listSmokes.size() > 0) {
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(listSmokes);
			System.out.println(jObject);
			response.getWriter().write(jObject);
		} else {
			String jObject = "[]";
			response.getWriter().write(jObject);
		}
	}

}
