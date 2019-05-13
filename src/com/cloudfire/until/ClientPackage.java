package com.cloudfire.until;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.THDeviceInfoDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.AllSmokeDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.DevicesDaoImpl;
import com.cloudfire.dao.impl.GetSmokeMacByRepeaterDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.dao.impl.THDeviceInfoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.BQ100Entity;
import com.cloudfire.entity.BQ200Entity;
import com.cloudfire.entity.ChJEntity;
import com.cloudfire.entity.DeviceAlarmEntity;
import com.cloudfire.entity.DtuData;
import com.cloudfire.entity.DtuDataGroup;
import com.cloudfire.entity.ElectricEnergyEntity;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.EnvironmentEntity;
import com.cloudfire.entity.GpsEntityBean;
import com.cloudfire.entity.NanJing_NB_IOT_Entity;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.RePeaterUoolEntity;
import com.cloudfire.entity.RssiEntityQuery;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SocketToPCEntity;
import com.cloudfire.entity.THDevice;
import com.cloudfire.entity.ThreePhaseElectricEntity;
import com.cloudfire.entity.ThreePhaseMeterEntity;
import com.cloudfire.entity.Water;
import com.cloudfire.entity.WaterAckEntity;
import com.cloudfire.entity.meter.MeterInfoEntity;
import com.cloudfire.entity.meter.MeterReadingEntity;

public class ClientPackage {
	private final static Log log = LogFactory.getLog(ClientPackage.class);
	
