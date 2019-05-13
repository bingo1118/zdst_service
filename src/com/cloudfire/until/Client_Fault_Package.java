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
    	boolean crcRight = checkCrc(data,byteCount);  //crcУ��
    	if(crcRight){
    		RePeaterData mRePeaterData = new RePeaterData();
    		mRePeaterData.setSeqL(data[11]);
    		int msg = data[12]&0xff;
    		String deviceMac="";
    		byte[] devMac = new byte[4];
    		log.debug("msg================================"+msg);
        	switch (msg) {
    		case 203:   //����
    			mRePeaterData.setCmd((byte)0x01);
    			for(int i=0;i<4;i++){
    				devMac[i] = data[14+i];
    			}
    			deviceMac = IntegerTo16.bytes2Hex(devMac);
    			mRePeaterData.setAlarmSmokeMac(deviceMac);
    			break;
    		case 202:  //�������ݰ�
    			mRePeaterData.setCmd((byte)0x02);	//����
    			for(int i=0;i<4;i++){
    				devMac[i] = data[14+i];
    			}
    			deviceMac = IntegerTo16.bytes2Hex(devMac);
    			mRePeaterData.setAlarmSmokeMac(deviceMac);
    			break;
    		case 204:  //͸�����ݰ�
    			mRePeaterData.setCmd((byte)0x04);	//CC͸�����ݴ���
    			data = Client_Fault_Package.getByteDataByCC(dataN, dataN[12]);
    			int electricDataLen = data[11]&0xff;
    			int transparentType = data[12]&0xff;//͸�����ͣ�����������������������豸������CC(204),��ʾ��������
    			switch (transparentType) {
				case 204:
					mRePeaterData.setTransmissionType(1);
					byte[] electricMac = new byte[4];
	    			for(int i=0;i<4;i++){
	    				electricMac[i] = data[13+i];
	    			}
	    			String electricMacStr = IntegerTo16.bytes2Hex(electricMac);
	    			mRePeaterData.setElectricMac(electricMacStr);
	    			RePeaterDataDao redao = new RePeaterDataDaoImpl(); //����mac��ѯ�豸�������͡�
	    			int deviceTypeback = redao.getDeviceType(electricMacStr);
	    			if(deviceTypeback == 2){//2����ȼ��
	    				
	    			}else if(deviceTypeback == 13){	//13�����豸Ϊ����̽����
	    				mRePeaterData.setTransmissionType(4);	//1���ǵ�����2����������3��ˮѹ��4������̽������5�����塣6�����ܡ�7��ˮλ�豸����
	    				int co2=((data[20]&0xff)<<8)+(data[21]&0xff);//������̼ ppm
	    				double methanel =(((data[22]&0xff)<<8)+(data[23]&0xff))/1000.0;//��ȩ mg/m3
	    				int  humidity =(((data[24]&0xff)<<8)+(data[25]&0xff))/10;//ʪ�� 
	    				double temperature=(((data[26]&0xff)<<8)+(data[27]&0xff)-500)/10.0;  //�¶� .c
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
	    			}else if(deviceTypeback == 19){	//19ˮλ̽����
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
	    					water.setAlarmType(136);//��485����
	    				}
	    				mRePeaterData.setWater(water);
	    			}else if (deviceTypeback == 10){  //10����ˮѹ****************************
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
							log.debug("ˮѹ״̬:"+waterStatus);
							int waterValue = ((data[22]&0xff)<<8)+(data[21]&0xff);						
							switch (waterStatus) {
							case 219: //����
								water.setAlarmType(219);
								water.setValue(String.valueOf(waterValue));
								water.setType(1);
								break;
							case 218://��ˮѹ
								water.setAlarmType(218);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 209://D1,��ˮѹ
								water.setAlarmType(209);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 220:	//dc  �͵�ѹ
								water.setAlarmType(193);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 217:	//d9����
								water.setAlarmType(217);
								water.setValue(String.valueOf(waterValue));
								water.setType(2);
								break;
							case 210: 	//d2����
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
	    			}else if (deviceTypeback == 18){  //18 �����豸
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
							case 201: //@@11.02 ֹͣ����
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
						utilsDao.updateDeviceMac(electricMacStr);//@@��������
//						Utils.saveChangehistory("6601",electricMacStr,1);//@@12.22
	    				int flag=data[17]&0xff; //�ϼѵ����ı�־λ
	    				if(flag==95){  //�豸���ڳϼѵ��� 0x5f
	    					ChJEntity chj=new ChJEntity();
		    				int q_type=data[20]&0xff; //��ȡ������,Ҳ���ǵ�����ֵ
		    				int q_i=data[22]&0xff; //��ȡ������С
		    				chj.setData7(Utils.electricValues(q_i, 1));  //�洢��������
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
		    				int q_type3=data[21]&0xff; //��ȡ��������
		    				switch(q_type3){ //0xCB ������0xC2  ����������0xC3  ��·������0xC4  ���ȱ�����
		    				case 203:  //����
		    					chj.setType(1); 
		    					break;
		    				case 194:
		    					chj.setType(2); 
		    					chj.setAlarmType(45);
		    					break;
		    				case 195:
		    					chj.setType(2); 
		    					chj.setAlarmType(49);//��·
		    					break;
		    				case 196:
		    					chj.setType(2); 
		    					chj.setAlarmType(50);//����
		    					break;
		    				default:
		    					break;
		    				}
		    				mRePeaterData.setChj(chj);
		    				mRePeaterData.setDeviceType(4);  //����BQϵ����ϼѵ���
		    			}else if(flag==17){			//������
		    				BQ100Entity jtlEntity = new BQ100Entity();
		    				int deviceflag = data[23]&0xff;
		    				if(deviceflag == 17){	//�����豸
		    					int controllerflag = data[28]&0xff;
		    					log.debug("controllerflag: "+controllerflag+"  deviceflag:"+deviceflag);
		    					switch(controllerflag){
		    					case 1:	//�����͹���
		    						int state=((data[26]&0xff)<<8)+(data[27]&0xff);	//�����б�
		    						int alarmtype=((data[24]&0xff)<<8)+(data[25]&0xff);	//�����б�
		    						List<String> list6 = new ArrayList<String>();//6��ѹ
	    							List<String> list7 = new ArrayList<String>();//7����
	    							List<String> list9 = new ArrayList<String>();//9�¶�
	    							String data8="";//©����
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
		    						if(state==0){	//����+͸������
		    							jtlEntity.setType(2);
		    						}else{		//����+��������
		    							jtlEntity.setType(1);
		    							List<String> listAlarm = new ArrayList<String>();
		    							if((state&0x01)==1){	//A���ѹ����
		    								listAlarm.add("1");
//		    								list6.add(value61);
		    							}
		    							if((state&0x02)==2){	//B���ѹ����
		    								listAlarm.add("2");
//		    								list6.add(value62);
		    							}
		    							if((state&0x04)==4){	//C���ѹ����
		    								listAlarm.add("4");
//		    								list6.add(value63);
		    							}
		    							if((state&0x08)==8){	//A���������
		    								listAlarm.add("8");
//		    								list7.add(value71);
		    							}
		    							if((state&0x10)==16){	//B���������
		    								listAlarm.add("16");
//		    								list7.add(value72);
		    							}
		    							if((state&0x20)==32){	//C���������
		    								listAlarm.add("32");
//		    								list7.add(value73);
		    							}
		    							if((state&0x40)==64){	//©��������
		    								listAlarm.add("64");
//		    								jtlEntity.setData8(data8);
		    							}
		    							if((state&0x80)==128){	//A���¶ȱ���
		    								listAlarm.add("128");
//		    								list9.add(value91);
		    							}
		    							if((state&0x100)==256){	//B���¶ȱ���
		    								listAlarm.add("256");
//		    								list9.add(value92);
		    							}
		    							if((state&0x200)==512){	//C���¶ȱ���
		    								listAlarm.add("512");
//		    								list9.add(value93);
		    							}
		    							if((state&0x400)==1024){//N���¶ȱ���
		    								listAlarm.add("1024");
//		    								list9.add(value94);
		    							}
		    							jtlEntity.setListAlarm(listAlarm);
		    							switch(alarmtype){
		    							case 257:	//0x0101     Ƿѹ
		    								jtlEntity.setAlarmType(44);
		    								break;
		    							case 513:	//0x0201     ��ѹ
		    								jtlEntity.setAlarmType(43);
		    								break;
		    							case 769:	//0x0301     ����
		    								jtlEntity.setAlarmType(45);
		    								break;
		    							case 1025:	//0x0401     ©��
		    								jtlEntity.setAlarmType(46);
		    								break;
		    							case 1281:	//0x0501     �¶ȸ�
		    								jtlEntity.setAlarmType(47);
		    								break;
		    							case 1537:	//0x0601     ������·����
		    								jtlEntity.setAlarmType(49);
		    								break;
		    							case 1793:	//0x0701     ������·����
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
		    					case 10:	//��ֵ
		    						jtlEntity.setType(3);
									ElectricThresholdBean mElectricThresholdBean = Utils.jtlElectricThresholdBean(data,33);
									jtlEntity.setmElectricThresholdBean(mElectricThresholdBean);
		    						break;
		    					}
		    				}
		    				mRePeaterData.setmBQ100Entity(jtlEntity);
		    				mRePeaterData.setDeviceType(5);  //����jtlϵ����ϼѵ���
		    			}else if(flag==104){ //@@������
		    				ThreePhaseMeterEntity meter=new ThreePhaseMeterEntity();
		    				int makesure = data[18]&0xff;
		    				float threephasedata=0;
		    				if(makesure!=145){
		    					return null;
		    				}
		    				int commad_lengh=data[19];
		    				byte[] commad={data[20],data[21],data[22],data[23]};
		    				String commandType=IntegerTo16.bytes2Hex(commad);//@@��ʶ��
		    				byte[] dataByte = new byte[commad_lengh-4];
			    			for(int i=0;i<dataByte.length;i++){
			    				dataByte[i] = (byte) (data[23+dataByte.length-i]-(byte)0x33);
			    			}
			    			String commad_data = IntegerTo16.bytes2Hex(dataByte);//@@����
			    			try{
			    				threephasedata=Float.parseFloat(commad_data);
			    			}catch(Exception e){
			    				threephasedata=0;
			    			}
		    				switch (commandType) {
							case "33343435"://@@A���ѹ
								threephasedata=threephasedata/10;
								meter.setData_num(1);
								meter.setData(threephasedata+"");
								meter.setData_type(6);
								break;
							case "33353435"://@@B���ѹ
								threephasedata=threephasedata/10;
								meter.setData_num(2);
								meter.setData(threephasedata+"");
								meter.setData_type(6);
								break;
							case "33363435"://@@C���ѹ
								threephasedata=threephasedata/10;
								meter.setData_num(3);
								meter.setData(threephasedata+"");
								meter.setData_type(6);
								break;
							case "33343434"://@@A�����
								threephasedata=threephasedata/10;
								meter.setData_num(1);
								meter.setData(threephasedata+"");
								meter.setData_type(7);
								break;
							case "33353434"://@@B�����
								threephasedata=threephasedata/10;
								meter.setData_num(2);
								meter.setData(threephasedata+"");
								meter.setData_type(7);
								break;
							case "33363434"://@@C�����
								threephasedata=threephasedata/10;
								meter.setData_num(3);
								meter.setData(threephasedata+"");
								meter.setData_type(7);
								break;
							case "3333C334"://@@��ǰʣ����������(©������ֵ)
								threephasedata=threephasedata/10;
								break;
							case "3334C334"://@@��ǰʣ�����ֵ��©������
								threephasedata=threephasedata;
								meter.setData_num(1);
								meter.setData(threephasedata+"");
								meter.setData_type(8);
								break;
							case "37373337"://@@���ѹֵ
								threephasedata=threephasedata/10;
								break;
							case "38373337"://@@�����ֵ
								threephasedata=threephasedata/10;
								break;
							case "34383337"://@@����״̬��
								int alarm_reason = data[24]&0x1f;//@@����ԭ��
								switch(alarm_reason){
									case 0://00000-��������
										meter.setAlarmType(0);
										break;
									case 2://00010-ʣ�������©����������
										meter.setAlarmType(46);
										break;
									case 4://00100-ȱ��
										break;
									case 5://00101-����
										meter.setAlarmType(45);
										break;
									case 6://00110-��·
										meter.setAlarmType(49);
										break;
									case 7://00111-ȱ��
										break;
									case 8://01000-Ƿѹ
										meter.setAlarmType(44);
										break;
									case 9://01001-��ѹ
										meter.setAlarmType(43);
										break;
									case 10://01010-�ӵ�
										break;
									case 11://01011-ͣ��
										meter.setAlarmType(52);
										break;
									case 12://01100-��ʱ����
										break;
									case 13://01101-Զ��
										break;
									case 14://01110-��������
										break;
									case 15://01111-����
										break;
									case 18://10010-�ֶ�
										break;
									case 16://10000-����������
										break;
									case 17://10001-��բʧ��
										break;
									case 19://10011-���ø���
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
								int electricstate = (data[24]>>5)&0x03;//@@��բ״̬
								switch(electricstate){
									case 0://��բ
										meter.setElectric_state(1);
										break;
									case 2://�غ�բ������״̬�µķ�բ��
										meter.setElectric_state(2);
										break;
									case 3:
										meter.setElectric_state(2);//@@��բ
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
			    			int deviceType = data[21]&0xff;//�豸���ͣ�����BQ100��BQ200������1��ʾBQ200��2��ʾBQ100
			    			log.debug("deviceType="+deviceType);
			    			switch (deviceType) {
							case 1://BQ200
								int bq200Type = data[17]&0xff;
								log.debug("bq200Type="+bq200Type);  
								BQ200Entity mBQ200Entity = new BQ200Entity();
								switch (bq200Type) {
								case 229://E5 �����豸2.0��:
									mRePeaterData.setDeviceType(1);
									int typeNum200 = data[25]&0xff;
									switch (typeNum200) {
									case 35:
										//����
										mBQ200Entity.setType(1);
										int alarmType = data[30]&0xff;
										switch (alarmType) {
										case 1://1©����������2�¶ȱ�����3Ƿѹ������4��ѹ������5����������10ѹ����ѹ�� 
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
										//@@11.30���˱����쳣����
										if(mBQ200Entity.getElectricDevType()==6){
											if(Double.parseDouble(valueStr)<80||Double.parseDouble(valueStr)>300){
												return null;
											}//@@11.30����ͻ���������
										}else if(mBQ200Entity.getElectricDevType()==7){
											if(Double.parseDouble(valueStr)<0||Double.parseDouble(valueStr)>63){
												return null;
											}//@@11.30 ����ͻ���������
										}else{
											if(Double.parseDouble(valueStr)<0||Double.parseDouble(valueStr)>900){
												return null;
											}//@@11.30 ����ͻ���������
										}
										break;
									case 6:
										//����
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
												}//@@11.04 ����ͻ���������
												mBQ200Entity.setData6(valueData);
											}else{
												if(Double.parseDouble(valueData)<0||Double.parseDouble(valueData)>63){
													return null;
												}//@@11.04 ����ͻ���������
												mBQ200Entity.setData7(valueData);
											}
										}
										break;
									case 43://��ֵ
										mBQ200Entity.setType(4);
										ElectricThresholdBean mElectricThresholdBean = Utils.unElectricThresholdBeanPackage(data,2);
										mBQ200Entity.setmElectricThresholdBean(mElectricThresholdBean);
										break;
									case 36://����
										mBQ200Entity.setType(3);
										String alarmValue = Utils.getValue(data, 25);
										mBQ200Entity.setAlarmData(alarmValue);
										mBQ200Entity.setAlarmType(36);
										return null;//@@11.17���ι���
//										break;
									default:
										break;
									}
									mRePeaterData.setmBQ200Entity(mBQ200Entity);
									break;
		
								case 230://E6 �����豸1.0��:
									mRePeaterData.setDeviceType(1);
									int typeNum2001 = data[25]&0xff;
									switch (typeNum2001) {
									case 6://����
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
												}//@@11.04 ����ͻ���������
												mBQ200Entity.setData6(valueData);
											}else{
												if(Double.parseDouble(valueData)<0||Double.parseDouble(valueData)>63){
													return null;
												}//@@11.04 ����ͻ���������
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
									case 36://����
										mBQ200Entity.setType(3);
										String alarmValue = Utils.getValue(data, 25);
										mBQ200Entity.setAlarmData(alarmValue);
										mBQ200Entity.setAlarmType(36);
										return null;//@@11.17���ι���
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
								case 229://E5 �����豸2.0��
									mRePeaterData.setDeviceType(3);
									int typeNum = data[25]&0xff;
									switch (typeNum) {
										case 6://����
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
										case 43://��ֵ
											mBQ100Entity.setType(4);
											ElectricThresholdBean mElectricThresholdBean = Utils.unElectricThresholdBeanPackage(data,4);
											mBQ100Entity.setmElectricThresholdBean(mElectricThresholdBean);
											break;
										case 36://����
											mBQ100Entity.setType(3);
											String alarmValue = Utils.getValue(data, 25);
											mBQ100Entity.setAlarmData(alarmValue);
											mBQ100Entity.setAlarmType(36);
											return null;//@@11.17���ι���
//											break;
										case 35://����
											mBQ100Entity.setType(2);
											int alarmNumber = (int) Float.parseFloat((Utils.getValue(data, 25)));
											switch (alarmNumber) {//1©����������2�¶ȱ�����3Ƿѹ������4��ѹ������5����������10ѹ����ѹ�� 
											case 1://©��������
												String leakCurrent = Utils.getValue(data, 57);
												mBQ100Entity.setData8(leakCurrent);
												mBQ100Entity.setAlarmType(46);
												break;
											case 2://�¶ȱ���
												mBQ100Entity.setList9(Utils.dataList(4,data,65,9));
												mBQ100Entity.setAlarmType(47);
												break;
											case 3://Ƿѹ����
												mBQ100Entity.setList6(Utils.dataList(3,data,33,6));
												mBQ100Entity.setAlarmType(44);
												break;
											case 4://��ѹ����
												mBQ100Entity.setList6(Utils.dataList(3,data,33,6));
												mBQ100Entity.setAlarmType(43);
												break;
											case 5://��������
												mBQ100Entity.setList7(Utils.dataList(3,data,45,7));
												mBQ100Entity.setAlarmType(45);
												break;
											case 10://ѹ����ѹ
												mBQ100Entity.setAlarmType(6);
												break;
											default:
												break;
											}
											break;
									}
									break;
								
									
								case 231://E7 ���ݵ����豸2.0��:
									mRePeaterData.setDeviceType(1);
									int typeNum200 = data[25]&0xff;
									BQ200Entity mBq100Entity = new BQ200Entity();
									switch (typeNum200) {
									case 35:
										//����
										mBq100Entity.setType(1);
										int alarmType = data[30]&0xff;
										switch (alarmType) {
										case 1://1©����������2�¶ȱ�����3Ƿѹ������4��ѹ������5����������6��բ������ 
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
											mBq100Entity.setType(5);//�������Ϸ��أ������ͣ������浽��ʷ��¼
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
										//@@11.30���˱����쳣����
										if(mBq100Entity.getElectricDevType()==6){
											if(Double.parseDouble(valueStr)<80||Double.parseDouble(valueStr)>300){
												return null;
											}//@@11.30����ͻ���������
										}else if(mBq100Entity.getElectricDevType()==7){
											if(Double.parseDouble(valueStr)<0||Double.parseDouble(valueStr)>63){
												return null;
											}//@@11.30 ����ͻ���������
										}else{
											if(Double.parseDouble(valueStr)<0||Double.parseDouble(valueStr)>900){
												return null;
											}//@@11.30 ����ͻ���������
										}
										break;
									case 6:
										//����
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
												}//@@10.31 ����ͻ���������
												mBq100Entity.setData6(valueData);
											}else if(heartType==7){
												if(Double.parseDouble(valueData)<0||Double.parseDouble(valueData)>63){
													return null;
												}//@@10.31 ����ͻ���������
												mBq100Entity.setData7(valueData);
											}else{
												if(Double.parseDouble(valueData)<0||Double.parseDouble(valueData)>900){
													return null;
												}//@@10.31 ����ͻ���������
												mBq100Entity.setData8(valueData);
											}
										}
										break;
									case 43://��ֵ
										mBq100Entity.setType(4);
										ElectricThresholdBean mElectricThresholdBean = Utils.unElectricThresholdBeanPackage2(data);
										mBq100Entity.setmElectricThresholdBean(mElectricThresholdBean);
										break;
									case 36://����
										mBq100Entity.setType(5);
										int alarmValue1 = data[30]&0xff;
										log.debug(alarmValue1+"=alarmValue1");
										String alarmValue = alarmValue1+"";
										mBq100Entity.setAlarmData(alarmValue);
										mBq100Entity.setAlarmType(136);	
										return null;//@@11.17���ι���
//										break;
									default:
										break;
									}
									mRePeaterData.setmBQ200Entity(mBq100Entity);
									break;
		
								case 230://E6 �����豸1.0��
									mRePeaterData.setDeviceType(2);
									int bq100Len = electricDataLen-14;
									if(bq100Len==8){
										//����
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
										//����
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
		crcByte[11]=0x00;//��ѡ����
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
	
	//ͬ���̸�����������ն�,���fireMacList.size()>0,count=fireMacList.size()*4+2,���fireMacList.size()=0��count=0
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
	
	//@@����������
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
	
	
	//������λָ��� 22121720
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
		byte[] crcByte = new byte[byteLen*4+7];	//crc��֤
		byte[] sumByte = new byte[byteLen*4+35];	//У�����֤
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
	 * �·�����ȡ������
	 */
	public static byte[] ackAlarmPackage(String repeaterMac,byte type,List<String> soundList){
		int byteLen = soundList.size();
		
		byte[] ackByte = new byte[byteLen*4+40];
		byte[] crcByte = new byte[byteLen*4+7];		//crc��֤
		byte[] sumByte = new byte[byteLen*4+35];	//У�����֤
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
		byte[] sumByte = new byte[28+addresslen];	//У�����֤
		for(int i=0;i<24;i++){
			ackByte[i] = byteDatas[i];
			if(i>1){
				sumByte[i-2] = byteDatas[i];
			}
		}
		
		ackByte[24] = new IntegerTo16().algorismToHEXString(3+addresslen);//6e+���ݳ���1���ֽ�+0d��β3���ֽ�
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
	
	private static PrinterEntity getPrinterInfo(byte[] datas){//��39���ֽڿ�ʼ
		PrinterEntity mPrinterEntity = new PrinterEntity();
		byte[] printerMacByte = new byte[8];
		for(int i=0;i<8;i++){
			printerMacByte[i] = datas[39+i];
		}
		String macByte = IntegerTo16.bytes2Hex(printerMacByte);
		String printerMac = IntegerTo16.asciiStringToString(macByte);
		mPrinterEntity.setFaultCode(printerMac);
		String contentStr1 = IntegerTo16.bytes2Hex(datas).toUpperCase();
		//��ӡ���ĳ���
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
			String contentFirst = IntegerTo16.hexToStringGBK(list.get(0));//��18��18��  ���⾯����
			String contentSecond = IntegerTo16.hexToStringGBK(list.get(1));//����
			String contentThird = IntegerTo16.hexToStringGBK(list.get(2));//ũ�Ŵ��ô�
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
	
	private static PrinterEntity getPrinterInfo2(byte[] datas){//��14���ֽڿ�ʼ
		PrinterEntity mPrinterEntity = new PrinterEntity();
		Map<Integer,Integer> hMap = new HashMap<Integer,Integer>();
		int e13 = datas[13]&0xff;	//����
		int e14 = datas[14]&0xff;	//��ʼλ	
		int e17 = datas[17]&0xff; 	//��ȡ�����þݳ���
		int e40 = datas[40]&0xff;	//1��ʾ��������0��ʾ״̬�ı��
		if(e40 == 1){
			for (int i = 0; i < e17/2; i++) {
				int e1 = e14+i;
				int e2 = datas[19+2*i]&0xff;
				hMap.put(e1, e2);
			}
		}else if(e40 == 0){
			int e41 = datas[41]&0xff;		//��Ҫ����״̬������
			for(int i = 1;i<=e41;i++){
				int e1 = datas[41+i]&0xff;  //ȡ����״̬��λ��
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
		if(controlCmd==2){	//�������ݴ���
			int tabbingCmd = dataN[27]&0xff;	//��ʶ������ͱ�־
			int infoNum = dataN[28]&0xff;		//��Ϣ��Ŀ
			switch(tabbingCmd){
			case 1:	//�ϴ�����������ʩϵͳ״̬============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//ϵͳ��־
					int systemAddress = dataN[30+i*10]&0xff;	//ϵͳ��ַ
					int systemState = (dataN[32+i*10]&0xff)+((dataN[31+i*10]&0xff)<<8);	//ϵͳ״̬
					
					if((systemState&0x01)==1){	//��������״̬
//						stateTab.put(1, "��������״̬");
//						stateTabList.add("��������״̬");
					}else if((systemState&0x01)==0){	//����״̬
						stateTab.put(1, "����״̬");
					}
					if((systemState&0x02)==2){	//��
						stateTab.put(2, "��");
						stateTabList.add("��");
					}else if((systemState&0x02)==0){	//�޻�
						stateTab.put(2, "�޻�");
					}
					if((systemState&0x04)==4){	//����
						stateTab.put(3, "����");
						stateTabList.add("����");
					}else if((systemState&0x04)==0){	//�޹���
						stateTab.put(3, "�޹���");
					}
					if((systemState&0x08)==8){	//����
						stateTab.put(4, "����");
						stateTabList.add("����");
					}else if((systemState&0x08)==0){	//������
						stateTab.put(4, "������");
					}
					if((systemState&0x10)==16){	//���
						stateTab.put(5, "���");
						stateTabList.add("���");
					}else if((systemState&0x10)==0){	//�޼��
						stateTab.put(5, "�޼��");
					}
					if((systemState&0x20)==32){	//������������
						stateTab.put(6, "������������");
						stateTabList.add("������������");
					}else if((systemState&0x20)==0){	//ֹͣ���رգ�
						stateTab.put(6, "ֹͣ���رգ�");
					}
					if((systemState&0x40)==64){	//����
						stateTab.put(7, "����");
						stateTabList.add("����");
					}else if((systemState&0x40)==0){	//�޷���
						stateTab.put(7, "�޷���");
					}
					if((systemState&0x80)==128){	//��ʱ״̬
						stateTab.put(8, "��ʱ״̬");
						stateTabList.add("��ʱ״̬");
					}else if((systemState&0x80)==0){	//δ��ʱ
						stateTab.put(8, "δ��ʱ");
					}
					if((systemState&0x100)==256){	//�������
						stateTab.put(9, "�������");
						stateTabList.add("�������");
					}else if((systemState&0x100)==0){	//��������
						stateTab.put(9, "��������");
					}
					if((systemState&0x200)==512){	//�������
						stateTab.put(10, "�������");
						stateTabList.add("�������");
					}else if((systemState&0x200)==0){	//��������
						stateTab.put(10, "��������");
					}
					if((systemState&0x400)==1024){	//���߹���
						stateTab.put(11, "���߹���");
						stateTabList.add("���߹���");
					}else if((systemState&0x400)==0){	//��������
						stateTab.put(11, "��������");
					}
					if((systemState&0x800)==2048){	//�ֶ�״̬
						stateTab.put(12, "�ֶ�״̬");
						stateTabList.add("�ֶ�״̬");
					}else if((systemState&0x800)==0){	//�Զ�״̬
						stateTab.put(12, "�Զ�״̬");
					}
					if((systemState&0x1000)==4096){	//���øı�
						stateTab.put(13, "���øı�");
						stateTabList.add("���øı�");
					}else if((systemState&0x1000)==0){	//�����øı�
						stateTab.put(13, "�����øı�");
					}
					if((systemState&0x2000)==8192){	//��λ
						stateTab.put(14, "��λ");
						stateTabList.add("��λ");
					}else if((systemState&0x2000)==0){	//����
						stateTab.put(14, "����");
					}
					
					switch(systemTab){
					case 1:	//���ֱ���ϵͳ
						faultData.setSystemTab("���ֱ���ϵͳ");
						break;
					case 10:	//��������������
						faultData.setSystemTab("��������������");
						break;
					case 11:	//����˨ϵͳ
						faultData.setSystemTab("����˨ϵͳ");
						break;
					case 12:	//�Զ���ˮ���ϵͳ
						faultData.setSystemTab("�Զ���ˮ���ϵͳ");
						break;
					case 13:	//�������ϵͳ
						faultData.setSystemTab("�������ϵͳ");
						break;
					case 14:	//ˮ�������ϵͳ����������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ����������ʽ��");
						break;
					case 15:	//ˮ�������ϵͳ��ѹ������������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ��ѹ������������ʽ��");
						break;
					case 16:	//��ĭ���ϵͳ
						faultData.setSystemTab("��ĭ���ϵͳ");
						break;
					case 17:	//�ɷ����ϵͳ
						faultData.setSystemTab("�ɷ����ϵͳ");
						break;
					case 18:	//��������ϵͳ
						faultData.setSystemTab("��������ϵͳ");
						break;
					case 19:	//�����ż�����ϵͳ
						faultData.setSystemTab("�����ż�����ϵͳ");
						break;
					case 20:	//��������
						faultData.setSystemTab("��������");
						break;
					case 21:	//����Ӧ���㲥
						faultData.setSystemTab("����Ӧ���㲥");
						break;
					case 22:	//����Ӧ����������ɢָʾϵͳ
						faultData.setSystemTab("����Ӧ����������ɢָʾϵͳ");
						break;
					case 23:	//������Դ
						faultData.setSystemTab("������Դ");
						break;
					case 24:	//�����绰
						faultData.setSystemTab("�����绰");
						break;
					}
					faultData.setTabbingCmd("�ϴ�����������ʩϵͳ״̬");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 2:	//�ϴ�����������ʩ��������״̬==============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*46]&0xff;		//ϵͳ��־
					int systemAddress = dataN[30+i*46]&0xff;	//ϵͳ��ַ
					int unitType = dataN[31+i*46]&0xff;		//��������
					byte[] macByte = new byte[4];
		    		for(int j=0;j<4;j++){
		    			macByte[j] = dataN[32+j+i*46];
		    		}//��������Э���ȡ������ַ
		    		
		    		
		    		String unitAddress = IntegerTo16.bytes2Hex(macByte);
					/*int bj1 = (dataN[32+i*46]&0xff)+((dataN[33+i*46]&0xff)<<8); 	//λ��
					int bj2 = (dataN[34+i*46]&0xff)+((dataN[35+i*46]&0xff)<<8); 	//����
		    		String unitAddress = bj1+"λ"+bj2+"��";	//������ַ
*/		    		byte[] unitMemo = new byte[31];							//����˵��
		    		for (int j = 0; j < 31; j++) {
		    			unitMemo[i] = dataN[38+j+i*46];
					}
		    		String unitMemos = new String(unitMemo,"gb18030");		//����˵��ת�ɺ���
					int unitState = (dataN[37+i*46]&0xff)+((dataN[36+i*46]&0xff)<<8);	//����״̬
					if(unitState==11){
						stateTabList.add("��������");
					}else if(unitState==12){
						stateTabList.add("��������");
					}else if(unitState==13){
						stateTabList.add("��������");
					}else if(unitState==14){
						stateTabList.add("��������");
					}else if(unitState==15){
						stateTabList.add("��Դ��������");
					}else{
						if((unitState&0x01)==1){	//��������״̬
//							stateTab.put(1, "��������״̬");
//							stateTabList.add("��������״̬");
						}else if((unitState&0x01)==0){	//����״̬
							stateTab.put(1, "����״̬");
						}
						if((unitState&0x02)==2){	//��
							stateTab.put(2, "��");
							stateTabList.add("��");
						}else if((unitState&0x02)==0){	//�޻�
							stateTab.put(2, "�޻�");
						}
						if((unitState&0x04)==4){	//����
							stateTab.put(3, "����");
							stateTabList.add("����");
						}else if((unitState&0x04)==0){	//�޹���
							stateTab.put(3, "�޹���");
						}
						if((unitState&0x08)==8){	//����
							stateTab.put(4, "����");
							stateTabList.add("����");
						}else if((unitState&0x08)==0){	//������
							stateTab.put(4, "������");
						}
						if((unitState&0x10)==16){	//���
							stateTab.put(5, "���");
							stateTabList.add("���");
						}else if((unitState&0x10)==0){	//�޼��
							stateTab.put(5, "�޼��");
						}
						if((unitState&0x20)==32){	//������������
							stateTab.put(6, "������������");
							stateTabList.add("������������");
						}else if((unitState&0x20)==0){	//ֹͣ���رգ�
							stateTab.put(6, "ֹͣ���رգ�");
						}
						if((unitState&0x40)==64){	//����
							stateTab.put(7, "����");
							stateTabList.add("����");
						}else if((unitState&0x40)==0){	//�޷���
							stateTab.put(7, "�޷���");
						}
						if((unitState&0x80)==128){	//��ʱ״̬
							stateTab.put(8, "��ʱ״̬");
							stateTabList.add("��ʱ״̬");
						}else if((unitState&0x80)==0){	//δ��ʱ
							stateTab.put(8, "δ��ʱ");
						}
						if((unitState&0x100)==256){	//��Դ����
							stateTab.put(9, "��Դ����");
							stateTabList.add("��Դ����");
						}else if((unitState&0x100)==0){	//��Դ����
							stateTab.put(9, "��Դ����");
						}
						if(unitState==512){	//��
							stateTab.put(10, "��");
							stateTabList.add("��");
						}else if((unitState&0x100)==0){	//��Դ����
							stateTab.put(10, "�޻�");
						}
					}
					switch(unitType){		//��������
						case 1:	//	���ֱ���������
							faultData.setUnitType("���ֱ���������");
							break;
						case 10:	//��ȼ����̽����
							faultData.setUnitType("��ȼ����̽����");
							break;
						case 11:	//���Ϳ�ȼ����̽����
							faultData.setUnitType("���Ϳ�ȼ����̽����");
							break;
						case 12:	//����ʽ��ȼ����̽����
							faultData.setUnitType("����ʽ��ȼ����̽����");
							break;
						case 13:	//���Ϳ�ȼ����̽����
							faultData.setUnitType("���Ϳ�ȼ����̽����");
							break;
						case 16:	//�������ּ�ر�����
							faultData.setUnitType("�������ּ�ر�����");
							break;
						case 17:	//ʣ�����ʽ�������ּ��̽����
							faultData.setUnitType("ʣ�����ʽ�������ּ��̽����");
							break;
						case 18:	//����ʽ�������ּ��̽����
							faultData.setUnitType("����ʽ�������ּ��̽����");
							break;
						case 21:	//̽���· �ĳ������ӿڰ��·
//							faultData.setUnitType("̽���·");(�ӿڰ��·)����
							faultData.setUnitType("(�ӿڰ��·)����");
							break;
						case 22:	//������ʾ��
							faultData.setUnitType("������ʾ��");
							break;
						case 23:	//�ֶ����ֱ�����ť
							faultData.setUnitType("�ֶ����ֱ�����ť");
							break;
						case 24:	//����˨��ť
							faultData.setUnitType("����˨��ť");
							break;
						case 25:	//����̽����
							faultData.setUnitType("����̽����");
							break;
						case 30:	//���»���̽����
							faultData.setUnitType("���»���̽����");
							break;
						case 31:	//���͸��»���̽����
							faultData.setUnitType("���͸��»���̽����");
							break;
						case 32:	//���͸��»���̽������S�ͣ�
							faultData.setUnitType("���͸��»���̽������S�ͣ�");
							break;
						case 33:	//���͸��»���̽������R�ͣ�
							faultData.setUnitType("���͸��»���̽������R�ͣ�");
							break;
						case 34:	//���͸��»���̽����
							faultData.setUnitType("���͸��»���̽����");
							break;
						case 35:	//���͸��»���̽����(S��)
							faultData.setUnitType("���͸��»���̽����(S��)");
							break;
						case 36:	//���͸��»���̽����(R��)
							faultData.setUnitType("���͸��»���̽����(R��)");
							break;
						case 37:	//���˸��»���̽����
							faultData.setUnitType("���˸��»���̽����");
							break;
						case 40:	//���̻���̽����
							faultData.setUnitType("���̻���̽����");
							break;
						case 41:	//�������Ӹ��̻���̽����
							faultData.setUnitType("�������Ӹ��̻���̽����");
							break; 
						case 42:	//���͹����̻���̽����
							faultData.setUnitType("���͹����̻���̽����");
							break; 
						case 43:	//���͹������̻���̽����
							faultData.setUnitType("���͹������̻���̽����");
							break; 
						case 44:	//����ʽ���̻���̽����
							faultData.setUnitType("����ʽ���̻���̽����");
							break; 
						case 50:	//����ʽ����̽����
							faultData.setUnitType("����ʽ����̽����");
							break; 
						case 51:	//����ʽ���̸��»���̽����
							faultData.setUnitType("����ʽ���̸��»���̽����");
							break; 
						case 52:	//����ʽ�й���»���̽����
							faultData.setUnitType("����ʽ�й���»���̽����");
							break; 
						case 53:	//����ʽ�й���̻���̽����
							faultData.setUnitType("����ʽ�й���̻���̽����");
							break; 
						case 61:	//�������̽����
							faultData.setUnitType("�������̽����");
							break; 
						case 62:	//�������̽����
							faultData.setUnitType("�������̽����");
							break; 
						case 69:	//�й����̽����
							faultData.setUnitType("�й����̽����");
							break; 
						case 74:	//����̽����
							faultData.setUnitType("����̽����");
							break; 
						case 78:	//ͼ������ʽ����̽����
							faultData.setUnitType("ͼ������ʽ����̽����");
							break;
						case 79:	//�������ֲ���
							faultData.setUnitType("�������ֲ���");
							break; 
						case 81:	//������������
							faultData.setUnitType("������������");
							break; 
						case 82:	//������������װ��
							faultData.setUnitType("������������װ��");
							break; 
						case 83:	//����������ͼ����ʾװ��
							faultData.setUnitType("����������ͼ����ʾװ��");
							break; 
						case 84:	//ģ��
							faultData.setUnitType("ģ��");
							break;
						case 85:	//����ģ��
							faultData.setUnitType("����ģ��");
							break;
						case 86:	//���ģ��
							faultData.setUnitType("���ģ��");
							break;
						case 87:	//�������ģ��
							faultData.setUnitType("�������ģ��");
							break;
						case 88:	//�м�ģ��
							faultData.setUnitType("�м�ģ��");
							break;
						case 91:	//����ˮ��
							faultData.setUnitType("����ˮ��");
							break;
						case 92:	//����ˮ��
							faultData.setUnitType("����ˮ��");
							break;
						case 95:	//���ܱ�
							faultData.setUnitType("���ܱ�");
							break;
						case 96:	//ˮ��ָʾ��
							faultData.setUnitType("ˮ��ָʾ��");
							break;
						case 97:	//�źŷ�
							faultData.setUnitType("�źŷ�");
							break;
						case 98:	//������
							faultData.setUnitType("������");
							break;
						case 99:	//ѹ������
							faultData.setUnitType("ѹ������");
							break;
						case 101:	//������װ��
							faultData.setUnitType("������װ��");
							break;
						case 102:	//������
							faultData.setUnitType("������");
							break;
						case 103:	//����
							faultData.setUnitType("����");
							break;
						case 104:	//ͨ��յ�
							faultData.setUnitType("ͨ��յ�");
							break;
						case 105:	//��ĭҺ��
							faultData.setUnitType("ĭҺ��");
							break;
						case 106:	//������ŷ�
							faultData.setUnitType("������ŷ�");
							break;
						case 111:	//�������̷��
							faultData.setUnitType("�������̷��");
							break;
						case 113:	//���̷���
							faultData.setUnitType("���̷���");
							break;
						case 114:	//�����ͷ��
							faultData.setUnitType("�����ͷ��");
							break;
						case 115:	//���̿�
							faultData.setUnitType("���̿�");
							break;
						case 116:	//��ص��̴���
							faultData.setUnitType("��ص��̴���");
							break;
						case 117:	//�������������
							faultData.setUnitType("�������������");
							break;
						case 118:	//�����ż����
							faultData.setUnitType("�����ż����");
							break;
						case 121:	//����װ��
							faultData.setUnitType("����װ��");
							break;
						case 128:	//��ӡ�����ϡ�������ӡ������
							faultData.setUnitType("��ӡ������");
							break;
						case 251:	//������ϡ������������
							faultData.setUnitType("�������");
							break;
						case 249:	//������ϡ������������
							faultData.setUnitType("�������");
							break;
					}
					switch(systemTab){
					case 1:	//���ֱ���ϵͳ
						faultData.setSystemTab("���ֱ���ϵͳ");
						break;
					case 10:	//��������������
						faultData.setSystemTab("��������������");
						break;
					case 11:	//����˨ϵͳ
						faultData.setSystemTab("����˨ϵͳ");
						break;
					case 12:	//�Զ���ˮ���ϵͳ
						faultData.setSystemTab("�Զ���ˮ���ϵͳ");
						break;
					case 13:	//�������ϵͳ
						faultData.setSystemTab("�������ϵͳ");
						break;
					case 14:	//ˮ�������ϵͳ����������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ����������ʽ��");
						break;
					case 15:	//ˮ�������ϵͳ��ѹ������������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ��ѹ������������ʽ��");
						break;
					case 16:	//��ĭ���ϵͳ
						faultData.setSystemTab("��ĭ���ϵͳ");
						break;
					case 17:	//�ɷ����ϵͳ
						faultData.setSystemTab("�ɷ����ϵͳ");
						break;
					case 18:	//��������ϵͳ
						faultData.setSystemTab("��������ϵͳ");
						break;
					case 19:	//�����ż�����ϵͳ
						faultData.setSystemTab("�����ż�����ϵͳ");
						break;
					case 20:	//��������
						faultData.setSystemTab("��������");
						break;
					case 21:	//����Ӧ���㲥
						faultData.setSystemTab("����Ӧ���㲥");
						break;
					case 22:	//����Ӧ����������ɢָʾϵͳ
						faultData.setSystemTab("����Ӧ����������ɢָʾϵͳ");
						break;
					case 23:	//������Դ
						faultData.setSystemTab("������Դ");
						break;
					case 24:	//�����绰
						faultData.setSystemTab("�����绰");
						break;
					}
					faultData.setTabbingCmd("�ϴ�����������ʩ��������״̬");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setUnitAddress(unitAddress);
					faultData.setUnitMemos(unitMemos);
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 3:	//�ϴ�����������ʩ����ģ����ֵ
				break;
			case 4:	//�ϴ�����������ʩ������Ϣ==================
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//ϵͳ��־
					int systemAddress = dataN[30+i*10]&0xff;	//ϵͳ��ַ
					int operaTab = dataN[31+i*10]&0xff;	//������־
					int operator = dataN[32+i*10]&0xff;	//����Ա���
					faultData.setTabbingCmd("�ϴ�����������ʩ������Ϣ");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//��λ
						stateTab.put(1, "��λ");
						stateTabList.add("��λ");
					}else if((operaTab&0x01)==0){	//�޲���
						stateTab.put(1, "�޲���");
					}
					if((operaTab&0x02)==2){	//����
						stateTab.put(2, "����");
						stateTabList.add("����");
					}else if((operaTab&0x02)==0){	//�޲���
						stateTab.put(2, "�޲���");
					}
					if((operaTab&0x04)==4){	//�ֶ�����
						stateTab.put(3, "�ֶ�����");
						stateTabList.add("�ֶ�����");
					}else if((operaTab&0x04)==0){	//�޲���
						stateTab.put(3, "�޲���");
					}
					if((operaTab&0x08)==8){	//��������
						stateTab.put(4, "��������");
						stateTabList.add("��������");
					}else if((operaTab&0x08)==0){	//�޲���
						stateTab.put(4, "�޲���");
					}
					if((operaTab&0x10)==16){	//�Լ�
						stateTab.put(5, "�Լ�");
						stateTabList.add("�Լ�");
					}else if((operaTab&0x10)==0){	//�޲���
						stateTab.put(5, "�޲���");
					}
					if((operaTab&0x20)==32){	//ȷ��
						stateTab.put(6, "ȷ��");
						stateTabList.add("ȷ��");
					}else if((operaTab&0x20)==0){	//�޲���
						stateTab.put(6, "�޲���");
					}
					if((operaTab&0x40)==64){	//����
						stateTab.put(7, "����");
						stateTabList.add("����");
					}else if((operaTab&0x40)==0){	//�޲���
						stateTab.put(7, "�޲���");
					}
					faultData.setStateTab(stateTab);
					switch(systemTab){
					case 1:	//���ֱ���ϵͳ
						faultData.setSystemTab("���ֱ���ϵͳ");
						break;
					case 10:	//��������������
						faultData.setSystemTab("��������������");
						break;
					case 11:	//����˨ϵͳ
						faultData.setSystemTab("����˨ϵͳ");
						break;
					case 12:	//�Զ���ˮ���ϵͳ
						faultData.setSystemTab("�Զ���ˮ���ϵͳ");
						break;
					case 13:	//�������ϵͳ
						faultData.setSystemTab("�������ϵͳ");
						break;
					case 14:	//ˮ�������ϵͳ����������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ����������ʽ��");
						break;
					case 15:	//ˮ�������ϵͳ��ѹ������������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ��ѹ������������ʽ��");
						break;
					case 16:	//��ĭ���ϵͳ
						faultData.setSystemTab("��ĭ���ϵͳ");
						break;
					case 17:	//�ɷ����ϵͳ
						faultData.setSystemTab("�ɷ����ϵͳ");
						break;
					case 18:	//��������ϵͳ
						faultData.setSystemTab("��������ϵͳ");
						break;
					case 19:	//�����ż�����ϵͳ
						faultData.setSystemTab("�����ż�����ϵͳ");
						break;
					case 20:	//��������
						faultData.setSystemTab("��������");
						break;
					case 21:	//����Ӧ���㲥
						faultData.setSystemTab("����Ӧ���㲥");
						break;
					case 22:	//����Ӧ����������ɢָʾϵͳ
						faultData.setSystemTab("����Ӧ����������ɢָʾϵͳ");
						break;
					case 23:	//������Դ
						faultData.setSystemTab("������Դ");
						break;
					case 24:	//�����绰
						faultData.setSystemTab("�����绰");
						break;
					}
				}
				break;
			case 5:	//�ϴ�����������ʩ����汾
				break;
			case 6:	//�ϴ�����������ʩϵͳ�������
				break;
			case 7:	//�ϴ�����������ʩϵͳ�����������
				break;
			case 8:	//�ϴ�����������ʩϵͳʱ��
				break;
			case 22:	//Ԥ������������
				faultData.setSystemTab("��������");
				byte ackByte[] = getAckByte(dataN,22);
				faultData.setAckByte(ackByte);
				break;
			case 21:	//�ϴ��û���Ϣ����װ������״̬******===============
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("�ϴ��û���Ϣ����װ������״̬");
					int runTab = dataN[29+i*7]&0xff;		//���б�־
					if((runTab&0x01)==1){	//��������״̬
//						stateTab.put(1, "��������״̬");
//						stateTabList.add("��������״̬");
					}else if((runTab&0x01)==0){	//����״̬
						stateTab.put(1, "����״̬");
					}
					if((runTab&0x02)==2){	//��
						stateTab.put(2, "��");
						stateTabList.add("��");
					}else if((runTab&0x02)==0){	//�޻�
						stateTab.put(2, "�޻�");
					}
					if((runTab&0x04)==4){	//����
						stateTab.put(3, "����");
						stateTabList.add("����");
					}else if((runTab&0x04)==0){	//�޹���
						stateTab.put(3, "�޹���");
					}
					if((runTab&0x08)==8){	//�������
						stateTab.put(4, "�������");
						stateTabList.add("�������");
					}else if((runTab&0x08)==0){	//��������
						stateTab.put(4, "��������");
					}
					if((runTab&0x10)==16){	//�������
						stateTab.put(5, "�������");
						stateTabList.add("�������");
					}else if((runTab&0x10)==0){	//��������
						stateTab.put(5, "��������");
					}
					if((runTab&0x20)==32){	//��������ͨ���ŵ�����
						stateTab.put(6, "��������ͨ���ŵ�����");
						stateTabList.add("��������ͨ���ŵ�����");
					}else if((runTab&0x20)==0){	//ͨ���ŵ�����
						stateTab.put(6, "ͨ���ŵ�����");
					}
					if((runTab&0x40)==64){	//���������·����
						stateTab.put(7, "���������·����");
						stateTabList.add("���������·����");
					}else if((runTab&0x40)==0){	//���������·����
						stateTab.put(7, "���������·����");
					}
					faultData.setStateTab(stateTab);
				}
				break;
			case 24:	//�ϴ��û���Ϣ����װ�ò�����Ϣ��¼===================
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("�ϴ��û���Ϣ����װ�ò�����Ϣ��¼");
					int operaTab = dataN[29+i*8]&0xff;	//������־
					int operator = dataN[30+i*8]&0xff;	//����Ա���
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//��λ
						stateTab.put(1, "��λ");
						stateTabList.add("��λ");
					}else if((operaTab&0x01)==0){	//�޲���
						stateTab.put(1, "�޲���");
					}
					if((operaTab&0x02)==2){	//����
						stateTab.put(2, "����");
						stateTabList.add("����");
					}else if((operaTab&0x02)==0){	//�޲���
						stateTab.put(2, "�޲���");
					}
					if((operaTab&0x04)==4){	//�ֶ�����
						stateTab.put(3, "�ֶ�����");
						stateTabList.add("�ֶ�����");
						byte[] lzack4 ={(byte)0x40,(byte)0x40,(byte)0x29,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0A,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x01,(byte)0x20,(byte)0x01,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x22,(byte)0x23,(byte)0x23}; 
						faultData.setAckByte(lzack4);
					}else if((operaTab&0x04)==0){	//�޲���
						stateTab.put(3, "�޲���");
					}
					if((operaTab&0x08)==8){	//��������
						stateTab.put(4, "��������");
						stateTabList.add("��������");
					}else if((operaTab&0x08)==0){	//�޲���
						stateTab.put(4, "�޲���");
					}
					if((operaTab&0x10)==16){	//�Լ�
						stateTab.put(5, "�Լ�");
						stateTabList.add("�Լ�");
					}else if((operaTab&0x10)==0){	//�޲���
						stateTab.put(5, "�޲���");
					}
					if((operaTab&0x20)==32){	//���Ӧ��
						stateTab.put(6, "���Ӧ��");
						stateTabList.add("���Ӧ��");
					}else if((operaTab&0x20)==0){	//�޲���
						stateTab.put(6, "�޲���");
					}
					if((operaTab&0x40)==64){	//���Զ�Ӧ���ڵ�����
						stateTab.put(7, "����");
						stateTabList.add("����");
						byte[] LZackByte = {(byte)0x40,(byte)0x40,(byte)0x04,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x1A,(byte)0x02,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x03,(byte)0x00,(byte)0x02,(byte)0x5B,(byte)0x01,(byte)0x01,(byte)0xC2,(byte)0x23,(byte)0x23};
						faultData.setAckByte(LZackByte);
						return null;
					}else if((operaTab&0x40)==0){	//�޲���
						stateTab.put(7, "�޲���");
					}
					faultData.setStateTab(stateTab);
				}
				break;
			case 25:	//�ϴ��û���Ϣ����װ������汾
				break;
			case 26:	//�ϴ��û���Ϣ����װ���������
				break;
			case 28:	//�ϴ��û���Ϣ����װ��ϵͳʱ��
				break;
			case 30:	//����װ�� GSM ����
				stateTabList.add("GSM ����");
				faultData.setTabbingCmd("����װ�� GSM ����");
				faultData.setSystemAddress("0");
				faultData.setUnitAddress("0");
				byte ackByte30[] = getAckByte(dataN,30);
				faultData.setAckByte(ackByte30);
				break;
			case 61:	//������������ʩϵͳ״̬
				break;
			case 62:	//������������ʩ��������״̬
				break;
			case 63:	//������������ʩ����ģ����ֵ
				break;
			case 64:	//������������ʩ������Ϣ
				break;
			case 65:	//������������ʩ����汾
				break;
			case 66:	//������������ʩϵͳ�������
				break;
			case 67:	//������������ʩ�����������
				break;
			case 68:	//������������ʩϵͳʱ��
				break;
			case 81:	//���û���Ϣ����װ������״̬
				break;
			case 84:	//���û���Ϣ����װ�ò�����Ϣ��¼
				break;
			case 85:	//���û���Ϣ����װ������汾
				break;
			case 86:	//���û���Ϣ����װ���������
				break;
			case 88:	//���û���Ϣ����װ��ϵͳʱ��
				break;
			case 89:	//��ʼ���û���Ϣ����װ��
				break;
			case 90:	//ͬ���û���Ϣ����װ��ʱ��
				break;
			case 91:	//�������
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
		if(controlCmd==2){	//�������ݴ���
			int tabbingCmd = dataN[27]&0xff;	//��ʶ������ͱ�־
			int infoNum = dataN[28]&0xff;		//��Ϣ��Ŀ
			switch(tabbingCmd){
			case 1:	//�ϴ�����������ʩϵͳ״̬============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//ϵͳ��־
					int systemAddress = dataN[30+i*10]&0xff;	//ϵͳ��ַ
					int systemState = (dataN[32+i*10]&0xff)+((dataN[31+i*10]&0xff)<<8);	//ϵͳ״̬
					
					if((systemState&0x01)==1){	//��������״̬
//						stateTab.put(1, "��������״̬");
//						stateTabList.add("��������״̬");
					}else if((systemState&0x01)==0){	//����״̬
						stateTab.put(1, "����״̬");
					}
					if((systemState&0x02)==2){	//��
						stateTab.put(2, "��");
						stateTabList.add("��");
					}else if((systemState&0x02)==0){	//�޻�
						stateTab.put(2, "�޻�");
					}
					if((systemState&0x04)==4){	//����
						stateTab.put(3, "����");
						stateTabList.add("����");
					}else if((systemState&0x04)==0){	//�޹���
						stateTab.put(3, "�޹���");
					}
					if((systemState&0x08)==8){	//����
						stateTab.put(4, "����");
						stateTabList.add("����");
					}else if((systemState&0x08)==0){	//������
						stateTab.put(4, "������");
					}
					if((systemState&0x10)==16){	//���
						stateTab.put(5, "���");
						stateTabList.add("���");
					}else if((systemState&0x10)==0){	//�޼��
						stateTab.put(5, "�޼��");
					}
					if((systemState&0x20)==32){	//������������
						stateTab.put(6, "������������");
						stateTabList.add("������������");
					}else if((systemState&0x20)==0){	//ֹͣ���رգ�
						stateTab.put(6, "ֹͣ���رգ�");
					}
					if((systemState&0x40)==64){	//����
						stateTab.put(7, "����");
						stateTabList.add("����");
					}else if((systemState&0x40)==0){	//�޷���
						stateTab.put(7, "�޷���");
					}
					if((systemState&0x80)==128){	//��ʱ״̬
						stateTab.put(8, "��ʱ״̬");
						stateTabList.add("��ʱ״̬");
					}else if((systemState&0x80)==0){	//δ��ʱ
						stateTab.put(8, "δ��ʱ");
					}
					if((systemState&0x100)==256){	//�������
						stateTab.put(9, "�������");
						stateTabList.add("�������");
					}else if((systemState&0x100)==0){	//��������
						stateTab.put(9, "��������");
					}
					if((systemState&0x200)==512){	//�������
						stateTab.put(10, "�������");
						stateTabList.add("�������");
					}else if((systemState&0x200)==0){	//��������
						stateTab.put(10, "��������");
					}
					if((systemState&0x400)==1024){	//���߹���
						stateTab.put(11, "���߹���");
						stateTabList.add("���߹���");
					}else if((systemState&0x400)==0){	//��������
						stateTab.put(11, "��������");
					}
					if((systemState&0x800)==2048){	//�ֶ�״̬
						stateTab.put(12, "�ֶ�״̬");
						stateTabList.add("�ֶ�״̬");
					}else if((systemState&0x800)==0){	//�Զ�״̬
						stateTab.put(12, "�Զ�״̬");
					}
					if((systemState&0x1000)==4096){	//���øı�
						stateTab.put(13, "���øı�");
						stateTabList.add("���øı�");
					}else if((systemState&0x1000)==0){	//�����øı�
						stateTab.put(13, "�����øı�");
					}
					if((systemState&0x2000)==8192){	//��λ
						stateTab.put(14, "��λ");
						stateTabList.add("��λ");
					}else if((systemState&0x2000)==0){	//����
						stateTab.put(14, "����");
					}
					
					switch(systemTab){
					case 1:	//���ֱ���ϵͳ
						faultData.setSystemTab("���ֱ���ϵͳ");
						break;
					case 10:	//��������������
						faultData.setSystemTab("��������������");
						break;
					case 11:	//����˨ϵͳ
						faultData.setSystemTab("����˨ϵͳ");
						break;
					case 12:	//�Զ���ˮ���ϵͳ
						faultData.setSystemTab("�Զ���ˮ���ϵͳ");
						break;
					case 13:	//�������ϵͳ
						faultData.setSystemTab("�������ϵͳ");
						break;
					case 14:	//ˮ�������ϵͳ����������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ����������ʽ��");
						break;
					case 15:	//ˮ�������ϵͳ��ѹ������������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ��ѹ������������ʽ��");
						break;
					case 16:	//��ĭ���ϵͳ
						faultData.setSystemTab("��ĭ���ϵͳ");
						break;
					case 17:	//�ɷ����ϵͳ
						faultData.setSystemTab("�ɷ����ϵͳ");
						break;
					case 18:	//��������ϵͳ
						faultData.setSystemTab("��������ϵͳ");
						break;
					case 19:	//�����ż�����ϵͳ
						faultData.setSystemTab("�����ż�����ϵͳ");
						break;
					case 20:	//��������
						faultData.setSystemTab("��������");
						break;
					case 21:	//����Ӧ���㲥
						faultData.setSystemTab("����Ӧ���㲥");
						break;
					case 22:	//����Ӧ����������ɢָʾϵͳ
						faultData.setSystemTab("����Ӧ����������ɢָʾϵͳ");
						break;
					case 23:	//������Դ
						faultData.setSystemTab("������Դ");
						break;
					case 24:	//�����绰
						faultData.setSystemTab("�����绰");
						break;
					}
					faultData.setTabbingCmd("�ϴ�����������ʩϵͳ״̬");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 2:	//�ϴ�����������ʩ��������״̬==============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*46]&0xff;		//ϵͳ��־
					int systemAddress = dataN[30+i*46]&0xff;	//ϵͳ��ַ
					int unitType = dataN[31+i*46]&0xff;		//��������
					byte[] macByte = new byte[4];
		    		for(int j=0;j<4;j++){
		    			macByte[j] = dataN[32+j+i*46];
		    		}//��������Э���ȡ������ַ
		    		
		    		
		    		String unitAddress = IntegerTo16.bytes2Hex(macByte);
					/*int bj1 = (dataN[32+i*46]&0xff)+((dataN[33+i*46]&0xff)<<8); 	//λ��
					int bj2 = (dataN[34+i*46]&0xff)+((dataN[35+i*46]&0xff)<<8); 	//����
		    		String unitAddress = bj1+"λ"+bj2+"��";	//������ַ
*/		    		byte[] unitMemo = new byte[31];							//����˵��
		    		for (int j = 0; j < 31; j++) {
		    			unitMemo[i] = dataN[38+j+i*46];
					}
		    		String unitMemos = new String(unitMemo,"gb18030");		//����˵��ת�ɺ���
					int unitState = (dataN[37+i*46]&0xff)+((dataN[36+i*46]&0xff)<<8);	//����״̬
					if(unitState==11){
						stateTabList.add("��������");
					}else if(unitState==12){
						stateTabList.add("��������");
					}else if(unitState==13){
						stateTabList.add("��������");
					}else if(unitState==14){
						stateTabList.add("��������");
					}else if(unitState==15){
						stateTabList.add("��Դ��������");
					}else{
						if((unitState&0x01)==1){	//��������״̬
//							stateTab.put(1, "��������״̬");
//							stateTabList.add("��������״̬");
						}else if((unitState&0x01)==0){	//����״̬
//							stateTab.put(1, "����״̬");
						}
						if((unitState&0x02)==2){	//��
							stateTab.put(2, "��");
							stateTabList.add("��");
						}else if((unitState&0x02)==0){	//�޻�
//							stateTab.put(2, "�޻�");
						}
						if((unitState&0x04)==4){	//����
							stateTab.put(3, "����");
							stateTabList.add("����");
						}else if((unitState&0x04)==0){	//�޹���
//							stateTab.put(3, "�޹���");��
						}
						if((unitState&0x08)==8){	//����
							stateTab.put(4, "����");
							stateTabList.add("����");
						}else if((unitState&0x08)==0){	//������
//							stateTab.put(4, "������");
						}
						if((unitState&0x10)==16){	//���
							stateTab.put(5, "���");
							stateTabList.add("���");
						}else if((unitState&0x10)==0){	//�޼��
//							stateTab.put(5, "�޼��");
						}
						if((unitState&0x20)==32){	//������������
							stateTab.put(6, "������������");
							stateTabList.add("������������");
						}else if((unitState&0x20)==0){	//ֹͣ���رգ�
//							stateTab.put(6, "ֹͣ���رգ�");
						}
						if((unitState&0x40)==64){	//����
							stateTab.put(7, "����");
							stateTabList.add("����");
						}else if((unitState&0x40)==0){	//�޷���
//							stateTab.put(7, "�޷���");
						}
						if((unitState&0x80)==128){	//��ʱ״̬
							stateTab.put(8, "��ʱ״̬");
							stateTabList.add("��ʱ״̬");
						}else if((unitState&0x80)==0){	//δ��ʱ
//							stateTab.put(8, "δ��ʱ");
						}
						if((unitState&0x100)==256){	//��Դ����
							stateTab.put(9, "��Դ����");
							stateTabList.add("��Դ����");
						}else if((unitState&0x100)==0){	//��Դ����
//							stateTab.put(9, "��Դ����");
						}
						if(unitState==512){	//��
							stateTab.put(10, "��");
							stateTabList.add("��");
						}else if((unitState&0x100)==0){	//��Դ����
//							stateTab.put(10, "�޻�");
						}
					}
					switch(unitType){		//��������
						case 1:	//	���ֱ���������
							faultData.setUnitType("���ֱ���������");
							break;
						case 10:	//��ȼ����̽����
							faultData.setUnitType("��ȼ����̽����");
							break;
						case 11:	//���Ϳ�ȼ����̽����
							faultData.setUnitType("���Ϳ�ȼ����̽����");
							break;
						case 12:	//����ʽ��ȼ����̽����
							faultData.setUnitType("����ʽ��ȼ����̽����");
							break;
						case 13:	//���Ϳ�ȼ����̽����
							faultData.setUnitType("���Ϳ�ȼ����̽����");
							break;
						case 16:	//�������ּ�ر�����
							faultData.setUnitType("�������ּ�ر�����");
							break;
						case 17:	//ʣ�����ʽ�������ּ��̽����
							faultData.setUnitType("ʣ�����ʽ�������ּ��̽����");
							break;
						case 18:	//����ʽ�������ּ��̽����
							faultData.setUnitType("����ʽ�������ּ��̽����");
							break;
						case 21:	//̽���· �ĳ������ӿڰ��·
//							faultData.setUnitType("̽���·");(�ӿڰ��·)����
							faultData.setUnitType("(�ӿڰ��·)����");
							break;
						case 22:	//������ʾ��
							faultData.setUnitType("������ʾ��");
							break;
						case 23:	//�ֶ����ֱ�����ť
							faultData.setUnitType("�ֶ����ֱ�����ť");
							break;
						case 24:	//����˨��ť
							faultData.setUnitType("����˨��ť");
							break;
						case 25:	//����̽����
							faultData.setUnitType("����̽����");
							break;
						case 30:	//���»���̽����
							faultData.setUnitType("���»���̽����");
							break;
						case 31:	//���͸��»���̽����
							faultData.setUnitType("���͸��»���̽����");
							break;
						case 32:	//���͸��»���̽������S�ͣ�
							faultData.setUnitType("���͸��»���̽������S�ͣ�");
							break;
						case 33:	//���͸��»���̽������R�ͣ�
							faultData.setUnitType("���͸��»���̽������R�ͣ�");
							break;
						case 34:	//���͸��»���̽����
							faultData.setUnitType("���͸��»���̽����");
							break;
						case 35:	//���͸��»���̽����(S��)
							faultData.setUnitType("���͸��»���̽����(S��)");
							break;
						case 36:	//���͸��»���̽����(R��)
							faultData.setUnitType("���͸��»���̽����(R��)");
							break;
						case 37:	//���˸��»���̽����
							faultData.setUnitType("���˸��»���̽����");
							break;
						case 40:	//���̻���̽����
							faultData.setUnitType("���̻���̽����");
							break;
						case 41:	//�������Ӹ��̻���̽����
							faultData.setUnitType("�������Ӹ��̻���̽����");
							break; 
						case 42:	//���͹����̻���̽����
							faultData.setUnitType("���͹����̻���̽����");
							break; 
						case 43:	//���͹������̻���̽����
							faultData.setUnitType("���͹������̻���̽����");
							break; 
						case 44:	//����ʽ���̻���̽����
							faultData.setUnitType("����ʽ���̻���̽����");
							break; 
						case 50:	//����ʽ����̽����
							faultData.setUnitType("����ʽ����̽����");
							break; 
						case 51:	//����ʽ���̸��»���̽����
							faultData.setUnitType("����ʽ���̸��»���̽����");
							break; 
						case 52:	//����ʽ�й���»���̽����
							faultData.setUnitType("����ʽ�й���»���̽����");
							break; 
						case 53:	//����ʽ�й���̻���̽����
							faultData.setUnitType("����ʽ�й���̻���̽����");
							break; 
						case 61:	//�������̽����
							faultData.setUnitType("�������̽����");
							break; 
						case 62:	//�������̽����
							faultData.setUnitType("�������̽����");
							break; 
						case 69:	//�й����̽����
							faultData.setUnitType("�й����̽����");
							break; 
						case 74:	//����̽����
							faultData.setUnitType("����̽����");
							break; 
						case 78:	//ͼ������ʽ����̽����
							faultData.setUnitType("ͼ������ʽ����̽����");
							break;
						case 79:	//�������ֲ���
							faultData.setUnitType("�������ֲ���");
							break; 
						case 81:	//������������
							faultData.setUnitType("������������");
							break; 
						case 82:	//������������װ��
							faultData.setUnitType("������������װ��");
							break; 
						case 83:	//����������ͼ����ʾװ��
							faultData.setUnitType("����������ͼ����ʾװ��");
							break; 
						case 84:	//ģ��
							faultData.setUnitType("ģ��");
							break;
						case 85:	//����ģ��
							faultData.setUnitType("����ģ��");
							break;
						case 86:	//���ģ��
							faultData.setUnitType("���ģ��");
							break;
						case 87:	//�������ģ��
							faultData.setUnitType("�������ģ��");
							break;
						case 88:	//�м�ģ��
							faultData.setUnitType("�м�ģ��");
							break;
						case 91:	//����ˮ��
							faultData.setUnitType("����ˮ��");
							break;
						case 92:	//����ˮ��
							faultData.setUnitType("����ˮ��");
							break;
						case 95:	//���ܱ�
							faultData.setUnitType("���ܱ�");
							break;
						case 96:	//ˮ��ָʾ��
							faultData.setUnitType("ˮ��ָʾ��");
							break;
						case 97:	//�źŷ�
							faultData.setUnitType("�źŷ�");
							break;
						case 98:	//������
							faultData.setUnitType("������");
							break;
						case 99:	//ѹ������
							faultData.setUnitType("ѹ������");
							break;
						case 101:	//������װ��
							faultData.setUnitType("������װ��");
							break;
						case 102:	//������
							faultData.setUnitType("������");
							break;
						case 103:	//����
							faultData.setUnitType("����");
							break;
						case 104:	//ͨ��յ�
							faultData.setUnitType("ͨ��յ�");
							break;
						case 105:	//��ĭҺ��
							faultData.setUnitType("ĭҺ��");
							break;
						case 106:	//������ŷ�
							faultData.setUnitType("������ŷ�");
							break;
						case 111:	//�������̷��
							faultData.setUnitType("�������̷��");
							break;
						case 113:	//���̷���
							faultData.setUnitType("���̷���");
							break;
						case 114:	//�����ͷ��
							faultData.setUnitType("�����ͷ��");
							break;
						case 115:	//���̿�
							faultData.setUnitType("���̿�");
							break;
						case 116:	//��ص��̴���
							faultData.setUnitType("��ص��̴���");
							break;
						case 117:	//�������������
							faultData.setUnitType("�������������");
							break;
						case 118:	//�����ż����
							faultData.setUnitType("�����ż����");
							break;
						case 121:	//����װ��
							faultData.setUnitType("����װ��");
							break;
						case 128:	//��ӡ�����ϡ�������ӡ������
							faultData.setUnitType("��ӡ������");
							break;
						case 251:	//������ϡ������������
							faultData.setUnitType("�������");
							break;
						case 249:	//������ϡ������������
							faultData.setUnitType("�������");
							break;
					}
					switch(systemTab){
					case 1:	//���ֱ���ϵͳ
						faultData.setSystemTab("���ֱ���ϵͳ");
						break;
					case 10:	//��������������
						faultData.setSystemTab("��������������");
						break;
					case 11:	//����˨ϵͳ
						faultData.setSystemTab("����˨ϵͳ");
						break;
					case 12:	//�Զ���ˮ���ϵͳ
						faultData.setSystemTab("�Զ���ˮ���ϵͳ");
						break;
					case 13:	//�������ϵͳ
						faultData.setSystemTab("�������ϵͳ");
						break;
					case 14:	//ˮ�������ϵͳ����������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ����������ʽ��");
						break;
					case 15:	//ˮ�������ϵͳ��ѹ������������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ��ѹ������������ʽ��");
						break;
					case 16:	//��ĭ���ϵͳ
						faultData.setSystemTab("��ĭ���ϵͳ");
						break;
					case 17:	//�ɷ����ϵͳ
						faultData.setSystemTab("�ɷ����ϵͳ");
						break;
					case 18:	//��������ϵͳ
						faultData.setSystemTab("��������ϵͳ");
						break;
					case 19:	//�����ż�����ϵͳ
						faultData.setSystemTab("�����ż�����ϵͳ");
						break;
					case 20:	//��������
						faultData.setSystemTab("��������");
						break;
					case 21:	//����Ӧ���㲥
						faultData.setSystemTab("����Ӧ���㲥");
						break;
					case 22:	//����Ӧ����������ɢָʾϵͳ
						faultData.setSystemTab("����Ӧ����������ɢָʾϵͳ");
						break;
					case 23:	//������Դ
						faultData.setSystemTab("������Դ");
						break;
					case 24:	//�����绰
						faultData.setSystemTab("�����绰");
						break;
					}
					faultData.setTabbingCmd("�ϴ�����������ʩ��������״̬");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setUnitAddress(unitAddress);
					faultData.setUnitMemos(unitMemos);
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 3:	//�ϴ�����������ʩ����ģ����ֵ
				break;
			case 4:	//�ϴ�����������ʩ������Ϣ==================
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//ϵͳ��־
					int systemAddress = dataN[30+i*10]&0xff;	//ϵͳ��ַ
					int operaTab = dataN[31+i*10]&0xff;	//������־
					int operator = dataN[32+i*10]&0xff;	//����Ա���
					faultData.setTabbingCmd("�ϴ�����������ʩ������Ϣ");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//��λ
						stateTab.put(1, "��λ");
						stateTabList.add("��λ");
					}else if((operaTab&0x01)==0){	//�޲���
//						stateTab.put(1, "�޲���");
					}
					if((operaTab&0x02)==2){	//����
						stateTab.put(2, "����");
						stateTabList.add("����");
					}else if((operaTab&0x02)==0){	//�޲���
//						stateTab.put(2, "�޲���");
					}
					if((operaTab&0x04)==4){	//�ֶ�����
						stateTab.put(3, "�ֶ�����");
						stateTabList.add("�ֶ�����");
					}else if((operaTab&0x04)==0){	//�޲���
//						stateTab.put(3, "�޲���");
					}
					if((operaTab&0x08)==8){	//��������
						stateTab.put(4, "��������");
						stateTabList.add("��������");
					}else if((operaTab&0x08)==0){	//�޲���
//						stateTab.put(4, "�޲���");
					}
					if((operaTab&0x10)==16){	//�Լ�
						stateTab.put(5, "�Լ�");
						stateTabList.add("�Լ�");
					}else if((operaTab&0x10)==0){	//�޲���
//						stateTab.put(5, "�޲���");
					}
					if((operaTab&0x20)==32){	//ȷ��
						stateTab.put(6, "ȷ��");
						stateTabList.add("ȷ��");
					}else if((operaTab&0x20)==0){	//�޲���
//						stateTab.put(6, "�޲���");
					}
					if((operaTab&0x40)==64){	//����
						stateTab.put(7, "����");
						stateTabList.add("����");
					}else if((operaTab&0x40)==0){	//�޲���
//						stateTab.put(7, "�޲���");
					}
					faultData.setStateTab(stateTab);
					switch(systemTab){
					case 1:	//���ֱ���ϵͳ
						faultData.setSystemTab("���ֱ���ϵͳ");
						break;
					case 10:	//��������������
						faultData.setSystemTab("��������������");
						break;
					case 11:	//����˨ϵͳ
						faultData.setSystemTab("����˨ϵͳ");
						break;
					case 12:	//�Զ���ˮ���ϵͳ
						faultData.setSystemTab("�Զ���ˮ���ϵͳ");
						break;
					case 13:	//�������ϵͳ
						faultData.setSystemTab("�������ϵͳ");
						break;
					case 14:	//ˮ�������ϵͳ����������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ����������ʽ��");
						break;
					case 15:	//ˮ�������ϵͳ��ѹ������������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ��ѹ������������ʽ��");
						break;
					case 16:	//��ĭ���ϵͳ
						faultData.setSystemTab("��ĭ���ϵͳ");
						break;
					case 17:	//�ɷ����ϵͳ
						faultData.setSystemTab("�ɷ����ϵͳ");
						break;
					case 18:	//��������ϵͳ
						faultData.setSystemTab("��������ϵͳ");
						break;
					case 19:	//�����ż�����ϵͳ
						faultData.setSystemTab("�����ż�����ϵͳ");
						break;
					case 20:	//��������
						faultData.setSystemTab("��������");
						break;
					case 21:	//����Ӧ���㲥
						faultData.setSystemTab("����Ӧ���㲥");
						break;
					case 22:	//����Ӧ����������ɢָʾϵͳ
						faultData.setSystemTab("����Ӧ����������ɢָʾϵͳ");
						break;
					case 23:	//������Դ
						faultData.setSystemTab("������Դ");
						break;
					case 24:	//�����绰
						faultData.setSystemTab("�����绰");
						break;
					}
				}
				break;
			case 5:	//�ϴ�����������ʩ����汾
				break;
			case 6:	//�ϴ�����������ʩϵͳ�������
				break;
			case 7:	//�ϴ�����������ʩϵͳ�����������
				break;
			case 8:	//�ϴ�����������ʩϵͳʱ��
				break;
			case 22:	//Ԥ������������
				faultData.setSystemTab("��������");
				byte ackByte[] = getAckByte(dataN,22);
				faultData.setAckByte(ackByte);
				break;
			case 21:	//�ϴ��û���Ϣ����װ������״̬******===============
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("�ϴ��û���Ϣ����װ������״̬");
					int runTab = dataN[29+i*7]&0xff;		//���б�־
					if((runTab&0x01)==1){	//��������״̬
//						stateTab.put(1, "��������״̬");
//						stateTabList.add("��������״̬");
					}else if((runTab&0x01)==0){	//����״̬
//						stateTab.put(1, "����״̬");
					}
					if((runTab&0x02)==2){	//��
						stateTab.put(2, "��");
						stateTabList.add("��");
					}else if((runTab&0x02)==0){	//�޻�
//						stateTab.put(2, "�޻�");
					}
					if((runTab&0x04)==4){	//����
						stateTab.put(3, "����");
						stateTabList.add("����");
					}else if((runTab&0x04)==0){	//�޹���
//						stateTab.put(3, "�޹���");
					}
					if((runTab&0x08)==8){	//�������
						stateTab.put(4, "�������");
						stateTabList.add("�������");
					}else if((runTab&0x08)==0){	//��������
//						stateTab.put(4, "��������");
					}
					if((runTab&0x10)==16){	//�������
						stateTab.put(5, "�������");
						stateTabList.add("�������");
					}else if((runTab&0x10)==0){	//��������
//						stateTab.put(5, "��������");
					}
					if((runTab&0x20)==32){	//��������ͨ���ŵ�����
						stateTab.put(6, "��������ͨ���ŵ�����");
						stateTabList.add("��������ͨ���ŵ�����");
					}else if((runTab&0x20)==0){	//ͨ���ŵ�����
						stateTab.put(6, "ͨ���ŵ�����");
					}
					if((runTab&0x40)==64){	//���������·����
						stateTab.put(7, "���������·����");
						stateTabList.add("���������·����");
					}else if((runTab&0x40)==0){	//���������·����
						stateTab.put(7, "���������·����");
					}
					faultData.setStateTab(stateTab);
				}
				break;
			case 24:	//�ϴ��û���Ϣ����װ�ò�����Ϣ��¼===================
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("�ϴ��û���Ϣ����װ�ò�����Ϣ��¼");
					int operaTab = dataN[29+i*8]&0xff;	//������־
					int operator = dataN[30+i*8]&0xff;	//����Ա���
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//��λ
						stateTab.put(1, "��λ");
						stateTabList.add("��λ");
					}else if((operaTab&0x01)==0){	//�޲���
//						stateTab.put(1, "�޲���");
					}
					if((operaTab&0x02)==2){	//����
						stateTab.put(2, "����");
						stateTabList.add("����");
					}else if((operaTab&0x02)==0){	//�޲���
//						stateTab.put(2, "�޲���");
					}
					if((operaTab&0x04)==4){	//�ֶ�����
						stateTab.put(3, "�ֶ�����");
						stateTabList.add("�ֶ�����");
						byte[] lzack4 ={(byte)0x40,(byte)0x40,(byte)0x29,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0A,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x01,(byte)0x20,(byte)0x01,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x22,(byte)0x23,(byte)0x23}; 
						faultData.setAckByte(lzack4);
					}else if((operaTab&0x04)==0){	//�޲���
//						stateTab.put(3, "�޲���");
					}
					if((operaTab&0x08)==8){	//��������
						stateTab.put(4, "��������");
						stateTabList.add("��������");
					}else if((operaTab&0x08)==0){	//�޲���
//						stateTab.put(4, "�޲���");
					}
					if((operaTab&0x10)==16){	//�Լ�
						stateTab.put(5, "�Լ�");
						stateTabList.add("�Լ�");
					}else if((operaTab&0x10)==0){	//�޲���
//						stateTab.put(5, "�޲���");
					}
					if((operaTab&0x20)==32){	//���Ӧ��
						stateTab.put(6, "���Ӧ��");
						stateTabList.add("���Ӧ��");
					}else if((operaTab&0x20)==0){	//�޲���
//						stateTab.put(6, "�޲���");
					}
					if((operaTab&0x40)==64){	//���Զ�Ӧ���ڵ�����
						stateTab.put(7, "����");
						stateTabList.add("����");
						byte[] LZackByte = {(byte)0x40,(byte)0x40,(byte)0x04,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x1A,(byte)0x02,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x03,(byte)0x00,(byte)0x02,(byte)0x5B,(byte)0x01,(byte)0x01,(byte)0xC2,(byte)0x23,(byte)0x23};
						faultData.setAckByte(LZackByte);
						return null;
					}else if((operaTab&0x40)==0){	//�޲���
//						stateTab.put(7, "�޲���");
					}
					faultData.setStateTab(stateTab);
				}
				break;
			case 25:	//�ϴ��û���Ϣ����װ������汾
				break;
			case 26:	//�ϴ��û���Ϣ����װ���������
				break;
			case 28:	//�ϴ��û���Ϣ����װ��ϵͳʱ��
				break;
			case 30:	//����װ�� GSM ����
				stateTabList.add("GSM ����");
				faultData.setTabbingCmd("����װ�� GSM ����");
				faultData.setSystemAddress("0");
				faultData.setUnitAddress("0");
				byte ackByte30[] = getAckByte(dataN,30);
				faultData.setAckByte(ackByte30);
				break;
			case 61:	//������������ʩϵͳ״̬
				break;
			case 62:	//������������ʩ��������״̬
				break;
			case 63:	//������������ʩ����ģ����ֵ
				break;
			case 64:	//������������ʩ������Ϣ
				break;
			case 65:	//������������ʩ����汾
				break;
			case 66:	//������������ʩϵͳ�������
				break;
			case 67:	//������������ʩ�����������
				break;
			case 68:	//������������ʩϵͳʱ��
				break;
			case 81:	//���û���Ϣ����װ������״̬
				break;
			case 84:	//���û���Ϣ����װ�ò�����Ϣ��¼
				break;
			case 85:	//���û���Ϣ����װ������汾
				break;
			case 86:	//���û���Ϣ����װ���������
				break;
			case 88:	//���û���Ϣ����װ��ϵͳʱ��
				break;
			case 89:	//��ʼ���û���Ϣ����װ��
				break;
			case 90:	//ͬ���û���Ϣ����װ��ʱ��
				break;
			case 91:	//�������
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
		if(controlCmd==2){	//�������ݴ���
			int tabbingCmd = dataN[27]&0xff;	//��ʶ������ͱ�־
			int infoNum = dataN[28]&0xff;		//��Ϣ��Ŀ
			switch(tabbingCmd){
			case 1:	//�ϴ�����������ʩϵͳ״̬============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//ϵͳ��־
					int systemAddress = dataN[30+i*10]&0xff;	//ϵͳ��ַ
					int systemState = (dataN[32+i*10]&0xff)+((dataN[31+i*10]&0xff)<<8);	//ϵͳ״̬
					
					if((systemState&0x01)==1){	//��������״̬
//						stateTab.put(1, "��������״̬");
//						stateTabList.add("��������״̬");
					}else if((systemState&0x01)==0){	//����״̬
						stateTab.put(1, "����״̬");
					}
					if((systemState&0x02)==2){	//��
						stateTab.put(2, "��");
						stateTabList.add("��");
					}else if((systemState&0x02)==0){	//�޻�
						stateTab.put(2, "�޻�");
					}
					if((systemState&0x04)==4){	//����
						stateTab.put(3, "����");
						stateTabList.add("����");
					}else if((systemState&0x04)==0){	//�޹���
						stateTab.put(3, "�޹���");
					}
					if((systemState&0x08)==8){	//����
						stateTab.put(4, "����");
						stateTabList.add("����");
					}else if((systemState&0x08)==0){	//������
						stateTab.put(4, "������");
					}
					if((systemState&0x10)==16){	//���
						stateTab.put(5, "���");
						stateTabList.add("���");
					}else if((systemState&0x10)==0){	//�޼��
						stateTab.put(5, "�޼��");
					}
					if((systemState&0x20)==32){	//������������
						stateTab.put(6, "������������");
						stateTabList.add("������������");
					}else if((systemState&0x20)==0){	//ֹͣ���رգ�
						stateTab.put(6, "ֹͣ���رգ�");
					}
					if((systemState&0x40)==64){	//����
						stateTab.put(7, "����");
						stateTabList.add("����");
					}else if((systemState&0x40)==0){	//�޷���
						stateTab.put(7, "�޷���");
					}
					if((systemState&0x80)==128){	//��ʱ״̬
						stateTab.put(8, "��ʱ״̬");
						stateTabList.add("��ʱ״̬");
					}else if((systemState&0x80)==0){	//δ��ʱ
						stateTab.put(8, "δ��ʱ");
					}
					if((systemState&0x100)==256){	//�������
						stateTab.put(9, "�������");
						stateTabList.add("�������");
					}else if((systemState&0x100)==0){	//��������
						stateTab.put(9, "��������");
					}
					if((systemState&0x200)==512){	//�������
						stateTab.put(10, "�������");
						stateTabList.add("�������");
					}else if((systemState&0x200)==0){	//��������
						stateTab.put(10, "��������");
					}
					if((systemState&0x400)==1024){	//���߹���
						stateTab.put(11, "���߹���");
						stateTabList.add("���߹���");
					}else if((systemState&0x400)==0){	//��������
						stateTab.put(11, "��������");
					}
					if((systemState&0x800)==2048){	//�ֶ�״̬
						stateTab.put(12, "�ֶ�״̬");
						stateTabList.add("�ֶ�״̬");
					}else if((systemState&0x800)==0){	//�Զ�״̬
						stateTab.put(12, "�Զ�״̬");
					}
					if((systemState&0x1000)==4096){	//���øı�
						stateTab.put(13, "���øı�");
						stateTabList.add("���øı�");
					}else if((systemState&0x1000)==0){	//�����øı�
						stateTab.put(13, "�����øı�");
					}
					if((systemState&0x2000)==8192){	//��λ
						stateTab.put(14, "��λ");
						stateTabList.add("��λ");
					}else if((systemState&0x2000)==0){	//����
						stateTab.put(14, "����");
					}
					
					switch(systemTab){
					case 1:	//���ֱ���ϵͳ
						faultData.setSystemTab("���ֱ���ϵͳ");
						break;
					case 10:	//��������������
						faultData.setSystemTab("��������������");
						break;
					case 11:	//����˨ϵͳ
						faultData.setSystemTab("����˨ϵͳ");
						break;
					case 12:	//�Զ���ˮ���ϵͳ
						faultData.setSystemTab("�Զ���ˮ���ϵͳ");
						break;
					case 13:	//�������ϵͳ
						faultData.setSystemTab("�������ϵͳ");
						break;
					case 14:	//ˮ�������ϵͳ����������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ����������ʽ��");
						break;
					case 15:	//ˮ�������ϵͳ��ѹ������������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ��ѹ������������ʽ��");
						break;
					case 16:	//��ĭ���ϵͳ
						faultData.setSystemTab("��ĭ���ϵͳ");
						break;
					case 17:	//�ɷ����ϵͳ
						faultData.setSystemTab("�ɷ����ϵͳ");
						break;
					case 18:	//��������ϵͳ
						faultData.setSystemTab("��������ϵͳ");
						break;
					case 19:	//�����ż�����ϵͳ
						faultData.setSystemTab("�����ż�����ϵͳ");
						break;
					case 20:	//��������
						faultData.setSystemTab("��������");
						break;
					case 21:	//����Ӧ���㲥
						faultData.setSystemTab("����Ӧ���㲥");
						break;
					case 22:	//����Ӧ����������ɢָʾϵͳ
						faultData.setSystemTab("����Ӧ����������ɢָʾϵͳ");
						break;
					case 23:	//������Դ
						faultData.setSystemTab("������Դ");
						break;
					case 24:	//�����绰
						faultData.setSystemTab("�����绰");
						break;
					}
					faultData.setTabbingCmd("�ϴ�����������ʩϵͳ״̬");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 135:
			case 2:	//�ϴ�����������ʩ��������״̬==============
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*46]&0xff;		//ϵͳ��־
					int systemAddress = dataN[30+i*46]&0xff;	//ϵͳ��ַ
					int unitType = dataN[31+i*46]&0xff;		//��������
					byte[] macByte = new byte[4];
		    		for(int j=0;j<4;j++){
		    			macByte[j] = dataN[32+j+i*46];
		    		}//��������Э���ȡ������ַ
		    		
		    		
		    		String unitNumber = IntegerTo16.bytes2Hex(macByte);
					int bj1 = (dataN[32+i*46]&0xff)+((dataN[33+i*46]&0xff)<<8); 	//λ��
					int bj2 = (dataN[34+i*46]&0xff)+((dataN[35+i*46]&0xff)<<8); 	//����
		    		String unitAddress = bj2+"��"+bj1+"��";	//������ַ
		    		byte[] unitMemo = new byte[31];							//����˵��
		    		/*for (int j = 0; j < 31; j++) {
		    			unitMemo[i] = dataN[38+j+i*46];
					}
		    		String unitMemos = new String(unitMemo,"gb18030");		//����˵��ת�ɺ���
*/					int unitState = (dataN[37+i*46]&0xff)+((dataN[36+i*46]&0xff)<<8);	//����״̬
					if(unitState==11){
						stateTabList.add("��������");
					}else if(unitState==12){
						stateTabList.add("��������");
					}else if(unitState==13){
						stateTabList.add("��������");
					}else if(unitState==14){
						stateTabList.add("��������");
					}else if(unitState==15){
						stateTabList.add("��Դ��������");
					}else{
						if((unitState&0x01)==1){	//��������״̬
//							stateTab.put(1, "��������״̬");
//							stateTabList.add("��������״̬");
						}else if((unitState&0x01)==0){	//����״̬
							stateTab.put(1, "����״̬");
						}
						if((unitState&0x02)==2){	//��
							stateTab.put(2, "��");
							stateTabList.add("��");
						}else if((unitState&0x02)==0){	//�޻�
							stateTab.put(2, "�޻�");
						}
						if((unitState&0x04)==4){	//����
							stateTab.put(3, "����");
							stateTabList.add("����");
						}else if((unitState&0x04)==0){	//�޹���
							stateTab.put(3, "�޹���");
						}
						if((unitState&0x08)==8){	//����
							stateTab.put(4, "����");
							stateTabList.add("����");
						}else if((unitState&0x08)==0){	//������
							stateTab.put(4, "������");
						}
						if((unitState&0x10)==16){	//���
							stateTab.put(5, "���");
							stateTabList.add("���");
						}else if((unitState&0x10)==0){	//�޼��
							stateTab.put(5, "�޼��");
						}
						if((unitState&0x20)==32){	//������������
							stateTab.put(6, "������������");
							stateTabList.add("������������");
						}else if((unitState&0x20)==0){	//ֹͣ���رգ�
							stateTab.put(6, "ֹͣ���رգ�");
						}
						if((unitState&0x40)==64){	//����
							stateTab.put(7, "����");
							stateTabList.add("����");
						}else if((unitState&0x40)==0){	//�޷���
							stateTab.put(7, "�޷���");
						}
						if((unitState&0x80)==128){	//��ʱ״̬
							stateTab.put(8, "��ʱ״̬");
							stateTabList.add("��ʱ״̬");
						}else if((unitState&0x80)==0){	//δ��ʱ
							stateTab.put(8, "δ��ʱ");
						}
						if((unitState&0x100)==256){	//��Դ����
							stateTab.put(9, "��Դ����");
							stateTabList.add("��Դ����");
						}else if((unitState&0x100)==0){	//��Դ����
							stateTab.put(9, "��Դ����");
						}
						if(unitState==512){	//��
							stateTab.put(10, "��");
							stateTabList.add("��");
						}else if((unitState&0x100)==0){	//��Դ����
							stateTab.put(10, "�޻�");
						}
						if(unitState==1024){	//��
							stateTab.put(10, "�𾯻ָ�");
							stateTabList.add("�𾯻ָ�");
						}
						byte[] ackByte = {(byte)0x40,(byte)0x40,(byte)0x01,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x23,(byte)0x3A,(byte)0x09,(byte)0x1A,(byte)0x09,(byte)0x0C,(byte)0x38,(byte)0x5B,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x79,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xAB,(byte)0x23,(byte)0x23};
						faultData.setAckByte(ackByte);
					}
					switch(unitType){		//��������
						case 1:	//	���ֱ���������
							faultData.setUnitType("���ֱ���������");
							break;
						case 10:	//��ȼ����̽����
							faultData.setUnitType("��ȼ����̽����");
							break;
						case 11:	//���Ϳ�ȼ����̽����
							faultData.setUnitType("���Ϳ�ȼ����̽����");
							break;
						case 12:	//����ʽ��ȼ����̽����
							faultData.setUnitType("����ʽ��ȼ����̽����");
							break;
						case 13:	//���Ϳ�ȼ����̽����
							faultData.setUnitType("���Ϳ�ȼ����̽����");
							break;
						case 16:	//�������ּ�ر�����
							faultData.setUnitType("�������ּ�ر�����");
							break;
						case 17:	//ʣ�����ʽ�������ּ��̽����
							faultData.setUnitType("ʣ�����ʽ�������ּ��̽����");
							break;
						case 18:	//����ʽ�������ּ��̽����
							faultData.setUnitType("����ʽ�������ּ��̽����");
							break;
						case 21:	//̽���· �ĳ������ӿڰ��·
//							faultData.setUnitType("̽���·");(�ӿڰ��·)����
							faultData.setUnitType("(�ӿڰ��·)����");
							break;
						case 22:	//������ʾ��
							faultData.setUnitType("������ʾ��");
							break;
						case 23:	//�ֶ����ֱ�����ť
							faultData.setUnitType("�ֶ����ֱ�����ť");
							break;
						case 24:	//����˨��ť
							faultData.setUnitType("����˨��ť");
							break;
						case 25:	//����̽����
							faultData.setUnitType("����̽����");
							break;
						case 30:	//���»���̽����
							faultData.setUnitType("���»���̽����");
							break;
						case 31:	//���͸��»���̽����
							faultData.setUnitType("���͸��»���̽����");
							break;
						case 32:	//���͸��»���̽������S�ͣ�
							faultData.setUnitType("���͸��»���̽������S�ͣ�");
							break;
						case 33:	//���͸��»���̽������R�ͣ�
							faultData.setUnitType("���͸��»���̽������R�ͣ�");
							break;
						case 34:	//���͸��»���̽����
							faultData.setUnitType("���͸��»���̽����");
							break;
						case 35:	//���͸��»���̽����(S��)
							faultData.setUnitType("���͸��»���̽����(S��)");
							break;
						case 36:	//���͸��»���̽����(R��)
							faultData.setUnitType("���͸��»���̽����(R��)");
							break;
						case 37:	//���˸��»���̽����
							faultData.setUnitType("���˸��»���̽����");
							break;
						case 40:	//���̻���̽����
							faultData.setUnitType("���̻���̽����");
							break;
						case 41:	//�������Ӹ��̻���̽����
							faultData.setUnitType("�������Ӹ��̻���̽����");
							break; 
						case 42:	//���͹����̻���̽����
							faultData.setUnitType("���͹����̻���̽����");
							break; 
						case 43:	//���͹������̻���̽����
							faultData.setUnitType("���͹������̻���̽����");
							break; 
						case 44:	//����ʽ���̻���̽����
							faultData.setUnitType("����ʽ���̻���̽����");
							break; 
						case 50:	//����ʽ����̽����
							faultData.setUnitType("����ʽ����̽����");
							break; 
						case 51:	//����ʽ���̸��»���̽����
							faultData.setUnitType("����ʽ���̸��»���̽����");
							break; 
						case 52:	//����ʽ�й���»���̽����
							faultData.setUnitType("����ʽ�й���»���̽����");
							break; 
						case 53:	//����ʽ�й���̻���̽����
							faultData.setUnitType("����ʽ�й���̻���̽����");
							break; 
						case 61:	//�������̽����
							faultData.setUnitType("�������̽����");
							break; 
						case 62:	//�������̽����
							faultData.setUnitType("�������̽����");
							break; 
						case 69:	//�й����̽����
							faultData.setUnitType("�й����̽����");
							break; 
						case 74:	//����̽����
							faultData.setUnitType("����̽����");
							break; 
						case 78:	//ͼ������ʽ����̽����
							faultData.setUnitType("ͼ������ʽ����̽����");
							break;
						case 79:	//�������ֲ���
							faultData.setUnitType("�������ֲ���");
							break; 
						case 81:	//������������
							faultData.setUnitType("������������");
							break; 
						case 82:	//������������װ��
							faultData.setUnitType("������������װ��");
							break; 
						case 83:	//����������ͼ����ʾװ��
							faultData.setUnitType("����������ͼ����ʾװ��");
							break; 
						case 84:	//ģ��
							faultData.setUnitType("ģ��");
							break;
						case 85:	//����ģ��
							faultData.setUnitType("����ģ��");
							break;
						case 86:	//���ģ��
							faultData.setUnitType("���ģ��");
							break;
						case 87:	//�������ģ��
							faultData.setUnitType("�������ģ��");
							break;
						case 88:	//�м�ģ��
							faultData.setUnitType("�м�ģ��");
							break;
						case 91:	//����ˮ��
							faultData.setUnitType("����ˮ��");
							break;
						case 92:	//����ˮ��
							faultData.setUnitType("����ˮ��");
							break;
						case 95:	//���ܱ�
							faultData.setUnitType("���ܱ�");
							break;
						case 96:	//ˮ��ָʾ��
							faultData.setUnitType("ˮ��ָʾ��");
							break;
						case 97:	//�źŷ�
							faultData.setUnitType("�źŷ�");
							break;
						case 98:	//������
							faultData.setUnitType("������");
							break;
						case 99:	//ѹ������
							faultData.setUnitType("ѹ������");
							break;
						case 101:	//������װ��
							faultData.setUnitType("������װ��");
							break;
						case 102:	//������
							faultData.setUnitType("������");
							break;
						case 103:	//����
							faultData.setUnitType("����");
							break;
						case 104:	//ͨ��յ�
							faultData.setUnitType("ͨ��յ�");
							break;
						case 105:	//��ĭҺ��
							faultData.setUnitType("ĭҺ��");
							break;
						case 106:	//������ŷ�
							faultData.setUnitType("������ŷ�");
							break;
						case 111:	//�������̷��
							faultData.setUnitType("�������̷��");
							break;
						case 113:	//���̷���
							faultData.setUnitType("���̷���");
							break;
						case 114:	//�����ͷ��
							faultData.setUnitType("�����ͷ��");
							break;
						case 115:	//���̿�
							faultData.setUnitType("���̿�");
							break;
						case 116:	//��ص��̴���
							faultData.setUnitType("��ص��̴���");
							break;
						case 117:	//�������������
							faultData.setUnitType("�������������");
							break;
						case 118:	//�����ż����
							faultData.setUnitType("�����ż����");
							break;
						case 121:	//����װ��
							faultData.setUnitType("����װ��");
							break;
						case 128:	//��ӡ�����ϡ�������ӡ������
							faultData.setUnitType("��ӡ������");
							break;
						case 251:	//������ϡ������������
							faultData.setUnitType("�������");
							break;
						case 249:	//������ϡ������������
							faultData.setUnitType("�������");
							break;
					}
					switch(systemTab){
					case 1:	//���ֱ���ϵͳ
						faultData.setSystemTab("���ֱ���ϵͳ");
						break;
					case 10:	//��������������
						faultData.setSystemTab("��������������");
						break;
					case 11:	//����˨ϵͳ
						faultData.setSystemTab("����˨ϵͳ");
						break;
					case 12:	//�Զ���ˮ���ϵͳ
						faultData.setSystemTab("�Զ���ˮ���ϵͳ");
						break;
					case 13:	//�������ϵͳ
						faultData.setSystemTab("�������ϵͳ");
						break;
					case 14:	//ˮ�������ϵͳ����������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ����������ʽ��");
						break;
					case 15:	//ˮ�������ϵͳ��ѹ������������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ��ѹ������������ʽ��");
						break;
					case 16:	//��ĭ���ϵͳ
						faultData.setSystemTab("��ĭ���ϵͳ");
						break;
					case 17:	//�ɷ����ϵͳ
						faultData.setSystemTab("�ɷ����ϵͳ");
						break;
					case 18:	//��������ϵͳ
						faultData.setSystemTab("��������ϵͳ");
						break;
					case 19:	//�����ż�����ϵͳ
						faultData.setSystemTab("�����ż�����ϵͳ");
						break;
					case 20:	//��������
						faultData.setSystemTab("��������");
						break;
					case 21:	//����Ӧ���㲥
						faultData.setSystemTab("����Ӧ���㲥");
						break;
					case 22:	//����Ӧ����������ɢָʾϵͳ
						faultData.setSystemTab("����Ӧ����������ɢָʾϵͳ");
						break;
					case 23:	//������Դ
						faultData.setSystemTab("������Դ");
						break;
					case 24:	//�����绰
						faultData.setSystemTab("�����绰");
						break;
					}
					faultData.setTabbingCmd("�ϴ�����������ʩ��������״̬");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setUnitAddress(unitAddress);
					faultData.setUnitMemos(unitNumber);
					faultData.setInfoNum(infoNum);
					faultData.setStateTab(stateTab);
				}
				break;
			case 3:	//�ϴ�����������ʩ����ģ����ֵ
				break;
			case 4:	//�ϴ�����������ʩ������Ϣ==================
				for(int i = 0;i<infoNum;i++){
					int systemTab = dataN[29+i*10]&0xff;		//ϵͳ��־
					int systemAddress = dataN[30+i*10]&0xff;	//ϵͳ��ַ
					int operaTab = dataN[31+i*10]&0xff;	//������־
					int operator = dataN[32+i*10]&0xff;	//����Ա���
					faultData.setTabbingCmd("�ϴ�����������ʩ������Ϣ");
					faultData.setSystemAddress(systemAddress+"");
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//��λ
						stateTab.put(1, "��λ");
						stateTabList.add("��λ");
					}else if((operaTab&0x01)==0){	//�޲���
						stateTab.put(1, "�޲���");
					}
					if((operaTab&0x02)==2){	//����
						stateTab.put(2, "����");
						stateTabList.add("����");
					}else if((operaTab&0x02)==0){	//�޲���
						stateTab.put(2, "�޲���");
					}
					if((operaTab&0x04)==4){	//�ֶ�����
						stateTab.put(3, "�ֶ�����");
						stateTabList.add("�ֶ�����");
					}else if((operaTab&0x04)==0){	//�޲���
						stateTab.put(3, "�޲���");
					}
					if((operaTab&0x08)==8){	//��������
						stateTab.put(4, "��������");
						stateTabList.add("��������");
					}else if((operaTab&0x08)==0){	//�޲���
						stateTab.put(4, "�޲���");
					}
					if((operaTab&0x10)==16){	//�Լ�
						stateTab.put(5, "�Լ�");
						stateTabList.add("�Լ�");
					}else if((operaTab&0x10)==0){	//�޲���
						stateTab.put(5, "�޲���");
					}
					if((operaTab&0x20)==32){	//ȷ��
						stateTab.put(6, "ȷ��");
						stateTabList.add("ȷ��");
					}else if((operaTab&0x20)==0){	//�޲���
						stateTab.put(6, "�޲���");
					}
					if((operaTab&0x40)==64){	//����
						stateTab.put(7, "����");
						stateTabList.add("����");
					}else if((operaTab&0x40)==0){	//�޲���
						stateTab.put(7, "�޲���");
					}
					faultData.setStateTab(stateTab);
					switch(systemTab){
					case 1:	//���ֱ���ϵͳ
						faultData.setSystemTab("���ֱ���ϵͳ");
						break;
					case 10:	//��������������
						faultData.setSystemTab("��������������");
						break;
					case 11:	//����˨ϵͳ
						faultData.setSystemTab("����˨ϵͳ");
						break;
					case 12:	//�Զ���ˮ���ϵͳ
						faultData.setSystemTab("�Զ���ˮ���ϵͳ");
						break;
					case 13:	//�������ϵͳ
						faultData.setSystemTab("�������ϵͳ");
						break;
					case 14:	//ˮ�������ϵͳ����������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ����������ʽ��");
						break;
					case 15:	//ˮ�������ϵͳ��ѹ������������ʽ��
						faultData.setSystemTab("ˮ�������ϵͳ��ѹ������������ʽ��");
						break;
					case 16:	//��ĭ���ϵͳ
						faultData.setSystemTab("��ĭ���ϵͳ");
						break;
					case 17:	//�ɷ����ϵͳ
						faultData.setSystemTab("�ɷ����ϵͳ");
						break;
					case 18:	//��������ϵͳ
						faultData.setSystemTab("��������ϵͳ");
						break;
					case 19:	//�����ż�����ϵͳ
						faultData.setSystemTab("�����ż�����ϵͳ");
						break;
					case 20:	//��������
						faultData.setSystemTab("��������");
						break;
					case 21:	//����Ӧ���㲥
						faultData.setSystemTab("����Ӧ���㲥");
						break;
					case 22:	//����Ӧ����������ɢָʾϵͳ
						faultData.setSystemTab("����Ӧ����������ɢָʾϵͳ");
						break;
					case 23:	//������Դ
						faultData.setSystemTab("������Դ");
						break;
					case 24:	//�����绰
						faultData.setSystemTab("�����绰");
						break;
					}
				}
				break;
			case 5:	//�ϴ�����������ʩ����汾
				break;
			case 6:	//�ϴ�����������ʩϵͳ�������
				break;
			case 7:	//�ϴ�����������ʩϵͳ�����������
				break;
			case 8:	//�ϴ�����������ʩϵͳʱ��
				break;
			case 22:	//Ԥ������������
				faultData.setSystemTab("��������");
				byte ackByte[] = getAckByte(dataN,22);
				faultData.setAckByte(ackByte);
				break;
			case 21:	//�ϴ��û���Ϣ����װ������״̬******===============
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("�ϴ��û���Ϣ����װ������״̬");
					int runTab = dataN[29+i*7]&0xff;		//���б�־
					if((runTab&0x01)==1){	//��������״̬
//						stateTab.put(1, "��������״̬");
//						stateTabList.add("��������״̬");
					}else if((runTab&0x01)==0){	//����״̬
						stateTab.put(1, "����״̬");
					}
					if((runTab&0x02)==2){	//��
						stateTab.put(2, "��");
						stateTabList.add("��");
					}else if((runTab&0x02)==0){	//�޻�
						stateTab.put(2, "�޻�");
					}
					if((runTab&0x04)==4){	//����
						stateTab.put(3, "����");
						stateTabList.add("����");
					}else if((runTab&0x04)==0){	//�޹���
						stateTab.put(3, "�޹���");
					}
					if((runTab&0x08)==8){	//�������
						stateTab.put(4, "�������");
						stateTabList.add("�������");
					}else if((runTab&0x08)==0){	//��������
						stateTab.put(4, "��������");
					}
					if((runTab&0x10)==16){	//�������
						stateTab.put(5, "�������");
						stateTabList.add("�������");
					}else if((runTab&0x10)==0){	//��������
						stateTab.put(5, "��������");
					}
					if((runTab&0x20)==32){	//��������ͨ���ŵ�����
						stateTab.put(6, "��������ͨ���ŵ�����");
						stateTabList.add("��������ͨ���ŵ�����");
					}else if((runTab&0x20)==0){	//ͨ���ŵ�����
						stateTab.put(6, "ͨ���ŵ�����");
					}
					if((runTab&0x40)==64){	//���������·����
						stateTab.put(7, "���������·����");
						stateTabList.add("���������·����");
					}else if((runTab&0x40)==0){	//���������·����
						stateTab.put(7, "���������·����");
					}
					byte[] ackByte22 = {(byte)0x40,(byte)0x40,(byte)0x01,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x23,(byte)0x3A,(byte)0x09,(byte)0x1A,(byte)0x09,(byte)0x0C,(byte)0x38,(byte)0x5B,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x79,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xAB,(byte)0x23,(byte)0x23};
					faultData.setAckByte(ackByte22);
					faultData.setStateTab(stateTab);
				}
				break;
			case 24:	//�ϴ��û���Ϣ����װ�ò�����Ϣ��¼===================
				for(int i = 0;i<infoNum;i++){
					faultData.setSystemTab("�ϴ��û���Ϣ����װ�ò�����Ϣ��¼");
					int operaTab = dataN[29+i*8]&0xff;	//������־
					int operator = dataN[30+i*8]&0xff;	//����Ա���
					faultData.setOperator(operator+"");
					if((operaTab&0x01)==1){	//��λ
						stateTab.put(1, "��λ");
						stateTabList.add("��λ");
					}else if((operaTab&0x01)==0){	//�޲���
						stateTab.put(1, "�޲���");
					}
					if((operaTab&0x02)==2){	//����
						stateTab.put(2, "����");
						stateTabList.add("����");
					}else if((operaTab&0x02)==0){	//�޲���
						stateTab.put(2, "�޲���");
					}
					if((operaTab&0x04)==4){	//�ֶ�����
						stateTab.put(3, "�ֶ�����");
						stateTabList.add("�ֶ�����");
						byte[] lzack4 ={(byte)0x40,(byte)0x40,(byte)0x29,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0A,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x01,(byte)0x20,(byte)0x01,(byte)0x18,(byte)0x03,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x22,(byte)0x23,(byte)0x23}; 
						faultData.setAckByte(lzack4);
					}else if((operaTab&0x04)==0){	//�޲���
						stateTab.put(3, "�޲���");
					}
					if((operaTab&0x08)==8){	//��������
						stateTab.put(4, "��������");
						stateTabList.add("��������");
					}else if((operaTab&0x08)==0){	//�޲���
						stateTab.put(4, "�޲���");
					}
					if((operaTab&0x10)==16){	//�Լ�
						stateTab.put(5, "�Լ�");
						stateTabList.add("�Լ�");
					}else if((operaTab&0x10)==0){	//�޲���
						stateTab.put(5, "�޲���");
					}
					if((operaTab&0x20)==32){	//���Ӧ��
						stateTab.put(6, "���Ӧ��");
						stateTabList.add("���Ӧ��");
					}else if((operaTab&0x20)==0){	//�޲���
						stateTab.put(6, "�޲���");
					}
					if((operaTab&0x40)==64){	//���Զ�Ӧ���ڵ�����
						stateTab.put(7, "����");
						stateTabList.add("����");
						byte[] LZackByte = {(byte)0x40,(byte)0x40,(byte)0x04,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x1A,(byte)0x02,(byte)0x0E,(byte)0x12,(byte)0x0C,(byte)0x11,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x03,(byte)0x00,(byte)0x02,(byte)0x5B,(byte)0x01,(byte)0x01,(byte)0xC2,(byte)0x23,(byte)0x23};
						faultData.setAckByte(LZackByte);
						return null;
					}else if((operaTab&0x40)==0){	//�޲���
						stateTab.put(7, "�޲���");
					}
					faultData.setStateTab(stateTab);
					byte[] ackByte24 = {(byte)0x40,(byte)0x40,(byte)0x01,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x23,(byte)0x3A,(byte)0x09,(byte)0x1A,(byte)0x09,(byte)0x0C,(byte)0x38,(byte)0x5B,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x79,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0xAB,(byte)0x23,(byte)0x23};
					faultData.setAckByte(ackByte24);
				}
				break;
			case 25:	//�ϴ��û���Ϣ����װ������汾
				break;
			case 26:	//�ϴ��û���Ϣ����װ���������
				break;
			case 28:	//�ϴ��û���Ϣ����װ��ϵͳʱ��
				break;
			case 30:	//����װ�� GSM ����
				stateTabList.add("GSM ����");
				faultData.setTabbingCmd("����װ�� GSM ����");
				faultData.setSystemAddress("0");
				faultData.setUnitAddress("0");
				byte ackByte30[] = getAckByte(dataN,30);
				faultData.setAckByte(ackByte30);
				break;
			case 61:	//������������ʩϵͳ״̬
				break;
			case 62:	//������������ʩ��������״̬
				break;
			case 63:	//������������ʩ����ģ����ֵ
				break;
			case 64:	//������������ʩ������Ϣ
				break;
			case 65:	//������������ʩ����汾
				break;
			case 66:	//������������ʩϵͳ�������
				break;
			case 67:	//������������ʩ�����������
				break;
			case 68:	//������������ʩϵͳʱ��
				break;
			case 81:	//���û���Ϣ����װ������״̬
				break;
			case 84:	//���û���Ϣ����װ�ò�����Ϣ��¼
				break;
			case 85:	//���û���Ϣ����װ������汾
				break;
			case 86:	//���û���Ϣ����װ���������
				break;
			case 88:	//���û���Ϣ����װ��ϵͳʱ��
				break;
			case 89:	//��ʼ���û���Ϣ����װ��
				break;
			case 90:	//ͬ���û���Ϣ����װ��ʱ��
				break;
			case 91:	//�������
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
		int byteLen = (byteData[13]&0xff)+10;	//���ݳ���
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
