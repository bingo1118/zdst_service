package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.MyDevicesVo;

/**
 * @author cheng
 *2017-3-20
 *下午1:43:45
 *建筑单位,我的设备的信息
 */
public interface IMyDevices {
	
	public List<MyDevicesVo> getMyDevicesMsg(List<String> areaId);
	
	
	public List<MyDevicesVo> getMyDevicesByareaId(List<String> areaId);
	
	/**
	 * @author lzo
	 * @return 分页查询
	 */
	public List<MyDevicesVo> getMyDevicesByareaIdPage(String userId,int currentPage);
	
	public List<MyDevicesVo> getMyDevicesByareaIdPageAreaId(String areaId,String userId,int currentPage);
	
	public int getCountByUserId(String userId); //统计总记录数量
	
	public int getCountByUserIdAreaId(String userId,String areaId);
}
