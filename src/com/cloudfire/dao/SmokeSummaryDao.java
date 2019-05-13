package com.cloudfire.dao;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.BQMacInfo;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SmokeSummaryEntity;

public interface SmokeSummaryDao {
	//userId=13622215085&privilege=&areaId=
	public int getSmokeSummary(String userId,String privilege,String areaId,String appId,String placeTypeId,String devType);
	public int getLostSmokeSummary(String userId,String privilege,String areaId,String appId,String placeTypeId,String devType);
	public int getDevSummary(String userId,String privilege,String parentId,String areaId,String placeTypeId,String devType);//@@liangbin
	public int getLostDevSummary(String userId,String privilege,String parentId,String areaId,String placeTypeId,String devType);//@@liangbin
//	public SmokeSummaryEntity getTotalSmokeSummary(String userId,String privilege,String areaId);
	public SmokeSummaryEntity getTotalSmokeSummary(String userId,String privilege,String areaId,String appId,String placeTypeId,String devType);//@@2017.8.11 add devType
	public SmokeSummaryEntity getTotalDevSummary(String userId,String privilege,String parentId,String areaId,String placeTypeId,String devType);//@@liangbin
	/** ͨ��macɾ��һ���̸�*/
	public void deleteBysmokeMac(String smokeMac);
	/** ͨ��mac��������ɾ���豸 */
	public void deleteBysmokeMacBate(String[] smokeMacs);
	/**
	 * ���ɾ���豸�ļ�¼ 
	 */
	public void addDelDevMac(String[] delMac,String userid,HttpServletRequest request);
	
	//add by daill    ͨ���̸�ID��ѯ�̸���Ϣ
	public boolean selectSmokeMacById(String smokeMac);
	
	//���������̸�
	public void insertSmokeMac(BQMacEntity bqMac);
	
	//ͨ������ʱ��,�豸ID��ѯ�Ƿ��������
	public boolean selectEleInfoByTime(String  deviceId,String createTime);
	
	//ͨ������ʱ��,��������,����ֵ��ѯ�Ƿ��������
	public boolean selectEleInfoByTimeValue(String createTime,String valueType,String value);
	
	//��������������������
	public void insertByType(BQMacEntity bqMac,String valueType,String value,String createTime);
	
	//�������������޸�����
	public void updateByType(BQMacEntity bqMac,String valueType,String value,String createTime);
	
	//����ʵʱ����
	public void insertNowData(BQMacInfo info);
	
	//�����豸ID��ѯ�����豸����
	public List<BQMacInfo> getDataById(String deviceId);
	
	//��ѯ���б��ؿͻ���˾
	public List<BQMacEntity> getAllBQMacForName(List<String> areaIds);
	SmokeSummaryEntity getTotalNFCSummary(String userId,String privilege,String areaId, String period,String devicetype);
	
	public SmokeSummaryEntity getSmokeSummaryTwo(String areaId,String devType);
	
	/**
	 * �����豸��ѯ�ն�
	 */
	public String getRepeaterMacBySmokeMac(String smokeMac);
}
