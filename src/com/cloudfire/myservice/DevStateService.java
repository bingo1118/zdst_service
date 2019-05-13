package com.cloudfire.myservice;


import java.util.List;

import com.cloudfire.entity.DeviceCostEntity;
import com.cloudfire.entity.RepeaterBean;
import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.entity.query.SmokeQuery;

import common.page.Pagination;

public interface DevStateService {
	
	Pagination selectAllSmokeListWithPage(List<String> areaIds,SmokeQuery query);
	
	Pagination getRepeaterInfoPage(RepeaterBean query);
	
}
