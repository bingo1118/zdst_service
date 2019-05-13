package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SmokeInfo;

public interface SmokeDao {
	public List<SmokeBean> getSmokesByAreaid(int areaid);
	
	public SmokeBean getSmokeByMac(String mac);
	
	public SmokeBean getSmokeByMac2(String mac);
	
	public SmokeBean getSmokeByMac3(String mac);
	
	public int getDeviceTypeByMac(String mac);
	
	public  List<Integer> getDevices(String userId,int privilege);
	
	public String getHeartTimeByMac(String mac);
	
	public boolean updateHeartTime(String mac);

	public boolean deleteDevFromSmoke(String smokeMac); 
	/**
	 * ����ȡ��������ӵ����ݿ���
	 * @param s
	 * @return
	 */
	public boolean addOrUpdateSmoke(SmokeInfo s);
	/**
	 * ��ѯ�ͻ�ƽ̨��������
	 * @return
	 */
	public int queryCountCustomerSmoke();
	/**
	 * ��ѯ�Ƿ���ʾ�ͻ�ƽ̨���ݵĲ���
	 * @return
	 */
	public int selectIfShow();
	/**
	 * �޸��Ƿ���ʾ�ͻ�ƽ̨���ݵĲ���
	 * @param ifShow
	 * @return
	 */
	public boolean updateIfShow(int ifShow);

	public List<SmokeBean> getSmokesByBuilding(String buildingname);
	
	public String getDeviceTypeNameByType(int deviceType);

	public String getAlarmNameByAlarmType(int alarmtype);

	public int getDeviceMiniType(int deviceType);
}
