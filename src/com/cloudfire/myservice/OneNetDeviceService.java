package com.cloudfire.myservice;

import com.cloudfire.entity.OneNetBody;
//import com.cloudfire.entity.OneNetResponse;
import com.cloudfire.entity.OneNetResponse;

public interface OneNetDeviceService {
	public OneNetResponse addDevice(OneNetBody onb,String deviceType); //����豸��Ϣ
	public String updateDevice(OneNetBody onb); //�����豸��Ϣ
	public String getOneDeviceInfo(String device_id,String deviceType); //�鿴�����豸����Ϣ
	public OneNetResponse delDevice(String deviceId,String deviceType);  //ɾ�������豸
	public String readDeviceResource(String deviceId); //��ȡ�豸��Դ
	public OneNetResponse writeDeviceResource(String imei,String deviceType); //д�豸��Դ
	public String sendCommand(String deviceId,String cmd,String devieType); //�·�����
	public OneNetResponse sendCommand(String imei, String deviceType);
	
	public String getAllResources(String imei);   //��ȡ��Դ�б�
	
	

}
