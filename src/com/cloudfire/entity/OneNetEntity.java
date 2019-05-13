package com.cloudfire.entity;

import com.cloudfire.action.MsgOneNetEntity;

public class OneNetEntity {
	private String msg_signature;
	private String nonce;
	
	private MsgOneNetEntity msg;
	

	public String getMsg_signature() {
		return msg_signature;
	}

	public void setMsg_signature(String msg_signature) {
		this.msg_signature = msg_signature;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public MsgOneNetEntity getMsg() {
		return msg;
	}

	public void setMsg(MsgOneNetEntity msg) {
		this.msg = msg;
	}
}
