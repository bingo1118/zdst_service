package com.cloudfire.thread;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.cloudfire.dao.FreeDao;
import com.cloudfire.dao.impl.FreeDaoImpl;
import com.cloudfire.entity.Recharge;
import com.cloudfire.until.DateUtil;

public class StopDeviceTask extends TimerTask {
	
	private FreeDao freeDao;

	@Override
	public void run() {
		freeDao = new FreeDaoImpl();
		List<Recharge> reachargeList = freeDao.listRecharge();//存在充值记录中的，即充值过的
		
		for(int i=0; i<reachargeList.size(); i++) {
			if(freeDao.recordExistMac(reachargeList.get(i).getMac())){//匹配充值记录Mac，充值记录有mac的，表明冲过钱
				//到期可根据充值时间为空串，到期时间为空串，金额为0.00判断(考虑到数据库中有人乱插值，可加上充值记录一起判断)
				if(!reachargeList.get(i).getStopTime().equals("") && !reachargeList.get(i).getFeetime().equals("") && !reachargeList.get(i).getBeforeFee().equals("0.00")) {//未到期
					//通知情况：到期前一个月，10天，已到期
					//通知人：设备号本区域的list成员
					//通知方式：弹框，发短信，邮件通知
					//到期操作：充值时间，充值金额，到期时间清空，发通知，停设备
					//到期处理，清充值时间，到期时间，金额字段为0
					
					JSONObject jObject1 = new JSONObject(reachargeList.get(i));
					System.out.print("到期时间不为空："+jObject1.toString());
					String now = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(new Date(System.currentTimeMillis()));
					//到期前10天
						try {
							if(DateUtil.countDay(DateUtil.formatDate(now), DateUtil.formatDate(reachargeList.get(i).getStopTime())) == 10) {
								
								
								
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					
					
					
				}else{//已到期，发一次提醒
					JSONObject jObject1 = new JSONObject(reachargeList.get(i));
					System.out.print("到期时间为空："+jObject1.toString());
					
					
					
				}
			}else{
				
			}
			
		}
		
		
		
	}

}
