package com.cloudfire.action;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.cloudfire.dao.CompanyDao;
import com.cloudfire.dao.impl.CompanyDaoImpl;
import com.cloudfire.entity.CompanyEntity;
import com.opensymphony.xwork2.ActionSupport;

public class AddCompanyAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = -4581514309685063419L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private File frontimage1;
	private String frontimage1FileName;
	private File frontimage2;
	private String frontimage2FileName;
	private File frontimage3;
	private String frontimage3FileName;
	private File frontimage4;
	private String frontimage4FileName;
	
	
	
	public File getFrontimage2() {
		return frontimage2;
	}

	public void setFrontimage2(File frontimage2) {
		this.frontimage2 = frontimage2;
	}

	public String getFrontimage2FileName() {
		return frontimage2FileName;
	}

	public void setFrontimage2FileName(String frontimage2FileName) {
		this.frontimage2FileName = frontimage2FileName;
	}

	public File getFrontimage3() {
		return frontimage3;
	}

	public void setFrontimage3(File frontimage3) {
		this.frontimage3 = frontimage3;
	}

	public String getFrontimage3FileName() {
		return frontimage3FileName;
	}

	public void setFrontimage3FileName(String frontimage3FileName) {
		this.frontimage3FileName = frontimage3FileName;
	}

	public File getFrontimage4() {
		return frontimage4;
	}

	public void setFrontimage4(File frontimage4) {
		this.frontimage4 = frontimage4;
	}

	public String getFrontimage4FileName() {
		return frontimage4FileName;
	}

	public void setFrontimage4FileName(String frontimage4FileName) {
		this.frontimage4FileName = frontimage4FileName;
	}

	public File getFrontimage1() {
		return frontimage1;
	}

	public void setFrontimage1(File frontimage1) {
		this.frontimage1 = frontimage1;
	}

	public String getFrontimage1FileName() {
		return frontimage1FileName;
	}

	public void setFrontimage1FileName(String frontimage1FileName) {
		this.frontimage1FileName = frontimage1FileName;
	}

