package com.cloudfire.until;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.cloudfire.db.SystemConfig;

public class Constant {
	
	//�Ͼ�ƽ̨����
//    public static String BASE_URL =  SystemConfig.getConfigInfomation("BASE_URL");
    
	
	public static final String ContextPath  = "zdst";
	public static final String port = "51091";
	

	public static String outerIp="49.4.64.182";
	
	static {
		String slash=File.separator; 
		String demo_base_Path = System.getProperty("catalina.base")+slash+"webapps"+slash+"ip.txt";
		File file = new File(demo_base_Path);
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String content = "";
			while(content != null) {
				content = br.readLine();
				if (content!=null){
					sb.append(content.trim());
				}
			}
			outerIp = sb.toString();
			br.close();
			isr.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}		
	
	/**
	 * goeasy���ͣ�����
	 */
	public static  String appk_web = "";
	public static  String appk_web_sub = "";
	
	//��ƽ
	public static String qpsecret = "";
	public static String qpappkey = "";
	
	//�ƶ�NB-iotOneNetƽ̨apiKey
	public static final String gdapikey = "S60Brswndrcu3wmNMY8hVyhGOzE="; //�㶫ƽ̨
	public static final String chqapikey = "mF6xhSt=wY=B9WaINrAs=tg0=HU= "; //����ƽ̨
	
	
	//���š��绰֪ͨapi��APPID��appkey������
	public static final int NOTICE_APPID=0;
	public static final String NOTICE_APPKEY="";
	
	
	//����ƽ̨	oqIZLzjKpwf7VLK_3IifVhGtnAEa
	public static final String CALLBACK_BASE_URL = "http://49.4.64.182:51091/zdst"; 
//	public static final String BASE_URL = "https://180.101.147.89:8743";
	public static final String BASE_URL = "https://device.api.ct10649.com:8743";
	
	public static final String APPID = "";//��֤�豸
    public static final String SECRET = "";
	
    public static final String JDAPPID = "";//��֤�豸
    public static final String JDSECRET = "";
   
    
    public static final String RHAPPID = "";
    public static final String RHSECRET = "";
    
    public static final String RQAPPID7 = "";
    public static final String RQSECRET7 = "";
    
    public static final String PTSYAPPID = "";	
	public static final String PTSYSECRET = "";
	
	public static final String UTAPPID = "";	
	public static final String UTSECRET = "";
	
	
	//���Ե���
//	 public static final String DQAPPID1 = "2mDubvrd20frMzy436oI0ows16sa";	
//	 public static final String DQSECRET1 = "kk83RFBs7o9e883nJQch2fCI7xMa";
	 
	//���õ���
	 public static final String DQAPPID1 = "Ib1bFj2lv9KZ8UA064NAnU8Qz64a";	
	 public static final String DQSECRET1 = "OD_lh3kuEIZwVc3masvHuquaSqMa";
	 
    
    
    
    /*
     * complete callback url��
     * please replace uri, when you use the demo.
     */
    public static final String DEVICE_ADDED_CALLBACK_URL = CALLBACK_BASE_URL + "/na_iocm_devNotify_v1.1.0_addDevice";
    public static final String DEVICE_INFO_CHANGED_CALLBACK_URL = CALLBACK_BASE_URL + "/na_iocm_devNotify_v1.1.0_updateDeviceInfo";
    public static final String DEVICE_DELETED_CALLBACK_URL = CALLBACK_BASE_URL + "/na_iocm_devNotify_v1.1.0_deletedDevice";
    public static final String MESSAGE_CONFIRM_CALLBACK_URL = CALLBACK_BASE_URL + "/na_iocm_devNotify_v1.1.0_commandConfirmData";
    public static final String SERVICE_INFO_CHANGED_CALLBACK_URL = CALLBACK_BASE_URL + "/na_iocm_devNotify_v1.1.0_updateServiceInfo";
    public static final String COMMAND_RSP_CALLBACK_URL = CALLBACK_BASE_URL + "/na_iocm_devNotify_v1.1.0_commandRspData";
    public static final String DEVICE_EVENT_CALLBACK_URL = CALLBACK_BASE_URL + "/na_iocm_devNotify_v1.1.0_DeviceEvent";
    public static final String RULE_EVENT_CALLBACK_URL = CALLBACK_BASE_URL + "/na_iocm_devNotify_v1.1.0_RulEevent";
    public static final String DEVICE_DATAS_CHANGED_CALLBACK_URL = CALLBACK_BASE_URL + "/nanjing-report-dev-callback";
    public static final String DEVICE_DATA_CHANGED_CALLBACK_URL = CALLBACK_BASE_URL + "/nanjing-report-devs-callback";


