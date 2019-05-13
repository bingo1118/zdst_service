package com.cloudfire.dao;


import com.cloudfire.entity.DvrInfo;

public interface DvrInfoDao {
	public DvrInfo getDvrInfo();
	public DvrInfo getDvrInfoByConstructionId(int constructionId);
}
