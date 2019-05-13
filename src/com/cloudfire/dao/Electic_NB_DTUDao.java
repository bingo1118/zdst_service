package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.query.ElectricDTUQuery;

public interface Electic_NB_DTUDao {
	public List<ElectricDTUQuery> getNeedNBDTU(String mac,String type);
	
	public ElectricDTUQuery getNeedNBDTUEntity(String mac,String type);
}
