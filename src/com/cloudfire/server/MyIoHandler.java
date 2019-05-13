package com.cloudfire.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.EnvironmentDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.LinkActionDao;
import com.cloudfire.dao.MeterInfoDao;
import com.cloudfire.dao.PrinterDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.RssiValueDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.WorkOrderDao;
import com.cloudfire.dao.impl.AlarmThresholdValueDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.ElectricTypeInfoDaoImpl;
import com.cloudfire.dao.impl.EnvironmentDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.GetSmokeMacByRepeaterDaoImpl;
import com.cloudfire.dao.impl.LinkActionDaoImpl;
import com.cloudfire.dao.impl.MeterInfoDaoImp;
import com.cloudfire.dao.impl.PrinterDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.RssiValueDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.entity.AlarmPushOnlyEntity;
import com.cloudfire.entity.BQ100Entity;
import com.cloudfire.entity.BQ200Entity;
import com.cloudfire.entity.ChJEntity;
import com.cloudfire.entity.ElectricHistoryEntity;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.EnvironmentEntity;
import com.cloudfire.entity.MqttElectricEntity;
import com.cloudfire.entity.MqttGasEntity;
import com.cloudfire.entity.MqttSmokeEntity;
import com.cloudfire.entity.MqttTempHumiEntity;
import com.cloudfire.entity.MqttWaterEntity;
import com.cloudfire.entity.PcUserMap;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.PushAlarmToPCEntity;
import com.cloudfire.entity.RePeaterData;
//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.entity.RssiEntityQuery;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SocketToPCEntity;
import com.cloudfire.entity.THDevice;
import com.cloudfire.entity.ThreePhaseMeterEntity;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.entity.TimerMapReSet;
import com.cloudfire.entity.UserMap;
import com.cloudfire.entity.Water;
import com.cloudfire.entity.WorkOrder;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.thread.SmokeLineThread;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IfStopAlarm;
import com.cloudfire.until.LzstoneTimeTask;
import com.cloudfire.until.SendCommand;
import com.cloudfire.until.Utils;

