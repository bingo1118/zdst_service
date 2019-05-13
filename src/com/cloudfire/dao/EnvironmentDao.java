package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.EnvironmentEntity;
import com.cloudfire.entity.EnvironmentHistoryMsgEntity;
import com.cloudfire.entity.SmokeBean;

public interface EnvironmentDao {
	/**
	 * ���ݷ�װ�Ļ���̽����͸������õ����ݰ������ӻ��޸����ݡ�
	 * @param environment
	 */
	public void addEnvironmentInfo(EnvironmentEntity environment);
	
	/**
	 * ��ȡ�豸����Ϊ11-12-13���豸��Ϣ
	 * @return
	 */
	public AllSmokeEntity getNotSmokeMac(String userId, String privilege,String page);
	
	public AllSmokeEntity getSuperNotSmokeMac(String userId, String privilege,String page);
	
	/**
	 * ��ȡ
	 * @param airMac
	 * @return
	 */
	public EnvironmentEntity getEnvironmentEntityInfo(String airMac);
	
	/**
	 * get 7 day data
	 */
	public EnvironmentHistoryMsgEntity getHistoryData(String deviceMac,String timeStart,String timeEnd,int type);
}
