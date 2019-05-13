package com.cloudfire.dao;

import java.util.List;

import com.cloudfire.entity.MyDevicesVo;

/**
 * @author cheng
 *2017-3-20
 *����1:43:45
 *������λ,�ҵ��豸����Ϣ
 */
public interface IMyDevices {
	
	public List<MyDevicesVo> getMyDevicesMsg(List<String> areaId);
	
	
	public List<MyDevicesVo> getMyDevicesByareaId(List<String> areaId);
	
	/**
	 * @author lzo
	 * @return ��ҳ��ѯ
	 */
	public List<MyDevicesVo> getMyDevicesByareaIdPage(String userId,int currentPage);
	
	public List<MyDevicesVo> getMyDevicesByareaIdPageAreaId(String areaId,String userId,int currentPage);
	
	public int getCountByUserId(String userId); //ͳ���ܼ�¼����
	
	public int getCountByUserIdAreaId(String userId,String areaId);
}
