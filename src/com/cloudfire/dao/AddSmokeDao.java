package com.cloudfire.dao;

import com.cloudfire.entity.HttpRsult;

public interface AddSmokeDao {
	/*userId=13622215085&
smokeMac=19381731&
privilege=&
smokeName=&
address=&
longitude=&
latitude=&
placeAddress=&
placeTypeId=&
principal1=&
principal1Phone=&
principal2=&
principal2Phone=&
areaId=&
repeater=&
camera=&
deviceType=&
positions=&
floors=&
storeys=
*/
	public HttpRsult addSmoke(String userId,String smokeMac,String privilege,
			String smokeName,String address,String longitude,String latitude,
			String placeAddress,String placeTypeId,String principal1,String principal1Phone,
			String principal2,String principal2Phone,String areaId,String repeater,String camera,
			String deviceType,String cameraChannel,String electrState, String image);
	public boolean isExited(String smokeMac);
	
	
	public HttpRsult addSmokeTwo(String userId,String smokeMac,String privilege,
			String smokeName,String address,String longitude,String latitude,
			String placeAddress,String placeTypeId,String principal1,String principal1Phone,
			String principal2,String principal2Phone,String areaId,String repeater,String camera,
			String deviceType, String positions, String floors, String storeys);
	
	public HttpRsult addNFC(String userId,String uId,String areaId,String deviceType,
			String deviceName,String address,String longitude,String latitude,
			String memo,String producer,String makeTime,String workerPhone,String makeAddress);
	
	
	
	// by liao zw 2017.11.5
	public boolean meterAddDevice(String mac,String name,String address);
	HttpRsult addAdministrationInfo(String fsocial_uuids, String fsocial_name,
			String fprovince_code, String fcity_code, String fcounty_code,
			String ftown_code, String faddress, String flink_man,
			String ftel_no, String flongitude, String flatitude,
			String funit_type, String fis_active);
	public HttpRsult addSmoke_ytr(String userId, String smokeMac,
			String privilege, String smokeName, String address,
			String longitude, String latitude, String placeAddress,
			String placeTypeId, String principal1, String principal1Phone,
			String principal2, String principal2Phone, String areaId,
			String repeater, String camera, String deviceType,
			String cameraChannel, String electrState);
	public HttpRsult addNFCRecord(String UUID, String longitude, String latutide,
			String userId, String endTime, String devicestate, String memo,
			String photo1, String items);
	
//国瑞消防添加设备
	public HttpRsult addSmokeForGR(String userId, String smokeMac, String privilege, String smokeName, String address,
			String longitude, String latitude, String placeAddress, String placeTypeId, String principal1,
			String principal1Phone, String principal2, String principal2Phone, String areaId, String repeater,
			String camera, String deviceType, String cameraChannel, String electrState, String companyId,
			String buildingId);
	
}
