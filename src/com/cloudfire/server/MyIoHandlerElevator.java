package com.cloudfire.server;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;


import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.NeedSmokeDao;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.NeedSmokeDaoImpl;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.ElevatorInfoBeanEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.PcUserMap;
import com.cloudfire.entity.SessionMap;
import com.gexin.fastjson.serializer.SerializerFeature;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;




public class MyIoHandlerElevator extends IoHandlerAdapter {
	
	private NeedSmokeDao mNeedSmokeDao;
	private PcUserMap mPcUserMap;
	private GetPushUserIdDao mGetPushUserIdDao;
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// TODO Auto-generated method stub
		IoBuffer buffer=(IoBuffer) message;
		int dataLen=buffer.limit();
		byte [] data=new byte[dataLen];
		buffer.get(data);
		String dataString=new String(data);
		
		JsonParser jsonParser= new JsonParser();
		JsonObject jsonObject=jsonParser.parse(dataString).getAsJsonObject();
		String id=jsonObject.get("parameters").getAsJsonObject().get("ID").getAsString();
		JsonObject stateObject =jsonObject.get("state").getAsJsonObject();
		String overGroudString= stateObject.get("DS_DX").getAsString();
		String storeNumberString=stateObject.get("lou_Ceng").getAsString();
		String statusString=stateObject.get("status").getAsString();
		String doorString=stateObject.get("door").getAsString();
		String people=stateObject.get("people").getAsString();
		
		JsonObject alarmObject=jsonObject.get("alarm").getAsJsonObject();
		String alarmString=alarmObject.get("y_N").getAsString();
		String storeyStuckAlarm=alarmObject.get("kaceng").getAsString();
		String besiegeAlarmString=alarmObject.get("kunren").getAsString();
		String upperLimitString=alarmObject.get("UP_Alarm").getAsString();
		String lowerLimitString=alarmObject.get("down_Alarm").getAsString();
		String runOpenDoorString=alarmObject.get("run_door").getAsString();
		String overSpeed=alarmObject.get("speeding").getAsString();
		String netStateString=alarmObject.get("power").getAsString();
		
