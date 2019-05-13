package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.cloudfire.action.MsgOneNetEntity;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.ToolOneNetDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.OneNetBody;
import com.cloudfire.entity.OneNetEntity;
import com.cloudfire.entity.OneNetResponse;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.myservice.OneNetDeviceService;
import com.cloudfire.myservice.impl.OneNetDeviceServiceImpl;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IntegerTo16;

public class ToolOneNetDaoImpl implements ToolOneNetDao {
	
	public static void main(String[] args) {
		ToolOneNetDao tond = new ToolOneNetDaoImpl();
//		System.out.println(tond.insertOneNetDev("865820031003267", "865820031003267", "测试烟感OneNet"));
//		tond.addOneNetTable("1885665245e39", "1885665245e39", "4568228933");
//		byte[] b = {(byte)0xff};
//		System.out.println(b);
//		System.out.println(IntegerTo16.byteToInt2(b));
//		System.out.println(IntegerTo16.byteToInt3(b));
//		System.out.println(IntegerTo16.bytes2Hex(b));
	}

	@Override
	public String insertOneNetDev(String imei, String imsi,String title,String deviceType) {
		OneNetDeviceService onds = new OneNetDeviceServiceImpl();
		OneNetBody onb = new OneNetBody();
		Map<String,String> auth_info = new HashMap<String,String>();
		auth_info.put(imei, imsi);
		onb.setAuth_info(auth_info);
		onb.setTitle(title);
		onb.setProtocol("LWM2M");//必须
//		String[] tags = new String[6];
		String[] tags = {"china","mobile"};
		onb.setTags(tags);
		Map<String,Float> location = new HashMap<>();//必须
		location.put("lon", new Float(106));
		location.put("lat", new Float(29));
//		location.put("ele", new Float(370));
		onb.setLocation(location);
		onb.setObsv(true); //必须
		Map<String,String> other = new HashMap<>(); //必须
//		other.put("version", "1.0.0");
//		other.put("manu","china mobile");
		onb.setOther(other);
		OneNetResponse result =  onds.addDevice(onb,deviceType);
		if ("0".equals(result.getErrno())) {  //注册成功，添加到onenetimei表
			ToolOneNetDao tond = new ToolOneNetDaoImpl();
			tond.addOneNetTable(imei, imsi, result.getData().get("device_id"));
		}
		return result.getErrno();
	}

