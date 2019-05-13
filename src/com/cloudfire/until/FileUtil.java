package com.cloudfire.until;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

public class FileUtil {
	
	public static String mkUpload(HttpServletRequest request) {
		/*String path = req.getSession().getServletContext().getRealPath("/"); // ��ȡ��Ŀ��������ʵ��·��
		File file = new File(path, "devimages");
		if (!file.exists()) {
			file.mkdir(); // ���devimages�ļ��в����ڣ��򴴽����ļ���
		}*/
		String temp = request.getSession().getServletContext().getRealPath(""); 
		int index = temp.lastIndexOf("\\");
		String filePath = temp.substring(0, index+1);
		String savePath = filePath+ "devimages";  //ƽ��ͼ�ļ�������
		File file = new File(savePath);
		if(!file.exists()) {
			file.mkdir();
		}
		return file.getAbsolutePath();
	}
	
	public static void save(HttpServletRequest req, FileItem item) {
		try {
			InputStream in = item.getInputStream(); // ��ȡ�ļ�������
			byte[] bytes = new byte[1024]; // ÿ��ֻ��1024���ֽ�
			int total = -1;
			FileOutputStream out = new FileOutputStream(FileUtil.mkUpload(req) + "/" + item.getName());
			while ((total = in.read(bytes)) != -1) { // �ӿͻ��˶�ȡ�ļ�
				// д���������
				out.write(bytes);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	
}