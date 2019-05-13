package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.IDealOkMsg;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.DealOkAlarmEntity;

public class DealOkMsgImpl implements IDealOkMsg {
	
	public List<DealOkAlarmEntity> getokDealAlarmMsg(List<String> areaIds,DealOkAlarmEntity query) {
		String limit = " limit "+query.getStartRow() +","+query.getPageSize();
		
		StringBuffer sb = new StringBuffer();
		sb.append("select a.alarmTime,a.dealTime,a.dealUserId,a.dealPeople,a.alarmType,a.dealDetail ,a.alarmFamily,s.deviceType,s.address,s.mac,s.named,d.deviceName,tp.alarmName ");
		sb.append(" from alarm a,smoke s,devices d,alarmtype tp ");
		sb.append(" where d.id=s.deviceType and tp.alarmId=a.alarmType and a.smokeMac = s.mac and a.ifDealAlarm=1 ");
		
		if (StringUtils.isNotBlank(query.getAlarmAddress())) {
			sb.append( " and s.address like '%"+query.getAlarmAddress()+ "%'");
		}
		if (StringUtils.isNotBlank(query.getDeviceType())) {
			sb.append( " and s.deviceType = " +query.getDeviceType());
		}
		if (StringUtils.isNotBlank(query.getDevMac())) {
			sb.append( " and s.mac like '%"+query.getDevMac()+"%'");
		}
		if (StringUtils.isNotBlank(query.getAlarmType())) {
			sb.append( " and a.alarmType = " +query.getAlarmType());
		}
		
		int len=areaIds.size();
		sb.append(" and s.areaId in ( ");
		if (len ==1) {
			sb.append(areaIds.get(0)+") ");
		} else {
			for (int i=0 ;i<len- 1 ;i ++) {
				sb.append(areaIds.get(i)+",");
			}
			sb.append(areaIds.get(len - 1) +")");
		}
		
		sb.append(" order by  a.id desc ");
		
		String mySql = sb.toString() + limit;
//		String mySql ="select aa.*,count(*) alarmCount from ("+sb.toString()+" ) aa group by aa.mac,aa.alarmType  desc"+ limit;
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, mySql);
		ResultSet rs = null;
		List<DealOkAlarmEntity> list = new ArrayList<DealOkAlarmEntity>();
		
		try {
			rs = ppst.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				DealOkAlarmEntity dealOkAlarmEntity = new DealOkAlarmEntity();
				dealOkAlarmEntity.setRow(++ row);
				dealOkAlarmEntity.setAlarmAddress(rs.getString("address"));
				dealOkAlarmEntity.setDealDetail(rs.getString("dealdetail"));
//				dealOkAlarmEntity.setAlarmCount(rs.getString("alarmCount"));
				dealOkAlarmEntity.setAlarmTime(rs.getString("alarmTime"));
				dealOkAlarmEntity.setDealTime(rs.getString("dealTime"));
				dealOkAlarmEntity.setDealUserId(rs.getString("dealUserId"));
				dealOkAlarmEntity.setDealUserName(rs.getString("dealPeople"));
				dealOkAlarmEntity.setDevMac(rs.getString("mac"));
				dealOkAlarmEntity.setNamed(rs.getString("named"));
//				int alarmType = rs.getInt(4);
//				int alarmFamily = rs.getInt(9);//alarmFamily
				dealOkAlarmEntity.setDevName(rs.getString("deviceName"));
				dealOkAlarmEntity.setDeviceType(rs.getString("deviceType"));
				dealOkAlarmEntity.setAlarmType(rs.getString("alarmName"));
				list.add(dealOkAlarmEntity);
		  }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(connection);
		}
		return list;
	}
	

	public int getDealAlarmMsgCount(List<String> areaIds,DealOkAlarmEntity query){
		int totalcount= 0;
		StringBuffer sb = new StringBuffer();
		sb.append("select count(a.id) as totalcount " );
		sb.append(" from alarm a,smoke s,devices d,alarmtype tp ");
		sb.append(" where d.id=s.deviceType and tp.alarmId=a.alarmType and a.smokeMac = s.mac and a.ifDealAlarm=1 ");
	
		if (StringUtils.isNotBlank(query.getAlarmAddress())) {
			sb.append( " and s.address like '%"+query.getAlarmAddress()+ "%'");
		}
		if (StringUtils.isNotBlank(query.getDeviceType())) {
			sb.append( " and s.deviceType = " +query.getDeviceType());
		}
		if (StringUtils.isNotBlank(query.getDevMac())) {
			sb.append( " and s.mac like '%"+query.getDevMac()+"%'");
		}
		if (StringUtils.isNotBlank(query.getAlarmType())) {
			sb.append( " and a.alarmType = "+query.getAlarmType());
		}
		
		int len = areaIds.size();
		sb.append(" and s.areaId in (");
		if (len ==1) {
			sb.append(areaIds.get(0)+") ");
		}else {
			for (int i=0 ;i<len - 1 ;i ++) {
				sb.append(areaIds.get(i)+",");
			}
			sb.append(areaIds.get(len - 1)+") ");
		}
		
//		sb.append(" group by s.mac,a.dealTime,a.alarmType)temp");
		
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sb.toString());
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {				
				totalcount = rs.getInt("totalcount");		
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(connection);
		}
		
		return totalcount;
	}
	

}
