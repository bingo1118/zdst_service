package com.cloudfire.until.pojo;
public class PageModel {
	/** 当前�?*/
	private int pageIndex;
	/** 每页的记录数 */
	private int pageSize;
	/** 总记录数*/
	private int recordCount;
	public int getPageIndex() {
		return pageIndex <= 1 ? 1 : pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getPageSize() {
		return pageSize <=1 ? 1 : pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	
	/*public int getStartRow(){
		return (getPageIndex()-1) * getPageSize();
	}*/
//  oracle第二个问号：(1 - 1) * 5  mysql(limit的第�?��问号) 
	public int getStartRow() {
		return (getPageIndex() - 1) * getPageSize();
	}
}