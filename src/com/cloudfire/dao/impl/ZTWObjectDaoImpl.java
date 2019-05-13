package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.ZTWObjectDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.ZTWObjectEntity;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IntegerTo16;

public class ZTWObjectDaoImpl implements ZTWObjectDao {

	@Override
	public void saveObject(ZTWObjectEntity ztwObj) {
		
		String deviceMac = ztwObj.getDevImei_V();	//设备MAC
//		String ztwDevType = ztwObj.getDevType_V();//设备类型
		String devImsi = ztwObj.getDevImsi_V();	//imsi
//		String rssiValue = ztwObj.getSignal_V(); //信号强度
//		String battery = ztwObj.getBattery_V();	//电池量
		String alarmType = ztwObj.getDevState_V();	//设备状态
		String estateId = ztwObj.getEstate_V();	//小区ID
		String receiptSignal = ztwObj.getReceiptSignal_V();//接收信号强度
		String signal_noise = ztwObj.getSignal_noise_V();//信噪比
		String coverageLeve = ztwObj.getCoverageLeve_V();//覆盖等级
		String earfcn = ztwObj.getEARFCN_V();	//频点
//		IntegerTo16.parseInt(rssiValue);
		saveSmoke(ztwObj);

	}
	
	public void saveSmoke(ZTWObjectEntity ztw) {
		StringBuffer sql = new StringBuffer();
		sql.append("update smoke set netState = 1");
		if(StringUtils.isNotBlank(ztw.getBattery_V())){ //电池量
			sql.append(",lowVoltage = ").append(ztw.getBattery_V());
		}
		if(StringUtils.isNotBlank(ztw.getSignal_V())){
			sql.append(",rssivalue = ").append(ztw.getSignal_V()); //信号强度
		}
		
		sql.append(",heartime = '").append(GetTime.ConvertTimeByLong());
		sql.append("' where mac = '").append(ztw.getDevImei_V()+"'");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		try {
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
	}

}
