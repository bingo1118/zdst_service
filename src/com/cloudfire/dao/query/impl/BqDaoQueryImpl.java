/**
 * 下午4:35:25
 */
package com.cloudfire.dao.query.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.dao.query.BqDaoQuery;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.query.ElectricDTUQuery;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

/**
 * @author cheng
 *2017-5-18
 *下午4:35:25
 */
public class BqDaoQueryImpl implements BqDaoQuery{
	
	public int getBqCount(BQMacEntity query,List<String> areaIds){
		int totalcount = 0;
		String sqlstr = "select * from bqmac where 1=1 and projectName = (select projectName from bqmac where ";
		int len = areaIds.size();
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceId())) {
				sqlstr += " deviceId= '"+query.getDeviceId()+"' ) ";
			}
			if (!StringUtils.isBlank(query.getNamed())) {
				sqlstr += " and equipName like '%" + query.getNamed()
						+ "%'";
			}
			if (!StringUtils.isBlank(query.getDevicetype())) {
				sqlstr += " and equipType ='" + query.getDevicetype() + "'";
			}
			if (!StringUtils.isBlank(query.getStatusStr())) {
				sqlstr += " and status ='" + query.getStatusStr() + "'";
			}
			if (!StringUtils.isBlank(query.getBegintime())) {
				sqlstr += " and createTime >='" + query.getBegintime() + "'";
			}
			if (!StringUtils.isBlank(query.getEndtime())) {
				sqlstr += " and createTime <='" + query.getEndtime() + "'";
			}
			if (len == 0) {
				return (Integer) null;
			}if (len ==1) {
				sqlstr += " and areaId in (?) ";
			}else {
				for (int i=0 ;i<len ;i ++) {
					if (i==0) {
						sqlstr += " and areaId in (?,";
					}else if (i == (len-1)) {
						sqlstr += " ?)  ";
					}else {
						sqlstr +=" ?, ";
					}
				}
			}
		}
		
		Connection conn = DBConnectionManager.getConnection();
		String allsql ="select count(*) as totalcount from ("+sqlstr +" ) aa";
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allsql);
		ResultSet rs = null;
			try {
				for(int i=1;i<=len ;i++){
					ppst.setString(i, areaIds.get(i-1));
				}
				rs = ppst.executeQuery();				
				while(rs.next()){
					totalcount = rs.getInt("totalcount");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ppst);
				DBConnectionManager.close(conn);
			}
		return totalcount;
	}
	
	public List<BQMacEntity> getList(BQMacEntity query,List<String> areaIds){
		List<BQMacEntity> list = new ArrayList<>();
		String sqlstr = "select * from bqmac where 1=1 and projectName = (select projectName from bqmac where ";
		int len = areaIds.size();
		String limit = " limit "+query.getStartRow() +","+query.getPageSize();
		if (query != null) {
			if (!StringUtils.isBlank(query.getDeviceId())) {
				sqlstr += " deviceId= '"+query.getDeviceId()+"' ) ";
			}
			if (!StringUtils.isBlank(query.getNamed())) {
				sqlstr += " and equipName like '%" + query.getNamed()
						+ "%'";
			}
			if (!StringUtils.isBlank(query.getDevicetype())) {
				sqlstr += " and equipType ='" + query.getDevicetype() + "'";
			}
			if (!StringUtils.isBlank(query.getStatusStr())) {
				sqlstr += " and status ='" + query.getStatusStr() + "'";
			}
			if (!StringUtils.isBlank(query.getBegintime())) {
				sqlstr += " and createTime >='" + query.getBegintime() + "'";
			}
			if (!StringUtils.isBlank(query.getEndtime())) {
				sqlstr += " and createTime <='" + query.getEndtime() + "'";
			}
			if (len == 0) {
				return null;
			}if (len ==1) {
				sqlstr += " and areaId in (?) "+limit;
			}else {
				for (int i=0 ;i<len ;i ++) {
					if (i==0) {
						sqlstr += " and areaId in (?,";
					}else if (i == (len-1)) {
						sqlstr += " ?)  "+limit;
					}else {
						sqlstr +=" ?, ";
					}
				}
			}
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
	
			try {
				for(int i=1;i<=len ;i++){
					ppst.setString(i, areaIds.get(i-1));
				}
				rs = ppst.executeQuery();
				int row = query.getStartRow();
				while(rs.next()){
					BQMacEntity bqMacEntity = new BQMacEntity();
					bqMacEntity.setRow(++ row);
					bqMacEntity.setNamed(rs.getString(2));
					bqMacEntity.setDevicetype(rs.getString(3));
					bqMacEntity.setDeviceId(rs.getInt(4) + "");
					bqMacEntity.setProjectName(rs.getString(5));
					bqMacEntity.setAddress(rs.getString(6));
					int status = rs.getInt(7);
					switch (status) {
					case 0:
						bqMacEntity.setStatusStr(Constant.Bq0);
						break;
					case 1:
						bqMacEntity.setStatusStr(Constant.Bq1);
						break;
					default:
						break;
					}
					bqMacEntity.setCreateTime(rs.getString(8));
					list.add(bqMacEntity);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ppst);
				DBConnectionManager.close(conn);
			}
		
		return list;
	}
	
	public BQMacEntity getBqMacEntity(String deviceId){
		String sqlstr = "select * from bqmac where 1=1  ";
			
		if (!StringUtils.isBlank(deviceId)) {
					sqlstr += " and deviceId= '"+deviceId+"'";
		}
			
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
	
			try {
				
				rs = ppst.executeQuery();
				BQMacEntity bqMacEntity = new BQMacEntity();
				while(rs.next()){
					
					bqMacEntity.setNamed(rs.getString(2));
					bqMacEntity.setDevicetype(rs.getString(3));
					bqMacEntity.setDeviceId(rs.getInt(4) + "");
					bqMacEntity.setProjectName(rs.getString(5));
					bqMacEntity.setAddress(rs.getString(6));
					int status = rs.getInt(7);
					switch (status) {
					case 0:
						bqMacEntity.setStatusStr(Constant.Bq0);
						break;
					case 1:
						bqMacEntity.setStatusStr(Constant.Bq1);
						break;
					default:
						break;
					}
					bqMacEntity.setCreateTime(rs.getString(8));
					
				}
				return bqMacEntity;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ppst);
				DBConnectionManager.close(conn);
			}
			return null;
	}

	@Override
	public int getElectricCount(ElectricDTUQuery query) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.* FROM (");
		sb.append(" SELECT id,smokeMac,electricValue1,electricValue2,electricValue3,electricValue4,electricTime,repeaterMac,electricType,electricDevType ");
		sb.append(" FROM electricinfo ");
		sb.append(" WHERE smokeMac = repeaterMac ");
		
		if(Utils.isNullStr(query.getSmokeMac())){
			sb.append(" AND smokeMac like '%" + query.getSmokeMac()+"%'");
		}
		
		sb.append(" ORDER BY electricTime DESC ");
		
		
		sb.append(") AS s ");
		String limit = " GROUP BY smokeMac limit "+query.getStartRow() +","+query.getPageSize();
		String sqlstr = sb.toString()+limit;
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				result++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public List<ElectricDTUQuery> getElectricDTU(ElectricDTUQuery query) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.* FROM (");
		sb.append(" SELECT id,smokeMac,electricValue1,electricValue2,electricValue3,electricValue4,electricTime,repeaterMac,electricType,electricDevType ");
		sb.append(" FROM electricinfo ");
		sb.append(" WHERE smokeMac = repeaterMac ");
		if(Utils.isNullStr(query.getSmokeMac())){
			sb.append(" AND smokeMac like '%" + query.getSmokeMac()+"%'");
		}
		
		sb.append(" ORDER BY electricTime DESC ");
		
		
		sb.append(") AS s ");
		String limit = " GROUP BY smokeMac,electricType limit "+query.getStartRow() +","+query.getPageSize();
		String sqlstr = sb.toString()+limit;
		
		List<ElectricDTUQuery> list = new ArrayList<ElectricDTUQuery>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		ElectricDTUQuery dtu = null;
		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			String mac = "";
			String mac2 = "";
			while(rs.next()){
				mac = rs.getString("smokeMac");
				if(!Utils.isNullStr(mac2)){
					dtu = new ElectricDTUQuery();
					dtu.setRow(++row);
					dtu.setSmokeMac(rs.getString("smokeMac"));
					//1表示bq200,2表示bq100 1.0版本，3表示bq100 2.0版本
					int devType = rs.getInt("electricDevType");
					if(devType == 1){
						dtu.setElectricDevType("bq200");
					}else if(devType ==2){
						dtu.setElectricDevType("bq100_1.1");
					}else if(devType == 3){
						dtu.setElectricDevType("bq100_2.0");
					}
					mac2 = dtu.getSmokeMac();
					dtu.setElectricTime(rs.getString("electricTime"));
					String time = rs.getString("electricTime");
					Date date=null;
					SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						date=formatter.parse(time);
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					if(System.currentTimeMillis()-date.getTime()<3600000){
						dtu.setNetstate("在线");
					}else{
						dtu.setNetstate("离线");
					}
				}else if(!dtu.getSmokeMac().equals(mac)){
					dtu = new ElectricDTUQuery();
					dtu.setRow(++row);
					dtu.setSmokeMac(rs.getString("smokeMac"));
					//1表示bq200,2表示bq100 1.0版本，3表示bq100 2.0版本
					int devType = rs.getInt("electricDevType");
					if(devType == 1){
						dtu.setElectricDevType("bq200");
					}else if(devType ==2){
						dtu.setElectricDevType("bq100_1.0");
					}else if(devType == 3){
						dtu.setElectricDevType("bq100_2.0");
					}
					mac2 = dtu.getSmokeMac();
					String time = rs.getString("electricTime");
					dtu.setElectricTime(rs.getString("electricTime"));
					Date date=null;
					SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						date=formatter.parse(time);
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					if(System.currentTimeMillis()-date.getTime()<3600000){
						dtu.setNetstate("在线");
					}else{
						dtu.setNetstate("离线");
					}
				}
				
				int type = rs.getInt("electricType");
				
				if(type == 6){
					dtu.setElectricValue1(rs.getString("electricValue1"));
				}else if(type == 7){
					dtu.setElectricValue2(rs.getString("electricValue1"));
				}else if(type == 8){
					dtu.setElectricValue3(rs.getString("electricValue1"));
				}else if(type == 9){
					dtu.setElectricValue4(rs.getString("electricValue1"));
				}
				dtu.setElectricType(type+"");
				if(dtu.getSmokeMac().equals(mac)){
					list.remove(dtu);
				}
				list.add(dtu);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return list;
	}
}
