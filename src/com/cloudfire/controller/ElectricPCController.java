package com.cloudfire.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cloudfire.dao.ElectricPCDao;
import com.cloudfire.dao.impl.ElectricPCDaoImpl;
import com.cloudfire.entity.MyElectricInfo;
import com.cloudfire.interceptor.NorthqinHandler2;
import com.cloudfire.until.WriteJson;
import common.page.Pagination;

@Controller
public class ElectricPCController {
	private ElectricPCDao mElectricPCDao;
	
	
	@RequestMapping(value="/electricValue.do",method=RequestMethod.GET)
	public void deviceState(HttpServletRequest request,String currentId,Integer type,HttpServletResponse response,Integer pageNo){
		pageNo = Pagination.cpn(pageNo);
		int pageSize = 20;
		String mac = (String)request.getParameter("mac");
		try {
			mElectricPCDao = new ElectricPCDaoImpl();
			List<MyElectricInfo> lists = null;
			if(type>95){
				lists = mElectricPCDao.getAllThreeValue4(type, mac);
			}else{
//				lists = mElectricPCDao.getAllValue4(type+"", mac);
				int totalCount =  mElectricPCDao.getAllValue4Count(""+type, mac);
				if (totalCount != 0) {
					lists = mElectricPCDao.getAllValue4(type+"", mac, pageNo, pageSize);
					int pages =(totalCount%20==0)? totalCount/20:totalCount/20+1;
					MyElectricInfo total = new MyElectricInfo();
					total.setSmokeMac(pages+"");
					lists.add(total);
				}
			}
			WriteJson writeJson = new WriteJson();
			if (lists!=null && lists.size()>0) {
				String object = writeJson.getJsonData(lists);
				response.getWriter().write(object);
			} else {
				response.getWriter().write("[]");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	} 
	
	@RequestMapping(value="/bqElectricValue.do",method=RequestMethod.GET)
	public void bqDeviceState(HttpServletRequest request,String currentId,String type,HttpServletResponse response){
		//String url = "http://www.northqin.com/hanrun/connection/realtime/info";
		String url="http://www.northqin.com/hanrun/connection/equip/info";
		String result = new NorthqinHandler2().sendPost(url, "auth=$apr1$");
		JSONObject  object=JSONObject.fromObject(result);
		
	} 

}
