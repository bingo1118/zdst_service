package com.cloudfire.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.ServletActionContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.impl.AddCameraDaoImpl;
import com.cloudfire.dao.impl.DevicesDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.opensymphony.xwork2.ActionSupport;

//@@9.26
@SuppressWarnings("serial")
public class UploadFileWebAction extends ActionSupport {
	
	private DeviceDao deviceDao;
	
	private String img_src;

	public String getImg_src() {
		return img_src;
	}

	public void setImg_src(String img_src) {
		this.img_src = img_src;
	}
	
	private File photo;

	public File getPhoto() {
		return photo;
	}

	public void setPhoto(File photo) {
		this.photo = photo;
	}
	
	private String photoFileName;

	public String getPhotoFileName() {
		return photoFileName;
	}

	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}
	
	private File video;
	
	public File getVideo() {
		return video;
	}

	public void setVideo(File video) {
		this.video = video;
	}
	
	private String videoFileName;
	
	public String getVideoFileName() {
		return videoFileName;
	}

	public void setVideoFileName(String videoFileName) {
		this.videoFileName = videoFileName;
	}

	@Override
	public String execute() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
//			System.out.println("文件存放目录: " + getSavePath());
			System.out.println("文件名称: " + img_src);
			System.out.println("文件大小: " + img_src.length());
			String location=request.getParameter("location");

			File dir=new File(request.getSession().getServletContext().getRealPath("")).getParentFile();
			File dir2;
			if(location==null){
				dir2=new File(dir.getAbsolutePath()+"//nfcimages//"+request.getParameter("areaId"));
			}else{
				dir2=new File(dir.getAbsolutePath()+"//"+location);
			}
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			System.out.println("文件上传成功");
			HttpRsult hr = new HttpRsult();
			hr.setError("SUSSESS");
			hr.setErrorCode(0);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		} catch (Exception e) {
			System.out.println("文件上传失败");
			e.printStackTrace();
		} finally {
			close(fos, fis);
		}
		return SUCCESS;
	}

	/**
	 * 文件存放目录
	 * 
	 * @return
	 */
