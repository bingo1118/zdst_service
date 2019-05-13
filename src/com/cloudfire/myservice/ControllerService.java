/**
 * ионГ10:31:33
 */
package com.cloudfire.myservice;

import java.util.List;

import com.cloudfire.entity.DealOkAlarmEntity;

import common.page.Pagination;

/**
 * @author cheng
 *2017-5-3
 *ионГ10:31:33
 */
public interface ControllerService {
	Pagination selectControllerListWithPage(List<String> areaIds,DealOkAlarmEntity query);
}
