package com.cloudfire.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.cloudfire.db.SystemConfig;
import com.cloudfire.until.Constant;


public class ServerHandler extends IoHandlerAdapter {
	 /** 30秒发送一次心跳包 */  
    private static final int HEARTBEATRATE = 50; 
    private static Logger logger = Logger.getLogger(ServerHandler.class);

    public ServerHandler() {
    	
    	String result = SystemConfig.getConfigInfomation("ServerHandlerSort");
    	//第一版本，绑定第一个端口4567
        NioDatagramAcceptor acceptor = new NioDatagramAcceptor(); //NioDatagramAcceptor,非阻塞的数据包UDP IoAcceptor
        acceptor.getFilterChain()
        .addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool())); 
        
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10); //设置为10秒无操作则为空闲
        
        KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl(); 
        KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,  
                IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        //设置是否forward到下一个filter  
        heartBeat.setForwardEvent(true);  
        //设置心跳频率  
        heartBeat.setRequestInterval(HEARTBEATRATE);    
        acceptor.getFilterChain().addLast("heartbeat", heartBeat); 
        acceptor.setHandler(new MyIoHandler());
        DatagramSessionConfig dcfg = acceptor.getSessionConfig();
        dcfg.setReuseAddress(true);
        try {
        	if(StringUtils.equals(result, "1")){
        		acceptor.bind(new InetSocketAddress(3567));
        	}else if(StringUtils.equals(result, "0")){
        		acceptor.bind(new InetSocketAddress(4567));
        	}else{
        		acceptor.bind(new InetSocketAddress(4567));
        	}
		} catch (IOException e) {
			logger.error("bind udp port 4567 failed");
			e.printStackTrace();
		}
//        acceptor.bind(new InetSocketAddress(Constant.ide4567));	//更改调试端口
        
        //4G端口4566
        NioDatagramAcceptor acep4G = new NioDatagramAcceptor(); 
        acep4G.getFilterChain()
        .addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool())); 
        
        acep4G.getSessionConfig().setReadBufferSize(2048);
        acep4G.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10); //设置为10秒无操作则为空闲
        
        KeepAliveMessageFactory heart4G = new KeepAliveMessageFactoryImpl(); 
        KeepAliveFilter hBeat4G = new KeepAliveFilter(heart4G,  
                IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        //设置是否forward到下一个filter  
        hBeat4G.setForwardEvent(true);  
        //设置心跳频率  
        hBeat4G.setRequestInterval(HEARTBEATRATE);    
        acep4G.getFilterChain().addLast("hBeat4G", hBeat4G); 
        acep4G.setHandler(new IoHandler4G());
        DatagramSessionConfig dsc4G = acep4G.getSessionConfig();
        dsc4G.setReuseAddress(true);
        try {
        	if(StringUtils.equals(result, "1")){
        		acep4G.bind(new InetSocketAddress(3566));
        	}else if(StringUtils.equals(result, "0")){
        		acep4G.bind(new InetSocketAddress(4566));
        	}else{
        		acep4G.bind(new InetSocketAddress(4566));
        	}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
      //用于接收处理中天微的处理接口
        NioDatagramAcceptor ztwacceptor = new NioDatagramAcceptor(); //NioDatagramAcceptor,非阻塞的数据包UDP IoAcceptor
        ztwacceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool())); 
        
        ztwacceptor.getSessionConfig().setReadBufferSize(2048);
        ztwacceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10); //设置为10秒无操作则为空闲
        
        KeepAliveMessageFactory ztwheartBeatFactory = new KeepAliveMessageFactoryImpl(); 
        KeepAliveFilter ztwheartBeat = new KeepAliveFilter(ztwheartBeatFactory,IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        //设置是否forward到下一个filter  
        ztwheartBeat.setForwardEvent(true);  
        //设置心跳频率  
        ztwheartBeat.setRequestInterval(HEARTBEATRATE);    
        ztwacceptor.getFilterChain().addLast("heartbeat", ztwheartBeat); 
        ztwacceptor.setHandler(new ZTWMyIoHandler());
        DatagramSessionConfig ztwdcfg = ztwacceptor.getSessionConfig();
        ztwdcfg.setReuseAddress(true);
        try {
        	if(StringUtils.equals(result, "1")){
        		ztwacceptor.bind(new InetSocketAddress(3573));
        	}else if(StringUtils.equals(result, "0")){
        		ztwacceptor.bind(new InetSocketAddress(4573));
        	}else{
        		ztwacceptor.bind(new InetSocketAddress(4573));
        	}
		} catch (IOException e) {
			logger.error("bind udp port 3567 failed");
			e.printStackTrace();
		}
        
        //第二版本，绑定第二个端口4568： 
        NioDatagramAcceptor acceptorTwo = new NioDatagramAcceptor();
        acceptorTwo.getFilterChain()
        .addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool())); 
        
        acceptorTwo.getSessionConfig().setReadBufferSize(2048);
        acceptorTwo.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        
        KeepAliveMessageFactory heartBeatFactoryTwo = new KeepAliveMessageFactoryImpl(); 
        KeepAliveFilter heartBeatTwo = new KeepAliveFilter(heartBeatFactoryTwo,  
                IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());  
        //设置是否forward到下一个filter  
        heartBeatTwo.setForwardEvent(true);  
        //设置心跳频率  
        heartBeatTwo.setRequestInterval(HEARTBEATRATE);    
        acceptorTwo.getFilterChain().addLast("heartbeat", heartBeatTwo); 
        acceptorTwo.setHandler(new MyIoHandlerNB_IOT());
        DatagramSessionConfig dcfgTwo = acceptorTwo.getSessionConfig();
        dcfgTwo.setReuseAddress(true);
        try {
        	if(StringUtils.equals(result, "1")){
        		acceptorTwo.bind(new InetSocketAddress(3568));
        	}else if(StringUtils.equals(result, "0")){
        		acceptorTwo.bind(new InetSocketAddress(4568));
        	}else{
        		acceptorTwo.bind(new InetSocketAddress(4568));
        	}
		} catch (IOException e) {
			logger.error("bind udp port 4568 failed");
			e.printStackTrace();
		}
