package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.Repeater;
//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.thread.AutoAckSmokeThread;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

import redis.clients.jedis.Jedis;

public class SmokeLineDaoImpl implements SmokeLineDao{
//	private RepeaterMap mRepeaterMap;

	public void RepeaterOffLine(String repeaterMac,int netState) {
		/*if(getRepeaterMacAreaid(repeaterMac)){	//@@增加判断是贵州电气的话，则不掉线。
			return;
		}*/
		/*if ("42101740".equals(repeaterMac)||"46101740".equals(repeaterMac)){
		  return ;
		}*/
		String sql = "update smoke set netState=? where repeater=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setInt(1, netState);
			ps.setString(2, repeaterMac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public boolean getRepeaterMacAreaid(String repeaterMac){
		boolean result = false;
		String sql = "SELECT area from areaidarea where parentId = 72 and area = ?";
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
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public void allRepeaterOffLine() {
		String sql = "update smoke set netState=0 ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	public void getAllRepeater() {
		/*PublicUtils pdao = new PublicUtilsImpl();
//		new AutoAckSmokeThread().start();
//		pdao.deleteSixData();	//add by lzz at 2017-12-08
		String sql = "select distinct repeater from smoke ";
//		resetUserInfo();//fuwuqi chongqi yunxu yonghu denglu;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
//				if(mRepeaterMap==null){
//					mRepeaterMap = RepeaterMap.newInstance();
//				}
//				String mac = rs.getString("repeater");
//				mRepeaterMap.addTime(mac,0L);
//				mRepeaterMap.addRepeater(mac);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}*/
	}
	
	public void resetUserInfo(){
		String sql = "update user set loginState = 0";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	//@@增加判断属于贵州烟感设备，只上线不掉线。
	public void smokeOffLine(String smokeMac,int netState) {
		/*if (("42101740".equals(getRepeater(smokeMac))||"46101740".equals(getRepeater(smokeMac)))&&(netState==0)){
			  return ;
		}*/
		
//		if((netState==0)&&(getParentId(smokeMac)==72)){
//			return;
//		}else{
			String sql = "update smoke set netState=? where mac=?";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
			try {
				ps.setInt(1, netState);
				ps.setString(2, smokeMac);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
//		}
	}
	
	private String getRepeater(String smokeMac) {
		String sql = "select repeater from smoke  where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		String repeater = "";
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				repeater = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return repeater;
	}

	public int getParentId(String smokeMac){
		int result = 0;
		String sqlstr = "SELECT areaid from smoke where areaid in(SELECT areaid from areaidarea where parentid = 72) and mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = 72;
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
	
	public  void smokeLossUp(String repeaterMac,String smakeMac,String lossUp){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime=sdf.format(new Date());
		String sql="insert into lossUp (smoke,lossUp,time,repeater) values(?,?,?,?)";
		Connection conn=DBConnectionManager.getConnection();
		PreparedStatement ps=DBConnectionManager.prepare(conn,sql);
		try{
			ps.setString(1,smakeMac);
			ps.setString(2, lossUp);
			ps.setString(3, nowTime);
			ps.setString(4, repeaterMac);
			ps.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(conn);
			DBConnectionManager.close(ps);
			
		}
	}
	
	public void RepeaterOnLine(String repeaterMac,Map<String,Long> list ) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("UPDATE smoke SET netState = 1 WHERE repeater = ? ");
		if(Utils.isNullStr(list.toString())&&list.size()>0){
			sqlstr.append(" AND mac NOT IN (1");
			for(String smokeMac:list.keySet()){
				sqlstr.append(","+smokeMac);
			}
			sqlstr.append(")");
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlstr.toString());
		try {
			ps.setString(1, repeaterMac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public Map<String,Long> RepeaterLoss(String repeaterMac){
		Map<String,Long> map = new HashMap<String,Long>();
		String sqlstr = "select mac from smoke where repeater = ? and deviceType <> 5";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			long nowTime = System.currentTimeMillis();
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				map.put(rs.getString(1), nowTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return map;
	}

	@Override
	public void setElectrState(String smokeMac, int state) {
		String sql = "update smoke set electrState=? where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		Log log = LogFactory.getLog(SmokeLineDaoImpl.class);
		log.debug("bingo_SmokeLineDaoImpl_setElectrState(electricMac, 1):"+smokeMac+"_"+state);
		try {
			ps.setInt(1, state);
			ps.setString(2, smokeMac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	@Override
	public int updateHeartime(String mac) {
		String sql="update smoke set netState=?, heartime=? where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int a = 0;
		try {
			ps.setInt(1, 1);//现在时间
			String now = GetTime.ConvertTimeByLong();
			ps.setString(2, now);//一年后
			ps.setString(3, mac);
			a = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return a;
	}

	//根据主机mac获取非电气设备,主机的离线列表
	public static List<String> getMacs(String repeaterMac) {

		List<String> macs = new ArrayList<String>();
		String sqlstr = "select mac from smoke where repeater = ? and deviceType not in (5,14,16,22,23,31,35,41,42,51,53,55,56,58,59,61,69,70,72,73,75,76,77,78,79,119,124,125) and netState =0"; //redis-test
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				macs.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return macs;
	}
	
	//获取主机下非电气和非主机的设备
	public static List<String> getMacs2(String repeaterMac) {

		List<String> macs = new ArrayList<String>();
		String sqlstr = "select mac from smoke where repeater = ? and deviceType <> 5 and mac <> repeater ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				macs.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return macs;
	}
	
	/**
	 * 默认所有主机状态为在线，心跳为当前时间（14min后主机的状态与实际的转态应该一致）
	 */
	@Override
	public List<Repeater> getAllRepeaters(){
		String sql = "select repeater from smoke where repeater!='' group by repeater";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<Repeater> repeaters = new ArrayList<Repeater>();
		try {
			rs = ps.executeQuery();
			while (rs.next()){
				Repeater rep = new Repeater();
				rep.setRepeaterMac(rs.getString(1));
				Repeater temp = SmokeLineDaoImpl.getRepeaterState(rep.getRepeaterMac());
				rep.setNetState(temp.getNetState());
				rep.setPowerState(temp.getPowerState());
				rep.setPowerChangeTime(temp.getPowerChangeTime());
				rep.setHeartime(temp.getHeartime());
				repeaters.add(rep);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return repeaters;
	}
	
	public static Repeater getRepeaterState(String repeaterMac){
		Repeater rep = new Repeater();
		rep.setRepeaterMac(repeaterMac);
		String sql="select rt.time from repeaterTime rt where repeater= ?";
		String sql2="select ri.repeaterState,ri.stateTime from repeaterinfo ri where repeaterMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				long heartime = GetTime.getTimeByString(rs.getString(1));
				boolean result = GetTime.ifOffLine(heartime);
				if(result){
					rep.setNetState(0);
				}else {
					rep.setNetState(1);
				}
				rep.setHeartime(heartime);
			}
			ps =DBConnectionManager.prepare(conn, sql2);
			ps.setString(1, repeaterMac);
			rs= ps.executeQuery();
			while (rs.next()){
				rep.setPowerState(rs.getInt(1));
				rep.setPowerChangeTime(GetTime.getTimeByString(rs.getString(2)));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rep;
	}

	@Override
	public void RepeaterOffLine2(String repeaterMac,int state) {
		String sql = "update smoke set netState=? where repeater=?"; //redis-test
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setInt(1, state);
			ps.setString(2, repeaterMac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}

	@Override
	public void smokeOffLine2(String mac, int state) {
		String sql = "update smoke set netState=? where mac=?"; //redis-test
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setInt(1, state);
			ps.setString(2, mac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}
	
	@Override
	public void smokeOffLine3(List<String> macs, int netState) {
		int size = macs.size();
		if (size>0){
			StringBuffer sb = new StringBuffer();
			sb.append( "update smoke set netState=? where mac in (");//redis-test
			for(int i=0;i<size-1;i++){
				sb.append("'"+macs.get(i)+"',");
			}
			sb.append("'"+macs.get(size -1)+"')");
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sb.toString());
			try {
				ps.setInt(1, netState);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
		}
	}
}
