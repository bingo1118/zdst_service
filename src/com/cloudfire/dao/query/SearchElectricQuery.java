package com.cloudfire.dao.query;

import java.util.List;

import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.until.core.dao.HibernateDao;

/**
 * @author cheng
 *2017-4-5
 *上午9:19:17
 */

public interface SearchElectricQuery {
	//多条件查询电气火灾设备
	public List<SearchElectricInfo> searchElectricInfo(SearchElectricInfo query,List<String> areaList);
	//@lzo update 8-1
	public List<SearchElectricInfo> searchElectricInfos(SearchElectricInfo query,List<String> areaList);
	public List<SearchElectricInfo> searchElectricInfo2(SearchElectricInfo query,List<String> areaList);
	public int getElectricInfoCount(SearchElectricInfo query,List<String> areaList);
	
	public List<SearchElectricInfo> searchElectricHu(SearchElectricInfo query,List<String> areaList);
	//电弧设备总数量
	public int getElectricHuCount(SearchElectricInfo query,List<String> areaList);
	
	/**
	 * 获取NB电气数量统计
	 */
	public int getNB_ElectricInfoCount(SearchElectricInfo query,List<String> areaList);
	/**
	 * 获取NB电气数据
	 */
	public List<SearchElectricInfo> searchNBElectricInfo(SearchElectricInfo query,List<String> areaList);
	
	public SearchElectricInfo queryDeviceTypeByMac(String mac,List<String> areaids);
	
	//根据设备mac获取设备详情
	public SearchElectricInfo getDeviceDetail(String smokeMac);
}
