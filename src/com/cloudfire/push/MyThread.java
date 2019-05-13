package com.cloudfire.push;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.dwr.push.PushMessageUtil;
import com.cloudfire.entity.PcUserMap;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.thread.WXpushThread;
import com.cloudfire.thread.getWXTokenTask;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;
import com.gexin.fastjson.JSONObject;
import com.gexin.fastjson.serializer.SerializerFeature;

public class MyThread extends Thread{
	private final static Log log = LogFactory.getLog(MyThread.class);
	private PushtoAPP mPushtoAPP;
	private Object content;
	private List<String> aliasList;
	private PcUserMap mPcUserMap;
	private Map<String,String> iosMap;
	private int type;
	private String mac;
	
	public MyThread(Object content,List<String> aliasList,Map<String,String> iosMap){
		this.aliasList = aliasList;
		this.content = content;
		this.iosMap = iosMap;
		System.out.println("MyThread:MyThread="+GetTime.ConvertTimeByLong());
	}
	
	public MyThread(Object content,List<String> aliasList,Map<String,String> iosMap,int type){
		this.aliasList = aliasList;
		this.content = content;
		this.iosMap = iosMap;
		this.type = type;
	}
	
	public MyThread(Object content,List<String> aliasList,Map<String,String> iosMap,int type,String mac){
		this.aliasList = aliasList;
		this.content = content;
		this.iosMap = iosMap;
		this.type = type;
		this.mac = mac;
	}
	
	public void run() {  
		if(content instanceof PushAlarmMsgEntity){
			PushAlarmMsgEntity entity=(PushAlarmMsgEntity)content;
			String devID=entity.getMac();
			if("84400104".equals(devID)){
				return;
			}
		}
		
		pushtoWX((PushAlarmMsgEntity)content,aliasList);//@@推送微信服务号
		
		mPushtoAPP = new PushtoAPP();
		if(aliasList!=null&&aliasList.size()>0&&type!=1){
			mPushtoAPP.alarmPushToApp(content, aliasList);
		}
		
		
		if(iosMap!=null&&iosMap.size()>0){
			mPushtoAPP.pushNotificationToIOS(content, iosMap);
		}
		mPcUserMap = PcUserMap.newInstance();
		for(String userId :aliasList){
			String macStr = mPcUserMap.getUserMac(userId);
			if(macStr!=null&&macStr.length()>0){
				IoSession session = SessionMap.newInstance().getSession(macStr);
				log.debug("测试session是否为空:"+session);
				if(session!=null){
					ByteBuffer byteBuffer = null;
					try {
						String pp = JSONObject.toJSONString(content,SerializerFeature.WriteMapNullValue);
						byteBuffer = ByteBuffer.wrap(pp.getBytes());
						IoBuffer buf = IoBuffer.wrap(byteBuffer);
				        WriteFuture future = session.write(buf);  
				        future.awaitUninterruptibly(100);
				        log.debug("PC推送已执行完");
				        if( future.isWritten() ){
				        	log.debug("报警消息发送PC端sucess!");
				        }else{
				        	log.debug("报警消息发送PC端failed!");
				        }
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			

		}
		
	}

	private void pushtoWX(PushAlarmMsgEntity content, List<String> aliasList) {
		System.out.println("WXPUSH_getin");
		Map<String,String> map=getUseridOpenid();
//		System.out.println("WXPUSHuserlist:"+map.toString());
		if(map!=null&&map.size()>0){
			String alarmTypeName=getAlarmTypeName(content.getAlarmType());
			for (String alia : aliasList) {
				if(map.containsKey(alia)){
					System.out.println("WXPUSHuserid:"+alia);
					new Thread(new WXpushThread(map.get(alia),content,alarmTypeName)).start();
				}
			}
		}
	}
	
	private static String getAlarmTypeName(String alarmType) {
		String sql="select * from alarmtype where alarmId =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			ps.setString(1, alarmType+"");
			rs = ps.executeQuery();
			while(rs.next()){
				return rs.getString(2);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return "火警";
	}
	
	private static String getAlarmTypeName(int alarmType) {
		String sql="select * from alarmtype where alarmId =?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			ps.setString(1, alarmType+"");
			rs = ps.executeQuery();
			while(rs.next()){
				return rs.getString(2);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return "火警";
	}
	
	public Map<String,String> getUseridOpenid() {
		// TODO Auto-generated method stub
		String sql="select * from userid_openId";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		Map<String,String> map =null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(map==null){
					map = new HashMap<String, String>();
				}
				map.put(rs.getString(1), rs.getString(2));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return map;
	}
}
