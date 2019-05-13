package com.cloudfire.dao;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.BQMacInfo;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SmokeSummaryEntity;

public interface SmokeSummaryDao {
	//userId=13622215085&privilege=&areaId=
	public int getSmokeSummary(String userId,String privilege,String areaId,String appId,String placeTypeId,String devType);
	public int getLostSmokeSummary(String userId,String privilege,String areaId,String appId,String placeTypeId,String devType);
	public int getDevSummary(String userId,String privilege,String parentId,String areaId,String placeTypeId,String devType);//@@liangbin
	public int getLostDevSummary(String userId,String privilege,String parentId,String areaId,String placeTypeId,String devType);//@@liangbin
//	public SmokeSummaryEntity getTotalSmokeSummary(String userId,String privilege,String areaId);
	public SmokeSummaryEntity getTotalSmokeSummary(String userId,String privilege,String areaId,String appId,String placeTypeId,String devType);//@@2017.8.11 add devType
	public SmokeSummaryEntity getTotalDevSummary(String userId,String privilege,String parentId,String areaId,String placeTypeId,String devType);//@@liangbin
	/** 通过mac删除一个烟感*/
	public void deleteBysmokeMac(String smokeMac);
	/** 通过mac数据批量删除设备 */
	public void deleteBysmokeMacBate(String[] smokeMacs);
	/**
	 * 添加删除设备的记录 
	 */
	public void addDelDevMac(String[] delMac,String userid,HttpServletRequest request);
	
	//add by daill    通过烟感ID查询烟感信息
	public boolean selectSmokeMacById(String smokeMac);
	
	//新增北擎烟感
	public void insertSmokeMac(BQMacEntity bqMac);
	
	//通过创建时间,设备ID查询是否存在数据
	public boolean selectEleInfoByTime(String  deviceId,String createTime);
	
	//通过创建时间,数据类型,数据值查询是否存在数据
	public boolean selectEleInfoByTimeValue(String createTime,String valueType,String value);
	
	//根据数据类型新增数据
	public void insertByType(BQMacEntity bqMac,String valueType,String value,String createTime);
	
	//根据数据类型修改数据
	public void updateByType(BQMacEntity bqMac,String valueType,String value,String createTime);
	
	//新增实时数据
	public void insertNowData(BQMacInfo info);
	
	//根据设备ID查询北秦设备数据
	public List<BQMacInfo> getDataById(String deviceId);
	
	//查询所有北秦客户公司
	public List<BQMacEntity> getAllBQMacForName(List<String> areaIds);
	SmokeSummaryEntity getTotalNFCSummary(String userId,String privilege,String areaId, String period,String devicetype);
	
	public SmokeSummaryEntity getSmokeSummaryTwo(String areaId,String devType);
	
	/**
	 * 根据设备查询终端
	 */
	public String getRepeaterMacBySmokeMac(String smokeMac);
}
