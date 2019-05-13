package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.AllAreaEntity;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.AreaBeanEntity;
import com.cloudfire.entity.AreaIdEntity;
import com.cloudfire.entity.AreaParentEntity;
import com.cloudfire.entity.AreaidGoEasy;
import com.cloudfire.entity.City;
import com.cloudfire.entity.ParentArea;
import com.cloudfire.entity.Province;
import com.cloudfire.entity.PushAlarmMqttEntity;
import com.cloudfire.entity.RepeaterLevelUp;
import com.cloudfire.entity.SecondArea;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SmokeXYZ;
import com.cloudfire.entity.Town;

public interface AreaDao {
	public Map<Integer, String> getAreaById(List<String> includeStr);

	public Map<Integer, String> getAreaById2(List<String> includeStr, int type);// @@

	public List<String> getAreaStr(String userId, String privilege);
	
	public List<String> getAreaStr(String userId, String privilege,int[] devices);
	
	public List<String> getAreaStr(String parentId);

	public List<String> getAreaStrByMac(List<String> listMac);

	public AreaIdEntity getAreaByUserId(String userId, String privilege,
			String page);

	public String getOneAreaName(int areaId);

	public Map<Integer, String> getAllArea(String userid);

	public List<AreaBean> getAll();
	
	public List<AreaBean> getAll(String userid);
	
	public Map<Integer,String> getParentAll();
	
	public Map<Integer,String> getParentAll(String userid);

	public List<AreaAndRepeater> getAllAreaByUserId(String userId,String privilege);
	
	public List<AreaAndRepeater> getAllWaterAreaByUserId(String userId,String privilege);
	
	public List<AreaAndRepeater> getWaterAreaByUserId(String userId,String privilege);
	
	public List<AreaAndRepeater> getWaterLeveAreaByUserId(String userId,String privilege);
	
	/**
	 * @author lzo
	 * @return NFC有数据的区域。如若没有数据，则显示中文全部
	 */
	public List<AreaAndRepeater> getNFCAreaByUserId(String userId,
			String privilege);
	public List<AreaBean> getNFCAreaByUserId2(String userId,
			String privilege);
	public List<AreaAndRepeater> getNFCAreasByUserId(String userId,
			String privilege);
	public List<AreaAndRepeater> getNFCRecordAreasByUserId(String userId,
			String privilege);
	public List<AreaAndRepeater> getAllAreaByUserIdLg(String userId,
			String privilege);

	public int updateAreaByareaId(AreaBean areaBean);

	public int deleteAreaById(String areaId);

//	public List<AreaAndRepeater> getAllAreaByUserIdFirstId(String userId,
//			String privilege);

	/**
	 * @author lzo
	 * @pra 通过区域的id获取区域的名字
	 */
	public String getAreaNameById(int areaId);
	public String getAreaNameById(String areaId);
	public int getIsTxtById(int areaId);
	/**
	 * 根据区域获取所有关联该区域的维护人员
	 * @param areaId
	 * @return
	 */
	public List<String> getUserByAreaId(String areaId);
	/**
	 * @author lzo
	 * @par 增加新区域方法，iD值取areaId的最大值在加1做为areaId
	 */
	// public boolean addArea(ParentArea parentArea);

	/**
	 * @author lzo
	 * @param area
	 *            判断区域名字是否存在，如果存在返回true,否则返回false
	 */
	public boolean ifexitArea(String area);


	/** 获取省份列表 */
	public List<Province> getProviceList();

	/** 通过省份的code获得城市 */
	public List<City> getCityByProvinceCode(String provinceCode);

	/** 通过城市的code获得城镇 */
	public List<Town> getTownByCityCode(String cityCode);

	public List<ParentArea> getParentAreaByTownCode(String townCode);

	/** 添加父级的区域 */
	public boolean addParentArea(ParentArea parentArea);

	/** 添加二级区域 */
	public boolean addSecondArea(SecondArea secondArea);
	
	/** 添加二级区域 返回区域ID*/
	public int insertSecondArea(SecondArea secondArea);

	public List<SmokeBean> getSmokeInfo(String smokeMac);

	public List<SmokeBean> saveSmokeInfo(String smokeMac, String longitude,
			String latitude);

	public AreaBeanEntity getAllAreaInfo(String userId, String privilege);

	public AllAreaEntity getAllAreas(String userId, String privilege);
	
	/**根据区域ID和父区域ID，进行修改绑定关系	 */
	public int updatePareaIdAreaId(int areaid,int pareantid);
	
	/**
	 * 根据区域获取所有设备的经度纬度。
	 */
	public List<SmokeXYZ> getAllXYZ(List<String> list);

	public String getAreaIdByMac(String mac);
	
	public int getAreaIdBySmokeMac(String mac);
	
	public int getAreaIdByRepeater(String repeater);
	
	public int getSuperAreaIdBySmokeMac(String mac);
	
	public int getSuperAreaIdByRepeater(String repeater);
	
	public int getIfStartMqqt(String mac);
	
	public int getIfMqqtByParentId(int parentId);
	/**
	 * 报警MQTT通信推送数据信息
	 */
	public PushAlarmMqttEntity getalarmInfo(String mac);
	
	public AreaidGoEasy queryAppidAppkey(String areaid);

	public String getWaterAreaStr(String userId, String privilege);
	
	public boolean updateAreaTxtState(int areaId,int num);
	
	/**
	 * 用于查询区域二级和一级区域信息
	 */
	public List<AreaParentEntity> getParentList(List<String> areaids);
	
	public Map<String,String> getRepeaterList(String areaId);
	
	public List<RepeaterLevelUp> getParentList(String areaId);
	
	public List<RepeaterLevelUp> getParentList(String areaId,String parentId);
	
	public void updateRepeaterTime(String repeaterMac,int leveUpState);
	
	public List<RepeaterLevelUp> getParentLists(String[] repeaters);

	public AreaIdEntity getDXAreaId(String userId, String privilege, String page);
	
	public Map<String, String> getAllParentArea();
	
	public Map<String,String> getAreasByParentId(String parentId);
}