		String sqlString="insert into elevator(mac,overGround,storeyNumber,status,door,people,alarm,storeyStuckAlarm,besiegeAlarm,"+
				"upperLimitAlarm,lowerLimitAlarm,runOpenDoorAlarm,overSpeedAlarm,netState) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
				"on duplicate key update overGround=?,storeyNumber=?,"+
				"status=?,door=?,people=?,alarm=?,storeyStuckAlarm=?,besiegeAlarm=?,upperLimitAlarm=?,lowerLimitAlarm=?,runOpenDoorAlarm=?,"+
				"overSpeedAlarm=?,netState=?";
		Connection connection =DBConnectionManager.getConnection();
		PreparedStatement preparedStatement=DBConnectionManager.prepare(connection, sqlString);
		try{
			preparedStatement.setString(1, id);
			preparedStatement.setByte(2, Byte.parseByte(overGroudString));
			preparedStatement.setShort(3, Short.valueOf(storeNumberString));
			preparedStatement.setByte(4, Byte.parseByte(statusString));
			preparedStatement.setByte(5, Byte.parseByte(doorString));
			preparedStatement.setByte(6, Byte.parseByte(people));
			preparedStatement.setByte(7, Byte.parseByte(alarmString));
			preparedStatement.setByte(8, Byte.parseByte(storeyStuckAlarm));
			preparedStatement.setByte(9, Byte.parseByte(besiegeAlarmString));
			preparedStatement.setByte(10, Byte.parseByte(upperLimitString));
			preparedStatement.setByte(11, Byte.parseByte(lowerLimitString));
			preparedStatement.setByte(12, Byte.parseByte(runOpenDoorString));
			preparedStatement.setByte(13, Byte.parseByte(overSpeed));
			preparedStatement.setByte(14, Byte.parseByte(netStateString));
			
			preparedStatement.setByte(2+13, Byte.parseByte(overGroudString));
			preparedStatement.setShort(3+13, Short.valueOf(storeNumberString));
			preparedStatement.setByte(4+13, Byte.parseByte(statusString));
			preparedStatement.setByte(5+13, Byte.parseByte(doorString));
			preparedStatement.setByte(6+13, Byte.parseByte(people));
			preparedStatement.setByte(7+13, Byte.parseByte(alarmString));
			preparedStatement.setByte(8+13, Byte.parseByte(storeyStuckAlarm));
			preparedStatement.setByte(9+13, Byte.parseByte(besiegeAlarmString));
			preparedStatement.setByte(10+13, Byte.parseByte(upperLimitString));
			preparedStatement.setByte(11+13, Byte.parseByte(lowerLimitString));
			preparedStatement.setByte(12+13, Byte.parseByte(runOpenDoorString));
			preparedStatement.setByte(13+13, Byte.parseByte(overSpeed));
			preparedStatement.setByte(14+13, Byte.parseByte(netStateString));
			
			preparedStatement.executeUpdate();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		finally{
			DBConnectionManager.close(preparedStatement);
			DBConnectionManager.close(connection);
		}
		
		Thread.sleep(5);
		String mac = id;
		ElevatorInfoBeanEntity eibe = null;
		HttpRsult hr = null;
		Object result = null;
		if(mac==null){
			hr = new HttpRsult();
			hr.setError("��������");
			hr.setErrorCode(1);
			result = hr;
		}else{
			mNeedSmokeDao = new NeedSmokeDaoImpl();
			eibe = mNeedSmokeDao.getOneElevatorDev(mac);
			if(eibe==null){
				hr = new HttpRsult();
				hr.setError("û������");
				hr.setErrorCode(2);
				result = hr;
			}else{
				eibe.setError("��ȡ�ɹ�");
				eibe.setErrorCode(0);
				result = eibe;
			}
		}
		
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		List<String> userList = mGetPushUserIdDao.getAllUser(mac);
		
		if(userList!=null&&userList.size()>0){
			//Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
			mPcUserMap = PcUserMap.newInstance();
			for(String userId :userList){
				String macStr = mPcUserMap.getUserMac(userId);
				if(macStr!=null&&macStr.length()>0){
					IoSession sessionPC = SessionMap.newInstance().getSession(macStr);
					if(session!=null){
						ByteBuffer byteBuffer = null;
						try {
							String pp=com.gexin.fastjson.JSONObject.toJSONString(result,SerializerFeature.WriteMapNullValue);							
							byteBuffer = ByteBuffer.wrap(pp.getBytes());
							IoBuffer buf = IoBuffer.wrap(byteBuffer);
					        WriteFuture future = sessionPC.write(buf);  
					        future.awaitUninterruptibly(100);
					        if( future.isWritten() ){
					        	
					        }else{
					        	
					        }
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		}
		
		
		
		/*
		 create table elevator (
	mac varchar(30) not null  primary key comment '����mac',
	overGround tinyint(1) not null default -1 comment '0������    1������' ,
	storeyNumber smallint(2) not null default -1 comment '¥����' ,
	status tinyint(1) not null  default -1  comment '��������״̬: 0:ֹͣ 1������ 2������',
	door tinyint(1) not null  default -1 comment '0������  1������'  ,
	people tinyint(1) not null  default -1  comment '0������  1������',
	alarm  tinyint(1) not null default -1 comment '�Ƿ��б�����Ϣ 0���� 1����',
	storeyStuckAlarm tinyint(1)  not null  default -1  comment '�Ƿ��п��㱨�� 0���� 1����',
	besiegeAlarm tinyint(1)  not null  default -1  comment '�Ƿ������˱��� 0���� 1����',
	upperLimitAlarm tinyint(1) not null default -1 comment '�Ƿ����ϼ��ޱ��� 0���� 1����',
	lowerLimitAlarm tinyint(1) not null default -1 comment '�Ƿ����¼��ޱ��� 0���� 1����',
	runOpenDoorAlarm tinyint(1) not null default -1 comment '�Ƿ��п������б��� 0���� 1����',
	overSpeedAlarm tinyint(1) not null default -1 comment '�Ƿ��г��ٱ��� 0���� 1����',
	netState tinyint(1) not null default -1 comment '0���豸����  1���豸����'
) engine=innodb default charset=gb2312;
		 */
		/*
		{
		    "Parameters": 
		    {
		        "ID":"0868182009536473"	ע���豸ID��
		    },
		    "State": 
		    {
		        "DS_DX":"1",			ע��0������    1������
		        "Lou_Ceng":"12",		ע��¥����
		        "status":"1",			ע����������״̬: 0:ֹͣ 1������ 2������
		        "Door":"1",			ע��0������  1������
		        "People":"1",			ע��0������  1������
		        "G_X": "93", 			ע��δ��
		        "G_Y": "2",  			ע��δ��
		        "G_Z": "18" 			ע��δ��

		    }, 
		    "Alarm": 
		    {
		        "Y_N":"1",			ע���Ƿ��б�����Ϣ 0���� 1����
		        "Kaceng":"1",			ע���Ƿ��п��㱨�� 0���� 1����
		        "Kunren":"1",			ע���Ƿ������˱��� 0���� 1����
		        "UP_Alarm":"1",			ע���Ƿ����ϼ��ޱ��� 0���� 1����
		        "Down_Alarm":"0",		ע���Ƿ����¼��ޱ��� 0���� 1����
		        "Run_door":"1",			ע���Ƿ��п������б��� 0���� 1����
		        "Speeding":"0",			ע���Ƿ��г��ٱ��� 0���� 1����
		        "Power":"0"			ע��0���豸����  1���豸����
		    }
		}
		*/
	}
	
}
