package com.cloudfire.myservice.impl;

import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.OperationDao;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.OperationDaoImpl;
import com.cloudfire.entity.CompanyEntity;
import com.cloudfire.entity.Operation;
import com.cloudfire.entity.OperationQuery;
import com.cloudfire.entity.query.TenanceEntityQuery;
import com.cloudfire.myservice.OperationService;
import common.page.Pagination;

public class OperationServiceImpl implements OperationService {

	@Override
	public Pagination getOperations(OperationQuery query) {
		OperationDao od = new OperationDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = od.getOperationCount(query);
		List<Operation> list = new ArrayList<Operation>();
		list = od.getOperations(query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

}
