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
	 * @return NFC�����ݵ���������û�����ݣ�����ʾ����ȫ��
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
	 * @pra ͨ�������id��ȡ���������
	 */
	public String getAreaNameById(int areaId);
	public String getAreaNameById(String areaId);
	public int getIsTxtById(int areaId);
	/**
	 * ���������ȡ���й����������ά����Ա
	 * @param areaId
	 * @return
	 */
	public List<String> getUserByAreaId(String areaId);
	/**
	 * @author lzo
	 * @par ���������򷽷���iDֵȡareaId�����ֵ�ڼ�1��ΪareaId
	 */
	// public boolean addArea(ParentArea parentArea);

	/**
	 * @author lzo
	 * @param area
	 *            �ж����������Ƿ���ڣ�������ڷ���true,���򷵻�false
	 */
	public boolean ifexitArea(String area);


	/** ��ȡʡ���б� */
	public List<Province> getProviceList();

	/** ͨ��ʡ�ݵ�code��ó��� */
	public List<City> getCityByProvinceCode(String provinceCode);

	/** ͨ�����е�code��ó��� */
	public List<Town> getTownByCityCode(String cityCode);

	public List<ParentArea> getParentAreaByTownCode(String townCode);

	/** ��Ӹ��������� */
	public boolean addParentArea(ParentArea parentArea);

	/** ��Ӷ������� */
	public boolean addSecondArea(SecondArea secondArea);
	
	/** ��Ӷ������� ��������ID*/
	public int insertSecondArea(SecondArea secondArea);

	public List<SmokeBean> getSmokeInfo(String smokeMac);

	public List<SmokeBean> saveSmokeInfo(String smokeMac, String longitude,
			String latitude);

	public AreaBeanEntity getAllAreaInfo(String userId, String privilege);

	public AllAreaEntity getAllAreas(String userId, String privilege);
	
	/**��������ID�͸�����ID�������޸İ󶨹�ϵ	 */
	public int updatePareaIdAreaId(int areaid,int pareantid);
	
	/**
	 * ���������ȡ�����豸�ľ���γ�ȡ�
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
	 * ����MQTTͨ������������Ϣ
	 */
	public PushAlarmMqttEntity getalarmInfo(String mac);
	
	public AreaidGoEasy queryAppidAppkey(String areaid);

	public String getWaterAreaStr(String userId, String privilege);
	
	public boolean updateAreaTxtState(int areaId,int num);
	
	/**
	 * ���ڲ�ѯ���������һ��������Ϣ
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