    /*
     * Specifies the callback URL for the command execution result notification.
     * For details about the execution result notification definition.
     * please replace uri, when you use the demo.
     */
    public static final String REPORT_CMD_EXEC_RESULT_CALLBACK_URL = CALLBACK_BASE_URL + "/na/iocm/devNotify/v1.1.0/reportCmdExecResult";


    //Paths of certificates.
    public static String SELFCERTPATH = "outgoing.CertwithKey.pkcs12";
    public static String TRUSTCAPATH = "ca.jks";
    

    //Password of certificates.
    public static String SELFCERTPWD = "IoM@1234";
    public static String TRUSTCAPWD = "Huawei@123";






    //*************************** The following constants do not need to be modified *********************************//

    /*
     * request header
     * 1. HEADER_APP_KEY
     * 2. HEADER_APP_AUTH
     */
    public static final String HEADER_APP_KEY = "app_key";
    public static final String HEADER_APP_AUTH = "Authorization";
    
    /*
     * Application Access Security:
     * 1. APP_AUTH
     * 2. REFRESH_TOKEN
     */
    public static final String APP_AUTH = BASE_URL + "/iocm/app/sec/v1.1.0/login";
    public static final String REFRESH_TOKEN = BASE_URL + "/iocm/app/sec/v1.1.0/refreshToken";
    
    //ע���豸ֱ��
    public static final String REGISTER_DEVICE = BASE_URL + "/iocm/app/reg/v1.1.0/deviceCredentials";
    public static final String REGISTER_DEVICE_CREDENTIALS = BASE_URL + "/iocm/app/reg/v2.0.0/deviceCredentials";
    //�޸��豸��Ϣ
    public static final String MODIFY_DEVICE_INFO = BASE_URL + "/iocm/app/dm/v1.4.0/devices";
    public static final String QUERY_DEVICE_ACTIVATION_STATUS = BASE_URL + "/iocm/app/reg/v1.1.0/devices";
    //ɾ��ֱ���豸
    public static final String DELETE_DEVICES = BASE_URL + "/iocm/app/dm/v1.4.0/devices";
    public static final String DISCOVER_INDIRECT_DEVICE = BASE_URL + "/iocm/app/signaltrans/v1.1.0/devices/%s/services/%s/sendCommand";
    public static final String REMOVE_INDIRECT_DEVICE = BASE_URL + "/iocm/app/signaltrans/v1.1.0/devices/%s/services/%s/sendCommand";

