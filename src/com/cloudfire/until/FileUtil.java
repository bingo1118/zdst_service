package com.cloudfire.until;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

public class FileUtil {
	
	public static String mkUpload(HttpServletRequest request) {
		/*String path = req.getSession().getServletContext().getRealPath("/"); // 获取项目部署后的真实根路径
		File file = new File(path, "devimages");
		if (!file.exists()) {
			file.mkdir(); // 如果devimages文件夹不存在，则创建此文件夹
		}*/
		String temp = request.getSession().getServletContext().getRealPath(""); 
		int index = temp.lastIndexOf("\\");
		String filePath = temp.substring(0, index+1);
		String savePath = filePath+ "devimages";  //平面图文件夹名称
		File file = new File(savePath);
		if(!file.exists()) {
			file.mkdir();
		}
		return file.getAbsolutePath();
	}
	
	public static void save(HttpServletRequest req, FileItem item) {
		try {
			InputStream in = item.getInputStream(); // 获取文件输入流
			byte[] bytes = new byte[1024]; // 每次只读1024个字节
			int total = -1;
			FileOutputStream out = new FileOutputStream(FileUtil.mkUpload(req) + "/" + item.getName());
			while ((total = in.read(bytes)) != -1) { // 从客户端读取文件
				// 写出到服务端
				out.write(bytes);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	
}