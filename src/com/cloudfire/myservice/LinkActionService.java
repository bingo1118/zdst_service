package com.cloudfire.myservice;


import com.cloudfire.entity.LinkAction;

import common.page.Pagination;

public interface LinkActionService {
	
	public Pagination getLinkActionPage(LinkAction query);

}
