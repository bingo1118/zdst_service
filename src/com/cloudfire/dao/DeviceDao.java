package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.Devices;
import com.cloudfire.entity.SearchDto;
import com.cloudfire.entity.WorkingTime;
import com.cloudfire.entity.query.BqMacType;
import com.cloudfire.entity.query.DeviceNetState;
import com.cloudfire.until.PageBean;

public interface DeviceDao {
	public List<Devices> getAllDevices();  //��ȡ�豸����
	
	public List<CountValue> getCountNum(String areaId);    //��ȡ����ͳ�ƽ������ 
	
	public CountValue getCount(String areaId);			//���������װ�ɶ���
	
	public CountValue getCountByMac(int devType);   //�����豸����������ͳ��
	
	public CountValue getCountByMacSearch(SearchDto dto);//���ݶ������������ͳ��
	
	public CountValue getCountByPrivilege(String privilege,String areaId);

	public CountValue getCountByMacAndUserId(int devType,String userId);
	 
	 //add by daill
	public List<BQMacEntity> getBqMacEntity(String deviceId,SearchDto dto,int pageNo,int pageSize);//��ѯ���б��ص��豸����
	 
	public List<BqMacType> getBqMacType();//��ѯ���б��ص��豸����
	public List<DeviceNetState> getBqMacStatus(String deviceId);//��ѯ���еı��ص�״̬
	 
	public int getBqCount(SearchDto dto);//��ѯ�����������ܼ�¼��
	 
	public List<BQMacEntity> getBqMacEntitiesByName(String name);//���ݹ�˾�����ұ���Mac
	
	public int getDeviceTypeByMac(String mac);
	
	public String getDeviceName(int deviceType); //�����豸����id,��ȡ�豸��������
	
	public List<Integer> getAllFire(List<String> areaIds);
	
	public List<Devices> getDeviceNums(List<String> areaIds); //��ȡ�����͵��豸����
	public List<Devices> getAlarmNums(List<String> areaIds); //��ȡ�����͵���ʵ��������
	
	public List<Devices> getStaticsByDeviceType(List<String> areaIds);
	
	/**
	 * �޸��ϰ�֪ͨ������Ϣ
	 */
	public void updateUserInfo(String working,String userId);
	
	public WorkingTime selectParentIdByUserId(String id);
	
	/**
	 * ��ȡ�ϰ��û���Ϣ
	 */
	public Map<String,String> getUserWorking();
	/**
	 * ��ȡ�ϰ�֪ͨ�û���Ϣ
	 */
	public List<WorkingTime> getWorkingTime(String userId);
	
	public void uploadImg(String imgPath, String mac);
	
	public String getImgSrc(String mac);
	
	public void uploadVideo(String videoPath, String mac);
	
	public String getVideoSrc(String mac);
	
}
