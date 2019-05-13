/**
 * обнГ4:28:21
 */
package com.cloudfire.myservice;

import java.util.List;

import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.query.ElectricDTUQuery;

import common.page.Pagination;

/**
 * @author cheng
 *2017-5-18
 *обнГ4:28:21
 */
public interface BqService {
	public Pagination getBqListPage(BQMacEntity query,List<String> areaIds);
	
	public BQMacEntity getOnetBqMacEntity(String deviceId);
	
	
	public Pagination getElectriDTUListPage(ElectricDTUQuery query);
}
