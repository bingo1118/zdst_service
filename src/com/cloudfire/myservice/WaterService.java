/**
 * ����3:32:54
 */
package com.cloudfire.myservice;

import java.util.List;


import com.cloudfire.entity.query.WaterQuery;

import common.page.Pagination;

/**
 * @author cheng
 *2017-5-10
 *����3:32:54
 */
public interface WaterService {
	/** ѡ�����е�ˮѹ�豸 */
	Pagination selectAllWaterListWithPage(List<String> areaIds,WaterQuery query);
	
	/** ˮѹ���ݼ�¼��¼ */
	Pagination selectAllWaterRecordWithPage(WaterQuery query);
	
	/**ѡ�����е�ʪ�¶��豸*/
	Pagination selectAllThListWithPage(List<String> areaIds,WaterQuery query);
	
	/** ʪ�¶����ݼ�¼��¼ */
	Pagination selectAllThRecordWithPage(WaterQuery query);
	
	/** ˮѹˮλ���ݼ�¼��¼ */
	 Pagination NBWaterDetail(WaterQuery query);
	 
	/** ����ˮλ̽�������ݼ�¼��¼ */
	Pagination waterViewRecordPage(WaterQuery query);

}
