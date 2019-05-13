package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.PlanDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.Plan;
import com.cloudfire.entity.PlanPoint;

public class PlanDaoImpl implements PlanDao {


	@Override
	public Plan getPlan(int areaid) {
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Plan plan =null;
		String sql ="select planPath,mac,macX,macY,area from plan p,areaidarea a where p.areaid = a.areaid and p.areaid = "+areaid;
		ps =DBConnectionManager.prepare(conn, sql);
		try {
			List<PlanPoint> lstpp = new ArrayList<PlanPoint>(); 
			String planPath = "";
			String areaName="";
			rs = ps.executeQuery(); 
			while (rs.next()){
				PlanPoint pp = new PlanPoint();
				pp.setMac(rs.getString("mac"));
				pp.setMacX(rs.getFloat("macX"));
				pp.setMacY(rs.getFloat("macY"));
				planPath=rs.getString("planPath");
				areaName=rs.getString("area");
				if(pp.getMac() == null || "".equals(pp.getMac())) {
					continue;
				}
				lstpp.add(pp);
			}
			plan = new Plan();
			plan.setPlanPath(planPath);
			plan.setLstpp(lstpp);
			plan.setAreaid(areaid);
			plan.setAreaName(areaName);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return plan;
	}

	@Override
	public boolean addPlan(String planPath, List<PlanPoint> lstpp, int areaid) {
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		
		try {
			for (PlanPoint pp : lstpp) {
				boolean flag=exist(pp.getMac());
				String sql = "";
				if (flag) {
					sql = "update plan set macX = ?,macY = ? where mac = ?";
				} else {
					sql = "insert into plan(planPath,macX,macY,mac,areaid) values(?,?,?,?,?)";
				}
				ps = DBConnectionManager.prepare(conn, sql);
				if (flag) {
					ps.setString(1, pp.getLeft());
					ps.setString(2, pp.getTop());
					ps.setString(3, pp.getMac());
				} else {
					ps.setString(1, planPath);
					ps.setString(2, pp.getLeft());
					ps.setString(3, pp.getTop());
					ps.setString(4, pp.getMac());
					ps.setInt(5, areaid);
				}
				ps.executeUpdate();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return true;
	}

	private boolean exist(String mac) {
		String sql="select planid from plan where mac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs=null;
		try {
			ps.setString(1, mac);
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return false;
	}

	public boolean existsPlan(int areaid) {
		boolean flag = false;
		String sql="select planid from plan where areaid = ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		try {
			ps.setInt(1, areaid);
			rs = ps.executeQuery();
			if (rs.next()) {
				flag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return flag;
	}
	
	@Override
	public boolean addPlan(String planPath, int areaid) {
		boolean result=false;
		String sql="insert into plan(planPath,areaid) values(?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		try {
			ps.setString(1, planPath);
			ps.setInt(2, areaid);
			int rs=ps.executeUpdate();
			if(rs >0) {
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

	@Override
	public boolean deletePlan(int planid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updatePlan(int planid, List<PlanPoint> lstpp, int areaid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Plan getPlanByMac(String mac) {
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Plan plan =null;
		if(!exist(mac)){
			return null;
		}
		String sql ="select * from plan where mac = ?";
		ps =DBConnectionManager.prepare(conn, sql);
		try {
			List<PlanPoint> lstpp = new ArrayList<PlanPoint>(); 
			String planPath = "";
			int planid = -1;
			ps.setString(1, mac);
			rs = ps.executeQuery(); 
			if (rs.next()){
				PlanPoint pp = new PlanPoint();
				pp.setMac(rs.getString("mac"));
				pp.setMacX(rs.getFloat("macX"));
				pp.setMacY(rs.getFloat("macY"));
				planPath=rs.getString("planPath");
				planid = rs.getInt("planid");
				lstpp.add(pp);
			}
			plan = new Plan();
			plan.setPlanPath(planPath);
			plan.setLstpp(lstpp);
			plan.setPlanid(planid);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return plan;
	}

}