	@Override
	public OneNetEntity getOneNetValues(String jsonString) {
		OneNetEntity one = new OneNetEntity();
		MsgOneNetEntity mone = new MsgOneNetEntity();
		try {
			JSONObject json = new JSONObject(jsonString);
			one.setMsg_signature(json.getString("msg_signature"));
			one.setNonce(json.getString("nonce"));
			System.out.println(json.get("msg"));
			JSONObject jsonMsg = new JSONObject(json.getString("msg"));
			mone.setAt(jsonMsg.getString("at"));
			mone.setDev_id(jsonMsg.getString("dev_id"));
			String type = jsonMsg.getString("type"); 
			mone.setType(type);
			switch(type){ //区分上传的是数据消息还是设备消息
			case "1"://数据消息
				mone.setDs_id(jsonMsg.getString("ds_id"));
				mone.setValue(jsonMsg.getString("value"));
				break;
			case "2": //设备在线消息
				mone.setLogin_type(jsonMsg.getString("login_type"));
				mone.setStatus(jsonMsg.getString("status"));
				break;
			}
			one.setMsg(mone);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return one;
	}

	@Override
	public void addOneNetTable(String imei, String imsi, String devId) {
//		String sql = "INSERT into onenetimei VALUES(?,?,?,?)";
		String sql = "replace into onenetimei(imei,imsi,device_id,addTime) values(?,?,?,?)";
		String dataTime = GetTime.ConvertTimeByLong();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, imei);
			ps.setString(2, imsi);
			ps.setString(3, devId);
			ps.setString(4, dataTime);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public void updateDs(String imei,String ds_id) {
		String sql = "update onenetimei set ds_id = ? where imei = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, ds_id);
			ps.setString(2, imei);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}
	
	@Override
	public String getMacByDeviceId(String device_id){
		String mac ="";
		String sql = "select imei from onenetimei where device_id = ? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, device_id);
			rs = ps.executeQuery();
			if (rs.next()) {
				mac = rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return mac;
	}

	@Override
	public MsgOneNetEntity getMsgByDeviceId(String device_id) {
		String sql = "select imei,ds_id from onenetimei where device_id = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		MsgOneNetEntity mone = null;
		try {
			ps.setString(1, device_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				mone = new MsgOneNetEntity();
				mone.setImei(rs.getString(1));
				mone.setDs_id(rs.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return mone;
	}

	@Override
	public boolean delDeviceByMac(String imei) {
		boolean del = false;
		String device_id = getDeviceIdByImei(imei);
		if (!("".equals(device_id))) {
			OneNetDeviceService onds = new  OneNetDeviceServiceImpl();
			OneNetResponse result = onds.delDevice(device_id,"58"); //hr 57,jd58
			if ("0".equals(result.getErrno())) {
				del = true;
			}
		}
		return del;
	}

	@Override
	public String getDeviceIdByImei(String imei) {
		String sql = "select device_id from onenetimei where imei = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String device_id = "";
		try {
			ps.setString(1, imei);
			rs = ps.executeQuery();
			while (rs.next()) {
				device_id = rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return device_id;
	}
	
	@Override
	public String sendCmd(String imei,String deviceType){
		OneNetDeviceService onds = new OneNetDeviceServiceImpl();
		OneNetResponse onr = onds.writeDeviceResource(imei, deviceType);
		String errno = onr.getErrno();
		return errno;
	}

	@Override
	public MsgOneNetEntity getMsgByImei(String imei) {
		String sql = "select imei,ds_id from onenetimei where imei= ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		MsgOneNetEntity mone = null;
		try {
			ps.setString(1, imei);
			rs = ps.executeQuery();
			while (rs.next()) {
				mone = new MsgOneNetEntity();
				mone.setImei(rs.getString(1));
				mone.setDs_id(rs.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return mone;
	}

	@Override
	public int updatePower(String mac, String power) {
		PublicUtils pu = new PublicUtilsImpl();
		String sql = "update smoke set lowVoltage = ?,netState = ?,heartime = ? where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
 		
		//获取设备的低电量状态
		int powerState = pu.getVoltageState(mac);
 		if(powerState == 1) { //低电量
 			if (Integer.parseInt(power) > 15) {
 				sql =  "update smoke set lowVoltage = ?,netState = ?,heartime = ? ,voltage = 0 where mac = ?";
 				
 				FromRepeaterAlarmDao mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
				mFromRepeaterAlarmDao.addAlarmMsg(mac, "", 194 , 1);  //将报警信息添加到alarm表中
				 
				GetPushUserIdDao mGetPushUserIdDao = new GetPushUserIdDaoImpl();
				List<String> userList = mGetPushUserIdDao.getAllUser(mac);
				List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //获取useridsmoke表里的用户用于短信通知
				if(userList!=null&&userList.size()>0){
					//开启推送消息的线程  手机推送
					PushAlarmMsgDao mPushAlarmMsgDao = new  PushAlarmMsgDaoImpl();
					Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
					PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,194);
					new MyThread(push,userList,iosMap).start();  
					//网页推送开启
					
					new WebThread(userList,mac).start();
					//短信推送
					if (txtUserList != null &&txtUserList.size()>0) {
						new TxtThread(txtUserList,mac).start();        //短信通知的线程
					}
				}
			}
 		}
		
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int rs = 0;
		try {
			ps.setString(1, power);
			ps.setInt(2, 1);
			ps.setString(3,GetTime.ConvertTimeByLong());
			ps.setString(4, mac);
			rs = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	@Override
	public int updatePower2(String mac, String power) {
		String sql = "update smoke set lowVoltage = ?,netState = ?,netState2= 1,heartime = ? where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int rs = 0;
		try {
			ps.setString(1, power);
			ps.setInt(2, 1);
			ps.setString(3,GetTime.ConvertTimeByLong());
			ps.setString(4, mac);
			rs = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}
	
	
	@Override
	public int updatePower(String mac, String power, int voltageState) {
		String sql = "update smoke set lowVoltage = ?,netState = ?,heartime = ?,voltage = ? where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int rs = 0;
		try {
			ps.setString(1, power);
			ps.setInt(2, 1);
			ps.setString(3,GetTime.ConvertTimeByLong());
			ps.setInt(4, voltageState);
			ps.setString(5, mac);
			rs = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

}