	public static RePeaterData isHeart(byte[] dataN,int byteLen,int versions) throws Exception {
		byte[] data = null;
		if(versions==4){
			data = Utils.formatByte(dataN,versions);
		}else{
			data = Utils.formatByte(dataN);
		}
		int byteCount =data.length;
		System.out.println(IntegerTo16.bytes2Hex(data));
		System.out.println(IntegerTo16.bytes2Hex(dataN));
    	boolean crcRight = checkCrc(data,byteCount);  //crc校验
    	if(crcRight){
    		RePeaterData mRePeaterData = new RePeaterData();
    		mRePeaterData.setSeqL(data[3]);
    		mRePeaterData.setSeqH(data[4]);
    		byte[] macByte = new byte[4];
    		for(int i=0;i<4;i++){
    			macByte[i] = data[7+i];
    		}
    		String repeaterMac = IntegerTo16.bytes2Hex(macByte);
    		mRePeaterData.setRepeatMac(repeaterMac);
    		byte[] dataLen = new byte[2];
    		dataLen[1] = data[6];
    		dataLen[0] = data[5];
    		int len = IntegerTo16.bytesToInt(dataLen);
    		int msg = data[2]&0xff;
    		log.debug("msg================================"+msg);
        	switch (msg) {
    		case 1:   //中继心跳
    			mRePeaterData.setCmd2((byte)0x01);
    			Map<String,Long> macList = new HashMap<String,Long>();
    			if(len>5){
    				dataLen[0] = data[11];
    				dataLen[1] = data[12];
    				int macNum = IntegerTo16.bytesToInt(dataLen);
//    				log.debug("len=========="+len+"   macNum="+macNum+" byteCount:"+byteCount );
    				if(len==(macNum*4+7)){
    					int repeaterState = data[byteCount-4]&0xff;
    					mRePeaterData.setRepeaterState(repeaterState);
    				}
    				long heartTime = System.currentTimeMillis();
//    				synchronized (macList) {
    					for(int i=0;i<macNum;i++){
        					byte[] mac = new byte[4];
        					for(int j=0;j<4;j++){
        						mac[j] = data[13+i*4+j];
        					}
        					String macStr = IntegerTo16.bytes2Hex(mac);
        					DeviceDao deviceDao = new DevicesDaoImpl();
        					int deviceTypeDao = deviceDao.getDeviceTypeByMac(macStr);
        					if(macStr.equals(repeaterMac)||macStr==repeaterMac||deviceTypeDao==5||StringUtils.isBlank(repeaterMac)){  //若mac等于中继的mac或者设备属于电气火灾探测器，不放入macList(离线表)
//        						log.debug("222222222deviceTypeDao==="+deviceTypeDao+"  macStr:"+macStr);
        						macList.remove(macStr);
        					}else{
//        						log.debug("33333333333deviceTypeDao==="+deviceTypeDao+"  macStr:"+macStr);
        						macList.put(macStr, heartTime);   //
        					}
//        					macList.put(macStr, heartTime);
        				}
//					}
    			}else if(len==5){ //数据包长度为5，表明包是说明中继的电源状态的，默认为0，1表示主电源、2表示备电源，3表示主备电源
    				int repeaterState = data[11]&0xff;
					mRePeaterData.setRepeaterState(repeaterState);
					log.debug("repeaterState2 = "+repeaterState);
    			}
    			
    			mRePeaterData.setFireMacList(macList);
    			break;
    		case 2:  //报警数据包
    			mRePeaterData.setCmd2((byte)0x02);	//报警
    			byte[] alarmMac = new byte[4];
    			for(int i=0;i<4;i++){
    				alarmMac[i] = data[11+i];
    			}
    			String alarmSmokeMac = IntegerTo16.bytes2Hex(alarmMac);
    			mRePeaterData.setAlarmSmokeMac(alarmSmokeMac);
    			mRePeaterData.setCmd(data[15]);  //如果是烟感:data[15]:ca(转为10进制是202)
    			break;
    		case 8:  //报警数据包
    			mRePeaterData.setCmd2((byte)0x08);	
    			byte[] alarmMac7 = new byte[4];
    			for(int i=0;i<4;i++){
    				alarmMac7[i] = data[11+i];
    			}
    			String alarmSmokeMac7 = IntegerTo16.bytes2Hex(alarmMac7);
    			mRePeaterData.setAlarmSmokeMac(alarmSmokeMac7);
    			mRePeaterData.setCmd(data[15]);
    			break;
    		case 19:  //设置阈值回复
    			mRePeaterData.setCmd2((byte)0x16);	
    			byte[] alarmMac19 = new byte[4];
    			for(int i=0;i<4;i++){
    				alarmMac19[i] = data[11+i];
    			}
    			String alarmSmokeMac19 = IntegerTo16.bytes2Hex(alarmMac19);
    			mRePeaterData.setAlarmSmokeMac(alarmSmokeMac19);
    			break;
    		case 21:  //根据设置水压波动阈值成功后需要处理的数据
    			mRePeaterData.setCmd2((byte)0x15);	
    			byte[] alarmMac21 = new byte[4];
    			for(int i=0;i<4;i++){
    				alarmMac21[i] = data[11+i];
    			}
    			String alarmSmokeMac21 = IntegerTo16.bytes2Hex(alarmMac21);
    			mRePeaterData.setAlarmSmokeMac(alarmSmokeMac21);
    			int ifSuccess=data[15]&0xff;//判断是否成功。0代表成功，否则失败。
    			if(ifSuccess==0){
    				Map<String,WaterAckEntity> clist = Utils.objWater;
    				WaterAckEntity wa = new WaterAckEntity();
        			wa.setWaterMac(alarmSmokeMac21);
        			wa.setWaveValue(0);
        			wa.setAckTimes(0);
        			clist.put(alarmSmokeMac21, wa);
    			}
    			break;
    		case 22:  //NB通信电弧数据
    			mRePeaterData.setCmd2((byte)0x16);	
    			byte[] alarmMac22 = new byte[4];
    			for(int i=0;i<4;i++){
    				alarmMac22[i] = data[11+i];
    			}
    			String alarmSmokeMac22 = IntegerTo16.bytes2Hex(alarmMac22);
    			mRePeaterData.setAlarmSmokeMac(alarmSmokeMac22);
    			int arc=data[15]&0xff;//电弧数据类型 0心跳  1电弧报警 2 485故障  3电弧探测器故障  4消音开  5消音关
    			mRePeaterData.setDeviceType(arc);
    			break;
    		case 37:  //0x25   优特电气下发控制回复
    		case 39:	//0x27  优特电气下发控制回复
    			mRePeaterData.setCmd2((byte)0x25);	
    			byte[] alarmMac37 = new byte[4];
    			for(int i=0;i<4;i++){
    				alarmMac37[i] = data[11+i];
    			}
    			String alarmMac37Str = IntegerTo16.bytes2Hex(alarmMac37);
    			mRePeaterData.setAlarmSmokeMac(alarmMac37Str);
    			break;
    		case 96:  //开机同步列表回复
    			mRePeaterData.setCmd2((byte)0x60);	
    			break;
    		case 210://设备 主机升级开始回复 0x7e 0x0e 0xd2 递增  1byte 1byte 中继器mac(4)软件版本号（4）（版本号要包括设备类型） 1byte 1byte 0x7f	
    			byte[] eleRepeaterMac210 = new byte[4];
    			for(int i=0;i<4;i++){
    				eleRepeaterMac210[i] = data[7+i];
    			}
    			String RepeaterMac210 = IntegerTo16.bytes2Hex(eleRepeaterMac210);
    			int levelNum = Utils.hostVersion.get(RepeaterMac210);
    			int data14 = data[14]&0xff;
    			System.out.println("jion d2 is :  ============   "+levelNum+" data[14] value is :" + data[14]+" data15:"+data14);
    			if(data14==levelNum){
    				Utils.hostLevenUp.put(RepeaterMac210, 1);
    				System.out.println("jion d2 is :   data[14] ============    "+data[14]);
    			}
    			mRePeaterData.setCmd2((byte)0xD2);
    			break;
    		case 211://d3设备 主机升级数据回复 0x7e 0x0e 0xd3 递增 1byte 1byte 中继器mac(4)数据包num（2）数据偏移（2）数据长度（2）固件接收状态（1）（0失败 1成功） 1byte 1byte 0x7f
    			byte[] eleRepeaterMac211 = new byte[4];
    			for(int i=0;i<4;i++){
    				eleRepeaterMac211[i] = data[7+i];
    			}
    			byte[] dataLe = new byte[2];
        		dataLe[1] = data[11];
        		dataLe[0] = data[12];
        		int le = IntegerTo16.bytesToInt(dataLe);
    			String RepeaterMac211 = IntegerTo16.bytes2Hex(eleRepeaterMac211);
    			int stateSucess = data[data.length-4]&0xff;
    			if(stateSucess==1){//成功则改变值
    				Utils.hostLevenUp.put(RepeaterMac211, le);
    			}
    			mRePeaterData.setCmd2((byte)0xD3);
    			break;
    		case 212://d4设备 主机升级完成数据回复 0x7e 0x0e 0xd3 递增 1byte 1byte 中继器mac(4)数据包num（2）数据偏移（2）数据长度（2）固件接收状态（1）（0失败 1成功） 1byte 1byte 0x7f
    			byte[] eleRepeaterMac212 = new byte[4];
    			for(int i=0;i<4;i++){
    				eleRepeaterMac212[i] = data[7+i];
    			}
    			String RepeaterMac212 = IntegerTo16.bytes2Hex(eleRepeaterMac212);
    			int statSucess = data[data.length-4]&0xff;
    			if(statSucess==1){//成功则改变值
    				Utils.hostLevenUp.put(RepeaterMac212, 1);
    			}
    			System.out.println("join d4设备 主机升级完成数据:"+IntegerTo16.bytes2Hex(data)+"  Utils.hostLevenUp.put:"+Utils.hostLevenUp.get(RepeaterMac212)+" :"+RepeaterMac212);
    			mRePeaterData.setCmd2((byte)0xD4);
    			break;
    		case 160://RSSI值
    			log.debug("rssivalue:160");
    			mRePeaterData.setCmd2((byte)0xA0);	
    			byte[] devMac16 = new byte[4];
    			for(int i=0;i<4;i++){
    				devMac16[i] = data[11+i];
    			}
    			RssiEntityQuery rssientity = new RssiEntityQuery();
    			String devMac16s = IntegerTo16.bytes2Hex(devMac16);
    			rssientity.setDeviceMac(devMac16s);
    			int rssivalue = 0;
//    			if((data[15]&0x80)==128){
    				rssivalue = Utils.getrssi(data[15], data[16]);
    				if(rssivalue<-135){
    					rssivalue=-30;
    				}
//    			}else{
//    				rssivalue = ((data[15]&0xff)<<8)+data[16]&0xff;
//    			}
    			rssientity.setRssivalue(rssivalue+"");
    			log.debug("devMac16s:"+devMac16s+"  rssivalue="+rssivalue);
    			mRePeaterData.setRssientity(rssientity);
    			break;
    		case 4:  //透传数据包
    			mRePeaterData.setCmd2((byte)0x04);
    			int electricDataLen = data[11]&0xff;
    			int transparentType = data[12]&0xff;//透传类型，用于区别电气火灾与三江设备，其中CC(204),表示电气火灾；DD(221)表示三江设备;EE(238)海湾设备;
    			switch (transparentType) {
    			case 238:
    				int b16 = data[16]&0xff;
    				mRePeaterData.setTransmissionType(5);// 1、是电气。2、是三江。3、水压。4、环境探测器。5、海湾。6、喷淋,7温湿度 8水浸
    				switch(b16){
    				case 99:
    					mRePeaterData.setPrinterAction(99);
    					break;
    				case 3:
    					mRePeaterData.setPrinterAction(3);
    					PrinterEntity mPrinterEntity2 = getPrinterInfo2(data);
        				int b13 = data[13]&0xff;
        				mPrinterEntity2.setFaultCode(b13+"");
        				mRePeaterData.setmPrinterEntity(mPrinterEntity2);
        				break;
    				}
    				
    				break;
    			case 221:
    				int b14 = data[15]&0xff;
    				mRePeaterData.setTransmissionType(2);
    				switch (b14) {
					case 50:  //报警数据
						PrinterEntity mPrinterEntity = getPrinterInfo(data);
	    				mRePeaterData.setmPrinterEntity(mPrinterEntity);
	    				mRePeaterData.setPrinterAction(1);
						break;
					case 114:  //复位操作
						mRePeaterData.setPrinterAction(2);
						break;
					case 99:	//连接正常
						mRePeaterData.setPrinterAction(3);
						break;
					case 100: //连接不正常
						mRePeaterData.setPrinterAction(4);
						break;
					}
    				
    				break;
				case 204:
					mRePeaterData.setTransmissionType(1);
					byte[] electricMac = new byte[4];
	    			for(int i=0;i<4;i++){
	    				electricMac[i] = data[13+i];
	    			}
	    			String electricMacStr = IntegerTo16.bytes2Hex(electricMac);
	    			mRePeaterData.setElectricMac(electricMacStr);
	    			RePeaterDataDao redao = new RePeaterDataDaoImpl(); //根据mac查询设备所属类型。
	    			int deviceTypeback = redao.getDeviceType(electricMacStr);
	    			//更新设备心跳
	    			PublicUtils pu = new PublicUtilsImpl();
	    			pu.updateDeviceMac(electricMacStr);
	    			if(deviceTypeback==81){
	    				ThreePhaseElectricEntity threeElec = null;
	    				mRePeaterData.setTransmissionType(10);//优特电气处理
	    				int dataCmd = data[19]&0xff;//数据类型
	    				mRePeaterData.setDeviceType(dataCmd);
	    				switch(dataCmd){
	    				case 0://电气基本数据
	    				case 3://485故障
	    					threeElec = new ThreePhaseElectricEntity();
	    					threeElec.setImeiStr(electricMacStr);
	    					String runAlarmState = "0";//告警状态
	    	    			String runCauseState = "0";//报警类型
	    	    			String runGateState = "1";//闸位状态--U特电气的报警值
	    					
	    					byte[] electrc_value = new byte[2];
	    					electrc_value[1] = data[20];
	    					electrc_value[0] = data[21];
							int voltageA = IntegerTo16.bytesToInt(electrc_value);//电压A
							threeElec.setVoltageA(voltageA+"");
							
							electrc_value = new byte[2];
							electrc_value[1] = data[22];
	    					electrc_value[0] = data[23];
	    					int voltageB = IntegerTo16.bytesToInt(electrc_value);//电压B
	    					threeElec.setVoltageB(voltageB+"");
	    					
	    					electrc_value = new byte[2];
	    					electrc_value[1] = data[24];
	    					electrc_value[0] = data[25];
	    					int voltageC = IntegerTo16.bytesToInt(electrc_value);//电压C
	    					threeElec.setVoltageC(voltageC+"");
	    					
	    					electrc_value = new byte[2];
	    					electrc_value[1] = data[26];
	    					electrc_value[0] = data[27];
	    					int electricA = IntegerTo16.bytesToInt(electrc_value);//电流A
	    					threeElec.setElectricityA(Utils.getEnergyValue(electricA));
	    					
	    					electrc_value = new byte[2];
	    					electrc_value[1] = data[28];
	    					electrc_value[0] = data[29];
	    					int electricB = IntegerTo16.bytesToInt(electrc_value);//电流B
	    					threeElec.setElectricityB(Utils.getEnergyValue(electricB));
	    					
	    					electrc_value = new byte[2];
	    					electrc_value[1] = data[30];
	    					electrc_value[0] = data[31];
	    					int electricC = IntegerTo16.bytesToInt(electrc_value);//电流C
	    					threeElec.setElectricityC(Utils.getEnergyValue(electricC));
	    					
	    					electrc_value = new byte[2];
	    					electrc_value[1] = data[32];
	    					electrc_value[0] = data[33];
	    					int surplusElectri = IntegerTo16.bytesToInt(electrc_value);//漏电流
	    					threeElec.setSurplusElectri(surplusElectri+"");
	    					
	    					electrc_value = new byte[2];
	    					electrc_value[1] = data[34];
	    					electrc_value[0] = data[35];
	    					int Temperature1 = IntegerTo16.bytesToInt(electrc_value);//温度1
	    					threeElec.setTempValueA(((float)Temperature1/10)+"");
	    					
	    					electrc_value = new byte[2];
	    					electrc_value[1] = data[36];
	    					electrc_value[0] = data[37];
	    					int Temperature2 = IntegerTo16.bytesToInt(electrc_value);//温度2
	    					threeElec.setTempValueB(((float)Temperature2/10)+"");
	    					
	    					electrc_value = new byte[2];
	    					electrc_value[1] = data[38];
	    					electrc_value[0] = data[39];
	    					int Temperature3 = IntegerTo16.bytesToInt(electrc_value);//温度3
	    					threeElec.setTempValueC(((float)Temperature3/10)+"");
	    					
	    					electrc_value = new byte[2];
	    					electrc_value[1] = data[40];
	    					electrc_value[0] = data[41];
	    					int Temperature4 = IntegerTo16.bytesToInt(electrc_value);//温度4
	    					threeElec.setTempValueD(((float)Temperature4/10)+"");
	    					
	    					int dataType_three = data[42]&0xff;	//报警状态
	    					switch(dataType_three){
	    					case 0:
								runAlarmState = "0";
								break;
							case 1:	//过压
								runCauseState = "43";
								runGateState = threeElec.getVoltageA();
								runAlarmState = "1";
								break;
							case 2:	//欠压
								runCauseState = "44";
								runGateState = threeElec.getVoltageA();
								runAlarmState = "1";
								break;
							case 3:	//过流
								runCauseState = "45";
								runGateState = threeElec.getElectricityA();
								runAlarmState = "1";
								break;
							case 4:	//漏电流
								runCauseState = "46";
								runGateState = threeElec.getElectricityA();
								runAlarmState = "1";
								break;
							case 5:	//温度
								runCauseState = "47";
								runGateState = threeElec.getTempValueA();
								runAlarmState = "1";
								break;
							case 6:	//传感器开路故障
								runCauseState = "74";
								runGateState = "1";
								runAlarmState = "1";
								break;
							case 7:	//传感器短路
								runCauseState = "74";
								runGateState = "1";
								runAlarmState = "1";
								break;
							case 8:	//错相
								runCauseState = "162";
								runGateState = "1";
								runAlarmState = "1";
								break;
							case 9:	//缺相
								runCauseState = "153";
								runGateState = "1";
								runAlarmState = "1";
								break;
							case 16:
								runCauseState = "36";
								runGateState = "1";
								runAlarmState = "1";
								break;
							default:
								runAlarmState = "0";
								if(dataCmd==3){
									runAlarmState = "1";
								}
								break;
	    					}
	    					threeElec.setRunGateState(runGateState);
	    					threeElec.setRunCauseState(runCauseState);
	    	    			threeElec.setRunAlarmState(runAlarmState);
	    	    			threeElec.setHeartime(GetTime.ConvertTimeByLong());
	    					mRePeaterData.setThreeElectric(threeElec);
	    					break;
	    				case 32://阈值处理
	    					threeElec = new ThreePhaseElectricEntity();
	    					threeElec.setImeiStr(electricMacStr);
	    					ElectricThresholdBean thresholdBean = new ElectricThresholdBean();
	    					byte[] electrc_Threshold = new byte[2];
	    					electrc_Threshold[1] = data[20];
	    					electrc_Threshold[0] = data[21];
							int OverVoltageThreshold = IntegerTo16.bytesToInt(electrc_Threshold);//过压阈值
							thresholdBean.setOverpressure(OverVoltageThreshold+"");
							
							electrc_Threshold = new byte[2];
							electrc_Threshold[1] = data[22];
	    					electrc_Threshold[0] = data[23];
							int UnderVoltageThreshold = IntegerTo16.bytesToInt(electrc_Threshold);//欠压阈值
							thresholdBean.setUnpressure(UnderVoltageThreshold+"");
							
							electrc_Threshold = new byte[2];
							electrc_Threshold[1] = data[24];
	    					electrc_Threshold[0] = data[25];
							int OverCurrentThreshold = IntegerTo16.bytesToInt(electrc_Threshold);//过流阈值
							thresholdBean.setOverCurrent(OverCurrentThreshold+"");
							
							electrc_Threshold = new byte[2];
							electrc_Threshold[1] = data[26];
	    					electrc_Threshold[0] = data[27];
							int ResidualCurrentThreshold = IntegerTo16.bytesToInt(electrc_Threshold);//欠流阈值
							thresholdBean.setLeakCurrent(ResidualCurrentThreshold+"");
							
							electrc_Threshold = new byte[2];
							electrc_Threshold[1] = data[28];
	    					electrc_Threshold[0] = data[29];
							String TemperatureThresnhold = IntegerTo16.bytesToInt(electrc_Threshold)+"";//温度阈值
							List<String> temperatures = new ArrayList<String>();
			    			temperatures.add(TemperatureThresnhold);
			    			temperatures.add(TemperatureThresnhold);
			    			temperatures.add(TemperatureThresnhold);
			    			temperatures.add(TemperatureThresnhold);
			    			thresholdBean.setTemperatures(temperatures);
			    			
			    			threeElec.setElectricBean(thresholdBean);
			    			mRePeaterData.setThreeElectric(threeElec);
	    					break;
	    				case 48://有功电率
	    					ElectricEnergyEntity eee = new ElectricEnergyEntity();
	    					int shunt = data[20]&0xff; //分励关联
	    					eee.setShunt(shunt);
	    					int shuntRelevanceTime = shunt&0x1F;//分励时间：0 - 无输出; 0x1F(31) – 常开; ’1-30’ - 分励保持时
	    					eee.setShuntRelevanceTime(shuntRelevanceTime);
	    					int shuntCuPer = (shunt&0x20)-31;//Bit5: 1 - 电压/电流分励;
	    					eee.setShuntCuPer(shuntCuPer);
	    					int shuntTemp = (shunt&0x40)-63;//Bit6: 1 - 温度/漏电分励;
	    					eee.setShuntTemp(shuntTemp);
	    					int shuntLink = shunt>>7;//Bit7: 1 – 联动分励;
	    					eee.setShuntLink(shuntLink);
	    					
	    					byte[] Energy_Data = new byte[2];
	    					Energy_Data[1] = data[21];
	    					Energy_Data[0] = data[22];
	    					String activePowerA = IntegerTo16.bytesToInt(Energy_Data)+"";//有功功率A相
	    					eee.setActivePowerA(activePowerA);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[23];
	    					Energy_Data[0] = data[24];
	    					String activePowerB = IntegerTo16.bytesToInt(Energy_Data)+"";//有功功率B相
	    					eee.setActivePowerB(activePowerB);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[25];
	    					Energy_Data[0] = data[26];
	    					String activePowerC = IntegerTo16.bytesToInt(Energy_Data)+"";//有功功率C相
	    					eee.setActivePowerC(activePowerC);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[27];
	    					Energy_Data[0] = data[28];
	    					String reactivePowerA = IntegerTo16.bytesToInt(Energy_Data)+"";//无功功率A相	
	    					eee.setReactivePowerA(reactivePowerA);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[29];
	    					Energy_Data[0] = data[30];
	    					String reactivePowerB = IntegerTo16.bytesToInt(Energy_Data)+"";//无功功率B相	
	    					eee.setReactivePowerB(reactivePowerB);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[31];
	    					Energy_Data[0] = data[32];
	    					String reactivePowerC = IntegerTo16.bytesToInt(Energy_Data)+"";//无功功率C相	
	    					eee.setReactivePowerC(reactivePowerC);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[33];
	    					Energy_Data[0] = data[34];
	    					String apparentPowerA = IntegerTo16.bytesToInt(Energy_Data)+"";//视在功率A相		
	    					eee.setApparentPowerA(apparentPowerA);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[35];
	    					Energy_Data[0] = data[36];
	    					String apparentPowerB = IntegerTo16.bytesToInt(Energy_Data)+"";//视在功率B相		
	    					eee.setApparentPowerB(apparentPowerB);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[37];
	    					Energy_Data[0] = data[38];
	    					String apparentPowerC = IntegerTo16.bytesToInt(Energy_Data)+"";//视在功率C相		
	    					eee.setApparentPowerC(apparentPowerC);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[39];
	    					Energy_Data[0] = data[40];
	    					String powerFactorA = IntegerTo16.bytesToInt(Energy_Data)+"";//功率因素A相	
	    					eee.setPowerFactorA(powerFactorA);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[41];
	    					Energy_Data[0] = data[42];
	    					String powerFactorB = IntegerTo16.bytesToInt(Energy_Data)+"";//功率因素B相	
	    					eee.setPowerFactorB(powerFactorB);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[43];
	    					Energy_Data[0] = data[44];
	    					String powerFactorC = IntegerTo16.bytesToInt(Energy_Data)+"";//功率因素C相	
	    					eee.setPowerFactorC(powerFactorC);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[45];
	    					Energy_Data[0] = data[46];
	    					String activeEnergyA = IntegerTo16.bytesToInt(Energy_Data)+"";//有功电能A相
	    					eee.setActiveEnergyA(activeEnergyA);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[47];
	    					Energy_Data[0] = data[48];
	    					String activeEnergyB = IntegerTo16.bytesToInt(Energy_Data)+"";//有功电能B相
	    					eee.setActiveEnergyB(activeEnergyB);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[49];
	    					Energy_Data[0] = data[50];
	    					String activeEnergyC = IntegerTo16.bytesToInt(Energy_Data)+"";//有功电能C相
	    					eee.setActiveEnergyC(activeEnergyC);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[51];
	    					Energy_Data[0] = data[52];
	    					String reactiveEnergyA = IntegerTo16.bytesToInt(Energy_Data)+"";//无功电能A相	
	    					eee.setReactiveEnergyA(reactiveEnergyA);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[53];
	    					Energy_Data[0] = data[54];
	    					String reactiveEnergyB = IntegerTo16.bytesToInt(Energy_Data)+"";//无功电能B相	
	    					eee.setReactiveEnergyB(reactiveEnergyB);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[55];
	    					Energy_Data[0] = data[56];
	    					String reactiveEnergyC = IntegerTo16.bytesToInt(Energy_Data)+"";//无功电能C相	
	    					eee.setReactiveEnergyC(reactiveEnergyC);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[57];
	    					Energy_Data[0] = data[58];
	    					String apparentEnergyA = IntegerTo16.bytesToInt(Energy_Data)+"";//视在电能A相	
	    					eee.setApparentEnergyA(apparentEnergyA);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[59];
	    					Energy_Data[0] = data[60];
	    					String apparentEnergyB = IntegerTo16.bytesToInt(Energy_Data)+"";//视在电能B相	
	    					eee.setApparentEnergyB(apparentEnergyB);
	    					
	    					Energy_Data = new byte[2];
	    					Energy_Data[1] = data[61];
	    					Energy_Data[0] = data[62];
	    					String apparentEnergyC = IntegerTo16.bytesToInt(Energy_Data)+"";//视在电能C相	
	    					eee.setApparentEnergyC(apparentEnergyC);
	    					
	    					String dataTime = GetTime.ConvertTimeByLong();	//	心跳时间
	    					eee.setDataTime(dataTime);
	    					eee.setImei(electricMacStr);
	    					
	    					mRePeaterData.setEee(eee);
	    					break;
	    				}
	    				
	    			}else if(deviceTypeback == 72){//72代表防爆燃气
	    				mRePeaterData.setTransmissionType(9);
	    				int proofgasmark = data[18]&0xff;	//A1=161  燃气标志
	    				if(proofgasmark==161){
	    					ProofGasEntity gas = new ProofGasEntity();
	    					gas.setProofGasMac(electricMacStr);
	    					int proofGasType = data[20]&0xFF;//探测器类型	2byte
							switch(proofGasType){
							case 0:
								gas.setProofGasType("防爆燃气");
								break;
							case 1:
								gas.setProofGasType("防爆燃气");
								break;
							}
							
							int proofGasUnit_low = data[23]&0xFF;//单位	
							switch (proofGasUnit_low) {
							case 0:
								gas.setProofGasUnit("V/V");
								break;
							case 1:
								gas.setProofGasUnit("LEL");
								break;
							case 2:
								gas.setProofGasUnit("PPM");
								break;
							case 3:
								gas.setProofGasUnit("KPPM");
								break;
							case 4:
								gas.setProofGasUnit("Mg/M3");
								break;
							default:
								break;
							}
							
							byte[] gasliangcheng = new byte[2];
							gasliangcheng[1] = data[24];
							gasliangcheng[0] = data[25];
							int proofGasliangcheng = IntegerTo16.bytesToInt(gasliangcheng);//量程	2byte
							gas.setProofGasliangcheng(proofGasliangcheng+"");
							
							byte[] a1Alarm = new byte[2];
							a1Alarm[1] = data[26];
							a1Alarm[0] = data[27];
							int proofGasA1Alarm = IntegerTo16.bytesToInt(a1Alarm);//A1报警点	2byte
							gas.setProofGasA1Alarm(proofGasA1Alarm+"");
							
							byte[] a2Alarm = new byte[2];
							a2Alarm[1] = data[28];
							a2Alarm[0] = data[29];
							int proofGasA2Alarm = IntegerTo16.bytesToInt(a2Alarm);//A2报警点	2byte
							gas.setProofGasA2Alarm(proofGasA2Alarm+"");
							
							int proofGasState = data[31]&0xff;	//探测器状态
							gas.setProofGasState(proofGasState+"");
							
							
							byte[] gasMmol = new byte[2];
							gasMmol[1] = data[34];
							gasMmol[0] = data[35];
							int proofGasMmol = IntegerTo16.bytesToInt(gasMmol);//气体浓度  2byte
							gas.setProofGasMmol(proofGasMmol+"");
							
							byte[] gasTemp = new byte[2];
							gasTemp[1] = data[36];
							gasTemp[0] = data[37];
							int proofGasTemp = IntegerTo16.bytesToInt(gasTemp);//传感器温度  2byte
							gas.setProofGasTemp(proofGasTemp+"");
							mRePeaterData.setGasEntity(gas);
	    				}
	    			}else if(deviceTypeback == 13){	//13代表设备为环境探测器
	    				mRePeaterData.setTransmissionType(4);	//1、是电气。2、是三江。3、水压。4、环境探测器。5、海湾。6、喷淋。7、水位设备处理
	    				int co2=((data[20]&0xff)<<8)+(data[21]&0xff);//二氧化碳 ppm
	    				double methanel =(((data[22]&0xff)<<8)+(data[23]&0xff))/1000.0;//甲醛 mg/m3
	    				int  humidity =(((data[24]&0xff)<<8)+(data[25]&0xff))/10;//湿度 
	    				double temperature=(((data[26]&0xff)<<8)+(data[27]&0xff)-500)/10.0;  //温度 .c
	    				int pm25=((data[28]&0xff)<<8)+(data[29]&0xff);//pm 2.5 ug/m3 
	    				
	    				String methanelString=new DecimalFormat("0.000").format(methanel);
	    				String temperatureString=new DecimalFormat("0.0").format(temperature);
	    				EnvironmentEntity env = new EnvironmentEntity();
	    				env.setArimac(electricMacStr);
	    				env.setCo2(co2+"");
	    				env.setMethanal(methanelString);
	    				env.setHumidity(humidity+"");
	    				env.setTemperature(temperatureString);
	    				env.setPm25(pm25+"");
	    				mRePeaterData.setEnvironment(env);
	    				log.debug("env=========="+env);
	    				break;
	    			}else if(deviceTypeback == 19 || deviceTypeback == 46){	//19水位探测器 lora,数据里有小数点位置,服务端判断报警，46NB水位，数据是整数，没有小数点位置，设备端判断报警
	    				System.out.println("deviceTypeback:"+deviceTypeback);
	    				byte[] waterRepeater = new byte[4];
		    			for(int j=0;j<4;j++){
		    				waterRepeater[j]=data[7+j];
		    			}
		    			String waterRepeaterMac = IntegerTo16.bytes2Hex(waterRepeater);
		    			mRePeaterData.setWaterRepeaterMac(waterRepeaterMac);
		    			mRePeaterData.setWaterMac(electricMacStr);
		    			int data18 = data[18]&0xff; //D2:
		    			String waterValue1 =((data[20]&0xff)<<8)+(data[21]&0xff)+"";
	    				int pow =  (int)Math.pow(10,((((data[22]&0xff)<<8)+(data[23]&0xff))));
	    				String waterleve = Float.parseFloat(waterValue1)/pow +"";//19的
		    			Water water = new Water();
		    			WaterInfoDao waterDao = new WaterInfoDaoImpl();
	    				int alarmType = waterDao.getWaterLeve(electricMacStr, waterleve);
		    			water.setRepeaterMac(repeaterMac);
		    			System.out.println("----------------repeaterMac:"+repeaterMac);
		    			
	    				water.setWaterMac(electricMacStr);
	    				System.out.println("----------------electricMacStr:"+electricMacStr);
	    				water.setValue(waterleve);
	    				System.out.println("data18:"+data18+"deviceTypeback:"+deviceTypeback);
	    				mRePeaterData.setTransmissionType(6);
		    			switch (data18) {
						case 210:
							//mRePeaterData.setTransmissionType(6);
							int waterStatus = data[20]&0xff;
							log.debug("水位状态:"+waterStatus);
							float waterValue = (((data[22]&0xff)<<8)+(data[21]&0xff))/1000.0f;						
							switch (waterStatus) {
							case 219: //正常  心跳
								water.setAlarmType(219);
								water.setValue(String.valueOf(waterValue));
								water.setType(1);
								break;
							case 218://高水位   208是高水位的报警类型
								water.setAlarmType(208);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 209://D1,低水位  207是低水位的报警类型
								water.setAlarmType(207);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 220:	//dc  低水压
								water.setAlarmType(193);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 195:	//dc  485故障
								water.setAlarmType(136);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							default:
								water.setAlarmType(12);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							}
							mRePeaterData.setWater(water);
							break;
						case 195:
							water.setAlarmType(136);
							water.setValue(String.valueOf(+9));
							water.setType(19);
							mRePeaterData.setWater(water);
							break;
						case 193:
							water.setAlarmType(193);//19的低电量
							water.setValue(String.valueOf(waterleve));
							water.setType(19);
							mRePeaterData.setWater(water);
							break;
						default:
							water.setAlarmType(alarmType);
							water.setValue(String.valueOf(waterleve));
							water.setType(19);
							mRePeaterData.setWater(water);
							break;
						}
	    			}else if (deviceTypeback == 10){  //10代表lora水压,包括普通和防爆，服务器端判断报警
	    			/** begin */
		    			byte[] waterRepeater = new byte[4];
		    			for(int j=0;j<4;j++){
		    				waterRepeater[j]=data[7+j];
		    			}
		    			String waterRepeaterMac = IntegerTo16.bytes2Hex(waterRepeater);
		    			mRePeaterData.setWaterRepeaterMac(waterRepeaterMac);
		    			mRePeaterData.setWaterMac(electricMacStr);
//		    			int data17 = data[17]&0xff;
		    			int data18 = data[18]&0xff; //DD:
		    			Water water = new Water();
		    			water.setWaterMac(electricMacStr);
		    			switch (data18) {
						case 221:
							mRePeaterData.setTransmissionType(3);
							int waterStatus = data[20]&0xff;
							log.debug("水压状态:"+waterStatus);
							int waterValue = ((data[22]&0xff)<<8)+(data[21]&0xff);
							WaterInfoDao waterDao = new WaterInfoDaoImpl();
		    				int alarmType = waterDao.getWaterGage2(electricMacStr, waterValue+"");
		    				if (alarmType == 0) {
		    					water.setAlarmType(219);
			    				water.setValue(waterValue+"");
			    				water.setType(1);
		    				} else {
			    				water.setAlarmType(alarmType);
			    				water.setValue(waterValue+"");
			    				water.setType(2);
		    				}
		    				
		    				if (waterStatus == 220){
		    					water.setAlarmType(193);
			    				water.setValue(waterValue+"");
			    				water.setType(2);
		    				}
							mRePeaterData.setWater(water);
							break;
						default:
							break;
						}
		    			/** end*/
	    			}else if (deviceTypeback == 42){  //42代表NB水压，包括普通和防爆，设备端判断报警
	    				/** begin */
		    			byte[] waterRepeater = new byte[4];
		    			for(int j=0;j<4;j++){
		    				waterRepeater[j]=data[7+j];
		    			}
		    			String waterRepeaterMac = IntegerTo16.bytes2Hex(waterRepeater);
		    			mRePeaterData.setWaterRepeaterMac(waterRepeaterMac);
		    			mRePeaterData.setWaterMac(electricMacStr);
//		    			int data17 = data[17]&0xff;
		    			int data18 = data[18]&0xff; //DD:
		    			Water water = new Water();
		    			water.setWaterMac(electricMacStr);
		    			switch (data18) {
						case 221:
							mRePeaterData.setTransmissionType(3);
							int waterStatus = data[20]&0xff;
							log.debug("水压状态:"+waterStatus);
							int waterValue = ((data[22]&0xff)<<8)+(data[21]&0xff);
							switch (waterStatus) {
							case 219: //正常
								water.setAlarmType(219);
								water.setValue(String.valueOf(waterValue));
								water.setType(1);
								break;
							case 218://高水压
								water.setAlarmType(218);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 209://D1,低水压
								water.setAlarmType(209);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 220:	//dc  低电压
								water.setAlarmType(193);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 217:	//d9升高
								water.setAlarmType(217);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 210: 	//d2降低
								water.setAlarmType(210);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							default:
								water.setAlarmType(12);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							}
							mRePeaterData.setWater(water);
							break;
						default:
							break;
						}
		    			/** end*/
	    			}else if (deviceTypeback ==25){  //单向温湿度设备
	    			/** begin */
	    				mRePeaterData.setTransmissionType(7);
		    			byte[] waterRepeater = new byte[4];
		    			for(int j=0;j<4;j++){
		    				waterRepeater[j]=data[7+j];
		    			}
		    			String waterRepeaterMac = IntegerTo16.bytes2Hex(waterRepeater);
		    			mRePeaterData.setWaterRepeaterMac(waterRepeaterMac);
		    			mRePeaterData.setWaterMac(electricMacStr);
		    			THDevice td = new THDevice();
		    			short h = Utils.getbyte2short(data[18],data[19]);
		    			short t = Utils.getbyte2short(data[20],data[21]);
		    			td.setHumidity(h/10.0f+"");
		    			td.setTemperature(t/10.0f+"");
		    			THDeviceInfoDao mThDeviceInfoDao=new THDeviceInfoImpl();
		    			mThDeviceInfoDao.addTHDeviceInfo(electricMacStr, td.getTemperature(), td.getHumidity());
		    			int state=data[17];
		    			switch(state){
		    				case 0:
				    			WaterInfoDao waterDao = new WaterInfoDaoImpl();
				    			String highTemperature = waterDao.getHighTemperature(electricMacStr);
				    			String lowTemperature = waterDao.getlowTemperature(electricMacStr);
				    			String highHumidity = waterDao.getHighHumidity(electricMacStr);
				    			String lowHumidity = waterDao.getlowHumidity(electricMacStr);
				    			if(lowTemperature != null && (t/10.0f) < Float.parseFloat(lowTemperature)){ 
				    				td.setAlarmType_T(307);//@@温度过低
				    				td.setAlarmValue_T((t/10.0f)+"");
				    			}
				    			if(highTemperature != null && (t/10.0f)>Float.parseFloat(highTemperature)){ 
				    				td.setAlarmType_T(308);//@@温度过高
				    				td.setAlarmValue_T((t/10.0f)+"");
				    			}
				    			if(highHumidity != null && (h/10.0f) > Float.parseFloat(highHumidity)){ 
				    				td.setAlarmType_H(408);//@@湿度过高
				    				td.setAlarmValue_H((h/10.0f)+"");
				    			}
				    			if(lowHumidity != null && (h/10.0f) < Float.parseFloat(lowHumidity)){ 
				    				td.setAlarmType_H(407);//@@湿度过低
				    				td.setAlarmValue_H((h/10.0f)+"");
				    			}
				    			break;
		    				case 1:
		    					td.setAlarmType_T(193);
		    					td.setAlarmValue_T(0+"");
		    					break;
		    			}
		    			mRePeaterData.setTd(td);
		    			/** end*/
	    			} 
	    			else if (deviceTypeback == 26) { //双向温湿度
	    				/** begin */
	    				THDevice td = new THDevice();
	    				td.setDevMac(electricMacStr);
	    				
	    				mRePeaterData.setTransmissionType(7);
		    			mRePeaterData.setWaterRepeaterMac(repeaterMac);
		    			mRePeaterData.setWaterMac(electricMacStr);
		    			
		    			int data20 = data[20]&0xff; //状态标志位命令 DB：219心跳    DC：220 低电压报警   DE : 222 缓存包  D1:209 低水压  DA:218 高水压 
	    				int temperature = Utils.getbyte2Int(data[22], data[21]);//+tempadjusted;	//获取收到的温度的值
	    				String tempStr = temperature/10.0f+"";
	    				td.setTemperature(tempStr);
	    				int humidity = Utils.getbyte2Int(data[24], data[23]);//+humadjusted;  //湿度值
	    				String humiStr = humidity/10.0f+"";
	    				td.setHumidity(humiStr);
		    		
	    				/*//更新设备心跳
		    			PublicUtils pu = new PublicUtilsImpl();
		    			pu.updateDeviceMac(electricMacStr);*/
	    				
		    			//保存温湿度值
		    			THDeviceInfoDao mThDeviceInfoDao=new THDeviceInfoImpl();
		    			mThDeviceInfoDao.addTHDeviceInfo(electricMacStr, tempStr, humiStr);
						
		    			
		    			int datavalue = 219;//@@报警类型 219 默认正常
						switch(data20){
						case 209: // 低温  D1
							datavalue = 307;
							td.setAlarmType_T(datavalue);
							td.setAlarmValue_T(tempStr);
							break;
						case 218: //高温  DA
							datavalue = 308;
							td.setAlarmType_T(datavalue);
							td.setAlarmValue_T(tempStr);
							break;
						case 210: //低湿  D2
							datavalue = 407;
							td.setAlarmType_H(datavalue);
							td.setAlarmValue_H(humiStr);
							break;
						case 217: //高湿度  D9
							datavalue = 408;
							td.setAlarmType_H(datavalue);
							td.setAlarmValue_H(humiStr);
							break;
						case 220: //低电压 DC
							datavalue = 193;
							td.setAlarmType_H(datavalue);
							td.setAlarmValue_H("0");
							break;
						case 219: //心跳 DB
							break;
						}
						mRePeaterData.setDeviceType(26);
	    				mRePeaterData.setTd(td);
	    			}
	    			else if (deviceTypeback == 18){  //18 喷淋设备
	    			/** begin */
		    			byte[] waterRepeater = new byte[4];
		    			for(int j=0;j<4;j++){
		    				waterRepeater[j]=data[7+j];
		    			}
		    			String waterRepeaterMac = IntegerTo16.bytes2Hex(waterRepeater);
		    			mRePeaterData.setWaterRepeaterMac(waterRepeaterMac);
		    			mRePeaterData.setWaterMac(electricMacStr);
//		    			int data17 = data[17]&0xff;
		    			int data18 = data[18]&0xff; //DE
		    			Water water = new Water();
		    			
		    			switch (data18) {
						case 222:
							mRePeaterData.setTransmissionType(6);
							int waterStatus = data[20]&0xff;					
							switch (waterStatus) {
							case 201: //@@11.02 停止报警
								SmokeLineDao mSmokeLineDao = new SmokeLineDaoImpl();
								mSmokeLineDao.setElectrState(electricMacStr, 2);
								water.setAlarmType(201);
								water.setType(1);
								break;
							}
							mRePeaterData.setWater(water);
							break;
						default:
							break;
						}
		    			/** end*/
	    			}
	    			else if (deviceTypeback == 27){  //vanke水浸
	    				/** begin */
		    			String waterRepeaterMac = repeaterMac;
		    			mRePeaterData.setWaterRepeaterMac(waterRepeaterMac);
		    			mRePeaterData.setWaterMac(electricMacStr);
		    			System.out.println("electricMacStr:=====>>>>>>>>>>>>>>");
		    			System.out.println(electricMacStr+"   "+waterRepeaterMac);
		    			System.out.println("dae.getAlarmthreshold1():=====>>>>>>>>>>");
		    			mRePeaterData.setTransmissionType(8);
		    			
		    			Water water = new Water();//处理后得到的对象
		    			int data20 = data[20]&0xff; //状态标志位命令 4b：75 心跳    4a：74 水浸报警  
    					switch(data20){
    					case  74:
    						water.setAlarmType(221);
    						break;
    					case 75:
		    				water.setAlarmType(0);
		    				/*缓存包结束*/
    						break;
    					}
    					
    					mRePeaterData.setDeviceType(deviceTypeback);
	    				mRePeaterData.setWater(water);
	    				/** end*/
	    			}
	    			
	    			else if(deviceTypeback == 5||deviceTypeback==52){
	    				/*PublicUtils utilsDao= new PublicUtilsImpl();
						utilsDao.updateDeviceMac(electricMacStr);//@@电气上线
*/						
						AreaDaoImpl areaDaoImpl=new AreaDaoImpl();//屏蔽贵州区域电气
						int areaid=areaDaoImpl.getAreaIdBySmokeMac(electricMacStr);
						if(areaid==105){
							mRePeaterData.setDeviceType(105);
							break;
						}
//						Utils.saveChangehistory("6601",electricMacStr,1);//@@12.22
	    				int flag=data[17]&0xff; //诚佳电气的标志位
	    				if(flag==95){  //设备属于诚佳电气 0x5f
	    					ChJEntity chj=new ChJEntity();
		    				int q_type=data[20]&0xff; //获取规格编码,也就是电流阈值
		    				int q_i=data[22]&0xff; //获取电流大小
		    				chj.setData7(Utils.electricValues(q_i, 1));  //存储电流数据
		    				switch(q_type){  //0x0A 10A; 0x14 20A;0x20 30A 
		    				case 10:
		    					chj.setThreshold(10);
		    					break;
		    				case 20:
		    					chj.setThreshold(20);
		    					break;
		    				case 32:
		    					chj.setThreshold(30);
		    					break;
		    				default:
		    					break;
		    				}
		    				int q_type3=data[21]&0xff; //获取包的类型
		    				switch(q_type3){ //0xCB 心跳；0xC2  过流报警；0xC3  短路报警；0xC4  过热报警；
		    				case 203:  //心跳
		    					chj.setType(1); 
		    					break;
		    				case 194:
		    					chj.setType(2); 
		    					chj.setAlarmType(45);
		    					break;
		    				case 195:
		    					chj.setType(2); 
		    					chj.setAlarmType(49);//短路
		    					break;
		    				case 196:
		    					chj.setType(2); 
		    					chj.setAlarmType(50);//过热
		    					break;
		    				default:
		    					break;
		    				}
		    				mRePeaterData.setChj(chj);
		    				mRePeaterData.setDeviceType(4);  //区分BQ系列与诚佳电气
		    			}else if(flag==17){			//金特莱
		    				BQ100Entity jtlEntity = new BQ100Entity();
		    				int deviceflag = data[23]&0xff;
		    				if(deviceflag == 17){	//电气设备
		    					int controllerflag = data[28]&0xff;
		    					log.debug("controllerflag: "+controllerflag+"  deviceflag:"+deviceflag);
		    					switch(controllerflag){
		    					case 1:	//心跳和故障
		    						int state=((data[26]&0xff)<<8)+(data[27]&0xff);	//报警列表
		    						int alarmtype=((data[24]&0xff)<<8)+(data[25]&0xff);	//报警列表
		    						List<String> list6 = new ArrayList<String>();//6电压
	    							List<String> list7 = new ArrayList<String>();//7电流
	    							List<String> list9 = new ArrayList<String>();//9温度
	    							String data8="";//漏电流
	    							String value61 = ((data[31]&0x7f)<<8)+(data[32]&0xff)+"";
									String value62 = ((data[33]&0x7f)<<8)+(data[34]&0xff)+"";
									String value63 = ((data[35]&0x7f)<<8)+(data[36]&0xff)+"";
									String value71 = ((data[37]&0x7f)<<8)+(data[38]&0xff)+"";
									String value72 = ((data[39]&0x7f)<<8)+(data[40]&0xff)+"";
									String value73 = ((data[41]&0x7f)<<8)+(data[42]&0xff)+"";
									value71 = Float.parseFloat(value71)/10+"";
									value72 = Float.parseFloat(value72)/10+"";
									value73 = Float.parseFloat(value73)/10+"";
									data8 = ((data[43]&0x7f)<<8)+(data[44]&0xff)+"";
									String value91 = ((data[45]&0x7f)<<8)+(data[46]&0xff)+"";
									String value92 = ((data[47]&0x7f)<<8)+(data[48]&0xff)+"";
									String value93 = ((data[49]&0x7f)<<8)+(data[50]&0xff)+"";
									String value94 = ((data[51]&0x7f)<<8)+(data[52]&0xff)+"";
		    						if(state==0){	//正常+透传数据
		    							jtlEntity.setType(2);
		    							jtlEntity.setAlarmType(0);
		    						}else{		//报警+报警数据
		    							jtlEntity.setType(1);
		    							List<String> listAlarm = new ArrayList<String>();
		    							if((state&0x01)==1){	//A相电压报警
		    								listAlarm.add("1");
//		    								list6.add(value61);
		    							}
		    							if((state&0x02)==2){	//B相电压报警
		    								listAlarm.add("2");
//		    								list6.add(value62);
		    							}
		    							if((state&0x04)==4){	//C相电压报警
		    								listAlarm.add("4");
//		    								list6.add(value63);
		    							}
		    							if((state&0x08)==8){	//A相电流报警
		    								listAlarm.add("8");
//		    								list7.add(value71);
		    							}
		    							if((state&0x10)==16){	//B相电流报警
		    								listAlarm.add("16");
//		    								list7.add(value72);
		    							}
		    							if((state&0x20)==32){	//C相电流报警
		    								listAlarm.add("32");
//		    								list7.add(value73);
		    							}
		    							if((state&0x40)==64){	//漏电流报警
		    								listAlarm.add("64");
//		    								jtlEntity.setData8(data8);
		    							}
		    							if((state&0x80)==128){	//A相温度报警
		    								listAlarm.add("128");
//		    								list9.add(value91);
		    							}
		    							if((state&0x100)==256){	//B相温度报警
		    								listAlarm.add("256");
//		    								list9.add(value92);
		    							}
		    							if((state&0x200)==512){	//C相温度报警
		    								listAlarm.add("512");
//		    								list9.add(value93);
		    							}
		    							if((state&0x400)==1024){//N相温度报警
		    								listAlarm.add("1024");
//		    								list9.add(value94);
		    							}if((state&0x800)==2048){//485故障
		    								listAlarm.add("2048");
//		    								list9.add(value94);
		    							}
		    							jtlEntity.setListAlarm(listAlarm);
		    							switch(alarmtype){
		    							case 257:	//0x0101     欠压
		    								jtlEntity.setAlarmType(44);
		    								break;
		    							case 513:	//0x0201     过压
		    								jtlEntity.setAlarmType(43);
		    								break;
		    							case 769:	//0x0301     过载
		    								jtlEntity.setAlarmType(45);
		    								break;
		    							case 1025:	//0x0401     漏电
		    								jtlEntity.setAlarmType(46);
		    								break;
		    							case 1281:	//0x0501     温度高
		    								jtlEntity.setAlarmType(47);
		    								break;
		    							case 1537:	//0x0601     电气短路故障
		    								jtlEntity.setAlarmType(49);
		    								break;
		    							case 1793:	//0x0701     电气断路故障
		    								jtlEntity.setAlarmType(52);
		    								break;
		    							default:
		    								jtlEntity.setAlarmType(0);
		    								break;
		    							}
		    						}
		    						list6.add(value61);
									list6.add(value62);
									list6.add(value63);
									
									list7.add(value71);
									list7.add(value72);
									list7.add(value73);
									
									list9.add(value91);
									list9.add(value92);
									list9.add(value93);
									list9.add(value94);
									jtlEntity.setList6(list6);
									jtlEntity.setData8(data8);
									jtlEntity.setList7(list7);
									jtlEntity.setList9(list9);
		    						break;
		    					case 10:	//阈值
		    						jtlEntity.setType(3);
									ElectricThresholdBean mElectricThresholdBean = Utils.jtlElectricThresholdBean(data,33);
									if(mElectricThresholdBean!=null){
										jtlEntity.setmElectricThresholdBean(mElectricThresholdBean);
									}
		    						break;
		    					}
		    				}
		    				mRePeaterData.setmBQ100Entity(jtlEntity);
		    				mRePeaterData.setDeviceType(5);  //区分jtl系列与诚佳电气
		    			}else if(flag==104){ //@@三相电表
		    				ThreePhaseMeterEntity meter=new ThreePhaseMeterEntity();
		    				int makesure = data[18]&0xff;
		    				float threephasedata=0;
		    				if(makesure!=145){
		    					return null;
		    				}
		    				int commad_lengh=data[19];
		    				byte[] commad={data[20],data[21],data[22],data[23]};
		    				String commandType=IntegerTo16.bytes2Hex(commad);//@@标识码
		    				byte[] dataByte = new byte[commad_lengh-4];
			    			for(int i=0;i<dataByte.length;i++){
			    				dataByte[i] = (byte) (data[23+dataByte.length-i]-(byte)0x33);
			    			}
			    			
			    			String commad_data = IntegerTo16.bytes2Hex(dataByte);//@@数据
			    			try{
			    				threephasedata=Float.parseFloat(commad_data);
			    			}catch(Exception e){
			    				threephasedata=0;
			    			}
		    				switch (commandType) {
							case "33343435"://@@A相电压
								threephasedata=threephasedata/10;
								meter.setData_num(1);
								meter.setData(threephasedata+"");
								meter.setData_type(61);
								meter.setType(1);
								break;
							case "33353435"://@@B相电压
								threephasedata=threephasedata/10;
								meter.setData_num(2);
								meter.setData(threephasedata+"");
								meter.setData_type(62);
								meter.setType(1);
								break;
							case "33363435"://@@C相电压
								threephasedata=threephasedata/10;
								meter.setData_num(3);
								meter.setData(threephasedata+"");
								meter.setData_type(63);
								meter.setType(1);
								break;
							case "33343535"://@@A相电流
								threephasedata=threephasedata/10;
								meter.setData_num(1);
								meter.setData(threephasedata+"");
								meter.setData_type(71);
								meter.setType(1);
								break;
							case "33353535"://@@B相电流
								threephasedata=threephasedata/10;
								meter.setData_num(2);
								meter.setData(threephasedata+"");
								meter.setData_type(72);
								meter.setType(1);
								break;
							case "33363535"://@@C相电流
								threephasedata=threephasedata/10;
								meter.setData_num(3);
								meter.setData(threephasedata+"");
								meter.setData_type(73);
								meter.setType(1);
								break;
							case "3333C335"://@@当前剩余电流最大相
								threephasedata=threephasedata/10;
								break;
//							case "3334C435"://@@当前剩余电流值（漏电流）@@
//								meter.setData_num(1);
//								meter.setData(threephasedata+"");
//								meter.setData_type(81);
//								meter.setType(1);
//								break;
							case "3334C335"://@@当前剩余电流值（漏电流）@@
								meter.setData_num(1);
								meter.setData(threephasedata+"");
								meter.setData_type(81);
								meter.setType(1);
								break;
							case "37373337"://@@额定电压值
								threephasedata=threephasedata/10;
								break;
							case "38373337"://@@额定电流值
								threephasedata=threephasedata/10;
								break;
							case "34463337"://@@过压阈值
								threephasedata=threephasedata/10;
								if(threephasedata<100||threephasedata>320){
									return null;
								}
								meter.setOverV(threephasedata+"");
								meter.setType(43);
								break;
							case "35463337"://@@欠压阈值
								threephasedata=threephasedata/10;
								meter.setDownV(threephasedata+"");
								meter.setType(44);
								break;
//							case "34443337"://@@漏电阈值
//								threephasedata=threephasedata/10;
//								meter.setLossI(threephasedata+"");
//								meter.setType(46);
//								break;
							case "3334C435"://@@漏电阈值
								if(threephasedata<0||threephasedata>3500){
									return null;
								}
								meter.setLossI(threephasedata+"");
								meter.setType(46);
								break;
							case "34473337"://@@过流阈值
								threephasedata=threephasedata/10;
								if(threephasedata<0||threephasedata>300){
									return null;
								}
								meter.setOverI(threephasedata+"");
								meter.setType(45);
								break;
							case "0408050E":
								meter.setAlarmType(136);//@@485故障
								meter.setType(2);
								break;
							case "34383337"://@@运行状态字
								meter.setType(2);
								byte temp=(byte) (data[24]-(byte)0x33);
								int alarm_reason = temp&0x1f;//@@报警原因
								switch(alarm_reason){
									case 0://00000-正常运行
										meter.setAlarmType(0);
										break;
									case 2://00010-剩余电流（漏电流报警）
										meter.setAlarmType(46);
										break;
									case 4://00100-缺零
										break;
									case 5://00101-过载
										meter.setAlarmType(45);
										break;
									case 6://00110-短路
										meter.setAlarmType(49);
										break;
									case 7://00111-缺相
										break;
									case 8://01000-欠压
										meter.setAlarmType(44);
										break;
									case 9://01001-过压
										meter.setAlarmType(43);
										break;
									case 10://01010-接地
										break;
									case 11://01011-停电
										meter.setAlarmType(52);
										break;
									case 12://01100-定时试验
										break;
									case 13://01101-远程
										break;
									case 14://01110-按键试验
										break;
									case 15://01111-闭锁
										break;
									case 18://10010-手动
										meter.setAlarmType(66);//@@测试
										break;
									case 16://10000-互感器故障
										break;
									case 17://10001-合闸失败
										break;
									case 19://10011-设置更改
										break;
								}
								int alarmstate = (temp>>7)&0xff;
								switch(alarmstate){
									case 0:
										meter.setIfalarm(0);
										break;
									case 1:
										meter.setIfalarm(1);
										break;
								}
								int electricstate = (temp>>5)&0x03;//@@合闸状态
								switch(electricstate){
									case 0://合闸
										meter.setElectric_state(1);
										break;
									case 1://保留（分闸）
										meter.setElectric_state(2);
										break;
									case 2://重合闸（故障状态下的分闸）
										meter.setElectric_state(2);
										break;
									case 3:
										meter.setElectric_state(2);//@@分闸
										break;
									default:
										meter.setElectric_state(0);
										break;
								}
								break;
							default:
								break;
							}
		    				mRePeaterData.setDeviceType(6);
		    				mRePeaterData.setThreePhaseMeterEntity(meter);
		    			}else{
			    			int deviceType = data[21]&0xff;//设备类型，区分BQ100与BQ200，其中1表示BQ200，2表示BQ100
			    			log.debug("deviceType="+deviceType);
			    			switch (deviceType) {
							case 1://BQ200
								int bq200Type = data[17]&0xff;
								log.debug("bq200Type="+bq200Type);  
								BQ200Entity mBQ200Entity = new BQ200Entity();
								switch (bq200Type) {
								case 229://E5 电气设备2.0版:
									mRePeaterData.setDeviceType(1);
									int typeNum200 = data[25]&0xff;
									switch (typeNum200) {
									case 35:
										//报警
										mBQ200Entity.setType(1);
										int alarmType = data[30]&0xff;
										switch (alarmType) {
										case 1://1漏电流报警；2温度报警；3欠压报警；4过压报警；5过流报警；10压力过压； 
											mBQ200Entity.setAlarmType(46);
											mBQ200Entity.setElectricDevType(8);//@@11.30
											break;
										case 2:
											mBQ200Entity.setAlarmType(47);
											mBQ200Entity.setElectricDevType(9);//@@11.30
											break;
										case 3:
											mBQ200Entity.setAlarmType(44);
											mBQ200Entity.setElectricDevType(6);//@@11.30
											break;
										case 4:
											mBQ200Entity.setAlarmType(43);
											mBQ200Entity.setElectricDevType(6);//@@11.30
											break;
										case 5:
											mBQ200Entity.setAlarmType(45);
											mBQ200Entity.setElectricDevType(7);//@@11.30
											break;
										case 10:
											mBQ200Entity.setAlarmType(6);
											break;
										default:
											break;
										}
										byte[] valueByte = new byte[2];
										valueByte[0] = data[34];
										valueByte[1] = data[35];
										int two = IntegerTo16.bytesToInt(valueByte);
										valueByte[0] = data[37];
										valueByte[1] = data[38];
										int one = IntegerTo16.bytesToInt(valueByte); 
										String valueStr = Utils.electricValues(one, two);
										mBQ200Entity.setAlarmData(valueStr);
										//@@11.30过滤报警异常数据
										if(mBQ200Entity.getElectricDevType()==6){
											if(Double.parseDouble(valueStr)<80||Double.parseDouble(valueStr)>300){
												return null;
											}//@@11.30过滤突变错误数据
										}else if(mBQ200Entity.getElectricDevType()==7){
											if(Double.parseDouble(valueStr)<0||Double.parseDouble(valueStr)>63){
												return null;
											}//@@11.30 过滤突变错误数据
										}else{
											if(Double.parseDouble(valueStr)<0||Double.parseDouble(valueStr)>900){
												return null;
											}//@@11.30 过滤突变错误数据
										}
										break;
									case 6:
										//心跳
										mBQ200Entity.setType(2);
										for(int i=0;i<2;i++){
											int heartType = data[25+(8*i)]&0xff;
											byte[] heartByte = new byte[2];
											heartByte[0] = data[27+(8*i)];
											heartByte[1] = data[28+(8*i)];
											int heartTwo = IntegerTo16.bytesToInt(heartByte);
											heartByte[0] = data[30+(8*i)];
											heartByte[1] = data[31+(8*i)];
											int heartOne = IntegerTo16.bytesToInt(heartByte);
											String valueData = Utils.electricValues(heartOne, heartTwo);
											if(heartType==6){
												if(Double.parseDouble(valueData)<80||Double.parseDouble(valueData)>300){
													return null;
												}//@@11.04 过滤突变错误数据
												mBQ200Entity.setData6(valueData);
											}else{
												if(Double.parseDouble(valueData)<0||Double.parseDouble(valueData)>63){
													return null;
												}//@@11.04 过滤突变错误数据
												mBQ200Entity.setData7(valueData);
											}
										}
										break;
									case 43://阈值
										mBQ200Entity.setType(4);
										ElectricThresholdBean mElectricThresholdBean = Utils.unElectricThresholdBeanPackage(data,2);
										if(mElectricThresholdBean!=null){
											mBQ200Entity.setmElectricThresholdBean(mElectricThresholdBean);
										}
										break;
									case 36://故障
										mBQ200Entity.setType(3);
										String alarmValue = Utils.getValue(data, 25);
										mBQ200Entity.setAlarmData(alarmValue);
										mBQ200Entity.setAlarmType(36);
//										return null;//@@11.17屏蔽故障
										if(!ifPushThisFault(electricMacStr)){
											return null;
										}
										break;
									default:
										break;
									}
									mRePeaterData.setmBQ200Entity(mBQ200Entity);
									break;
		
								case 230://E6 电气设备1.0版:
									mRePeaterData.setDeviceType(1);
									int typeNum2001 = data[25]&0xff;
									switch (typeNum2001) {
									case 6://心跳
										mBQ200Entity.setType(2);
										for(int i=0;i<2;i++){
											int heartType = data[25+(8*i)]&0xff;
											byte[] heartByte = new byte[2];
											heartByte[0] = data[27+(8*i)];
											heartByte[1] = data[28+(8*i)];
											int heartTwo = IntegerTo16.bytesToInt(heartByte);
											heartByte[0] = data[30+(8*i)];
											heartByte[1] = data[31+(8*i)];
											int heartOne = IntegerTo16.bytesToInt(heartByte);
											String valueData = Utils.electricValues(heartOne, heartTwo);
											if(heartType==6){
												if(Double.parseDouble(valueData)<80||Double.parseDouble(valueData)>300){
													return null;
												}//@@11.04 过滤突变错误数据
												mBQ200Entity.setData6(valueData);
											}else{
												if(Double.parseDouble(valueData)<0||Double.parseDouble(valueData)>63){
													return null;
												}//@@11.04 过滤突变错误数据
												mBQ200Entity.setData7(valueData);
											}
										}
										break;
									case 43:
									case 44:
									case 45:
									case 46:
									case 47:
										mBQ200Entity.setType(1);
										mBQ200Entity.setAlarmType(typeNum2001);
										byte[] valueByte = new byte[2];
										valueByte[0] = data[27];
										valueByte[1] = data[28];
										int two = IntegerTo16.bytesToInt(valueByte);
										valueByte[0] = data[30];
										valueByte[1] = data[31];
										int one = IntegerTo16.bytesToInt(valueByte); 
										String valueStr = Utils.electricValues(one, two);
										mBQ200Entity.setAlarmData(valueStr);
										break;
									case 36://故障
										mBQ200Entity.setType(3);
										String alarmValue = Utils.getValue(data, 25);
										mBQ200Entity.setAlarmData(alarmValue);
										mBQ200Entity.setAlarmType(36);
//										return null;//@@11.17屏蔽故障
										if(!ifPushThisFault(electricMacStr)){
											return null;
										}
										break;
									}
									mRePeaterData.setmBQ200Entity(mBQ200Entity);
									break;
								}
								
								break;
							case 2://BQ100
								int bq100Type = data[17]&0xff;
								BQ100Entity mBQ100Entity = new BQ100Entity();
								switch (bq100Type) {
								case 229://E5 电气设备2.0版
									mRePeaterData.setDeviceType(3);
									int typeNum = data[25]&0xff;
									switch (typeNum) {
										case 6://心跳
											mBQ100Entity.setType(1);
											int lenMark = 25;
											List<String> list=null;
											for(int i=0;i<4;i++){
												int heartType = data[lenMark]&0xff;
												int heartNum = data[lenMark+4]&0xff;
												switch (heartType) {
												case 6:
													list=Utils.dataList(heartNum, data, lenMark,heartType);
													if(list==null){
														return null;
													}
													mBQ100Entity.setList6(list);
													lenMark = lenMark+8+(heartNum-1)*2;
													break;
												case 7:
													list=Utils.dataList(heartNum, data, lenMark,heartType);
													if(list==null){
														return null;
													}
													mBQ100Entity.setList7(Utils.dataList(heartNum, data, lenMark,heartType));
													lenMark = lenMark+8+(heartNum-1)*2;
													break;
												case 8:
													list=Utils.dataList(heartNum, data, lenMark,heartType);
													if(list==null){
														return null;
													}
													mBQ100Entity.setData8(list.get(0));
													lenMark = lenMark+8+(heartNum-1)*2;
													break;
												case 9:
													mBQ100Entity.setList9(Utils.dataList(heartNum, data, lenMark,heartType));
													lenMark = lenMark+8+(heartNum-1)*2;
													break;
												default:
													break;
												}
											}
											break;
										case 43://阈值
											mBQ100Entity.setType(4);
											ElectricThresholdBean mElectricThresholdBean = Utils.unElectricThresholdBeanPackage(data,4);
											if(mElectricThresholdBean!=null){
												mBQ100Entity.setmElectricThresholdBean(mElectricThresholdBean);
											}
											break;
										case 36://故障
											mBQ100Entity.setType(3);
											String alarmValue = Utils.getValue(data, 25);
											mBQ100Entity.setAlarmData(alarmValue);
											mBQ100Entity.setAlarmType(36);
//											return null;//@@11.17屏蔽故障
											if(!ifPushThisFault(electricMacStr)){
												return null;
											}
											break;
										case 35://报警
											mBQ100Entity.setType(2);
											int alarmNumber = (int) Float.parseFloat((Utils.getValue(data, 25)));
											switch (alarmNumber) {//1漏电流报警；2温度报警；3欠压报警；4过压报警；5过流报警；10压力过压； 
											case 1://漏电流报警
												String leakCurrent = Utils.getValue(data, 57);
												mBQ100Entity.setData8(leakCurrent);
												mBQ100Entity.setAlarmType(46);
												break;
											case 2://温度报警
												mBQ100Entity.setList9(Utils.dataList(4,data,65,9));
												mBQ100Entity.setAlarmType(47);
												break;
											case 3://欠压报警
												mBQ100Entity.setList6(Utils.dataList(3,data,33,6));
												mBQ100Entity.setAlarmType(44);
												break;
											case 4://过压报警
												mBQ100Entity.setList6(Utils.dataList(3,data,33,6));
												mBQ100Entity.setAlarmType(43);
												break;
											case 5://过流报警
												mBQ100Entity.setList7(Utils.dataList(3,data,45,7));
												mBQ100Entity.setAlarmType(45);
												break;
											case 10://压力过压
												mBQ100Entity.setAlarmType(6);
												break;
											default:
												break;
											}
											break;
									}
									break;
								
									
								case 231://E7 贵州电气设备2.0版:
									mRePeaterData.setDeviceType(1);
									int typeNum200 = data[25]&0xff;
									BQ200Entity mBq100Entity = new BQ200Entity();
									switch (typeNum200) {
									case 35:
										//报警
										mBq100Entity.setType(1);
										int alarmType = data[30]&0xff;
										switch (alarmType) {
										case 1://1漏电流报警；2温度报警；3欠压报警；4过压报警；5过流报警；6合闸报警； 
											mBq100Entity.setAlarmType(146);
											mBq100Entity.setElectricDevType(8);
											mBq100Entity.setAlarmData("146");
											break;
										case 2:
											mBq100Entity.setAlarmType(147);
											mBq100Entity.setElectricDevType(9);
											mBq100Entity.setAlarmData("147");
											break;
										case 3:
											mBq100Entity.setAlarmType(144);
											mBq100Entity.setElectricDevType(6);
											mBq100Entity.setAlarmData("144");
											break;
										case 4:
											mBq100Entity.setAlarmType(143);
											mBq100Entity.setElectricDevType(6);
											mBq100Entity.setAlarmData("143");
											break;
										case 5:
											mBq100Entity.setAlarmType(145);
											mBq100Entity.setElectricDevType(7);
											mBq100Entity.setAlarmData("145");
											break;
										case 6:
											mBq100Entity.setType(5);//当作故障返回，不推送，但保存到历史纪录
											mBq100Entity.setAlarmType(148);
											mBq100Entity.setElectricDevType(10);
											mBq100Entity.setAlarmData("148");
											SmokeLineDaoImpl mSmokeLineDao = new SmokeLineDaoImpl();
											mSmokeLineDao.setElectrState(electricMacStr, 1);//@@2018.04.14
											Utils.saveChangehistory("6603",electricMacStr,1);//@@12.22
											GetSmokeMacByRepeaterDao mbrDao = new GetSmokeMacByRepeaterDaoImpl();
											mbrDao.updateSmokeInfo(electricMacStr,0);//@@2018.03.27
//											mBq100Entity.setAlarmType(148);
//											mBq100Entity.setElectricDevType(10);
//											mBq100Entity.setAlarmData("148");
//											Utils.saveChangehistory("6603",electricMacStr,1);//@@12.22
//											return null;
											break;
										default:
											break;
										}
										byte[] valueByte = new byte[2];
										valueByte[0] = data[35];
										valueByte[1] = data[36];
										int two = IntegerTo16.bytesToInt(valueByte);
										valueByte[0] = data[38];
										valueByte[1] = data[39];
										int one = IntegerTo16.bytesToInt(valueByte); 
										String valueStr = Utils.electricValues(one, two);
										mBq100Entity.setAlarmData(valueStr);
										//@@11.30过滤报警异常数据
										if(mBq100Entity.getElectricDevType()==6){
											if(Double.parseDouble(valueStr)<80||Double.parseDouble(valueStr)>300){
												return null;
											}//@@11.30过滤突变错误数据
										}else if(mBq100Entity.getElectricDevType()==7){
											if(Double.parseDouble(valueStr)<0||Double.parseDouble(valueStr)>63){
												return null;
											}//@@11.30 过滤突变错误数据
										}else if(mBq100Entity.getElectricDevType()==8){
											if(Double.parseDouble(valueStr)<0||Double.parseDouble(valueStr)>900){
												return null;
											}//@@11.30 过滤突变错误数据
										}
										break;
									case 6:
										//心跳
										mBq100Entity.setType(2);
										for(int i=0;i<3;i++){
											int heartType = data[25+(8*i)]&0xff;
											byte[] heartByte = new byte[2];
											heartByte[0] = data[27+(8*i)];
											heartByte[1] = data[28+(8*i)];
											int heartTwo = IntegerTo16.bytesToInt(heartByte);
											heartByte[0] = data[30+(8*i)];
											heartByte[1] = data[31+(8*i)];
											int heartOne = IntegerTo16.bytesToInt(heartByte);
											String valueData = Utils.electricValues(heartOne, heartTwo);
											if(heartType==6){
												if(Double.parseDouble(valueData)<80||Double.parseDouble(valueData)>300){
													return null;
												}//@@10.31 过滤突变错误数据
												mBq100Entity.setData6(valueData);
											}else if(heartType==7){
												if(Double.parseDouble(valueData)<0||Double.parseDouble(valueData)>63){
													return null;
												}//@@10.31 过滤突变错误数据
												mBq100Entity.setData7(valueData);
											}else{
												if(Double.parseDouble(valueData)<0||Double.parseDouble(valueData)>900){
													return null;
												}//@@10.31 过滤突变错误数据
												mBq100Entity.setData8(valueData);
											}
										}
										break;
									case 43://阈值
										mBq100Entity.setType(4);
										ElectricThresholdBean mElectricThresholdBean = Utils.unElectricThresholdBeanPackage2(data);
										if(mElectricThresholdBean!=null){
											mBq100Entity.setmElectricThresholdBean(mElectricThresholdBean);
										}
										break;
									case 36://故障
										mBq100Entity.setType(5);
										int alarmValue1 = data[30]&0xff;
										log.debug(alarmValue1+"=alarmValue1");
										String alarmValue = alarmValue1+"";
										mBq100Entity.setAlarmData(alarmValue);
										mBq100Entity.setAlarmType(136);	
//										return null;//@@11.17屏蔽故障
										if(!ifPushThisFault(electricMacStr)){
											return null;
										}
										break;
									default:
										break;
									}
									mRePeaterData.setmBQ200Entity(mBq100Entity);
									break;
		
								case 230://E6 电气设备1.0版
									mRePeaterData.setDeviceType(2);
									int bq100Len = electricDataLen-14;
									if(bq100Len==8){
										//报警
										mBQ100Entity.setType(1);
										int alarmType = data[25]&0xff;
										mBQ100Entity.setAlarmType(alarmType);
										byte[] valueByte = new byte[2];
										valueByte[0] = data[27];
										valueByte[1] = data[28];
										int two = IntegerTo16.bytesToInt(valueByte);
										valueByte[0] = data[30];
										valueByte[1] = data[31];
										int one = IntegerTo16.bytesToInt(valueByte); 
										String valueStr = Utils.electricValues(one, two);
										mBQ100Entity.setAlarmData(valueStr);
									}else{
										//心跳
										mBQ100Entity.setType(2);
										int lenMark = 25;
										List<String> list=null;
										for(int i=0;i<4;i++){
											int heartType = data[lenMark]&0xff;
											int heartNum = data[lenMark+4]&0xff;
											switch (heartType) {
											case 6:
												list=Utils.dataList(heartNum, data, lenMark,heartType);
												if(list==null){
													return null;
												}
												mBQ100Entity.setList6(list);
												lenMark = lenMark+8+(heartNum-1)*2;
												break;
											case 7:
												list=Utils.dataList(heartNum, data, lenMark,heartType);
												if(list==null){
													return null;
												}
												mBQ100Entity.setList7(list);
												lenMark = lenMark+8+(heartNum-1)*2;
												break;
											case 8:
												list=Utils.dataList(heartNum, data, lenMark,heartType);
												if(list==null){
													return null;
												}
												mBQ100Entity.setData8(list.get(0));
												lenMark = lenMark+8+(heartNum-1)*2;
												break;
											case 9:
												mBQ100Entity.setList9(Utils.dataList(heartNum, data, lenMark,heartType));
												lenMark = lenMark+8+(heartNum-1)*2;
												break;
											default:
												break;
											}
										}
									}
									break;
								}
								mRePeaterData.setmBQ100Entity(mBQ100Entity);
								break;
							
							default:
								break;
							}
							break;
		    			}
	    			}
					default:
						break;
				}
    			break;
    			//透传包结束
    			
    		case 6:
    			mRePeaterData.setCmd2((byte)0x06);
    			break;
    		case 209://@@三相分合闸控制回包
    			byte[] eleRepeaterMac209 = new byte[4];
    			for(int i=0;i<4;i++){
    				eleRepeaterMac209[i] = data[7+i];
    			}
    			String RepeaterMac209 = IntegerTo16.bytes2Hex(eleRepeaterMac209);
    			mRePeaterData.setRepeatMac(RepeaterMac209);
    			byte[] eleMac1209 = new byte[4];
    			for(int i=0;i<4;i++){
    				eleMac1209[i] = data[11+i];
    			}
    			String eleMac209 = IntegerTo16.bytes2Hex(eleMac1209);
    			mRePeaterData.setElectricMac(eleMac209);
    			mRePeaterData.setCmd2((byte)0xD1);
    			break;
    		case 11:
    			byte[] eleRepeaterMac11 = new byte[4];
    			for(int i=0;i<4;i++){
    				eleRepeaterMac11[i] = data[7+i];
    			}
    			String RepeaterMac = IntegerTo16.bytes2Hex(eleRepeaterMac11);
    			mRePeaterData.setRepeatMac(RepeaterMac);
    			byte[] eleMac11 = new byte[4];
    			for(int i=0;i<4;i++){
    				eleMac11[i] = data[11+i];
    			}
    			String eleMac = IntegerTo16.bytes2Hex(eleMac11);
    			mRePeaterData.setElectricMac(eleMac);
    			mRePeaterData.setCmd2((byte)0x0B);
    			break;
    		case 12:
    			byte[] eleRepeaterMac12 = new byte[4];
    			for(int i=0;i<4;i++){
    				eleRepeaterMac12[i] = data[7+i];
    			}
    			String RepeaterMac12 = IntegerTo16.bytes2Hex(eleRepeaterMac12);
    			mRePeaterData.setRepeatMac(RepeaterMac12);
    			byte[] elecMac12 = new byte[4];
    			for(int i=0;i<4;i++){
    				elecMac12[i] = data[11+i];
    			}
    			String eleMac12 = IntegerTo16.bytes2Hex(elecMac12);
    			mRePeaterData.setElectricMac(eleMac12);
    			mRePeaterData.setCmd2((byte)0x0C);
    			break;
    		case 13:
    			byte[] eleRepeaterMac13 = new byte[4];
    			for(int i=0;i<4;i++){
    				eleRepeaterMac13[i] = data[7+i];
    			}
    			String RepeaterMac13 = IntegerTo16.bytes2Hex(eleRepeaterMac13);
    			mRePeaterData.setRepeatMac(RepeaterMac13);
    			byte[] elecMac13 = new byte[4];
    			for(int i=0;i<4;i++){
    				elecMac13[i] = data[11+i];
    			}
    			String eleMac13 = IntegerTo16.bytes2Hex(elecMac13);
    			mRePeaterData.setElectricMac(eleMac13);
    			mRePeaterData.setCmd2((byte)0x0D);
    			break;
    		case 153:
    			mRePeaterData.setCmd2((byte)0x99);
    			break;
    		default:
    			break;
    		}
        	return mRePeaterData;
    	}else{
    		return null;
    	}
    
	}
	
	
	//@@判断是否推送和保存该故障
	private static boolean ifPushThisFault(String electricMacStr) {
		boolean ifpush=true;
		if(IfStopAlarm.ifPushFault.containsKey(electricMacStr)){
			if((System.currentTimeMillis()-IfStopAlarm.ifPushFault.get(electricMacStr))>60*60*1000){  
				ifpush=true;
				IfStopAlarm.ifPushFault.put(electricMacStr, System.currentTimeMillis());
			}else{
				ifpush=false;
			}
		}else{
			IfStopAlarm.ifPushFault.put(electricMacStr, System.currentTimeMillis());
		}
		return ifpush;
	}
	
