package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.FaultDataEntity;
import com.cloudfire.entity.FaultInfoEntity;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.query.FaultInfoEntityQuery;

/**
 * @author lzo
 *	���ߴ���װ��
 */
public interface FaultInfoDao {
	public List<FaultInfoEntity> getFaultInfoAll();
	
	//1����ѯ����
	public int selectFaultCount(FaultInfoEntityQuery query,List<String> areaIds);
	
	public int selectFaultCountBySmoke(FaultInfoEntityQuery query,List<String> areaIds);
	
	public List<FaultInfoEntity> selectFaultInfo(FaultInfoEntityQuery query,List<String> areaIds);
	
	public List<FaultInfoEntity> selectFaultInfoBySmoke(FaultInfoEntityQuery query,List<String> areaIds);
	
	//2����ѯ�����µ��豸
	public int selectFaultCount2(FaultInfoEntityQuery query,List<String> areaIds,String hostCode,String faultCode);
	
	public List<FaultInfoEntity> selectFaultInfo2(FaultInfoEntityQuery query,List<String> areaIds,String repeater,String faultCode);
	
	//3����ѯĳһ���豸����ʷ��¼
	public int selectFaultCount3(FaultInfoEntityQuery query,String repeater,String faultCode);
	
	public List<FaultInfoEntity> selectFaultInfo3(FaultInfoEntityQuery query,String repeater,String faultCode);
	
	public int insertFaultByHW(PrinterEntity ePrinter);
	
	/**
	 * ���ڱ��洫��װ��������Ϣ��faultinfoSystem
	 */
	public void saveFaultInfoSystem(FaultDataEntity faultData);
	/**
	 * ���ڱ��洫��װ��������Ϣ��faultinfoSystem
	 */
	public int insertFaultInfoSystem(FaultDataEntity faultData);
	/**
	 * ���ڱ��洫��װ��������Ϣ��faultinfo)
	 */
	public int insertFaultInfo(FaultDataEntity faultData);
	
	public int insertSJFaultInfo(FaultDataEntity faultData);
	
	/**
	 * ���ڱ��洫��װ��������Ϣ��faultinfo)
	 */
	public int insertFaultInfoQP(FaultDataEntity faultData);
}
