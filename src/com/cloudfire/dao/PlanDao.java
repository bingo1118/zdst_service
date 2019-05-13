package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.Plan;
import com.cloudfire.entity.PlanPoint;

public interface PlanDao {
	public boolean addPlan(String planPath,List<PlanPoint> lstpp,int areaid);
	public boolean addPlan(String planPath, int areaid);
	public boolean deletePlan(int planid);
	public boolean updatePlan(int planid,List<PlanPoint> lstpp,int areaid);
	public boolean existsPlan(int areaid);
	public Plan getPlan(int areaid);
	public Plan getPlanByMac(String mac);
}
