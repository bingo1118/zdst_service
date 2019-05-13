/**
 * ÏÂÎç3:36:50
 */
package com.cloudfire.myservice.impl;

import java.util.List;

import com.cloudfire.controller.water.WaterInfoController;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.entity.query.WaterQuery;
import com.cloudfire.myservice.WaterService;

import common.page.Pagination;

/**
 * @author cheng
 *2017-5-10
 *ÏÂÎç3:36:50
 */
public class WaterServiceImpl implements WaterService{
	private WaterInfoDao waterInfoDao; 
	
	public Pagination selectAllWaterListWithPage(List<String> areaIds,WaterQuery query){
		waterInfoDao = new WaterInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = waterInfoDao.getAllWaterInfoCount(areaIds, query);
		List<WaterQuery> list = waterInfoDao.getAllWaterInfo(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

	@Override
	public Pagination selectAllWaterRecordWithPage(WaterQuery query) {
		waterInfoDao = new WaterInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = waterInfoDao.getAllWaterRecordCount(query);
		List<WaterQuery> list = waterInfoDao.getAllWaterRecord(query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	@Override
	public Pagination waterViewRecordPage(WaterQuery query) {
		waterInfoDao = new WaterInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = waterInfoDao.getAllWaterRecordCount(query);
		List<WaterQuery> list = waterInfoDao.getAllWaterRecordList(query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	@Override
	public Pagination NBWaterDetail(WaterQuery query) {
		waterInfoDao = new WaterInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = waterInfoDao.getAllWaterRecordCount(query);
		List<WaterQuery> list = waterInfoDao.NBWaterDetailList(query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

	@Override
	public Pagination selectAllThListWithPage(List<String> areaIds,
			WaterQuery query) {
		waterInfoDao = new WaterInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = waterInfoDao.getAllThInfoCount(areaIds, query);
		List<WaterQuery> list = waterInfoDao.getAllThInfo(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	@Override
	public Pagination selectAllThRecordWithPage(WaterQuery query) {
		waterInfoDao = new WaterInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = waterInfoDao.getAllThRecordCount(query);
		List<WaterQuery> list = waterInfoDao.getAllThRecord(query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	
}
