package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.FaultInfoDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PrinterDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.FaultDataEntity;
import com.cloudfire.entity.FaultInfoEntity;
import com.cloudfire.entity.Fault_CHZ_01;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.PushAlarmToPCEntity;
import com.cloudfire.entity.query.FaultInfoEntityQuery;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class FaultInfoDaoImpl implements FaultInfoDao {
	
	private GetPushUserIdDao mGetPushUserIdDao;
	private PrinterDao mPrinterDao;
	@Override
	public List<FaultInfoEntity> getFaultInfoAll() {
		String sqlstr = "SELECT DISTINCT id,repeaterMac,faultCode,faultDevDesc ,faultType," +
						"faultInfo,faultTime,dealUser FROM faultinfo WHERE 1 = 1 " +
						"AND SUBSTRING(faultCode,8,1)<>0 ORDER BY faultTime DESC";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		List<FaultInfoEntity> faultList = null;
		ResultSet rs = null;
		try { 
			rs = ps.executeQuery();
			while(rs.next()){
				if(faultList == null){
					faultList = new ArrayList<FaultInfoEntity>();
				}
				FaultInfoEntity fe = new FaultInfoEntity();
				fe.setId(rs.getInt(1));
				fe.setRepeaterMac(rs.getString(2));
				fe.setFaultCode(rs.getString(3));
				String name = rs.getString(4);
				if(name.length()>6){
					String devA = name.substring(0,7);
					String devB = name.substring(7);
					fe.setFaultDevDesc(devA);
					fe.setFaultRoominfo(devB);
				}else{
					fe.setFaultDevDesc(name);
				}
				
				fe.setFaultType(rs.getString(5));
				fe.setFaultInfo(rs.getString(6));
				fe.setFaultTime(rs.getString(7));
				fe.setDealUser(rs.getString(8));
				faultList.add(fe);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return faultList;
	}

	@Override
	public int selectFaultCount(FaultInfoEntityQuery query,List<String> areaIds) {
		String sb = "";
		sb += "SELECT  s.mac,s.named ,s.time,s.areaid,ar.area,s.address,s.principal1,s.netState,s.rssivalue FROM smoke s,areaidarea ar WHERE 1 = 1 AND s.areaid = ar.areaId AND s.deviceType in(9,119,126) ";
		
		int len = 0;
		if(areaIds!=null){
			len = areaIds.size();
		}
		
		if(Utils.isNullStr(query.getRepeaterMac())){
			sb +=" AND s.mac like '%" +query.getRepeaterMac()+"%'";
		}
		if(Utils.isNullStr(query.getJ_xl_1())){
			sb +=" AND s.time >= '" +query.getJ_xl_1()+"'";
		}
		if(Utils.isNullStr(query.getJ_xl_2())){
			sb +=" AND s.time <= '" +query.getJ_xl_2()+"'";
		}
		if(Utils.isNullStr(query.getStatus())) {
			sb += "and s.netState = '" + query.getStatus() +"'";
		}
		//区域的查询
		if(len==1){
			sb += " and s.areaId in ('"+areaIds.get(0)+"') order by time desc " ;
		}if(len>1){
			for(int i=0;i<len;i++){
				if(i==0){
					sb +=" and s.areaId in ('"+areaIds.get(i)+"', ";
				}else if(i==(len-1)){
					sb +=" "+areaIds.get(i)+") order by s.time " ;
				}else{
					sb +=" "+areaIds.get(i)+",";
				}
			}
		}
		
		
		String sql = " select count(*) as totalcount from ( "+sb+" ) aa ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int totalcount =0;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				totalcount = rs.getInt("totalcount");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}
	
	@Override
	public int selectFaultCountBySmoke(FaultInfoEntityQuery query,List<String> areaIds) {
		String sb = "count(*) as tolSum FROM (SELECT DISTINCT s.repeater,s.named ,s.time,s.areaid,ar.area,s.address,s.principal1,s.netState,s.rssivalue FROM smoke s,areaidarea ar WHERE 1 = 1 AND s.areaid = ar.areaId AND s.deviceType in(9,119,126) ";
		int totalcount = 0;
		int len = 0;
		if(areaIds!=null){
			len = areaIds.size();
		}
		String limit = "  ) AS a ";
		if(Utils.isNullStr(query.getRepeaterMac())){
			sb +=" AND s.repeater like '%" +query.getRepeaterMac()+"%'";
		}
		if(Utils.isNullStr(query.getJ_xl_1())){
			sb +=" AND s.time >= '" +query.getJ_xl_1()+"'";
		}
		if(Utils.isNullStr(query.getJ_xl_2())){
			sb +=" AND s.time <= '" +query.getJ_xl_2()+"'";
		}	
		//区域的查询
		if(len==0){
			sb+=limit;
		}
		if(len==1){
			sb += " and s.areaId in ('"+areaIds.get(0)+"') order by time desc " + limit;
		}if(len>1){
			for(int i=0;i<len;i++){
				if(i==0){
					sb +=" and s.areaId in ('"+areaIds.get(i)+"', ";
				}else if(i==(len-1)){
					sb +=" "+areaIds.get(i)+") order by s.time " +limit;
				}else{
					sb +=" "+areaIds.get(i)+",";
				}
			}
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				totalcount = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}

	@Override
	public List<FaultInfoEntity> selectFaultInfo(FaultInfoEntityQuery query,List<String> areaIds) {
//		String sb = "SELECT a.* FROM (SELECT DISTINCT f.id,f.repeaterMac,f.faultCode,f.faultDevDesc ,f.faultType,f.faultInfo,f.faultTime,f.dealUser,s.areaid,ar.area,s.address,s.principal1,s.netState FROM faultinfo f,smoke s,areaidarea ar WHERE 1 = 1 AND s.areaid = ar.areaId AND f.repeaterMac=s.mac ";
		
		//edit by yfs @2017.11.10
		String sb = "SELECT a.* FROM (SELECT DISTINCT f.id,f.repeaterMac,f.faultCode,s.named ,f.faultType,f.faultInfo,s.time,f.dealUser,s.areaid,ar.area,s.address,s.principal1,s.netState,s.rssivalue FROM faultinfo f,smoke s,areaidarea ar WHERE 1 = 1 AND s.areaid = ar.areaId AND f.repeaterMac=s.mac ";
		
		int len = 0;
		if(areaIds!=null){
			len = areaIds.size();
		}
		String limit = "  ) AS a GROUP BY repeaterMac limit "+query.getStartRow() + ","+query.getPageSize();
		if(Utils.isNullStr(query.getRepeaterMac())){
			sb +=" AND f.repeaterMac like '%" +query.getRepeaterMac()+"%'";
		}
		if(Utils.isNullStr(query.getJ_xl_1())){
			sb +=" AND f.faultTime >= '" +query.getJ_xl_1()+"'";
		}
		if(Utils.isNullStr(query.getJ_xl_2())){
			sb +=" AND f.faultTime <= '" +query.getJ_xl_2()+"'";
		}	
		//区域的查询
		if(len==0){
			sb+=limit;
		}
		if(len==1){
			sb += " and s.areaId in ('"+areaIds.get(0)+"') order by f.faultTime desc " + limit;
		}if(len>1){
			for(int i=0;i<len;i++){
				if(i==0){
					sb +=" and s.areaId in ('"+areaIds.get(i)+"', ";
				}else if(i==(len-1)){
					sb +=" "+areaIds.get(i)+") order by f.faultTime " +limit;
				}else{
					sb +=" "+areaIds.get(i)+",";
				}
			}
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb);
		List<FaultInfoEntity> faultList = null;
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while(rs.next()){
				if(faultList == null){
					faultList = new ArrayList<FaultInfoEntity>();
				}
				FaultInfoEntity fe = new FaultInfoEntity();
				fe.setRow(++row);
				fe.setId(rs.getInt(1));
				fe.setRepeaterMac(rs.getString(2));
				fe.setFaultCode(rs.getString(3));
				String name = rs.getString(4);
				if(name.length()>6){
					String devA = name.substring(0,7);
					String devB = name.substring(7);
					fe.setFaultDevDesc(devA);
					fe.setFaultRoominfo(devB);
				}else{
					fe.setFaultDevDesc(name);
				}
				fe.setFaultType(rs.getString(5));
				fe.setFaultInfo(rs.getString(6));
				fe.setFaultTime(rs.getString(7));
				fe.setDealUser(rs.getString(8));
				fe.setAreaName(rs.getString("area"));
				fe.setAddress(rs.getString("address"));
				fe.setPrincipal1(rs.getString("principal1"));
				fe.setRssivalue(rs.getString("rssivalue"));
				if(rs.getInt("netState") == 1){
					fe.setNetState("在线");
				}else{
					fe.setNetState("离线");
				}
				faultList.add(fe);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return faultList;
	}
	
	@Override
	public List<FaultInfoEntity> selectFaultInfoBySmoke(FaultInfoEntityQuery query,List<String> areaIds) {
		String sb = "select  first.*,second.faultType from ";
//		String sb = "";
		sb += "(SELECT a.* FROM (SELECT  s.mac,s.named ,s.time,s.areaid,ar.area,s.address,s.principal1,s.netState,s.rssivalue FROM smoke s,areaidarea ar WHERE 1 = 1 AND s.areaid = ar.areaId AND s.deviceType in(9,119,126) ";
		
		int len = 0;
		if(areaIds!=null){
			len = areaIds.size();
		}
		
		String limit = "  ) AS a  limit "+query.getStartRow() + ","+query.getPageSize();
		if(Utils.isNullStr(query.getRepeaterMac())){
			sb +=" AND s.mac like '%" +query.getRepeaterMac()+"%'";
		}
		if(Utils.isNullStr(query.getJ_xl_1())){
			sb +=" AND s.time >= '" +query.getJ_xl_1()+"'";
		}
		if(Utils.isNullStr(query.getJ_xl_2())){
			sb +=" AND s.time <= '" +query.getJ_xl_2()+"'";
		}	
		if(Utils.isNullStr(query.getStatus())) {
			sb += "and s.netState = '" + query.getStatus() +"'";
		}
		//区域的查询
		if(len==1){
			sb += " and s.areaId in ('"+areaIds.get(0)+"') order by time desc " ;
		}if(len>1){
			for(int i=0;i<len;i++){
				if(i==0){
					sb +=" and s.areaId in ('"+areaIds.get(i)+"', ";
				}else if(i==(len-1)){
					sb +=" "+areaIds.get(i)+") order by s.time " ;
				}else{
					sb +=" "+areaIds.get(i)+",";
				}
			}
		}
		
		sb += limit;
		
		sb += ") first left join (select t.* from (select repeaterMac,faultType from faultinfo  order by faultTime DESC) t group by repeaterMac) second on first.mac=second.repeaterMac";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb);
		List<FaultInfoEntity> faultList = null;
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while(rs.next()){
				if(faultList == null){
					faultList = new ArrayList<FaultInfoEntity>();
				}
				FaultInfoEntity fe = new FaultInfoEntity();
				fe.setRow(++row);
				fe.setRepeaterMac(rs.getString("mac"));
				fe.setFaultDevDesc(rs.getString("named"));
//				String name = rs.getString("named");
//				if(name.length()>6){
//					String devA = name.substring(0,7);
//					String devB = name.substring(7);
//					fe.setFaultDevDesc(devA);
//					fe.setFaultRoominfo(devB);
//				}else{
//					fe.setFaultDevDesc(name);
//				}
				fe.setFaultTime(rs.getString("time"));
				fe.setAreaName(rs.getString("area"));
				fe.setAddress(rs.getString("address"));
				fe.setPrincipal1(rs.getString("principal1"));
				String faultType = rs.getString("faultType");
				if(faultType == null || "正常监视状态".equals(faultType)){
					fe.setIfAlarm(1);
				} else {
					fe.setIfAlarm(0);
					fe.setAlarmName("报警");
				}
				if(rs.getInt("netState") == 1){
					fe.setNetState("在线");
				}else{
					fe.setNetState("掉线");
				}
				faultList.add(fe);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return faultList;
	}
	
	@Override
	public int selectFaultCount2(FaultInfoEntityQuery query,List<String> areaIds,String repeaterMac,String faultCode) {
		String sb = "SELECT a.* FROM (SELECT DISTINCT id,repeaterMac,faultCode,faultDevDesc ,faultType,faultInfo,faultTime,dealUser FROM faultinfo,smoke WHERE 1 = 1 AND faultInfo.repeaterMac=smoke.mac ";
		if(Utils.isNullStr(repeaterMac)){
			sb = sb + " AND repeaterMac = '"+repeaterMac+"'";
		}
		
		if(Utils.isNullStr(faultCode)){
			/*if(Utils.isNumeric(faultCode)){
				sb = sb + " AND SUBSTRING(faultCode,1,3) = "+faultCode.substring(0,3);
			}else{*/
				sb = sb + " and faultCode = '" + faultCode+"'";
			/*}*/
			
		}
		
		String limit = "  ) AS a GROUP BY repeaterMac,faultCode ";
		int len = 0;
		if(areaIds!=null){
			len = areaIds.size();
		}
		if(Utils.isNullStr(query.getRepeaterMac())){
			sb +=" AND repeaterMac like '%" +query.getRepeaterMac()+"%'";
		}
		if(Utils.isNullStr(query.getJ_xl_1())){
			sb +=" AND faultTime >= '" +query.getJ_xl_1()+"'";
		}
		if(Utils.isNullStr(query.getJ_xl_2())){
			sb +=" AND faultTime <= '" +query.getJ_xl_2()+"'";
		}	
		//区域的查询
		if(len==0){
//			return (Integer) null;
		}
		if(len==1){
			sb += " and smoke.areaId in ('"+areaIds.get(0)+"') order by faultTime desc " +limit ;
		}if(len>1){
			for(int i=0;i<len;i++){
				if(i==0){
					sb +=" and smoke.areaId in ('"+areaIds.get(i)+"', ";
				}else if(i==(len-1)){
					sb +=" "+areaIds.get(i)+") order by faultTime desc " +limit;
				}else{
					sb +=" "+areaIds.get(i)+",";
				}
			}
		}
		
		String sql = " select count(*) as totalcount from ( "+sb+" ) aa ";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		System.out.println("sql:"+sql);
		List<FaultInfoEntity> faultList = null;
		ResultSet rs = null;
		int totalcount =0;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				totalcount = rs.getInt("totalcount");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}

	@Override
	public List<FaultInfoEntity> selectFaultInfo2(FaultInfoEntityQuery query,List<String> areaIds,String repeaterMac,String faultCode) {
		String sb = "SELECT a.* FROM (SELECT DISTINCT id,repeaterMac,faultCode,faultDevDesc ,faultType,faultInfo,faultTime,dealUser FROM faultinfo,smoke WHERE 1 = 1 AND faultInfo.repeaterMac=smoke.mac ";
		
		if(Utils.isNullStr(faultCode)){
			sb = sb + " and faultCode = '"+ faultCode+"'";
		}
		
		int len = 0;
		if(areaIds!=null){
			len = areaIds.size();
		}
		String limit = "  ) AS a GROUP BY repeaterMac,faultCode ";
//		String limit = "  ) AS a GROUP BY repeaterMac,faultCode limit "+query.getStartRow() + ","+query.getPageSize();
		if(Utils.isNullStr(query.getRepeaterMac())){
			sb +=" AND repeaterMac like '%" +query.getRepeaterMac()+"%'";
		}
		if(Utils.isNullStr(query.getJ_xl_1())){
			sb +=" AND faultTime >= '" +query.getJ_xl_1()+"'";
		}
		if(Utils.isNullStr(query.getJ_xl_2())){
			sb +=" AND faultTime <= '" +query.getJ_xl_2()+"'";
		}	
		//区域的查询
		if(len==0){
//			return null;
		}
		if(len==1){
			sb += " and smoke.areaId in ('"+areaIds.get(0)+"') order by faultTime desc " + limit;
		}if(len>1){
			for(int i=0;i<len;i++){
				if(i==0){
					sb +=" and smoke.areaId in ('"+areaIds.get(i)+"', ";
				}else if(i==(len-1)){
					sb +=" "+areaIds.get(i)+") order by faultTime " +limit;
				}else{
					sb +=" "+areaIds.get(i)+",";
				}
			}
		}
		
		sb = "SELECT * from ("+sb+") as e ORDER BY faultTime desc limit "+query.getStartRow() + ","+query.getPageSize();
		Connection conn = DBConnectionManager.getConnection();
		
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb);
		List<FaultInfoEntity> faultList = null;
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while(rs.next()){
				if(faultList == null){
					faultList = new ArrayList<FaultInfoEntity>();
				}
				FaultInfoEntity fe = new FaultInfoEntity();
				fe.setRow(++row);
				fe.setId(rs.getInt(1));
				fe.setRepeaterMac(rs.getString(2));
				fe.setFaultCode(rs.getString(3));
				String name = rs.getString(4);
				if(name.length()>6){
					String devA = name.substring(0,7);
					String devB = name.substring(7);
					fe.setFaultDevDesc(devA);
					fe.setFaultRoominfo(devB);
				}else{
					fe.setFaultDevDesc(name);
				}
				fe.setFaultType(rs.getString(5));
				fe.setFaultInfo(rs.getString(6));
				fe.setFaultTime(rs.getString(7));
				fe.setDealUser(rs.getString(8));
				int alarmNume = selectFaultCount3(query,rs.getString("repeaterMac"),rs.getString("faultCode"));
				fe.setAlarmNume(alarmNume);
				faultList.add(fe);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return faultList;
	}
	
	@Override
	public int selectFaultCount3(FaultInfoEntityQuery query,String repeaterMac,String faultCode) {
		String sb = "SELECT DISTINCT id,repeaterMac,faultCode,faultDevDesc ,faultType,faultInfo,faultTime,dealUser FROM faultinfo,smoke WHERE 1 = 1 AND faultInfo.repeaterMac=smoke.mac ";
		
		if(Utils.isNullStr(faultCode)){
			
			sb = sb + " and faultCode = '" + faultCode+"'"; 
		}
		
		if(Utils.isNullStr(query.getRepeaterMac())){
			sb +=" AND repeaterMac like '%" +query.getRepeaterMac()+"%'";
		}
		if(Utils.isNullStr(query.getJ_xl_1())){
			sb +=" AND faultTime >= '" +query.getJ_xl_1()+"'";
		}
		if(Utils.isNullStr(query.getJ_xl_2())){
			sb +=" AND faultTime <= '" +query.getJ_xl_2()+"'";
		}	
		
		String sql = " select count(*) as totalcount from ( "+sb+" ) aa ";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int totalcount =0;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				totalcount = rs.getInt("totalcount");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return totalcount;
	}

	@Override
	public List<FaultInfoEntity> selectFaultInfo3(FaultInfoEntityQuery query,String repeaterMac,String faultCode) {
		String sb = "SELECT DISTINCT id,repeaterMac,faultCode,faultDevDesc ,faultType,faultInfo,faultTime,dealUser,dealTime,dealText FROM faultinfo,smoke WHERE 1 = 1 AND faultInfo.repeaterMac=smoke.mac ";
		
		if(Utils.isNullStr(faultCode)){
			sb = sb + " and faultCode = '" + faultCode+"'"; 
		}
		
		String limit = " order by faultTime desc limit "+query.getStartRow() + ","+query.getPageSize();
		if(Utils.isNullStr(query.getRepeaterMac())){
			sb +=" AND repeaterMac like '%" +query.getRepeaterMac()+"%'";
		}
		if(Utils.isNullStr(query.getJ_xl_1())){
			sb +=" AND faultTime >= '" +query.getJ_xl_1()+"'";
		}
		if(Utils.isNullStr(query.getJ_xl_2())){
			sb +=" AND faultTime <= '" +query.getJ_xl_2()+"'";
		}	
		
		sb = sb + limit;
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb);
		List<FaultInfoEntity> faultList = null;
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while(rs.next()){
				if(faultList == null){
					faultList = new ArrayList<FaultInfoEntity>();
				}
				FaultInfoEntity fe = new FaultInfoEntity();
				fe.setRow(++row);
				fe.setId(rs.getInt(1));
				fe.setRepeaterMac(rs.getString(2));
				fe.setFaultCode(rs.getString(3));
				String name = rs.getString(4);
				if(name.length()>6){
					String devA = name.substring(0,7);
					String devB = name.substring(7);
					fe.setFaultDevDesc(devA);
					fe.setFaultRoominfo(devB);
				}else{
					fe.setFaultDevDesc(name);
				}
				fe.setFaultType(rs.getString(5));
				fe.setFaultInfo(rs.getString(6));
				fe.setFaultTime(rs.getString(7));
				fe.setDealUser(rs.getString(8));
				fe.setDealText(rs.getString("dealText"));
				fe.setDealTime(rs.getString("dealTime"));
				faultList.add(fe);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return faultList;
	}
	
	public int insertFaultByHW(PrinterEntity mPrinter){
		int openState = mPrinter.getOpenState();
		Map<Integer,Integer> hMap = mPrinter.getHwMap();
		for(Integer key:hMap.keySet()){
			int values = hMap.get(key);
//			System.out.println("keykeykey="+key+"valuesvalues="+values);
			String repeater = mPrinter.getRepeater();
			String faultTime = GetTime.ConvertTimeByLong();
			String faultCodes = mPrinter.getFaultCode();
			int keys = key + 1;
			String faultDescs = keys+"号设备";
			String faultTypes = "";
			if(values == 1){
				faultTypes = "火警";
			}else if(values == 2){
				faultTypes = "故障";
			}else if(values == 3){
				faultTypes = "动作";
			}else if(values == 4){
				faultTypes = "";
			}else if(values == 5){
				faultTypes = "启动";
			}else if(values == 6){
				faultTypes = "停动";
			}else if(values == 7){
				faultTypes = "隔离";
			}else if(values == 8){
				faultTypes = "释放";
			}else if(values == 9){
				faultTypes = "主电备电恢复";
			}else if(values == 0){
				faultTypes = "正常";
			}
			if(openState == 1){
				if(values != 0 ){
					saveFault(repeater,faultCodes,keys+"",faultTypes+"",faultTime);
					saveMasterequipment(repeater,faultCodes,keys+"",faultTypes+"",faultTime);
				}else{
					int result = updateMasterequipment(repeater,faultCodes,keys+"",faultTime);
					if(result == 1){
						saveFault(repeater,faultCodes,keys+"",faultTypes,faultTime);
					}
				}
			}else if(openState == 0){
				saveFault(repeater,faultCodes,keys+"",faultTypes+"",faultTime);
				saveMasterequipment(repeater,faultCodes,keys+"",faultTypes+"",faultTime);
			}
			
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			List<String> pcUsers5 = mGetPushUserIdDao.getUserByRepeaterMac(repeater);
			PrinterEntity pe = new PrinterEntity();
			pe.setFaultCode(faultCodes+"分区");
			pe.setFaultDevDesc(faultDescs);
			pe.setFaultTime(faultTime);
			pe.setFaultType(faultTypes);
			pe.setRepeater(repeater);
			if(pcUsers5!=null&&pcUsers5.size()>0&&values!=0){
				PushAlarmToPCEntity mPushAlarmToPCEntity = new PushAlarmToPCEntity();
				mPushAlarmToPCEntity.setMasterFault(pe);
				mPushAlarmToPCEntity.setDeviceType(224);//224报警代表海湾主机报警信息
				new MyThread(mPushAlarmToPCEntity,pcUsers5,null,2).start();
				new WebThread(pcUsers5,repeater,2).start();
			}
		}
		return 0;
	}
	
	public void updateFault(String repeater,String faultCode,String faultDesc,String faultTime){
		String sql = "UPDATE faultinfo SET faultType = '正常',faultTime = ? WHERE repeaterMac=? AND faultCode = ? AND faultDevDesc=?";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, faultTime);
			ps.setString(2, repeater);
			ps.setString(3, faultCode);
			ps.setString(4, faultDesc);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public void saveFault(String repeater,String faultCode,String faultDesc,String faultType,String faultTime){
		String sql = "insert into faultInfo(repeaterMac,faultCode,faultDevDesc,faultType,faultTime,alarmType) values(?,?,?,?,?,?)";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, repeater);
			ps.setString(2, faultCode);
			ps.setString(3, faultDesc);
			ps.setString(4, faultType);
			ps.setString(5, faultTime);
			ps.setString(6, "224");
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public int updateMasterequipment(String repeater,String faultCode,String faultDesc,String faultTime){
		String sql="UPDATE masterequipment SET equipmentFaultType = '正常',updateTime = ? WHERE repeater=? AND mac = ? AND equipmentDesc=?";
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, faultTime);
			ps.setString(2, repeater);
			ps.setString(3, faultCode);
			ps.setString(4, faultDesc);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public void saveMasterequipment(String repeater,String faultCode,String faultDesc,String faultType,String faultTime){
		boolean result = ifExit(repeater,faultCode,faultDesc,faultType);
		String sql=null;
		if(result){
			sql="update masterequipment set equipmentFaultType=?,updateTime=?,hostType=? " +
					" where equipmentDesc=? and mac=? and repeater=?";
		}else{
			sql = "insert masterequipment (equipmentFaultType,updateTime,hostType,equipmentDesc,mac,repeater) values (?,?,?,?,?,?)";
		}
		
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, faultType);
			ps.setString(2, faultTime);
			ps.setString(4, faultDesc);
			ps.setString(5, faultCode);
			ps.setString(6, repeater);
			ps.setString(3, 224+"");
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public boolean ifExit(String repeater,String faultCode,String faultDesc,String faultType) {
		// TODO Auto-generated method stub
		String sql = "select * from masterequipment where mac=? and repeater=? and equipmentDesc =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		boolean result=false;
		try {
			ps.setString(1, faultCode);
			ps.setString(2, repeater);
			ps.setString(3, faultDesc);
			rs = ps.executeQuery();
			if(rs.next()){
				result=true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public void saveFaultInfoSystem(FaultDataEntity faultData) {
		int[] result ;
		Map<Integer,String> stateTab = faultData.getStateTab();
		String sql = "insert into faultinfosystem (faultCode,faultCmd,systemTab,systemAddress,unitAddress,faultState,faultTab,unitMemo,operator) VALUES(?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			conn.setAutoCommit(false);
			ps.setString(1, faultData.getRepeaterMac());
			ps.setString(2, faultData.getTabbingCmd());
			ps.setString(3, faultData.getSystemTab());
			ps.setString(4, faultData.getSystemAddress());
			ps.setString(5, faultData.getUnitAddress());
			for(Integer key:stateTab.keySet()){
				ps.setInt(6, key);
				ps.setString(7, stateTab.get(key));
				ps.addBatch();
			}
			ps.setString(8, faultData.getUnitMemos());
			ps.setString(9, faultData.getOperator());
			result = ps.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public int insertSJFaultInfo(FaultDataEntity faultData){
		int result = 0;
		List<String> alarmName = faultData.getStateTabList();
		for (String alarmInfo : alarmName){
			String fdate = GetTime.ConvertTimeByLong();
			PrinterEntity mPrinterEntity = new PrinterEntity();
			mPrinterEntity.setFaultCode(faultData.getUnitAddress());
			mPrinterEntity.setRepeater(faultData.getRepeaterMac());
			mPrinterEntity.setFaultDevDesc(faultData.getSystemTab());
			mPrinterEntity.setFaultTime(fdate);
			mPrinterEntity.setFaultInfo(faultData.getUnitType());
			mPrinterEntity.setFaultType(alarmInfo);
			PushAlarmToPCEntity mPushAlarmToPCEntity = new PushAlarmToPCEntity();
			mPushAlarmToPCEntity.setMasterFault(mPrinterEntity);
			mPushAlarmToPCEntity.setDeviceType(9);
			PrinterDao pd = new PrinterDaoImpl();
			pd.getPrinterAlarm(mPrinterEntity);
			new WebThread(null,mPrinterEntity.getRepeater(),2).start();
		}
		return result;
	}
	
	public int insertFaultInfo(FaultDataEntity faultData){
		int result = 0;
		String repeaterMac = faultData.getRepeaterMac();
		List<String> alarmName = faultData.getStateTabList();
		for (String alarmInfo : alarmName){
			String fdate = GetTime.ConvertTimeByLong();
			PrinterEntity mPrinterEntity = new PrinterEntity();
			String code = faultData.getUnitAddress();
			mPrinterEntity.setFaultCode(code);//
			mPrinterEntity.setRepeater(repeaterMac);
			String devdesc = "";
			if(code.length()>3){
				devdesc = code.substring(code.length()-3);
				if(devdesc.equals("000")){
					devdesc = "主机";
				}else{
					devdesc = devdesc+"号";
				}
			}
			if(StringUtils.isBlank(devdesc)){
				mPrinterEntity.setFaultDevDesc(faultData.getSystemTab());
			}else{
				mPrinterEntity.setFaultDevDesc(devdesc);
			}
			mPrinterEntity.setFaultTime(fdate);
			mPrinterEntity.setFaultInfo(faultData.getUnitType());
			mPrinterEntity.setFaultType(alarmInfo);
			PushAlarmToPCEntity mPushAlarmToPCEntity = new PushAlarmToPCEntity();
			switch(repeaterMac){
			case "000000000020":
				Fault_CHZ_01 fentity = null;
				if(code.length()>3){
					new Fault_CHZ_01();
					fentity = getFaultInfo_s(code.substring(2));
					if(fentity!=null){
						mPrinterEntity.setFaultInfo(fentity.getDevType());
						mPrinterEntity.setFaultDevDesc(fentity.getNoteMsg());
					}
				}
				break;
			}
			
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			List<String> pcUsers = mGetPushUserIdDao.getUserByRepeaterMac(faultData.getRepeaterMac());
			mPrinterDao = new PrinterDaoImpl();
			mPrinterDao.getPrinterInfo(mPrinterEntity);
			mPrinterDao.getPrinterAlarm(mPrinterEntity);
			if(pcUsers!=null&&pcUsers.size()>0){
				mPushAlarmToPCEntity.setMasterFault(mPrinterEntity);
				mPushAlarmToPCEntity.setDeviceType(221);
				new MyThread(mPushAlarmToPCEntity,pcUsers,null,221).start();
				new WebThread(pcUsers,mPrinterEntity.getRepeater(),2).start();
//				if (txtUserList != null &&txtUserList.size()>0) {
//					new TxtThread(txtUserList,waterMac).start();        //短信通知的线程
//				}
			}
			if(alarmInfo.equals("火警")){
				
			}
		}
		return result;
	}
	
	
	public int insertFaultInfoQP(FaultDataEntity faultData){
		int result = 0;
		List<String> alarmName = faultData.getStateTabList();
		for (String alarmInfo : alarmName){
			String fdate = GetTime.ConvertTimeByLong();
			PrinterEntity mPrinterEntity = new PrinterEntity();
			String code = faultData.getUnitAddress();
			mPrinterEntity.setFaultDevDesc(code);//
			mPrinterEntity.setRepeater(faultData.getRepeaterMac());
			String devdesc = "";
			if(code.length()>3){
				devdesc = code.substring(code.length()-3);
				if(devdesc.equals("000")){
					devdesc = "主机";
				}else{
					devdesc = devdesc+"";
				}
			}
			
			if(StringUtils.isBlank(devdesc)){
				mPrinterEntity.setFaultCode(faultData.getSystemTab());
			}else{
				mPrinterEntity.setFaultCode(devdesc);
			}
			mPrinterEntity.setFaultTime(fdate);
			mPrinterEntity.setFaultInfo(faultData.getUnitType());
			mPrinterEntity.setFaultType(alarmInfo);
			PushAlarmToPCEntity mPushAlarmToPCEntity = new PushAlarmToPCEntity();
			
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			List<String> pcUsers = mGetPushUserIdDao.getUserByRepeaterMac(faultData.getRepeaterMac());
			mPrinterDao = new PrinterDaoImpl();
//			mPrinterDao.getPrinterInfo(mPrinterEntity);
			mPrinterDao.getPrinterAlarm(mPrinterEntity);
			if(pcUsers!=null&&pcUsers.size()>0){
				mPushAlarmToPCEntity.setMasterFault(mPrinterEntity);
				mPushAlarmToPCEntity.setDeviceType(221);
				new MyThread(mPushAlarmToPCEntity,pcUsers,null,221).start();
				new WebThread(pcUsers,mPrinterEntity.getRepeater(),2).start();
//				if (txtUserList != null &&txtUserList.size()>0) {
//					new TxtThread(txtUserList,waterMac).start();        //短信通知的线程
//				}
			}
		}
		return result;
	}

	@Override
	public int insertFaultInfoSystem(FaultDataEntity faultData) {
		int[] result = new int[]{};
		List<String> stateTab = faultData.getStateTabList();
		String faultTime = GetTime.ConvertTimeByLong();
		if(stateTab==null||stateTab.size()<1){
			return 0;
		}
		String sql = "insert into faultinfosystem (faultCode,faultCmd,systemTab,systemAddress,unitAddress,faultState,faultTab,unitMemo,operator,faultTime) VALUES(?,?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			conn.setAutoCommit(false);
			ps.setString(1, faultData.getRepeaterMac());
			ps.setString(2, faultData.getTabbingCmd());
			ps.setString(3, faultData.getSystemTab());
			ps.setString(4, faultData.getSystemAddress());
			ps.setString(5, faultData.getUnitAddress());
			ps.setInt(6, 0);
			if(stateTab!=null&&stateTab.size()>0){
				for(String tabList:stateTab){
					ps.setString(7, tabList);
				}
			}
			ps.setString(8, faultData.getUnitMemos());
			ps.setString(9, faultData.getOperator());
			ps.setString(10, faultTime);
			result = ps.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result.length;
	}
	
	public Fault_CHZ_01 getFaultInfo_s(String twoCode){
		Fault_CHZ_01 faultEntity = new Fault_CHZ_01();
		String sql = "SELECT twoCode,devType,charProp,noteMsg from fault_cz_001 where twoCode = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, twoCode);
			rs = ps.executeQuery();
			while(rs.next()){
				faultEntity.setTwoCode(rs.getString("twoCode"));
				faultEntity.setNoteMsg(rs.getString("noteMsg"));
				faultEntity.setDevType(rs.getString("devType"));
				faultEntity.setCharProp(rs.getString("charProp"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return faultEntity;
	}

}


















