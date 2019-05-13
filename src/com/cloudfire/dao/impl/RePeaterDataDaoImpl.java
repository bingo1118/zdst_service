package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;

import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.DeviceAlarmEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.Repeater;
import com.cloudfire.entity.RepeaterBean;
//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class RePeaterDataDaoImpl implements RePeaterDataDao {
//	private RepeaterMap mRepeaterMap;
	
	@Override
	public int getDeviceType(String mac) {
		int result = 0;
		String sql = "SELECT deviceType FROM smoke WHERE mac= ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
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
	public DeviceAlarmEntity getDeviceEntity(String mac) {
		DeviceAlarmEntity dae = new DeviceAlarmEntity();
		String sql = "SELECT * from (SELECT * from (SELECT mac,deviceType from smoke where mac = ? ) a LEFT JOIN (SELECT smokeMac,alarmthreshold1,alarmthreshold2,alarmthreshold3,alarmthreshold4,alarmFamily from alarmthreshold where smokeMac = ?) b on a.mac = b.smokeMac) c LEFT JOIN (SELECT waterMac,value,time from waterinfo where waterMac = ?  ORDER BY id desc LIMIT 1) w on w.waterMac = c.mac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			ps.setString(2, mac);
			ps.setString(3, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				dae.setSmokeMac(mac);
				dae.setDeviceType(rs.getInt("deviceType"));
				if(rs.getInt("alarmFamily") == 208){
					dae.setAlarmthreshold1(rs.getInt("alarmthreshold1"));
				}else if(rs.getInt("alarmFamily") == 207){
					dae.setAlarmthreshold2(rs.getInt("alarmthreshold1"));
				}
				dae.setCurrentValue(rs.getInt("value"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return dae;
	}

	@Override
	public void saveRepeaterInfo(RePeaterData repeaterData) {
		String sql =""; 
		if(ifExitRepeater(repeaterData.getRepeatMac())){
			sql ="update repeaterinfo set stateTime=?,repeaterState=? where repeaterMac = ?";
		}else{
			sql ="insert into repeaterinfo(stateTime,repeaterState,repeaterMac) values(?,?,?) ";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		String timestate = GetTime.ConvertTimeByLong();
		try {
			ps.setString(3, repeaterData.getRepeatMac());
			ps.setString(1, timestate);
			ps.setInt(2, repeaterData.getRepeaterState());
			ps.executeUpdate();
			
			//更新主机的主备电状态
			Jedis jedis = RedisConnection.getJedis();
			if (jedis != null){
				if (RedisOps.exist(jedis,"R"+repeaterData.getRepeatMac())) {
					Repeater rep  = (Repeater) RedisOps.getObject(jedis,"R"+repeaterData.getRepeatMac());
					rep.setPowerChangeTime(GetTime.getTimeByString(timestate));
					rep.setPowerState(repeaterData.getRepeaterState());
					RedisOps.setObject(jedis,"R"+repeaterData.getRepeatMac(), rep);
				}
				jedis.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	public boolean ifExitRepeater(String repeaterMac){
		String sqlstr = "SELECT repeaterMac from repeaterinfo where repeaterMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		boolean result = false;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			if(rs.next()){
				result = true;
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
	public List<RepeaterBean> queryRepeaterInfo(RepeaterBean query) {
		StringBuffer sqlstr = new StringBuffer();
		/*sqlstr.append("select * from(select repeaterMac,stateTime,repeaterState from repeaterinfo where 1=1");
		if(query.getRepeaterMac()!=null&&query.getRepeaterMac()!=""){
			sqlstr.append(" and repeaterMac="+query.getRepeaterMac());
		}
		sqlstr.append(" order by stateTime desc ) as a group by repeaterMac");*/
		String limit = " limit " + query.getStartRow() + ","+ query.getPageSize();
		sqlstr.append("select repeaterMac, stateTime, repeaterState from repeaterinfo where 1=1 ");
		if(query.getRepeaterMac()!=null&&query.getRepeaterMac()!=""){
			sqlstr.append(" and repeaterMac= '"+query.getRepeaterMac() + "'");
		}
		sqlstr.append(" group by repeaterMac ");
		String sql = sqlstr.toString() + limit;
		Connection conn = DBConnectionManager.getConnection();
		System.out.println("数据："+sql);
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<RepeaterBean> repeaterList = null;
		try {
			repeaterList = new ArrayList<RepeaterBean>();
			rs = ps.executeQuery();
			int row = query.getStartRow();
//			mRepeaterMap = RepeaterMap.newInstance();
			while(rs.next()){
				RepeaterBean rb = new RepeaterBean();
				rb.setRow(++row);
//				long repeaterTime = mRepeaterMap.getTime(rs.getString(1));
//				boolean result = GetTime.ifOffLine(repeaterTime);
				boolean result=Utils.ifLineRepeater(rs.getString(1));
				if(result){
					rb.setNetStates("离线");
					rb.setNetstate(0);
				}else{
					rb.setNetStates("在线");
					rb.setNetstate(1);
				}
				rb.setRepeaterMac(rs.getString(1));
				rb.setRepeaterTime(rs.getString(2));
				int states = rs.getInt(3);
				rb.setHoststate(states);
				if(states==1){
					rb.setHostStates("主电");
				}else if(states==2){
					rb.setHostStates("备电");
				}else if(states==3){
					rb.setHostStates("主备电共存");
				}else{
					rb.setHostStates("未开启");
				}
				repeaterList.add(rb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return repeaterList;
	}
	
	@Override
	public int countRepeaterInfo(RepeaterBean query) {
		int totalcount = 0;
		String sql = "select count(id) as totalcount from repeaterinfo where 1=1 ";
		if(query.getRepeaterMac()!=null&&query.getRepeaterMac()!=""){
			sql += " and repeaterMac = '"+query.getRepeaterMac() + "'";
		}
		Connection conn = DBConnectionManager.getConnection();
		System.out.println("数量："+sql);
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				totalcount = rs.getInt(1);
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
	public void addRepeaterTime(String repeaterMac) {
		String sql = "";
		if(ifExistRepeater(repeaterMac)){
			sql = "update repeatertime set time = ?,ipaddress = ? where repeater = ?";
		}else{
			sql = "INSERT into repeatertime(time,ipaddress,repeater) VALUES (?,?,?)";
		}
		String reptime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, reptime);
			ps.setString(2, repeaterMac);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}
	
	@Override
	public void addRepeaterTime(String repeaterMac,String ipaddress) {
		String sql = "";
		if(ifExistRepeater(repeaterMac)){
			sql = "update repeatertime set time = ?,ipaddress = ? where repeater = ?";
		}else{
			sql = "INSERT into repeatertime(time,ipaddress,repeater) VALUES (?,?,?)";
		}
		String reptime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, reptime);
			ps.setString(2, ipaddress);
			ps.setString(3, repeaterMac);
			ps.executeUpdate();
			
			Jedis jedis = RedisConnection.getJedis();
			if (jedis != null){
			//更新主机心跳
				String requestId = UUID.randomUUID().toString().replace("-", "");
				while(!RedisOps.tryGetDistributedLock(jedis, "L"+repeaterMac, requestId, 10000)){
					try {
						Thread.currentThread().sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if (RedisOps.exist(jedis,"R"+repeaterMac)){
					Repeater rep = (Repeater) RedisOps.getObject(jedis,"R"+repeaterMac);
					rep.setNetState(1);
					rep.setHeartime(GetTime.getTimeByString(reptime));
					RedisOps.setObject(jedis,"R"+repeaterMac, rep);
				}
				
				RedisOps.releaseDistributedLock(jedis, "L"+repeaterMac, requestId);
				jedis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}
	
	public boolean ifExistRepeater(String repeaterMac){
		boolean result = false;
		String sql = "SELECT repeater from repeatertime where repeater = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public String getRepeaterTime(String repeaterMac) {
		String sqlstr = "SELECT repeater,time from repeatertime where repeater = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		String repeaterTime = "";
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				repeaterTime = rs.getString(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return repeaterTime;
	}

	@Override
	public int getDeviceTypeNum(String mac) {
		int result = 0;
		String sql = "SELECT devTypeNum FROM smoke WHERE mac= ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
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
	public String getIpByRepeater(String repeaterMac) {
		String sql = "select ipaddress from repeatertime where repeater = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String ip = "";
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				ip = rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return ip;
	}

	public static Long getRepeaterHeartime(String repeaterMac){
		Long heartime = 0L;
		String sql ="select time from repeatertime where repeater=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			if (rs.next()){
				heartime=GetTime.getTimeByString(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return heartime; 
	}
	
	public static RepeaterBean getRepeaterState(String repeaterMac) {
		RepeaterBean rb = new RepeaterBean();
		Long heartime = getRepeaterHeartime(repeaterMac);
		
//		RepeaterMap.newInstance().getTime(repeaterMac);
		boolean result = GetTime.ifOffLine(heartime);
		if(result){
			rb.setNetstate(0);
		}else {
			rb.setNetstate(1);
		}
		if (heartime==0) {
			rb.setHeartime("暂无心跳");
		} else {
			rb.setHeartime(GetTime.getTimeByLong(heartime));
		}
		rb.setHoststate(0);
		rb.setRepeaterTime("暂无电源状态改变");
		rb.setRepeaterMac(repeaterMac);
		String sql = "select repeaterMac,statetime,repeaterState from repeaterInfo where repeaterMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				rb.setHoststate(rs.getInt(3));
				if (StringUtils.isNotBlank(rs.getString(2))){
					rb.setRepeaterTime(rs.getString(2));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return rb;
	}

	

}
