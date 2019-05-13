/**
 * 下午2:24:56
 */
package com.cloudfire.myservice;

import java.util.List;

import com.cloudfire.entity.City;
import com.cloudfire.entity.ParentArea;
import com.cloudfire.entity.Province;
import com.cloudfire.entity.Town;

/**
 * @author cheng
 *2017-6-7
 *下午2:24:56
 */
public interface AreaService {
	
	/** 获取所有的省份 */
	public List<Province> getAllPrivinceList();
	
	/** 通过省份的code获得城市 */
	public List<City> getCityByProviceCode(String provinceCode);
	
	/** 通过城市的code获得城镇 */
	public List<Town> getTownByCityCode(String cityCode);
	
	/**通过城镇的id获得父级的id */
	public List<ParentArea> getParentAreaByCityCode(String townCode);
	
}
