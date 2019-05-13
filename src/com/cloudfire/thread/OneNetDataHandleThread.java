package com.cloudfire.thread;

import com.cloudfire.server.HrOneNetServer;

public class OneNetDataHandleThread extends Thread {
	private String data;
	
	public OneNetDataHandleThread(String data){
		this.data = data;
	}

	@Override
	public void run() {
		HrOneNetServer hrOneNetServer = new HrOneNetServer();
		 try {
			hrOneNetServer.dealOneNetString(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