    /*
     * Data Collection:
     * 1. QUERY_DEVICES
     * 2. QUERY_DEVICE_DATA
     * 3. QUERY_DEVICE_HISTORY_DATA
     * 4. QUERY_DEVICE_CAPABILITIES
     * 5. SUBSCRIBE_NOTIFYCATION
     */
    public static final String QUERY_DEVICES = BASE_URL + "/iocm/app/dm/v1.3.0/devices";
    public static final String QUERY_DEVICE_DATA = BASE_URL + "/iocm/app/dm/v1.3.0/devices";
    public static final String QUERY_DEVICE_HISTORY_DATA = BASE_URL + "/iocm/app/data/v1.1.0/deviceDataHistory";
    public static final String QUERY_DEVICE_CAPABILITIES = BASE_URL + "/iocm/app/data/v1.1.0/deviceCapabilities";
    public static final String SUBSCRIBE_NOTIFYCATION = BASE_URL + "/iocm/app/sub/v1.1.0/subscribe";
    
    
    /*
     * Signaling Delivery��
     * 1. POST_ASYN_CMD
     * 2. QUERY_DEVICE_CMD
     * 3. UPDATE_ASYN_COMMAND
     * 4. CREATE_DEVICECMD_CANCEL_TASK
     * 5. QUERY_DEVICECMD_CANCEL_TASK
     *
     */
    public static final String POST_ASYN_CMD = BASE_URL + "/iocm/app/cmd/v1.4.0/deviceCommands";
    public static final String QUERY_DEVICE_CMD = BASE_URL + "/iocm/app/cmd/v1.4.0/deviceCommands";
    public static final String UPDATE_ASYN_COMMAND = BASE_URL + "/iocm/app/cmd/v1.4.0/deviceCommands/%s";
    public static final String CREATE_DEVICECMD_CANCEL_TASK = BASE_URL + "/iocm/app/cmd/v1.4.0/deviceCommandCancelTasks";
    public static final String QUERY_DEVICECMD_CANCEL_TASK = BASE_URL + "/iocm/app/cmd/v1.4.0/deviceCommandCancelTasks";


    /*
     * notify Type
     * serviceInfoChanged|deviceInfoChanged|LocationChanged|deviceDataChanged|deviceDatasChanged
     * deviceAdded|deviceDeleted|messageConfirm|commandRsp|deviceEvent|ruleEvent
     */
    public static final String DEVICE_ADDED = "deviceAdded";
    public static final String DEVICE_INFO_CHANGED = "deviceInfoChanged";
    public static final String DEVICE_DATA_CHANGED = "deviceDataChanged";
    public static final String DEVICE_DELETED = "deviceDeleted";
    public static final String MESSAGE_CONFIRM = "messageConfirm";
    public static final String SERVICE_INFO_CHANGED = "serviceInfoChanged";
    public static final String COMMAND_RSP = "commandRsp";
    public static final String DEVICE_EVENT = "deviceEvent";
    public static final String RULE_EVENT = "ruleEvent";
    public static final String DEVICE_DATAS_CHANGED = "deviceDatasChanged";
	
	public static final String EASY_IOT_HR_ACCOUNT="gzhrdev01";
	
	public static final String EASY_IOT_HR_PWD="pii1icRi";
	
	public static final String IOT_SERVER_ID = "ctc-nanjing-iot-137";
	
	public static final String OFF = "�ر�";
	
	public static final String ON = "����";
	
	//����״̬
	public static final String NETSTATE1 = "����";
	
	//����״̬
	public static final String NETSTATE0 = "����";
	
	//�豸����Ϊ1
	public static final String DEVICETYPE1 = "���߸��̻���̽�ⱨ����";
	
	//�豸����Ϊ2
	public static final String DEVICETYPE2 = "���߿�ȼ����̽����";
	
	//�豸����Ϊ5
	public static final String DEVICETYPE5 = "���ߵ�������̽����";
	
	//�豸����Ϊ7
	public static final String DEVICETYPE7 = "�������ⱨ����";
	
	//�豸����Ϊ8
	public static final String DEVICETYPE8 = "�����ֶ�������ť";
	
	//�豸����Ϊ9
	public static final String DEVICETYPE9 = "��������";
	
	//�豸����Ϊ10
	public static final String DEVICETYPE10 = "����ˮѹ̽����";
	
	//�豸����Ϊ11
	public static final String DEVICETYPE11 = "������";
	
	//�豸����Ϊ12
	public static final String DEVICETYPE12 = "�Ŵ�";
	
