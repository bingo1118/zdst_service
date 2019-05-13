package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AckDeviceBean;
import com.cloudfire.entity.AckToSoundAndDevice;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class PublicUtilsImpl implements PublicUtils {

	@Override
	public List<SmokeBean> selectDevByRep(String repeater) {
		String sql = "SELECT mac,deviceType FROM smoke WHERE repeater = ?"	;
		List<SmokeBean> smokeList = new ArrayList<SmokeBean>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, repeater);
			rs = ps.executeQuery();
			while(rs.next()){
				SmokeBean smokeBean = new SmokeBean();
				smokeBean.setMac(rs.getString("mac"));
				int dev = rs.getInt("deviceType");
				if(dev==1){
					smokeBean.setName("烟感报警器");
				}else if(dev == 2){
					smokeBean.setName("燃气报警器");
				}else if(dev == 5){
					smokeBean.setName("电气火灾报警器");
				}else if(dev == 7){
					smokeBean.setName("声光报警器");
				}else if(dev == 8){
					smokeBean.setName("手动报警器");
				}else if(dev == 9){
					smokeBean.setName("三江设备");
				}else if((dev == 10)||(dev == 125)){
					smokeBean.setName("水压");
				}else if(dev == 11){
					smokeBean.setName("红外线");
				}else if(dev == 12){
					smokeBean.setName("门磁");
				}else if(dev == 13){
					smokeBean.setName("环境探测器");
				}else if(dev == 14){
					smokeBean.setName("GPRS");
				}else if(dev == 15){
					smokeBean.setName("水浸");
				}else if(dev == 20){
					smokeBean.setName("无线模块");
				}else if(dev == 18){
					smokeBean.setName("喷淋");
				}else{
					smokeBean.setName("未定义设备型号");
				}
				smokeBean.setDeviceType(dev);
				smokeList.add(smokeBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smokeList;
	}
	
	@Override
	public List<AckToSoundAndDevice> selectAckBySAD(String repeaterMac){
		AckToSoundAndDevice ackTosad = new AckToSoundAndDevice();
		List<AckToSoundAndDevice> acktlist = new ArrayList<AckToSoundAndDevice>();
		List<SmokeBean> smokeList =  selectDevByRep(repeaterMac);
		List<AckDeviceBean> ackList = selectAckToDevByRep(repeaterMac);
		ackTosad.setAckList(ackList);
		ackTosad.setSmokeList(smokeList);
		acktlist.add(ackTosad);
		return acktlist;
	}
	
	@Override
	public void saveAckBySAD(String repeaterMac,String[] devList,String[] ackList){
		if(devList!=null&&ackList!=null&&devList.length>0&&ackList.length>0){
			for (int i = 0; i < ackList.length; i++) {
				deleteAckBySoundMac(ackList[i]);
				for (int j = 0; j < devList.length; j++) {
					String devMac1 = devList[j].substring(1);
					String devType = devList[j].substring(0,1);
					saveInfo(repeaterMac,devMac1,ackList[i],devType);
				}
			}
		}
	}

	@Override
	public List<AckDeviceBean> selectAckToDevByRep(String repeater) {
		String sql = "SELECT deviceMac,soundMac,repeaterMac from ackDevice where repeaterMac = ? group by soundMac";
		List<AckDeviceBean> ackList = new ArrayList<AckDeviceBean>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, repeater);
			rs = ps.executeQuery();
			while(rs.next()){
				AckDeviceBean ackBean = new AckDeviceBean();
				Map<String,List<AckDeviceBean>> ackMap = new HashMap<String,List<AckDeviceBean>>();
				List<AckDeviceBean> slist = selectDevBean(rs.getString("soundMac"));
				ackMap.put(rs.getString("soundMac"), slist);
				ackBean.setAckMap(ackMap);
				ackList.add(ackBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ackList;
	}
	
	@Override
	public void deleteAckBySoundMac(String soundMac){
		String sql = "DELETE from ackDevice where soundMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, soundMac);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	@Override
	public List<AckDeviceBean> selectDevBean(String soundMac){
		String sql = "SELECT deviceMac,soundMac,repeaterMac,deviceType from ackDevice where soundMac = ?";
		List<AckDeviceBean> ackList = new ArrayList<AckDeviceBean>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, soundMac);
			rs = ps.executeQuery();
			while(rs.next()){
				AckDeviceBean adb = new AckDeviceBean();
				int dev = rs.getInt("deviceType");
				if(dev==1){
					adb.setDeviceType("烟感报警器");
				}else if(dev == 2){
					adb.setDeviceType("燃气报警器");
				}else if(dev == 5){
					adb.setDeviceType("电气火灾报警器");
				}else if(dev == 7){
					adb.setDeviceType("声光报警器");
				}else if(dev == 8){
					adb.setDeviceType("手动报警器");
				}else if(dev == 9){
					adb.setDeviceType("三江设备");
				}else if(dev == 10){
					adb.setDeviceType("水压");
				}else if(dev == 11){
					adb.setDeviceType("红外线");
				}else if(dev == 12){
					adb.setDeviceType("门磁");
				}else if(dev == 13){
					adb.setDeviceType("环境探测器");
				}else if(dev == 14){
					adb.setDeviceType("GRPS");
				}else if(dev == 15){
					adb.setDeviceType("水浸");
				}else if(dev == 18){
					adb.setDeviceType("喷淋");
				}else{
					adb.setDeviceType("未定义设备型号");
				}
				adb.setDeviceMac(rs.getString("deviceMac"));
				ackList.add(adb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ackList;
	}
	
	public int saveInfo(String repeaterMac,String devMac,String ackMac,String devType){
		int result = 0;
		String sql = "insert into ackDevice (deviceMac,soundMac,repeaterMac,deviceType) values (?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, devMac);
			ps.setString(2,ackMac);
			ps.setString(3, repeaterMac);
			ps.setString(4, devType);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public int bindSmokeByUserName(String userName, String[] smokes) {
		int result = 0;
		for (int i = 0; i < smokes.length; i++) {
			result =  addBindSmoke(userName,smokes[i]);
		}
		return result;
	}
	
	@Override
	public int bindCameraByUserName(String userName, String[] smokes) {
		int result = 0;
		for (int i = 0; i < smokes.length; i++) {
			result =  addBindCamera(userName,smokes[i]);
		}
		return result;
	}
	
	@Override
	public int bindRepeaterByUserName(String userName, String[] smokes) {
		int result = 0;
		for (int i = 0; i < smokes.length; i++) {
			result =  addBindRepeater(userName,smokes[i]);
		}
		return result;
	}
	
	public int addBindRepeater(String userName,String repeater){
		String sql = "INSERT INTO useridrepeaterid VALUES(?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int result = 0;
		try {
			ps.setString(1, userName);
			ps.setString(2, repeater);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public int addBindCamera(String userName,String smokeMac){
		String sql = "INSERT INTO useridcameraid VALUES(?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int result = 0;
		try {
			ps.setString(1, userName);
			ps.setString(2, smokeMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public int addBindSmoke(String userName,String smokeMac){
		String sql = "INSERT INTO useridsmoke VALUES(?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int result = 0;
		try {
			ps.setString(1, userName);
			ps.setString(2, smokeMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public int updateDeviceMac(String deviceMac) {
		int result = 0;
		String sqlstr = "update smoke set netState = 1,netState2= 1, heartime = ? where mac = ?";
		String stateTime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, stateTime);
			ps.setString(2, deviceMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public int updateDeviceOnlineState(String deviceMac,int state,long time) {
		int result = 0;
		String sqlstr = "update smoke set netState = ?,heartime = ? where mac = ?";
		String stateTime = GetTime.getTimeByLong(time);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setInt(1, state);
			ps.setString(2, stateTime);
			ps.setString(3, deviceMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public int updateVoltageState(String deviceMac) {
		int result = 0;
		String sqlstr = "update smoke set voltage = 0,netState = 1,heartime = ?  where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, GetTime.ConvertTimeByLong());
			ps.setString(2, deviceMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public int updateVoltageState(String deviceMac,int voltage) {
		int result = 0;
		String sqlstr = "update smoke set voltage = ? where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setInt(1, voltage);
			ps.setString(2, deviceMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public int updateDeviceMac(String deviceMac, int devState) {
		int result = 0;
		String sqlstr = "update smoke set netState = 1,heartime = ?,electrState=? where mac = ?";
		String stateTime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, stateTime);
			ps.setInt(2, devState);
			ps.setString(3, deviceMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public int updateDeviceMac(String deviceMac,String rssi) {
		int result = 0;
		String sqlstr = "update smoke set netState = 1,heartime = ?,netState2 = 1 ,rssivalue = ? where mac = ?";
		String stateTime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, stateTime);
			ps.setString(2, rssi);
			ps.setString(3, deviceMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public void deleteSixData() {
		String[] sqls = Constant.sqlstr;
		for (int i = 0; i < sqls.length; i++) {
			del(sqls[i]);
		}
	}
	
	private void del(String sql){
		String dataTime = Utils.getSixDate();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		System.out.println(sql+"==================");
		try {
			ps.setString(1, dataTime);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public List<String> selectCamera(String username){
		List<String> listc = new ArrayList<String>();
		String sql = "SELECT cameraId from useridcameraid where userid = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, username);
			rs = ps.executeQuery();
			while(rs.next()){
				listc.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return listc;
	}
	
	public int delCamera(String username,String devMac){
		int result = 0;
		String sql = "DELETE from useridcameraid where userid = ? and cameraId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, username);
			ps.setString(2, devMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public List<String> selectRepeater(String username){
		List<String> listc = new ArrayList<String>();
		String sql = "SELECT repeaterMac from useridrepeaterid where userid = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, username);
			rs = ps.executeQuery();
			while(rs.next()){
				listc.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return listc;
	}
	
	public int delRepeater(String username,String devMac){
		int result = 0;
		String sql = "DELETE from useridrepeaterid where userid = ? and repeaterMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, username);
			ps.setString(2, devMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public List<String> selectSmoke(String username){
		List<String> listc = new ArrayList<String>();
		String sql = "SELECT smokeMac from useridSmoke where userid = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, username);
			rs = ps.executeQuery();
			while(rs.next()){
				listc.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return listc;
	}
	
	public int delSmoke(String username,String devMac){
		int result = 0;
		String sql = "DELETE from useridsmoke where userid = ? and smokeMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, username);
			ps.setString(2, devMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public int bindSmokeSoundRepeater(String repeaterMac, String soundMac,
			String devMac) {
		String sqlstr = "INSERT INTO ackdevice(deviceMac,soundMac,repeaterMac,deviceType)VALUES(?,?,?,1)";
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, devMac);
			ps.setString(2,soundMac);
			ps.setString(3, repeaterMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}

	@Override
	public List<AckDeviceBean> selectFaultInfo(String repeaterMac) {
		String sqlstr = " SELECT deviceMac,soundMac,repeaterMac from ackdevice where repeaterMac = ?";
		List<AckDeviceBean> devBeans = new ArrayList<AckDeviceBean>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				AckDeviceBean dev = new AckDeviceBean();
				dev.setDeviceMac(rs.getString("deviceMac"));
				dev.setSoundMac(rs.getString("soundMac"));
				dev.setRepeaterMac(rs.getString("repeaterMac"));
				devBeans.add(dev);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return devBeans;
	}

	@Override
	public int delFaultInfo(String repeaterMac, String soundMac, String devMac) {
		String sqlstr = "DELETE from ackdevice where deviceMac = ? and soundMac = ? and repeaterMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		int result = 0;
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, devMac);
			ps.setString(2, soundMac);
			ps.setString(3, repeaterMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public boolean ifOverTime(String times) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int updateDevBattery(String deviceMac, String lowVoltage) {
		int result = 0;
		String sqlstr = "update smoke set netState = 1,heartime = ?,lowVoltage = ? where mac = ?";
		String stateTime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		
		
		//获取设备的低电量状态
//		int powerState = getVoltageState(deviceMac);
// 		if(powerState == 1) { //低电量
// 			if (Integer.parseInt(lowVoltage) > 15) {
// 				sqlstr =  "update smoke set netState = 1,heartime = ?,lowVoltage = ? ,voltage = 0 where mac = ?";
// 				
// 				FromRepeaterAlarmDao mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
//				mFromRepeaterAlarmDao.addAlarmMsg(deviceMac, "", 194 , 1);  //将报警信息添加到alarm表中
//				 
//				GetPushUserIdDao mGetPushUserIdDao = new GetPushUserIdDaoImpl();
//				List<String> userList = mGetPushUserIdDao.getAllUser(deviceMac);
//				List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(deviceMac); //获取useridsmoke表里的用户用于短信通知
//				if(userList!=null&&userList.size()>0){
//					//开启推送消息的线程  手机推送
//					PushAlarmMsgDao mPushAlarmMsgDao = new  PushAlarmMsgDaoImpl();
//					Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
//					PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(deviceMac,194);
//					new MyThread(push,userList,iosMap).start();  
//					//网页推送开启
//					
//					new WebThread(userList,deviceMac).start();
//					//短信推送
//					if (txtUserList != null &&txtUserList.size()>0) {
//						new TxtThread(txtUserList,deviceMac).start();        //短信通知的线程
//					}
//				}
//			}
// 		}
		
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, stateTime);
			ps.setString(2, lowVoltage);
			ps.setString(3, deviceMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public int getVoltageState(String mac){
		String sqlstr = "select voltage from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int voltageState = 0;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				voltageState = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return voltageState;
	}
	
	@Override
	public int updateElectricSwitch(String deviceMac, String elecSwitch) {
		int result = 0;
		String sqlstr = "update smoke set netState = 1,heartime = ?,electrState = ? where mac = ?";
		String stateTime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, stateTime);
			ps.setString(2, elecSwitch);
			ps.setString(3, deviceMac);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
}
