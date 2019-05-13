/**
 * ÏÂÎç4:02:20
 */
package com.cloudfire.myservice.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.IAlarmInfoDao_PC;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl_PC;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AlarmInfoEntity_PC;
import com.cloudfire.entity.query.AlarmInfoQuery;
import com.cloudfire.myservice.AlarmInfoService;
import common.page.Pagination;

/**
 * @author cheng
 *2017-5-3
 *ÏÂÎç4:02:20
 */
@Service
public class AlarmInfoServiceImpl implements AlarmInfoService{
	private IAlarmInfoDao_PC dao;
	private AlarmInfoDao mAlarmInfoDao;
	
	public Pagination selectAlarmInfoListByPage(List<String> areaIds,AlarmInfoEntity_PC query){
		dao = new AlarmInfoDaoImpl_PC();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = dao.getAlarmInfoCount(areaIds, query);
		List<AlarmInfoEntity_PC> list = dao.getAlarmInfo(query, areaIds);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	public Pagination selectAllAlarmInfoListByPage(List<String> areaIds,AlarmInfoQuery query){
		dao = new AlarmInfoDaoImpl_PC();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = dao.getAllAlarmInfoCount(areaIds, query);
		List<AlarmInfoQuery> list = dao.getAllAlarmInfoMsg(areaIds, query);
		Pagination pagination = new Pagination(pageNo, pageSize, totalCount, list);
		return pagination;
	}
	
	public Pagination selectAllAlarmInfoListByPage(List<String> areaIds,AlarmInfoEntity query){
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = mAlarmInfoDao.getAllAlarmInfoCount(areaIds, query);
		List<AlarmInfoEntity> list = mAlarmInfoDao.getAllAlarmInfo(areaIds, query);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}

	@Override
	public Pagination selectAlarmInfoListByMac(AlarmInfoEntity_PC query, String mac,List<String> areaIds) {
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		int pageNo = query.getPageNo();
		int pageSize = query.getPageSize();
		int totalCount = mAlarmInfoDao.getAlarmInfoCount(mac, query, areaIds);
		List<AlarmInfoQuery> list = mAlarmInfoDao.getAllAlarmInfoByMac(mac, query,areaIds);
		Pagination pagination = new Pagination(pageNo,pageSize,totalCount,list);
		return pagination;
	}
}
