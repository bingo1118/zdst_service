package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.DX_NB_NJ_DataMqttEntity;
import com.cloudfire.entity.EasyIOT;
import com.cloudfire.entity.NanJing_NB_IOT_Entity;

public interface ToolNanJinDao {
	
	/**
	 * lora�����·���������
	 * @param mac
	 * @param eCmd
	 * @param repeaterMac
	 * @param dCmd
	 * @return
	 */
	public byte[] ackLoraUte(String mac,byte eCmd,String repeaterMac,int dCmd,String shuntState,String Leakage,String Undervoltage,String Overcurrent,String Overvoltage,String temperature,String heartime);
	
	//update smoke by imei field is rssi/blow/
	public void updateSmokeInfo(NanJing_NB_IOT_Entity entity);
	
	/**
	 * @param deviceId �豸��dev�ţ���������
	 * @param devid �豸����ID
	 * @param conCmd ��������
	 * @return
	 */
	public boolean reSetThread(String deviceId,int devid,int conCmd,String Leakage,String Unervoltage,String Overcurrent,String Overvoltage,String temperature,String currentMAX,String shuntState);
	
	
	/**
	 * @param deviceId �豸��dev�ţ���������
	 * @param devid �豸����ID
	 * @param conCmd ��������
	 * @return
	 */
	public boolean reSetThread(String deviceId,int devid,int conCmd,String Leakage,String Unervoltage,String Overcurrent,String Overvoltage);
	
	
	//ɾ���豸
	public boolean deleteGasInfo(String imei);
	
	//ɾ���豸
	public boolean deleteDevFromSmoke(String smokeMac);
	
	//get imei info from smoke table
	public DX_NB_NJ_DataMqttEntity getImeiInfo(String imei);
	
	//nanjing jiade yangan xiaoyin
	public boolean getCancelCounld(String deviceId);
	public boolean getCancelCounld(String deviceId,int devid);
	public boolean getCancelCounld(String deviceId,int devid,String devState);
	
	//nanjing shuiya yuzhi shijian shezhi
	//	hight_set (2)��ѹ��ֵ //	low_set (2)��ѹ��ֵ //	collect_time (2)�ɼ�ʱ�� //	send_time (2)�ϱ�ʱ��
	public boolean reSet_water_data(String deviceId,int devid,int conCmd,String hight_set,String low_set,String collect_time,String send_time);
	
//	Low_TempSet (2)������ֵ //	Hight_TempSet (2)������ֵ //Low_HumiSet  ��ʪ�� //Hight_HumiSet ��ʪ��   Tcollect_time (2)�ɼ�ʱ�� //	Tsend_time (2)�ϱ�ʱ��
	public boolean reSet_TempHumi_data(String deviceId,int devid,int conCmd,String low_TempSet,String hight_TempSet,String low_HumiSet,String Hight_HumiSet,String Tcollect_time,String Tsend_time);
	
	
	public int getDeviceTypeByDev(String smokeMac); 
	
	//add imei_deviceId info
	public void addImeiDeviceId(String imei,String deviceId);
	public void addImeiDeviceId(String imei,String deviceId,String imsi);
	
	//get deviceId  by iemi
	public String getDeviceByImei(String imei);
	
	//get imei  by deviceId
	public String getImeiBydeviceId(String deviceId);
	/**
	 * ����appid��acrest ����ȡtoken
	 */
	public String getTokenByAppId(String appId,String secret);
	
	/**
	 * ע�ᶩ�ĵ�ַ
     * ��ƽ̨ע�������Ӧ�ý�����Ϣ�Ļص���ַ���ö��ĵ�ַ��ƽ̨�������Ӧ�ûص��Ľӿڵ�ַ�̶����֡��ӿڵĿɱ䲿�ֲμ����ص��ӿڵĶ��塣
	 * @param accessToken accessToken="����д�ɹ���½���accessToken";
	 * @param serverID serverID="����д�û���";
	 * @param callbackUrl ���ڽ��պ�������Ϣ�ĵ�ַcallbackUrl="����д������Ϣ��ַ";
	 * @return
	 */
	public String subAccessToKen(String accessToken,String serverID,String callbackUrl);
	
