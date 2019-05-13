package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.KeepSystemDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.KeepEntity;
//import com.cloudfire.entity.RepeaterMap;
//import com.cloudfire.entity.SmokeMap;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Indexes;
import com.cloudfire.until.Utils;

public class KeepSystemDaoImpl implements KeepSystemDao{
	private AreaDao mAreaDao;
//	private RepeaterMap mRepeaterMap;
//	private SmokeMap mSmokeMap;

	public List<KeepEntity> getRepeaterData(String repeater,String smokeMac,List<String> areaIds) {
		// TODO Auto-generated method stub
		boolean result = true;
		if(repeater==null||repeater==""){
			if(smokeMac!=null&&smokeMac.length()>0){
				repeater = getRepeaterOfSmoke(smokeMac);
			}else{
				return null;
			}
		}
		 
		String repeaterState = null;
		if(StringUtils.isNotBlank(repeater)){
			result = Utils.ifLineRepeater(repeater);
		}
		if(result){
			repeaterState="在线";
		}else{
			repeaterState="离线";
			updateRepeater(repeater);
		}
//		Map<String, Map<String,Long>> map = SmokeMap.newInstance().getSmokeMap();
//		Map<String,Long> mapSmoke = map.get(repeater);
//		if(mapSmoke==null){
//			mapSmoke = new HashMap<String,Long>();
//		}
		int len = areaIds.size();
		String sql = null;
		if(smokeMac!=null&&smokeMac.length()>0){
			sql = "SELECT s.named,s.mac,s.time,s.netState,s.address,area.area as area,s.principal1,s.principal1Phone,s.repeater,s.principal2,s.principal2Phone from smoke s,areaidarea area where 1=1 and s.areaId=area.areaId and s.repeater = ? and s.mac = ?";
		}else{
			sql = "SELECT s.named,s.mac,s.time,s.netState,s.address,area.area as area,s.principal1,s.principal1Phone,s.repeater,s.principal2,s.principal2Phone from smoke s,areaidarea area where s.repeater = ? and s.areaId=area.areaId";
		}
		if (len == 0) {
			return null;
		}
		if (len == 1) {
			sql += " and s.areaId in ('" + areaIds.get(0)
					+ "')";
		}
		if (len > 1) {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					sql += " and s.areaId in ('" + areaIds.get(i) + "', ";
				} else if (i == (len - 1)) {
					sql += " " + areaIds.get(i)
							+ ")" ;
				} else {
					sql += " " + areaIds.get(i) + ",";
				}
			}
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<KeepEntity> lists = null;
		long nowTime =System.currentTimeMillis();
		try {
			ps.setString(1, repeater);
			if(smokeMac!=null&&smokeMac.length()>0){
				ps.setString(2, smokeMac);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				KeepEntity mKeepEntity = new KeepEntity();
				if(lists==null){
					lists = new ArrayList<KeepEntity>();
				}
				String mac = rs.getString(2);
				int netState = rs.getInt(4);
				
				mKeepEntity.setRepeaterState(repeaterState);
				mKeepEntity.setSmokeMac(mac);
				mKeepEntity.setAreaName(rs.getString("area"));
				mKeepEntity.setAddTime(rs.getString(3));
				mKeepEntity.setSmokeName(rs.getString(1));
				mKeepEntity.setRepeaterMac(repeater);
				mKeepEntity.setAddress(rs.getString(5));
				mKeepEntity.setPrincipal1(rs.getString("principal1"));
				mKeepEntity.setPrincipal1Phone(rs.getString("principal1Phone"));
				mKeepEntity.setPrincipal2(rs.getString("principal2"));
				mKeepEntity.setPrincipal2Phone(rs.getString("principal2Phone"));
				if(netState==0){
					mKeepEntity.setLineState("离线");
				}else{
					mKeepEntity.setLineState("在线");
				}
				mKeepEntity.setRepeaterMac(rs.getString("repeater"));
				lists.add(mKeepEntity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}
	
	public List<KeepEntity> getRepeaterData(String repeater,String smokeMac,String areaid) {
		// TODO Auto-generated method stub
		String repeaterState = "";
		boolean repeaterOn = false;
		if(StringUtils.isBlank(repeater)){
			if(StringUtils.isNotBlank(smokeMac)){
				repeater = getRepeaterOfSmoke(smokeMac);
			} else {
				return null;
			}
		} else if(repeater.equals("无主机")){
			repeater = "";
		}else{
			/*mRepeaterMap = RepeaterMap.newInstance();
			long repeaterTime = mRepeaterMap.getTime(repeater);
			result = GetTime.ifOffLine(repeaterTime);
			System.out.println("repeater:"+repeater+"repeaterTime:"+repeaterTime+">>>>>>>>>>>>>>>>>++++++TRUE===="+result);
//			int parentId = getParentIdByareaId(repeater);
*/			
			//获取主机的在线状态
			repeaterOn = Utils.ifLineRepeater(repeater);
			if(repeaterOn){
				repeaterState="在线";
			}else{
				repeaterState="离线";
				//更新离线主机下的设备状态为离线
				updateRepeater(repeater);
			}
		}
		
		//获取主机下非电气设备的最近一次心跳时刻表
//		Map<String, Map<String,Long>> map = SmokeMap.newInstance().getSmokeMap();
//		Map<String,Long> mapSmoke = map.get(repeater);
//		if(mapSmoke==null){
//			mapSmoke = new HashMap<String,Long>();
//		}
		
		String sql =  "SELECT s.named,s.mac,s.time,s.netState,s.address,area.area as area,s.principal1,s.principal1Phone,s.repeater,s.areaid,s.principal2,s.principal2Phone from smoke s,areaidarea area where 1=1 and s.areaId=area.areaId and s.repeater = ? ";
		if(StringUtils.isNotBlank(smokeMac)){
			sql += " and s.mac = ? ";
		}
		if(StringUtils.isNotBlank(areaid)){
			sql +=" and s.areaid = "+areaid;
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<KeepEntity> lists = null;
//		long nowTime =System.currentTimeMillis();
		try {
			ps.setString(1, repeater);
			if(StringUtils.isNotBlank(smokeMac)){
				ps.setString(2, smokeMac);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				KeepEntity mKeepEntity = new KeepEntity();
				if(lists==null){
					lists = new ArrayList<KeepEntity>();
				}
				String mac = rs.getString(2);
				int netState = rs.getInt(4);
				
				mKeepEntity.setRepeaterState(repeaterState);
				mKeepEntity.setSmokeMac(mac);
				mKeepEntity.setAreaName(rs.getString("area"));
				mKeepEntity.setAddTime(rs.getString(3));
				mKeepEntity.setSmokeName(rs.getString(1));
				mKeepEntity.setRepeaterMac(repeater);
				mKeepEntity.setAddress(rs.getString(5));
				mKeepEntity.setPrincipal1(rs.getString("principal1"));
				mKeepEntity.setPrincipal1Phone(rs.getString("principal1Phone"));
				mKeepEntity.setPrincipal2(rs.getString("principal2"));
				mKeepEntity.setPrincipal2Phone(rs.getString("principal2Phone"));
				
				if (netState == 0){
					mKeepEntity.setLineState("离线");
				} else {
					mKeepEntity.setLineState("在线");
				}
//				if(mapSmoke.containsKey(mac)){
//					long smokeTime = mapSmoke.get(mac);
//					boolean smokeResult = GetTime.ifSmokeOffLine(smokeTime, nowTime);
//					String smokeHeartTime = GetTime.getTimeByLong(smokeTime);
//					mKeepEntity.setHeartTime(smokeHeartTime);
//					if(!repeaterOn){
//						mKeepEntity.setLineState("离线");
//					}else{
//						if(smokeResult||netState==0){
//							mKeepEntity.setLineState("离线");
//						}else{
//							mKeepEntity.setLineState("在线");
//						}
//					}	
//				}else{
//					if(netState==0){
//						mKeepEntity.setLineState("离线");
//					}else{
//						mKeepEntity.setLineState("在线");
//					}
//				}
				mKeepEntity.setRepeaterMac(rs.getString("repeater"));
				lists.add(mKeepEntity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}
	
	public void updateRepeater(String repeaterMac){
		if ("42101740".equals(repeaterMac)||"46101740".equals(repeaterMac)){
			  return ;
		}
		
		String sql = "update smoke set netState = 0 ,netState2 = 0 where repeater = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, repeaterMac);
			ps.executeUpdate();
			
			Jedis jedis = RedisConnection.getJedis();
			if (jedis!=null) {
				String requestId = UUID.randomUUID().toString().replace("-", "");
				while(!RedisOps.tryGetDistributedLock(jedis, "L"+repeaterMac, requestId, 10000)){
					try {
						Thread.currentThread().sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				RedisOps.setList(jedis, repeaterMac,SmokeLineDaoImpl.getMacs(repeaterMac));
				
				RedisOps.releaseDistributedLock(jedis, "L"+repeaterMac, requestId);
				jedis.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	public List<AreaAndRepeater> getAreaAndRepeater() {
		// TODO Auto-generated method stub
		mAreaDao = new AreaDaoImpl();
		Map<Integer,String> map = mAreaDao.getAllArea(null);
		if(map==null){
			return null;
		}
		String sql = "SELECT DISTINCT repeater from smoke where areaId = ? ";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<AreaAndRepeater> listAreaAndRepeater=null;
		try{
			Set<Integer> set = map.keySet();
			for (Integer key : set) {
				if(listAreaAndRepeater==null){
					listAreaAndRepeater = new ArrayList<AreaAndRepeater>();
				}
				AreaAndRepeater mAreaAndRepeater = new AreaAndRepeater();
				mAreaAndRepeater.setAreaId(key);
				mAreaAndRepeater.setAreaName(map.get(key));
				List<String> lists = new ArrayList<String>();
				ps.setInt(1, key);
				rs = ps.executeQuery();
				while(rs.next()){
					String repeaterMac = rs.getString(1);
					if(repeaterMac!=null&&repeaterMac.length()>0){
						lists.add(repeaterMac);
					}else{
						lists.add("无主机");
					}
				}
				if(lists.size()>0){
					mAreaAndRepeater.setListMac(lists);
					listAreaAndRepeater.add(mAreaAndRepeater);
				}
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return listAreaAndRepeater;
	}
	
	public List<AreaAndRepeater> getAreaAndRepeater(String userId){
		List<AreaAndRepeater> listAreaAndRepeater=null;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT s.repeater,s.areaid,a.area from smoke s,areaidarea a,useridareaid u where s.areaId = a.areaId and u.areaId = a.areaId ");
		
		if(StringUtils.isNotBlank(userId)){
			sql.append(" and u.userid = '"+userId+"'");
		}
		sql.append(" ORDER BY a.areaId desc");
		Connection conn =DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		List<String> rlist = null;
		AreaAndRepeater mAreaAndRepeater = null;
		int areaid = 0;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				int aid = rs.getInt("areaid");
				if(listAreaAndRepeater==null){
					listAreaAndRepeater = new ArrayList<AreaAndRepeater>();
				}
				if(rlist==null||areaid!=aid){
					if(rlist!=null){
						mAreaAndRepeater.setListMac(rlist);
						listAreaAndRepeater.add(mAreaAndRepeater);
					}
					rlist = new ArrayList<String>();
					mAreaAndRepeater = new AreaAndRepeater();
					mAreaAndRepeater.setAreaId(aid);
					mAreaAndRepeater.setAreaName(rs.getString("area"));
				}
				
				String repeaterMac = rs.getString("repeater");
				if(StringUtils.isNotBlank(repeaterMac)){
					rlist.add(repeaterMac);
				}else{
					rlist.add("无主机");
				}
				areaid = aid;
			}
			if(rlist.size()>0){
				mAreaAndRepeater.setListMac(rlist);
				listAreaAndRepeater.add(mAreaAndRepeater);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return listAreaAndRepeater;
	}

	public String getRepeaterOfSmoke(String smokeMac) {
		String sql = "select repeater from smoke where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		String result = null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public List<AreaAndRepeater> getAreaAndRepeaterByUserId(String userId) {
		mAreaDao = new AreaDaoImpl();
		Map<Integer,String> map = mAreaDao.getAllArea(userId);
		if(map==null){
			return null;
		}
		String sql = "SELECT DISTINCT repeater from smoke where areaId = ? ";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<AreaAndRepeater> listAreaAndRepeater=null;
		try{
			Set<Integer> set = map.keySet();
			for (Integer key : set) {
				if(listAreaAndRepeater==null){
					listAreaAndRepeater = new ArrayList<AreaAndRepeater>();
				}
				AreaAndRepeater mAreaAndRepeater = new AreaAndRepeater();
				mAreaAndRepeater.setAreaId(key);
				mAreaAndRepeater.setAreaName(map.get(key));
				List<String> lists = new ArrayList<String>();
				ps.setInt(1, key);
				rs = ps.executeQuery();
				while(rs.next()){
					String repeaterMac = rs.getString(1);
					if(repeaterMac!=null&&repeaterMac.length()>0){
						lists.add(repeaterMac);
					}
				}
				if(lists.size()>0){
					mAreaAndRepeater.setListMac(lists);
					listAreaAndRepeater.add(mAreaAndRepeater);
				}
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return listAreaAndRepeater;
	}
	
	
	public KeepEntity findSmokeBySmokeMac(String smokeMac) {
		
		String sql = null;
		if(smokeMac!=null&&smokeMac.length()>0){
			sql = "SELECT s.named,s.mac,s.time,s.netState,s.address,s.areaId as area,s.principal1,s.principal1Phone,s.linkageDistance,s.principal2,s.principal2Phone,s.placeTypeId,p.placeName,s.latitude,s.longitude from smoke s,areaidarea area,placetypeidplacename p where 1=1 and s.placeTypeId=p.placeTypeId and s.areaId=area.areaId and s.mac = ?";
		}else{
			sql = "SELECT s.named,s.mac,s.time,s.netState,s.address,s.areaId as area,s.principal1,s.principal1Phone,s.linkageDistance,s.principal2,s.principal2Phone from smoke s,areaidarea area,placetypeidplacename p where s.repeater = ? and s.areaId=area.areaId and s.placeTypeId=p.placeTypeId";
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		KeepEntity mKeepEntity = new KeepEntity();	
		try {
			if(smokeMac!=null&&smokeMac.length()>0){
				ps.setString(1, smokeMac);
			}
			rs = ps.executeQuery();
			while(rs.next()){
							
				String mac = rs.getString(2);
				int netState = rs.getInt(4);
				
				//mKeepEntity.setRepeaterState(repeaterState);
				mKeepEntity.setSmokeMac(mac);
				mKeepEntity.setAreaName(rs.getString("area"));
				mKeepEntity.setAddTime(rs.getString(3));
				mKeepEntity.setSmokeName(rs.getString(1));
				//mKeepEntity.setRepeaterMac(repeater);
				mKeepEntity.setAddress(rs.getString(5));
				mKeepEntity.setPrincipal1(rs.getString("principal1"));
				mKeepEntity.setLinkageDistance(rs.getString("linkageDistance"));
				mKeepEntity.setPrincipal1Phone(rs.getString("principal1Phone"));
				mKeepEntity.setPrincipal2(rs.getString("principal2"));
				mKeepEntity.setPrincipal2Phone(rs.getString("principal2Phone"));
				mKeepEntity.setPlaceTypeId(rs.getString("placeTypeId"));
				mKeepEntity.setPlaceName(rs.getString("placeName"));
				mKeepEntity.setLatitude(rs.getString("latitude"));
				mKeepEntity.setLongitude(rs.getString("longitude"));
					if(netState==0){
						mKeepEntity.setLineState("0");
					}else{
						mKeepEntity.setLineState("1");
					}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return mKeepEntity;
	}
	
	
	public int updateSmokeBySmokeMac(KeepEntity entity){
		//SELECT s.named,s.mac,s.time,s.netState,s.address,s.areaId as area from smoke s,areaidarea area where 1=1 and s.areaId=area.areaId and s.mac = ?
		String sql = " update smoke s set ";
		if (StringUtils.isNotBlank(entity.getSmokeName())) {
			sql += " s.named = '"+entity.getSmokeName() + "' ,";
		}
		if(StringUtils.isNotBlank(entity.getAddTime())){
			sql += " s.time = '" + entity.getAddTime()+ "' ,";
		}
		if (StringUtils.isNotBlank(entity.getLineState())) {
			if(entity.getLineState().equals(Constant.NETSTATE1)){			
				sql += " s.netState = 1,";
			}
			else if(entity.getLineState().equals(Constant.NETSTATE0)){
				sql += " s.netState = 0,";
			}
			
		}
		if(StringUtils.isNotBlank(entity.getAddress())){
			sql += " s.address = '" +entity.getAddress() +" ' ,";
		}
		if(StringUtils.isNotBlank(entity.getPrincipal1())){
			sql +=" s.principal1 = '"+entity.getPrincipal1()+" ',";
		}
		if(StringUtils.isNotBlank(entity.getPrincipal1Phone())){
			sql +=" s.principal1Phone = '"+entity.getPrincipal1Phone()+" ',";
		}
		if(StringUtils.isNotBlank(entity.getPrincipal2())){
			sql +=" s.principal2 = '"+entity.getPrincipal2()+" ',";
		}
		if(StringUtils.isNotBlank(entity.getPrincipal2Phone())){
			sql +=" s.principal2Phone = '"+entity.getPrincipal2Phone()+" ',";
		}
		if (StringUtils.isNotBlank(entity.getAreaName())) {
			sql += " s.areaId = "+entity.getAreaName() ;
		}
		if (StringUtils.isNotBlank(entity.getLinkageDistance())) {
			sql += " ,s.linkageDistance ='"+entity.getLinkageDistance() +"' ";
		}
		if (StringUtils.isNotBlank(entity.getPlaceTypeId())) {
			sql += " ,s.placeTypeId ='"+entity.getPlaceTypeId() +"' ";
		}
		if (StringUtils.isNotBlank(entity.getLatitude())) {
			sql += " ,s.latitude ='"+entity.getLatitude() +"' ";
		}
		if (StringUtils.isNotBlank(entity.getLongitude())) {
			sql += " ,s.longitude ='"+entity.getLongitude() +"' ";
		}
		
		if (StringUtils.isNotBlank(entity.getSmokeMac())) {
			sql += " where s.mac ='"+entity.getSmokeMac() +"' ";
		}
		
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		int result = 0;
		try {
			
			 result = ps.executeUpdate();
			
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
	
	public int getParentIdByareaId(String areaId){
		String sql = "SELECT area,parentId from areaidarea where area = ?";
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs  = null;
		try {
			ps.setString(1, areaId);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt("parentId");
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
	public List<KeepEntity> getRepeaterDataBySmoke(String smokeName,String repeater, String areaid) {
		String repeaterState = null;
		boolean result = true;
		if(StringUtils.isBlank(repeater)){
			repeater =null;
		}else if(repeater.equals("无主机")||repeater=="无主机"){
			repeater = "";
		}else{
			/*mRepeaterMap = RepeaterMap.newInstance();
			long repeaterTime = mRepeaterMap.getTime(repeater);
			result = GetTime.ifOffLine(repeaterTime);
			System.out.println("repeater:"+repeater+"repeaterTime:"+repeaterTime+">>>>>>>>>>>>>>>>>++++++TRUE===="+result);
//			int parentId = getParentIdByareaId(repeater);
*/			
			result = Utils.ifLineRepeater(repeater);
			if(result){
				repeaterState="在线";
			}else{
				repeaterState="离线";
				updateRepeater(repeater);
			}
		}
		
		
//		mSmokeMap = SmokeMap.newInstance();
//		Map<String, Map<String,Long>> map = mSmokeMap.getSmokeMap();
//		Map<String,Long> mapSmoke = map.get(repeater);
//		if(mapSmoke==null){
//			mapSmoke = new HashMap<String,Long>();
//		}
		
		String sql = "SELECT s.named,s.mac,s.time,s.netState,s.address,area.area as area,s.principal1,s.principal1Phone,s.repeater,s.areaid from smoke s,areaidarea area where  s.areaId=area.areaId";
		
		
		if(StringUtils.isNotBlank(areaid)){
			sql +=" and s.areaid = "+areaid;
		}
		if (repeater!=null) {
			sql += " and s.repeater= '"+repeater+"'";
		}
		if(StringUtils.isNotBlank(smokeName)){
			sql +=" and s.named like '%" + smokeName + "%'";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<KeepEntity> lists = null;
		long nowTime =System.currentTimeMillis();
		try {
//			ps.setString(1, repeater);
			rs = ps.executeQuery();
			while(rs.next()){
				KeepEntity mKeepEntity = new KeepEntity();
				if(lists==null){
					lists = new ArrayList<KeepEntity>();
				}
				String mac = rs.getString(2);
				int netState = rs.getInt(4);
				
				mKeepEntity.setRepeaterState(repeaterState);
				mKeepEntity.setSmokeMac(mac);
				mKeepEntity.setAreaName(rs.getString("area"));
				mKeepEntity.setAddTime(rs.getString(3));
				mKeepEntity.setSmokeName(rs.getString(1));
				mKeepEntity.setRepeaterMac(repeater);
				mKeepEntity.setAddress(rs.getString(5));
				mKeepEntity.setPrincipal1(rs.getString("principal1"));
				mKeepEntity.setPrincipal1Phone(rs.getString("principal1Phone"));
				if(netState==0){
					mKeepEntity.setLineState("离线");
				}else{
					mKeepEntity.setLineState("在线");
				}	
				mKeepEntity.setRepeaterMac(rs.getString("repeater"));
				lists.add(mKeepEntity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}

}