	public static RePeaterData NB_IOTHeart(byte[] dataN,int byteLen) throws Exception {
		byte[] data = Utils.formatByte(dataN);
		int byteCount =data.length;
    	boolean crcRight = checkCrc(data,byteCount);
    	if(crcRight){
    		RePeaterData mRePeaterData = new RePeaterData();
    		mRePeaterData.setSeqL(data[3]);
    		mRePeaterData.setSeqH(data[4]);
    		byte[] macByte = new byte[4];
    		for(int i=0;i<4;i++){
    			macByte[i] = data[7+i];
    		}
    		String repeaterMac = IntegerTo16.bytes2Hex(macByte);
//    		mRePeaterData.setRepeatMac(repeaterMac);
//    		byte[] dataLen = new byte[2];
//    		dataLen[1] = data[6];
//    		dataLen[0] = data[5];
//    		int len = IntegerTo16.bytesToInt(dataLen);
    		int msg = data[2]&0xff;
    		log.debug("msg================================"+msg);
    		byte[] eletrcImei;
    		byte[] eletrcImsi;
    		String imeiStr;
    		String imsiStr;
    		String rssiVal;
    		Water water;
    		ThreePhaseElectricEntity threeElec;
    		byte[] dataValue;
    		int waterValue = 0;
    		int waterDev = 0;
    		int alarmType = 0;
        	switch (msg) {
    		case 1:
    			mRePeaterData.setCmd2((byte)0x01);
				byte[] mac = new byte[4];
				for(int j=0;j<4;j++){
					mac[j] = data[7+j];
				}
				imeiStr = IntegerTo16.bytes2Hex(mac);
				mRePeaterData.setAlarmSmokeMac(imeiStr);
				mRePeaterData.setRepeatMac(imeiStr);
    			break;
    		case 2:
    			mRePeaterData.setCmd2((byte)0x02);	//报警
    			byte[] alarmMac = new byte[4];
    			for(int i=0;i<4;i++){
    				alarmMac[i] = data[7+i];
    			}
    			imeiStr = IntegerTo16.bytes2Hex(alarmMac);
    			mRePeaterData.setAlarmSmokeMac(imeiStr);
    			mRePeaterData.setRepeatMac(imeiStr);
    			break;
    		case 8:
    			mRePeaterData.setCmd2((byte)0x08);	
    			byte[] alarmMac7 = new byte[4];
    			for(int i=0;i<4;i++){
    				alarmMac7[i] = data[11+i];
    			}
    			imeiStr = IntegerTo16.bytes2Hex(alarmMac7);
    			mRePeaterData.setAlarmSmokeMac(imeiStr);
    			mRePeaterData.setCmd(data[15]);
    			mRePeaterData.setRepeatMac(imeiStr);
    			break;
    		case 38: //NB-水压水位阈值 0x26
    			mRePeaterData.setCmd2((byte)0x26);
    			water = new Water();
    			waterDev = data[7]&0xff; //设备类型//0xaa 水压 170 //0xBB 水位 187
    			
    			
    			eletrcImei = new byte[8];
    			for(int i=0;i<8;i++){
    				eletrcImei[i] = data[8+i];
    			}
    			imeiStr = IntegerTo16.bytes2Hex(eletrcImei).substring(1);
    			water.setWaterMac(imeiStr);
    			mRePeaterData.setRepeatMac(imeiStr);
//    			2byte->高水压（高水位）阈值（Kpa）
    			dataValue = new byte[2];
    			dataValue[1] = data[17];
    			dataValue[0] = data[16];
        		waterValue = IntegerTo16.bytesToInt(dataValue);
//        		water.setH_value(waterValue);
        		if(waterDev == 170){	
    				water.setDeviceType(1);	//水压
    				water.setH_value(waterValue);
    			}else if(waterDev == 187){	
    				water.setDeviceType(2);	//水位
    				water.setH_value((float)waterValue/1000);
    			}
        		
//        		 2byte->低水压（低水位）阈值(Kpa) 
        		dataValue = new byte[2];
    			dataValue[1] = data[19];
    			dataValue[0] = data[18];
        		waterValue = IntegerTo16.bytesToInt(dataValue);
        		water.setL_value(waterValue);
        		
        		if(waterDev == 170){	
    				water.setDeviceType(1);	//水压
    				water.setL_value(waterValue);
    			}else if(waterDev == 187){	
    				water.setDeviceType(2);	//水位
    				water.setL_value((float)waterValue/1000);
    			}
        		
//        		2byte->发送时间（分） 
        		dataValue = new byte[2];
    			dataValue[1] = data[21];
    			dataValue[0] = data[20];
        		waterValue = IntegerTo16.bytesToInt(dataValue);
        		water.setHeartTime(waterValue+"");
        		
//        		 2byte采集时间（分）
        		dataValue = new byte[2];
    			dataValue[1] = data[23];
    			dataValue[0] = data[22];
        		waterValue = IntegerTo16.bytesToInt(dataValue);
        		
        		water.setWaterTime(waterValue+"");
        		
        		
        		
        		mRePeaterData.setWater(water);
    			
    			break;
    		case 37://NB-水压水位阈值回复 0x25
//    			mRePeaterData.setCmd2((byte)0x25);
    			waterDev = data[7]&0xff; //0x03->(修改阈值成功)or 0x04->(修改阈值失败)
    			eletrcImei = new byte[8];
    			for(int i=0;i<8;i++){
    				eletrcImei[i] = data[8+i];
    			}
    			imeiStr = IntegerTo16.bytes2Hex(eletrcImei).substring(1);
    			if(waterDev==3){
    				Utils.objWater.remove(imeiStr);
    			}
    			
    			break;
    		case 34: //NB-水压水位报警 0x22
//    			报警	0x7e	0x1e	0x22	递增 1byte	递增 1byte	1byte	1byte	(设备类型水压0xaa，水位0xbb) mac(8byte)报警类型（1byte）（高压报警0xda,低压报警0xd1,低电压0xc3)报警数据（2 byte）		1byte	1byte	0x7f
    			mRePeaterData.setCmd2((byte)0x22);
    			water = new Water();
    			waterDev = data[7]&0xff; //设备类型//0xaa 水压 170 //0xBB 水位 187
    			alarmType = data[16]&0xff;
//    			高压报警0xda,低压报警0xd1,低电压0xc3
    			
    			
    			water.setAlarmType(alarmType);
    			eletrcImei = new byte[8];
    			for(int i=0;i<8;i++){
    				eletrcImei[i] = data[8+i];
    			}
    			imeiStr = IntegerTo16.bytes2Hex(eletrcImei).substring(1);
    			water.setWaterMac(imeiStr);
    			mRePeaterData.setRepeatMac(imeiStr);
    			dataValue = new byte[2];
    			dataValue[1] = data[18];
    			dataValue[0] = data[17];
        		waterValue = IntegerTo16.bytesToInt(dataValue);
        		if(waterDev == 170){	
        			water.setValue(waterValue+"");
    			}else if(waterDev == 187){		
    				if(alarmType == 218){
    					alarmType = 208;	//高水位
    				}else if(alarmType == 209){
    					alarmType = 207;	//水位低
    				}
    				water.setValue((float)waterValue/1000+"");
    			}
        		water.setWaterMac(imeiStr);
        		
        		mRePeaterData.setWater(water);
    			break;
    		case 33: //NB-水压水位心跳 0x21
//    			设备	心跳	0x7e	0x1e	0x21	递增 1byte	递增 1byte	1byte	1byte	(设备类型水压0xaa，水位0xbb) mac(8byte)+2byte(水压值Kpa) OR 2byte(水位值cm)    		1byte	1byte	0x7f
    			mRePeaterData.setCmd2((byte)0x21);
    			water = new Water();
    			waterDev = data[7]&0xff; //设备类型//0xaa 水压 170 //0xBB 水位 187
    			
    			
    			eletrcImei = new byte[8];
    			for(int i=0;i<8;i++){
    				eletrcImei[i] = data[8+i];
    			}
    			imeiStr = IntegerTo16.bytes2Hex(eletrcImei).substring(1);
    			
    			dataValue = new byte[2];
    			dataValue[1] = data[17];
    			dataValue[0] = data[16];
        		waterValue = IntegerTo16.bytesToInt(dataValue);
        		water.setWaterMac(imeiStr);
        		mRePeaterData.setRepeatMac(imeiStr);
        		if(waterDev == 170){	
        			water.setValue(waterValue+"");
    			}else if(waterDev == 187){		
    				water.setValue((float)waterValue/1000+"");
    			}
        		
        		mRePeaterData.setWater(water);
    			
    			break;
    		case 30://三相电气 透传数据
    			threeElec = new ThreePhaseElectricEntity();
    			mRePeaterData.setCmd2((byte)0x1E);	
    			eletrcImei = new byte[8];
    			for(int i=0;i<8;i++){
    				eletrcImei[i] = data[7+i];
    			}
    			imeiStr = IntegerTo16.bytes2Hex(eletrcImei).substring(1);	//IMEI(15)   7+15
    			threeElec.setImeiStr(imeiStr);
    			mRePeaterData.setRepeatMac(imeiStr);
    			eletrcImsi = new byte[8];
    			for(int i=0;i<8;i++){
    				eletrcImsi[i] = data[15+i];
    			}
    			imsiStr = IntegerTo16.bytes2Hex(eletrcImsi).substring(1);	//IMSI(15) 22+15
    			threeElec.setImsiStr(imsiStr);
    			
    			rssiVal = Utils.getrssi(data[24], data[23])+""; //RSSI(2) 38-37
    			threeElec.setRssiVal(rssiVal);
    			
    			float voltageA = ((float)(((data[26]&0x7f)<<8)+(data[25]&0xff)))/10;	//A相电压(2) 39-40
    			threeElec.setVoltageA(voltageA+"");
    			
    			float voltageB = ((float)(((data[28]&0x7f)<<8)+(data[27]&0xff)))/10;	//B相电压(2) 
    			threeElec.setVoltageB(voltageB+"");
    			
    			float voltageC = ((float)(((data[30]&0x7f)<<8)+(data[29]&0xff)))/10;	//C相电压(2) 
    			threeElec.setVoltageC(voltageC+"");
    			
    			float electricityA = ((float)(((data[32]&0x7f)<<8)+(data[31]&0xff)))/10;	//A相电流(2)
    			threeElec.setElectricityA(electricityA+"");
    			
    			float electricityB = ((float)(((data[34]&0x7f)<<8)+(data[33]&0xff)))/10;	//B相电流(2)
    			threeElec.setElectricityB(electricityB+"");
    			
    			float electricityC = ((float)(((data[36]&0x7f)<<8)+(data[35]&0xff)))/10;	//C相电流(2)
    			threeElec.setElectricityC(electricityC+"");
    			
    			float surplusElectri = ((float)(((data[38]&0x7f)<<8)+(data[37]&0xff)))/10; //剩余电流(2)漏电流
    			threeElec.setSurplusElectri(surplusElectri+"");
    			
    			int currentMaximum = data[39]&0xff; ////剩余电流最大相(1)
    			threeElec.setCurrentMaximum(currentMaximum+"");
    			
    			String runAlarmState = "0";//告警状态
    			String runGateState = "0";//闸位状态
    			
    			int runState = 0;	//运行状态1
    			
    			if((data[39]&0x80)==128){
    				runAlarmState = "1";
    			}
    			
    			if((data[39]&0x60)==96){
    				runGateState = "3";
    			}else if((data[39]&0x40)==64){
    				runGateState = "2";
    			}else if((data[39]&0x20)==32){
    				runGateState = "1";
    			}
    			
    			runState = data[39]&0x1f;
    			String runCauseState = runState + "";	//跳闸、告警原因： 运行状态字(1)
    			switch(runState){
    			case 19:	//10011-设置更改
    				break;
    			case 18:	//10010-手动
    				runCauseState = "161";
    				break;
    			case 17:	//10001-合闸失败
    				runCauseState = "48";
    				break;
    			case 16:	//10000-互感器故障
    				runCauseState = "75";
    				break;
    			case 15:	//01111-闭锁
    				runCauseState = "156";
    				break;
    			case 14:	//01110-按键试验
    				runCauseState = "159";
    				break;
    			case 13:	//01101-远程
    				runCauseState = "158";
    				break;
    			case 12:	//01100-定时试验
    				runCauseState = "157";
    				break;
    			case 11:	//01011-停电
    				runCauseState = "155";
    				break;
    			case 10:	//01010-接地
    				runCauseState = "154";
    				break;
    			case 9:	//01001-过压
    				runCauseState = "143";
    				break;
    			case 8:	//01000-欠压
    				runCauseState = "144";
    				break;
    			case 7:	//00111-缺相
    				runCauseState = "153";
    				break;
    			case 6:	//00110-短路
    				runCauseState = "49";
    				break;
    			case 5:	//00101-过载
    				runCauseState = "45";
    				break;
    			case 4:	//00100-缺零
    				runCauseState = "151";
    				break;
    			case 2:	//00010-剩余电流
    				runCauseState = "46";
    				break;
    			case 0:	//00000-正常
    				runCauseState = "0";
    				break;
    			}
    			threeElec.setRunCauseState(runCauseState);
    			threeElec.setRunGateState(runGateState);
    			
    			mRePeaterData.setThreeElectric(threeElec);
    			break;
    		case 31://三相电气阈值处理
    			ElectricThresholdBean thresholdBean = new ElectricThresholdBean();
    			threeElec = new ThreePhaseElectricEntity();
    			
    			mRePeaterData.setCmd2((byte)0x1F);	
    			eletrcImei = new byte[8];
    			for(int i=0;i<8;i++){
    				eletrcImei[i] = data[7+i];
    			}
    			imeiStr = IntegerTo16.bytes2Hex(eletrcImei).substring(1);	//IMEI(15)   7+8
    			threeElec.setImeiStr(imeiStr);
    			mRePeaterData.setRepeatMac(imeiStr);
    			eletrcImsi = new byte[8];
    			for(int i=0;i<8;i++){
    				eletrcImsi[i] = data[15+i];
    			}
    			imsiStr = IntegerTo16.bytes2Hex(eletrcImsi).substring(1);	//IMSI(15) 15+8
    			threeElec.setImsiStr(imsiStr);
    			
    			rssiVal = Utils.getrssi(data[24], data[23])+""; //RSSI(2) 24-23
    			threeElec.setRssiVal(rssiVal);
    			
    			float overpressure = ((float)(((data[26]&0x7f)<<8)+(data[25]&0xff)))/10;	//过压阈值
    			thresholdBean.setOverpressure(overpressure+"");
    			
    			float unpressure = ((float)(((data[28]&0x7f)<<8)+(data[27]&0xff)))/10;	//欠压阈值
    			thresholdBean.setUnpressure(unpressure+"");
    			
    			float overCurrent = ((float)(((data[30]&0x7f)<<8)+(data[29]&0xff)))/10;	 //过流阈值 
    			thresholdBean.setOverCurrent(overCurrent+"");
    			
    			float leakCurrent = ((float)(((data[32]&0x7f)<<8)+(data[31]&0xff)))/10;	 //剩余电流阈值
    			thresholdBean.setLeakCurrent(leakCurrent+"");
    			
    			threeElec.setElectricBean(thresholdBean);
    			mRePeaterData.setThreeElectric(threeElec);
    			break;
    		case 4:
    			mRePeaterData.setCmd2((byte)0x04);
    			int dataType=data[11]&0xff;	//数据类型
    			switch (dataType) {
	    			case 0x01://防爆燃气
	    				mRePeaterData.setDeviceType(72);
	    				ProofGasEntity gas = new ProofGasEntity();
						int proofGasType = data[14]&0xFF;//探测器类型	2byte
						switch(proofGasType){
						case 0:
							gas.setProofGasType("甲烷");
							break;
						case 1:
							gas.setProofGasType("氧气");
							break;
						case 2:
							gas.setProofGasType("H2");
							break;
						case 3:
							gas.setProofGasType("CO");
							break;
						case 4:
							gas.setProofGasType("H2S");
							break;
						case 5:
							gas.setProofGasType("NH3");
							break;
						case 6:
							gas.setProofGasType("CL2");
							break;
						case 7:
							gas.setProofGasType("NO");
							break;
						case 8:
							gas.setProofGasType("SO2");
							break;
						case 9:
							gas.setProofGasType("NO2");
							break;
						case 10:
							gas.setProofGasType("CO2");
							break;
						case 11:
							gas.setProofGasType("O3");
							break;
						case 12:
							gas.setProofGasType("ETO");
							break;
						case 13:
							gas.setProofGasType("HCN");
							break;
						case 14:
							gas.setProofGasType("PH");
							break;
						case 15:
							gas.setProofGasType("HCL");
							break;
						case 16:
							gas.setProofGasType("LPG");
							break;
						case 17:
							gas.setProofGasType("LNG");
							break;
						case 18:
							gas.setProofGasType("FLA");
							break;
						case 19:
							gas.setProofGasType("其它气体");
							break;
						case 20:
							gas.setProofGasType("GAS_HF");
							break;
						case 21:
							gas.setProofGasType("C6H6");
							break;
						case 22:
							gas.setProofGasType("VOC");
							break;
						case 23:
							gas.setProofGasType("C2H2");
							break;
						case 24:
							gas.setProofGasType("COCL");
							break;
						}
						
						int proofGasUnit_low = data[16]&0xFF;//单位	
						switch (proofGasUnit_low) {
						case 0:
							gas.setProofGasUnit("V/V");
							break;
						case 1:
							gas.setProofGasUnit("LEL");
							break;
						case 2:
							gas.setProofGasUnit("PPM");
							break;
						case 3:
							gas.setProofGasUnit("KPPM");
							break;
						case 4:
							gas.setProofGasUnit("Mg/M3");
							break;
						default:
							break;
						}
						
						int proofGasUnit_high = data[15]&0xFF;//数值倍率	
						
						byte[] gasliangcheng = new byte[2];
						gasliangcheng[1] = data[17];
						gasliangcheng[0] = data[18];
						int proofGasliangcheng = IntegerTo16.bytesToInt(gasliangcheng);//量程	2byte
						gas.setProofGasliangcheng(proofGasliangcheng+"");
						
						byte[] a1Alarm = new byte[2];
						a1Alarm[1] = data[19];
						a1Alarm[0] = data[20];
						int proofGasA1Alarm = IntegerTo16.bytesToInt(a1Alarm);//A1报警点	2byte
						gas.setProofGasA1Alarm(proofGasA1Alarm+"");
						
						byte[] a2Alarm = new byte[2];
						a2Alarm[1] = data[21];
						a2Alarm[0] = data[22];
						int proofGasA2Alarm = IntegerTo16.bytesToInt(a2Alarm);//A2报警点	2byte
						gas.setProofGasA2Alarm(proofGasA2Alarm+"");
						
						byte[] gasState = new byte[2];
						gasState[1] = data[23];
						gasState[0] = data[24];
						int proofGasState = IntegerTo16.bytesToInt(gasState);//探测器状态	2byte
						gas.setProofGasState(proofGasState+"");
						
						
						byte[] gasMmol = new byte[2];
						gasMmol[1] = data[27];
						gasMmol[0] = data[28];
						int proofGasMmol = IntegerTo16.bytesToInt(gasMmol);//气体浓度  2byte
						float gasValue=0.0f;
						switch (proofGasUnit_high) {
						case 0:
							gasValue=proofGasMmol*1.0f;
							break;
						case 1:
							gasValue=proofGasMmol*0.1f;
							break;
						case 2:
							gasValue=proofGasMmol*0.01f;
							break;
						case 3:
							gasValue=proofGasMmol*0.001f;
							break;
						default:
							break;
						}
						gas.setProofGasMmol(gasValue+"");
						
						byte[] gasTemp = new byte[2];
						gasTemp[1] = data[29];
						gasTemp[0] = data[30];
						int proofGasTemp = IntegerTo16.bytesToInt(gasTemp);//传感器温度  2byte
						gas.setProofGasTemp(proofGasTemp+"");
						gas.setProofGasMac(repeaterMac);
						mRePeaterData.setRepeatMac(repeaterMac);
						mRePeaterData.setGasEntity(gas);
	    				break;
					case 0x00://电表
						// data[15]-18 电量     19-22  年月日周   23-25 时分秒
						mRePeaterData.setDeviceType(88);
						double account=0.0;
						String time;
						StringBuffer timeBuffer = new StringBuffer();
						byte[] accountIntBytes=new byte[3];
						for(int i=0;i<3;i++){
							accountIntBytes[i]=data[15+i];
						}
						int accountInt=Integer.parseInt(IntegerTo16.bytes2Hex(accountIntBytes));					
						int accountFloat=IntegerTo16.byteToInt(data[18]);
						account=accountInt+accountFloat*0.01;
						String accountString=String.valueOf(account);
						
						if(data[19]<10)
							timeBuffer.append(0);
						timeBuffer.append(IntegerTo16.byteToInt(data[19]));
						timeBuffer.append("-");
						if(data[20]<10)
							timeBuffer.append(0);
						timeBuffer.append(IntegerTo16.byteToInt(data[20]));
						timeBuffer.append("-");
						if(data[21]<10)
							timeBuffer.append(0);
						timeBuffer.append(IntegerTo16.byteToInt(data[21]));
						timeBuffer.append(" ");
						if(data[23]<10)
							timeBuffer.append(0);
						timeBuffer.append(IntegerTo16.byteToInt(data[23]));
						timeBuffer.append(":");
						if(data[24]<10)
							timeBuffer.append(0);
						timeBuffer.append(IntegerTo16.byteToInt(data[24]));
						timeBuffer.append(":");
						if(data[25]<10)
							timeBuffer.append(0);
						timeBuffer.append(IntegerTo16.byteToInt(data[25]));	
						time=timeBuffer.toString();
						
						MeterReadingEntity mre = new MeterReadingEntity();
						mre.setMac(repeaterMac);
						mRePeaterData.setRepeatMac(repeaterMac);
						mre.setQuantity(accountString);
						mre.setTime(time);
						
						AllSmokeDao mAllSmokeDao=new AllSmokeDaoImpl();
						MeterInfoEntity mie=mAllSmokeDao.getMeterInfoByMac(repeaterMac);
						int ifV=mie.getIfSendVoltage();
						int ifE=mie.getIfSendElectricity();
						int ifP=mie.getIfSendPower();
						if(ifV<=0&&ifE<=0&&ifP>0){
							// 只发功率  001
							double power=0.0;
							byte[] powerBytes=new byte[3];
							for(int i=0;i<3;i++){
								powerBytes[i]=data[26+i];
							}
							power=Integer.parseInt(IntegerTo16.bytes2Hex(powerBytes))*0.0001;
							mre.setPower(String.valueOf(power));
						}
						else if(ifV<=0&&ifE>0&&ifP>0){
							// 只发电流  功率  011
							double electricity=0.0;
							double power=0.0;
							byte[] electricityBytes=new byte[3];
							byte[] powerBytes=new byte[3];					
							for(int i=0;i<3;i++){
								electricityBytes[i]=data[26+i];
							}
							for(int i=0;i<3;i++){
								powerBytes[i]=data[29+i];
							}
							electricity=Integer.parseInt(IntegerTo16.bytes2Hex(electricityBytes))*0.001;
							power=Integer.parseInt(IntegerTo16.bytes2Hex(powerBytes))*0.0001;
							mre.setElectricity(String.valueOf(electricity));
							mre.setPower(String.valueOf(power));						
						}
						else if(ifV<=0&&ifE>0&&ifP<=0){
							// 只发电流   010
							double electricity=0.0;
							byte[] electricityBytes=new byte[3];
							for(int i=0;i<3;i++){
								electricityBytes[i]=data[26+i];
							}
							electricity=Integer.parseInt(IntegerTo16.bytes2Hex(electricityBytes))*0.001;
							mre.setElectricity(String.valueOf(electricity));
						}
						else if(ifV>0&&ifE<=0&&ifP<=0){
							// 只发电压   100
							double voltage=0.0;
							byte[] voltageBytes=new byte[2];
							for(int i=0;i<2;i++){
								voltageBytes[i]=data[26+i];
							}
							voltage=Integer.parseInt(IntegerTo16.bytes2Hex(voltageBytes))*0.1;
							mre.setVoltage(String.valueOf(voltage));
						}
						else if(ifV>0&&ifE<=0&&ifP>0){
							// 只发电压 功率  101
							double voltage=0.0;
							double power=0.0;
							byte[] voltageBytes=new byte[2];
							byte[] powerBytes=new byte[3];
							for(int i=0;i<2;i++){
								voltageBytes[i]=data[26+i];
							}				
							for(int i=0;i<3;i++){
								powerBytes[i]=data[28+i];
							}
							voltage=Integer.parseInt(IntegerTo16.bytes2Hex(voltageBytes))*0.1;
							power=Integer.parseInt(IntegerTo16.bytes2Hex(powerBytes))*0.0001;
							mre.setPower(String.valueOf(power));
							mre.setVoltage(String.valueOf(voltage));
						}
						else if(ifV>0&&ifE>0&&ifP<=0){
							// 只发电压 电流  110
							double voltage=0.0;
							double electricity=0.0;
							byte[] voltageBytes=new byte[2];
							byte[] electricityBytes=new byte[3];
							for(int i=0;i<2;i++){
								voltageBytes[i]=data[26+i];
							}
							for(int i=0;i<3;i++){
								electricityBytes[i]=data[28+i];
							}						
							voltage=Integer.parseInt(IntegerTo16.bytes2Hex(voltageBytes))*0.1;
							electricity=Integer.parseInt(IntegerTo16.bytes2Hex(electricityBytes))*0.001;
							mre.setElectricity(String.valueOf(electricity));
							mre.setVoltage(String.valueOf(voltage));
	
						}
						else if(ifV>0&&ifE>0&&ifP>0){
							// 只发电压 电流 功率  111
							double voltage=0.0;
							double electricity=0.0;
							double power=0.0;
							byte[] voltageBytes=new byte[2];
							byte[] electricityBytes=new byte[3];
							byte[] powerBytes=new byte[3];
							for(int i=0;i<2;i++){
								voltageBytes[i]=data[26+i];
							}
							for(int i=0;i<3;i++){
								electricityBytes[i]=data[28+i];
							}
							for(int i=0;i<3;i++){
								powerBytes[i]=data[31+i];
							}
							voltage=Integer.parseInt(IntegerTo16.bytes2Hex(voltageBytes))*0.1;
							electricity=Integer.parseInt(IntegerTo16.bytes2Hex(electricityBytes))*0.001;
							power=Integer.parseInt(IntegerTo16.bytes2Hex(powerBytes))*0.0001;
							mre.setElectricity(String.valueOf(electricity));
							mre.setPower(String.valueOf(power));
							mre.setVoltage(String.valueOf(voltage));
						}
						mRePeaterData.setElectricMeter(mre);
						break;
					default:
						break;
				}
    			break;
    		default:
    			break;
    		}
        	return mRePeaterData;
    	}else{
    		return null;
    	}
    
	}
	
