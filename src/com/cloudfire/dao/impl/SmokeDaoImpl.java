package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SmokeInfo;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class SmokeDaoImpl implements SmokeDao{

	@Override
	public List<SmokeBean> getSmokesByAreaid(int areaid) {
		List<SmokeBean> lstSmoke = null;
		String sql = "select mac,named from smoke where areaid =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs=null;
		try {
			ps.setInt(1, areaid);
			rs = ps.executeQuery();
			while (rs.next()) {
				if(lstSmoke == null)
					lstSmoke = new ArrayList<SmokeBean>();
				SmokeBean smoke = new SmokeBean();
				smoke.setMac(rs.getString("mac"));
				smoke.setName(rs.getString("named"));
				lstSmoke.add(smoke);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return lstSmoke;
	}

	@Override
	public SmokeBean getSmokeByMac(String mac) {
		SmokeBean smoke=null;
		String sql = "select s.mac,s.named,s.address,s.netState,s.areaId,a.area from smoke s,areaidarea a where s.areaId = a.areaId and s.mac =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs=null;
		try {
			ps.setString(1, mac);
			rs=ps.executeQuery();
			if(rs.next()){
				smoke = new SmokeBean();
				smoke.setName(rs.getString("named"));
				smoke.setAddress(rs.getString("address"));
				smoke.setMac(mac);
				smoke.setNetState(rs.getInt("netState"));
				smoke.setAlarmName(rs.getString("area"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smoke;
	}
	
	@Override
	public SmokeBean getSmokeByMac3(String mac) {
		SmokeBean smoke=null;
		String sql = "select s.mac,s.named,s.address,s.netState,s.areaId,a.area,a.parentId,s.deviceType,d.deviceName from smoke s,areaidarea a,devices d where s.areaId = a.areaId and s.deviceType = d.id and s.mac =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs=null;
		try {
			ps.setString(1, mac);
			rs=ps.executeQuery();
			if(rs.next()){
				smoke = new SmokeBean();
				smoke.setName(rs.getString("named"));
				smoke.setAddress(rs.getString("address"));
				smoke.setMac(mac);
				smoke.setNetState(rs.getInt("netState"));
				smoke.setAlarmName(rs.getString("area"));
				smoke.setDeviceType(rs.getInt("deviceType"));
				smoke.setParentId(rs.getInt("parentId"));
				smoke.setDeviceTypeName(rs.getString("deviceName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smoke;
	}
	
	@Override
	public SmokeBean getSmokeByMac2(String mac) {
		SmokeBean smoke=null;
		String sql = "select s.mac,s.named,s.address,s.netState,s.areaId,a.area,s.deviceType from smoke s,areaidarea a where s.areaId = a.areaId and s.mac =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs=null;
		try {
			ps.setString(1, mac);
			rs=ps.executeQuery();
			if(rs.next()){
				smoke = new SmokeBean();
				smoke.setName(rs.getString("named"));
				smoke.setAddress(rs.getString("address"));
				smoke.setMac(mac);
				smoke.setNetState(rs.getInt("netState"));
				smoke.setAlarmName(rs.getString("area"));
				smoke.setAreaId(rs.getInt("areaId"));
				/*smoke.setInterval1(rs.getInt("interval1"));
				smoke.setInterval2(rs.getInt("interval2"));*/
				int deviceType = rs.getInt("deviceType");
				smoke.setDeviceType(deviceType);
				/*smoke.setOffTime(rs.getInt("offTime"));*/
				/*smoke.setAdjusted(rs.getString("adjusted"));*/
				WaterInfoDao wid = new WaterInfoDaoImpl();
				smoke.setHighTemp(wid.getGage(mac, 308));
				smoke.setLowTemp(wid.getGage(mac, 307));
				smoke.setHighHumi(wid.getGage(mac, 408));
				smoke.setLowHumi(wid.getGage(mac, 407));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smoke;
	}

	@Override
	public int getDeviceTypeByMac(String mac) {
		int deviceType = 1;
		String sql = "select deviceType  from smoke where mac = '"+mac+"'";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs=null;
		try {
			rs = ps.executeQuery();
			if(rs.next()) {
				deviceType = rs.getInt("deviceType");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return deviceType;
	}

	@Override
	public List<Integer> getDevices(String userId, int privilege) {
		String sql = "";
		List<Integer> lstDevice = null;
		switch (privilege) {
		case 1:
			sql = "select distinct deviceType from smoke where mac in (select smokeMac  from useridsmoke where userid='"+userId+"') ";
			break;
		case 3:
			sql = "select distinct deviceType from smoke where areaId in (select areaId from useridareaId where userid ='" +userId+"')";
			break;
		case 4:
			sql = "select distinct deviceType from smoke where areaId in (select areaid from areaidarea )";
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs=null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				if(lstDevice == null) 
					lstDevice = new ArrayList<Integer>();
				lstDevice.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstDevice;
	}

	/*@Override
	public String getHeartTimeByMac(String mac) {
		String sql = "select heartime from smoke where mac = " +mac;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs=null;
		String heartTime ="";
		try {
			rs = ps.executeQuery();
			if (rs.next()){
				heartTime = rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return heartTime;
	}*/
	
	public String getHeartTimeByMac(String mac) {
		String sql = "select heartime from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String heartTime = null;
		try {
			if (Utils.isNullStr(mac)) {
				ps.setString(1, mac);
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				heartTime = rs.getString(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return heartTime;
	}
	
	//更新设备的心跳时间
	@Override
	public boolean updateHeartTime(String mac){ 
		boolean result = false;
		String now = GetTime.getTimeByLong(System.currentTimeMillis());
		String sql = "update smoke set heartime = ? where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, now);
			ps.setString(2, mac);
			int num = ps.executeUpdate();
			if (num > 0) {
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result; 
	}
	
	@Override
	public boolean deleteDevFromSmoke(String smokeMac) {
		String sql = "delete from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = -1;
		boolean result = false;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeUpdate();
			if(rs>0){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public boolean addOrUpdateSmoke(SmokeInfo s) {
		String sql = "replace into customerSmoke(named,mac,address,areaId,netState,time,repeater,deviceType,addUserId,ifAlarm,heartime,ifShow) value(?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		boolean bool = false;
		try {
			ps.setString(1, s.getNamed());
			ps.setString(2, s.getMac());
			ps.setString(3, s.getAddress());
			ps.setInt(4, s.getAreaId());
			ps.setInt(5, s.getNetState());
			ps.setString(6, s.getTime());
			ps.setString(7, s.getRepeater());
			ps.setInt(8, s.getDeviceType());
			ps.setString(9, s.getAddUserId());
			ps.setInt(10, s.getIfAlarm());
			ps.setString(11, s.getHeartime());
			ps.setInt(12, s.getIfShow());
			int rs = ps.executeUpdate();
			if(rs>0){
				bool = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return bool;
	}

	@Override
	public int queryCountCustomerSmoke() {
		String sql = "select count(mac) as totalCount from customerSmoke where ifshow = 1";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		int totalCount = 0;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				totalCount =rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return totalCount;
	}

	@Override
	public int selectIfShow() {
		String sql = "select distinct ifShow from customerSmoke";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		int ifShow = 0;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				ifShow = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ifShow;
	}

	@Override
	public boolean updateIfShow(int ifShow) {
		String sql = "update customerSmoke set ifShow = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		int rs = 0;
		boolean bool = false;
		try {
			ps.setInt(1, ifShow);
			rs = ps.executeUpdate();
			if(rs>0){
				bool = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return bool;
	}

	@Override
	public List<SmokeBean> getSmokesByBuilding(String buildingname) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.named,d.deviceName,s.netState,s.address,s.mac,s.longitude,s.latitude,s.principal1,s.principal1Phone,s.principal2,s.principal2Phone,s.ifAlarm,a.area,s.deviceType");
		sb.append(" FROM smoke as s,areaidarea as a,devices d");
		sb.append(" WHERE s.areaId=a.areaId and s.deviceType=d.id ");
		sb.append(" and s.floors =" + buildingname);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sb.toString());
		ResultSet rs = null;
		List<SmokeBean> lstSmoke = new ArrayList<SmokeBean>();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				SmokeBean sbe = new SmokeBean();
				sbe.setName(rs.getString(1));
				sbe.setPlaceeAddress(rs.getString(2));
				int netState = rs.getInt(3);
				int ifAlarm = rs.getInt("ifAlarm");
				int state = 0;
				if (netState == 1) {
					if (ifAlarm == 1){
						state = 1;
					}
				} else {
					if (ifAlarm == 0){
						state = 2;
					} else {
						state = 3;
					}
				}
				sbe.setNetState(state); // 0 在线不报警 1在线报警 2离线不报警 3离线报警
				sbe.setAreaName(rs.getString("area"));
				sbe.setAddress(rs.getString(4));
				sbe.setMac(rs.getString(5));
				sbe.setLongitude(rs.getString(6));
				sbe.setLatitude(rs.getString(7));
				sbe.setPrincipal1(rs.getString(8));
				sbe.setPrincipal1Phone(rs.getString(9));
				sbe.setPrincipal2(rs.getString(10));
				sbe.setPrincipal2Phone(rs.getString(11));
				lstSmoke.add(sbe);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstSmoke;
	}
	
	public static void main(String[] args) {
//		15888212724  13958913232  18157764186 15381576756 13736343510 13858721682 13695756720 13587831853 15888472541
		SmokeDaoImpl sd = new SmokeDaoImpl();
		sd.bindSmokeByPhone("15888472541");
	}
	
	//按照负责人一的电话批量绑定给用户批量绑定烟感。
	public void bindSmokeByPhone(String principal1Phone){ //2264
		List<TempEntity> smokes = new ArrayList<TempEntity>();
//		List<String> smokes =new ArrayList<String>();
		String sql = "select mac ,principal1Phone from smoke where areaid in (select areaid from areaidarea where parentId = 168 ) and principal1Phone !=''";
//		String sql = "select mac  from smoke where named like  '%七都%' ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
//			ps.setString(1, principal1Phone);
			rs = ps.executeQuery();
			while (rs.next()){
				TempEntity te = new TempEntity();
				te.setMac(rs.getString(1));
				te.setPrincipal1Phone(rs.getString(2));
				smokes.add(te);
//				smokes.add(rs.getString(1));
			}
			
			conn.setAutoCommit(false);
			sql = "replace into useridsmoke(userid,smokeMac) values(?,?)";
			ps = DBConnectionManager.prepare(conn,sql);
			for (TempEntity tempEntity : smokes) {
				ps.setString(1, tempEntity.getPrincipal1Phone());
				ps.setString(2, tempEntity.getMac());
				ps.addBatch();
			}
//			for(String mac:smokes){
//				ps.setString(1, "15068269119");
//				ps.setString(2, mac);
//				ps.addBatch();
//			}
			int[] executeBatch = ps.executeBatch();
			for (int i=0;i<executeBatch.length;i++) {
				if (executeBatch[i]==0)
					System.out.print(i+",");
			}
			conn.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}
	
	//批量注册用户
	public void registerUsers(List<String> userIds){
		String sql = "insert into user(userid,pwd,privId,privilege) values(?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			for (String userId : userIds) {
				if(!existUser(userId)) {
					ps.setString(1, userId);
					ps.setString(2, "123456");
					ps.setString(3,"3");
					ps.setString(4, "1");
					ps.addBatch();
				}
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	
	private boolean existUser(String userId) {
		boolean exist = false;
		String sql = "select userid from user where userid = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				exist = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return exist;
	}


	class TempEntity{
		private String mac;
		private String principal1Phone;
		public String getMac() {
			return mac;
		}
		public void setMac(String mac) {
			this.mac = mac;
		}
		public String getPrincipal1Phone() {
			return principal1Phone;
		}
		public void setPrincipal1Phone(String principal1Phone) {
			this.principal1Phone = principal1Phone;
		}
		
	}


	public static boolean ifPowerOff(String mac) {
		boolean poweroff = false;
		String sql = "select voltage from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (1==rs.getInt(1)){
					poweroff = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return poweroff;
	}
	
	/**
	 * 更新设备的电源状态 0 满电 1 低电
	 * @param mac
	 * @param powerState
	 */
	public static void setPowerState(String mac,int powerState){
		String sql = "update smoke set voltage = ? where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setInt(1, powerState);
			ps.setString(2, mac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	@Override
	public String getDeviceTypeNameByType(int deviceType) {
		String sql = "select deviceName from devices where id=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs =null;
		String deviceTypeName = "";
		try {
			ps.setInt(1, deviceType);
			rs = ps.executeQuery();
			if(rs.next()){
				deviceTypeName= rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return deviceTypeName;
	}

	@Override
	public String getAlarmNameByAlarmType(int alarmtype) {
		String sql = "select alarmName from alarmType where alarmId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs =null;
		String alarmTypeName = "";
		try {
			ps.setInt(1, alarmtype);
			rs = ps.executeQuery();
			if(rs.next()){
				alarmTypeName= rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return alarmTypeName;
	}

	@Override
	public int getDeviceMiniType(int deviceType) {
		String sql = "select deviceminitype from devicetypetrans where deviceType=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs =null;
		int deviceMiniType = 4;
		try {
			ps.setInt(1, deviceType);
			rs = ps.executeQuery();
			if(rs.next()){
				deviceMiniType= rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return deviceMiniType;
	}
}
