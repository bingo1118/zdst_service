package com.cloudfire.dao;

public interface DtuDao {

	 // dtu�豸��Ϣ���smoke
	public int updateDtu(String imei,String hearttime,int netState); 
	
	//dtu�ɼ���������Ϣ���waterinfo
	public int addDtuData(String imei,int state,float value,int unit,String time);
	
	//���������ݴ������ݿ�alarm
	public int addAlarmMsg(String imei,String time,int alarmType);
	
	//����imei��ȡ���������
//	public double getValueByMac(String imei);
	
	//��ѯdtudatainfo�����Ϣ
//	public List<DtuDataGroup> getDtuData()
}
