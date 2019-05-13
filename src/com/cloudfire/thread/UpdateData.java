package com.cloudfire.thread;

import java.util.Map;

import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.until.Utils;

public class UpdateData extends Thread{
	public UpdateData() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
		PublicUtils pdao = new PublicUtilsImpl();
		pdao.deleteSixData();
	}
}