	/**
	 * �豸����
     * ͨ�����ӿ����豸���Ϳ������ͨ������ӿڻ��ָ���ִ�н����
	 * @param accessToken accessToken="����д�ɹ���½���accessToken";
	 * @param serverID serverID="����д�û���";
	 * @param devSerial devSerial="����д�豸���к�,��IMEI";
	 * @param method method="����д��������id";
	 * @param value ="method"��ֵ
	 * @return
	 */
	public String ackAccessToKen(String accessToken,String serverID,String devSerial,String method,String key,int value);
	
	/**
	 * @param param �������ֵ
	 * @return
	 */
	public String ackAccessToKens(String accessToken,String serverID,String devSerial,String method,Map<String,Integer> param);
	
	/**
	 * ��ѯIoT����ƽ̨
		�ӿ�˵������ȡ�����ӵ��������ն����ӹ���ƽ̨��Ϣ��
		����ӿ���Ҫ����������ͻ�Ӧ���ṩ���豸ע��ӿ������롰connectPointId���ֶ�ֵ���á�
		�����ڡ����豸ע�ᡱ�����豸����ע�ᡱ�ӿ��У���Ҫ���ݵ� connectPointIdֵ����Ҫ�ӱ��ӿ��л�ȡ��
		ע�����ӿ����ݽ����ȡһ�μ��ɣ��û��ɽ���ȡ�����ݱ������������������ٴε��ã�
		����ע��ʱ����������ѭ���з������á� 
	 */
	public String connectionPlatform(String serverID,String accessToken);
	
	/**
	 * ��ѯ IoT ƽ̨����ģʽ
	 * �ӿ�˵������ȡĳ��IoT����ƽ̨��֧�ֵķ���ģʽ��
	 * ����ģʽ���豸ע��ʱѡ�������ģʽ������PSM��eDRX�ȣ�
	 * ������һ���豸��NB-IoT���������е�ģʽ��
	 * ÿ��IoTƽ̨��֧�ֵ�ģʽ���ܲ�ͬ��
	 * ������ƽ̨ͨ�����ӿڲ�ѯĳ��IoT����ƽ̨��֧�ֵ�ҵ��ģʽ��
	 * �����豸ע��ҳ���������������ʽ��ʾ���û�ѡ��
	 */
	public String queryServiceMode(String serverID,String accessToken,String iotserverId);
	
	/**
	 * ���豸ע�� 
	 * �ӿ�˵������ƽ̨ע�ᵥ���豸���κ�һ���豸��ֻ�о���ע�Ტ�ɹ��󣬲��ܽ��п��ƺͶ�����Ϣ��
	 * ���ñ�ע���豸�ӿ�ǰ��������connectPointId����Idͨ��ǰ���ġ���ѯIoT����ƽ̨���ӿڻ�á�
	 * @param serverID
	 * @param accessToken
	 * @param devSerial ��ѡStringbody�豸���кţ�Ŀǰʹ�õ���ģ���Ӧ��IMEI��
	 * @param name ��ѡString(1-256)body�豸����
	 * @param deviceType ��ѡString(1-256)body�豸���ͣ���Ʒ�ͺţ������Ѵ����Ĳ�Ʒ����
	 * @param connectPointId��ѡStringbody�豸���ӵ�IDֵЯ���ӡ���ѯIoT����ƽ̨���ӿڵķ���ֵ��ĳ��ƽ̨��IDֵ
	 * @param serviceMode��ѡStringbody����ģʽ���ƣ������á���ѯ IoT ƽ̨����ģʽ���ӿڵķ���ֵ�е�ĳ������ģʽ��ȱʡΪPSMģʽ��Ŀǰ��HW-test-iot-117���͡�HW-test-iot-112���������ӵ��֧��PSMģʽ����ctc-nanjing-iot-137��ͬʱ֧��PSM��eDRXģʽ��
	 * @param endUserName��ѡString(1-256)body�ն��û����ƣ����ֻ������
	 * @param endUserInfo��ѡString(1-256)body�ն��û���Ϣ�����ֻ������
	 * @param location��ѡString(1-1024)body�豸��λ��
	 * @param longitude��ѡFloatbody�豸����װλ�õľ���
	 * @param latitude��ѡFloatbody�豸����װλ�õ�γ��
	 * @param extend_type��ѡStringbody	��չ���Ͳ������粻ȷ����������
	 * @return
	 */
	public String registeredPlant(String serverID, String accessToken,
			String devSerial, String name, String deviceType,
			String connectPointId, String serviceMode, String endUserName,
			String endUserInfo, String location, String longitude,
			String latitude, String extend_type);
	
