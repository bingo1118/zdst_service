package com.cloudfire.entity;

public class PageBeanEntity {
	private int currentPage = 1; // ��ǰҳ, Ĭ����ʾ��һҳ
	private int pageCount = 10;   // ÿҳ��ʾ������(��ѯ���ص�����), Ĭ��ÿҳ��ʾ10��
	private int totalCount;      // �ܼ�¼��
	private int totalPage;       // ��ҳ�� = �ܼ�¼�� / ÿҳ��ʾ������  (+ 1) Ҳ��ĩҳ
	private String areaId;
	
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
	
}
