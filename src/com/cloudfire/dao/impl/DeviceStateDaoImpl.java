package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.AllCameraDao;
import com.cloudfire.dao.DeviceStateDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.DeviceCostEntity;
import com.cloudfire.entity.DeviceStateEntity;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.query.SmokeQuery;

public class DeviceStateDaoImpl implements DeviceStateDao {
	
	PlaceTypeDao mPlaceTypeDao = null;
	AllCameraDao mAllCameraDao = null;

	@Override
	public List<DeviceStateEntity> getDeviceStates(String user,List<String> aids) {
		if(aids == null || aids.size() == 0){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id,devName,systemName from devsystem where id in(");
		sql.append(" SELECT devid from devices where id in(");
		sql.append(" SELECT devicetype from smoke where areaid in(");
		for(String aid : aids){
			sql.append(aid+",");
		}
		sql.append(" -1) GROUP by devicetype");
		sql.append(" ))");
		
		List<DeviceStateEntity> dslist = new ArrayList<DeviceStateEntity>();;
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		DeviceStateEntity dse = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				dse = new DeviceStateEntity();
				dse.setId(rs.getInt("id"));
				dse.setDevName(rs.getString("devName"));
				dse.setSystemName(rs.getString("systemName"));
				dslist.add(dse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return dslist;
	}

	@Override
	public Map<String, Map<Integer, String>> getDeviceStateToMap(List<String> aids) {
		if(aids == null || aids.size() == 0){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id,devName,systemName from devsystem where id in(");
		sql.append(" SELECT devid from devices where id in(");
		sql.append(" SELECT devicetype from smoke where areaid in(");
		for(String aid : aids){
			sql.append(aid+",");
		}
		sql.append(" -1) GROUP by devicetype");
		sql.append(" ))");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		Map<String, Map<Integer, String>> result = new LinkedHashMap<String,Map<Integer,String>>();
		Map<Integer,String> devMap = null;
		try {
			rs = ps.executeQuery();
			String devNames = "";
			while(rs.next()){
				String dName = rs.getString("systemName");
				if(!devNames.equals(dName)){
					if(devMap!=null){
						result.put(devNames, devMap);
					}
					devMap = new HashMap<Integer,String>();
					devNames = rs.getString("systemName");
				}
				devMap.put(rs.getInt("id"), rs.getString("devName"));
			}
			if(devMap!=null){
				result.put(devNames, devMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public int getAllDeviceCount(List<String> aids, SmokeQuery query) {
		// TODO Auto-generated method stub
		if(aids == null || aids.size()==0){
			return 0;
		}
		int totalcount = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*) as totalcount from ( ");
		
		sql.append(" select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.freeTime, d.money, s.stopTime from smoke s,areaidarea a,devices d  ");
		sql.append(" where 1=1 and s.areaId = a.areaId and d.id = s.deviceType  ");
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql.append(" and s.deviceType=" + query.getDeviceType());
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql.append(" and s.mac like '%" + query.getMac() + "%'");
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql.append(" and s.netState ='" + query.getNetState() + "'");
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql.append(" and s.areaId = '"+ query.getAreaName() + "'");
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql.append(" and s.placeTypeId = '" + query.getPlaceType() + "'");
			}
			if (StringUtils.isNotBlank(query.getType())){
				sql.append(" and d.devid = " + query.getType());
			}
			
			sql.append(" and s.areaId in (-1");
			for(String aid : aids){
				sql.append(","+aid);
			}
			sql.append(") order by s.time desc");
		}
		sql.append(" ) aa");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}

	@Override
	public List<SmartControlEntity> getAllDeviceInfo(List<String> aids, SmokeQuery query) {
		if(aids == null||aids.size() == 0){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select  s.named,s.mac,s.address,s.netState,s.repeater,a.area,s.placeTypeId,s.time,s.deviceType,s.freeTime, d.money, s.stopTime from smoke s,areaidarea a,devices d  ");
		sql.append(" where 1=1 and s.areaId = a.areaId and d.id = s.deviceType  ");
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceType())) {
				sql.append(" and s.deviceType=" + query.getDeviceType());
			}
			if (!StringUtils.isBlank(query.getMac())) {
				sql.append(" and s.mac like '%" + query.getMac() + "%'");
			}
			if (!StringUtils.isBlank(query.getNetState())) {
				sql.append(" and s.netState ='" + query.getNetState() + "'");
			}
			if (!StringUtils.isBlank(query.getAreaName())) {
				sql.append(" and s.areaId = '"+ query.getAreaName() + "'");
			}
			if (!StringUtils.isBlank(query.getPlaceType())) {
				sql.append(" and s.placeTypeId = '" + query.getPlaceType() + "'");
			}
			if (StringUtils.isNotBlank(query.getType())){
				sql.append(" and d.devid = " + query.getType());
			}
			
			sql.append(" and s.areaId in (-1");
			for(String aid : aids){
				sql.append(","+aid);
			}
			sql.append(") order by s.time desc");
		}
		sql.append(" ) aa");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		List<SmartControlEntity> sceList = new ArrayList<SmartControlEntity>();
		SmartControlEntity dce = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				dce = new SmartControlEntity();
//				dce.seta
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return sceList;
	}

}
