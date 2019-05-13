package com.cloudfire.myservice.impl;

import java.util.List;

import com.cloudfire.dao.FreeDao;
import com.cloudfire.dao.impl.FreeDaoImpl;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.query.DevicesFee;
import com.cloudfire.entity.query.SmokeQuery;
import com.cloudfire.myservice.FreeService;

import common.page.Pagination;

public class FreeServiceImpl implements FreeService {
	
	private FreeDao freeDao;

	@Override
	public Pagination getDevicesPage(DevicesFee query) {
		freeDao = new FreeDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = freeDao.countDeviceType(query);
		List<DevicesFee> list = freeDao.deviceList(query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

	@Override
	public Pagination getDetailPage(List<String> areaList, SmokeQuery query) {
		freeDao = new FreeDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = freeDao.countDetail(areaList, query);
		List<SmartControlEntity> list = freeDao.detailList(areaList, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

}
