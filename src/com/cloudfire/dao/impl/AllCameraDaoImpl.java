package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudfire.dao.AllCameraDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AllCamera;
import com.cloudfire.entity.AllCameraEntity;
import com.cloudfire.entity.CameraBean;
import com.cloudfire.entity.PushAlarmMsgEntity;

public class AllCameraDaoImpl implements AllCameraDao{
	private AreaDao mAreaDao;
	private PlaceTypeDao mPlaceTypeDao;

	public Map<String, CameraBean> getCameraByMac(List<String> listNum) {
		// TODO Auto-generated method stub
//		select * from smoke,camera  where smoke.areaId in (14) and smoke.camera is not NULL and smoke.camera!='' and camera.cameraId = smoke.camera
		StringBuffer strSql = new StringBuffer();
		int len = listNum.size();
		if(len==1){
			strSql.append(" s.areaId in (?)");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append(" s.areaId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?)");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		String loginSql = new String("select s.mac,c.cameraAddress,c.cameraId," +
				" c.cameraPwd,c.cameraName,c.latitude,c.longitude,c.principal1,c.principal1Phone,c.principal2,c.principal2Phone " +
				" from smoke s,camera c where s.camera is not NULL and s.camera!='' and c.cameraId = s.camera and ");
		String sqlStr = new String(loginSql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		Map<String, CameraBean> map = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(map==null){
					map = new HashMap<String, CameraBean>();
				}
				String mac = rs.getString(1);
				CameraBean cb = new CameraBean();
				cb.setCameraAddress(rs.getString(2));
				cb.setCameraId(rs.getString(3));
				cb.setCameraName(rs.getString(5));
				cb.setCameraPwd(rs.getString(4));
				cb.setLatitude(rs.getString(6));
				cb.setLongitude(rs.getString(7));
				cb.setPrincipal1(rs.getString(8));
				cb.setPrincipal1Phone(rs.getString(9));
				cb.setPrincipal2(rs.getString(10));
				cb.setPrincipal2Phone(rs.getString(11));
				map.put(mac,cb);
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

	public AllCameraEntity getAllCamera(String userId, String privilege,String page,int everypage) {
		// TODO Auto-generated method stub
		mAreaDao = new AreaDaoImpl();
		List<String> listNum = mAreaDao.getAreaStr(userId,privilege);
		if(listNum==null){
			return null;
		}
		Map<Integer, String> mapArea = mAreaDao.getAreaById(listNum);
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		StringBuffer strSql = new StringBuffer();
		int len = listNum.size();
		String pageSql = null;
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
//				int startNum = (pageInt-1)*20;
//				int endNum = 20;
				int startNum = (pageInt-1)*everypage;
				int endNum = everypage;
				pageSql = new String("order by time desc limit "+startNum+" , "+everypage);
			}
		}
		if(len==1){
			strSql.append("and areaId in (?) ");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					strSql.append("and areaId in (?, ");
				}else if(i==(len-1)){
					strSql.append(" ?)");
				}else{
					strSql.append(" ?, ");
				}
			}
		}
		String loginSql = new String("select cameraAddress,cameraId," +
				" cameraPwd,cameraName,latitude,longitude,principal1,principal1Phone,principal2,principal2Phone, " +
				" areaId,placeTypeId from camera where placeTypeId<>99 ");
		String sqlStr = null;
		if(pageSql!=null){
			sqlStr= new String(loginSql+strSql.toString()+pageSql);
		}else{
			sqlStr= new String(loginSql+strSql.toString());
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllCameraEntity ace = null;
		try {
			for(int i=1;i<=len;i++){
				ps.setInt(i, Integer.parseInt(listNum.get(i-1)));
			}
			rs = ps.executeQuery();
			List<AllCamera> listCamera = null;
			while(rs.next()){
				if(ace==null){
					ace = new AllCameraEntity();
					listCamera = new ArrayList<AllCamera>();
				}
				AllCamera ac = new AllCamera();
				ac.setCameraAddress(rs.getString("cameraAddress"));
				ac.setCameraId(rs.getString("cameraId"));
				ac.setCameraName(rs.getString("cameraName"));
				ac.setCameraPwd(rs.getString("cameraPwd"));
				ac.setLatitude(rs.getString("latitude"));
				ac.setLongitude(rs.getString("longitude"));
				ac.setPrincipal1(rs.getString("principal1"));
				ac.setPrincipal1Phone(rs.getString("principal1Phone"));
				ac.setPrincipal2(rs.getString("principal2"));
				ac.setPrincipal2Phone(rs.getString("principal2Phone"));
				int areaId = rs.getInt("areaId");
				String placeTypeId = rs.getString("placeTypeId");
				String placeName =null;
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeId);
					ac.setPlaceType(placeName);
				}
				String areaName =null;
				if(mapArea!=null&&mapArea.size()>0){
					areaName = mapArea.get(areaId);
					ac.setAreaName(areaName);
				}
				listCamera.add(ac);
			}
			if(ace!=null&&listCamera!=null&&listCamera.size()>0){
				ace.setCamera(listCamera);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ace;
	}
	
	
	public AllCameraEntity getPrivi1Camera(String userId, String privilege,String page,int everypage) {
		// TODO Auto-generated method stub
		mPlaceTypeDao = new PlaceTypeDaoImpl();
		Map<String,String> map = mPlaceTypeDao.getShopTypeById();
		String pageSql = null;
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*everypage;
				int endNum = everypage;
				pageSql = new String("order by time desc limit "+startNum+" , "+everypage);
			}
		}
		String loginSql = new String("select a.area,cameraAddress,c.cameraId," +
				" cameraPwd,cameraName,latitude,longitude,principal1,principal1Phone,principal2,principal2Phone, " +
				" c.areaId,placeTypeId from camera c,useridcameraid u,areaidarea a where placeTypeId<>99 and c.cameraId=u.cameraId and u.userId=?  and a.areaid=c.areaId ");
		String sqlStr = null;
		if(pageSql!=null){
			sqlStr= new String(loginSql+pageSql);
		}else{
			sqlStr= new String(loginSql);
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		AllCameraEntity ace = null;
		try {
			ps.setString(1, userId);
			rs = ps.executeQuery();
			List<AllCamera> listCamera = null;
			while(rs.next()){
				if(ace==null){
					ace = new AllCameraEntity();
					listCamera = new ArrayList<AllCamera>();
				}
				AllCamera ac = new AllCamera();
				ac.setCameraAddress(rs.getString("cameraAddress"));
				ac.setCameraId(rs.getString("cameraId"));
				ac.setCameraName(rs.getString("cameraName"));
				ac.setCameraPwd(rs.getString("cameraPwd"));
				ac.setLatitude(rs.getString("latitude"));
				ac.setLongitude(rs.getString("longitude"));
				ac.setPrincipal1(rs.getString("principal1"));
				ac.setPrincipal1Phone(rs.getString("principal1Phone"));
				ac.setPrincipal2(rs.getString("principal2"));
				ac.setPrincipal2Phone(rs.getString("principal2Phone"));
				ac.setAreaName(rs.getString("area"));//@@
				int areaId = rs.getInt("areaId");
				String placeTypeId = rs.getString("placeTypeId");
				String placeName =null;
				if(map!=null&&map.size()>0){
					placeName = map.get(placeTypeId);
					ac.setPlaceType(placeName);
				}
				listCamera.add(ac);
			}
			if(ace!=null&&listCamera!=null&&listCamera.size()>0){
				ace.setCamera(listCamera);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return ace;
	}

	/* (non-Javadoc)
	 * @see com.cloudfire.dao.AllCameraDao#getCameraBySmokeMac(java.lang.String)
	 */
	public CameraBean getCameraBySmokeMac(String smokeMac) {
		// TODO Auto-generated method stub
		String loginSql = new String("select c.cameraAddress,c.cameraId," +
				" c.cameraPwd,c.cameraName,c.latitude,c.longitude,c.principal1,c.principal1Phone,c.principal2,c.principal2Phone, " +
				" c.areaId,c.placeTypeId,c.videoPosition,c.videoSize,c.mac1,c.mac2,c.mac3,c.mac4,c.mac5 from camera c,smoke s where s.camera = c.cameraId and s.mac = ?");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		CameraBean cb = null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(cb==null){
					cb = new CameraBean();
				}
				cb.setCameraAddress(rs.getString(1));
				cb.setCameraId(rs.getString(2));
				cb.setCameraName(rs.getString(4));
				cb.setCameraPwd(rs.getString(3));
				cb.setLatitude(rs.getString(5));
				cb.setLongitude(rs.getString(6));
				cb.setPrincipal1(rs.getString(7));
				cb.setPrincipal1Phone(rs.getString(8));
				cb.setPrincipal2(rs.getString(9));
				cb.setPrincipal2Phone(rs.getString(10));
				cb.setPosition(rs.getInt(13));
				cb.setSize(rs.getInt(14));
				cb.setMac1(rs.getString("mac1"));
				cb.setMac2(rs.getString("mac2"));
				cb.setMac3(rs.getString("mac3"));
				cb.setMac4(rs.getString("mac4"));
				cb.setMac5(rs.getString("mac5"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return cb;
	}

	public PushAlarmMsgEntity.CameraBean getCameraByCameraId(String cameraId) {
		// TODO Auto-generated method stub
		String loginSql = new String("select cameraPwd from camera where cameraId = ?");
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,loginSql);
		ResultSet rs = null;
		PushAlarmMsgEntity.CameraBean cb = null;
		try {
			ps.setString(1, cameraId);
			rs = ps.executeQuery();
			while(rs.next()){
				if(cb==null){
					cb = new PushAlarmMsgEntity.CameraBean();
				}
				cb.setCameraId(cameraId);
				cb.setCameraPwd(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return cb;
	}

}