	/**
	 * ����һ����ע���豸����Ϣ��
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param devSerial	�豸���к� = "����дҪ���µ��豸���к�,��IMEI";
	 * @param name	�豸���ƣ���ѡ = "����д�µ��豸����";
	 * @param longitude	���ȣ���ѡ = "����д�µľ���";
	 * @param latitude	γ�ȣ���ѡ = "����д�µ�γ��";
	 * @return
	 */
	public String updateDevice(String serverID,String accessToken,String devSerial,String name,String longitude,String latitude);
	
	/**
	 * ɾ���豸
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param devSerial	�豸���к� = "����дҪ���µ��豸���к�,��IMEI";
	 * @return
	 */
	public String deleteDevice(String serverID,String accessToken,String devSerial);
	
	/**
	 * �豸��Ϣ��ѯ
	 * ��ѯһ����ע���豸����ϸ��Ϣ�������豸�Ļ�����Ϣ����ǰҵ�����ݣ��澯���ݣ��Լ���ʱ��������ݡ�
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param devSerial	�豸���к� = "����дҪ���µ��豸���к�,��IMEI";
	 * @return
	 */
	public String queryDevice(String serverID,String accessToken,String devSerial);
	
	/**
	 * ��ѯ�豸���ͣ���Ʒ�ͺţ���Ϣ
	 * ͨ�����ӿڲ�ѯһ���豸���ͣ���Ʒ�ͺţ�����Ϣ
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param devType	ָ���豸����(��Ʒ�ͺ�)	="����д�豸����(��Ʒ�ͺ�)";
	 * @return
	 */
	public String queryDeviceType(String serverID, String accessToken, String devType);
	
	/**
	 * ��ѯ���п��õ��豸���ͣ���Ʒ�ͺţ�
	 * �ӿ�˵������ѯһ��ƽ̨�ʺ������п������豸ע����豸���ͣ���Ʒ�ͺţ�
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @return
	 */
	public String queryAllAvailableDeviceTypes(String serverID,String accessToken);
	
	/**
	 * ��ѯ�û������豸
	 * ��ѯһ��ƽ̨�ʺ���ע��������豸
	 * @param serverID ��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @return
	 */
	public String queryUserAllDevice(String serverID,String accessToken);
	
	/**
	 * ��ѯ�豸ҵ������
	 * �ӿ�˵����������ͨ���ýӿڲ�ѯ�豸��ҵ��������ʷ��¼
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param devSerial	�豸���к� = "����дҪ���µ��豸���к�,��IMEI";
	 * @return
	 */
	public String queryTheDeviceDusinessData(String serverID,String accessToken,String devSerial);
	
	/**
	 * ȡ�����ĵ�ַ
	 * ��ƽ̨ȡ��������ƽ̨������Ϣ�Ļص���ַ
	 * @param serverID ��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @return
	 */
	public String unSubscribe(String serverID,String accessToken);
	
	/**
	 * ��ѯ���ĵ�ַ
	 * ��ƽ̨��ѯ������ƽ̨������Ϣ�Ļص���ַ����������ע��Ķ��ĵ�ַ�Ǳ�ƽ̨��õ�����ƽ̨�ص��Ľӿڵ�ַ�̶����֡��ӿڵĿɱ䲿�ֲμ����ص��ӿڵĶ��塣
	 * @param serverID ��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @return
	 */
	public String querySubscribe(String serverID,String accessToken);
	