public class MyIoHandler extends IoHandlerAdapter{
	private final static Log log = LogFactory.getLog(MyIoHandler.class);
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private GetSmokeMacByRepeaterDao mGetSmokeMacByRepeaterDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private ElectricTypeInfoDao mElectricTypeInfoDao;
	private AlarmThresholdValueDao mAlarmThresholdValueDao;
	private SessionMap sessionMap;
	private TimerMap mTimerMap;
	private TimerMapReSet mTimerMapReSet;
//	private RepeaterMap mRepeaterMap;
	private PrinterDao mPrinterDao;
	private SmokeLineDao mSmokeLineDao;
	private WaterInfoDao mWaterInfoDao;
	private RePeaterDataDao repeaterDao;
	private Map<String,Integer> AckEleMap ;
	private PublicUtils utilsDao;
	private AreaDao areaDao;
	private WorkOrder wo;
	private SmokeDao smokeDao;
	private WorkOrderDao wod;
	private MeterInfoDao meterInfoDao;

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.debug("-------------sessionOpened----------------");
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
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
		byte[] byetData = ClientPackage.filerByte(data,byteLen);
    	if(byetData==null){
    		return;
    	}
    	byteLen =byetData.length;
    	int cmd1 = byetData[1]&0xff;   //0E的10进制:14 与0xff做与运算是因为byte有符号为，表示-128--127，而要取的是它表示的无符号数。
    	byte[] ack = null;
    	byte[] ack2 = null;
    	AreaDao ad = null;
    	Object obj = null;
    	switch (cmd1) {   
		case 14:
			RePeaterData mRePeaterData = ClientPackage.isHeart(byetData,byteLen,1);   //对接受到的数据处理得到RePeaterData
			if(mRePeaterData!=null){
				String repeater = mRePeaterData.getRepeatMac(); 
				RePeaterDataDao rDao = new RePeaterDataDaoImpl();
				rDao.addRepeaterTime(repeater,Constant.outerIp);
				sessionMap.addSession(repeater,session);
				MqttElectricEntity mee = null;
				MqttGasEntity mge = null;
				int cmd2 = byetData[2]&0xff; //获取第三位，cmd2
				switch (cmd2) {
				case 1:  //设备-->发送心跳，更新中继的电源状态，开启更新设备在线状态的线程
					ack = ClientPackage.ackToClient(mRePeaterData,(byte)0x99);
					long nowTime = System.currentTimeMillis();
//					mRepeaterMap.addTime(repeater, nowTime);  //更新内存（RepeaterMap.map repeater-心跳时间表）中repeater的最近一次心跳时间
					Map<String,Integer> rMap = Utils.repeaterStateMap;   // 中继电源状态表 repeaterState默认为0，1表示主电源、2表示备电源，3表示主备电源
//					log.debug("mRePeaterData.getRepeaterState()====="+mRePeaterData.getRepeaterState());
					if(mRePeaterData.getRepeaterState()!=0){  //中继电源状态不为0
//						log.debug("rMap.get(repeater====="+rMap.get(repeater));
						if(rMap.get(repeater)!=null){ //若中继电源状态表已存在该中继
							if(rMap.get(repeater)!=mRePeaterData.getRepeaterState()){ //中继的电源状态发生改变
								rMap.put(repeater, mRePeaterData.getRepeaterState());//将中继电源状态存入中继电源状态表
								repeaterDao = new RePeaterDataDaoImpl();
								repeaterDao.saveRepeaterInfo(mRePeaterData); //将中继的电源状态（repeaterMac,stateTime,repeaterState)存入repeaterInfo表
								//@@推送主机备电
								if(mRePeaterData.getRepeaterState()==2){
									if(Utils.repeaterStateAlarm.containsKey(repeater)){
										if((System.currentTimeMillis()-Utils.repeaterStateAlarm.get(repeater))>60*60*1000){
											pushRepeaterAlarm(mRePeaterData);
											Utils.repeaterStateAlarm.put(repeater, System.currentTimeMillis());
										}
									}else{
										pushRepeaterAlarm(mRePeaterData);
										Utils.repeaterStateAlarm.put(repeater, System.currentTimeMillis());
									}
								}else{
									if(Utils.repeaterStateAlarm.containsKey(repeater)){
										Utils.repeaterStateAlarm.remove(repeater);
									}
								}
							}
						}else{  //中继电源状态表中不存在该中继
							rMap.put(repeater, mRePeaterData.getRepeaterState()); //将中继电源状态存入中继电源状态表
							repeaterDao = new RePeaterDataDaoImpl();
							repeaterDao.saveRepeaterInfo(mRePeaterData); //将中继的电源状态（repeaterMac,stateTime,repeaterState)存入repeaterInfo表
							
						}
					}
					
					//屏蔽番职院的设备掉线
					AreaDao areaDaoImpl=new AreaDaoImpl();
					int pid=areaDaoImpl.getSuperAreaIdByRepeater(repeater);
					if (pid != 162) {
//					int areaid=areaDaoImpl.getAreaIdByRepeater(repeater);
//					if(areaid!=27&&areaid!=34&&areaid!=2052){//(27,34,2052)
						new SmokeLineThread(mRePeaterData,nowTime).start(); //更新设备的在线，离线状态
					}
					break;
				case 2:  //设备-->发送报警信息
					String mac = mRePeaterData.getAlarmSmokeMac();
					if(mac==null){
						break;
					}
					mSmokeLineDao = new SmokeLineDaoImpl();
					mSmokeLineDao.smokeOffLine(mac, 1);//将报警设备设置为上线
					mSmokeLineDao.smokeOffLine2(mac, 1);
					mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
					mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
					ad = new AreaDaoImpl();
					int deviceType = mFromRepeaterAlarmDao.getDeviceType(mac);//获取报警设备的deviceType（设备类型）
					int alarmType2 = mRePeaterData.getCmd()&0xff;
					if((mRePeaterData.getCmd()&0xff)==202) {//根据设备类型设置报警类型
						switch (deviceType) {
						case 1:	//烟感
							alarmType2 = 202;
							System.out.println("烟感报烟雾报警");
							LinkActionDao linkActionDao = new LinkActionDaoImpl();
							List<String> ResponseMac = linkActionDao.getLoraElectircMacByAlarmMac(mac);
							for(int i=0; i<ResponseMac.size(); i++) {
								System.out.println("响应的lora电气设备："+ResponseMac.get(i));
								SendCommand.stopLoraElectric(mac, ResponseMac.get(i), 2);//下发停lora电气的命令，有待测试
							}
							break;
						case 2: 	//燃气
						case 16:
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
						} 
					}
					ad = new AreaDaoImpl();
					int parentId = ad.getSuperAreaIdBySmokeMac(mac);
					if(parentId==9&&alarmType2==193){//越秀区
						return;
					}
					if(deviceType==1&&parentId==162&&alarmType2==202){
						boolean result = Utils.ifPushAlarmSmoke(mac);
						System.out.println("判断番禺职业学院烟感报警过滤误报数据。deviceType:"+deviceType+"parentId:"+parentId+"alarmType2:"+alarmType2+"result:"+result);
						if(result){
							return;//判断番禺职业学院烟感报警过滤误报数据。
						}
					}
					if(deviceType==2&&alarmType2==193){
						return;
					}//@@过滤燃气低电压报警
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
					if(mSmokeBean!=null){
						mSmokeBean.setRepeater(repeater);
						mSmokeBean.setMac(mac);		//add by lzo at 2017-11-24
						PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,alarmType2);
						byte[] numOne = push.getPrincipal1Phone().getBytes();
						byte[] numTwo = push.getPrincipal2Phone().getBytes();
						mSmokeBean = mFromRepeaterAlarmDao.getSmokeInfo(mSmokeBean); //add by lzo at 20171124
						ack = ClientPackage.ackAlarmPackageTwo(mRePeaterData,numOne,numTwo,mSmokeBean,(byte)0x05);
						if((deviceType!=5)&&(deviceType!=52)){//除了电气报警
//							log.debug("bingo0125:Alarm"+mac);
							mFromRepeaterAlarmDao.addAlarmMsg(mac, repeater, alarmType2 , 1);  //将报警信息添加到alarm表中
							
							if(parentId==162&&alarmType2==193){//番禺职业学院，低电压保存记录不推送。
								return;
							}
							mGetPushUserIdDao = new GetPushUserIdDaoImpl();
							List<String> userList = mGetPushUserIdDao.getAllUser(mac);
							List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //获取useridsmoke表里的用户用于短信通知
							if(userList!=null&&userList.size()>0){
								Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
								if(IfStopAlarm.map.containsKey(mac)){  //ifStopAlarm.map 停止报警设备表
									if((System.currentTimeMillis()-IfStopAlarm.map.get(mac))>10*60*1000){  //停止报警时间超过10分钟可以重新报警
										new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
										new WebThread(userList,mac).start();
										if (txtUserList != null &&txtUserList.size()>0) {
											new TxtThread(txtUserList,mac).start();        //短信通知的线程
										}
									}
								}else{
									new MyThread(push,userList,iosMap).start();
									new WebThread(userList,mac).start();
									if (txtUserList != null &&txtUserList.size()>0) {
										new TxtThread(txtUserList,mac).start();        //短信通知的线程
									}
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
				case 4:   //设备-->发送透传
					ack = ClientPackage.ackToClient(mRePeaterData,(byte)0x07);
					int transmissionType=mRePeaterData.getTransmissionType();
					log.debug("MyIoHandler:transmissionType="+transmissionType);
					switch (transmissionType) {
					case 9://72防爆燃气
						meterInfoDao = new MeterInfoDaoImp();
						utilsDao = new PublicUtilsImpl();
						ProofGasEntity gasEntity = mRePeaterData.getGasEntity();
						if (gasEntity!=null){
							int alarmType = 0;
							meterInfoDao.insertProofGas(gasEntity);
							switch (gasEntity.getProofGasState()) {
							case "0"://心跳
							case "2"://预热
								break;
							case "7":
							case "8":
							case "3":
								alarmType = 36;
								break;
							case "4":
							case "5":
								alarmType = 2;
								break;
							case "26":
								alarmType = 136;
								break;
							default:
								alarmType = Integer.parseInt(gasEntity.getProofGasState());
								break;
							}
							if(alarmType!=0){
								AlarmPushOnlyEntity pushEntity = new AlarmPushOnlyEntity();
								pushEntity.setSmokeMac(gasEntity.getProofGasMac());
								pushEntity.setRepeaterMac(repeater);
								pushEntity.setAlarmFamily(gasEntity.getProofGasMmol());
								pushEntity.setAlarmType(alarmType);
								Utils.pushAlarmInfo(pushEntity);
							}
							ad = new AreaDaoImpl();
						}
						break;
					case 8: //水浸
						Water leach = mRePeaterData.getWater();
						int leachAlarmType = leach.getAlarmType();
						switch(leachAlarmType){
						case 0:
							mSmokeLineDao = new SmokeLineDaoImpl();
							mSmokeLineDao.updateHeartime(mRePeaterData.getWaterMac());
							break;
						case 221:
							mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
							PushAlarmMsgEntity push2 = mPushAlarmMsgDao.getPushAlarmMsg(mRePeaterData.getWaterMac(),leachAlarmType);
							mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
							mFromRepeaterAlarmDao.addAlarmMsg(mRePeaterData.getElectricMac(), repeater, leachAlarmType,1);
							mGetPushUserIdDao = new GetPushUserIdDaoImpl();
							List<String> userList2 = mGetPushUserIdDao.getAllUser(mRePeaterData.getElectricMac());
							if(userList2!=null&&userList2.size()>0){
								Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList2);
								new MyThread(push2,userList2,iosMap).start();
								new WebThread(userList2,mRePeaterData.getElectricMac(),5).start();
							}
							break;
						}
						ack = null; //水浸不需要回复包
						break;
					/** begin*/
					case 7: //温湿度
						THDevice td =mRePeaterData.getTd();
						if(td!=null){
							td.setDevMac(mRePeaterData.getElectricMac());
							
							ad = new AreaDaoImpl();
						}
						int alarmType7_T=td.getAlarmType_T();
						if(alarmType7_T!=0){
							mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
							PushAlarmMsgEntity push2 = mPushAlarmMsgDao.getPushAlarmMsg(mRePeaterData.getElectricMac(),alarmType7_T);
							mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
							mFromRepeaterAlarmDao.addAlarmMsg(mRePeaterData.getElectricMac(), repeater, alarmType7_T,td.getAlarmValue_T());
							mGetPushUserIdDao = new GetPushUserIdDaoImpl();
							List<String> userList2 = mGetPushUserIdDao.getAllUser(mRePeaterData.getElectricMac());
							if(userList2!=null&&userList2.size()>0){
								Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList2);
								new MyThread(push2,userList2,iosMap).start();
								new WebThread(userList2,mRePeaterData.getElectricMac(),5).start();
							}
						}
						int alarmType7_H=td.getAlarmType_H();
						if(alarmType7_H!=0){
							mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
							PushAlarmMsgEntity push2 = mPushAlarmMsgDao.getPushAlarmMsg(mRePeaterData.getElectricMac(),alarmType7_H);
							mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
							mFromRepeaterAlarmDao.addAlarmMsg(mRePeaterData.getElectricMac(), repeater, alarmType7_H,td.getAlarmValue_H());
							mGetPushUserIdDao = new GetPushUserIdDaoImpl();
							List<String> userList2 = mGetPushUserIdDao.getAllUser(mRePeaterData.getElectricMac());
							if(userList2!=null&&userList2.size()>0){
								Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList2);
								new MyThread(push2,userList2,iosMap).start();
								new WebThread(userList2,mRePeaterData.getElectricMac()).start();
							}
						}
						
						//双向温湿度的回包不一样
						if(mRePeaterData.getDeviceType() == 26){
							ack = ClientPackage.ackToWaterGage(mRePeaterData,(byte)0x18);
						}
						
						break;
					case 6: //水位，喷淋
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
								List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mRePeaterData.getWaterMac()); //获取useridsmoke表里的用户用于短信通知
								if(userList!=null&&userList.size()>0){
									Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
									new MyThread(push1,userList,iosMap).start();
									new WebThread(userList,water1.getWaterMac()).start();
									if (txtUserList != null &&txtUserList.size()>0) {
										new TxtThread(txtUserList,mRePeaterData.getWaterMac()).start();        //短信通知的线程
									}
								}
								break;
							case 207:	//水位低报警
							case 136:	//485故障报警
							case 208:	//水位高报警
							case 193:	//低电量
								log.debug(water1.getWaterMac()+"  :  "+water1.getAlarmType()+" : "+water1.getRepeaterMac()+" : "+water1.getAlarmType()+" : "+water1.getValue());
								mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
								PushAlarmMsgEntity push2 = mPushAlarmMsgDao.getPushAlarmMsg(water1.getWaterMac(),water1.getAlarmType());
								mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
								mFromRepeaterAlarmDao.addAlarmMsg(water1.getWaterMac(), water1.getRepeaterMac(), water1.getAlarmType(),water1.getValue());
								mGetPushUserIdDao = new GetPushUserIdDaoImpl();
								List<String> userList2 = mGetPushUserIdDao.getAllUser(water1.getWaterMac());
								List<String> txtUserList2 = mGetPushUserIdDao.getPushUserIdByMac(water1.getWaterMac()); //获取useridsmoke表里的用户用于短信通知
								if(userList2!=null&&userList2.size()>0){
									Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList2);
									new MyThread(push2,userList2,iosMap).start();
									new WebThread(userList2,water1.getWaterMac()).start();
									if (txtUserList2 != null &&txtUserList2.size()>0) {
										new TxtThread(txtUserList2,water1.getWaterMac()).start();        //短信通知的线程
									}
								}
								boolean a = waterLeveDao.updateWaterLeve(water1);
								System.out.println("是否添加成功:"+a);
								System.out.println("-----------------水位发出报警--------------------");
								log.debug("repeaterMac:"+water1.getRepeaterMac()+" status="+water1.getAlarmType()+" value="+water1.getValue()+" mac:="+water1.getWaterMac());
								break;
							case 0:		//水位正常
							case 219:
								log.debug("repeaterMac:"+water1.getRepeaterMac()+" status="+water1.getAlarmType()+" value="+water1.getValue()+" mac:="+water1.getWaterMac());
								boolean b = waterLeveDao.updateWaterLeve(water1);
								System.out.println("是否添加成功:"+b);
								mSmokeLineDao = new SmokeLineDaoImpl();
								mSmokeLineDao.updateHeartime(water1.getWaterMac());
								System.out.println("------------------水位正常-----------------------");
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
						ack = ClientPackage.ackToClientByMobile(mRePeaterData,(byte)0x14);
						Water water = mRePeaterData.getWater();
						String waterRepeaterMac = mRePeaterData.getWaterRepeaterMac();
						String waterMac = mRePeaterData.getWaterMac();
						utilsDao = new PublicUtilsImpl();
						utilsDao.updateDeviceMac(waterMac);
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
							if(waterAlarmType!=193){
								mWaterInfoDao.addWaterInfo(waterRepeaterMac, waterMac, waterStatus, waterValue);
							}
							pushAlarm.setAlarmFamily(waterValue);
							pushAlarm.setAlarmType(waterAlarmType+"");
							mGetPushUserIdDao = new GetPushUserIdDaoImpl();
							List<String> users = mGetPushUserIdDao.getAllUser(waterMac);
							List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(waterMac); //获取useridsmoke表里的用户用于短信通知
							if(users!=null&&users.size()>0){
								Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
								new MyThread(pushAlarm,users,iosMap).start();
								new WebThread(users,waterMac).start();
								if (txtUserList != null &&txtUserList.size()>0) {
									new TxtThread(txtUserList,waterMac).start();        //短信通知的线程
								}
							}
							break;
						default:
							break;
						}
						break;
					/** end */
					case 2://三江设备
						String repeaterMac = repeater;
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
//								List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(repeaterMac); //获取useridsmoke表里的用户用于短信通知
								mPrinterDao = new PrinterDaoImpl();
								mPrinterDao.getPrinterInfo(mPrinterEntity);
								mPrinterDao.getPrinterAlarm(mPrinterEntity);
								if(pcUsers!=null&&pcUsers.size()>0){
									PushAlarmToPCEntity mPushAlarmToPCEntity = new PushAlarmToPCEntity();
									mPushAlarmToPCEntity.setMasterFault(mPrinterEntity);
									mPushAlarmToPCEntity.setDeviceType(221);
									new MyThread(mPushAlarmToPCEntity,pcUsers,null,2).start();
									new WebThread(pcUsers,mPrinterEntity.getFaultCode()).start();
//									if (txtUserList != null &&txtUserList.size()>0) {
//										new TxtThread(txtUserList,repeaterMac).start();        //短信通知的线程
//									}
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
//						utilsDao = new PublicUtilsImpl();
//						utilsDao.updateDeviceMac(electricMac);
						if(electricMac==null){
							break;
						}if(devType==5){
							sessionMap.addSession(electricMac,session);
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
											mElectricTypeInfoDao.addThreePhaseElectricInfo(electricData, electricMac, repeater
													, data_type, data_num);
										}catch(Exception e){
											e.printStackTrace();
										}
										break;
									case 2://@@报警&&分合闸
										int ifalarm=meter.getIfalarm();
//										if(ifalarm==1&&meter.getAlarmType()!=0){//@@报警
										if(meter.getAlarmType()!=0){//@@报警
											int alarmType = meter.getAlarmType();
											int alarmFamily = 0;
											mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
											PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmType);
											if(pushAlarm==null){
												break;
											}
											mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
											mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeater, alarmType,alarmFamily);
											pushAlarm.setAlarmFamily(alarmType+"");
											pushAlarm.setAlarmType(alarmFamily+"");
											mGetPushUserIdDao = new GetPushUserIdDaoImpl();
											List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
											List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(electricMac); //获取useridsmoke表里的用户用于短信通知
											if(users!=null&&users.size()>0){
												Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
												new MyThread(pushAlarm,users,iosMap).start();
												new WebThread(users,electricMac).start();
												if (txtUserList != null &&txtUserList.size()>0) {
													new TxtThread(txtUserList,electricMac).start();        //短信通知的线程
												}
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
												meter.getOverV(), meter.getOverV(), repeater, 43);
										break;
									case 44://@@欠压阈值
										mAlarmThresholdValueDao.addThresholdValue(electricMac, meter.getDownV(), meter.getDownV(), 
												meter.getDownV(), meter.getDownV(), repeater, 44);
										break;
									case 45://@@过流阈值
										mAlarmThresholdValueDao.addThresholdValue(electricMac, meter.getOverI(), meter.getOverI(), 
												meter.getOverI(), meter.getOverI(), repeater, 45);
										break;
									case 46://@@漏电阈值
										mAlarmThresholdValueDao.addThresholdValue(electricMac, meter.getLossI(), "", 
												"", "", repeater, 46);
										break;
								}
								
								
								break;
							case 5://金特莱电气
								BQ100Entity jtlEntity = mRePeaterData.getmBQ100Entity();
								int jtltype = jtlEntity.getType();
								ad = new AreaDaoImpl();
								mee = new MqttElectricEntity();
								mee.setServiceType("electric");
								mee.setDeviceId("77");
								mee.setMac(electricMac);
								mee.setAlarmType("0");
								
