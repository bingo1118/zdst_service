package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.IAlarmInfoDao_PC;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.query.AlarmInfoQuery;
import com.cloudfire.until.Constant;

public class AlarmInfoDaoImpl_PC implements IAlarmInfoDao_PC{
	
	/** 根据用户的userId 和权限查询报警的信息 */
	public int getAllAlarmInfoCount(List<String> areaIds,AlarmInfoQuery query){
		StringBuffer sb = new StringBuffer();
		/*sb.append( "select count(a.id) as totalcount from alarm a where 1=1 ");
		if (StringUtils.isNotBlank(query.getAlarmType())) {
			sb.append( " and a.alarmType="+query.getAlarmType());
		}
		if (StringUtils.isNotBlank(query.getEndtime())) {
			sb.append(" and a.alarmTime <= '" + query.getEndtime() + "'");
		}
		if (StringUtils.isNotBlank(query.getBegintime())) {
			sb.append( " and a.alarmTime >= '" + query.getBegintime() + "'");
		}
		
		sb.append(" and a.smokeMac in ( select  mac  from smoke s where 1=1 ");
		
		if (StringUtils.isNotBlank(query.getDeviceType())) {
			sb.append( " and s.deviceType= "+query.getDeviceType());							
		}
		if (StringUtils.isNotBlank(query.getNetState())) {
			sb.append(  " and s.netState= "+query.getNetState());
		}
		if (StringUtils.isNotBlank(query.getDevMac())) {
			sb.append( " and s.mac like '%" + query.getDevMac() + "%'");
		}
		if (StringUtils.isNotBlank(query.getAreaName())) {
			sb.append(  " and s.areaId = " + query.getAreaName());
		}

		//区域的查询
		int len = areaIds.size();
		if(len==1){
			sb.append( " and s.areaId in ("+areaIds.get(0)+")  ");
		}else {
			sb.append( " and s.areaId in (");
			for(int i=0;i< len - 1;i++){
				sb.append(areaIds.get(i)+", ");
			}
			sb.append(areaIds.get(len - 1)+")");
		}
		
		sb.append(" )");*/
	
		sb.append("select a.alarmTime,a.alarmType,a.alarmFamily,a.alarmTruth,s.address,s.deviceType,s.named,s.principal1,s.principal1Phone,s.netState as netState,s.mac as mac,area.area as area ,p.placeName as placeName,d.deviceName,tp.alarmName ");
		sb.append(" from alarm a,smoke s,areaidarea area,placetypeidplacename p,devices d,alarmtype tp" );
        sb.append(" where a.smokeMac = s.mac and d.id=s.deviceType and tp.alarmId=a.alarmtype  and area.areaId=s.areaId and p.placeTypeId = s.placeTypeId  ");

		//开始添加查询条件
		if (StringUtils.isNotBlank(query.getDeviceType())) {
			 sb.append(" and s.deviceType= "+query.getDeviceType());							
		}
		if (StringUtils.isNotBlank(query.getNetState())) {
			 sb.append(" and s.netState= "+query.getNetState());
		}
		if (StringUtils.isNotBlank(query.getAlarmType())) {
			 sb.append(" and a.alarmType="+query.getAlarmType());
		}
		if (StringUtils.isNotBlank(query.getDevMac())) {
			 sb.append(" and s.mac like '%" + query.getDevMac() + "%'");
		}
		if (StringUtils.isNotBlank(query.getAreaName())) {
			 sb.append(" and s.areaId =" + query.getAreaName());
		}
		if (StringUtils.isNotBlank(query.getEndtime())) {
			 sb.append(" and a.alarmTime <= '" + query.getEndtime() + "'");
		}
		if (StringUtils.isNotBlank(query.getBegintime())) {
			 sb.append(" and a.alarmTime >= '" + query.getBegintime() + "'");
		}
		//区域的查询
		int len = areaIds.size();
		sb.append(" and s.areaId in (");
		if (len==1) {
			 sb.append(areaIds.get(0)+")"); 
		} else {
			for(int i=0;i<len - 1;i++){
				sb.append(areaIds.get(i)+", ");
			}
			sb.append(areaIds.get(len - 1)+")");
		}
		
		String sqlString ="select count(*) as totalcount from ( "+sb.toString() +" ) aa";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sqlString);
		ResultSet rs = null;
		int totalcount = 0;
		try {				
			rs = ppst.executeQuery();				
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");				
			}				
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(rs);
			DBConnectionManager.close(connection);
		}
		return totalcount;
	}
	
	
	
	
	/** 根据用户的userId 和权限查询所有的报警的信息记录 */
	public List<AlarmInfoQuery> getAllAlarmInfoMsg(List<String> areaIds,AlarmInfoQuery query){
		StringBuffer sb = new StringBuffer();
		sb.append("select a.alarmTime,a.alarmType,a.alarmFamily,a.alarmTruth,s.address,s.deviceType,s.named,s.principal1,s.principal1Phone,s.netState as netState,s.mac as mac,area.area as area ,p.placeName as placeName,d.deviceName,tp.alarmName ");
		sb.append(" from alarm a,smoke s,areaidarea area,placetypeidplacename p,devices d,alarmtype tp" );
        sb.append(" where a.smokeMac = s.mac and d.id=s.deviceType and tp.alarmId=a.alarmtype  and area.areaId=s.areaId and p.placeTypeId = s.placeTypeId  ");

		String limit = " limit "+query.getStartRow() + ","+query.getPageSize();
		
		//开始添加查询条件
		if (StringUtils.isNotBlank(query.getDeviceType())) {
			 sb.append(" and s.deviceType= "+query.getDeviceType());							
		}
		if (StringUtils.isNotBlank(query.getNetState())) {
			 sb.append(" and s.netState= "+query.getNetState());
		}
		if (StringUtils.isNotBlank(query.getAlarmType())) {
			 sb.append(" and a.alarmType="+query.getAlarmType());
		}
		if (StringUtils.isNotBlank(query.getDevMac())) {
			 sb.append(" and s.mac like '%" + query.getDevMac() + "%'");
		}
		if (StringUtils.isNotBlank(query.getAreaName())) {
			 sb.append(" and s.areaId =" + query.getAreaName());
		}
		if (StringUtils.isNotBlank(query.getEndtime())) {
			 sb.append(" and a.alarmTime <= '" + query.getEndtime() + "'");
		}
		if (StringUtils.isNotBlank(query.getBegintime())) {
			 sb.append(" and a.alarmTime >= '" + query.getBegintime() + "'");
		}
		//区域的查询
		int len = areaIds.size();
		sb.append(" and s.areaId in (");
		if (len==1) {
			 sb.append(areaIds.get(0)+")"); 
		} else {
			for(int i=0;i<len - 1;i++){
				sb.append(areaIds.get(i)+", ");
			}
			sb.append(areaIds.get(len - 1)+")");
		}
		
		sb.append(" order by a.id desc "+ limit);
		
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sb.toString());
		ResultSet rs = null;
		List<AlarmInfoQuery> list = new ArrayList<>();
		try {
			rs = ppst.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				AlarmInfoQuery query2 = new AlarmInfoQuery();	
				query2.setRow(++ row);
				query2.setAlarmTime(rs.getString("alarmTime")); //ok
				query2.setAlarmAddress(rs.getString("address"));//ok
				query2.setPrincipal1(rs.getString("principal1"));
				query2.setPrincipalPhone(rs.getString("principal1Phone"));
				query2.setAreaName(rs.getString("area"));
				query2.setPlaceName(rs.getString("placeName"));
				query2.setMac(rs.getString("mac"));
				
				int netState = rs.getInt("netState");
				query2.setNetState(netState==0 ?Constant.NETSTATE0:Constant.NETSTATE1);
//				int alarmType = rs.getInt(2);
				query2.setDevMac(rs.getString("named"));
//				int devType = rs.getInt(4);
//				int alarmFamily = rs.getInt(8);
//				int alarmTruth = rs.getInt(9);
				query2.setDevName(rs.getString("deviceName"));
				query2.setAlarmType(rs.getString("alarmName"));
				list.add(query2);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(rs);
			DBConnectionManager.close(connection);
		}				
		return list;
	}
	
	
	
	/** 根据用户的userId 和权限查询报警的信息 */
	public List<AlarmInfoEntity_PC> getAlarmInfo(AlarmInfoEntity_PC query,List<String> areaIds){
		StringBuffer sb = new StringBuffer();
		sb.append("select a.alarmTime,a.alarmType,s.address,s.deviceType,s.named,s.principal1,s.principal1Phone,a.alarmFamily,a.alarmTruth,s.mac as mac,d.deviceName,tp.alarmName ");
		sb.append(" from alarm a,smoke s,devices d,alarmtype tp ");
		sb.append(" where a.smokeMac = s.mac and d.id=s.deviceType and tp.alarmId=a.alarmType and ifDealAlarm = 0  ");

		String limit = " limit "+query.getStartRow() + ","+query.getPageSize();
		
		//开始添加查询条件
		if (StringUtils.isNotBlank(query.getDeviceType())) {
			sb.append(" and s.deviceType= "+query.getDeviceType());							
		}
		if (StringUtils.isNotBlank(query.getAlarmType())) {
			sb.append(" and a.alarmType="+query.getAlarmType());
		}
		if (StringUtils.isNotBlank(query.getMac())) {
			sb.append( " and s.mac like '%" + query.getMac() + "%'");
		}
		if (StringUtils.isNotBlank(query.getEndtime())) {
			sb.append( " and a.alarmTime <= '" + query.getEndtime() + "'");
		}
		if (StringUtils.isNotBlank(query.getBegintime())) {
			sb.append(" and a.alarmTime >= '" + query.getBegintime() + "'");
		}
		
		//区域的个数
		int len = areaIds.size();
		sb.append(" and s.areaId in (");
		if (len==1) {
			sb.append(areaIds.get(0)+") ");
		} else {
			for(int i=0;i<len - 1;i++){
				sb.append(areaIds.get(i)+", ");
			}
			sb.append(areaIds.get(len - 1)+")");
		}
		
		String mySql = new String(sb.toString()+" order by a.id desc "+limit);
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, mySql);
		ResultSet rs = null;
		List<AlarmInfoEntity_PC> list = new ArrayList<>();
		try {
			rs = ppst.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				AlarmInfoEntity_PC alarmInfoquery_PC = new AlarmInfoEntity_PC();	
				alarmInfoquery_PC.setRow(++ row);
				alarmInfoquery_PC.setAlarmTime(rs.getString(1)); //ok
				alarmInfoquery_PC.setAlarmAddress(rs.getString(3));//ok
				alarmInfoquery_PC.setPrincipal1(rs.getString(6));
				alarmInfoquery_PC.setPrincipalPhone(rs.getString(7));
				alarmInfoquery_PC.setMac(rs.getString("mac"));
//				int alarmType = rs.getInt(2);
				alarmInfoquery_PC.setDevMac(rs.getString(5));
//				int devType = rs.getInt(4);
//				int alarmFamily = rs.getInt(8);
//				int alarmTruth = rs.getInt(9);
				alarmInfoquery_PC.setDevName(rs.getString("deviceName"));
				alarmInfoquery_PC.setAlarmType(rs.getString("alarmName"));
				list.add(alarmInfoquery_PC);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(rs);
			DBConnectionManager.close(connection);
		}
		return list;
	}
	
	public int getAlarmInfoCount(List<String> areaIds,AlarmInfoEntity_PC query){
		int totalcount = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("select a.alarmTime,a.alarmType,s.address,s.deviceType,s.named,s.principal1,s.principal1Phone,a.alarmFamily,a.alarmTruth,s.mac as mac,d.deviceName,tp.alarmName ");
		sb.append(" from alarm a,smoke s,devices d,alarmtype tp ");
		sb.append(" where a.smokeMac = s.mac and d.id=s.deviceType and tp.alarmId=a.alarmType and ifDealAlarm = 0  ");

		//开始添加查询条件
		if (StringUtils.isNotBlank(query.getDeviceType())) {
			sb.append(" and s.deviceType= "+query.getDeviceType());							
		}
		if (StringUtils.isNotBlank(query.getAlarmType())) {
			sb.append(" and a.alarmType="+query.getAlarmType());
		}
		if (StringUtils.isNotBlank(query.getMac())) {
			sb.append( " and s.mac like '%" + query.getMac() + "%'");
		}
		if (StringUtils.isNotBlank(query.getEndtime())) {
			sb.append( " and a.alarmTime <= '" + query.getEndtime() + "'");
		}
		if (StringUtils.isNotBlank(query.getBegintime())) {
			sb.append(" and a.alarmTime >= '" + query.getBegintime() + "'");
		}

		//区域的个数
		int len = areaIds.size();
		sb.append(" and s.areaId in (");
		if (len==1) {
			sb.append(areaIds.get(0)+") ");
		} else {
			for(int i=0;i<len - 1;i++){
				sb.append(areaIds.get(i)+", ");
			}
			sb.append(areaIds.get(len - 1)+")");
		}
		
		String sqlString ="select count(*) as totalcount from ( "+sb.toString() +" ) aa";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sqlString);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while(rs.next()){
				totalcount = rs.getInt("totalcount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}	
	
}