//	private static Logger logger = Logger.getLogger(Buildinginfo.class);
//	private static final long serialVersionUid = 1302377908285976972L; 
	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public void addCompanyInfo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		
		String path = "";
		if(frontimage1 == null || frontimage2 ==null ||frontimage3 == null ||frontimage4==null){
			path = "/WEB-INF/page/contractor/jianzhudanwei_tianjiaxinxi.jsp";
		}
		
		String imagepath = "\\fireSystem\\image\\";
		String basePath = request.getSession().getServletContext().getRealPath("/image");
		
		File saveFile1 = new File(basePath,System.currentTimeMillis()+frontimage1FileName);
		String frontimage1path = imagepath+System.currentTimeMillis()+frontimage1FileName;
		File saveFile2 = new File(basePath,System.currentTimeMillis()+frontimage2FileName);
		String frontimage2path = imagepath+System.currentTimeMillis()+frontimage2FileName;
		File saveFile3 = new File(basePath,System.currentTimeMillis()+frontimage3FileName);
		String frontimage3path = imagepath+System.currentTimeMillis()+frontimage3FileName;
		File saveFile4 = new File(basePath,System.currentTimeMillis()+frontimage4FileName);
		String frontimage4path = imagepath+System.currentTimeMillis()+frontimage4FileName;
		if(!saveFile1.getParentFile().exists()){
			saveFile1.getParentFile().mkdir();
		}
		if(!saveFile2.getParentFile().exists()){
			saveFile2.getParentFile().mkdir();
		}
		if(!saveFile3.getParentFile().exists()){
			saveFile3.getParentFile().mkdir();
		}
		if(!saveFile4.getParentFile().exists()){
			saveFile4.getParentFile().mkdir();
		}
		try {
			FileUtils.copyFile(frontimage1, saveFile1);
			FileUtils.copyFile(frontimage2, saveFile2);
			FileUtils.copyFile(frontimage3, saveFile3);
			FileUtils.copyFile(frontimage4, saveFile4);
			CompanyDao cd = new CompanyDaoImpl();
			CompanyEntity ce = new CompanyEntity();
			ce.setFrontimage1(frontimage1path);
			ce.setFrontimage2(frontimage2path);
			ce.setFrontimage3(frontimage3path);
			ce.setFrontimage4(frontimage4path);
			String comanyName = this.request.getParameter("comanyName");
			if(comanyName != null) {
				ce.setComanyName(comanyName);
			}else {
				ce.setComanyName("0");
			}
			
			String characterId = this.request.getParameter("characterId");
			if(characterId != null){
				ce.setCharacterId(Integer.parseInt(characterId));
			}else ce.setCharacterId(0);
			
			String telephone = this.request.getParameter("telephone");
			if(telephone!=null){
				ce.setTelephone(telephone);
			}else ce.setTelephone("0");
			
			String email = this.request.getParameter("email");
			if(email !=null){
				ce.setEmail(email);
			} else ce.setEmail("0");
			
			String registration = this.request.getParameter("registration");
			if(registration != null){
				ce.setRegistration(registration);
			} else ce.setRegistration("0");
			
			String workers = this.request.getParameter("workers");
			if(workers != null){
				ce.setWorkers(Integer.parseInt(workers));
			} else ce.setWorkers(0);
			
			String floorArea = this.request.getParameter("floorArea");
			if(floorArea != null){
				ce.setFloorArea(floorArea);
			} else ce.setFloorArea("0");
			
			String buildingArea = this.request.getParameter("buildingArea");
			if(buildingArea != null){
				ce.setBuildingArea(buildingArea);
			}else ce.setBuildingArea("0");
			
			String storageArea = this.request.getParameter("storageArea");
			if(storageArea !=null){
				ce.setStorageArea(storageArea);
			}else ce.setStorageArea("0");
			
			String foundTime = this.request.getParameter("foundTime");
			if(foundTime != null){
				ce.setFoundTime(foundTime);
			}else ce.setFoundTime("0");
			
			String selectNameArea = this.request.getParameter("selectNameArea");
			if(selectNameArea !=null){ //区域ID
				ce.setAreaId(Integer.parseInt(selectNameArea));
			} else ce.setAreaId(1);
			
			String involved = this.request.getParameter("involved");
			if(involved !=null){ //所属行业
				ce.setInvolved(involved);
			}else ce.setInvolved("0");
			
			String longitude = this.request.getParameter("longitude");
			if(longitude !=null){ //经度
				ce.setLongitude(longitude);
			} else ce.setLongitude("0");
			
			String latitude = this.request.getParameter("latitude");
			if(latitude !=null){ //纬度
				ce.setLatitude(latitude);
			}else ce.setLatitude("0");
			
			String adress = this.request.getParameter("adress");
			if(adress != null){ //地址
				ce.setAdress(adress);
			}else ce.setAdress("0");
			
			String dangerous = this.request.getParameter("dangerous");
			if(dangerous != null){ //危险化学物品
				ce.setDangerous(dangerous);
			}else ce.setDangerous("0");
			
			String maxdanger = this.request.getParameter("maxdanger");
			if(maxdanger != null){ //危险化学物品数量
				ce.setMaxdanger(maxdanger);
			}else ce.setMaxdanger("0");
			
			String[] fireFacility = this.request.getParameterValues("fireFacility");
			if(fireFacility.length>0){//消防设施
				String	namestr = "";
				for(int i = 0;i<fireFacility.length;i++){
					namestr = namestr + fireFacility[i]+"、";
				}
				ce.setFighting(namestr.substring(0,namestr.length()-1));
			}else ce.setFighting("0");
			
			String firelane = this.request.getParameter("firelane");
			if(firelane !=null){//消防车道
				ce.setFirelane(Integer.parseInt(firelane));
			}else ce.setFirelane(0);
			
			String safetyexit = this.request.getParameter("safetyexit");
			if(safetyexit !=null){//安全出口
				ce.setSafetyexit(Integer.parseInt(safetyexit));
			}else ce.setSafetyexit(0);
			
			String extincteur = this.request.getParameter("extincteur");
			if(extincteur !=null){//灭火器
				ce.setExtincteur(Integer.parseInt(extincteur));
			}else ce.setExtincteur(0);
			
			String elevator = this.request.getParameter("elevator");
			if(elevator !=null){//消防电梯
				ce.setElevator(Integer.parseInt(elevator));
			}else ce.setElevator(0);
			
			String staircase = this.request.getParameter("staircase");
			if(staircase !=null){//疏散楼梯
				ce.setStaircase(Integer.parseInt(staircase));
			}else ce.setStaircase(0);
			
			String marks = this.request.getParameter("marks");
			if(marks !=null){//公司信息
				ce.setMarks(marks);
			}else ce.setMarks("0");
			
			String positions = this.request.getParameter("positions");
			if(positions !=null){//职位
				ce.setPositions(positions);
			} else ce.setPositions("0");
			
			String person = this.request.getParameter("person");
			if(person !=null){//名字
				ce.setPerson(person);
			}else ce.setPerson("0");
			
			String cardid = this.request.getParameter("cardid");
			if(cardid !=null){//职位
				ce.setCardid(cardid);
			}else ce.setCardid("0");
			
			String phone = this.request.getParameter("phone");
			if(phone !=null){//职位
				ce.setPhone(phone);
			}else ce.setPhone("0");
			
			cd.companyInsert(ce);
//			path = "/mydevicesAction.do";
			path = "/WEB-INF/page/contractor/jianzhudanwei_wodeshebei.jsp";
		} catch (Exception e) {
			e.printStackTrace();
			path = "/WEB-INF/page/contractor/jianzhudanwei_tianjiaxinxi.jsp";
		}
		try {
			this.request.getRequestDispatcher(path).forward(this.request, this.response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
