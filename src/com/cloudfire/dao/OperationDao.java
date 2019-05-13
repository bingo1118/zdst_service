package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.Operation;
import com.cloudfire.entity.OperationQuery;
import com.cloudfire.entity.OperationType;

public interface OperationDao {
	//保存操作记录
	public int saveOperationRecord(int optype,String active,String passive,String time,int status);
	
	//获取所有操作类型
	public List<OperationType> getAllType();
	
	//获取符合条件的操作数
	int getOperationCount(OperationQuery query);
	
	//获取符合条件的操作信息
	public List<Operation> getOperations(OperationQuery query);
	
	/**
	 * 保存操作记录
	 * @return
	 */
	public int saveOperation(Operation operation);
	
	/**
	 * 获取操作的结果
	 * @param waterMac
	 * @param optType
	 * @return
	 */
	public int getState(String waterMac, int optType);
	
	/**
	 * 更新操作记录的状态
	 * @param repeaterMac
	 * @param smokeMac
	 * @param state
	 * @return
	 */
	public int updateOperationState(String smokeMac,int state);
}
