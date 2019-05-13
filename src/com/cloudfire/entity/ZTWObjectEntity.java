package com.cloudfire.entity;

public class ZTWObjectEntity {
	private String devType_T = ""; //设备类型
	private String devType_L = "";
	private String devType_V = "";
	
	private String devImei_T = "";//设备IMEI
	private String devImei_L = "";
	private String devImei_V = "";
	
	private String devImsi_T = "";//设备IMEI
	private String devImsi_L = "";
	private String devImsi_V = "";
	
	private String signal_T = "";	//信号强度
	private String signal_L = "";
	private String signal_V = "";
	
	private String battery_T = "";	//电池量
	private String battery_L = "";
	private String battery_V = "";
	
	private String devState_T = "";	//设备状态(报警....心跳)
	private String devState_L = "";
	private String devState_V = "";
	
	private String sampling_T = "";	//采样频率
	private String sampling_L = "";
	private String sampling_V = "";
	
	private String report_T = "";	//上报频率
	private String report_L = "";
	private String report_V = "";
	
	private String Iccid_T = "";	//Iccid--卡号
	private String Iccid_L = "";
	private String Iccid_V = "";
	
	private String MUCVersion_T = "";	//muc软件版本
	private String MUCVersion_L = "";
	private String MUCVersion_V = "";
	
	private String NBfirmware_T = "";	//NB固件版本
	private String NBfirmware_L = "";
	private String NBfirmware_V = "";
	
	private String estate_T = "";	//小区ID
	private String estate_L = "";
	private String estate_V = "";
	
	private String Physics_T = "";	//物理小区
	private String Physics_L = "";
	private String Physics_V = "";
	
	private String receiptSignal_T = "";	//接收信号
	private String receiptSignal_L = "";
	private String receiptSignal_V = "";
	
	private String signal_noise_T = "";	//信噪比
	private String signal_noise_L = "";
	private String signal_noise_V = "";
	
	private String coverageLeve_T = "";	//覆盖等级
	private String coverageLeve_L = "";
	private String coverageLeve_V = "";
	
	private String EARFCN_T = "";	//EARFCN 频点
	private String EARFCN_L = "";
	private String EARFCN_V = "";
	
	private String test_frequency_T = "";//检测频率
	private String test_frequency_L = "";
	private String test_frequency_V = "";
	
	private String powerThreshold_T = "";//电量阈值
	private String powerThreshold_L = "";
	private String powerThreshold_V = "";
	
	private String manufacturer_T = "";//厂商
	private String manufacturer_L = "";
	private String manufacturer_V = "";
	
	private String devValue_T = "";	//	设备对应值
	private String devValue_L = "";
	private String devValue_V = "";
	
	private String devThreashold_T = "";//设备对应的阈值
	private String devThreashold_L = "";
	private String devThreashold_V = "";
	
	/*private String gasConcentra_T = "";	//燃气浓度
	private String gasConcentra_L = "";
	private String gasConcentra_V = "";
	
	private String gasThreshold_T = "";//燃气阈值
	private String gasThreshold_L = "";
	private String gasThreshold_V = "";
	
	private String waterValue_T = "";//水压值
	private String waterValue_L = "";
	private String waterValue_V = "";
	
	private String waterThreashold_T = "";//水压阈值
	private String waterThreashold_L = "";
	private String waterThreashold_V = "";*/
	
