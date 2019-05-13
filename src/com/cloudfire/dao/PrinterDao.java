package com.cloudfire.dao;

import com.cloudfire.entity.PrinterAlarmResult;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.PrinterInfoEntity;

public interface PrinterDao {
	public boolean getPrinterInfo(PrinterEntity mPrinterEntity);
	public boolean ifExit(PrinterEntity mPrinterEntity);
	public void getPrinterAlarm(PrinterEntity mPrinterEntity);
	public PrinterInfoEntity getEquipmentOfOneRepeater(String repeaterMac,String page);
	public PrinterInfoEntity getEquipmentOfOneRepeater2(String repeaterMac,String page);
//	public PrinterAlarmResult getAlarmOfRepeater(String repeaterMac,String startTime,String endTime);
	public PrinterAlarmResult getAlarmOfRepeater(String repeaterMac,String startTime,String endTime,String smokeMac,String page,String faultCode);
	public void updateInfo(String repeaterMac);
	public int updateSmokeByRepeaterMac(String repeaterMac,int netState);
	public int getRepeaterType(String repeaterMac);
	
}
