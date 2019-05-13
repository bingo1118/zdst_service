package com.cloudfire.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cloudfire.server.HrOneNetServer;
import com.cloudfire.thread.OneNetDataHandleThread;
import com.cloudfire.until.Utils;


@Controller
public class OneNetToolController {
	private static String token ="gzhrsst";//用户自定义token和OneNet第三方平台配置里的token一致
//    private static String aeskey ="whBx2ZwAU5LOHVimPj1MPx56QRe3OsGGWRe4dr17crV";//aeskey和OneNet第三方平台配置里的token一致
    private static HrOneNetServer hrOneNetServer;
    
    
    /**
     * 功能描述：第三方平台数据接收。<p>
     * 注:
     * 1.OneNet平台为了保证数据不丢失，有重发机制，如果重复数据对业务有影响，数据接收端需要对重复数据进行排除重复处理。
     * 2.OneNet每一次post数据请求后，等待客户端的响应都设有时限，在规定时限内没有收到响应会认为发送失败。
     * 	  接收程序接收到数据时，尽量先缓存起来，再做业务逻辑处理。
     * @param body 数据消息
     * @return 任意字符串。OneNet平台接收到http 200的响应，才会认为数据推送成功，否则会重发。
     * @throws Exception 
     */
    @RequestMapping(value = "uploadDevices.do",method = RequestMethod.POST)
	public void uploadDevices(HttpServletResponse response,HttpServletRequest request) throws Exception{
    	//根据request对象读取body的值
    	 
    	 BufferedReader br = null;
         br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
         String line = null;
         StringBuilder oneNetString = new StringBuilder();
         while ((line = br.readLine()) != null) {
        	 oneNetString.append(line);
         }
         System.out.println("oneNetDataBody:"+oneNetString.toString());
//         hrOneNetServer = new HrOneNetServer();
//         try {
//        	 hrOneNetServer.dealOneNetString(oneNetString.toString());  //将从oneNet平台接收到的消息交给HrOneNetServer处理
//         } catch (Exception e){
//        	 e.printStackTrace();
//         }
         new OneNetDataHandleThread(oneNetString.toString()).start();
         response.getWriter().write("ok");
        	
	}
    
    
    /**
     * 功能说明： URL&Token验证接口。如果验证成功返回msg的值，否则返回其他值。
     * @param msg 验证消息
     * @param nonce 随机串
     * @param signature 签名
     * @return msg值
     * @throws Exception 
     */
    
    @RequestMapping(value = "uploadDevices.do",method = RequestMethod.GET)
	public void check(HttpServletResponse response,HttpServletRequest request) throws Exception{
    	String msg = request.getParameter("msg");
    	String nonce = request.getParameter("nonce");
    	String signature = request.getParameter("signature");
    	String jObject = "ok";
		
        if (StringUtils.isNotBlank(msg)){
        	if(Utils.checkToken(msg, nonce, signature, token)){
        		jObject =  msg;
        	}
        }else {
        	jObject =  "error";
        }
        System.out.println("resp:"+jObject);
        try {
			response.getWriter().write(jObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
