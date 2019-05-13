package com.cloudfire.until;

import java.io.Serializable;

/**
 * 鏉′欢瀵硅薄鍏敤瀵硅薄
 * 
 * @author lx
 */
public class BaseQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	// 定义常量 每页数
	public final static int DEFAULT_SIZE = 10;
	// 每页数
	protected int pageSize = DEFAULT_SIZE;
	// 起始行
	protected int startRow;// 起始行
	// 页码
	protected int pageNo = 1;
	// Sql查询字段
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
