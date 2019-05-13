package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.Devices;
import com.cloudfire.entity.SearchDto;
import com.cloudfire.entity.WorkingTime;
import com.cloudfire.entity.query.BqMacType;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.until.PageBean;

public interface DeviceDao {
	public List<Devices> getAllDevices();  //获取设备名称
	
	public List<CountValue> getCountNum(String areaId);    //获取各个统计结果数据 
	
	public CountValue getCount(String areaId);			//将结果集封装成对象
	
	public CountValue getCountByMac(int devType);   //根据设备类型来进行统计
	
	public CountValue getCountByMacSearch(SearchDto dto);//根据多个条件来进行统计
	
	public CountValue getCountByPrivilege(String privilege,String areaId);

	public CountValue getCountByMacAndUserId(int devType,String userId);
	 
	 //add by daill
	public List<BQMacEntity> getBqMacEntity(String deviceId,SearchDto dto,int pageNo,int pageSize);//查询所有北秦的设备数据
	 
	public List<BqMacType> getBqMacType();//查询所有北秦的设备类型
	public List<DeviceNetState> getBqMacStatus(String deviceId);//查询所有的北秦的状态
	 
	public int getBqCount(SearchDto dto);//查询满足条件的总记录数
	 
	public List<BQMacEntity> getBqMacEntitiesByName(String name);//根据公司名查找北秦Mac
	
	public int getDeviceTypeByMac(String mac);
	
	public String getDeviceName(int deviceType); //根据设备类型id,获取设备类型名字
	
	public List<Integer> getAllFire(List<String> areaIds);
	
	public List<Devices> getDeviceNums(List<String> areaIds); //获取各类型的设备数量
	public List<Devices> getAlarmNums(List<String> areaIds); //获取各类型的真实报警数量
	
	public List<Devices> getStaticsByDeviceType(List<String> areaIds);
	
	/**
	 * 修改上班通知数据信息
	 */
	public void updateUserInfo(String working,String userId);
	
	public WorkingTime selectParentIdByUserId(String id);
	
	/**
	 * 获取上班用户信息
	 */
	public Map<String,String> getUserWorking();
	/**
	 * 获取上班通知用户信息
	 */
	public List<WorkingTime> getWorkingTime(String userId);
	
	public void uploadImg(String imgPath, String mac);
	
	public String getImgSrc(String mac);
	
	public void uploadVideo(String videoPath, String mac);
	
	public String getVideoSrc(String mac);
	
}
