package com.cloudfire.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cloudfire.dao.ExportExcelDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.ExportExcelDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.SmokeBean;


@Controller
public class ExportExcelController {
	
	private ExportExcelDao exportExcel;
	private SmartControlDao smartControlDao;
	
	
	@RequestMapping(value = "/exportExcel.do",method=RequestMethod.GET)
	public void exportExcel(HttpServletRequest request,HttpServletResponse response,String currentId){
		String methodName = request.getParameter("methodName");
		String privilege  = (String) request.getSession().getAttribute("privilege");
		
		String path = "C:\\excel\\";
		if(methodName.equals("infoWeb")||methodName == "infoWeb"){
			path = path + "infoWeb.xls";
			InfoManagerDao companydao = new InfoManagerDaoImpl();
			List<SmokeBean> list = new ArrayList<SmokeBean>();
			if(privilege=="4"){
				list = companydao.getAllSmokesInfo(null);
			}else{
				list = companydao.getAllSmokesInfo(currentId);
			}
			exportExcel = new ExportExcelDaoImpl();
			exportExcel.exportExcel(path, list);
		}else if(methodName.equals("allDevice")||methodName == "allDevice"){
			path = path + "alldevice.xls";
			smartControlDao = new SmartControlDaoImpl();
			@SuppressWarnings("unchecked")
			List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
			List<SmartControlEntity> list = new ArrayList<SmartControlEntity>();
			list = smartControlDao.getAllDeviceInfo(areaIds);
			exportExcel = new ExportExcelDaoImpl();
			exportExcel.exportExcelByAllDevice(path, list);
		}
	}
}
