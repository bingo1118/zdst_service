/**
 * ÉÏÎç10:45:16
 */
package com.cloudfire.myservice.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cloudfire.dao.query.SearchElectricQuery;
import com.cloudfire.dao.query.impl.SearchElectricQueryImpl;
import com.cloudfire.entity.ElectricInfo;
import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.myservice.ElectricInfoService;
import com.opensymphony.module.sitemesh.Page;

import common.page.Pagination;

/**
 * @author cheng
 *2017-5-4
 *ÉÏÎç10:45:16
 */

@Service
public class ElectricInfoServiceImpl implements ElectricInfoService{
	private SearchElectricQuery electricDao;
	
	public Pagination getElectricListWithPage(List<String> areaIds,SearchElectricInfo query){
		electricDao = new SearchElectricQueryImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = electricDao.getElectricInfoCount(query,areaIds);
		List<SearchElectricInfo> list = electricDao.searchElectricInfo(query, areaIds);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	public Pagination getElectricHuListWithPage(List<String> areaIds,SearchElectricInfo query){
		electricDao = new SearchElectricQueryImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = electricDao.getElectricHuCount(query,areaIds);
		List<SearchElectricInfo> list = electricDao.searchElectricHu(query, areaIds);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	
	public Pagination get_NB_ElectricList(List<String> areaIds,SearchElectricInfo query){
		electricDao = new SearchElectricQueryImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = electricDao.getNB_ElectricInfoCount(query,areaIds);
		List<SearchElectricInfo> list = electricDao.searchNBElectricInfo(query, areaIds);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
}
