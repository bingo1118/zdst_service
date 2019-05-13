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

import com.cloudfire.dao.VideoDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.Camera;
import com.cloudfire.entity.VideosInArea;
import com.cloudfire.until.Utils;

public class VideoDaoImpl implements VideoDao {

	@Override
	public String getVideoByMac(String mac) {
		String sql = "select camera from smoke where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		String cameraIndexCode = "";
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			while (rs.next()) {
				cameraIndexCode = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return cameraIndexCode;
	}

	@Override
	public List<VideosInArea> getVideosInArea(List<String> areaIds) {
		List<VideosInArea> lstVideo = new ArrayList<VideosInArea>();
		StringBuffer sb = new StringBuffer();
		sb.append( "select v.areaid,a.area,v.indexcode,v.name from ( ");
		sb.append(" select areaid,indexcode,name from video where areaid in ("+ Utils.list2String(areaIds)+" )  order by areaid");
		sb.append(" ) v ,areaidarea a where v.areaid = a.areaid");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sb.toString());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			VideosInArea va = null;
			int i = -1;
			while (rs.next()) {
				if (va == null || va.getAreaId()!=rs.getInt("areaid")) {
					va = new VideosInArea();
					va.setAreaId(rs.getInt("areaId"));
					va.setAreaName(rs.getString("area"));
					List<Camera> cameras = new ArrayList<Camera>();
					va.setCameras(cameras);
					lstVideo.add(va);
					i++;
				} 
				
				Camera ca = new Camera();
				ca.setIndexCode(rs.getString("indexcode"));
				ca.setName(rs.getString("name"));
				lstVideo.get(i).getCameras().add(ca);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstVideo;
	}

	/**
	 * 根据别名获取监控点编号
	 */
	@Override
	public String getIndexByName(String name) {
		String sql = "select indexcode from video where name = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		String indexCode = "";
		try {
			ps.setString(1, name);
			rs = ps.executeQuery();
			while (rs.next()) {
				 indexCode = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return indexCode;
	}

	//给烟感绑定视频
	@Override
	public boolean bindVideo(String cameraIndexCode, String smokeMac,String areaId) {
		String sql = "update smoke set camera = ? where mac = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, cameraIndexCode);
			ps.setString(2, smokeMac);
			int n = ps.executeUpdate();
			if (n > 0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return true;
	}

	@Override
	public boolean updateVideo(String indexCode, String videoName, String areaId) {
		boolean rs = false;
		String sql ="replace into video(indexcode,name,areaId) values(?,?,?)";
		if (StringUtils.isBlank(areaId)){
			areaId="0";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, indexCode);
			ps.setString(2, videoName);
			ps.setString(3, areaId);
			int n = ps.executeUpdate();
			if (n>0){
				rs = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return rs;
	}

	@Override
	public Map<String, String> getVideoInfo(String seqNum) {
		String sql = "select indexcode,areaId,name from video where seqNum = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		Map<String,String> videoInfo = new HashMap<String,String>();
		try {
			ps.setString(1, seqNum);
			rs  = ps.executeQuery();
			while (rs.next()) {
				videoInfo.put("indexcode", rs.getString(1));
				videoInfo.put("areaId", rs.getString(2));
				videoInfo.put("name", rs.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return videoInfo;
	}
	
	@Override
	public Map<String, String> getVideoInfoByIndexCode(String indexCode) {
		String sql = "select areaid,seqNum from video where indexcode = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		Map<String,String> videoInfo  = new HashMap<String,String>();
		try {
			ps.setString(1, indexCode);
			rs = ps.executeQuery();
			while (rs.next()){
				videoInfo.put("areaId", rs.getString(1));
				videoInfo.put("seqNum", rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return videoInfo;
	}

	@Override
	public boolean addVideoInfo(String seqNum, String indexCode, String areaId, String name) {
		String sql = "insert into smoke(mac,named,areaId,deviceType) values (?,?,?,?)";
		String sql2 = "insert into video(indexcode,name,areaId,seqNum) values (?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int rs = 0;
		try {
			ps.setString(1, seqNum);
			ps.setString(2, name);
			ps.setString(3, areaId);
			ps.setString(4, "127");
			rs = ps.executeUpdate();
			if (rs > 0) {
				ps = DBConnectionManager.prepare(conn, sql2);
				ps.setString(1, indexCode);
				ps.setString(2, name);
				ps.setString(3, areaId);
				ps.setString(4, seqNum);
				rs  = ps.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		if (rs > 0)
			return true;
		else 
			return false;
	}
}
