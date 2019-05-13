package com.cloudfire.dao;


public interface WorkOrderDao {
	/**
	 * 生成一个新的工单
	 * @param w
	 */
	public boolean insertOrder(String mac ,String alarmType,String deviceType,String alarmAddress);
}
