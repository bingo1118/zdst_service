package com.cloudfire.entity;

/**
 * @author lzo
 *	�����������ѯ������ѯ��װ��Entity����
 */
public class ConditionEntity {
	private String deviceType;  	//�豸����1:"�̸�̽����"2:"��ȼ����̽����"5:"��������̽����"7:"���ⱨ����"8:"�ֶ�������"9:"�����豸"
	private String userId;			//�û���¼�˻�
	private String mac;				//�豸��mac��ַ
	private int netState = 9;		//�豸������״̬��1Ϊ���ߣ�0Ϊ���ߡ� Ĭ��Ϊ9����û��ֵ��������
	private String startDate;		//��ʼʱ��  
	private String endDate;			//��ֹ��Χʱ��
	private int limit=1;				//��ҳ������ǰҳ
	
	
	
	private int currentPage = 1; // ��ǰҳ, Ĭ����ʾ��һҳ
	private int pageCount = 10;   // ÿҳ��ʾ������(��ѯ���ص�����), Ĭ��ÿҳ��ʾ10��
	private int totalCount;      // �ܼ�¼��
	
	private String areaId;		//����
	
	public String getAreaId() {
		return areaId;
	}
	
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	private int totalPage;       // ��ҳ�� = �ܼ�¼�� / ÿҳ��ʾ������  (+ 1) Ҳ��ĩҳ
	
	
	
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		
		this.limit = (limit-1)*10;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public int getNetState() {
		return netState;
	}
	public void setNetState(int netState) {
		this.netState = netState;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
}
