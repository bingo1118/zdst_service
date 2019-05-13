package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.CompanyEntity;
import com.cloudfire.entity.MyDevicesEntityQuery;
import com.cloudfire.entity.PageBeanEntity;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.query.MainIndexEntityQuery;
import com.cloudfire.entity.query.TenanceEntityQuery;

public interface InfoManagerDao {
	
	/**
	 * @author lzo
	 * @param ���ݵ�λ���ʺ͵�λ���Ƶ��ｨ��λ����ѯcompany����Ϣ�����ؽ�����浽һ��List���������
	 */
	public List<CompanyEntity> getInfoByProvinceID(String provinceID,String companyName,String userId);
	
	/**
	 * @author lzo
	 * @param ��������������ｨ��λ����ѯcompany����Ϣ�����ؽ�����浽һ��List���������
	 */
	public List<CompanyEntity> getInfoAll(String areaId,String companyName);
	
	/**
	 * @author lzo
	 * @param ���ݽ�����λ���ƣ���λ���ʣ���ҵ��������Ѱ���ݣ����ؽ�����浽һ��List���������
	 */
	public List<CompanyEntity> getInfoAll(String companyName,String companyNature,String involed);
	
	public List<MyDevicesEntityQuery> getInfoAllCount(List<String> areaIds,MyDevicesEntityQuery query);
	
	public int getInfoCount(List<String> areaIds,MyDevicesEntityQuery query);
	
	/**
	 * @author lzo
	 * @param ����������ֱ�Ӳ�ѯ�������ݣ����ؽ�����浽һ��List���������
	 */
	public List<CompanyEntity> getInfoAlls();
	
	/**
	 * @author lzo
	 * @param ������������ѯ���ݣ����ؽ�����浽һ��List���������
	 */
	public List<CompanyEntity> getInfoAll(String areaId,TenanceEntityQuery query);
	
	public int getInfoByAreaId(String areaId,TenanceEntityQuery query); //��ȡcompany���ܼ�¼����
	
	public List<SmokeBean> getAllSmokesByQuery(String areaId,MainIndexEntityQuery query);  //��ȡsmoke����Ϣ������ѯ
	
	public int getSmokeByQuery(String areaId,MainIndexEntityQuery query);
	
	public List<SmokeBean> getAllSmokesInfo(String areaId);  //��ȡsmoke����Ϣ�����ڵ���excel��
	
	
	/**
	 * @author lzo
	 * @param �����û�ID���߱���Ȩ�޼�������ѯ���ݣ����ؽ�����浽һ��List���������
	 */
	public List<SmokeBean> getAllSmokes(String userId,int privilege);
	
	public int getTotalCount(String areaId);		//�@ȡ��ӛ䛔�

	/**
	 * @author lzo
	 * @param �����û�ID���߱���Ȩ�޼�������ѯ���ݣ����ؽ�����浽һ��List���������
	 */
	
	public List<SmokeBean> getAllSmokes(PageBeanEntity pageBean,String userId,String areaId);
	
	
	/**
	 * @author lzo
	 * @param �����û�ID����ѯ���ݣ����ؽ�����浽һ��List���������
	 */
	public List<SmokeBean> getAllSmokes(String userId);
	
	/**
	 * @author lzo
	 * @param ��������ID����ѯ���ݣ����ؽ�����浽һ��List���������
	 */
	public List<SmokeBean> getSmokesByAreaId(String areaId,PageBeanEntity pageBean,String userId);
	
	
	/**
	 * @author lzo
	 * @param ���ݳ�����������ѯ���ݣ�������С���������ؽ�����浽һ��List���������
	 */
	public List<SmokeBean> getSmokeByCharacterId(String characterId);
	
	/**
	 * @author lzo
	 * @param ���ݳ�����������ѯ���ݣ�������С���������ؽ�����浽һ��List���������
	 */
	public List<SmokeBean> getSmokeByIdAndName(String characterId,String charaId,String companyName,PageBeanEntity pageBean);
	
	/**
	 * @author lzo
	 * @param ���ݳ�����������ѯ���ݣ�������С���������ؽ�����浽һ��List���������
	 */
	public List<SmokeBean> getSmokeByIdAndName(String characterId,String charaId,String companyName,int areaId,PageBeanEntity pageBean);
	
	
	/**
	 * @author lzo
	 * @param ��ѯ������ҵ����
	 */
	public List<ShopTypeEntity> getShopTypeEntity();
	
	
	
}
