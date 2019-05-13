package com.cloudfire.dao.query.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.ElectricPCDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.dao.impl.ElectricPCDaoImpl;
import com.cloudfire.dao.impl.PlaceTypeDaoImpl;
import com.cloudfire.dao.query.SearchElectricQuery;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.MyElectricInfo;
import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.until.Utils;
import com.cloudfire.until.core.dao.impl.HibernateDaoImpl;


public class SearchElectricQueryImpl  implements SearchElectricQuery{
	private PlaceTypeDao mPlaceTypeDao;
	private ElectricPCDao mElectricPCDao;
	
	//多条件查询电气火灾设备
	public List<SearchElectricInfo> searchElectricInfo(SearchElectricInfo info,List<String> areaList){
		String limit = " limit "+info.getStartRow() +","+info.getPageSize();
		Connection conn = DBConnectionManager.getConnection();
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		List<SearchElectricInfo> list = new ArrayList<>();
		/** 区域的查询*/
		StringBuffer sb = new StringBuffer();

		/**主查询 */
		sb.append("select s.time,s.address,s.mac,s.named,s.netState,s.placeTypeId,s.placeAddress,s.repeater,s.ifAlarm,area.area,s.heartime,s.rssivalue,s.deviceType");
		sb.append(" from smoke s,areaidarea area,devices d"); 
		sb.append(" where s.deviceType = d.id and s.areaId=area.areaId ");
		
		if (info!=null) {
			if (!StringUtils.isBlank(info.getMac())) {
				sb.append(" and s.mac like '%" +info.getMac() + "%'");
			}
			if (!StringUtils.isBlank(info.getNetState())) {
				sb.append(" and s.netState = '" +info.getNetState()+ "'");
			}
			if (!StringUtils.isBlank(info.getAreaName())) {
				sb.append(" and s.areaId = '" + Integer.parseInt(info.getAreaName()) + "'");
			}
			if (!StringUtils.isBlank(info.getPlaceType())) {
				sb.append(" and s.placeTypeId = '" + Integer.parseInt(info.getPlaceType()) + "'");
			}	
			if(StringUtils.isNotBlank(info.getType())){
				sb.append(" and d.devId = " + info.getType());
			}
		}
		
		int len = areaList.size();
		if (len == 0) {
			return null;
		} else {
			sb.append(" and s.areaId in ("+Utils.list2String(areaList)+") order by s.time desc " +limit);
		}
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {	
			int row = info.getStartRow();
			rs = ppst.executeQuery();
			SearchElectricInfo searchInfo = null;
			while (rs.next()) {
				if (searchInfo == null || !searchInfo.getMac().equals(rs.getString(3))) {
					searchInfo = new SearchElectricInfo();
					searchInfo.setRow(++ row);
					searchInfo.setAddSmokeTime(rs.getString(1));
					searchInfo.setAddress(rs.getString(2));
					searchInfo.setAreaName(rs.getString(10));
					searchInfo.setMac(rs.getString(3));
					searchInfo.setName(rs.getString(4));
					String netState = rs.getString(5);
					searchInfo.setNetState(Integer.parseInt(netState)==0 ?"掉线":"在线");
					String placeType = rs.getString(6) + "";
					if (StringUtils.isNumeric(placeType)) {
						searchInfo.setPlaceType(map.get(placeType));
					}
					
					searchInfo.setDeviceType(rs.getInt("deviceType"));
					searchInfo.setPlaceeAddress(rs.getString(7));
					searchInfo.setRepeater(rs.getString(8));
					String ifDealAlarm = rs.getString(9);
					searchInfo.setHeartime(rs.getString("heartime"));
					int ifAlarm = rs.getInt("ifAlarm");
					searchInfo.setIfAlarm(ifAlarm);
					if(ifAlarm == 0){ //报警状态
						searchInfo.setAlarmName("报警");
					}
					searchInfo.setRssivalue(rs.getString("rssivalue"));
					list.add(searchInfo);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	public List<SearchElectricInfo> searchElectricInfos(SearchElectricInfo info,List<String> areaList){
		return null;
	}
	
	public int getElectricInfoCount(SearchElectricInfo info,List<String> areaList){	
		int len = areaList.size();
		int totalcount = 0;		
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		/** 区域的查询*/
		StringBuffer strSql = new StringBuffer();

		/**主查询 */
		String sql = "select s.time,s.address,s.mac,s.named,s.netState" +
				",s.placeTypeId,s.placeAddress,s.repeater,s.ifAlarm,area.area,s.deviceType from smoke s,areaidarea area,devices d " +
				" where s.deviceType = d.id and s.areaId=area.areaId ";
		
		if (info!=null) {
			if (StringUtils.isNotBlank(info.getMac())) {
				sql += " and s.mac like '%" +info.getMac() + "%'";
			}
			if (StringUtils.isNotBlank(info.getNetState())) {
				sql += " and s.netState = '" +info.getNetState()+ "'";
			}
			if (StringUtils.isNotBlank(info.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(info.getAreaName()) + "'";
			}
			if (StringUtils.isNotBlank(info.getPlaceType())) {
				sql += " and s.placeTypeId = '" + Integer.parseInt(info.getPlaceType()) + "'";
			}
			if	(StringUtils.isNotBlank(info.getType())){
				sql += " and d.devId = '" + info.getType() + "'";
			}

			if (len == 0) {
				return (Integer) null;
			}if (len ==1) {
				sql += " and s.areaId in (?) order by s.time desc ";
			}else {
				for (int i=0 ;i<len ;i ++) {
					if (i==0) {
						sql += " and s.areaId in (?,";
					}else if (i == (len-1)) {
						sql += " ?) order by s.time desc ";
					}else {
						sql += " ?, ";
					}
				}
			}
			
		}
		String allSQL =new String(sql + strSql);
		String basicSQL = "select count(*) as totalcount from ( "+allSQL+" ) aa ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {	
			for(int i=1;i<=len ;i++){
				ppst.setString(i, areaList.get(i-1));
			}
			rs = ppst.executeQuery(); 
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
			}	
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
			
		return totalcount;
	}
	
	
	//多条件查询电弧设备
	public List<SearchElectricInfo> searchElectricHu(SearchElectricInfo info,List<String> areaList){
		String limit = " limit "+info.getStartRow() +","+info.getPageSize();
		Connection conn = DBConnectionManager.getConnection();
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		List<SearchElectricInfo> list = new ArrayList<>();
		/** 区域的查询*/
		StringBuffer strSql = new StringBuffer();

		/**主查询 */
//		String sqlBefore = "select * from (";
//		String sql = "select first.*,second.* from (";
		String sql = "";
		sql += "select s.time,s.address,s.mac,s.named,s.netState" +
				",s.placeTypeId,s.placeAddress,s.repeater,s.ifAlarm,area.area,s.heartime,s.rssivalue from smoke s,areaidarea area " +
				" where s.deviceType in (34,35,36) and s.areaId=area.areaId ";
		
		if (info!=null) {
			if (!StringUtils.isBlank(info.getMac())) {
				sql += " and s.mac like '%" +info.getMac() + "%'";
			}
			if (!StringUtils.isBlank(info.getNetState())) {
				sql += " and s.netState = '" +info.getNetState()+ "'";
			}
			if (!StringUtils.isBlank(info.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(info.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(info.getPlaceType())) {
				sql += " and s.placeTypeId = '" + Integer.parseInt(info.getPlaceType()) + "'";
			}	
		}
		
		int len = areaList.size();
		if (len == 0) {
			return null;
		}
		if (len ==1) {
			sql += " and s.areaId in (?) order by s.time desc " +limit;
		}else {
			for (int i=0 ;i<len ;i ++) {
				if (i==0) {
					sql += " and s.areaId in (?,";
				}else if (i == (len-1)) {
					sql += " ?) order by s.time desc " +limit;
				}else {
					sql += " ?, ";
				}
			}
		}
//		sql += ") first left join (select * from " +
//				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
//				"where a.alarmType = al.alarmId and smokeMac  in (select mac  from smoke s where s.deviceType in (34,35,36)) order by alarmTime desc) ala group by smokeMac) second " +
//				" on first.mac = second.smokeMac";	
//		
//		String sqlAfter = ") third left join (select smokeMac,electricType,count(*) from electricinfo where smokeMac in (select mac  from smoke s where s.deviceType in (34,35,36)) group by smokeMac,electricType) fourth on third.mac = fourth.smokeMac ";
//		String allSQL =new String(sqlBefore+sql + strSql+sqlAfter);
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {	
			for(int i=1;i<=len ;i++){
				ppst.setString(i, areaList.get(i-1));
			}
			int row = info.getStartRow();
			rs = ppst.executeQuery();
			SearchElectricInfo searchInfo = null;
			while (rs.next()) {
				if (searchInfo == null || !searchInfo.getMac().equals(rs.getString(3))) {
					searchInfo = new SearchElectricInfo();
					searchInfo.setRow(++ row);
					searchInfo.setAddSmokeTime(rs.getString(1));
					searchInfo.setAddress(rs.getString(2));
					searchInfo.setAreaName(rs.getString(10));
					searchInfo.setMac(rs.getString(3));
					searchInfo.setName(rs.getString(4));
					String netState = rs.getString(5);
					searchInfo.setNetState(Integer.parseInt(netState)==0 ?"掉线":"在线");
	//				searchInfo.setNetState(netState=="0"?"掉线":"在线");
					String placeType = rs.getString(6) + "";
					if (StringUtils.isNumeric(placeType)) {
						searchInfo.setPlaceType(map.get(placeType));
					}
					
					searchInfo.setDeviceType(5);
					searchInfo.setPlaceeAddress(rs.getString(7));
					searchInfo.setRepeater(rs.getString(8));
					String ifDealAlarm = rs.getString(9);
					searchInfo.setHeartime(rs.getString("heartime"));
					searchInfo.setIfDealAlarm(ifDealAlarm.equals("1")?"已处理":"未处理");
					int ifAlarm = rs.getInt("ifAlarm");
					searchInfo.setIfAlarm(ifAlarm);
					if(ifAlarm == 0){ //报警状态
//						searchInfo.setAlarmType(rs.getInt("alarmType"));
						searchInfo.setAlarmName(rs.getString("报警"));
					}
					searchInfo.setRssivalue(rs.getString("rssivalue"));
					searchInfo.setData6(true);
				
				list.add(searchInfo);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	
	//电弧总数量
	public int getElectricHuCount(SearchElectricInfo info,List<String> areaList){	
		int len = areaList.size();
		int totalcount = 0;		
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		/** 区域的查询*/
		StringBuffer strSql = new StringBuffer();

		/**主查询 */
		String sql = "select s.time,s.address,s.mac,s.named,s.netState" +
				",s.placeTypeId,s.placeAddress,s.repeater,s.ifAlarm,area.area from smoke s,areaidarea area " +
				" where s.deviceType in  (34,35,36) and s.areaId=area.areaId ";
		
		if (info!=null) {
			if (!StringUtils.isBlank(info.getMac())) {
				sql += " and s.mac like '%" +info.getMac() + "%'";
			}
			if (!StringUtils.isBlank(info.getNetState())) {
				sql += " and s.netState = '" +info.getNetState()+ "'";
			}
			if (!StringUtils.isBlank(info.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(info.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(info.getPlaceType())) {
				sql += " and s.placeTypeId = '" + Integer.parseInt(info.getPlaceType()) + "'";
			}
			/*if (!StringUtils.isBlank(info.getAlarmBeginTime())) {
				sql +=" and alarm.alarmTime >= '" +info.getAlarmBeginTime()+"'";
			}
			if (!StringUtils.isBlank(info.getAlarmEndTime())) {
				sql += " and alarm.alarmTime <= '" +info.getAlarmEndTime()+ "'";
			}*/

			if (len == 0) {
				return (Integer) null;
			}if (len ==1) {
				sql += " and s.areaId in (?) order by s.time desc ";
			}else {
				for (int i=0 ;i<len ;i ++) {
					if (i==0) {
						sql += " and s.areaId in (?,";
					}else if (i == (len-1)) {
						sql += " ?) order by s.time desc ";
					}else {
						sql += " ?, ";
					}
				}
			}
			
		}
		String allSQL =new String(sql + strSql);
		String basicSQL = "select count(*) as totalcount from ( "+allSQL+" ) aa ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {	
			for(int i=1;i<=len ;i++){
				ppst.setString(i, areaList.get(i-1));
			}
			rs = ppst.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
			}	
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
			
		return totalcount;
	}
	
	
	
	public int getNB_ElectricInfoCount(SearchElectricInfo info,List<String> areaList){	
		int len = areaList.size();
		int totalcount = 0;		
//		mPlaceTypeDao = new PlaceTypeDaoImpl();
//		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		/** 区域的查询*/
		StringBuffer strSql = new StringBuffer();

		/**主查询 */
		String sql = "select s.time,s.address,s.mac,s.named,s.netState" +
				",s.placeTypeId,s.placeAddress,s.repeater,s.ifAlarm,area.area from smoke s,areaidarea area,devices d " +
				" where s.deviceType in (53,59,75,76,77,80,81) and s.areaId=area.areaId and d.id = s.deviceType ";
		
		if (info!=null) {
			if(StringUtils.isNotBlank(info.getType())){
				sql += " and d.devId = " + info.getType();
			}
			if (StringUtils.isNotBlank(info.getMac())) {
				sql += " and s.mac like '%" +info.getMac() + "%'";
			}
			if (!StringUtils.isBlank(info.getNetState())) {
				sql += " and s.netState = '" +info.getNetState()+ "'";
			}
			if (!StringUtils.isBlank(info.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(info.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(info.getPlaceType())) {
				sql += " and s.placeTypeId = '" + Integer.parseInt(info.getPlaceType()) + "'";
			}
			if(StringUtils.isNotBlank(info.getType())){
				sql += " and d.devId = "+info.getType();
			}
			if(StringUtils.isNotBlank(info.getDevictTypeName())){//三相电气 76
				sql += " and s.deviceType "+info.getDevictTypeName();
			}else{
				sql += " and s.deviceType in(53,59,75)";
			}

			if (len == 0) {
				return (Integer) null;
			}if (len ==1) {
				sql += " and s.areaId in (?) order by s.time desc ";
			}else {
				for (int i=0 ;i<len ;i ++) {
					if (i==0) {
						sql += " and s.areaId in (?,";
					}else if (i == (len-1)) {
						sql += " ?) order by s.time desc ";
					}else {
						sql += " ?, ";
					}
				}
			}
			
		}
		String allSQL =new String(sql + strSql);
		String basicSQL = "select count(*) as totalcount from ( "+allSQL+" ) aa ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {	
			for(int i=1;i<=len ;i++){
				ppst.setString(i, areaList.get(i-1));
			}
			rs = ppst.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt("totalcount");
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
			
		return totalcount;
	}
	
	public List<SearchElectricInfo> searchElectricInfo2(SearchElectricInfo info,List<String> areaList){
		String limit = " limit "+info.getStartRow() +","+info.getPageSize();
		Connection conn = DBConnectionManager.getConnection();
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		List<SearchElectricInfo> list = new ArrayList<>();
		/** 区域的查询*/
		StringBuffer strSql = new StringBuffer();

		/**主查询 */
		String sql = "select first.*,second.* from (";
		sql += "select s.time,s.address,s.mac,s.named,s.netState" +
				",s.placeTypeId,s.placeAddress,s.repeater,s.ifAlarm,area.area,s.heartime,s.rssivalue from smoke s,areaidarea area " +
				" where s.deviceType in (5,52) and s.areaId=area.areaId ";
		
		if (info!=null) {
			if (!StringUtils.isBlank(info.getMac())) {
				sql += " and s.mac like '%" +info.getMac() + "%'";
			}
			if (!StringUtils.isBlank(info.getNetState())) {
				sql += " and s.netState = '" +info.getNetState()+ "'";
			}
			if (!StringUtils.isBlank(info.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(info.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(info.getPlaceType())) {
				sql += " and s.placeTypeId = '" + Integer.parseInt(info.getPlaceType()) + "'";
			}	
		}
		
		int len = areaList.size();
		if (len == 0) {
			return null;
		}if (len ==1) {
			sql += " and s.areaId in (?) order by s.time desc " +limit;
		}else {
			for (int i=0 ;i<len ;i ++) {
				if (i==0) {
					sql += " and s.areaId in (?,";
				}else if (i == (len-1)) {
					sql += " ?) order by s.time desc " +limit;
				}else {
					sql += " ?, ";
				}
			}
		}
		sql += ") first left join (select * from " +
				"(select smokeMac,a.alarmType,al.alarmName from alarm a,alarmType al " +
				"where a.alarmType = al.alarmId and smokeMac  in (select mac  from smoke s where s.deviceType in (5,52)) order by a.id desc) ala group by smokeMac) second " +
				" on first.mac = second.smokeMac";	
		
		String allSQL =new String(sql + strSql);
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		ResultSet rs = null;
		try {	
			for(int i=1;i<=len ;i++){
				ppst.setString(i, areaList.get(i-1));
			}
			int row = info.getStartRow();
			rs = ppst.executeQuery();
			SearchElectricInfo searchInfo = null;
			List<String> macs = null;
			while (rs.next()) {
				searchInfo = new SearchElectricInfo();
				searchInfo.setRow(++ row);
				searchInfo.setAddSmokeTime(rs.getString(1));
				searchInfo.setAddress(rs.getString(2));
				searchInfo.setAreaName(rs.getString(10));
				if (macs == null) {
					macs = new ArrayList<String>();
				}
				macs.add(rs.getString(3));
				searchInfo.setMac(rs.getString(3));
				searchInfo.setName(rs.getString(4));
				String netState = rs.getString(5);
				searchInfo.setNetState(Integer.parseInt(netState)==0 ?"掉线":"在线");
//				searchInfo.setNetState(netState=="0"?"掉线":"在线");
				String placeType = rs.getString(6) + "";
				if (StringUtils.isNumeric(placeType)) {
					searchInfo.setPlaceType(map.get(placeType));
				}
				
				searchInfo.setDeviceType(5);
				searchInfo.setPlaceeAddress(rs.getString(7));
				searchInfo.setRepeater(rs.getString(8));
				String ifDealAlarm = rs.getString(9);
				searchInfo.setHeartime(rs.getString("heartime"));
				searchInfo.setIfDealAlarm(ifDealAlarm.equals("1")?"已处理":"未处理");
				int ifAlarm = rs.getInt("ifAlarm");
				searchInfo.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
					searchInfo.setAlarmType(rs.getInt("alarmType"));
					searchInfo.setAlarmName(rs.getString("alarmName"));
				}
				searchInfo.setRssivalue(rs.getString("rssivalue"));
				list.add(searchInfo);
			}
			
			List<SearchElectricInfo> secondList = getElectricInfoByMacs(macs);
			for (int i = 0; i < secondList.size(); i++) {
				SearchElectricInfo second = secondList.get(i);
				for(int j = 0; j< list.size(); j++) {
					SearchElectricInfo first = list.get(j);
					if (second.getMac().equals(first.getMac())) {
						if (second.isData6()){
							list.get(j).setData6(true);
						} 
						if (second.isData7()){
							list.get(j).setData7(true);
						}
						if (second.isData8()){
							list.get(j).setData8(true);
						}
						if (second.isData9()){
							list.get(j).setData9(true);
						}
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	public List<SearchElectricInfo> searchNBElectricInfo(SearchElectricInfo info,List<String> areaList){
		String limit = " limit "+info.getStartRow() +","+info.getPageSize();
		Connection conn = DBConnectionManager.getConnection();
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		List<SearchElectricInfo> list = new ArrayList<>();
		/** 区域的查询*/
		StringBuffer strSql = new StringBuffer();

		/**主查询 */
//		String sql = "select first.*,second.* from (";
		String sql = "";
		sql += "select s.time,s.address,s.mac,s.named,s.netState" +
				",s.placeTypeId,s.placeAddress,s.repeater,s.ifAlarm,area.area,s.heartime,s.rssivalue from smoke s,areaidarea area,devices d " +
				" where s.deviceType in (53,59,75,76,77,80,81) and s.areaId=area.areaId and d.id = s.deviceType ";
		
		if (info!=null) {
			if(StringUtils.isNotBlank(info.getType())){
				sql += " and d.devId = " + info.getType();
			}
			if (!StringUtils.isBlank(info.getMac())) {
				sql += " and s.mac like '%" +info.getMac() + "%'";
			}
			if (!StringUtils.isBlank(info.getNetState())) {
				sql += " and s.netState = '" +info.getNetState()+ "'";
			}
			if (!StringUtils.isBlank(info.getAreaName())) {
				sql += " and s.areaId = '" + Integer.parseInt(info.getAreaName()) + "'";
			}
			if (!StringUtils.isBlank(info.getPlaceType())) {
				sql += " and s.placeTypeId = '" + Integer.parseInt(info.getPlaceType()) + "'";
			}
			if(StringUtils.isNotBlank(info.getType())){
				sql += " and d.devId = " + info.getType();
			}
			if(StringUtils.isNotBlank(info.getDevictTypeName())){//三相电气 76
				sql += " and s.deviceType "+info.getDevictTypeName();
			}else{
				sql += " and s.deviceType in(53,59,75,76,77,80,81)";
			}
		}
		
		int len = areaList.size();
		if (len == 0) {
			return null;
		}if (len ==1) {
			sql += " and s.areaId in (?) order by s.time desc " +limit;
		}else {
			for (int i=0 ;i<len ;i ++) {
				if (i==0) {
					sql += " and s.areaId in (?,";
				}else if (i == (len-1)) {
					sql += " ?) order by s.time desc " +limit;
				}else {
					sql += " ?, ";
				}
			}
		}
		
		String allSQL =new String(sql + strSql);
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		ResultSet rs = null;
		try {	
			for(int i=1;i<=len ;i++){
				ppst.setString(i, areaList.get(i-1));
			}
			int row = info.getStartRow();
			rs = ppst.executeQuery();
			SearchElectricInfo searchInfo = null;
			List<String> macs = null;
			while (rs.next()) {
				searchInfo = new SearchElectricInfo();
				searchInfo.setRow(++ row);
				searchInfo.setAddSmokeTime(rs.getString(1));
				searchInfo.setAddress(rs.getString(2));
				searchInfo.setAreaName(rs.getString(10));
				if (macs == null) {
					macs = new ArrayList<String>();
				}
				macs.add(rs.getString(3));
				searchInfo.setMac(rs.getString(3));
				searchInfo.setName(rs.getString(4));
				String netState = rs.getString(5);
				searchInfo.setNetState(Integer.parseInt(netState)==0 ?"掉线":"在线");
//				searchInfo.setNetState(netState=="0"?"掉线":"在线");
				String placeType = rs.getString(6) + "";
				if (StringUtils.isNumeric(placeType)) {
					searchInfo.setPlaceType(map.get(placeType));
				}
				
				searchInfo.setDeviceType(5);
				searchInfo.setPlaceeAddress(rs.getString(7));
				searchInfo.setRepeater(rs.getString(8));
				String ifDealAlarm = rs.getString(9);
				searchInfo.setHeartime(rs.getString("heartime"));
				searchInfo.setIfDealAlarm(ifDealAlarm.equals("1")?"已处理":"未处理");
				int ifAlarm = rs.getInt("ifAlarm");
				searchInfo.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
					searchInfo.setAlarmName("报警");
				}
				searchInfo.setRssivalue(rs.getString("rssivalue"));
				list.add(searchInfo);
			}
			
//			List<SearchElectricInfo> secondList = getElectricInfoByMacs(macs);
//			for (int i = 0; i < secondList.size(); i++) {
//				SearchElectricInfo second = secondList.get(i);
//				for(int j = 0; j< list.size(); j++) {
//					SearchElectricInfo first = list.get(j);
//					if (second.getMac().equals(first.getMac())) {
//						if (second.isData6()){
//							list.get(j).setData6(true);
//						} 
//						if (second.isData7()){
//							list.get(j).setData7(true);
//						}
//						if (second.isData8()){
//							list.get(j).setData8(true);
//						}
//						if (second.isData9()){
//							list.get(j).setData9(true);
//						}
//					}
//				}
//			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	public List<SearchElectricInfo> getElectricInfoByMacs(List<String> macs){
		String sql = "select smokeMac,electricType,count(*) from electricinfo where smokeMac in (" ;
		Iterator<String> iterator = macs.iterator();
		while(iterator.hasNext()) {
			String next = iterator.next();
			if (iterator.hasNext()){
				sql += "'"+next+"',";
			} else {
				sql += "'"+next+"'";
			}
		}
		sql += ") group by smokeMac,electricType";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs  = null;
		SearchElectricInfo searchInfo = null;
		List<SearchElectricInfo> list = new ArrayList<>();
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				if (searchInfo == null || !searchInfo.getMac().equals(rs.getString("smokeMac"))) {
					if (searchInfo != null){
						list.add(searchInfo);
					}
					searchInfo = new SearchElectricInfo();
					searchInfo.setMac(rs.getString("smokeMac"));
				}
				int electricType = rs.getInt("electricType");
				if (electricType != 0){
					switch(electricType){
					case 6:
					case 61:
					case 62:
					case 63:
						searchInfo.setData6(true);
						break;
					case 7:
					case 71:
					case 72:
					case 73:
						searchInfo.setData7(true);
						break;
					case 8:
					case 81:
					case 82:
					case 83:
						searchInfo.setData8(true);
						break;
					case 9:
					case 91:
					case 92:
					case 93:
						searchInfo.setData9(true);
						break;
					}
				}
			}
			list.add(searchInfo);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	@Override
	public SearchElectricInfo queryDeviceTypeByMac(String mac, List<String> areaids) {
		String sql="select a.*,e.* FROM (select mac from smoke  where mac= ?  ";
		
		/*int len = areaids.size();
		if (len == 0) {
			return null;
		}if (len ==1) {
			sql += " and s.areaId in (?) order by s.time desc ";
		}else {
			for (int i=0 ;i<len ;i ++) {
				if (i==0) {
					sql += " and s.areaId in (?,";
				}else if (i == (len-1)) {
					sql += " ?) order by s.time desc ";
				}else {
					sql += " ?, ";
				}
			}
		}*/
		sql += ") a LEFT JOIN (SELECT smokeMac,electricType FROM electricinfo ) e on  a.mac = e.smokeMac ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		SearchElectricInfo searchInfo = null;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			/*for(int i=1;i<=len ;i++){
				ps.setString(i+1, areaids.get(i-1));
			}*/
			rs = ps.executeQuery();
			searchInfo = null;
			while (rs.next()) {
				if(searchInfo == null || !searchInfo.getMac().equals(rs.getString("mac"))){
					searchInfo = new SearchElectricInfo();
					searchInfo.setMac(rs.getString("mac"));
				}
				int electricType = rs.getInt("electricType");
				if (electricType != 0){
					switch(electricType){
					case 6:
					case 61:
					case 62:
					case 63:
						searchInfo.setData6(true);
						break;
					case 7:
					case 71:
					case 72:
					case 73:
						searchInfo.setData7(true);
						break;
					case 8:
					case 81:
					case 82:
					case 83:
						searchInfo.setData8(true);
						break;
					case 9:
					case 91:
					case 92:
					case 93:
						searchInfo.setData9(true);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return searchInfo;
	}

	@Override
	public SearchElectricInfo getDeviceDetail(String smokeMac) {
		StringBuffer sb = new StringBuffer();
		sb.append("select s.time,s.address,s.mac,s.named,s.netState,s.placeTypeId,s.placeAddress,s.repeater,s.ifAlarm,area.area,s.heartime,s.rssivalue,p.placeName");
		sb.append(" from smoke s,areaidarea area,placetypeidplacename p "); 
		sb.append(" where s.deviceType in (5,52,53,59,75,76) and s.areaId=area.areaId and s.placeTypeId=p.placeTypeId");
		sb.append(" and s.mac='"+smokeMac + "'");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		SearchElectricInfo searchInfo= new SearchElectricInfo();
		try {	
			rs = ppst.executeQuery();
			while (rs.next()) {
				searchInfo.setAddSmokeTime(rs.getString(1));
				searchInfo.setAddress(rs.getString(2));
				searchInfo.setAreaName(rs.getString(10));
				searchInfo.setMac(rs.getString(3));
				searchInfo.setName(rs.getString(4));
				String netState = rs.getString(5);
				searchInfo.setNetState(Integer.parseInt(netState)==0 ?"掉线":"在线");
				searchInfo.setPlaceType(rs.getString("placeName"));
				searchInfo.setDeviceType(5);
				searchInfo.setPlaceeAddress(rs.getString(7));
				searchInfo.setRepeater(rs.getString(8));
//				String ifDealAlarm = rs.getString(9);
				searchInfo.setHeartime(rs.getString("heartime"));
				int ifAlarm = rs.getInt("ifAlarm");
				searchInfo.setIfAlarm(ifAlarm);
				if(ifAlarm == 0){ //报警状态
					searchInfo.setAlarmName("报警");
				}
				searchInfo.setRssivalue(rs.getString("rssivalue"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return searchInfo;
	}
}
