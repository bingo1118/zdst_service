package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.IMyDevices;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.MyDevicesVo;


/**
 * @author cheng
 *2017-3-20
 *����1:45:53
 */
public class MyDeviceMsgImpl implements IMyDevices{

	/* (non-Javadoc)
	 * @see com.cloudfire.dao.IMyDevices#getMyDevicesMsg(java.lang.String)
	 */
	@Override
	public List<MyDevicesVo> getMyDevicesMsg(List<String> areaId) {
		StringBuffer strSql = new StringBuffer();
		int len = areaId.size();
		if(len==0){
			return null;
		}
		if(len==1){
			strSql.append(" and smoke.areaId in (?) order by alarm.id desc");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" and smoke.areaId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?) order by alarm.id desc");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		
		
		String basicSql = "select smoke.deviceType, smoke.mac,smoke.floors,smoke.storeys,smoke.netState,alarm.alarmType,alarm.alarmTime,alarm.ifDealAlarm,alarm.alarmFamily from smoke ,alarm where smoke.mac = alarm.smokeMac";
		String sql = new String(basicSql+strSql);
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sql);
		ResultSet rs = null;
		List<MyDevicesVo> list = new ArrayList<>();
		try {
			for(int i=1;i<=len;i++){
				ppst.setString(i, areaId.get(i-1));
			}
			rs = ppst.executeQuery();
			while(rs.next()){
				MyDevicesVo vo = new MyDevicesVo();
				vo.setDevMac(rs.getString(2));
				vo.setFloor(rs.getString(3));
				vo.setStoreys(rs.getString(4));
				vo.setNetStates(rs.getString(5));
				vo.setAlarmTime(rs.getString(7));
				vo.setIfDealAlarm(rs.getString(8));
				/**	private String devictType;
	           	private String devName;
	         	private String devMac;
				private String floor;
				private String storeys;
				private String position;
				private String netStates;
				private String alarmType;
				private String alarmTime;
				private String ifDealAlarm;
				 * 
				 *  */
				int devType = rs.getInt(1);
				int alarmFamily = rs.getInt(9);
				switch (devType) {
				case 1:
					vo.setDevName("�̸�̽����");
					break;
				case 2:
					vo.setDevName("��ȼ����̽����");
					vo.setAlarmType("��");
					break;
				case 5:
					vo.setDevName("��������̽����");
					switch(alarmFamily){
					/**43:��ѹ����
						44:Ƿѹ����
						45:��������
						46:©�籨��
						47:�¶ȱ��� */
					case 43:
						vo.setAlarmType("��ѹ����");
						break;
					case 44:
						vo.setAlarmType("Ƿѹ����");
						break;
					case 45:
						vo.setAlarmType("��������");
						break;
					case 46:
						vo.setAlarmType("©��������");
						break;
					case 47:
						vo.setAlarmType("�¶ȱ���");
						break;
					}
					//vo.setAlarmType("�������ֱ���");
					//break;
				case 7:vo.setDevName("���ⱨ����");
					vo.setAlarmType("���ⱨ��");
					break;
				case 8:
					vo.setDevName("�ֶ�������");
					vo.setAlarmType("�ֶ�����");
					break;
				case 9:
					vo.setDevName("�����豸");
					break;	
					
				}
				int alarmType = rs.getInt(6);
				switch (alarmType) {
				case 193:
					vo.setAlarmType("�̸е�����");
					break;
				
				case 202:
					vo.setAlarmType("��");					
				}
				list.add(vo);		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(connection);
		}
		return list;
		
		
	}

	@Override
	public List<MyDevicesVo> getMyDevicesByareaId(List<String> areaId) {

		StringBuffer strSql = new StringBuffer();
		int len = areaId.size();
		if(len==0){
			return null;
		}
		if(len==1){
			strSql.append(" and smoke.areaId in (?) order by smoke.mac desc");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" and smoke.areaId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?) order by smoke.mac desc");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		
		String basicSql = "select smoke.deviceType, smoke.mac,smoke.floors,smoke.storeys," +
				          "smoke.netState,smoke.address,smoke.time  from smoke  where 1 = 1 ";
		String sql = new String(basicSql+strSql);
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sql);
		ResultSet rs = null;
		List<MyDevicesVo> list = new ArrayList<>();
		try {
			for(int i=1;i<=len;i++){
				ppst.setString(i, areaId.get(i-1));
			}
			rs = ppst.executeQuery();
			while(rs.next()){
				MyDevicesVo vo = new MyDevicesVo();
				vo.setDevMac(rs.getString(2));
				vo.setFloor(rs.getString(3));
				vo.setStoreys(rs.getString(4));
				vo.setNetStates(rs.getString(5));
				vo.setAddress(rs.getString(6));
				vo.setTime(rs.getString(7));
				int devType = rs.getInt(1);
				switch (devType) {
				case 1:
					vo.setDevName("�̸�̽����");
					break;
				case 2:
					vo.setDevName("��ȼ����̽����");
					break;
				case 5:
					vo.setDevName("��������̽����");
					break;
				case 7:vo.setDevName("���ⱨ����");
					break;
				case 8:
					vo.setDevName("�ֶ�������");
					break;
				case 9:
					vo.setDevName("�����豸");
					break;	
					
				}
				list.add(vo);		
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(connection);
		}
		return list;
	}

