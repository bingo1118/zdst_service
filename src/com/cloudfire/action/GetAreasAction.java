package com.cloudfire.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.NeedSmokeDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.NeedSmokeDaoImpl;
import com.cloudfire.entity.AllAreaEntity;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.AreaBeanEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.query.MainIndexEntityQuery;
import com.opensymphony.xwork2.ActionSupport;

public class GetAreasAction extends ActionSupport implements 
ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = 1L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AreaDao areaDao;
	private NeedSmokeDao mNeedSmokeDao;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void getSmokeBeanInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			String areaId = this.request.getParameter("areaId");
			String placeTypeId  = this.request.getParameter("placeTypeId");
			String prentId = this.request.getParameter("prentId");
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mNeedSmokeDao = new NeedSmokeDaoImpl();
				ase = mNeedSmokeDao.getSmokeAllInfo(userId, privilege, page, areaId, placeTypeId,prentId);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有烟感");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取烟感成功");
					ase.setErrorCode(0);
					result = ase;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void getAreaInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			Map<Map<Integer,String>,Map<Integer,String>> areaMaps = new HashMap<Map<Integer,String>,Map<Integer,String>>();
			HttpRsult hr = null;
			JSONObject jObject;
			AreaBeanEntity abe;
			Object result = null;
			if(userId==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
				jObject = new JSONObject(result);
			}else{
				areaDao = new AreaDaoImpl();
				abe = areaDao.getAllAreaInfo(userId, privilege);
				List<AreaBeanEntity> areaList = abe.getAbeList();
				Map<Integer,String> areaMap = abe.getAreaMap();
				
				for(Integer key:areaMap.keySet()){
					Map<Integer,String> newMap = new HashMap<Integer,String>();
					Map<Integer,String> firMap = new HashMap<Integer,String>();
					firMap.put(key, areaMap.get(key));
					for(AreaBeanEntity area:areaList){
						if(key == area.getParentId()){
							newMap.put(area.getAreaId(), area.getAreaName());
						}
					}
					areaMaps.put(firMap, newMap);
				}
				if(areaMaps==null){
					hr = new HttpRsult();
					hr.setError("获取数据失败");
					hr.setErrorCode(2);
					result = hr;
					jObject = new JSONObject(result);
				}else {
					jObject = new JSONObject(areaMaps);
				}
			}
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getAreaInfo2(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			HttpRsult hr = null;
			AllAreaEntity ase = null;
			Object result = null;
			if(userId==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				areaDao = new AreaDaoImpl();
				ase = areaDao.getAllAreas(userId, privilege);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("参数错误");
					hr.setErrorCode(1);
					result = hr;
				}else{
					result=ase;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
