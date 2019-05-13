package com.cloudfire.action;

import java.io.*;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsx3.net.Request;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;
public class UploadAction extends ActionSupport implements ServletRequestAware,ServletResponseAware{
 /**
	 * 
	 */
	private static final long serialVersionUID = 4284182248457811362L;
/**
	 * 
	 */
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String title;
	 private File upload;
	 private String uploadContentType;
	 private String uploadFileName;
	 private String allowTypes;
 // 接受依赖注入的属性
 private String savePath;
 // 接受依赖注入的方法
 public void setSavePath(String value) {
  this.savePath = value;
 }
 private String getSavePath() throws Exception {
  return ServletActionContext.getServletContext().getRealPath(savePath);
 }
 public void setTitle(String title) {
  this.title = title;
 }
 public void setUpload(File upload) {
  this.upload = upload;
 }
 public void setUploadContentType(String uploadContentType) {
  this.uploadContentType = uploadContentType;
 }
 public void setUploadFileName(String uploadFileName) {
  this.uploadFileName = uploadFileName;
 }
 public String getTitle() {
  return (this.title);
 }
 public File getUpload() {
  return (this.upload);
 }
 public String getUploadContentType() {
  return (this.uploadContentType);
 }
 public String getUploadFileName() {
  return (this.uploadFileName);
 }
 @Override
 public String execute() throws Exception {
  System.out.println("开始上传单个文件---");
  System.out.println(getSavePath());
  System.out.println("==========" + getUploadFileName());
  System.out.println("==========" + getUploadContentType());
  System.out.println("==========" + getUpload());
  System.out.println("=============="+getTitle());
  
  String fileFullPath;
  String fileName=getUploadFileName();
  int startNum=fileName.indexOf("___");
  String startStr;
 
  if(startNum!=-1)
  {
	  startStr=fileName.substring(0,startNum);
	  

	  if(startStr.equals("peixun"))
	  {
		  String [] filePart=fileName.split("___", 4);
		  String dir1=filePart[1];
		  String dir2=filePart[2];
		  String fileNameChange=filePart[3];
		  String path=ServletActionContext.getServletContext().getRealPath("");
		  File file=new File(path).getParentFile();
		  String path2=file.getAbsolutePath()+"\\"+"safety";
		  fileFullPath=path2+"\\"+dir1+"\\"+dir2+"\\"+fileNameChange;
		  System.out.println("full:"+fileFullPath);
	  }
	  else if(startStr.equals("zhidu"))
	  {
		  String [] filePart=fileName.split("___",2);
		  String fileNameChange=filePart[1];
		  String path=ServletActionContext.getServletContext().getRealPath("");
		  File file=new File(path).getParentFile();
		  String path2=file.getAbsolutePath()+"\\"+"forbiddenObject";
		  fileFullPath=path2+"\\"+fileNameChange;
		  System.out.println("full zhidu:"+fileFullPath);
	  }
	  else
	  {
		  return null;
	  }
	 
  }
  else
  {
	  return null;
  }
  
 
  // 以服务器的文件保存地址和原文件名建立上传文件输出流
  FileOutputStream fos = new FileOutputStream(fileFullPath);
  
  FileInputStream fis = new FileInputStream(getUpload());
  byte[] buffer = new byte[1024];
  int len = 0;
  while ((len = fis.read(buffer)) > 0) {
   fos.write(buffer, 0, len);
  }
  return SUCCESS;
 }
 public String filterType(String[] types) {
  String fileType = this.getUploadContentType();
  for (String type : types) {
   if (type.equals(fileType)) {
    return null;
   }
  }
  return INPUT;
 }
 public String getAllowTypes() {
  return allowTypes;
 }
 public void setAllowTypes(String allowTypes) {
  this.allowTypes = allowTypes;
 }
@Override
public void setServletResponse(HttpServletResponse arg0) {
	// TODO Auto-generated method stub
	this.response=arg0;
	try {
		this.request.setCharacterEncoding("gbk");
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
@Override
public void setServletRequest(HttpServletRequest arg0) {
	// TODO Auto-generated method stub
	this.setRequest(arg0);
	try {
		this.request.setCharacterEncoding("utf-8");
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
public HttpServletRequest getRequest() {
	return request;
}
public void setRequest(HttpServletRequest request) {
	this.request = request;
}
}
