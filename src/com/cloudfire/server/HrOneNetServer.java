package com.cloudfire.server;

import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import com.cloudfire.action.MsgOneNetEntity;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.ToolOneNetDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.dao.impl.ToolOneNetDaoImpl;
import com.cloudfire.entity.MqttSmokeEntity;
import com.cloudfire.entity.NanJing_NB_IOT_Entity;
import com.cloudfire.entity.OneNetEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.CRC16;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IntegerTo16;
import com.cloudfire.until.OneNetHttpMethod;
import com.cloudfire.until.Utils;


/**
 * 针对OneNet平台对接处理服务
 * @author lzo
 */
public class HrOneNetServer {
	
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private ToolOneNetDao tond;
	private PushAlarmMsgDao mPushAlarmMsgDao;
	
	public void dealOneNetString(String oneNetString) throws Exception{
		 ToolOneNetDao tond = new ToolOneNetDaoImpl();
		 SmokeLineDao mSmokeLineDao = new SmokeLineDaoImpl();
		 PublicUtils pu = new PublicUtilsImpl();
		 OneNetEntity one =  tond.getOneNetValues(oneNetString); //对json字符串进行初步处理
		 MsgOneNetEntity msg = one.getMsg();  
         String devMac =msg.getDev_id(); //device_id
         String mac =  tond.getMacByDeviceId(devMac);  //imei
         SmokeDao sd = new SmokeDaoImpl();
         int type = sd.getDeviceTypeByMac(mac); //获取设备类型。
         switch(msg.getType()){ //判断消息是数据消息还是设备上下线消息
         case "1": //数据消息
     		String dealValue = msg.getValue();
     		String ds_id = msg.getDs_id(); //数据流id  obj_id+"_"+obj_inst_id+"_"+res_id,下发命令必填参数。
     		tond.updateDs(mac, ds_id);//存储ds_id值
     		switch (type) {
     		case  57: //我们自己的烟感
     			/*1.从oneNet平台返回的msg属性的value中截取7C开头1C结尾的作为一包数据
         		*2.crc校验
         		*3.解析数据
         		*/
         		String packet = getPacket(dealValue); //将value转化为大写并从中截取合适的字符串
         		byte[] data = IntegerTo16.hexString2Bytes(packet);  //将该包转为byte[]
         		if(checkCrc(data,data.length)) { //crc校验通过,然后处理数据。
         			int deviceType = data[1] & 0xff;
         			switch(deviceType) {
         			case 225: //E1烟感
         				byte[] dataLen = new byte[2]; 
         				dataLen[0] = data[2];
         				dataLen[1] = data[3];
//         				int datalen = IntegerTo16.bytesToInt(dataLen); //数据长度
         				int deviceState = data[4] & 0xff; //设备状态 203CB心跳 202CA烟雾报警 193C1低电压
         				int alarmType = -1; //默认烟感报警类型-1
         				switch (deviceState){
         				case 203://更新设备的心跳时间,并使之上线
         					pu.updateDeviceMac(mac);
         					break;
         				case 202:
         					alarmType = 202;
         					break;
         				case 193:
         					alarmType = 193;
         					break;
         				}
         				
         				if (alarmType != -1){ //报警
         					mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
         					mFromRepeaterAlarmDao.addAlarmMsg(mac, "", alarmType , 1);  //将报警信息添加到alarm表中
         					 
         					mSmokeLineDao.smokeOffLine(mac, 1); //报警让设备上线
         					mSmokeLineDao.smokeOffLine2(mac, 1);
         					
         					mGetPushUserIdDao = new GetPushUserIdDaoImpl();
         					List<String> userList = mGetPushUserIdDao.getAllUser(mac);
         					List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //获取useridsmoke表里的用户用于短信通知
         					if(userList!=null&&userList.size()>0){
         						//开启推送消息的线程  手机推送
         						mPushAlarmMsgDao = new  PushAlarmMsgDaoImpl();
         						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
         						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType);
         						new MyThread(push,userList,iosMap).start();  
         						
         						//网页推送开启
         						new WebThread(userList,mac).start();
         						
         						//短信推送
         						if (txtUserList != null &&txtUserList.size()>0) {
         							new TxtThread(txtUserList,mac).start();        //短信通知的线程
         						}
         					}
         				}
//         				int voiceState = data[5] & 0xff; //消音状态 00消音中 01 正常
         				break; //烟感结束
         				
         			}//switch(deviceType) end
         			
         		}//crc正确数据处理结束
     			break; //我们自己的设备结束
     			
     		case  58: //嘉德烟感
     			byte[] data2 = IntegerTo16.hexString2Bytes(dealValue); //将接收到的value字符串转化为byte数组 
     			
     			int seq = IntegerTo16.byteToInt2(data2[0]); //第一个字节为序列号
     			
     			byte[] dataLen = new byte[2];  //第2，3字节为后面的数据长度
 				dataLen[0] = data2[2];
 				dataLen[1] = data2[1];
     			int  datalen =IntegerTo16.bytesToInt(dataLen); 

     			String jdmac = ""; //设备mac 
     			int jdDeviceType = 0;  //设备类型
     			int jdState = 0; //设备状态
     			int jdSignal=0;//信号强度
     			int jdPower = 0; //剩余电量
     			
     			//数据解析
     			int index = 0;
     			while (index < datalen) { 
	     			int jdtype = IntegerTo16.byteToInt2(data2[3+index]); //jd数据类型
	     			
	     			byte[] jdLen = new byte[2];  //jd数据长度
	     			jdLen[0] = data2[5+index];
	     			jdLen[1] = data2[4+index];
         			int  jdlen =IntegerTo16.bytesToInt(jdLen);
         			
         			byte[] jdContent = new byte[jdlen];//jd数据内容
         			for (int i = 0; i < jdlen; i++){
         				jdContent[i] = data2[6+index+i];
         			}
         			
         			index += 3 + jdlen;
         			
         			switch (jdtype) {
         			case  1:
         				jdmac = IntegerTo16.bytes2Hex(jdContent);
         				break;
         			case 2:
         				jdDeviceType = IntegerTo16.byteToInt2(jdContent[0]);
         				break;
         			case 3:
         				jdState = IntegerTo16.byteToInt2(jdContent[0]);
         				break;
         			case 4:
         				jdSignal=IntegerTo16.byteToInt2(jdContent[0]);
         				break;
         			case 5:
         				jdPower=IntegerTo16.byteToInt2(jdContent[0]);
         				break;
         			}
     			}
     			
	     		int alarmType = -1;
	     		switch (jdState){
	     		case 0:  //心跳
//	     			//更新心跳保存电量，上线，,判断是否是电量回复
//		     		tond.updatePower(mac, jdPower+"");
	     			//判断是否为之前是否为低电状态，是则恢复且推恢复
		     		if (SmokeDaoImpl.ifPowerOff(mac)) {
		     			SmokeDaoImpl.setPowerState(mac, 0);
		     			alarmType=194;
		     		}
	     			break;
	     		case 1:  //烟雾报警
	     			alarmType = 202;
	     			break;
	     		case 2: //低电压
	     			alarmType = 193;
	     			//设置设备的电量状态为低电
	     			SmokeDaoImpl.setPowerState(mac, 1);
	     			break;
	     		case 8: //对码
//	     			alarmType = 68;
	     			break;
	     		case 13: //0x0D测试
	     			alarmType = 67; 
	     			break;
	     		case 17: //0x11 烟雾报警恢复
	     			alarmType = 102;
	     			break;
	     		case 18: //0x12 低电压恢复
	     			alarmType = 194;
	     			break;
	     		case 19: //0x13 防拆报警
	     			alarmType=14;
	     			break;
	     		case 20: //0x14 防拆报警回复
	     			alarmType=15;
	     			break;
	     		}
     		
	     		AreaDao ad2 = new AreaDaoImpl();
	    		int parentId2 = ad2.getSuperAreaIdBySmokeMac(mac);
	    		
	    		if(parentId2==9&&alarmType==193){//越秀区屏蔽低电压
	    			return;
	    		}
	    		
	    		
	    		//保存电量信息，并让设备上线
	    		tond.updatePower2(mac, jdPower+"");
	    		
	     		if (alarmType != -1){ //报警处理
//	     			//保存电量信息，并让设备上线
//	     			tond.updatePower2(mac, jdPower+"");
	     			
					mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
					mFromRepeaterAlarmDao.addAlarmMsg(mac, "", alarmType , 1);  //将报警信息添加到alarm表中
					 
					mGetPushUserIdDao = new GetPushUserIdDaoImpl();
					List<String> userList = mGetPushUserIdDao.getAllUser(mac);
					List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //获取useridsmoke表里的用户用于短信通知
					if(userList!=null&&userList.size()>0){
						//开启推送消息的线程  手机推送
						mPushAlarmMsgDao = new  PushAlarmMsgDaoImpl();
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType);
						new MyThread(push,userList,iosMap).start();  
						//网页推送开启
						
						new WebThread(userList,mac).start();
						//短信推送
						if (txtUserList != null &&txtUserList.size()>0) {
							new TxtThread(txtUserList,mac).start();        //短信通知的线程
							
							
							
							
						}
						ToolOneNetDao tod = new ToolOneNetDaoImpl();
						AreaDao ad = new AreaDaoImpl();
						int parentId = ad.getSuperAreaIdBySmokeMac(mac);
						if(parentId == 157){
							String fdevice_uuid = tod.getDeviceIdByImei(mac);
							Utils.pushAlarm(null, null, fdevice_uuid,alarmType);
						}
					}
				}
	     		break; //嘉德设备结束
	     		
     		} //switch(type) end
     		