	public String getDevType_T() {
		return devType_T;
	}
	public String getDevValue_T() {
		return devValue_T;
	}
	public void setDevValue_T(String devValue_T) {
		this.devValue_T = devValue_T;
	}
	public String getDevValue_L() {
		return devValue_L;
	}
	public void setDevValue_L(String devValue_L) {
		this.devValue_L = devValue_L;
	}
	public String getDevValue_V() {
		return devValue_V;
	}
	public void setDevValue_V(String devValue_V) {
		this.devValue_V = devValue_V;
	}
	public String getDevThreashold_T() {
		return devThreashold_T;
	}
	public void setDevThreashold_T(String devThreashold_T) {
		this.devThreashold_T = devThreashold_T;
	}
	public String getDevThreashold_L() {
		return devThreashold_L;
	}
	public void setDevThreashold_L(String devThreashold_L) {
		this.devThreashold_L = devThreashold_L;
	}
	public String getDevThreashold_V() {
		return devThreashold_V;
	}
	public void setDevThreashold_V(String devThreashold_V) {
		this.devThreashold_V = devThreashold_V;
	}
	public void setDevType_T(String devType_T) {
		this.devType_T = devType_T;
	}
	public String getDevType_L() {
		return devType_L;
	}
	public void setDevType_L(String devType_L) {
		this.devType_L = devType_L;
	}
	public String getDevType_V() {
		return devType_V;
	}
	public void setDevType_V(String devType_V) {
		this.devType_V = devType_V;
	}
	public String getDevImei_T() {
		return devImei_T;
	}
	public void setDevImei_T(String devImei_T) {
		this.devImei_T = devImei_T;
	}
	public String getDevImei_L() {
		return devImei_L;
	}
	public void setDevImei_L(String devImei_L) {
		this.devImei_L = devImei_L;
	}
	public String getDevImei_V() {
		return devImei_V;
	}
	public void setDevImei_V(String devImei_V) {
		this.devImei_V = devImei_V;
	}
	public String getDevImsi_T() {
		return devImsi_T;
	}
	public void setDevImsi_T(String devImsi_T) {
		this.devImsi_T = devImsi_T;
	}
	public String getDevImsi_L() {
		return devImsi_L;
	}
	public void setDevImsi_L(String devImsi_L) {
		this.devImsi_L = devImsi_L;
	}
	public String getDevImsi_V() {
		return devImsi_V;
	}
	public void setDevImsi_V(String devImsi_V) {
		this.devImsi_V = devImsi_V;
	}
	public String getSignal_T() {
		return signal_T;
	}
	public void setSignal_T(String signal_T) {
		this.signal_T = signal_T;
	}
	public String getSignal_L() {
		return signal_L;
	}
	public void setSignal_L(String signal_L) {
		this.signal_L = signal_L;
	}
	public String getSignal_V() {
		return signal_V;
	}
	public void setSignal_V(String signal_V) {
		this.signal_V = signal_V;
	}
	public String getBattery_T() {
		return battery_T;
	}
	public void setBattery_T(String battery_T) {
		this.battery_T = battery_T;
	}
	public String getBattery_L() {
		return battery_L;
	}
	public void setBattery_L(String battery_L) {
		this.battery_L = battery_L;
	}
	public String getBattery_V() {
		return battery_V;
	}
	public void setBattery_V(String battery_V) {
		this.battery_V = battery_V;
	}
	public String getDevState_T() {
		return devState_T;
	}
	public void setDevState_T(String devState_T) {
		this.devState_T = devState_T;
	}
	public String getDevState_L() {
		return devState_L;
	}
	public void setDevState_L(String devState_L) {
		this.devState_L = devState_L;
	}
	public String getDevState_V() {
		return devState_V;
	}
	public void setDevState_V(String devState_V) {
		this.devState_V = devState_V;
	}
	public String getSampling_T() {
		return sampling_T;
	}
	public void setSampling_T(String sampling_T) {
		this.sampling_T = sampling_T;
	}
	public String getSampling_L() {
		return sampling_L;
	}
	public void setSampling_L(String sampling_L) {
		this.sampling_L = sampling_L;
	}
	public String getSampling_V() {
		return sampling_V;
	}
	public void setSampling_V(String sampling_V) {
		this.sampling_V = sampling_V;
	}
	public String getReport_T() {
		return report_T;
	}
	public void setReport_T(String report_T) {
		this.report_T = report_T;
	}
	public String getReport_L() {
		return report_L;
	}
	public void setReport_L(String report_L) {
		this.report_L = report_L;
	}
	public String getReport_V() {
		return report_V;
	}
	public void setReport_V(String report_V) {
		this.report_V = report_V;
	}
	public String getIccid_T() {
		return Iccid_T;
	}
	public void setIccid_T(String iccid_T) {
		Iccid_T = iccid_T;
	}
	public String getIccid_L() {
		return Iccid_L;
	}
	public void setIccid_L(String iccid_L) {
		Iccid_L = iccid_L;
	}
	public String getIccid_V() {
		return Iccid_V;
	}
	public void setIccid_V(String iccid_V) {
		Iccid_V = iccid_V;
	}
	public String getMUCVersion_T() {
		return MUCVersion_T;
	}
	public void setMUCVersion_T(String mUCVersion_T) {
		MUCVersion_T = mUCVersion_T;
	}
	public String getMUCVersion_L() {
		return MUCVersion_L;
	}
	public void setMUCVersion_L(String mUCVersion_L) {
		MUCVersion_L = mUCVersion_L;
	}
	public String getMUCVersion_V() {
		return MUCVersion_V;
	}
	public void setMUCVersion_V(String mUCVersion_V) {
		MUCVersion_V = mUCVersion_V;
	}
	public String getNBfirmware_T() {
		return NBfirmware_T;
	}
	public void setNBfirmware_T(String nBfirmware_T) {
		NBfirmware_T = nBfirmware_T;
	}
	public String getNBfirmware_L() {
		return NBfirmware_L;
	}
	public void setNBfirmware_L(String nBfirmware_L) {
		NBfirmware_L = nBfirmware_L;
	}
	public String getNBfirmware_V() {
		return NBfirmware_V;
	}
	public void setNBfirmware_V(String nBfirmware_V) {
		NBfirmware_V = nBfirmware_V;
	}
	public String getEstate_T() {
		return estate_T;
	}
	public void setEstate_T(String estate_T) {
		this.estate_T = estate_T;
	}
	public String getEstate_L() {
		return estate_L;
	}
	public void setEstate_L(String estate_L) {
		this.estate_L = estate_L;
	}
	public String getEstate_V() {
		return estate_V;
	}
	public void setEstate_V(String estate_V) {
		this.estate_V = estate_V;
	}
	public String getPhysics_T() {
		return Physics_T;
	}
	public void setPhysics_T(String physics_T) {
		Physics_T = physics_T;
	}
	public String getPhysics_L() {
		return Physics_L;
	}
	public void setPhysics_L(String physics_L) {
		Physics_L = physics_L;
	}
	public String getPhysics_V() {
		return Physics_V;
	}
	public void setPhysics_V(String physics_V) {
		Physics_V = physics_V;
	}
	public String getReceiptSignal_T() {
		return receiptSignal_T;
	}
	public void setReceiptSignal_T(String receiptSignal_T) {
		this.receiptSignal_T = receiptSignal_T;
	}
	public String getReceiptSignal_L() {
		return receiptSignal_L;
	}
	public void setReceiptSignal_L(String receiptSignal_L) {
		this.receiptSignal_L = receiptSignal_L;
	}
	public String getReceiptSignal_V() {
		return receiptSignal_V;
	}
	public void setReceiptSignal_V(String receiptSignal_V) {
		this.receiptSignal_V = receiptSignal_V;
	}
	public String getSignal_noise_T() {
		return signal_noise_T;
	}
	public void setSignal_noise_T(String signal_noise_T) {
		this.signal_noise_T = signal_noise_T;
	}
	public String getSignal_noise_L() {
		return signal_noise_L;
	}
	public void setSignal_noise_L(String signal_noise_L) {
		this.signal_noise_L = signal_noise_L;
	}
	public String getSignal_noise_V() {
		return signal_noise_V;
	}
	public void setSignal_noise_V(String signal_noise_V) {
		this.signal_noise_V = signal_noise_V;
	}
	public String getCoverageLeve_T() {
		return coverageLeve_T;
	}
	public void setCoverageLeve_T(String coverageLeve_T) {
		this.coverageLeve_T = coverageLeve_T;
	}
	public String getCoverageLeve_L() {
		return coverageLeve_L;
	}
	public void setCoverageLeve_L(String coverageLeve_L) {
		this.coverageLeve_L = coverageLeve_L;
	}
	public String getCoverageLeve_V() {
		return coverageLeve_V;
	}
	public void setCoverageLeve_V(String coverageLeve_V) {
		this.coverageLeve_V = coverageLeve_V;
	}
	public String getEARFCN_T() {
		return EARFCN_T;
	}
	public void setEARFCN_T(String eARFCN_T) {
		EARFCN_T = eARFCN_T;
	}
	public String getEARFCN_L() {
		return EARFCN_L;
	}
	public void setEARFCN_L(String eARFCN_L) {
		EARFCN_L = eARFCN_L;
	}
	public String getEARFCN_V() {
		return EARFCN_V;
	}
	public void setEARFCN_V(String eARFCN_V) {
		EARFCN_V = eARFCN_V;
	}
	public String getTest_frequency_T() {
		return test_frequency_T;
	}
	public void setTest_frequency_T(String test_frequency_T) {
		this.test_frequency_T = test_frequency_T;
	}
	public String getTest_frequency_L() {
		return test_frequency_L;
	}
	public void setTest_frequency_L(String test_frequency_L) {
		this.test_frequency_L = test_frequency_L;
	}
	public String getTest_frequency_V() {
		return test_frequency_V;
	}
	public void setTest_frequency_V(String test_frequency_V) {
		this.test_frequency_V = test_frequency_V;
	}
	public String getPowerThreshold_T() {
		return powerThreshold_T;
	}
	public void setPowerThreshold_T(String powerThreshold_T) {
		this.powerThreshold_T = powerThreshold_T;
	}
	public String getPowerThreshold_L() {
		return powerThreshold_L;
	}
	public void setPowerThreshold_L(String powerThreshold_L) {
		this.powerThreshold_L = powerThreshold_L;
	}
	public String getPowerThreshold_V() {
		return powerThreshold_V;
	}
	public void setPowerThreshold_V(String powerThreshold_V) {
		this.powerThreshold_V = powerThreshold_V;
	}
	public String getManufacturer_T() {
		return manufacturer_T;
	}
	public void setManufacturer_T(String manufacturer_T) {
		this.manufacturer_T = manufacturer_T;
	}
	public String getManufacturer_L() {
		return manufacturer_L;
	}
	public void setManufacturer_L(String manufacturer_L) {
		this.manufacturer_L = manufacturer_L;
	}
	public String getManufacturer_V() {
		return manufacturer_V;
	}
	public void setManufacturer_V(String manufacturer_V) {
		this.manufacturer_V = manufacturer_V;
	}
}
