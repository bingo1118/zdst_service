package com.cloudfire.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.EnvironmentDao;
import com.cloudfire.dao.FaultInfoDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.PrinterDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.AlarmThresholdValueDaoImpl;
import com.cloudfire.dao.impl.ElectricTypeInfoDaoImpl;
import com.cloudfire.dao.impl.EnvironmentDaoImpl;
import com.cloudfire.dao.impl.FaultInfoDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PrinterDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.entity.BQ100Entity;
import com.cloudfire.entity.BQ200Entity;
import com.cloudfire.entity.ChJEntity;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.EnvironmentEntity;
import com.cloudfire.entity.FaultDataEntity;
import com.cloudfire.entity.PrinterEntity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.PushAlarmToPCEntity;
import com.cloudfire.entity.RePeaterData;
//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.ThreePhaseMeterEntity;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.entity.Water;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.Client_Fault_Package;
import com.cloudfire.until.Constant;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IfStopAlarm;
import com.cloudfire.until.IntegerTo16;
import com.cloudfire.until.Utils;
import com.google.common.base.Strings;

import freemarker.template.utility.StringUtil;

public class QPMyIoHandlerFaultInfo extends IoHandlerAdapter{
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
	private FaultDataEntity faultData = null;
	private WaterInfoDao mWaterInfoDao;
	private RePeaterDataDao repeaterDao;
	private Map<String,Integer> AckEleMap ;

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------sessionOpened----------------");
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------lianzongsessionClosed----------------");
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		log.debug("-------------lianzongsessionCreated----------------");
		log.debug(session+":"+session.toString());
		log.debug("-------------lianzongsessionCreated----------------");
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
    	byte[] byetData = ClientPackage.faultByte(data,byteLen);  //得到一个data中16进制是以4040开头，结尾是2323的byte[]
    	if(byetData==null){
    		return;
    	}
    	String repeaterMac = "";
    	int cmd1 = data[0]&0xff;
    	int cmd2 = data[byteLen-1]&0xff;
    	log.debug("lianzongfaultAck==="+IntegerTo16.bytes2Hex(data));
    	byte[] ack = null;
    	String weiyima = System.currentTimeMillis()+"";
    	System.out.println(""+GetTime.ConvertTimeByLong()+"QPMyIoHandlerFaultInfo Start time:"+weiyima);
    	if(cmd1==(data[1]&0xff)&&cmd2==(data[byteLen-1]&0xff)){
    		boolean crcRight = Client_Fault_Package.checkSumCrc(byetData);  //校验和校验
    		if(crcRight){
	    		int controllcmd = data[26]&0xff;
	    		byte[] macByte = new byte[6];
	    		for(int i=0;i<6;i++){
	    			macByte[i] = data[12+i];
	    		}
	    		repeaterMac = IntegerTo16.bytes2Hex(macByte);
	    		log.debug("lianzongrepeaterMac:"+repeaterMac);
	    		long nowTime = System.currentTimeMillis();
//				mRepeaterMap.addTime(repeaterMac, nowTime);
	    		RePeaterDataDao rDao = new RePeaterDataDaoImpl();
				rDao.addRepeaterTime(repeaterMac,Constant.outerIp);
				sessionMap.addSession(repeaterMac,session);
				utilsDao= new PublicUtilsImpl();
    			utilsDao.updateDeviceMac(repeaterMac);
	    		switch(controllcmd){
	    		case 1: // 控制命令，时间同步 ****
	    			break;
	    		case 2:	//发送数据、发送火灾报警和建筑消防设施运行状态等信息========
	    			faultData = Client_Fault_Package.dealFaultByQP(byetData, byteLen,2);
	    			if(faultData!=null){
	    				ack = faultData.getAckByte();
	    			}
	    			break;
	    		case 3: //确认、对控制命令和发送信息的确认回答****
	    			faultData = Client_Fault_Package.dealFaultByQP(byetData, byteLen,3);
	    			ack = faultData.getAckByte();
	    			break;
	    		case 4:	//请求、查询火灾报警和建筑消防设施运行状态等信息============
	    			faultData = Client_Fault_Package.dealFaultByQP(byetData, byteLen,4);
	    			ack = faultData.getAckByte();
	    			break;
	    		case 5:	//应答、返回查询的信息
	    			faultData = Client_Fault_Package.dealFaultByQP(byetData, byteLen,5);
	    			ack = faultData.getAckByte();
	    			break;
	    		case 6:	//否认、对控制指令和发送信息的否认回答*****=================
	    			faultData = Client_Fault_Package.dealFaultByQP(byetData, byteLen,6);
	    			ack = faultData.getAckByte();
	    			break;
	    		}
	    		if(faultData!=null){
	    			log.debug("QPfaultData:="+faultData.toString());
	    			faultData.setRepeaterMac(repeaterMac);
	    			FaultInfoDao faultDao = new FaultInfoDaoImpl();
	    			faultDao.insertFaultInfoSystem(faultData);
	    			if(faultData.getPushState()==1){
	    				faultDao.insertFaultInfoQP(faultData);
	    			}
	    		}
	    	}
    	}
    	System.out.println(""+GetTime.ConvertTimeByLong()+"QPMyIoHandlerFaultInfo Stop time:"+weiyima);
		if(ack!=null){
			IoBuffer buf = IoBuffer.wrap(ack);
	        WriteFuture future = session.write(buf);  
	        future.awaitUninterruptibly(100);
	        if( future.isWritten() ){
	        	log.debug("---------lianzongsend sucess!1111111---------:"+IntegerTo16.bytes2Hex(ack));
	        }else{
	        	log.debug("---------lianzongsend failed!1111111---------:"+IntegerTo16.bytes2Hex(ack));
	        }
		} 
	}
}
