package com.cloudfire.myservice;

import com.cloudfire.entity.query.GpsQuery;

import common.page.Pagination;

public interface GpsService {
	public Pagination  getGpsRecordInfo(GpsQuery query);
}
