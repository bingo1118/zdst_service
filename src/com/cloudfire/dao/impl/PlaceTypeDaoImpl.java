package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.AreaIdEntity;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.NFCDeviceTypeEntity;
import com.cloudfire.entity.PlaceTypeEntity;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.StringUtil;

public class PlaceTypeDaoImpl implements PlaceTypeDao{

	public PlaceTypeEntity getAllShopType(String page) {
		// TODO Auto-generated method stub
		String pageSql = null;
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*20;
				int endNum =20;
				pageSql = new String("order by placeTypeId asc limit "+startNum+" , "+endNum);
			}
		}
		String loginSql =null;
		if(pageSql!=null){
			loginSql = "select placeTypeId,placeName from placetypeidplacename "+pageSql;
		}else{
			loginSql = "select placeTypeId,placeName from placetypeidplacename ";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		List<ShopTypeEntity> listShopType = null;
		PlaceTypeEntity pte = new PlaceTypeEntity();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(listShopType==null){
					listShopType = new ArrayList<ShopTypeEntity>();
				}
				ShopTypeEntity stp = new ShopTypeEntity();
				stp.setPlaceTypeId(rs.getString("placeTypeId"));
				stp.setPlaceTypeName(rs.getString("placeName"));
				listShopType.add(stp);
			}
			if(listShopType!=null&&listShopType.size()>0){
				pte.setErrorCode(0);
				pte.setError("获取商铺类型成功");
				pte.setPlaceType(listShopType);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return pte;
	}
	
	public PlaceTypeEntity getAllShopTypeByUserId(String userid) {
		// TODO Auto-generated method stub
		
		String loginSql =null;
		loginSql = "select p.placeTypeId,p.placeName from placetypeidplacename p,userid_placeid u where p.placeTypeId=u.placeId and u.userId="+userid;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		List<ShopTypeEntity> listShopType = null;
		PlaceTypeEntity pte = new PlaceTypeEntity();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(listShopType==null){
					listShopType = new ArrayList<ShopTypeEntity>();
				}
				ShopTypeEntity stp = new ShopTypeEntity();
				stp.setPlaceTypeId(rs.getString("placeTypeId"));
				stp.setPlaceTypeName(rs.getString("placeName"));
				listShopType.add(stp);
			}
			if(listShopType!=null&&listShopType.size()>0){
				pte.setErrorCode(0);
				pte.setError("获取商铺类型成功");
				pte.setPlaceType(listShopType);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return pte;
	}
	
	
	public AreaIdEntity getNeedAdministrationInfo(String type,String father_code) {
		// TODO Auto-generated method stub
		
		String loginSql =null;
		switch(type){
			case "1"://省级
				loginSql = "select code,name from province";
				break;
			case "2"://市级
				loginSql = "select code,name from city";
				if(!StringUtil.strIsNullOrEmpty(father_code)){
					loginSql=loginSql+" where province="+father_code;
				}
				break;
			case "3"://区级
				loginSql = "select code,name from town";
				if(!StringUtil.strIsNullOrEmpty(father_code)){
					loginSql=loginSql+" where city="+father_code;
				}
				break;
			case "4"://社区级
				loginSql = "select code,name from downtown";
				break;
			case "5"://单位级
				loginSql = "select fsocial_uuid,fsocial_name from administration_info";
				break;
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		List<AreaBean> listShopType = null;
		AreaIdEntity pte = new AreaIdEntity();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(listShopType==null){
					listShopType = new ArrayList<AreaBean>();
				}
				AreaBean stp = new AreaBean();
				if(type.equals("5")){
					stp.setAreaId(rs.getString("fsocial_uuid"));
					stp.setAreaName(rs.getString("fsocial_name"));
				}else{
					stp.setAreaId(rs.getString("code"));
					stp.setAreaName(rs.getString("name"));
				}
				listShopType.add(stp);
			}
			if(listShopType!=null&&listShopType.size()>0){
				pte.setErrorCode(0);
				pte.setError("获取成功");
				pte.setSmoke(listShopType);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return pte;
	}

	public Map<String, String> getShopTypeById() {
		// TODO Auto-generated method stub
		String loginSql = "select placeTypeId,placeName from placetypeidplacename ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		Map<String, String> map = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(map==null){
					map = new HashMap<String,String>();
				}
				String placeTypeId = rs.getString("placeTypeId");
				String placeName = rs.getString("placeName");
				map.put(placeTypeId, placeName);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return map;
	}
	
	@Override
	public NFCDeviceTypeEntity getAllDeviceType() {
		
		String loginSql =null;
		loginSql = "select * from nfc_device_type";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		List<ShopTypeEntity> listShopType = null;
		NFCDeviceTypeEntity pte = new NFCDeviceTypeEntity();
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(listShopType==null){
					listShopType = new ArrayList<ShopTypeEntity>();
				}
				ShopTypeEntity stp = new ShopTypeEntity();
				stp.setPlaceTypeId(rs.getString("devTypeId"));
				stp.setPlaceTypeName(rs.getString("devTypeName"));
				listShopType.add(stp);
			}
			if(listShopType!=null&&listShopType.size()>0){
				pte.setErrorCode(0);
				pte.setError("获取商铺类型成功");
				pte.setDeviceType(listShopType);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return pte;
	}

}
