package com.cloudfire.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.APIDao;
import com.cloudfire.dao.AddSmokeDao;
import com.cloudfire.dao.KeepSystemDao;
import com.cloudfire.dao.impl.APIDaoImpl;
import com.cloudfire.dao.impl.AddSmokeDaoImpl;
import com.cloudfire.dao.impl.KeepSystemDaoImpl;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.APIResult;
import com.cloudfire.entity.BuildingInfo;
import com.cloudfire.entity.CompanyInfo;
import com.cloudfire.entity.DataEntity;
import com.cloudfire.entity.Equipment;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.Repeater;
import com.cloudfire.entity.StisEntity;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

import redis.clients.jedis.Jedis;

public class APIforControl extends ActionSupport implements ServletRequestAware,ServletResponseAware{
	private static final long serialVersionUID = 2521573698133314347L;
	private HttpServletRequest req;
	private HttpServletResponse resp;
	private int pageNo = 1;
	private int pageSize = 50;
	private long createDateB =0;
	private long   createDateE =0;
	private long updateDateB=0;
	private long updateDateE =0;
	private int areaid = 0;
	private String id ="";
	private static  long MAXINTERVAL =   7*24*60*60*1000 ;
	private APIResult rs ;
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.resp=response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.req=request;
	}
	
	public void bindDeviceType(){
		String deviceType=req.getParameter("deviceType");
		String deviceMiniType=req.getParameter("deviceMinaType");
		APIDao ad= new APIDaoImpl();
		rs = ad.bindDeviceType(deviceType,deviceMiniType);
		
		writeResult();
	}
	
	public void addBuilding() {
//	if (trueToken()){
		 String buildingId = req.getParameter("buildingId");
		 String buildingName = req.getParameter("buildingName");
		 String companyId = req.getParameter("companyId");
		 String address = req.getParameter("address");
		 String buildingType = req.getParameter("buildingType"); //可选
		 String lng = req.getParameter("lng");
		 String lat = req.getParameter("lat");
		 String areaStr = req.getParameter("areaId");
		 areaid = Integer.parseInt(areaStr);
//			 int deviceNum;
//			 int isOnline;
//			 int rechargeNum;
		String regionCode = req.getParameter("regionCode");
		if (StringUtils.isBlank(buildingId) || StringUtils.isBlank(buildingName) || StringUtils.isBlank(address)
				|| StringUtils.isBlank(companyId) || StringUtils.isBlank(lng) || StringUtils.isBlank(lat) || StringUtils.isBlank(regionCode)){
			rs = new APIResult();
			rs.setCode(1);
			rs.setMessage("参数错误");
		} else {
			APIDao apid = new APIDaoImpl();
			rs = apid.addBuilding(buildingId,buildingName,companyId,address,buildingType,lng,lat,regionCode,areaid);
		}
//	}else {
//			rs = new APIResult();
//			rs.setCode(2);
//			rs.setMessage("token验证失败");
//		}
		
		writeResult();
	}
	
	public void deleteBuilding(){
		if (trueToken()){
			String buildingId = req.getParameter("buildingId");
			APIDao apid = new APIDaoImpl();
			rs = apid.delBuilding(buildingId);
		}else {
			rs = new APIResult();
			rs.setCode(2);
			rs.setMessage("token验证失败");
		}
		
		writeResult();
	}
	
	public void updateBuilding(){
		if (trueToken()){
			 String buildingId = req.getParameter("buildingId");
			 String buildingName = req.getParameter("buildingName");
			 String companyId = req.getParameter("companyId");
			 String address = req.getParameter("address");
			 String buildingType = req.getParameter("buildingType"); //可选
			 String lng = req.getParameter("lng");
			 String lat = req.getParameter("lat");
//			 int deviceNum;
//			 int isOnline;
//			 int rechargeNum;
			String regionCode = req.getParameter("regionCode");
			if (StringUtils.isBlank(buildingId) || StringUtils.isBlank(buildingName) || StringUtils.isBlank(address)
					|| StringUtils.isBlank(companyId) || StringUtils.isBlank(lng) || StringUtils.isBlank(lat) || StringUtils.isBlank(regionCode)){
				rs = new APIResult();
				rs.setCode(1);
				rs.setMessage("参数错误");
			} else {
				APIDao apid = new APIDaoImpl();
				rs = apid.updateBuilding(buildingId,buildingName,companyId,address,buildingType,lng,lat,regionCode,areaid);
			}
		}else {
			rs = new APIResult();
			rs.setCode(2);
			rs.setMessage("token验证失败");
		}
		
		writeResult();
	}
	
	public void getBuildingInfo(){
		if (trueToken()){
			if (checkParams()){
				APIDao apid = new APIDaoImpl();
				List<BuildingInfo> buildings = apid.getBuidings(id, createDateB, createDateE, updateDateB, updateDateE,pageNo,pageSize,areaid);
				int count  = apid.getBuildingCount(id, createDateB, createDateE, updateDateB, updateDateE,areaid);
				rs = packData(buildings,count);
			} else {
				rs = new APIResult();
				rs.setCode(1);
				rs.setMessage("参数错误");
			}
		} else {
			rs = new APIResult();
			rs.setCode(2);
			rs.setMessage("token验证失败");
		}
		
		writeResult();
	}
	
	public void verifyCompanyId(){
		String companyId=req.getParameter("companyId");
		APIDao apid = new APIDaoImpl();
		rs = apid.verifyCompanyId(companyId);
		writeResult();
	}
	public void verifyBuildingId(){
		String buildingId=req.getParameter("buildingId");
		APIDao apid = new APIDaoImpl();
		rs = apid.verifyBuildingId(buildingId);
		writeResult();
	}
	
	public void addCompany(){
//	if (trueToken()){
		String companyId = req.getParameter("companyId");
		String companyName = req.getParameter("companyName");
		String companyCode = req.getParameter("companyCode"); //非必需
		String address = req.getParameter("address");
		String contactName = req.getParameter("contactName");
		String contactTel = req.getParameter("contactTel");
		areaid = Integer.parseInt(req.getParameter("areaId"));
		if (StringUtils.isBlank(companyId) || StringUtils.isBlank(companyName) || StringUtils.isBlank(address)
				|| StringUtils.isBlank(contactName) || StringUtils.isBlank(contactTel)){
			rs = new APIResult();
			rs.setCode(1);
			rs.setMessage("参数错误");
		} else {
			APIDao apid = new APIDaoImpl();
			rs = apid.addCompany(companyId,companyName,companyCode,address,contactName,contactTel,areaid);
		}
//	}else {
//			rs = new APIResult();
//			rs.setCode(2);
//			rs.setMessage("token验证失败");
//		}
		
		writeResult();
	}
	
	public void deleteCompany(){
		if (trueToken()){
			String companyId = req.getParameter("companyId");
			APIDao apid = new APIDaoImpl();
			rs = apid.delCompany(companyId);
		}else {
			rs = new APIResult();
			rs.setCode(2);
			rs.setMessage("token验证失败");
		}
		
		writeResult();
	}
	
	public void updateCompany(){
//	if (trueToken()){
		String companyId = req.getParameter("companyId");
		String companyName = req.getParameter("companyName");
		String companyCode = req.getParameter("companyCode"); //非必需
		String address = req.getParameter("address");
		String contactName = req.getParameter("contactName");
		String contactTel = req.getParameter("contactTel");
		areaid = Integer.parseInt(req.getParameter("areaId"));
		if (StringUtils.isBlank(companyId) || StringUtils.isBlank(companyName) || StringUtils.isBlank(address)
				|| StringUtils.isBlank(contactName) || StringUtils.isBlank(contactTel)){
			rs = new APIResult();
			rs.setCode(1);
			rs.setMessage("参数错误");
		} else {
			APIDao apid = new APIDaoImpl();
			rs = apid.updateCompany(companyId,companyName,companyCode,address,contactName,contactTel,areaid);
		}
//	}else {
//			rs = new APIResult();
//			rs.setCode(2);
//			rs.setMessage("token验证失败");
//		}
		
		writeResult();
	}
	
	public void getCompanyInfo(){
		if (trueToken()){
			if (checkParams()){
				APIDao apid = new APIDaoImpl();
				List<CompanyInfo> companys = apid.getCompanys(id, createDateB, createDateE, updateDateB, updateDateE,pageNo,pageSize,areaid);
				int count  = apid.getCompanyCount(id, createDateB, createDateE, updateDateB, updateDateE,areaid);
				rs = packData(companys,count);
			} else {
				rs = new APIResult();
				rs.setCode(1);
				rs.setMessage("参数错误");
			}
		}else {
			rs = new APIResult();
			rs.setCode(2);
			rs.setMessage("token验证失败");
		}
		
		writeResult();
	}
	
	public void addOrUpdateEquipment(){
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("GBK");
//	if (trueToken()){
		HttpRsult hr =null;
		try {
			req.setCharacterEncoding("utf-8");
			String userId = req.getParameter("userId");
			String smokeMac = req.getParameter("smokeMac");
			String privilege = req.getParameter("privilege");
			String smokeName = req.getParameter("smokeName");
			String address = req.getParameter("address");
			String longitude = req.getParameter("longitude");
			String latitude = req.getParameter("latitude");
			String placeAddress = req.getParameter("placeAddress");
			String placeTypeId = req.getParameter("placeTypeId");
			String principal1 = req.getParameter("principal1");
			String principal1Phone = req.getParameter("principal1Phone");
			String principal2 = req.getParameter("principal2");
			String principal2Phone = req.getParameter("principal2Phone");
			String areaId = req.getParameter("areaId");
			String repeater = req.getParameter("repeater");
			String camera = req.getParameter("camera");
			String deviceType = req.getParameter("deviceType");
			String cameraChannel=req.getParameter("cameraChannel");
			String electrState=req.getParameter("electrState");//电气开关状态
			String companyId=req.getParameter("companyId");
			String buildingId =req.getParameter("buildingId");
			
			if(electrState==null||electrState.equals("")){
				electrState="0";
			}
			
			KeepSystemDao keepSystemDao=new  KeepSystemDaoImpl();
			String oldRepeaterString=keepSystemDao.getRepeaterOfSmoke(smokeMac);
			if(userId==null||smokeMac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
			}else{
				AddSmokeDao mAddSmokeDao = new AddSmokeDaoImpl();
				hr = mAddSmokeDao.addSmokeForGR(userId, smokeMac, privilege, smokeName, address, longitude, latitude,
						placeAddress, placeTypeId, principal1, principal1Phone, principal2, principal2Phone, 
						areaId, repeater, camera, deviceType,cameraChannel,electrState,companyId,buildingId);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("添加失败");
					hr.setErrorCode(2);
				}else{
					if (hr.getErrorCode() == 0){ //添加成功才需要下发离线表和修改缓存内的离线列表
						if(Utils.isNullStr(repeater)&&!repeater.equals(oldRepeaterString)){
							Utils.sendRepeaterList(repeater);
						}
						if(Utils.isNullStr(oldRepeaterString)&&!oldRepeaterString.equals(repeater)){
							Utils.sendRepeaterList(oldRepeaterString);
						}
						//更新添加设备redis的离线列表
						if(StringUtils.isNotBlank(repeater)){
							updateOffSmokeList( repeater, deviceType, smokeMac, oldRepeaterString);
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (hr == null) {
				hr = new HttpRsult();
				hr.setErrorCode(2);
				hr.setError("添加失败");
			}
			JSONObject jObject = new JSONObject(hr);
			try {
				resp.getWriter().write(jObject.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
//	}else {
//			rs = new APIResult();
//			rs.setCode(2);
//			rs.setMessage("token验证失败");
//		}
//		
//		writeResult();
	}
	
//	public void deleteEquipment(){
//		if (trueToken()){
//			String companyId = req.getParameter("companyId");
//			APIDao apid = new APIDaoImpl();
//			rs = apid.delCompany(companyId);
//		}else {
//			rs = new APIResult();
//			rs.setCode(2);
//			rs.setMessage("token验证失败");
//		}
//		
//		writeResult();
//	}
//	
//	public void updateEquipment(){
//		if (trueToken()){
//			String companyId = req.getParameter("companyId");
//			String companyName = req.getParameter("companyName");
//			String companyCode = req.getParameter("companyCode"); //非必需
//			String address = req.getParameter("address");
//			String contactName = req.getParameter("contactName");
//			String contactTel = req.getParameter("contactTel");
//			if (StringUtils.isBlank(companyId) || StringUtils.isBlank(companyName) || StringUtils.isBlank(address)
//					|| StringUtils.isBlank(contactName) || StringUtils.isBlank(contactTel)){
//				rs = new APIResult();
//				rs.setCode(1);
//				rs.setMessage("参数错误");
//			} else {
//				APIDao apid = new APIDaoImpl();
//				rs = apid.updateCompany(companyId,companyName,companyCode,address,contactName,contactTel);
//			}
//		}else {
//			rs = new APIResult();
//			rs.setCode(2);
//			rs.setMessage("token验证失败");
//		}
//		
//		writeResult();
//	}
	
	public void getEquipment(){
		if (trueToken()){
			if (checkParams()){
				APIDao apid = new APIDaoImpl();
				List<Equipment> equipments = apid.getEquipments(id, createDateB, createDateE, updateDateB, updateDateE,pageNo,pageSize,areaid);
				int count  = apid.getEquipmentCount(id, createDateB, createDateE, updateDateB, updateDateE,areaid);
				rs = packData(equipments,count);
			} else {
				rs = new APIResult();
				rs.setCode(1);
				rs.setMessage("参数错误");
			}
		} else {
			rs = new APIResult();
			rs.setCode(2);
			rs.setMessage("token验证失败");
		}
		writeResult();
	}
	
	public void getStatistics(){
		if (trueToken()){
			String stisDataBStr = req.getParameter("statiDataB");
			String stisDataEStr = req.getParameter("statiDataE");
			String pageNoStr = req.getParameter("pageNo");
			String pageSizeStr = req.getParameter("pageSize");
			if(StringUtils.isNotBlank(pageNoStr)){
				pageNo = Integer.parseInt(pageNoStr);
			}
			if(StringUtils.isNotBlank(pageSizeStr)){
				pageSize = Integer.parseInt(pageSizeStr);
			}
			if (StringUtils.isNotBlank(stisDataBStr)){
				createDateB = Long.parseLong(stisDataBStr)*1000;
			}
			if (StringUtils.isNotBlank(stisDataEStr)){
				createDateE = Long.parseLong(stisDataEStr)*1000;
			}
			
			APIDao apid = new APIDaoImpl();
			List<StisEntity> equipments = apid.getStatics(createDateB,createDateE,pageNo,pageSize,areaid);
			int count = apid.getStisCount(createDateB,createDateE,areaid);
			rs = packData(equipments,count);
		}  else {
			rs = new APIResult();
			rs.setCode(2);
			rs.setMessage("token验证失败");
		}
		
		writeResult();
	}
	
	private boolean checkParams(){
		boolean paramsCorrect = false;
		String pageNoStr = req.getParameter("pageNo");
		String pageSizeStr = req.getParameter("pageSize");
		String createDateBStr =req.getParameter("createDateB");
		String createDateEStr =req.getParameter("createDateE");
		String updateDateBStr =req.getParameter("updateDateB");
		String updateDateEStr =req.getParameter("updateDateE");
		id =req.getParameter("id");
		if(StringUtils.isNotBlank(pageNoStr)){
			pageNo = Integer.parseInt(pageNoStr);
		}
		if(StringUtils.isNotBlank(pageSizeStr)){
			pageSize = Integer.parseInt(pageSizeStr);
		}
		if (StringUtils.isNotBlank(createDateBStr)){
			createDateB = Long.parseLong(createDateBStr)*1000;
		}
		if (StringUtils.isNotBlank(createDateEStr)){
			createDateE = Long.parseLong(createDateEStr)*1000;
		}
		if (StringUtils.isNotBlank(updateDateBStr)){
			updateDateB = Long.parseLong(updateDateBStr)*1000;
		}
		if (StringUtils.isNotBlank(updateDateEStr)){
			updateDateE = Long.parseLong(updateDateEStr)*1000;
		}
		
		System.out.println((917233665 - 917233665)+" compare to "+ MAXINTERVAL);
		
		if (StringUtils.isNotBlank(id) ){
			paramsCorrect = true;
		}else  if    (createDateE - createDateB > 0 ) {
			paramsCorrect = true;
		}else if ( updateDateE - updateDateB > 0){
			paramsCorrect = true;
		} else {
			paramsCorrect = false;
		}
		
		return paramsCorrect;
	}
	
	private APIResult packData(List<?> list,int count){
		APIResult rs = new APIResult();
		rs.setCode(0);
		rs.setMessage("获取成功");
		int pageCount = 0;
		if (count % pageSize == 0){
			pageCount = count /pageSize;
		} else {
			pageCount = count/pageSize + 1;
		}
		
		DataEntity de = new DataEntity();
		de.setPageNo(pageNo);
		de.setPageSize(pageSize);
		de.setPageCount(pageCount);
		de.setCount(count);
		de.setList(list);
//		rs.setData(new JSONObject(de).toString());
		
//		JSONArray jsonarray = JSONArray.parseArray(JSON.toJSONString(list));
//		Map<String,Object> data = new HashMap<String,Object>();
//		data.put("pageNo", pageNo);
//		data.put("pageSize", pageSize);
//		data.put("pageCount", pageCount);
//		data.put("count", count);
//		data.put("list",JSONArray.toJSON(jsonarray).toString());
		rs.setData(de);
		return rs;
	} 
	
	private void writeResult(){
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(rs);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean trueToken(){
		String token = req.getHeader("tokenId");
		if (StringUtils.isBlank(token)|| !token.equals(APIAuthAction.tokenId)){
			return false;
		}
		areaid = APIDaoImpl.getAreaIdByToken(token);
		return true;
	}
	
	private void  updateOffSmokeList(String repeater,String deviceType,String smokeMac,String oldRepeaterString){
		try{
			//若主机是新加的，添加到redis
			Jedis jedis = RedisConnection.getJedis();
			if (jedis!=null) {
				String requestId = UUID.randomUUID().toString().replace("-", "");
				
				if (!deviceType.equals("5")){
					if (!RedisOps.exist(jedis,"R"+repeater)) {
						Repeater rep  = new Repeater();
						rep.setNetState(0);
						rep.setHeartime(0);
						rep.setRepeaterMac(repeater);
						rep.setPowerChangeTime(0);
						rep.setPowerState(0);
						RedisOps.setObject(jedis,"R"+repeater, rep);
						
						List<String> offMacs = new ArrayList<String>();
						offMacs.add(smokeMac);
						RedisOps.setList(jedis,repeater, offMacs);
					} else {
						//对旧的主机下离线列表进行处理
						if (StringUtils.isNotBlank(oldRepeaterString)&&!repeater.equals(oldRepeaterString)){
							while(!RedisOps.tryGetDistributedLock(jedis, "L"+oldRepeaterString, requestId, 10000)){
								try {
									Thread.currentThread().sleep(300);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							List<String> oldOffMacs = RedisOps.getList(jedis,oldRepeaterString);
							Iterator<String> it = oldOffMacs.iterator();
							boolean changed = false;
							while(it.hasNext()){
								if (it.next().equals(smokeMac)){
									it.remove();
									changed = true;
									break;
								}
							}
							if (changed){
								RedisOps.setList(jedis,oldRepeaterString,oldOffMacs);
							}
							RedisOps.releaseDistributedLock(jedis, "L"+oldRepeaterString, requestId);
						}
						
						//对新主机下离线列表进行处理
						while(!RedisOps.tryGetDistributedLock(jedis, "L"+repeater, requestId, 10000)){
							try {
								Thread.currentThread().sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						List<String> newOffMacs = RedisOps.getList(jedis,repeater);
						Iterator<String> it = newOffMacs.iterator();
						boolean changed = true;
						while(it.hasNext()){
							if (it.next().equals(smokeMac)){
								changed = false;
								break;
							}
						}
						if (changed){
							newOffMacs.add(smokeMac);
							RedisOps.setList(jedis,repeater,newOffMacs);
						}
						RedisOps.releaseDistributedLock(jedis, "L"+repeater, requestId);
						
					}
				}
				
				jedis.close();
			}
				
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void getAllCompany(){
		APIDao ad = new APIDaoImpl();
		String areaId = req.getParameter("areaId");
		Map<String,String> map = null;
		if (StringUtils.isNotBlank(areaId))
			map = ad.getAllCompany(areaId);
		else 
			map = ad.getAllCompany();
		
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(map);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void getAllBuilding(){
		APIDao ad = new APIDaoImpl();
		String areaId = req.getParameter("areaId");
		Map<String,String> map = null;
		if (StringUtils.isNotBlank(areaId))
			map = ad.getAllBuilding(areaId);
		else 
			map = ad.getAllBuilding();
		
		
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(map);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void getAllBuildingType(){
		APIDao ad = new APIDaoImpl();
		Map<String,String> map = ad.getAllBuildingType();
		
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(map);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void getAllCity(){
		APIDao ad = new APIDaoImpl();
		Map<String,String> map = ad.getAllCity();
		
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(map);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getAllSystem(){
		APIDao ad = new APIDaoImpl();
		Map<String,String> map = ad.getAllSystem();
		
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(map);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void selectCountry(){
		String cityCode = req.getParameter("cityCode");
		APIDao ad = new APIDaoImpl();
		Map<String,String> map = ad.selectCountry(cityCode);
		
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(map);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void selectTown(){
		String countryCode = req.getParameter("countryCode");
		APIDao ad = new APIDaoImpl();
		Map<String,String> map = ad.selectTown(countryCode);
		
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(map);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(GetTime.getTimeByString("2019-01-18 00:00:00"));
		System.out.println(GetTime.getTimeByString("2019-01-22 00:00:00"));
	}

}