	/**
	 * �豸��Ϣ֪ͨ
	 * ƽ̨ͨ���ýӿ��������Ӧ�ñ��������ĵ��豸������ҵ�����ݡ�
	 * @param callbackUrl  ����{callbackUrl}Ϊ�豸��Ϣ���Ľӿ����ṩ�ĵ�����ƽ̨�Ļص���ַ
	 * @param devSerial �豸���кţ�Ŀǰʹ�õ���ģ���Ӧ��IMEI
	 * @param createTime �豸ע�ᴴ��ʱ��
	 * @param lastMesssageTime ���豸����յ��豸ҵ������ʱ��
	 * @param iotEventTime IoTƽ̨���յ���Ϣ��ʱ��
	 * @param serviceData �豸ҵ�����ݣ������ṹ�μ�����ʾ�����롣
	 * @return
	 */
	public String deviceMessageNotification(String callbackUrl,String devSerial,String createTime,String lastMesssageTime,String iotEventTime,String serviceData);
	
	/**
	 * ����ע��
	 * @param serverID ��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param objdev ע����ϢList����
	 * @return
	 */
	public String registrationCompleted(String serverID,String accessToken,List<EasyIOT> objdev);
	
	/**
	 * @return
	 */
	public String queryService(String hostID);
	
	/**
	 * ���� easyIot�����ݼ�¼
	 */
	public void addIotEntity(String devSerial,String createTime,String iotEventTime,String serviceId,String IOT_key,String IOT_value);
	
	/**
	 * ע�ᶩ�ĵ�ַ
     * ��ƽ̨ע�������Ӧ�ý�����Ϣ�Ļص���ַ���ö��ĵ�ַ��ƽ̨�������Ӧ�ûص��Ľӿڵ�ַ�̶����֡��ӿڵĿɱ䲿�ֲμ����ص��ӿڵĶ��塣
	 * @param accessToken accessToken="����д�ɹ���½���accessToken";
	 * @param serverID serverID="����д�û���";
	 * @param callbackUrl ���ڽ��պ�������Ϣ�ĵ�ַcallbackUrl="����д������Ϣ��ַ";
	 * @return
	 */
	public String subAccessToKen(String accessToken,String serverID,String callbackUrl,String IotUrl);
	
	/**
	 * �豸����
     * ͨ�����ӿ����豸���Ϳ������ͨ������ӿڻ��ָ���ִ�н����
	 * @param accessToken accessToken="����д�ɹ���½���accessToken";
	 * @param serverID serverID="����д�û���";
	 * @param devSerial devSerial="����д�豸���к�,��IMEI";
	 * @param method method="����д��������id";
	 * @param value ="method"��ֵ
	 * @return
	 */
	public String ackAccessToKen(String accessToken,String serverID,String devSerial,String method,String key,int value,String IotUrl);
	
	/**
	 * @param param �������ֵ
	 * @return
	 */
	public String ackAccessToKens(String accessToken,String serverID,String devSerial,String method,Map<String,Integer> param,String IotUrl);
	
	/**
	 * ��ѯIoT����ƽ̨
		�ӿ�˵������ȡ�����ӵ��������ն����ӹ���ƽ̨��Ϣ��
		����ӿ���Ҫ����������ͻ�Ӧ���ṩ���豸ע��ӿ������롰connectPointId���ֶ�ֵ���á�
		�����ڡ����豸ע�ᡱ�����豸����ע�ᡱ�ӿ��У���Ҫ���ݵ� connectPointIdֵ����Ҫ�ӱ��ӿ��л�ȡ��
		ע�����ӿ����ݽ����ȡһ�μ��ɣ��û��ɽ���ȡ�����ݱ������������������ٴε��ã�
		����ע��ʱ����������ѭ���з������á� 
	 */
	public String connectionPlatform(String serverID,String accessToken,String IotUrl);
	
