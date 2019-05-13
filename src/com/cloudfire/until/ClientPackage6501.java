package com.cloudfire.until;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.impl.AllSmokeDaoImpl;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.ThreePhaseElectricEntity;
import com.cloudfire.entity.ZTWObjectEntity;
import com.cloudfire.entity.meter.MeterInfoEntity;
import com.cloudfire.entity.meter.MeterReadingEntity;

public class ClientPackage6501 {
	private final static Log log = LogFactory.getLog(ClientPackage6501.class);
	
	public static RePeaterData isHeart(byte[] dataN,int byteLen) throws Exception {
		byte[] data = dataN;
		int byteCount =data.length;
    	boolean crcRight = checkCrc(data,byteCount);  //校验
    	byte[] dataValue;
    	String dataString = "";
    	if(crcRight){
    		int msg0 = data[2]&0xff;	//30--0--48
    		if(msg0!=49){
    			return null;
    		}
    		RePeaterData mRePeaterData = new RePeaterData();
    		mRePeaterData.setSeqL(data[3]);
    		mRePeaterData.setSeqH(data[4]);
    		String devMac;
    		int mrsg = data[4]&0xff;
    		int msg = data[5]&0xff;	//30--0--48
    		int cmd18 = data[30]&0xff;	//
			int cmd19 = data[31]&0xff;	//
			byte[] macByte;
    		log.debug("msg================================"+msg);
        	switch (msg) {
        	case 57:
        		macByte = new byte[15];
        		for(int i=0;i<15;i++){
//        			System.out.println(data[14+i]);
        			macByte[i] = data[14+i];
        		}
//        		devMac = (IntegerTo16.unicode2Hex(macByte)).substring(0, 15);
        		devMac = IntegerTo16.unicode2Hex(macByte);
        		mRePeaterData.setRepeatMac(devMac);
        		mRePeaterData.setCmd2((byte)0x09);	//报警
    			mRePeaterData.setAlarmSmokeMac(devMac);
    			ZTWObjectEntity objs = null;
//    			恢复类型（1byte）（报警0xca，模块低电压0xc1，防拆0xc2，烟感低电压0xc3）
    			cmd18 = data[30]&0xff;	//
    			cmd19 = data[31]&0xff;	//
    			if(cmd18==67){	//43
    				switch(cmd19){
    				case 65:	//报警恢复202 CA 43-41
    					mRePeaterData.setRepeaterState(102);
    					break;
    				case 49:	//模块低电压恢复	C1 43-31
    					mRePeaterData.setRepeaterState(194);
    					break;
    				case 50:	//防拆恢复		C2	43-32
    					mRePeaterData.setRepeaterState(69);
    					break;	
    				case 51:	//烟感低电压恢复	C3	43-33
    					mRePeaterData.setRepeaterState(194);
    					break;
    				}
    			}else{
    				cmd18 = data[byteLen-12]&0xff;
    				cmd19 = data[byteLen-11]&0xff;
    				if(cmd18==67){	//43
        				switch(cmd19){
        				case 65:	//报警202恢复 CA 43-41
        					mRePeaterData.setRepeaterState(102);
        					break;
        				case 49:	//模块低电压恢复	C1 43-31
        					mRePeaterData.setRepeaterState(194);
        					break;
        				case 50:	//防拆恢复		C2	43-32
        					mRePeaterData.setRepeaterState(69);
        					break;	
        				case 51:	//烟感低电压恢复	C3	43-33
        					mRePeaterData.setRepeaterState(194);
        					break;
        				}
        			}
    			}
    			Map<String,Integer> AckEleMap = Utils.AckEleMap;
    			if(AckEleMap.get(devMac)==null){
    				mRePeaterData.setDeviceType(0);
    			}else{
    				mRePeaterData.setDeviceType(AckEleMap.get(devMac));
    			}
    			
        		break;
    		case 49:   //设备心跳===1  0x31
    			if(mrsg == 48){
    				macByte = new byte[15];
            		for(int i=0;i<15;i++){
            			System.out.println(data[14+i]);
            			macByte[i] = data[14+i];
            		}
            		devMac = IntegerTo16.unicode2Hex(macByte);
	    			mRePeaterData.setCmd2((byte)0x01);
	    			mRePeaterData.setAlarmSmokeMac(devMac);
	    			break;
	    		}else if(mrsg == 51){//令甲的喷淋复合协议
	    			objs = new ZTWObjectEntity();
	    			mRePeaterData.setCmd2((byte)0x0A);
//	    			mRePeaterData.setAlarmSmokeMac(devMac);
	    			dataValue = new byte[4]; //数据长度
	    			for(int i = 0;i<4;i++){
	    				dataValue[i] = data[10+i];
	    			}
	    			dataString = IntegerTo16.unicode2Hex(dataValue);
	    			int sums = Integer.parseInt(dataString, 16)-2;//数据长度
	    			int dataInt_T = 0;
	    			int dataInt_L = 0;
	    			int dataInt_V = 0;
	    			String dataStr_T = "";
	    			String dataStr_L = "";
	    			String dataStr_V = "";
	    			int step = 0;//负责循环阶梯
	    			while(sums>0){
	    				dataValue = new byte[4]; //T
		    			for(int i = 0;i<4;i++){
		    				dataValue[i] = data[14+i+step];
		    			}
		    			dataStr_T = IntegerTo16.unicode2Hex(dataValue);
		    			dataInt_T = IntegerTo16.parseInt(dataStr_T);//T值
		    			sums = sums - 2;
		    			step = step + 4;
		    			
		    			dataValue = new byte[4]; //L值
		    			for(int i = 0;i<4;i++){
		    				dataValue[i] = data[14+i+step];
		    			}
		    			dataStr_L = IntegerTo16.unicode2Hex(dataValue);
		    			dataInt_L = IntegerTo16.parseInt(dataStr_L);//L值
		    			
		    			sums = sums - 2;
		    			step = step + 4;
		    			
		    			
		    			dataValue = new byte[dataInt_L*2]; //V
		    			for(int i = 0;i<dataInt_L*2;i++){
		    				dataValue[i] = data[14+i+step];
		    			}
		    			dataStr_V = IntegerTo16.unicode2Hex(dataValue);
		    			dataInt_V = IntegerTo16.parseInt(dataStr_V);//V值
		    			
		    			sums = sums - dataInt_L;
		    			step = step + dataInt_L*2;
//		    			dataStr_T = dataStr_T.toUpperCase();
	    				switch(dataStr_T){
	    				case "1001"://0x1001 设备类型处理
	    					objs.setDevType_T(dataStr_T);
	    					objs.setDevType_V(dataStr_V);
	    					objs.setDevType_L(dataStr_L);
	    					break;
	    				case "1002"://0x1002 设备IMEI
	    					objs.setDevImei_L(dataStr_L);
	    					objs.setDevImei_T(dataStr_T);
	    					objs.setDevImei_V(dataStr_V.substring(0, 15));
	    					break;
	    				case "1003"://0x1003设备IMSI
	    					objs.setDevImsi_L(dataStr_L);
	    					objs.setDevImsi_T(dataStr_T);
	    					objs.setDevImsi_V(dataStr_V.substring(0, 15));
	    					break;
	    				case "1004"://0x1004信号强度
	    					objs.setSignal_T(dataStr_T);
	    					objs.setSignal_L(dataStr_L);
	    					objs.setSignal_V(dataInt_V+"");
	    					break;
	    				case "1005"://0x1005电量
	    					objs.setBattery_T(dataStr_T);
	    					objs.setBattery_L(dataStr_L);
	    					objs.setBattery_V(dataInt_V+"");
	    					break;
	    				case "1006"://0x1006设备状态
	    					objs.setDevState_L(dataStr_L);
	    					objs.setDevState_T(dataStr_T);
	    					objs.setDevState_V(dataStr_V);
	    					break;
	    				case "2001"://0x2001采样频率 （秒）
	    					objs.setSampling_T(dataStr_T);
	    					objs.setSampling_L(dataStr_L);
	    					objs.setSampling_V(dataStr_V);
	    					break;
	    				case "2002"://0x2002上报频率
	    					objs.setReport_T(dataStr_T);
	    					objs.setReport_L(dataStr_L);
	    					objs.setReport_V(dataStr_V);
	    					break;
	    				case "2003"://0x2003Iccid--卡号
	    					objs.setIccid_T(dataStr_T);
	    					objs.setIccid_T(dataStr_T);
	    					objs.setIccid_T(dataStr_T);
	    					break;
	    				case "2004"://0x2004muc软件版本
	    					objs.setMUCVersion_T(dataStr_T);
	    					objs.setMUCVersion_L(dataStr_L);
	    					objs.setMUCVersion_V(dataStr_V);
	    					break;
	    				case "2005"://0x2005NB固件版本
	    					objs.setNBfirmware_T(dataStr_T);
	    					objs.setNBfirmware_L(dataStr_L);
	    					objs.setNBfirmware_V(dataStr_V);
	    					break;
	    				case "2006"://0x2006 小区ID
	    					objs.setEstate_T(dataStr_T);
	    					objs.setEstate_L(dataStr_L);
	    					objs.setEstate_V(dataStr_V);
	    					break;
	    				case "2007"://0x2007物理小区
	    					objs.setPhysics_T(dataStr_T);
	    					objs.setPhysics_L(dataStr_L);
	    					objs.setPhysics_V(dataStr_V);
	    					break;
	    				case "2008"://0x2008//接收信号
	    					objs.setReceiptSignal_T(dataStr_T);
	    					objs.setReceiptSignal_L(dataStr_L);
	    					objs.setReceiptSignal_V(dataInt_V+"");
	    					break;
	    				case "2009"://0x2009信噪比
	    					objs.setSignal_noise_T(dataStr_T);
	    					objs.setSignal_noise_L(dataStr_L);
	    					objs.setSignal_noise_V(dataStr_V);
	    					break;
	    				case "200A"://0x200a覆盖等级
	    					objs.setCoverageLeve_T(dataStr_T);
	    					objs.setCoverageLeve_L(dataStr_L);
	    					objs.setCoverageLeve_V(dataInt_V+"");
	    					break;
	    				case "200B"://0x200bEARFCN
	    					objs.setEARFCN_T(dataStr_T);
	    					objs.setEARFCN_L(dataStr_L);
	    					objs.setEARFCN_V(dataStr_V);
	    					break;
	    				case "200C"://0x200C检测频率
	    					objs.setTest_frequency_T(dataStr_T);
	    					objs.setTest_frequency_L(dataStr_L);
	    					objs.setTest_frequency_V(dataStr_V);
	    					break;
	    				case "200D"://0x200D电量阈值
	    					objs.setPowerThreshold_T(dataStr_T);
	    					objs.setPowerThreshold_L(dataStr_L);
	    					objs.setPowerThreshold_V(dataStr_V);
	    					break;
	    				case "200E"://0x200E厂商
	    					objs.setManufacturer_T(dataStr_T);
	    					objs.setManufacturer_L(dataStr_L);
	    					objs.setManufacturer_V(dataStr_V);
	    					break;
	    				case "3001"://燃气浓度值
	    				case "3005"://水压值
	    					objs.setDevValue_T(dataStr_T);
	    					objs.setDevValue_L(dataStr_L);
	    					objs.setDevValue_V(dataInt_V+"");
	    					break;
	    				case "3006"://水压阈值
	    				case "3002"://燃气阈值
	    					objs.setDevThreashold_T(dataStr_T);
	    					objs.setDevThreashold_L(dataStr_L);
	    					objs.setDevThreashold_V(dataInt_V+"");
	    					break;
	    				}
	    				
	    			}
	    			mRePeaterData.setZtwObj(objs);
	    			break;
	    		}
    		case 50:  //报警数据包
    			macByte = new byte[15];
        		for(int i=0;i<15;i++){
        			System.out.println(data[14+i]);
        			macByte[i] = data[14+i];
        		}
        		devMac = IntegerTo16.unicode2Hex(macByte);
    			mrsg = data[4]&0xff;
    			mRePeaterData.setCmd2((byte)0x02);	//报警
    			mRePeaterData.setAlarmSmokeMac(devMac);
    				//报警0xca，模块低电压0xc1，防拆0xc2，烟感低电压0xc3
    			cmd18 = data[30]&0xff;	//
    			cmd19 = data[31]&0xff;	//
    			if(cmd18==67){	//43
    				switch(cmd19){
    				case 65:	//报警202 CA 43-41
    					mRePeaterData.setRepeaterState(202);
    					break;
    				case 49:	//模块低电压	C1 43-31
    					mRePeaterData.setRepeaterState(192);
    					break;
    				case 50:	//防拆		C2	43-32
    					mRePeaterData.setRepeaterState(14);
    					break;	
    				case 51:	//烟感低电压	C3	43-33
    					mRePeaterData.setRepeaterState(193);
    					break;
    				case 52:	//烟感自检	C4	43-33
    					mRePeaterData.setRepeaterState(67);
    					break;
    				}
    			}else{
    				cmd18 = data[byteLen-12]&0xff;
    				cmd19 = data[byteLen-11]&0xff;
    				if(cmd18==67){	//43
        				switch(cmd19){
        				case 65:	//报警202 CA 43-41
        					mRePeaterData.setRepeaterState(202);
        					break;
        				case 49:	//模块低电压	C1 43-31
        					mRePeaterData.setRepeaterState(192);
        					break;
        				case 50:	//防拆		C2	43-32
        					mRePeaterData.setRepeaterState(14);
        					break;	
        				case 51:	//烟感低电压	C3	43-33
        					mRePeaterData.setRepeaterState(193);
        					break;
        				case 52:	//烟感自检	C4	43-33
        					mRePeaterData.setRepeaterState(67);
        					break;
        				}
        			}
    			}
    			int devState = (data[byteLen-7]&0xff)-48;//报警带消音状态 1消音0无效 
    			mRePeaterData.setDeviceType(devState);
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
    		mRePeaterData.setRepeatMac(repeaterMac);
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
    		ThreePhaseElectricEntity threeElec;
        	switch (msg) {
    		case 1:
    			mRePeaterData.setCmd2((byte)0x01);
				byte[] mac = new byte[4];
				for(int j=0;j<4;j++){
					mac[j] = data[7+j];
				}
				String macStr = IntegerTo16.bytes2Hex(mac);
				mRePeaterData.setAlarmSmokeMac(macStr);
    			break;
    		case 2:
    			mRePeaterData.setCmd2((byte)0x02);	//报警
    			byte[] alarmMac = new byte[4];
    			for(int i=0;i<4;i++){
    				alarmMac[i] = data[7+i];
    			}
    			String alarmSmokeMac = IntegerTo16.bytes2Hex(alarmMac);
    			mRePeaterData.setAlarmSmokeMac(alarmSmokeMac);
    			mRePeaterData.setRepeatMac(alarmSmokeMac);
    			break;
    		case 8:
    			mRePeaterData.setCmd2((byte)0x08);	
    			byte[] alarmMac7 = new byte[4];
    			for(int i=0;i<4;i++){
    				alarmMac7[i] = data[11+i];
    			}
    			String alarmSmokeMac7 = IntegerTo16.bytes2Hex(alarmMac7);
    			mRePeaterData.setAlarmSmokeMac(alarmSmokeMac7);
    			mRePeaterData.setCmd(data[15]);
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
    				runCauseState = "8";
    				break;
    			case 17:	//10001-合闸失败
    				runCauseState = "48";
    				break;
    			case 16:	//10000-互感器故障
    				runCauseState = "75";
    				break;
    			case 15:	//01111-闭锁
    				break;
    			case 14:	//01110-按键试验
    				break;
    			case 13:	//01101-远程
    				break;
    			case 12:	//01100-定时试验
    				break;
    			case 11:	//01011-停电
    				break;
    			case 10:	//01010-接地
    				break;
    			case 9:	//01001-过压
    				runCauseState = "143";
    				break;
    			case 8:	//01000-欠压
    				runCauseState = "144";
    				break;
    			case 7:	//00111-缺相
    				break;
    			case 6:	//00110-短路
    				runCauseState = "49";
    				break;
    			case 5:	//00101-过载
    				break;
    			case 4:	//00100-缺零
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
		int cmd0 = data[0]&0xff;
		int cmd1 = data[1]&0xff;
		int cmd2 = data[dataLen-2]&0xff;
		int cmd3 = data[dataLen-1]&0xff;
		if(cmd0==55&&cmd1==69&&cmd2==55&&cmd3==70){
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
	
	
	public static byte[] ackToNB_IOTClient(RePeaterData peaterData,byte[] data) {
		byte[] ackByte = new byte[38];
		for(int i = 0;i<30;i++){
			ackByte[i] =data[i]; 
		}
		ackByte[4] = 48;
		ackByte[5] = 51;
		
		int devState = 0;
		if(Utils.AckEleMap.get(peaterData.getAlarmSmokeMac())!=null){
			devState = Utils.AckEleMap.get(peaterData.getAlarmSmokeMac());
		}
		ackByte[30] = 48;
		if(devState==0){
			ackByte[31] = 48;
		}else if(devState==1){
			ackByte[31] = 49;
		}else if(devState==2){
			ackByte[31] = 50;
		}else if(devState==3){
			ackByte[31] = 51;
		}
		for (int i = 0; i < 6; i++) {
			ackByte[37-i] = data[data.length-1-i];
		}
		
		return ackByte;
	}
	
	public static byte[] ackToNB_IOTClient(RePeaterData peaterData,byte[] data,int cmd1) {
		byte[] ackByte = null;
		if(cmd1==1){	//心跳
			ackByte = new byte[38];
		}else{			//时间
			ackByte = new byte[46];
		}
		for(int i = 0;i<30;i++){
			ackByte[i] =data[i]; 
		}
		
		ackByte[4] = 57;
		ackByte[5] = 57;
		
		if(cmd1==1){
			ackByte[11] = 57;
		}
		
		if(cmd1==1){ 	//心跳回复
			ackByte[30] = 48;
			ackByte[31] = 48;
		}else{		//修改心跳时间
			byte[] ackTimeMill = ackTimeSmoke(cmd1+"");//单位秒
			for (int i = 0; i < ackTimeMill.length; i++) {
				ackByte[32+i] = ackTimeMill[i];
			}
		}
		
		for (int i = 0; i < 6; i++) {
			if(cmd1 == 1){
				ackByte[37-i] = data[data.length-1-i];
			}else{
				ackByte[45-i] = data[data.length-1-i];
			}
		}
		
		return ackByte;
	}
	
	//下发时间单位秒转换byte数组4个字节
	public static byte[] ackTimeSmoke(String timeStr) {
        char[] chars = timeStr.toCharArray();
        byte[] ackTime = new byte[8];
        for (int i = 0; i < chars.length; i++){
//            System.out.println(" " + chars[chars.length-i-1] + " " + (int)chars[chars.length-i-1] );
            ackTime[7-i] = (byte)chars[chars.length-i-1];
        }
        return ackTime;
	}
	
	/*public static void main(String[] args) {
		int timeStr = 86400;
		byte[] b1 = ackTimeSmoke(timeStr+"");
		int i = 86400;
		System.out.println(i);
		System.out.println("b1="+IntegerTo16.bytes2Hex(b1));
	}*/
}
