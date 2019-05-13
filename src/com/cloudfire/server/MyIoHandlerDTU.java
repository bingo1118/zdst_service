package com.cloudfire.server;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.cloudfire.dao.DtuDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.DtuDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.entity.DtuData;
import com.cloudfire.entity.DtuDataGroup;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.Water;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.JavaByteArrToHex;

public class MyIoHandlerDTU extends IoHandlerAdapter {
	private final static Log log = LogFactory.getLog(MyIoHandler.class);
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------sessionOpened----------------");
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------sessionClosed----------------");
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------sessionCreated----------------");
//		sessionMap = SessionMap.newInstance();
//		mRepeaterMap = RepeaterMap.newInstance();
	}
	
	public static void main(String[] args) {
//		String str = "18 01 26 11 46 35";
//		SimpleDateFormat sdf = new SimpleDateFormat("yy MM dd hh mm ss");
//		try {
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(sdf.parse(str));
//			cal.add(Calendar.SECOND, 35);
//			System.out.println(cal.getTime());
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// TODO Auto-generated method stub
		IoBuffer buffer = (IoBuffer) message; 
		int byteLen = buffer.limit();
    	byte[] data = new byte[byteLen];
    	buffer.get(data);  //将缓冲区的数据读入byte[] data
    	System.out.println("1111111111111111111111111111--Received"+JavaByteArrToHex.bytesToHex(data).toUpperCase());
    	byte[] byteData = ClientPackage.dtuByte(data,byteLen);  //得到一个data中16进制是以"7E7E7E7E"开头的byte[]
    	String clientIP = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
    	if(byteData==null){
    		return;
    	}
    	System.out.println("1111111111111111111111111112--Received"+JavaByteArrToHex.bytesToHex(byteData).toUpperCase());
    	byteLen =byteData.length;
    	DtuData dtu = ClientPackage.getDataOfDTU(byteData, byteLen);
    	String imei = dtu.getDtuId();  //获取设备imei
    	String name =dtu.getDtuName(); //获取设备别名
    	int msgType = dtu.getMsgType();   //包的类型
    	byte[] ack = null;
    	switch (msgType) {
    	case 1: //注册 ack 01 msglen = 0   获取连接存入内存
//    		sessionMap.addSession(dtu.getDtuId(), session);  //将与DTU设备的session 存入内存，
    		ack = ClientPackage.ackToDTU(byteData,(byte)0x01);
    		break;
    	case 2: //心跳 ack 02 发起心跳收到回应后更新dtu的在线状态,需要一个定时器向dtu设备发送心跳。
    		break;
    	case 3: //数据 不用回复 存储上传的数据
    		break;
    	case 5: //AT命令 消息体包含如下内容：AT命令
    		break;
    	case 6: //点对点配置信息 ack 06 msglen = 0      消息长度=IMEI号个数*16 消息体：IMEI列表。
    		ack = ClientPackage.ackToDTU(byteData,(byte)0x06);
    		break;
    	case  7: //dtu数据采集间隔 ack 07  msglen = 0       消息长度 = 2bytes   消息内容：间隔时间，单位s。间隔时间范围（0~65535）
    		ack = ClientPackage.ackToDTU(byteData,(byte)0x07);
    		break;
    	case  8: //dtu每次发送的数据包数目 ack 08   msglen = 0        消息长度 = 1byte，范围（1~255）
    		ack = ClientPackage.ackToDTU(byteData,(byte)0x08);
    		break;
    	case 9: //智能表上传数据消息
    		//获取数据组
    		List<DtuDataGroup> lstdg2 = dtu.getLstdg();
    		int size2 = lstdg2.size(); 
    		
    		ack = ClientPackage.ackToDTU(byteData,(byte)0x09);
    		
    		int interval = dtu.getInterval(); //采集时间间隔
    		
    		String time = dtu.getTime(); //上传的采集时间
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("yy MM dd HH mm ss"); //上传的时间格式
    		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //存储在数据库中的时间格式
    		Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(time)); //将传过来的时间转为calendar对象
			
			int unit =lstdg2.get(0).getUnit(); //单位
			
			//获取设备的高低阈值，用来判断设备的历史状态
//			WaterInfoDao waterDao = new WaterInfoDaoImpl();
//			float waterlow = 0,waterhigh = 0;
//			if(unit==5){ //水位设备的阈值
//				waterlow = Float.parseFloat(waterDao.getWaterLow(imei));
//				waterhigh = Float.parseFloat(waterDao.getWaterHigh(imei));
//			}
//			else { //水压设备阈值
//				waterlow = Float.parseFloat(waterDao.getWaterLow2(imei));
//				waterhigh = Float.parseFloat(waterDao.getWaterHigh2(imei));
//			}
			
			DtuDao dd = new DtuDaoImpl(); 
			
    		for (int i = 0; i< size2;i++) {  //处理数据组，将数据存入数据库
    			time = sdf2.format(cal.getTime()); //获取数据采集时间并将之转化为数据库时间格式
	    		float value = lstdg2.get(i).getValue(); //值
	    		int state = lstdg2.get(i).getState();
//	    		switch(unit) {
//	    		case 1: //Mpa
//	    		case 2: //Bar
//	    		case 3: //Kpa
//	    			state = lstdg2.get(i).getState();  //压力有四种状态
//	    			int alarmType = 0;
//	    			switch(state) {
//	    			case 0:
//	    				break;
//	    			case 1: //低水压
//	    				alarmType = 209;
//	    				break;
//	    			case 2: //高水压
//	    				alarmType = 218;
//	    				break;
//	    			case 3: //故障
//	    				alarmType = 36;
//	    				break;
//	    			}
//	    			if (state != 0){  //报警数据入库
//	    				dd.addAlarmMsg(imei, time, alarmType);
//	    				state = alarmType;
//	    				mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
//						mFromRepeaterAlarmDao.addAlarmMsg(imei, "", alarmType,value+"");
//	    				//报警推送 手机 和 平台
//	    				mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
//	    				PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(imei,alarmType);
//	    				mGetPushUserIdDao = new GetPushUserIdDaoImpl();
//						List<String> userList = mGetPushUserIdDao.getAllUser(imei);
//						if(userList!=null&&userList.size()>0){
//							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
//							new MyThread(push,userList,iosMap).start();
//							new WebThread(userList,imei).start();
//						}
//	    			}
//	    			break;
//	    		case 4: //`温度C
//	    			//double temperature =value;
//	    			break;
//	    		case 5: //液位 M
//	    			WaterInfoDao waterDao = new WaterInfoDaoImpl();
//	    			state = waterDao.getWaterLeve(imei,value+"");
//	    			break;
//	    		case 6://流量 m^3/h
//	    			break;
//	    		case 7: //角度。C
//	    			break;
//	    		case 8: //浮球 18 48 88 88表示浮球启动
//	    			break;
//	    		}
    			
	    		dd.addDtuData(imei, state, value, unit,time); //将设备采集的数据入库waterinfo，unit已无效，水压为kPa,水位为m
	    		cal.add(Calendar.SECOND, interval);  //数据采集时间按采集时间间隔递增
    		} 
    		//数据组处理结束
    		
    		if (unit == 5 || unit == 1 || unit == 2 || unit == 3) { //水压，水位设备,根据最后一组数据的水位值来判断是否报警
	    		//水位设备的报警处理
    			DtuDataGroup  last =  lstdg2.get(lstdg2.size() - 1);
    			int alarmType2 = last.getState();
				switch(alarmType2) {
				case 207:	//水位低报警
				case 208:	//水位高报警
				case 209: //低水压报警
				case 218: //高水压报警
					mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
					PushAlarmMsgEntity push2 = mPushAlarmMsgDao.getPushAlarmMsg(imei,alarmType2);
					mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
					mFromRepeaterAlarmDao.addAlarmMsg(imei, "", alarmType2,last.getValue()+"");
					mGetPushUserIdDao = new GetPushUserIdDaoImpl();
					List<String> userList2 = mGetPushUserIdDao.getAllUser(imei);
					if(userList2!=null&&userList2.size()>0){  //推送
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList2);
						new MyThread(push2,userList2,iosMap).start();
						new WebThread(userList2,imei).start();
					}
					break;
				}
    		}
    		
//    		cal.add(Calendar.SECOND,-1*interval); //由于最后多加了一个时间间隔，把它调回
    		dd.updateDtu(imei, sdf2.format(new Date()),1); //将dtu 设备信息入库smoke，更新设备的心跳时间为当前时间，并使之上线
    		break;
    	}
	
		if(ack!=null){
			System.out.println("1111111111111111111111111113--Received"+JavaByteArrToHex.bytesToHex(ack).toUpperCase());
			IoBuffer buf = IoBuffer.wrap(ack);
	        WriteFuture future = session.write(buf);  //将ack回应给中继
	        future.awaitUninterruptibly(100);
	        if( future.isWritten() ){
	        	log.debug("---------send sucess!---------");
	        }else{
	        	log.debug("---------send failed!---------");
	        }
		}   
	}

}
