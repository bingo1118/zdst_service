package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.BQEletricAVGData;
import com.cloudfire.entity.ElectricPCBean;
import com.cloudfire.entity.MyElectricInfo;
import com.cloudfire.entity.SearchDto;

public interface ElectricPCDao {
	
	public List<ElectricPCBean> getAllValue(String type);
	
	public List<ElectricPCBean> getAllValue(String type,String mac);
	
	/*public List<MyElectricInfo> getAllValue1(String type,String mac);*/
	
	public List<String> getAllValue2(String type,String mac);
	
	/*public List<String> getAllValue3(String type,String mac);*/
	
	//ͳ�Ƶ����豸ĳ�����ݵ�����
	public int getAllValue4Count(String type, String mac);
	
	public List<MyElectricInfo> getAllValue4(String type,String mac);
	//��ҳ��ʾ�����豸������
	public List<MyElectricInfo> getAllValue4(String type, String mac, Integer pageNo,Integer pageSize);
	
	//add by daill ��������ID��smokeMac��ѯ�豸�ĵ�ѹ������
	public List<BQEletricAVGData> getBqMonthAVGDatas(List<String> list,SearchDto dto);
	
	/**
	 * @param type
	 * @param mac
	 * @return
	 */
	public List<MyElectricInfo> getAllThreeValue4(int type,String mac);

}
