package com.cloudfire.myservice;


import java.util.List;

import com.cloudfire.entity.DeviceCostEntity;
import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.entity.query.SmokeQuery;

import common.page.Pagination;

public interface SmokeService {
	
	Pagination selectAllSmokeListWithPage(List<String> areaIds,SmokeQuery query);
	
	Pagination selectAllSmokesListWithPage(List<String> areaIds,SmokeQuery query);
	
	Pagination selectSmokeListWithPage(List<String> areaIds,SmokeQuery query);
	
	Pagination searchSmokeListPyPage(SearchElectricInfo info,List<String> areaList);
	
	/**
	 * @param ��ѯNB�����̸�����
	 * @return
	 */
	Pagination selectNBSmokeList(List<String> areaIds,SmokeQuery query);
	
	Pagination getSmokeListPage(List<String> areaIds,SmokeQuery query);
	
	Pagination getNBWater(List<String> areaIds,SmokeQuery query);

	Pagination selectAllSmokeListWithPage(String areaIdsStr, SmokeQuery query);
	
	public Pagination selectUnknownPage(List<String> areaIds,SmokeQuery query);
}
