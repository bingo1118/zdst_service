package com.cloudfire.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;


import com.cloudfire.dao.PlanDao;
import com.cloudfire.dao.impl.PlanDaoImpl;
import com.cloudfire.entity.Plan;
import com.cloudfire.entity.PlanPoint;
import com.cloudfire.entity.SmokeBean;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class PlanAction extends ActionSupport implements 
ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = -1521072896542407509L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private File plan;
	private String planFileName;
	private List<PlanPoint> lstPP;
	
	public List<PlanPoint> getLstPP() {
		return lstPP;
	}

	public void setLstPP(List<PlanPoint> lstPP) {
		this.lstPP = lstPP;
	}

	public String getPlanFileName() {
		return planFileName;
	}

	public void setPlanFileName(String planFileName) {
		this.planFileName = planFileName;
	}

	public File getPlan() {
		return plan;
	}

	public void setPlan(File plan) {
		this.plan = plan;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void addPlan() throws JsonParseException, JsonMappingException { 
		boolean result = false;
		 //创建保存图片的文件
		System.out.println("into action first");
		String temp = request.getSession().getServletContext().getRealPath(""); 
		int index=temp.lastIndexOf("\\");
		String filePath=temp.substring(0, index+1);
		String savePath = filePath+ "plans";  //平面图文件夹名称
		File plans = new File(savePath);
		if(!plans.exists()) {
			plans.mkdir();
		}
		
		String planPath = "";
		
		String planArea = request.getParameter("planArea");
		int areaid =  1;
		Plan plan2 = null;
		PlanDao pd = new PlanDaoImpl();
		if (!planArea.equals("")) {
			areaid = Integer.parseInt(planArea);  //获取图片对应的areaId
			plan2 = pd.getPlan(areaid);  //根据areaid获取平面图的信息
		}
		
		
//		List<PlanPoint> lstPP = null;
//		lstPP = request.getParameter("lstPP");
//		if (lstPP!=null){
//			System.out.println(lstPP.size());
//		}
//		String smokes = request.getParameter("smokes");
	
		System.out.println("into action first get some parameters");
		
		try {
//			if ( smokes != null && !smokes.equals("[]") ) {
//				JSONArray fromObject = JSONArray.fromObject(smokes);
//				lstPP = new ArrayList<PlanPoint>();
//				for(int i=0;i<fromObject.size();i++){
//					PlanPoint pp= new PlanPoint();
//					Object object = fromObject.get(i);
//					if(object.toString().equals("null")){
//						continue;
//					}
//					JSONObject ob= (JSONObject)object;
//					try {
//						pp.setMac(ob.getString("mac"));
//						pp.setMacX(Float.parseFloat(ob.getString("left")));
//						pp.setMacY(Float.parseFloat(ob.getString("top")));
//						lstPP.add(pp);
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				
//				System.out.println("into action first smokes is not null");
//			}
			
			if (plan == null) { //没有上传图片
				if (lstPP != null && lstPP.size() > 0) { // 入库
					planPath = plan2.getPlanPath();
			  		result = pd.addPlan(planPath,lstPP, areaid);
			  	} 
			} else  { //有上传图片
				System.out.println("into action first plan is not null");
				if (plan2.getPlanPath().equals("")) { //平面图不存在
					planPath = System.currentTimeMillis()+planFileName;
				} else {  //已有平面图
					planPath = plan2.getPlanPath();
				}
				
				FileInputStream fis = new FileInputStream(plan);
				FileOutputStream fos=new FileOutputStream(savePath+"\\"+planPath);
			    byte[] b = new byte[10*1024]; 
			    int byteCount=0;
			    while((byteCount =fis.read(b))!=-1){  
			      fos.write(b,0,byteCount);  
			    } 
			    fis.close();
			  	fos.close();
			  	
			  	System.out.println("into action first file uploaded");
			  	if (lstPP != null && lstPP.size() > 0) { // 入库
			  		result = pd.addPlan(planPath,lstPP, areaid);
			  		System.out.println("入库成功");
			  	} else {
			  		if (!pd.existsPlan(areaid)) { //平面图不存在
			  			result = pd.addPlan(planPath, areaid); 
			  		} else {
			  			
			  		}
			  	}
			  	System.out.println("into action first smokes in database");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		request.setAttribute("planArea", planArea);
		try {
			System.out.println("into action first go to controller");
//			request.getRequestDispatcher("addsb.jsp").forward(request,response);
			request.getRequestDispatcher("getPlan.do").forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
}
