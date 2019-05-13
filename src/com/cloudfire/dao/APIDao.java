package com.cloudfire.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudfire.entity.APIResult;
import com.cloudfire.entity.BuildingInfo;
import com.cloudfire.entity.CompanyInfo;
import com.cloudfire.entity.Equipment;
import com.cloudfire.entity.StisEntity;

public interface APIDao {
	public APIResult verifyBuildingId(String buildingId);
	public APIResult addBuilding(String buildingId, String buildingName, String companyId, String address,String buildingType, String lng, String lat, String regionCode,int areaid);
	public APIResult delBuilding(String buildingId);
	public APIResult updateBuilding(String buildingId, String buildingName, String companyId, String address,String buildingType, String lng, String lat, String regionCode,int areaid);
	public List<BuildingInfo> getBuidings(String buildingId,long createDateB,long createDateE,long updateDateB,long updateDateE,int pageNo,int pageSize,int areaid );
	public int getBuildingCount(String buildingId,long createDateB,long createDateE,long updateDateB,long updateDateE,int areaid);
	
	public APIResult verifyCompanyId(String companyId);
	public APIResult addCompany(String companyId, String companyName, String companyCode, String address,String contactName, String contactTel,int areaid);
	public APIResult delCompany(String companyId);
	public APIResult updateCompany(String companyId, String companyName, String companyCode, String address,String contactName, String contactTel,int areaid);
	public List<CompanyInfo> getCompanys(String id, long createDateB,long createDateE, long updateDateB, long updateDateE, int pageNo,int pageSize,int areaid);
	public int getCompanyCount(String id, long createDateB, long createDateE,long updateDateB, long updateDateE,int areaid);
	
	public List<Equipment> getEquipments(String id, long createDateB,long createDateE, long updateDateB, long updateDateE, int pageNo,int pageSize,int areaid);
	public int getEquipmentCount(String id, long createDateB, long createDateE,long updateDateB, long updateDateE,int areaid);
	
	public List<StisEntity> getStatics(long createDateB, long createDateE,int pageNo, int pageSize,int areaid);
	public int getStisCount(long createDateB, long createDateE,int areaid);
	
	public APIResult bindDeviceType(String deviceType, String deviceMiniType);
	
	public List<StisEntity> dailyStatics();
	public void dailyCount(List<StisEntity> lstStis);
	
	public Map<String, String> getAllBuilding(String areaId);
	public Map<String, String> getAllCompany(String areaId);
	public Map<String, String> getAllBuilding();
	public Map<String, String> getAllCompany();
	public Map<String, String> getAllBuildingType();
	public Map<String, String> getAllCity();
	public Map<String, String> selectCountry(String cityCode);
	public Map<String, String> selectTown(String countryCode);
	public Map<String, String> getAllSystem();
	
	
	
	
}