								mee.setTime(GetTime.ConvertTimeByLong());
								
								mAlarmThresholdValueDao=new AlarmThresholdValueDaoImpl();
								switch(jtltype){
								case 1:	//报警+故障
									List<String> listAlarm = jtlEntity.getListAlarm();
									for(String alarmFamilys :listAlarm){
										int alarmFamily = Integer.parseInt(alarmFamilys);
										int alarmType = jtlEntity.getAlarmType();
										mee.setAlarmType(alarmType+"");
										mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
										PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmType);
										if(pushAlarm==null){
											break;
										}
										mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
										mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeater, alarmType,alarmFamily);
										pushAlarm.setAlarmFamily(alarmType+"");
										pushAlarm.setAlarmType(alarmFamily+"");
										mGetPushUserIdDao = new GetPushUserIdDaoImpl();
										List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
										List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(electricMac); //获取useridsmoke表里的用户用于短信通知
										if(users!=null&&users.size()>0){
											Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
											new MyThread(pushAlarm,users,iosMap).start();
											new WebThread(users,electricMac).start();
											if (txtUserList != null &&txtUserList.size()>0) {
												new TxtThread(txtUserList,electricMac).start();        //短信通知的线程
											}
										}
										break;
									}
									log.debug("ssssssss8:==="+jtlEntity.getData8()+"   rep:==="+repeater+"  = "+electricMac);
									if(jtlEntity.getList6()!=null&&jtlEntity.getList6().size()>0){
										mElectricTypeInfoDao.addAlarmElectric(jtlEntity.getList6(), electricMac, repeater, 4, 6);
										mee.setVoltageA(jtlEntity.getList6().get(0));
										mee.setVoltageB(jtlEntity.getList6().get(1));
										mee.setVoltageC(jtlEntity.getList6().get(2));
									}
									if(jtlEntity.getList7()!=null&&jtlEntity.getList7().size()>0){
										mElectricTypeInfoDao.addAlarmElectric(jtlEntity.getList7(), electricMac, repeater, 4, 7);
										mee.setElectricityA(jtlEntity.getList7().get(0));
										mee.setElectricityB(jtlEntity.getList7().get(1));
										mee.setElectricityC(jtlEntity.getList7().get(2));
									}
									if(StringUtils.isNotBlank(jtlEntity.getData8())){
										List<String> list = new ArrayList<String>();
										list.add(jtlEntity.getData8());
										mElectricTypeInfoDao.addAlarmElectric(list, electricMac, repeater, 4, 8);
										mee.setSurplusElectri(jtlEntity.getData8());
									}
									if(jtlEntity.getList9()!=null&&jtlEntity.getList9().size()>0){
										mElectricTypeInfoDao.addAlarmElectric(jtlEntity.getList9(), electricMac, repeater, 4, 9);
										mee.setTemperatureA(jtlEntity.getList9().get(0));
										mee.setTemperatureB(jtlEntity.getList9().get(1));
										mee.setTemperatureC(jtlEntity.getList9().get(2));
										mee.setTemperatureN(jtlEntity.getList9().get(3));
									}
									obj = mee;
									break;
								case 2://心跳+正常
									mElectricTypeInfoDao.addElectricInfo(jtlEntity.getList6(), jtlEntity.getList7(), jtlEntity.getData8(), jtlEntity.getList9(), electricMac, repeater, 2);
									mee.setVoltageA(jtlEntity.getList6().get(0));
									mee.setVoltageB(jtlEntity.getList6().get(1));
									mee.setVoltageC(jtlEntity.getList6().get(2));
									
									mee.setElectricityA(jtlEntity.getList7().get(0));
									mee.setElectricityB(jtlEntity.getList7().get(1));
									mee.setElectricityC(jtlEntity.getList7().get(2));
									
									mee.setSurplusElectri(jtlEntity.getData8());
									mee.setTemperatureA(jtlEntity.getList9().get(0));
									mee.setTemperatureB(jtlEntity.getList9().get(1));
									mee.setTemperatureC(jtlEntity.getList9().get(2));
									mee.setTemperatureN(jtlEntity.getList9().get(3));
									
									obj = mee;
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
											"", "", repeater, 46);
									mAlarmThresholdValueDao.addThresholdValue(electricMac, oc, "", 
											"", "", repeater, 45);
									mAlarmThresholdValueDao.addThresholdValue(electricMac, op, "", 
											"", "", repeater, 43);
									mAlarmThresholdValueDao.addThresholdValue(electricMac, up, "", 
											"", "", repeater, 44);
									mAlarmThresholdValueDao.addThresholdValue(electricMac, tem.get(0), tem.get(0), 
											tem.get(0), tem.get(0), repeater, 47);
									break;
								}
								break;
							case 4://诚佳电气
								ChJEntity chj=mRePeaterData.getChj();
								int qtype=chj.getType();  //1 心跳 2报警
								//设置阈值
								mAlarmThresholdValueDao=new AlarmThresholdValueDaoImpl();
								mAlarmThresholdValueDao.addThresholdValue(mRePeaterData.getElectricMac(),((Integer)chj.getThreshold()).toString(),"", "", "", repeater, 45);
								
								switch(qtype){
								case 1:  //心跳
									List<String> list7=new ArrayList<String>();
									list7.add(chj.getData7());  //电流
									mElectricTypeInfoDao.addElectricInfo(null, list7,null, null, electricMac, repeater, 3);  //诚佳电气的electricdevtype是3
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
									mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeater, alarmType,alarmFamily);
									pushAlarm.setAlarmFamily(alarmType+"");
									pushAlarm.setAlarmType(alarmFamily+"");
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
									List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(electricMac); //获取useridsmoke表里的用户用于短信通知
									if(users!=null&&users.size()>0){
										Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
										new MyThread(pushAlarm,users,iosMap).start();
										new WebThread(users,electricMac).start();
										if (txtUserList != null &&txtUserList.size()>0) {
											new TxtThread(txtUserList,electricMac).start();        //短信通知的线程
										}
									}
									List<String> list = new ArrayList<String>();
									list.add(chj.getData7());
									mElectricTypeInfoDao.addAlarmElectric(list, electricMac, repeater, 3, 7);
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
									mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeater, alarmType,alarmFamily);
									pushAlarm.setAlarmFamily(alarmType+"");
									pushAlarm.setAlarmType(alarmFamily+"");
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
									List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(electricMac); //获取useridsmoke表里的用户用于短信通知
									if(users!=null&&users.size()>0){
										Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
										new MyThread(pushAlarm,users,iosMap).start();
										new WebThread(users,electricMac).start();
										if (txtUserList != null &&txtUserList.size()>0) {
											new TxtThread(txtUserList,electricMac).start();        //短信通知的线程
										}
									}
									List<String> list = new ArrayList<String>();
									list.add(mBQ200Entity.getAlarmData());
