package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.DeviceAlarmEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.RepeaterBean;

public interface RePeaterDataDao {
	/**
	 * ����MAC��ַ��ѯ�����豸����
	 * ����int
	 */
	public int getDeviceType(String mac);
	
	/**
	 * ����MAC��ַ��ѯ�����豸����
	 * ����һ������
	 */
	public DeviceAlarmEntity getDeviceEntity(String mac);
	
	/**
	 * ����MAC��ַ��ѯ�����豸�ͺ�
	 * ����int
	 */
	public int getDeviceTypeNum(String mac);
	
	/**
	 * �����������洢����������״̬
	 */
	public void saveRepeaterInfo(RePeaterData repeaterData);
	
	/**
	 * ������ʾ�ն���Ϣ���������ߣ�������������״̬
	 */
	public List<RepeaterBean> queryRepeaterInfo(RepeaterBean query);
	
	
	int countRepeaterInfo(RepeaterBean query);
	
	/**
	 * ��������ն��������ʱ��
	 */
	public void addRepeaterTime(String repeaterMac);
	
	/**
	 * ��������ն��������ʱ��
	 */
	public void addRepeaterTime(String repeaterMac,String address);
	
	/**
	 * �����ն���Ϣ�����ն��������ʱ�䡣
	 */
	public String getRepeaterTime(String repeaterMac);
	
	/**
	 * �����ն˷���ip
	 * @param repeaterMac
	 * @return
	 */
	public String getIpByRepeater(String repeaterMac);
	
	
}
