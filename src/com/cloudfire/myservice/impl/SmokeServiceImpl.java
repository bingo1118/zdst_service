package com.cloudfire.myservice.impl;

import java.util.List;


import org.springframework.stereotype.Service;

import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.entity.DeviceCostEntity;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.entity.query.SmokeQuery;
import com.cloudfire.myservice.SmokeService;

import common.page.Pagination;

@Service
public class SmokeServiceImpl implements SmokeService{
	
	private SmartControlDao smartControlDao;
	
	public Pagination selectAllSmokeListWithPage(List<String> areaIds,SmokeQuery query){
		smartControlDao = new SmartControlDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = smartControlDao.getAllSmokeInfoCount(areaIds, query);
		List<SmartControlEntity> list = smartControlDao.getAllSmokeInfo(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	public Pagination selectSmokeListWithPage(List<String> areaIds,SmokeQuery query){
		smartControlDao = new SmartControlDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = smartControlDao.getSmokeInfoCount(areaIds, query);
		List<SmartControlEntity> list = smartControlDao.smokeInfo(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	public Pagination searchSmokeListPyPage(SearchElectricInfo info,List<String> areaList){
		smartControlDao = new SmartControlDaoImpl();
		int pageNo = info.getPageNo();
		int pageSize = info.getPageSize();
		List<SearchElectricInfo> list = smartControlDao.searchDeviceByTypeQuery(info, areaList);
		int totalCount = smartControlDao.searchCountDeviceByTypeQuery(info,areaList);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	public Pagination selectNBSmokeList(List<String> areaIds,SmokeQuery query){
		smartControlDao = new SmartControlDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = smartControlDao.getNBSmokeInfoCount(areaIds, query);
		List<SmartControlEntity> list = smartControlDao.NBsmokeInfo(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	public Pagination getSmokeListPage(List<String> areaIds,SmokeQuery query){
		smartControlDao = new SmartControlDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = smartControlDao.getSmokeListCount(areaIds, query);
		List<SmartControlEntity> list = smartControlDao.getSmokeList(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

	@Override
	public Pagination selectAllSmokesListWithPage(List<String> areaIds, SmokeQuery query) {
		smartControlDao = new SmartControlDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = smartControlDao.getAllSmokesInfoCount(areaIds, query);
		List<DeviceCostEntity> list = smartControlDao.getAllSmokesInfo(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

	@Override
	public Pagination getNBWater(List<String> areaIds, SmokeQuery query) {
		smartControlDao = new SmartControlDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = smartControlDao.countNBWater(areaIds, query);
		List<SmartControlEntity> list = smartControlDao.NBWaterList(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

	@Override
	public Pagination selectAllSmokeListWithPage(String areaIdsStr,
			SmokeQuery query) {
		smartControlDao = new SmartControlDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		long t1 = System.currentTimeMillis();
		int totalCount = smartControlDao.getAllSmokeInfoCount(areaIdsStr, query);
		long t2 = System.currentTimeMillis();
		List<SmartControlEntity> list = smartControlDao.getAllSmokeInfo(areaIdsStr, query);
		long t3 = System.currentTimeMillis();
		System.out.println(t1);
		System.out.println(t2);
		System.out.println(t3);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	
	}
	
	public Pagination selectUnknownPage(List<String> areaIds,SmokeQuery query){
		smartControlDao = new SmartControlDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = smartControlDao.countUnknow(areaIds, query);
		List<SmartControlEntity> list = smartControlDao.getUnknowList(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
}
