package com.cloudfire.until.core.dao;
/**
 * 
 * @author chengfengyun
 * @email Jiroing@163.com
 * @date 2016å¹?2æœ?7æ—?
 * @version 1.0
 */
public interface GeneratorDao extends HibernateDao {
	String generatorKey(Class<?> clazz,String field, String parentCode, int codeLen);
}
