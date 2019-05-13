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
		List<Recharge> reachargeList = freeDao.listRecharge();//���ڳ�ֵ��¼�еģ�����ֵ����
		
		for(int i=0; i<reachargeList.size(); i++) {
			if(freeDao.recordExistMac(reachargeList.get(i).getMac())){//ƥ���ֵ��¼Mac����ֵ��¼��mac�ģ��������Ǯ
				//���ڿɸ��ݳ�ֵʱ��Ϊ�մ�������ʱ��Ϊ�մ������Ϊ0.00�ж�(���ǵ����ݿ��������Ҳ�ֵ���ɼ��ϳ�ֵ��¼һ���ж�)
				if(!reachargeList.get(i).getStopTime().equals("") && !reachargeList.get(i).getFeetime().equals("") && !reachargeList.get(i).getBeforeFee().equals("0.00")) {//δ����
					//֪ͨ���������ǰһ���£�10�죬�ѵ���
					//֪ͨ�ˣ��豸�ű������list��Ա
					//֪ͨ��ʽ�����򣬷����ţ��ʼ�֪ͨ
					//���ڲ�������ֵʱ�䣬��ֵ������ʱ����գ���֪ͨ��ͣ�豸
					//���ڴ������ֵʱ�䣬����ʱ�䣬����ֶ�Ϊ0
					
					JSONObject jObject1 = new JSONObject(reachargeList.get(i));
					System.out.print("����ʱ�䲻Ϊ�գ�"+jObject1.toString());
					String now = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(new Date(System.currentTimeMillis()));
					//����ǰ10��
						try {
							if(DateUtil.countDay(DateUtil.formatDate(now), DateUtil.formatDate(reachargeList.get(i).getStopTime())) == 10) {
								
								
								
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					
					
					
				}else{//�ѵ��ڣ���һ������
					JSONObject jObject1 = new JSONObject(reachargeList.get(i));
					System.out.print("����ʱ��Ϊ�գ�"+jObject1.toString());
					
					
					
				}
			}else{
				
			}
			
		}
		
		
		
	}

}
