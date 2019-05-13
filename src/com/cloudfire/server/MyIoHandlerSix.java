package com.cloudfire.server;

import java.net.InetSocketAddress;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.GpsInfoDao;
import com.cloudfire.dao.PrinterDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.impl.GpsInfoDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.entity.GpsEntityBean;
//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.Constant;

public class MyIoHandlerSix extends IoHandlerAdapter{
	private final static Log log = LogFactory.getLog(MyIoHandler.class);
	private PushAlarmMsgDao mPushAlarmMsgDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	private GetSmokeMacByRepeaterDao mGetSmokeMacByRepeaterDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private ElectricTypeInfoDao mElectricTypeInfoDao;
	private AlarmThresholdValueDao mAlarmThresholdValueDao;
	private String ip;
	private SessionMap sessionMap;
	private TimerMap mTimerMap;
//	private RepeaterMap mRepeaterMap;
	private PrinterDao mPrinterDao;
	private SmokeLineDao mSmokeLineDao;
	private PublicUtils utilsDao;
	

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
		sessionMap = SessionMap.newInstance();
//		mRepeaterMap = RepeaterMap.newInstance();
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// TODO Auto-generated method stub
//		String clientIP = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();  
		IoBuffer buffer = (IoBuffer) message;
		int byteLen = buffer.limit();
    	byte[] data = new byte[byteLen];
    	buffer.get(data);
    	byteLen =data.length;
    	int cmd1 = data[0]&0xff;
    	int cmd2 = data[1]&0xff;
    	log.debug("clientIP="+Constant.outerIp+"     cmd1+cmd2="+cmd1+"+"+cmd2);
    	if(cmd1!=cmd2||Constant.outerIp==null){
    		return;
    	}
    	byte[] ack = null;
    	log.debug("cmd1="+cmd1);		//cmd1和cmd2这里固定为0x67
    	switch (cmd1) {
    	case 103:
    		GpsEntityBean gpsBean = ClientPackage.isHeart2(data,byteLen,Constant.outerIp);
    		int cmd3 = data[2]&0xff;	//协议号
    		log.debug("cmd3cmd3:="+cmd3);
    		switch(cmd3){
    		case 1:		//登录数据包      ACK
    			ack = data;
    			break;
    		case 2:
    		case 18:		//GPS数据包
    			int gps11 = data[11]&0x01;
    			if(gps11!=1){
    				break;
    			}
        		GpsInfoDao gpsdao = new GpsInfoDaoImpl();
        		gpsdao.saveGpsInfo(gpsBean);
        		gpsdao.sageGpsEqument(gpsBean);
    			break;
    		case 3:		//心跳数据包   	ACK
    			ack = gpsBean.getDataM();
    			utilsDao = new PublicUtilsImpl();
				utilsDao.updateDeviceMac(gpsBean.getDevMac());
//    			mSmokeLineDao = new SmokeLineDaoImpl();
//				mSmokeLineDao.smokeOffLine(gpsBean.getDevMac(), 1);
    			break;
    		case 20:
    		case 21:
    		case 22:
    		case 23:
    		case 24:
    		case 25:
    		case 26:
    		case 27:
    		case 128:
    		case 129:	
    			ack = gpsBean.getDataM();
    			break;
    		}
    		break;
    	}
    	
	
		if(ack!=null){
			IoBuffer buf = IoBuffer.wrap(ack);
	        WriteFuture future = session.write(buf);  
	        future.awaitUninterruptibly(100);
	        if( future.isWritten() ){
	        	log.debug("---------send sucess!---------");
	        }else{
	        	log.debug("---------send failed!---------");
	        }
		}   
	}
}
