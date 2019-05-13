package com.cloudfire.dao;

import com.cloudfire.entity.ProofGasEntity;

public interface MeterInfoDao {
	public boolean updateMeterInfo(String mac,String quantity,String time,String voltage,String electricity,String power);
	
	public void insertProofGas(ProofGasEntity gas);
}
