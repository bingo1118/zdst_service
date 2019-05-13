/**
 * ����2:24:56
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
 *����2:24:56
 */
public interface AreaService {
	
	/** ��ȡ���е�ʡ�� */
	public List<Province> getAllPrivinceList();
	
	/** ͨ��ʡ�ݵ�code��ó��� */
	public List<City> getCityByProviceCode(String provinceCode);
	
	/** ͨ�����е�code��ó��� */
	public List<Town> getTownByCityCode(String cityCode);
	
	/**ͨ�������id��ø�����id */
	public List<ParentArea> getParentAreaByCityCode(String townCode);
	
}
