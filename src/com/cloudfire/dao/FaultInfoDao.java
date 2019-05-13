package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.FaultDataEntity;
import com.cloudfire.entity.FaultInfoEntity;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.query.FaultInfoEntityQuery;

/**
 * @author lzo
 *	有线传输装置
 */
public interface FaultInfoDao {
	public List<FaultInfoEntity> getFaultInfoAll();
	
	//1级查询主机
	public int selectFaultCount(FaultInfoEntityQuery query,List<String> areaIds);
	
	public int selectFaultCountBySmoke(FaultInfoEntityQuery query,List<String> areaIds);
	
	public List<FaultInfoEntity> selectFaultInfo(FaultInfoEntityQuery query,List<String> areaIds);
	
	public List<FaultInfoEntity> selectFaultInfoBySmoke(FaultInfoEntityQuery query,List<String> areaIds);
	
	//2级查询主机下的设备
	public int selectFaultCount2(FaultInfoEntityQuery query,List<String> areaIds,String hostCode,String faultCode);
	
	public List<FaultInfoEntity> selectFaultInfo2(FaultInfoEntityQuery query,List<String> areaIds,String repeater,String faultCode);
	
	//3级查询某一个设备的历史记录
	public int selectFaultCount3(FaultInfoEntityQuery query,String repeater,String faultCode);
	
	public List<FaultInfoEntity> selectFaultInfo3(FaultInfoEntityQuery query,String repeater,String faultCode);
	
	public int insertFaultByHW(PrinterEntity ePrinter);
	
	/**
	 * 用于保存传输装置数据信息（faultinfoSystem
	 */
	public void saveFaultInfoSystem(FaultDataEntity faultData);
	/**
	 * 用于保存传输装置数据信息（faultinfoSystem
	 */
	public int insertFaultInfoSystem(FaultDataEntity faultData);
	/**
	 * 用于保存传输装置数据信息（faultinfo)
	 */
	public int insertFaultInfo(FaultDataEntity faultData);
	
	public int insertSJFaultInfo(FaultDataEntity faultData);
	
	/**
	 * 用于保存传输装置数据信息（faultinfo)
	 */
	public int insertFaultInfoQP(FaultDataEntity faultData);
}
