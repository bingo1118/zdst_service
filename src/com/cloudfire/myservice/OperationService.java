package com.cloudfire.myservice;

import com.cloudfire.entity.OperationQuery;

import common.page.Pagination;

public interface OperationService {
	public Pagination getOperations(OperationQuery query);
}
