package com.cloudfire.until;
import java.util.List;

public class PageBean<T> {
	private List<T> data;// 当前页的数据集合
	private int pageNo;// 当前页号
	private int pageSize;// 当前页大小
	private int totalRecords;// 满足条件的总记录数

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	// 计算总页数的方法
	public int getTotalPages() {
		if (this.totalRecords % pageSize == 0)
			return this.totalRecords / pageSize;
		return this.totalRecords / pageSize + 1;
	}

	// 首页
	public int getFirst() {
		return 1;
	}

	// 末页
	public int getLast() {
		return getTotalPages();
	}

	// 上一页
	public int getUp() {
		if (this.pageNo == 1)
			return 1;
		return this.pageNo - 1;
	}

	// 下一页
	public int getDown() {
		if (this.pageNo == this.getLast())
			return this.pageNo;
		return this.pageNo + 1;
	}
}