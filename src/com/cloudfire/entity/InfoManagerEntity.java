package com.cloudfire.entity;

/**
 * 信息管理表实体类
 * @author lzo
 *
 */
public class InfoManagerEntity {
	private int id;						//int(11)主键ID
	private String comanyName;				//varchar(50)企业名称
	private String comanyNature;			//varchar(30)企业性质
	private String person;					//varchar(30)企业负责人
	private String registration;			//varchar(15)注册号
	private String workers;					//int(11)工作人员数量
	private String phone;					//varchar(20)联系电话
	private String policeStation;			//varchar(30)所属派出所
	private String email;					//varchar(50)电子邮箱
	private String floorArea;				//varchar(50)区域面积
	private String buildingArea;			//varchar(50)建筑面积
	private String storageArea;				//varchar(50)楼栋面积
	private String foundTime;				//varchar(30)创立时间
	private String adress;					//varchar(50)地址
	private String longitude;				//varchar(20)经度
	private String latitude;				//varchar(20)纬度
	private String involved;				//varchar(20)所属行业
	private int areaId;						//int(4)所属区域
	private String image;					//varchar(50)相关图片
	private String marks;					//varchar(100)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getComanyName() {
		return comanyName;
	}
	public void setComanyName(String comanyName) {
		this.comanyName = comanyName;
	}
	public String getComanyNature() {
		return comanyNature;
	}
	public void setComanyNature(String comanyNature) {
		this.comanyNature = comanyNature;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getRegistration() {
		return registration;
	}
	public void setRegistration(String registration) {
		this.registration = registration;
	}
	public String getWorkers() {
		return workers;
	}
	public void setWorkers(String workers) {
		this.workers = workers;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPoliceStation() {
		return policeStation;
	}
	public void setPoliceStation(String policeStation) {
		this.policeStation = policeStation;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFloorArea() {
		return floorArea;
	}
	public void setFloorArea(String floorArea) {
		this.floorArea = floorArea;
	}
	public String getBuildingArea() {
		return buildingArea;
	}
	public void setBuildingArea(String buildingArea) {
		this.buildingArea = buildingArea;
	}
	public String getStorageArea() {
		return storageArea;
	}
	public void setStorageArea(String storageArea) {
		this.storageArea = storageArea;
	}
	public String getFoundTime() {
		return foundTime;
	}
	public void setFoundTime(String foundTime) {
		this.foundTime = foundTime;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getInvolved() {
		return involved;
	}
	public void setInvolved(String involved) {
		this.involved = involved;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getMarks() {
		return marks;
	}
	public void setMarks(String marks) {
		this.marks = marks;
	}
	
	
	
}
