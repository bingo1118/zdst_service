package com.cloudfire.until;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.cloudfire.db.SystemConfig;

public class Constant {
	
	//南京平台配置
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
	 * goeasy推送！！！
	 */
	public static  String appk_web = "";
	public static  String appk_web_sub = "";
	
	//侨平
	public static String qpsecret = "";
	public static String qpappkey = "";
	
	//移动NB-iotOneNet平台apiKey
	public static final String gdapikey = "S60Brswndrcu3wmNMY8hVyhGOzE="; //广东平台
	public static final String chqapikey = "mF6xhSt=wY=B9WaINrAs=tg0=HU= "; //重庆平台
	
	
	//短信、电话通知api的APPID和appkey！！！
	public static final int NOTICE_APPID=0;
	public static final String NOTICE_APPKEY="";
	
	
	//测试平台	oqIZLzjKpwf7VLK_3IifVhGtnAEa
	public static final String CALLBACK_BASE_URL = "http://49.4.64.182:51091/zdst"; 
//	public static final String BASE_URL = "https://180.101.147.89:8743";
	public static final String BASE_URL = "https://device.api.ct10649.com:8743";
	
	public static final String APPID = "";//认证设备
    public static final String SECRET = "";
	
    public static final String JDAPPID = "";//认证设备
    public static final String JDSECRET = "";
   
    
    public static final String RHAPPID = "";
    public static final String RHSECRET = "";
    
    public static final String RQAPPID7 = "";
    public static final String RQSECRET7 = "";
    
    public static final String PTSYAPPID = "";	
	public static final String PTSYSECRET = "";
	
	public static final String UTAPPID = "";	
	public static final String UTSECRET = "";
	
	
	//测试电气
//	 public static final String DQAPPID1 = "2mDubvrd20frMzy436oI0ows16sa";	
//	 public static final String DQSECRET1 = "kk83RFBs7o9e883nJQch2fCI7xMa";
	 
