package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.SmokeBean;

public interface LoraWanDao {
	public void ifLossUpLoraWan();
	
	public List<SmokeBean> getLoraList();
}
