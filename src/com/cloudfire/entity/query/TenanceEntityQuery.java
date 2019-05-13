package com.cloudfire.entity.query;

import com.cloudfire.until.BaseQuery;

public class TenanceEntityQuery extends BaseQuery {

	private static final long serialVersionUID = 4144332594403986626L;
	
	private String areaIdstr;
	
	private int id;							//int(11)����ID
	private String comanyName;				//varchar(50)��ҵ����
	private String comanyNature = "0";		//varchar(30)��ҵ����
	private String person;					//varchar(30)��ҵ������
	private String registration;			//varchar(15)ע��� /Ӫҵִ�պ�
	private int workers = 0;					//int(11)������Ա����
	private String phone;					//varchar(20)��ϵ�绰
	private String policeStation;			//varchar(30)�����ɳ���
	private String email;					//varchar(50)��������
	private String floorArea;				//varchar(50)����ռ�����
	private String buildingArea;			//varchar(50)�������
	private String storageArea;				//varchar(50)¥���ֿ����
	private String foundTime;				//varchar(30)����ʱ��
	private String adress;					//varchar(50)��ַ
	private String longitude;				//varchar(20)����
	private String latitude;				//varchar(20)γ��
	private String involved;				//varchar(20)������ҵ
	private int areaId = 0;						//int(4)��������
	private String image;					//varchar(50)���ͼƬ
	private String marks;					//varchar(100)
	private int characterId = 0;				//��λ�������
	private String telephone;				//varchar(20)��λ�绰
	private String dangerous;				//Σ�ջ�ѧ��Ʒ
	private String maxdanger;				//��󴢴���
	private String fighting;				//������ʩ
	private int firelane = 0;					//������������λ��
	private int safetyexit = 0;					//��ȫ��������
	private int extincteur = 0;					//���������
	private int elevator = 0;					//������������
	private int staircase = 0;					//��ɢ¥��
	private String frontimage1;				//��λ����ͼ
	private String frontimage2;				//��ȫ���ڼ�����˨λ��ƽ��ͼ
	private String frontimage3;				//������ȫ�ص㲿λ��Ƭ
	private String frontimage4;				//����ɢƽ��ͼ
	private String positions;				//ְ��
	private String cardid;					//���֤��
	public String getAreaIdstr() {
		return areaIdstr;
	}
	public void setAreaIdstr(String areaIdstr) {
		this.areaIdstr = areaIdstr;
	}
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
	public int getWorkers() {
		return workers;
	}
	public void setWorkers(int workers) {
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
	public int getCharacterId() {
		return characterId;
	}
	public void setCharacterId(int characterId) {
		this.characterId = characterId;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getDangerous() {
		return dangerous;
	}
	public void setDangerous(String dangerous) {
		this.dangerous = dangerous;
	}
	public String getMaxdanger() {
		return maxdanger;
	}
	public void setMaxdanger(String maxdanger) {
		this.maxdanger = maxdanger;
	}
	public String getFighting() {
		return fighting;
	}
	public void setFighting(String fighting) {
		this.fighting = fighting;
	}
	public int getFirelane() {
		return firelane;
	}
	public void setFirelane(int firelane) {
		this.firelane = firelane;
	}
	public int getSafetyexit() {
		return safetyexit;
	}
	public void setSafetyexit(int safetyexit) {
		this.safetyexit = safetyexit;
	}
	public int getExtincteur() {
		return extincteur;
	}
	public void setExtincteur(int extincteur) {
		this.extincteur = extincteur;
	}
	public int getElevator() {
		return elevator;
	}
	public void setElevator(int elevator) {
		this.elevator = elevator;
	}
	public int getStaircase() {
		return staircase;
	}
	public void setStaircase(int staircase) {
		this.staircase = staircase;
	}
	public String getFrontimage1() {
		return frontimage1;
	}
	public void setFrontimage1(String frontimage1) {
		this.frontimage1 = frontimage1;
	}
	public String getFrontimage2() {
		return frontimage2;
	}
	public void setFrontimage2(String frontimage2) {
		this.frontimage2 = frontimage2;
	}
	public String getFrontimage3() {
		return frontimage3;
	}
	public void setFrontimage3(String frontimage3) {
		this.frontimage3 = frontimage3;
	}
	public String getFrontimage4() {
		return frontimage4;
	}
	public void setFrontimage4(String frontimage4) {
		this.frontimage4 = frontimage4;
	}
	public String getPositions() {
		return positions;
	}
	public void setPositions(String positions) {
		this.positions = positions;
	}
	public String getCardid() {
		return cardid;
	}
	public void setCardid(String cardid) {
		this.cardid = cardid;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
