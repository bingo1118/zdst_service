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
 * ���OneNetƽ̨�ԽӴ������
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
		 OneNetEntity one =  tond.getOneNetValues(oneNetString); //��json�ַ������г�������
		 MsgOneNetEntity msg = one.getMsg();  
         String devMac =msg.getDev_id(); //device_id
         String mac =  tond.getMacByDeviceId(devMac);  //imei
         SmokeDao sd = new SmokeDaoImpl();
         int type = sd.getDeviceTypeByMac(mac); //��ȡ�豸���͡�
         switch(msg.getType()){ //�ж���Ϣ��������Ϣ�����豸��������Ϣ
         case "1": //������Ϣ
     		String dealValue = msg.getValue();
     		String ds_id = msg.getDs_id(); //������id  obj_id+"_"+obj_inst_id+"_"+res_id,�·�������������
     		tond.updateDs(mac, ds_id);//�洢ds_idֵ
     		switch (type) {
     		case  57: //�����Լ����̸�
     			/*1.��oneNetƽ̨���ص�msg���Ե�value�н�ȡ7C��ͷ1C��β����Ϊһ������
         		*2.crcУ��
         		*3.��������
         		*/
         		String packet = getPacket(dealValue); //��valueת��Ϊ��д�����н�ȡ���ʵ��ַ���
         		byte[] data = IntegerTo16.hexString2Bytes(packet);  //���ð�תΪbyte[]
         		if(checkCrc(data,data.length)) { //crcУ��ͨ��,Ȼ�������ݡ�
         			int deviceType = data[1] & 0xff;
         			switch(deviceType) {
         			case 225: //E1�̸�
         				byte[] dataLen = new byte[2]; 
         				dataLen[0] = data[2];
         				dataLen[1] = data[3];
//         				int datalen = IntegerTo16.bytesToInt(dataLen); //���ݳ���
         				int deviceState = data[4] & 0xff; //�豸״̬ 203CB���� 202CA������ 193C1�͵�ѹ
         				int alarmType = -1; //Ĭ���̸б�������-1
         				switch (deviceState){
         				case 203://�����豸������ʱ��,��ʹ֮����
         					pu.updateDeviceMac(mac);
         					break;
         				case 202:
         					alarmType = 202;
         					break;
         				case 193:
         					alarmType = 193;
         					break;
         				}
         				
         				if (alarmType != -1){ //����
         					mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
         					mFromRepeaterAlarmDao.addAlarmMsg(mac, "", alarmType , 1);  //��������Ϣ��ӵ�alarm����
         					 
         					mSmokeLineDao.smokeOffLine(mac, 1); //�������豸����
         					mSmokeLineDao.smokeOffLine2(mac, 1);
         					
         					mGetPushUserIdDao = new GetPushUserIdDaoImpl();
         					List<String> userList = mGetPushUserIdDao.getAllUser(mac);
         					List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //��ȡuseridsmoke������û����ڶ���֪ͨ
         					if(userList!=null&&userList.size()>0){
         						//����������Ϣ���߳�  �ֻ�����
         						mPushAlarmMsgDao = new  PushAlarmMsgDaoImpl();
         						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //��ȡios�û��б�
         						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType);
         						new MyThread(push,userList,iosMap).start();  
         						
         						//��ҳ���Ϳ���
         						new WebThread(userList,mac).start();
         						
         						//��������
         						if (txtUserList != null &&txtUserList.size()>0) {
         							new TxtThread(txtUserList,mac).start();        //����֪ͨ���߳�
         						}
         					}
         				}
//         				int voiceState = data[5] & 0xff; //����״̬ 00������ 01 ����
         				break; //�̸н���
         				
         			}//switch(deviceType) end
         			
         		}//crc��ȷ���ݴ������
     			break; //�����Լ����豸����
     			
     		case  58: //�ε��̸�
     			byte[] data2 = IntegerTo16.hexString2Bytes(dealValue); //�����յ���value�ַ���ת��Ϊbyte���� 
     			
     			int seq = IntegerTo16.byteToInt2(data2[0]); //��һ���ֽ�Ϊ���к�
     			
     			byte[] dataLen = new byte[2];  //��2��3�ֽ�Ϊ��������ݳ���
 				dataLen[0] = data2[2];
 				dataLen[1] = data2[1];
     			int  datalen =IntegerTo16.bytesToInt(dataLen); 

     			String jdmac = ""; //�豸mac 
     			int jdDeviceType = 0;  //�豸����
     			int jdState = 0; //�豸״̬
     			int jdSignal=0;//�ź�ǿ��
     			int jdPower = 0; //ʣ�����
     			
     			//���ݽ���
     			int index = 0;
     			while (index < datalen) { 
	     			int jdtype = IntegerTo16.byteToInt2(data2[3+index]); //jd��������
	     			
	     			byte[] jdLen = new byte[2];  //jd���ݳ���
	     			jdLen[0] = data2[5+index];
	     			jdLen[1] = data2[4+index];
         			int  jdlen =IntegerTo16.bytesToInt(jdLen);
         			
         			byte[] jdContent = new byte[jdlen];//jd��������
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
	     		case 0:  //����
//	     			//��������������������ߣ�,�ж��Ƿ��ǵ����ظ�
//		     		tond.updatePower(mac, jdPower+"");
	     			//�ж��Ƿ�Ϊ֮ǰ�Ƿ�Ϊ�͵�״̬������ָ����ƻָ�
		     		if (SmokeDaoImpl.ifPowerOff(mac)) {
		     			SmokeDaoImpl.setPowerState(mac, 0);
		     			alarmType=194;
		     		}
	     			break;
	     		case 1:  //������
	     			alarmType = 202;
	     			break;
	     		case 2: //�͵�ѹ
	     			alarmType = 193;
	     			//�����豸�ĵ���״̬Ϊ�͵�
	     			SmokeDaoImpl.setPowerState(mac, 1);
	     			break;
	     		case 8: //����
//	     			alarmType = 68;
	     			break;
	     		case 13: //0x0D����
	     			alarmType = 67; 
	     			break;
	     		case 17: //0x11 �������ָ�
	     			alarmType = 102;
	     			break;
	     		case 18: //0x12 �͵�ѹ�ָ�
	     			alarmType = 194;
	     			break;
	     		case 19: //0x13 ���𱨾�
	     			alarmType=14;
	     			break;
	     		case 20: //0x14 ���𱨾��ظ�
	     			alarmType=15;
	     			break;
	     		}
     		
	     		AreaDao ad2 = new AreaDaoImpl();
	    		int parentId2 = ad2.getSuperAreaIdBySmokeMac(mac);
	    		
	    		if(parentId2==9&&alarmType==193){//Խ�������ε͵�ѹ
	    			return;
	    		}
	    		
	    		
	    		//���������Ϣ�������豸����
	    		tond.updatePower2(mac, jdPower+"");
	    		
	     		if (alarmType != -1){ //��������
//	     			//���������Ϣ�������豸����
//	     			tond.updatePower2(mac, jdPower+"");
	     			
					mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
					mFromRepeaterAlarmDao.addAlarmMsg(mac, "", alarmType , 1);  //��������Ϣ��ӵ�alarm����
					 
					mGetPushUserIdDao = new GetPushUserIdDaoImpl();
					List<String> userList = mGetPushUserIdDao.getAllUser(mac);
					List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //��ȡuseridsmoke������û����ڶ���֪ͨ
					if(userList!=null&&userList.size()>0){
						//����������Ϣ���߳�  �ֻ�����
						mPushAlarmMsgDao = new  PushAlarmMsgDaoImpl();
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //��ȡios�û��б�
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType);
						new MyThread(push,userList,iosMap).start();  
						//��ҳ���Ϳ���
						
						new WebThread(userList,mac).start();
						//��������
						if (txtUserList != null &&txtUserList.size()>0) {
							new TxtThread(txtUserList,mac).start();        //����֪ͨ���߳�
							
							
							
							
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
	     		break; //�ε��豸����
	     		
     		} //switch(type) end
     		
     		break;  //msg.getType() == 1
		case "2": //�豸��������Ϣ
			switch(one.getMsg().getStatus()){
			case "0"://����
//				mSmokeLineDao.smokeOffLine(mac, 0);
				break;
			case "1"://����
				 mSmokeLineDao.smokeOffLine(mac, 1);
				 break;
			}
			
	    	 break;
	    
         } //switch(msg.getType) end
		
	}
	
	
	/**
	 * ���ַ���ת��Ϊ��д
	 * ���ַ����н�ȡ7C��ͷ1C��β����Ϊһ������
	 * @param orig ԭʼ�ַ���
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
	 * crcУ��
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
   	//����
    	String oneNetString = "{\"msg\":{\"at\":1547376858663,\"imei\":\"869405032227531\",\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"1300180100055035988261020001030300010D0400012405000164\",\"dev_id\":517680979},\"msg_signature\":\"9moiinGAi2rUBN9jNJddpg==\",\"nonce\":\"71b_O$!5\"}";
    	String url = "http://localhost:8080/fireSystem/uploadDevices.do";
    	String putJson = OneNetHttpMethod.postJson(url, oneNetString);
    	System.out.println(putJson);
    	

	}

}
