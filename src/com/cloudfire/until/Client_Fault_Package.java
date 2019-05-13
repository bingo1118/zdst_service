package com.cloudfire.until;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.AllSmokeDaoImpl;
import com.cloudfire.dao.impl.DevicesDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
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
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SocketToPCEntity;
import com.cloudfire.entity.ThreePhaseMeterEntity;
import com.cloudfire.entity.Water;
import com.cloudfire.entity.meter.MeterInfoEntity;

public class Client_Fault_Package {
	private final static Log log = LogFactory.getLog(Client_Fault_Package.class);
	
	public static RePeaterData isHeart(byte[] dataN,int byteLen) throws Exception {
		byte[] data = Utils.formatByte(dataN);
		int byteCount =data.length;
    	boolean crcRight = checkCrc(data,byteCount);  //crc校验
    	if(crcRight){
    		RePeaterData mRePeaterData = new RePeaterData();
    		mRePeaterData.setSeqL(data[11]);
    		int msg = data[12]&0xff;
    		String deviceMac="";
    		byte[] devMac = new byte[4];
    		log.debug("msg================================"+msg);
        	switch (msg) {
    		case 203:   //心跳
    			mRePeaterData.setCmd((byte)0x01);
    			for(int i=0;i<4;i++){
    				devMac[i] = data[14+i];
    			}
    			deviceMac = IntegerTo16.bytes2Hex(devMac);
    			mRePeaterData.setAlarmSmokeMac(deviceMac);
    			break;
    		case 202:  //报警数据包
    			mRePeaterData.setCmd((byte)0x02);	//报警
    			for(int i=0;i<4;i++){
    				devMac[i] = data[14+i];
    			}
    			deviceMac = IntegerTo16.bytes2Hex(devMac);
    			mRePeaterData.setAlarmSmokeMac(deviceMac);
    			break;
    		case 204:  //透传数据包
    			mRePeaterData.setCmd((byte)0x04);	//CC透传数据处理
    			data = Client_Fault_Package.getByteDataByCC(dataN, dataN[12]);
    			int electricDataLen = data[11]&0xff;
    			int transparentType = data[12]&0xff;//透传类型，用于区别电气火灾与三江设备，其中CC(204),表示电气火灾
    			switch (transparentType) {
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
	    			if(deviceTypeback == 2){//2代表燃气
	    				
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
	    			}else if(deviceTypeback == 19){	//19水位探测器
	    				log.debug("deviceTypeback:"+deviceTypeback+"   :"+electricMacStr);
	    				mRePeaterData.setTransmissionType(6);
	    				int c3state = data[18]&0xff;
	    				String waterValue =((data[20]&0xff)<<8)+(data[21]&0xff)+"";
	    				int pow =  (int)Math.pow(10,((((data[22]&0xff)<<8)+(data[23]&0xff))));
	    				String waterleve = Float.parseFloat(waterValue)/pow +"";
	    				Water water = new Water();
	    				WaterInfoDao waterDao = new WaterInfoDaoImpl();
	    				int alarmType = waterDao.getWaterLeve(electricMacStr, waterleve);
    					water.setAlarmType(alarmType);
	    				water.setValue(waterleve);
	    				water.setWaterMac(electricMacStr);
	    				log.debug("waterleve:"+waterleve+" :alarmType="+alarmType+" waterValue:"+waterValue+" electricMacStr="+electricMacStr);
	    				if(c3state == 195){	
	    					water.setAlarmType(136);//报485故障
	    				}
	    				mRePeaterData.setWater(water);
	    			}else if (deviceTypeback == 10){  //10代表水压****************************
	    			/** begin */
		    			byte[] waterRepeater = new byte[4];
		    			for(int j=0;j<4;j++){
		    				waterRepeater[j]=data[7+j];
		    			}
		    			String waterRepeaterMac = IntegerTo16.bytes2Hex(waterRepeater);
		    			mRePeaterData.setWaterRepeaterMac(waterRepeaterMac);
		    			mRePeaterData.setWaterMac(electricMacStr);
		    			int data17 = data[17]&0xff;
		    			int data18 = data[18]&0xff; //DD:
		    			Water water = new Water();
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
	    			}else if (deviceTypeback == 18){  //18 喷淋设备
	    			/** begin */
		    			byte[] waterRepeater = new byte[4];
		    			for(int j=0;j<4;j++){
		    				waterRepeater[j]=data[7+j];
		    			}
		    			String waterRepeaterMac = IntegerTo16.bytes2Hex(waterRepeater);
		    			mRePeaterData.setWaterRepeaterMac(waterRepeaterMac);
		    			mRePeaterData.setWaterMac(electricMacStr);
		    			int data17 = data[17]&0xff;
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
	    			}else if(deviceTypeback == 5){
	    				PublicUtils utilsDao= new PublicUtilsImpl();
						utilsDao.updateDeviceMac(electricMacStr);//@@电气上线
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
									log.debug("value611:"+value61+" 2 "+value62+" 3 "+value63);
									log.debug("1 "+value71+" 2 "+value72+" 3 "+value73+"   8:"+data8);
									log.debug("1 "+value91+" 2 "+value92+" 3 "+value93);
		    						break;
		    					case 10:	//阈值
		    						jtlEntity.setType(3);
									ElectricThresholdBean mElectricThresholdBean = Utils.jtlElectricThresholdBean(data,33);
									jtlEntity.setmElectricThresholdBean(mElectricThresholdBean);
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
								meter.setData_type(6);
								break;
							case "33353435"://@@B相电压
								threephasedata=threephasedata/10;
								meter.setData_num(2);
								meter.setData(threephasedata+"");
								meter.setData_type(6);
								break;
							case "33363435"://@@C相电压
								threephasedata=threephasedata/10;
								meter.setData_num(3);
								meter.setData(threephasedata+"");
								meter.setData_type(6);
								break;
							case "33343434"://@@A相电流
								threephasedata=threephasedata/10;
								meter.setData_num(1);
								meter.setData(threephasedata+"");
								meter.setData_type(7);
								break;
							case "33353434"://@@B相电流
								threephasedata=threephasedata/10;
								meter.setData_num(2);
								meter.setData(threephasedata+"");
								meter.setData_type(7);
								break;
							case "33363434"://@@C相电流
								threephasedata=threephasedata/10;
								meter.setData_num(3);
								meter.setData(threephasedata+"");
								meter.setData_type(7);
								break;
							case "3333C334"://@@当前剩余电流最大相(漏电流阈值)
								threephasedata=threephasedata/10;
								break;
							case "3334C334"://@@当前剩余电流值（漏电流）
								threephasedata=threephasedata;
								meter.setData_num(1);
								meter.setData(threephasedata+"");
								meter.setData_type(8);
								break;
							case "37373337"://@@额定电压值
								threephasedata=threephasedata/10;
								break;
							case "38373337"://@@额定电流值
								threephasedata=threephasedata/10;
								break;
							case "34383337"://@@运行状态字
								int alarm_reason = data[24]&0x1f;//@@报警原因
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
										break;
									case 16://10000-互感器故障
										break;
									case 17://10001-合闸失败
										break;
									case 19://10011-设置更改
										break;
								}
								int alarmstate = (data[24]>>7)&0xff;
								switch(alarmstate){
									case 0:
										meter.setIfalarm(0);
										break;
									case 1:
										meter.setIfalarm(1);
										break;
								}
								int electricstate = (data[24]>>5)&0x03;//@@合闸状态
								switch(electricstate){
									case 0://合闸
										meter.setElectric_state(1);
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
										mBQ200Entity.setmElectricThresholdBean(mElectricThresholdBean);
										break;
									case 36://故障
										mBQ200Entity.setType(3);
										String alarmValue = Utils.getValue(data, 25);
										mBQ200Entity.setAlarmData(alarmValue);
										mBQ200Entity.setAlarmType(36);
										return null;//@@11.17屏蔽故障
//										break;
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
										return null;//@@11.17屏蔽故障
//										break;
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
											mBQ100Entity.setmElectricThresholdBean(mElectricThresholdBean);
											break;
										case 36://故障
											mBQ100Entity.setType(3);
											String alarmValue = Utils.getValue(data, 25);
											mBQ100Entity.setAlarmData(alarmValue);
											mBQ100Entity.setAlarmType(36);
											return null;//@@11.17屏蔽故障
//											break;
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
											Utils.saveChangehistory("6603",electricMacStr,1);//@@12.22
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
										}else{
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
										mBq100Entity.setmElectricThresholdBean(mElectricThresholdBean);
										break;
									case 36://故障
										mBq100Entity.setType(5);
										int alarmValue1 = data[30]&0xff;
										log.debug(alarmValue1+"=alarmValue1");
										String alarmValue = alarmValue1+"";
										mBq100Entity.setAlarmData(alarmValue);
										mBq100Entity.setAlarmType(136);	
										return null;//@@11.17屏蔽故障
//										break;
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
    		case 6:
    			mRePeaterData.setCmd2((byte)0x06);
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
		if((crcL==clientCrcL&&crcH==clientCrcH)||(crcL==clientCrcH&&crcH==clientCrcL)){
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
	public static byte[] ackToNB_IOTClient(RePeaterData peaterData,byte ackType) {
		AllSmokeDao mAllSmokeDao=new AllSmokeDaoImpl();
		MeterInfoEntity mie=mAllSmokeDao.getMeterInfoByMac(peaterData.getRepeatMac());
		byte[] ackByte = new byte[18];
		byte[] crcByte = new byte[14];
		ackByte[0]=0x7e;
		ackByte[1]=0x1e;
		ackByte[2]=ackType;
		ackByte[3]=peaterData.getSeqL();
		ackByte[4]=peaterData.getSeqH();
		ackByte[5]=0x08;
		ackByte[6]=0x00;
		crcByte[0] = 0x1e;
		crcByte[1] = ackType;
		crcByte[2] = peaterData.getSeqL();
		crcByte[3] = peaterData.getSeqH();
		crcByte[4] = 0x08;
		crcByte[5] = 0x00;
		byte[] mac = IntegerTo16.hexString2Bytes(peaterData.getRepeatMac());
		for(int i=0;i<4;i++){
			ackByte[7+i] = mac[i];
			crcByte[6+i] = mac[i];
		}
		crcByte[10]=0x00;
		crcByte[11]=0x00;//可选配置
		if(mie.getIfSendVoltage()!=0)
			crcByte[11]|=0x01;
		if(mie.getIfSendElectricity()!=0)
			crcByte[11]|=0x02;
		if(mie.getIfSendPower()!=0)
			crcByte[11]|=0x04;
		
		
		crcByte[12]=0x00;
		crcByte[13]=0x00;
		
		ackByte[11] = 0x00;
		ackByte[12] = crcByte[11];
		ackByte[13] = 0x00;
		ackByte[14] = 0x00;
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[15] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[16] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		ackByte[17]=0x7f;
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
	
	public static byte[] ackAlarmPackage(SmokeBean mSmokeBean,byte type,byte[] byteDatas){
		List<String> soundList = new ArrayList<String>();
		soundList = mSmokeBean.getList();
		int byteLen = soundList.size();
		
		byte[] ackByte = new byte[byteLen*4+40];
		byte[] crcByte = new byte[byteLen*4+7];	//crc验证
		byte[] sumByte = new byte[byteLen*4+35];	//校验和验证
		for(int i=0;i<24;i++){
			ackByte[i] = byteDatas[i];
			if(i>1){
				sumByte[i-2] = byteDatas[i];
			}
		}
		
		ackByte[24] = new IntegerTo16().algorismToHEXString(10+byteLen*4);
		sumByte[22] = ackByte[24];
		
		ackByte[25] = 0x00;
		sumByte[22] = ackByte[24];
		
		
		if((type&0xff)==05){
			ackByte[26]=(byte)0xFF;
			sumByte[24] = ackByte[26];
		}else{
			ackByte[26]=(byte)0xFF;
			sumByte[24] = ackByte[26];
		}
		ackByte[27]=0x7e;
		sumByte[25] = ackByte[27];
		
		int crcLen = 7+byteLen*4;
		ackByte[28]=new IntegerTo16().algorismToHEXString(crcLen);
		sumByte[26] = ackByte[28];
		
		ackByte[29]=type;
		sumByte[27] = ackByte[29];
		
		ackByte[30] = new IntegerTo16().algorismToHEXString(byteLen);
		sumByte[28] = ackByte[30];
		
		ackByte[31] = 0x00;
		sumByte[29] = ackByte[31];
		
		ackByte[32] = (byte)0x88;
		sumByte[30] = ackByte[32];
		
		ackByte[33]=0x00;
		sumByte[31] = ackByte[33];
		
		
		crcByte[0] = ackByte[28];
		crcByte[1] = ackByte[29];
		crcByte[2] = ackByte[30];
		crcByte[3] = ackByte[31];
		crcByte[4] = ackByte[32];
		crcByte[5] = ackByte[33];
		
		for(int i = 0;i<soundList.size();i++){
			byte[] sMac = IntegerTo16.hexString2Bytes(soundList.get(i));
			for(int j = 0;j<4;j++){
				ackByte[34+i*4+j] = sMac[j];
				sumByte[32+i*4+j] = sMac[j];
				crcByte[6+i*4+j] = sMac[j];
			}
		}
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[34+byteLen*4] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		sumByte[32+byteLen*4] = ackByte[34+byteLen*4];
		
		ackByte[35+byteLen*4] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		sumByte[33+byteLen*4] = ackByte[35+byteLen*4];
		ackByte[36+byteLen*4] = 0x0d;
		sumByte[34+byteLen*4] = ackByte[36+byteLen*4];
		
		String sumCrc = CRC16.SumCheckToHex(sumByte);
		ackByte[37+byteLen*4] = new IntegerTo16().str16ToByte(sumCrc);
		ackByte[38+byteLen*4] = 0x23;
		ackByte[39+byteLen*4] = 0x23;
		log.debug("dataN=====:"+IntegerTo16.bytes2Hex(byteDatas));
		log.debug("sumCrc=====:"+IntegerTo16.bytes2Hex(sumByte));
		log.debug("ackByte=====:"+IntegerTo16.bytes2Hex(ackByte));
		return ackByte;
	}
	/*
	 * 下发声光取消命令
	 */
	public static byte[] ackAlarmPackage(String repeaterMac,byte type,List<String> soundList){
		int byteLen = soundList.size();
		
		byte[] ackByte = new byte[byteLen*4+40];
		byte[] crcByte = new byte[byteLen*4+7];		//crc验证
		byte[] sumByte = new byte[byteLen*4+35];	//校验和验证
		ackByte[0] = 0x40;
		ackByte[1] = 0x40;
		
		ackByte[2] = 0x01;
		ackByte[3] = 0x00;
		
		ackByte[4] = 0x10;
		ackByte[5] = 0x00;
		
		ackByte[6] = 0x00;
		ackByte[7] = 0x00;
		ackByte[8] = 0x00;
		ackByte[9] = 0x00;
		ackByte[10] = 0x00;
		ackByte[11] = 0x00;
		
		ackByte[12] = 0x00;
		ackByte[13] = 0x00;
		ackByte[14] = 0x00;
		ackByte[15] = 0x00;
		ackByte[16] = 0x00;
		ackByte[17] = 0x00;
		
		ackByte[18] = 0x00;
		ackByte[19] = 0x00;
		ackByte[20] = 0x00;
		ackByte[21] = 0x00;
		ackByte[22] = 0x00;
		ackByte[23] = 0x00;
		
		for(int i=0;i<24;i++){
			if(i>1){
				sumByte[i-2] = ackByte[i];
			}
		}
		
		
		ackByte[24] = new IntegerTo16().algorismToHEXString(10+byteLen*4);
		sumByte[22] = ackByte[24];
		
		ackByte[25] = 0x00;
		sumByte[23] = ackByte[25];
		
		
		if((type&0xff)==05){
			ackByte[26]=(byte)0xFF;
			sumByte[24] = ackByte[26];
		}else{
			ackByte[26]=(byte)0xFF;
			sumByte[24] = ackByte[26];
		}
		ackByte[27]=0x7e;
		sumByte[25] = ackByte[27];
		
		int crcLen = 7+byteLen*4;
		ackByte[28]=new IntegerTo16().algorismToHEXString(crcLen);
		sumByte[26] = ackByte[28];
		
		ackByte[29]=type;
		sumByte[27] = ackByte[29];
		
		ackByte[30] = new IntegerTo16().algorismToHEXString(byteLen);
		sumByte[28] = ackByte[30];
		
		ackByte[31] = 0x00;
		sumByte[29] = ackByte[31];
		
		ackByte[32] = (byte)0x87;
		sumByte[30] = ackByte[32];
		
		ackByte[33]=0x00;
		sumByte[31] = ackByte[33];
		
		
		crcByte[0] = ackByte[28];
		crcByte[1] = ackByte[29];
		crcByte[2] = ackByte[30];
		crcByte[3] = ackByte[31];
		crcByte[4] = ackByte[32];
		crcByte[5] = ackByte[33];
		
		for(int i = 0;i<soundList.size();i++){
			byte[] sMac = IntegerTo16.hexString2Bytes(soundList.get(i));
			for(int j = 0;j<4;j++){
				ackByte[34+i*4+j] = sMac[j];
				sumByte[32+i*4+j] = sMac[j];
				crcByte[6+i*4+j] = sMac[j];
			}
		}
		
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[34+byteLen*4] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		sumByte[32+byteLen*4] = ackByte[34+byteLen*4];
		
		ackByte[35+byteLen*4] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		sumByte[33+byteLen*4] = ackByte[35+byteLen*4];
		ackByte[36+byteLen*4] = 0x0d;
		sumByte[34+byteLen*4] = ackByte[36+byteLen*4];
		
		String sumCrc = CRC16.SumCheckToHex(sumByte);
		ackByte[37+byteLen*4] = new IntegerTo16().str16ToByte(sumCrc);
		ackByte[38+byteLen*4] = 0x23;
		ackByte[39+byteLen*4] = 0x23;
		log.debug("sumCrc=====:"+IntegerTo16.bytes2Hex(sumByte));
		log.debug("ackByte=====:"+IntegerTo16.bytes2Hex(ackByte));
		return ackByte;
	}
	
	public static byte[] ackAlarmAddress(SmokeBean mSmokeBean,byte type,byte[] byteDatas){
//		byte[] addresssbyte = mSmokeBean.getAddressbyte();
		byte[] addresssbyte = mSmokeBean.getSmokeNamed();
		int addresslen = addresssbyte.length;
		System.out.println("address1="+IntegerTo16.bytes2Hex(addresssbyte));
		System.out.println("address2="+IntegerTo16.hexToStringGBK(IntegerTo16.bytes2Hex(addresssbyte)));
		byte[] ackByte = new byte[33+addresslen];
		byte[] sumByte = new byte[28+addresslen];	//校验和验证
		for(int i=0;i<24;i++){
			ackByte[i] = byteDatas[i];
			if(i>1){
				sumByte[i-2] = byteDatas[i];
			}
		}
		
		ackByte[24] = new IntegerTo16().algorismToHEXString(3+addresslen);//6e+数据长度1个字节+0d结尾3个字节
		sumByte[22] = ackByte[24];
		
		ackByte[25] = 0x00;
		sumByte[22] = ackByte[24];
		
		if((type&0xff)==05){
			ackByte[26]=(byte)0xFF;
			sumByte[24] = ackByte[26];
		}else{
			ackByte[26]=(byte)0xFF;
			sumByte[24] = ackByte[26];
		}
		ackByte[27]=0x6e;
		sumByte[25] = ackByte[27];
		
		ackByte[28]=new IntegerTo16().algorismToHEXString(addresslen);
		sumByte[26] = ackByte[28];
		
		for(int i = 0;i<addresslen;i++){
			ackByte[29+i] = addresssbyte[i];
			sumByte[27+i] = addresssbyte[i];
		}
		
		ackByte[29+addresslen] = 0x0d;
		sumByte[27+addresslen] = 0x0d;
		
		String sumCrc = CRC16.SumCheckToHex(sumByte);
		ackByte[30+addresslen] = new IntegerTo16().str16ToByte(sumCrc);
		ackByte[31+addresslen] = 0x23;
		ackByte[32+addresslen] = 0x23;
		log.debug("dataN=====:"+IntegerTo16.bytes2Hex(byteDatas));
		log.debug("sumCrc=====:"+IntegerTo16.bytes2Hex(sumByte));
		log.debug("ackByte=====:"+IntegerTo16.bytes2Hex(ackByte));
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
	
	
	public static byte[] filerByte(byte[] data,int len){
		String byteStr = JavaByteArrToHex.bytesToHex(data).toUpperCase();;
		String ss1 = byteStr.substring(0,2);
		String ss2 = byteStr.substring(byteStr.length()-2);
		if(ss1.equalsIgnoreCase("7E")&&ss2.equalsIgnoreCase("0D")){
			return data;
		}else{
			if(!ss1.equalsIgnoreCase("7E")&&ss2.equalsIgnoreCase("0D")){
				int indexStart = byteStr.indexOf("7E");
				String subStr;
				byte[] dataByte = null;
				if(indexStart>=0){
					subStr = byteStr.substring(indexStart, byteStr.length());
					dataByte = IntegerTo16.hexString2Bytes(subStr);
				}
				return dataByte;
			}else if(ss1.equalsIgnoreCase("7E")&&!ss2.equalsIgnoreCase("0D")){
				int indexEnd = byteStr.indexOf("0D");
				String subStr;
				byte[] dataByte = null;
				if(indexEnd>0){
					subStr = byteStr.substring(0, indexEnd+2);
					dataByte = IntegerTo16.hexString2Bytes(subStr);
				}
				return dataByte;
			}else{
				int indexStart = byteStr.indexOf("7E");
				int indexEnd = byteStr.indexOf("0D");
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
	
	private static PrinterEntity getPrinterInfo(byte[] datas){//第39个字节开始
		PrinterEntity mPrinterEntity = new PrinterEntity();
		byte[] printerMacByte = new byte[8];
		for(int i=0;i<8;i++){
			printerMacByte[i] = datas[39+i];
		}
		String macByte = IntegerTo16.bytes2Hex(printerMacByte);
		String printerMac = IntegerTo16.asciiStringToString(macByte);
		mPrinterEntity.setFaultCode(printerMac);
		String contentStr1 = IntegerTo16.bytes2Hex(datas).toUpperCase();
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
	
	public static FaultDataEntity sendDataFault(byte[] dataN,int byteLen,int controlCmd) throws Exception{
		FaultDataEntity faultData = new FaultDataEntity();
//		List<String> stateTab = new ArrayList<String>();
		Map<Integer,String> stateTab = new HashMap<Integer,String>();
		List<String> stateTabList = new ArrayList<String>();
		if(controlCmd==2){	//发送数据处理
			int tabbingCmd = dataN[27]&0xff;	//标识命令、类型标志
			int infoNum = dataN[28]&0xff;		//信息数目
			switch(tabbingCmd){
			case 1:	//上传建筑消防设施系统状态============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//系统标志
					int systemAddress = dataN[30+i*10]&0xff;	//系统地址
					int systemState = (dataN[32+i*10]&0xff)+((dataN[31+i*10]&0xff)<<8);	//系统状态
					
					if((systemState&0x01)==1){	//正常运行状态
//						stateTab.put(1, "正常运行状态");
//						stateTabList.add("正常运行状态");
					}else if((systemState&0x01)==0){	//测试状态
						stateTab.put(1, "测试状态");
					}
					if((systemState&0x02)==2){	//火警
						stateTab.put(2, "火警");
						stateTabList.add("火警");
					}else if((systemState&0x02)==0){	//无火警
						stateTab.put(2, "无火警");
					}
					if((systemState&0x04)==4){	//故障
						stateTab.put(3, "故障");
						stateTabList.add("故障");
					}else if((systemState&0x04)==0){	//无故障
						stateTab.put(3, "无故障");
					}
					if((systemState&0x08)==8){	//屏蔽
						stateTab.put(4, "屏蔽");
						stateTabList.add("屏蔽");
					}else if((systemState&0x08)==0){	//无屏蔽
						stateTab.put(4, "无屏蔽");
					}
					if((systemState&0x10)==16){	//监管
						stateTab.put(5, "监管");
						stateTabList.add("监管");
					}else if((systemState&0x10)==0){	//无监管
						stateTab.put(5, "无监管");
					}
					if((systemState&0x20)==32){	//启动（开启）
						stateTab.put(6, "启动（开启）");
						stateTabList.add("启动（开启）");
					}else if((systemState&0x20)==0){	//停止（关闭）
						stateTab.put(6, "停止（关闭）");
					}
					if((systemState&0x40)==64){	//反馈
						stateTab.put(7, "反馈");
						stateTabList.add("反馈");
					}else if((systemState&0x40)==0){	//无反馈
						stateTab.put(7, "无反馈");
					}
					if((systemState&0x80)==128){	//延时状态
						stateTab.put(8, "延时状态");
						stateTabList.add("延时状态");
					}else if((systemState&0x80)==0){	//未延时
						stateTab.put(8, "未延时");
					}
					if((systemState&0x100)==256){	//主电故障
						stateTab.put(9, "主电故障");
						stateTabList.add("主电故障");
					}else if((systemState&0x100)==0){	//主电正常
						stateTab.put(9, "主电正常");
					}
					if((systemState&0x200)==512){	//备电故障
						stateTab.put(10, "备电故障");
						stateTabList.add("备电故障");
					}else if((systemState&0x200)==0){	//备电正常
						stateTab.put(10, "备电正常");
					}
					if((systemState&0x400)==1024){	//总线故障
						stateTab.put(11, "总线故障");
						stateTabList.add("总线故障");
					}else if((systemState&0x400)==0){	//总线正常
						stateTab.put(11, "总线正常");
					}
					if((systemState&0x800)==2048){	//手动状态
						stateTab.put(12, "手动状态");
						stateTabList.add("手动状态");
					}else if((systemState&0x800)==0){	//自动状态
						stateTab.put(12, "自动状态");
					}
					if((systemState&0x1000)==4096){	//配置改变
						stateTab.put(13, "配置改变");
						stateTabList.add("配置改变");
					}else if((systemState&0x1000)==0){	//无配置改变
						stateTab.put(13, "无配置改变");
					}
					if((systemState&0x2000)==8192){	//复位
						stateTab.put(14, "复位");
						stateTabList.add("复位");
					}else if((systemState&0x2000)==0){	//正常
						stateTab.put(14, "正常");
					}
					
					switch(systemTab){
					case 1:	//火灾报警系统
						faultData.setSystemTab("火灾报警系统");
						break;
					case 10:	//消防联动控制器
						faultData.setSystemTab("消防联动控制器");
						break;
					case 11:	//消火栓系统
						faultData.setSystemTab("消火栓系统");
						break;
					case 12:	//自动喷水灭火系统
						faultData.setSystemTab("自动喷水灭火系统");
						break;
					case 13:	//气体灭火系统
						faultData.setSystemTab("气体灭火系统");
						break;
					case 14:	//水喷雾灭火系统（泵启动方式）
						faultData.setSystemTab("水喷雾灭火系统（泵启动方式）");
						break;
					case 15:	//水喷雾灭火系统（压力容器启动方式）
						faultData.setSystemTab("水喷雾灭火系统（压力容器启动方式）");
						break;
					case 16:	//泡沫灭火系统
						faultData.setSystemTab("泡沫灭火系统");
						break;
					case 17:	//干粉灭火系统
						faultData.setSystemTab("干粉灭火系统");
						break;
					case 18:	//防烟排烟系统
						faultData.setSystemTab("防烟排烟系统");
						break;
					case 19:	//防火门及卷帘系统
						faultData.setSystemTab("防火门及卷帘系统");
						break;
					case 20:	//消防电梯
						faultData.setSystemTab("消防电梯");
						break;
					case 21:	//消防应急广播
						faultData.setSystemTab("消防应急广播");
						break;
					case 22:	//消防应急照明和疏散指示系统
						faultData.setSystemTab("消防应急照明和疏散指示系统");
						break;
					case 23:	//消防电源
						faultData.setSystemTab("消防电源");
						break;
					case 24:	//消防电话
						faultData.setSystemTab("消防电话");
						break;
					}
					faultData.setTabbingCmd("上传建筑消防设施系统状态");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 2:	//上传建筑消防设施部件运行状态==============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*46]&0xff;		//系统标志
					int systemAddress = dataN[30+i*46]&0xff;	//系统地址
					int unitType = dataN[31+i*46]&0xff;		//部件类型
					byte[] macByte = new byte[4];
		    		for(int j=0;j<4;j++){
		    			macByte[j] = dataN[32+j+i*46];
		    		}//更换三江协议获取部件地址
		    		
		    		
		    		String unitAddress = IntegerTo16.bytes2Hex(macByte);
					/*int bj1 = (dataN[32+i*46]&0xff)+((dataN[33+i*46]&0xff)<<8); 	//位号
					int bj2 = (dataN[34+i*46]&0xff)+((dataN[35+i*46]&0xff)<<8); 	//区号
		    		String unitAddress = bj1+"位"+bj2+"区";	//部件地址
*/		    		byte[] unitMemo = new byte[31];							//部件说明
		    		for (int j = 0; j < 31; j++) {
		    			unitMemo[i] = dataN[38+j+i*46];
					}
		    		String unitMemos = new String(unitMemo,"gb18030");		//部件说明转成汉字
					int unitState = (dataN[37+i*46]&0xff)+((dataN[36+i*46]&0xff)<<8);	//部件状态
					if(unitState==11){
						stateTabList.add("屏蔽消除");
					}else if(unitState==12){
						stateTabList.add("故障消除");
					}else if(unitState==13){
						stateTabList.add("请求消除");
					}else if(unitState==14){
						stateTabList.add("反馈消除");
					}else if(unitState==15){
						stateTabList.add("电源故障消除");
					}else{
						if((unitState&0x01)==1){	//正常运行状态
//							stateTab.put(1, "正常运行状态");
//							stateTabList.add("正常运行状态");
						}else if((unitState&0x01)==0){	//测试状态
							stateTab.put(1, "测试状态");
						}
						if((unitState&0x02)==2){	//火警
							stateTab.put(2, "火警");
							stateTabList.add("火警");
						}else if((unitState&0x02)==0){	//无火警
							stateTab.put(2, "无火警");
						}
						if((unitState&0x04)==4){	//故障
							stateTab.put(3, "故障");
							stateTabList.add("故障");
						}else if((unitState&0x04)==0){	//无故障
							stateTab.put(3, "无故障");
						}
						if((unitState&0x08)==8){	//屏蔽
							stateTab.put(4, "屏蔽");
							stateTabList.add("屏蔽");
						}else if((unitState&0x08)==0){	//无屏蔽
							stateTab.put(4, "无屏蔽");
						}
						if((unitState&0x10)==16){	//监管
							stateTab.put(5, "监管");
							stateTabList.add("监管");
						}else if((unitState&0x10)==0){	//无监管
							stateTab.put(5, "无监管");
						}
						if((unitState&0x20)==32){	//启动（开启）
							stateTab.put(6, "启动（开启）");
							stateTabList.add("启动（开启）");
						}else if((unitState&0x20)==0){	//停止（关闭）
							stateTab.put(6, "停止（关闭）");
						}
						if((unitState&0x40)==64){	//反馈
							stateTab.put(7, "反馈");
							stateTabList.add("反馈");
						}else if((unitState&0x40)==0){	//无反馈
							stateTab.put(7, "无反馈");
						}
						if((unitState&0x80)==128){	//延时状态
							stateTab.put(8, "延时状态");
							stateTabList.add("延时状态");
						}else if((unitState&0x80)==0){	//未延时
							stateTab.put(8, "未延时");
						}
						if((unitState&0x100)==256){	//电源故障
							stateTab.put(9, "电源故障");
							stateTabList.add("电源故障");
						}else if((unitState&0x100)==0){	//电源正常
							stateTab.put(9, "电源正常");
						}
						if(unitState==512){	//火警
							stateTab.put(10, "火警");
							stateTabList.add("火警");
						}else if((unitState&0x100)==0){	//电源正常
							stateTab.put(10, "无火警");
						}
					}
					switch(unitType){		//部件类型
						case 1:	//	火灾报警控制器
							faultData.setUnitType("火灾报警控制器");
							break;
						case 10:	//可燃气体探测器
							faultData.setUnitType("可燃气体探测器");
							break;
						case 11:	//典型可燃气体探测器
							faultData.setUnitType("典型可燃气体探测器");
							break;
						case 12:	//独立式可燃气体探测器
							faultData.setUnitType("独立式可燃气体探测器");
							break;
						case 13:	//线型可燃气体探测器
							faultData.setUnitType("线型可燃气体探测器");
							break;
						case 16:	//电气火灾监控报警器
							faultData.setUnitType("电气火灾监控报警器");
							break;
						case 17:	//剩余电流式电气火灾监控探测器
							faultData.setUnitType("剩余电流式电气火灾监控探测器");
							break;
						case 18:	//测温式电气火灾监控探测器
							faultData.setUnitType("测温式电气火灾监控探测器");
							break;
						case 21:	//探测回路 改成三江接口板回路
//							faultData.setUnitType("探测回路");(接口板回路)故障
							faultData.setUnitType("(接口板回路)故障");
							break;
						case 22:	//火灾显示盘
							faultData.setUnitType("火灾显示盘");
							break;
						case 23:	//手动火灾报警按钮
							faultData.setUnitType("手动火灾报警按钮");
							break;
						case 24:	//消火栓按钮
							faultData.setUnitType("消火栓按钮");
							break;
						case 25:	//火灾探测器
							faultData.setUnitType("火灾探测器");
							break;
						case 30:	//感温火灾探测器
							faultData.setUnitType("感温火灾探测器");
							break;
						case 31:	//点型感温火灾探测器
							faultData.setUnitType("点型感温火灾探测器");
							break;
						case 32:	//点型感温火灾探测器（S型）
							faultData.setUnitType("点型感温火灾探测器（S型）");
							break;
						case 33:	//点型感温火灾探测器（R型）
							faultData.setUnitType("点型感温火灾探测器（R型）");
							break;
						case 34:	//线型感温火灾探测器
							faultData.setUnitType("线型感温火灾探测器");
							break;
						case 35:	//线型感温火灾探测器(S型)
							faultData.setUnitType("线型感温火灾探测器(S型)");
							break;
						case 36:	//线型感温火灾探测器(R型)
							faultData.setUnitType("线型感温火灾探测器(R型)");
							break;
						case 37:	//光纤感温火灾探测器
							faultData.setUnitType("光纤感温火灾探测器");
							break;
						case 40:	//感烟火灾探测器
							faultData.setUnitType("感烟火灾探测器");
							break;
						case 41:	//点型离子感烟火灾探测器
							faultData.setUnitType("点型离子感烟火灾探测器");
							break; 
						case 42:	//点型光电感烟火灾探测器
							faultData.setUnitType("点型光电感烟火灾探测器");
							break; 
						case 43:	//线型光束感烟火灾探测器
							faultData.setUnitType("线型光束感烟火灾探测器");
							break; 
						case 44:	//吸气式感烟火灾探测器
							faultData.setUnitType("吸气式感烟火灾探测器");
							break; 
						case 50:	//复合式火灾探测器
							faultData.setUnitType("复合式火灾探测器");
							break; 
						case 51:	//复合式感烟感温火灾探测器
							faultData.setUnitType("复合式感烟感温火灾探测器");
							break; 
						case 52:	//复合式感光感温火灾探测器
							faultData.setUnitType("复合式感光感温火灾探测器");
							break; 
						case 53:	//复合式感光感烟火灾探测器
							faultData.setUnitType("复合式感光感烟火灾探测器");
							break; 
						case 61:	//紫外火焰探测器
							faultData.setUnitType("紫外火焰探测器");
							break; 
						case 62:	//红外火焰探测器
							faultData.setUnitType("红外火焰探测器");
							break; 
						case 69:	//感光火灾探测器
							faultData.setUnitType("感光火灾探测器");
							break; 
						case 74:	//气体探测器
							faultData.setUnitType("气体探测器");
							break; 
						case 78:	//图像摄像方式火灾探测器
							faultData.setUnitType("图像摄像方式火灾探测器");
							break;
						case 79:	//感声火灾测器
							faultData.setUnitType("感声火灾测器");
							break; 
						case 81:	//气体灭火控制器
							faultData.setUnitType("气体灭火控制器");
							break; 
						case 82:	//消防电气控制装置
							faultData.setUnitType("消防电气控制装置");
							break; 
						case 83:	//消防控制室图形显示装置
							faultData.setUnitType("消防控制室图形显示装置");
							break; 
						case 84:	//模块
							faultData.setUnitType("模块");
							break;
						case 85:	//输入模块
							faultData.setUnitType("输入模块");
							break;
						case 86:	//输出模块
							faultData.setUnitType("输出模块");
							break;
						case 87:	//输入输出模块
							faultData.setUnitType("输入输出模块");
							break;
						case 88:	//中继模块
							faultData.setUnitType("中继模块");
							break;
						case 91:	//消防水泵
							faultData.setUnitType("消防水泵");
							break;
						case 92:	//消防水箱
							faultData.setUnitType("消防水箱");
							break;
						case 95:	//喷淋泵
							faultData.setUnitType("喷淋泵");
							break;
						case 96:	//水流指示器
							faultData.setUnitType("水流指示器");
							break;
						case 97:	//信号阀
							faultData.setUnitType("信号阀");
							break;
						case 98:	//报警阀
							faultData.setUnitType("报警阀");
							break;
						case 99:	//压力开关
							faultData.setUnitType("压力开关");
							break;
						case 101:	//阀驱动装置
							faultData.setUnitType("阀驱动装置");
							break;
						case 102:	//防火门
							faultData.setUnitType("防火门");
							break;
						case 103:	//防火阀
							faultData.setUnitType("防火阀");
							break;
						case 104:	//通风空调
							faultData.setUnitType("通风空调");
							break;
						case 105:	//泡沫液泵
							faultData.setUnitType("沫液泵");
							break;
						case 106:	//管网电磁阀
							faultData.setUnitType("管网电磁阀");
							break;
						case 111:	//防烟排烟风机
							faultData.setUnitType("防烟排烟风机");
							break;
						case 113:	//排烟防火阀
							faultData.setUnitType("排烟防火阀");
							break;
						case 114:	//常闭送风口
							faultData.setUnitType("常闭送风口");
							break;
						case 115:	//排烟口
							faultData.setUnitType("排烟口");
							break;
						case 116:	//电控挡烟垂壁
							faultData.setUnitType("电控挡烟垂壁");
							break;
						case 117:	//防火卷帘控制器
							faultData.setUnitType("防火卷帘控制器");
							break;
						case 118:	//防火门监控器
							faultData.setUnitType("防火门监控器");
							break;
						case 121:	//警报装置
							faultData.setUnitType("警报装置");
							break;
						case 128:	//打印机故障、三江打印机故障
							faultData.setUnitType("打印机故障");
							break;
						case 251:	//主电故障、三江主电故障
							faultData.setUnitType("主电故障");
							break;
						case 249:	//备电故障、三江备电故障
							faultData.setUnitType("备电故障");
							break;
					}
					switch(systemTab){
					case 1:	//火灾报警系统
						faultData.setSystemTab("火灾报警系统");
						break;
					case 10:	//消防联动控制器
						faultData.setSystemTab("消防联动控制器");
						break;
					case 11:	//消火栓系统
						faultData.setSystemTab("消火栓系统");
						break;
					case 12:	//自动喷水灭火系统
						faultData.setSystemTab("自动喷水灭火系统");
						break;
					case 13:	//气体灭火系统
						faultData.setSystemTab("气体灭火系统");
						break;
					case 14:	//水喷雾灭火系统（泵启动方式）
						faultData.setSystemTab("水喷雾灭火系统（泵启动方式）");
						break;
					case 15:	//水喷雾灭火系统（压力容器启动方式）
						faultData.setSystemTab("水喷雾灭火系统（压力容器启动方式）");
						break;
					case 16:	//泡沫灭火系统
						faultData.setSystemTab("泡沫灭火系统");
						break;
					case 17:	//干粉灭火系统
						faultData.setSystemTab("干粉灭火系统");
						break;
					case 18:	//防烟排烟系统
						faultData.setSystemTab("防烟排烟系统");
						break;
					case 19:	//防火门及卷帘系统
						faultData.setSystemTab("防火门及卷帘系统");
						break;
					case 20:	//消防电梯
						faultData.setSystemTab("消防电梯");
						break;
					case 21:	//消防应急广播
						faultData.setSystemTab("消防应急广播");
						break;
					case 22:	//消防应急照明和疏散指示系统
						faultData.setSystemTab("消防应急照明和疏散指示系统");
						break;
					case 23:	//消防电源
						faultData.setSystemTab("消防电源");
						break;
					case 24:	//消防电话
						faultData.setSystemTab("消防电话");
						break;
					}
					faultData.setTabbingCmd("上传建筑消防设施部件运行状态");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setUnitAddress(unitAddress);
					faultData.setUnitMemos(unitMemos);
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 3:	//上传建筑消防设施部件模拟量值
				break;
			case 4:	//上传建筑消防设施操作信息==================
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//系统标志
					int systemAddress = dataN[30+i*10]&0xff;	//系统地址
					int operaTab = dataN[31+i*10]&0xff;	//操作标志
					int operator = dataN[32+i*10]&0xff;	//操作员编号
					faultData.setTabbingCmd("上传建筑消防设施操作信息");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//复位
						stateTab.put(1, "复位");
						stateTabList.add("复位");
					}else if((operaTab&0x01)==0){	//无操作
						stateTab.put(1, "无操作");
					}
					if((operaTab&0x02)==2){	//消音
						stateTab.put(2, "消音");
						stateTabList.add("消音");
					}else if((operaTab&0x02)==0){	//无操作
						stateTab.put(2, "无操作");
					}
					if((operaTab&0x04)==4){	//手动报警
						stateTab.put(3, "手动报警");
						stateTabList.add("手动报警");
					}else if((operaTab&0x04)==0){	//无操作
						stateTab.put(3, "无操作");
					}
					if((operaTab&0x08)==8){	//警情消除
						stateTab.put(4, "警情消除");
						stateTabList.add("警情消除");
					}else if((operaTab&0x08)==0){	//无操作
						stateTab.put(4, "无操作");
					}
					if((operaTab&0x10)==16){	//自检
						stateTab.put(5, "自检");
						stateTabList.add("自检");
					}else if((operaTab&0x10)==0){	//无操作
						stateTab.put(5, "无操作");
					}
					if((operaTab&0x20)==32){	//确认
						stateTab.put(6, "确认");
						stateTabList.add("确认");
					}else if((operaTab&0x20)==0){	//无操作
						stateTab.put(6, "无操作");
					}
					if((operaTab&0x40)==64){	//测试
						stateTab.put(7, "测试");
						stateTabList.add("测试");
					}else if((operaTab&0x40)==0){	//无操作
						stateTab.put(7, "无操作");
					}
					faultData.setStateTab(stateTab);
					switch(systemTab){
					case 1:	//火灾报警系统
						faultData.setSystemTab("火灾报警系统");
						break;
					case 10:	//消防联动控制器
						faultData.setSystemTab("消防联动控制器");
						break;
					case 11:	//消火栓系统
						faultData.setSystemTab("消火栓系统");
						break;
					case 12:	//自动喷水灭火系统
						faultData.setSystemTab("自动喷水灭火系统");
						break;
					case 13:	//气体灭火系统
						faultData.setSystemTab("气体灭火系统");
						break;
					case 14:	//水喷雾灭火系统（泵启动方式）
						faultData.setSystemTab("水喷雾灭火系统（泵启动方式）");
						break;
					case 15:	//水喷雾灭火系统（压力容器启动方式）
						faultData.setSystemTab("水喷雾灭火系统（压力容器启动方式）");
						break;
					case 16:	//泡沫灭火系统
						faultData.setSystemTab("泡沫灭火系统");
						break;
					case 17:	//干粉灭火系统
						faultData.setSystemTab("干粉灭火系统");
						break;
					case 18:	//防烟排烟系统
						faultData.setSystemTab("防烟排烟系统");
						break;
					case 19:	//防火门及卷帘系统
						faultData.setSystemTab("防火门及卷帘系统");
						break;
					case 20:	//消防电梯
						faultData.setSystemTab("消防电梯");
						break;
					case 21:	//消防应急广播
						faultData.setSystemTab("消防应急广播");
						break;
					case 22:	//消防应急照明和疏散指示系统
						faultData.setSystemTab("消防应急照明和疏散指示系统");
						break;
					case 23:	//消防电源
						faultData.setSystemTab("消防电源");
						break;
					case 24:	//消防电话
						faultData.setSystemTab("消防电话");
						break;
					}
				}
				break;
			case 5:	//上传建筑消防设施软件版本
				break;
			case 6:	//上传建筑消防设施系统配置情况
				break;
			case 7:	//上传建筑消防设施系统部件配置情况
				break;
			case 8:	//上传建筑消防设施系统时间
				break;
			case 22:	//预留，三江心跳
				faultData.setSystemTab("三江心跳");
				byte ackByte[] = getAckByte(dataN,22);
				faultData.setAckByte(ackByte);
				break;
			case 21:	//上传用户信息传输装置运行状态******===============
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("上传用户信息传输装置运行状态");
					int runTab = dataN[29+i*7]&0xff;		//运行标志
					if((runTab&0x01)==1){	//正常监视状态
//						stateTab.put(1, "正常监视状态");
//						stateTabList.add("正常监视状态");
					}else if((runTab&0x01)==0){	//测试状态
						stateTab.put(1, "测试状态");
					}
					if((runTab&0x02)==2){	//火警
						stateTab.put(2, "火警");
						stateTabList.add("火警");
					}else if((runTab&0x02)==0){	//无火警
						stateTab.put(2, "无火警");
					}
					if((runTab&0x04)==4){	//故障
						stateTab.put(3, "故障");
						stateTabList.add("故障");
					}else if((runTab&0x04)==0){	//无故障
						stateTab.put(3, "无故障");
					}
					if((runTab&0x08)==8){	//主电故障
						stateTab.put(4, "主电故障");
						stateTabList.add("主电故障");
					}else if((runTab&0x08)==0){	//主电正常
						stateTab.put(4, "主电正常");
					}
					if((runTab&0x10)==16){	//备电故障
						stateTab.put(5, "备电故障");
						stateTabList.add("备电故障");
					}else if((runTab&0x10)==0){	//备电正常
						stateTab.put(5, "备电正常");
					}
					if((runTab&0x20)==32){	//与监控中心通信信道故障
						stateTab.put(6, "与监控中心通信信道故障");
						stateTabList.add("与监控中心通信信道故障");
					}else if((runTab&0x20)==0){	//通信信道正常
						stateTab.put(6, "通信信道正常");
					}
					if((runTab&0x40)==64){	//监测连接线路故障
						stateTab.put(7, "监测连接线路故障");
						stateTabList.add("监测连接线路故障");
					}else if((runTab&0x40)==0){	//监测连接线路正常
						stateTab.put(7, "监测连接线路正常");
					}
					faultData.setStateTab(stateTab);
				}
				break;
			case 24:	//上传用户信息传输装置操作信息记录===================
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("上传用户信息传输装置操作信息记录");
					int operaTab = dataN[29+i*8]&0xff;	//操作标志
					int operator = dataN[30+i*8]&0xff;	//操作员编号
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//复位
						stateTab.put(1, "复位");
						stateTabList.add("复位");
					}else if((operaTab&0x01)==0){	//无操作
						stateTab.put(1, "无操作");
					}
					if((operaTab&0x02)==2){	//消音
						stateTab.put(2, "消音");
						stateTabList.add("消音");
					}else if((operaTab&0x02)==0){	//无操作
						stateTab.put(2, "无操作");
					}
					if((operaTab&0x04)==4){	//手动报警
						stateTab.put(3, "手动报警");
						stateTabList.add("手动报警");
						byte[] lzack4 ={(byte)0x40,(byte)0x40,(byte)0x29,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0A,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x01,(byte)0x20,(byte)0x01,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x22,(byte)0x23,(byte)0x23}; 
						faultData.setAckByte(lzack4);
					}else if((operaTab&0x04)==0){	//无操作
						stateTab.put(3, "无操作");
					}
					if((operaTab&0x08)==8){	//警情消除
						stateTab.put(4, "警情消除");
						stateTabList.add("警情消除");
					}else if((operaTab&0x08)==0){	//无操作
						stateTab.put(4, "无操作");
					}
					if((operaTab&0x10)==16){	//自检
						stateTab.put(5, "自检");
						stateTabList.add("自检");
					}else if((operaTab&0x10)==0){	//无操作
						stateTab.put(5, "无操作");
					}
					if((operaTab&0x20)==32){	//查岗应答
						stateTab.put(6, "查岗应答");
						stateTabList.add("查岗应答");
					}else if((operaTab&0x20)==0){	//无操作
						stateTab.put(6, "无操作");
					}
					if((operaTab&0x40)==64){	//测试对应联众的心跳
						stateTab.put(7, "心跳");
						stateTabList.add("心跳");
						byte[] LZackByte = {(byte)0x40,(byte)0x40,(byte)0x04,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x1A,(byte)0x02,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x03,(byte)0x00,(byte)0x02,(byte)0x5B,(byte)0x01,(byte)0x01,(byte)0xC2,(byte)0x23,(byte)0x23};
						faultData.setAckByte(LZackByte);
						return null;
					}else if((operaTab&0x40)==0){	//无操作
						stateTab.put(7, "无操作");
					}
					faultData.setStateTab(stateTab);
				}
				break;
			case 25:	//上传用户信息传输装置软件版本
				break;
			case 26:	//上传用户信息传输装置配置情况
				break;
			case 28:	//上传用户信息传输装置系统时间
				break;
			case 30:	//传输装置 GSM 故障
				stateTabList.add("GSM 故障");
				faultData.setTabbingCmd("传输装置 GSM 故障");
				faultData.setSystemAddress("0");
				faultData.setUnitAddress("0");
				byte ackByte30[] = getAckByte(dataN,30);
				faultData.setAckByte(ackByte30);
				break;
			case 61:	//读建筑消防设施系统状态
				break;
			case 62:	//读建筑消防设施部件运行状态
				break;
			case 63:	//读建筑消防设施部件模拟量值
				break;
			case 64:	//读建筑消防设施操作信息
				break;
			case 65:	//读建筑消防设施软件版本
				break;
			case 66:	//读建筑消防设施系统配置情况
				break;
			case 67:	//读建筑消防设施部件配置情况
				break;
			case 68:	//读建筑消防设施系统时间
				break;
			case 81:	//读用户信息传输装置运行状态
				break;
			case 84:	//读用户信息传输装置操作信息记录
				break;
			case 85:	//读用户信息传输装置软件版本
				break;
			case 86:	//读用户信息传输装置配置情况
				break;
			case 88:	//读用户信息传输装置系统时间
				break;
			case 89:	//初始化用户信息传输装置
				break;
			case 90:	//同步用户信息传输装置时钟
				break;
			case 91:	//查岗命令
				break;
			}
		}
		faultData.setStateTabList(stateTabList);
		if(stateTabList!=null&&stateTabList.size()>0){
			faultData.setPushState(1);
		}
		return faultData;
	}
	
	public static FaultDataEntity sendSJDataFault(byte[] dataN,int byteLen,int controlCmd) throws Exception{
		FaultDataEntity faultData = new FaultDataEntity();
//		List<String> stateTab = new ArrayList<String>();
		Map<Integer,String> stateTab = new HashMap<Integer,String>();
		List<String> stateTabList = new ArrayList<String>();
		if(controlCmd==2){	//发送数据处理
			int tabbingCmd = dataN[27]&0xff;	//标识命令、类型标志
			int infoNum = dataN[28]&0xff;		//信息数目
			switch(tabbingCmd){
			case 1:	//上传建筑消防设施系统状态============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//系统标志
					int systemAddress = dataN[30+i*10]&0xff;	//系统地址
					int systemState = (dataN[32+i*10]&0xff)+((dataN[31+i*10]&0xff)<<8);	//系统状态
					
					if((systemState&0x01)==1){	//正常运行状态
//						stateTab.put(1, "正常运行状态");
//						stateTabList.add("正常运行状态");
					}else if((systemState&0x01)==0){	//测试状态
						stateTab.put(1, "测试状态");
					}
					if((systemState&0x02)==2){	//火警
						stateTab.put(2, "火警");
						stateTabList.add("火警");
					}else if((systemState&0x02)==0){	//无火警
						stateTab.put(2, "无火警");
					}
					if((systemState&0x04)==4){	//故障
						stateTab.put(3, "故障");
						stateTabList.add("故障");
					}else if((systemState&0x04)==0){	//无故障
						stateTab.put(3, "无故障");
					}
					if((systemState&0x08)==8){	//屏蔽
						stateTab.put(4, "屏蔽");
						stateTabList.add("屏蔽");
					}else if((systemState&0x08)==0){	//无屏蔽
						stateTab.put(4, "无屏蔽");
					}
					if((systemState&0x10)==16){	//监管
						stateTab.put(5, "监管");
						stateTabList.add("监管");
					}else if((systemState&0x10)==0){	//无监管
						stateTab.put(5, "无监管");
					}
					if((systemState&0x20)==32){	//启动（开启）
						stateTab.put(6, "启动（开启）");
						stateTabList.add("启动（开启）");
					}else if((systemState&0x20)==0){	//停止（关闭）
						stateTab.put(6, "停止（关闭）");
					}
					if((systemState&0x40)==64){	//反馈
						stateTab.put(7, "反馈");
						stateTabList.add("反馈");
					}else if((systemState&0x40)==0){	//无反馈
						stateTab.put(7, "无反馈");
					}
					if((systemState&0x80)==128){	//延时状态
						stateTab.put(8, "延时状态");
						stateTabList.add("延时状态");
					}else if((systemState&0x80)==0){	//未延时
						stateTab.put(8, "未延时");
					}
					if((systemState&0x100)==256){	//主电故障
						stateTab.put(9, "主电故障");
						stateTabList.add("主电故障");
					}else if((systemState&0x100)==0){	//主电正常
						stateTab.put(9, "主电正常");
					}
					if((systemState&0x200)==512){	//备电故障
						stateTab.put(10, "备电故障");
						stateTabList.add("备电故障");
					}else if((systemState&0x200)==0){	//备电正常
						stateTab.put(10, "备电正常");
					}
					if((systemState&0x400)==1024){	//总线故障
						stateTab.put(11, "总线故障");
						stateTabList.add("总线故障");
					}else if((systemState&0x400)==0){	//总线正常
						stateTab.put(11, "总线正常");
					}
					if((systemState&0x800)==2048){	//手动状态
						stateTab.put(12, "手动状态");
						stateTabList.add("手动状态");
					}else if((systemState&0x800)==0){	//自动状态
						stateTab.put(12, "自动状态");
					}
					if((systemState&0x1000)==4096){	//配置改变
						stateTab.put(13, "配置改变");
						stateTabList.add("配置改变");
					}else if((systemState&0x1000)==0){	//无配置改变
						stateTab.put(13, "无配置改变");
					}
					if((systemState&0x2000)==8192){	//复位
						stateTab.put(14, "复位");
						stateTabList.add("复位");
					}else if((systemState&0x2000)==0){	//正常
						stateTab.put(14, "正常");
					}
					
					switch(systemTab){
					case 1:	//火灾报警系统
						faultData.setSystemTab("火灾报警系统");
						break;
					case 10:	//消防联动控制器
						faultData.setSystemTab("消防联动控制器");
						break;
					case 11:	//消火栓系统
						faultData.setSystemTab("消火栓系统");
						break;
					case 12:	//自动喷水灭火系统
						faultData.setSystemTab("自动喷水灭火系统");
						break;
					case 13:	//气体灭火系统
						faultData.setSystemTab("气体灭火系统");
						break;
					case 14:	//水喷雾灭火系统（泵启动方式）
						faultData.setSystemTab("水喷雾灭火系统（泵启动方式）");
						break;
					case 15:	//水喷雾灭火系统（压力容器启动方式）
						faultData.setSystemTab("水喷雾灭火系统（压力容器启动方式）");
						break;
					case 16:	//泡沫灭火系统
						faultData.setSystemTab("泡沫灭火系统");
						break;
					case 17:	//干粉灭火系统
						faultData.setSystemTab("干粉灭火系统");
						break;
					case 18:	//防烟排烟系统
						faultData.setSystemTab("防烟排烟系统");
						break;
					case 19:	//防火门及卷帘系统
						faultData.setSystemTab("防火门及卷帘系统");
						break;
					case 20:	//消防电梯
						faultData.setSystemTab("消防电梯");
						break;
					case 21:	//消防应急广播
						faultData.setSystemTab("消防应急广播");
						break;
					case 22:	//消防应急照明和疏散指示系统
						faultData.setSystemTab("消防应急照明和疏散指示系统");
						break;
					case 23:	//消防电源
						faultData.setSystemTab("消防电源");
						break;
					case 24:	//消防电话
						faultData.setSystemTab("消防电话");
						break;
					}
					faultData.setTabbingCmd("上传建筑消防设施系统状态");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 2:	//上传建筑消防设施部件运行状态==============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*46]&0xff;		//系统标志
					int systemAddress = dataN[30+i*46]&0xff;	//系统地址
					int unitType = dataN[31+i*46]&0xff;		//部件类型
					byte[] macByte = new byte[4];
		    		for(int j=0;j<4;j++){
		    			macByte[j] = dataN[32+j+i*46];
		    		}//更换三江协议获取部件地址
		    		
		    		
		    		String unitAddress = IntegerTo16.bytes2Hex(macByte);
					/*int bj1 = (dataN[32+i*46]&0xff)+((dataN[33+i*46]&0xff)<<8); 	//位号
					int bj2 = (dataN[34+i*46]&0xff)+((dataN[35+i*46]&0xff)<<8); 	//区号
		    		String unitAddress = bj1+"位"+bj2+"区";	//部件地址
*/		    		byte[] unitMemo = new byte[31];							//部件说明
		    		for (int j = 0; j < 31; j++) {
		    			unitMemo[i] = dataN[38+j+i*46];
					}
		    		String unitMemos = new String(unitMemo,"gb18030");		//部件说明转成汉字
					int unitState = (dataN[37+i*46]&0xff)+((dataN[36+i*46]&0xff)<<8);	//部件状态
					if(unitState==11){
						stateTabList.add("屏蔽消除");
					}else if(unitState==12){
						stateTabList.add("故障消除");
					}else if(unitState==13){
						stateTabList.add("请求消除");
					}else if(unitState==14){
						stateTabList.add("反馈消除");
					}else if(unitState==15){
						stateTabList.add("电源故障消除");
					}else{
						if((unitState&0x01)==1){	//正常运行状态
//							stateTab.put(1, "正常运行状态");
//							stateTabList.add("正常运行状态");
						}else if((unitState&0x01)==0){	//测试状态
//							stateTab.put(1, "测试状态");
						}
						if((unitState&0x02)==2){	//火警
							stateTab.put(2, "火警");
							stateTabList.add("火警");
						}else if((unitState&0x02)==0){	//无火警
//							stateTab.put(2, "无火警");
						}
						if((unitState&0x04)==4){	//故障
							stateTab.put(3, "故障");
							stateTabList.add("故障");
						}else if((unitState&0x04)==0){	//无故障
//							stateTab.put(3, "无故障");、
						}
						if((unitState&0x08)==8){	//屏蔽
							stateTab.put(4, "屏蔽");
							stateTabList.add("屏蔽");
						}else if((unitState&0x08)==0){	//无屏蔽
//							stateTab.put(4, "无屏蔽");
						}
						if((unitState&0x10)==16){	//监管
							stateTab.put(5, "监管");
							stateTabList.add("监管");
						}else if((unitState&0x10)==0){	//无监管
//							stateTab.put(5, "无监管");
						}
						if((unitState&0x20)==32){	//启动（开启）
							stateTab.put(6, "启动（开启）");
							stateTabList.add("启动（开启）");
						}else if((unitState&0x20)==0){	//停止（关闭）
//							stateTab.put(6, "停止（关闭）");
						}
						if((unitState&0x40)==64){	//反馈
							stateTab.put(7, "反馈");
							stateTabList.add("反馈");
						}else if((unitState&0x40)==0){	//无反馈
//							stateTab.put(7, "无反馈");
						}
						if((unitState&0x80)==128){	//延时状态
							stateTab.put(8, "延时状态");
							stateTabList.add("延时状态");
						}else if((unitState&0x80)==0){	//未延时
//							stateTab.put(8, "未延时");
						}
						if((unitState&0x100)==256){	//电源故障
							stateTab.put(9, "电源故障");
							stateTabList.add("电源故障");
						}else if((unitState&0x100)==0){	//电源正常
//							stateTab.put(9, "电源正常");
						}
						if(unitState==512){	//火警
							stateTab.put(10, "火警");
							stateTabList.add("火警");
						}else if((unitState&0x100)==0){	//电源正常
//							stateTab.put(10, "无火警");
						}
					}
					switch(unitType){		//部件类型
						case 1:	//	火灾报警控制器
							faultData.setUnitType("火灾报警控制器");
							break;
						case 10:	//可燃气体探测器
							faultData.setUnitType("可燃气体探测器");
							break;
						case 11:	//典型可燃气体探测器
							faultData.setUnitType("典型可燃气体探测器");
							break;
						case 12:	//独立式可燃气体探测器
							faultData.setUnitType("独立式可燃气体探测器");
							break;
						case 13:	//线型可燃气体探测器
							faultData.setUnitType("线型可燃气体探测器");
							break;
						case 16:	//电气火灾监控报警器
							faultData.setUnitType("电气火灾监控报警器");
							break;
						case 17:	//剩余电流式电气火灾监控探测器
							faultData.setUnitType("剩余电流式电气火灾监控探测器");
							break;
						case 18:	//测温式电气火灾监控探测器
							faultData.setUnitType("测温式电气火灾监控探测器");
							break;
						case 21:	//探测回路 改成三江接口板回路
//							faultData.setUnitType("探测回路");(接口板回路)故障
							faultData.setUnitType("(接口板回路)故障");
							break;
						case 22:	//火灾显示盘
							faultData.setUnitType("火灾显示盘");
							break;
						case 23:	//手动火灾报警按钮
							faultData.setUnitType("手动火灾报警按钮");
							break;
						case 24:	//消火栓按钮
							faultData.setUnitType("消火栓按钮");
							break;
						case 25:	//火灾探测器
							faultData.setUnitType("火灾探测器");
							break;
						case 30:	//感温火灾探测器
							faultData.setUnitType("感温火灾探测器");
							break;
						case 31:	//点型感温火灾探测器
							faultData.setUnitType("点型感温火灾探测器");
							break;
						case 32:	//点型感温火灾探测器（S型）
							faultData.setUnitType("点型感温火灾探测器（S型）");
							break;
						case 33:	//点型感温火灾探测器（R型）
							faultData.setUnitType("点型感温火灾探测器（R型）");
							break;
						case 34:	//线型感温火灾探测器
							faultData.setUnitType("线型感温火灾探测器");
							break;
						case 35:	//线型感温火灾探测器(S型)
							faultData.setUnitType("线型感温火灾探测器(S型)");
							break;
						case 36:	//线型感温火灾探测器(R型)
							faultData.setUnitType("线型感温火灾探测器(R型)");
							break;
						case 37:	//光纤感温火灾探测器
							faultData.setUnitType("光纤感温火灾探测器");
							break;
						case 40:	//感烟火灾探测器
							faultData.setUnitType("感烟火灾探测器");
							break;
						case 41:	//点型离子感烟火灾探测器
							faultData.setUnitType("点型离子感烟火灾探测器");
							break; 
						case 42:	//点型光电感烟火灾探测器
							faultData.setUnitType("点型光电感烟火灾探测器");
							break; 
						case 43:	//线型光束感烟火灾探测器
							faultData.setUnitType("线型光束感烟火灾探测器");
							break; 
						case 44:	//吸气式感烟火灾探测器
							faultData.setUnitType("吸气式感烟火灾探测器");
							break; 
						case 50:	//复合式火灾探测器
							faultData.setUnitType("复合式火灾探测器");
							break; 
						case 51:	//复合式感烟感温火灾探测器
							faultData.setUnitType("复合式感烟感温火灾探测器");
							break; 
						case 52:	//复合式感光感温火灾探测器
							faultData.setUnitType("复合式感光感温火灾探测器");
							break; 
						case 53:	//复合式感光感烟火灾探测器
							faultData.setUnitType("复合式感光感烟火灾探测器");
							break; 
						case 61:	//紫外火焰探测器
							faultData.setUnitType("紫外火焰探测器");
							break; 
						case 62:	//红外火焰探测器
							faultData.setUnitType("红外火焰探测器");
							break; 
						case 69:	//感光火灾探测器
							faultData.setUnitType("感光火灾探测器");
							break; 
						case 74:	//气体探测器
							faultData.setUnitType("气体探测器");
							break; 
						case 78:	//图像摄像方式火灾探测器
							faultData.setUnitType("图像摄像方式火灾探测器");
							break;
						case 79:	//感声火灾测器
							faultData.setUnitType("感声火灾测器");
							break; 
						case 81:	//气体灭火控制器
							faultData.setUnitType("气体灭火控制器");
							break; 
						case 82:	//消防电气控制装置
							faultData.setUnitType("消防电气控制装置");
							break; 
						case 83:	//消防控制室图形显示装置
							faultData.setUnitType("消防控制室图形显示装置");
							break; 
						case 84:	//模块
							faultData.setUnitType("模块");
							break;
						case 85:	//输入模块
							faultData.setUnitType("输入模块");
							break;
						case 86:	//输出模块
							faultData.setUnitType("输出模块");
							break;
						case 87:	//输入输出模块
							faultData.setUnitType("输入输出模块");
							break;
						case 88:	//中继模块
							faultData.setUnitType("中继模块");
							break;
						case 91:	//消防水泵
							faultData.setUnitType("消防水泵");
							break;
						case 92:	//消防水箱
							faultData.setUnitType("消防水箱");
							break;
						case 95:	//喷淋泵
							faultData.setUnitType("喷淋泵");
							break;
						case 96:	//水流指示器
							faultData.setUnitType("水流指示器");
							break;
						case 97:	//信号阀
							faultData.setUnitType("信号阀");
							break;
						case 98:	//报警阀
							faultData.setUnitType("报警阀");
							break;
						case 99:	//压力开关
							faultData.setUnitType("压力开关");
							break;
						case 101:	//阀驱动装置
							faultData.setUnitType("阀驱动装置");
							break;
						case 102:	//防火门
							faultData.setUnitType("防火门");
							break;
						case 103:	//防火阀
							faultData.setUnitType("防火阀");
							break;
						case 104:	//通风空调
							faultData.setUnitType("通风空调");
							break;
						case 105:	//泡沫液泵
							faultData.setUnitType("沫液泵");
							break;
						case 106:	//管网电磁阀
							faultData.setUnitType("管网电磁阀");
							break;
						case 111:	//防烟排烟风机
							faultData.setUnitType("防烟排烟风机");
							break;
						case 113:	//排烟防火阀
							faultData.setUnitType("排烟防火阀");
							break;
						case 114:	//常闭送风口
							faultData.setUnitType("常闭送风口");
							break;
						case 115:	//排烟口
							faultData.setUnitType("排烟口");
							break;
						case 116:	//电控挡烟垂壁
							faultData.setUnitType("电控挡烟垂壁");
							break;
						case 117:	//防火卷帘控制器
							faultData.setUnitType("防火卷帘控制器");
							break;
						case 118:	//防火门监控器
							faultData.setUnitType("防火门监控器");
							break;
						case 121:	//警报装置
							faultData.setUnitType("警报装置");
							break;
						case 128:	//打印机故障、三江打印机故障
							faultData.setUnitType("打印机故障");
							break;
						case 251:	//主电故障、三江主电故障
							faultData.setUnitType("主电故障");
							break;
						case 249:	//备电故障、三江备电故障
							faultData.setUnitType("备电故障");
							break;
					}
					switch(systemTab){
					case 1:	//火灾报警系统
						faultData.setSystemTab("火灾报警系统");
						break;
					case 10:	//消防联动控制器
						faultData.setSystemTab("消防联动控制器");
						break;
					case 11:	//消火栓系统
						faultData.setSystemTab("消火栓系统");
						break;
					case 12:	//自动喷水灭火系统
						faultData.setSystemTab("自动喷水灭火系统");
						break;
					case 13:	//气体灭火系统
						faultData.setSystemTab("气体灭火系统");
						break;
					case 14:	//水喷雾灭火系统（泵启动方式）
						faultData.setSystemTab("水喷雾灭火系统（泵启动方式）");
						break;
					case 15:	//水喷雾灭火系统（压力容器启动方式）
						faultData.setSystemTab("水喷雾灭火系统（压力容器启动方式）");
						break;
					case 16:	//泡沫灭火系统
						faultData.setSystemTab("泡沫灭火系统");
						break;
					case 17:	//干粉灭火系统
						faultData.setSystemTab("干粉灭火系统");
						break;
					case 18:	//防烟排烟系统
						faultData.setSystemTab("防烟排烟系统");
						break;
					case 19:	//防火门及卷帘系统
						faultData.setSystemTab("防火门及卷帘系统");
						break;
					case 20:	//消防电梯
						faultData.setSystemTab("消防电梯");
						break;
					case 21:	//消防应急广播
						faultData.setSystemTab("消防应急广播");
						break;
					case 22:	//消防应急照明和疏散指示系统
						faultData.setSystemTab("消防应急照明和疏散指示系统");
						break;
					case 23:	//消防电源
						faultData.setSystemTab("消防电源");
						break;
					case 24:	//消防电话
						faultData.setSystemTab("消防电话");
						break;
					}
					faultData.setTabbingCmd("上传建筑消防设施部件运行状态");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setUnitAddress(unitAddress);
					faultData.setUnitMemos(unitMemos);
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 3:	//上传建筑消防设施部件模拟量值
				break;
			case 4:	//上传建筑消防设施操作信息==================
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//系统标志
					int systemAddress = dataN[30+i*10]&0xff;	//系统地址
					int operaTab = dataN[31+i*10]&0xff;	//操作标志
					int operator = dataN[32+i*10]&0xff;	//操作员编号
					faultData.setTabbingCmd("上传建筑消防设施操作信息");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//复位
						stateTab.put(1, "复位");
						stateTabList.add("复位");
					}else if((operaTab&0x01)==0){	//无操作
//						stateTab.put(1, "无操作");
					}
					if((operaTab&0x02)==2){	//消音
						stateTab.put(2, "消音");
						stateTabList.add("消音");
					}else if((operaTab&0x02)==0){	//无操作
//						stateTab.put(2, "无操作");
					}
					if((operaTab&0x04)==4){	//手动报警
						stateTab.put(3, "手动报警");
						stateTabList.add("手动报警");
					}else if((operaTab&0x04)==0){	//无操作
//						stateTab.put(3, "无操作");
					}
					if((operaTab&0x08)==8){	//警情消除
						stateTab.put(4, "警情消除");
						stateTabList.add("警情消除");
					}else if((operaTab&0x08)==0){	//无操作
//						stateTab.put(4, "无操作");
					}
					if((operaTab&0x10)==16){	//自检
						stateTab.put(5, "自检");
						stateTabList.add("自检");
					}else if((operaTab&0x10)==0){	//无操作
//						stateTab.put(5, "无操作");
					}
					if((operaTab&0x20)==32){	//确认
						stateTab.put(6, "确认");
						stateTabList.add("确认");
					}else if((operaTab&0x20)==0){	//无操作
//						stateTab.put(6, "无操作");
					}
					if((operaTab&0x40)==64){	//测试
						stateTab.put(7, "测试");
						stateTabList.add("测试");
					}else if((operaTab&0x40)==0){	//无操作
//						stateTab.put(7, "无操作");
					}
					faultData.setStateTab(stateTab);
					switch(systemTab){
					case 1:	//火灾报警系统
						faultData.setSystemTab("火灾报警系统");
						break;
					case 10:	//消防联动控制器
						faultData.setSystemTab("消防联动控制器");
						break;
					case 11:	//消火栓系统
						faultData.setSystemTab("消火栓系统");
						break;
					case 12:	//自动喷水灭火系统
						faultData.setSystemTab("自动喷水灭火系统");
						break;
					case 13:	//气体灭火系统
						faultData.setSystemTab("气体灭火系统");
						break;
					case 14:	//水喷雾灭火系统（泵启动方式）
						faultData.setSystemTab("水喷雾灭火系统（泵启动方式）");
						break;
					case 15:	//水喷雾灭火系统（压力容器启动方式）
						faultData.setSystemTab("水喷雾灭火系统（压力容器启动方式）");
						break;
					case 16:	//泡沫灭火系统
						faultData.setSystemTab("泡沫灭火系统");
						break;
					case 17:	//干粉灭火系统
						faultData.setSystemTab("干粉灭火系统");
						break;
					case 18:	//防烟排烟系统
						faultData.setSystemTab("防烟排烟系统");
						break;
					case 19:	//防火门及卷帘系统
						faultData.setSystemTab("防火门及卷帘系统");
						break;
					case 20:	//消防电梯
						faultData.setSystemTab("消防电梯");
						break;
					case 21:	//消防应急广播
						faultData.setSystemTab("消防应急广播");
						break;
					case 22:	//消防应急照明和疏散指示系统
						faultData.setSystemTab("消防应急照明和疏散指示系统");
						break;
					case 23:	//消防电源
						faultData.setSystemTab("消防电源");
						break;
					case 24:	//消防电话
						faultData.setSystemTab("消防电话");
						break;
					}
				}
				break;
			case 5:	//上传建筑消防设施软件版本
				break;
			case 6:	//上传建筑消防设施系统配置情况
				break;
			case 7:	//上传建筑消防设施系统部件配置情况
				break;
			case 8:	//上传建筑消防设施系统时间
				break;
			case 22:	//预留，三江心跳
				faultData.setSystemTab("三江心跳");
				byte ackByte[] = getAckByte(dataN,22);
				faultData.setAckByte(ackByte);
				break;
			case 21:	//上传用户信息传输装置运行状态******===============
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("上传用户信息传输装置运行状态");
					int runTab = dataN[29+i*7]&0xff;		//运行标志
					if((runTab&0x01)==1){	//正常监视状态
//						stateTab.put(1, "正常监视状态");
//						stateTabList.add("正常监视状态");
					}else if((runTab&0x01)==0){	//测试状态
//						stateTab.put(1, "测试状态");
					}
					if((runTab&0x02)==2){	//火警
						stateTab.put(2, "火警");
						stateTabList.add("火警");
					}else if((runTab&0x02)==0){	//无火警
//						stateTab.put(2, "无火警");
					}
					if((runTab&0x04)==4){	//故障
						stateTab.put(3, "故障");
						stateTabList.add("故障");
					}else if((runTab&0x04)==0){	//无故障
//						stateTab.put(3, "无故障");
					}
					if((runTab&0x08)==8){	//主电故障
						stateTab.put(4, "主电故障");
						stateTabList.add("主电故障");
					}else if((runTab&0x08)==0){	//主电正常
//						stateTab.put(4, "主电正常");
					}
					if((runTab&0x10)==16){	//备电故障
						stateTab.put(5, "备电故障");
						stateTabList.add("备电故障");
					}else if((runTab&0x10)==0){	//备电正常
//						stateTab.put(5, "备电正常");
					}
					if((runTab&0x20)==32){	//与监控中心通信信道故障
						stateTab.put(6, "与监控中心通信信道故障");
						stateTabList.add("与监控中心通信信道故障");
					}else if((runTab&0x20)==0){	//通信信道正常
						stateTab.put(6, "通信信道正常");
					}
					if((runTab&0x40)==64){	//监测连接线路故障
						stateTab.put(7, "监测连接线路故障");
						stateTabList.add("监测连接线路故障");
					}else if((runTab&0x40)==0){	//监测连接线路正常
						stateTab.put(7, "监测连接线路正常");
					}
					faultData.setStateTab(stateTab);
				}
				break;
			case 24:	//上传用户信息传输装置操作信息记录===================
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("上传用户信息传输装置操作信息记录");
					int operaTab = dataN[29+i*8]&0xff;	//操作标志
					int operator = dataN[30+i*8]&0xff;	//操作员编号
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//复位
						stateTab.put(1, "复位");
						stateTabList.add("复位");
					}else if((operaTab&0x01)==0){	//无操作
//						stateTab.put(1, "无操作");
					}
					if((operaTab&0x02)==2){	//消音
						stateTab.put(2, "消音");
						stateTabList.add("消音");
					}else if((operaTab&0x02)==0){	//无操作
//						stateTab.put(2, "无操作");
					}
					if((operaTab&0x04)==4){	//手动报警
						stateTab.put(3, "手动报警");
						stateTabList.add("手动报警");
						byte[] lzack4 ={(byte)0x40,(byte)0x40,(byte)0x29,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0A,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x01,(byte)0x20,(byte)0x01,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x22,(byte)0x23,(byte)0x23}; 
						faultData.setAckByte(lzack4);
					}else if((operaTab&0x04)==0){	//无操作
//						stateTab.put(3, "无操作");
					}
					if((operaTab&0x08)==8){	//警情消除
						stateTab.put(4, "警情消除");
						stateTabList.add("警情消除");
					}else if((operaTab&0x08)==0){	//无操作
//						stateTab.put(4, "无操作");
					}
					if((operaTab&0x10)==16){	//自检
						stateTab.put(5, "自检");
						stateTabList.add("自检");
					}else if((operaTab&0x10)==0){	//无操作
//						stateTab.put(5, "无操作");
					}
					if((operaTab&0x20)==32){	//查岗应答
						stateTab.put(6, "查岗应答");
						stateTabList.add("查岗应答");
					}else if((operaTab&0x20)==0){	//无操作
//						stateTab.put(6, "无操作");
					}
					if((operaTab&0x40)==64){	//测试对应联众的心跳
						stateTab.put(7, "心跳");
						stateTabList.add("心跳");
						byte[] LZackByte = {(byte)0x40,(byte)0x40,(byte)0x04,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x1A,(byte)0x02,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x03,(byte)0x00,(byte)0x02,(byte)0x5B,(byte)0x01,(byte)0x01,(byte)0xC2,(byte)0x23,(byte)0x23};
						faultData.setAckByte(LZackByte);
						return null;
					}else if((operaTab&0x40)==0){	//无操作
//						stateTab.put(7, "无操作");
					}
					faultData.setStateTab(stateTab);
				}
				break;
			case 25:	//上传用户信息传输装置软件版本
				break;
			case 26:	//上传用户信息传输装置配置情况
				break;
			case 28:	//上传用户信息传输装置系统时间
				break;
			case 30:	//传输装置 GSM 故障
				stateTabList.add("GSM 故障");
				faultData.setTabbingCmd("传输装置 GSM 故障");
				faultData.setSystemAddress("0");
				faultData.setUnitAddress("0");
				byte ackByte30[] = getAckByte(dataN,30);
				faultData.setAckByte(ackByte30);
				break;
			case 61:	//读建筑消防设施系统状态
				break;
			case 62:	//读建筑消防设施部件运行状态
				break;
			case 63:	//读建筑消防设施部件模拟量值
				break;
			case 64:	//读建筑消防设施操作信息
				break;
			case 65:	//读建筑消防设施软件版本
				break;
			case 66:	//读建筑消防设施系统配置情况
				break;
			case 67:	//读建筑消防设施部件配置情况
				break;
			case 68:	//读建筑消防设施系统时间
				break;
			case 81:	//读用户信息传输装置运行状态
				break;
			case 84:	//读用户信息传输装置操作信息记录
				break;
			case 85:	//读用户信息传输装置软件版本
				break;
			case 86:	//读用户信息传输装置配置情况
				break;
			case 88:	//读用户信息传输装置系统时间
				break;
			case 89:	//初始化用户信息传输装置
				break;
			case 90:	//同步用户信息传输装置时钟
				break;
			case 91:	//查岗命令
				break;
			}
		}
		faultData.setStateTabList(stateTabList);
		if(stateTabList!=null&&stateTabList.size()>0){
			faultData.setPushState(1);
		}
		return faultData;
	}
	
	public static FaultDataEntity dealFaultByQP(byte[] dataN,int byteLen,int controlCmd) throws Exception{
		FaultDataEntity faultData = new FaultDataEntity();
//		List<String> stateTab = new ArrayList<String>();
		Map<Integer,String> stateTab = new HashMap<Integer,String>();
		List<String> stateTabList = new ArrayList<String>();
		if(controlCmd==2){	//发送数据处理
			int tabbingCmd = dataN[27]&0xff;	//标识命令、类型标志
			int infoNum = dataN[28]&0xff;		//信息数目
			switch(tabbingCmd){
			case 1:	//上传建筑消防设施系统状态============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//系统标志
					int systemAddress = dataN[30+i*10]&0xff;	//系统地址
					int systemState = (dataN[32+i*10]&0xff)+((dataN[31+i*10]&0xff)<<8);	//系统状态
					
					if((systemState&0x01)==1){	//正常运行状态
//						stateTab.put(1, "正常运行状态");
//						stateTabList.add("正常运行状态");
					}else if((systemState&0x01)==0){	//测试状态
						stateTab.put(1, "测试状态");
					}
					if((systemState&0x02)==2){	//火警
						stateTab.put(2, "火警");
						stateTabList.add("火警");
					}else if((systemState&0x02)==0){	//无火警
						stateTab.put(2, "无火警");
					}
					if((systemState&0x04)==4){	//故障
						stateTab.put(3, "故障");
						stateTabList.add("故障");
					}else if((systemState&0x04)==0){	//无故障
						stateTab.put(3, "无故障");
					}
					if((systemState&0x08)==8){	//屏蔽
						stateTab.put(4, "屏蔽");
						stateTabList.add("屏蔽");
					}else if((systemState&0x08)==0){	//无屏蔽
						stateTab.put(4, "无屏蔽");
					}
					if((systemState&0x10)==16){	//监管
						stateTab.put(5, "监管");
						stateTabList.add("监管");
					}else if((systemState&0x10)==0){	//无监管
						stateTab.put(5, "无监管");
					}
					if((systemState&0x20)==32){	//启动（开启）
						stateTab.put(6, "启动（开启）");
						stateTabList.add("启动（开启）");
					}else if((systemState&0x20)==0){	//停止（关闭）
						stateTab.put(6, "停止（关闭）");
					}
					if((systemState&0x40)==64){	//反馈
						stateTab.put(7, "反馈");
						stateTabList.add("反馈");
					}else if((systemState&0x40)==0){	//无反馈
						stateTab.put(7, "无反馈");
					}
					if((systemState&0x80)==128){	//延时状态
						stateTab.put(8, "延时状态");
						stateTabList.add("延时状态");
					}else if((systemState&0x80)==0){	//未延时
						stateTab.put(8, "未延时");
					}
					if((systemState&0x100)==256){	//主电故障
						stateTab.put(9, "主电故障");
						stateTabList.add("主电故障");
					}else if((systemState&0x100)==0){	//主电正常
						stateTab.put(9, "主电正常");
					}
					if((systemState&0x200)==512){	//备电故障
						stateTab.put(10, "备电故障");
						stateTabList.add("备电故障");
					}else if((systemState&0x200)==0){	//备电正常
						stateTab.put(10, "备电正常");
					}
					if((systemState&0x400)==1024){	//总线故障
						stateTab.put(11, "总线故障");
						stateTabList.add("总线故障");
					}else if((systemState&0x400)==0){	//总线正常
						stateTab.put(11, "总线正常");
					}
					if((systemState&0x800)==2048){	//手动状态
						stateTab.put(12, "手动状态");
						stateTabList.add("手动状态");
					}else if((systemState&0x800)==0){	//自动状态
						stateTab.put(12, "自动状态");
					}
					if((systemState&0x1000)==4096){	//配置改变
						stateTab.put(13, "配置改变");
						stateTabList.add("配置改变");
					}else if((systemState&0x1000)==0){	//无配置改变
						stateTab.put(13, "无配置改变");
					}
					if((systemState&0x2000)==8192){	//复位
						stateTab.put(14, "复位");
						stateTabList.add("复位");
					}else if((systemState&0x2000)==0){	//正常
						stateTab.put(14, "正常");
					}
					
					switch(systemTab){
					case 1:	//火灾报警系统
						faultData.setSystemTab("火灾报警系统");
						break;
					case 10:	//消防联动控制器
						faultData.setSystemTab("消防联动控制器");
						break;
					case 11:	//消火栓系统
						faultData.setSystemTab("消火栓系统");
						break;
					case 12:	//自动喷水灭火系统
						faultData.setSystemTab("自动喷水灭火系统");
						break;
					case 13:	//气体灭火系统
						faultData.setSystemTab("气体灭火系统");
						break;
					case 14:	//水喷雾灭火系统（泵启动方式）
						faultData.setSystemTab("水喷雾灭火系统（泵启动方式）");
						break;
					case 15:	//水喷雾灭火系统（压力容器启动方式）
						faultData.setSystemTab("水喷雾灭火系统（压力容器启动方式）");
						break;
					case 16:	//泡沫灭火系统
						faultData.setSystemTab("泡沫灭火系统");
						break;
					case 17:	//干粉灭火系统
						faultData.setSystemTab("干粉灭火系统");
						break;
					case 18:	//防烟排烟系统
						faultData.setSystemTab("防烟排烟系统");
						break;
					case 19:	//防火门及卷帘系统
						faultData.setSystemTab("防火门及卷帘系统");
						break;
					case 20:	//消防电梯
						faultData.setSystemTab("消防电梯");
						break;
					case 21:	//消防应急广播
						faultData.setSystemTab("消防应急广播");
						break;
					case 22:	//消防应急照明和疏散指示系统
						faultData.setSystemTab("消防应急照明和疏散指示系统");
						break;
					case 23:	//消防电源
						faultData.setSystemTab("消防电源");
						break;
					case 24:	//消防电话
						faultData.setSystemTab("消防电话");
						break;
					}
					faultData.setTabbingCmd("上传建筑消防设施系统状态");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 135:
			case 2:	//上传建筑消防设施部件运行状态==============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*46]&0xff;		//系统标志
					int systemAddress = dataN[30+i*46]&0xff;	//系统地址
					int unitType = dataN[31+i*46]&0xff;		//部件类型
					byte[] macByte = new byte[4];
		    		for(int j=0;j<4;j++){
		    			macByte[j] = dataN[32+j+i*46];
		    		}//更换三江协议获取部件地址
		    		
		    		
		    		String unitNumber = IntegerTo16.bytes2Hex(macByte);
					int bj1 = (dataN[32+i*46]&0xff)+((dataN[33+i*46]&0xff)<<8); 	//位号
					int bj2 = (dataN[34+i*46]&0xff)+((dataN[35+i*46]&0xff)<<8); 	//区号
		    		String unitAddress = bj2+"区"+bj1+"号";	//部件地址
		    		byte[] unitMemo = new byte[31];							//部件说明
		    		/*for (int j = 0; j < 31; j++) {
		    			unitMemo[i] = dataN[38+j+i*46];
					}
		    		String unitMemos = new String(unitMemo,"gb18030");		//部件说明转成汉字
*/					int unitState = (dataN[37+i*46]&0xff)+((dataN[36+i*46]&0xff)<<8);	//部件状态
					if(unitState==11){
						stateTabList.add("屏蔽消除");
					}else if(unitState==12){
						stateTabList.add("故障消除");
					}else if(unitState==13){
						stateTabList.add("请求消除");
					}else if(unitState==14){
						stateTabList.add("反馈消除");
					}else if(unitState==15){
						stateTabList.add("电源故障消除");
					}else{
						if((unitState&0x01)==1){	//正常运行状态
//							stateTab.put(1, "正常运行状态");
//							stateTabList.add("正常运行状态");
						}else if((unitState&0x01)==0){	//测试状态
							stateTab.put(1, "测试状态");
						}
						if((unitState&0x02)==2){	//火警
							stateTab.put(2, "火警");
							stateTabList.add("火警");
						}else if((unitState&0x02)==0){	//无火警
							stateTab.put(2, "无火警");
						}
						if((unitState&0x04)==4){	//故障
							stateTab.put(3, "故障");
							stateTabList.add("故障");
						}else if((unitState&0x04)==0){	//无故障
							stateTab.put(3, "无故障");
						}
						if((unitState&0x08)==8){	//屏蔽
							stateTab.put(4, "屏蔽");
							stateTabList.add("屏蔽");
						}else if((unitState&0x08)==0){	//无屏蔽
							stateTab.put(4, "无屏蔽");
						}
						if((unitState&0x10)==16){	//监管
							stateTab.put(5, "监管");
							stateTabList.add("监管");
						}else if((unitState&0x10)==0){	//无监管
							stateTab.put(5, "无监管");
						}
						if((unitState&0x20)==32){	//启动（开启）
							stateTab.put(6, "启动（开启）");
							stateTabList.add("启动（开启）");
						}else if((unitState&0x20)==0){	//停止（关闭）
							stateTab.put(6, "停止（关闭）");
						}
						if((unitState&0x40)==64){	//反馈
							stateTab.put(7, "反馈");
							stateTabList.add("反馈");
						}else if((unitState&0x40)==0){	//无反馈
							stateTab.put(7, "无反馈");
						}
						if((unitState&0x80)==128){	//延时状态
							stateTab.put(8, "延时状态");
							stateTabList.add("延时状态");
						}else if((unitState&0x80)==0){	//未延时
							stateTab.put(8, "未延时");
						}
						if((unitState&0x100)==256){	//电源故障
							stateTab.put(9, "电源故障");
							stateTabList.add("电源故障");
						}else if((unitState&0x100)==0){	//电源正常
							stateTab.put(9, "电源正常");
						}
						if(unitState==512){	//火警
							stateTab.put(10, "火警");
							stateTabList.add("火警");
						}else if((unitState&0x100)==0){	//电源正常
							stateTab.put(10, "无火警");
						}
						if(unitState==1024){	//火警
							stateTab.put(10, "火警恢复");
							stateTabList.add("火警恢复");
						}
						byte[] ackByte = {(byte)0x40,(byte)0x40,(byte)0x01,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x23,(byte)0x3A,(byte)0x09,(byte)0x1A,(byte)0x09,(byte)0x0C,(byte)0x38,(byte)0x5B,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x79,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xAB,(byte)0x23,(byte)0x23};
						faultData.setAckByte(ackByte);
					}
					switch(unitType){		//部件类型
						case 1:	//	火灾报警控制器
							faultData.setUnitType("火灾报警控制器");
							break;
						case 10:	//可燃气体探测器
							faultData.setUnitType("可燃气体探测器");
							break;
						case 11:	//典型可燃气体探测器
							faultData.setUnitType("典型可燃气体探测器");
							break;
						case 12:	//独立式可燃气体探测器
							faultData.setUnitType("独立式可燃气体探测器");
							break;
						case 13:	//线型可燃气体探测器
							faultData.setUnitType("线型可燃气体探测器");
							break;
						case 16:	//电气火灾监控报警器
							faultData.setUnitType("电气火灾监控报警器");
							break;
						case 17:	//剩余电流式电气火灾监控探测器
							faultData.setUnitType("剩余电流式电气火灾监控探测器");
							break;
						case 18:	//测温式电气火灾监控探测器
							faultData.setUnitType("测温式电气火灾监控探测器");
							break;
						case 21:	//探测回路 改成三江接口板回路
//							faultData.setUnitType("探测回路");(接口板回路)故障
							faultData.setUnitType("(接口板回路)故障");
							break;
						case 22:	//火灾显示盘
							faultData.setUnitType("火灾显示盘");
							break;
						case 23:	//手动火灾报警按钮
							faultData.setUnitType("手动火灾报警按钮");
							break;
						case 24:	//消火栓按钮
							faultData.setUnitType("消火栓按钮");
							break;
						case 25:	//火灾探测器
							faultData.setUnitType("火灾探测器");
							break;
						case 30:	//感温火灾探测器
							faultData.setUnitType("感温火灾探测器");
							break;
						case 31:	//点型感温火灾探测器
							faultData.setUnitType("点型感温火灾探测器");
							break;
						case 32:	//点型感温火灾探测器（S型）
							faultData.setUnitType("点型感温火灾探测器（S型）");
							break;
						case 33:	//点型感温火灾探测器（R型）
							faultData.setUnitType("点型感温火灾探测器（R型）");
							break;
						case 34:	//线型感温火灾探测器
							faultData.setUnitType("线型感温火灾探测器");
							break;
						case 35:	//线型感温火灾探测器(S型)
							faultData.setUnitType("线型感温火灾探测器(S型)");
							break;
						case 36:	//线型感温火灾探测器(R型)
							faultData.setUnitType("线型感温火灾探测器(R型)");
							break;
						case 37:	//光纤感温火灾探测器
							faultData.setUnitType("光纤感温火灾探测器");
							break;
						case 40:	//感烟火灾探测器
							faultData.setUnitType("感烟火灾探测器");
							break;
						case 41:	//点型离子感烟火灾探测器
							faultData.setUnitType("点型离子感烟火灾探测器");
							break; 
						case 42:	//点型光电感烟火灾探测器
							faultData.setUnitType("点型光电感烟火灾探测器");
							break; 
						case 43:	//线型光束感烟火灾探测器
							faultData.setUnitType("线型光束感烟火灾探测器");
							break; 
						case 44:	//吸气式感烟火灾探测器
							faultData.setUnitType("吸气式感烟火灾探测器");
							break; 
						case 50:	//复合式火灾探测器
							faultData.setUnitType("复合式火灾探测器");
							break; 
						case 51:	//复合式感烟感温火灾探测器
							faultData.setUnitType("复合式感烟感温火灾探测器");
							break; 
						case 52:	//复合式感光感温火灾探测器
							faultData.setUnitType("复合式感光感温火灾探测器");
							break; 
						case 53:	//复合式感光感烟火灾探测器
							faultData.setUnitType("复合式感光感烟火灾探测器");
							break; 
						case 61:	//紫外火焰探测器
							faultData.setUnitType("紫外火焰探测器");
							break; 
						case 62:	//红外火焰探测器
							faultData.setUnitType("红外火焰探测器");
							break; 
						case 69:	//感光火灾探测器
							faultData.setUnitType("感光火灾探测器");
							break; 
						case 74:	//气体探测器
							faultData.setUnitType("气体探测器");
							break; 
						case 78:	//图像摄像方式火灾探测器
							faultData.setUnitType("图像摄像方式火灾探测器");
							break;
						case 79:	//感声火灾测器
							faultData.setUnitType("感声火灾测器");
							break; 
						case 81:	//气体灭火控制器
							faultData.setUnitType("气体灭火控制器");
							break; 
						case 82:	//消防电气控制装置
							faultData.setUnitType("消防电气控制装置");
							break; 
						case 83:	//消防控制室图形显示装置
							faultData.setUnitType("消防控制室图形显示装置");
							break; 
						case 84:	//模块
							faultData.setUnitType("模块");
							break;
						case 85:	//输入模块
							faultData.setUnitType("输入模块");
							break;
						case 86:	//输出模块
							faultData.setUnitType("输出模块");
							break;
						case 87:	//输入输出模块
							faultData.setUnitType("输入输出模块");
							break;
						case 88:	//中继模块
							faultData.setUnitType("中继模块");
							break;
						case 91:	//消防水泵
							faultData.setUnitType("消防水泵");
							break;
						case 92:	//消防水箱
							faultData.setUnitType("消防水箱");
							break;
						case 95:	//喷淋泵
							faultData.setUnitType("喷淋泵");
							break;
						case 96:	//水流指示器
							faultData.setUnitType("水流指示器");
							break;
						case 97:	//信号阀
							faultData.setUnitType("信号阀");
							break;
						case 98:	//报警阀
							faultData.setUnitType("报警阀");
							break;
						case 99:	//压力开关
							faultData.setUnitType("压力开关");
							break;
						case 101:	//阀驱动装置
							faultData.setUnitType("阀驱动装置");
							break;
						case 102:	//防火门
							faultData.setUnitType("防火门");
							break;
						case 103:	//防火阀
							faultData.setUnitType("防火阀");
							break;
						case 104:	//通风空调
							faultData.setUnitType("通风空调");
							break;
						case 105:	//泡沫液泵
							faultData.setUnitType("沫液泵");
							break;
						case 106:	//管网电磁阀
							faultData.setUnitType("管网电磁阀");
							break;
						case 111:	//防烟排烟风机
							faultData.setUnitType("防烟排烟风机");
							break;
						case 113:	//排烟防火阀
							faultData.setUnitType("排烟防火阀");
							break;
						case 114:	//常闭送风口
							faultData.setUnitType("常闭送风口");
							break;
						case 115:	//排烟口
							faultData.setUnitType("排烟口");
							break;
						case 116:	//电控挡烟垂壁
							faultData.setUnitType("电控挡烟垂壁");
							break;
						case 117:	//防火卷帘控制器
							faultData.setUnitType("防火卷帘控制器");
							break;
						case 118:	//防火门监控器
							faultData.setUnitType("防火门监控器");
							break;
						case 121:	//警报装置
							faultData.setUnitType("警报装置");
							break;
						case 128:	//打印机故障、三江打印机故障
							faultData.setUnitType("打印机故障");
							break;
						case 251:	//主电故障、三江主电故障
							faultData.setUnitType("主电故障");
							break;
						case 249:	//备电故障、三江备电故障
							faultData.setUnitType("备电故障");
							break;
					}
					switch(systemTab){
					case 1:	//火灾报警系统
						faultData.setSystemTab("火灾报警系统");
						break;
					case 10:	//消防联动控制器
						faultData.setSystemTab("消防联动控制器");
						break;
					case 11:	//消火栓系统
						faultData.setSystemTab("消火栓系统");
						break;
					case 12:	//自动喷水灭火系统
						faultData.setSystemTab("自动喷水灭火系统");
						break;
					case 13:	//气体灭火系统
						faultData.setSystemTab("气体灭火系统");
						break;
					case 14:	//水喷雾灭火系统（泵启动方式）
						faultData.setSystemTab("水喷雾灭火系统（泵启动方式）");
						break;
					case 15:	//水喷雾灭火系统（压力容器启动方式）
						faultData.setSystemTab("水喷雾灭火系统（压力容器启动方式）");
						break;
					case 16:	//泡沫灭火系统
						faultData.setSystemTab("泡沫灭火系统");
						break;
					case 17:	//干粉灭火系统
						faultData.setSystemTab("干粉灭火系统");
						break;
					case 18:	//防烟排烟系统
						faultData.setSystemTab("防烟排烟系统");
						break;
					case 19:	//防火门及卷帘系统
						faultData.setSystemTab("防火门及卷帘系统");
						break;
					case 20:	//消防电梯
						faultData.setSystemTab("消防电梯");
						break;
					case 21:	//消防应急广播
						faultData.setSystemTab("消防应急广播");
						break;
					case 22:	//消防应急照明和疏散指示系统
						faultData.setSystemTab("消防应急照明和疏散指示系统");
						break;
					case 23:	//消防电源
						faultData.setSystemTab("消防电源");
						break;
					case 24:	//消防电话
						faultData.setSystemTab("消防电话");
						break;
					}
					faultData.setTabbingCmd("上传建筑消防设施部件运行状态");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setUnitAddress(unitAddress);
					faultData.setUnitMemos(unitNumber);
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 3:	//上传建筑消防设施部件模拟量值
				break;
			case 4:	//上传建筑消防设施操作信息==================
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//系统标志
					int systemAddress = dataN[30+i*10]&0xff;	//系统地址
					int operaTab = dataN[31+i*10]&0xff;	//操作标志
					int operator = dataN[32+i*10]&0xff;	//操作员编号
					faultData.setTabbingCmd("上传建筑消防设施操作信息");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//复位
						stateTab.put(1, "复位");
						stateTabList.add("复位");
					}else if((operaTab&0x01)==0){	//无操作
						stateTab.put(1, "无操作");
					}
					if((operaTab&0x02)==2){	//消音
						stateTab.put(2, "消音");
						stateTabList.add("消音");
					}else if((operaTab&0x02)==0){	//无操作
						stateTab.put(2, "无操作");
					}
					if((operaTab&0x04)==4){	//手动报警
						stateTab.put(3, "手动报警");
						stateTabList.add("手动报警");
					}else if((operaTab&0x04)==0){	//无操作
						stateTab.put(3, "无操作");
					}
					if((operaTab&0x08)==8){	//警情消除
						stateTab.put(4, "警情消除");
						stateTabList.add("警情消除");
					}else if((operaTab&0x08)==0){	//无操作
						stateTab.put(4, "无操作");
					}
					if((operaTab&0x10)==16){	//自检
						stateTab.put(5, "自检");
						stateTabList.add("自检");
					}else if((operaTab&0x10)==0){	//无操作
						stateTab.put(5, "无操作");
					}
					if((operaTab&0x20)==32){	//确认
						stateTab.put(6, "确认");
						stateTabList.add("确认");
					}else if((operaTab&0x20)==0){	//无操作
						stateTab.put(6, "无操作");
					}
					if((operaTab&0x40)==64){	//测试
						stateTab.put(7, "测试");
						stateTabList.add("测试");
					}else if((operaTab&0x40)==0){	//无操作
						stateTab.put(7, "无操作");
					}
					faultData.setStateTab(stateTab);
					switch(systemTab){
					case 1:	//火灾报警系统
						faultData.setSystemTab("火灾报警系统");
						break;
					case 10:	//消防联动控制器
						faultData.setSystemTab("消防联动控制器");
						break;
					case 11:	//消火栓系统
						faultData.setSystemTab("消火栓系统");
						break;
					case 12:	//自动喷水灭火系统
						faultData.setSystemTab("自动喷水灭火系统");
						break;
					case 13:	//气体灭火系统
						faultData.setSystemTab("气体灭火系统");
						break;
					case 14:	//水喷雾灭火系统（泵启动方式）
						faultData.setSystemTab("水喷雾灭火系统（泵启动方式）");
						break;
					case 15:	//水喷雾灭火系统（压力容器启动方式）
						faultData.setSystemTab("水喷雾灭火系统（压力容器启动方式）");
						break;
					case 16:	//泡沫灭火系统
						faultData.setSystemTab("泡沫灭火系统");
						break;
					case 17:	//干粉灭火系统
						faultData.setSystemTab("干粉灭火系统");
						break;
					case 18:	//防烟排烟系统
						faultData.setSystemTab("防烟排烟系统");
						break;
					case 19:	//防火门及卷帘系统
						faultData.setSystemTab("防火门及卷帘系统");
						break;
					case 20:	//消防电梯
						faultData.setSystemTab("消防电梯");
						break;
					case 21:	//消防应急广播
						faultData.setSystemTab("消防应急广播");
						break;
					case 22:	//消防应急照明和疏散指示系统
						faultData.setSystemTab("消防应急照明和疏散指示系统");
						break;
					case 23:	//消防电源
						faultData.setSystemTab("消防电源");
						break;
					case 24:	//消防电话
						faultData.setSystemTab("消防电话");
						break;
					}
				}
				break;
			case 5:	//上传建筑消防设施软件版本
				break;
			case 6:	//上传建筑消防设施系统配置情况
				break;
			case 7:	//上传建筑消防设施系统部件配置情况
				break;
			case 8:	//上传建筑消防设施系统时间
				break;
			case 22:	//预留，三江心跳
				faultData.setSystemTab("三江心跳");
				byte ackByte[] = getAckByte(dataN,22);
				faultData.setAckByte(ackByte);
				break;
			case 21:	//上传用户信息传输装置运行状态******===============
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("上传用户信息传输装置运行状态");
					int runTab = dataN[29+i*7]&0xff;		//运行标志
					if((runTab&0x01)==1){	//正常监视状态
//						stateTab.put(1, "正常监视状态");
//						stateTabList.add("正常监视状态");
					}else if((runTab&0x01)==0){	//测试状态
						stateTab.put(1, "测试状态");
					}
					if((runTab&0x02)==2){	//火警
						stateTab.put(2, "火警");
						stateTabList.add("火警");
					}else if((runTab&0x02)==0){	//无火警
						stateTab.put(2, "无火警");
					}
					if((runTab&0x04)==4){	//故障
						stateTab.put(3, "故障");
						stateTabList.add("故障");
					}else if((runTab&0x04)==0){	//无故障
						stateTab.put(3, "无故障");
					}
					if((runTab&0x08)==8){	//主电故障
						stateTab.put(4, "主电故障");
						stateTabList.add("主电故障");
					}else if((runTab&0x08)==0){	//主电正常
						stateTab.put(4, "主电正常");
					}
					if((runTab&0x10)==16){	//备电故障
						stateTab.put(5, "备电故障");
						stateTabList.add("备电故障");
					}else if((runTab&0x10)==0){	//备电正常
						stateTab.put(5, "备电正常");
					}
					if((runTab&0x20)==32){	//与监控中心通信信道故障
						stateTab.put(6, "与监控中心通信信道故障");
						stateTabList.add("与监控中心通信信道故障");
					}else if((runTab&0x20)==0){	//通信信道正常
						stateTab.put(6, "通信信道正常");
					}
					if((runTab&0x40)==64){	//监测连接线路故障
						stateTab.put(7, "监测连接线路故障");
						stateTabList.add("监测连接线路故障");
					}else if((runTab&0x40)==0){	//监测连接线路正常
						stateTab.put(7, "监测连接线路正常");
					}
					byte[] ackByte22 = {(byte)0x40,(byte)0x40,(byte)0x01,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x23,(byte)0x3A,(byte)0x09,(byte)0x1A,(byte)0x09,(byte)0x0C,(byte)0x38,(byte)0x5B,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x79,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xAB,(byte)0x23,(byte)0x23};
					faultData.setAckByte(ackByte22);
					faultData.setStateTab(stateTab);
				}
				break;
			case 24:	//上传用户信息传输装置操作信息记录===================
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("上传用户信息传输装置操作信息记录");
					int operaTab = dataN[29+i*8]&0xff;	//操作标志
					int operator = dataN[30+i*8]&0xff;	//操作员编号
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//复位
						stateTab.put(1, "复位");
						stateTabList.add("复位");
					}else if((operaTab&0x01)==0){	//无操作
						stateTab.put(1, "无操作");
					}
					if((operaTab&0x02)==2){	//消音
						stateTab.put(2, "消音");
						stateTabList.add("消音");
					}else if((operaTab&0x02)==0){	//无操作
						stateTab.put(2, "无操作");
					}
					if((operaTab&0x04)==4){	//手动报警
						stateTab.put(3, "手动报警");
						stateTabList.add("手动报警");
						byte[] lzack4 ={(byte)0x40,(byte)0x40,(byte)0x29,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0A,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x01,(byte)0x20,(byte)0x01,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x22,(byte)0x23,(byte)0x23}; 
						faultData.setAckByte(lzack4);
					}else if((operaTab&0x04)==0){	//无操作
						stateTab.put(3, "无操作");
					}
					if((operaTab&0x08)==8){	//警情消除
						stateTab.put(4, "警情消除");
						stateTabList.add("警情消除");
					}else if((operaTab&0x08)==0){	//无操作
						stateTab.put(4, "无操作");
					}
					if((operaTab&0x10)==16){	//自检
						stateTab.put(5, "自检");
						stateTabList.add("自检");
					}else if((operaTab&0x10)==0){	//无操作
						stateTab.put(5, "无操作");
					}
					if((operaTab&0x20)==32){	//查岗应答
						stateTab.put(6, "查岗应答");
						stateTabList.add("查岗应答");
					}else if((operaTab&0x20)==0){	//无操作
						stateTab.put(6, "无操作");
					}
					if((operaTab&0x40)==64){	//测试对应联众的心跳
						stateTab.put(7, "心跳");
						stateTabList.add("心跳");
						byte[] LZackByte = {(byte)0x40,(byte)0x40,(byte)0x04,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x1A,(byte)0x02,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x03,(byte)0x00,(byte)0x02,(byte)0x5B,(byte)0x01,(byte)0x01,(byte)0xC2,(byte)0x23,(byte)0x23};
						faultData.setAckByte(LZackByte);
						return null;
					}else if((operaTab&0x40)==0){	//无操作
						stateTab.put(7, "无操作");
					}
					faultData.setStateTab(stateTab);
					byte[] ackByte24 = {(byte)0x40,(byte)0x40,(byte)0x01,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x23,(byte)0x3A,(byte)0x09,(byte)0x1A,(byte)0x09,(byte)0x0C,(byte)0x38,(byte)0x5B,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x79,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xAB,(byte)0x23,(byte)0x23};
					faultData.setAckByte(ackByte24);
				}
				break;
			case 25:	//上传用户信息传输装置软件版本
				break;
			case 26:	//上传用户信息传输装置配置情况
				break;
			case 28:	//上传用户信息传输装置系统时间
				break;
			case 30:	//传输装置 GSM 故障
				stateTabList.add("GSM 故障");
				faultData.setTabbingCmd("传输装置 GSM 故障");
				faultData.setSystemAddress("0");
				faultData.setUnitAddress("0");
				byte ackByte30[] = getAckByte(dataN,30);
				faultData.setAckByte(ackByte30);
				break;
			case 61:	//读建筑消防设施系统状态
				break;
			case 62:	//读建筑消防设施部件运行状态
				break;
			case 63:	//读建筑消防设施部件模拟量值
				break;
			case 64:	//读建筑消防设施操作信息
				break;
			case 65:	//读建筑消防设施软件版本
				break;
			case 66:	//读建筑消防设施系统配置情况
				break;
			case 67:	//读建筑消防设施部件配置情况
				break;
			case 68:	//读建筑消防设施系统时间
				break;
			case 81:	//读用户信息传输装置运行状态
				break;
			case 84:	//读用户信息传输装置操作信息记录
				break;
			case 85:	//读用户信息传输装置软件版本
				break;
			case 86:	//读用户信息传输装置配置情况
				break;
			case 88:	//读用户信息传输装置系统时间
				break;
			case 89:	//初始化用户信息传输装置
				break;
			case 90:	//同步用户信息传输装置时钟
				break;
			case 91:	//查岗命令
				break;
			}
		}
		faultData.setStateTabList(stateTabList);
		if(stateTabList!=null&&stateTabList.size()>0){
			faultData.setPushState(1);
		}
		return faultData;
	}
	
	public static String IntegerToLongitude(int longidata){
		float d1 = (float)longidata/30000f;
		float d2 = d1/60f;
		return d2+"";
	}
	
	public static byte[] getByteData(byte[] byteData){
		int byteLen = byteData.length - 30;
		int ifLen = byteData[24]&0xff;
		if(byteLen!=ifLen){
			return null;
		}
		byte[] dataByte = new byte[byteLen];
		for (int i = 0; i < dataByte.length; i++) {
			dataByte[i] = byteData[27+i];
		}
		return dataByte;
	}
	public static byte[] getByteDataByCC(byte[] byteData,byte cc){
		int byteLen = (byteData[13]&0xff)+10;	//数据长度
		byte[] data = new byte[byteLen];
		data[0] = 0x7e;
		data[1] = 0x0e;
		data[2] = 0x04;
		data[3] = 0x01;
		data[4] = 0x00;
		data[5] = 0x00;
		data[6] = 0x00;
		data[7] = 0x01;
		data[8] = 0x00;
		data[9] = 0x00;
		data[10] = 0x00;
		data[11] = new IntegerTo16().algorismToHEXString(byteLen-11);
		data[12] = cc;
		for(int i = 14;i<18;i++){
			data[i-1] = byteData[i];
		}
		for(int i = 23;i<byteData.length;i++){
			data[i-6] = byteData[i];
		}
		return data;
	}
	
	public static byte[] getAckByte(byte[] dataN,int tab){
		int dataNLen = dataN.length;
		int crclen = dataNLen - 5;
		byte[] ackByte = new byte[dataNLen];
		byte[] crcByte = new byte[crclen];
		ackByte[0] = 0x40;
		ackByte[1] = 0x40;
		for (int i = 2; i < crcByte.length+2; i++) {
			if(i==27){
				if(tab == 22){
					ackByte[i] = 0x17;
					crcByte[i-2] = 0x17;
				}else if(tab == 30){
					ackByte[i] = (byte)0x80;
					crcByte[i-2] = (byte)0x80;
				}
			}else if(i==26){
				if(tab == 22){
					ackByte[i] = 0x03;
					crcByte[i-2] = 0x03;
				}else if(tab == 30){
					ackByte[i] = (byte)0x04;
					crcByte[i-2] = (byte)0x04;
				}
			}else{
				ackByte[i] = dataN[i];
				crcByte[i-2] = dataN[i];
			}
		}
		String srcStr =CRC16.SumCheckToHex(crcByte);
		System.out.println("crcbyte[]:"+IntegerTo16.bytes2Hex(crcByte)+"=============="+srcStr);
		ackByte[crclen+2] = new IntegerTo16().str16ToByte(srcStr);
		ackByte[crclen+3] = 0x23;
		ackByte[crclen+4] = 0x23;
		return ackByte;
	}
	
	public static boolean checkSumCrc(byte[] data){
		int dataLen = data.length;
		int crcLen = dataLen-5;
		byte[] crcByte = new byte[crcLen];
		for(int i=0;i<crcLen;i++){
			crcByte[i] = data[i+2];
		}
		String srcStr = CRC16.SumCheckToHex(crcByte);
		byte crcH = new IntegerTo16().str16ToByte(srcStr);
		byte clientCrcL = data[dataLen-3];
		System.out.println("CRCH:"+crcH+"clientCrcL:"+clientCrcL);
		if(crcH==clientCrcL){
			return true;
		}else{
			return false;
		}
	}
}
