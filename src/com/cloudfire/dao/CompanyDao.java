package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.CompanyEntity;

public interface CompanyDao {
	
	/**
	 * @author lzo
	 * @param ����phone����ѯcompany����Ϣ
	 * ��ѯ���Ľ�����ص�һ��List�������档
	 */
	public List<CompanyEntity> getAllCompanyBy(String phone);
	
	/**
	 * @author lzo
	 * @param ���ݱ�Company��id�ֶΣ�������ɾ��������
	 * ɾ���ɹ�����true�����򷵻�false
	 */
	public boolean removed(int id);
	
	/**
	 * @author lzo
	 * @param companyEntity
	 * ���ӱ�Company��Ϣ���ֶαȽ϶࣬�����ݷ�װ��ʵ�����У��ڽ��в������ݶ�����
	 * �������ݳɹ�����trueֵ�����򷵻�false
	 */
	public boolean companyInsert(CompanyEntity companyEntity);
	
	/**
	 * @author lzo
	 * @param ����phone�ֶ����жϴ��û��Ƿ���ڡ����ڷ���trueֵ
	 */
	public boolean ifExitCompany(String phone);
	
	/**
	 * @author lzo
	 * @param ����cardid���֤�ֶ����жϴ��û��Ƿ���ڡ����ڷ���trueֵ
	 */
	public boolean ifExitCompanyBycardId(String cardId);
	
	/**
	 * @author lzo
	 * @param ����phone�ֶ����޸��û���Ϣ
	 */
	public boolean modifyCompanyByC(CompanyEntity companyEntity);
	
}
