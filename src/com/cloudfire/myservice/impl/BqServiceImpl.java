/**
 * ÏÂÎç4:29:47
 */
package com.cloudfire.myservice.impl;

import java.util.List;

import com.cloudfire.dao.query.BqDaoQuery;
import com.cloudfire.dao.query.impl.BqDaoQueryImpl;
import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.query.ElectricDTUQuery;
import com.cloudfire.myservice.BqService;

import common.page.Pagination;

/**
 * @author cheng
 *2017-5-18
 *ÏÂÎç4:29:47
 */
public class BqServiceImpl implements BqService{
	private BqDaoQuery bqDaoQuery ;
	
	public Pagination getBqListPage(BQMacEntity query,List<String> areaIds){
		bqDaoQuery = new BqDaoQueryImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = bqDaoQuery.getBqCount(query, areaIds);
		List<BQMacEntity> list = bqDaoQuery.getList(query, areaIds);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	public BQMacEntity getOnetBqMacEntity(String deviceId){
		BQMacEntity entity = new BQMacEntity();
		bqDaoQuery = new BqDaoQueryImpl();
		entity = bqDaoQuery.getBqMacEntity(deviceId);
		return entity;
	}

	@Override
	public Pagination getElectriDTUListPage(ElectricDTUQuery query) {
		bqDaoQuery = new BqDaoQueryImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = bqDaoQuery.getElectricCount(query);
		List<ElectricDTUQuery> list = bqDaoQuery.getElectricDTU(query);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}
	
	
}
