package com.cloudfire.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.entity.RepeaterBean;
import com.cloudfire.entity.SmokeInfo;
import com.cloudfire.until.OneNetHttpMethod;
import com.cloudfire.until.WriteJson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
@Controller
public class GetAllSmokeMessage {
	private SmokeDao smokeDao;
	
	@RequestMapping(value="getSomeSmokes.do" ,method=RequestMethod.GET)
	public  ModelAndView getAllssss(HttpServletRequest request){
		 ModelAndView modelAndView = new ModelAndView();
		 modelAndView.setViewName("/otherSmokeInfo");
		 return modelAndView;
	   	
   }
	/**
	 * 访问客户平台的外部方法，再将数据write到公司平台（json格式的String）
	 * 远程获取客户平台数据库中的设备数据
	 * @param request
	 * @return
	 *//*
	@RequestMapping(value="searchSomeSmokes.do" ,method=RequestMethod.GET)
	public ModelAndView getAllSmokeInfo(HttpServletRequest request){
		 String  ip = request.getParameter("ip");//客户平台ip
		 String named = request.getParameter("named");//客户平台名称
		 ModelAndView modelAndView = new ModelAndView(); 
		 String url = "http://"+ip+":51091/"+named+"/getAllSmokeInfo.do";
	   	 String result = "";
	   	 CloseableHttpClient httpClient = HttpClients.createDefault();
	   	 CloseableHttpResponse response = null;
	   	 List<SmokeInfo> ps = new ArrayList<SmokeInfo>();
	   	 try {
	        URIBuilder builder = new URIBuilder(url);
	        HttpGet get = new HttpGet(builder.build());
	        get.setHeader("Content-Type","application/json");
	        response = httpClient.execute(get);
	        if(response != null && response.getStatusLine().getStatusCode() == 200)
	        {
	            HttpEntity entity = response.getEntity();
	            result = OneNetHttpMethod.entityToString(entity); 
	            ps = (new Gson()).fromJson(result, new TypeToken<List<SmokeInfo>>(){}.getType());
	            smokeDao = new SmokeDaoImpl();
	            int ifShow = 0;
	            ifShow = smokeDao.selectIfShow();
	            for (int i = 0; i < ps.size(); i++) {
					ps.get(i).setNetState(1);//将所有设备的状态标记为在线
					ps.get(i).setIfAlarm(1);//正常状态未报警
					ps.get(i).setAreaId(8012);//将所有数据放入一个区域
					//先查看数据库中该数据的ifshow值来给定新加入数据的ifshow值
					if(ifShow == 0 ){
						ps.get(i).setIfShow(0);
					}else{
						ps.get(i).setIfShow(1);
					}
					//将客户平台数据存入到公司平台数据库中
		            //存在就修改不存在就新增
					boolean b = smokeDao.addOrUpdateSmoke(ps.get(i));
	            }
	            System.out.println(ps.size());
	            
	        }
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally {
	        try {
	            httpClient.close();
	            if(response != null)
	            {
	                response.close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	   	modelAndView.addObject("ps", ps);
	   	modelAndView.setViewName("/otherSmokeInfo");
	    return modelAndView;
	}*/
	/**
	 * 开启或者关闭客户平台数据显示操作
	 * 0代表关闭 1代表开启
	 * @param request
	 * @param response
	 *//*
	@RequestMapping(value="/openOrClose.do",method=RequestMethod.GET)
	public void openOrClose(HttpServletRequest request,HttpServletResponse response){
		smokeDao = new SmokeDaoImpl();
		int ifShow = Integer.parseInt(request.getParameter("ifShow"));
		boolean bool =false;
		if(ifShow == 1){
			bool = smokeDao.updateIfShow(1);
			request.getSession().setAttribute("ifShow", 1);
		}else{
			request.getSession().setAttribute("ifShow", 0);
			bool = smokeDao.updateIfShow(0);
		}
		String jObject = (new WriteJson()).getJsonStr(bool);
		try {
			response.getWriter().write(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
}
