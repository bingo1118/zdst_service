package com.cloudfire.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.PrinterDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.ZTWObjectDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.ZTWObjectDaoImpl;
import com.cloudfire.entity.AlarmPushOnlyEntity;
import com.cloudfire.entity.Nb_SmokeAlarmEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.entity.ZTWObjectEntity;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.ClientPackage6501;
import com.cloudfire.until.Constant;
import com.cloudfire.until.IfStopAlarm;
import com.cloudfire.until.IntegerTo16;
import com.cloudfire.until.IpUtil;
import com.cloudfire.until.Utils;

public class ZTWMyIoHandler extends IoHandlerAdapter{
	private final static Log log = LogFactory.getLog(ZTWMyIoHandler.class);
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private GetSmokeMacByRepeaterDao mGetSmokeMacByRepeaterDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private ElectricTypeInfoDao mElectricTypeInfoDao;
	private AlarmThresholdValueDao mAlarmThresholdValueDao;
	private SessionMap sessionMap;
	private TimerMap mTimerMap;
	private String deviceMac;
//	private RepeaterMap mRepeaterMap;
	private PrinterDao mPrinterDao;
	private SmokeLineDao mSmokeLineDao;
	private WaterInfoDao mWaterInfoDao;
	private RePeaterDataDao repeaterDao;
	private Map<String,Integer> AckEleMap ;
	private PublicUtils utilsDao;

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.debug("-------------sessionOpened----------------");
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.debug("-------------sessionClosed----------------");
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		log.debug("-------------sessionCreated----------------");
		sessionMap = SessionMap.newInstance();
//		mRepeaterMap = RepeaterMap.newInstance();
	}
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		IoBuffer buffer = (IoBuffer) message; 
		int byteLen = buffer.limit();
    	byte[] data = new byte[byteLen];
    	buffer.get(data);  //将缓冲区的数据读入byte[] data
    	String localIp = IpUtil.getIP(1); //获取本机外网Ip
		byte[] byetData = data;//ClientPackage.filerByte(data,byteLen);//{7e,(byte)0x0e,(byte)0x04,(byte)0x94,(byte)0x3a,(byte)0x44,(byte)0x00,(byte)0x12,(byte)0x12,(byte)0x17,(byte)0x20,(byte)0x3e,(byte)0xcc,(byte)0x85,(byte)0x71,(byte)0x00,(byte)0x02,(byte)0xe5,(byte)0x3e,(byte)0x01,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x05,(byte)0x55,(byte)0x06,(byte)0x00,(byte)0x64,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x55,(byte)0x07,(byte)0x00,(byte)0xe8,(byte)0x03,(byte)0x04,(byte)0x28,(byte)0x00,(byte)0x23,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x1c,(byte)0x00,(byte)0x55,(byte)0x08,(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x55,(byte)0x09,(byte)0x00,(byte)0x64,(byte)0x00,(byte)0x04,(byte)0x4a,(byte)0x0d,(byte)0xfd,(byte)0x0c,(byte)0x2a,(byte)0x0d,(byte)0xc8,(byte)0x0d,(byte)0xb3,(byte)0x5f,(byte)0x29,(byte)0x0f,(byte)0x7f};
    	int i = byetData[0]&0xff;
    	System.out.println("ZTWMyIoHandler222 :============="+IntegerTo16.bytes2Hex(byetData));
		if(byetData==null||((byetData[0]&0xff)!=55)){
    		return;
    	}
    	System.out.println("ZTWMyIoHandler :============="+IntegerTo16.bytes2Hex(byetData));
    	byteLen =byetData.length;
    	int cmd1 = byetData[1]&0xff;   //0E的10进制:14 与0xff做与运算是因为byte有符号为，表示-128--127，而要取的是它表示的无符号数。
    	byte[] ack = null;
    	System.out.println("ZTWMyIoHandler :=============11111111111"+cmd1);
    	switch (cmd1) {
    	case 69:
    		RePeaterData mRePeaterData = ClientPackage6501.isHeart(byetData,byteLen);   //对接受到的数据处理得到RePeaterData
    		if (mRePeaterData != null) {
				RePeaterDataDao rDao = new RePeaterDataDaoImpl();
				rDao.addRepeaterTime(mRePeaterData.getAlarmSmokeMac(),Constant.outerIp);
				int cmd2 = mRePeaterData.getCmd2();
				switch (cmd2) {
				case 1: // 设备-->发送心跳
					ack = ClientPackage6501.ackToNB_IOTClient(mRePeaterData,byetData,1);
					deviceMac = mRePeaterData.getAlarmSmokeMac();
					utilsDao = new PublicUtilsImpl();
					utilsDao.updateDeviceMac(deviceMac);
					break;
				case 2: // 设备-->发送报警信息
					ack = ClientPackage6501.ackToNB_IOTClient(mRePeaterData,byetData);
					pushAlarm(mRePeaterData,byetData);
					break;
				case 9://报警恢复
					ack = ClientPackage6501.ackToNB_IOTClient(mRePeaterData,byetData,9);
					pushAlarm(mRePeaterData,byetData);
					break;
				case 10://令甲的喷淋复合协议
					ZTWObjectEntity ztwObj = mRePeaterData.getZtwObj();
					if(ztwObj!=null){
						deviceMac = ztwObj.getDevImei_V();	//设备MAC
						if(StringUtils.isNotBlank(deviceMac)){
							ZTWObjectDao ztw = new ZTWObjectDaoImpl();
							ztw.saveObject(ztwObj);
							
							String ztwDevType = ztwObj.getDevType_V();//设备类型
							String devImsi = ztwObj.getDevImsi_V();	//imsi
							String rssiValue = ztwObj.getSignal_V(); //信号强度
							String battery = ztwObj.getBattery_V();	//电池量
							String alarmType = ztwObj.getDevState_V();	//设备状态
							String estateId = ztwObj.getEstate_V();	//小区ID
							String receiptSignal = ztwObj.getReceiptSignal_V();//接收信号强度
							String signal_noise = ztwObj.getSignal_noise_V();//信噪比
							String coverageLeve = ztwObj.getCoverageLeve_V();//覆盖等级
							String earfcn = ztwObj.getEARFCN_V();	//频点
							int ifAlarm = 0;
							AlarmPushOnlyEntity pushEntity = new AlarmPushOnlyEntity();
							pushEntity.setSmokeMac(deviceMac);
							pushEntity.setRepeaterMac(deviceMac);
							
							switch(ztwDevType){ //设备类型
								case "0002"://0x0002	燃气（防爆）设备带一个值的
								case "0003"://0x0003	水压（防爆）
								case "0004"://0x0004	水压（普通）
									break;
								case "0005"://0x0005	喷淋	设备不带值的
								case "0001"://0x0001  烟感 
									break;
								default:
									break;
							}
							switch(alarmType){
								case "0001"://0x0001 正常心跳
									break;
								case "0002"://0x0002 普通报警
									ifAlarm = 1;
									switch(ztwDevType){
									case "0001"://0x0001烟感报警
										pushEntity.setAlarmType(202);
										pushEntity.setAlarmFamily("0");
										break;
									case "0002"://0x0002 燃气报警
										pushEntity.setAlarmType(2);
										pushEntity.setAlarmFamily("0");
										break;
									}
									break;
								case "0003"://0x0003 故障
									pushEntity.setAlarmType(36);
									pushEntity.setAlarmFamily("0");
									ifAlarm = 1;
									break;
								case "0004"://0x0004 低电量
									ifAlarm = 1;
									pushEntity.setAlarmType(193);
									pushEntity.setAlarmFamily("0");
									break;
								case "0005"://0x0005 开阀报警 
									ifAlarm = 1;
									pushEntity.setAlarmType(203);
									pushEntity.setAlarmFamily("0");
									break;
								case "0006"://0x0006 关阀报警
									ifAlarm = 1;
									pushEntity.setAlarmType(201);
									pushEntity.setAlarmFamily("0");
									break;
								
							}
							if(ifAlarm == 1){ //是否有报警
								Utils.pushAlarmInfo(pushEntity);
							}
						}
					}
					break;
				default:
					break;
				}
    		}
    		System.out.println("ZTWMyIoHandler :=============222222222"+IntegerTo16.bytes2Hex(ack));
    		break;
		default:
			break;
		}
		if(ack!=null){
			IoBuffer buf = IoBuffer.wrap(ack);
	        WriteFuture future = session.write(buf); 
	        future.awaitUninterruptibly(100);
	        if( future.isWritten() ){
	        	log.debug("---------send sucess!---------");
	        }else{
	        	log.debug("---------send failed!---------");
	        }
		}  
		
	}

	private void pushAlarm(RePeaterData mRePeaterData, byte[] byetData) {
		Map<String,Integer> AckEleMap = Utils.AckEleMap;
		String mac = mRePeaterData.getAlarmSmokeMac();
		if (StringUtils.isBlank(mac)) {
			mac = mRePeaterData.getRepeatMac();
			if(StringUtils.isBlank(mac)){
				return;
			}
		}
		int macState = mRePeaterData.getDeviceType();	//消音状态
		log.debug("macState======="+macState);
		AckEleMap.put(mac, macState);
		utilsDao = new PublicUtilsImpl();
		AreaDao areadao = new AreaDaoImpl();
		utilsDao.updateDeviceMac(mac,macState);
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
		int deviceType = mFromRepeaterAlarmDao.getDeviceType(mac);
		int deviceAreaId = areadao.getAreaIdBySmokeMac(mac);
		int alarmType2 = mRePeaterData.getRepeaterState();	//报警类型
		PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac, alarmType2);
		if (deviceType != 5) {
			mFromRepeaterAlarmDao.addAlarmMsg(mac,mRePeaterData.getRepeatMac(), alarmType2, 1);
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			List<String> userList = mGetPushUserIdDao.getAllUser(mac);
			List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //获取useridsmoke表里的用户用于短信通知

			if (userList != null && userList.size() > 0) {
				Map<String, String> iosMap = mGetPushUserIdDao.getIosUser(userList);
				if(IfStopAlarm.lastAlarmTime.containsKey(mac)){  //ifStopAlarm.map 停止报警设备表
					if((System.currentTimeMillis()-IfStopAlarm.lastAlarmTime.get(mac))>IfStopAlarm.cycleTime){
						new MyThread(push,userList,iosMap).start(); 
						if(mRePeaterData.getGasEntity()==null){
							new WebThread(userList,mac).start();  
						}else{
							new WebThread(mac,1).start();  
						}
						IfStopAlarm.lastAlarmTime.put(mac, System.currentTimeMillis());
					}
				}else{
					new MyThread(push,userList,iosMap).start();
					if(mRePeaterData.getGasEntity()==null){
						new WebThread(userList,mac).start();  
					}else{
						new WebThread(mac,1).start();  
					}
					IfStopAlarm.lastAlarmTime.put(mac, System.currentTimeMillis());
				}
				if (txtUserList != null &&txtUserList.size()>0) {
					new TxtThread(txtUserList,mac).start();        //短信通知的线程
				}
				if(deviceType==1||deviceType==41){
					float distance = mFromRepeaterAlarmDao.getLinkageDistance(mac);
					if(distance>0){
						PushAlarmMsgEntity push2=(PushAlarmMsgEntity)push.clone();
						push2.setDeviceType(119);
						List<String> listLinked=mFromRepeaterAlarmDao.getSmokeInLinkageDistance(mac, distance);
						log.debug("bingo0125:distance:"+distance);
						log.debug("bingo0125:listLinked:"+listLinked.toString());
						if(listLinked.size()>0){
							Set<String> userSetLinked = new HashSet<String>();
							for (String smokemac : listLinked) {
								mGetPushUserIdDao = new GetPushUserIdDaoImpl();
								userSetLinked.addAll(mGetPushUserIdDao.getAllUser(smokemac));
							}
							if(userSetLinked!=null&&userSetLinked.size()>0){
								for (String user : userList) {
									if(userSetLinked.contains(user)){
										userSetLinked.remove(user);//@@防止重复推送
									}
								}
								List<String> userListLinked=new ArrayList<String>(userSetLinked);
								log.debug("bingo0125:userListLinked:"+userListLinked.toString());
								Map<String,String> iosMap2 = mGetPushUserIdDao.getIosUser(userListLinked); //获取ios用户列表
								new MyThread(push2,userListLinked,iosMap2).start();  //开启推送消息的线程
							}
						}
					}
				}
			}
		}
		//联动报警---NB烟雾报警器多点联动功能，当同一组群内的2个点位及以上烟感同时告警时，平台确认为火警
		System.out.println("jinruliandongbaojing");
		Nb_SmokeAlarmEntity nbentity = (Nb_SmokeAlarmEntity)Utils.objectNb.get(deviceAreaId);
		double nowTime = System.currentTimeMillis();
		double oldTime = 0;
		if(nbentity!=null){
			oldTime = nbentity.getDevTime();
		}
		if((nbentity!=null)&&(!nbentity.getDeviceMac().equals(mac))&&(nowTime-oldTime<60000)){
			System.out.println("nbentity.getDeviceMac()="+nbentity.getDeviceMac()+"  Mac="+mac+" nowTime:"+nowTime+" oldTime"+oldTime);
			GetPushUserIdDao mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			List<String> userList = mGetPushUserIdDao.getMakeSurePushAreaUserIdByMac(mac);
			if(userList!=null&&userList.size()>0){
				PushAlarmMsgDao mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
				PushAlarmMsgEntity pushs = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType2);
				Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
				
				new MyThread(pushs,userList,iosMap).start();  
				if(mRePeaterData.getGasEntity()==null){
					new WebThread(userList,mac).start();  
				}else{
					new WebThread(mac,1).start();  
				}  
						
			}
		}else{
			nbentity = new Nb_SmokeAlarmEntity();
			nbentity.setDeviceMac(mac);
			nbentity.setDevTime(System.currentTimeMillis());
			Utils.objectNb.put(deviceAreaId, nbentity);
		}
		
		
		Utils.objectNb.put(deviceAreaId, nbentity);
	}

}
