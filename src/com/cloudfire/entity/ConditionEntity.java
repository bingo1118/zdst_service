package com.cloudfire.entity;

/**
 * @author lzo
 *	按多个条件查询，将查询封装成Entity数据
 */
public class ConditionEntity {
	private String deviceType;  	//设备类型1:"烟感探测器"2:"可燃气体探测器"5:"电气火灾探测器"7:"声光报警器"8:"手动报警器"9:"三江设备"
	private String userId;			//用户登录账户
	private String mac;				//设备的mac地址
	private int netState = 9;		//设备的在线状态，1为在线，0为掉线。 默认为9代表没有值传进来。
	private String startDate;		//起始时间  
	private String endDate;			//截止范围时间
	private int limit=1;				//分页参数当前页
	
	
	
	private int currentPage = 1; // 当前页, 默认显示第一页
	private int pageCount = 10;   // 每页显示的行数(查询返回的行数), 默认每页显示10行
	private int totalCount;      // 总记录数
	
	private String areaId;		//区域
	
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
	private int totalPage;       // 总页数 = 总记录数 / 每页显示的行数  (+ 1) 也是末页
	
	
	
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
