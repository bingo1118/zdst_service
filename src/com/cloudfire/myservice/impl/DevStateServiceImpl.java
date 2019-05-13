package com.cloudfire.myservice.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cloudfire.dao.DeviceStateDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.impl.DeviceStateDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.entity.LinkAction;
import com.cloudfire.entity.RepeaterBean;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.query.SmokeQuery;
import com.cloudfire.myservice.DevStateService;

import common.page.Pagination;

@Service
public class DevStateServiceImpl implements DevStateService {
	
	private DeviceStateDao deviceStateDao;
	
	private RePeaterDataDao reDao;
	
	public Pagination selectAllSmokeListWithPage(List<String> areaIds,SmokeQuery query){
		deviceStateDao = new DeviceStateDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = deviceStateDao.getAllDeviceCount(areaIds, query);
		List<SmartControlEntity> list = deviceStateDao.getAllDeviceInfo(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

	@Override
	public Pagination getRepeaterInfoPage(RepeaterBean query) {
		reDao = new RePeaterDataDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = reDao.countRepeaterInfo(query);
		List<RepeaterBean> list = reDao.queryRepeaterInfo(query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
}
