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
	public List<ProofGasEntity> getGasRecord(GpsQuery query);  //��ѯ��������ȼ���Ĳɼ����ݼ�¼
	public int getGasRecordCount(GpsQuery query);
}