//									int electricDevType = 0;
									int electricDevType = mBQ200Entity.getElectricDevType(); //update by lzo at 2017-7-3
									mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
									/*switch (alarmFamily) {
										case 43:
										case 44:
											electricDevType=6;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ200Entity.getAlarmData(), "", "",
													"", repeater, alarmFamily);
											break;
										case 45:
											electricDevType=7;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ200Entity.getAlarmData(), "", "",
													"", repeater, alarmFamily);
											break;
										}*/
										mElectricTypeInfoDao.addAlarmElectric(list, electricMac, repeater, 1, electricDevType);
										Utils.saveChangehistory("6604",electricMac,0);//@@2018.04.17
										GetSmokeMacByRepeaterDao mbrDao = new GetSmokeMacByRepeaterDaoImpl();
										mbrDao.updateSmokeInfo(electricMac,1);//@@2018.03.27
										break;
								case 2://心跳
									List<String> list6 = new ArrayList<String>();
									List<String> list7 = new ArrayList<String>();
									String list8 = mBQ200Entity.getData8();  //add by lzo at 2017-6-27
									list6.add(mBQ200Entity.getData6());
									list7.add(mBQ200Entity.getData7());
//									mElectricTypeInfoDao.addElectricInfo(list6, list7, null, null, 
									mElectricTypeInfoDao.addElectricInfo(list6, list7, list8, null, //update by lzo at 2017-6-27
											electricMac, repeater, 1);
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
									mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeater, alarmData,family);
									break;
									
								case 5://故障
									int family1 = Integer.parseInt(mBQ200Entity.getAlarmData());
