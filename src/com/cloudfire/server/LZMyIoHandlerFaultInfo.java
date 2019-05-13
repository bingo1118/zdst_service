package com.cloudfire.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.EnvironmentDao;
import com.cloudfire.dao.FaultInfoDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.PrinterDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.AlarmThresholdValueDaoImpl;
import com.cloudfire.dao.impl.ElectricTypeInfoDaoImpl;
import com.cloudfire.dao.impl.EnvironmentDaoImpl;
import com.cloudfire.dao.impl.FaultInfoDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PrinterDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.entity.BQ100Entity;
import com.cloudfire.entity.BQ200Entity;
import com.cloudfire.entity.ChJEntity;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.EnvironmentEntity;
import com.cloudfire.entity.FaultDataEntity;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.PushAlarmToPCEntity;
import com.cloudfire.entity.RePeaterData;
//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.ThreePhaseMeterEntity;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.entity.Water;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.Client_Fault_Package;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IfStopAlarm;
import com.cloudfire.until.IntegerTo16;
import com.cloudfire.until.Utils;
import com.google.common.base.Strings;

import freemarker.template.utility.StringUtil;

public class LZMyIoHandlerFaultInfo extends IoHandlerAdapter{
	private final static Log log = LogFactory.getLog(MyIoHandler.class);
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
	private FaultDataEntity faultData = null;
	private WaterInfoDao mWaterInfoDao;
	private RePeaterDataDao repeaterDao;
	private Map<String,Integer> AckEleMap ;

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------sessionOpened----------------");
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------lianzongsessionClosed----------------");
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------lianzongsessionCreated----------------");
		log.debug(session+":"+session.toString());
		log.debug("-------------lianzongsessionCreated----------------");
		sessionMap = SessionMap.newInstance();
//		mRepeaterMap = RepeaterMap.newInstance();
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// TODO Auto-generated method stub
		String clientIP = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();  
		IoBuffer buffer = (IoBuffer) message;
		int byteLen = buffer.limit();
    	byte[] data = new byte[byteLen];
    	buffer.get(data);
    	byte[] byetData = ClientPackage.faultByte(data,byteLen);  //得到一个data中16进制是以4040开头，结尾是2323的byte[]
    	if(byetData==null){
    		return;
    	}
    	String repeaterMac = "";
    	int cmd1 = data[0]&0xff;
    	int cmd2 = data[byteLen-1]&0xff;
    	log.debug("lianzongfaultClientIP="+clientIP+"     cmd1+cmd2="+cmd1+"+"+cmd2);
    	log.debug("lianzongfaultAck==="+IntegerTo16.bytes2Hex(data));
    	byte[] ack = null;
    	if(cmd1==(data[1]&0xff)&&cmd2==(data[byteLen-1]&0xff)){
    		boolean crcRight = Client_Fault_Package.checkSumCrc(byetData);  //校验和校验
    		if(crcRight){
	    		int controllcmd = data[26]&0xff;
	    		byte[] macByte = new byte[6];
	    		for(int i=0;i<6;i++){
	    			macByte[i] = data[12+i];
	    		}
	    		repeaterMac = IntegerTo16.bytes2Hex(macByte);
	    		log.debug("lianzongrepeaterMac:"+repeaterMac);
	    		long nowTime = System.currentTimeMillis();
//				mRepeaterMap.addTime(repeaterMac, nowTime);
	    		RePeaterDataDao rDao = new RePeaterDataDaoImpl();
				rDao.addRepeaterTime(repeaterMac,clientIP);
				sessionMap.addSession(repeaterMac,session);
				utilsDao= new PublicUtilsImpl();
    			utilsDao.updateDeviceMac(repeaterMac);
	    		switch(controllcmd){
	    		case 254:
	    			byte[] byteData = Client_Fault_Package.getByteData(byetData);
	    			int lenByte = byteData.length;
	    			RePeaterData mRePeaterData = Client_Fault_Package.isHeart(byteData,lenByte);   //对接受到的数据处理得到RePeaterData
	    			if(mRePeaterData==null){
	    				break;
	    			}
	    			int cmd = mRePeaterData.getCmd()&0xff; //获取第三位，cmd2
	    			switch(cmd){
	    			case 4:   //设备-->发送透传
//						ack = ClientPackage.ackToClient(mRePeaterData,(byte)0x07);
						int transmissionType=mRePeaterData.getTransmissionType();
						log.debug("lianzongMyIoHandler:transmissionType="+transmissionType);
						switch (transmissionType) {
						/** begin*/
						case 6:
							Water water1 = mRePeaterData.getWater();
							WaterInfoDao waterLeveDao = new WaterInfoDaoImpl();
							log.debug("MyIoHandler:AlarmType="+water1.getAlarmType());
							switch (water1.getAlarmType()){
								case 201://@@ 喷淋停止报警  C9
									mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
									PushAlarmMsgEntity push1 = mPushAlarmMsgDao.getPushAlarmMsg(mRePeaterData.getWaterMac(),water1.getAlarmType());
									mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
									mFromRepeaterAlarmDao.addAlarmMsg(mRePeaterData.getWaterMac(), mRePeaterData.getWaterRepeaterMac(), 201,1);
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> userList = mGetPushUserIdDao.getAllUser(mRePeaterData.getWaterMac());
									if(userList!=null&&userList.size()>0){
										Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
										new MyThread(push1,userList,iosMap).start();
										new WebThread(userList,water1.getWaterMac()).start();
									}
									break;
								case 207:	//水位低报警
								case 136:	//485故障报警
								case 208:	//水位高报警
									log.debug(water1.getWaterMac()+"  :  "+water1.getAlarmType()+" : "+water1.getRepeaterMac()+" : "+water1.getAlarmType()+" : "+water1.getValue());
									mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
									PushAlarmMsgEntity push2 = mPushAlarmMsgDao.getPushAlarmMsg(water1.getWaterMac(),water1.getAlarmType());
									mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
									mFromRepeaterAlarmDao.addAlarmMsg(water1.getWaterMac(), water1.getRepeaterMac(), water1.getAlarmType(),water1.getValue());
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> userList2 = mGetPushUserIdDao.getAllUser(water1.getWaterMac());
									if(userList2!=null&&userList2.size()>0){
										Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList2);
										new MyThread(push2,userList2,iosMap).start();
										new WebThread(userList2,water1.getWaterMac()).start();
									}
									waterLeveDao.updateWaterLeve(water1);
									break;
								case 0:		//水位正常
									log.debug("repeaterMac:"+water1.getRepeaterMac()+" status="+water1.getAlarmType()+" value="+water1.getValue()+" mac:="+water1.getWaterMac());
									waterLeveDao.updateWaterLeve(water1);
									break;
							}
							break;
						case 4://环境探测器
							EnvironmentEntity env = mRePeaterData.getEnvironment();
							log.debug("envarimac="+env.getArimac());
							if(env !=null){
								EnvironmentDao eDao = new EnvironmentDaoImpl();
								eDao.addEnvironmentInfo(env);
								SmokeLineDao sDao = new SmokeLineDaoImpl();
								sDao.smokeOffLine(env.getArimac(),1);
							}
							break;
						case 3://水压设备
							Water water = mRePeaterData.getWater();
							String waterRepeaterMac = mRePeaterData.getWaterRepeaterMac();
							String waterMac = mRePeaterData.getWaterMac();
							int waterStatus = water.getAlarmType();
							String waterValue = water.getValue();
							int waterType = water.getType();
							switch (waterType) {
							case 1: //正常
								mWaterInfoDao = new WaterInfoDaoImpl();
								mWaterInfoDao.addWaterInfo(waterRepeaterMac, waterMac, waterStatus, waterValue);
								break;
							case 2://报警
								int waterAlarmType = water.getAlarmType();//报警的类型,有218和209
								int waterAlarmValue = Integer.parseInt(water.getValue());
								mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
								mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
								PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(waterMac, waterAlarmType);
								if (pushAlarm ==null) {
									break;
								}
								mFromRepeaterAlarmDao.addAlarmMsg(waterMac, waterRepeaterMac, waterAlarmType, waterAlarmValue);
								mWaterInfoDao = new WaterInfoDaoImpl();
								mWaterInfoDao.addWaterInfo(waterRepeaterMac, waterMac, waterStatus, waterValue);
								pushAlarm.setAlarmFamily(waterAlarmValue+"");
								pushAlarm.setAlarmType(waterAlarmType+"");
								mGetPushUserIdDao = new GetPushUserIdDaoImpl();
								List<String> users = mGetPushUserIdDao.getAllUser(waterMac);
								if(users!=null&&users.size()>0){
									Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
									new MyThread(pushAlarm,users,iosMap).start();
									new WebThread(users,waterMac).start();
								}
								break;
							default:
								break;
							}
							break;
						/** end */
						case 2://三江设备
							int printerAction = mRePeaterData.getPrinterAction();
							//printerAction开始
							switch (printerAction) {
							case 1:
								if(repeaterMac!=null&&repeaterMac.length()>0){
									String time = GetTime.ConvertTimeByLong();
									PrinterEntity mPrinterEntity =mRePeaterData.getmPrinterEntity();
									mPrinterEntity.setRepeater(repeaterMac);
									mPrinterEntity.setFaultTime(time);
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> pcUsers = mGetPushUserIdDao.getUserByRepeaterMac(repeaterMac);
									mPrinterDao = new PrinterDaoImpl();
									mPrinterDao.getPrinterInfo(mPrinterEntity);
									mPrinterDao.getPrinterAlarm(mPrinterEntity);
									if(pcUsers!=null&&pcUsers.size()>0){
										PushAlarmToPCEntity mPushAlarmToPCEntity = new PushAlarmToPCEntity();
										mPushAlarmToPCEntity.setMasterFault(mPrinterEntity);
										mPushAlarmToPCEntity.setDeviceType(221);
										new MyThread(mPushAlarmToPCEntity,pcUsers,null,2).start();
										new WebThread(pcUsers,mPrinterEntity.getFaultCode()).start();
									}
								}
								break;

							case 2:   //复位
								mPrinterDao = new PrinterDaoImpl();
								mPrinterDao.updateInfo(repeaterMac);
								break;
							case 3:  //连接正常
								if(repeaterMac!=null&&repeaterMac.length()>0){
									mPrinterDao = new PrinterDaoImpl();
									mPrinterDao.updateSmokeByRepeaterMac(repeaterMac, 1);
								}
								break;
							case 4:	//连接不正常
								if(repeaterMac!=null&&repeaterMac.length()>0){
									mPrinterDao = new PrinterDaoImpl();
									mPrinterDao.updateSmokeByRepeaterMac(repeaterMac, 0);
								}
								break;
							}
							//printerAction结束
							
							break;
						case 1://电气设备
							int devType = mRePeaterData.getDeviceType();
							String electricMac = mRePeaterData.getElectricMac();
//							utilsDao = new PublicUtilsImpl();
							utilsDao.updateDeviceMac(electricMac);
							if(electricMac==null){
								break;
							}
							mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
							switch (devType) { //表示设备所属系统 BQ200,BQ100,BQ100 2.0 诚佳电气  金特莱电气 1 2 3 4 5
								case 6://@@三相电表2018.01.16
									ThreePhaseMeterEntity meter=mRePeaterData.getThreePhaseMeterEntity();
									int heatType=meter.getType();
									mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
									switch(heatType){
										case 1://@@心跳
											String electricData=meter.getData();
											int data_type=meter.getData_type();
											int data_num=meter.getData_num();
											try{
												float electrData=Float.parseFloat(electricData);
												mElectricTypeInfoDao.addThreePhaseElectricInfo(electricData, electricMac, mRePeaterData.getRepeatMac()
														, data_type, data_num);
											}catch(Exception e){
												e.printStackTrace();
											}
											break;
										case 2://@@报警&&分合闸
											int ifalarm=meter.getIfalarm();
											if(meter.getAlarmType()!=0){//@@报警
												int alarmType = meter.getAlarmType();
												int alarmFamily = 0;
												mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
												PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmType);
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
											}
											int electricstate=meter.getElectric_state();//@@分合闸状态
											mSmokeLineDao = new SmokeLineDaoImpl();
											if(electricstate==1){  
												mSmokeLineDao.setElectrState(electricMac, 1);     //合闸
												log.debug("bingo_setElectrState(electricMac, 1):"+electricMac);
											}else if(electricstate==2){
												mSmokeLineDao.setElectrState(electricMac, 2);  //分闸
												log.debug("bingo_setElectrState(electricMac, 2):"+electricMac);
											}
											break;
										case 43://@@过压阈值
											mAlarmThresholdValueDao.addThresholdValue(electricMac, meter.getOverV(), meter.getOverV(), 
													meter.getOverV(), meter.getOverV(), mRePeaterData.getRepeatMac(), 43);
											break;
										case 44://@@欠压阈值
											mAlarmThresholdValueDao.addThresholdValue(electricMac, meter.getOverV(), meter.getOverV(), 
													meter.getOverV(), meter.getOverV(), mRePeaterData.getRepeatMac(), 44);
											break;
										case 45://@@过流阈值
											mAlarmThresholdValueDao.addThresholdValue(electricMac, meter.getOverI(), meter.getOverI(), 
													meter.getOverI(), meter.getOverI(), mRePeaterData.getRepeatMac(), 45);
											break;
										case 46://@@漏电阈值
											mAlarmThresholdValueDao.addThresholdValue(electricMac, meter.getLossI(), "", 
													"", "", mRePeaterData.getRepeatMac(), 46);
											break;
									}
									
									
									break;
								case 5://金特莱电气
									BQ100Entity jtlEntity = mRePeaterData.getmBQ100Entity();
									int jtltype = jtlEntity.getType();
									mAlarmThresholdValueDao=new AlarmThresholdValueDaoImpl();
									switch(jtltype){
									case 1:	//报警+故障
										List<String> listAlarm = jtlEntity.getListAlarm();
										for(String alarmFamilys :listAlarm){
											int alarmFamily = Integer.parseInt(alarmFamilys);
											int alarmType = jtlEntity.getAlarmType();
											mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
											PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmType);
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
											break;
										}
										log.debug("ssssssss8:==="+jtlEntity.getData8()+"   rep:==="+mRePeaterData.getRepeatMac()+"  = "+electricMac);
										if(jtlEntity.getList6()!=null&&jtlEntity.getList6().size()>0){
											mElectricTypeInfoDao.addAlarmElectric(jtlEntity.getList6(), electricMac, mRePeaterData.getRepeatMac(), 4, 6);
										}
										if(jtlEntity.getList7()!=null&&jtlEntity.getList7().size()>0){
											mElectricTypeInfoDao.addAlarmElectric(jtlEntity.getList7(), electricMac, mRePeaterData.getRepeatMac(), 4, 7);
										}
										if(StringUtils.isNotBlank(jtlEntity.getData8())){
											List<String> list = new ArrayList<String>();
											list.add(jtlEntity.getData8());
											mElectricTypeInfoDao.addAlarmElectric(list, electricMac, mRePeaterData.getRepeatMac(), 4, 8);
										}
										if(jtlEntity.getList9()!=null&&jtlEntity.getList9().size()>0){
											mElectricTypeInfoDao.addAlarmElectric(jtlEntity.getList9(), electricMac, mRePeaterData.getRepeatMac(), 4, 9);
										}
										break;
									case 2://心跳+正常
										mElectricTypeInfoDao.addElectricInfo(jtlEntity.getList6(), jtlEntity.getList7(), jtlEntity.getData8(), jtlEntity.getList9(), electricMac, mRePeaterData.getRepeatMac(), 2);
										break;
									case 3://阈值
										ElectricThresholdBean etb = jtlEntity.getmElectricThresholdBean();
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
										mAlarmThresholdValueDao.addThresholdValue(electricMac, tem.get(0), tem.get(0), 
												tem.get(0), tem.get(0), mRePeaterData.getRepeatMac(), 47);
										break;
									}
									break;
								case 4://诚佳电气
									ChJEntity chj=mRePeaterData.getChj();
									int qtype=chj.getType();  //1 心跳 2报警
									//设置阈值
									mAlarmThresholdValueDao=new AlarmThresholdValueDaoImpl();
									mAlarmThresholdValueDao.addThresholdValue(mRePeaterData.getElectricMac(),((Integer)chj.getThreshold()).toString(),"", "", "", mRePeaterData.getRepeatMac(), 45);
									
									switch(qtype){
									case 1:  //心跳
										List<String> list7=new ArrayList<String>();
										list7.add(chj.getData7());  //电流
										mElectricTypeInfoDao.addElectricInfo(null, list7,null, null, electricMac, mRePeaterData.getRepeatMac(), 3);  //诚佳电气的electricdevtype是3
										break;
									case 2://报警
										int alarmType = chj.getAlarmType();
										int alarmFamily = 0;
										if(Utils.isNullStr(chj.getData7())){
											alarmFamily = (int) Float.parseFloat(chj.getData7());
										}
										mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
										PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmType);
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
										list.add(chj.getData7());
										mElectricTypeInfoDao.addAlarmElectric(list, electricMac, mRePeaterData.getRepeatMac(), 3, 7);
										break;
									}
									break;
									
								case 1://BQ200
									BQ200Entity mBQ200Entity = mRePeaterData.getmBQ200Entity();
									int type = mBQ200Entity.getType();
									
									//type开始
									switch (type) {  //信息类型
									case 1://报警
										int alarmType = mBQ200Entity.getAlarmType();
										log.debug(mBQ200Entity.getAlarmData()+"mBQ200Entity.getAlarmData()");
										int alarmFamily = 0;
										if(Utils.isNullStr(mBQ200Entity.getAlarmData())){
											alarmFamily = (int) Float.parseFloat(mBQ200Entity.getAlarmData());
										}
										mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
										PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmType);
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
										int electricDevType = mBQ200Entity.getElectricDevType(); //update by lzo at 2017-7-3
										mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
											mElectricTypeInfoDao.addAlarmElectric(list, electricMac, mRePeaterData.getRepeatMac(), 1, electricDevType);
											break;
									case 2://心跳
										List<String> list6 = new ArrayList<String>();
										List<String> list7 = new ArrayList<String>();
										String list8 = mBQ200Entity.getData8();  //add by lzo at 2017-6-27
										list6.add(mBQ200Entity.getData6());
										list7.add(mBQ200Entity.getData7());
										mElectricTypeInfoDao.addElectricInfo(list6, list7, list8, null, //update by lzo at 2017-6-27
												electricMac, mRePeaterData.getRepeatMac(), 1);
										break;
									case 3://故障
										int family = (int) Float.parseFloat(mBQ200Entity.getAlarmData());
										int alarmData = mBQ200Entity.getAlarmType();
										mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
										mFromRepeaterAlarmDao.addAlarmMsg(electricMac, mRePeaterData.getRepeatMac(), alarmData,family);
										break;
										
									case 5://故障
										int family1 = Integer.parseInt(mBQ200Entity.getAlarmData());
//										log.debug(alarmData1+"alarmData1");
										int alarmData1 = mBQ200Entity.getAlarmType();
										mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
										mFromRepeaterAlarmDao.addAlarmMsg(electricMac, mRePeaterData.getRepeatMac(), alarmData1,family1);
										break;
									case 4://阈值
										ElectricThresholdBean etb = mBQ200Entity.getmElectricThresholdBean();
										String oc = etb.getOverCurrent();
										String op = etb.getOverpressure();
										String up = etb.getUnpressure();
										String le = etb.getLeakCurrent();
										mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
										mAlarmThresholdValueDao.addThresholdValue(electricMac, oc, "", 
												"", "", mRePeaterData.getRepeatMac(), 45);
										mAlarmThresholdValueDao.addThresholdValue(electricMac, op, "", 
												"", "", mRePeaterData.getRepeatMac(), 43);
										mAlarmThresholdValueDao.addThresholdValue(electricMac, up, "", 
												"", "", mRePeaterData.getRepeatMac(), 44);
										if(Utils.isNullStr(le)){
											mAlarmThresholdValueDao.addThresholdValue(electricMac, le, "", 
													"", "", mRePeaterData.getRepeatMac(), 46);
										}
										break;
									}
									//type结束
									
									break;
								case 2://BQ100
									BQ100Entity mBQ100Entity = mRePeaterData.getmBQ100Entity();
									int typeBQ100 = mBQ100Entity.getType();
									
									//typeBQ100开始
									switch (typeBQ100) {
									case 1://报警
										int alarmType = mBQ100Entity.getAlarmType();
										int alarmFamily = (int) Float.parseFloat(mBQ100Entity.getAlarmData());
										mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
										PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmType);
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
										mElectricTypeInfoDao.addAlarmElectric(list, electricMac, mRePeaterData.getRepeatMac(), 2, electricDevType);
										break;
									case 2://心跳
										List<String> list6 = mBQ100Entity.getList6();
										List<String> list7 = mBQ100Entity.getList7();
										List<String> list9 = mBQ100Entity.getList9();
										mElectricTypeInfoDao.addElectricInfo(list6, list7, mBQ100Entity.getData8(), list9, 
												electricMac, mRePeaterData.getRepeatMac(), 2);
										break;
									case 3://故障
										int family = (int) Float.parseFloat(mBQ100Entity.getAlarmData());
										int alarmData = mBQ100Entity.getAlarmType();
										mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
										mFromRepeaterAlarmDao.addAlarmMsg(electricMac, mRePeaterData.getRepeatMac(), alarmData,family);
										break;
									}
									//typeBQ100结束

