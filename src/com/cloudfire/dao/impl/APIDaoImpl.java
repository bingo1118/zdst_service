package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.APIDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.APIResult;
import com.cloudfire.entity.BuildingInfo;
import com.cloudfire.entity.CompanyInfo;
import com.cloudfire.entity.Equipment;
import com.cloudfire.entity.LngLat;
import com.cloudfire.entity.StisEntity;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.MapUtil;

import antlr.Utils;

public class APIDaoImpl implements APIDao {

	@Override
	public APIResult addBuilding(String buildingId, String buildingName, String companyId, String address,String buildingType, String lng, String lat, String regionCode,int areaid) {
		String sql = "insert into building_info(buildingId,buildingName,companyId,address,buildingType,lng,lat,regionCode,createTime,areaid) values(?,?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		APIResult rs = new APIResult();
		try {
			ps.setString(1, buildingId);
			ps.setString(2, buildingName);
			ps.setString(3, companyId);
			ps.setString(4, address);
			ps.setString(5, buildingType);
			ps.setString(6, lng);
			ps.setString(7, lat);
			ps.setString(8, regionCode);
			ps.setString(9, GetTime.ConvertTimeByLong());
			ps.setInt(10, areaid);
			int count = ps.executeUpdate();
			if (count > 0) {
				rs.setCode(0);
				rs.setMessage("添加成功");
			} else {
				rs.setCode(1);
				rs.setMessage("buidingId已存在,请核对后重新添加");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	@Override
	public APIResult delBuilding(String buildingId) {
		String sql = "delete from buiding_info where buildingId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		APIResult rs = new APIResult();
		try {
			ps.setString(1, buildingId);
			int count = ps.executeUpdate();
			if (count > 0) {
				rs.setCode(0);
				rs.setMessage("删除成功");
			} else {
				rs.setCode(1);
				rs.setMessage("删除失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	@Override
	public APIResult updateBuilding(String buildingId, String buildingName, String companyId, String address,
			String buildingType, String lng, String lat, String regionCode,int areaid) {
		String sql = "update building_info set buildingName=?,companyId=?,address=?,buildingType=?,lng=?,lat=?,regionCode=?,updateTime=?,areai=? where buildingId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		APIResult rs = new APIResult();
		try {
			ps.setString(10, buildingId);
			ps.setString(1, buildingName);
			ps.setString(2, companyId);
			ps.setString(3, address);
			ps.setString(4, buildingType);
			ps.setString(5, lng);
			ps.setString(6, lat);
			ps.setString(7, regionCode);
			ps.setString(8, GetTime.ConvertTimeByLong());
			ps.setInt(9, areaid);
			int count = ps.executeUpdate();
			if (count > 0) {
				rs.setCode(0);
				rs.setMessage("修改成功");
			} else {
				rs.setCode(1);
				rs.setMessage("修改失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	@Override
	public List<BuildingInfo> getBuidings(String buildingId, long createDateB,long createDateE, long updateDateB, long updateDateE,int pageNo,int pageSize,int areaid) {
		StringBuffer sb = new StringBuffer();
		int start = (pageNo - 1) * pageSize;
//		int end =  pageNo * pageSize;
		sb.append("select buildingId,buildingName,companyId,address,buildingType,lat,lng,regionCode,deviceNum,isOnline,rechargeNum from building_info where areaid in (select areaid from areaidarea where parentId = "+areaid+ ") ");
		if (StringUtils.isNotBlank(buildingId)){
			sb.append(" and buildingId = '"+buildingId+"'");
		}
		if (createDateB !=0 && createDateE !=0) {
			sb.append(" and createTime > '"+ GetTime.getTimeByLong(createDateB)+"' and createTime < '"+ GetTime.getTimeByLong(createDateE)+"'");
		}
		if (updateDateB !=0 && updateDateE !=0){
			sb.append(" and updateTime > '"+ GetTime.getTimeByLong(updateDateB)+"' and updateTime < '"+ GetTime.getTimeByLong(updateDateE)+"'");
		}
		
		sb.append(" limit "+start+","+pageSize);
		
		List<BuildingInfo> buildings = new ArrayList<BuildingInfo>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				BuildingInfo building = new BuildingInfo();
				building.setBuildingId(rs.getString("buildingId"));
				building.setBuildingName(rs.getString("buildingName"));
				building.setCompanyId(rs.getString("companyId"));
				building.setAddress(rs.getString("address"));
				building.setBuildingType(rs.getString("buildingType"));
				//百度地图坐标转成高德坐标
				LngLat lnglat =MapUtil.coordinateTrans("baidu","gaode",rs.getString("lng"),rs.getString("lat"));
				building.setLat(lnglat.getLantitude()+"");
				building.setLng(lnglat.getLongitude()+"");
//				building.setLat(rs.getString("lat"));
//				building.setLng(rs.getString("lng"));
				building.setRegionCode(rs.getString("regionCode"));
				int deviceNum = getDeviceNumByBuldingId(rs.getString("buildingId"));
				building.setDeviceNum(deviceNum);
				building.setIsOnline(rs.getInt("isOnline"));
				building.setRechargeNum(rs.getInt("rechargeNum"));
				buildings.add(building);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return buildings;
	}

	private int getDeviceNumByBuldingId(String buildingId) {
		String sql ="select count(*) from smoke where buildingId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int deviceNum = 0;
		try {
			ps.setString(1, buildingId);
			rs = ps.executeQuery();
			while (rs.next()){
				deviceNum = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return deviceNum;
	}

	@Override
	public int getBuildingCount(String buildingId, long createDateB,long createDateE, long updateDateB, long updateDateE,int areaid) {
		int count = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("select count(buildingId) as count from building_info where 1=1 and areaid in (select areaid from areaidarea where parentId = "+areaid+ ") ");
		if (StringUtils.isNotBlank(buildingId)){
			sb.append(" and buildingId = '"+buildingId+"'");
		}
		if (createDateB !=0 && createDateE !=0) {
			sb.append(" and createTime > '"+ GetTime.getTimeByLong(createDateB)+"' and createTime < '"+ GetTime.getTimeByLong(createDateE)+"'");
		}
		if (updateDateB !=0 && updateDateE !=0){
			sb.append(" and updateTime > '"+ GetTime.getTimeByLong(updateDateB)+"' and updateTime < '"+ GetTime.getTimeByLong(updateDateE)+"'");
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			if (rs.next()){
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return count;
	}

	@Override
	public APIResult addCompany(String companyId, String companyName, String companyCode, String address,String contactName, String contactTel,int areaid) {
		String sql = "insert into company_info(companyId,companyName,companyCode,address,contactName,contactTel,createTime,areaid) values(?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		APIResult rs = new APIResult();
		try {
			ps.setString(1, companyId);
			ps.setString(2, companyName);
			ps.setString(3, companyCode);
			ps.setString(4, address);
			ps.setString(5, contactName);
			ps.setString(6, contactTel);
			ps.setString(7, GetTime.ConvertTimeByLong());
			ps.setInt(8, areaid);
			int count = ps.executeUpdate();
			if (count > 0) {
				rs.setCode(0);
				rs.setMessage("添加成功");
			} else {
				rs.setCode(1);
				rs.setMessage("companyId已存在,请核对后重新添加");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	@Override
	public APIResult delCompany(String companyId) {
		String sql ="delete from company_info where companyId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		APIResult rs = new APIResult();
		try {
			ps.setString(1, companyId);
			int count = ps.executeUpdate();
			if (count > 0) {
				rs.setCode(0);
				rs.setMessage("删除成功");
			} else {
				rs.setCode(1);
				rs.setMessage("删除失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	@Override
	public APIResult updateCompany(String companyId, String companyName, String companyCode, String address,String contactName, String contactTel,int areaid) {
		String sql = "update company_info set companyName=?,companyCode=?,address=?,contactName=?,contactTel=?,updateTime=? where companyId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		APIResult rs = new APIResult();
		try {
			ps.setString(7, companyId);
			ps.setString(1, companyName);
			ps.setString(2, companyCode);
			ps.setString(3, address);
			ps.setString(4, contactName);
			ps.setString(5, contactTel);
			ps.setString(6, GetTime.ConvertTimeByLong());
			int count = ps.executeUpdate();
			if (count > 0) {
				rs.setCode(0);
				rs.setMessage("修改成功");
			} else {
				rs.setCode(1);
				rs.setMessage("修改失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	@Override
	public List<CompanyInfo> getCompanys(String companyId, long createDateB,long createDateE, long updateDateB, long updateDateE, int pageNo,int pageSize,int areaid) {
		StringBuffer sb = new StringBuffer();
		int start = (pageNo - 1) * pageSize;
		sb.append("select companyId,companyName,companyCode,address,contactName,contactTel from company_info where 1=1 and areaid in (select areaid from areaidarea where parentId = "+areaid+ ") ");
		if (StringUtils.isNotBlank(companyId)){
			sb.append(" and companyId = '"+companyId+"'");
		}
		if (createDateB !=0 && createDateE !=0) {
			sb.append(" and createTime > '"+ GetTime.getTimeByLong(createDateB)+"' and createTime < '"+ GetTime.getTimeByLong(createDateE)+"'");
		}
		if (updateDateB !=0 && updateDateE !=0){
			sb.append(" and updateTime > '"+ GetTime.getTimeByLong(updateDateB)+"' and updateTime < '"+ GetTime.getTimeByLong(updateDateE)+"'");
		}
		
		sb.append(" limit "+start+","+pageSize);
		
		List<CompanyInfo> companys = new ArrayList<CompanyInfo>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				CompanyInfo company = new CompanyInfo();
				company.setCompanyId(rs.getString("companyId"));
				company.setCompanyName(rs.getString("companyName"));
				company.setCompanyCode(rs.getString("companyCode"));
				company.setAddress(rs.getString("address"));
				company.setContactName(rs.getString("contactName"));
				company.setContactTel(rs.getString("contactTel"));
				companys.add(company);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return companys;
	}

	@Override
	public int getCompanyCount(String companyId, long createDateB, long createDateE,long updateDateB, long updateDateE,int areaid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(companyId) from company_info where 1=1 and areaid in (select areaid from areaidarea where parentId = "+areaid+ ") ");
		if (StringUtils.isNotBlank(companyId)){
			sb.append(" and companyId = '"+companyId+"'");
		}
		if (createDateB !=0 && createDateE !=0) {
			sb.append(" and createTime > '"+ GetTime.getTimeByLong(createDateB)+"' and createTime < '"+ GetTime.getTimeByLong(createDateE)+"'");
		}
		if (updateDateB !=0 && updateDateE !=0){
			sb.append(" and updateTime > '"+ GetTime.getTimeByLong(updateDateB)+"' and updateTime < '"+ GetTime.getTimeByLong(updateDateE)+"'");
		}
		
		int count = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			if(rs.next()){
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return count;
	}

	@Override
	public List<Equipment> getEquipments(String mac, long createDateB,long createDateE, long updateDateB, long updateDateE, int pageNo,int pageSize,int areaid) {
		StringBuffer sb = new StringBuffer();
		int start = (pageNo - 1) * pageSize;
//		int end =  pageNo * pageSize;
		sb.append("select mac,named,s.deviceType,address,latitude,longitude,companyId,buildingId,producerCode,time,netstate,dt.deviceMiniType  from smoke s,deviceTypetrans dt  where 1=1 and s.deviceType=dt.deviceType and areaid  in (select areaid from areaidarea where parentId = "+areaid+ ") ");
		if (StringUtils.isNotBlank(mac)){
			sb.append(" and mac = '"+mac+"'");
		}
		if (createDateB !=0 && createDateE !=0) {
			sb.append(" and time > '"+ GetTime.getTimeByLong(createDateB)+"' and time < '"+ GetTime.getTimeByLong(createDateE)+"'");
		}
		if (updateDateB !=0 && updateDateE !=0){
			sb.append(" and updateTime > '"+ GetTime.getTimeByLong(updateDateB)+"' and updateTime < '"+ GetTime.getTimeByLong(updateDateE)+"'");
		}
		
		sb.append(" limit "+start+","+pageSize);
		
		List<Equipment> equipments = new ArrayList<Equipment>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()){
				Equipment eq = new Equipment();
				eq.setDeviceId(rs.getString("mac"));
				eq.setDeviceName(rs.getString("named"));
				int deviceType =rs.getInt("deviceType");
				//此处应该有设备类型转换
				eq.setDeviceType(rs.getInt("deviceMiniType")+"");
				eq.setDeviceMiniType(deviceType+"");
				
				eq.setAddress(rs.getString("address"));
				//百度地图坐标转成高德坐标
				LngLat lnglat =MapUtil.coordinateTrans("baidu","gaode",rs.getString("longitude"),rs.getString("latitude"));
				eq.setLat(lnglat.getLantitude()+"");
				eq.setLng(lnglat.getLongitude()+"");
				eq.setBuildingId(rs.getString("buildingId"));
				eq.setCompanyId(rs.getString("companyId"));
				eq.setInstallDate(rs.getString("time"));
				eq.setEquipmentStatus(rs.getString("netstate"));
				eq.setProducerCode(rs.getString("producerCode"));
				equipments.add(eq);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return equipments;
	}

	@Override
	public int getEquipmentCount(String mac, long createDateB, long createDateE,long updateDateB, long updateDateE,int areaid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(mac) from smoke where 1=1 and areaid in (select areaid from areaidarea where parentId = "+areaid+ ") ");
		if (StringUtils.isNotBlank(mac)){
			sb.append(" and mac = '"+mac+"'");
		}
		if (createDateB !=0 && createDateE !=0) {
			sb.append(" and time > '"+ GetTime.getTimeByLong(createDateB)+"' and time < '"+ GetTime.getTimeByLong(createDateE)+"'");
		}
		if (updateDateB !=0 && updateDateE !=0){
			sb.append(" and updateTime > '"+ GetTime.getTimeByLong(updateDateB)+"' and updateTime < '"+ GetTime.getTimeByLong(updateDateE)+"'");
		}
		
		int count = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			if (rs.next()){
				count =rs.getInt(1);
			}
		}catch (SQLException e) {
				e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return count;
	}

	@Override
	public List<StisEntity> getStatics(long createDateB, long createDateE,int pageNo, int pageSize,int areaid) {
		StringBuffer sb = new StringBuffer();
		int start = (pageNo - 1) * pageSize;
//		int end =  pageNo * pageSize;
		sb.append("select stisDate,s.buildingId,s.deviceNum,deviceOnlineNum,deviceOfflineNum,deviceFaultNum,deviceAlarmNum,deviceUseNum  from stas s,building_info b where 1=1 " );
		sb.append(" and s.buildingId = b.buildingId and b.areaid  in (select areaid from areaidarea where parentId = "+areaid+ ") ");
		if (createDateB !=0 && createDateE !=0) {
			sb.append(" and stisDate > '"+ GetTime.getTimeByLong(createDateB)+"' and stisDate < '"+ GetTime.getTimeByLong(createDateE)+"'");
		}
		
		sb.append("  limit "+start+","+pageSize);
		List<StisEntity> lstStis = new ArrayList<StisEntity>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				StisEntity se = new StisEntity();
				se.setStisDate(rs.getString("stisDate"));
				se.setBuildingId(rs.getString("buildingId"));
				se.setDeviceNum(rs.getInt("deviceNum"));
				se.setDeviceOnlineNum(rs.getInt("deviceOnlineNum"));
				se.setDeviceOfflineNum(rs.getInt("deviceOfflineNum"));
				se.setDeviceFaultNum(rs.getInt("deviceFaultNum"));
				se.setDeviceAlarmNum(rs.getInt("deviceAlarmNum"));
				se.setDeviceUseNum(rs.getInt("deviceUseNum"));
				lstStis.add(se);
			}
		}catch (SQLException e) {
				e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstStis;
	}

	@Override
	public int getStisCount(long createDateB, long createDateE,int areaid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(id)  from stas s,building_info b where 1=1 and s.buildingId = b.buildingId and b.areaid  in (select areaid from areaidarea where parentId = "+areaid+ ") " );
		if (createDateB !=0 && createDateE !=0) {
			sb.append(" and stisDate > '"+ GetTime.getTimeByLong(createDateB)+"' and stisDate < '"+ GetTime.getTimeByLong(createDateE)+"'");
		}
		int count = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			if (rs.next()){
				count = rs.getInt(1);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return count;
	}

	
	@Override
	public APIResult bindDeviceType(String deviceType, String deviceMiniType) {
		String sql = "insert into deviceTypeTrans(deviceType,deviceMiniType) values (?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		APIResult rs = new APIResult();
		try {
			ps.setString(1, deviceType);
			ps.setString(2, deviceMiniType);
			int count = ps.executeUpdate();
			if (count > 0) {
				rs.setCode(0);
				rs.setMessage("绑定成功");
			} else {
				rs.setCode(1);
				rs.setMessage("绑定失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	//按buildingId统计国瑞消防区域的设备总数，在线数，离线数，故障数，报警数，充电口使用次数
	public List<StisEntity> dailyStatics(){
		StringBuffer sb = new StringBuffer();
		sb.append("select count(mac),netState,buildingId from smoke where areaid in (select areaid from areaidarea where parentid in (select areaid from producercode)) group by buildingId,netState ");
		List<StisEntity> lstStis = new ArrayList<StisEntity>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			String temp = "";
			StisEntity  se = null;
			while (rs.next()){
				String buildingId = rs.getString(3);
				int count = rs.getInt(1);
				int netState = rs.getInt(2);
				if (StringUtils.isNotBlank(buildingId)&&(!temp.equals(buildingId))) {
					if (se != null) {
						se.setDeviceNum(se.getDeviceOnlineNum()+se.getDeviceOfflineNum());
						se.setDeviceAlarmNum(getAlarmCountByBuildingId(buildingId));  //获取报警统计
						se.setDeviceFaultNum(0); //获取故障统计
						se.setDeviceUseNum(0); //获取充电口使用统计
						lstStis.add(se);
					}
					se = new StisEntity();
					se.setBuildingId(buildingId);
					temp = buildingId;
				}
				if (se != null ){
					if (netState == 0){
						se.setDeviceOfflineNum(count);
					} else {
						se.setDeviceOnlineNum(count);
					}
				}
			}
			if (se != null) {
				se.setDeviceNum(se.getDeviceOnlineNum()+se.getDeviceOfflineNum());
				se.setDeviceAlarmNum(getAlarmCountByBuildingId(se.getBuildingId()));  //获取报警统计
				se.setDeviceFaultNum(0); //获取故障统计
				se.setDeviceUseNum(0); //获取充电口使用统计
				lstStis.add(se);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstStis;
	}

	private int getAlarmCountByBuildingId(String buildingId) {
		String sql = "select count(*) from (select id from alarm where alarmTime > ? and smokeMac in (select mac from smoke where  buildingId = ?) group by smokeMac)t";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		int alarmCount =0;
		try {
			ps.setString(1, GetTime.getTimeByLong(System.currentTimeMillis()- 24 * 60 * 60 * 1000));
			ps.setString(2, buildingId);
			rs = ps.executeQuery();
			while (rs.next()){
				alarmCount =rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return alarmCount;
	}
	
	@Override
	public void dailyCount(List<StisEntity> lstStis) {
		 String sql = "insert into stas(stisDate,buildingId,deviceNum,deviceOnlineNum,deviceOfflineNum,"
		    		+ "deviceFaultNum,deviceAlarmNum,deviceUseNum) values(?,?,?,?,?,?,?,?)";
	    Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try{
			conn.setAutoCommit(false);
		    for (StisEntity se : lstStis) {
				ps.setString(1, GetTime.ConvertTimeByLong());
				ps.setString(2,se.getBuildingId());
				ps.setInt(3, se.getDeviceNum());
				ps.setInt(4, se.getDeviceOnlineNum());
				ps.setInt(5, se.getDeviceOfflineNum());
				ps.setInt(6, se.getDeviceFaultNum());
				ps.setInt(7, se.getDeviceAlarmNum());
				ps.setInt(8, se.getDeviceUseNum());
				ps.addBatch();
			}
		    int[] executeBatch = ps.executeBatch();
		    conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	public  static boolean updateToken(String user,String token){
		boolean result =false;
		String sql ="update producercode set tokenId=? where user = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, token);
			ps.setString(2, user);
			int rs = ps.executeUpdate();
			if (rs > 0){
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public static int getAreaIdByToken(String token) {
		int areaid = 0;
		String sql = "select areaid from producercode where tokenId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, token);
			rs = ps.executeQuery();
			while (rs.next()){
				areaid = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return areaid;
	}

	@Override
	public APIResult verifyCompanyId(String companyId) {
		String sql = "select companyId from company_info where companyId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		APIResult ar = new APIResult();
		ar.setCode(0);
		ar.setMessage("companyId未被注册");
		try {
			ps.setString(1, companyId);
			rs = ps.executeQuery();
			while (rs.next()){
				ar.setCode(1);
				ar.setMessage("companyId已被注册");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ar;
	}

	@Override
	public Map<String, String> getAllBuilding(String areaId) {
		String sql="select buildingId,buildingName from building_Info where areaId = ?";
		Map<String,String> map =new HashMap<String,String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, areaId);
			rs =ps.executeQuery();
			while (rs.next()){
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return map;
	}
	@Override
	public Map<String, String> getAllCompany(String areaId) {
		String sql="select companyId,companyName from company_info where areaId = ?";
		Map<String,String> map =new HashMap<String,String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, areaId);
			rs =ps.executeQuery();
			while (rs.next()){
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return map;
	}
	
	@Override
	public Map<String, String> getAllBuilding() {
		String sql="select buildingId,buildingName from building_Info";
		Map<String,String> map =new HashMap<String,String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs =ps.executeQuery();
			while (rs.next()){
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return map;
	}
	@Override
	public Map<String, String> getAllCompany() {
		String sql="select companyId,companyName from company_info";
		Map<String,String> map =new HashMap<String,String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs =ps.executeQuery();
			while (rs.next()){
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return map;
	}

	@Override
	public APIResult verifyBuildingId(String buildingId) {
		String sql = "select buildingId from building_info where buildingId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		APIResult ar = new APIResult();
		ar.setCode(0);
		ar.setMessage("buildingId未被注册");
		try {
			ps.setString(1, buildingId);
			rs = ps.executeQuery();
			while (rs.next()){
				ar.setCode(1);
				ar.setMessage("buildingId已被注册");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ar;
	}

	@Override
	public Map<String, String> getAllBuildingType() {
		String sql="select building_type_id,building_type_name from building_type";
		Map<String,String> map =new HashMap<String,String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs =ps.executeQuery();
			while (rs.next()){
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return map;
	}

	@Override
	public Map<String, String> getAllCity() {
		String sql="select code,name from area_dj where level_code = 2";
		Map<String,String> map =new HashMap<String,String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs =ps.executeQuery();
			while (rs.next()){
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return map;
	}
	
	@Override
	public Map<String, String> getAllSystem() { //获取国瑞和伊特诺所有子区域
		String sql = "select areaid,area from areaidarea where parentId in (select areaid from producercode)";
//		String sql="select areaid,name from producercode ";
		Map<String,String> map =new HashMap<String,String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs =ps.executeQuery();
			while (rs.next()){
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return map;
	}

	@Override
	public Map<String, String> selectCountry(String cityCode) {
		String sql="select code,name from area_dj where level_code = 3 and parent='"+cityCode+"'";
		Map<String,String> map =new HashMap<String,String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs =ps.executeQuery();
			while (rs.next()){
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return map;
	}

	@Override
	public Map<String, String> selectTown(String countryCode) {
		String sql="select code,name from area_dj where level_code = 4 and parent="+countryCode;
		Map<String,String> map =new HashMap<String,String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs =ps.executeQuery();
			while (rs.next()){
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return map;
	}

	public static boolean existUser(String username, String pwd) {
		String sql = "select user from producercode where user=? and pwd=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		boolean exist = false;
		try {
			ps.setString(1, username);
			ps.setString(2, pwd);
			rs = ps.executeQuery();
			if (rs.next()) {
				exist = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return exist;
	}

	public static String getPwd(String usr) {
		String sql = "select pwd from producercode where user=? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		String  pwd = "";
		try {
			ps.setString(1, usr);
			rs = ps.executeQuery();
			if (rs.next()) {
				pwd = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		} 
		
		return pwd;
	}
}
