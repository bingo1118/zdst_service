package com.cloudfire.dao;

/**
 * @author lzo
 * 根据小单位的各种查询方法
 */
import java.util.List;

import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.CompanyEntity;
import com.cloudfire.entity.ConditionEntity;
import com.cloudfire.entity.MyDevicesVo;

public interface BuildingDao {
	
	/**
	 * @author lzo
	 * 获取建筑单位的全部信息
	 */
	public List<CompanyEntity> getBuildInfo();
	
	/**
	 * @author lzo
	 * 根据区域来获取建筑单位的信息
	 */
	public List<CompanyEntity> getBuildInfoByAreaId(int areaId);
	
	/**
	 * @author lzo
	 * 根据设备mac来获取报警记录信息
	 */
	public List<AlarmInfoEntity_PC> getAlarmInfo(String devMac);
	
	
	
	
	/**
	 * @author lzo
	 * 根据用户所获取的区域来查询报警当中误报的次数
	 */
	public int getAlarmByUserId(String userId);
	
	/**
	 * @author lzo
	 * 根据用户所获取的区域和设备的mac地址来查询报警当中误报的次数
	 */
	public int getAlarmByUserIdAndDevMac(String userId,String devMac);
	
	/**
	 * @author lzo
	 * 根据用户来查询某类的设备误报次数。（比如查烟感的误报数量）
	 */
	public int getAlarmByuserIdAndAlarmType(String userId,String deviceType);
	
	/**
	 * @author lzo
	 * 根据用户所获取的区域来查询设备报警类型为火警的次数
	 */
	public int getAlarmTypeByUserId(String userId);
	
	/**
	 * @author lzo
	 * 根据用户所获取的区域和设备的mac地址来查询报警类型为火警的次数
	 */
	public int getAlarmTypeByUserIdAndDevMac(String userId,String devMac);
	
	/**
	 * @author lzo
	 * 根据用户所获取的区域来查询设备正常连接使用的数量
	 */
	public int getSmokeByUserId(String userId);
	/**
	 * @author lzo
	 * 根据用户所获取的区域和设备的mac地址来查询正常连接使用的次数
	 */
	public int getSmokeByUserIdAndDevMac(String userId,String devMac);
	
	/**
	 * @author lzo
	 * 根据用户所获取的区域来查询设备掉线的数量
	 */
	public int getNoLineByUserId(String userId);
	/**
	 * @author lzo
	 * 根据用户所获取的区域和设备的mac地址来查询设备的数量
	 */
	public int getNoLineIdAndDevMac(String userId,String devMac);
	
	/**
	 * @author lzo
	 * 根据用户所获取的区域来查询设备处于故障状态的数量
	 */
	public int getFaultByUserId(String userId);
	/**
	 * @author lzo
	 * 根据用户所获取的区域和设备的mac地址来查询设备处于故障状态的数量
	 */
	public int getFaultIdAndDevMac(String userId,String devMac);
	
	
	/**
	 * @author lzo
	 * 根据封装的条件来查询数据信息,返回一个list结果集合。
	 */
	public List<MyDevicesVo> getDevicesByC(ConditionEntity ce);
	
	
	/**
	 * @author lzo
	 * 根据传过来的参数来统计     设备 误报 数   据进行分析
	 */
	public int getAlarmThBy(int deviceName,String J_xl_1,String J_xl_2,String userId);
	
	/**
	 * @author lzo
	 * 根据传过来的参数来统计    掉线   据进行分析
	 */
	public int getDevLossThBy(int deviceName,String J_xl_1,String J_xl_2,String userId);
	
	/**
	 * @author lzo
	 * 根据传过来的参数来统计   报警 火警 数量   据进行分析
	 */
	public int getAlarmNumThBy(int deviceName,String J_xl_1,String J_xl_2,String userId);
	
	/**
	 * @author lzo
	 * 根据传过来的参数来统计   设备 故障 总数   据进行分析
	 */
	public int getDevErrNumThBy(int deviceName,String J_xl_1,String J_xl_2,String userId);
	
	/**
	 * @author lzo
	 * 根据传过来的参数来统计   设备 正常 工作总数   据进行分析
	 */
	public int getDevNetStateThBy(int deviceName,String J_xl_1,String J_xl_2,String userId);
	
}
