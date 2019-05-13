package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AckDeviceBean;
import com.cloudfire.entity.AckToSoundAndDevice;
import com.cloudfire.entity.SmokeBean;

public interface PublicUtils {
	
	/**
	 * 根据声光MAC查询绑定的设备
	 */
	public List<AckDeviceBean> selectDevBean(String soundMac);
	/**
	 * 查询终端下的设备，用于绑定声光使用
	 */
	public List<SmokeBean> selectDevByRep(String repeater);
	
	/**
	 * 查询已绑定的声光设备对应关系数据
	 */
	public List<AckDeviceBean> selectAckToDevByRep(String repeater);
	
	/**
	 * 返回封装两个集合，方便前端处理。
	 */
	public List<AckToSoundAndDevice> selectAckBySAD(String repeaterMac);
	
	/**
	 * 根据两个数组来保存数据
	 */
	public void saveAckBySAD(String repeaterMac,String[] electricValue,String[] deviceResponseArray);
	
	/**
	 * 根据声光来删除数据
	 */
	public void deleteAckBySoundMac(String soundMac);
	
	/**
	 * bind smoke by userid use sanxiao changsuo
	 */
	public int bindSmokeByUserName(String userName,String[] smokes);
	
	/**
	 * bind camera by userid use sanxiao changsuo
	 */
	public int bindCameraByUserName(String userName,String[] smokes);
	
	/**
	 * bind repeater by userid use sanxiao changsuo
	 */
	public int bindRepeaterByUserName(String userName,String[] smokes);
	
	/**
	 * bind repeater by userid use sanxiao changsuo
	 */
	public int bindSmokeSoundRepeater(String repeaterMac,String soundMac,String devMac);
	
	/**
	 * if LossUp of DeviceMac 
	 */
	public int updateDeviceMac(String deviceMac);
	
	/**
	 * 针对设备状态进行保存操作（1消音，0无）
	 * @param deviceMac
	 * @param devState
	 * @return
	 */
	public int updateDeviceMac(String deviceMac,int devState);
	
	/**
	 * if LossUp of DeviceMac and rssi 
	 */
	public int updateDevBattery(String deviceMac,String DevBattery);
	
	public int updateElectricSwitch(String deviceMac,String elecSwitch);
	
	/**
	 *  DeviceMac and rssi 
	 */
	public int updateDeviceMac(String deviceMac,String rssi);
	
	/**
	 * 删除6个月之前的数据
	 */
	public void deleteSixData();
	
	/**
	 * 根据用户查询绑定的摄像头
	 */
	public List<String> selectCamera(String username);
	
	/**
	 * 根据用户和摄像头ID进行单个解绑
	 */
	public int delCamera(String username,String devMac);
	
	
	/**
	 * 根据用户查询绑定的主机
	 */
	public List<String> selectRepeater(String username);
	
	/**
	 * 根据用户和主机ID进行单个解绑
	 */
	public int delRepeater(String username,String devMac);
	
	
	/**
	 * 根据用户查询绑定的摄像头
	 */
	public List<String> selectSmoke(String username);
	
	/**
	 * 根据用户和摄像头ID进行单个解绑
	 */
	public int delSmoke(String username,String devMac);
	
	/**
	 * 查询绑定的传输装置下绑定的数据信息
	 */
	public List<AckDeviceBean> selectFaultInfo(String repeaterMac);
	
	
	/**
	 * 查询绑定的传输装置下绑定的数据信息
	 */
	public int delFaultInfo(String repeaterMac,String soundMac,String devMac);
	
	/**
	 * 判断是否过时
	 */
	public boolean ifOverTime(String times);
	
	//查询电量状态
	public int getVoltageState(String mac);
	
	//更新电量状态为正常
	public int updateVoltageState(String deviceMac);
	//更新电池电量状态 0 正常 1低电量
	public int updateVoltageState(String deviceMac, int voltage);
	int updateDeviceOnlineState(String deviceMac, int state, long time);
	
}
