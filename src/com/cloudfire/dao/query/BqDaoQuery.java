/**
 * обнГ4:32:31
 */
package com.cloudfire.dao.query;

import java.util.List;

import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.query.ElectricDTUQuery;

/**
 * @author cheng
 *2017-5-18
 *обнГ4:32:31
 */
public interface BqDaoQuery {
	public int getBqCount(BQMacEntity query,List<String> areaIds);
	
	public List<BQMacEntity> getList(BQMacEntity query,List<String> areaIds);
	
	public BQMacEntity getBqMacEntity(String deviceId);
	
	public int getElectricCount(ElectricDTUQuery query);
	
	public List<ElectricDTUQuery> getElectricDTU(ElectricDTUQuery query);
	
}