//									log.debug(alarmData1+"alarmData1");
									int alarmData1 = mBQ200Entity.getAlarmType();
//									log.debug("family1"+family1);
									/*PushAlarmMsgEntity pushAlarm2001 = mPushAlarmMsgDao.getPushAlarmMsg(electricMac, alarmData1);
									if(pushAlarm2001==null){
										break;
									}
									pushAlarm2001.setAlarmFamily(family1);
									pushAlarm2001.setAlarmType(alarmData1);
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> users2001 = mGetPushUserIdDao.getAllUser(electricMac);
									if(users2001!=null&&users2001.size()>0){
										Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users2001);
										new MyThread(pushAlarm2001,users2001,iosMap).start();
									}*/
									mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
									mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeater, alarmData1,family1);
									break;
								case 4://阈值
									ElectricThresholdBean etb = mBQ200Entity.getmElectricThresholdBean();
									String oc = etb.getOverCurrent();
									String op = etb.getOverpressure();
									String up = etb.getUnpressure();
									String le = etb.getLeakCurrent();
									mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
									mAlarmThresholdValueDao.addThresholdValue(electricMac, oc, "", 
											"", "", repeater, 45);
									mAlarmThresholdValueDao.addThresholdValue(electricMac, op, "", 
											"", "", repeater, 43);
									mAlarmThresholdValueDao.addThresholdValue(electricMac, up, "", 
											"", "", repeater, 44);
									if(Utils.isNullStr(le)){
										mAlarmThresholdValueDao.addThresholdValue(electricMac, le, "", 
												"", "", repeater, 46);
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
									mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeater, alarmType,alarmFamily);
									pushAlarm.setAlarmFamily(alarmType+"");
									pushAlarm.setAlarmType(alarmFamily+"");
									mGetPushUserIdDao = new GetPushUserIdDaoImpl();
									List<String> users = mGetPushUserIdDao.getAllUser(electricMac);
									List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(electricMac); //获取useridsmoke表里的用户用于短信通知
									if(users!=null&&users.size()>0){
										Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
										new MyThread(pushAlarm,users,iosMap).start();
										new WebThread(users,electricMac).start();
										if (txtUserList != null &&txtUserList.size()>0) {
											new TxtThread(txtUserList,electricMac).start();        //短信通知的线程
										}
									}
									List<String> list = new ArrayList<String>();
									list.add(mBQ100Entity.getAlarmData());
									int electricDevType = 0;
									/*mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
									switch (alarmFamily) {
										case 43:
										case 44:
											electricDevType=6;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ100Entity.getAlarmData(), "", "",
													"", repeater, alarmFamily);
											break;
										case 45:
											electricDevType=7;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ100Entity.getAlarmData(), "", "",
													"", repeater, alarmFamily);
											break;
										case 46:
											electricDevType=8;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ100Entity.getAlarmData(), "", "",
													"", repeater, alarmFamily);
											break;
										case 47:
											electricDevType=9;
											mAlarmThresholdValueDao.addThresholdValue(electricMac, mBQ100Entity.getAlarmData(), "", "",
													"", repeater, alarmFamily);
											break;
									}*/
									mElectricTypeInfoDao.addAlarmElectric(list, electricMac, repeater, 2, electricDevType);
									Utils.saveChangehistory("6604",electricMac,0);//@@2018.04.17
									GetSmokeMacByRepeaterDao mbrDao = new GetSmokeMacByRepeaterDaoImpl();
									mbrDao.updateSmokeInfo(electricMac,1);//@@2018.03.27
									break;
								case 2://心跳
									List<String> list6 = mBQ100Entity.getList6();
									List<String> list7 = mBQ100Entity.getList7();
									List<String> list9 = mBQ100Entity.getList9();
									mElectricTypeInfoDao.addElectricInfo(list6, list7, mBQ100Entity.getData8(), list9, 
											electricMac, repeater, 2);
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
									mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeater, alarmData,family);
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
												electricMac, repeater, 2);
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
														repeater, 2, 6);
												break;
											case 45:
												List<String> List7 = bQ100Entity.getList7();
												alarmFamily = Utils.getString(List7);
												mElectricTypeInfoDao.addAlarmElectric(List7, electricMac, 
														repeater, 2, 7);
												break;
											case 46:
												String str8 = bQ100Entity.getData8();
												List<String> List8 = new ArrayList<String>();
												List8.add(str8);
												alarmFamily = StringUtils.isBlank(str8)?-1:(int)Float.parseFloat(str8);;
												mElectricTypeInfoDao.addAlarmElectric(List8, electricMac, 
														repeater, 2, 8);
												break;
											case 47:
												List<String> List9 = bQ100Entity.getList9();
												alarmFamily = Utils.getString(List9);
												mElectricTypeInfoDao.addAlarmElectric(List9, electricMac, 
														repeater, 2, 9);
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
											List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(electricMac); //获取useridsmoke表里的用户用于短信通知
											if(users!=null&&users.size()>0){
												Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
												new MyThread(pushAlarm,users,iosMap).start();
												new WebThread(users,electricMac).start();
												if (txtUserList != null &&txtUserList.size()>0) {
													new TxtThread(txtUserList,electricMac).start();        //短信通知的线程
												}
											}
											mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
											mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeater, alarmType,alarmFamily);
											Utils.saveChangehistory("6604",electricMac,0);//@@2018.04.17
											GetSmokeMacByRepeaterDao mbrDao = new GetSmokeMacByRepeaterDaoImpl();
											mbrDao.updateSmokeInfo(electricMac,1);//@@2018.03.27
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
										mFromRepeaterAlarmDao.addAlarmMsg(electricMac, repeater, alarmData,family);
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
												"", "", repeater, 46);
										mAlarmThresholdValueDao.addThresholdValue(electricMac, oc, "", 
												"", "", repeater, 45);
										mAlarmThresholdValueDao.addThresholdValue(electricMac, op, "", 
												"", "", repeater, 43);
										mAlarmThresholdValueDao.addThresholdValue(electricMac, up, "", 
												"", "", repeater, 44);
										mAlarmThresholdValueDao.addThresholdValue(electricMac, tem.get(0), tem.get(1), 
												tem.get(2), tem.get(3), repeater, 47);
										break;
									default:
										break;
								}
								break;//typeBQ100TwoPointZero结束
						}
						break; //电气结束
					}
					break;//透传结束
				case 6:
					System.out.println("send synchronousFire");
					String repeaterMac = repeater;
					if(repeaterMac!=null&&repeaterMac.length()>0){
						mGetSmokeMacByRepeaterDao = new GetSmokeMacByRepeaterDaoImpl();
						List<String> listStr = mGetSmokeMacByRepeaterDao.getSmokeMacByRepeater(repeaterMac);
						int count=listStr.size()*4+2;
						//同步烟感与区域管理终端,如果fireMacList.size()>0,count=fireMacList.size()*4+2,如果fireMacList.size()=0，count=0
						ack = ClientPackage.synchronousFire(mRePeaterData,listStr,count); 
						Timer timer = new Timer();
			            timer.schedule(new LzstoneTimeTask(ack,session),5000,1*5000); 
						mTimerMap = TimerMap.newInstance();
						Timer oldTimer=mTimerMap.getTimer(repeaterMac);
						if(oldTimer!=null){
							oldTimer.cancel();
						}
						mTimerMap.addTimer(repeaterMac, timer);
//						========>>>>在线列表<<<=========
						List<String> listStrOnline = mGetSmokeMacByRepeaterDao.getSmokeOnLineMacByRepeater(repeaterMac);
						int num=listStrOnline.size()*4+2;
						ack2 = ClientPackage.fireSynchronous(mRePeaterData,listStrOnline,num); 
						Timer timer2 = new Timer();
			            timer2.schedule(new LzstoneTimeTask(ack2,session),5000,1*5000); 
			            mTimerMapReSet = TimerMapReSet.newInstance();
						Timer oldTimerReSet=mTimerMapReSet.getTimer(repeaterMac);
						if(oldTimerReSet!=null){
							oldTimerReSet.cancel();
						}
						mTimerMapReSet.addTimer(repeaterMac, timer2);
					}
					break;
				case 12://状态包
					int seqh = byetData.length;
					int eleState12 = byetData[seqh-4]&0xff;
					if(eleState12 >3){
						eleState12 = byetData[seqh-5]&0xff;
					}
					log.debug("electrcinfo eleState12:"+eleState12+" electricMac:"+mRePeaterData.getElectricMac());
					GetSmokeMacByRepeaterDao mbrDao12 = new GetSmokeMacByRepeaterDaoImpl();
					mbrDao12.updateSmokeInfo(mRePeaterData.getElectricMac(), eleState12);
					break;
				case 209://@@三相电表分合闸回复
