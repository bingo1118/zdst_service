package com.cloudfire.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import com.cloudfire.dao.impl.AddCameraDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.opensymphony.xwork2.ActionSupport;

//@@9.26
@SuppressWarnings("serial")
public class UploadFileAction extends ActionSupport {
	// �ϴ��ļ���
	private File image;
	// �ϴ��ļ�����
	private String imageContentType;
	// ��װ�ϴ��ļ���
	private String imageFileName;
	// ��������ע�������
	private String savePath;

	@Override
	public String execute() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			System.out.println("��ȡAndroid�˴���������ͨ��Ϣ��");
			System.out.println("�û�����" + request.getParameter("username"));
			System.out.println("����" + request.getParameter("areaId"));
			System.out.println("�ļ�����" + request.getParameter("time"));
			System.out.println("��ȡAndroid�˴��������ļ���Ϣ��");
//			System.out.println("�ļ����Ŀ¼: " + getSavePath());
			System.out.println("�ļ�����: " + imageFileName);
			System.out.println("�ļ���С: " + image.length());
			System.out.println("�ļ�����: " + imageContentType);
			String location=request.getParameter("location");
			String filename=request.getParameter("mac");

			File dir=new File(request.getSession().getServletContext().getRealPath("")).getParentFile();
//			File dir2=new File(dir.getAbsolutePath()+"//nfcimages//"+request.getParameter("areaId"));
			File dir2;
			if(location==null){
				dir2=new File(dir.getAbsolutePath()+"//nfcimages//"+request.getParameter("areaId"));
			}else{
				dir2=new File(dir.getAbsolutePath()+"//"+location);
			}
			dir2.mkdirs();
			if(filename==null){
				fos = new FileOutputStream(dir2.getAbsolutePath() + "//" +request.getParameter("time")+ getImageFileName().substring(getImageFileName().lastIndexOf(".")));
			}else{
				fos = new FileOutputStream(dir2.getAbsolutePath() + "//" +filename+ getImageFileName().substring(getImageFileName().lastIndexOf(".")));
			}
//			fos = new FileOutputStream(dir2.getAbsolutePath() + "//" +request.getParameter("time")+ getImageFileName().substring(getImageFileName().lastIndexOf(".")));
			fis = new FileInputStream(getImage());
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			System.out.println("�ļ��ϴ��ɹ�");
			HttpRsult hr = new HttpRsult();
			hr.setError("SUSSESS");
			hr.setErrorCode(0);
			JSONObject jObject = new JSONObject(hr);
			response.getWriter().write(jObject.toString());
		} catch (Exception e) {
			System.out.println("�ļ��ϴ�ʧ��");
			e.printStackTrace();
		} finally {
			close(fos, fis);
		}
		return SUCCESS;
	}

	/**
	 * �ļ����Ŀ¼
	 * 
	 * @return
	 */
//	public String getSavePath() throws Exception {
//		return ServletActionContext.getServletContext().getRealPath(savePath);
//	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public String getImageContentType() {
		return imageContentType;
	}

	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	private void close(FileOutputStream fos, FileInputStream fis) {
		if (fis != null) {
			try {
				fis.close();
				fis = null;
			} catch (IOException e) {
				System.out.println("FileInputStream�ر�ʧ��");
				e.printStackTrace();
			}
		}
		if (fos != null) {
			try {
				fos.close();
				fis = null;
			} catch (IOException e) {
				System.out.println("FileOutputStream�ر�ʧ��");
				e.printStackTrace();
			}
		}
	}

}
