package com.cloudfire.dao;

import java.util.Calendar;
import java.util.List;

import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.FireBean;
import com.cloudfire.entity.StatisticAnalysisEntity;
import com.cloudfire.entity.StatisticBean;

public interface StatisticDao {
	
	/*�����豸����ϵͳ��ȡ����ͳ������*/
	public StatisticBean getStatistic(int deviceType, List<String> areaIds );
	//��ȡ�����豸�����ߣ�����ͳ��
	public CountValue getStatistic(List<String> areaIds); 
	
	/*����areaId��ȡ����ͳ������*/
	public StatisticBean getStatistic2(String areaId);
	public List<StatisticBean> getStatistic2(List<String> areaId);
	
	/*����ʱ��λ�ȡͳ�Ƶ����豸����*/
	public List<Integer> getStatistic3(Calendar calendar,int flag,List<String> areaIds);
	
	
	/*�����豸���ͻ�ȡ�������ͼ���������*/
	public List<FireBean> getAlarmCountByDeviceType(List<String> areaIds);
	
	
	/**
	 * ͳ��ÿ����������ߺ�������
	 */
	public List<StatisticAnalysisEntity> getStatistNum(List<String> areaIds,String parentId);
	public List<StatisticAnalysisEntity> getStatistNum2(List<String> areaIds);
}
