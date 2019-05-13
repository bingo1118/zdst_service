package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudfire.dao.AllCameraDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.EnvironmentDao;
import com.cloudfire.dao.IfDealAlarmDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.EnvironmentEntity;
import com.cloudfire.entity.EnvironmentHistoryMsgEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.Constant;

public class EnvironmentDaoImpl implements EnvironmentDao {
	private PlaceTypeDao mPlaceTypeDao;
	private AreaDao mAreaDao;
	private AllCameraDao mAllCameraDao;
	private IfDealAlarmDao mIfDealAlarmDao;
	
	@Override
	public void addEnvironmentInfo(EnvironmentEntity env) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTimeString=sdf.format(new Date());
		String sql = null;
		ifDeleteEnv(env.getArimac());
		sql = "insert into environment(co2,temperature,humidity,pm25,methanal,timeMilliSecond,dateTime,arimac) values(?,?,?,?,?,?,?,?)";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(8, env.getArimac());
			ps.setInt(1, Integer.parseInt(env.getCo2()));
			ps.setDouble(2, Double.parseDouble(env.getTemperature()));
			ps.setInt(3, Integer.parseInt(env.getHumidity()));
			ps.setInt(4, Integer.parseInt(env.getPm25()));
			ps.setDouble(5, Double.parseDouble(env.getMethanal()));
			ps.setLong(6, System.currentTimeMillis());
			ps.setString(7, dateTimeString);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public void deleteEnvironment(String airMac,Long airTime){
		String sql = "delete from environment where arimac = ? and timeMilliSecond = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, airMac);
			ps.setLong(2, airTime);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}
	