	@Override
	public List<MyDevicesVo> getMyDevicesByareaIdPage(String userId,
			int currentPage) {
		
		String sql = "SELECT s.deviceType, s.mac,s.floors,s.storeys,s.netState,s.address,s.time  FROM smoke s,useridareaid ua WHERE 1 = 1  AND s.areaId = ua.areaId AND ua.userId = ? ORDER BY s.mac DESC LIMIT ?,10";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sql);
		ResultSet rs = null;
		List<MyDevicesVo> list = new ArrayList<>();
		try {
			ppst.setString(1, userId);
			ppst.setInt(2, (currentPage-1)*10);
			rs = ppst.executeQuery();
			while(rs.next()){
				MyDevicesVo vo = new MyDevicesVo();
				vo.setDevMac(rs.getString(2));
				vo.setFloor(rs.getString(3));
				vo.setStoreys(rs.getString(4));
				vo.setNetStates(rs.getString(5));
				vo.setAddress(rs.getString(6));
				vo.setTime(rs.getString(7));
				int devType = rs.getInt(1);
				switch (devType) {
				case 1:
					vo.setDevName("�̸�̽����");
					break;
				case 2:
					vo.setDevName("��ȼ����̽����");
					break;
				case 5:
					vo.setDevName("��������̽����");
					break;
				case 7:vo.setDevName("���ⱨ����");
					break;
				case 8:
					vo.setDevName("�ֶ�������");
					break;
				case 9:
					vo.setDevName("�����豸");
					break;	
					
				}
				list.add(vo);		
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(connection);
		}
		return list;
	}
	
	@Override
	public List<MyDevicesVo> getMyDevicesByareaIdPageAreaId(String areaId,String userId,
			int currentPage) {
		
		String sql = "SELECT s.deviceType, s.mac,s.floors,s.storeys,s.netState,s.address,s.time  FROM smoke s,useridareaid ua WHERE 1 = 1  AND s.areaId = ua.areaId AND ua.userId = ? AND s.areaId = ? ORDER BY s.mac DESC LIMIT ?,10";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(connection, sql);
		ResultSet rs = null;
		List<MyDevicesVo> list = new ArrayList<>();
		try {
			ppst.setString(1, userId);
			ppst.setString(2, areaId);
			ppst.setInt(3, (currentPage-1)*10);
			rs = ppst.executeQuery();
			while(rs.next()){
				MyDevicesVo vo = new MyDevicesVo();
				vo.setDevMac(rs.getString(2));
				vo.setFloor(rs.getString(3));
				vo.setStoreys(rs.getString(4));
				vo.setNetStates(rs.getString(5));
				vo.setAddress(rs.getString(6));
				vo.setTime(rs.getString(7));
				int devType = rs.getInt(1);
				switch (devType) {
				case 1:
					vo.setDevName("�̸�̽����");
					break;
				case 2:
					vo.setDevName("��ȼ����̽����");
					break;
				case 5:
					vo.setDevName("��������̽����");
					break;
				case 7:vo.setDevName("���ⱨ����");
					break;
				case 8:
					vo.setDevName("�ֶ�������");
					break;
				case 9:
					vo.setDevName("�����豸");
					break;	
				}
				vo.setAreaId(areaId);
				list.add(vo);		
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(connection);
		}
		return list;
	}

	@Override
	public int getCountByUserId(String userId) {
		String sqlstr = "SELECT COUNT(*)  FROM smoke s,useridareaid ua WHERE 1 = 1  AND s.areaId = ua.areaId AND ua.userId = ? ";
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public int getCountByUserIdAreaId(String userId,String areaId){
		String sqlstr = "SELECT COUNT(*)  FROM smoke s,useridareaid ua WHERE 1 = 1  AND s.areaId = ua.areaId AND ua.userId = ? AND ua.areaId = ? ";
		
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, userId);
			ps.setString(2, areaId);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	

}
