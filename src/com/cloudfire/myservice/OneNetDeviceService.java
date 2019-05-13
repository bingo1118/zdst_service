package com.cloudfire.myservice;

import com.cloudfire.entity.OneNetBody;
//import com.cloudfire.entity.OneNetResponse;
import com.cloudfire.entity.OneNetResponse;

public interface OneNetDeviceService {
	public OneNetResponse addDevice(OneNetBody onb,String deviceType); //添加设备信息
	public String updateDevice(OneNetBody onb); //更新设备信息
	public String getOneDeviceInfo(String device_id,String deviceType); //查看单个设备的信息
	public OneNetResponse delDevice(String deviceId,String deviceType);  //删除单个设备
	public String readDeviceResource(String deviceId); //读取设备资源
	public OneNetResponse writeDeviceResource(String imei,String deviceType); //写设备资源
	public String sendCommand(String deviceId,String cmd,String devieType); //下发命令
	public OneNetResponse sendCommand(String imei, String deviceType);
	
	public String getAllResources(String imei);   //获取资源列表
	
	

}
