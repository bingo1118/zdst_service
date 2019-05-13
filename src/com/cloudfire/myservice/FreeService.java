package com.cloudfire.myservice;


import java.util.List;

import com.cloudfire.entity.query.DevicesFee;
import com.cloudfire.entity.query.SmokeQuery;

import common.page.Pagination;

public interface FreeService {
	
	Pagination getDevicesPage(DevicesFee query);
	
	Pagination getDetailPage(List<String> areaList,SmokeQuery query);
	
	

}
