/**
 * ����4:23:31
 */
package com.cloudfire.dao.query;

import java.util.List;

import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.query.SearchAnalysisInfo;
import com.cloudfire.entity.query.SearchAnalysisinfo1;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * @author cheng
 *2017-4-14
 *����4:23:31
 */
public interface SearchAnalysisQuery {
	public Integer getAlarmCountLg(List<String> areaIds,List<String> listalarmType,String type,SearchAnalysisinfo1 query);
	/** ͨ���û���id��ѯ������ӵ�е��豸 */
	public List<SearchAnalysisInfo> getAnalysisInfoData(List<String> areaIds,String type);
	
	public List<SearchAnalysisInfo> getAnalysisInfoData(List<String> areaIds,CountValue cv);
	
	/**
	 * ��д�Ż�֮ǰSQL���
	 * @param areaIds
	 * @return
	 */
	public List<SearchAnalysisInfo> getAnalysisInfoData(List<String> areaIds);
	
	/**��ѯ���е���� */
	public List<String> getAllYear();
	
	/**��ʼ����������ͳ��ͼ������ */
	public Integer getAlarmCount202(List<String> areaIds,String devId,SearchAnalysisinfo1 query);
	public Integer getAlarmCount193(String type ,SearchAnalysisinfo1 query);
	
	
	
	/** ��ʼ���豸����Ϊ5���豸�ķ������ϵĴ��� */
	public Integer getAlarmCount36(String type ,SearchAnalysisinfo1 query);
	public Integer getAlarmCount43(String type ,SearchAnalysisinfo1 query);
	public Integer getAlarmCount44(String type ,SearchAnalysisinfo1 query);
	public Integer getAlarmCount45(String type ,SearchAnalysisinfo1 query);
	public Integer getAlarmCount46(String type ,SearchAnalysisinfo1 query);
	public Integer getAlarmCount47(String type ,SearchAnalysisinfo1 query);
	
	
	/** ��ʼ���豸����Ϊ5���豸�ĸ�����ƽ������ */	
	public Float getAlarmCount43and44avg(String type ,SearchAnalysisinfo1 query);
	public Float getAlarmCount45avg(String type ,SearchAnalysisinfo1 query);
	public Float getAlarmCount46avg(String type ,SearchAnalysisinfo1 query);
	public Float getAlarmCount47avg(String type ,SearchAnalysisinfo1 query);
}
