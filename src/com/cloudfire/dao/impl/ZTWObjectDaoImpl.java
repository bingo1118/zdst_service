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
		
		String deviceMac = ztwObj.getDevImei_V();	//�豸MAC
//		String ztwDevType = ztwObj.getDevType_V();//�豸����
		String devImsi = ztwObj.getDevImsi_V();	//imsi
//		String rssiValue = ztwObj.getSignal_V(); //�ź�ǿ��
//		String battery = ztwObj.getBattery_V();	//�����
		String alarmType = ztwObj.getDevState_V();	//�豸״̬
		String estateId = ztwObj.getEstate_V();	//С��ID
		String receiptSignal = ztwObj.getReceiptSignal_V();//�����ź�ǿ��
		String signal_noise = ztwObj.getSignal_noise_V();//�����
		String coverageLeve = ztwObj.getCoverageLeve_V();//���ǵȼ�
		String earfcn = ztwObj.getEARFCN_V();	//Ƶ��
//		IntegerTo16.parseInt(rssiValue);
		saveSmoke(ztwObj);

	}
	
	public void saveSmoke(ZTWObjectEntity ztw) {
		StringBuffer sql = new StringBuffer();
		sql.append("update smoke set netState = 1");
		if(StringUtils.isNotBlank(ztw.getBattery_V())){ //�����
			sql.append(",lowVoltage = ").append(ztw.getBattery_V());
		}
		if(StringUtils.isNotBlank(ztw.getSignal_V())){
			sql.append(",rssivalue = ").append(ztw.getSignal_V()); //�ź�ǿ��
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
