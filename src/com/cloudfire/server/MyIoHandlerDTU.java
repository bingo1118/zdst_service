package com.cloudfire.server;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.cloudfire.dao.DtuDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.DtuDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.entity.DtuData;
import com.cloudfire.entity.DtuDataGroup;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.Water;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.JavaByteArrToHex;

public class MyIoHandlerDTU extends IoHandlerAdapter {
	private final static Log log = LogFactory.getLog(MyIoHandler.class);
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------sessionOpened----------------");
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------sessionClosed----------------");
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------sessionCreated----------------");
//		sessionMap = SessionMap.newInstance();
//		mRepeaterMap = RepeaterMap.newInstance();
	}
	
	public static void main(String[] args) {
//		String str = "18 01 26 11 46 35";
//		SimpleDateFormat sdf = new SimpleDateFormat("yy MM dd hh mm ss");
//		try {
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(sdf.parse(str));
//			cal.add(Calendar.SECOND, 35);
//			System.out.println(cal.getTime());
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// TODO Auto-generated method stub
		IoBuffer buffer = (IoBuffer) message; 
		int byteLen = buffer.limit();
    	byte[] data = new byte[byteLen];
    	buffer.get(data);  //�������������ݶ���byte[] data
    	System.out.println("1111111111111111111111111111--Received"+JavaByteArrToHex.bytesToHex(data).toUpperCase());
    	byte[] byteData = ClientPackage.dtuByte(data,byteLen);  //�õ�һ��data��16��������"7E7E7E7E"��ͷ��byte[]
    	String clientIP = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
    	if(byteData==null){
    		return;
    	}
    	System.out.println("1111111111111111111111111112--Received"+JavaByteArrToHex.bytesToHex(byteData).toUpperCase());
    	byteLen =byteData.length;
    	DtuData dtu = ClientPackage.getDataOfDTU(byteData, byteLen);
    	String imei = dtu.getDtuId();  //��ȡ�豸imei
    	String name =dtu.getDtuName(); //��ȡ�豸����
    	int msgType = dtu.getMsgType();   //��������
    	byte[] ack = null;
    	switch (msgType) {
    	case 1: //ע�� ack 01 msglen = 0   ��ȡ���Ӵ����ڴ�
//    		sessionMap.addSession(dtu.getDtuId(), session);  //����DTU�豸��session �����ڴ棬
    		ack = ClientPackage.ackToDTU(byteData,(byte)0x01);
    		break;
    	case 2: //���� ack 02 ���������յ���Ӧ�����dtu������״̬,��Ҫһ����ʱ����dtu�豸����������
    		break;
    	case 3: //���� ���ûظ� �洢�ϴ�������
    		break;
    	case 5: //AT���� ��Ϣ������������ݣ�AT����
    		break;
    	case 6: //��Ե�������Ϣ ack 06 msglen = 0      ��Ϣ����=IMEI�Ÿ���*16 ��Ϣ�壺IMEI�б�
    		ack = ClientPackage.ackToDTU(byteData,(byte)0x06);
    		break;
    	case  7: //dtu���ݲɼ���� ack 07  msglen = 0       ��Ϣ���� = 2bytes   ��Ϣ���ݣ����ʱ�䣬��λs�����ʱ�䷶Χ��0~65535��
    		ack = ClientPackage.ackToDTU(byteData,(byte)0x07);
    		break;
    	case  8: //dtuÿ�η��͵����ݰ���Ŀ ack 08   msglen = 0        ��Ϣ���� = 1byte����Χ��1~255��
    		ack = ClientPackage.ackToDTU(byteData,(byte)0x08);
    		break;
    	case 9: //���ܱ��ϴ�������Ϣ
    		//��ȡ������
    		List<DtuDataGroup> lstdg2 = dtu.getLstdg();
    		int size2 = lstdg2.size(); 
    		
    		ack = ClientPackage.ackToDTU(byteData,(byte)0x09);
    		
    		int interval = dtu.getInterval(); //�ɼ�ʱ����
    		
    		String time = dtu.getTime(); //�ϴ��Ĳɼ�ʱ��
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("yy MM dd HH mm ss"); //�ϴ���ʱ���ʽ
    		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //�洢�����ݿ��е�ʱ���ʽ
    		Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(time)); //����������ʱ��תΪcalendar����
			
			int unit =lstdg2.get(0).getUnit(); //��λ
			
			//��ȡ�豸�ĸߵ���ֵ�������ж��豸����ʷ״̬
//			WaterInfoDao waterDao = new WaterInfoDaoImpl();
//			float waterlow = 0,waterhigh = 0;
//			if(unit==5){ //ˮλ�豸����ֵ
//				waterlow = Float.parseFloat(waterDao.getWaterLow(imei));
//				waterhigh = Float.parseFloat(waterDao.getWaterHigh(imei));
//			}
//			else { //ˮѹ�豸��ֵ
//				waterlow = Float.parseFloat(waterDao.getWaterLow2(imei));
//				waterhigh = Float.parseFloat(waterDao.getWaterHigh2(imei));
//			}
			
			DtuDao dd = new DtuDaoImpl(); 
			
    		for (int i = 0; i< size2;i++) {  //���������飬�����ݴ������ݿ�
    			time = sdf2.format(cal.getTime()); //��ȡ���ݲɼ�ʱ�䲢��֮ת��Ϊ���ݿ�ʱ���ʽ
	    		float value = lstdg2.get(i).getValue(); //ֵ
	    		int state = lstdg2.get(i).getState();
//	    		switch(unit) {
//	    		case 1: //Mpa
//	    		case 2: //Bar
//	    		case 3: //Kpa
//	    			state = lstdg2.get(i).getState();  //ѹ��������״̬
//	    			int alarmType = 0;
//	    			switch(state) {
//	    			case 0:
//	    				break;
//	    			case 1: //��ˮѹ
//	    				alarmType = 209;
//	    				break;
//	    			case 2: //��ˮѹ
//	    				alarmType = 218;
//	    				break;
//	    			case 3: //����
//	    				alarmType = 36;
//	    				break;
//	    			}
//	    			if (state != 0){  //�����������
//	    				dd.addAlarmMsg(imei, time, alarmType);
//	    				state = alarmType;
//	    				mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
//						mFromRepeaterAlarmDao.addAlarmMsg(imei, "", alarmType,value+"");
//	    				//�������� �ֻ� �� ƽ̨
//	    				mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
//	    				PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(imei,alarmType);
//	    				mGetPushUserIdDao = new GetPushUserIdDaoImpl();
//						List<String> userList = mGetPushUserIdDao.getAllUser(imei);
//						if(userList!=null&&userList.size()>0){
//							Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //��ȡios�û��б�
//							new MyThread(push,userList,iosMap).start();
//							new WebThread(userList,imei).start();
//						}
//	    			}
//	    			break;
//	    		case 4: //`�¶�C
//	    			//double temperature =value;
//	    			break;
//	    		case 5: //Һλ M
//	    			WaterInfoDao waterDao = new WaterInfoDaoImpl();
//	    			state = waterDao.getWaterLeve(imei,value+"");
//	    			break;
//	    		case 6://���� m^3/h
//	    			break;
//	    		case 7: //�Ƕȡ�C
//	    			break;
//	    		case 8: //���� 18 48 88 88��ʾ��������
//	    			break;
//	    		}
    			
	    		dd.addDtuData(imei, state, value, unit,time); //���豸�ɼ����������waterinfo��unit����Ч��ˮѹΪkPa,ˮλΪm
	    		cal.add(Calendar.SECOND, interval);  //���ݲɼ�ʱ�䰴�ɼ�ʱ��������
    		} 
    		//�����鴦�����
    		
    		if (unit == 5 || unit == 1 || unit == 2 || unit == 3) { //ˮѹ��ˮλ�豸,�������һ�����ݵ�ˮλֵ���ж��Ƿ񱨾�
	    		//ˮλ�豸�ı�������
    			DtuDataGroup  last =  lstdg2.get(lstdg2.size() - 1);
    			int alarmType2 = last.getState();
				switch(alarmType2) {
				case 207:	//ˮλ�ͱ���
				case 208:	//ˮλ�߱���
				case 209: //��ˮѹ����
				case 218: //��ˮѹ����
					mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
					PushAlarmMsgEntity push2 = mPushAlarmMsgDao.getPushAlarmMsg(imei,alarmType2);
					mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
					mFromRepeaterAlarmDao.addAlarmMsg(imei, "", alarmType2,last.getValue()+"");
					mGetPushUserIdDao = new GetPushUserIdDaoImpl();
					List<String> userList2 = mGetPushUserIdDao.getAllUser(imei);
					if(userList2!=null&&userList2.size()>0){  //����
						Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList2);
						new MyThread(push2,userList2,iosMap).start();
						new WebThread(userList2,imei).start();
					}
					break;
				}
    		}
    		
//    		cal.add(Calendar.SECOND,-1*interval); //�����������һ��ʱ��������������
    		dd.updateDtu(imei, sdf2.format(new Date()),1); //��dtu �豸��Ϣ���smoke�������豸������ʱ��Ϊ��ǰʱ�䣬��ʹ֮����
    		break;
    	}
	
		if(ack!=null){
			System.out.println("1111111111111111111111111113--Received"+JavaByteArrToHex.bytesToHex(ack).toUpperCase());
			IoBuffer buf = IoBuffer.wrap(ack);
	        WriteFuture future = session.write(buf);  //��ack��Ӧ���м�
	        future.awaitUninterruptibly(100);
	        if( future.isWritten() ){
	        	log.debug("---------send sucess!---------");
	        }else{
	        	log.debug("---------send failed!---------");
	        }
		}   
	}

}
