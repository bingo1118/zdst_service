package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.CompanyEntity;

public interface CompanyDao {
	
	/**
	 * @author lzo
	 * @param 根据phone来查询company表信息
	 * 查询到的结果返回到一个List集合里面。
	 */
	public List<CompanyEntity> getAllCompanyBy(String phone);
	
	/**
	 * @author lzo
	 * @param 根据表Company的id字段，来进行删除操作。
	 * 删除成功返回true，否则返回false
	 */
	public boolean removed(int id);
	
	/**
	 * @author lzo
	 * @param companyEntity
	 * 增加表Company信息，字段比较多，把数据封装到实体类中，在进行插入数据动作。
	 * 插入数据成功返回true值，否则返回false
	 */
	public boolean companyInsert(CompanyEntity companyEntity);
	
	/**
	 * @author lzo
	 * @param 根据phone字段来判断此用户是否存在。存在返回true值
	 */
	public boolean ifExitCompany(String phone);
	
	/**
	 * @author lzo
	 * @param 根据cardid身份证字段来判断此用户是否存在。存在返回true值
	 */
	public boolean ifExitCompanyBycardId(String cardId);
	
	/**
	 * @author lzo
	 * @param 根据phone字段来修改用户信息
	 */
	public boolean modifyCompanyByC(CompanyEntity companyEntity);
	
}