									break;
								case 3://BQ100 2.0版本
									BQ100Entity bQ100Entity = mRePeaterData.getmBQ100Entity();
									int typeBQ100TwoPointZero = bQ100Entity.getType();
									
									//typeBQ100TwoPointZero开始
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
											
											//alarmFamily开始
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
											//alarmFamily结束
											
											if(alarmType>0){
												PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmType);
												if(pushAlarm==null){
													break;
												}
												pushAlarm.setAlarmFamily(alarmType+"");
												pushAlarm.setAlarmType(alarmFamily+"");
												mGetPushUserIdDao = new GetPushUserIdDaoImpl();
												List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
												if(users!=null&&users.size()>0){
													Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
													new MyThread(pushAlarm,users,iosMap).start();
													new WebThread(users,electricMac).start();
												}
												mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
												mFromRepeaterAlarmDao.addAlarmMsg(electricMac, mRePeaterData.getRepeatMac(), alarmType,alarmFamily);
												
											}
											break;
										case 3://故障
											int family = (int) Float.parseFloat(bQ100Entity.getAlarmData());
											int alarmData = bQ100Entity.getAlarmType();
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
									break;//typeBQ100TwoPointZero结束
							}
							break; //电气结束
						}
						break;//透传结束
	    			case 1:	//心跳
	    				log.debug("xintiao=========");
	    				if(mRePeaterData.getAlarmSmokeMac()!=null){
	    					utilsDao.updateDeviceMac(mRePeaterData.getAlarmSmokeMac());
	    				}
	    				break;
	    			case 2: //报警
	    				log.debug("baojing=========");
	    				String mac = mRePeaterData.getAlarmSmokeMac();
	    				if(mac==null){
							break;
						}
	    				utilsDao.updateDeviceMac(mac);
	    				mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
						mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
						int deviceType = mFromRepeaterAlarmDao.getDeviceType(mac);
						int alarmType2 = byteData[12]&0xff;
						if(alarmType2==202) {
							switch (deviceType) {
							case 1:	//烟感
								alarmType2 = 202;
								break;
							case 2: 	//燃气
								alarmType2 = 2;
								break;
							case 7:	//声光
								alarmType2 = 7;
								break;
							case 8:	//手报
								alarmType2 = 204;
								break;
							case 11:      //红外线
								alarmType2 = 206;
								break;
							case 12: 	//门磁
								alarmType2 = 205;
								break;
							case 15:     //水浸
								alarmType2 = 221;
								break;
							case 16:		//NB燃气
								alarmType2 = 2;
								break;
							} 
						}
						if(deviceType==18){//@@10.31  喷淋设备
							if((int)(mRePeaterData.getCmd()&0xff)==202){  
								mSmokeLineDao.setElectrState(mac, 1);     //设备开
								alarmType2 = 203;
							}else if((int)(mRePeaterData.getCmd()&0xff)==201){
								mSmokeLineDao.setElectrState(mac, 2);  //设备关
								alarmType2 = 201;
							}
						}//@@10.31 by liangbin
						SmokeBean mSmokeBean = mFromRepeaterAlarmDao.getSmoke(mac);
						log.debug("smokeBean:"+mSmokeBean.toString());
						if(mSmokeBean!=null){
							mSmokeBean.setRepeater(mRePeaterData.getRepeatMac());
							mSmokeBean.setMac(mac);
							PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType2);
							mSmokeBean = mFromRepeaterAlarmDao.getSmokeInfo(mSmokeBean); 
							log.debug("mSmokeBeanlist:==="+mSmokeBean.getList().toString());
							if(mSmokeBean.getList()!=null){
								ack = Client_Fault_Package.ackAlarmPackage(mSmokeBean,(byte)0x05,byetData);
//								ackaddress = Client_Fault_Package.ackAlarmAddress(mSmokeBean,(byte)0x06,byetData);
								log.debug("faultAckackAckackAckack==="+IntegerTo16.bytes2Hex(ack));
							}
							if(deviceType!=5){
								mFromRepeaterAlarmDao.addAlarmMsg(mac, mRePeaterData.getRepeatMac(), alarmType2 , 1);
								mGetPushUserIdDao = new GetPushUserIdDaoImpl();
								List<String> userList = mGetPushUserIdDao.getAllUser(mac);
								if(userList!=null&&userList.size()>0){
									Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); 
									if(IfStopAlarm.map.containsKey(mac)){ 
										if((System.currentTimeMillis()-IfStopAlarm.map.get(mac))>10*60*1000){
											new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
											new WebThread(userList,mac).start();
											new TxtThread(userList,mac).start();        //短信通知的线程
										}
									}else{
										new MyThread(push,userList,iosMap).start();
										new WebThread(userList,mac).start();
										new TxtThread(userList,mac).start();
									}//@@10.31 设备设置停止报警后间隔一小时再次恢复可以报警状态 by liangbin
								}
								//@@联动报警
								if(deviceType==1){
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
												Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userListLinked); //获取ios用户列表
												new MyThread(push2,userListLinked,iosMap).start();  //开启推送消息的线程
											}
										}
									}
								}
							}
						}
	    				break;
	    			}
	    			if(ack==null){
	    				System.out.println("ack===null");
	    			}else{
	    				log.debug("yunxingfirestack==="+IntegerTo16.bytes2Hex(ack));
	    			}
	    			break;
	    		case 1: // 控制命令，时间同步 ****
	    			break;
	    		case 2:	//发送数据、发送火灾报警和建筑消防设施运行状态等信息========
	    			faultData = Client_Fault_Package.sendDataFault(byetData, byteLen,2);
	    			if(faultData!=null){
	    				ack = faultData.getAckByte();
	    			}
	    			break;
	    		case 3: //确认、对控制命令和发送信息的确认回答****
	    			faultData = Client_Fault_Package.sendDataFault(byetData, byteLen,2);
	    			ack = faultData.getAckByte();
	    			break;
	    		case 4:	//请求、查询火灾报警和建筑消防设施运行状态等信息============
	    			faultData = Client_Fault_Package.sendDataFault(byetData, byteLen,2);
	    			ack = faultData.getAckByte();
	    			break;
	    		case 5:	//应答、返回查询的信息
	    			faultData = Client_Fault_Package.sendDataFault(byetData, byteLen,2);
	    			ack = faultData.getAckByte();
	    			break;
	    		case 6:	//否认、对控制指令和发送信息的否认回答*****=================
	    			faultData = Client_Fault_Package.sendDataFault(byetData, byteLen,2);
	    			ack = faultData.getAckByte();
	    			break;
	    		}
	    		if(faultData!=null){
	    			log.debug("lianzongfaultData:="+faultData.toString());
	    			faultData.setRepeaterMac(repeaterMac);
	    			FaultInfoDao faultDao = new FaultInfoDaoImpl();
//	    			faultDao.saveFaultInfoSystem(faultData);insertFaultInfoSystem
	    			faultDao.insertFaultInfoSystem(faultData);
	    			if(faultData.getPushState()==1){
	    				faultDao.insertFaultInfo(faultData);
	    			}
	    		}
	    	}
    	}
		
		if(ack!=null){
			IoBuffer buf = IoBuffer.wrap(ack);
	        WriteFuture future = session.write(buf);  
	        future.awaitUninterruptibly(100);
	        if( future.isWritten() ){
	        	log.debug("---------lianzongsend sucess!1111111---------:"+IntegerTo16.bytes2Hex(ack));
	        }else{
	        	log.debug("---------lianzongsend failed!1111111---------:"+IntegerTo16.bytes2Hex(ack));
	        }
		} 
	}
}