//        acceptorTwo.bind(new InetSocketAddress(Constant.ide4568)); //更改调试端口
        
        //第三版本，绑定第三个端口4569	环境探测器、门磁，红外线 
        NioDatagramAcceptor acceptorThree = new NioDatagramAcceptor();
        acceptorThree.getFilterChain()
        .addFirst("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        
        acceptorThree.getSessionConfig().setReadBufferSize(2048);
        acceptorThree.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        KeepAliveMessageFactory heartBeatFactoryThree = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter heartBeatThree = new KeepAliveFilter(heartBeatFactoryThree,
        		IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        heartBeatThree.setForwardEvent(true);
        heartBeatThree.setRequestInterval(HEARTBEATRATE);
        acceptorThree.getFilterChain().addLast("heartbeat", heartBeatThree);
        acceptorThree.setHandler(new MyIoHandlerTwo());
        DatagramSessionConfig dcfgThree = acceptorThree.getSessionConfig();
        dcfgThree.setReuseAddress(true);
        try {
        	if(StringUtils.equals(result, "1")){
        		acceptorThree.bind(new InetSocketAddress(3569));
        	}else if(StringUtils.equals(result, "0")){
        		acceptorThree.bind(new InetSocketAddress(4569));
        	}else{
        		acceptorThree.bind(new InetSocketAddress(4569));
        	}
		} catch (IOException e) {
			logger.error("bind udp port 4569 failed");
			e.printStackTrace();
		}
//        acceptorThree.bind(new InetSocketAddress(Constant.ide4569));//更改调试端口
        
        
        //第四个版本，绑定第三个端口4571	针对北擎数据和DTU直接做对接,没有按我们的协议单独做的对接。
        NioDatagramAcceptor acceptor5 = new NioDatagramAcceptor();
        acceptor5.getFilterChain()
        .addFirst("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        
        acceptor5.getSessionConfig().setReadBufferSize(2048);
        acceptor5.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        KeepAliveMessageFactory heartBeatFactory5 = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter heartBeat5 = new KeepAliveFilter(heartBeatFactory5,
        		IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        heartBeat5.setForwardEvent(true);
        heartBeat5.setRequestInterval(HEARTBEATRATE);
        acceptor5.getFilterChain().addLast("heartbeat", heartBeat5);
        acceptor5.setHandler(new MyIoHandlerFire());
        DatagramSessionConfig dcfg5 = acceptorThree.getSessionConfig();
        dcfg5.setReuseAddress(true);
        try {
        	if(StringUtils.equals(result, "1")){
        		acceptor5.bind(new InetSocketAddress(3571));
        	}else if(StringUtils.equals(result, "0")){
        		acceptor5.bind(new InetSocketAddress(4571));
        	}else{
        		acceptor5.bind(new InetSocketAddress(4571));
        	}
		} catch (IOException e) {
			logger.error("bind udp port 4571 failed");
			e.printStackTrace();
		}
//        acceptor5.bind(new InetSocketAddress(Constant.ide4571));//更改调试端口
        
        //三江传输装置对接协议--LZO
        NioSocketAcceptor acceptorFaultInfo = new NioSocketAcceptor();
        acceptorFaultInfo.getFilterChain().addFirst("faultPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        acceptorFaultInfo.getSessionConfig().setReadBufferSize(2048);
        acceptorFaultInfo.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        KeepAliveMessageFactory faultHeartBeatFactory = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter faultHeartBeat = new KeepAliveFilter(faultHeartBeatFactory,IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        faultHeartBeat.setForwardEvent(true);
        faultHeartBeat.setRequestInterval(HEARTBEATRATE);
        acceptorFaultInfo.getFilterChain().addLast("heartbeat", faultHeartBeat);
        acceptorFaultInfo.setHandler(new MyIoHandlerFaultInfo());
        SocketSessionConfig dcfgFault = acceptorFaultInfo.getSessionConfig();
        dcfgFault.setReuseAddress(true);
        if(StringUtils.equals(result, "1")){
        	acceptorFaultInfo.setDefaultLocalAddress(new InetSocketAddress(3000));
    	}else if(StringUtils.equals(result, "0")){
    		acceptorFaultInfo.setDefaultLocalAddress(new InetSocketAddress(2000));
    	}else{
    		acceptorFaultInfo.setDefaultLocalAddress(new InetSocketAddress(2000));
    	}
        try {
			acceptorFaultInfo.bind();
		} catch (IOException e) {
			logger.error("bind tcp port 2000 failed");			
			e.printStackTrace();
		}
        
        
        //联众传输装置对接协议--LZO
        NioSocketAcceptor acceptorFaultInfoLZ = new NioSocketAcceptor();
        acceptorFaultInfoLZ.getFilterChain().addFirst("faultPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        acceptorFaultInfoLZ.getSessionConfig().setReadBufferSize(2048);
        acceptorFaultInfoLZ.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        KeepAliveMessageFactory faultHeartBeatFactoryLZ = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter faultHeartBeatLZ = new KeepAliveFilter(faultHeartBeatFactoryLZ,IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        faultHeartBeatLZ.setForwardEvent(true);
        faultHeartBeatLZ.setRequestInterval(HEARTBEATRATE);
        acceptorFaultInfoLZ.getFilterChain().addLast("heartbeat", faultHeartBeatLZ);
        acceptorFaultInfoLZ.setHandler(new LZMyIoHandlerFaultInfo());//ZTWMyIoHandler
        SocketSessionConfig dcfgFaultLZ = acceptorFaultInfoLZ.getSessionConfig();
        dcfgFaultLZ.setReuseAddress(true);
        if(StringUtils.equals(result, "1")){
        	acceptorFaultInfoLZ.setDefaultLocalAddress(new InetSocketAddress(3010));
    	}else if(StringUtils.equals(result, "0")){
    		acceptorFaultInfoLZ.setDefaultLocalAddress(new InetSocketAddress(2010));
    	}else{
    		acceptorFaultInfoLZ.setDefaultLocalAddress(new InetSocketAddress(2010));
    	}
        try {
			acceptorFaultInfoLZ.bind();
		} catch (IOException e) {
			logger.error("bind tcp port 2010 failed");
			e.printStackTrace();
		}
        
        //侨平 物联网数据传输协议--LZO --2020
        NioSocketAcceptor acceptorFaultInfoQP = new NioSocketAcceptor();
        acceptorFaultInfoQP.getFilterChain().addFirst("faultPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        acceptorFaultInfoQP.getSessionConfig().setReadBufferSize(2048);
        acceptorFaultInfoQP.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        KeepAliveMessageFactory faultHeartBeatFactoryQP = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter faultHeartBeatQP = new KeepAliveFilter(faultHeartBeatFactoryQP,IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        faultHeartBeatQP.setForwardEvent(true);
        faultHeartBeatQP.setRequestInterval(HEARTBEATRATE);
        acceptorFaultInfoQP.getFilterChain().addLast("heartbeat", faultHeartBeatQP);
        acceptorFaultInfoQP.setHandler(new QPMyIoHandlerFaultInfo());
        SocketSessionConfig dcfgFaultQP = acceptorFaultInfoQP.getSessionConfig();
        dcfgFaultQP.setReuseAddress(true);
        if(StringUtils.equals(result, "1")){
        	acceptorFaultInfoQP.setDefaultLocalAddress(new InetSocketAddress(3020));
    	}else if(StringUtils.equals(result, "0")){
    		acceptorFaultInfoQP.setDefaultLocalAddress(new InetSocketAddress(2020));
    	}else{
    		acceptorFaultInfoQP.setDefaultLocalAddress(new InetSocketAddress(2020));
    	}
        try {
			acceptorFaultInfoQP.bind();
		} catch (IOException e) {
			logger.error("bind tcp port 2020 failed");
		}
        
        
        //DTU 4560 TCP by yfs
        IoAcceptor acceptor_dtu = new NioSocketAcceptor();
        acceptor_dtu.getFilterChain().addFirst("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        
        acceptor_dtu.getSessionConfig().setReadBufferSize(2048);
        acceptor_dtu.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        KeepAliveMessageFactory heartBeatFactory_dtu = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter heartBeat_dtu = new KeepAliveFilter(heartBeatFactory_dtu,
        		IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        heartBeat_dtu.setForwardEvent(true);
        heartBeat_dtu.setRequestInterval(HEARTBEATRATE);
        acceptor_dtu.getFilterChain().addLast("heartbeat", heartBeatThree);
        acceptor_dtu.setHandler(new MyIoHandlerDTU());
        DatagramSessionConfig dcfg_dut = acceptor5.getSessionConfig();
        dcfg_dut.setReuseAddress(true);
        if(StringUtils.equals(result, "1")){
        	acceptor_dtu.setDefaultLocalAddress(new InetSocketAddress(3560));
    	}else if(StringUtils.equals(result, "0")){
    		acceptor_dtu.setDefaultLocalAddress(new InetSocketAddress(4560));
    	}else{
    		acceptor_dtu.setDefaultLocalAddress(new InetSocketAddress(4560));
    	}
        try {
			acceptor_dtu.bind();
		} catch (IOException e) {
			logger.error("bind tcp port 4560 failed");
			e.printStackTrace();
		}
        
		//第5个版本，绑定第三个端口4571	GPS定位系统，按照GPS协议解析,TCP协议
		IoAcceptor acceptor6 = new NioSocketAcceptor();
        acceptor6.getFilterChain().addFirst("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        
        acceptor6.getSessionConfig().setReadBufferSize(2048);
        acceptor6.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        KeepAliveMessageFactory heartBeatFactory6 = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter heartBeat6 = new KeepAliveFilter(heartBeatFactory6,
        		IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        heartBeat6.setForwardEvent(true);
        heartBeat6.setRequestInterval(HEARTBEATRATE);
        acceptor6.getFilterChain().addLast("heartbeat", heartBeatThree);
        acceptor6.setHandler(new MyIoHandlerSix());
        DatagramSessionConfig dcfg6 = acceptor5.getSessionConfig();
        dcfg6.setReuseAddress(true);
        if(StringUtils.equals(result, "1")){
        	acceptor6.setDefaultLocalAddress(new InetSocketAddress(3571));
    	}else if(StringUtils.equals(result, "0")){
    		acceptor6.setDefaultLocalAddress(new InetSocketAddress(4571));
    	}else{
    		acceptor6.setDefaultLocalAddress(new InetSocketAddress(4571));
    	}
        try {
			acceptor6.bind();
		} catch (IOException e) {
			logger.error("bind tcp port 4571 failed");
			e.printStackTrace();
		}
        
        //创安燃气
  		  IoAcceptor acceptor7 = new NioSocketAcceptor();
	      acceptor7.getFilterChain().addFirst("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
	      
	      acceptor7.getSessionConfig().setReadBufferSize(2048);
	      acceptor7.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
	      KeepAliveMessageFactory heartBeatFactory7 = new KeepAliveMessageFactoryImpl();
	      KeepAliveFilter heartBeat7 = new KeepAliveFilter(heartBeatFactory7,
	      		IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
	      heartBeat7.setForwardEvent(true);
	      heartBeat7.setRequestInterval(HEARTBEATRATE);
	      acceptor7.getFilterChain().addLast("heartbeat", heartBeatThree);
	      acceptor7.setHandler(new MyIohandlerChuangAn());
	      DatagramSessionConfig dcfg7 = acceptor5.getSessionConfig();
	      dcfg7.setReuseAddress(true);
	      if(StringUtils.equals(result, "1")){
	    	  acceptor7.setDefaultLocalAddress(new InetSocketAddress(3561));
	    	}else if(StringUtils.equals(result, "0")){
	    		acceptor7.setDefaultLocalAddress(new InetSocketAddress(4561));
	    	}else{
	    		acceptor7.setDefaultLocalAddress(new InetSocketAddress(4561));
	    	}
	     try {
			acceptor7.bind();
		} catch (IOException e) {
			logger.error("bind tcp port 4561 failed");
			e.printStackTrace();
		}
        
     // 吉林电梯udp 4572
        NioDatagramAcceptor acceptorElevator = new NioDatagramAcceptor();
        acceptorElevator.getFilterChain()
        .addFirst("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        
        acceptorElevator.getSessionConfig().setReadBufferSize(2048);
        acceptorElevator.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        KeepAliveMessageFactory heartBeatFactoryElevator = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter heartBeatElevator = new KeepAliveFilter(heartBeatFactoryElevator,
        		IdleStatus.BOTH_IDLE,new RequestTimeoutCloseHandler());
        heartBeatElevator.setForwardEvent(true);
        heartBeatElevator.setRequestInterval(HEARTBEATRATE);
        acceptorElevator.getFilterChain().addLast("heartbeat", heartBeatElevator);
        acceptorElevator.setHandler(new MyIoHandlerElevator());
        DatagramSessionConfig dcfgElevator = acceptorElevator.getSessionConfig();
        dcfgElevator.setReuseAddress(true);
        try {
        	if(StringUtils.equals(result, "1")){
        		acceptorElevator.bind(new InetSocketAddress(3572));
  	    	}else if(StringUtils.equals(result, "0")){
  	    		acceptorElevator.bind(new InetSocketAddress(4572));
  	    	}else{
  	    		acceptorElevator.bind(new InetSocketAddress(4572));
  	    	}
		} catch (IOException e) {
			logger.error("bind udp port 4572 failed");
			e.printStackTrace();
		}
//        acceptorElevator.bind(new InetSocketAddress(Constant.ide4572));//更改调试端口
        
    }
	
    public void messageReceived(IoSession session, Object message)
            throws Exception {
    	// 读取收到的数据 
    	IoBuffer buffer = (IoBuffer) message;
    	byte[] b = new byte[buffer.limit()];
    	buffer.get(b); 
        // 注意：当客户使用不依赖于MINA库的情况下，以下官方推
        // 荐的读取方法会在数据首部出现几个字节的未知乱码
        //String str = message.toString();
        int msg = b[0];
        switch (msg) {
		case 1:
		
		case 2:
			
		default:
			break;
		}
        //session.close(true);
    }

    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
    	session.close(true);
    }

    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        int count = session.getService().getManagedSessionCount();
        System.out.println("count="+count);
    }

    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        super.sessionIdle(session, status);
        session.close(true);
    }
    
}
