package com.cloudfire.dao;


public interface WorkOrderDao {
	/**
	 * ����һ���µĹ���
	 * @param w
	 */
	public boolean insertOrder(String mac ,String alarmType,String deviceType,String alarmAddress);
}
