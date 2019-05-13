package com.cloudfire.action;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import antlr.collections.List;

import com.cloudfire.dao.SafetyStudyDao;
import com.cloudfire.dao.impl.PlaceTypeDaoImpl;
import com.cloudfire.dao.impl.SafetyStudyDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.PlaceTypeEntity;
import com.cloudfire.entity.SafetyContentListEntity;
import com.cloudfire.entity.SafetyItemEntity;
import com.opensymphony.xwork2.ActionSupport;

public class GetSafetyItemAction extends ActionSupport implements 
ServletRequestAware,ServletResponseAware{
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private SafetyStudyDao safetyStudyDao;

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response=arg0;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request=arg0;
	}
	
	public void getContentlist(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String firstDir = new String(request.getParameter("firstDir").getBytes("ISO-8859-1"),"utf-8");
			String secondDir = new String(request.getParameter("secondDir").getBytes("ISO-8859-1"),"utf-8");
//			String firstDir = this.request.getParameter("firstDir");
//			String secondDir = this.request.getParameter("secondDir");
			String path = request.getSession().getServletContext().getRealPath(""); 
			File file=new File(path).getParentFile();
			String path2=file.getAbsolutePath()+"/safety/"+firstDir+"/"+secondDir;
			java.util.List<String> list=tree(new File(path2));
			System.out.println(path2);
			Object result = null;
			SafetyContentListEntity scle=new SafetyContentListEntity();
			if(list.size()>0){
				scle.setError("获取内容成功");
				scle.setErrorCode(0);
				scle.setSafetyItems(list);
				result = scle;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getRuleContentlist(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String firstDir = new String(request.getParameter("firstDir").getBytes("ISO-8859-1"),"utf-8");
//			String firstDir = this.request.getParameter("firstDir");
//			String secondDir = this.request.getParameter("secondDir");
			String path = request.getSession().getServletContext().getRealPath(""); 
			File file=new File(path).getParentFile();
			String path2=file.getAbsolutePath()+"/rule/"+firstDir;
			java.util.List<String> list=tree(new File(path2));
			System.out.println(path2);
			Object result = null;
			SafetyContentListEntity scle=new SafetyContentListEntity();
			if(list.size()>0){
				scle.setError("获取内容成功");
				scle.setErrorCode(0);
				scle.setSafetyItems(list);
				result = scle;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getStudyItems(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			SafetyItemEntity sie = null;
			HttpRsult hr = null;
			Object result = null;
				safetyStudyDao = new SafetyStudyDaoImpl();
				sie = safetyStudyDao.getAllSafetyItem();
				if(sie==null){
					hr = new HttpRsult();
					hr.setError("获取培训内容失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					sie.setError("获取培训内容成功");
					sie.setErrorCode(0);
					result = sie;
				}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	public void getStudyRuleItems(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			SafetyItemEntity sie = null;
			HttpRsult hr = null;
			Object result = null;
				safetyStudyDao = new SafetyStudyDaoImpl();
				sie = safetyStudyDao.getAllSafetyRuleItem();
				if(sie==null){
					hr = new HttpRsult();
					hr.setError("获取培训内容失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					sie.setError("获取培训内容成功");
					sie.setErrorCode(0);
					result = sie;
				}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//显示目录的方法
    public static java.util.List<String> tree(File f){
    	java.util.List<String> filelist=  new ArrayList<String>();
        //判断传入对象是否为一个文件夹对象
        if(!f.isDirectory()){
            System.out.println("你输入的不是一个文件夹，请检查路径是否有误！！");
        }
        else{
            File[] t = f.listFiles();
            for(int i=0;i<t.length;i++){
                //判断文件列表中的对象是否为文件夹对象，如果是则执行tree递归，直到把此文件夹中所有文件输出为止
                if(t[i].isDirectory()){
                    System.out.println(t[i].getName());
//                    tree(t[i]);
                }
                else{
                	filelist.add(t[i].getName());
                    System.out.println(t[i].getName());
                }
            }
        }
        return filelist;
    }
	
    
    public void getForBiddenObject(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			
			String path = request.getSession().getServletContext().getRealPath(""); 
			File file=new File(path).getParentFile();
			String path2=file.getAbsolutePath()+"/forbiddenObject/";
			java.util.List<String> list=tree(new File(path2));
			System.out.println(path2);
			Object result = null;
			SafetyContentListEntity scle=new SafetyContentListEntity();
			if(list.size()>0){
				scle.setError("获取内容成功");
				scle.setErrorCode(0);
				scle.setSafetyItems(list);
				result = scle;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	

}
