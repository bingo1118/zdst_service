package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.CompanyEntity;
import com.cloudfire.entity.MyDevicesEntityQuery;
import com.cloudfire.entity.PageBeanEntity;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.query.MainIndexEntityQuery;
import com.cloudfire.entity.query.TenanceEntityQuery;

public interface InfoManagerDao {
	
	/**
	 * @author lzo
	 * @param 根据单位性质和单位名称的物建单位来查询company表信息，返回结果保存到一个List结果集合中
	 */
	public List<CompanyEntity> getInfoByProvinceID(String provinceID,String companyName,String userId);
	
	/**
	 * @author lzo
	 * @param 根据区域下面的物建单位来查询company表信息，返回结果保存到一个List结果集合中
	 */
	public List<CompanyEntity> getInfoAll(String areaId,String companyName);
	
	/**
	 * @author lzo
	 * @param 根据建筑单位名称，单位性质，行业类型来查寻数据，返回结果保存到一个List结果集合中
	 */
	public List<CompanyEntity> getInfoAll(String companyName,String companyNature,String involed);
	
	public List<MyDevicesEntityQuery> getInfoAllCount(List<String> areaIds,MyDevicesEntityQuery query);
	
	public int getInfoCount(List<String> areaIds,MyDevicesEntityQuery query);
	
	/**
	 * @author lzo
	 * @param 不带条件，直接查询所有数据，返回结果保存到一个List结果集合中
	 */
	public List<CompanyEntity> getInfoAlls();
	
	/**
	 * @author lzo
	 * @param 根据区域来查询数据，返回结果保存到一个List结果集合中
	 */
	public List<CompanyEntity> getInfoAll(String areaId,TenanceEntityQuery query);
	
	public int getInfoByAreaId(String areaId,TenanceEntityQuery query); //获取company的总记录数。
	
	public List<SmokeBean> getAllSmokesByQuery(String areaId,MainIndexEntityQuery query);  //获取smoke的信息。多表查询
	
	public int getSmokeByQuery(String areaId,MainIndexEntityQuery query);
	
	public List<SmokeBean> getAllSmokesInfo(String areaId);  //获取smoke的信息。用于导出excel表
	
	
	/**
	 * @author lzo
	 * @param 根据用户ID所具备的权限级别来查询数据，返回结果保存到一个List结果集合中
	 */
	public List<SmokeBean> getAllSmokes(String userId,int privilege);
	
	public int getTotalCount(String areaId);		//獲取總記錄數

	/**
	 * @author lzo
	 * @param 根据用户ID所具备的权限级别来查询数据，返回结果保存到一个List结果集合中
	 */
	
	public List<SmokeBean> getAllSmokes(PageBeanEntity pageBean,String userId,String areaId);
	
	
	/**
	 * @author lzo
	 * @param 根据用户ID来查询数据，返回结果保存到一个List结果集合中
	 */
	public List<SmokeBean> getAllSmokes(String userId);
	
	/**
	 * @author lzo
	 * @param 根据区域ID来查询数据，返回结果保存到一个List结果集合中
	 */
	public List<SmokeBean> getSmokesByAreaId(String areaId,PageBeanEntity pageBean,String userId);
	
	
	/**
	 * @author lzo
	 * @param 根据场所性质来查询数据，比如三小场所，返回结果保存到一个List结果集合中
	 */
	public List<SmokeBean> getSmokeByCharacterId(String characterId);
	
	/**
	 * @author lzo
	 * @param 根据场所性质来查询数据，比如三小场所，返回结果保存到一个List结果集合中
	 */
	public List<SmokeBean> getSmokeByIdAndName(String characterId,String charaId,String companyName,PageBeanEntity pageBean);
	
	/**
	 * @author lzo
	 * @param 根据场所性质来查询数据，比如三小场所，返回结果保存到一个List结果集合中
	 */
	public List<SmokeBean> getSmokeByIdAndName(String characterId,String charaId,String companyName,int areaId,PageBeanEntity pageBean);
	
	
	/**
	 * @author lzo
	 * @param 查询所有行业类型
	 */
	public List<ShopTypeEntity> getShopTypeEntity();
	
	
	
}
