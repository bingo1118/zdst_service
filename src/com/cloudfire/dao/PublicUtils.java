package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AckDeviceBean;
import com.cloudfire.entity.AckToSoundAndDevice;
import com.cloudfire.entity.SmokeBean;

public interface PublicUtils {
	
	/**
	 * ��������MAC��ѯ�󶨵��豸
	 */
	public List<AckDeviceBean> selectDevBean(String soundMac);
	/**
	 * ��ѯ�ն��µ��豸�����ڰ�����ʹ��
	 */
	public List<SmokeBean> selectDevByRep(String repeater);
	
	/**
	 * ��ѯ�Ѱ󶨵������豸��Ӧ��ϵ����
	 */
	public List<AckDeviceBean> selectAckToDevByRep(String repeater);
	
	/**
	 * ���ط�װ�������ϣ�����ǰ�˴���
	 */
	public List<AckToSoundAndDevice> selectAckBySAD(String repeaterMac);
	
	/**
	 * ����������������������
	 */
	public void saveAckBySAD(String repeaterMac,String[] electricValue,String[] deviceResponseArray);
	
	/**
	 * ����������ɾ������
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
	 * ����豸״̬���б��������1������0�ޣ�
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
	 * ɾ��6����֮ǰ������
	 */
	public void deleteSixData();
	
	/**
	 * �����û���ѯ�󶨵�����ͷ
	 */
	public List<String> selectCamera(String username);
	
	/**
	 * �����û�������ͷID���е������
	 */
	public int delCamera(String username,String devMac);
	
	
	/**
	 * �����û���ѯ�󶨵�����
	 */
	public List<String> selectRepeater(String username);
	
	/**
	 * �����û�������ID���е������
	 */
	public int delRepeater(String username,String devMac);
	
	
	/**
	 * �����û���ѯ�󶨵�����ͷ
	 */
	public List<String> selectSmoke(String username);
	
	/**
	 * �����û�������ͷID���е������
	 */
	public int delSmoke(String username,String devMac);
	
	/**
	 * ��ѯ�󶨵Ĵ���װ���°󶨵�������Ϣ
	 */
	public List<AckDeviceBean> selectFaultInfo(String repeaterMac);
	
	
	/**
	 * ��ѯ�󶨵Ĵ���װ���°󶨵�������Ϣ
	 */
	public int delFaultInfo(String repeaterMac,String soundMac,String devMac);
	
	/**
	 * �ж��Ƿ��ʱ
	 */
	public boolean ifOverTime(String times);
	
	//��ѯ����״̬
	public int getVoltageState(String mac);
	
	//���µ���״̬Ϊ����
	public int updateVoltageState(String deviceMac);
	//���µ�ص���״̬ 0 ���� 1�͵���
	public int updateVoltageState(String deviceMac, int voltage);
	int updateDeviceOnlineState(String deviceMac, int state, long time);
	
}