	//商用电气
	 public static final String DQAPPID1 = "Ib1bFj2lv9KZ8UA064NAnU8Qz64a";	
	 public static final String DQSECRET1 = "OD_lh3kuEIZwVc3masvHuquaSqMa";
	 
    
    
    
    /*
     * complete callback url：
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
    
    //注册设备直连
    public static final String REGISTER_DEVICE = BASE_URL + "/iocm/app/reg/v1.1.0/deviceCredentials";
    public static final String REGISTER_DEVICE_CREDENTIALS = BASE_URL + "/iocm/app/reg/v2.0.0/deviceCredentials";
    //修改设备信息
    public static final String MODIFY_DEVICE_INFO = BASE_URL + "/iocm/app/dm/v1.4.0/devices";
    public static final String QUERY_DEVICE_ACTIVATION_STATUS = BASE_URL + "/iocm/app/reg/v1.1.0/devices";
    //删除直连设备
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
     * Signaling Delivery：
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
	
	public static final String OFF = "关闭";
	
	public static final String ON = "开启";
	
	//在线状态
	public static final String NETSTATE1 = "在线";
	
	//掉线状态
	public static final String NETSTATE0 = "掉线";
	
	//设备类型为1
	public static final String DEVICETYPE1 = "无线感烟火灾探测报警器";
	
	//设备类型为2
	public static final String DEVICETYPE2 = "无线可燃气体探测器";
	
	//设备类型为5
	public static final String DEVICETYPE5 = "无线电气火灾探测器";
	
	//设备类型为7
	public static final String DEVICETYPE7 = "无线声光报警器";
	
	//设备类型为8
	public static final String DEVICETYPE8 = "无线手动报警按钮";
	
	//设备类型为9
	public static final String DEVICETYPE9 = "三江主机";
	
	//设备类型为10
	public static final String DEVICETYPE10 = "无线水压探测器";
	
	//设备类型为11
	public static final String DEVICETYPE11 = "红外线";
	
	//设备类型为12
	public static final String DEVICETYPE12 = "门磁";
	
	//设备类型为13
	public static final String DEVICETYPE13 = "环境探测器";
	
	//设备类型为13
	public static final String DEVICETYPE14 = "GPS定位器";
	
	//设备类型为13
	public static final String DEVICETYPE15 = "水浸";
	
	//设备类型为13
	public static final String DEVICETYPE16 = "NB燃气";
	
	public static final String DEVICETYPE17 = "电梯";
	
	public static final String DEVICETYPE18 = "喷淋设备";
	
	public static final String DEVICETYPE19 ="水位探测器";
	
	public static final String DEVICETYPE21 ="loraWan烟感";
	
	public static final String DEVICETYPE31 ="三江烟感";
	
	public static final String DEVICETYPE126 = "海湾主机";
	
	//第一页
	public static final String CP="1";
	
	//每页的大小
	public static final Integer PAGESIZE=10;
	
	//报警类型的名称：当是202时是火警
	public static final String 	ALARMTYPE202="烟雾";
	
	//报警类型的名称：当是193时是火警
	public static final String 	ALARMTYPE193="低电压";
	public static final String ALARMTYPE2="燃气报警";
	public static final String ALARMTYPE7="声光报警";
	public static final String ALARMtYPE8="手动报警";
	public static final String ALARMtYPE11="红外报警";
	public static final String ALARMtYPE12="报警 ";
	public static final String ALARMtYPE13="报警";
	public static final String ALARMtYPE201="阀门关闭";
	
	//报警类型的名称：当是209时是水压的：低水压报警
	public static final String 	ALARMTYPE209="低水压报警";
	
	//报警类型的名称：当是218时是火警
	public static final String 	ALARMTYPE218="高水压报警";
	
	public static final String 	ALARMTYPE210="水压降低报警";
	
	public static final String 	ALARMTYPE217="水压升高报警";
	
	public static final String ALARMFAMILY36="故障";
	public static final String ALARMFAMILY136="485故障";
	
	public static final String ALARMFAMILY43="过压报警";
	
	public static final String ALARMFAMILY44="欠压报警";
	
	public static final String ALARMFAMILY45="过流报警";
	//诚佳电气
	public static final String ALARMFAMILY49="短路报警";
	public static final String ALARMFAMILY50="过热报警";
	//诚佳电气
	public static final String ALARMFAMILY46="漏电流报警";
	
	public static final String ALARMFAMILY47="温度报警";
	
	public static final String ALARMFAMILY48="合闸报警";
	
	public static final String Bq0 ="正常";
	public static final String Bq1="报警";
	
	public static final String waterInfosSql = " in (10,70,125)"; 	//水压
	public static final String waterLevelSql = " in (19,69,124)"; 	//水位
	public static final String NBDEVICEALLSQL = " in (16,22,23,34,35,36,41,42,45,53,55,56,57,58,59,61)";		//所有NB设备类型
	public static final String NBSMOKEDEVICESQL = " in (41,53,55,56,57,58,59,61)";		//所有NB烟感
	public static final String NBFIREGASSQL = " in (16,22,23,45,53,55,56,57,58,59,61)";		//所有NB燃气
	public static final String NBELECTRSQL = " in (34,35,36,53,55,56,57,58,59,61)";		//所有NB电气
	public static final String NBWATERSQL = " in (42,53,55,56,57,58,59,61)";		//所有NB水压
//	public static final String sql = " in (1,2,5,7,8,9,10,16,18,21,126) ";
	
	public static final String hrsst_tel = "02028148948";
	
	/**
	 * easyIot-服务器地址
	 */
	public static final String hm_url = "http://59.36.137.251:5001/";			//海曼URL
	public static final String iot_url = "https://www.easy-iot.cn/";		//EasyIot平台的Url


	
	/**
	 * NB设备ID 41\海曼烟感，我们自己烟感(UDP).45\海曼气感.53\NB贵州电气。55\嘉德烟感电信.56\NB烟感电信.57我们的onenet烟感。58\嘉德的移动烟感.59\NB北秦电气电信
	 */
	public static String nb_devtype1 = "(41,55,56,57,58,61)";	//烟感
	public static String nb_devtype2 = "(45,72,73)";	//燃气
	public static String nb_devtype5 = "(53,59,35)";	//电气(包括电弧)
	
	public static String[] sqlstr = {"DELETE from electricinfo where electricTime < ?",
		"DELETE from alarm where alarmTime < ?",
		"DELETE from electric_change_history where changetime < ?","DELETE from environment where dateTime < ? ",
		"DELETE from faultinfo where faultTime < ?","DELETE from gpsinfo where dataTime < ?",
		"DELETE from masterequipment where updateTime < ?","DELETE from nfcrecord where endTime < ?",
		"DELETE from waterinfo where time < ?"};
	
	//@@根据设备类型选择查询语句条件2018.05.18
    public static String devTypeChooseSQLStatement(String devType){
    	String sql="";
    	if(devType=="1"||"1".equals(devType)){		//by liangbin //重点单位
    		sql=" AND deviceType not in (5,10,11,12,13,14,15,18,19,25,26,27,35,42,43,46,47,48,52,53,59,68,69,70,75,76,77,78,79,80,81,119,124,125,126) AND mac<>repeater ";
		}else if(devType=="3"||"3".equals(devType)){		 //电气防火
			sql=" AND deviceType in (5,35,52,53,59,75,76,77,80,81)";
		}else if(devType=="4"||"4".equals(devType)){		 //消防物联
			sql=" AND deviceType in (10,11,12,13,15,18,19,25,26,27,42,43,46,47,48,68,69,70,78,79,124,125)";
		}else if(devType=="2"||"2".equals(devType)){		//传输装置
			sql=" AND (mac=repeater or deviceType in(119,126) ) ";
		}
    	return sql;
    }
    
  //@@根据设备类型选择查询语句条件（安全监管）
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
    
    //故障集
    //故障,电弧探测器故障,短路故障,开路故障,机械手故障,传感器故障,
    //串口通讯故障,烟雾故障报警,温湿度故障报警,485故障
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
