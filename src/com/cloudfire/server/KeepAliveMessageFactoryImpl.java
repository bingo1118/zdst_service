package com.cloudfire.server;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {

	public Object getRequest(IoSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getResponse(IoSession session, Object object) {
		// TODO Auto-generated method stub
		System.out.println("getResponse="+object);
		return object;
	}

	

	public boolean isResponse(IoSession session, Object object) {
		// TODO Auto-generated method stub
		System.out.println("isResponse="+object);
		return false;
	}

	public boolean isRequest(IoSession arg0, Object object) {
		// TODO Auto-generated method stub
		System.out.println("isRequest="+object);
		return false;
	}

}