     		break;  //msg.getType() == 1
		case "2": //设备上下线消息
			switch(one.getMsg().getStatus()){
			case "0"://下线
//				mSmokeLineDao.smokeOffLine(mac, 0);
				break;
			case "1"://上线
				 mSmokeLineDao.smokeOffLine(mac, 1);
				 break;
			}
			
	    	 break;
	    
         } //switch(msg.getType) end
		
	}
	
	
	/**
	 * 将字符床转化为大写
	 * 从字符串中截取7C开头1C结尾的作为一包数据
	 * @param orig 原始字符串
	 * @return
	 */
	private String getPacket(String orig) {
		orig = orig.toUpperCase();
		String packet = "";
		int index1 = orig.indexOf("7C");
		int index2 = orig.indexOf("1C");
		if (index1 < index2) {
			packet = orig.substring(index1, index2 - index1 + 2);
		}
		return packet;
	}
	

	/**
	 * crc校验
	 * @param data
	 * @param dataLen
	 * @return
	 */
	private  boolean checkCrc(byte[] data,int dataLen){
		int crcLen = dataLen-4;
		byte[] crcByte = new byte[crcLen];
		for(int i=0;i<crcLen;i++){
			crcByte[i] = data[i+1];
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		byte crcL = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		byte crcH = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		byte clientCrcL = data[dataLen-3];
		byte clientCrcH = data[dataLen-2];
//		log.debug("CRC==========crcL:"+crcL+" crcH:"+crcH+" clientCrcL:"+clientCrcL+" clientCrcH:"+clientCrcH+"   dataLen="+dataLen);
		if(crcL==clientCrcL&&crcH==clientCrcH){
			return true;
		}else{
			return false;
		}
	}
	
    public static void main(String[] args) {
   	//心跳
    	String oneNetString = "{\"msg\":{\"at\":1547376858663,\"imei\":\"869405032227531\",\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"1300180100055035988261020001030300010D0400012405000164\",\"dev_id\":517680979},\"msg_signature\":\"9moiinGAi2rUBN9jNJddpg==\",\"nonce\":\"71b_O$!5\"}";
    	String url = "http://localhost:8080/fireSystem/uploadDevices.do";
    	String putJson = OneNetHttpMethod.postJson(url, oneNetString);
    	System.out.println(putJson);
    	

	}

}
