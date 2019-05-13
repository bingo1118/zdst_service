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
	private static String token ="gzhrsst";//�û��Զ���token��OneNet������ƽ̨�������tokenһ��
//    private static String aeskey ="whBx2ZwAU5LOHVimPj1MPx56QRe3OsGGWRe4dr17crV";//aeskey��OneNet������ƽ̨�������tokenһ��
    private static HrOneNetServer hrOneNetServer;
    
    
    /**
     * ����������������ƽ̨���ݽ��ա�<p>
     * ע:
     * 1.OneNetƽ̨Ϊ�˱�֤���ݲ���ʧ�����ط����ƣ�����ظ����ݶ�ҵ����Ӱ�죬���ݽ��ն���Ҫ���ظ����ݽ����ų��ظ�����
     * 2.OneNetÿһ��post��������󣬵ȴ��ͻ��˵���Ӧ������ʱ�ޣ��ڹ涨ʱ����û���յ���Ӧ����Ϊ����ʧ�ܡ�
     * 	  ���ճ�����յ�����ʱ�������Ȼ�������������ҵ���߼�����
     * @param body ������Ϣ
     * @return �����ַ�����OneNetƽ̨���յ�http 200����Ӧ���Ż���Ϊ�������ͳɹ���������ط���
     * @throws Exception 
     */
    @RequestMapping(value = "uploadDevices.do",method = RequestMethod.POST)
	public void uploadDevices(HttpServletResponse response,HttpServletRequest request) throws Exception{
    	//����request�����ȡbody��ֵ
    	 
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
//        	 hrOneNetServer.dealOneNetString(oneNetString.toString());  //����oneNetƽ̨���յ�����Ϣ����HrOneNetServer����
//         } catch (Exception e){
//        	 e.printStackTrace();
//         }
         new OneNetDataHandleThread(oneNetString.toString()).start();
         response.getWriter().write("ok");
        	
	}
    
    
    /**
     * ����˵���� URL&Token��֤�ӿڡ������֤�ɹ�����msg��ֵ�����򷵻�����ֵ��
     * @param msg ��֤��Ϣ
     * @param nonce �����
     * @param signature ǩ��
     * @return msgֵ
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
