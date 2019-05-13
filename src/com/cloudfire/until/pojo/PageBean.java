package com.cloudfire.until.pojo;

import java.util.List;

/**
 * @author cheng
 *2017-4-27
 *下午5:50:31
 */
public class PageBean {
	private Integer firstPage; // 第一页
	private Integer totalPage; // 总页数或最后一页
	private Integer currentPage; // 当前页
	private Integer previousPage; // 上一页
	private Integer nextPage; // 下一页
	private Integer pageSize; // 页面的大小
	private Integer totalCount; // 总记录数
	private List<?> data; // 当前页的数据
	

	public Integer getFirstPage() {
		return 1;  	//得到第一页
	}

	public void setFirstPage(Integer firstPage) {
		this.firstPage = firstPage;
	}

	/**
	 * 总页数
	 * @return
	 */
	public Integer getTotalPage() {  
		//如果总条数 % 页面大小 == 0，则正好是它们的商。如果不等于0，则是商加1.
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
	 * 计算上一页
	 * @return
	 */
	public Integer getPreviousPage() {
		//当前页大于1
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
	 * 计算下一页
	 * @return
	 */
	public Integer getNextPage() {
		//当前页小于最后一页
		if (currentPage < getTotalPage()) {
			nextPage = currentPage + 1;
		}
		else {
			//下一页就等于最后一页
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
