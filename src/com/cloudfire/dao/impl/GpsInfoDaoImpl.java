package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.dao.GpsInfoDao;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.GpsEntityBean;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.query.GpsQuery;
import com.cloudfire.push.MyThread;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class GpsInfoDaoImpl implements GpsInfoDao {

	@Override
	public void saveGpsInfo(GpsEntityBean gpsBean) {
		
		boolean bool = ifExitGpsByGps(gpsBean);
		if(bool){
			String sql = "INSERT INTO gpsinfo(devMac,longitude,latitude,dataTime) VALUES(?,?,?,?)";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
			try {
				ps.setString(1, gpsBean.getDevMac());
				ps.setString(2, gpsBean.getLongitude());
				ps.setString(3, gpsBean.getLatitude());
				ps.setString(4, gpsBean.getDataTime());
				ps.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
		}
	}
	
	public boolean ifExitGpsByGps(GpsEntityBean gpsBean){
		boolean bool = true;
		String sql = "SELECT devMac from gpsinfo WHERE devMac = ? and longitude=? and latitude=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, gpsBean.getDevMac());
			ps.setString(2, gpsBean.getLongitude());
			ps.setString(3, gpsBean.getLatitude());
			rs = ps.executeQuery();
			if(rs.next()){
				bool = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return bool;
	}

	@Override
	public void sageGpsEqument(GpsEntityBean gpsBean) {
		String sql = "";
		boolean bool = ifExitGps(gpsBean.getDevMac());
		if(bool){
			sql = "update smoke set longitude = ?,latitude = ?,time = ?,netState = ?,deviceType=? where mac = ?";
		}else{
			sql = "insert into smoke(longitude,latitude,time,netState,deviceType,mac) values(?,?,?,?,?,?)";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, gpsBean.getLongitude());
			ps.setString(2, gpsBean.getLatitude());
			ps.setString(3, gpsBean.getDataTime());
			ps.setInt(4, gpsBean.getGpsState());
			ps.setInt(5, 14);
			ps.setString(6, gpsBean.getDevMac());
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}
	
	public boolean ifExitGps(String gpsMac){
		String sql = "SELECT mac FROM smoke WHERE mac = ?";
		boolean result = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, gpsMac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = true;
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
	public static void main(String[] args) {
		String dataTime = "2019-01-01 15:00:00";
		System.out.println(StringUtils.isBlank(dataTime));
		System.out.println(System.currentTimeMillis()-Utils.getTimeByStr(dataTime)>1000*60*60*24);
		System.out.println((StringUtils.isBlank(dataTime))||(System.currentTimeMillis()-Utils.getTimeByStr(dataTime)>1000*60*60*24));
		
	}
	@Override
	public void ifLossGpsEqument() {
		String sqlstr = "UPDATE smoke SET netState = 0,netState2 = 0  WHERE mac = ?";
		List<SmokeBean> gpsList = getSmokeGpsInfo();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			conn.setAutoCommit(false);
			for(SmokeBean devBean:gpsList){
				if(devBean.getDeviceType()==31||devBean.getDeviceType()==41||devBean.getDeviceType()==42||devBean.getDeviceType()==55||devBean.getDeviceType()==58||devBean.getDeviceType()==61||devBean.getDeviceType()==69||devBean.getDeviceType()==70){
					String dataTime =devBean.getHeartime();
					if((StringUtils.isBlank(dataTime))||(System.currentTimeMillis()-Utils.getTimeByStr(dataTime)>1000*60*60*24)){
						System.out.println("HeartTimebeforeAlarm: "+dataTime+"--AlarmTime: "+GetTime.getTimeByLong(System.currentTimeMillis())); //输出电气设备报警前心跳和报警时间
						
						ps.setString(1, devBean.getMac());
						ps.addBatch();
					}
				}else{
					String dataTime =devBean.getHeartime();
					if((StringUtils.isBlank(dataTime))||(System.currentTimeMillis()-Utils.getTimeByStr(dataTime)>1000*60*40)){
						System.out.println("HeartTimebeforeAlarm: "+dataTime+"--AlarmTime: "+GetTime.getTimeByLong(System.currentTimeMillis())); //输出电气设备报警前心跳和报警时间
						
						ps.setString(1, devBean.getMac());
						ps.addBatch();
						
						if(devBean.getDeviceType()==5&&StringUtils.isNotBlank(dataTime)){
//							pushOffLineToApp(devBean.getMac(),51,devBean.getRepeater());//@@12.4
							Utils.saveChangehistory("6602",devBean.getMac(),2);
						}
					}
				}
			}
			ps.executeBatch();
			conn.commit();
		} catch (Exception e) {
			if(conn!=null){
               try {
            	   conn.rollback();
               } catch (Exception e1) {
                   e1.printStackTrace();
               }
			}   
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	//@@推送分闸到手机
	private void pushOffLineToApp(String electricMac,int alarmType,String repeatMac) {
		PushAlarmMsgDao mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmType);
		if(pushAlarm==null){
			return;
		}
		FromRepeaterAlarmDaoImpl mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeatMac, alarmType,alarmType);
		pushAlarm.setAlarmFamily(alarmType+"");
		pushAlarm.setAlarmType("0");
		GetPushUserIdDaoImpl mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
		if(users!=null&&users.size()>0){
			Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
			new MyThread(pushAlarm,users,iosMap).start();
		}
		
	}

	public List<SmokeBean> getSmokeGpsInfo(){
		List<SmokeBean> gpsList = new ArrayList<SmokeBean>(); //添加127，128 DTU水压探测器和水位探测器
		String sql = "SELECT mac,longitude,latitude,netState,TIME,heartime,repeater,deviceType FROM smoke WHERE deviceType in(5,14,16,22,23,31,35,41,42,46,51,53,55,56,58,59,61,69,70,72,73,75,76,77,78,79,119,124,125) and netState=1 ";

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				SmokeBean gpsBean = new SmokeBean();
				gpsBean.setMac(rs.getString("mac"));
				gpsBean.setLongitude(rs.getString("longitude"));
				gpsBean.setLatitude(rs.getString("latitude"));
				gpsBean.setNetState(rs.getInt("netState"));
				gpsBean.setAddTime(rs.getString("time"));
				gpsBean.setHeartime(rs.getString("heartime"));
				gpsBean.setRepeater(rs.getString("repeater"));//@@12.4
				gpsBean.setDeviceType(rs.getInt("deviceType"));
				gpsList.add(gpsBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return gpsList;
	}
	
	@Override
	public List<GpsEntityBean> getGpsEqument() {
		List<GpsEntityBean> gpsList = new ArrayList<GpsEntityBean>();
		String sql = "SELECT gpsmac,gpslongitude,gpslatitude,gpsstate,gpsname,gpstime,gpsmemo FROM gpsequipment GROUP BY gpsmac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				GpsEntityBean gpsBean = new GpsEntityBean();
				gpsBean.setDevMac(rs.getString("gpsmac"));
				gpsBean.setLongitude(rs.getString("gpslongitude"));
				gpsBean.setLatitude(rs.getString("gpslatitude"));
				gpsBean.setGpsState(rs.getInt("gpsstate"));
				gpsBean.setNamed(rs.getString("gpsname"));
				gpsBean.setDataTime(rs.getString("gpstime"));
				gpsBean.setMemos(rs.getString("gpsmemo"));
				gpsList.add(gpsBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return gpsList;
	}

	@Override
	public List<ProofGasEntity> getGasRecord(GpsQuery query) {
		String limit = " limit " + query.getStartRow() + "," + query.getPageSize();
		String sql  = "select proofGasType,proofGasMmol,proofGasTemp,proofGasModel,proofGasUnit,proofGasState,proofGasTime from g_proofgas where proofGasMac = ?  ";
		if (query.getStartTime()!=null && query.getStartTime().length() > 0) {
			sql += " AND proofGasTime >'"+ query.getStartTime()+" 00:00:01'";
		}
		if (query.getEndTime()!=null && query.getEndTime().length() > 0) {
			sql += " AND proofGasTime <'"+ query.getEndTime()+" 23:59:59'";
		}
		if(query.getStatus()!=null && query.getStatus().length() > 0) {
			sql += "and proofGasState in (" + query.getStatus() + ") " ;
		}
		if(query.getStatus7020()!=null && query.getStatus7020().length() > 0) {
			sql += "and proofGasTemp in (" + query.getStatus7020() + ") " ;
		}
		sql += " order by id desc";
		sql += limit;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<ProofGasEntity> lstpge = new ArrayList<ProofGasEntity>();
		try {
			ps.setString(1, query.getGasMac());
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while(rs.next()){
				ProofGasEntity pge = new ProofGasEntity();
				pge.setRow(++row);
				pge.setProofGasMac(query.getGasMac());
				pge.setProofGasType(rs.getString(1));
				pge.setProofGasMmol(rs.getString(2));
				pge.setProofGasTemp(rs.getString(3));
				pge.setProofGasModel(rs.getString(4));
				pge.setProofGasUnit(rs.getString(5));
				switch(rs.getString(6)){
				//4\5\7\8\10
				case "4":
				case "5":
				case "7":
				case "8":
				case "10":
					pge.setProofGasState("报警");
					break;
				default:
					pge.setProofGasState("正常");
				}
				
				pge.setProofGasTime(rs.getString(7));
				lstpge.add(pge);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstpge;
	}

	@Override
	public int getGasRecordCount(GpsQuery query) {
		String sql  = "select count(id) total from g_proofgas where proofGasMac = ? ";
		if (query.getStartTime()!=null && query.getStartTime().length() > 0) {
			sql += " AND proofGasTime >'"+ query.getStartTime()+" 00:00:01'";
		}
		if (query.getEndTime()!=null && query.getEndTime().length() > 0) {
			sql += " AND proofGasTime <'"+ query.getEndTime()+" 23:59:59'";
		}
		if(query.getStatus()!=null && query.getStatus().length() > 0) {
			sql += "and proofGasState in (" + query.getStatus() + ")" ;
		}
		if(query.getStatus7020()!=null && query.getStatus7020().length() > 0) {
			sql += "and proofGasTemp in (" + query.getStatus7020() + ") " ;
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int total = 0;
		try {
			ps.setString(1, query.getGasMac());
			rs = ps.executeQuery();
			while(rs.next()){
				total = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return total;
	}
	
	
}
