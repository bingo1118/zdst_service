package com.cloudfire.dao;

/**
 * @author lzo
 * ����С��λ�ĸ��ֲ�ѯ����
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
	 * ��ȡ������λ��ȫ����Ϣ
	 */
	public List<CompanyEntity> getBuildInfo();
	
	/**
	 * @author lzo
	 * ������������ȡ������λ����Ϣ
	 */
	public List<CompanyEntity> getBuildInfoByAreaId(int areaId);
	
	/**
	 * @author lzo
	 * �����豸mac����ȡ������¼��Ϣ
	 */
	public List<AlarmInfoEntity_PC> getAlarmInfo(String devMac);
	
	
	
	
	/**
	 * @author lzo
	 * �����û�����ȡ����������ѯ���������󱨵Ĵ���
	 */
	public int getAlarmByUserId(String userId);
	
	/**
	 * @author lzo
	 * �����û�����ȡ��������豸��mac��ַ����ѯ���������󱨵Ĵ���
	 */
	public int getAlarmByUserIdAndDevMac(String userId,String devMac);
	
	/**
	 * @author lzo
	 * �����û�����ѯĳ����豸�󱨴�������������̸е���������
	 */
	public int getAlarmByuserIdAndAlarmType(String userId,String deviceType);
	
	/**
	 * @author lzo
	 * �����û�����ȡ����������ѯ�豸��������Ϊ�𾯵Ĵ���
	 */
	public int getAlarmTypeByUserId(String userId);
	
	/**
	 * @author lzo
	 * �����û�����ȡ��������豸��mac��ַ����ѯ��������Ϊ�𾯵Ĵ���
	 */
	public int getAlarmTypeByUserIdAndDevMac(String userId,String devMac);
	
	/**
	 * @author lzo
	 * �����û�����ȡ����������ѯ�豸��������ʹ�õ�����
	 */
	public int getSmokeByUserId(String userId);
	/**
	 * @author lzo
	 * �����û�����ȡ��������豸��mac��ַ����ѯ��������ʹ�õĴ���
	 */
	public int getSmokeByUserIdAndDevMac(String userId,String devMac);
	
	/**
	 * @author lzo
	 * �����û�����ȡ����������ѯ�豸���ߵ�����
	 */
	public int getNoLineByUserId(String userId);
	/**
	 * @author lzo
	 * �����û�����ȡ��������豸��mac��ַ����ѯ�豸������
	 */
	public int getNoLineIdAndDevMac(String userId,String devMac);
	
	/**
	 * @author lzo
	 * �����û�����ȡ����������ѯ�豸���ڹ���״̬������
	 */
	public int getFaultByUserId(String userId);
	/**
	 * @author lzo
	 * �����û�����ȡ��������豸��mac��ַ����ѯ�豸���ڹ���״̬������
	 */
	public int getFaultIdAndDevMac(String userId,String devMac);
	
	
	/**
	 * @author lzo
	 * ���ݷ�װ����������ѯ������Ϣ,����һ��list������ϡ�
	 */
	public List<MyDevicesVo> getDevicesByC(ConditionEntity ce);
	
	
	/**
	 * @author lzo
	 * ���ݴ������Ĳ�����ͳ��     �豸 �� ��   �ݽ��з���
	 */
	public int getAlarmThBy(int deviceName,String J_xl_1,String J_xl_2,String userId);
	
	/**
	 * @author lzo
	 * ���ݴ������Ĳ�����ͳ��    ����   �ݽ��з���
	 */
	public int getDevLossThBy(int deviceName,String J_xl_1,String J_xl_2,String userId);
	
	/**
	 * @author lzo
	 * ���ݴ������Ĳ�����ͳ��   ���� �� ����   �ݽ��з���
	 */
	public int getAlarmNumThBy(int deviceName,String J_xl_1,String J_xl_2,String userId);
	
	/**
	 * @author lzo
	 * ���ݴ������Ĳ�����ͳ��   �豸 ���� ����   �ݽ��з���
	 */
	public int getDevErrNumThBy(int deviceName,String J_xl_1,String J_xl_2,String userId);
	
	/**
	 * @author lzo
	 * ���ݴ������Ĳ�����ͳ��   �豸 ���� ��������   �ݽ��з���
	 */
	public int getDevNetStateThBy(int deviceName,String J_xl_1,String J_xl_2,String userId);
	
}
