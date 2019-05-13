package com.cloudfire.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

import com.cloudfire.entity.TimerMap;
import com.cloudfire.entity.UserMap;
import com.cloudfire.until.Utils;


public class AckElecContrThread extends Thread{
	private byte[] ack;
	private IoSession session;
	private String repeaterMac;
	public int sessionNum = 0;
	
	public AckElecContrThread(byte[] ack,IoSession session,String repeaterMac){
		this.ack = ack;
		this.session = session;
		this.repeaterMac = repeaterMac;
	}
	
	@Override
	public void run() {
		IoBuffer buf = IoBuffer.wrap(ack);
        WriteFuture future = session.write(buf);  
        future.awaitUninterruptibly(100);
        if( future.isWritten() ){
           System.out.println("heartAck send sucess!"+this.repeaterMac+"================00000000000000");
        }else{
        	System.out.println("heartAck send failed!"+this.repeaterMac+"================00000000000000");
        }
	}
}
