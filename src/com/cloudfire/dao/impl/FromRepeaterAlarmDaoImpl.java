package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AlarmEntityForMQ;
import com.cloudfire.entity.AlarmPushOnlyEntity;
import com.cloudfire.entity.ElectricEnergyEntity;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.NanJing_NB_IOT_Entity;
import com.cloudfire.entity.PushAlarmMqttEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.ThreePhaseElectricEntity;
import com.cloudfire.push.RBMQClient;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class FromRepeaterAlarmDaoImpl implements FromRepeaterAlarmDao{

	public boolean addAlarmMsg(String smokeMac, String repeaterMac,
			int alarmtype,int alarmFamily) {
		if (alarmtype == 193){ //低电压保存低电压状态
			updateSmokeAlarm(smokeMac,0,1);
		} else if(Constant.ifFault(alarmtype)){ //故障保存故障状态
			updateSmokeFault(smokeMac,0,1);
		} else {
			updateSmokeAlarm(smokeMac,0);
		}
		String addTime = GetTime.ConvertTimeByLong();
		String sql="insert into alarm (smokeMac,alarmType,alarmTime,alarmFamily,repeaterMac) values (?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		boolean result=false;
		try {
			ps.setString(1, smokeMac);
			ps.setInt(2, alarmtype);
			ps.setString(3, addTime);
			ps.setInt(4, alarmFamily);
			ps.setString(5, repeaterMac);
			int rs = ps.executeUpdate();
			if(rs>0){
				result=true;
				/*AreaDao ad = new AreaDaoImpl();
				int parentId = ad.getSuperAreaIdBySmokeMac(smokeMac);
				SmokeDao sd = new SmokeDaoImpl();
				int deviceType = sd.getDeviceTypeByMac(smokeMac);
				if(parentId==153&&deviceType!=5){//民泰安区域开启MQTT推送
					PushAlarmMqttEntity pam = new PushAlarmMqttEntity();
					AreaDao areaDaoImpl = new AreaDaoImpl();
					pam = areaDaoImpl.getalarmInfo(smokeMac);
					MQTTPublicThread mqtt1 = new MQTTPublicThread(smokeMac,new JSONObject(pam).toString());
					mqtt1.start();
				}*/
			}
		
//			PushToDianXin(smokeMac,alarmtype);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
//	public static  void PushToDianXin(String smokeMac,int alarmtype) {
//		SmokeDao sd = new SmokeDaoImpl();
//		SmokeBean smoke = sd.getSmokeByMac2(smokeMac);
//		if (smoke.getAreaId() == 2306 || smoke.getAreaId() == 2269) { //国瑞消防的需要推rbmq，伊特诺的也要推
//			int deviceType =smoke.getDeviceType();
//			int deviceMiniType = sd.getDeviceMiniType(deviceType);
//			String deviceTypeName = sd.getDeviceTypeNameByType(deviceType);
//			String alarmName = sd.getAlarmNameByAlarmType(alarmtype);
//			switch(alarmtype) {
//			case 202:
//				alarmtype=401;
//				break;
//			case 193:
//				alarmtype=404;
//				break;
//			default:
//				alarmtype=499;
//			}
//			AlarmEntityForMQ ae = new AlarmEntityForMQ();
//			ae.setAlarmEventId(UUID.randomUUID().toString().replace("-", ""));
//			ae.setAlarmType(alarmtype+"");
//			ae.setChannelsCode("1");
//			ae.setDeviceId(smokeMac);
//			ae.setHappenTime(GetTime.ConvertTimeByLong());
//			switch(smoke.getAreaId()) {
//			case 2306: //国瑞消防
//				ae.setProducerCode("GRXF");
//				break;
//			case 2269:// 伊特诺
//				ae.setProducerCode("YTN");
//				break;
//			}
//		
//			Map<String,String> alarmContent = new HashMap<String,String>();
//			alarmContent.put(alarmName, deviceTypeName);
////			alarmContent.put("电压过压", "400V");
////			alarmContent.put("电流过载", "100A");
//			ae.setAlarmContent(alarmContent);
//			String message = (new JSONObject(ae)).toString();
//			int queue_index = 5;
//			if (deviceMiniType < 99 ){
//				queue_index = deviceMiniType - 1;
//			}
//			RBMQClient.send(message, queue_index);
//		}
//		
//	}

	@Override
	public boolean addAlarmMsg(String smokeMac, String repeaterMac,int alarmtype,int alarmFamily,Object obj) {
		if (alarmtype == 193){ //低电压保存低电压状态
			updateSmokeAlarm(smokeMac,0,1);
		} else if(Constant.ifFault(alarmtype)){ //故障保存故障状态
			updateSmokeFault(smokeMac,0,1);
		} else {
			updateSmokeAlarm(smokeMac,0);
		}
		String addTime = GetTime.ConvertTimeByLong();
		String sql="insert into alarm (smokeMac,alarmType,alarmTime,alarmFamily,repeaterMac) values (?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		boolean result=false;
		try {
			ps.setString(1, smokeMac);
			ps.setInt(2, alarmtype);
			ps.setString(3, addTime);
			ps.setInt(4, alarmFamily);
			ps.setString(5, repeaterMac);
			int rs = ps.executeUpdate();
			if(rs>0){
				result=true;
				/*AreaDao ad = new AreaDaoImpl();
				int parentId = ad.getSuperAreaIdBySmokeMac(smokeMac);
				if(parentId==153){//民泰安区域开启MQTT推送
					PushAlarmMqttEntity pam = new PushAlarmMqttEntity();
					AreaDao areaDaoImpl = new AreaDaoImpl();
					pam = areaDaoImpl.getalarmInfo(smokeMac);
					pam.setObj(obj);
					MQTTPublicThread mqtt1 = new MQTTPublicThread(smokeMac,new JSONObject(pam).toString());
					mqtt1.start();
				}*/
			}
			
//			PushToDianXin(smokeMac,alarmtype);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public boolean addAlarmMsg(AlarmPushOnlyEntity pushEntity) {
		if (pushEntity.getAlarmType() == 193){ //低电压保存低电压状态
			updateSmokeAlarm(pushEntity.getSmokeMac(),0,1);
		} else if(Constant.ifFault(pushEntity.getAlarmType())){ //故障保存故障状态
			updateSmokeFault(pushEntity.getSmokeMac(),0,1);
		} else {
			updateSmokeAlarm(pushEntity.getSmokeMac(),0);
		}
		String addTime = pushEntity.getAlarmTime();
		if(StringUtils.isBlank(addTime)){
			addTime = GetTime.ConvertTimeByLong();
		}
				
				
		String sql="insert into alarm (smokeMac,alarmType,alarmTime,alarmFamily,repeaterMac) values (?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		boolean result=false;
		try {
			ps.setString(1, pushEntity.getSmokeMac());
			ps.setInt(2, pushEntity.getAlarmType());
			ps.setString(3, addTime);
			ps.setString(4, pushEntity.getAlarmFamily());
			ps.setString(5, pushEntity.getRepeaterMac());
			int rs = ps.executeUpdate();
			if(rs>0){
				result=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public boolean addAlarmMsg(String smokeMac, String repeaterMac,int alarmtype,String alarmFamily,Object obj) {
		if (alarmtype == 193){ //低电压保存低电压状态
			updateSmokeAlarm(smokeMac,0,1);
		} else if(Constant.ifFault(alarmtype)){ //故障保存故障状态
			updateSmokeFault(smokeMac,0,1);
		} else {
			updateSmokeAlarm(smokeMac,0);
		}
		String addTime = GetTime.ConvertTimeByLong();
		String sql="insert into alarm (smokeMac,alarmType,alarmTime,alarmFamily,repeaterMac) values (?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		boolean result=false;
		try {
			ps.setString(1, smokeMac);
			ps.setInt(2, alarmtype);
			ps.setString(3, addTime);
			ps.setString(4, alarmFamily);
			ps.setString(5, repeaterMac);
			int rs = ps.executeUpdate();
			if(rs>0){
				result=true;
				/*AreaDao ad = new AreaDaoImpl();
				int parentId = ad.getSuperAreaIdBySmokeMac(smokeMac);
				if(parentId==153){//民泰安区域开启MQTT推送
					PushAlarmMqttEntity pam = new PushAlarmMqttEntity();
					AreaDao areaDaoImpl = new AreaDaoImpl();
					pam = areaDaoImpl.getalarmInfo(smokeMac);
					pam.setObj(obj);
					MQTTPublicThread mqtt1 = new MQTTPublicThread(smokeMac,new JSONObject(pam).toString());
					mqtt1.start();
				}*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public boolean addAlarmMsg(String smokeMac, String repeaterMac,int alarmtype,String alarmFamily) {
		if (alarmtype == 193){ //低电压保存低电压状态
			updateSmokeAlarm(smokeMac,0,1);
		} else if(Constant.ifFault(alarmtype)){ //故障保存故障状态
			updateSmokeFault(smokeMac,0,1);
		} else {
			updateSmokeAlarm(smokeMac,0);
		}
		String addTime = GetTime.ConvertTimeByLong();
		String sql="insert into alarm (smokeMac,alarmType,alarmTime,alarmFamily,repeaterMac) values (?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		boolean result=false;
		try {
			ps.setString(1, smokeMac);
			ps.setInt(2, alarmtype);
			ps.setString(3, addTime);
			ps.setString(4, alarmFamily);
			ps.setString(5, repeaterMac);
			int rs = ps.executeUpdate();
			if(rs>0){
				result=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public int getDeviceType(String deviceMac) {
		String sql = "select deviceType from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		int type = -1;
		try {
			ps.setString(1, deviceMac);
			rs= ps.executeQuery();
			while(rs.next()){
				type = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return type;
	}
	
	
	public float getLat(String deviceMac) {
		String sql = "select latitude from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		float type = 0;
		try {
			ps.setString(1, deviceMac);
			rs= ps.executeQuery();
			while(rs.next()){
				type = rs.getFloat(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return type;
	}
	
	public float getLon(String deviceMac) {
		String sql = "select longitude from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		float type = 0;
		try {
			ps.setString(1, deviceMac);
			rs= ps.executeQuery();
			while(rs.next()){
				type = rs.getFloat(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return type;
	}
	
	public float getLinkageDistance(String deviceMac) {
		String sql = "select linkageDistance from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		float type = 0;
		try {
			ps.setString(1, deviceMac);
			rs= ps.executeQuery();
			while(rs.next()){
				type = rs.getFloat(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return type;
	}
	
	public List<String> getSmokeInLinkageDistance(String deviceMac,float distance) {
		distance=distance/111000;//@@经纬度与长度转换
		float lon=getLon(deviceMac);
		float lat=getLat(deviceMac);
		String sql = "select mac from smoke where (ABS(longitude-?))<? AND (ABS(latitude-?))<? AND mac<>?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();
		try {
			ps.setString(1, lon+"");
			ps.setString(2, distance+"");
			ps.setString(3, lat+"");
			ps.setString(4, distance+"");
			ps.setString(5, deviceMac);
			rs= ps.executeQuery();
			while(rs.next()){
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public void updateSmokeAlarm(String smokeMac,int type) {
		String sql="update smoke set ifAlarm=? where mac=?";
		if (type == 1) {
			sql = "update smoke set ifAlarm = ?,voltage=0,ifFault=0 where mac = ?";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setInt(1, type);
			ps.setString(2, smokeMac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public void updateSmokeAlarm(String smokeMac,int type,int lowVoltage) {
		String sql="update smoke set ifAlarm=?,voltage=? where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setInt(1, type);
			ps.setInt(2, lowVoltage);
			ps.setString(3, smokeMac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public void updateSmokeFault(String smokeMac,int type,int ifFault) {
		String sql="update smoke set ifAlarm=?,ifFault=? where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setInt(1, type);
			ps.setInt(2, ifFault);
			ps.setString(3, smokeMac);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public void updateSmokeAlarm() { //一键处理所有报警
		String sql="update smoke set ifAlarm=1,voltage=0,ifFault=0";
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
	
	public boolean addnotifyMsg(String smokeMac, String repeaterMac,
			int alarmtype,int alarmFamily) {
		if (alarmtype == 193){ //低电压保存低电压状态
			updateSmokeAlarm(smokeMac,0,1);
		} else if(Constant.ifFault(alarmtype)){ //故障保存故障状态
			updateSmokeFault(smokeMac,0,1);
		} else {
			updateSmokeAlarm(smokeMac,0);
		}
		String addTime = GetTime.ConvertTimeByLong();
		String sql = "";
		if(isexitmac(smokeMac)){
			sql = "UPDATE alarm set alarmType=?,alarmTime=?,alarmFamily=?,repeaterMac=?,ifDealAlarm=0 where smokeMac=?";
		}else{
			sql="insert into alarm (alarmType,alarmTime,alarmFamily,repeaterMac,smokeMac) values (?,?,?,?,?)";
		}
		
		
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		boolean result=false;
		try {
			ps.setString(5, smokeMac);
			ps.setInt(1, alarmtype);
			ps.setString(2, addTime);
			ps.setInt(3, alarmFamily);
			ps.setString(4, repeaterMac);
			int rs = ps.executeUpdate();
			if(rs>0){
				result=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public SmokeBean getSmoke(String deviceMac) {
		String sql = "select deviceType,placeAddress,address,areaId,repeater,named from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		SmokeBean mSmokeBean=null;
		try {
			ps.setString(1, deviceMac);
			rs= ps.executeQuery();
			while(rs.next()){
				if(mSmokeBean==null){
					mSmokeBean= new SmokeBean();
				}
				//add by lzo 2017-5-9 begin
				byte[] addressbyte = rs.getString("address").getBytes("gbk");
				byte[] placeaddressbyte = rs.getString("placeAddress").getBytes("gbk");
				byte[] smokeNamed = rs.getString("named").getBytes("gbk");
				mSmokeBean.setSmokeNamed(smokeNamed);
				mSmokeBean.setAddressbyte(addressbyte);
				mSmokeBean.setPlaceaddressbyte(placeaddressbyte);
				mSmokeBean.setAreaId(rs.getInt(4));
				//add by lzo 2017-5-9 end;
				mSmokeBean.setDeviceType(rs.getInt(1));
				mSmokeBean.setAddress(rs.getString(3));
				mSmokeBean.setPlaceeAddress(rs.getString(2));
				mSmokeBean.setRepeater(rs.getString("repeater"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return mSmokeBean;
	}

	@Override
	public SmokeBean getSmokeInfo(SmokeBean smokeBean) {
		int count = 0;
//		String sql = "SELECT named,mac FROM smoke WHERE areaid= ? AND deviceType='7'";  //查询区域下的声光
//		String sql = "SELECT named,mac FROM smoke WHERE areaid= ? AND deviceType='7' and repeater= ?";  //查询区域下终端里面的声光  //update 2017-7-10
		String sql = "SELECT deviceMac,soundMac FROM ackdevice WHERE deviceMac = ?"; 	//查询烟感绑定的声光
		List<String> list = new ArrayList<String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
//			ps.setInt(1, smokeBean.getAreaId());
			ps.setString(1, smokeBean.getMac());
			rs = ps.executeQuery();
			while(rs.next()){
				count++;
				list.add(rs.getString(2));
			}
			smokeBean.setCount(count);
			smokeBean.setList(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smokeBean;
	}
	
	public boolean isexitmac(String mac){
		boolean result = false;
		String sql = "SELECT * from alarm where smokeMac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
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

	@Override
	public void addThreeElectric(RePeaterData mRePeaterData, int cmd) {
		ThreePhaseElectricEntity threeElectric = mRePeaterData.getThreeElectric();
			if(threeElectric!=null){
				String mac = threeElectric.getImeiStr();
				PublicUtils putil = new PublicUtilsImpl();
				ElectricTypeInfoDao edao = new ElectricTypeInfoDaoImpl();
				if(cmd==1){	//	处理阈值数据
					ElectricThresholdBean etb = threeElectric.getElectricBean();
					if(etb!=null){
						String lc = etb.getLeakCurrent();
						String oc = etb.getOverCurrent();
						String op = etb.getOverpressure();
						String up = etb.getUnpressure();
						AlarmThresholdValueDao mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
						mAlarmThresholdValueDao.addThresholdValue(mac, lc,"","", "", mac, 46);
						mAlarmThresholdValueDao.addThresholdValue(mac, oc,"","", "", mac, 45);
						mAlarmThresholdValueDao.addThresholdValue(mac, op, "","", "",mac, 43);
						mAlarmThresholdValueDao.addThresholdValue(mac, up,"","", "", mac, 44);
						if(etb.getTemperatures()!=null&&(etb.getTemperatures().size()>0)){//代表有温度阈值针对优特电气
							String tempvalue = etb.getTemperatures().get(1);
							mAlarmThresholdValueDao.addThresholdValue(mac, tempvalue,tempvalue,tempvalue,tempvalue, mac, 47);
						}
					}
				}else{		//处理透传数据
					if(StringUtils.isNotBlank(threeElectric.getRunGateState())){
						putil.updateDeviceMac(threeElectric.getImeiStr(),(int)Float.parseFloat(threeElectric.getRunGateState()));
					}
					edao.addThreePhaseElectricInfo(threeElectric);
					if(threeElectric.getRunAlarmState().equals("1")||threeElectric.getRunAlarmState()=="1"){	//1报警状态
						AlarmPushOnlyEntity pushEntity = new AlarmPushOnlyEntity();
						pushEntity.setSmokeMac(mac);
						pushEntity.setRepeaterMac(mac);
						pushEntity.setAlarmTime(threeElectric.getHeartime());
						pushEntity.setAlarmType(Integer.parseInt(threeElectric.getRunCauseState()));
						if(mRePeaterData.getDeviceType()==3){
							pushEntity.setAlarmType(136);
						}
						pushEntity.setAlarmFamily(threeElectric.getRunGateState());
						Utils.pushAlarmInfo(pushEntity,threeElectric);
					}
				}
			}
	}

	@Override
	public void addElectricEnergy(RePeaterData mRePeaterData, int type) {
		ElectricEnergyEntity eee = mRePeaterData.getEee();
		if(eee!=null){
			ElectricTypeInfoDao etd = new ElectricTypeInfoDaoImpl();
			switch(type){
			case 0: //U特点率
				etd.saveElectricEnergyEntity(eee);
				break;
			case 1: //U特电气心跳
				etd.saveElectricEnergyEntity(eee);
				break;
			case 2: //U特电气阈值
				etd.saveElectricEnergyEntity(eee);
				break;
			}
		}
	}

}