	/**
	 * ��ѯ IoT ƽ̨����ģʽ
	 * �ӿ�˵������ȡĳ��IoT����ƽ̨��֧�ֵķ���ģʽ��
	 * ����ģʽ���豸ע��ʱѡ�������ģʽ������PSM��eDRX�ȣ�
	 * ������һ���豸��NB-IoT���������е�ģʽ��
	 * ÿ��IoTƽ̨��֧�ֵ�ģʽ���ܲ�ͬ��
	 * ������ƽ̨ͨ�����ӿڲ�ѯĳ��IoT����ƽ̨��֧�ֵ�ҵ��ģʽ��
	 * �����豸ע��ҳ���������������ʽ��ʾ���û�ѡ��
	 */
	public String queryServiceMode(String serverID,String accessToken,String iotserverId,String IotUrl);
	
	/**
	 * ���豸ע�� 
	 * �ӿ�˵������ƽ̨ע�ᵥ���豸���κ�һ���豸��ֻ�о���ע�Ტ�ɹ��󣬲��ܽ��п��ƺͶ�����Ϣ��
	 * ���ñ�ע���豸�ӿ�ǰ��������connectPointId����Idͨ��ǰ���ġ���ѯIoT����ƽ̨���ӿڻ�á�
	 * @param serverID
	 * @param accessToken
	 * @param devSerial ��ѡStringbody�豸���кţ�Ŀǰʹ�õ���ģ���Ӧ��IMEI��
	 * @param name ��ѡString(1-256)body�豸����
	 * @param deviceType ��ѡString(1-256)body�豸���ͣ���Ʒ�ͺţ������Ѵ����Ĳ�Ʒ����
	 * @param connectPointId��ѡStringbody�豸���ӵ�IDֵЯ���ӡ���ѯIoT����ƽ̨���ӿڵķ���ֵ��ĳ��ƽ̨��IDֵ
	 * @param serviceMode��ѡStringbody����ģʽ���ƣ������á���ѯ IoT ƽ̨����ģʽ���ӿڵķ���ֵ�е�ĳ������ģʽ��ȱʡΪPSMģʽ��Ŀǰ��HW-test-iot-117���͡�HW-test-iot-112���������ӵ��֧��PSMģʽ����ctc-nanjing-iot-137��ͬʱ֧��PSM��eDRXģʽ��
	 * @param endUserName��ѡString(1-256)body�ն��û����ƣ����ֻ������
	 * @param endUserInfo��ѡString(1-256)body�ն��û���Ϣ�����ֻ������
	 * @param location��ѡString(1-1024)body�豸��λ��
	 * @param longitude��ѡFloatbody�豸����װλ�õľ���
	 * @param latitude��ѡFloatbody�豸����װλ�õ�γ��
	 * @param extend_type��ѡStringbody	��չ���Ͳ������粻ȷ����������
	 * @return
	 */
	public String registeredPlant(String serverID, String accessToken,
			String devSerial, String name, String deviceType,
			String connectPointId, String serviceMode, String endUserName,
			String endUserInfo, String location, String longitude,
			String latitude, String extend_type,String IotUrl);
	
	/**
	 * ����һ����ע���豸����Ϣ��
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param devSerial	�豸���к� = "����дҪ���µ��豸���к�,��IMEI";
	 * @param name	�豸���ƣ���ѡ = "����д�µ��豸����";
	 * @param longitude	���ȣ���ѡ = "����д�µľ���";
	 * @param latitude	γ�ȣ���ѡ = "����д�µ�γ��";
	 * @return
	 */
	public String updateDevice(String serverID,String accessToken,String devSerial,String name,String longitude,String latitude,String IotUrl);
	
	/**
	 * ɾ���豸
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param devSerial	�豸���к� = "����дҪ���µ��豸���к�,��IMEI";
	 * @return
	 */
	public String deleteDevice(String serverID,String accessToken,String devSerial,String IotUrl);
	
	/**
	 * �豸��Ϣ��ѯ
	 * ��ѯһ����ע���豸����ϸ��Ϣ�������豸�Ļ�����Ϣ����ǰҵ�����ݣ��澯���ݣ��Լ���ʱ��������ݡ�
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param devSerial	�豸���к� = "����дҪ���µ��豸���к�,��IMEI";
	 * @return
	 */
	public String queryDevice(String serverID,String accessToken,String devSerial,String IotUrl);
	
