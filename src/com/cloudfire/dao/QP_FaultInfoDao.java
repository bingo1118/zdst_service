package com.cloudfire.dao;

import com.cloudfire.entity.QP_FaultInfos;

public interface QP_FaultInfoDao {
	/**
	 * @param deviceId ����ID��ѯ����װ�õ���ϸ������Ϣ
	 * @return
	 */
	public QP_FaultInfos getFaultInfos(String deviceId); 
	
	/**
	 * �����ƽ����װ�û�������¥����Ϣ
	 */
	public boolean addFaultInfos(QP_FaultInfos qpf);
	
	/**
	 * �޸���ƽ������־��������¥����Ϣ
	 */
	public boolean updateFaultInfo(QP_FaultInfos qpf);
}
