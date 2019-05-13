package com.cloudfire.until.pojo;

import java.util.List;

/**
 * @author cheng
 *2017-4-27
 *����5:50:31
 */
public class PageBean {
	private Integer firstPage; // ��һҳ
	private Integer totalPage; // ��ҳ�������һҳ
	private Integer currentPage; // ��ǰҳ
	private Integer previousPage; // ��һҳ
	private Integer nextPage; // ��һҳ
	private Integer pageSize; // ҳ��Ĵ�С
	private Integer totalCount; // �ܼ�¼��
	private List<?> data; // ��ǰҳ������
	

	public Integer getFirstPage() {
		return 1;  	//�õ���һҳ
	}

	public void setFirstPage(Integer firstPage) {
		this.firstPage = firstPage;
	}

	/**
	 * ��ҳ��
	 * @return
	 */
	public Integer getTotalPage() {  
		//��������� % ҳ���С == 0�������������ǵ��̡����������0�������̼�1.
		if (totalCount % pageSize == 0) {
			totalPage = totalCount / pageSize;
		}
		else {
			totalPage = totalCount / pageSize + 1;
		}
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * ������һҳ
	 * @return
	 */
	public Integer getPreviousPage() {
		//��ǰҳ����1
		if (currentPage > 1) {
			previousPage = currentPage - 1;
		}
		else {
			previousPage = 1;
		}
		return previousPage;
	}

	public void setPreviousPage(Integer previousPage) {
		this.previousPage = previousPage;
	}

	/**
	 * ������һҳ
	 * @return
	 */
	public Integer getNextPage() {
		//��ǰҳС�����һҳ
		if (currentPage < getTotalPage()) {
			nextPage = currentPage + 1;
		}
		else {
			//��һҳ�͵������һҳ
			nextPage = totalPage;
		}
		return nextPage;
	}

	public void setNextPage(Integer nextPage) {
		this.nextPage = nextPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

}
