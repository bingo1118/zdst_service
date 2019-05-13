package com.cloudfire.interceptor;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;

import javax.servlet.ServletContext;

import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.SmokeSummaryDao;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.dao.impl.SmokeSummaryDaoImpl;
import com.cloudfire.entity.BQMacEntity;
import com.cloudfire.entity.SmokeBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 与北擎数据对接的监听器中需要调用的任务
 * 
 * @author daill
 * */



public class NorthqinTask extends TimerTask {
	private ServletContext context = null;

	public NorthqinTask(ServletContext context) {
		this.context = context;
	}
	
	public void test123(){
		System.out.println("11111111111111");
		System.out.println("2222222222222222");
	}

	@Override
	public void run() {
		System.out.println("------成功添加北擎数据接口监听器------");
		String url = "http://www.northqin.com/hanrun/connection/equip/info";
		String result = new NorthqinHandler2().sendPost(url, "auth=$apr1$");
		JSONArray array = JSONArray.fromObject(result);
		SmokeSummaryDao smokeDao = new SmokeSummaryDaoImpl();
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = array.getJSONObject(i);
			BQMacEntity bqMac = (BQMacEntity) object.toBean(object,
					BQMacEntity.class);
			boolean flag = smokeDao.selectSmokeMacById(bqMac.getDeviceId());// 判断设备是否存在
			if (flag == false) {
				smokeDao.insertSmokeMac(bqMac);
				this.inserHistoryData(bqMac);
			} else {
				this.inserHistoryData(bqMac);
			}

		}

	}

	// common方法处理json数组
	public void dealArray(BQMacEntity bqMac, JSONArray array, String keyString,
			Entry entry) {
		JSONArray temp1Array = (JSONArray) entry.getValue();
		SmokeSummaryDao smokeDao = new SmokeSummaryDaoImpl();
		for (int j = 0; j < temp1Array.size(); j++) {
			JSONObject temp1Obj = temp1Array.getJSONObject(j);
			String nodeValue = temp1Obj.getString("nodeValue");
			String createtime = (String) temp1Obj.get("createTime");
			if (smokeDao.selectEleInfoByTime( bqMac.getDeviceId(),createtime)) {// 判断创建时间是否存在
				if (!smokeDao.selectEleInfoByTimeValue(createtime, keyString,
						nodeValue)) {
					smokeDao.updateByType(bqMac,keyString, nodeValue, createtime);
				}
			} else {
				smokeDao.insertByType(bqMac, keyString, nodeValue, createtime);
			}
		}
	}
	
	//录入北擎历史数据
	public void inserHistoryData(BQMacEntity bqMac){
		String url2 = "http://www.northqin.com/hanrun/connection/history/info";
		String historyResult = new NorthqinHandler2().sendPost(url2,
				"auth=$apr1$&deviceId=" + bqMac.getDeviceId());
		JSONObject json = JSONObject.fromObject(historyResult);
		Set set = json.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();
			String keyString = (String) entry.getKey();
			JSONArray valueArray = (JSONArray) entry.getValue();
			if (keyString.equals("temperature1")) {
				this.dealArray(bqMac, valueArray, keyString, entry);
			} else if (keyString.equals("temperature2")) {
				this.dealArray(bqMac, valueArray, keyString, entry);
			} else if (keyString.equals("temperature3")) {
				this.dealArray(bqMac, valueArray, keyString, entry);
			} else if (keyString.equals("temperature4")) {
				this.dealArray(bqMac, valueArray, keyString, entry);
			} else if (keyString.equals("eleCurrent1")) {
				this.dealArray(bqMac, valueArray, keyString, entry);
			} else if (keyString.equals("eleCurrent2")) {
				this.dealArray(bqMac, valueArray, keyString, entry);
			} else if (keyString.equals("eleCurrent3")) {
				this.dealArray(bqMac, valueArray, keyString, entry);
			} else if (keyString.equals("voltage1")) {
				this.dealArray(bqMac, valueArray, keyString, entry);
			} else if (keyString.equals("voltage2")) {
				this.dealArray(bqMac, valueArray, keyString, entry);
			} else if (keyString.equals("voltage3")) {
				this.dealArray(bqMac, valueArray, keyString, entry);
			} else {
				this.dealArray(bqMac, valueArray, keyString, entry);
			}
		}
	}

}
