package com.cloudfire.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.ToolOneNetDao;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.dao.impl.ToolOneNetDaoImpl;
import com.cloudfire.entity.BodyObj;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.server.HrEasyIotServer;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


public class OneNetToolAction extends ActionSupport implements 
ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = -4581514309685063419L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HrEasyIotServer hrServer;
	private final static Log log = LogFactory.getLog(OneNetToolAction.class);
	private static String token ="hanrunkejigufenyouxiangongsi";//用户自定义token和OneNet第三方平台配置里的token一致
    private static String aeskey ="whBx2ZwAU5LOHVimPj1MPx56QRe3OsGGWRe4dr17crV";//aeskey和OneNet第三方平台配置里的token一致
	
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	private String getRequestPostData(HttpServletRequest request) {
		String result = "";
		int contentLength = request.getContentLength()+1;
		System.out.println("数据调用getRequestPostData方法"+contentLength);
		if(contentLength<0){
			return null;
		}
		byte buffer[] = new byte[contentLength];
		try {
			for (int i = 0; i <contentLength; i++) {
				int len = request.getInputStream().read(buffer,0,contentLength-1);
				if(len == -1){
					break;
				}
				i += len;
			}
			result = new String(buffer,"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String execute() throws Exception {
		ActionContext context = ActionContext.getContext();
		Map<String,Object> map = context.getParameters();
		Set<String> keys = map.keySet();
		for (String key:keys) {
			Object[] obj = (Object[])map.get(key);
			System.out.println(Arrays.toString(obj));
		}
		return NONE;
	}
	
	/**
	 * 数据上传回调
	 */
	public String uploadDevices(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			
			String body = this.request.getParameter("body");
			String msg = this.request.getParameter("msg");
			String nonce = this.request.getParameter("nonce");
			String signature = this.request.getParameter("signature");
			
			BodyObj obj = Utils.resolveBody(body, false);
			if (obj != null){
	            boolean dataRight = Utils.checkSignature(obj, token);
	            if (dataRight){
	                System.out.println("data receive: content" + obj.toString());
	            }else {
	            	 System.out.println("data receive: signature error");
	            }

	        }else {
	        	 System.out.println("data receive: body empty error");
	        }
			System.out.println("onet:>>>>>>>>>>>>"+msg+nonce+signature+"<<<<<<<<<");
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "ok";
	}
	
	public void deleteOneNetDevice(){
		this.response.setContentType("text/html;charset=utf-8");
		
		String imei = this.request.getParameter("imei");
		ToolOneNetDao tond = new ToolOneNetDaoImpl();
		boolean del = tond.delDeviceByMac(imei); //尝试从oneNet平台删除设备
		HttpRsult hr = new HttpRsult();
		if (del) {
			//将设备从smoke表删除
			SmokeDao sd = new SmokeDaoImpl();
			sd.deleteDevFromSmoke(imei);
			
			hr.setErrorCode(0);
			hr.setError("删除成功");
		}else{
			hr.setErrorCode(2);
			hr.setError("删除失败");
		}
		JSONObject jObject = new JSONObject(hr);
		try {
			this.response.getWriter().write(jObject.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
}
