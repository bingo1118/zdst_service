/**
 * 下午2:26:09
 */
package com.cloudfire.myservice.impl;

import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.entity.City;
import com.cloudfire.entity.ParentArea;
import com.cloudfire.entity.Province;
import com.cloudfire.entity.Town;
import com.cloudfire.myservice.AreaService;

/**
 * @author cheng
 *2017-6-7
 *下午2:26:09
 */
public class AreaServiceImpl implements AreaService{
	private AreaDao areaDao;
	/** 获取所有的省份 */
	public List<Province> getAllPrivinceList(){
		List<Province> listProvice = new ArrayList<>();
		areaDao = new AreaDaoImpl();
		listProvice = areaDao.getProviceList();
		return listProvice;
	}
	
	/** 通过省份的code获得城市 */
	public List<City> getCityByProviceCode(String provinceCode){
		List<City> list = new ArrayList<>();
		areaDao = new AreaDaoImpl();
		list = areaDao.getCityByProvinceCode(provinceCode);
		return list;
	}
	
	/** 通过城市的code获得城镇 */
	public List<Town> getTownByCityCode(String cityCode){
		List<Town> list = new ArrayList<Town>();
		areaDao = new AreaDaoImpl();
		list = areaDao.getTownByCityCode(cityCode);
		return list;
	}
	
	public List<ParentArea> getParentAreaByCityCode(String townCode){
		List<ParentArea> list = new ArrayList<ParentArea>();
		areaDao = new AreaDaoImpl();
		list = areaDao.getParentAreaByTownCode(townCode);
		return list;
	}
	
}
