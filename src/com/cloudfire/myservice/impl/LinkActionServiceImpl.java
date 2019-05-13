package com.cloudfire.myservice.impl;

import java.util.List;

import com.cloudfire.dao.LinkActionDao;
import com.cloudfire.dao.impl.LinkActionDaoImpl;
import com.cloudfire.entity.LinkAction;
import com.cloudfire.myservice.LinkActionService;

import common.page.Pagination;

public class LinkActionServiceImpl implements LinkActionService {
	
private LinkActionDao linkActionDao;
	
	public LinkActionServiceImpl() {
		linkActionDao = new LinkActionDaoImpl();
	}

	@Override
	public Pagination getLinkActionPage(LinkAction query) {
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = linkActionDao.countLinkAction(query);
		List<LinkAction> list = linkActionDao.linkActionList(query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

}
