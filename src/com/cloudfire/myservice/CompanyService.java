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
	 * 维护单位，实现分页查询功能
	 * @param areaIds 根据区域条件
	 * @param query	分页封装
	 * @return
	 */
	Pagination selectCompanyInfo(String areaIds,TenanceEntityQuery query);
	
	/**
	 * 消防单位，实现分页查询功能，按query统一标准分页
	 * @param areaId	根据区域条件
	 * @param query		分页封装数据
	 * @return
	 */
	Pagination selectSmokeInfo(String areaId,MainIndexEntityQuery query);
	
	/**
	 * 只能监控，设备状态，用户信息传输装置的分页功能实现。
	 * @param query 分页条件封装
	 * @return
	 */
	Pagination selectFaultinfo(FaultInfoEntityQuery query,List<String> areaIds);
	
	/**
	 * 只能监控，设备状态，用户信息传输装置的分页功能实现。2级显示
	 * @param query 分页条件封装
	 * @return
	 */
	Pagination selectFaultinfo2(FaultInfoEntityQuery query,List<String> areaIds,String repeaterMac,String faultCode);
	
	/**
	 * 只能监控，设备状态，用户信息传输装置的分页功能实现。3级显示
	 * @param query 分页条件封装
	 * @return
	 */
	Pagination selectFaultinfo3(FaultInfoEntityQuery query,String repeaterMac,String faultCode);
	
	
	/**
	 * 建筑单位信息查询。
	 * @param query 分页条件封装
	 * @return
	 */
	Pagination selectBudinginfo(MyDevicesEntityQuery query,List<String> areaIds);
	
	/**
	 * NFC巡逻维保记录
	 * @param areaIds
	 * @param query
	 * @return
	 */
	Pagination selectNFC_info(List<String> areaIds,NFC_infoEntity query);
	
	/**
	 * NFC巡逻维保记录
	 * @param areaIds
	 * @param query
	 * @return
	 */
	Pagination selectNFC_info_record(String uuid,NFC_infoEntity query);
	
	Pagination selectNFC_info_record1(String uuid,NFC_infoEntity query);
}
