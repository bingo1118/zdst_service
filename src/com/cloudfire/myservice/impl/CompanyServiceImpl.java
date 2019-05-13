package com.cloudfire.myservice.impl;

import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.FaultInfoDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.NeedSmokeDao;
import com.cloudfire.dao.impl.FaultInfoDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.NeedSmokeDaoImpl;
import com.cloudfire.entity.CompanyEntity;
import com.cloudfire.entity.FaultInfoEntity;
import com.cloudfire.entity.MyDevicesEntityQuery;
import com.cloudfire.entity.NFC_infoEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.query.FaultInfoEntityQuery;
import com.cloudfire.entity.query.MainIndexEntityQuery;
import com.cloudfire.entity.query.TenanceEntityQuery;
import com.cloudfire.myservice.CompanyService;
import common.page.Pagination;

public class CompanyServiceImpl implements CompanyService {

	@Override
	public Pagination selectCompanyInfo(String areaId,
			TenanceEntityQuery query) {
		InfoManagerDao companydao = new InfoManagerDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = companydao.getInfoByAreaId(areaId,query);
		List<CompanyEntity> list = new ArrayList<CompanyEntity>();
		list = companydao.getInfoAll(areaId,query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}

	@Override
	public Pagination selectSmokeInfo(String areaId, MainIndexEntityQuery query) {
		InfoManagerDao companydao = new InfoManagerDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = companydao.getSmokeByQuery(areaId, query)	;
		List<SmokeBean> list =  companydao.getAllSmokesByQuery(areaId, query);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}
	
	@Override
	public Pagination selectFaultinfo(FaultInfoEntityQuery query,List<String> areaIds){
		FaultInfoDao faultinfo = new FaultInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = faultinfo.selectFaultCount(query,areaIds);
		List<FaultInfoEntity> list = faultinfo.selectFaultInfoBySmoke(query,areaIds);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}
	
	@Override
	public Pagination selectFaultinfo2(FaultInfoEntityQuery query,List<String> areaIds,String repeaterMac,String faultCode){
		FaultInfoDao faultinfo = new FaultInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = faultinfo.selectFaultCount2(query,areaIds,repeaterMac,faultCode);
		List<FaultInfoEntity> list = new ArrayList<FaultInfoEntity>();
		list = faultinfo.selectFaultInfo2(query,areaIds,repeaterMac,faultCode);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}
	
	@Override
	public Pagination selectFaultinfo3(FaultInfoEntityQuery query,String repeaterMac,String faultCode){
		FaultInfoDao faultinfo = new FaultInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = faultinfo.selectFaultCount3(query,repeaterMac,faultCode);
		List<FaultInfoEntity> list = new ArrayList<FaultInfoEntity>();
		list = faultinfo.selectFaultInfo3(query,repeaterMac,faultCode);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}

	@Override
	public Pagination selectBudinginfo(MyDevicesEntityQuery query,
			List<String> areaIds) {
		InfoManagerDao companydao = new InfoManagerDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = companydao.getInfoCount(areaIds, query);
		List<MyDevicesEntityQuery> list = new ArrayList<MyDevicesEntityQuery>();
		list = companydao.getInfoAllCount(areaIds, query);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}

	@Override
	public Pagination selectNFC_info(List<String> areaIds, NFC_infoEntity query) {
		NeedSmokeDao nsdao = new NeedSmokeDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = nsdao.getAllNFCInfo_web_count(areaIds, query,null,null);
		List<NFC_infoEntity> list = new ArrayList<NFC_infoEntity>();
		list = nsdao.getAllNFCInfo_web(areaIds, query,null,null);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}
	
	@Override
	public Pagination selectNFC_info_record(String uuid, NFC_infoEntity query) {
		NeedSmokeDao nsdao = new NeedSmokeDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = nsdao.getAllNFCInfo_web_record_count(uuid, query);
		List<NFC_infoEntity> list = new ArrayList<NFC_infoEntity>();
		list = nsdao.getAllNFCInfo_web_record(uuid, query);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}
	
	@Override
	public Pagination selectNFC_info_record1(String uuid, NFC_infoEntity query) {
		NeedSmokeDao nsdao = new NeedSmokeDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = nsdao.getAllNFCInfo_web_record_count1(uuid, query);
		List<NFC_infoEntity> list = new ArrayList<NFC_infoEntity>();
		list = nsdao.getAllNFCInfo_web_record1(uuid, query);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}
}