	//�豸����Ϊ13
	public static final String DEVICETYPE13 = "����̽����";
	
	//�豸����Ϊ13
	public static final String DEVICETYPE14 = "GPS��λ��";
	
	//�豸����Ϊ13
	public static final String DEVICETYPE15 = "ˮ��";
	
	//�豸����Ϊ13
	public static final String DEVICETYPE16 = "NBȼ��";
	
	public static final String DEVICETYPE17 = "����";
	
	public static final String DEVICETYPE18 = "�����豸";
	
	public static final String DEVICETYPE19 ="ˮλ̽����";
	
	public static final String DEVICETYPE21 ="loraWan�̸�";
	
	public static final String DEVICETYPE31 ="�����̸�";
	
	public static final String DEVICETYPE126 = "��������";
	
	//��һҳ
	public static final String CP="1";
	
	//ÿҳ�Ĵ�С
	public static final Integer PAGESIZE=10;
	
	//�������͵����ƣ�����202ʱ�ǻ�
	public static final String 	ALARMTYPE202="����";
	
	//�������͵����ƣ�����193ʱ�ǻ�
	public static final String 	ALARMTYPE193="�͵�ѹ";
	public static final String ALARMTYPE2="ȼ������";
	public static final String ALARMTYPE7="���ⱨ��";
	public static final String ALARMtYPE8="�ֶ�����";
	public static final String ALARMtYPE11="���ⱨ��";
	public static final String ALARMtYPE12="���� ";
	public static final String ALARMtYPE13="����";
	public static final String ALARMtYPE201="���Źر�";
	
	//�������͵����ƣ�����209ʱ��ˮѹ�ģ���ˮѹ����
	public static final String 	ALARMTYPE209="��ˮѹ����";
	
	//�������͵����ƣ�����218ʱ�ǻ�
	public static final String 	ALARMTYPE218="��ˮѹ����";
	
	public static final String 	ALARMTYPE210="ˮѹ���ͱ���";
	
	public static final String 	ALARMTYPE217="ˮѹ���߱���";
	
	public static final String ALARMFAMILY36="����";
	public static final String ALARMFAMILY136="485����";
	
	public static final String ALARMFAMILY43="��ѹ����";
	
	public static final String ALARMFAMILY44="Ƿѹ����";
	
	public static final String ALARMFAMILY45="��������";
	//�ϼѵ���
	public static final String ALARMFAMILY49="��·����";
	public static final String ALARMFAMILY50="���ȱ���";
	//�ϼѵ���
	public static final String ALARMFAMILY46="©��������";
	
	public static final String ALARMFAMILY47="�¶ȱ���";
	
	public static final String ALARMFAMILY48="��բ����";
	
	public static final String Bq0 ="����";
	public static final String Bq1="����";
	
	public static final String waterInfosSql = " in (10,70,125)"; 	//ˮѹ
	public static final String waterLevelSql = " in (19,69,124)"; 	//ˮλ
	public static final String NBDEVICEALLSQL = " in (16,22,23,34,35,36,41,42,45,53,55,56,57,58,59,61)";		//����NB�豸����
	public static final String NBSMOKEDEVICESQL = " in (41,53,55,56,57,58,59,61)";		//����NB�̸�
	public static final String NBFIREGASSQL = " in (16,22,23,45,53,55,56,57,58,59,61)";		//����NBȼ��
	public static final String NBELECTRSQL = " in (34,35,36,53,55,56,57,58,59,61)";		//����NB����
	public static final String NBWATERSQL = " in (42,53,55,56,57,58,59,61)";		//����NBˮѹ
//	public static final String sql = " in (1,2,5,7,8,9,10,16,18,21,126) ";
	
	public static final String hrsst_tel = "02028148948";
	
