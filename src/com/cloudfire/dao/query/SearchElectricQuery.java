package com.cloudfire.dao.query;

import java.util.List;

import com.cloudfire.entity.query.SearchElectricInfo;
import com.cloudfire.until.core.dao.HibernateDao;

/**
 * @author cheng
 *2017-4-5
 *����9:19:17
 */

public interface SearchElectricQuery {
	//��������ѯ���������豸
	public List<SearchElectricInfo> searchElectricInfo(SearchElectricInfo query,List<String> areaList);
	//@lzo update 8-1
	public List<SearchElectricInfo> searchElectricInfos(SearchElectricInfo query,List<String> areaList);
	public List<SearchElectricInfo> searchElectricInfo2(SearchElectricInfo query,List<String> areaList);
	public int getElectricInfoCount(SearchElectricInfo query,List<String> areaList);
	
	public List<SearchElectricInfo> searchElectricHu(SearchElectricInfo query,List<String> areaList);
	//�绡�豸������
	public int getElectricHuCount(SearchElectricInfo query,List<String> areaList);
	
	/**
	 * ��ȡNB��������ͳ��
	 */
	public int getNB_ElectricInfoCount(SearchElectricInfo query,List<String> areaList);
	/**
	 * ��ȡNB��������
	 */
	public List<SearchElectricInfo> searchNBElectricInfo(SearchElectricInfo query,List<String> areaList);
	
	public SearchElectricInfo queryDeviceTypeByMac(String mac,List<String> areaids);
	
	//�����豸mac��ȡ�豸����
	public SearchElectricInfo getDeviceDetail(String smokeMac);
}