//	public String getSavePath() throws Exception {
//		return ServletActionContext.getServletContext().getRealPath(savePath);
//	}


	private void close(FileOutputStream fos, FileInputStream fis) {
		if (fis != null) {
			try {
				fis.close();
				fis = null;
			} catch (IOException e) {
				System.out.println("FileInputStream关闭失败");
				e.printStackTrace();
			}
		}
		if (fos != null) {
			try {
				fos.close();
				fis = null;
			} catch (IOException e) {
				System.out.println("FileOutputStream关闭失败");
				e.printStackTrace();
			}
		}
	}
	
	public void uploadImg() throws IOException, JSONException{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String mac = request.getParameter("img_mac");
		System.out.println("进入了uploadImg()方法，mac:"+mac);
		//String fileName = request.getSession().getServletContext().getRealPath("/devimages/"+photoFileName);
		/*String fileName = request.getSession().getServletContext().getRealPath("/devimages/"+mac+photoFileName.substring(photoFileName.lastIndexOf(".")));
		File file = new File(fileName);*/
		File dir = new File(request.getSession().getServletContext().getRealPath("")).getParentFile();
		File file = new File(dir.getAbsolutePath()+"/devimages/"+mac+photoFileName.substring(photoFileName.lastIndexOf(".")));
		System.out.println("dir:"+dir+"dir2:"+file);
        System.out.println("photo："+photo);
		if(photo == null) {
			HttpRsult hr = new HttpRsult();//图片未传过来
			hr.setError("2");
			hr.setErrorCode(1);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}else{
			try {
				System.out.println("file:"+file);
	            FileUtils.copyFile(photo, file);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			String extension = "";
			int i = photoFileName.lastIndexOf('.');
			if (i > 0) {
			    extension = photoFileName.substring(i+1);
			}
			if("jpg".equals(extension)){
				String imgSrc =  mac+photoFileName.substring(photoFileName.lastIndexOf("."));
				deviceDao = new DevicesDaoImpl();
				deviceDao.uploadImg(imgSrc, mac);//保存路径到数据库
				HttpRsult hr = new HttpRsult();
				hr.setError(imgSrc);
				hr.setErrorCode(0);
				JSONObject jObject = new JSONObject(hr);
				response.getWriter().write(jObject.toString());
			}else{
				HttpRsult hr = new HttpRsult();//格式不正确
				hr.setError("1");
				hr.setErrorCode(1);
				JSONObject jObject = new JSONObject(hr);
				response.getWriter().write(jObject.toString());
			}
		}
	}
	
	
	public void getImgSrc() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String mac = request.getParameter("img_mac");
		deviceDao = new DevicesDaoImpl();
		if(request.getParameter("img_mac") == null || deviceDao.getImgSrc(mac) == null || deviceDao.getImgSrc(mac).equals("")) {
			HttpRsult hr = new HttpRsult();
			hr.setError("1");
			hr.setErrorCode(1);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}else{
			String imgSrc = deviceDao.getImgSrc(mac);
			HttpRsult hr = new HttpRsult();
			hr.setError(imgSrc);
			hr.setErrorCode(0);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}
	}
	
	public void uploadVideo() throws IOException, JSONException{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String mac = request.getParameter("video_mac");
		System.out.println("进入了uploadVideo方法，mac:"+mac);
		//String fileName = request.getSession().getServletContext().getRealPath("/devimages/"+photoFileName);
		/*String fileName = request.getSession().getServletContext().getRealPath("/devVideos/"+mac+videoFileName.substring(videoFileName.lastIndexOf(".")));
        File file = new File(fileName);*/
        File dir = new File(request.getSession().getServletContext().getRealPath("")).getParentFile();
		File file = new File(dir.getAbsolutePath()+"/devVideos/"+mac+videoFileName.substring(videoFileName.lastIndexOf(".")));
        System.out.println("video:"+video);
		if(video == null) {
			//out.write("{\"uploadImg\":\"0\"}");//图片不存在
			HttpRsult hr = new HttpRsult();
			hr.setError("2");
			hr.setErrorCode(1);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}else{
			String extension = "";
			int i = videoFileName.lastIndexOf('.');
			if (i > 0) {
			    extension = videoFileName.substring(i+1);
			}
			if("mp4".equals(extension)){
				FileUtils.copyFile(video, file);
				String videoSrc =  mac+videoFileName.substring(videoFileName.lastIndexOf("."));
		          deviceDao = new DevicesDaoImpl();
				  deviceDao.uploadVideo(videoSrc, mac);//保存路径到数据库
				  HttpRsult hr = new HttpRsult();
				  System.out.println("videoSrc:"+videoSrc);
			      hr.setError(videoSrc);
				  hr.setErrorCode(0);
				  JSONObject jObject = new JSONObject(hr);
				  response.getWriter().write(jObject.toString());
				  System.out.println("视频为mp4格式");
			}else {
				HttpRsult hr = new HttpRsult();//格式不正确
				hr.setError("视频格式不正确");
				hr.setErrorCode(1);
				JSONObject jObject = new JSONObject(hr);
				response.getWriter().write(jObject.toString());
				System.out.println("视频格式不正确");
			}
		
          /*FileUtils.copyFile(video, file);
          String videoSrc =  mac+videoFileName.substring(videoFileName.lastIndexOf("."));
          deviceDao = new DevicesDaoImpl();
		  deviceDao.uploadVideo(videoSrc, mac);//保存路径到数据库
		  HttpRsult hr = new HttpRsult();
		  System.out.println("videoSrc:"+videoSrc);
	      hr.setError(videoSrc);
		  hr.setErrorCode(0);
		  JSONObject jObject = new JSONObject(hr);
		  response.getWriter().write(jObject.toString());*/
		}
	}
	
	public void getVideoSrc() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String mac = request.getParameter("img_mac");
		deviceDao = new DevicesDaoImpl();
		if(request.getParameter("img_mac") == null || deviceDao.getVideoSrc(mac) == null || deviceDao.getVideoSrc(mac).equals("")) {
			HttpRsult hr = new HttpRsult();
			hr.setError("1");
			hr.setErrorCode(1);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}else{
			String videoSrc = deviceDao.getVideoSrc(mac);
			HttpRsult hr = new HttpRsult();
			hr.setError(videoSrc);
			hr.setErrorCode(0);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		}
	}
	
}
