package com.cloudfire.dao.impl;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AllAreaEntity;
import com.cloudfire.entity.Area;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.AreaBeanEntity;
import com.cloudfire.entity.AreaIdEntity;
import com.cloudfire.entity.AreaParentEntity;
import com.cloudfire.entity.AreaidGoEasy;
import com.cloudfire.entity.City;
import com.cloudfire.entity.ParentArea;
import com.cloudfire.entity.Province;
import com.cloudfire.entity.PushAlarmMqttEntity;
import com.cloudfire.entity.RepeaterBean;
import com.cloudfire.entity.RepeaterLevelUp;
import com.cloudfire.entity.SecondArea;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.SmokeXYZ;
import com.cloudfire.entity.Town;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;

public class AreaDaoImpl implements AreaDao {
	
	/**
	 * 根据设备mac获取所属区域
	 * @param mac
	 * @return
	 */
	public String getAreaIdByMac(String mac){
		String areaid="";
		String sql="select areaId from smoke where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs=ps.executeQuery();
			if(rs.next()){
				areaid=rs.getString(1);
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
	
	public int getAreaIdBySmokeMac(String mac){
		int areaid=0;
		String sql="select areaId from smoke where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs=ps.executeQuery();
			if(rs.next()){
				areaid=rs.getInt(1);
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

	/**
	 * 获取权限不为4的areaids
	 * @param userId
	 * @return
	 */
	public List<String> getAreaIdsByUserId(String userId){
		List<String> areaIds=new ArrayList<String>();
		String sql="select areaid from useridareaid ua,user u where ua.userId=u.userId and u.privilege !=4 and u.userId=?";
		if(userId.equals("17484023")||userId.equals("hanrun")){
			sql="select u.areaid from useridareaid u where u.userId=? or 1=1 group by u.areaid ";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, userId);
			rs=ps.executeQuery();
			while(rs.next()){
				String areaId=rs.getString(1);
				areaIds.add(areaId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return areaIds;
	}
	
	public Map<Integer, String> getAreaById(List<String> listNum) {
		StringBuffer strSql = new StringBuffer();
		int len = listNum.size();
		if (len == 1) {
			strSql.append(" where areaId in (?)");
		} else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					strSql.append(" where areaId in (?, ");
				} else if (i == (len - 1)) {
					strSql.append(" ?)");
				} else {
					strSql.append(" ?, ");
				}
			}
		}
		String loginSql = new String("select areaId,area from areaidarea ");
		String sqlStr = new String(loginSql + strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlStr);
		ResultSet rs = null;
		Map<Integer, String> map = null;
		try {
			for (int i = 1; i <= len; i++) {
				ps.setInt(i, Integer.parseInt(listNum.get(i - 1)));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (map == null) {
					map = new HashMap<Integer, String>();
				}
				map.put(rs.getInt("areaId"), rs.getString("area"));
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

	// @@liangbin 2017.9.11
	public Map<Integer, String> getAreaById2(List<String> listNum, int type) {
		StringBuffer strSql = new StringBuffer();
		int len = listNum.size();
		if (type == 1) {
			strSql.append(" where areaId in (SELECT areaid FROM areaidarea WHERE parentId = ?)");
		} else {
			strSql.append(" where areaId in (?)");
		}

		String loginSql = new String("select areaId,area from areaidarea ");
		String sqlStr = new String(loginSql + strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlStr);
		ResultSet rs = null;
		Map<Integer, String> map = null;
		try {
			for (int i = 1; i <= len; i++) {
				ps.setInt(i, Integer.parseInt(listNum.get(i - 1)));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (map == null) {
					map = new HashMap<Integer, String>();
				}
				map.put(rs.getInt("areaId"), rs.getString("area"));
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
	
	public List<String> getAreaStr(String parentId){
		String sqlstr = "select areaId from areaidarea WHERE parentId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		List<String> areaList = new ArrayList<String>();
		try {
			ps.setString(1, parentId);
			rs = ps.executeQuery();
			while(rs.next()){
				areaList.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return areaList;
	}

	public List<String> getAreaStr(String userId, String privilege) {
		String loginSql = null;
		int priv = Integer.parseInt(privilege);
		switch (priv) {
		case 4:
			loginSql = "select areaId from areaidarea group by areaId";
			break;
		case 3:
		case 2:
		case 5:
			loginSql = "select areaId from useridareaid where userId = ? group by areaId";
			break;
		case 1:
			break;
		case 10:
			loginSql = "select areaId from areaidarea limit 1 ";
			break;
		default:
			loginSql = "select areaId from useridareaid where userId = ? group by areaId";
			break;
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, loginSql);
		ResultSet rs = null;
		List<String> list = null;
		try {
			if ((priv != 4)&&(priv !=10)) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (list == null) {
					list = new ArrayList<String>();
				}
				list.add(rs.getInt("areaId") + "");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	public List<String> getAreaStr(String userId, String privilege,int[] devices) {
		StringBuffer loginSql = new StringBuffer();
		int priv = Integer.parseInt(privilege);
		switch (priv) {
		case 4:
			loginSql.append("select areaId from smoke where deviceType in(124)");
			break;
		case 3:
		case 2:
		case 5:
			loginSql.append("select areaId from useridareaid where userId = ? group by areaId");
			break;
		case 1:
			break;
		case 10:
			loginSql.append("select areaId from areaidarea limit 1 ");
			break;
		default:
			break;
		}
		for (int i = 0; i < devices.length; i++) {
			
		}
		loginSql.append(" group by areaId ");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, loginSql.toString());
		ResultSet rs = null;
		List<String> list = null;
		try {
			if ((priv != 4)&&(priv !=10)) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (list == null) {
					list = new ArrayList<String>();
				}
				list.add(rs.getInt("areaId") + "");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	

	public List<String> getAreaStrByMac(List<String> listMac) {
		StringBuffer strSql = new StringBuffer();
		if (listMac == null) {
			return null;
		}
		int len = listMac.size();
		if (len == 1) {
			strSql.append(" where mac in (?)");
		} else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					strSql.append(" where mac in (?, ");
				} else if (i == (len - 1)) {
					strSql.append(" ?)");
				} else {
					strSql.append(" ?, ");
				}
			}
		}
		String loginSql = new String("select areaId from smoke ");
		String sqlStr = new String(loginSql + strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlStr);
		ResultSet rs = null;
		List<String> list = null;
		try {
			for (int i = 1; i <= len; i++) {
				ps.setString(i, listMac.get(i - 1));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (list == null) {
					list = new ArrayList<String>();
				}
				list.add(rs.getInt("areaId") + "");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public AreaIdEntity getAreaByUserId(String userId, String privilege,
			String page) {
		List<String> areaNum = getAreaStr(userId, privilege);
		if (areaNum == null) {
			return null;
		}
		String pageSql = null;
		if (page != null && page.length() > 0) {
			int pageInt = Integer.parseInt(page);
			if (pageInt > 0) {
				int startNum = (pageInt - 1) * 20;
				int endNum = 20;
				pageSql = new String("order by areaId asc limit " + startNum
						+ " , " + endNum);
			}
		}
		StringBuffer strSql = new StringBuffer();
		int len = areaNum.size();
		if (len == 1) {
			strSql.append(" where areaId in (?)");
		} else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					strSql.append(" where areaId in (?, ");
				} else if (i == (len - 1)) {
					strSql.append(" ?)");
				} else {
					strSql.append(" ?, ");
				}
			}
		}
		String loginSql = new String("select areaId,area from areaidarea ");
		String sqlStr = null;
		if (pageSql != null) {
			sqlStr = new String(loginSql + strSql.toString()
					+ pageSql.toString());
		} else {
			sqlStr = new String(loginSql + strSql.toString());
		}

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlStr);
		ResultSet rs = null;
		AreaIdEntity aie = null;
		try {
			for (int i = 1; i <= len; i++) {
				ps.setInt(i, Integer.parseInt(areaNum.get(i - 1)));
			}
			rs = ps.executeQuery();
			List<AreaBean> list = null;
			while (rs.next()) {
				if (aie == null) {
					aie = new AreaIdEntity();
					list = new ArrayList<AreaBean>();
				}
				AreaBean ab = new AreaBean();
				ab.setAreaId(rs.getInt("areaId") + "");
				ab.setAreaName(rs.getString("area"));
				list.add(ab);
			}
			if (aie != null) {
				aie.setSmoke(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aie;
	}

	
	
	@Override
	public AreaIdEntity getDXAreaId(String userId, String privilege, String page) {
		String sql ="select a.areaid,a.area  from producercode p,areaidarea a where p.areaId = a.parentId";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		AreaIdEntity aie = null;
		try {
			rs = ps.executeQuery();
			List<AreaBean> list = null;
			while (rs.next()) {
				if (aie == null) {
					aie = new AreaIdEntity();
					list = new ArrayList<AreaBean>();
				}
				AreaBean ab = new AreaBean();
				ab.setAreaId(rs.getInt("areaId") + "");
				ab.setAreaName(rs.getString("area"));
				list.add(ab);
			}
			if (aie != null) {
				aie.setSmoke(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aie;
	}

	public String getOneAreaName(int areaId) {
		String loginSql = new String(
				"select area from areaidarea where areaId=?");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, loginSql);
		ResultSet rs = null;
		String areaName = null;
		try {
			ps.setInt(1, areaId);
			rs = ps.executeQuery();
			while (rs.next()) {
				areaName = rs.getString("area");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return areaName;
	}

	public Map<Integer, String> getAllArea(String userid) {
		StringBuffer loginSql = new StringBuffer(
				"SELECT s.areaId,a.area from smoke s,areaidarea a,useridareaid u where s.areaId = a.areaId and u.areaId = a.areaid ");
		if (userid != null && userid != "") {
			loginSql.append(" and u.userid = '" + userid+"'");
		}
		loginSql.append(" GROUP BY s.areaId ");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,
				loginSql.toString());
		ResultSet rs = null;
		Map<Integer, String> map = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				if (map == null) {
					map = new HashMap<Integer, String>();
				}
				map.put(rs.getInt("areaId"), rs.getString("area"));
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

	public List<AreaBean> getAll() {
		String sql = new String("select areaId,area,parentId from areaidarea ");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AreaBean> lists = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				if (lists == null) {
					lists = new ArrayList<AreaBean>();
				}
				AreaBean mAreaBean = new AreaBean();
				mAreaBean.setAreaId(rs.getInt(1) + "");
				mAreaBean.setAreaName(rs.getString(2));
				mAreaBean.setParentId(rs.getString("parentId"));
				lists.add(mAreaBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}
	
	public List<AreaBean> getAll(String userid) {
		String sql = new String("SELECT areaid,area,parentid from areaidarea where areaid in(SELECT areaid from useridareaid where userid = ?) ");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AreaBean> lists = null;
		try {
			ps.setString(1, userid);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (lists == null) {
					lists = new ArrayList<AreaBean>();
				}
				AreaBean mAreaBean = new AreaBean();
				mAreaBean.setAreaId(rs.getInt(1) + "");
				mAreaBean.setAreaName(rs.getString(2));
				mAreaBean.setParentId(rs.getString("parentid"));
				lists.add(mAreaBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}

	public Map<Integer, String> getParentAll() {
		String sql = new String("SELECT id,parentAreaName from p_areaidarea ");
		Map<Integer, String> parentIds = new HashMap<Integer, String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				parentIds.put(rs.getInt("id"), rs.getString("parentAreaName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return parentIds;
	}
	
	public Map<Integer, String> getParentAll(String userid) {
		String sql = new String("SELECT id,parentAreaName from p_areaidarea where id in(SELECT parentid from areaidarea where areaid in(SELECT areaid from useridareaid where userid = ?))");
		Map<Integer, String> parentIds = new HashMap<Integer, String>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, userid);
			rs = ps.executeQuery();
			while (rs.next()) {
				parentIds.put(rs.getInt("id"), rs.getString("parentAreaName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return parentIds;
	}

	public List<AreaAndRepeater> getAllAreaByUserIdLg(String userId,
			String privilege) {
		String sql = "";
		if (privilege.equals("4")) {
			sql = "SELECT s.areaId,s.area,s.parentId,p.id,p.parentAreaName from (SELECT a.areaId,a.area,a.parentId FROM areaidarea a ) as s left JOIN p_areaidarea p on s.parentId = p.id order by p.parentAreaName";
		} else {
			sql = "SELECT s.areaId,s.area,s.parentId,p.id,p.parentAreaName FROM (SELECT a.areaId,a.area,a.parentId FROM useridareaid u,areaidarea a WHERE u.userId=? AND u.areaId=a.areaId) AS s LEFT JOIN p_areaidarea p ON s.parentId = p.id order by p.parentAreaName";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		List<AreaAndRepeater> areaAndRepeaterList = new ArrayList<AreaAndRepeater>();
		ResultSet rs = null;
		AreaAndRepeater area = null;
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();
//			int temp = 0;
			while (rs.next()) {
				if (area == null || (area!=null && area.getParentId()!=rs.getInt("parentId"))) { 
					if (area!=null){
						areaAndRepeaterList.add(area);
					}
					area = new AreaAndRepeater();
					area.setParentId(rs.getInt("parentId"));
					area.setParentAreaName(rs.getString("parentAreaName"));
					List<AreaAndRepeater> secondAreaList= new ArrayList<AreaAndRepeater>();
					area.setList(secondAreaList);
					List<String> lstStr = new ArrayList<String>();
					area.setListEaraId(lstStr);
					
				}
				AreaAndRepeater secondArea = new AreaAndRepeater();
				secondArea.setAreaId(rs.getInt("areaId"));
				secondArea.setAreaName(rs.getString("area"));
				area.getList().add(secondArea);
				area.getListEaraId().add(rs.getInt("areaId")+"");
			}
			areaAndRepeaterList.add(area);	
//				area = new AreaAndRepeater();
//				area.setAreaId(rs.getInt("areaId"));
//				area.setAreaName(rs.getString("area"));
//				if (rs.getInt("id")!= temp){
//					if (temp != 0){
//						areaAndRepeaterList.add(area);
//					}
//					temp = rs.getInt("id");
//					area = new AreaAndRepeater();
//					List<AreaAndRepeater> secondAreaList= new ArrayList<AreaAndRepeater>();
//					area.setList(secondAreaList);
//					List<String> lstStr = new ArrayList<String>();
//					area.setListEaraId(lstStr);
//					area.setP_parentId(rs.getInt("id"));
//					area.setParentId(rs.getInt("parentId"));
//					area.setParentAreaName(rs.getString("parentAreaName"));
//				}

				
//				AreaAndRepeater secondArea = new AreaAndRepeater();
//				secondArea.setAreaId(rs.getInt("areaId"));
//				secondArea.setAreaName(rs.getString("area"));
//				
//				area.getList().add(secondArea);
//				area.getListEaraId().add(rs.getInt("areaId")+"");
//			}
//			areaAndRepeaterList.add(area);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return areaAndRepeaterList;
//		List<AreaAndRepeater> list = new ArrayList<AreaAndRepeater>();
////		if (areaAndRepeaterList == null) {
////			return null;
////		} else {
//			int size = areaAndRepeaterList.size();
//			for (int i = 0; i < size; i++) {
//				AreaAndRepeater areaContent = areaAndRepeaterList.get(i);
////				int areaAndRepeaterListFirst = areaContent.getParentId();
//				int parentId = areaContent.getParentId();
////				if (areaAndRepeaterListFirst == 0) {
////					list.add(areaAndRepeaterList.get(i));
////					areaAndRepeaterList.remove(i);
////					i--;
////					size--;
////				} else {
//					AreaAndRepeater b = new AreaAndRepeater();
//					List<AreaAndRepeater> secondListarea = new ArrayList<AreaAndRepeater>();
//					List<String> listString = new ArrayList<>();
//					String parentAreaName = areaContent.getParentAreaName();
//					b.setParentAreaName(parentAreaName);
//					b.setParentId(parentId);
//					for (int j = 0; j < size; j++) {
//						AreaAndRepeater areaContentOther = areaAndRepeaterList
//								.get(j);
//						int parentIdNow = areaContentOther.getParentId();
//						if (parentIdNow == parentId) {
//							listString.add(areaAndRepeaterList.get(j)
//									.getAreaId() + "");
//							secondListarea.add(areaAndRepeaterList.get(j));
//							areaAndRepeaterList.remove(j);
//							size--;
//							j--;
//						}
//
//					}
//					i = -1;
//					b.setList(secondListarea);
//					b.setListEaraId(listString);
//					list.add(b);
//
////				}
//			}
//			return list;
//		}

	}

	public AreaBeanEntity getAllAreaInfo(String userId, String privilege) {
		String sql = "";
		if (privilege.equals("4")) {
			sql = "SELECT s.areaId,s.area,s.parentId,p.id,p.parentAreaName from (SELECT a.areaId,a.area,a.parentId FROM areaidarea a ORDER BY a.area ) as s left JOIN p_areaidarea p on s.parentId = p.id order BY parentAreaName";
		} else {
			sql = "SELECT s.areaId,s.area,s.parentId,p.id,p.parentAreaName FROM (SELECT a.areaId,a.area,a.parentId FROM useridareaid u,areaidarea a WHERE u.userId=? AND u.areaId=a.areaId) AS s LEFT JOIN p_areaidarea p ON s.parentId = p.id";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		Map<Integer, String> areaMap = new HashMap<Integer, String>();
		Set<String> areaSet = new HashSet<String>();
		List<AreaBeanEntity> abeList = new ArrayList<AreaBeanEntity>();
		ResultSet rs = null;
		AreaBeanEntity abe = null;
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				abe = new AreaBeanEntity();
				abe.setAreaId(rs.getInt("areaId"));
				abe.setAreaName(rs.getString("area"));
				abe.setP_parentId(rs.getInt("id"));
				abe.setParentId(rs.getInt("parentId"));
				abe.setParentAreaName(rs.getString("parentAreaName"));
				abeList.add(abe);
				areaSet.add(rs.getString("parentAreaName"));
				areaMap.put(rs.getInt("id"), rs.getString("parentAreaName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		abe = new AreaBeanEntity();
		abe.setAbeList(abeList);
		abe.setAreaMap(areaMap);
		abe.setAreaSet(areaSet);
		return abe;
	}

	public AllAreaEntity getAllAreas(String userId, String privilege) {
		String sql = "";
		if (privilege.equals("4")) {
			sql = "SELECT s.areaId,s.area,s.parentId,p.id,p.parentAreaName from (SELECT a.areaId,a.area,a.parentId FROM areaidarea a ) as s left JOIN p_areaidarea p on s.parentId = p.id";
		} else {
			sql = "SELECT s.areaId,s.area,s.parentId,p.id,p.parentAreaName FROM (SELECT a.areaId,a.area,a.parentId FROM useridareaid u,areaidarea a WHERE u.userId=? AND u.areaId=a.areaId) AS s LEFT JOIN p_areaidarea p ON s.parentId = p.id";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		AllAreaEntity aae = null;
		Map<Integer, List<Area>> areaMap = new HashMap<Integer, List<Area>>();
		List<Area> parentAreas = new ArrayList<Area>();// @@父级区域
		ResultSet rs = null;
		Area abe = null;
		Area p_abe = null;
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (!areaMap.containsKey(rs.getInt("id"))) {
					areaMap.put(rs.getInt("id"), new ArrayList<Area>());
					p_abe = new Area();
					p_abe.setAreaId(rs.getInt("id") + "");
					p_abe.setAreaName(rs.getString("parentAreaName"));
					p_abe.setIsParentArea(1);
					parentAreas.add(p_abe);
				}
				abe = new Area();
				abe.setAreaId(rs.getInt("areaId") + "");
				abe.setAreaName(rs.getString("area"));
				abe.setIsParentArea(0);
				areaMap.get(rs.getInt("id")).add(abe);
			}
			for (Area a : parentAreas) {
				String sum = a.getAreaId();
				a.setAreas(areaMap.get(Integer.parseInt(sum)));
			}
			if (parentAreas.size() > 0) {
				aae = new AllAreaEntity();
				aae.setAreas(parentAreas);
				aae.setError("获取成功");
				aae.setErrorCode(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aae;
	}

	public List<AreaAndRepeater> getAllAreaByUserId(String userId,
			String privilege) {
		String sql = null;
		if (privilege.equals("4")) {
			// sql =
			// "select DISTINCT areaidarea.areaId,area from areaidarea,smoke WHERE areaidarea.areaId = smoke.areaId ORDER BY areaidarea.areaId";
			sql = "select areaId,area,parentId from areaidarea order by area";
		} else {
			sql = "select a.areaId,a.area,a.parentId from useridareaid u,areaidarea a where u.userId=? and u.areaId=a.areaId order by a.area";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AreaAndRepeater> list = null;
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				if (list == null) {
					list = new ArrayList<AreaAndRepeater>();
				}
				AreaAndRepeater mAreaAndRepeater = new AreaAndRepeater();
				mAreaAndRepeater.setAreaId(rs.getInt(1));
				mAreaAndRepeater.setAreaName(rs.getString(2));
				list.add(mAreaAndRepeater);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	
	//根据用户ID和权限获取所有水系统设备的区域
	public List<AreaAndRepeater> getAllWaterAreaByUserId(String userId,String privilege) {
		String sql = null;
		if (privilege.equals("4")) {
			sql = "SELECT areaid,area,parentId from areaidarea where areaId in(SELECT areaId from smoke where deviceType in(10,15,18,19,42,125)) ORDER BY area ";
		} else {
			sql = "SELECT areaid,area,parentId from areaidarea where areaId in(SELECT s.areaId from smoke s,useridareaid u where s.deviceType in(10,15,18,19,42,125) and u.areaid = s.areaid and u.userid = ?) ORDER BY area ";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AreaAndRepeater> list = null;
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				if (list == null) {
					list = new ArrayList<AreaAndRepeater>();
				}
				AreaAndRepeater mAreaAndRepeater = new AreaAndRepeater();
				mAreaAndRepeater.setAreaId(rs.getInt(1));
				mAreaAndRepeater.setAreaName(rs.getString(2));
				list.add(mAreaAndRepeater);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	//根据用户ID和权限获取所有水压设备的区域
	public List<AreaAndRepeater> getWaterAreaByUserId(String userId,String privilege) {
		String sql = "";
		if (privilege.equals("4")) {
			sql = "SELECT areaid,area,parentId from areaidarea where areaId in(SELECT areaId from smoke where deviceType in(10,42,125,70)) ORDER BY area ";
		} else {
			sql = "SELECT areaid,area,parentId from areaidarea where areaId in(SELECT s.areaId from smoke s,useridareaid u where s.deviceType in(10,42,125,70) and u.areaid = s.areaid and u.userid = ?) ORDER BY area ";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AreaAndRepeater> list = null;
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				if (list == null) {
					list = new ArrayList<AreaAndRepeater>();
				}
				AreaAndRepeater mAreaAndRepeater = new AreaAndRepeater();
				mAreaAndRepeater.setAreaId(rs.getInt(1));
				mAreaAndRepeater.setAreaName(rs.getString(2));
				list.add(mAreaAndRepeater);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	@Override
	public String getWaterAreaStr(String userId,String privilege){
		String sql = "";
		StringBuffer areas = new StringBuffer();
		if (privilege.equals("4")) {
			sql = "SELECT areaId from smoke where deviceType in(10,42,125,70)  ";
		} else {
			sql = "SELECT s.areaId from smoke s,useridareaid u where s.deviceType in(10,42,125,70) and u.areaid = s.areaid and u.userid = ? ";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				areas.append(rs.getString(1)+",");
			}
			if (!areas.equals("")){
				areas.append("0");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return areas.toString();
	}
	
	//根据用户ID和权限获取所有水位设备的区域
	public List<AreaAndRepeater> getWaterLeveAreaByUserId(String userId,String privilege) {
		String sql = null;
		if (privilege.equals("4")) {
			sql = "SELECT areaid,area,parentId from areaidarea where areaId in(SELECT areaId from smoke where deviceType in(19,69,124)) ORDER BY area ";
		} else {
			sql = "SELECT areaid,area,parentId from areaidarea where areaId in(SELECT s.areaId from smoke s,useridareaid u where s.deviceType in(19,69,124) and u.areaid = s.areaid and u.userid = ?) ORDER BY area ";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AreaAndRepeater> list = null;
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				if (list == null) {
					list = new ArrayList<AreaAndRepeater>();
				}
				AreaAndRepeater mAreaAndRepeater = new AreaAndRepeater();
				mAreaAndRepeater.setAreaId(rs.getInt(1));
				mAreaAndRepeater.setAreaName(rs.getString(2));
				list.add(mAreaAndRepeater);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	public List<AreaAndRepeater> getNFCAreaByUserId(String userId, String privilege) {
		String sql = null;
		if (privilege.equals("4")) {
			sql = "SELECT * from (SELECT * from (SELECT a.areaid,a.area,a.parentId from areaidarea a where a.areaid in(SELECT areaid from nfcinfo GROUP BY areaid)) as a GROUP BY a.areaId) as an ORDER BY area";
		} else {
			sql = "SELECT * from (SELECT * from (SELECT a.areaid,a.area,a.parentId,u.userid from areaidarea a,useridareaid u where a.areaid in(SELECT areaid from nfcinfo GROUP BY areaid) and u.areaId = a.areaid and u.userid = ? ) as a GROUP BY a.areaId) as an ORDER BY area";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AreaAndRepeater> list = null;
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				if (list == null) {
					list = new ArrayList<AreaAndRepeater>();
				}
				AreaAndRepeater mAreaAndRepeater = new AreaAndRepeater();
				mAreaAndRepeater.setAreaId(rs.getInt(1));
				mAreaAndRepeater.setAreaName(rs.getString(2));
				list.add(mAreaAndRepeater);
			}
			if(list == null){
				list = new ArrayList<AreaAndRepeater>();
				AreaAndRepeater mAreaAndRepeater = new AreaAndRepeater();
				mAreaAndRepeater.setAreaId(0);
				mAreaAndRepeater.setAreaName("无");
				list.add(mAreaAndRepeater);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	/** 通过区域的id获取区域的名字 */
	public String getAreaNameById(int areaId) {
		String sql = "select area from areaidarea WHERE areaidarea.areaId=?";
		String areaName = null;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;

		try {
			ps.setInt(1, areaId);
			rs = ps.executeQuery();
			while (rs.next()) {
				areaName = rs.getString("area");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return areaName;
	}
	
	public String getAreaNameById(String areaId) {
		String sql = "select area from areaidarea WHERE areaidarea.areaId=?";
		String areaName = null;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;

		try {
			ps.setString(1, areaId);
			rs = ps.executeQuery();
			while (rs.next()) {
				areaName = rs.getString("area");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return areaName;
	}

	/** 添加父级的区域 */
	@Override
	public boolean addParentArea(ParentArea parentArea) {
		int parentAreaId = selectMaxAreaId();
		boolean result = false;
		boolean ifexist = ifexistParentAreaName(parentArea);
		if (ifexist == false) {
			String strsql = "INSERT INTO p_areaidarea(id,province,city,town,parentAreaName,userId) VALUES(?,?,?,?,?,?)";
			int ids = -1;
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, strsql);
			try {
				ps.setInt(1, parentAreaId);
				ps.setString(2, parentArea.getProvince());
				ps.setString(3, parentArea.getCity());
				ps.setString(4, parentArea.getTown());
				ps.setString(5, parentArea.getParentAreaName());
				ps.setString(6, parentArea.getCurrentId());
				ids = ps.executeUpdate();
				if (ids > 0) {
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
		}

		return result;
	}

	/** 添加二级区域 */
	public boolean addSecondArea(SecondArea secondArea) {
		int secondAreaId = selectMaxSecondAreaId();
		boolean result = false;
		boolean result1 = ifexitArea(secondArea.getSecondAreaName());
		if (result1 == false) {
			String strsql = "INSERT INTO areaidarea(areaId,area,province,city,town,parentId,userId) VALUES(?,?,?,?,?,?,?)";

			int ids = -1;
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, strsql);
			try {
				ps.setInt(1, secondAreaId);
				ps.setString(2, secondArea.getSecondAreaName());
				ps.setString(3, secondArea.getProvince());
				ps.setString(4, secondArea.getCity());
				ps.setString(5, secondArea.getTown());
				ps.setString(6, secondArea.getParentAreaName());
				ps.setString(7, secondArea.getCurrentId());
				ids = ps.executeUpdate();
				if (ids > 0) {
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBConnectionManager.close(ps);
				// DBConnectionManager.close(rs);
				DBConnectionManager.close(conn);
			}
		}
		return result;
	}
	
	public int insertSecondArea(SecondArea secondArea){
		int secondAreaId = selectMaxSecondAreaId();
		int result = 0;
		boolean result1 = ifexitArea(secondArea.getSecondAreaName());
		if (result1 == false) {
			String strsql = "INSERT INTO areaidarea(areaId,area,province,city,town,parentId,userId) VALUES(?,?,?,?,?,?,?)";

			int ids = -1;
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, strsql);
			try {
				ps.setInt(1, secondAreaId);
				ps.setString(2, secondArea.getSecondAreaName());
				ps.setString(3, secondArea.getProvince());
				ps.setString(4, secondArea.getCity());
				ps.setString(5, secondArea.getTown());
				ps.setString(6, secondArea.getParentAreaName());
				ps.setString(7, secondArea.getCurrentId());
				ids = ps.executeUpdate();
				if (ids > 0) {
					result = secondAreaId;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBConnectionManager.close(ps);
				// DBConnectionManager.close(rs);
				DBConnectionManager.close(conn);
			}
		}
		return result;
	}

	public int selectMaxAreaId() {
		String sqlstr = "SELECT MAX(id) FROM p_areaidarea";
		int max = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				max = rs.getInt(1) + 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return max;
	}

	@Override
	public boolean ifexitArea(String area) {
		String sqlstr = "SELECT area FROM areaidarea WHERE AREA=?";
		String basic = "select count(*) as count1 from ( " + sqlstr + " ) aa ";
		boolean result = true;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, basic);
		ResultSet rs = null;
		try {
			ps.setString(1, area);
			rs = ps.executeQuery();
			while (rs.next()) {
				int count = rs.getInt("count1");
				if (count > 0) {
					result = true;
				} else {
					result = false;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	private boolean ifexistParentAreaName(ParentArea parentArea) {

		String basic = " select count(*) as count1 from( select province,city,town,parentAreaName from p_areaidarea where parentAreaName=?) aa";
		boolean result = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, basic);
		ResultSet rs = null;
		try {
			ps.setString(1, parentArea.getParentAreaName());
			rs = ps.executeQuery();
			while (rs.next()) {
				int count = rs.getInt("count1");
				if (count > 0) {
					result = true;
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	/** 获取省份列表dao的实现类 */
	public List<Province> getProviceList() {
		Connection conn = DBConnectionManager.getConnection();
		String sql = "select id,code,`name` from province ";
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<Province> listProvince = new ArrayList<Province>();
		try {
			rs = ppst.executeQuery();
			while (rs.next()) {
				Province province = new Province();
				province.setId(rs.getInt(1));
				province.setCode(rs.getString(2));
				province.setName(rs.getString(3));
				listProvince.add(province);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return listProvince;
	}

	/** 通过省份的code获取城市 */
	public List<City> getCityByProvinceCode(String provinceCode) {
		Connection conn = DBConnectionManager.getConnection();
		String sql = "select id,code,name,province from city where province= ? ";
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<City> listCity = new ArrayList<City>();
		try {
			ppst.setString(1, provinceCode);
			rs = ppst.executeQuery();
			while (rs.next()) {
				City city = new City();
				city.setId(rs.getInt(1));
				city.setCode(rs.getString(2));
				city.setName(rs.getString(3));
				city.setProvince(rs.getString(4));
				listCity.add(city);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return listCity;
	}

	/** 通过城市的code获得城镇 */
	public List<Town> getTownByCityCode(String cityCode) {
		Connection conn = DBConnectionManager.getConnection();
		String sql = " select id,code,name,city from town where city= ? ";
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		List<Town> list = new ArrayList<Town>();
		ResultSet rs = null;
		try {
			ppst.setString(1, cityCode);
			rs = ppst.executeQuery();
			while (rs.next()) {
				Town town = new Town();
				town.setId(rs.getInt(1));
				town.setCode(rs.getString(2));
				town.setName(rs.getString(3));
				town.setCity(rs.getString(4));
				list.add(town);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public List<ParentArea> getParentAreaByTownCode(String townCode) {
		Connection conn = DBConnectionManager.getConnection();
		// String sql =
		// " select id,province,city,town,parentAreaName,userId from p_areaidarea where town= ? ";
		String sql = " select id,province,city,town,parentAreaName,userId from p_areaidarea ";
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		List<ParentArea> list = new ArrayList<ParentArea>();
		ResultSet rs = null;
		try {
			// ppst.setString(1, townCode);
			rs = ppst.executeQuery();
			while (rs.next()) {
				ParentArea parentArea = new ParentArea();
				parentArea.setId(rs.getInt(1));
				parentArea.setProvince(rs.getString(2));
				parentArea.setCity(rs.getString(3));
				parentArea.setTown(rs.getString(4));
				parentArea.setParentAreaName(rs.getString(5));
				parentArea.setCurrentId(rs.getString(6));
				list.add(parentArea);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public int selectMaxAreaId1() {
		String sqlstr = "SELECT MAX(p_areaId) FROM p_areaidarea";
		int max = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				max = rs.getInt(1) + 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return max;
	}

	public int selectMaxParentAreaId() {
		String sqlstr = "SELECT MAX(id) FROM p_areaidarea";
		int max = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				max = rs.getInt(1) + 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return max;
	}

	public int selectMaxSecondAreaId() {
		String sqlstr = "SELECT MAX(areaId) FROM areaidarea";
		int max = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				max = rs.getInt(1) + 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return max;
	}

	public int updateAreaByareaId(AreaBean areaBean) {
		int result = 0;
		String sql = " update areaidarea a set a.area = ? where a.areaId = ? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, areaBean.getAreaName());
			ps.setString(2, areaBean.getAreaId());
			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;

	}

	public int deleteAreaById(String areaId) {
		int result = 0;
		String sql = " delete from areaidarea where areaId = ? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int areaId1 = Integer.parseInt(areaId);
		try {
			ppst.setInt(1, areaId1);
			result = ppst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	public List<SmokeBean> getSmokeInfo(String smokeMac) {
		SmokeBean smokeBean = new SmokeBean();
		String sql = "SELECT mac,address,longitude,latitude from smoke where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<SmokeBean> list = new ArrayList<SmokeBean>();
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while (rs.next()) {
				smokeBean.setMac(rs.getString("mac"));
				smokeBean.setAddress(rs.getString("address"));
				smokeBean.setLongitude(rs.getString("longitude"));
				smokeBean.setLatitude(rs.getString("latitude"));
				list.add(smokeBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public List<SmokeBean> saveSmokeInfo(String smokeMac, String longitude,
			String latitude) {
		SmokeBean smokeBean = new SmokeBean();
		String sql = "update smoke set longitude = ?,latitude=? where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);

		List<SmokeBean> list = new ArrayList<SmokeBean>();
		try {
			ps.setString(1, longitude);
			ps.setString(2, latitude);
			ps.setString(3, smokeMac);
			int result = ps.executeUpdate();
			if (result > 0) {
				smokeBean.setCount(result);
				list.add(smokeBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	@Override
	public int updatePareaIdAreaId(int areaid, int pareantid) {
		int result = 0;
		String sqlstr = "update areaidarea set parentId = ? where areaId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setInt(1, pareantid);
			ps.setInt(2, areaid);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public List<SmokeXYZ> getAllXYZ(List<String> list) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT mac,longitude,latitude from smoke WHERE areaId in(");
		int len = 0;
		if(list!=null){
			len = list.size();
			if(len == 1){
				sql.append(list.get(0)+")");
			}else{
				for (int i = 0; i < list.size(); i++) {
					if(i==0){
						sql.append(i);
					}else if(i == len-1){
						sql.append(","+list.get(i)+")");
					}else{
						sql.append(","+list.get(i));
					}
				}
			}
		}
		List<SmokeXYZ> xyzlist = new ArrayList<SmokeXYZ>();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				SmokeXYZ sk = new SmokeXYZ();
				sk.setMac(rs.getString(1));
				sk.setLongitude(rs.getString(2));
				sk.setLatitude(rs.getString(3));
				xyzlist.add(sk);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return xyzlist;
	}
	
	public List<AreaAndRepeater> getNFCAreasByUserId(String userId, String privilege) {
		String sql = null;
		if (privilege.equals("4")) {
			sql = "SELECT a.areaid,a.area,a.parentId,parentAreaName from areaidarea a,p_areaidarea p where parentId =  p.id and a.areaid in(SELECT distinct areaid from nfcinfo ) order by parentAreaName";
		} else {
			sql = "SELECT a.areaid,a.area,a.parentId,parentAreaName,u.userid from areaidarea a,useridareaid u,p_areaidarea p where a.parentId = p.id and a.areaid in(SELECT distinct areaid from nfcinfo ) and u.areaId = a.areaid and u.userid = ? ";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AreaAndRepeater> list = new ArrayList<AreaAndRepeater>();
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();

			AreaAndRepeater parent = null;
			while (rs.next()) {
				if (parent == null || parent.getAreaId() != rs.getInt("parentId")){
					if (parent != null) { //在新建parent之前，将其添加到list里
						list.add(parent);
					}
					parent = new AreaAndRepeater();
					parent.setAreaId(rs.getInt("parentId"));
					parent.setAreaName(rs.getString("parentAreaName"));
					List<AreaAndRepeater> secondAreaList = new ArrayList<AreaAndRepeater>();
					parent.setList(secondAreaList);
				} 
				AreaAndRepeater secondArea = new AreaAndRepeater();
				secondArea.setAreaId(rs.getInt("areaId"));
				secondArea.setAreaName(rs.getString("area"));
				parent.getList().add(secondArea);
			}
			list.add(parent); //将最后一个parent添加到list里面
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	public List<AreaAndRepeater> getNFCRecordAreasByUserId(String userId, String privilege) {
		String sql = null;
		if (privilege.equals("4")) {
			sql = "SELECT a.areaid,a.area,a.parentId,parentAreaName from areaidarea a,p_areaidarea p where parentId =  p.id and a.areaid in(SELECT distinct areaid from nfcinfo where uid in (select distinct uuid  from nfcrecord)) order by parentAreaName";
		} else {
			sql = "SELECT a.areaid,a.area,a.parentId,parentAreaName,u.userid from areaidarea a,useridareaid u,p_areaidarea p where a.parentId = p.id and a.areaid in(SELECT distinct areaid from nfcinfo where uid in (select distinct uuid  from nfcrecord)) and u.areaId = a.areaid and u.userid = ? ";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AreaAndRepeater> list = new ArrayList<AreaAndRepeater>();
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();

			AreaAndRepeater parent = null;
			while (rs.next()) {
				if (parent == null || parent.getAreaId() != rs.getInt("parentId")){
					if (parent != null) { //在新建parent之前，将其添加到list里
						list.add(parent);
					}
					parent = new AreaAndRepeater();
					parent.setAreaId(rs.getInt("parentId"));
					parent.setAreaName(rs.getString("parentAreaName"));
					List<AreaAndRepeater> secondAreaList = new ArrayList<AreaAndRepeater>();
					parent.setList(secondAreaList);
				} 
				AreaAndRepeater secondArea = new AreaAndRepeater();
				secondArea.setAreaId(rs.getInt("areaId"));
				secondArea.setAreaName(rs.getString("area"));
				parent.getList().add(secondArea);
			}
			list.add(parent); //将最后一个parent添加到list里面
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	public List<AreaBean> getNFCAreaByUserId2(String userId, String privilege) {
		String sql = null;
		if (privilege.equals("4")) {
			sql = "SELECT a.areaid,a.area from areaidarea a where a.areaid in(SELECT distinct areaid from nfcinfo ) order by area";
		} else {
			sql = "SELECT a.areaid,a.area from areaidarea a,useridareaid u where  a.areaid in(SELECT distinct areaid from nfcinfo ) and u.areaId = a.areaid and u.userid = ? ";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<AreaBean> list = new ArrayList<AreaBean>();
		try {
			if (!privilege.equals("4")) {
				ps.setString(1, userId);
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				AreaBean ab  = new AreaBean();
				ab.setAreaId(rs.getInt("areaId")+"");
				ab.setAreaName(rs.getString("area"));
				list.add(ab);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}

	@Override
	public PushAlarmMqttEntity getalarmInfo(String mac) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from (");
		sql.append(" SELECT DISTINCT s.mac,s.address,s.time,s.netState,s.named,s.principal1,s.principal1Phone,s.deviceType,d.deviceName,a.alarmType,p.alarmName,s.areaid,e.area,a.alarmTime");
		sql.append(" from smoke s,alarm a,devices d,alarmtype p,areaidarea e ");
		sql.append(" where s.mac = a.smokeMac and s.deviceType = d.id and p.alarmId = a.alarmType and s.areaid = e.areaid");
		sql.append(" and smokeMac = ?");
		sql.append(" ORDER BY a.id desc ) as ss GROUP BY ss.mac");
		PushAlarmMqttEntity pa = new PushAlarmMqttEntity();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		String alarmTime = GetTime.ConvertTimeByLong();
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while (rs.next()) {
				pa.setAreaName(rs.getString("area"));
				pa.setAreaid(rs.getInt("areaid"));
				pa.setAlarmName(rs.getString("alarmName"));
				pa.setAlarmType(rs.getInt("alarmType"));
				pa.setDeviceName(rs.getString("deviceName"));
				pa.setDeviceType(rs.getInt("deviceType"));
				pa.setPrincipal1Phone(rs.getString("principal1Phone"));
				pa.setPrincipal1(rs.getString("principal1"));
				pa.setAlarmTime(alarmTime);
				pa.setNamed(rs.getString("named"));
				pa.setNetState(rs.getInt("netState"));
				pa.setMac(rs.getString("mac"));
				pa.setTime(rs.getString("time"));
				pa.setAddress(rs.getString("address"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return pa;
	}

	@Override
	public int getAreaIdByRepeater(String repeater) {
		int areaid=0;
		String sql="select distinct areaid from smoke where repeater=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, repeater);
			rs=ps.executeQuery();
			if(rs.next()){
				areaid=rs.getInt(1);
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
	public AreaidGoEasy queryAppidAppkey(String areaid) {
		String sql = "select appid,appkey from areaidgoeasy where areaid = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		AreaidGoEasy age = null;
		try {
			ps.setString(1, areaid);
			rs = ps.executeQuery();
			if(rs.next()){
				age = new AreaidGoEasy();
				age.setAppid(rs.getInt(1));
				age.setAppkey(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return age;
	}

	@Override
	public int getSuperAreaIdBySmokeMac(String mac) {
		String sql = "SELECT id FROM p_areaidarea where id = (SELECT parentId from areaidarea where areaid = (SELECT areaid from smoke where mac = ?))";
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}
	
	@Override
	public int getSuperAreaIdByRepeater(String repeater) {
		String sql = "SELECT distinct a.parentId from areaidarea a,smoke s where s.areaid= a.areaid and s.repeater=? ";
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, repeater);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}

	@Override
	public List<String> getUserByAreaId(String areaId) {
		String sql = "select ua.userId ,u.privilege from useridareaid ua,user u where u.privilege = 5 and ua.userId = u.userId and ua.areaId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();
		try {
			ps.setInt(1, Integer.parseInt(areaId));
			rs = ps.executeQuery();
			while(rs.next()){
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	

	@Override
	public boolean updateAreaTxtState(int areaId, int num) {
		String sql="update areaidarea set isTxt=? where areaId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		boolean bool = false;
		int rs=0;
		try {
			ps.setInt(1, num);
			ps.setInt(2, areaId);
			rs = ps.executeUpdate();
			if(rs>0){
				bool=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(conn);
			DBConnectionManager.close(ps);
		}
		return bool;
	}

	@Override
	public int getIsTxtById(int areaId) {
		String sql = "select istxt from areaidarea where areaId=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int istxt = 0;
		try {
			ps.setInt(1, areaId);
			rs = ps.executeQuery();
			while(rs.next()){
				istxt=rs.getInt("istxt");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(conn);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(rs);
		}
		return istxt;
	}

	@Override
	public List<AreaParentEntity> getParentList(List<String> areaids) {
		List<AreaParentEntity> parentList = new ArrayList<AreaParentEntity>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.areaid,a.area,p.id,p.parentAreaName from areaidarea a,p_areaidarea p where a.parentId = p.id");
		if(areaids!=null&&areaids.size()>0){
			sql.append(" and a.areaid in(0");
			for(String aids:areaids){
				sql.append(","+aids);
			}
			sql.append(")");
		}
		
		sql.append(" ORDER BY p.parentAreaName");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		AreaParentEntity ape = null;
		int pid = 0;
		Map<Integer,String> aMap = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(pid!=rs.getInt("id")){
					if(ape!=null&&aMap!=null){
						ape.setAreaIds(aMap);
						parentList.add(ape);
					}
					aMap = new HashMap<Integer,String>();
					ape = new AreaParentEntity();
					ape.setParentId(rs.getString("id"));
					ape.setParentName(rs.getString("parentAreaName"));
					pid = rs.getInt("id");
				}
				aMap.put(rs.getInt("areaid"), rs.getString("area"));
			}
			if(ape!=null&&aMap!=null){
				ape.setAreaIds(aMap);
				parentList.add(ape);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return parentList;
	}

	@Override
	public Map<String,String> getRepeaterList(String areaId) {
		Map<String,String> rMap = null;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT s.repeater,s.areaid,a.area from smoke s,areaidarea a where s.areaid = a.areaid");
		if(StringUtils.isNotBlank(areaId)){
			sql.append(" and s.areaid='"+areaId+"' GROUP BY s.repeater ");
		}else{
			sql.append(" GROUP BY s.repeater limit 10");
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				if(rMap==null){
					rMap = new HashMap<String,String>();
				}
				rMap.put(rs.getString("repeater"), rs.getString("area"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return rMap;
	}
	
	@Override
	public List<RepeaterLevelUp> getParentList(String areaId) {
		List<RepeaterLevelUp> rlist = new ArrayList<RepeaterLevelUp>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT s.repeater,s.areaid,a.area from smoke s,areaidarea a where s.areaid = a.areaid");
		if(StringUtils.isNotBlank(areaId)){
			sql.append(" and s.areaid='"+areaId+"' GROUP BY s.repeater ");
		}else{
			sql.append(" GROUP BY s.repeater limit 10");
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				RepeaterLevelUp rp = new RepeaterLevelUp();
				rp.setAreaName(rs.getString("area"));
				rp.setRepeaterMac(rs.getString("repeater"));
				
				int leveState = 1;	
				if(Utils.hostLevenUpState.get(rs.getString("repeater"))==null){//1、正常，2、成功，3、失败，4、升级中
					leveState = 1;
				}else{
					leveState = Utils.hostLevenUpState.get(rs.getString("repeater"));
				}
				
				if(leveState==1){
					rp.setLeveState("未升级");
				}else if(leveState==2){
					rp.setLeveState("成功");
				}else if(leveState==3){
					rp.setLeveState("失败");
				}else{
					rp.setLeveState("升级中");
				}
				rlist.add(rp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return rlist;
	}
	
	@Override
	public List<RepeaterLevelUp> getParentList(String areaId,String parentId) {
		List<RepeaterLevelUp> rlist = new ArrayList<RepeaterLevelUp>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT s.repeater,s.areaid,a.area from smoke s,areaidarea a where s.areaid = a.areaid");
		if(StringUtils.isNotBlank(areaId)){
			sql.append(" and s.areaid='"+areaId+"' GROUP BY s.repeater ");
		}else if(StringUtils.isNotBlank(parentId)){
			sql.append(" and a.parentId='"+parentId+"' GROUP BY s.repeater ");
		}else{
			sql.append(" GROUP BY s.repeater limit 10");
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				RepeaterLevelUp rp = new RepeaterLevelUp();
				rp.setAreaName(rs.getString("area"));
				rp.setRepeaterMac(rs.getString("repeater"));
				
				int leveState = 1;	
				if(Utils.hostLevenUpState.get(rs.getString("repeater"))==null){//1、正常，2、成功，3、失败，4、升级中
					leveState = 1;
				}else{
					leveState = Utils.hostLevenUpState.get(rs.getString("repeater"));
				}
				
				if(leveState==1){
					rp.setLeveState("未升级");
				}else if(leveState==2){
					rp.setLeveState("成功");
				}else if(leveState==3){
					rp.setLeveState("失败");
				}else{
					rp.setLeveState("升级中");
				}
				rlist.add(rp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return rlist;
	}

	@Override
	public void updateRepeaterTime(String repeaterMac,int leveUpState) {
		String sql = "update repeaterTime set leveUpState = ? where repeater = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setInt(1, leveUpState);
			ps.setString(2, repeaterMac);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	@Override
	public List<RepeaterLevelUp> getParentLists(String[] repeaters) {
		List<RepeaterLevelUp> rlist = new ArrayList<RepeaterLevelUp>();
		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT s.repeater,s.areaid,a.area,t.leveUpState from smoke s,areaidarea a,repeatertime t where s.areaid = a.areaid and t.repeater = s.repeater");
		sql.append("SELECT s.repeater,s.areaid,a.area from smoke s,areaidarea a where s.areaid = a.areaid");
		if(repeaters!=null&&repeaters.length>0){
			sql.append(" and s.repeater in(");
			for (int i = 0; i < repeaters.length; i++) {
				if(i==0){
					sql.append("'"+repeaters[i]+"'");
				}else if(i==repeaters.length-1){
					sql.append(",'"+repeaters[i]+"'");
				}else{
					sql.append(",'"+repeaters[i]+"'");
				}
				
			}
			sql.append(")");
		}
		sql.append(" GROUP BY s.repeater");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				RepeaterLevelUp rp = new RepeaterLevelUp();
				rp.setAreaName(rs.getString("area"));
				rp.setRepeaterMac(rs.getString("repeater"));
				int leveState = 1;	
				if(Utils.hostLevenUpState.get(rs.getString("repeater"))==null){//1、正常，2、成功，3、失败，4、升级中
					leveState = 1;
				}else{
					leveState = Utils.hostLevenUpState.get(rs.getString("repeater"));
				}
				
				if(leveState==1){
					rp.setLeveState("未升级");
				}else if(leveState==2){
					rp.setLeveState("成功");
				}else if(leveState==3){
					rp.setLeveState("失败");
				}else{
					rp.setLeveState("升级中");
				}
				rlist.add(rp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return rlist;
	}

	@Override
	public int getIfStartMqqt(String mac) {
		int result = 1;
		String sql = "SELECT id,parentAreaName,ifMqtt from p_areaidarea where id = (SELECT parentId from areaidarea where areaid = (SELECT areaid from smoke where mac = ?))";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt("ifMqtt");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public int getIfMqqtByParentId(int parentId) {
		int result = 0;
		String sql = "SELECT id,parentAreaName,ifMqtt from p_areaidarea where id = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setInt(1, parentId);
			rs = ps.executeQuery();
			while(rs.next()){
				result = rs.getInt("ifMqtt");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	@Override
	public Map<String, String> getAllParentArea() {
		String sql ="select id,parentAreaName from p_areaidarea";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		Map<String,String> map = new HashMap<String,String>();
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
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
	public Map<String, String> getAreasByParentId(String parentId) {
		String sql ="select areaid,area from areaidarea where parentId = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		Map<String,String> map = new HashMap<String,String>();
		try {
			ps.setString(1, parentId);
			rs = ps.executeQuery();
			while (rs.next()) {
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
	
}