//					GetSmokeMacByRepeaterDao mbrDao19 = new GetSmokeMacByRepeaterDaoImpl();
//					ElectricHistoryEntity ehe9 = new ElectricHistoryEntity();
//					String repeater19 = repeater;
					String eleMac9 = mRePeaterData.getElectricMac();
//					String userId9 = UserMap.newInstance().getUser(repeater19);
//					ehe9.setUserId(userId9);
//					ehe9.setMac(mRePeaterData.getElectricMac());
					AckEleMap = Utils.AckEleMap;
					int state9 = AckEleMap.get(eleMac9);
//					
//					mbrDao19.updateSmokeInfo(mRePeaterData.getElectricMac(), state9);
					state9++;//@@12.22 为什么要+呢，详情查看AckControlAction
					AckEleMap.put(eleMac9, state9);
//					log.debug("bingo_209:"+AckEleMap.get(eleMac9));
//					ehe9.setState(state9);
//					mbrDao19.insert_Electric_change_history(ehe9);
					
					break;
				case 11:
					int eleState11 = byetData[15]&0xff;
					GetSmokeMacByRepeaterDao mbrDao11 = new GetSmokeMacByRepeaterDaoImpl();
					ElectricHistoryEntity ehe = new ElectricHistoryEntity();
					String repeater11 = repeater;
					String eleMac = mRePeaterData.getElectricMac();
					String userId = UserMap.newInstance().getUser(repeater11);
					ehe.setUserId(userId);
					ehe.setMac(mRePeaterData.getElectricMac());
					AckEleMap = Utils.AckEleMap;
					int state = AckEleMap.get(eleMac);
					if(eleState11 == 1){
//						mbrDao11.updateSmokeInfo(mRePeaterData.getElectricMac(), state);
						state++;//@@12.22 为什么要+呢，详情查看AckControlAction
						AckEleMap.put(eleMac, state);
						ehe.setState(state);
						mbrDao11.insert_Electric_change_history(ehe);
					}
					if(eleState11 == 0){
						AckEleMap.put(eleMac, 127);
					}
					log.debug("electrcinfo eleState11:"+eleState11+" electricMac:"+mRePeaterData.getElectricMac()+"  state:"+state);
					break;
				case 13:
					ack = ClientPackage.ackToEvnClient(mRePeaterData,(byte)0x0E);
					break;
				case 21:	//暂不做逻辑处理了。业务逻辑在前面已经处理过了。
					break;
				case 22:	//电弧数据类型 0心跳  1电弧报警 2 485故障  3电弧探测器故障  4消音开  5消音关
					int arcValue = mRePeaterData.getDeviceType();
					String arcMac = mRePeaterData.getAlarmSmokeMac();
					PublicUtils utilsDao= new PublicUtilsImpl();
					utilsDao.updateDeviceMac(arcMac);
					int alarmType = 0;
					int alarmFamily = arcValue;
					GetSmokeMacByRepeaterDao mbrDao22 = new GetSmokeMacByRepeaterDaoImpl();
					switch(arcValue){
						case 0:	//电弧数据类型 0心跳
							break;
						case 1:	//电弧报警
							alarmType = 53;
							break;
						case 2:	//485故障
							alarmType = 36;
							break;
						case 3:	//电弧探测器故障
							alarmType = 54;
							break;
						case 4:	//消音开
							mbrDao22.chanageElectric(mRePeaterData.getElectricMac(), 1);
							break;
						case 5:	//消音关
							mbrDao22.chanageElectric(mRePeaterData.getElectricMac(), 2);
							break;
					}
					if(alarmType!=0){
						log.debug("进入报警状态jinrubaojingzhuangtai");
						mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
						PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(arcMac, alarmType);
						if(pushAlarm==null){
							break;
						}
						pushAlarm.setAlarmFamily(alarmFamily+"");
						pushAlarm.setAlarmType(alarmType+"");
						mGetPushUserIdDao = new GetPushUserIdDaoImpl();
						List<String> users = mGetPushUserIdDao.getAllUser(arcMac);
						List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(arcMac); //获取useridsmoke表里的用户用于短信通知
						if(users!=null&&users.size()>0){
							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
							new MyThread(pushAlarm,users,iosMap).start();
							new WebThread(users,arcMac).start();
							if (txtUserList != null &&txtUserList.size()>0) {
								new TxtThread(txtUserList,arcMac).start();        //短信通知的线程
							}
						}
						mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
						mFromRepeaterAlarmDao.addAlarmMsg(arcMac, arcMac, alarmType,alarmFamily);
					}
					break;
				case 19:
					log.debug("进入设置阈值回复");
					Map<String,Integer> controlDev = Utils.controlDev;
					int controlvalue = byetData[15]&0xff;
					String smokeMac = mRePeaterData.getAlarmSmokeMac();
					log.debug("controlvalue========="+controlvalue+"         smokeMac======"+smokeMac);
					controlDev.put(smokeMac, controlvalue);
					break;
				case 160://rssi值
					RssiEntityQuery rssientity = mRePeaterData.getRssientity();
					log.debug(rssientity+" = rssientity");
					if(rssientity!=null){
//						RssiValueDao rssidao = new RssiValueDaoImpl();
//						rssidao.saveRssiValue(rssientity);
						utilsDao = new PublicUtilsImpl();
						utilsDao.updateDeviceMac(rssientity.getDeviceMac(),rssientity.getRssivalue());
					}
					break;
				case 153:  //153是十进制,99是它的十六进制  取消下发离线列表的定时器。
					Timer mTimer = TimerMap.newInstance().getTimer(repeater);
					if(mTimer!=null){
						mTimer.cancel();
						mTimer=null;
					}
					break;
				case 96:  //60取消下发在线列表列表的定时器。
					Timer mTimerLine = TimerMapReSet.newInstance().getTimer(repeater);
					if(mTimerLine!=null){
						mTimerLine.cancel();
						mTimerLine=null;
					}
					break;
				default:
					break;
				}
			}
			break;
		case 15:
//			log.debug("PC与服务器的通信");
			if(byteLen==44){
				SocketToPCEntity stpe = ClientPackage.ackPackage(byetData,byteLen);
				if(stpe!=null){
					byetData[2] = (byte) 0x99;
					ack = byetData;
					sessionMap.addSession(stpe.getPcMac(),session);
					PcUserMap.newInstance().addUserMac(stpe.getUserId(), stpe.getPcMac());
				}
			}
			break;
		default:
			break;
		}
		if(ack!=null){
			IoBuffer buf = IoBuffer.wrap(ack);
	        WriteFuture future = session.write(buf);  //将ack回应给中继
	        future.awaitUninterruptibly(100);
	        if( future.isWritten() ){
	        	log.debug("---------send sucess!---------");
	        }else{
	        	log.debug("---------send failed!---------");
	        }
		} 
		if(ack2!=null){
			IoBuffer buf2 = IoBuffer.wrap(ack2);
	        WriteFuture future2 = session.write(buf2);  //将ack回应给中继
	        future2.awaitUninterruptibly(100);
	        if( future2.isWritten() ){
	        	log.debug("---------send sucess222!---------");
	        }else{
	        	log.debug("---------send failed222!---------");
	        }
		}  
		
	}

	//@@推送备电报警到手机
	private void pushRepeaterAlarm(RePeaterData mRePeaterData) {
		List<String> userList = mGetPushUserIdDao.getAllRepeatUser(mRePeaterData.getRepeatMac());
		if(userList!=null&&userList.size()>0){
			Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
			PushAlarmMsgEntity push =new PushAlarmMsgEntity();
			String addTime = GetTime.ConvertTimeByLong();
			push.setAlarmType("2");
			push.setDeviceType(111);
			push.setMac(mRePeaterData.getRepeatMac());
			push.setName(mRePeaterData.getRepeatMac());
			push.setAlarmTime(addTime);
			new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
		}
	}
	
}