	public static GpsEntityBean isHeart2(byte[] dataN,int byteLen,String gpsIp) throws Exception {
		GpsEntityBean gpsBean = GpsEntityBean.newInstance();
		int cmd3 = dataN[2]&0xff;	//协议号
		switch(cmd3){
		case 1:		//登录数据包      ACK
			byte[] macByte = new byte[8];
    		for(int i=0;i<8;i++){
    			macByte[i] = dataN[7+i];
    		}
    		String gpsMac = IntegerTo16.bytes2Hex(macByte);
    		gpsMac = gpsMac.substring(1);
    		gpsBean.setDevMac(gpsMac);
    		Map<String,String> gMap = gpsBean.getGpsMap();
    		log.debug("gpsMac1="+gpsMac+"  gpsip="+gpsIp);
    		gMap.put(gpsMac, gpsIp);
			break;
		case 2:		//GPS数据包
		case 18:
			int gps11 = dataN[11]&0x01;
			if(gps11!=1){
				if((dataN[11]&0x02)==1){			//0000010  第一基站
					break;
				}else if((dataN[11]&0x04)==1){		//0000100 第二基站
					break;
				}else if((dataN[11]&0x08)==1){		//0001000 第三基站
					break;
				}else if((dataN[11]&0x10)==1){		//0010000 第一WIFF
					break;
				}else if((dataN[11]&0x20)==1){		//0100000 第二WIFF
					break;
				}else if((dataN[11]&0x40)==1){		//1000000 第三WIFF
					break;
				}else{
					break;
				}
			}
			String dateTime = GetTime.ConvertTimeByLong();
			Map<String,String> gMap2 = gpsBean.getGpsMap();
			String gpsMac2 = "";
			for(String key:gMap2.keySet()){
				if(gpsIp.equals(gMap2.get(key))){
					gpsMac2 = key;
					log.debug("gpsmac12="+gpsMac2+"   gpsip12="+gMap2.get(key));
				}
			}
			byte[] latiByte = new byte[4];
    		for(int i=0;i<4;i++){
    			latiByte[i] = dataN[12+i];
    		}
    		byte[] longiByte = new byte[4];
    		for(int i=0;i<4;i++){
    			longiByte[i] = dataN[16+i];
    		}
    		String longitude = IntegerTo16.bytes2Hex(longiByte);	//经度
    		String latitude = IntegerTo16.bytes2Hex(latiByte);		//纬度
    	
    		int lati = getLatiOrLong(dataN,latitude);
    		int logi = getLatiOrLong(dataN,longitude);
    		longitude = IntegerToLongitude(logi);
    		latitude = IntegerToLongitude(lati);
    		
    		log.debug("longitude="+longitude+"latitude="+latitude+"lati:"+lati+"   longitude="+longitude+" latitude="+latitude);
    		gpsBean.setLongitude(longitude);
    		gpsBean.setLatitude(latitude);
			gpsBean.setDataTime(dateTime);
			gpsBean.setGpsState(1);
			gpsBean.setDevMac(gpsMac2);
			break;
		case 3:		//心跳数据包   	ACK
			String dateTime3 = GetTime.ConvertTimeByLong();
			Map<String,String> gMap3 = gpsBean.getGpsMap();
			String gpsMac3 = "";
			for(String key:gMap3.keySet()){
				if(gpsIp.equals(gMap3.get(key))){
					gpsMac3 = key;
				}
			}
			byte gps3[] = new byte[7];
			for (int i = 0; i < gps3.length; i++) {
				gps3[i] = dataN[i];
			}
			gpsBean.setDataM(gps3);
			gpsBean.setDataTime(dateTime3);
			gpsBean.setGpsState(1);
			gpsBean.setDevMac(gpsMac3);
			break;
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
		case 25:
		case 26:
		case 27:
		case 128:
		case 129:
			String dateTime15 = GetTime.ConvertTimeByLong();
			Map<String,String> gMap15 = gpsBean.getGpsMap();
			String gpsMac15 = "";
			for(String key:gMap15.keySet()){
				if(gpsIp.equals(gMap15.get(key))){
					gpsMac15 = key;
				}
			}
			byte gps15[] = new byte[7];
			for (int i = 0; i < gps15.length; i++) {
				gps15[i] = dataN[i];
			}
			gpsBean.setDataM(gps15);
			gpsBean.setDataTime(dateTime15);
			gpsBean.setGpsState(1);
			gpsBean.setDevMac(gpsMac15);
			break;
		}
		
		return gpsBean;
	}
	
