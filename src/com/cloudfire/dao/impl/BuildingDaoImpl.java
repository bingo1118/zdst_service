package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.hsqldb.lib.StringUtil;

import com.cloudfire.dao.BuildingDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.CompanyEntity;
import com.cloudfire.entity.ConditionEntity;
import com.cloudfire.entity.MyDevicesVo;
import com.cloudfire.until.Utils;

public class BuildingDaoImpl implements BuildingDao {

	@Override
	public List<CompanyEntity> getBuildInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT comanyName,person,phone,areaId ");
		sb.append(" FROM company  ");
		String sql = sb.toString();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<CompanyEntity> ceList = null;
		try {
			rs = ps.executeQuery();
			if(ceList == null){
				ceList = new ArrayList<CompanyEntity>();
			}
			while(rs.next()){
				CompanyEntity ce = new CompanyEntity();
				ce.setComanyName(rs.getString(1));
				ce.setPerson(rs.getString(2));
				ce.setPhone(rs.getString(3));
				ce.setAreaId(rs.getInt(4));
				
				ceList.add(ce);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ceList;
	}

	@Override
	public List<CompanyEntity> getBuildInfoByAreaId(int areaId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AlarmInfoEntity_PC> getAlarmInfo(String devMac) {
		String sqlstr = "SELECT alarmTime,alarmType,ifDealAlarm,dealTime,dealPeople FROM alarm WHERE smokeMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		List<AlarmInfoEntity_PC> alarmList = null;
		try {
			ps.setString(1, devMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(alarmList == null){
					alarmList = new ArrayList<AlarmInfoEntity_PC>();
				}
				AlarmInfoEntity_PC ae = new AlarmInfoEntity_PC();
				ae.setAlarmTime(rs.getString("alarmTime"));
				int alarmType = rs.getInt(2);
				switch (alarmType) {
				case 202:
					ae.setAlarmType("��");
					break;
				case 193:
					ae.setAlarmType("�̸е�����");
				}
				int ifdealalarm = rs.getInt(3);
				switch(ifdealalarm){
				case 1:
					ae.setIfDealAlarm("�Ѵ���");
					break;
				case 0:
					ae.setIfDealAlarm("δ����");
				}
				ae.setDealTime(rs.getString("dealTime"));
				ae.setDealPeople(rs.getString("dealPeople"));
				alarmList.add(ae);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return alarmList;
	}

	/**
	 * @author lzo
	 * �����û�����ȡ����������ѯ���������󱨵Ĵ���
	 */
	@Override
	public int getAlarmByUserId(String userId) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM alarm a,smoke s,useridareaid ua WHERE 1 = 1 AND alarmTruth='1' AND a.smokeMac=s.mac AND s.areaId=ua.areaId AND ua.userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int result = 0;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while(rs.next()){
				result = Integer.parseInt(rs.getString(1));
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

	/**
	 * @author lzo
	 * �����û�����ȡ��������豸��mac��ַ����ѯ���������󱨵Ĵ���
	 */
	@Override
	public int getAlarmByUserIdAndDevMac(String userId, String devMac) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM alarm a,smoke s,useridareaid ua WHERE 1 = 1 AND alarmTruth='1' AND a.smokeMac=s.mac AND s.areaId=ua.areaId AND ua.userId=? AND a.smokeMac= ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int result = 0;
		try {
			ps.setString(1, userId);
			ps.setString(2, devMac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = Integer.parseInt(rs.getString(1));
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
	

	/**
	 * @author lzo
	 * �����û�����ȡ����������ѯ�豸��������Ϊ�𾯵Ĵ���
	 */
	@Override
	public int getAlarmTypeByUserId(String userId) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM alarm a,smoke s,useridareaid ua WHERE 1 = 1 AND alarmType ='202' AND a.smokeMac=s.mac AND s.areaId=ua.areaId AND ua.userId=?";
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
	/**
	 * @author lzo
	 * �����û�����ȡ��������豸��mac��ַ����ѯ��������Ϊ�𾯵Ĵ���
	 */
	@Override
	public int getAlarmTypeByUserIdAndDevMac(String userId, String devMac) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM alarm a,smoke s,useridareaid ua WHERE 1 = 1 AND alarmType ='202' AND a.smokeMac=s.mac AND s.areaId=ua.areaId AND ua.userId=? AND a.smokeMac= ?";
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			ps.setString(1, userId);
			ps.setString(2, devMac);
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

	/**
	 * @author lzo
	 * �����û�����ȡ����������ѯ�豸��������ʹ�õ�����
	 */
	@Override
	public int getSmokeByUserId(String userId) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM smoke s,useridareaid ua WHERE 1 = 1 AND s.areaId=ua.areaId AND s.netState = '1' AND ua.userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int result = 0;
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

	/**
	 * @author lzo
	 * �����û�����ȡ��������豸��mac��ַ����ѯ��������ʹ�õĴ���
	 */
	@Override
	public int getSmokeByUserIdAndDevMac(String userId, String devMac) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM smoke s,useridareaid ua WHERE 1 = 1 AND s.areaId=ua.areaId AND s.netState = '1' AND ua.userId=? AND s.mac= ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int result = 0;
		try {
			ps.setString(1, userId);
			ps.setString(2, devMac);
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

	/**
	 * @author lzo
	 * �����û�����ȡ����������ѯ�豸���ߵ�����
	 */
	@Override
	public int getNoLineByUserId(String userId) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM smoke s,useridareaid ua WHERE 1 = 1 AND s.areaId=ua.areaId AND s.netState = '0' AND ua.userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int result = 0;
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

	/**
	 * @author lzo
	 * �����û�����ȡ��������豸��mac��ַ����ѯ�豸������
	 */
	@Override
	public int getNoLineIdAndDevMac(String userId, String devMac) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM smoke s,useridareaid ua WHERE 1 = 1 AND s.areaId=ua.areaId AND s.netState = '0' AND ua.userId=? AND s.mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int result = 0;
		try {
			ps.setString(1, userId);
			ps.setString(2, devMac);
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

	/**
	 * @author lzo
	 * �����û�����ȡ����������ѯ���͵�ѹ������
	 */
	@Override
	public int getFaultByUserId(String userId) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM alarm a,smoke s,useridareaid ua WHERE 1 = 1 AND alarmType ='193' AND a.smokeMac=s.mac AND s.areaId=ua.areaId AND ua.userId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int result = 0;
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

	/**
	 * @author lzo
	 * �����û�����ȡ��������豸��mac��ַ����ѯ�豸���ڹ���״̬������
	 */
	@Override
	public int getFaultIdAndDevMac(String userId, String devMac) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM smoke s,useridareaid ua WHERE 1 = 1 AND s.areaId=ua.areaId AND s.netState <> '1' AND ua.userId=? AND s.mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int result = 0;
		try {
			ps.setString(1, userId);
			ps.setString(2, devMac);
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

	/**
	 * @author lzo
	 * �����û�����ѯĳ����豸�󱨴�������������̸е���������
	 */
	@Override
	public int getAlarmByuserIdAndAlarmType(String userId, String deviceType) {
		String sqlstr = "SELECT DISTINCT COUNT(*) FROM alarm a,smoke s,useridareaid ua WHERE 1 = 1 AND alarmTruth='1' AND a.smokeMac=s.mac AND s.areaId=ua.areaId AND ua.userId=? AND s.deviceType= ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		int result = 0;
		try {
			ps.setString(1, userId);
			ps.setString(2, deviceType);
			rs = ps.executeQuery();
			while(rs.next()){
				result = Integer.parseInt(rs.getString(1));
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

	
	/**
	 * @author lzo
	 * ����������ѯ�û����豸��Ϣ
	 */
	@Override
	public List<MyDevicesVo> getDevicesByC(ConditionEntity ce) {
		List<MyDevicesVo> list = null;
		StringBuffer strBuffer = new StringBuffer();
		String sqlstr = "SELECT s.deviceType, s.mac,s.floors,s.storeys,s.netState,s.address,s.time  FROM smoke s,useridareaid ua WHERE 1 = 1  AND s.areaId = ua.areaId";
		strBuffer.append(sqlstr);
		if(Utils.isNullStr(ce.getAreaId())){
			strBuffer.append(" AND s.areaId = "+ce.getAreaId());
		}
		if(Utils.isNullStr(ce.getUserId())){
			strBuffer.append(" AND ua.userId = ? ");
		}
		if(Utils.isNullStr(ce.getDeviceType())){
			strBuffer.append(" AND s.deviceType= ? ");
		}
		if(Utils.isNullStr(ce.getMac())){
			strBuffer.append(" AND s.mac= ? ");
		}
		if(ce.getNetState()!=9){
			strBuffer.append(" AND s.netState= ? ");
		}
		if(Utils.isNullStr(ce.getStartDate())){
			strBuffer.append(" AND s.time >= ? ");
		}
		if(Utils.isNullStr(ce.getEndDate())){
			strBuffer.append(" AND s.time <= ?");
		}
		String sqlcount = strBuffer.toString();
		int sumCount = getsumcount(sqlcount,ce); //��������ȡ�ܼ�¼����
		
		sqlstr = strBuffer.toString()+" LIMIT ?,10 ";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		
		try {
			if(Utils.isNullStr(ce.getUserId())){
				ps.setString(1, ce.getUserId());
				if(Utils.isNullStr(ce.getDeviceType())){
					ps.setString(2, ce.getDeviceType());
					if(Utils.isNullStr(ce.getMac())){
						ps.setString(3, ce.getMac());
						if(ce.getNetState()!=9){
							ps.setInt(4, ce.getNetState());
							if(Utils.isNullStr(ce.getStartDate())){
								ps.setString(5, ce.getStartDate());
								if(Utils.isNullStr(ce.getEndDate())){
									ps.setString(6, ce.getEndDate());
									ps.setInt(7, ce.getLimit());
								}else{
									ps.setInt(6, ce.getLimit());
								}
							}else if(Utils.isNullStr(ce.getEndDate())){
								ps.setString(5, ce.getEndDate());
								ps.setInt(6, ce.getLimit());
							}else{
								ps.setInt(5, ce.getLimit());
							}
						}else if(Utils.isNullStr(ce.getStartDate())){
							ps.setString(4, ce.getStartDate());
							if(Utils.isNullStr(ce.getEndDate())){
								ps.setString(5, ce.getEndDate());
								ps.setInt(6, ce.getLimit());
							}else{
								ps.setInt(5, ce.getLimit());
							}
						}else if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
							ps.setInt(5, ce.getLimit());
						}else {
							ps.setInt(4, ce.getLimit());
						}
					}else if(ce.getNetState()!=9){
						ps.setInt(3, ce.getNetState());
						if(Utils.isNullStr(ce.getStartDate())){
							ps.setString(4, ce.getStartDate());
							if(Utils.isNullStr(ce.getEndDate())){
								ps.setString(5, ce.getEndDate());
								ps.setInt(6, ce.getLimit());
							}else{
								ps.setInt(5, ce.getLimit());
							}
						}else if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
							ps.setInt(5, ce.getLimit());
						}else{
							ps.setInt(4, ce.getLimit());
						}
					}else if(Utils.isNullStr(ce.getStartDate())){
						ps.setString(3, ce.getStartDate());
						if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
							ps.setInt(5, ce.getLimit());
						}else{
							ps.setInt(4, ce.getLimit());
						}
					}else if(Utils.isNullStr(ce.getEndDate())){
						ps.setString(3, ce.getEndDate());
						ps.setInt(4, ce.getLimit());
					}else{
						ps.setInt(3, ce.getLimit());
					}
				}else if(Utils.isNullStr(ce.getMac())){
					ps.setString(2, ce.getMac());
					if(ce.getNetState() != 9){
						ps.setInt(3, ce.getNetState());
						if(Utils.isNullStr(ce.getStartDate())){
							ps.setString(4, ce.getStartDate());
							if(Utils.isNullStr(ce.getEndDate())){
								ps.setString(5, ce.getEndDate());
								ps.setInt(6, ce.getLimit());
							}else{
								ps.setInt(5, ce.getLimit());
							}
						}else if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
							ps.setInt(5, ce.getLimit());
						}else {
							ps.setInt(4, ce.getLimit());
						}
					}else if(Utils.isNullStr(ce.getStartDate())){
						ps.setString(3, ce.getStartDate());
						if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
							ps.setInt(5, ce.getLimit());
						}else{
							ps.setInt(4, ce.getLimit());
						}
					}else if(Utils.isNullStr(ce.getEndDate())){
						ps.setString(3, ce.getEndDate());
						ps.setInt(4, ce.getLimit());
					}else{
						ps.setInt(3, ce.getLimit());
					}
				}else if(ce.getNetState()!=9){
					ps.setInt(2, ce.getNetState());
					if(Utils.isNullStr(ce.getStartDate())){
						ps.setString(3, ce.getStartDate());
						if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
							ps.setInt(5, ce.getLimit());
						}else{
							ps.setInt(4, ce.getLimit());
						}
					}else if(Utils.isNullStr(ce.getEndDate())){
						ps.setString(3, ce.getEndDate());
						ps.setInt(4, ce.getLimit());
					}else{
						ps.setInt(3, ce.getLimit());
					}
				}else if(Utils.isNullStr(ce.getStartDate())){
					ps.setString(2, ce.getStartDate());
					if(Utils.isNullStr(ce.getEndDate())){
						ps.setString(3, ce.getEndDate());
						ps.setInt(4, ce.getLimit());
					}else {
						ps.setInt(3, ce.getLimit());
					}
				}else if(Utils.isNullStr(ce.getEndDate())){
					ps.setString(2, ce.getEndDate());
					ps.setInt(3, ce.getLimit());
				}else{
					ps.setInt(2, ce.getLimit());
				}
			}
//			System.out.println(sqlstr);
			if(list == null){
				list = new ArrayList<MyDevicesVo>();
			}
			;
			rs = ps.executeQuery();
			while(rs.next()){
				MyDevicesVo mv = new MyDevicesVo();
				int devictType = Integer.parseInt(rs.getString("deviceType"));
				if(devictType == 1){
					mv.setDevictType("�̸б�����");
				}else if(devictType == 2){
					mv.setDevictType("ȼ��������");
				}else if(devictType == 5){
					mv.setDevictType("�������ֱ�����");
				}else if(devictType == 7){
					mv.setDevictType("���ⱨ����");
				}else if(devictType == 8){
					mv.setDevictType("�ֶ�������");
				}
				mv.setDevMac(rs.getString("mac"));
				mv.setFloor(rs.getString("floors"));
				mv.setStoreys(rs.getString("storeys"));
				int netState = Integer.parseInt(rs.getString("netState"));
				if(netState == 1){
					mv.setNetStates("����");
				}else{
					mv.setNetStates("����");
				}
				mv.setAddress(rs.getString("address"));
				mv.setTime(rs.getString("time"));
				
				list.add(mv);
			}
			MyDevicesVo mv = new MyDevicesVo();
			mv.setCurrentPage(ce.getCurrentPage());		//��ǰҳ
			mv.setTotalCount(sumCount);					//�ܼ�¼����
			if(sumCount % 10 == 0){						//��ҳ��
				mv.setTotalPage(sumCount/10);
			}else{
				mv.setTotalPage(sumCount/10 + 1);
			}
			list.add(mv);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	
	public int getsumcount(String sqlcount,ConditionEntity ce){
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlcount);
		ResultSet rs = null;
		int result = 0;
		try {
			if(Utils.isNullStr(ce.getUserId())){
				ps.setString(1, ce.getUserId());
				if(Utils.isNullStr(ce.getDeviceType())){
					ps.setString(2, ce.getDeviceType());
					if(Utils.isNullStr(ce.getMac())){
						ps.setString(3, ce.getMac());
						if(ce.getNetState()!=9){
							ps.setInt(4, ce.getNetState());
							if(Utils.isNullStr(ce.getStartDate())){
								ps.setString(5, ce.getStartDate());
								if(Utils.isNullStr(ce.getEndDate())){
									ps.setString(6, ce.getEndDate());
								}
							}else if(Utils.isNullStr(ce.getEndDate())){
								ps.setString(5, ce.getEndDate());
							}
						}else if(Utils.isNullStr(ce.getStartDate())){
							ps.setString(4, ce.getStartDate());
							if(Utils.isNullStr(ce.getEndDate())){
								ps.setString(5, ce.getEndDate());
							}
						}else if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
						}
					}else if(ce.getNetState()!=9){
						ps.setInt(3, ce.getNetState());
						if(Utils.isNullStr(ce.getStartDate())){
							ps.setString(4, ce.getStartDate());
							if(Utils.isNullStr(ce.getEndDate())){
								ps.setString(5, ce.getEndDate());
							}
						}else if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
						}
					}else if(Utils.isNullStr(ce.getStartDate())){
						ps.setString(3, ce.getStartDate());
						if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
						}
					}else if(Utils.isNullStr(ce.getEndDate())){
						ps.setString(3, ce.getEndDate());
					}
				}else if(Utils.isNullStr(ce.getMac())){
					ps.setString(2, ce.getMac());
					if(ce.getNetState() != 9){
						ps.setInt(3, ce.getNetState());
						if(Utils.isNullStr(ce.getStartDate())){
							ps.setString(4, ce.getStartDate());
							if(Utils.isNullStr(ce.getEndDate())){
								ps.setString(5, ce.getEndDate());
							}
						}else if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
						}
					}else if(Utils.isNullStr(ce.getStartDate())){
						ps.setString(3, ce.getStartDate());
						if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
						}
					}else if(Utils.isNullStr(ce.getEndDate())){
						ps.setString(3, ce.getEndDate());
					}
				}else if(ce.getNetState()!=9){
					ps.setInt(2, ce.getNetState());
					if(Utils.isNullStr(ce.getStartDate())){
						ps.setString(3, ce.getStartDate());
						if(Utils.isNullStr(ce.getEndDate())){
							ps.setString(4, ce.getEndDate());
						}
					}else if(Utils.isNullStr(ce.getEndDate())){
						ps.setString(3, ce.getEndDate());
					}
				}else if(Utils.isNullStr(ce.getStartDate())){
					ps.setString(2, ce.getStartDate());
					if(Utils.isNullStr(ce.getEndDate())){
						ps.setString(3, ce.getEndDate());
					}
				}else if(Utils.isNullStr(ce.getEndDate())){
					ps.setString(2, ce.getEndDate());
				}
			}
			rs = ps.executeQuery();
			while(rs.next()){
				result = result + 1;
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

	
	/**
	 * @author lzo
	 * ���ݴ������Ĳ�����ͳ��     �豸����   �ݽ��з���
	 */
	@Override
	public int getAlarmThBy(int deviceName, String J_xl_1, String J_xl_2,String userId) {
		String hql = "SELECT DISTINCT COUNT(*) FROM smoke s,alarm a ,useridareaid u WHERE s.mac = a.smokeMac  AND a.alarmTruth = 1 AND s.areaid = u.areaid";
		int result = getCountDevStateBy(hql, deviceName, J_xl_1, J_xl_2, userId);
		return result;
	}

	/**
	 * @author lzo
	 * ���ݴ������Ĳ�����ͳ��    �豸�����   �ݽ��з���
	 */
	@Override
	public int getDevLossThBy(int deviceName, String J_xl_1, String J_xl_2,String userId) {
		String hql = "SELECT DISTINCT COUNT(*) FROM smoke s,useridareaid u WHERE s.netState = 0 AND s.areaid = u.areaid";
		int result = getCountDevStateBy(hql, deviceName, J_xl_1, J_xl_2, userId);
		return result;
	}

	/**
	 * @author lzo
	 * ���ݴ������Ĳ�����ͳ��   ��������   �ݽ��з���
	 */
	@Override
	public int getAlarmNumThBy(int deviceName, String J_xl_1, String J_xl_2,String userId) {
		String hql = "SELECT DISTINCT COUNT(*) FROM smoke s,alarm a ,useridareaid u WHERE s.mac = a.smokeMac  AND s.areaid = u.areaid AND a.alarmType = '202'  ";
		int result = getCountDevStateBy(hql, deviceName, J_xl_1, J_xl_2, userId);
		return result;
	}

	/**
	 * @author lzo
	 * ���ݴ������Ĳ�����ͳ�� ���͵�ѹ����   �ݽ��з���
	 */
	@Override
	public int getDevErrNumThBy(int deviceName, String J_xl_1, String J_xl_2,String userId) {
		String hql = "SELECT DISTINCT COUNT(*) FROM smoke s,alarm a ,useridareaid u WHERE s.mac = a.smokeMac  AND s.areaid = u.areaid AND a.alarmType = '193' ";
		int result = getCountDevStateBy(hql, deviceName, J_xl_1, J_xl_2, userId);
		return result;
	}

	/**
	 * @author lzo
	 * ���ݴ������Ĳ�����ͳ��   �豸������������   �ݽ��з���
	 */
	@Override
	public int getDevNetStateThBy(int deviceName, String J_xl_1, String J_xl_2,String userId) {
		String hql = "SELECT DISTINCT COUNT(*) FROM smoke s,useridareaid u WHERE s.netState = 1 AND s.areaid = u.areaid";
		int result = getCountDevStateBy(hql, deviceName, J_xl_1, J_xl_2, userId);
		return result;
	}
	
	
	public int getCountDevStateBy(String hql,int deviceName,String J_xl_1, String J_xl_2,String userId){
		StringBuffer strBuffer = new StringBuffer();
		String sqlstr = hql;
		strBuffer.append(sqlstr);
		int result = 0;
		
		if(deviceName!=0){
			strBuffer.append(" AND s.deviceType = ? ");
			if(Utils.isNullStr(J_xl_1)){
				strBuffer.append(" AND s.time>= ? ");
				if(Utils.isNullStr(J_xl_2)){
					strBuffer.append(" AND s.time <= ?");
				}
			}else if(Utils.isNullStr(J_xl_2)){
				strBuffer.append(" AND s.time<= ? ");
			}
		}else if(Utils.isNullStr(J_xl_1)){
			strBuffer.append(" AND s.time >= ?");
			if(Utils.isNullStr(J_xl_2)){
				strBuffer.append(" AND s.time <= ? ");
			}
		}else if(Utils.isNullStr(J_xl_2)){
			strBuffer.append(" AND s.time <= ?");
		}
		sqlstr = strBuffer.toString() + " AND u.userId = ?";
			
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;	
			
		try {
			if(deviceName!=0){
				ps.setInt(1, deviceName);
				if(Utils.isNullStr(J_xl_1)){
					ps.setString(2, J_xl_1);
					if(Utils.isNullStr(J_xl_2)){
						ps.setString(3, J_xl_2);
						ps.setString(4, userId);
					}else{
						ps.setString(3,userId);
					}
				}else if(Utils.isNullStr(J_xl_2)){
					ps.setString(2, J_xl_2);
					ps.setString(3, userId);
				}else{
					ps.setString(2, userId);
				}
			}else if(Utils.isNullStr(J_xl_1)){
				ps.setString(1, J_xl_1);
				if(Utils.isNullStr(J_xl_2)){
					ps.setString(2, J_xl_2);
					ps.setString(3, userId);
				}else{
					ps.setString(2, userId);
				}
			}else if(Utils.isNullStr(J_xl_2)){
				ps.setString(1, J_xl_1);
				ps.setString(2, userId);
			}
			
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}
}














