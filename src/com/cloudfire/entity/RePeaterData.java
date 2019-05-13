package com.cloudfire.entity;

import java.util.Map;

import com.cloudfire.entity.meter.MeterReadingEntity;

public class RePeaterData {
	private String repeatMac="";
	private Map<String,Long> fireMacList;
	private String electricMac="";
	private String alarmSmokeMac="";
	private ZTWObjectEntity ztwObj;
	
	/** begin*/
	private String waterMac="";
	private String waterRepeaterMac="";
	private Water water;
	/** end*/
	
	private THDevice td;
	private ThreePhaseElectricEntity threeElectric;
	
	private int transmissionType;//1表示电气设备，2表示三江设备,3表示水压设备
	private int printerAction;//1表示打印机，2表示复位
	private int deviceType;//1表示bq200,2表示bq100 1.0版本，3表示bq100 2.0版本
	private byte seqL;
	private byte seqH;
	private byte cmd2;
	private byte cmd;
	private byte cmd1;
	private BQ200Entity mBQ200Entity;
	private BQ100Entity mBQ100Entity;
	private PrinterEntity mPrinterEntity;
	private ProofGasEntity gasEntity;
	private int repeaterState = 0;//1表示主电源、2表示备电源，3表示主备电源
	
	private EnvironmentEntity environment;
	private MeterReadingEntity electricMeter; //  电表 by liao  zw
	
	private ChJEntity chj; //诚佳电气 by yfs
	private ThreePhaseMeterEntity threePhaseMeterEntity;//@@三相电表
	private RssiEntityQuery rssientity;	//rssi值 lzo
	private ElectricEnergyEntity eee;//优特电率
	

	public ZTWObjectEntity getZtwObj() {
		return ztwObj;
	}

	public void setZtwObj(ZTWObjectEntity ztwObj) {
		this.ztwObj = ztwObj;
	}

	public ElectricEnergyEntity getEee() {
		return eee;
	}

	public void setEee(ElectricEnergyEntity eee) {
		this.eee = eee;
	}

	public ThreePhaseElectricEntity getThreeElectric() {
		return threeElectric;
	}

	public void setThreeElectric(ThreePhaseElectricEntity threeElectric) {
		this.threeElectric = threeElectric;
	}

	public ProofGasEntity getGasEntity() {
		return gasEntity;
	}

	public void setGasEntity(ProofGasEntity gasEntity) {
		this.gasEntity = gasEntity;
	}

	public RssiEntityQuery getRssientity() {
		return rssientity;
	}

	public void setRssientity(RssiEntityQuery rssientity) {
		this.rssientity = rssientity;
	}

	public ChJEntity getChj() {
		return chj;
	}

	public void setChj(ChJEntity chj) {
		this.chj = chj;
	}

	public void setEnvironment(EnvironmentEntity environment) {
		this.environment = environment;
	}
	
	public EnvironmentEntity getEnvironment() {
		return environment;
	}
	
	public int getRepeaterState() {
		return repeaterState;
	}

	public void setRepeaterState(int repeaterState) {
		this.repeaterState = repeaterState;
	}

	public Water getWater() {
		return water;
	}
	public void setWater(Water water) {
		this.water = water;
	}
	public String getWaterRepeaterMac() {
		return waterRepeaterMac;
	}
	public void setWaterRepeaterMac(String waterRepeaterMac) {
		this.waterRepeaterMac = waterRepeaterMac;
	}
	public String getWaterMac() {
		return waterMac;
	}
	public void setWaterMac(String waterMac) {
		this.waterMac = waterMac;
	}
	
	public BQ100Entity getmBQ100Entity() {
		return mBQ100Entity;
	}
	public void setmBQ100Entity(BQ100Entity mBQ100Entity) {
		this.mBQ100Entity = mBQ100Entity;
	}
	public String getRepeatMac() {
		return repeatMac;
	}
	public void setRepeatMac(String repeatMac) {
		this.repeatMac = repeatMac;
	}
	public Map<String,Long> getFireMacList() {
		return fireMacList;
	}
	public void setFireMacList(Map<String,Long> fireMacList) {
		this.fireMacList = fireMacList;
	}
	public byte getSeqL() {
		return seqL;
	}
	public void setSeqL(byte seqL) {
		this.seqL = seqL;
	}
	public byte getSeqH() {
		return seqH;
	}
	public byte getCmd2() {
		return cmd2;
	}
	public void setCmd2(byte cmd2) {
		this.cmd2 = cmd2;
	}
	public void setSeqH(byte seqH) {
		this.seqH = seqH;
	}
	public String getAlarmSmokeMac() {
		return alarmSmokeMac;
	}
	public void setAlarmSmokeMac(String alarmSmokeMac) {
		this.alarmSmokeMac = alarmSmokeMac;
	}
	public byte getCmd() {
		return cmd;
	}
	public void setCmd(byte cmd) {
		this.cmd = cmd;
	}
	public byte getCmd1() {
		return cmd1;
	}
	public void setCmd1(byte cmd1) {
		this.cmd1 = cmd1;
	}
	public String getElectricMac() {
		return electricMac;
	}
	public void setElectricMac(String electricMac) {
		this.electricMac = electricMac;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public BQ200Entity getmBQ200Entity() {
		return mBQ200Entity;
	}
	public void setmBQ200Entity(BQ200Entity mBQ200Entity) {
		this.mBQ200Entity = mBQ200Entity;
	}
	public PrinterEntity getmPrinterEntity() {
		return mPrinterEntity;
	}
	public void setmPrinterEntity(PrinterEntity mPrinterEntity) {
		this.mPrinterEntity = mPrinterEntity;
	}
	public int getTransmissionType() {
		return transmissionType;
	}
	public void setTransmissionType(int transmissionType) {
		this.transmissionType = transmissionType;
	}
	public int getPrinterAction() {
		return printerAction;
	}
	public void setPrinterAction(int printerAction) {
		this.printerAction = printerAction;
	}

	public MeterReadingEntity getElectricMeter() {
		return electricMeter;
	}

	public void setElectricMeter(MeterReadingEntity electricMeter) {
		this.electricMeter = electricMeter;
	}

	public ThreePhaseMeterEntity getThreePhaseMeterEntity() {
		return threePhaseMeterEntity;
	}

	public void setThreePhaseMeterEntity(ThreePhaseMeterEntity threePhaseMeterEntity) {
		this.threePhaseMeterEntity = threePhaseMeterEntity;
	}

	public THDevice getTd() {
		return td;
	}

	public void setTd(THDevice td) {
		this.td = td;
	}
	
}