	public static int getLatiOrLong(byte[] dataN,String src){
		int data11 = dataN[11]&0x80;
		int result = 0;
		if(data11==8){
			int i11 = IntegerTo16.hexStringToAlgorism(src);
			double d11 = (double)i11 - 4294967296d;
			result = (int)d11;
		}else {
			result = IntegerTo16.hexStringToAlgorism(src);
		}
		log.debug("data11:"+data11+"   dataN11="+dataN[11]+"   result = "+result);
		return result;
	}
	
	private static boolean checkCrc(byte[] data,int dataLen){
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
		log.debug("CRC==========crcL:"+crcL+" crcH:"+crcH+" clientCrcL:"+clientCrcL+" clientCrcH:"+clientCrcH+"   dataLen="+dataLen);
		System.out.println("CRC==========crcL:"+crcL+" crcH:"+crcH+" clientCrcL:"+clientCrcL+" clientCrcH:"+clientCrcH+"   dataLen="+dataLen);
		if(crcL==clientCrcL&&crcH==clientCrcH){
			return true;
		}else{
			return false;
		}
	}
	
	public static byte[] ackToEvnClient(RePeaterData peaterData,byte ackType) {
		byte[] ackByte = new byte[26];
		byte[] crcByte = new byte[22];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=ackType;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		ackByte[5]=0x10;
		ackByte[6]=0x00;
		crcByte[0] = 0x0e;
		crcByte[1] = ackType;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = 0x10;
		crcByte[5] = 0x00;
		byte[] repmac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = repmac[i];
			crcByte[6+i] = repmac[i];
		}
		byte[] devmac = IntegerTo16.hexString2Bytes(peaterData.getElectricMac());
		for(int i=0;i<4;i++){
			ackByte[11+i] = devmac[i];
			crcByte[10+i] = devmac[i];
		}
		byte[] ackTime = GetTime.timeToByte();
		for (int i = 0; i <8; i++) {
			if(i==4){
				int i3 = Utils.getWeekOfDate();
				switch(i3){
				case 1:
					ackByte[15+i] = 0x01;
					crcByte[14+i] = 0x01;
					break;
				case 2:
					ackByte[15+i] = 0x02;
					crcByte[14+i] = 0x02;
					break;
				case 3:
					ackByte[15+i] = 0x03;
					crcByte[14+i] = 0x03;
					break;
				case 4:
					ackByte[15+i] = 0x04;
					crcByte[14+i] = 0x04;
					break;
				case 5:
					ackByte[15+i] = 0x05;
					crcByte[14+i] = 0x05;
					break;
				case 6:
					ackByte[15+i] = 0x06;
					crcByte[14+i] = 0x06;
					break;
				case 7:
					ackByte[15+i] = 0x07;
					crcByte[14+i] = 0x07;
					break;
				}
			}else if(i>4){
				ackByte[15+i] = ackTime[i-1];
				crcByte[14+i] = ackTime[i-1];
			}else{
				ackByte[15+i] = ackTime[i];
				crcByte[14+i] = ackTime[i];
			}
		}
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[23] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[24] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[25]=0x7f;
		return ackByte;
	}
	
	public static byte[] ackToClient(RePeaterData peaterData,byte ackType) {
		byte[] ackByte = new byte[14];
		byte[] crcByte = new byte[10];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=ackType;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		ackByte[5]=0x04;
		ackByte[6]=0x00;
		crcByte[0] = 0x0e;
		crcByte[1] = ackType;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = 0x04;
		crcByte[5] = 0x00;
		byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = mac[i];
			crcByte[6+i] = mac[i];
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[11] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[12] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[13]=0x7f;
		return ackByte;
	}
	
	/**
	 * @param ack 水压回复ack,通过手机设置波动阈值，和时间，来下发数据到硬件设备
	 * @return
	 */
	public static byte[] ackToClientByMobile(RePeaterData peaterData,byte ackType) {
		int dataLen = 12;
		int len = 10+dataLen;
		int ackLen = len-4;
		byte[] ackByte = new byte[len];
		byte[] crcByte = new byte[ackLen];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=ackType;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		byte[] dataLenByte = IntegerTo16.toByteArray(dataLen,2);
		ackByte[5] = dataLenByte[0];
		ackByte[6] = dataLenByte[1];
		crcByte[0] = 0x0e;
		crcByte[1] = ackType;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = dataLenByte[0];
		crcByte[5] = dataLenByte[1];
		byte[] repeatermac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = repeatermac[i];
			crcByte[6+i] = repeatermac[i];
		}
		byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getWaterMac());
		for(int i=0;i<4;i++){
			ackByte[11+i] = mac[i];
			crcByte[10+i] = mac[i];
		}
		Map<String,WaterAckEntity> controlDev = Utils.objWater;
		
		int waveValue = 0;	//波动阈值为0kp；
		int waveTime = 0;	//数据上报时间单位0分钟
		if(controlDev.get(peaterData.getWaterMac())!=null){
			waveValue = controlDev.get(peaterData.getWaterMac()).getWaveValue();
			waveTime = controlDev.get(peaterData.getWaterMac()).getAckTimes();
		}
		
		byte[] waveValueByte = IntegerTo16.toByteArray(waveValue,2);
		ackByte[15] = waveValueByte[0];
		ackByte[16] = waveValueByte[1];
		crcByte[14] = ackByte[15];
		crcByte[15] = ackByte[16];
		
		byte[] waveTimeByte = IntegerTo16.toByteArray(waveTime,2);
		ackByte[17] = waveTimeByte[0];
		ackByte[18] = waveTimeByte[1];
		crcByte[16] = ackByte[17];
		crcByte[17] = ackByte[18];
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[19] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[20] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[21]=0x7f;
		System.out.println("==========>>>>>>"+IntegerTo16.bytes2Hex(ackByte));
		System.out.println("==========>>>>>>"+IntegerTo16.bytes2Hex(crcByte));
		return ackByte;
	}
	
	public static byte[] ackToNB_IOTClient(RePeaterData peaterData,byte ackType) {
		byte[] ackByte = new byte[15];
		byte[] crcByte = new byte[11];
		ackByte[0]=0x7e;
		ackByte[1]=0x1e;
		ackByte[2]=ackType;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		ackByte[5]=0x05;
		ackByte[6]=0x00;
		crcByte[0] = 0x1e;
		crcByte[1] = ackType;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = 0x05;
		crcByte[5] = 0x00;
		byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getAlarmSmokeMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = mac[i];
			crcByte[6+i] = mac[i];
		}
		
		int devState = 0;
		if(Utils.AckEleMap.get(peaterData.getAlarmSmokeMac())!=null){
			devState = Utils.AckEleMap.get(peaterData.getAlarmSmokeMac());
		}
		ackByte[11]|=(byte)devState;
		crcByte[10]=ackByte[11];
		System.out.println("mac="+mac+"     devState="+devState);
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[12] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[13] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[14]=0x7f;
		return ackByte;
	}
	
	public static byte[] ackToNB_IOTClient(RePeaterData peaterData,byte ackType,byte ackCmd) {
		String devMac = peaterData.getRepeatMac();
		byte[] ackByte = null;
		byte[] crcByte = null; 
		byte[] twoByte = null;
		WaterAckEntity wae = Utils.objWater.get(devMac);
		if(wae != null){	//心跳回复
			ackByte = new byte[27];
			crcByte = new byte[23];
			ackCmd = (byte)0xd5;
		}else{		//通过心跳回复设置阈值功能
			ackByte = new byte[19];
			crcByte = new byte[15];
		}
		
		ackByte[0]=0x7e;
		ackByte[1]=0x1e;
		ackByte[2]=ackType;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		ackByte[5]=0x01;
		ackByte[6]=0x00;
		crcByte[0] = 0x1e;
		crcByte[1] = ackType;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = 0x09;
		crcByte[5] = 0x00;
		byte[] mac = IntegerTo16.hexString2Bytes("0"+devMac);
		for(int i=0;i<8;i++){
			ackByte[7+i] = mac[i];
			crcByte[6+i] = mac[i];
		}
		ackByte[15] = ackCmd;
		crcByte[14] = ackCmd;
		
		if(wae == null){
			String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
			ackByte[16] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
			ackByte[17] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
			ackByte[18]=0x7f;
		}else{
			twoByte = IntegerTo16.toByteArray(wae.getThreshold1(),2); //高压高位阈值
			ackByte[16] = twoByte[0];
			ackByte[17] = twoByte[1];
			crcByte[15] = ackByte[16];
			crcByte[16] = ackByte[17];
			
			twoByte = IntegerTo16.toByteArray(wae.getThreshold2(),2); //低压低位阈值
			ackByte[18] = twoByte[0];
			ackByte[19] = twoByte[1];
			crcByte[17] = ackByte[18];
			crcByte[18] = ackByte[19];
			
			twoByte = IntegerTo16.toByteArray(wae.getWaveValue(),2); //发送时间
			ackByte[20] = twoByte[0];
			ackByte[21] = twoByte[1];
			crcByte[19] = ackByte[20];
			crcByte[20] = ackByte[21];
			
			twoByte = IntegerTo16.toByteArray(wae.getAckTimes(),2); //采集时间
			ackByte[22] = twoByte[0];
			ackByte[23] = twoByte[1];
			crcByte[21] = ackByte[22];
			crcByte[22] = ackByte[23];
			
			String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
			ackByte[24] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
			ackByte[25] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
			ackByte[26]=0x7f;
		}
		
		
		return ackByte;
	}
	
	public static byte[] ackToClientTwo(RePeaterData peaterData,byte ackType) {
		byte[] ackByte = new byte[21];
		byte[] crcByte = new byte[17];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=ackType;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		ackByte[5]=0x0b;
		ackByte[6]=0x00;
		crcByte[0] = 0x0e;
		crcByte[1] = (byte) 0x99;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = 0x0b;
		crcByte[5] = 0x00;
		byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = mac[i];
			crcByte[6+i] = mac[i];
		}
		byte[] yearByte = GetTime.timeToByte();
		for(int i=0;i<7;i++){
			ackByte[11+i] = yearByte[i];
			crcByte[10+i] = yearByte[i];
		}
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[18] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[19] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[20]=0x7f;
		return ackByte;
	}
	
	//同步烟感与区域管理终端,如果fireMacList.size()>0,count=fireMacList.size()*4+2,如果fireMacList.size()=0，count=0
	public static byte[] synchronousFire(RePeaterData peaterData,List<String> fireMacList,int count){
		//同步数据库与redis离线列表
		try{
			Jedis jedis = RedisConnection.getJedis();
			if (jedis!=null) {
				String requestId = UUID.randomUUID().toString().replace("-", "");
				while(!RedisOps.tryGetDistributedLock(jedis, "L"+peaterData.getRepeatMac(), requestId, 10000)){
					try {
						Thread.currentThread().sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				RedisOps.setList(jedis,peaterData.getRepeatMac(), SmokeLineDaoImpl.getMacs(peaterData.getRepeatMac()));
				
				RedisOps.releaseDistributedLock(jedis, "L"+peaterData.getRepeatMac(), requestId);
				jedis.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		int dataLen = 4+count;
		int len = 10+dataLen;
		int ackLen = len-4;
		byte[] ackByte = new byte[len];
		byte[] crcByte = new byte[ackLen];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=0x03;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		byte[] dataLenByte = IntegerTo16.toByteArray(dataLen,2);
		ackByte[5] = dataLenByte[0];
		ackByte[6] = dataLenByte[1];
		crcByte[0] = 0x0e;
		crcByte[1] = 0x03;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = dataLenByte[0];
		crcByte[5] = dataLenByte[1];
		byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = mac[i];
			crcByte[6+i] = mac[i];
		}
		if(count==0){
			String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
			ackByte[11] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
			ackByte[12] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
			ackByte[13]=0x7f;
		}else{
			int num = fireMacList.size();
			byte[] numByte = IntegerTo16.toByteArray(num,2);
			ackByte[11] = numByte[0];
			ackByte[12] = numByte[1];
			crcByte[10] = numByte[0];
			crcByte[11] = numByte[1];
			for(int i=0;i<num;i++){
				byte[] fireMac= IntegerTo16.hexString2Bytes(fireMacList.get(i));
				for(int j=0;j<4;j++){
					ackByte[13+i*4+j] = fireMac[j];
					crcByte[12+i*4+j] = fireMac[j];
				}
			}
			String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
			ackByte[13+num*4] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
			ackByte[14+num*4] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
			ackByte[15+num*4]=0x7f;
		}
		return ackByte;
	}
	
	//同步烟感与区域管理终端,如果fireMacList.size()>0,count=fireMacList.size()*4+2,如果fireMacList.size()=0，count=0
		public static byte[] fireSynchronous(RePeaterData peaterData,List<String> fireMacList,int count){
			
			int dataLen = 4+count;
			int len = 10+dataLen;
			int ackLen = len-4;
			byte[] ackByte = new byte[len];
			byte[] crcByte = new byte[ackLen];
			ackByte[0]=0x7e;
			ackByte[1]=0x0e;
			ackByte[2]=0x30;
			ackByte[3]=peaterData.getSeqL();
			ackByte[4]=peaterData.getSeqH();
			byte[] dataLenByte = IntegerTo16.toByteArray(dataLen,2);
			ackByte[5] = dataLenByte[0];
			ackByte[6] = dataLenByte[1];
			crcByte[0] = 0x0e;
			crcByte[1] = 0x30;
			crcByte[2] = peaterData.getSeqL();
			crcByte[3] = peaterData.getSeqH();
			crcByte[4] = dataLenByte[0];
			crcByte[5] = dataLenByte[1];
			byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
			for(int i=0;i<4;i++){
				ackByte[7+i] = mac[i];
				crcByte[6+i] = mac[i];
			}
			if(count==0){
				String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
				ackByte[11] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
				ackByte[12] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
				ackByte[13]=0x7f;
			}else{
				int num = fireMacList.size();
				byte[] numByte = IntegerTo16.toByteArray(num,2);
				ackByte[11] = numByte[0];
				ackByte[12] = numByte[1];
				crcByte[10] = numByte[0];
				crcByte[11] = numByte[1];
				for(int i=0;i<num;i++){
					byte[] fireMac= IntegerTo16.hexString2Bytes(fireMacList.get(i));
					for(int j=0;j<4;j++){
						ackByte[13+i*4+j] = fireMac[j];
						crcByte[12+i*4+j] = fireMac[j];
					}
				}
				String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
				ackByte[13+num*4] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
				ackByte[14+num*4] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
				ackByte[15+num*4]=0x7f;
			}
			return ackByte;
		}
	
	/**
	 * @param peaterData
	 * @param 根据lora电气，设置4个阈值的ACK
	 * @return
	 */
	public static byte[] ackControlCvlsAction(RePeaterUoolEntity peaterData){
		int dataLen = 16;
		int len = 10+dataLen;
		int ackLen = len-4;
		byte[] ackByte = new byte[len];
		byte[] crcByte = new byte[ackLen];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=0x12;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		byte[] dataLenByte = IntegerTo16.toByteArray(dataLen,2);
		ackByte[5] = dataLenByte[0];
		ackByte[6] = dataLenByte[1];
		crcByte[0] = 0x0e;
		crcByte[1] = 0x12;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = dataLenByte[0];
		crcByte[5] = dataLenByte[1];
		byte[] repeateMac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = repeateMac[i];
			crcByte[6+i] = repeateMac[i];
		}
		byte[] devMac = IntegerTo16.hexString2Bytes(peaterData.getDevMac());
		for(int i=0;i<4;i++){
			ackByte[11+i] = devMac[i];
			crcByte[10+i] = devMac[i];
		}
		int Overvoltage = peaterData.getOvervoltage();
		byte[] numByte1 = IntegerTo16.toByteArray(Overvoltage,2);
		ackByte[15] = numByte1[0];
		ackByte[16] = numByte1[1];
		crcByte[14] = ackByte[15];
		crcByte[15] = ackByte[16];
		int Undervoltage = peaterData.getUndervoltage();
		byte[] numByte2 = IntegerTo16.toByteArray(Undervoltage,2);
		ackByte[17] = numByte2[0];
		ackByte[18] = numByte2[1];
		crcByte[16] = ackByte[17];
		crcByte[17] = ackByte[18];
		int Overcurrent = peaterData.getOvercurrent();
		byte[] numByte3 = IntegerTo16.toByteArray(Overcurrent,2);
		ackByte[19] = numByte3[0];
		ackByte[20] = numByte3[1];
		crcByte[18] = ackByte[19];
		crcByte[19] = ackByte[20];
		int Leakage = peaterData.getLeakage();
		byte[] numByte4 = IntegerTo16.toByteArray(Leakage,2);
		ackByte[21] = numByte4[0];
		ackByte[22] = numByte4[1];
		crcByte[20] = ackByte[21];
		crcByte[21] = ackByte[22];
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[23] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[24] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[25]=0x7f;
		return ackByte;
	}
	
	public static byte[] ackControlAction(RePeaterData peaterData,int count){
		int dataLen = 9;
		int len = 10+dataLen;
		int ackLen = len-4;
		byte[] ackByte = new byte[len];
		byte[] crcByte = new byte[ackLen];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=0x0A;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		byte[] dataLenByte = IntegerTo16.toByteArray(dataLen,2);
		ackByte[5] = dataLenByte[0];
		ackByte[6] = dataLenByte[1];
		crcByte[0] = 0x0e;
		crcByte[1] = 0x0A;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = dataLenByte[0];
		crcByte[5] = dataLenByte[1];
		byte[] repeateMac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = repeateMac[i];
			crcByte[6+i] = repeateMac[i];
		}
		byte[] eleMac = IntegerTo16.hexString2Bytes(peaterData.getElectricMac());
		for(int i=0;i<4;i++){
			ackByte[11+i] = eleMac[i];
			crcByte[10+i] = eleMac[i];
		}
		if(count==1){
			ackByte[15] = (byte)0x01;
		}else{
			ackByte[15] = (byte)0x00;
		}
		crcByte[14] = ackByte[15];
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[16] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[17] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[18]=0x7f;
		return ackByte;
	}
	
	//@@控制三相电表
	public static byte[] ackControlAction2(RePeaterData peaterData,int count){
		int dataLen = 9;
		int len = 10+dataLen;
		int ackLen = len-4;
		byte[] ackByte = new byte[len];
		byte[] crcByte = new byte[ackLen];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=(byte) 0xD0;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		byte[] dataLenByte = IntegerTo16.toByteArray(dataLen,2);
		ackByte[5] = dataLenByte[0];
		ackByte[6] = dataLenByte[1];
		crcByte[0] = 0x0e;
		crcByte[1] = (byte) 0xD0;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = dataLenByte[0];
		crcByte[5] = dataLenByte[1];
		byte[] repeateMac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = repeateMac[i];
			crcByte[6+i] = repeateMac[i];
		}
		byte[] eleMac = IntegerTo16.hexString2Bytes(peaterData.getElectricMac());
		for(int i=0;i<4;i++){
			ackByte[11+i] = eleMac[i];
			crcByte[10+i] = eleMac[i];
		}
		if(count==1){
			ackByte[15] = (byte)0x01;
		}else{
			ackByte[15] = (byte)0x00;
		}
		crcByte[14] = ackByte[15];
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[16] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[17] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[18]=0x7f;
		return ackByte;
	}
	
	
	//主机复位指令包 22121720
	public static byte[] ackResetRepeaterAction(RePeaterData peaterData){
		int dataLen = 4;
		int len = 10+dataLen;
		int ackLen = len-4;
		byte[] ackByte = new byte[len];
		byte[] crcByte = new byte[ackLen];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=0x10;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		byte[] dataLenByte = IntegerTo16.toByteArray(dataLen,2);
		ackByte[5] = dataLenByte[0];
		ackByte[6] = dataLenByte[1];
		crcByte[0] = 0x0e;
		crcByte[1] = 0x10;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = dataLenByte[0];
		crcByte[5] = dataLenByte[1];
		byte[] repeateMac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = repeateMac[i];
			crcByte[6+i] = repeateMac[i];
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[11] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[12] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[13]=0x7f;
		return ackByte;
	}
	
	public static byte[] ackAlarmPackageTwo(RePeaterData peaterData,byte[] phoneNumOne,byte[] phoneNumTwo,SmokeBean mSmokeBean,byte type){
		String address = null;
		address = mSmokeBean.getAddress();
		if(address==null){
			address = mSmokeBean.getPlaceeAddress();
		}
		if(address==null){
			address="";
		}
		
		byte[] addresssbyte = mSmokeBean.getAddressbyte(); //add by lzo at 2017-05-09
//		byte[] placeaddressbyte = mSmokeBean.getPlaceaddressbyte();//add by lzo at 2017-05-09
//		byte[] addressByte = address.getBytes();//update by lzo at 2017-05-09
		
		byte[] addressByte = addresssbyte;
//		int addressLen = addressByte.length+12; 
		int addressLen = addressByte.length+13;	//update by lzo at 2017-05-11 修改了之前的协议
		List<String> soundList = mSmokeBean.getList();
		int soundLen = soundList.size()*4+1;
//		int dataLen = 45+addressLen;
		int dataLen = 45+addressLen+soundLen;	//update by lzo at 2017-05-11 增加了新的协议
		byte[] ackByte = new byte[55+addressLen+soundLen];
		byte[] crcByte = new byte[51+addressLen+soundLen];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=type;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		ackByte[5]=new IntegerTo16().algorismToHEXString(dataLen);
		ackByte[6]=0x00;
		crcByte[0] = 0x0e;
		crcByte[1] = type;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = ackByte[5];
		crcByte[5] = 0x00;
		byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = mac[i];
			crcByte[6+i] = mac[i];
		}
		int len = phoneNumOne.length;
		int differenceOneLen = 20-len;
		if(differenceOneLen<=0){
			for(int i=0;i<20;i++){
				ackByte[11+i] = phoneNumOne[i];
				crcByte[10+i] = phoneNumOne[i];
			}
		}else{
			for(int i=0;i<len;i++){
				ackByte[11+i] = phoneNumOne[i];
				crcByte[10+i] = phoneNumOne[i];
			}
			for(int i=0;i<differenceOneLen;i++){
				ackByte[11+i+len] = -0;
				crcByte[10+i+len] = -0;
			}
		}
		int lenTwo = phoneNumTwo.length;
		int differenceTwoLen = 20-lenTwo;
		if(differenceTwoLen<=0){
			for(int i=0;i<20;i++){
				ackByte[31+i] = phoneNumTwo[i];
				crcByte[30+i] = phoneNumTwo[i];
			}
		}else{
			for(int i=0;i<lenTwo;i++){
				ackByte[31+i] = phoneNumTwo[i];
				crcByte[30+i] = phoneNumTwo[i];
			}
			for(int i=0;i<differenceTwoLen;i++){
				ackByte[31+i+lenTwo] = -0;
				crcByte[30+i+lenTwo] = -0;
			}
		}
		ackByte[51] = new IntegerTo16().algorismToHEXString(addressLen+soundLen);
		crcByte[50] = ackByte[51];
		ackByte[52] = new IntegerTo16().algorismToHEXString(mSmokeBean.getDeviceType());
		crcByte[51] = ackByte[52];
		byte[] devMac = IntegerTo16.hexString2Bytes(peaterData.getAlarmSmokeMac());
		for(int i=0;i<4;i++){
			ackByte[53+i] = devMac[i];
			crcByte[52+i] = devMac[i];
		}
		
		byte[] yearByte = GetTime.timeToByte();
		for(int i=0;i<7;i++){
			ackByte[57+i] = yearByte[i];
			crcByte[56+i] = yearByte[i];
		}
		
		/* update by lzo at 2017-5-11
		 * for(int i=0;i<(addressLen-12);i++){
			ackByte[64+i] = addressByte[i];
			crcByte[63+i] = addressByte[i];
		}*/
		//add begin start
		ackByte[64] = new IntegerTo16().algorismToHEXString(addressLen-13);
		crcByte[63] = ackByte[64];
		for(int i = 0;i<(addressLen-13);i++){
			ackByte[65+i] = addressByte[i];
			crcByte[64+i] = addressByte[i];
		}
		ackByte[52+addressLen] = new IntegerTo16().algorismToHEXString(mSmokeBean.getCount());
		crcByte[51+addressLen] = ackByte[52+addressLen];
		for(int i = 0;i<soundList.size();i++){
			byte[] sMac = IntegerTo16.hexString2Bytes(soundList.get(i));
			for(int j = 0;j<4;j++){
				ackByte[53+addressLen+i*4+j] = sMac[j];
				crcByte[52+addressLen+i*4+j] = sMac[j];
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[52+addressLen+soundLen] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[53+addressLen+soundLen] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[54+addressLen+soundLen] = 0x7f;
		/*String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[52+addressLen] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[53+addressLen] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[54+addressLen]=0x7f;*/
		return ackByte;
	}
	
	public static byte[] mobileDealAlarm(SmokeBean mSmokeBean,byte type){
		List<String> soundList = mSmokeBean.getList();
		int soundLen = soundList.size();
		int dataLen = soundLen*4;
		byte[] ackByte = new byte[15+dataLen];
		byte[] crcByte = new byte[11+dataLen];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=type;
		ackByte[3]=0x01;
		ackByte[4]=0x00;
		ackByte[5]=new IntegerTo16().algorismToHEXString(5+soundLen*4);
		ackByte[6]=0x00;
		crcByte[0] = 0x0e;
		crcByte[1] = type;
		crcByte[2] = ackByte[3];
		crcByte[3] = ackByte[4];
		crcByte[4] = ackByte[5];
		crcByte[5] = 0x00;
		byte[] mac = IntegerTo16.hexString2Bytes(mSmokeBean.getRepeater());
		for(int i=0;i<4;i++){
			ackByte[7+i] = mac[i];
			crcByte[6+i] = mac[i];
		}
		ackByte[11] = new IntegerTo16().algorismToHEXString(soundLen);
		crcByte[10] = ackByte[11];
		for(int i = 0;i<soundList.size();i++){
			byte[] sMac = IntegerTo16.hexString2Bytes(soundList.get(i));
			for(int j = 0;j<4;j++){
				ackByte[12+i*4+j] = sMac[j];
				crcByte[11+i*4+j] = sMac[j];
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[12+dataLen] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[13+dataLen] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[14+dataLen] = 0x7f;
		return ackByte;
	}
	
	public static byte[] ackAlarmPackage(RePeaterData peaterData,byte[] phoneNumOne,byte[] phoneNumTwo){
		byte[] ackByte = new byte[54];
		byte[] crcByte = new byte[50];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=0x05;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		ackByte[5]=0x2c;
		ackByte[6]=0x00;
		crcByte[0] = 0x0e;
		crcByte[1] = 0x05;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = 0x2c;
		crcByte[5] = 0x00;
		byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = mac[i];
			crcByte[6+i] = mac[i];
		}
		int len = phoneNumOne.length;
		int differenceOneLen = 20-len;
		if(differenceOneLen<=0){
			for(int i=0;i<20;i++){
				ackByte[11+i] = phoneNumOne[i];
				crcByte[10+i] = phoneNumOne[i];
			}
		}else{
			for(int i=0;i<len;i++){
				ackByte[11+i] = phoneNumOne[i];
				crcByte[10+i] = phoneNumOne[i];
			}
			for(int i=0;i<differenceOneLen;i++){
				ackByte[11+i+len] = -0;
				crcByte[10+i+len] = -0;
			}
		}
		int lenTwo = phoneNumTwo.length;
		int differenceTwoLen = 20-lenTwo;
		if(differenceTwoLen<=0){
			for(int i=0;i<20;i++){
				ackByte[31+i] = phoneNumTwo[i];
				crcByte[30+i] = phoneNumTwo[i];
			}
		}else{
			for(int i=0;i<lenTwo;i++){
				ackByte[31+i] = phoneNumTwo[i];
				crcByte[30+i] = phoneNumTwo[i];
			}
			for(int i=0;i<differenceTwoLen;i++){
				ackByte[31+i+lenTwo] = -0;
				crcByte[30+i+lenTwo] = -0;
			}
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[51] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[52] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[53]=0x7f;
		return ackByte;
	}
	
	//与电脑端通信
	public static SocketToPCEntity ackPackage(byte[] data,int byteLen){
		SocketToPCEntity stpe = null;
		switch (byteLen) {
		case 44:
			stpe = new SocketToPCEntity();
			byte[] mac= new byte[20];
			byte[] userId = new byte[20];
			for(int i=0;i<20;i++){
				mac[i] = data[3+i];
				userId[i] = data[23+i];
			}
			String macStr = new String(mac).toString().trim();
			String userIdStr = new String(userId).toString().trim();
			stpe.setPcMac(macStr);
			stpe.setUserId(userIdStr);
			break;
		case 54:
			stpe = new SocketToPCEntity();
			byte[] alarmSerialNumber= new byte[30];
			byte[] userID = new byte[20];
			for(int i=0;i<30;i++){
				alarmSerialNumber[i] = data[3+i];
			}
			for(int i=0;i<20;i++){
				userID[i] = data[33+i];
			}
			String alarmSerialNumberStr = IntegerTo16.bytes2Hex(alarmSerialNumber);
			String userIDStr = IntegerTo16.bytes2Hex(userID);
			stpe.setPcMac(alarmSerialNumberStr);
			stpe.setUserId(userIDStr);
			break;
		default:
			break;
		}
		return stpe;
	}
	
	public static byte[] filerByte(byte[] data,int len){
		String byteStr = JavaByteArrToHex.bytesToHex(data).toUpperCase();
		String ss1 = byteStr.substring(0,2);
		String ss2 = byteStr.substring(byteStr.length()-2);
		if(ss1.equalsIgnoreCase("7E")&&ss2.equalsIgnoreCase("7F")){
			return data;
		}else{
			if(!ss1.equalsIgnoreCase("7E")&&ss2.equalsIgnoreCase("7F")){
				int indexStart = byteStr.indexOf("7E");
				String subStr;
				byte[] dataByte = null;
				if(indexStart>=0){
					subStr = byteStr.substring(indexStart, byteStr.length());
					dataByte = IntegerTo16.hexString2Bytes(subStr);
				}
				return dataByte;
			}else if(ss1.equalsIgnoreCase("7E")&&!ss2.equalsIgnoreCase("7F")){
				int indexEnd = byteStr.indexOf("7F");
				String subStr;
				byte[] dataByte = null;
				if(indexEnd>0){
					subStr = byteStr.substring(0, indexEnd+2);
					dataByte = IntegerTo16.hexString2Bytes(subStr);
				}
				return dataByte;
			}else{
				int indexStart = byteStr.indexOf("7E");
				int indexEnd = byteStr.indexOf("7F");
				String subStr;
				byte[] dataByte = null;
				if(indexStart>=0&&indexEnd>0){
					subStr = byteStr.substring(indexStart, indexEnd+2);
					dataByte = IntegerTo16.hexString2Bytes(subStr);
				}
				return dataByte;
			}
			
		}
	}
	
	public static byte[] filerByte2(byte[] data,int len){
		byte start = data[0];
		byte end = data[len - 1];
		int start_index = -1;
		int end_index = -1;
		if(start == 126 && end == 127) {
			start_index = 0;
			end_index = len - 1;
		} else if (start == 126 && end !=  127){
			start_index = 0;
			for(int i = 1; i < len - 1; i++){
				if (data[i] == 127) {
					end_index = i;
					break;
				}
			}
		} else if (start != 126 && end == 127){
			end_index = len - 1;
			for(int i = 1; i < len - 1; i++){
				if (data[i] == 126) {
					start_index = i;
					break;
				}
			}
		} else if (start != 126 && end != 127) {
			for(int i = 1; i < len - 1; i++){
				if (start_index == -1) {
					if (data[i] == 126){
						start_index = i;
						continue;
					}
				} else {
					if (data[i] == 127){
						end_index = i;
						break;
					}
				}
			}
		}
		
		if(start_index >= 0 && end_index >= 0) 
			return Arrays.copyOfRange(data, start_index, end_index+1);
		return null;
	}
	
	public static byte[] faultByte(byte[] data,int len){
		String byteStr = JavaByteArrToHex.bytesToHex(data).toUpperCase();;
		String ss1 = byteStr.substring(0,4);
		String ss2 = byteStr.substring(byteStr.length()-4);
		if(ss1.equalsIgnoreCase("4040")&&ss2.equalsIgnoreCase("2323")){
			return data;
		}else{
			if(!ss1.equalsIgnoreCase("4040")&&ss2.equalsIgnoreCase("2323")){
				int indexStart = byteStr.indexOf("4040");
				String subStr;
				byte[] dataByte = null;
				if(indexStart>=0){
					subStr = byteStr.substring(indexStart, byteStr.length());
					dataByte = IntegerTo16.hexString2Bytes(subStr);
				}
				return dataByte;
			}else if(ss1.equalsIgnoreCase("4040")&&!ss2.equalsIgnoreCase("2323")){
				int indexEnd = byteStr.indexOf("2323");
				String subStr;
				byte[] dataByte = null;
				if(indexEnd>0){
					subStr = byteStr.substring(0, indexEnd+2);
					dataByte = IntegerTo16.hexString2Bytes(subStr);
				}
				return dataByte;
			}else{
				int indexStart = byteStr.indexOf("4040");
				int indexEnd = byteStr.indexOf("2323");
				String subStr;
				byte[] dataByte = null;
				if(indexStart>=0&&indexEnd>0){
					subStr = byteStr.substring(indexStart, indexEnd+2);
					dataByte = IntegerTo16.hexString2Bytes(subStr);
				}
				return dataByte;
			}
			
		}
	}
	
	public static byte[] dtuByte(byte[] data,int len) {
		String byteStr = JavaByteArrToHex.bytesToHex(data).toUpperCase();
		int indexStart = byteStr.indexOf("7E7E7E7E");
		if (indexStart == -1)
			return null;
		byte[] dataByte = null;
		byte[] msgLen = new byte[2];
		msgLen[1] = data[indexStart/2+38]; //低位在后
		msgLen[0] = data[indexStart/2+39]; //高位在前
		int msgLens = IntegerTo16.bytesToInt(msgLen);  //1 0
		String subStr = byteStr.substring(indexStart,indexStart+(40+msgLens)*2);
		dataByte = IntegerTo16.hexString2Bytes(subStr);
		return dataByte;
	}
	
	private static PrinterEntity getPrinterInfo(byte[] datas){//第39个字节开始
		PrinterEntity mPrinterEntity = new PrinterEntity();
		byte[] printerMacByte = new byte[8];
		for(int i=0;i<8;i++){
			printerMacByte[i] = datas[39+i];
		}
		String macByte = IntegerTo16.bytes2Hex(printerMacByte);
		String printerMac = IntegerTo16.asciiStringToString(macByte);
		mPrinterEntity.setFaultCode(printerMac);
//		String contentStr1 = IntegerTo16.bytes2Hex(datas).toUpperCase();
		//打印中文长度
		int len = datas.length-57;
		byte[] content = new byte[len];
		for(int i=0;i<len;i++){
			content[i] = datas[48+i];
		}
		String contentStr = IntegerTo16.bytes2Hex(content).toUpperCase();
		Pattern mpattern = Pattern.compile("1C26(.*?)1C2E");
		Matcher mmatcher = mpattern.matcher(contentStr);
		List<String> list = new ArrayList<String>();
		while(mmatcher.find()){
			list.add(mmatcher.group(1));
		}

		if(list.size()==3){
			String contentFirst = IntegerTo16.hexToStringGBK(list.get(0));//大18层18号  声光警报器
			String contentSecond = IntegerTo16.hexToStringGBK(list.get(1));//启动
			String contentThird = IntegerTo16.hexToStringGBK(list.get(2));//农信大厦大
			String[] strFirst = contentFirst.split("\r");
			String[] strSecond;
			String faultInfo;
			if(strFirst.length>1){
				strSecond = strFirst[1].replace("\r", "").split("  ");
				if(strSecond.length>1){
					faultInfo = strSecond[1]+strFirst[0];
					contentThird = contentThird+strSecond[0].replace("  ", "");
				}else{
					faultInfo = strSecond[0]+strFirst[0].replace("  ", "");
				}
			}else{
				contentFirst = contentFirst.replace("\r", "");
				strSecond = contentFirst.split("  ");
				if(strSecond.length>1){
					contentThird = contentThird+strSecond[0];
					faultInfo = strSecond[1].replace("  ", "");
				}else{
					faultInfo = strSecond[0].replace("  ", "");
				}
			}
			mPrinterEntity.setFaultInfo(faultInfo);
			mPrinterEntity.setFaultType(contentSecond);
			mPrinterEntity.setFaultDevDesc(contentThird);
		}
		
		return mPrinterEntity;
	}
	
	private static PrinterEntity getPrinterInfo2(byte[] datas){//第14个字节开始
		PrinterEntity mPrinterEntity = new PrinterEntity();
		Map<Integer,Integer> hMap = new HashMap<Integer,Integer>();
		int e13 = datas[13]&0xff;	//区号
		int e14 = datas[14]&0xff;	//起始位	
		int e17 = datas[17]&0xff; 	//获取数有用据长度
		int e40 = datas[40]&0xff;	//1表示开机包，0表示状态改变包
		if(e40 == 1){
			for (int i = 0; i < e17/2; i++) {
				int e1 = e14+i;
				int e2 = datas[19+2*i]&0xff;
				hMap.put(e1, e2);
			}
		}else if(e40 == 0){
			int e41 = datas[41]&0xff;		//需要更改状态的数量
			for(int i = 1;i<=e41;i++){
				int e1 = datas[41+i]&0xff;  //取更改状态的位置
				int e0 = (e1+1-e14)*2+17;
				int e2 = datas[e0]&0xff;
				hMap.put(e1, e2);
			}
		}
		mPrinterEntity.setOpenState(e40);
		mPrinterEntity.setHwMap(hMap);
		mPrinterEntity.setAreaCode(e13);
		
		return mPrinterEntity;
	}
	
	public static String IntegerToLongitude(int longidata){
		float d1 = (float)longidata/30000f;
		float d2 = d1/60f;
		return d2+"";
	}
	
	
	public static byte[] ackToDTU(byte[] byteData,byte cmd){ //固定模式 “+TOPSAIL 13 06 13 19 27 26(6位BCD码系统时间) ”
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		String acktime=sdf.format(new Date());
		int year = Integer.parseInt(acktime.substring(0, 1))*16+Integer.parseInt(acktime.substring(1, 2));
		int month = Integer.parseInt(acktime.substring(2, 3))*16+Integer.parseInt(acktime.substring(3, 4));
		int day=Integer.parseInt(acktime.substring(4, 5))*16+Integer.parseInt(acktime.substring(5, 6));
		int hour=Integer.parseInt(acktime.substring(6, 7))*16+Integer.parseInt(acktime.substring(7, 8));
		int minute=Integer.parseInt(acktime.substring(8, 9))*16+Integer.parseInt(acktime.substring(9, 10));
		int second =Integer.parseInt(acktime.substring(10, 11))*16+Integer.parseInt(acktime.substring(11, 12));
		
		//ack 2B 54 4F 50 53 41 49 4C 18 01 31 09 43 35 后6位表示时间
		byte[] ackByte={0x2B,0x54,0x4F,0x50,0x53,0x41,0x49,0x4C,(byte)year,(byte)month,
				(byte)day,(byte)hour,(byte)minute,(byte)second};
		//System.out.println(JavaByteArrToHex.bytesToHex(ackByte).toUpperCase());
		return ackByte;
	}
	
	
	public static DtuData getDataOfDTU(byte[] data,int len) {
		DtuData  dtu= new DtuData();
		byte[] iemi = new byte[16];
		for(int i = 0;i<16;i++) {
			iemi[i] = data[4+i];
		}
		String dtuId2 = IntegerTo16.bytes2Hex(iemi); 
		String dtuId = "";
		for (int i=0;i<dtuId2.length()/2;i++)
			dtuId+=dtuId2.charAt(2*i+1);
//		String dtuId = IntegerTo16.bytes2Hex(iemi); //DTU唯一标识
		dtu.setDtuId(dtuId);
		
		byte[] name = new byte[16];
		for(int i = 0;i<16;i++) {
			name[i] = data[20+i];
		}
		String dtuName2 = IntegerTo16.bytes2Hex(iemi); 
		String dtuName = "";
		for (int i=0;i<dtuName2.length()/2;i++)
			dtuName+=dtuName2.charAt(2*i+1);
//		String dtuName = IntegerTo16.bytes2Hex(name); //DTU别名
		dtu.setDtuName(dtuName);
		
		int msgType = data[36]&0xff; //DTU包类型
		dtu.setMsgType(msgType);
		
		byte[] msgLen = new byte[2];
		msgLen[1] = data[38]; //低位在后
		msgLen[0] = data[39]; //高位在前
		int msgLens = IntegerTo16.bytesToInt(msgLen); //数据部分长度（byte 个数）
		System.out.println("msgLens:"+msgLens);
		dtu.setMsgLens(msgLens);
		
		switch (msgType) {  //目前只用01（注册），09（数据）
		case 1: //注册 ack 01  获取连接存入内存
			byte[] heartbeat = new byte[2];
			heartbeat[0]=data[41];
			heartbeat[1]=data[40];
			int heartbeatRate=IntegerTo16.bytesToInt(heartbeat);   //心跳频率 s
			dtu.setHeartbeatRate(heartbeatRate);
    		break;
    	case 2: //心跳 ack 02 发起心跳收到回应后更新dtu的在线状态,需要一个定时器向dtu设备发送心跳。
    		break;
    	case 3: //数据 不用回复 存储上传的数据
    		break;
    	case 5: //AT命令
    		break;
    	case 6: //点对点配置信息 ack 06
    		break;
    	case  7: //dtu数据采集间隔 ack 07
    		break;
    	case  8: //dtu每次发送的数据包数目 ack 08
    		break;
    	case 9: //智能表上传数据消息
    		byte[] msg2 = new byte[msgLens];
    		for (int i=0;i<msgLens;i++ ){
    			msg2[i] = data[40+i];
    		}
    		String msgStr2 = JavaByteArrToHex.bytesToHex(msg2).toUpperCase(); //将数据段的转化为str
    		System.out.println("msgStr2:"+msgStr2);
    		String time2 = msgStr2.substring(0,2)+" "+msgStr2.substring(2,4)+" "+msgStr2.substring(4,6)+" "
    				+msgStr2.substring(6,8)+" "+msgStr2.substring(8,10)+" "+msgStr2.substring(10,12);
    		dtu.setTime(time2);  //数据上传时间
    		
    		int interval2 = data[46]&0xff; //采集时间间隔
    		dtu.setInterval(interval2);
    		
    		int leftEnergy2 = Integer.parseInt(msgStr2.substring(14,16)); //剩余电量
    		dtu.setLeftEnergy(leftEnergy2);
    		
    		int strong2 =data[48]&0xff; //0x31-0x35 信号强度递增 9
    		dtu.setStrong(strong2);
    		
    		int unit =Integer.parseInt(msgStr2.substring(21,22)); //单位
    		
    		//获取设备的高低阈值，用来判断设备的历史状态
			WaterInfoDao waterDao = new WaterInfoDaoImpl();
			String waterlow = waterDao.getWaterLow(dtuId);
			String waterhigh = waterDao.getWaterHigh(dtuId);
//			if(unit==5){ //水位设备的阈值
//				waterlow = waterDao.getWaterLow(dtuId);
//				waterhigh = waterDao.getWaterHigh(dtuId);
//			}
//			else { //水压设备阈值
//				waterlow = waterDao.getWaterLow2(dtuId);
//				waterhigh = waterDao.getWaterHigh2(dtuId);
//			}
    		
    		List<DtuDataGroup> lstdg2 =  new ArrayList<DtuDataGroup> (); //数据组集合
    		for (int i=0;i<msgLens*2 - 20; i+=8) { //处理数据组
    			DtuDataGroup ddg = new DtuDataGroup();
//	    		int unit =Integer.parseInt(msgStr2.substring(21+i,22+i)); //单位
	    		ddg.setUnit(unit);
//	    		int state = Integer.parseInt(msgStr2.substring(20+i,21+i));  //0表示数据正常，1表示低压超限，2表示高压超限，3故障
//	    		ddg.setState(state);
	    		
	    		int pointNum = Integer.parseInt(msgStr2.substring(22+i,23+i)); //小数点后的位数
	    		int sign  = 0; //默认为正
	    		int num = 0;
	    		float value = 0; //数值。
	    		if (unit == 4){ //4为温度，23为符号为，0为正，1为负
	    			sign = Integer.parseInt(msgStr2.substring(23+i,24+i));
	    			num = Integer.parseInt(msgStr2.substring(24+i,28+i));  //数值
	    		} else {
	    			num = Integer.parseInt(msgStr2.substring(23+i,28+i));
	    		}
	    		
	    		switch(unit){  //若是水压，统一Kpa为单位
	    		case  1: //Mpa ->1000Kpa
	    			value = (float) (num / Math.pow(10, pointNum-3));
	    			break;
	    		case 2://bar->100Kpa 101
	    			value = (float) (num * 101 / Math.pow(10, pointNum));
	    			break;
	    		default://水压Kpa,水位m
	    			value = (float) (num / Math.pow(10, pointNum));
	    			break;
	    		}
				if (sign == 1) {
					value = -1*value;
				} 
				ddg.setValue(value);
				
				int state = 0;  //默认正常0
    			if(waterlow != null && value < Float.parseFloat(waterlow)){ //小于阈值
    				if(unit == 5)
    					state = 207;
    				else 
    					state = 209;
    			}else if(waterhigh != null && value >  Float.parseFloat(waterhigh)){ //大于阈值
    				if(unit == 5)
    					state = 208;
    				else 
    					state = 218;
    			}
    			ddg.setState(state);
				
				lstdg2.add(ddg);
    		}
    		dtu.setLstdg(lstdg2);
    		break;
    	} 
		return dtu;
	}


	public static int ChuangAnseq=0;
	public static byte[] getChuangAnDataAction(String smokeMac) {
		byte[] ackByte = new byte[12];
		byte[] crcByte = new byte[10];
		ackByte[0]=(byte) 0xED;
		crcByte[0] = (byte) 0xED;
		byte[] mac = IntegerTo16.hexString2Bytes(smokeMac);
		for(int i=0;i<6;i++){
			ackByte[1+i] = mac[i];
			crcByte[1+i] = mac[i];
		}
		ackByte[7]=(byte) ChuangAnseq;
		ackByte[8]=0x00;
		ackByte[9]=0x0A;
		crcByte[7] = (byte) ChuangAnseq;
		crcByte[8] = 0x00;
		crcByte[9] = 0x0A;
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[10] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[11] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ChuangAnseq++;
		return ackByte;
	}
	
	public static int getWaterAlarmId(int waterValue, DeviceAlarmEntity dae){ //根据水压值 与 水压设备的高低水压阈值以及最近一次的水压值比较得出设备的报警类型
		int alarmId = 219; //默认219正常。
		if(waterValue>dae.getAlarmthreshold1()){	//高水压
			alarmId = 218;
		}else if(waterValue<dae.getAlarmthreshold2()){	//低水压
			alarmId = 209;
		}else if(waterValue<dae.getCurrentValue()){	//降低
			alarmId = 210;
		}else if(waterValue>dae.getCurrentValue() && dae.getCurrentValue()!=-1){	//升高
			alarmId = 217;
		}
		return alarmId;
	}
	
	/**
	 *回复双向温湿度设备的心跳，报警，低电压
	 * @param peaterData
	 * @param ackType  
	 * @return
	 */
	public static byte[] ackToWaterGage(RePeaterData peaterData,byte ackType) {
		int dataLen = 9;
		int len = 10+dataLen;
		int ackLen = len-4;
		byte[] ackByte = new byte[len];
		byte[] crcByte = new byte[ackLen];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=ackType;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		byte[] dataLenByte = IntegerTo16.toByteArray(dataLen,2);
		ackByte[5] = dataLenByte[0];
		ackByte[6] = dataLenByte[1];
		crcByte[0] = 0x0e;
		crcByte[1] = ackType;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = dataLenByte[0];
		crcByte[5] = dataLenByte[1];
		byte[] repeatermac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = repeatermac[i];
			crcByte[6+i] = repeatermac[i];
		}
		
		byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getWaterMac());
		for(int i=0;i<4;i++){
			ackByte[11+i] = mac[i];
			crcByte[10+i] = mac[i];
		}
		
		ackByte[15] = 0x01;
		crcByte[14] = 0x01;
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[16] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[17] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[18]=0x7f;
		return ackByte;
	}
	
	public static byte[] ackTo4GAlarm(byte[] data){
		byte[] src = {03};
		int count = Utils.getByteCountOf(data, src);
		byte[]	ackData = new byte[count+data.length];
		int j = 0;
		for(int i = 0;i<data.length;i++){
			if(data[i]!=src[0]){
				ackData[j] = data[i];
				j++;
			}else{
				ackData[j] = (byte)0xef;
				j++;
				ackData[j] = (byte)0xe3;
				j++;
			}
		}
		
		byte[] src2 = {0x7f};
		int count2 = Utils.getByteCountOf(ackData, src2)-1;
		byte[]	ackData2 = new byte[count2+ackData.length];
		j = 0;
		for(int i = 0;i<ackData.length;i++){
			if((ackData[i]==src2[0])&&(i!=ackData.length-1)){
				ackData2[j] = (byte)0xef;
				j++;
				ackData2[j] = (byte)0xe7;
				j++;
			}else{
				ackData2[j] = ackData[i];
				j++;
				
			}
		}
		return ackData2;
	}
	
	/**
	 * 主动下发采集时间间隔，上报时间间隔,高阈值，低阈值,高阈值2，低阈值2；
	 * @param peaterData
	 * @param ackType
	 * @param watervalue
	 * @param watertime
	 * @return
	 */
	public static byte[] ackToWaterGage4(RePeaterData peaterData,byte ackType,int interval2,int interval1,int highGage,int lowGage,int highGage2,int lowGage2) {
		int dataLen = 20;
		int len = 10+dataLen;
		int ackLen = len-4;
		byte[] ackByte = new byte[len];
		byte[] crcByte = new byte[ackLen];
		ackByte[0]=0x7e;
		ackByte[1]=0x0e;
		ackByte[2]=ackType;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		byte[] dataLenByte = IntegerTo16.toByteArray(dataLen,2);
		ackByte[5] = dataLenByte[0];
		ackByte[6] = dataLenByte[1];
		crcByte[0] = 0x0e;
		crcByte[1] = ackType;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = dataLenByte[0];
		crcByte[5] = dataLenByte[1];
		byte[] repeatermac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = repeatermac[i];
			crcByte[6+i] = repeatermac[i];
		}
	
		byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getWaterMac());
		for(int i=0;i<4;i++){
			ackByte[11+i] = mac[i];
			crcByte[10+i] = mac[i];
		}
		
		byte[] interval2Byte = IntegerTo16.toByteArray(interval2,2);
		ackByte[15] = interval2Byte[0];
		ackByte[16] = interval2Byte[1];
		crcByte[14] = ackByte[15];
		crcByte[15] = ackByte[16];
		
		byte[] interval1Byte = IntegerTo16.toByteArray(interval1,2);
		ackByte[17] = interval1Byte[0];
		ackByte[18] = interval1Byte[1];
		crcByte[16] = ackByte[17];
		crcByte[17] = ackByte[18];
		
		byte[] highGageByte = IntegerTo16.toByteArray(highGage, 2);
		ackByte[19] = highGageByte[0];
		ackByte[20] = highGageByte[1];
		crcByte[18] = ackByte[19];
		crcByte[19] = ackByte[20];
		
		byte[] lowGageByte = IntegerTo16.toByteArray(lowGage, 2);
		ackByte[21] = lowGageByte[0];
		ackByte[22] = lowGageByte[1];
		crcByte[20] = ackByte[21];
		crcByte[21] = ackByte[22];
		
		byte[] highGageByte2 = IntegerTo16.toByteArray(highGage2, 2);
		ackByte[23] = highGageByte2[0];
		ackByte[24] = highGageByte2[1];
		crcByte[22] = ackByte[23];
		crcByte[23] = ackByte[24];
		
		byte[] lowGageByte2 = IntegerTo16.toByteArray(lowGage2, 2);
		ackByte[25] = lowGageByte2[0];
		ackByte[26] = lowGageByte2[1];
		crcByte[24] = ackByte[25];
		crcByte[25] = ackByte[26];
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[27] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[28] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[29]=0x7f;
		System.out.println("==========>>>>>>"+IntegerTo16.bytes2Hex(ackByte));
		System.out.println("==========>>>>>>"+IntegerTo16.bytes2Hex(crcByte));
		return ackByte;
	}
}
