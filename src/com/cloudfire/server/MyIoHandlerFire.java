package com.cloudfire.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.EnvironmentDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.PrinterDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.impl.AlarmThresholdValueDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.ElectricTypeInfoDaoImpl;
import com.cloudfire.dao.impl.EnvironmentDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.GetSmokeMacByRepeaterDaoImpl;
import com.cloudfire.dao.impl.PrinterDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.entity.BQ100Entity;
import com.cloudfire.entity.BQ200Entity;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.EnvironmentEntity;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.PushAlarmToPCEntity;
import com.cloudfire.entity.RePeaterData;
//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.thread.SmokeLineThread;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.LzstoneTimeTask;
import com.cloudfire.until.Utils;

public class MyIoHandlerFire extends IoHandlerAdapter {
	private final static Log log = LogFactory.getLog(MyIoHandlerFire.class);
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private GetSmokeMacByRepeaterDao mGetSmokeMacByRepeaterDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private ElectricTypeInfoDao mElectricTypeInfoDao;
	private AlarmThresholdValueDao mAlarmThresholdValueDao;
	private String ip;
	private SessionMap sessionMap;
	private TimerMap mTimerMap;
//	private RepeaterMap mRepeaterMap;
	private PrinterDao mPrinterDao;
	private SmokeLineDao mSmokeLineDao;
	private PublicUtils utilsDao;
	
	
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
//		log.debug("-------------sessionCreated----------------");
//		System.out.println("sessionCreated显示客户端的ip和端口:"+session.getRemoteAddress().toString());
		sessionMap = SessionMap.newInstance();
//		mRepeaterMap = RepeaterMap.newInstance();
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// TODO Auto-generated method stub
//		log.debug("-------------messageReceived----------------");
//		String clientIP = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
		IoBuffer buffer = (IoBuffer) message;
		int byteLen = buffer.limit();
		byte[] data = new byte[byteLen];
		buffer.get(data);
		byte[] byetData = ClientPackage.filerByte(data,byteLen);
    	if(byetData==null){
    		return;
    	}
    	byteLen =byetData.length;
    	int cmd1 = byetData[1]&0xff;
    	byte[] ack = null;
    	switch (cmd1) {
    	case 14:
    		log.debug("cmd1="+cmd1);
    		RePeaterData mRePeaterData = ClientPackage.isHeart(byetData,byteLen,1);
    		log.debug("mRePeaterData="+mRePeaterData.toString());
    		if(mRePeaterData!=null){
    			RePeaterDataDao rDao = new RePeaterDataDaoImpl();
				rDao.addRepeaterTime(mRePeaterData.getRepeatMac(),Constant.outerIp);
    			sessionMap.addSession(mRePeaterData.getRepeatMac(),session);
				int cmd2 = byetData[2]&0xff;
				log.debug("cmd2="+cmd2);
				switch (cmd2) {
				case 4:
					ack = ClientPackage.ackToClient(mRePeaterData,(byte)0x07);
					int transmissionType=mRePeaterData.getTransmissionType();
					log.debug("transmissionType="+transmissionType);
					switch (transmissionType) {
					case 1://电气设备
						int devType = mRePeaterData.getDeviceType();
						String electricMac = mRePeaterData.getElectricMac();
						utilsDao = new PublicUtilsImpl();
						utilsDao.updateDeviceMac(electricMac);
						if(electricMac==null){
							break;
						}
						mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
						log.debug("devType="+devType);
						switch (devType) {
							case 1://BQ200
								BQ200Entity mBQ200Entity = mRePeaterData.getmBQ200Entity();
								int type = mBQ200Entity.getType();
								switch (type) {
								case 1://报警
									int alarmType = mBQ200Entity.getAlarmType();
									int alarmFamily = (int) Float.parseFloat(mBQ200Entity.getAlarmData());
									mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
									PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmFamily);
									if(pushAlarm==null){
										break;
									}
									mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
									mFromRepeaterAlarmDao.addAlarmMsg(electricMac, mRePeaterData.getRepeatMac(), alarmType,alarmFamily);
									pushAlarm.setAlarmFamily(alarmType+"");
									pushAlarm.setAlarmType(alarmFamily+"");
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
									if(users!=null&&users.size()>0){
										Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
										new MyThread(pushAlarm,users,iosMap).start();
										new WebThread(users,electricMac).start();
									}
									List<String> list = new ArrayList<String>();
									list.add(mBQ200Entity.getAlarmData());
									int electricDevType = 0;
									mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
									switch (alarmType) {
										case 43:
										case 44:
											electricDevType=6;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ200Entity.getAlarmData(), "", "",
													"", mRePeaterData.getRepeatMac(), alarmType);
											break;
										case 45:
											electricDevType=7;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ200Entity.getAlarmData(), "", "",
													"", mRePeaterData.getRepeatMac(), alarmType);
											break;
										}
										mElectricTypeInfoDao.addAlarmElectric(list, electricMac, mRePeaterData.getRepeatMac(), 1, electricDevType);
										break;
								case 2://心跳
									List<String> list6 = new ArrayList<String>();
									List<String> list7 = new ArrayList<String>();
									list6.add(mBQ200Entity.getData6());
									list7.add(mBQ200Entity.getData7());
									mElectricTypeInfoDao.addElectricInfo(list6, list7, null, null, 
											electricMac, mRePeaterData.getRepeatMac(), 1);
									break;
								case 3://故障
									int family = (int) Float.parseFloat(mBQ200Entity.getAlarmData());
									int alarmData = mBQ200Entity.getAlarmType();
									/*PushAlarmMsgEntity pushAlarm200 = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmData);
									if(pushAlarm200==null){
										break;
									}
									pushAlarm200.setAlarmFamily(family);
									pushAlarm200.setAlarmType(alarmData);
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> users200 = mGetPushUserIdDao.getAllUser(electricMac);
									if(users200!=null&&users200.size()>0){
										Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users200);
										new MyThread(pushAlarm200,users200,iosMap).start();
									}*/
									mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
									mFromRepeaterAlarmDao.addAlarmMsg(electricMac, mRePeaterData.getRepeatMac(), alarmData,family);
									break;
								case 4://阈值
									ElectricThresholdBean etb = mBQ200Entity.getmElectricThresholdBean();
									String oc = etb.getOverCurrent();
									String op = etb.getOverpressure();
									String up = etb.getUnpressure();
									mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
									mAlarmThresholdValueDao.addThresholdValue(electricMac, oc, "", 
											"", "", mRePeaterData.getRepeatMac(), 45);
									mAlarmThresholdValueDao.addThresholdValue(electricMac, op, "", 
											"", "", mRePeaterData.getRepeatMac(), 43);
									mAlarmThresholdValueDao.addThresholdValue(electricMac, up, "", 
											"", "", mRePeaterData.getRepeatMac(), 44);
									break;
								}
								break;
							case 2://BQ100
								BQ100Entity mBQ100Entity = mRePeaterData.getmBQ100Entity();
								int typeBQ100 = mBQ100Entity.getType();
								switch (typeBQ100) {
								case 1://报警
									int alarmType = mBQ100Entity.getAlarmType();
									int alarmFamily = (int) Float.parseFloat(mBQ100Entity.getAlarmData());
									mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
									PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmFamily);
									if(pushAlarm==null){
										break;
									}
									mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
									mFromRepeaterAlarmDao.addAlarmMsg(electricMac, mRePeaterData.getRepeatMac(), alarmType,alarmFamily);
									pushAlarm.setAlarmFamily(alarmType+"");
									pushAlarm.setAlarmType(alarmFamily+"");
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
									if(users!=null&&users.size()>0){
										Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
										new MyThread(pushAlarm,users,iosMap).start();
										new WebThread(users,electricMac).start();
									}
									List<String> list = new ArrayList<String>();
									list.add(mBQ100Entity.getAlarmData());
									int electricDevType = 0;
									mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
									switch (alarmType) {
										case 43:
										case 44:
											electricDevType=6;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ100Entity.getAlarmData(), "", "",
													"", mRePeaterData.getRepeatMac(), alarmType);
											break;
										case 45:
											electricDevType=7;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ100Entity.getAlarmData(), "", "",
													"", mRePeaterData.getRepeatMac(), alarmType);
											break;
										case 46:
											electricDevType=8;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ100Entity.getAlarmData(), "", "",
													"", mRePeaterData.getRepeatMac(), alarmType);
											break;
										case 47:
											electricDevType=9;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ100Entity.getAlarmData(), "", "",
													"", mRePeaterData.getRepeatMac(), alarmType);
											break;
									}
									mElectricTypeInfoDao.addAlarmElectric(list, electricMac, mRePeaterData.getRepeatMac(), 2, electricDevType);
									break;
								case 2://心跳
									List<String> list6 = mBQ100Entity.getList6();
									List<String> list7 = mBQ100Entity.getList7();
									List<String> list9 = mBQ100Entity.getList9();
									log.debug("list6="+mBQ100Entity.getList6());
									log.debug("list7="+mBQ100Entity.getList7());
									log.debug("list9="+mBQ100Entity.getList9());
									mElectricTypeInfoDao.addElectricInfo(list6, list7, mBQ100Entity.getData8(), list9, 
											electricMac, mRePeaterData.getRepeatMac(), 2);
									break;
								case 3://故障
									int family = (int) Float.parseFloat(mBQ100Entity.getAlarmData());
									int alarmData = mBQ100Entity.getAlarmType();
									/*PushAlarmMsgEntity pushAlarm200 = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmData);
									if(pushAlarm200==null){
										break;
									}
									pushAlarm200.setAlarmFamily(family);
									pushAlarm200.setAlarmType(alarmData);
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> users200 = mGetPushUserIdDao.getAllUser(electricMac);
									if(users200!=null&&users200.size()>0){
										Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users200);
										new MyThread(pushAlarm200,users200,iosMap).start();
									}*/
									mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
									mFromRepeaterAlarmDao.addAlarmMsg(electricMac, mRePeaterData.getRepeatMac(), alarmData,family);
									break;
								}
								break;
							case 3://BQ100 2.0版本
								BQ100Entity bQ100Entity = mRePeaterData.getmBQ100Entity();
								int typeBQ100TwoPointZero = bQ100Entity.getType();
								log.debug("typeBQ100TwoPointZero = "+typeBQ100TwoPointZero);
								switch (typeBQ100TwoPointZero) {
									case 1://心跳
										List<String> list6 = bQ100Entity.getList6();
										List<String> list7 = bQ100Entity.getList7();
										List<String> list9 = bQ100Entity.getList9();
										mElectricTypeInfoDao.addElectricInfo(list6, list7, bQ100Entity.getData8(), list9, 
												electricMac, mRePeaterData.getRepeatMac(), 2);
										break;
									case 2://报警
										int alarmType = bQ100Entity.getAlarmType();
										mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
										int alarmFamily=-1;
										log.debug("alarmFamily="+alarmType);
										switch (alarmType) {
											case 44:
											case 43:
												List<String> List6 = bQ100Entity.getList6();
												alarmFamily = Utils.getString(List6);
												mElectricTypeInfoDao.addAlarmElectric(List6, electricMac, 
														mRePeaterData.getRepeatMac(), 2, 6);
												break;
											case 45:
												List<String> List7 = bQ100Entity.getList7();
												alarmFamily = Utils.getString(List7);
												mElectricTypeInfoDao.addAlarmElectric(List7, electricMac, 
														mRePeaterData.getRepeatMac(), 2, 7);
												break;
											case 46:
												String str8 = bQ100Entity.getData8();
												List<String> List8 = new ArrayList<String>();
												List8.add(str8);
												alarmFamily = (int) Float.parseFloat(str8);
												mElectricTypeInfoDao.addAlarmElectric(List8, electricMac, 
														mRePeaterData.getRepeatMac(), 2, 8);
												break;
											case 47:
												List<String> List9 = bQ100Entity.getList9();
												alarmFamily = Utils.getString(List9);
												mElectricTypeInfoDao.addAlarmElectric(List9, electricMac, 
														mRePeaterData.getRepeatMac(), 2, 9);
												break;
										}
										log.debug("str = "+alarmFamily);
										if(alarmFamily>0){
//											PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, str);
//											if(pushAlarm==null){
//												break;
//											}
//											pushAlarm.setAlarmFamily(alarmFamily);
//											pushAlarm.setAlarmType(str);
//											mGetPushUserIdDao = new GetPushUserIdDaoImpl();
//											List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
//											if(users!=null&&users.size()>0){
//												Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
//												new MyThread(pushAlarm,users,iosMap).start();
//											}
											mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
											mFromRepeaterAlarmDao.addAlarmMsg(electricMac, mRePeaterData.getRepeatMac(), alarmType,alarmFamily);
											
										}
										break;
									case 3://故障
										int family = (int) Float.parseFloat(bQ100Entity.getAlarmData());
										int alarmData = bQ100Entity.getAlarmType();
										/*PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmData);
										if(pushAlarm==null){
											break;
										}
										pushAlarm.setAlarmFamily(family);
										pushAlarm.setAlarmType(alarmData);
										mGetPushUserIdDao = new GetPushUserIdDaoImpl();
										List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
										if(users!=null&&users.size()>0){
											Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
											new MyThread(pushAlarm,users,iosMap).start();
										}*/
										mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
										mFromRepeaterAlarmDao.addAlarmMsg(electricMac, mRePeaterData.getRepeatMac(), alarmData,family);
										break;
									case 4://阈值
										ElectricThresholdBean etb = bQ100Entity.getmElectricThresholdBean();
										String lc = etb.getLeakCurrent();
										String oc = etb.getOverCurrent();
										String op = etb.getOverpressure();
										String up = etb.getUnpressure();
										List<String> tem = etb.getTemperatures();
										mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
										mAlarmThresholdValueDao.addThresholdValue(electricMac, lc, "", 
												"", "", mRePeaterData.getRepeatMac(), 46);
										mAlarmThresholdValueDao.addThresholdValue(electricMac, oc, "", 
												"", "", mRePeaterData.getRepeatMac(), 45);
										mAlarmThresholdValueDao.addThresholdValue(electricMac, op, "", 
												"", "", mRePeaterData.getRepeatMac(), 43);
										mAlarmThresholdValueDao.addThresholdValue(electricMac, up, "", 
												"", "", mRePeaterData.getRepeatMac(), 44);
										mAlarmThresholdValueDao.addThresholdValue(electricMac, tem.get(0), tem.get(1), 
												tem.get(2), tem.get(3), mRePeaterData.getRepeatMac(), 47);
										break;
									default:
										break;
								}
								break;
						}
						break;
					}
					break;
				case 6:
					System.out.println("send synchronousFire");
					String repeaterMac = mRePeaterData.getRepeatMac();
					if(repeaterMac!=null&&repeaterMac.length()>0){
						mGetSmokeMacByRepeaterDao = new GetSmokeMacByRepeaterDaoImpl();
						List<String> listStr = mGetSmokeMacByRepeaterDao.getSmokeMacByRepeater(repeaterMac);
						int count=listStr.size()*4+2;
						ack = ClientPackage.synchronousFire(mRePeaterData,listStr,count);
						Timer timer = new Timer();
			            timer.schedule(new LzstoneTimeTask(ack,session),5000,1*5000); 
						mTimerMap = TimerMap.newInstance();
						mTimerMap.addTimer(repeaterMac, timer);
					}
					break;
				case 153:
					String repeater = mRePeaterData.getRepeatMac();
					Timer mTimer = TimerMap.newInstance().getTimer(repeater);
					if(mTimer!=null){
						mTimer.cancel();
						mTimer=null;
					}
					break;
				default:
					break;
				}
			}
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
