package com.cloudfire.myservice;


import java.util.List;

import com.cloudfire.entity.MyDevicesEntityQuery;
import com.cloudfire.entity.NFC_infoEntity;
import com.cloudfire.entity.query.FaultInfoEntityQuery;
import com.cloudfire.entity.query.MainIndexEntityQuery;
import com.cloudfire.entity.query.TenanceEntityQuery;


import common.page.Pagination;

public interface CompanyService {
	
	/**
	 * ά����λ��ʵ�ַ�ҳ��ѯ����
	 * @param areaIds ������������
	 * @param query	��ҳ��װ
	 * @return
	 */
	Pagination selectCompanyInfo(String areaIds,TenanceEntityQuery query);
	
	/**
	 * ������λ��ʵ�ַ�ҳ��ѯ���ܣ���queryͳһ��׼��ҳ
	 * @param areaId	������������
	 * @param query		��ҳ��װ����
	 * @return
	 */
	Pagination selectSmokeInfo(String areaId,MainIndexEntityQuery query);
	
	/**
	 * ֻ�ܼ�أ��豸״̬���û���Ϣ����װ�õķ�ҳ����ʵ�֡�
	 * @param query ��ҳ������װ
	 * @return
	 */
	Pagination selectFaultinfo(FaultInfoEntityQuery query,List<String> areaIds);
	
	/**
	 * ֻ�ܼ�أ��豸״̬���û���Ϣ����װ�õķ�ҳ����ʵ�֡�2����ʾ
	 * @param query ��ҳ������װ
	 * @return
	 */
	Pagination selectFaultinfo2(FaultInfoEntityQuery query,List<String> areaIds,String repeaterMac,String faultCode);
	
	/**
	 * ֻ�ܼ�أ��豸״̬���û���Ϣ����װ�õķ�ҳ����ʵ�֡�3����ʾ
	 * @param query ��ҳ������װ
	 * @return
	 */
	Pagination selectFaultinfo3(FaultInfoEntityQuery query,String repeaterMac,String faultCode);
	
	
	/**
	 * ������λ��Ϣ��ѯ��
	 * @param query ��ҳ������װ
	 * @return
	 */
	Pagination selectBudinginfo(MyDevicesEntityQuery query,List<String> areaIds);
	
	/**
	 * NFCѲ��ά����¼
	 * @param areaIds
	 * @param query
	 * @return
	 */
	Pagination selectNFC_info(List<String> areaIds,NFC_infoEntity query);
	
	/**
	 * NFCѲ��ά����¼
	 * @param areaIds
	 * @param query
	 * @return
	 */
	Pagination selectNFC_info_record(String uuid,NFC_infoEntity query);
	
	Pagination selectNFC_info_record1(String uuid,NFC_infoEntity query);
}
