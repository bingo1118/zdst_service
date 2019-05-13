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
import org.json.JSONObject;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.MeterInfoDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.THDeviceInfoDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.AlarmThresholdValueDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.ElectricTypeInfoDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.MeterInfoDaoImp;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.THDeviceInfoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.entity.AlarmPushOnlyEntity;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.MqttElectricEntity;
import com.cloudfire.entity.MqttGasEntity;
import com.cloudfire.entity.MqttSmokeEntity;
import com.cloudfire.entity.MqttWaterEntity;
import com.cloudfire.entity.Nb_SmokeAlarmEntity;
import com.cloudfire.entity.PcUserMap;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.SocketToPCEntity;
import com.cloudfire.entity.ThreePhaseElectricEntity;
import com.cloudfire.entity.Water;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IfStopAlarm;
import com.cloudfire.until.Utils;

public class MyIoHandlerNB_IOT extends IoHandlerAdapter {
	private final static Log log = LogFactory.getLog(MyIoHandlerNB_IOT.class);
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private String deviceMac;
	private SessionMap sessionMap;
	private PublicUtils utilsDao;
	private MeterInfoDao meterInfoDao;

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
		 sessionMap = SessionMap.newInstance();
//		 mRepeaterMap = RepeaterMap.newInstance();
	}
	

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// TODO Auto-generated method stub
		IoBuffer buffer = (IoBuffer) message;
		int byteLen = buffer.limit();
		byte[] data = new byte[byteLen];
		buffer.get(data);

		byte[] byetData = ClientPackage.filerByte(data, byteLen);
		if (byetData == null) {
			return;
		}
		byteLen = byetData.length;
		int cmd1 = byetData[1] & 0xff; // 1E��10����:30 ר��NB-IOT
		byte[] ack = null;
			switch (cmd1) {
			case 30:
				RePeaterData mRePeaterData = ClientPackage.NB_IOTHeart(byetData,byteLen);
				if (mRePeaterData != null) {
					String devMac = mRePeaterData.getRepeatMac();
					RePeaterDataDao rDao = new RePeaterDataDaoImpl();
					WaterInfoDao mWaterInfoDao = null;
					AreaDao ad = null;
					Water water = null;
					AlarmPushOnlyEntity pushEntity = null;
					MqttWaterEntity mwe = null;
					rDao.addRepeaterTime(mRePeaterData.getAlarmSmokeMac(),Constant.outerIp);
					sessionMap.addSession(devMac,session);
					int cmd2 = byetData[2] & 0xff;
					switch (cmd2) {
					case 1: // �豸-->��������
	//					ack = ClientPackage.ackToNB_IOTClient(mRePeaterData,(byte) 0x99);
						deviceMac = mRePeaterData.getAlarmSmokeMac();
						utilsDao = new PublicUtilsImpl();
						utilsDao.updateDeviceMac(deviceMac);
						
						break;
					case 2: // �豸-->���ͱ�����Ϣ
						ack = ClientPackage.ackToNB_IOTClient(mRePeaterData,(byte)0x03);
						pushAlarm(mRePeaterData,byetData);
						break;
					case 38://ˮѹˮλ��ֵ����
//						207-��ˮλ��208-��ˮλ ��209-��ˮѹ ��218-��ˮѹ��307-����
//						308-���¡�407-��ʪ�ȡ�408-��ʪ�ȡ�406-�ɼ�ʱ�䡢405-�ϱ�ʱ��
						ack = ClientPackage.ackToNB_IOTClient(mRePeaterData,(byte)0x23,(byte)0xd4);
						water = mRePeaterData.getWater();
						THDeviceInfoDao mThDeviceInfoDao=new THDeviceInfoImpl();
						mThDeviceInfoDao.updateWaterAckEntity(water.getWaterMac(), "405", water.getHeartTime());
						mThDeviceInfoDao.updateWaterAckEntity(water.getWaterMac(), "406", water.getWaterTime());
						if(water.getDeviceType()==1){//ˮѹ
							mThDeviceInfoDao.updateWaterAckEntity(water.getWaterMac(), "218", water.getH_value()+"");
							mThDeviceInfoDao.updateWaterAckEntity(water.getWaterMac(), "209", water.getL_value()+"");
						}else if(water.getDeviceType() == 2){ //ˮλ
							mThDeviceInfoDao.updateWaterAckEntity(water.getWaterMac(), "208", water.getH_value()+"");
							mThDeviceInfoDao.updateWaterAckEntity(water.getWaterMac(), "207", water.getL_value()+"");
						}
						break;
					case 34:	//ˮѹˮλ��������
						ack = ClientPackage.ackToNB_IOTClient(mRePeaterData,(byte)0x23,(byte)0xd3);
						water = mRePeaterData.getWater();
						mWaterInfoDao = new WaterInfoDaoImpl();
						mWaterInfoDao.addWaterInfo(water.getWaterMac(), water.getWaterMac(), water.getAlarmType(), water.getValue());
						
						pushEntity = new AlarmPushOnlyEntity();
						pushEntity.setSmokeMac(water.getWaterMac());
						pushEntity.setRepeaterMac(water.getWaterMac());
						pushEntity.setAlarmType(water.getAlarmType());
						pushEntity.setAlarmFamily(water.getValue());
						Utils.pushAlarmInfo(pushEntity);
						utilsDao = new PublicUtilsImpl();
						utilsDao.updateDeviceMac(water.getWaterMac());
						break;
					case 33:	//ˮѹˮλ����
						ack = ClientPackage.ackToNB_IOTClient(mRePeaterData,(byte)0x23,(byte)0xdb);
						water = mRePeaterData.getWater();
						mWaterInfoDao = new WaterInfoDaoImpl();
						mWaterInfoDao.addWaterInfo(water.getWaterMac(), water.getWaterMac(), 1, water.getValue());
						utilsDao = new PublicUtilsImpl();
						utilsDao.updateDeviceMac(water.getWaterMac());
						break;
					case 4: // �豸-->����͸��
	//					ack = ClientPackage.ackToClient(mRePeaterData, (byte) 0x07);
						int deviceId = mRePeaterData.getDeviceType();
						switch(deviceId){
							case 88:
								meterInfoDao = new MeterInfoDaoImp();
								if (meterInfoDao.updateMeterInfo(mRePeaterData.getRepeatMac(), 
										mRePeaterData.getElectricMeter().getQuantity(),
										mRePeaterData.getElectricMeter().getTime(),
										mRePeaterData.getElectricMeter().getVoltage(),
										mRePeaterData.getElectricMeter().getElectricity(), 
										mRePeaterData.getElectricMeter().getPower()));
								break;
							case 72://RB����ȼ��
								meterInfoDao = new MeterInfoDaoImp();
								utilsDao = new PublicUtilsImpl();
								ProofGasEntity gasEntity = mRePeaterData.getGasEntity();
								if (gasEntity!=null){
									meterInfoDao.insertProofGas(gasEntity);
									gasEntity.setProofGasType("��ȼ����");
									switch (gasEntity.getProofGasState()) {
									case "7":
									case "8":
									case "4":
									case "5":
									case "10":
										pushAlarm(mRePeaterData, byetData);
										break;
									default:
										utilsDao.updateDeviceMac(gasEntity.getProofGasMac());
										break;
									}
								}
								break; 
						}
						break;
					case 31:	//����NB������ֵ
					case 30:	//����NB����͸��
						ThreePhaseElectricEntity threeElectric = new ThreePhaseElectricEntity();
						threeElectric = mRePeaterData.getThreeElectric();
						if(threeElectric!=null){
							String imeiVal = threeElectric.getImeiStr();
							PublicUtils putil = new PublicUtilsImpl();
							ElectricTypeInfoDao edao = new ElectricTypeInfoDaoImpl();
							putil.updateDeviceMac(imeiVal,threeElectric.getImsiStr());
							if(cmd2==31){	//	������ֵ����
								ElectricThresholdBean etb = threeElectric.getElectricBean();
								if(etb!=null){
									String lc = etb.getLeakCurrent();
									String oc = etb.getOverCurrent();
									String op = etb.getOverpressure();
									String up = etb.getUnpressure();
									AlarmThresholdValueDao mAlarmThresholdValueDao = new AlarmThresholdValueDaoImpl();
									mAlarmThresholdValueDao.addThresholdValue(threeElectric.getImeiStr(), lc, "", 
											"", "", mRePeaterData.getRepeatMac(), 46);
									mAlarmThresholdValueDao.addThresholdValue(threeElectric.getImeiStr(), oc, "", 
											"", "", mRePeaterData.getRepeatMac(), 45);
									mAlarmThresholdValueDao.addThresholdValue(threeElectric.getImeiStr(), op, "", 
											"", "", mRePeaterData.getRepeatMac(), 43);
									mAlarmThresholdValueDao.addThresholdValue(threeElectric.getImeiStr(), up, "", 
											"", "", mRePeaterData.getRepeatMac(), 44);
								}
							}else{		//����͸������
								edao.addThreePhaseElectricInfo(threeElectric);
								if(threeElectric.getRunAlarmState().equals("1")||threeElectric.getRunAlarmState()=="1"){	//1����״̬
									pushEntity = new AlarmPushOnlyEntity();
									pushEntity.setSmokeMac(threeElectric.getImeiStr());
									pushEntity.setRepeaterMac(threeElectric.getImeiStr());
									pushEntity.setAlarmType(Integer.parseInt(threeElectric.getRunGateState()));
									pushEntity.setAlarmFamily(threeElectric.getRunGateState());
									Utils.pushAlarmInfo(pushEntity);
								}
							}
						}
						break;
					default:
						break;
					}
				}
				break;
			case 15:
				log.debug("PC���������ͨ��");
				if (byteLen == 44) {
					SocketToPCEntity stpe = ClientPackage.ackPackage(byetData,
							byteLen);
					log.debug("�ж�stpe�Ƿ�Ϊ��?");
					if (stpe != null) {
						byetData[2] = (byte) 0x99;
						ack = byetData;
						sessionMap.addSession(stpe.getPcMac(), session);
						PcUserMap.newInstance().addUserMac(stpe.getUserId(),
								stpe.getPcMac());
						log.debug("�Ƿ���ӵ�sessionMap��PcUserMap?stpe.getUserId():"
								+ stpe.getUserId() + ",stpe.getPcMac():"
								+ stpe.getPcMac());
					}
				}
				break;
			default:
				break;
			}
	
			if (ack != null) {
				IoBuffer buf = IoBuffer.wrap(ack);
				WriteFuture future = session.write(buf);
				future.awaitUninterruptibly(100);
				if (future.isWritten()) {
					log.debug("---------send sucess!---------");
				} else {
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
		int macState = byetData[byetData.length-4]&0xff;
		log.debug("macState======="+macState);
		AckEleMap.put(mac, macState);
		utilsDao = new PublicUtilsImpl();
		AreaDao areadao = new AreaDaoImpl();
		utilsDao.updateDeviceMac(mac,macState);
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mFromRepeaterAlarmDao = new FromRepeaterAlarmDaoImpl();
		int deviceType = mFromRepeaterAlarmDao.getDeviceType(mac);
		int deviceAreaId = areadao.getAreaIdBySmokeMac(mac);
		int alarmType2 = byetData[11] & 0xff;
		if ((byetData[11] & 0xff) == 202) {
			switch (deviceType) {
			case 1: // �̸�
				alarmType2 = 202;
				break;
			case 2: // ȼ��
				alarmType2 = 2;
				break;
			case 7: // ����
				alarmType2 = 7;
				break;
			case 8: // �ֱ�
				alarmType2 = 204;
				break;
			case 11: // ������
				alarmType2 = 206;
				break;
			case 12: // �Ŵ�
				alarmType2 = 205;
				break;
			case 15: // ˮ��
				alarmType2 = 221;
				break;
			case 16: // NBȼ��
			case 72://NB����ȼ��
				alarmType2 = 2;
				break;
			}
		}else if(alarmType2==203){//����
			alarmType2 = 14;
		}else if((byetData[11] & 0xff) == 1){//����ȼ��
			alarmType2 = 2;
		}
		PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac, alarmType2);

		if (deviceType != 5) {
			mFromRepeaterAlarmDao.addAlarmMsg(mac,mRePeaterData.getRepeatMac(), alarmType2, 1);
			mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			List<String> userList = mGetPushUserIdDao.getAllUser(mac);
			List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(mac); //��ȡuseridsmoke������û����ڶ���֪ͨ

			if (userList != null && userList.size() > 0) {
				Map<String, String> iosMap = mGetPushUserIdDao.getIosUser(userList);
				if(IfStopAlarm.lastAlarmTime.containsKey(mac)){  //ifStopAlarm.map ֹͣ�����豸��
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
					new TxtThread(txtUserList,mac).start();        //����֪ͨ���߳�
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
										userSetLinked.remove(user);//@@��ֹ�ظ�����
									}
								}
								List<String> userListLinked=new ArrayList<String>(userSetLinked);
								log.debug("bingo0125:userListLinked:"+userListLinked.toString());
								Map<String,String> iosMap2 = mGetPushUserIdDao.getIosUser(userListLinked); //��ȡios�û��б�
								new MyThread(push2,userListLinked,iosMap2).start();  //����������Ϣ���߳�
							}
						}
					}
				}
			}
		}
		//��������---NB������������������ܣ���ͬһ��Ⱥ�ڵ�2����λ�������̸�ͬʱ�澯ʱ��ƽ̨ȷ��Ϊ��
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
				Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //��ȡios�û��б�
				
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