	/**
	 * easyIot-��������ַ
	 */
	public static final String hm_url = "http://59.36.137.251:5001/";			//����URL
	public static final String iot_url = "https://www.easy-iot.cn/";		//EasyIotƽ̨��Url


	
	/**
	 * NB�豸ID 41\�����̸У������Լ��̸�(UDP).45\��������.53\NB���ݵ�����55\�ε��̸е���.56\NB�̸е���.57���ǵ�onenet�̸С�58\�εµ��ƶ��̸�.59\NB���ص�������
	 */
	public static String nb_devtype1 = "(41,55,56,57,58,61)";	//�̸�
	public static String nb_devtype2 = "(45,72,73)";	//ȼ��
	public static String nb_devtype5 = "(53,59,35)";	//����(�����绡)
	
	public static String[] sqlstr = {"DELETE from electricinfo where electricTime < ?",
		"DELETE from alarm where alarmTime < ?",
		"DELETE from electric_change_history where changetime < ?","DELETE from environment where dateTime < ? ",
		"DELETE from faultinfo where faultTime < ?","DELETE from gpsinfo where dataTime < ?",
		"DELETE from masterequipment where updateTime < ?","DELETE from nfcrecord where endTime < ?",
		"DELETE from waterinfo where time < ?"};
	
	//@@�����豸����ѡ���ѯ�������2018.05.18
    public static String devTypeChooseSQLStatement(String devType){
    	String sql="";
    	if(devType=="1"||"1".equals(devType)){		//by liangbin //�ص㵥λ
    		sql=" AND deviceType not in (5,10,11,12,13,14,15,18,19,25,26,27,35,42,43,46,47,48,52,53,59,68,69,70,75,76,77,78,79,80,81,119,124,125,126) AND mac<>repeater ";
		}else if(devType=="3"||"3".equals(devType)){		 //��������
			sql=" AND deviceType in (5,35,52,53,59,75,76,77,80,81)";
		}else if(devType=="4"||"4".equals(devType)){		 //��������
			sql=" AND deviceType in (10,11,12,13,15,18,19,25,26,27,42,43,46,47,48,68,69,70,78,79,124,125)";
		}else if(devType=="2"||"2".equals(devType)){		//����װ��
			sql=" AND (mac=repeater or deviceType in(119,126) ) ";
		}
    	return sql;
    }
    
  //@@�����豸����ѡ���ѯ�����������ȫ��ܣ�
    public static String devTypeChooseSQLStatement_zdst(String devType){
    	String strSql="";
    	if(devType=="1"||"1".equals(devType)){		//by liangbin
			strSql=" AND deviceType not in (5,10,11,12,13,14,15,17,18,19,25,35,42,43,46,52,53,59,69,70,75,76,77,78,79,80,81,119,124,125,126) AND mac<>repeater ";
		}else if(devType=="3"||"3".equals(devType)){		
			strSql=" AND deviceType in (10,11,12,15,18,19,25,42,43,46,69,70,78,79,124,125)";
		}else if(devType=="4"||"4".equals(devType)){		
			strSql=" AND deviceType=13";
		}else if(devType=="2"||"2".equals(devType)){		
			strSql=" AND deviceType in (5,35,52,53,59,75,76,77,80,81) ";
		}else if(devType=="6"||"6".equals(devType)){		
			strSql=" AND deviceType=14";
		}else if(devType=="8"||"8".equals(devType)){		
			strSql=" AND deviceType>0";
		}
    	return strSql;
    }
    
    //���ϼ�
    //����,�绡̽��������,��·����,��·����,��е�ֹ���,����������,
    //����ͨѶ����,������ϱ���,��ʪ�ȹ��ϱ���,485����
    public static int[] faultArrays = {36,54,73,74,75,76,77,109,111,136};
    public static boolean ifFault(int target){
    	boolean fault = false;
    	for (int i : faultArrays) {
			if (i == target) {
				fault = true;
				break;
			}
		}
    	return fault;
    }
}
