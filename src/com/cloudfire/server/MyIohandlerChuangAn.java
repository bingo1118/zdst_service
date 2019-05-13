package com.cloudfire.server;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.GpsInfoDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.GpsEntityBean;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.RePeaterData;
//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.CRC16;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.IfStopAlarm;
import com.cloudfire.until.IntegerTo16;

public class MyIohandlerChuangAn extends IoHandlerAdapter{
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
		IoBuffer buffer = (IoBuffer) message;
		int byteLen = buffer.limit();
    	byte[] data = new byte[byteLen];
    	buffer.get(data);
    	byteLen =data.length;
    	int cmd1 = data[0]&0xff;
    	byte[] ack = null;//@@回复包
    	if(cmd1!=0xEA||!checkCrc(data,byteLen)){
    		ack=ackToClient(data, (byte)0x01);
    		if(ack!=null){
    			IoBuffer buf = IoBuffer.wrap(ack);
    			WriteFuture future = session.write(buf);  //将ack回应给中继
    	        future.awaitUninterruptibly(100);
    		} 
    		return;
    	}
    	byte[] mac = new byte[6];
		for(int j=6;j>0;j--){
			mac[6-j] = data[7-j];
		}
		String devmac = IntegerTo16.bytes2Hex(mac);//@@设备mac
		sessionMap.addSession(devmac,session);
		PublicUtils utilsDao= new PublicUtilsImpl();
		utilsDao.updateDeviceMac(devmac);//@@设备上线2018.04.10
		int seq=data[7];//@@报文序号
		int afn=data[9];//@@功能码
		switch (afn) {
		case 8://@@控制器上传数据
			int reg_sum=data[11];//@@寄存器个数
			int startNum=((data[12]&0xff)<<8)|data[13]&0xff;//@@起始寄存器号
			for(int i=0;i<reg_sum;i++){
				int value=0;
				int state=0;
				if((data[14+2*i]&0x80)==0x80){//@@判断报警位
					value=((data[14+2*i]&0x7f)<<8)+(data[15+2*i]&0xff);
					pushAlarm(devmac,startNum+i,51);
				}else{
					switch (((data[14+2*i]&0xff)<<8)|data[15+2*i]&0xff) {
					case 0x7FFF:
						state=1;//@@空闲
						value=0;
						break;
					case 0x7F66:
						state=2;//@@离线
						value=0;
						break;
					case 0x7FD2:
						state=3;//@@故障
						value=0;
						break;
					case 0x7F67:
						state=4;//@@隔离
						value=0;
						break;
					default:
						state=0;//@@正常
						value=((data[14+2*i]&0x7f)<<8)+(data[15+2*i]&0xff);
						break;
					}
				}
				ack=ackToClient(data, (byte)0x00);
				addChuangAnInfo(devmac, startNum+i, value,state);
			}
			break;
		case 10://@@探测器上传数据
			int value=0;
			int state=0;
			if((data[10]&0x80)==0x80){//@@判断报警位
				value=((data[10]&0x7f)<<8)+(data[11]&0xff);
				pushAlarm(devmac,1,51);
			}else{
				switch ((data[10]<<8)+(data[11]&0xff)) {
				case 0x7FFF:
					state=1;//@@空闲
					value=0;
					break;
				case 0x7F66:
					state=2;//@@离线
					value=0;
					break;
				case 0x7FD2:
					state=3;//@@故障
					value=0;
					break;
				case 0x7F67:
					state=4;//@@隔离
					value=0;
					break;
				default:
					state=0;//@@正常
					int a=(data[10]&0x7f)<<8;
					int b=data[11]&0xff;
					value=((data[10]&0x7f)<<8)+(data[11]&0xff);
					break;
				}
				
			}
			ack=ackToClient(data, (byte)0x00);
			addChuangAnInfo(devmac, 1, value,state);
			break;
		default:
			break;
		}
		if(ack!=null){
			IoBuffer buf = IoBuffer.wrap(ack);
			WriteFuture future = session.write(buf);  //将ack回应给中继
	        future.awaitUninterruptibly(100);
		}  
	}
	
	
	//@@推送、保存报警信息
	private void pushAlarm(String mac,int line,int deviceType) {
		mSmokeLineDao = new SmokeLineDaoImpl();
		mSmokeLineDao.smokeOffLine(mac, 1);
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		SmokeBean mSmokeBean = mFromRepeaterAlarmDao.getSmoke(mac);
		if(mSmokeBean!=null){
			PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,line);
			mSmokeBean = mFromRepeaterAlarmDao.getSmokeInfo(mSmokeBean); 
			if(deviceType!=5){
				mFromRepeaterAlarmDao.addAlarmMsg(mac, "",2, line);  //将报警信息添加到alarm表中
				mGetPushUserIdDao = new GetPushUserIdDaoImpl();
				List<String> userList = mGetPushUserIdDao.getAllUser(mac);
				if(userList!=null&&userList.size()>0){
					Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList); //获取ios用户列表
					if(IfStopAlarm.map.containsKey(mac)){  //ifStopAlarm.map 停止报警设备表
						if((System.currentTimeMillis()-IfStopAlarm.map.get(mac))>10*60*1000){  //停止报警时间超过10分钟可以重新报警
							new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
							new WebThread(userList,mac).start();
						}
					}else{
						new MyThread(push,userList,iosMap).start();
						new WebThread(userList,mac).start();
					}//@@10.31 设备设置停止报警后间隔一小时再次恢复可以报警状态 by liangbin
				}
			}
		}
	}

	//@@保存创安燃气数据
	public static  void addChuangAnInfo(String devMac,int lineNum, int value,int state) {
		// TODO Auto-generated method stub
		String sql = "insert into chuangan_info (mac,value,time,line,state) values(?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps= DBConnectionManager.prepare(conn,sql);
		String addTime = GetTime.ConvertTimeByLong();
		try {
			ps.setString(1, devMac);
			ps.setInt(2, value);
			ps.setString(3, addTime);
			ps.setInt(4, lineNum);
			ps.setInt(5, state);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	//@@CRC校验
	private static boolean checkCrc(byte[] data,int dataLen){
		int crcLen = dataLen-2;
		byte[] crcByte = new byte[crcLen];
		for(int i=0;i<crcLen;i++){
			crcByte[i] = data[i];
		}
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		byte crcL = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		byte crcH = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		byte clientCrcL = data[dataLen-2];
		byte clientCrcH = data[dataLen-1];
		log.debug("CRC==========crcL:"+crcL+" crcH:"+crcH+" clientCrcL:"+clientCrcL+" clientCrcH:"+clientCrcH+"   dataLen="+dataLen);
		if(crcL==clientCrcL&&crcH==clientCrcH){
			return true;
		}else{
			return false;
		}
	}
	
	//@@回复包数据
	public static byte[] ackToClient(byte[] data,byte ifTrue) {
		byte[] ackByte = new byte[13];
		byte[] crcByte = new byte[11];
		ackByte[0]=(byte) 0xED;
		ackByte[1]=data[1];
		ackByte[2]=data[2];
		ackByte[3]=data[3];
		ackByte[4]=data[4];
		ackByte[5]=data[5];
		ackByte[6]=data[6];
		ackByte[7]=data[7];
		ackByte[8]=data[8];
		ackByte[9]=0x01;
		ackByte[10]=ifTrue;
		crcByte[0] = (byte) 0xED;
		crcByte[1] = data[1];
		crcByte[2] = data[2];
		crcByte[3] = data[3];
		crcByte[4] = data[4];
		crcByte[5] = data[5];
		crcByte[6] = data[6];
		crcByte[7] = data[7];
		crcByte[8] = data[8];
		crcByte[9] = 0x01;
		crcByte[10] = ifTrue;
		String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
		ackByte[11] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
		ackByte[12] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
		return ackByte;
	}
	//@@OA回复包数据
		public static byte[] ackToClient_0A(byte[] data,byte ifTrue) {
			byte[] ackByte = new byte[12];
			byte[] crcByte = new byte[10];
			ackByte[0]=(byte) 0xED;
			ackByte[1]=data[1];
			ackByte[2]=data[2];
			ackByte[3]=data[3];
			ackByte[4]=data[4];
			ackByte[5]=data[5];
			ackByte[6]=data[6];
			ackByte[7]=data[7];
			ackByte[8]=data[8];
			ackByte[9]=0x0A;
			crcByte[0] = (byte) 0xED;
			crcByte[1] = data[1];
			crcByte[2] = data[2];
			crcByte[3] = data[3];
			crcByte[4] = data[4];
			crcByte[5] = data[5];
			crcByte[6] = data[6];
			crcByte[7] = data[7];
			crcByte[8] = data[8];
			crcByte[9] = 0x0A;
			String srcStr = String.format("%04x", CRC16.calcCrc16(crcByte));
			ackByte[10] = new IntegerTo16().str16ToByte(srcStr.substring(0, 2));
			ackByte[11] = new IntegerTo16().str16ToByte(srcStr.substring(2, 4));
			return ackByte;
		}
}
