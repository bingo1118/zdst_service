package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.PrinterDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.FaultInfoEntity;
import com.cloudfire.entity.PrinterAlarmResult;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.PrinterInfo;
import com.cloudfire.entity.PrinterInfoEntity;
import com.cloudfire.until.LogHelper;
import com.cloudfire.until.Utils;

public class PrinterDaoImpl implements PrinterDao{

	public boolean getPrinterInfo(PrinterEntity mPrinterEntity) {
		// TODO Auto-generated method stub
		boolean result = ifExit(mPrinterEntity);
		String sql=null;
		if(result){
			sql="update masterequipment set equipmentFaultInfo=?,equipmentDesc=?,equipmentFaultType=?,updateTime=? " +
					" where mac=? and repeater=?";
		}else{
			sql = "insert masterequipment (equipmentFaultInfo,equipmentDesc,equipmentFaultType,updateTime,mac,repeater) values (?,?,?,?,?,?)";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, mPrinterEntity.getFaultInfo());
			ps.setString(2, mPrinterEntity.getFaultDevDesc());
			ps.setString(3, mPrinterEntity.getFaultType());
			ps.setString(4, mPrinterEntity.getFaultTime());
			ps.setString(5, mPrinterEntity.getFaultCode());
			ps.setString(6, mPrinterEntity.getRepeater());
			int rs = ps.executeUpdate();
			if(rs<1){
				result=false;
			}else{
				result=true;
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

	public boolean ifExit(PrinterEntity mPrinterEntity) {
		// TODO Auto-generated method stub
		String sql = "select * from masterequipment where mac=? and repeater=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		boolean result=false;
		try {
			ps.setString(1, mPrinterEntity.getFaultCode());
			ps.setString(2, mPrinterEntity.getRepeater());
			rs = ps.executeQuery();
			if(rs.next()){
				result=true;
			}
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

	public void getPrinterAlarm(PrinterEntity mPrinterEntity) {
		// TODO Auto-generated method stub
		String sql = "insert faultinfo (faultInfo,faultDevDesc,faultType,faultTime,faultCode,repeaterMac) " +
				"values (?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, mPrinterEntity.getFaultInfo());
			ps.setString(2, mPrinterEntity.getFaultDevDesc());
			ps.setString(3, mPrinterEntity.getFaultType());
			ps.setString(4, mPrinterEntity.getFaultTime());
			ps.setString(5, mPrinterEntity.getFaultCode());
			ps.setString(6, mPrinterEntity.getRepeater());
			int rs = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	public PrinterInfoEntity getEquipmentOfOneRepeater(String repeaterMac,String page) {
		// TODO Auto-generated method stub
//		String sql="SELECT s.* FROM (SELECT repeaterMac,faultCode,faultDevDesc,faultType,faultInfo,faultTime FROM faultinfo WHERE repeaterMac = ? ORDER BY faultTime DESC ) AS s GROUP BY faultCode";
		String sql = "SELECT mac,repeater,equipmentFaultinfo,equipmentDesc,equipmentFaultType,updateTime,hostType from masterequipment WHERE repeater = ? ";
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				sql = sql + " limit "+startNum+" , "+endNum;
			}
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		PrinterInfoEntity mPrinterInfoEntity = null; 
		List<FaultInfoEntity> lists = null;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(lists==null){
					lists = new ArrayList<FaultInfoEntity>();
				}
				FaultInfoEntity mFaultInfo = new FaultInfoEntity();
				mFaultInfo.setFaultCode(rs.getString("mac"));
				mFaultInfo.setRepeaterMac(rs.getString("repeater"));
				mFaultInfo.setFaultDevDesc(rs.getString("equipmentDesc"));
				mFaultInfo.setFaultType(rs.getString("equipmentFaultType"));
				mFaultInfo.setFaultInfo(rs.getString("equipmentFaultinfo"));
				mFaultInfo.setFaultTime(rs.getString("updateTime"));
				mFaultInfo.setHostType(rs.getString("hostType"));
				lists.add(mFaultInfo);
			}
			if(lists!=null&&lists.size()>0){
				mPrinterInfoEntity = new PrinterInfoEntity(); 
				mPrinterInfoEntity.setError("获取设备成功");
				mPrinterInfoEntity.setErrorCode(0);
				mPrinterInfoEntity.setFaultment(lists);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return mPrinterInfoEntity;
	}
	public PrinterInfoEntity getEquipmentOfOneRepeater2(String repeaterMac,String page) {
		// TODO Auto-generated method stub
		String sql="SELECT s.* FROM (SELECT repeaterMac,faultCode,faultDevDesc,faultType,faultInfo,faultTime FROM faultinfo WHERE repeaterMac = ? ORDER BY faultTime DESC ) AS s GROUP BY faultCode,faultDevDesc";
//		String sql = "SELECT mac,repeater,equipmentFaultinfo,equipmentDesc,equipmentFaultType,updateTime,hostType from masterequipment WHERE repeater = ? ";
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				sql = sql + " limit "+startNum+" , "+endNum;
			}
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		System.out.println("sqlfaultinfo="+sql);
		ResultSet rs = null;
		PrinterInfoEntity mPrinterInfoEntity = null; 
		List<FaultInfoEntity> lists = null;
		try {
			ps.setString(1, repeaterMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(lists==null){
					lists = new ArrayList<FaultInfoEntity>();
				}
				FaultInfoEntity mFaultInfo = new FaultInfoEntity();
				mFaultInfo.setFaultCode(rs.getString("faultCode"));
				mFaultInfo.setRepeaterMac(rs.getString("repeaterMac"));
				mFaultInfo.setFaultDevDesc(rs.getString("faultDevDesc"));
				mFaultInfo.setFaultType(rs.getString("faultType"));
				mFaultInfo.setFaultInfo(rs.getString("faultInfo"));
				mFaultInfo.setFaultTime(rs.getString("faultTime"));
				mFaultInfo.setHostType("224");
				lists.add(mFaultInfo);
			}
			if(lists!=null&&lists.size()>0){
				mPrinterInfoEntity = new PrinterInfoEntity(); 
				mPrinterInfoEntity.setError("获取设备成功");
				mPrinterInfoEntity.setErrorCode(0);
				mPrinterInfoEntity.setFaultment(lists);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return mPrinterInfoEntity;
	}

	public PrinterAlarmResult getAlarmOfRepeater(String repeaterMac,
			String startTime, String endTime,String faultCode,String page,String faultDesc) {
		// TODO Auto-generated method stub
		String sql="select faultCode,faultDevDesc,faultType,faultInfo,faultTime from faultinfo where repeaterMac=? ";
		LogHelper.d("@@@@@@@@@@@@@@@@@@"+faultDesc);
		if(Utils.isNullStr(faultCode)){
			sql = sql +" and faultCode = ? ";
		}
		
		if(Utils.isNullStr(faultDesc)){
			sql = sql + " and faultDevDesc = ? ";
		}
		
		if(Utils.isNullStr(startTime)&&Utils.isNullStr(endTime)){
			sql = sql+" and faultTime between ? and ?";
		}
		
		sql = sql + " order by faultTime desc";
		
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum = 20;
				sql = sql + " limit "+startNum+" , "+endNum;
			}
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		PrinterAlarmResult mPrinterAlarmResult = null;
		List<PrinterEntity> lists = null;
		try {
			ps.setString(1, repeaterMac);
			ps.setString(2, faultCode);
			ps.setString(3, faultDesc);
			if(Utils.isNullStr(startTime)&&Utils.isNullStr(endTime)){
				ps.setString(4, startTime);
				ps.setString(5, endTime);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(lists==null){
					lists = new ArrayList<PrinterEntity>();
				}
				PrinterEntity mPrinterEntity = new PrinterEntity();
				mPrinterEntity.setFaultCode(rs.getString(1));
				mPrinterEntity.setFaultDevDesc(rs.getString(2));
				mPrinterEntity.setFaultInfo(rs.getString(4));
				mPrinterEntity.setFaultTime(rs.getString(5));
				mPrinterEntity.setFaultType(rs.getString(3));
				mPrinterEntity.setRepeater(repeaterMac);
				lists.add(mPrinterEntity);
			}
			if(lists!=null&&lists.size()>0){
				mPrinterAlarmResult = new PrinterAlarmResult();
				mPrinterAlarmResult.setAlarm(lists);
				mPrinterAlarmResult.setError("获取报警成功");
				mPrinterAlarmResult.setErrorCode(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return mPrinterAlarmResult;
	}

	public void updateInfo(String repeaterMac) {
		// TODO Auto-generated method stub
		String sql="update masterequipment set equipmentFaultType=? where repeater=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, "正常");
			ps.setString(2, repeaterMac);
			int rs = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public int updateSmokeByRepeaterMac(String repeaterMac,int netState){
		int result = 0;
		String sql = "UPDATE smoke SET netstate = ? WHERE repeater = ? AND repeater = mac";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setInt(1, netState);
			ps.setString(2, repeaterMac);
			result = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public int getRepeaterType(String repeaterMac){
		int result = 0;
		String sql = "SELECT deviceType FROM smoke WHERE mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, repeaterMac);
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
	
}
