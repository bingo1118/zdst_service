/**
 * 下午3:32:54
 */
package com.cloudfire.myservice;

import java.util.List;


import com.cloudfire.entity.query.WaterQuery;

import common.page.Pagination;

/**
 * @author cheng
 *2017-5-10
 *下午3:32:54
 */
public interface WaterService {
	/** 选择所有的水压设备 */
	Pagination selectAllWaterListWithPage(List<String> areaIds,WaterQuery query);
	
	/** 水压数据记录记录 */
	Pagination selectAllWaterRecordWithPage(WaterQuery query);
	
	/**选择所有的湿温度设备*/
	Pagination selectAllThListWithPage(List<String> areaIds,WaterQuery query);
	
	/** 湿温度数据记录记录 */
	Pagination selectAllThRecordWithPage(WaterQuery query);
	
	/** 水压水位数据记录记录 */
	 Pagination NBWaterDetail(WaterQuery query);
	 
	/** 无线水位探测器数据记录记录 */
	Pagination waterViewRecordPage(WaterQuery query);

}
