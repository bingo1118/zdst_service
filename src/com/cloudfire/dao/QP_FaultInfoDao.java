package com.cloudfire.dao;

import com.cloudfire.entity.QP_FaultInfos;

public interface QP_FaultInfoDao {
	/**
	 * @param deviceId 根据ID查询传输装置的详细数据信息
	 * @return
	 */
	public QP_FaultInfos getFaultInfos(String deviceId); 
	
	/**
	 * 添加桥平传输装置基本数据楼栋信息
	 */
	public boolean addFaultInfos(QP_FaultInfos qpf);
	
	/**
	 * 修改桥平传输张志基本数据楼栋信息
	 */
	public boolean updateFaultInfo(QP_FaultInfos qpf);
}
