package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.Operation;
import com.cloudfire.entity.OperationQuery;
import com.cloudfire.entity.OperationType;

public interface OperationDao {
	//���������¼
	public int saveOperationRecord(int optype,String active,String passive,String time,int status);
	
	//��ȡ���в�������
	public List<OperationType> getAllType();
	
	//��ȡ���������Ĳ�����
	int getOperationCount(OperationQuery query);
	
	//��ȡ���������Ĳ�����Ϣ
	public List<Operation> getOperations(OperationQuery query);
	
	/**
	 * ���������¼
	 * @return
	 */
	public int saveOperation(Operation operation);
	
	/**
	 * ��ȡ�����Ľ��
	 * @param waterMac
	 * @param optType
	 * @return
	 */
	public int getState(String waterMac, int optType);
	
	/**
	 * ���²�����¼��״̬
	 * @param repeaterMac
	 * @param smokeMac
	 * @param state
	 * @return
	 */
	public int updateOperationState(String smokeMac,int state);
}
