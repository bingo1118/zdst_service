package com.cloudfire.myservice.impl;

import java.util.List;

import com.cloudfire.dao.GpsInfoDao;
import com.cloudfire.dao.impl.GpsInfoDaoImpl;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.entity.query.GpsQuery;
import com.cloudfire.myservice.GpsService;
import common.page.Pagination;

public class GpsServiceImpl implements GpsService {

	@Override
	public Pagination getGpsRecordInfo(GpsQuery query) {
		GpsInfoDao gid = new GpsInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = gid.getGasRecordCount( query);
		List<ProofGasEntity> list = gid.getGasRecord(query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

}