	public void ifDeleteEnv(String airMac){
		String loginSql = "select arimac,timeMilliSecond from environment where arimac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		try {
			ps.setString(1, airMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(System.currentTimeMillis()-rs.getLong("timeMilliSecond")>1000*60*60*24*8){
					deleteEnvironment(rs.getString("arimac"),rs.getLong("timeMilliSecond"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	@Override
	public AllSmokeEntity getNotSmokeMac(String userId, String privilege,String page) {
		mAreaDao = new AreaDaoImpl();
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		AllSmokeDaoImpl adao = new AllSmokeDaoImpl();
		List<String> listMac = adao.getNormalAllSmoke(userId,privilege,page);
		if(listMac==null){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		List<String> listNum = mAreaDao.getAreaStrByMac(listMac);
		mAllCameraDao = new AllCameraDaoImpl();
		Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		
		String pageSql = null;
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				pageSql = new String(" order by time desc limit "+startNum+" , "+endNum);
			}
		}
		
		StringBuffer strSql = new StringBuffer();
		int len = listMac.size();
		if(len==1){
			strSql.append(" and mac in (?)");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" and mac in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?)");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
//				" from smoke where deviceType in(11,12,15)");
				" from smoke where 1=1 ");
		loginSql+=Constant.devTypeChooseSQLStatement_zdst("3");
		String sqlStr = null;
		if(pageSql!=null){
			sqlStr= new String(loginSql+strSql.toString()+pageSql.toString());
		}else{
			sqlStr= new String(loginSql+strSql.toString());
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllSmokeEntity ae = null;
		List<SmokeBean> sbList = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setString(i, listMac.get(i-1));
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(ae==null){
					ae = new AllSmokeEntity();
				}
				String mac = rs.getString("mac");
				int areaId = rs.getInt("areaId");
				String placeTypeId = rs.getString("placeTypeId");
//				int ifDeal = 1;
//				if(macStateMap!=null&&macStateMap.size()>0){
//					ifDeal = macStateMap.get(mac);
//				}
				String placeName =null;
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeId);
				}
				String areaName =null;
				if(mapArea!=null&&mapArea.size()>0){
					areaName = mapArea.get(areaId);
				}
				SmokeBean sb = new SmokeBean();
				if(sbList==null){
					sbList = new ArrayList<SmokeBean>();	
				}
				CameraBean cb =null;
				if(mapCamera!=null&&mapCamera.size()>0){
					cb = mapCamera.get(mac);
				}
				if(cb!=null){
					cb.setAreaName(areaName);
					cb.setPlaceType(placeName);
					sb.setCamera(cb);
				}else{
					cb = new CameraBean();
					sb.setCamera(cb);
				}
				sb.setAddress(rs.getString("address"));
				sb.setDeviceType(rs.getInt("deviceType"));
				sb.setLatitude(rs.getString("latitude"));
				sb.setLongitude(rs.getString("longitude"));
				sb.setMac(mac);
				sb.setName(rs.getString("named"));
				sb.setNetState(rs.getInt("netState"));
				sb.setPlaceeAddress(rs.getString("placeAddress"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPlaceType(placeName);
				sb.setAreaName(areaName);
				sb.setIfDealAlarm(rs.getInt("ifAlarm"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				sb.setPrincipal2(rs.getString("principal2"));
				sb.setPrincipal2Phone(rs.getString("principal2Phone"));
				sb.setRepeater(rs.getString("repeater"));
				sbList.add(sb);
			}
			if(sbList!=null&&sbList.size()>0){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setSmoke(sbList);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return ae;
	}

	@Override
	public EnvironmentEntity getEnvironmentEntityInfo(String airMac) {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM (SELECT id,arimac,co2,temperature,humidity,pm25,methanal,timeMilliSecond,DATETIME FROM environment WHERE arimac=? ORDER BY DATETIME DESC) AS a GROUP BY arimac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		EnvironmentEntity env = null;
		try {
			ps.setString(1, airMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(env == null){
					env = new EnvironmentEntity();
				}
				env.setArimac(rs.getString("arimac"));
				env.setCo2(rs.getString("co2")+"");
				env.setTemperature(rs.getDouble("temperature")+"");
				env.setHumidity(rs.getInt("humidity")+"");
				int pm25 = rs.getInt(6);
				if(pm25>=180){
					env.setPriority2(4);
				}else if(pm25>=100&&pm25<180){
					env.setPriority2(3);
				}else if(pm25>=70&&pm25<100){
					env.setPriority2(2);
				}else if(pm25>=0&&pm25<70){
					env.setPriority2(1);
				}
				env.setPm25(pm25+"");
				double methanal = rs.getDouble(7);
				if(methanal>=0.080d){
					env.setPriority1(4);
				}else if(methanal>=0.030d&&methanal<0.080d){
					env.setPriority1(3);
				}else if(methanal>=0.020d&&methanal<0.030d){
					env.setPriority1(2);
				}else if(methanal>=0.000d&&methanal<0.020d){
					env.setPriority1(1);
				}
				env.setMethanal(methanal+"");
				int max = Math.max(env.getPriority1(), env.getPriority2());
				env.setPriority(max);
				env.setDataTimes(rs.getString("DATETIME"));
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return env;
	}

	@Override
	public AllSmokeEntity getSuperNotSmokeMac(String userId, String privilege,
			String page) {
		// TODO Auto-generated method stub
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null&&!privilege.equals("1")){
			return null;
		}
		mIfDealAlarmDao = new IfDealAlarmDaoImpl();
		List<String> listMac = mIfDealAlarmDao.getUserSmokeMac(listNum);
		if(listMac==null&&!privilege.equals("1")){
			return null;
		}
//		Map<String, Integer> macStateMap = mIfDealAlarmDao.getIfDealAlarm(listMac);
		mAllCameraDao = new AllCameraDaoImpl();
		Map<String,CameraBean> mapCamera = mAllCameraDao.getCameraByMac(listNum);
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		
		String pageSql = null;
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				pageSql = new String(" order by time desc limit "+startNum+" , "+endNum);
			}
		}
		
		StringBuffer strSql = new StringBuffer();
		int len = listNum.size();
		if(len==1){
			strSql.append(" and areaId in (?)");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" and areaId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?)");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		String loginSql = new String("select address,latitude,longitude,mac,named,netState,deviceType,placeAddress" +
				",principal1,principal1Phone,principal2,principal2Phone,repeater,areaId,placeTypeId,ifAlarm" +
//				" from smoke where deviceType in (11,12,15) ");
				" from smoke where 1=1 ");
		loginSql+=Constant.devTypeChooseSQLStatement_zdst("3");
		String sqlStr = null;
		if(pageSql!=null){
			sqlStr= new String(loginSql+strSql.toString()+pageSql.toString());
		}else{
			sqlStr= new String(loginSql+strSql.toString());
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllSmokeEntity ae = null;
		List<SmokeBean> sbList = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(ae==null){
					ae = new AllSmokeEntity();
				}
				String mac = rs.getString("mac");
				int areaId = rs.getInt("areaId");
				String placeTypeId = rs.getString("placeTypeId");
//				int ifDeal = 1;
//				if(macStateMap!=null&&macStateMap.size()>0){
//					ifDeal = macStateMap.get(mac);
//				}
				String placeName =null;
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeId);
				}
				String areaName =null;
				if(mapArea!=null&&mapArea.size()>0){
					areaName = mapArea.get(areaId);
				}
				SmokeBean sb = new SmokeBean();
				if(sbList==null){
					sbList = new ArrayList<SmokeBean>();	
				}
				CameraBean cb =null;
				if(mapCamera!=null&&mapCamera.size()>0){
					cb = mapCamera.get(mac);
				}
				if(cb!=null){
					cb.setAreaName(areaName);
					cb.setPlaceType(placeName);
					sb.setCamera(cb);
				}else{
					cb = new CameraBean();
					sb.setCamera(cb);
				}
				sb.setAddress(rs.getString("address"));
				sb.setDeviceType(rs.getInt("deviceType"));
				sb.setLatitude(rs.getString("latitude"));
				sb.setLongitude(rs.getString("longitude"));
				sb.setMac(mac);
				sb.setName(rs.getString("named"));
				sb.setNetState(rs.getInt("netState"));
				sb.setPlaceeAddress(rs.getString("placeAddress"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPlaceType(placeName);
				sb.setAreaName(areaName);
				sb.setIfDealAlarm(rs.getInt("ifAlarm"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				sb.setPrincipal2(rs.getString("principal2"));
				sb.setPrincipal2Phone(rs.getString("principal2Phone"));
				sb.setRepeater(rs.getString("repeater"));
				sbList.add(sb);
			}
			if(sbList!=null&&sbList.size()>0){
				ae.setError("获取烟感成功");
				ae.setErrorCode(0);
				ae.setSmoke(sbList);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return ae;
	}

	public EnvironmentHistoryMsgEntity getHistoryData(String deviceMac,String timeStart,String timeEnd,int type){
		EnvironmentHistoryMsgEntity environmentHistoryMsgEntity=null;
		String sqlString="";
		switch(type){
		case 1://co2
			sqlString="select max(co2),min(co2) from environment where dateTime between ? and ? and arimac=?";
			break;
		case 2://pm2.5
			sqlString="select max(pm25),min(pm25) from environment where dateTime between ? and ? and arimac=?";
			break;
		case 3:// methanal
			sqlString="select max(methanal), min(methanal) from environment where dateTime between ? and ? and arimac=?";
			break;
		case 4:// humidity
			sqlString="select max(humidity), min(humidity) from environment where dateTime between ? and ? and arimac=?";
			break;
		case 5://temperature
			sqlString="select max(temperature), min(temperature) from environment where dateTime between ? and ? and arimac=?";
			break;
		default:
			
			break;
		}
		Connection connection=DBConnectionManager.getConnection();
		PreparedStatement ps=DBConnectionManager.prepare(connection,sqlString);
		ResultSet rs=null;
		try{
			ps.setString(1, timeStart);
			ps.setString(2, timeEnd);
			ps.setString(3, deviceMac);
			System.out.println(sqlString);
			rs=ps.executeQuery();
			
			if(rs.first()){
				if(rs.getString(1)==null||rs.getString(2)==null)
					return null;
					environmentHistoryMsgEntity=new EnvironmentHistoryMsgEntity();
				
				
				switch(type){
				case 1://co2
					environmentHistoryMsgEntity.setMax(String.valueOf(rs.getInt(1)));
					environmentHistoryMsgEntity.setMin(String.valueOf(rs.getInt(2)));
					break;
				case 2://pm2.5
					environmentHistoryMsgEntity.setMax(String.valueOf(rs.getInt(1)));
					environmentHistoryMsgEntity.setMin(String.valueOf(rs.getInt(2)));
					break;
				case 3:// methanal
					environmentHistoryMsgEntity.setMax(String.valueOf(rs.getDouble(1)));
					environmentHistoryMsgEntity.setMin(String.valueOf(rs.getDouble(2)));
					break;
				case 4:// humidity
					environmentHistoryMsgEntity.setMax(String.valueOf(rs.getInt(1))+"%");
					environmentHistoryMsgEntity.setMin(String.valueOf(rs.getInt(2))+"%");
					break;
				case 5://temperature
					environmentHistoryMsgEntity.setMax(String.valueOf(rs.getDouble(1))+"°C");
					environmentHistoryMsgEntity.setMin(String.valueOf(rs.getDouble(2))+"°C");
					break;
				default:
					
					break;
				}
				
				String []timeStartStrings=timeStart.split("-");
				StringBuffer timeStartStringBuffer=new StringBuffer();
				
				timeStartStringBuffer.append(timeStartStrings[1]);
				timeStartStringBuffer.append("月");
				timeStartStringBuffer.append(timeStartStrings[2]);
				timeStartStringBuffer.append("日");
				environmentHistoryMsgEntity.setTime(timeStartStringBuffer.toString());
				if(type==3){
					double mathanal=rs.getDouble(2);
					
					if(mathanal<=0.02)
						//mathanalQuality=1;
						environmentHistoryMsgEntity.setQuality(1);
					else if(mathanal<=0.03)
						//mathanalQuality=2;
						environmentHistoryMsgEntity.setQuality(2);
					else if(mathanal<=0.8)
						//mathanalQuality=3;
						environmentHistoryMsgEntity.setQuality(3);
					else 
						//mathanalQuality=4;
						environmentHistoryMsgEntity.setQuality(4);
						
				}
				else if(type==2){
					double pm25=rs.getDouble(2);
					environmentHistoryMsgEntity.setQuality(1);
					if(pm25<=70)
						//pm25Quality=1;
						environmentHistoryMsgEntity.setQuality(1);
					else if(pm25<=100)
						//pm25Quality=2;
						environmentHistoryMsgEntity.setQuality(2);
					else if(pm25<=180)
						//pm25Quality=3;
						environmentHistoryMsgEntity.setQuality(3);
					else 
						//pm25Quality=4;
						environmentHistoryMsgEntity.setQuality(4);
				}
			}
			
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(connection);
		}
		
		return environmentHistoryMsgEntity;
	}

}
