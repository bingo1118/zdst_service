package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.BuildingBean;
import com.cloudfire.entity.NFC_infoEntity;
import com.cloudfire.entity.SmokeBean;

public interface MapControlDao {
	public List<SmokeBean> getAreaSmoke(String areaName, int areaId,
			String userId);

	public List<SmokeBean> getAreaSmokeLggData(String areaName, String comName,
			Integer[] areaId, String derviceId, String userId);

	public List<SmokeBean> getAreaSmokeLggNum(String areaName, String comName,
			List<Integer> areaId, String derviceId, String userId);
	public List<SmokeBean> getAreaSmokeLggNum(String areaName, String comName,
			String[] areaId, String derviceId, String userId);

	public List<SmokeBean> getAreaSmokeLgData(String areaName, Integer areaId,
			String userId, String privilege);

	public List<SmokeBean> getAreaSmokeLgNum(String areaName,
			List<?>  areaId, String userId, String privilege);
	
	// public List<SmokeBean> getAreaSmoke(int areaId);
	public List<SmokeBean> getAreaSmoke(String areaName, int areaId,
			int derviceId);

	/**
	 * @author lzo
	 * @param longitude
	 * @param latitude
	 * @return ���ݾ��Ⱥ�γ������ѯ��ȷ�����ݣ�������ת��map��ͼ����ʾ��
	 */
	public List<SmokeBean> getAreaSmokeByLongitu(String areaName,
			String longitude, String latitude, String smokeMac, String userId);

	public List<SmokeBean> getAreaSmoke(String areaName, String comName,
			String areaId, String derviceId, String userId);

	public List<NFC_infoEntity> getAreaNfc(String areaName, String areaId,
			NFC_infoEntity nfc);

	/*��ȡ�Խ�����Ϊ��λ���豸ͳ����Ϣ*/
	public List<BuildingBean> getBuildingStatics(String deviceId,String comName, String[] areaIds);
}
