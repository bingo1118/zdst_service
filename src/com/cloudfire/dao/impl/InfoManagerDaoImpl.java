package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.mortbay.jetty.Request;

import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.AreaIdEntity;
import com.cloudfire.entity.CompanyEntity;
import com.cloudfire.entity.MyDevicesEntityQuery;
import com.cloudfire.entity.PageBeanEntity;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.query.MainIndexEntityQuery;
import com.cloudfire.entity.query.TenanceEntityQuery;
import com.cloudfire.until.Utils;

public class InfoManagerDaoImpl implements InfoManagerDao {

	/**
	 * 根据建筑单位名称，单位性质，行业类型来查寻数据
	 */
	public List<CompanyEntity> getInfoAll(String companyName,
			String companyNature, String involed) {

		StringBuffer sqlstr = new StringBuffer();

		sqlstr.append("SELECT c.comanyName,c.adress,c.floorArea,c.person,c.phone,c.policeStation,a.area,c.involved,c.comanyNature,c.id ");
		sqlstr.append(" FROM company c,areaidarea a ");
		sqlstr.append(" WHERE c.areaId=a.areaId");

		if (companyName != null && !companyName.equals("")) {
			sqlstr.append(" and c.companyName =?");
		}

		if (companyNature != null && !companyNature.equals("")) {
			sqlstr.append(" and c.companyNature =?");
		}

		if ((involed != null) && (!companyNature.equals(""))) {
			sqlstr.append(" and c.involed =?");
		}

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<CompanyEntity> infoList = null;
		CompanyEntity info;
		try {

			int num = Utils.getNum(sqlstr.toString(), "?");
			if (num == 1) {
				ps.setString(1, companyName);
			}
			if (num == 2) {
				ps.setString(1, companyName);
				ps.setString(2, companyNature);
			}
			if (num == 3) {
				ps.setString(1, companyName);
				ps.setString(2, companyNature);
				ps.setString(3, involed);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (infoList == null) {
					infoList = new ArrayList<CompanyEntity>();
				}
				info = new CompanyEntity();
				info.setComanyName(rs.getString(1));
				info.setAdress(rs.getString(2));
				info.setFloorArea(rs.getString(3));
				info.setPerson(rs.getString(4));
				info.setPhone(rs.getString(5));
				info.setPoliceStation(rs.getString(6));
				info.setInvolved(rs.getString(8));
				info.setComanyNature(rs.getString(9));
				info.setId(rs.getInt(10));
				infoList.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return infoList;
	}

	/**
	 * 首页进入直接查询所以数据
	 */
	public List<CompanyEntity> getInfoAlls() {
		StringBuffer sqlstr = new StringBuffer();

		sqlstr.append("SELECT c.comanyName,c.adress,c.telephone,c.person,c.phone,c.id,c.characterId");
		sqlstr.append(" FROM company c ");
		sqlstr.append(" WHERE 1=1");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<CompanyEntity> infoList = null;
		CompanyEntity info;
		//System.out.println(sqlstr);
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				if (infoList == null) {
					infoList = new ArrayList<CompanyEntity>();
				}
				info = new CompanyEntity();
				info.setComanyName(rs.getString(1));
				info.setAdress(rs.getString(2));
				info.setTelephone(rs.getString(3));
				info.setPerson(rs.getString(4));
				info.setPhone(rs.getString(5));
				info.setId(rs.getInt(6));
				if(rs.getInt(7) == 1){
					info.setComanyNature("一般单位");
				}else if(rs.getInt(7) == 2){
					info.setComanyNature("重点单位");
				}else if(rs.getInt(7) == 3){
					info.setComanyNature("高危单位");
				}

				infoList.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return infoList;
	}

	/**
	 * 根据区域来查找单位信息
	 */

	public List<CompanyEntity> getInfoAll(String areaId,TenanceEntityQuery query) {
		StringBuffer sqlstr = new StringBuffer();
		String limit = " limit " + query.getStartRow()+","+query.getPageSize();
		sqlstr.append("SELECT DISTINCT c.comanyName,c.adress,c.telephone,c.person,c.phone,c.characterId,c.id");
		sqlstr.append(" FROM company c");
		sqlstr.append(" WHERE 1=1");
		
		if(areaId != "0" &&!"0".equals(areaId)){
			sqlstr.append(" AND c.areaId = "+areaId);
		}
		if(Utils.isNullStr(query.getComanyName())){
			sqlstr.append(" AND c.comanyName like '%"+query.getComanyName()+"%'");
		}
		if(query.getCharacterId()!=9){
			sqlstr.append(" AND c.characterId = "+query.getCharacterId());
		}

		sqlstr.append(limit);
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		
		ResultSet rs = null;
		List<CompanyEntity> infoList = null;
		CompanyEntity info;
		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				if (infoList == null) {
					infoList = new ArrayList<CompanyEntity>();
				}
				info = new CompanyEntity();
				info.setRow(++row);
				info.setComanyName(rs.getString(1));
				info.setAdress(rs.getString(2));
				info.setTelephone(rs.getString(3));
				info.setPerson(rs.getString(4));
				info.setPhone(rs.getString(5));
				if(rs.getInt(6) == 1){
					info.setComanyNature("一般单位");
				}else if(rs.getInt(6) == 2) {
					info.setComanyNature("重点单位");
				}else if(rs.getInt(6) == 3){
					info.setComanyNature("高危单位");
				}else if(rs.getInt(6) == 0) {
					info.setComanyNature("三小场所");
				}
				info.setId(rs.getInt(7));
				infoList.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return infoList;
	}
	
	public int getInfoByAreaId(String areaId,TenanceEntityQuery query) {
		StringBuffer sqlstr = new StringBuffer();
		int result = 0;
		sqlstr.append("SELECT COUNT(*)");
		sqlstr.append(" FROM company c");
		sqlstr.append(" WHERE 1=1");
		if(areaId != "0" &&!"0".equals(areaId)){
			sqlstr.append(" AND c.areaId = "+areaId);
		}
		if(Utils.isNullStr(query.getComanyName())){
			sqlstr.append(" AND c.comanyName like '%"+query.getComanyName()+"%'");
		}
		if(query.getCharacterId()!=9){
			sqlstr.append(" AND c.characterId = "+query.getCharacterId());
		}

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public List<SmokeBean> getAllSmokesInfo(String userId){
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT DISTINCT s.named,s.address,s.principal1,s.principal1Phone,s.placeTypeId,s.characterId,a.area ");
		sqlstr.append(" FROM smoke s,areaidarea a ,useridareaid u");
		sqlstr.append(" WHERE 1=1");
		sqlstr.append(" AND a.areaId=s.areaId");		
		sqlstr.append(" AND u.areaId=s.areaId");
		sqlstr.append(" AND u.areaId=a.areaId");
		sqlstr.append(" AND deviceType <11 ");
		if(Utils.isNullStr(userId)){
			sqlstr.append(" AND u.userId = "+userId);
		}
		
		sqlstr.append(" group by s.named,s.address ");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<SmokeBean> smokeList = null;
		SmokeBean sb;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				if (smokeList == null) {
					smokeList = new ArrayList<SmokeBean>();
				}
				sb = new SmokeBean();
				sb.setName(rs.getString("named"));
				sb.setAreaName(rs.getString("area"));
				sb.setAddress(rs.getString("address"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				
				
				if(rs.getInt(6) == 0){
					sb.setCharacterName("三小场所");
				}else if(rs.getInt(6) == 1){
					sb.setCharacterName("一般单位");
				}else if(rs.getInt(6) == 2){
					sb.setCharacterName("重点单位");
				}else if(rs.getInt(6) == 3){
					sb.setCharacterName("高危单位");
				}else{
					sb.setCharacterName("三小场所");
				}
				
				int placeid = rs.getInt("placeTypeId");
				
				switch(placeid){
					case 1:
						sb.setPlaceTypeName("烧烤");
						break;
					case 2:
						sb.setPlaceTypeName("住宅");
						break;
					case 3:
						sb.setPlaceTypeName("沐足");
						break;
					case 4:
						sb.setPlaceTypeName("餐饮店");
						break;
					case 5:
						sb.setPlaceTypeName("焊接");
						break;
					case 6:
						sb.setPlaceTypeName("工厂");
						break;
					case 7:
						sb.setPlaceTypeName("超市");
						break;
					case 8:
						sb.setPlaceTypeName("商场");
						break;
					case 9:
						sb.setPlaceTypeName("菜市场");
						break;
					case 10:
						sb.setPlaceTypeName("修理店");
						break;
					case 11:
						sb.setPlaceTypeName("五金建材店");
						break;
					case 12:
						sb.setPlaceTypeName("杂货铺");
						break;
					case 13:
						sb.setPlaceTypeName("网吧");
						break;
					case 14:
						sb.setPlaceTypeName("美容美发");
						break;
					case 15:
						sb.setPlaceTypeName("粮油食品店");
						break;
					default:
						sb.setPlaceTypeName("其他店");
				}
				
				smokeList.add(sb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return smokeList;
	}
	
	public List<SmokeBean> getAllSmokesByQuery(String areaId,MainIndexEntityQuery query){
		StringBuffer sqlstr = new StringBuffer();
		String limit = " limit " + query.getStartRow()+","+query.getPageSize();
		sqlstr.append("SELECT DISTINCT s.named,s.address,s.principal1,s.principal1Phone,s.placeTypeId,s.characterId,a.area,p.placeName,s.mac ");
		sqlstr.append(" FROM smoke s,areaidarea a , placetypeidplacename p");
		sqlstr.append(" WHERE 1=1");
		sqlstr.append(" AND a.areaId=s.areaId");		
		sqlstr.append(" AND p.placeTypeId=s.placeTypeId  ");
		
		List<String> areaids = query.getAreaIds();
		if(StringUtils.isNotBlank(areaId)&&!"0".equals(areaId)){
			sqlstr.append(" AND s.areaId = "+areaId);
		}else if(areaids!=null&&areaids.size()>0){
			if(query.getParentId()!=0){
				sqlstr.append(" AND a.parentId = "+query.getParentId());
			}
			
			sqlstr.append(" and s.areaId in(");
			for (int i = 0; i < areaids.size(); i++) {
				if(i==0){
					sqlstr.append(areaids.get(i));
				}else{
					sqlstr.append(","+areaids.get(i));
				}
			}
			sqlstr.append(")");
		}else if (areaids == null || areaids.size() == 0){
			sqlstr.append(" AND s.areaId = 0 ");
		}
		
		if(Utils.isNullStr(query.getName())){
			sqlstr.append(" AND s.named like '%"+query.getName()+"%'");
		}
		if(query.getCharacterId() != 9){
			if(query.getCharacterId() != 0){
				sqlstr.append(" AND s.characterId = "+query.getCharacterId());
			}else{
				sqlstr.append(" AND (s.characterId = "+query.getCharacterId()+" OR s.characterId IS NULL ) ");
			}
		}
		if(query.getPlaceTypeId() != "0" &&!"0".equals(query.getPlaceTypeId())&&Utils.isNullStr(query.getPlaceTypeId())){
			sqlstr.append(" AND s.placeTypeId = " + query.getPlaceTypeId());
		}
		
		sqlstr.append(" group by s.named,s.address ");
		sqlstr.append(limit);
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<SmokeBean> smokeList = null;
		SmokeBean sb;
		try {
			int row = query.getStartRow();
			rs = ps.executeQuery();
			while (rs.next()) {
				if (smokeList == null) {
					smokeList = new ArrayList<SmokeBean>();
				}
				sb = new SmokeBean();
				sb.setRow(++row);
				sb.setMac(rs.getString("mac"));
				sb.setName(rs.getString("named"));
				sb.setAreaName(rs.getString("area"));
				sb.setAddress(rs.getString("address"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				if(rs.getInt(6) == 0){
					sb.setCharacterName("三小场所");
				}else if(rs.getInt(6) == 1){
					sb.setCharacterName("一般单位");
				}else if(rs.getInt(6) == 2){
					sb.setCharacterName("重点单位");
				}else if(rs.getInt(6) == 3){
					sb.setCharacterName("高危单位");
				}else{
					sb.setCharacterName("三小场所");
				}
				sb.setPlaceTypeName(rs.getString("placeName"));
				sb.setPlaceTypeId(rs.getString("placeTypeId"));
				smokeList.add(sb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return smokeList;
	}
	
	public int getSmokeByQuery(String areaId,MainIndexEntityQuery query){
		StringBuffer sqlstr = new StringBuffer();
		int result = 0;
		sqlstr.append("SELECT count(*) from (");
		sqlstr.append("SELECT DISTINCT s.named,s.address,s.principal1,s.principal1Phone,s.placeTypeId,s.characterId,a.area ");
		sqlstr.append(" FROM smoke s,areaidarea a,placetypeidplacename p");
		sqlstr.append(" WHERE 1=1");
		sqlstr.append(" AND a.areaId=s.areaId");	
		sqlstr.append(" AND p.placeTypeId=s.placeTypeId");
		List<String> areaids = query.getAreaIds();
		
		if(StringUtils.isNotBlank(areaId)&&!"0".equals(areaId)){
			sqlstr.append(" AND s.areaId = "+areaId);
		}else if(areaids!=null&&areaids.size()>0){
			if(query.getParentId()!=0){
				sqlstr.append(" AND a.parentId = "+query.getParentId());
			}
			
			sqlstr.append(" and s.areaId in(");
			for (int i = 0; i < areaids.size(); i++) {
				if(i==0){
					sqlstr.append(areaids.get(i));
				}else{
					sqlstr.append(","+areaids.get(i));
				}
			}
			sqlstr.append(")");
		} else if (areaids == null || areaids.size() == 0){
			sqlstr.append(" AND s.areaId = 0 ");
		}
		
		if(Utils.isNullStr(query.getName())){
			sqlstr.append(" AND s.named like '%"+query.getName()+"%'");
		}
		if(query.getCharacterId() != 9){
			if(query.getCharacterId() != 0){
				sqlstr.append(" AND s.characterId = "+query.getCharacterId());
			}else {
				sqlstr.append(" AND (s.characterId = "+query.getCharacterId()+" OR s.characterId IS NULL)");
			}
		}
		if(query.getPlaceTypeId() != "0" &&!"0".equals(query.getPlaceTypeId())&&Utils.isNullStr(query.getPlaceTypeId())){
			sqlstr.append(" AND s.placeTypeId = " + query.getPlaceTypeId());
		}
		
		sqlstr.append(" group by s.named,s.address");
		sqlstr.append(") as ss");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return result;
	}

	/**
	 * 根据区域下面的物建单位来查数据
	 */
	public List<CompanyEntity> getInfoAll(String areaId, String registration) {
		StringBuffer sqlstr = new StringBuffer();

		sqlstr.append("SELECT c.comanyName,c.adress,c.floorArea,c.person,c.phone,c.policeStation,a.area,c.involved,c.comanyNature ");
		sqlstr.append(" FROM company c,areaidarea a ");
		sqlstr.append(" WHERE c.areaId=a.areaId");
		sqlstr.append(" AND a.areaId = ? AND registration = ?");

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<CompanyEntity> infoList = null;
		CompanyEntity info;
		try {
			ps.setString(1, areaId);
			ps.setString(2, registration);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (infoList == null) {
					infoList = new ArrayList<CompanyEntity>();
				}
				info = new CompanyEntity();
				info.setComanyName(rs.getString(1));
				info.setAdress(rs.getString(2));
				info.setFloorArea(rs.getString(3));
				info.setPerson(rs.getString(4));
				info.setPhone(rs.getString(5));
				info.setPoliceStation(rs.getString(6));
				info.setInvolved(rs.getString(8));
				info.setComanyNature(rs.getString(9));
				infoList.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return infoList;
	}
	
	public List<MyDevicesEntityQuery> getInfoAllCount(List<String> areaIds, MyDevicesEntityQuery query) {
		StringBuffer sqlstr = new StringBuffer();

		sqlstr.append("SELECT deviceType,mac,floors,storeys,positions,address,time,netState FROM smoke");
		sqlstr.append(" WHERE 1=1");
		String limit = " limit "+query.getStartRow() + ","+query.getPageSize();
		if(areaIds !=null&&areaIds.size()>0){
			for(int i = 0;i<areaIds.size();i++){
				if(i == 0){
					sqlstr.append(" AND areaId IN("+areaIds.get(0));
				}else if(i == areaIds.size()-1){
					sqlstr.append(","+areaIds.get(i)+") ");
				}else{
					sqlstr.append(","+areaIds.get(i));
				}
			}
		}
		
		if(Utils.isNullStr(query.getDevictType())){
			sqlstr.append(" AND  deviceType = " +query.getDevictType());
		}
		
		if(Utils.isNullStr(query.getDevMac())){
			sqlstr.append(" AND  mac = '" +query.getDevMac()+"'");
		}
		
		if(Utils.isNullStr(query.getNetStates())){
			sqlstr.append(" AND  netState = " +query.getNetStates());
		}
		
		if(Utils.isNullStr(query.getJ_xl_1())){
			sqlstr.append(" AND  time >= '" +query.getJ_xl_1()+" 00:00:00'");
		}
		
		if(Utils.isNullStr(query.getJ_xl_2())){
			sqlstr.append(" AND  time <= '" +query.getJ_xl_2()+" 23:59:59'");
		}
		
		if(Utils.isNullStr(query.getAreaId())){
			sqlstr.append("AND areaId = " + query.getAreaId());
		}
		
		
		
		sqlstr.append(limit);

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<MyDevicesEntityQuery> DevList = null;
		MyDevicesEntityQuery info;
		try {
			rs = ps.executeQuery();
			int row = query.getStartRow();
			while (rs.next()) {
				if (DevList == null) {
					DevList = new ArrayList<MyDevicesEntityQuery>();
				}
				info = new MyDevicesEntityQuery();
				info.setRow(++row);
				int deviceTypeInt = rs.getInt("deviceType");
				if(deviceTypeInt == 1){
					info.setDevName("烟感探测器");
				}else if(deviceTypeInt == 2){
					info.setDevName("燃气报警器");
				}else if(deviceTypeInt == 5){
					info.setDevName("电气火灾报警器");
				}else if(deviceTypeInt == 7){
					info.setDevName("声光报警器");
				}else if(deviceTypeInt == 8){
					info.setDevName("手动报警器");
				}else if(deviceTypeInt == 9){
					info.setDevName("三江设备");
				}else if(deviceTypeInt == 10){
					info.setDevName("水压");
				}else if(deviceTypeInt == 11){
					info.setDevName("红外线");
				}else if(deviceTypeInt == 12){
					info.setDevName("门磁");
				}else if(deviceTypeInt == 13){
					info.setDevName("环境探测器");
				}else if(deviceTypeInt == 14){
					info.setDevName("GRPS");
				}else if(deviceTypeInt == 15){
					info.setDevName("水浸");
				}else if(deviceTypeInt == 18){
					info.setDevName("喷淋");
				}else{
					info.setDevName("其他");
				}
				info.setDevMac(rs.getString("mac"));
				info.setFloor(rs.getString("floors"));
				info.setStoreys(rs.getString("storeys"));
				info.setPosition(rs.getString("positions"));
				info.setAddress(rs.getString("address"));
				info.setTime(rs.getString("time"));
				if(rs.getInt("netState") == 1){
					info.setNetStates("在线");
				}else{
					info.setNetStates("掉线");
				}
				
				DevList.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return DevList;
	}
	
	public int getInfoCount(List<String> areaIds, MyDevicesEntityQuery query) {
		StringBuffer sqlstr = new StringBuffer();
		int result = 0;
		sqlstr.append("SELECT deviceType,mac,floors,storeys,positions,address,time,netState FROM smoke");
		sqlstr.append(" WHERE 1=1");
		if(areaIds !=null&&areaIds.size()>0){
			for(int i = 0;i<areaIds.size();i++){
				if(i == 0){
					sqlstr.append(" AND areaId IN("+areaIds.get(0));
				}else if(i == areaIds.size()-1){
					sqlstr.append(","+areaIds.get(i)+") ");
				}else{
					sqlstr.append(","+areaIds.get(i));
				}
			}
		}
		
		if(Utils.isNullStr(query.getDevictType())){
			sqlstr.append(" AND  deviceType = " +query.getDevictType());
		}
		
		if(Utils.isNullStr(query.getDevMac())){
			sqlstr.append(" AND  mac = '" +query.getDevMac()+"'");
		}
		
		if(Utils.isNullStr(query.getNetStates())){
			sqlstr.append(" AND  netState = " +query.getNetStates());
		}
		
		if(Utils.isNullStr(query.getJ_xl_1())){
			sqlstr.append(" AND  time >= '" +query.getJ_xl_1()+" 00:00:00'");
		}
		
		if(Utils.isNullStr(query.getJ_xl_2())){
			sqlstr.append(" AND  time <= '" +query.getJ_xl_2()+" 23:59:59'");
		}
		
		if(Utils.isNullStr(query.getAreaId())){
			sqlstr.append("AND areaId = " + query.getAreaId());
		}
		

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlstr.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				result++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public int getTotalCount(String areaId){
		String sql = "SELECT COUNT(*) FROM smoke where 1 = 1 ";
		if(Utils.isNullStr(areaId)&&areaId!="0"&&!"0".equals(areaId)){
			sql  = sql + " AND areaId = " + areaId;
		}
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		ResultSet rs = null;
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}

	public List<SmokeBean> getAllSmokes(String userId,int privilege) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT DISTINCT s.named,s.address,s.principal1,s.principal1Phone,p.placeTypeId,p.placeName,c.characterName,a.area ");
		sqlstr.append(" FROM smoke s,placetypeidplacename p,characters c,areaidarea a ,USER u,useridareaid ud");
		sqlstr.append(" WHERE s.placeTypeId=p.placeTypeId");
		sqlstr.append(" AND c.id = s.characterId");
		sqlstr.append(" AND a.areaId=s.areaId");		
		sqlstr.append(" AND u.userId = ud.userId");
		sqlstr.append(" AND a.areaId=s.areaId");
		sqlstr.append(" AND ud.areaId = a.areaId");
		if(privilege != 4){
			sqlstr.append(" AND u.userId = ?");
		}
//		sqlstr.append("order by s.named");
//		sqlstr.append(" LIMIT 100");
				
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<SmokeBean> smokeList = null;
		List<ShopTypeEntity> shopList = null;
		SmokeBean sb;
		ShopTypeEntity se;
		//System.out.println(sqlstr.toString());
		try {
			
			if(privilege != 4) ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (smokeList == null) {
					smokeList = new ArrayList<SmokeBean>();
				}
				if (shopList == null) {
					shopList = new ArrayList<ShopTypeEntity>();
				}
				sb = new SmokeBean();
				sb.setName(rs.getString(1));
				sb.setAreaName(rs.getString(8));
				sb.setAddress(rs.getString(2));
				sb.setPrincipal1(rs.getString(3));
				sb.setPrincipal1Phone(rs.getString(4));
				se = new ShopTypeEntity();
				se.setPlaceTypeId(rs.getString(5));
				sb.setPlaceTypeName(rs.getString(6));
				sb.setCharacterName(rs.getString(7));
				
				smokeList.add(sb);
				shopList.add(se);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return smokeList;
	}
	
	public List<SmokeBean> getAllSmokes(PageBeanEntity pageBean,String userId,String areaId) {
		StringBuffer sqlstr = new StringBuffer();
		String hql = "SELECT DISTINCT s.named,s.address,s.principal1,s.principal1Phone,p.placeTypeId,p.placeName,c.characterName,a.area ";
		sqlstr.append(" FROM smoke s,placetypeidplacename p,characters c,areaidarea a ,useridareaid u");
		sqlstr.append(" WHERE s.placeTypeId=p.placeTypeId");
		sqlstr.append(" AND c.id = s.characterId");
		sqlstr.append(" AND a.areaId=s.areaId");		
		sqlstr.append(" AND u.areaId = a.areaId");
		if(Utils.isNullStr(userId)){
			sqlstr.append(" AND u.userId = "+ userId);
		}
		if(Utils.isNullStr(areaId)){
			sqlstr.append(" AND a.area = " + areaId);
		}
		int totalCount = Utils.getTotalCount(hql+sqlstr.toString());
		pageBean.setTotalCount(totalCount);
		int totalPage = 0;
		if(totalCount % 10 == 0){
			totalPage = totalCount/10 ;
		}else{
			totalPage = totalCount/10+1;
		}
		pageBean.setTotalPage(totalPage);
		if(pageBean.getCurrentPage() <=0){
			pageBean.setCurrentPage(1);
		}else if(pageBean.getCurrentPage()>=pageBean.getTotalPage()){
			pageBean.setCurrentPage(totalPage);
		}
		
		int currentPage = pageBean.getCurrentPage();
		int index = (currentPage -1)* pageBean.getPageCount();    		//从起始行开始
		int count = pageBean.getPageCount();							//显示记录条数

		
		
		sqlstr.append(" limit ?,?");
		
		hql = hql + sqlstr;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,hql);
		ResultSet rs = null;
		List<SmokeBean> smokeList = null;
		SmokeBean sb;
		//System.out.println(sqlstr.toString());
		try {
			ps.setInt(1, index);
			ps.setInt(2, count);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (smokeList == null) {
					smokeList = new ArrayList<SmokeBean>();
				}
				
				sb = new SmokeBean();
				sb.setName(rs.getString(1));
				sb.setAreaName(rs.getString(8));
				sb.setAddress(rs.getString(2));
				sb.setPrincipal1(rs.getString(3));
				sb.setPrincipal1Phone(rs.getString(4));
				sb.setPlaceTypeId(rs.getString(5));
				sb.setPlaceTypeName(rs.getString(6));
				sb.setCharacterName(rs.getString(7));
				smokeList.add(sb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smokeList;
	}

	

	public List<SmokeBean> getSmokesByAreaId(String areaId,PageBeanEntity pageBean,String userId) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT DISTINCT s.named,s.address,s.principal1,s.principal1Phone,p.placeTypeId,p.placeName,c.characterName,a.area ");
		sqlstr.append(" FROM smoke s,placetypeidplacename p,characters c,areaidarea a ,useridareaid u");
		sqlstr.append(" WHERE s.placeTypeId=p.placeTypeId");
		sqlstr.append(" AND c.id = s.characterId");
		sqlstr.append(" AND u.areaid = a.areaid");
		sqlstr.append(" AND a.areaId=s.areaId");
		
		if(Utils.isNullStr(userId)){
			sqlstr.append(" AND u.userid = "+userId);
		}
		
		sqlstr.append(" AND s.areaId="+areaId);
		
		int totalCount = Utils.getTotalCount(sqlstr.toString());
		pageBean.setTotalCount(totalCount);
		int totalPage = 0;
		if(totalCount % 10 == 0){
			totalPage = totalCount/10 ;
		}else{
			totalPage = totalCount/10+1;
		}
		pageBean.setTotalPage(totalPage);
		if(pageBean.getCurrentPage() <=0){
			pageBean.setCurrentPage(1);
		}else if(pageBean.getCurrentPage()>=pageBean.getTotalPage()){
			pageBean.setCurrentPage(totalPage);
		}
		
		int currentPage = pageBean.getCurrentPage();
		int index = (currentPage -1)* pageBean.getPageCount();    		//从起始行开始
		int count = pageBean.getPageCount();							//显示记录条数
		
		sqlstr.append(" limit ?,?");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<SmokeBean> smokeList = null;
		SmokeBean sb;
		try {
			ps.setInt(1, index);
			ps.setInt(2, count);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (smokeList == null) {
					smokeList = new ArrayList<SmokeBean>();
				}
				sb = new SmokeBean();
				sb.setName(rs.getString(1));
				sb.setAreaName(rs.getString(8));
				sb.setAddress(rs.getString(2));
				sb.setPrincipal1(rs.getString(3));
				sb.setPrincipal1Phone(rs.getString(4));
				sb.setPlaceTypeId(rs.getString(5));
				sb.setPlaceTypeName(rs.getString(6));
				sb.setCharacterName(rs.getString(7));
				sb.setAreaId(Integer.parseInt(areaId));
				smokeList.add(sb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smokeList;
	}

	public List<SmokeBean> getSmokeByCharacterId(String characterId) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT s.named,s.address,s.principal1,s.principal1Phone,p.placeTypeId,p.placeName,c.characterName,a.area ");
		sqlstr.append(" FROM smoke s,placetypeidplacename p,characters c,areaidarea a ");
		sqlstr.append(" WHERE s.placeTypeId=p.placeTypeId");
		sqlstr.append(" AND c.id = s.characterId");
		sqlstr.append(" AND a.areaId=s.areaId");
		sqlstr.append(" AND s.characterId=?");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<SmokeBean> smokeList = null;
		List<ShopTypeEntity> shopList = null;
		SmokeBean sb;
		ShopTypeEntity se;
		//System.out.println(sqlstr);
		try {
			ps.setString(1, characterId);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (smokeList == null) {
					smokeList = new ArrayList<SmokeBean>();
				}
				if (shopList == null) {
					shopList = new ArrayList<ShopTypeEntity>();
				}
				sb = new SmokeBean();
				sb.setName(rs.getString(1));
				sb.setAreaName(rs.getString(8));
				sb.setAddress(rs.getString(2));
				sb.setPrincipal1(rs.getString(3));
				sb.setPrincipal1Phone(rs.getString(4));
				se = new ShopTypeEntity();
				se.setPlaceTypeId(rs.getString(5));
				sb.setPlaceTypeName(rs.getString(6));
				sb.setCharacterName(rs.getString(7));
				
				smokeList.add(sb);
				shopList.add(se);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smokeList;
	}

	@Override
	public List<SmokeBean> getAllSmokes(String userId) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT s.named,s.address,s.principal1,s.principal1Phone,p.placeTypeId,p.placeName,c.characterName,a.area ");
		sqlstr.append(" FROM smoke s,placetypeidplacename p,characters c,areaidarea a ,USER u,useridareaid ud");
		sqlstr.append(" WHERE s.placeTypeId=p.placeTypeId");
		sqlstr.append(" AND c.id = s.characterId");
		sqlstr.append(" AND a.areaId=s.areaId");		
		sqlstr.append(" AND u.userId = ud.userId");
		sqlstr.append(" AND a.areaId=s.areaId");
		sqlstr.append(" AND ud.areaId = a.areaId");
			sqlstr.append(" AND u.userId = ?");
				
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				sqlstr.toString());
		ResultSet rs = null;
		List<SmokeBean> smokeList = null;
		List<ShopTypeEntity> shopList = null;
		SmokeBean sb;
		ShopTypeEntity se;
		//System.out.println(sqlstr.toString());
		try {
			
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (smokeList == null) {
					smokeList = new ArrayList<SmokeBean>();
				}
				if (shopList == null) {
					shopList = new ArrayList<ShopTypeEntity>();
				}
				sb = new SmokeBean();
				sb.setName(rs.getString(1));
				sb.setAreaName(rs.getString(8));
				sb.setAddress(rs.getString(2));
				sb.setPrincipal1(rs.getString(3));
				sb.setPrincipal1Phone(rs.getString(4));
				se = new ShopTypeEntity();
				se.setPlaceTypeId(rs.getString(5));
				sb.setPlaceTypeName(rs.getString(6));
				sb.setCharacterName(rs.getString(7));
				
				smokeList.add(sb);
				shopList.add(se);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return smokeList;
	}

	@Override
	public List<SmokeBean> getSmokeByIdAndName(String provinceID,
			String placeTypeId, String companyName,PageBeanEntity pageBean) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT s.named,s.address,s.principal1,s.principal1Phone,p.placeTypeId,p.placeName,c.characterName,a.area ");
		sqlstr.append(" FROM smoke s,placetypeidplacename p,characters c,areaidarea a ");
		sqlstr.append(" WHERE s.placeTypeId=p.placeTypeId");
		sqlstr.append(" AND c.id = s.characterId");
		sqlstr.append(" AND a.areaId=s.areaId");
		if(provinceID != "9" &&!"9".equals(provinceID)){
			sqlstr.append(" AND s.characterId= " +provinceID);
		}
		if(placeTypeId != "0" &&!"0".equals(placeTypeId)){
			sqlstr.append(" AND p.placeTypeId = "+placeTypeId);
		}
		if(Utils.isNullStr(companyName)){
			sqlstr.append(" AND s.named like  '%"+companyName+"%'");
		}
		String hqlstr = sqlstr.toString();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,hqlstr);
		ResultSet rs = null;
		List<SmokeBean> smokeList = null;
		List<ShopTypeEntity> shopList = null;
		
		ShopTypeEntity se;
		//System.out.println(sqlstr);
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				if (smokeList == null) {
					smokeList = new ArrayList<SmokeBean>();
				}
				if (shopList == null) {
					shopList = new ArrayList<ShopTypeEntity>();
				}
				SmokeBean sb = new SmokeBean();
				sb.setName(rs.getString("named"));
				sb.setAreaName(rs.getString("area"));
				sb.setAddress(rs.getString("address"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				
//				se = new ShopTypeEntity();
//				se.setPlaceTypeId(rs.getString(5));
				sb.setPlaceTypeName(rs.getString("placeName"));
				sb.setCharacterName(rs.getString("characterName"));
				
				smokeList.add(sb);
//				shopList.add(se);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smokeList;
	}
	
	@Override
	public List<SmokeBean> getSmokeByIdAndName(String provinceID,
			String placeTypeId, String companyName,int areaId,PageBeanEntity pageBean) {
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT DISTINCT s.named,s.address,s.principal1,s.principal1Phone,p.placeTypeId,p.placeName,c.characterName,a.area  ");
		sqlstr.append(" FROM smoke s,placetypeidplacename p,characters c,areaidarea a ,useridareaid u ");
		sqlstr.append(" WHERE s.placeTypeId=p.placeTypeId ");
		sqlstr.append(" AND c.id = s.characterId ");
		sqlstr.append(" AND a.areaId=s.areaId ");
		sqlstr.append(" AND u.areaId = a.areaId");
		if(areaId !=0&&!"0".equals(areaId)){
			sqlstr.append(" AND s.areaId = "+areaId);
		}
		if(provinceID != "9" &&!"9".equals(provinceID)){
			sqlstr.append(" AND s.characterId= "+provinceID);
		}
		if(placeTypeId != "0" &&!"0".equals(placeTypeId)){
			sqlstr.append(" AND p.placeTypeId = " + placeTypeId);
		}
		if(Utils.isNullStr(companyName)){
			sqlstr.append(" AND s.named like  '%"+companyName+"%'");
		}
		
		int currentPage = pageBean.getCurrentPage();
		int index = (currentPage -1)* pageBean.getPageCount();    		//从起始行开始
		int count = pageBean.getPageCount();							//显示记录条数
		int totalCount = Utils.getTotalCount(sqlstr.toString());
		pageBean.setTotalCount(totalCount);
		
		sqlstr.append(" limit ?,?");
		
		String hqlstr = sqlstr.toString();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,hqlstr);
		ResultSet rs = null;
		List<SmokeBean> smokeList = null;
		List<ShopTypeEntity> shopList = null;
		ShopTypeEntity se;
		try {
			ps.setInt(1, index);
			ps.setInt(2, count);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (smokeList == null) {
					smokeList = new ArrayList<SmokeBean>();
				}
				if (shopList == null) {
					shopList = new ArrayList<ShopTypeEntity>();
				}
				SmokeBean sb = new SmokeBean();
				sb.setName(rs.getString("named"));
				sb.setAreaName(rs.getString("area"));
				sb.setAddress(rs.getString("address"));
				sb.setPrincipal1(rs.getString("principal1"));
				sb.setPrincipal1Phone(rs.getString("principal1Phone"));
				
//				se = new ShopTypeEntity();
//				se.setPlaceTypeId(rs.getString(5));
				sb.setPlaceTypeName(rs.getString("placeName"));
				sb.setCharacterName(rs.getString("characterName"));
				sb.setAreaId(areaId);
				smokeList.add(sb);
//				shopList.add(se);
			}
			
			if(pageBean.getTotalCount()%10==0){
				pageBean.setTotalPage(pageBean.getTotalCount()/10);
			}else pageBean.setTotalPage(pageBean.getTotalCount()/10 +1);				//設置總頁數
			SmokeBean sb = new SmokeBean();
			sb.setTotalCount(pageBean.getTotalCount());
			sb.setCurrentPage(currentPage);
			sb.setTotalPage(pageBean.getTotalPage());
			sb.setAreaId(areaId);
			smokeList.add(sb);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return smokeList;
	}

	@Override
	public List<ShopTypeEntity> getShopTypeEntity() {
		String sqlstr = "SELECT placeTypeId,placeName FROM placetypeidplacename ORDER BY placeName";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		List<ShopTypeEntity> shopList = new ArrayList<ShopTypeEntity>();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				ShopTypeEntity se = new ShopTypeEntity();
				se.setPlaceTypeId(rs.getString("placeTypeId"));
				se.setPlaceTypeName(rs.getString("placeName"));
				shopList.add(se);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return shopList;
	}

	@Override
	public List<CompanyEntity> getInfoByProvinceID(String provinceID,
			String companyName,String userId) {
		StringBuffer bs = new StringBuffer();
		bs.append("SELECT DISTINCT c.id, c.telephone, c.comanyName,c.adress,c.floorArea,c.person,c.phone,c.policeStation,c.involved,c.comanyNature,c.areaId,c.characterId ");
		bs.append(" FROM company c,useridareaid u");
		bs.append(" WHERE 1= 1");
		bs.append(" AND u.areaId = c.areaId ");
		
		
		if(Utils.isNullStr(provinceID)){
			bs.append(" AND characterId = " + provinceID);
		}
		if(Utils.isNullStr(companyName)){
			bs.append(" AND comanyName like '%"+companyName+"%'");
		}
		
		String hqlstr = bs.toString();
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, hqlstr);
		ResultSet rs = null;
		List<CompanyEntity> compList = new ArrayList<CompanyEntity>();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				CompanyEntity ce = new CompanyEntity();
				ce.setComanyName(rs.getString("comanyName"));
				ce.setAdress(rs.getString("adress"));
				ce.setFloorArea(rs.getString("floorArea"));
				ce.setPhone(rs.getString("phone"));
				ce.setPerson(rs.getString("person"));
				ce.setInvolved(rs.getString("involved"));
				ce.setTelephone(rs.getString("telephone"));
				ce.setId(rs.getInt("id"));
				ce.setCharacterId(rs.getInt("characterId"));
				if(rs.getInt("characterId") == 1){
					ce.setComanyNature("一般单位");
				}else if(rs.getInt("characterId") == 2){
					ce.setComanyNature("重点单位");
				}else if(rs.getInt("characterId") == 3){
					ce.setComanyNature("高危单位");
				}
				compList.add(ce);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
 
		return compList;
	}

}
