package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.GpsEntityBean;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.entity.query.GpsQuery;

public interface GpsInfoDao {
	public void saveGpsInfo(GpsEntityBean gpsBean);
	public void sageGpsEqument(GpsEntityBean gpsBean);
	public List<GpsEntityBean> getGpsEqument();
	public void ifLossGpsEqument();
	public List<ProofGasEntity> getGasRecord(GpsQuery query);  //查询单个防爆燃气的采集数据记录
	public int getGasRecordCount(GpsQuery query);
}
