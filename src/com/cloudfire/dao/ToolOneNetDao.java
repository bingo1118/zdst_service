package com.cloudfire.dao;

import com.cloudfire.action.MsgOneNetEntity;
import com.cloudfire.entity.OneNetEntity;


public interface ToolOneNetDao {
	
	/**
	 * ����豸��OneNetƽ̨
	 * �����豸dev_idֵ
	 */
	public String insertOneNetDev(String imei,String imsi,String title,String deviceType);
	
	/**
	 * ����IMEI��IMSI��ֵ�������ݿ�
	 */
	public void addOneNetTable(String imei,String imsi,String devId);
	
	//�����豸��ds_id
	public void updateDs(String imei,String ds_id);
	
	//����device_id��ȡ�豸��imei+ds_id
	public MsgOneNetEntity getMsgByDeviceId(String device_id);
	
	//����device_id��ȡ�豸��imei+ds_id
	public MsgOneNetEntity getMsgByImei(String imei);
	
	/**
	 * ����JSON�������ض���ֵ�������豸devid,��valueֵ���Լ�����ֵ
	 */
	public OneNetEntity getOneNetValues(String jsonString);
	
	/**
	 * ����device_id ��ȡ�豸imei
	 * @param device_id
	 * @return
	 */
	public String getMacByDeviceId(String device_id);
	
	/**
	 * ɾ��OneNetƽ̨�ϵ��豸
	 * @param imei
	 * @return
	 */
	public boolean delDeviceByMac(String imei);
	
	/**
	 * ����imei��ȡ�豸��device_id
	 * @param imei
	 * @return
	 */
	public String getDeviceIdByImei(String imei);

	/**
	 * ��������
	 * @param imei �εµ��ƶ��̸�
	 * @param deviceType
	 * @return
	 */
	public String sendCmd(String imei, String deviceType);
	
	/**
	 * �����豸������Ϣ��ͬʱ�ж��Ƿ��ǵ����ظ�
	 */
	public int updatePower(String mac,String power);
	
	//�����豸������Ϣ
	public int updatePower2(String mac,String power);

	//���µ�����Ϣ���͵���״̬
	public int updatePower(String mac, String string, int voltageState);
}

