package com.cloudfire.until.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


import com.cloudfire.until.core.dao.GeneratorDao;
import com.cloudfire.until.pojo.PageModel;

/**
 * 
 * @author chengfengyun
 * @email Jiroing@163.com
 * @date 2016�?2�?7�?
 * @version 1.0
 */
public class GeneratorDaoImpl extends HibernateDaoImpl implements GeneratorDao {

	/**
	 * 主键生成的方�?
	 * @param clazz 持久化类
	 * @param field 主键对应的字�?
	 * @param parentCode 父级编号
	 * @param codeLen 生成的位�?
	 * @return 主键�?
	 */
	public String generatorKey(Class<?> clazz,String field, 
				String parentCode, int codeLen){
		// select max(code) from bos_id_resource where length(code) = 4 // 没有传父级code
		// select max(code) from bos_id_resource where length(code) = 8 and code like '0001%' // 有传父级code 0001
		StringBuilder hql = new StringBuilder();
		hql.append("select max("+ field +") from " + clazz.getSimpleName());
		hql.append(" where length("+ field +") = ?");
		List<Object> params = new ArrayList<>();
		parentCode = StringUtils.isEmpty(parentCode) ? "" : parentCode;
		params.add(parentCode.length() + codeLen);
		if (!StringUtils.isEmpty(parentCode)){
			hql.append(" and " + field + " like ?");
			params.add(parentCode + "%");
		}
		String maxCode = this.findUniqueEntity(hql.toString(), params.toArray());
		
		/** 判断�?��的code是不是空 */
		if (StringUtils.isEmpty(maxCode)){
			/** 生成前缀 */
			String prefix = "";
			for (int i = 1; i < codeLen; i++){
				prefix += "0"; // 000
			}
			return parentCode + prefix + 1; // 0001
			// 0001
			// 00040001
		}else{
			// 0003 --> 0004
			// 00010005 --> 00010006
			/** 截取�?��四位 */
			String tempCode = maxCode.substring(maxCode.length() - codeLen, maxCode.length());
			// 0003 --> 4
			// 0009 --> 10
			/** 生成后缀 */
			String suffix = String.valueOf(Integer.valueOf(tempCode) + 1);
			/** 判断生成的�?是否大于总位�?*/
			if(suffix.length() > codeLen){
				throw new RuntimeException("主键生成已越界！");
			}
			return parentCode + tempCode.substring(0, tempCode.length() - suffix.length()) + suffix;
		}
	}

	/* (non-Javadoc)
	 * @see com.cloudfire.until.core.dao.HibernateDao#findByPage(java.lang.String, com.cloudfire.until.pojo.PageModel, java.util.List)
	 */
	@Override
	public <T> List<T> findByPage(String queryString, PageModel pageModel,
			List<?> params) {
		// TODO Auto-generated method stub
		return null;
	}
}
