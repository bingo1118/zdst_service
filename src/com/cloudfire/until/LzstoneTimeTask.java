package com.cloudfire.until;

import java.util.TimerTask;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

public class LzstoneTimeTask extends TimerTask{
	private byte[] ack;
	private IoSession session;
	private int count = 0;
	
	public LzstoneTimeTask(byte[] ack,IoSession session){
		this.ack = ack;
		this.session = session;
	}
	
	public LzstoneTimeTask(){
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("LzstoneTimeTask:"+count);
		count++;
		if(count==10){
			this.cancel();
		}
		IoBuffer buf = IoBuffer.wrap(ack);
        WriteFuture future = session.write(buf);  
        future.awaitUninterruptibly(100);
        if( future.isWritten() ){
           System.out.println("heartAck send sucess!");
        }else{
        	System.out.println("heartAck send failed!");
        }
	}

}
