package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.SmokeBean;

public interface ExportExcelDao {
	
	public void exportExcel(String filePath,List<SmokeBean> smokeList);
	
	public void exportExcelByAllDevice(String filePath,List<SmartControlEntity> smartList);
}
