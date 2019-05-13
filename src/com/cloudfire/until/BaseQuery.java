package com.cloudfire.until;

import java.io.Serializable;

/**
 * 条件对象公用对象
 * 
 * @author lx
 */
public class BaseQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	// ���峣�� ÿҳ��
	public final static int DEFAULT_SIZE = 10;
	// ÿҳ��
	protected int pageSize = DEFAULT_SIZE;
	// ��ʼ��
	protected int startRow;// ��ʼ��
	// ҳ��
	protected int pageNo = 1;
	// Sql��ѯ�ֶ�
	protected String fields;
	
	protected int currentPage;
	
	protected int row;
	

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
		this.startRow = (pageNo - 1) * this.pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public BaseQuery setPageSize(int pageSize) {
		this.pageSize = pageSize;
		this.startRow = (pageNo - 1) * this.pageSize;
		return this;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

}
