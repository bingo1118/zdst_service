/**
 * ����7:17:21
 */
package com.cloudfire.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.cloudfire.entity.Water;
import com.cloudfire.entity.WaterBean;
import com.cloudfire.entity.WaterEntity;
import com.cloudfire.entity.query.WaterQuery;

/**
 * @author cheng
 *2017-3-21
 *����7:17:21
 */
public interface WaterInfoDao {
	
	//���ˮѹ
	public boolean addWaterInfo(String waterRepeater,String waterMac,int status,String value);
	
	//���ˮѹ�豸����Ϣ
	public List<WaterQuery> getAllWaterInfo(List<String> areaIds,WaterQuery query);
	public int getAllWaterInfoCount(List<String> areaIds,WaterQuery query);
	
	//��ȡˮѹ�豸��¼������
	public List<WaterQuery> getAllWaterRecord(WaterQuery query);
	public int getAllWaterRecordCount(WaterQuery query);
	
	
	//�����ʪ���豸����Ϣ
	public List<WaterQuery> getAllThInfo(List<String> areaIds,WaterQuery query);
	public int getAllThInfoCount(List<String> areaIds,WaterQuery query);
	
	//��ȡˮѹ�豸��¼������
	public List<WaterQuery> getAllThRecord(WaterQuery query);
	public int getAllThRecordCount(WaterQuery query);
		
	/**
	 * �����豸MAC�鿴ˮλ��ֵ,�ж���ˮλ�ͻ���ˮλ�߲�����״̬
	 * 207��ˮλ�ͱ���
	 * 208��ˮλ�߱���
	 * 0������
	 */
	public int getWaterLeve(String waterMac,String values); //����ˮλֵ��ȡˮλ�豸�ı���״̬
	public int getWaterGage2(String waterMac, String values); //����ˮѹֵ��ȡˮѹ�豸�ı���״̬
	public int getWaterGage(String waterMac,String values); //����ˮѹֵ��ȡˮѹ�豸�ı���״̬
	/**
	 * ���ڱ���ˮλ���ݣ��������һ��һ�����򲻱��浽���ݿ�
	 */
	public boolean updateWaterLeve(Water water);
	
	public String getGage(String waterMac,int alarmFamily);
	/**
	 * ��ȡ�ߵ�ˮλֵ
	 */
	public String getWaterLow(String waterMac);
	public String getWaterHigh(String waterMac);
	
	/*
	 * ��ȡ�ߵ�ˮѹֵ
	 */
	public String getWaterLow2(String waterMac);
	public String getWaterHigh2(String waterMac);
	
	public List<Float> getWaterThreshold(String waterMac);

	public String getHighTemperature(String electricMacStr);

	public String getlowTemperature(String electricMacStr);

	public String getHighHumidity(String electricMacStr);

	public String getlowHumidity(String electricMacStr);
	
	/**
	 * ͳ�Ʒ���ˮϵͳ
	 * @throws ParseException 
	 * @throws SQLException 
	 */
	public List<WaterBean> getWaterStatistic(String startTime,String endTime,String areaId);
	public List<WaterBean> getWaterStatistic2(String startTime,String endTime,String areaId);
	
	/**
	 * ����޸�ˮѹ������ֵ
	 * Hvalue:��ˮѹ
	 * Lvalue����ˮѹ
	 */
	public int addWaterWaveValue(String repeaterMac,String waterMac,int Hvalue,int Lvalue);
	
	/**
	 * �������ˮѹ��¼
	 * @param lstWater
	 */
	public void addWaters(List<Water> lstWater); 
	
	public boolean addWaterInfo(String waterRepeater, String waterMac, int status,String value,String time) ;
	
	/**
	 * ��ȡĳ��ʱ���֮ǰ�����һ��ˮѹֵ
	 * @param time
	 * @param mac
	 * @return
	 */
	public String getWaterValue(String time,String mac);
	
	
	/**
	 * ��ȡ�̶�ʱ��������ɢʱ����ˮϵͳ�豸��ֵ,�ݶ�ʱ����Ϊ1Сʱ����ʱ���Ϊ����
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Water> getWaterRecords(String startTime,String endTime,String waterMac);
	
	/**
	 * NBˮѹˮλ�����б�
	 * @param query
	 * @return
	 */
	public List<WaterQuery> NBWaterDetailList(WaterQuery query);
	

	//����areaid��ȡˮѹ�豸�б�
	public List<WaterEntity> getWatersByAreaid(String areaid);
	//����areaid��ȡˮλ�豸�б�
	public List<WaterEntity> getWaterLevelsByAreaid(String string);
	
	public WaterEntity getWaterEntity(String waterMac);

	//����ˮλ̽����
	public List<WaterQuery> getAllWaterRecordList(WaterQuery query);

	/**
	 * ����mac��ȡ��ֹʱ�䣬У��ֵ���ϱ�ʱ����������mac,������ֵ
	 * @param waterMac
	 * @return
	 */
	public Water getWaterByMac(String waterMac);
	
	
	int updateGage(String waterValue, String waterMac, int alarmFamily);

	public boolean addHengxingFalanWaterInfo(String string, String waterMac,
			int waterStatus, String string2, long getDataTime);
	
	
}
