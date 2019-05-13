/**
 * 上午10:45:01
 */
package com.cloudfire.myservice;

import java.util.List;

import com.cloudfire.entity.query.SearchElectricInfo;

import common.page.Pagination;

/**
 * @author cheng
 *2017-5-4
 *上午10:45:01
 */
public interface ElectricInfoService {
	Pagination getElectricListWithPage(List<String> areaIds,SearchElectricInfo query);
	
	Pagination getElectricHuListWithPage(List<String> areaIds,SearchElectricInfo query);
	/**
	 * @param NB电气数据处理
	 * @return
	 */
	Pagination get_NB_ElectricList(List<String> areaIds,SearchElectricInfo query);
}