	/**
	 * ��ѯ�豸���ͣ���Ʒ�ͺţ���Ϣ
	 * ͨ�����ӿڲ�ѯһ���豸���ͣ���Ʒ�ͺţ�����Ϣ
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param devType	ָ���豸����(��Ʒ�ͺ�)	="����д�豸����(��Ʒ�ͺ�)";
	 * @return
	 */
	public String queryDeviceType(String serverID, String accessToken, String devType,String IotUrl);
	
	/**
	 * ��ѯ���п��õ��豸���ͣ���Ʒ�ͺţ�
	 * �ӿ�˵������ѯһ��ƽ̨�ʺ������п������豸ע����豸���ͣ���Ʒ�ͺţ�
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @return
	 */
	public String queryAllAvailableDeviceTypes(String serverID,String accessToken,String IotUrl);
	
	/**
	 * ��ѯ�û������豸
	 * ��ѯһ��ƽ̨�ʺ���ע��������豸
	 * @param serverID ��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @return
	 */
	public String queryUserAllDevice(String serverID,String accessToken,String IotUrl);
	
	/**
	 * ��ѯ�豸ҵ������
	 * �ӿ�˵����������ͨ���ýӿڲ�ѯ�豸��ҵ��������ʷ��¼
	 * @param serverID	��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken	��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param devSerial	�豸���к� = "����дҪ���µ��豸���к�,��IMEI";
	 * @return
	 */
	public String queryTheDeviceDusinessData(String serverID,String accessToken,String devSerial,String IotUrl);
	
	/**
	 * ȡ�����ĵ�ַ
	 * ��ƽ̨ȡ��������ƽ̨������Ϣ�Ļص���ַ
	 * @param serverID ��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @return
	 */
	public String unSubscribe(String serverID,String accessToken,String IotUrl);
	
	/**
	 * ��ѯ���ĵ�ַ
	 * ��ƽ̨��ѯ������ƽ̨������Ϣ�Ļص���ַ����������ע��Ķ��ĵ�ַ�Ǳ�ƽ̨��õ�����ƽ̨�ص��Ľӿڵ�ַ�̶����֡��ӿڵĿɱ䲿�ֲμ����ص��ӿڵĶ��塣
	 * @param serverID ��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @return
	 */
	public String querySubscribe(String serverID,String accessToken,String IotUrl);
	
	/**
	 * �豸��Ϣ֪ͨ
	 * ƽ̨ͨ���ýӿ��������Ӧ�ñ��������ĵ��豸������ҵ�����ݡ�
	 * @param callbackUrl  ����{callbackUrl}Ϊ�豸��Ϣ���Ľӿ����ṩ�ĵ�����ƽ̨�Ļص���ַ
	 * @param devSerial �豸���кţ�Ŀǰʹ�õ���ģ���Ӧ��IMEI
	 * @param createTime �豸ע�ᴴ��ʱ��
	 * @param lastMesssageTime ���豸����յ��豸ҵ������ʱ��
	 * @param iotEventTime IoTƽ̨���յ���Ϣ��ʱ��
	 * @param serviceData �豸ҵ�����ݣ������ṹ�μ�����ʾ�����롣
	 * @return
	 */
	public String deviceMessageNotification(String callbackUrl,String devSerial,String createTime,String lastMesssageTime,String iotEventTime,String serviceData,String IotUrl);
	
	/**
	 * ����ע��
	 * @param serverID ��Ϣͷ����serverID = "����д�û���";
	 * @param accessToken ��Ϣͷ����accessToken = "����д�ɹ���½���accessToken";
	 * @param objdev ע����ϢList����
	 * @return
	 */
	public String registrationCompleted(String serverID,String accessToken,List<EasyIOT> objdev,String IotUrl);
	
	/**
	 * @return
	 */
	public String queryService(String hostID,String IotUrl);
	
	/**
	 * ���� easyIot�����ݼ�¼
	 */
	public void addIotEntity(String devSerial,String createTime,String iotEventTime,String serviceId,String IOT_key,String IOT_value,String IotUrl);
	
	
	/**
	 * ���Ӻ������������򱨾�����
	 */
	public void addgasbyhm(String gasMac,int gasalarm,int language,int alarmType,int beepsoundlevel,int handonoff);
}
