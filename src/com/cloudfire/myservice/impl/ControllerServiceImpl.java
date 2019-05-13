/**
 * ионГ10:32:47
 */
package com.cloudfire.myservice.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cloudfire.dao.IDealOkMsg;
import com.cloudfire.dao.impl.DealOkMsgImpl;
import com.cloudfire.entity.DealOkAlarmEntity;
import com.cloudfire.myservice.ControllerService;
import common.page.Pagination;

/**
 * @author cheng
 *2017-5-3
 *ионГ10:32:47
 */
@Service
public class ControllerServiceImpl implements ControllerService {
	private IDealOkMsg dealOkMsgdao;

	public Pagination selectControllerListWithPage(List<String> areaIds,DealOkAlarmEntity query) {
		dealOkMsgdao = new DealOkMsgImpl();
		int pageNo =query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = dealOkMsgdao.getDealAlarmMsgCount(areaIds, query);
		List<DealOkAlarmEntity> list = dealOkMsgdao.getokDealAlarmMsg(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
		
	}

}
