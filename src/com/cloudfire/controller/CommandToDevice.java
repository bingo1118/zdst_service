package com.cloudfire.controller;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.until.IntegerTo16;

@Controller
public class CommandToDevice {
	@RequestMapping(value = "/cmdTodev.do" ,method = RequestMethod.GET)
	public void lookdisp(HttpServletRequest request,HttpServletResponse response,String currentId){
		String repeater = request.getParameter("repeater");
		String ackStr = request.getParameter("ackStr");
		
		byte[] ack = IntegerTo16.hexString2Bytes(ackStr);
		
		IoSession session = SessionMap.newInstance().getSession(repeater);
		 try {
	        	PrintWriter writer = response.getWriter();
				if(session!=null){
					IoBuffer buf = IoBuffer.wrap(ack);
			        WriteFuture future = session.write(buf);  
			        future.awaitUninterruptibly(100);
			       
				        if( future.isWritten() ){
				           System.out.println("heartAck send sucess!");
							writer.write("sucess");
				        }else{
				        	System.out.println("heartAck send failed!");
				        	writer.write("failed");
				        }
			       
				} else {
					writer.write("sessionnull");
				}
		 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
//	@RequestMapping(value = "/ifLineRepeater.do" ,method = RequestMethod.GET)
//	public void lookdisp(HttpServletRequest request,HttpServletResponse response){
//		String repeater = request.getParameter("repeater");
//		 try {
//	        	PrintWriter writer = response.getWriter();
//				if(RepeaterMap.newInstance().ifOffline(repeater)){
//					writer.write("notFindRepeater");
//				} else {
//					writer.write("findRepeater");
//				}
//		 } catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	
}
