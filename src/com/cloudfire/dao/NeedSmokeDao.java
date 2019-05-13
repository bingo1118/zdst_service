package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.AllCheakItemEntity;
import com.cloudfire.entity.AllEnviDevEntity;
import com.cloudfire.entity.AllNFCInfoEntity;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.ElevatorInfoBeanEntity;
import com.cloudfire.entity.NFCInfoEntity;
import com.cloudfire.entity.NFCTraceEntity;
import com.cloudfire.entity.NFC_infoEntity;
import com.cloudfire.entity.NeedNFCRecordEntity;
import com.cloudfire.entity.OneGPSInfoEntity;
import com.cloudfire.entity.OneGPSTraceEntity;
import com.cloudfire.entity.THInfoBeanEntity;

public interface NeedSmokeDao {
	public AllSmokeEntity getNeedSmoke(String userId,String privilege,String page,String areaId,String placeTypeId); 
	
	/**
	 * @author lzo
	 * @return 返回11-12-13的设备类型
	 */
	public AllSmokeEntity getNeedSecurity(String userId,String privilege,String page,String areaId,String placeTypeId);
	
	//针对电器设备
	public AllSmokeEntity getNeedDevice(String userId,String privilege,String page,String areaId,String placeTypeId); 
	public AllSmokeEntity getNeedDev(String userId,String privilege,String page,String areaId,String placeTypeId,String devType,String parentId); 
	public AllSmokeEntity getNeedLossDev(String userId,String privilege,String page,String areaId,String placeTypeId,String devType,String parentId); 
	
	public AllEnviDevEntity getNeedEnviDev(String userId,String privilege,String page,String areaId,String placeTypeId); 
	public AllSmokeEntity getNeedGPSDev(String userId,String privilege,String page,String areaId,String placeTypeId); 
	public OneGPSInfoEntity getOneGPSInfo(String mac); 
	public OneGPSTraceEntity getOneGPSTrace(String mac,String begintime,String endtime); 
	
	public AllSmokeEntity getNeedElevatorDev(String userId,String privilege,String page,String areaId,String placeTypeId); 
	public ElevatorInfoBeanEntity getOneElevatorDev(String id);
	
	/**
	 * add by lzo for getall nfcinto yong yu shouji
	 * @param period 
	 */
	public AllNFCInfoEntity getAllNFCInfo(List<String> areaIds,String page,String areaId, String period,String devicetype,String devicestate);
	
	/**
	 * add by lzo for getall nfcinto yong yu diannao
	 */
	public List<NFC_infoEntity> getAllNFCInfo_web(List<String> areaIds,NFC_infoEntity query);
	
	/**
	 * add by lzo for getall nfcinto yong yu diannao ditu
	 */
	public List<NFC_infoEntity> getAllNFCInfo_map(List<String> areaIds,NFC_infoEntity query);
	
	/**
	 * add by lzo for getall nfcinto yong yu diannao ditu统计数据
	 */
	public NFC_infoEntity getNFCInfo_map(List<String> areaIds,NFC_infoEntity query);
	
	/**
	 * add by lzo for getall nfcinto yong yu diannao lishijilu
	 */
	public List<NFC_infoEntity> getAllNFCInfo_web_record(String uuid,NFC_infoEntity query);
	

	/**
	 * add by lzo for getall nfcinto yong yu diannao
	 */
	public int getAllNFCInfo_web_record_count(String record,NFC_infoEntity query);
	
	
	/**
	 * add by lzo for getall nfcinto yong yu diannao
	 */
	public int getAllNFCInfo_web_count(List<String> areaIds,NFC_infoEntity query);
	
	/**
	 * add by lzo for getall nfcrecord in uid
	 */
	public AllNFCInfoEntity getAllNFCRecord(String uid,String page,String userId,String privilege);
	
	/**
	 * add by lzo for getsmokeallinfo
	 */
	public AllSmokeEntity getSmokeAllInfo(String userId,String privilege,String page,String areaId,String placeTypeId,String prentId);

	public NFCTraceEntity getNFCTrace(String areaId, String begintime, String endtime);

	public AllSmokeEntity getAdminAllSmoke(String userId, String privilege,
			String page, String devType); 
	
	/**
	 * add by lzo for getNFCinfos
	 */
	public NFCInfoEntity getNFC_Info_Entity(String uuid);
	public THInfoBeanEntity getTHDevInfo(String mac);

	int getAllNFCInfo_web_count(List<String> areaIds, NFC_infoEntity query,
			String time1, String time2);

	List<NFC_infoEntity> getAllNFCInfo_web(List<String> areaIds,
			NFC_infoEntity query, String time1, String time2);
	
	public List<NFC_infoEntity> getAllNFCInfo_web_record1(String uuid,NFC_infoEntity query);
	
	int getAllNFCInfo_web_record_count1(String uuid,NFC_infoEntity query);

	AllCheakItemEntity getNFCCheakItems(String devType);

	NeedNFCRecordEntity getAllNFCRecordByCondition(String page,
			String userId, String privilege, String areaid, String starttime,
			String endtime);
	
}
