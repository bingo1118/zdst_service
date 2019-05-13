package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.AddSmokeDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.ToolDao;
import com.cloudfire.dao.ToolOneNetDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.myservice.deviceManagement.RegisterDirectlyConnectedDevice;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.OneNetHttpMethod;
import com.cloudfire.until.Utils;

public class AddSmokeDaoImpl implements AddSmokeDao{

	public HttpRsult addSmoke(String userId, String smokeMac, String privilege,
			String smokeName, String address, String longitude,
			String latitude, String placeAddress, String placeTypeId,
			String principal1, String principal1Phone, String principal2,
			String principal2Phone, String areaId, String repeater,
			String camera, String deviceType,String cameraChannel,String electrState,String image) {
		boolean isExited = isExited(smokeMac);
		String addTime = GetTime.ConvertTimeByLong();
		String sql =null;
		if(isExited){
			sql = "update smoke set named = ?," +
					"address= ?,longitude= ? ," +
					"latitude= ?,placeAddress= ? ," +
					"placeTypeId= ?,principal1= ? ," +
					"principal1Phone= ?,principal2= ? ," +
					"principal2Phone= ?,areaId= ? ," +
					"netState= ?,time= ? ," +
					"repeater= ?,camera= ?,deviceType= ?,addUserId=? ,cameraChannel=? ,electrState=?,devTypeNum=?,image=?,netState2=0 where mac= ?";
		}else{
			sql = "insert smoke (named,address,longitude,latitude,placeAddress,placeTypeId," +
					"principal1,principal1Phone,principal2,principal2Phone,areaId,netState,time,repeater," +
					"camera,deviceType,addUserId,cameraChannel,electrState,devTypeNum,image,mac) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		HttpRsult hr = null;
		try {
			ps.setString(1, smokeName);
			if(electrState.equals("3")){
				ps.setString(19, "1");
				ps.setString(20, "3");//@@表示三相电表
			}else{
				ps.setString(19, electrState);
				ps.setString(20, "1");//@@表示电气设备
			}
			if(image!=null&&image.length()>0){
				ps.setString(21, image);
			}else{
				ps.setString(21, "");
			}
			ps.setString(22, smokeMac);
			ps.setString(17, userId);
//			ps.setInt(18, Integer.parseInt(cameraChannel)); 
			if(Utils.isNullStr(cameraChannel)){		//update by lzo at 2017-7-9
				ps.setInt(18, Integer.parseInt(cameraChannel));
			}else{
				ps.setInt(18,0);
			}
			ps.setString(2, address);
			ps.setString(3, longitude);
			ps.setString(4, latitude);
			ps.setString(5, placeAddress);
			if(placeTypeId!=null&&placeTypeId.length()>0){
				ps.setInt(6, Integer.parseInt(placeTypeId));
			}else{
				ps.setInt(6, 16);
			}
			//@@
			if(principal1!=null&&principal1.length()>0){
				ps.setString(7, principal1);
			}else{
				ps.setString(7,"");
			}
			if(principal1Phone!=null&&principal1Phone.length()>0){
				ps.setString(8, principal1Phone);
			}else{
				ps.setString(8, "");
			}
			if(principal2!=null&&principal2.length()>0){
				ps.setString(9, principal2);
			}else{
				ps.setString(9, "");
			}
			if(principal2Phone!=null&&principal2Phone.length()>0){
				ps.setString(10, principal2Phone);
			}else{
				ps.setString(10, "");
			}
			if(repeater!=null&&repeater.length()>0){
				ps.setString(14, repeater);
			}else{
				ps.setString(14, "");
			}
			if(camera!=null&&camera.length()>0){
				ps.setString(15, camera);
			}else{
				ps.setString(15, "");
			}
			//@@
//			ps.setString(7, principal1);
//			ps.setString(8, principal1Phone);
//			ps.setString(9, principal2);
//			ps.setString(10, principal2Phone);
			if(areaId!=null&&areaId.length()>0){
				ps.setInt(11, Integer.parseInt(areaId));
			}else{
				ps.setInt(11, 0);
			}
			ps.setInt(12, 0); //zhong update 2017-3-23改成默认值为0状态掉线
			ps.setString(13, addTime);
//			ps.setString(14, repeater);
//			ps.setString(15, camera);
			if(deviceType!=null&&deviceType.length()>0){
				ps.setInt(16, Integer.parseInt(deviceType));
			}else{
				ps.setInt(16, 0);
			}
			int count = ifCount200(repeater);
//			if(count>200&&!"12091620".equals(repeater)){
			if(count>=200&&!isExited&&StringUtils.isNotBlank(repeater)){
				hr = new HttpRsult();
				hr.setError("添加设备失败,该终端已经添加了"+count+"个");
				hr.setErrorCode(2);
			}else{
				int rs = ps.executeUpdate();
				hr = new HttpRsult();
				if(rs<=0){
					hr.setError("添加烟感失败");
					hr.setErrorCode(2);
				}else{
					count++;
					if(repeater!=null&&repeater.length()>0&&StringUtils.isNotBlank(repeater)){//@@2017.8.3
						hr.setError("添加设备成功,该终端已经添加了"+count+"个");
					}
						HttpRsult temp=null;
						switch(deviceType){
						case "35":
							temp=addToEasyIot(smokeMac, smokeName,"NB_arc_electric", userId, address, longitude, latitude,"eDrx");
							if(temp!=null&&!isExited){
								return temp;
							}
							break;
						case "53":
							temp=addToEasyIot(smokeMac, smokeName,"NB_guizhou_electric", userId, address, longitude, latitude,"eDrx");
							if(temp!=null&&!isExited){
								return temp;
							}
							break;
						case "55":
							temp=addToEasyIot(smokeMac, smokeName,"jiadepro", userId, address, longitude, latitude,"PSM");
							if(temp!=null){
								return temp;
							}
							break;
						case "56":
							temp=addToEasyIot(smokeMac, smokeName,"NB_smoke", userId, address, longitude, latitude,"PSM");
							if(temp!=null){
								return temp;
							}
							break;
						case "57": //OneNet平台我们自己的烟感
						case "58"://OneNet平台嘉德的烟感
							temp=addToOneNet(smokeMac,smokeName,deviceType);
							if(temp!=null){
								return temp;
							}else if(temp == null){
								ToolOneNetDao tod = new ToolOneNetDaoImpl();
								AreaDao ad = new AreaDaoImpl();
								int parentId = ad.getSuperAreaIdBySmokeMac(smokeMac);
								if(parentId == 157){
									String fdevice_uuid = tod.getDeviceIdByImei(smokeMac);
									
									Utils.addSmokeInfo(null, null, fdevice_uuid, smokeName, smokeMac, address);
								}
							}
							break;
						case "59":
							temp=addToEasyIot(smokeMac, smokeName,"NB_beiqin_electric", userId, address, longitude, latitude,"eDrx");
							if(temp!=null&&!isExited){
								return temp;
							}
							break;
						case "22":
							temp=addToNanJingIot(smokeMac);
							if(temp!=null){
								return temp;
							}
							break;
						case "23":
							temp=addToNanJingIotByRiHai(smokeMac);
							if(temp!=null){
								return temp;
							}
							break;
						case "61":	//嘉德南京烟感
							temp=addToNanJingIotJiadeSmoke(smokeMac);
							if(temp!=null){
								return temp;
							}
							break;
						case "73":	//南京平台7020燃气
						case "75":	//南京平台电气1N-Q  
						case "77":	//南京平台三相电气设备 
						case "78":	//南京平台普通水压设备
						case "79":	//南京平台普通温湿度设备
						case "80":	//南京平台U特电气设备
							temp=addToNanJingIotDevice(smokeMac,deviceType+"");
							if(temp!=null){
								return temp;
							}
							break;
						}
						hr.setError("添加成功");
					hr.setErrorCode(0);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return hr;
	}
	
	private String getDeviceIdByImei(String smokeMac) {
		Connection conn = DBConnectionManager.getConnection();
		String sql = "select deviceId from imei_deviceId  where imei=?";
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs =null;
		String deviceId = "";
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				deviceId = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return deviceId;
	}


	public HttpRsult addSmoke_ytr(String userId, String smokeMac, String privilege,
			String smokeName, String address, String longitude,
			String latitude, String placeAddress, String placeTypeId,
			String principal1, String principal1Phone, String principal2,
			String principal2Phone, String areaId, String repeater,
			String camera, String deviceType,String cameraChannel,String electrState) {
		boolean isExited = isExited(smokeMac);
		String addTime = GetTime.ConvertTimeByLong();
		String sql =null;
		if(isExited){
			sql = "update smoke set named = ?," +
					"address= ?,longitude= ? ," +
					"latitude= ?,placeAddress= ? ," +
					"placeTypeId= ?,principal1= ? ," +
					"principal1Phone= ?,principal2= ? ," +
					"principal2Phone= ?,areaId= ? ," +
					"netState= ?,time= ? ," +
					"repeater= ?,camera= ?,deviceType= ?,addUserId=? ,cameraChannel=? ,electrState=?,devTypeNum=? where mac= ?";
		}else{
			sql = "insert smoke (named,address,longitude,latitude,placeAddress,placeTypeId," +
					"principal1,principal1Phone,principal2,principal2Phone,areaId,netState,time,repeater," +
					"camera,deviceType,addUserId,cameraChannel,electrState,devTypeNum,mac) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		HttpRsult hr = null;
		try {
			ps.setString(1, smokeName);
			if(electrState.equals("3")){
				ps.setString(19, "1");
				ps.setString(20, "3");//@@表示三相电表
			}else{
				ps.setString(19, electrState);
				ps.setString(20, "1");//@@表示电气设备
			}
			ps.setString(21, smokeMac);
			ps.setString(17, userId);
//			ps.setInt(18, Integer.parseInt(cameraChannel)); 
			if(Utils.isNullStr(cameraChannel)){		//update by lzo at 2017-7-9
				ps.setInt(18, Integer.parseInt(cameraChannel));
			}else{
				ps.setInt(18,0);
			}
			ps.setString(2, address);
			ps.setString(3, longitude);
			ps.setString(4, latitude);
			ps.setString(5, placeAddress);
			if(placeTypeId!=null&&placeTypeId.length()>0){
				ps.setInt(6, Integer.parseInt(placeTypeId));
			}else{
				ps.setInt(6, 16);
			}
			//@@
			if(principal1!=null&&principal1.length()>0){
				ps.setString(7, principal1);
			}else{
				ps.setString(7,"");
			}
			if(principal1Phone!=null&&principal1Phone.length()>0){
				ps.setString(8, principal1Phone);
			}else{
				ps.setString(8, "");
			}
			if(principal2!=null&&principal2.length()>0){
				ps.setString(9, principal2);
			}else{
				ps.setString(9, "");
			}
			if(principal2Phone!=null&&principal2Phone.length()>0){
				ps.setString(10, principal2Phone);
			}else{
				ps.setString(10, "");
			}
			if(repeater!=null&&repeater.length()>0){
				ps.setString(14, repeater);
			}else{
				ps.setString(14, "");
			}
			if(camera!=null&&camera.length()>0){
				ps.setString(15, camera);
			}else{
				ps.setString(15, "");
			}
			//@@
//			ps.setString(7, principal1);
//			ps.setString(8, principal1Phone);
//			ps.setString(9, principal2);
//			ps.setString(10, principal2Phone);
			if(areaId!=null&&areaId.length()>0){
				ps.setInt(11, Integer.parseInt(areaId));
			}else{
				ps.setInt(11, 0);
			}
			ps.setInt(12, 0); //zhong update 2017-3-23改成默认值为0状态掉线
			ps.setString(13, addTime);
//			ps.setString(14, repeater);
//			ps.setString(15, camera);
			if(deviceType!=null&&deviceType.length()>0){
				ps.setInt(16, Integer.parseInt(deviceType));
			}else{
				ps.setInt(16, 0);
			}
			int count = ifCount200(repeater);
//			if(count>200&&!"12091620".equals(repeater)){
			if(count>=200&&!isExited&&StringUtils.isNotBlank(repeater)){
				hr = new HttpRsult();
				hr.setError("添加设备失败,该终端已经添加了"+count+"个");
				hr.setErrorCode(2);
			}else{
				int rs = ps.executeUpdate();
				hr = new HttpRsult();
				if(rs<=0){
					hr.setError("添加烟感失败");
					hr.setErrorCode(2);
				}else{
					count++;
					if(repeater!=null&&repeater.length()>0&&StringUtils.isNotBlank(repeater)){//@@2017.8.3
						hr.setError("添加设备成功,该终端已经添加了"+count+"个");
					}
						HttpRsult temp=null;
						switch(deviceType){
						case "35":
							temp=addToEasyIot(smokeMac, smokeName,"NB_arc_electric", userId, address, longitude, latitude,"eDrx");
							if(temp!=null&&!isExited){
								return temp;
							}
							break;
						case "53":
							temp=addToEasyIot(smokeMac, smokeName,"NB_guizhou_electric", userId, address, longitude, latitude,"eDrx");
							if(temp!=null&&!isExited){
								return temp;
							}
							break;
						case "55":
							temp=addToEasyIot(smokeMac, smokeName,"jiadepro", userId, address, longitude, latitude,"PSM");
							if(temp!=null){
								return temp;
							}
							break;
						case "56":
							temp=addToEasyIot(smokeMac, smokeName,"NB_smoke", userId, address, longitude, latitude,"PSM");
							if(temp!=null){
								return temp;
							}
							break;
						case "57": //OneNet平台我们自己的烟感
						case "58"://OneNet平台嘉德的烟感
							temp=addToOneNet(smokeMac,smokeName,deviceType);
							if(temp!=null){
								return temp;
							}else if(temp == null){
								ToolOneNetDao tod = new ToolOneNetDaoImpl();
								AreaDao ad = new AreaDaoImpl();
								int parentId = ad.getSuperAreaIdBySmokeMac(smokeMac);
								if(parentId == 157){
									String fdevice_uuid = tod.getDeviceIdByImei(smokeMac);
									
									Utils.addSmokeInfo(null, null, fdevice_uuid, smokeName, smokeMac, address);
								}
							}
							break;
						case "59":
							temp=addToEasyIot(smokeMac, smokeName,"NB_beiqin_electric", userId, address, longitude, latitude,"eDrx");
							if(temp!=null&&!isExited){
								return temp;
							}
							break;
						case "22":
							temp=addToNanJingIot(smokeMac);
							if(temp!=null){
								return temp;
							}
							break;
						case "23":
							temp=addToNanJingIotByRiHai(smokeMac);
							if(temp!=null){
								return temp;
							}
							break;
						case "61":	//嘉德南京烟感
							temp=addToNanJingIotJiadeSmoke(smokeMac);
							if(temp!=null){
								return temp;
							}
							break;
						case "73":	//南京平台7020燃气
							temp=addToNanJingIotDevice(smokeMac,"73");
							if(temp!=null){
								return temp;
							}
							break;
						case "75":	//南京平台电气1N-Q  
							temp=addToNanJingIotDevice(smokeMac,"75");
							if(temp!=null){
								return temp;
							}
							break;
						case "77":	//南京平台电气三相 
							temp=addToNanJingIotDevice(smokeMac,"77");
							if(temp!=null){
								return temp;
							}
							break;
						}
						hr.setError("添加成功");
					hr.setErrorCode(0);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return hr;
	}
	
	private HttpRsult addToEasyIot(String smokeMac,String smokeName,String devType,String userId,
			String address,String longitude,String latitude,String serverMode) {
		HttpRsult hr;
		ToolDao tool = new ToolDaoImpl();
		LoginDao login = new LoginDaoImpl();
		String accessToken = login.getEaseIotAccessToKen(Constant.EASY_IOT_HR_ACCOUNT, Constant.EASY_IOT_HR_PWD);
		String result=tool.registeredPlant(Constant.IOT_SERVER_ID,
				accessToken, smokeMac, smokeName, devType, Constant.IOT_SERVER_ID
				, serverMode, userId, userId, address, longitude, latitude, "");
		if(!result.equals("成功")){
			hr = new HttpRsult();
			if(result.contains("1004")){
				hr.setError("IOT平台已添加过该设备，数据已保存到瀚润平台");
			}else{
				hr.setError("添加到iot平台失败，数据已保存到瀚润平台");
			}
			hr.setErrorCode(2);
			return hr;
		}
		return null;
	}
	
	
	private HttpRsult addToNanJingIotDevice(String smokeMac,String deviceType) {
		HttpRsult hr;
		System.out.println("add addToNanJingIotDevice join "+smokeMac+"   =======  "+deviceType);
		String result=RegisterDirectlyConnectedDevice.registerDevice(Constant.JDAPPID, smokeMac,deviceType);
		if(StringUtils.isNotBlank(result)){
			hr = new HttpRsult();
			if(result.contains("100007")){
				hr.setError("参数不合法，数据已保存到瀚润平台:"+result);
			}else{
				hr.setError("添加到iot平台失败，数据已保存到瀚润平台:"+result);
			}
			hr.setErrorCode(2);
			return hr;
		}
		return null;
	}
	
	private HttpRsult addToNanJingIotJiadeSmoke(String smokeMac) {
		HttpRsult hr;
		String result=RegisterDirectlyConnectedDevice.registerDevice(Constant.JDAPPID, smokeMac,"61");
		if(StringUtils.isNotBlank(result)){
			hr = new HttpRsult();
			if(result.contains("100007")){
				hr.setError("参数不合法，数据已保存到瀚润平台:"+result);
			}else{
				hr.setError("添加到iot平台失败，数据已保存到瀚润平台:"+result);
			}
			hr.setErrorCode(2);
			return hr;
		}
		return null;
	}
	
	private HttpRsult addToNanJingIotByRiHai(String smokeMac) {
		HttpRsult hr;
		String result=RegisterDirectlyConnectedDevice.registerDevice("89ROeDVLwgoYFzMVTKAcZCHz1Bga", smokeMac);
		if(StringUtils.isNotBlank(result)){
			hr = new HttpRsult();
			if(result.contains("100007")){
				hr.setError("参数不合法，数据已保存到瀚润平台:"+result);
			}else{
				hr.setError("添加到iot平台失败，数据已保存到瀚润平台:"+result);
			}
			hr.setErrorCode(2);
			return hr;
		}
		return null;
	}
	
	private HttpRsult addToNanJingIot(String smokeMac) {
		HttpRsult hr;
		String result=RegisterDirectlyConnectedDevice.registerDevice(null, smokeMac);
		if(StringUtils.isNotBlank(result)){
			hr = new HttpRsult();
			if(result.contains("100007")){
				hr.setError("参数不合法，数据已保存到瀚润平台:"+result);
			}else{
				hr.setError("添加到iot平台失败，数据已保存到瀚润平台:"+result);
			}
			hr.setErrorCode(2);
			return hr;
		}
		return null;
	}
	
	private HttpRsult addToOneNet(String smokeMac,String smokeName,String  deviceType) {
		HttpRsult hr;
		ToolOneNetDao oneNetTool=new ToolOneNetDaoImpl();
		String result=oneNetTool.insertOneNetDev(smokeMac,smokeMac,smokeName,deviceType);
		if(!result.equals("0")){
			hr = new HttpRsult();
			if(result.equals("6")){
				hr.setError("IOT平台已添加该设备，数据已保存到瀚润平台");
				hr.setErrorCode(0);
			}else{
				hr.setError("添加到iot平台失败，数据已保存到瀚润平台");
			}
			hr.setErrorCode(2);
			return hr;
		}
		return null;
	}

	public boolean isExited(String smokeMac){
		String loginSql = "select mac from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		boolean result = false;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			if(rs.next()){
				result = true;
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
	
	/**
	 * 根据烟感查询所属中继器下的所有设备。
	 * @param mac
	 * @return
	 */
	public int ifCount200(String mac){
		int result = 0;
		String sql = "SELECT COUNT(*) FROM smoke WHERE repeater = ?";
		
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
	
	public String getRepeateMac(String mac){
		String sql = "SELECT repeater FROM smoke WHERE mac = ?";
		String result="";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getString(1);
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
	public HttpRsult addSmokeTwo(String userId, String smokeMac,
			String privilege, String smokeName, String address,
			String longitude, String latitude, String placeAddress,
			String placeTypeId, String principal1, String principal1Phone,
			String principal2, String principal2Phone, String areaId,
			String repeater, String camera, String deviceType,
			String positions, String floors, String storeys) {


		boolean isExited = isExited(smokeMac);
		String addTime = GetTime.ConvertTimeByLong();
		String sql =null;
		if(isExited){
			sql = "update smoke set named = ?," +
					"address= ?,longitude= ? ," +
					"latitude= ?,placeAddress= ? ," +
					"placeTypeId= ?,principal1= ? ," +
					"principal1Phone= ?,principal2= ? ," +
					"principal2Phone= ?,areaId= ? ," +
					"netState= ?,time= ? ," +
					"repeater= ?,camera= ?,deviceType= ?," +
					"positions= ?,floors= ?,storeys= ? where mac= ?";
		}else{
			sql = "insert smoke (named,address,longitude,latitude,placeAddress,placeTypeId," +
					"principal1,principal1Phone,principal2,principal2Phone,areaId,netState,time,repeater," +
					"camera,deviceType,mac,positions,floors,storeys) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		}
		//INSERT table (auto_id, auto_name) values (1, ‘yourname') ON DUPLICATE KEY UPDATE auto_name='yourname'
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		HttpRsult hr = null;
		try {
			ps.setString(1, smokeName);
			ps.setString(17, smokeMac);
			ps.setString(2, address);
			ps.setString(3, longitude);
			ps.setString(4, latitude);
			ps.setString(5, placeAddress);
			if(placeTypeId!=null&&placeTypeId.length()>0){
				ps.setInt(6, Integer.parseInt(placeTypeId));
			}else{
				ps.setInt(6, 16);
			}
			ps.setString(7, principal1);
			ps.setString(8, principal1Phone);
			ps.setString(9, principal2);
			ps.setString(10, principal2Phone);
			if(areaId!=null&&areaId.length()>0){
				ps.setInt(11, Integer.parseInt(areaId));
			}else{
				ps.setInt(11, 0);
			}
			ps.setInt(12, 0); //zhong update 2017-3-23改成默认值为0状态掉线
			ps.setString(13, addTime);
			ps.setString(14, repeater);
			ps.setString(15, camera);
			if(deviceType!=null&&deviceType.length()>0){
				ps.setInt(16, Integer.parseInt(deviceType));
			}else{
				ps.setInt(16, 0);
			}
			
			ps.setString(18, positions);
			ps.setString(19, floors);
			ps.setString(20, storeys);
			int rs = ps.executeUpdate();
			hr = new HttpRsult();
			if(rs<=0){
				hr.setError("添加烟感失败");
				hr.setErrorCode(2);
			}else{
				hr.setError("添加烟感成功");
				hr.setErrorCode(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return hr;
	}

	@Override
	public HttpRsult addNFC(String userId, String uId, String areaId,
			String deviceType, String deviceName, String address,
			String longitude, String latitude, String memo,String producer,String makeTime,String workerPhone,String makeAddress) {
		if(makeAddress==null){
			makeAddress="";
		}
		boolean isExited = isNFCExited(uId);
		String addNFCTime = GetTime.ConvertTimeByLong();
		String sql =null;
		if(isExited){
			sql = "update nfcinfo set deviceName = ?," +
					"address= ?,longitude= ? ," +
					"latitude= ?," +
					"areaid= ? ," +
					"addTime= ? ," +
					"deviceType= ?,userId=?,memo=?,producer=?,makeTime=?,workerPhone=?,makeAddress=? where uid= ?";
		}else{
			sql = "insert nfcinfo (deviceName,address,longitude,latitude," +
					"areaid,addTime," +
					"deviceType,userId,memo,producer,makeTime,workerPhone,makeAddress,uid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		HttpRsult hr = null;
		try {
			ps.setString(1, deviceName);
			ps.setString(14, uId);
			ps.setString(8, userId);
			ps.setString(2, address);
			ps.setString(3, longitude);
			ps.setString(4, latitude);
			ps.setString(13, makeAddress);
			if(memo==null){
				memo="";
			}
			ps.setString(9, memo);
			if(producer==null){
				producer="";
			}
			ps.setString(10, producer);
			if(makeTime==null){
				makeTime="";
			}
			ps.setString(11, makeTime);
			if(workerPhone==null){
				workerPhone="";
			}
			ps.setString(12, workerPhone);
			if(areaId!=null&&areaId.length()>0){
				ps.setInt(5, Integer.parseInt(areaId));
			}else{
				ps.setInt(5, 0);
			}
			ps.setString(6, addNFCTime);
			if(deviceType!=null&&deviceType.length()>0){
				ps.setInt(7, Integer.parseInt(deviceType));
			}else{
				ps.setInt(7, 0);
			}
			int rs = ps.executeUpdate();
			hr = new HttpRsult();
			if(rs<=0){
				hr.setError("添加烟感失败");
				hr.setErrorCode(2);
			}else{
				hr.setError("添加成功");
				hr.setErrorCode(0);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return hr;
	  }
		public boolean isNFCExited(String smokeMac){
			String loginSql = "select uid from nfcinfo where uid = ?";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
			ResultSet rs = null;
			boolean result = false;
			try {
				ps.setString(1, smokeMac);
				rs = ps.executeQuery();
				if(rs.next()){
					result = true;
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

		@Override
		public HttpRsult addNFCRecord(String UUID, String longitude,
				String latutide, String userId, String endTime,
				String devicestate, String memo,String photo1,
				String items) {
			
			String sql = "INSERT INTO nfcrecord(UUID,longitude,latitude,userId,endTime,devicestate,memo,photo1,items)VALUES(?,?,?,?,?,?,?,?,?)";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
			HttpRsult hr = null;
			try {
				ps.setString(1, UUID);
				ps.setString(2, longitude);
				ps.setString(3, latutide);
				ps.setString(4, userId);
				ps.setString(5, endTime);
				ps.setString(6, devicestate);
				ps.setString(7, memo);
				ps.setString(8, photo1);
				ps.setString(9, items);
				int rs = ps.executeUpdate();
				hr = new HttpRsult();
				if(rs<=0){
					hr.setError("添加设备失败");
					hr.setErrorCode(2);
				}else{
					hr.setError("添加成功");
					hr.setErrorCode(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
			return hr;
		}

		@Override
		public boolean meterAddDevice(String mac, String name,String address) {
			boolean result=false;
			String sql="insert elecMeterDevice (device,name,address) values(?,?,?) on duplicate key update name=? ,address=?";
			Connection conn=DBConnectionManager.getConnection();
			PreparedStatement ps=DBConnectionManager.prepare(conn, sql);
			
			try{
				ps.setString(1, mac);;
				ps.setString(2, name);
				ps.setString(3, address);
				ps.setString(4, name);
				ps.setString(5, address);
				
				if(ps.executeUpdate()>0)
				{
					result=true;
				}
				else {
					
				}
					
			}catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(ps);;
				DBConnectionManager.close(conn);
			}
			return result;
		}
		@Override
		public HttpRsult addAdministrationInfo(String fsocial_uuids,String fsocial_name,String fprovince_code,
				String fcity_code,String fcounty_code,String ftown_code,String faddress,String flink_man,String ftel_no,
				String flongitude,String flatitude,String funit_type,String fis_active) {
			String addTime = GetTime.ConvertTimeByLong();
			String sql =null;
			
			sql = "insert administration_info (fsocial_uuid,fsocial_name,fprovince_code,fcity_code," +
					"fcounty_code,ftown_code," +
					"faddress,flink_man,ftel_no,flongitude,flatitude,funit_type,fis_active,add_time) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
			HttpRsult hr = null;
			try {
				ps.setString(1, fsocial_uuids);
				ps.setString(2, fsocial_name);
				ps.setString(3, fprovince_code);
				ps.setString(4, fcity_code);
				ps.setString(5, fcounty_code);
				ps.setString(6, ftown_code);
				ps.setString(7, faddress);
				ps.setString(8, flink_man);
				ps.setString(9, ftel_no);
				ps.setString(10, flongitude);
				ps.setString(11, flatitude);
				ps.setString(12, funit_type);
				ps.setString(13, fis_active);
				ps.setString(14, addTime);
				
				int rs = ps.executeUpdate();
				hr = new HttpRsult();
				if(rs<=0){
					hr.setError("添加烟感失败");
					hr.setErrorCode(2);
				}else{
					hr.setError("添加成功");
					hr.setErrorCode(0);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
			return hr;
		  }

		@Override
		public HttpRsult addSmokeForGR(String userId, String smokeMac, String privilege, String smokeName, String address,
				String longitude, String latitude, String placeAddress, String placeTypeId, String principal1,
				String principal1Phone, String principal2, String principal2Phone, String areaId, String repeater,
				String camera, String deviceType, String cameraChannel, String electrState, String companyId,
				String buildingId) {
			boolean isExited = isExited(smokeMac);
			String addTime = GetTime.ConvertTimeByLong();
			String sql =null;
			if(isExited){
				sql = "update smoke set named = ?," +
						"address= ?,longitude= ? ," +
						"latitude= ?,placeAddress= ? ," +
						"placeTypeId= ?,principal1= ? ," +
						"principal1Phone= ?,principal2= ? ," +
						"principal2Phone= ?,areaId= ? ," +
						"netState= ?,updatetime= ? ," +
						"repeater= ?,camera= ?,deviceType= ?,addUserId=? ,cameraChannel=? ,electrState=?,devTypeNum=?,companyId=?,buildingId=? where mac= ?";
			}else{
				sql = "insert smoke (named,address,longitude,latitude,placeAddress,placeTypeId," +
						"principal1,principal1Phone,principal2,principal2Phone,areaId,netState,time,repeater," +
						"camera,deviceType,addUserId,cameraChannel,electrState,devTypeNum,companyId,buildingId,mac) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			}
			//INSERT table (auto_id, auto_name) values (1, ‘yourname') ON DUPLICATE KEY UPDATE auto_name='yourname'
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
			HttpRsult hr = null;
			try {
				ps.setString(1, smokeName);
				if(electrState.equals("3")){
					ps.setString(19, "1");
					ps.setString(20, "3");//@@表示三相电表
				}else{
					ps.setString(19, electrState);
					ps.setString(20, "1");//@@表示电气设备
				}
				ps.setString(21, companyId);
				ps.setString(22, buildingId);
				ps.setString(23, smokeMac);
				ps.setString(17, userId);
				if(Utils.isNullStr(cameraChannel)){		//update by lzo at 2017-7-9
					ps.setInt(18, Integer.parseInt(cameraChannel));
				}else{
					ps.setInt(18,0);
				}
				ps.setString(2, address);
				ps.setString(3, longitude);
				ps.setString(4, latitude);
				ps.setString(5, placeAddress);
				if(placeTypeId!=null&&placeTypeId.length()>0){
					ps.setInt(6, Integer.parseInt(placeTypeId));
				}else{
					ps.setInt(6, 16);
				}
				//@@
				if(principal1!=null&&principal1.length()>0){
					ps.setString(7, principal1);
				}else{
					ps.setString(7,"");
				}
				if(principal1Phone!=null&&principal1Phone.length()>0){
					ps.setString(8, principal1Phone);
				}else{
					ps.setString(8, "");
				}
				if(principal2!=null&&principal2.length()>0){
					ps.setString(9, principal2);
				}else{
					ps.setString(9, "");
				}
				if(principal2Phone!=null&&principal2Phone.length()>0){
					ps.setString(10, principal2Phone);
				}else{
					ps.setString(10, "");
				}
				if(repeater!=null&&repeater.length()>0){
					ps.setString(14, repeater);
				}else{
					ps.setString(14, "");
				}
				if(camera!=null&&camera.length()>0){
					ps.setString(15, camera);
				}else{
					ps.setString(15, "");
				}
				if(areaId!=null&&areaId.length()>0){
					ps.setInt(11, Integer.parseInt(areaId));
				}else{
					ps.setInt(11, 0);
				}
				ps.setInt(12, 0); //zhong update 2017-3-23改成默认值为0状态掉线
				ps.setString(13, addTime);
				if(deviceType!=null&&deviceType.length()>0){
					ps.setInt(16, Integer.parseInt(deviceType));
				}else{
					ps.setInt(16, 0);
				}
				int count = ifCount200(repeater);
				if(count>=200&&!isExited&&StringUtils.isNotBlank(repeater)){
					hr = new HttpRsult();
					hr.setError("添加设备失败,该终端已经添加了"+count+"个");
					hr.setErrorCode(2);
				}else{
					int rs = ps.executeUpdate();
					hr = new HttpRsult();
					if(rs<=0){
						hr.setError("添加烟感失败");
						hr.setErrorCode(2);
					}else{
						count++;
						if(repeater!=null&&repeater.length()>0&&StringUtils.isNotBlank(repeater)){//@@2017.8.3
							hr.setError("添加设备成功,该终端已经添加了"+count+"个");
						}
						HttpRsult temp=null;
						switch(deviceType){
						case "35":
							temp=addToEasyIot(smokeMac, smokeName,"NB_arc_electric", userId, address, longitude, latitude,"eDrx");
							if(temp!=null&&!isExited){
								return temp;
							}
							break;
						case "53":
							temp=addToEasyIot(smokeMac, smokeName,"NB_guizhou_electric", userId, address, longitude, latitude,"eDrx");
							if(temp!=null&&!isExited){
								return temp;
							}
							break;
						case "55":
							temp=addToEasyIot(smokeMac, smokeName,"jiadepro", userId, address, longitude, latitude,"PSM");
							if(temp!=null){
								return temp;
							}
							break;
						case "56":
							temp=addToEasyIot(smokeMac, smokeName,"NB_smoke", userId, address, longitude, latitude,"PSM");
							if(temp!=null){
								return temp;
							}
							break;
						case "57": //OneNet平台我们自己的烟感
						case "58"://OneNet平台嘉德的烟感
							temp=addToOneNet(smokeMac,smokeName,deviceType);
							if(temp!=null){
								return temp;
							}else if(temp == null){
								ToolOneNetDao tod = new ToolOneNetDaoImpl();
								AreaDao ad = new AreaDaoImpl();
								int parentId = ad.getSuperAreaIdBySmokeMac(smokeMac);
								if(parentId == 157){
									String fdevice_uuid = tod.getDeviceIdByImei(smokeMac);
									Utils.addSmokeInfo(null, null, fdevice_uuid, smokeName, smokeMac, address);
								}
							}
							break;
						case "59":
							temp=addToEasyIot(smokeMac, smokeName,"NB_beiqin_electric", userId, address, longitude, latitude,"eDrx");
							if(temp!=null&&!isExited){
								return temp;
							}
							break;
						case "22":
							temp=addToNanJingIot(smokeMac);
							if(temp!=null){
								return temp;
							}
							break;
						case "23":
							temp=addToNanJingIotByRiHai(smokeMac);
							if(temp!=null){
								return temp;
							}
							break;
						case "61":	//嘉德南京烟感
							temp=addToNanJingIotJiadeSmoke(smokeMac);
							if(temp!=null){
								return temp;
							}
							break;
						case "73":	//南京平台7020燃气
						case "75":	//南京平台电气1N-Q  
						case "77":	//南京平台三相电气设备 
						case "78":	//南京平台普通水压设备
						case "79":	//南京平台普通温湿度设备
						case "80":	//南京平台U特电气设备
							temp=addToNanJingIotDevice(smokeMac,deviceType+"");
							if(temp!=null){
								return temp;
							}
							break;
						}
						hr.setError("添加成功");
						hr.setErrorCode(0);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
			return hr;
		}

		
}
