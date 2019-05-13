package com.cloudfire.until;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.Security;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import com.cloudfire.dao.AlarmThresholdValueDao;
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.PrinterDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.GetSmokeMacByRepeaterDaoImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.AlarmPushOnlyEntity;
import com.cloudfire.entity.AreaidGoEasy;
import com.cloudfire.entity.BodyObj;
import com.cloudfire.entity.ElectricHistoryEntity;
import com.cloudfire.entity.ElectricThresholdBean;
import com.cloudfire.entity.EnergrEntity;
import com.cloudfire.entity.NanJing_NB_IOT_Entity;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.Repeater;
//import com.cloudfire.entity.RepeaterMap;
import com.cloudfire.entity.SequenceEntity;
import com.cloudfire.entity.SessionMap;
//import com.cloudfire.entity.SmokeMap;
import com.cloudfire.entity.ThreePhaseElectricEntity;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.entity.Water;
import com.cloudfire.entity.WaterAckEntity;
import com.cloudfire.myservice.appAccessSecurity.Authentication;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.SmsVoicePromptSender;
import com.github.qcloudsms.SmsVoicePromptSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

import io.goeasy.GoEasy;

public class Utils {
	private static PushAlarmMsgDao mPushAlarmMsgDao;
	private static GetPushUserIdDao mGetPushUserIdDao;
	private static FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private ElectricTypeInfoDao mElectricTypeInfoDao;
	private AlarmThresholdValueDao mAlarmThresholdValueDao;
	private SessionMap sessionMap;
	private TimerMap mTimerMap;
	private PrinterDao mPrinterDao;
	private static SmokeLineDao mSmokeLineDao;
	private WaterInfoDao mWaterInfoDao;
	private RePeaterDataDao repeaterDao;
	private PublicUtils utilsDao;
	
	public static final Map<String,Long> repeaterStateAlarm = new HashMap<String,Long>();//@@主机备电报警时间
	public static final Map<String,Integer> AckEleMap = new HashMap<String,Integer>();
	public static final Map<String,Integer> controlDev = new HashMap<String,Integer>();//@lzo控制设备是否成功
	public static final Map<String,Map<String,Integer>> eleThreshold = new HashMap<String,Map<String,Integer>>();//@lzo电气阈值下发是否成功
	public static final Map<String,Integer> repeaterStateMap = new HashMap<String,Integer>();
	public static final Map<String,String> userMd5 = new HashMap<String,String>();	//根据用户登录成功，存储toKen值
	private static GetSmokeMacByRepeaterDao mGetSmokeMacByRepeaterDao;
	private static MessageDigest mdInst;	//	用于移动onenet初始化
	public static  Map<String,WaterAckEntity> objWater = new HashMap<String,WaterAckEntity>();//@lzo控制水压数据
	public static final Map<String,Integer> hostLevenUp = new HashMap<String,Integer>();//lzo用于主机升级
	public static final Map<String,Integer> hostVersion = new HashMap<String,Integer>();//lzo用于判断主机升级版本比对。
	public static  Map<Integer,Object> objectNb = new HashMap<Integer,Object>();//lzo-NB联动报警
	
	//1、正常，2、成功，3、失败，4、升级中
	public static final Map<String,Integer> hostLevenUpState = new HashMap<String,Integer>();//lzo用于主机升级状态
	
	public static final Map<String,Integer> smokeAlarmCount = new HashMap<String,Integer>();//烟感报警次数,配合smokeAlarmTime使用
	public static final Map<String,Long> smokeAlarmTime = new HashMap<String,Long>();//烟感报警时间
	
	public static final Map<String,Long> deviceTime = new HashMap<String,Long>();//设备数据处理时间间隔
	
	public static  Map<String,EnergrEntity> objEnergr = new HashMap<String,EnergrEntity>();//@lzo中电数据处理
	
	
	public static void sendMessage(String userName,String message){  //使用GoEasy发布消息到公共频道
		GoEasy goEasy = new GoEasy(Constant.appk_web);
//		System.out.println("N:"+userName+"    appk="+Constant.appk_web);
		if(!userName.equals("105")&&userName!="105"){		//屏蔽贵州区域推送
			goEasy.publish(userName, message);
		}
	}
	
	
	static {
        try {
            mdInst = MessageDigest.getInstance("MD5");
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/** 
     * 每一个用户存放一个session。便于各种操作！！！ 
     */
  
	public static boolean isNumeric(String str) {
		
		if (str!=null&&str.length()>0) {
			if(str.equals("0")){
				return false;
			}
			Pattern pattern = Pattern.compile("[0-9]*");
			return pattern.matcher(str).matches();
		}else{
			return false;
		}
	}
	
	/**
	 * 生成16位随机订单号（用于微信支付）
	 * @return
	 */
	public static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + r.nextInt();
        key = key.replaceAll("-", "").substring(0, 15);
        return key;
    }
	
	public static boolean isNullStr(String str) {
		if(str!=null&&str.length()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public static String convertEncode(String requestStr) throws UnsupportedEncodingException{
		if(requestStr!=null&&requestStr.length()>0){
			return new String(requestStr.getBytes("iso8859-1"),"utf-8");
		}
		return requestStr;
	}
	
    public static ByteBuffer getByteBuffer(Serializable obj) throws IOException {  
        byte[] bytes = getBytes(obj);  
        ByteBuffer buff = ByteBuffer.wrap(bytes);  
        return buff;  
    }  
    
    public static byte[] getBytes(Serializable obj) throws IOException {  
        ByteArrayOutputStream bout = new ByteArrayOutputStream();  
        ObjectOutputStream out = new ObjectOutputStream(bout);  
        out.writeObject(obj);  
        out.flush();  
        byte[] bytes = bout.toByteArray();  
        bout.close();  
        out.close();  
        return bytes;  
    } 
    
    public static byte[] formatByte(byte[] pdata){
    	int len = pdata.length;
    	int i = 0; 
    	int j = 0;
    	byte[] pbuf = new byte[len];
	    for (i = 0; i < len; i++){
	        if (pdata[i] == (byte)0xef &&( pdata[i+1] == (byte)0xea || pdata[i+1] == (byte)0xef||pdata[i+1]==(byte)0xeb)){
	        	switch (pdata[i+1]) {
				case (byte) 0xea:
					pbuf[j++] = 0x1a;
					break;
				case (byte) 0xef:
					pbuf[j++] = (byte)0xef;
					break;
				case (byte) 0xeb:
					pbuf[j++] = 0x1b;
					break;
				default:
					break;
				}
	            i = i+1;
	        }else {
	            pbuf[j++] = pdata[i]; 
	        }
		}
	    byte[] data = new byte[j];
	    for(int h =0;h<j;h++){
	    	data[h] = pbuf[h];
	    }
    	return data;
    }
    
    public static byte[] formatByte(byte[] pdata,int state){
    	int len = pdata.length;
    	int i = 0; 
    	int j = 0;
    	byte[] pbuf = new byte[len];
	    for (i = 0; i < len; i++){
	        if (pdata[i] == (byte)0xef &&(pdata[i+1] == (byte)0xe3|| pdata[i+1] == (byte)0xea || pdata[i+1] == (byte)0xef||pdata[i+1]==(byte)0xeb)){
	        	switch (pdata[i+1]) {
				case (byte) 0xea:
					pbuf[j++] = 0x1a;
					break;
				case (byte) 0xef:
					pbuf[j++] = (byte)0xef;
					break;
				case (byte) 0xeb:
					pbuf[j++] = 0x1b;
					break;
				case (byte) 0xe3:
					pbuf[j++] = 0x03;
					break;
				default:
					break;
				}
	            i = i+1;
	        }else {
	            pbuf[j++] = pdata[i]; 
	        }
		}
	    byte[] data = new byte[j];
	    for(int h =0;h<j;h++){
	    	data[h] = pbuf[h];
	    }
    	return data;
    }
    
    public static String getOneKeyUserId(String alarmSerialNumber){
    	String[] str = alarmSerialNumber.split("--");
    	if(str!=null&&str.length==2){
    		return str[1];
    	}else{
    		return null;
    	}
		
    }
    
    public static String electricValues(int one,int two){
    	String filesize;
    	if(two==0){
    		filesize = "0.00";
    	}else{
	    	float size = (float)one/two;
	    	DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
	    	filesize = df.format(size);
    	}
    	return filesize;
    }
    
    public static List<String> dataList(int num,byte[] data,int lenMark, int heartType){
    	List<String> list = new ArrayList<String>();
    	byte[] heartByteTwo = new byte[2];
    	heartByteTwo[0] = data[lenMark+2];
    	heartByteTwo[1] = data[lenMark+3];
		int heartTwo = IntegerTo16.bytesToInt(heartByteTwo);
    	for(int i=0;i<num;i++){
    		byte[] heartByte = new byte[2];
    		heartByte[0] = data[lenMark+5+i*2];
    		heartByte[1] = data[lenMark+6+i*2];
    		int heartOne = IntegerTo16.bytesToInt(heartByte);
    		String valueData = Utils.electricValues(heartOne, heartTwo);
    		switch(heartType){
    			case 6:
    				if(Double.parseDouble(valueData)<80||Double.parseDouble(valueData)>300){
						return null;
					}//@@11.04 过滤突变错误数据
    				break;
    			case 7:
    				if(Double.parseDouble(valueData)<0||Double.parseDouble(valueData)>63){
						return null;
					}//@@11.04 过滤突变错误数据
    				break;
    			case 8:
    				if(Double.parseDouble(valueData)<0||Double.parseDouble(valueData)>900){
						return null;
					}//@@11.04 过滤突变错误数据
    				break;
    		}
    		list.add(valueData);
    	}
    	return list;
    }
    
    public static ElectricThresholdBean unElectricThresholdBeanPackage(byte[] data,int num){
    	int markLen = 25;
    	ElectricThresholdBean mElectricThresholdBean = new ElectricThresholdBean();
    	for(int i=0;i<5;i++){
    		int type = data[markLen]&0xff;
    		String valueData43=null;
    		if(type!=47){
    			valueData43 = getValue(data,markLen);
    		}
    		switch (type) {
    		case 43:
    			if(Double.parseDouble(valueData43)<100||Double.parseDouble(valueData43)>320){
					return null;
				}//@@过滤突变错误数据
	    		mElectricThresholdBean.setOverpressure(valueData43);
    			markLen = markLen+8;
    			break;
    		case 44:
    			//String valueData44 = getValue(data,markLen);
    			mElectricThresholdBean.setUnpressure(valueData43);
    			markLen = markLen+8;
    			break;
    		case 45:
    			if(Double.parseDouble(valueData43)<0||Double.parseDouble(valueData43)>300){
					return null;
				}//@@过滤突变错误数据
    			//String valueData45 = getValue(data,markLen);
    			mElectricThresholdBean.setOverCurrent(valueData43);
    			markLen = markLen+8;
    			break;
    		case 46:
    			if(Double.parseDouble(valueData43)<0||Double.parseDouble(valueData43)>3500){
					return null;
				}//@@过滤突变错误数据
    			//String valueData46 = getValue(data,markLen);
    			mElectricThresholdBean.setLeakCurrent(valueData43);
    			markLen = markLen+8;
    			break;
    		case 47:
    			List<String> lists = dataList(num,data,markLen,0);
    			mElectricThresholdBean.setTemperatures(lists);
    			markLen = markLen+14;
    			break;
    		}
    	}
    	return mElectricThresholdBean;
    }
    
    public static ElectricThresholdBean jtlElectricThresholdBean(byte[] data,int markLen){
    	ElectricThresholdBean mElectricThresholdBean = new ElectricThresholdBean();
    	for(int i=0;i<5;i++){
    		int type = 43+i;
    		String valueData43=null;
			valueData43 = ((data[markLen]&0xff)<<8)+(data[markLen+1]&0xff)+"";	//报警列表
			System.out.println(" valueData43:===="+valueData43);
    		switch (type) {
    		case 44:
    			if(Double.parseDouble(valueData43)<100||Double.parseDouble(valueData43)>320){
					return null;
				}//@@过滤突变错误数据
	    		mElectricThresholdBean.setOverpressure(valueData43);
    			markLen = markLen+2;
    			break;
    		case 43:
    			mElectricThresholdBean.setUnpressure(valueData43);
    			markLen = markLen+2;
    			break;
    		case 45:
    			valueData43 = Integer.parseInt(valueData43)/10+"";
    			if(Double.parseDouble(valueData43)<0||Double.parseDouble(valueData43)>1000){
					return null;
				}//@@过滤突变错误数据
    			mElectricThresholdBean.setOverCurrent(valueData43);
    			markLen = markLen+2;
    			break;
    		case 46:
    			if(Double.parseDouble(valueData43)<0||Double.parseDouble(valueData43)>3500){
					return null;
				}//@@过滤突变错误数据
    			mElectricThresholdBean.setLeakCurrent(valueData43);
    			markLen = markLen+2;
    			break;
    		case 47:
    			List<String> lists = new ArrayList<String>();
    			lists.add(valueData43);
    			mElectricThresholdBean.setTemperatures(lists);
    			break;
    		}
    	}
    	return mElectricThresholdBean;
    }
    
    public static ElectricThresholdBean unElectricThresholdBeanPackage2(byte[] data){
    	int markLen = 25;
    	ElectricThresholdBean mElectricThresholdBean = new ElectricThresholdBean();
    	for(int i=0;i<4;i++){
    		int type = data[markLen]&0xff;
    		String valueData43=null;
    		if(type!=47){
    			valueData43 = getValue(data,markLen);
    		}
    		switch (type) {
    		case 43:
    			if(Double.parseDouble(valueData43)<100||Double.parseDouble(valueData43)>320){
					return null;
				}//@@过滤突变错误数据
	    		mElectricThresholdBean.setOverpressure(valueData43);	//过压
    			markLen = markLen+8;
    			break;
    		case 44:
    			//String valueData44 = getValue(data,markLen);
    			mElectricThresholdBean.setUnpressure(valueData43);		//欠压
    			markLen = markLen+8;
    			break;
    		case 45:
    			if(Double.parseDouble(valueData43)<0||Double.parseDouble(valueData43)>300){
					return null;
				}//@@过滤突变错误数据
    			//String valueData45 = getValue(data,markLen);
    			mElectricThresholdBean.setOverCurrent(valueData43);		//过流
    			markLen = markLen+8;
    			break;
    		case 46:
    			if(Double.parseDouble(valueData43)<0||Double.parseDouble(valueData43)>3500){
					return null;
				}//@@过滤突变错误数据
    			//String valueData46 = getValue(data,markLen);
    			mElectricThresholdBean.setLeakCurrent(valueData43);		//漏电
    			markLen = markLen+8;
    			break;
    		}
    	}
    	return mElectricThresholdBean;
    }
    
    public static String getValue(byte[] data,int markLen){
    	byte[] heartByteTwo = new byte[2];
    	heartByteTwo[0] = data[markLen+2];
    	heartByteTwo[1] = data[markLen+3];
    	int heartTwo = IntegerTo16.bytesToInt(heartByteTwo);
		byte[] heartByte = new byte[2];
		heartByte[0] = data[markLen+5];
		heartByte[1] = data[markLen+6];
		int heartOne = IntegerTo16.bytesToInt(heartByte);
		String valueData = Utils.electricValues(heartOne, heartTwo);
    	return valueData;
    }
    
    public static int getString(List<String> list){
    	if(list == null||list.get(0)==null){
    		return -1;
    	}
    	int ss = (int)Float.parseFloat(list.get(0));
    	return ss;
    	/*for(String str:list){
    		if(str!=null&&str.length()>0){
    			int ss = (int) Float.parseFloat(str);
    			if(ss>0){
    				return ss;
    			}
    		}
    	}
    	return -1;*/
    }
    
    public static boolean smokeChanged(Map<String,Long> newSmokes,Map<String,Long> oldSmokes){
    	int oldSize = oldSmokes.size();
    	int newSize = newSmokes.size();
    	int len = oldSize-newSize;
    	if(len!=0){
    		return true;
    	}else{
    		for (String key : newSmokes.keySet()) {
        		boolean con = oldSmokes.containsKey(key);
        		if(!con){
        			return true;
        		}
    		}
    	}
		return false;
    }
    
    /**
	 * 求c在strs中出现的次数， （求SQL语句中带有几个问号）
	 * add lzo
	 * @return
	 */
	public static int getNum(String strs, String c) {
		int num = 0;
		String newstr = "";
		for (int i = 0; i < strs.length(); i++) {
			int j = i + 1;
			newstr = strs.substring(i, j);
			if (strs == c || newstr.equals(c)) {
				num++;
			}
		}
		return num;
	}
	
	/**
	 * @author lzo
	 * 2017/4/26  根据字符串来统计记录条数。
	 * @return 
	 */
	public static int getTotalCount(String sql){
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			while(rs.next()){
				result ++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}
	
	public static List<String> getLossSmokeList(Map<String,Long> newSmokes,Map<String,Long>oldSmokes){
    	List<String> list=new ArrayList<String>();
    	for(String newSmokeKey:newSmokes.keySet()){
    		boolean con=oldSmokes.containsKey(newSmokeKey);
    		if(!con){
    			list.add(newSmokeKey);
    		}
    	}
    	return list;
    }
    public static List<String> getUpSmokeList(Map<String,Long> newSmokes,Map<String,Long>oldSmokes){
    	List<String> list=new ArrayList<String>();
    	for(String oldSmokeKey:oldSmokes.keySet()){
    		boolean con=newSmokes.containsKey(oldSmokeKey);
    		if(!con){
    			list.add(oldSmokeKey);
    		}
    	}
    	return list;
    }
    
    /**
     * @param t_Name 表名
     * @return 返回表中的列名
     */
    public static List<String> getColumnsByT_Name(String t_Name){
    	List<String> t_list = new ArrayList<String>();
    	String sql = "SHOW COLUMNS FROM "+t_Name;
    	Connection conn = DBConnectionManager.getConnection();
    	PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
    	ResultSet rs = null;
    	try {
			rs = ps.executeQuery();
			while(rs.next()){
				t_list.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
    	
    	return t_list;
    }
    
    /**
     * @param 查询语句
     * @return 返回语句中的列名
     */
    public static List<String> getColumnsBySql(String t_Sql){
    	List<String> t_list = new ArrayList<String>();
    	t_Sql = t_Sql.substring(0,t_Sql.toLowerCase().indexOf("from"));
    	String str[] = t_Sql.split(",");
		for(int i = 0;i<str.length;i++){
			if(i == 0){
				t_list.add(str[i].substring(7));
			}else if(i == str.length -1){
				t_list.add(str[i].substring(0, str[i].indexOf(" ")));
			}else{
				t_list.add(str[i]);
			}
		}
    	return t_list;
    }
    
    /**
     * @author lzo
     * 获取真实的IP地址
     */
    public static String getIpAddress(HttpServletRequest request) { 
        String ip = request.getHeader("x-forwarded-for"); 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
          ip = request.getHeader("Proxy-Client-IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
          ip = request.getHeader("WL-Proxy-Client-IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
          ip = request.getHeader("HTTP_CLIENT_IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
          ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
          ip = request.getRemoteAddr(); 
        } 
        return ip; 
      } 
    
    /**
     * 将日期格式2017-01-01 18:25:58转换成LONG长整型
     */
    public static long getTimeByStr(String times){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = null;
    	long newTime = 0l;
    	try {
			date = sdf.parse(times);
			newTime = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return newTime;
    }
    
    /**
     * 根据DATA日期返回周几数字
     */
    public static int getWeekOfDate() {
    	Date date = new Date();
    	SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
    	dateFm.format(date);
        int[] weekDays = {7, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    
    /**
     * 给终端下发信息数据
     */
    public static void sendMessageRepeater(String repeater,byte[] ack){
    	if(StringUtils.isNotBlank(repeater)){
    		RePeaterDataDao  rdd  = new RePeaterDataDaoImpl();
    		String oldIp = rdd.getIpByRepeater(repeater); //根据repeaterMac 查询原有的外网Ip
    		String newIp = Constant.outerIp;  //  IpUtil.getIP(1);
    		if(StringUtils.isNotBlank(oldIp))
    			oldIp = oldIp.replace("\r\n", "");
    		if(StringUtils.isNotBlank(newIp))
    			newIp = newIp.replace("\r\n", "");
    		System.out.println("oldIp: "+oldIp+"\nnewIp:"+newIp+"\nrepeater:"+repeater);
    		if (newIp.equals(oldIp)) { //repeater对应session在本机
    			IoSession session = SessionMap.newInstance().getSession(repeater);
    			if(session!=null){
    				IoBuffer buf = IoBuffer.wrap(ack);
    		        WriteFuture future = session.write(buf);  
    		        future.awaitUninterruptibly(100);
    		        if( future.isWritten() ){
    		           System.out.println("heartAck send sucess!");
    		        }else{
    		        	System.out.println("heartAck send failed!");
    		        }
    			}else{
    				System.out.println("区域管理终端不在线----------session=" + session);
    			}
    		} else { //repeater对应session不在本机
    			String url = "http://"+oldIp + ":51091/fireSystem/cmdTodev.do";
    			Map<String,String> map = new HashMap<>();
    			String ackStr = IntegerTo16.bytes2Hex(ack);
    			map.put("repeater", repeater);
    			map.put("ackStr", ackStr);
    			String map2 = OneNetHttpMethod.getMap(url, map);
    			System.out.println("转发结果："+map2);
    		}
			
    	}
    }
    /**
     * 给终端下发信息数据，并返回是否成功
     */
    public static boolean sendMessageRepeater(String repeater,byte[] ack,String mac){
    	boolean result = false;
    	if(StringUtils.isNotBlank(repeater)){
    		RePeaterDataDao  rdd  = new RePeaterDataDaoImpl();
    		String oldIp = rdd.getIpByRepeater(repeater); //根据repeaterMac 查询原有的外网Ip
    		String newIp = Constant.outerIp;  //  IpUtil.getIP(1);
    		if(StringUtils.isNotBlank(oldIp))
    			oldIp = oldIp.replace("\r\n", "");
    		if(StringUtils.isNotBlank(newIp))
    			newIp = newIp.replace("\r\n", "");
    		System.out.println("oldIp: "+oldIp+"\nnewIp:"+newIp+"\nrepeater:"+repeater);
			IoSession session = SessionMap.newInstance().getSession(repeater);
			if(session!=null){
				IoBuffer buf = IoBuffer.wrap(ack);
		        WriteFuture future = session.write(buf);  
		        future.awaitUninterruptibly(100);
		        if( future.isWritten() ){
		        	controlDev.put(mac, 3);//设备命令发送中。。。
    				result = true;
		           System.out.println("heartAck send sucess!");
		        }else{
		        	System.out.println("heartAck send failed!");
		        }
			}else { //repeater对应session不在本机
    			String url = "http://"+oldIp + ":51091/fireSystem/cmdTodev.do";
    			Map<String,String> map = new HashMap<>();
    			String ackStr = IntegerTo16.bytes2Hex(ack);
    			map.put("repeater", repeater);
    			map.put("ackStr", ackStr);
    			String map2 = OneNetHttpMethod.getMap(url, map);
    			System.out.println("转发结果："+map2);
    			if(StringUtils.isBlank(map2)){
    				controlDev.put(mac, 2);//设备命令发送中。。。
    				result = true;
    			}else if(map2.equals("success")){
    				controlDev.put(mac, 3);//设备命令发送中。。。
    				result = true;
    			}
    		}
    	}
    	return result;
    }
    
    /**
     * 实时刷新后台主机状态数据
     * @param repeater
     * @param ack
     */
    public static boolean ifLineRepeater(String repeater){
    	boolean online=true;
    	Jedis jedis = RedisConnection.getJedis();
		if (jedis!=null) {
			String requestId = UUID.randomUUID().toString().replace("-", "");
			while(!RedisOps.tryGetDistributedLock(jedis, "L"+repeater, requestId, 10000)){
				try {
					Thread.currentThread().sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if (RedisOps.exist(jedis,"R"+repeater)){
				Repeater rep  = (Repeater)RedisOps.getObject(jedis,"R"+repeater);
				if (rep.getNetState()==0){
					online=false;
				}
			}
			
			RedisOps.releaseDistributedLock(jedis, "L"+repeater, requestId);
			jedis.close();
		}
    	
//    	if(StringUtils.isNotBlank(repeater)){
//    		RePeaterDataDao  rdd  = new RePeaterDataDaoImpl();
//    		String oldIp = rdd.getIpByRepeater(repeater); //根据repeaterMac 查询原有的外网Ip
//    		String newIp =Constant.outerIp; 
//    		if(oldIp!=null&&oldIp!=""){
//    			oldIp = oldIp.replace("\r\n", "");
//    			newIp = newIp.replace("\r\n", "");
//	    		if (oldIp.equals(newIp)) { //repeater对应session在本机
//	    			if(RepeaterMap.newInstance().ifOffline(repeater)){
//						bool = false;
//					} else {
//						bool = true;
//					}
//	    		} else { //repeater对应session不在本机
//	    			String url = "http://"+oldIp + ":51091/fireSystem/ifLineRepeater.do";
//	    			Map<String,String> map = new HashMap<>();
//	    			String ackStr = "";
//	    			map.put("repeater", repeater);
//	    			map.put("ackStr", ackStr);
//	    			bool = OneNetHttpMethod.getRepeaterUrl(url, map);
//	    		}
//				
//    		}
//    	}
		return online;
    }
    
    
    /**给单个水压设置波动阈值和上报时间间隔**/
    public static boolean updateSettingsByMac(String waterMac,int waveTime, int waveValue,int highGage,int lowGage,int highGage2,int lowGage2){
    	WaterInfoDao wid = new WaterInfoDaoImpl();
    	Water water = wid.getWaterByMac(waterMac);
    	String repeaterMac = water.getRepeaterMac();
    	RePeaterData repeaterData = new RePeaterData();
    	repeaterData.setRepeatMac(repeaterMac);
    	repeaterData.setWaterMac(waterMac);
    	byte seqH= (byte)0x10;
    	byte seqL=(byte)0x10;
    	repeaterData.setSeqH(seqH);
    	repeaterData.setSeqL(seqL);
    	byte[] ack = ClientPackage.ackToWaterGage4(repeaterData,(byte)0x14 , waveValue, waveTime,highGage,lowGage,highGage2,lowGage2);
    	//命令下发
    	boolean result=sendMessageRepeater2(repeaterMac,ack);
    	
    	return result;
    }
    
    /**
     * 给终端下发信息数据
     * 和下面一个hostLevenUp方法一样逻辑
     */
    public static boolean sendMessageRepeater2(String repeater,byte[] ack){
    	String result = "";
    	boolean bool = false;
    	if(StringUtils.isNotBlank(repeater)){
    		RePeaterDataDao  rdd  = new RePeaterDataDaoImpl();
    		String oldIp = rdd.getIpByRepeater(repeater); //根据repeaterMac 查询原有的外网Ip
    		String newIp = Constant.outerIp; 
    		oldIp = oldIp.replace("\r\n","");
    		newIp = newIp.replace("\r\n", "");
    		System.out.println("oldIp: "+oldIp+"\nnewIp:"+newIp+"\nrepeater:"+repeater);
    		if (oldIp.equals(newIp)) { //repeater对应session在本机
    			IoSession session = SessionMap.newInstance().getSession(repeater);
    			if(session!=null){
    				IoBuffer buf = IoBuffer.wrap(ack);
    		        WriteFuture future = session.write(buf);  
    		        future.awaitUninterruptibly(100);
    		        if( future.isWritten() ){
    		        	 System.out.println("命令下发成功");
						 result ="命令下发成功";
						 bool = true;
    		        }else{
    		        	System.out.println("命令下发失败");
			        	result ="命令下发失败";
    		        }
    			}else{
    				System.out.print("找不到session");
					result ="找不到session";
    			}
    		} else { //repeater对应session不在本机
    			String url = "http://"+oldIp + ":51091/"+Constant.ContextPath+"/cmdTodev.do";
    			Map<String,String> map = new HashMap<>();
    			String ackStr = IntegerTo16.bytes2Hex(ack);
    			map.put("repeater", repeater);
    			map.put("ackStr", ackStr);
    			result = OneNetHttpMethod.getMap(url, map);
    			if(result.equals(ackStr) || result.equals("命令下发成功")){
    	    		bool = true;
    	    	}
    			System.out.println("命令下发结果："+result);
    		}
    	}
    	return bool;
    }
    
    /**
     * 给终端下发设备列表
     */
    public static void sendRepeaterList(String repeater){
    	if(StringUtils.isNotBlank(repeater)){
    		RePeaterData mRePeaterData = new RePeaterData();
    		mRePeaterData.setRepeatMac(repeater);
    		String sqlString= String.format("%04x", SequenceEntity.getIntance().getSeq());
    		byte seqL=new IntegerTo16().str16ToByte(sqlString.substring(0,2));
			byte seqH=new IntegerTo16().str16ToByte(sqlString.substring(2,4));
			mRePeaterData.setSeqL(seqL);
			mRePeaterData.setSeqH(seqH);
			mGetSmokeMacByRepeaterDao = new GetSmokeMacByRepeaterDaoImpl();
			List<String> listStr = mGetSmokeMacByRepeaterDao.getSmokeMacByRepeater(repeater);
			int count=listStr.size()*4+2;
			byte[] ack = ClientPackage.synchronousFire(mRePeaterData,listStr,count);
			IoSession session = SessionMap.newInstance().getSession(repeater);
			if(session!=null){
				System.out.println("---------send3 sucess3!---------"+repeater);
				System.out.println(session+":"+session.toString());
	    		System.out.println("---------send3 sucess3!---------"+repeater);
				Timer timer = new Timer();     
	            timer.schedule(new LzstoneTimeTask(ack,session),0,1*5000); 
	            Timer oldTimer=TimerMap.newInstance().getTimer(repeater);
				if(oldTimer!=null){
					oldTimer.cancel();
				}
	            TimerMap.newInstance().addTimer(repeater, timer);
			}else{
				System.out.println("33333区域管理终端不在线----------session33333=" + session);
			}
    	}
    }
    /**
     * 返回6个月之前的日期
     */
    public static String getSixDate(){
    	Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -6);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(calendar.getTime());
    }

    
    //@@12.22
    public static void saveChangehistory(String userId,String mac,int state) {
		GetSmokeMacByRepeaterDao mbrDao11 = new GetSmokeMacByRepeaterDaoImpl();
		ElectricHistoryEntity ehe = new ElectricHistoryEntity();
		ehe.setUserId(userId);
		ehe.setMac(mac);
		ehe.setState(state);
		mbrDao11.insert_Electric_change_history(ehe);
		
	}
    
    public static short getrssi(byte hbyte,byte lbyte){		//15+16
    	short s1 = 0;
    	byte[] bytedata = new byte[2];
    	bytedata[0] = hbyte;
    	bytedata[1] = lbyte;
    	s1 = IntegerTo16.bytesToShort(bytedata);
    	return s1;
    }
    public static boolean getSystemIde(){
    	
    	String computerName = System.getProperty ("os.name").toLowerCase(); 
    	if(computerName.contains("linux")){
    		return true;
    	}
    	return false;
    }
    
    public static String getCurrentRunningServerComputerName () {  
        // Windows  
        String computerName = System.getenv().get("COMPUTERNAME");  
          
        if (computerName == null) {  
        	InetAddress iAddress;
			try {
				iAddress = InetAddress.getLocalHost();
				computerName = iAddress.getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
        }  
        return computerName;  
      }  
    
    /**
     * host sub
     */
    public static String getHostInfoSub(String host){
    	String newhost = convertMD5(host);
    	String result = newhost.substring(5);
    	return result;
    }
    
    /**
     * host add
     * @param host
     * @return
     */
    public static String getHostInfoAdd(String host){
    	String newHost = "hrsst"+host;
    	String result = convertMD5(newHost);
    	return result;
    }
    
    public static String string2MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    
    public static String convertMD5(String inStr) {
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;
    }
    
    
    public static BodyObj resolveBody(String body, boolean encrypted) throws Exception {
        JSONObject jsonMsg = new JSONObject(body);
        BodyObj obj = new BodyObj();
        obj.setNonce(jsonMsg.getString("nonce"));
        obj.setMsgSignature(jsonMsg.getString("msg_signature"));
        if (encrypted) {
            if (!jsonMsg.has("enc_msg")) {
                return null;
            }
            obj.setMsg(jsonMsg.getString("enc_msg"));
        } else {
            if (!jsonMsg.has("msg")) {
                return null;
            }
            obj.setMsg(jsonMsg.getJSONObject("msg"));
        }
        return obj;
    }
    
    /**
     * 功能描述: 检查接收数据的信息摘要是否正确。<p>
     *          方法非线程安全。
     * @param obj 消息体对象
     * @param token OneNet平台配置页面token的值
     * @return
     */
    public static boolean checkSignature(BodyObj obj, String token)  {
        //计算接受到的消息的摘要
        //token长度 + 8B随机字符串长度 + 消息长度
        byte[] signature = new byte[token.length() + 8 + obj.getMsg().toString().length()];
        System.arraycopy(token.getBytes(), 0, signature, 0, token.length());
        System.arraycopy(obj.getNonce().getBytes(), 0, signature, token.length(), 8);
        System.arraycopy(obj.getMsg().toString().getBytes(), 0, signature, token.length() + 8, obj.getMsg().toString().length());
        mdInst.update(signature);
        byte[] calSig = Base64.encodeBase64(mdInst.digest());
        System.out.println("check signature: result:{}  receive sig:{},calculate sig: {}");
        return calSig.equals(obj.getMsgSignature());
    }
    
    /**
     * 功能描述:在OneNet平台配置数据接收地址时，平台会发送URL&token验证请求使用此功能函数验证token
     * @param msg 请求参数 的值
     * @param nonce 请求参数的值
     * @param signature 请求参数的值
     * @param token OneNet平台配置页面token的值
     * @return token检验成功返回true；token校验失败返回false
     */
    public static boolean checkToken(String msg,String nonce,String signature, String token) {

        byte[] paramB = new byte[token.length() + 8 + msg.length()];
        System.arraycopy(token.getBytes(), 0, paramB, 0, token.length());
        System.arraycopy(nonce.getBytes(), 0, paramB, token.length(), 8);
        System.arraycopy(msg.getBytes(), 0, paramB, token.length() + 8, msg.length());
        String sig =  com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(mdInst.digest(paramB));
        System.out.println("url&token validation: result {},  detail receive:{} calculate:{}");
        return sig.equals(signature.replace(' ','+'));
    }
    
    /**
     * 电话语音报警通知
     * @param args
     * @throws phone1 接收电话   TXurl=https://cloud.tim.qq.com/v5/tlsvoicesvr/sendvoiceprompt
     * @throws JSONException
     * @throws IOException
     */
    
    public static String telphone(String phone1,String devName,String address,AreaidGoEasy areaidGoEasy){
    	String sentext = "您好，"+devName+"在"+address+"检测到异常，请及时检查现场情况并作出处理。";
    	// 短信应用SDK AppID
    	int appid = areaidGoEasy.getAppid(); // 1400开头
    	// 短信应用SDK AppKey
    	String appkey = areaidGoEasy.getAppkey();
    	
    	SmsVoicePromptSender vpsender = new SmsVoicePromptSender(appid, appkey);
    	SmsVoicePromptSenderResult result = null;
    	try {
				result = vpsender.send("86", phone1, 2, 2, sentext, "");
    		} catch (HTTPException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
       
    	
    	return result.toString();
    }
    
    /**
     * 单发短信报警通知
     * @param phone1
     * @param devName  TXurl=https://yun.tim.qq.com/v5/tlssmssvr/sendsms
     * @param address
     * @return
     */
    public static String sentMessage(String phone1,String devName,String address,AreaidGoEasy areaidGoEasy){
    	String sentext = "您好，"+devName+"在"+address+"检测到异常，请及时检查现场情况并作出处理。";
    	// 短信应用SDK AppID
    	int appid = areaidGoEasy.getAppid(); // 1400开头

    	// 短信应用SDK AppKey
    	String appkey = areaidGoEasy.getAppkey();
    	
    	SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
    	SmsSingleSenderResult result = null;
    	 try {
    		 result=ssender.send(0, "86", phone1, sentext, "", "");
		} catch (HTTPException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
    }
    
    /**
     * 语音验证码
     * @param phone1
     * @return 返回result中errmsg为OK则成功
     */
  /*  public static String voiceCodeSender(String phone1){
    	Random  d = new Random();
    	String code = "";
    	for(int i=0;i<4;i++){
	    	int num = d.nextInt(10);
	    	code += num+"";
    	}
    	// 短信应用SDK AppID
    	int appid = 1400095962; // 1400开头

    	// 短信应用SDK AppKey
    	String appkey = "f5340401e535a471d0b86a95ef36094a";
    	
    	SmsVoiceVerifyCodeSender  vpsender = new SmsVoiceVerifyCodeSender(appid, appkey);
    	SmsVoiceVerifyCodeSenderResult  result = null;
    	try {
				result = vpsender.send("86", phone1, code,2, "");
				 System.out.print(result);
    		} catch (HTTPException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
       
    	
    	return result.toString();
    }
    */
    /**
     * 将字符串切割成字符串数组
     * @param value 字符串
     * @param step  平均截取step个字符
     * @return
     */
    public static String[] getStringArr(String value,int step){
    	int length = value.length()/step;
    	String[] str = new String[length];
    	int j =0;
    	for (int i = 0; i < value.length(); i = i+step) {
			str[j] = value.substring(i, i+step);
			j++;
		}
    	return str;
    }
    
    //@@2字节转化为有符号十进制数
    public static short getbyte2short(byte h,byte l){
		short s = 0;  
		if((h&0x80)==0x80){
			s = (short)-((((h&0x7f) << 8) | l & 0xff));
		}else{
			s = (short) (((h << 8) | l & 0xff));
		}
		return s;
	}
    
    /**
     * @param 获取文件，拆分成二进制1000字节进行分组，保存到list集合中
     * @return
     */
    public static List<byte[]> getLevelUp(File filePath,int fact) {
		byte[] ack = getContent(filePath);
		int ackLength = ack.length;
		List<byte[]> olist =new ArrayList<byte[]>();
		System.out.println(ackLength);
		System.out.println(ackLength%fact);
		int ackMax = ackLength/fact*fact;
		byte[] afk = new byte[fact];
		byte[] akf = new byte[ackLength%fact];
		
		for (int i = 0; i < ack.length; i++) {
			if(i>=ackMax||ackMax==0){
				akf[i%fact] = ack[i];
			}else{
				afk[i%fact] = ack[i];
			}
			if(i%fact==fact-1){
				olist.add(afk);
				afk = new byte[fact];
			}else if(i==ack.length -1){
				olist.add(akf);
			}
		}
		
		return olist;
	}
    
    /**
     * @param 获取文件，拆分成二进制1000字节进行分组，保存到list集合中
     * @return
     */
    public static List<byte[]> getLevelUp(String filePath) {
		byte[] ack = getContent(filePath);
		int ackLength = ack.length;
		List<byte[]> olist =new ArrayList<byte[]>();
		System.out.println(ackLength);
		System.out.println(ackLength%1000);
		int ackMax = ackLength/1000*1000;
		byte[] afk = new byte[1000];
		byte[] akf = new byte[ackLength%1000];
		
		for (int i = 0; i < ack.length; i++) {
			if(i>=ackMax||ackMax==0){
				akf[i%1000] = ack[i];
			}else{
				afk[i%1000] = ack[i];
			}
			if(i%1000==999){
				olist.add(afk);
				afk = new byte[1000];
			}else if(i==ack.length -1){
				olist.add(akf);
			}
		}
		
		return olist;
	}
    
    /**
     * @param 获取文件，拆分成二进制1000字节进行分组，保存到list集合中
     * @return
     */
    public static List<byte[]> getLevelUp(File filePath) {
		byte[] ack = getContent(filePath);
		int ackLength = ack.length;
		List<byte[]> olist =new ArrayList<byte[]>();
		System.out.println(ackLength);
		System.out.println(ackLength%1000);
		int ackMax = ackLength/1000*1000;
		byte[] afk = new byte[1000];
		byte[] akf = new byte[ackLength%1000];
		
		for (int i = 0; i < ack.length; i++) {
			if(i>=ackMax||ackMax==0){
				akf[i%1000] = ack[i];
			}else{
				afk[i%1000] = ack[i];
			}
			if(i%1000==999){
				olist.add(afk);
				afk = new byte[1000];
			}else if(i==ack.length -1){
				olist.add(akf);
			}
		}
		
		return olist;
	}
    
    
    public static byte[] getContent(String filePath) {
		File file = new File(filePath);
		long fileSize = file.length();
		if (fileSize > Integer.MAX_VALUE) {
			System.out.println("file too big...");
			return null;
		}
		byte[] buffer = new byte[(int) fileSize];
		try {
			FileInputStream fi = new FileInputStream(file);
			int offset = 0;
			int numRead = 0;
			while (offset < buffer.length
			&& (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
				offset += numRead;
			}
			// 确保所有数据均被读取
			if (offset != buffer.length) {
			throw new IOException("Could not completely read file "
						+ file.getName());
			}
			fi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}
    
    public static byte[] getContent(File file) {
//		File file = new File(filePath);
		long fileSize = file.length();
		if (fileSize > Integer.MAX_VALUE) {
			System.out.println("file too big...");
			return null;
		}
		byte[] buffer = new byte[(int) fileSize];
		try {
			FileInputStream fi = new FileInputStream(file);
			int offset = 0;
			int numRead = 0;
			while (offset < buffer.length
			&& (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
				offset += numRead;
			}
			// 确保所有数据均被读取
			if (offset != buffer.length) {
			throw new IOException("Could not completely read file "
						+ file.getName());
			}
			fi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}
 
	/**
	 * the traditional io way
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(String filename) throws IOException {
 
		File f = new File(filename);
		if (!f.exists()) {
			throw new FileNotFoundException(filename);
		}
 
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bos.close();
		}
	}
	
	/**
     * lzo主机升级
     */
    public static boolean hostLevenUp(String repeater,byte[] ack){
    	boolean bool = false;
    	if(StringUtils.isNotBlank(repeater)){
    		RePeaterDataDao  rdd  = new RePeaterDataDaoImpl();
    		String oldIp = rdd.getIpByRepeater(repeater); //根据repeaterMac 查询原有的外网Ip
    		String newIp = Constant.outerIp; //IpUtil.getIP(1);
    		oldIp = oldIp.replace("\r\n","");
    		newIp = newIp.replace("\r\n", "");
    		System.out.println("oldIp: "+oldIp+"\nnewIp:"+newIp+"\nrepeater:"+repeater);
    		if (oldIp.equals(newIp)) { //repeater对应session在本机
    			IoSession session = SessionMap.newInstance().getSession(repeater);
    			if(session!=null){
    				IoBuffer buf = IoBuffer.wrap(ack);
    		        WriteFuture future = session.write(buf);  
    		        future.awaitUninterruptibly(100);
    		        if( future.isWritten() ){
    		        	bool = true;
    		           System.out.println("heartAck send sucess!");
    		        }else{
    		        	System.out.println("heartAck send failed!");
    		        }
    			}else{
    				System.out.println("区域管理终端不在线----------session=" + session);
    			}
    		} else { //repeater对应session不在本机
    			String url = "http://"+oldIp + ":51091/"+Constant.ContextPath+"/cmdTodev.do";
    			Map<String,String> map = new HashMap<>();
    			String ackStr = IntegerTo16.bytes2Hex(ack);
    			map.put("repeater", repeater);
    			map.put("ackStr", ackStr);
    			String map2 = OneNetHttpMethod.getMap(url, map);
    			System.out.println("转发结果："+map2);
    			if(map2==null){
    				bool = false;
    			}else if(map2.equals("sucess")){
    				bool = true;
    			}else{
    				bool = false;
    			}
    		}
			
    	}
    	return bool;
    }
	
	    //推送报警方法
		public static void pushAlarmText(String[] args) {
			PushAlarmMsgDao mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
			GetPushUserIdDao mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			String mac = "001CE6F8";
			PushAlarmMsgEntity push = mPushAlarmMsgDao.getPushAlarmMsg(mac,202);
			List<String> userList = mGetPushUserIdDao.getAllUser(mac);
			userList.add("18312286056");
			Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(userList);
			new MyThread(push,userList,iosMap).start();  //开启推送消息的线程
			new WebThread(userList,mac).start();
		}
	
	
	 		/**
	       * post请求
	       * @param url
	       * @param json
	      * @return
	       */
	      public static JSONObject doPost(String url,JSONObject json,String appid){
	          DefaultHttpClient client = new DefaultHttpClient();
	          HttpPost post = new HttpPost(url);
	          JSONObject response = null;
	          try {
	              StringEntity s = new StringEntity(json.toString());
	              s.setContentEncoding("UTF-8");
	              s.setContentType("application/json");//发送json数据需要设置contentType
	              post.setEntity(s);
	              HttpResponse res = client.execute(post);
	              if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	                  HttpEntity entity = res.getEntity();
	                  String result = EntityUtils.toString(res.getEntity());// 返回json格式：
//	                  response = JSONObject.fromObject(result);
	              }
	          } catch (Exception e) {
	              throw new RuntimeException(e);
	          }
	          return response;
	      }
	
	//删除APPID对应订阅
	public static void deleSubscribe(String appId,String serset) {
		try {
			
			HttpsUtil httpsUtil = new  HttpsUtil();
			httpsUtil.initSSLConfigForTwoWay();
			String token = Authentication.getAccessToken(appId, serset);
			
			String urlSubscribe = "https://device.api.ct10649.com:8743/iocm/app/sub/v1.2.0/subscriptions?appId="+appId;
			
			Map<String, String> header = new HashMap<>();
			header.put(Constant.HEADER_APP_KEY, appId);
			header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + token);
			HttpResponse httpResponse = httpsUtil.doDelete(urlSubscribe, header);
	        System.out.print(httpResponse.getStatusLine());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getbyte2Int(byte h, byte l) {
		int s = 0;
		if((h&0x80)==0x80){
			s = (int)-((((h&0x7f) << 8) | l & 0xff));
		}else{
			s = (int) (((h << 8) | l & 0xff));
		}
		return s;
	}
	
	public static void pushAlarmInfo(AlarmPushOnlyEntity pushEntity){
		if(pushEntity==null){
			return;
		}
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mFromRepeaterAlarmDao.addAlarmMsg(pushEntity.getSmokeMac(), pushEntity.getRepeaterMac(), pushEntity.getAlarmType(),pushEntity.getAlarmFamily());
		
		PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(pushEntity.getSmokeMac(), pushEntity.getAlarmType());
		if(pushAlarm==null){
			return;
		}
		pushAlarm.setAlarmFamily(pushEntity.getAlarmFamily());
		pushAlarm.setAlarmType(pushEntity.getAlarmType()+"");
		
		
		List<String> users = mGetPushUserIdDao.getAllUser(pushEntity.getSmokeMac());
		List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(pushEntity.getSmokeMac());
		
		if(users!=null&&users.size()>0){
			Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
			new MyThread(pushAlarm,users,iosMap).start();
			new WebThread(users,pushEntity.getSmokeMac()).start();
			if (txtUserList != null &&txtUserList.size()>0) {
				new TxtThread(txtUserList,pushEntity.getSmokeMac()).start();        //短信通知的线程
			}
		}
	}
	
	/*
	 * 判断设备类型，电气设备则推送反的，alarmFamily和alarmType，以便手机处理
	 */
	public static void pushAlarmInfo(AlarmPushOnlyEntity pushEntity,int deviceType){
		if(pushEntity==null){
			return;
		}
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mFromRepeaterAlarmDao.addAlarmMsg(pushEntity.getSmokeMac(), pushEntity.getRepeaterMac(), pushEntity.getAlarmType(),pushEntity.getAlarmFamily());
		
		PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(pushEntity.getSmokeMac(), pushEntity.getAlarmType());
		if(pushAlarm==null){
			return;
		}
		if(deviceType==5){
			pushAlarm.setAlarmType(pushEntity.getAlarmFamily());
			pushAlarm.setAlarmFamily(pushEntity.getAlarmType()+"");
		}else{
			pushAlarm.setAlarmFamily(pushEntity.getAlarmFamily());
			pushAlarm.setAlarmType(pushEntity.getAlarmType()+"");
		}
		
		
		
		List<String> users = mGetPushUserIdDao.getAllUser(pushEntity.getSmokeMac());
		List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(pushEntity.getSmokeMac());
		
		if(users!=null&&users.size()>0){
			Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
			new MyThread(pushAlarm,users,iosMap).start();
			new WebThread(users,pushEntity.getSmokeMac()).start();
			if (txtUserList != null &&txtUserList.size()>0) {
				new TxtThread(txtUserList,pushEntity.getSmokeMac()).start();        //短信通知的线程
			}
		}
	}
	
	public static void pushAlarmInfo(AlarmPushOnlyEntity pushEntity,Object obj){
		if(pushEntity==null){
			return;
		}
		mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		mFromRepeaterAlarmDao.addAlarmMsg(pushEntity);
		
		PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(pushEntity.getSmokeMac(), pushEntity.getAlarmType());
		if(pushAlarm==null){
			return;
		}
		pushAlarm.setAlarmFamily(pushEntity.getAlarmType()+"");
		pushAlarm.setAlarmType(pushEntity.getAlarmFamily());
		
		
		List<String> users = mGetPushUserIdDao.getAllUser(pushEntity.getSmokeMac());
		List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(pushEntity.getSmokeMac());
		
		if(users!=null&&users.size()>0){
			Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
			new MyThread(pushAlarm,users,iosMap).start();
			new WebThread(users,pushEntity.getSmokeMac()).start();
			if (txtUserList != null &&txtUserList.size()>0) {
				new TxtThread(txtUserList,pushEntity.getSmokeMac()).start();        //短信通知的线程
			}
		}
	}
	
	//	伊特若平台对接心跳接口--无线烟感设备信息
	public static boolean addSmokeInfo(String token,String fsocial_uuid,String fdevice_uuid,String name,String imei,String address){
		boolean result = false;
//		String url = "http://47.104.240.202:8090/tolspp/api?action=obj.interface&method=cagWDeviceInfo";
		String url = "http://120.55.78.242:9058/gzinterface/api?action=obj.interface&method=cagWDeviceInfo";
		if(StringUtils.isBlank(token)){
			token = getToken();
		}
		String param = "token="+token+"&transdata=";
		try {
			JSONObject json = new JSONObject();
			if(StringUtils.isBlank(fsocial_uuid)){
				fsocial_uuid = "a3601a0330224a0f9fe11f79b14cd3fd";
			}
//			String fdevice_uuid = "50f4fedf-be6c-4e8d-9c24-8305932a679c";
			String fdevicecnname = name;//"NB测试烟感";
			String fdeviceclass = "SBTYPE40";
			String ftransmission_id = imei;//"123456789066666";
			String floop_num = "0";
			String fis_nb = "1";
			String faddress = address;//"浙江省平阳县厚垟村后垟南路45号";
			
			json.put("fsocial_uuid", fsocial_uuid);
			json.put("fdevice_uuid", fdevice_uuid);
			json.put("fdevicecnname", fdevicecnname);
			json.put("fdeviceclass", fdeviceclass);
			json.put("ftransmission_id", ftransmission_id);
			json.put("floop_num", floop_num);
			json.put("fis_nb", fis_nb);
			json.put("faddress", faddress);
			param = param + json.toString();
			String data = getAddUid(url,param);
			System.out.println(data);
			JSONObject jsonData = new JSONObject(data);
			if(jsonData.getInt("ret_code")==1){
				result = true;
			}else{
				result = false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//伊特若平台对接报警接口--无线烟感实时监控信息
		public static boolean pushAlarm(String token,String fsocial_uuid,String fdevice_uuid,int alarmType){
			boolean result = false;
//			String url = "http://47.104.240.202:8090/tolspp/api?action=obj.interface&method=cabWireAlarmMonitor";
			String url = "http://120.55.78.242:9058/gzinterface/api?action=obj.interface&method=cabWireAlarmMonitor";
			if(StringUtils.isBlank(token)){
				token = getToken();
			}
			String param = "token="+token+"&transdata=";
			try {
				JSONObject json = new JSONObject();
				String f_type = "G4435";
				if(StringUtils.isBlank(fsocial_uuid)){
					fsocial_uuid = "a3601a0330224a0f9fe11f79b14cd3fd";
				}
				switch(alarmType){
				case 202:
					f_type = "G4435";
					break;
				case 109:
					f_type = "G4427";
					break;
				case 4425:
					f_type = "G4425";
					break;
				case 107:
				case 193:
					f_type = "G4432";
					break;
				case 106:
					f_type = "G4494";
					break;
				case 1:
				default:
					f_type = "G4437";
					break;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String fcome_time = sdf.format(System.currentTimeMillis());
				json.put("f_type", f_type);
				json.put("fsocial_uuid", fsocial_uuid);
				json.put("fdevice_uuid", fdevice_uuid);
				json.put("fcome_time",fcome_time);
				param = param + json.toString();
				String data = getAddUid(url,param);
				System.out.println(data);
				JSONObject jsonData = new JSONObject(data);
				if(jsonData.getInt("ret_code")==1){
					result = true;
				}else{
					result = false;
				}
			} catch (JSONException e) {
//				throw new RuntimeException(e);
				e.printStackTrace();
			}
			return result;
		}
	
	
	//伊特若平台对接添加接口--社会单位信息
	public static boolean addDev(String fsocial_uuids,String fsocial_name,String fprovince_code,
			String fcity_code,String fcounty_code,String ftown_code,String faddress,String flink_man,String ftel_no,
			String flongitude,String flatitude,String funit_type,String fis_active){
		boolean result = false;
		String token = getToken();
//		String url = "http://47.104.240.202:8090/tolspp/api?action=obj.interface&method=cagSocialInfo";
		String url = "http://120.55.78.242:9058/gzinterface/api?action=obj.interface&method=cagSocialInfo";
		String param = "token="+token+"&transdata=";
		try {
			String fsocial_uuid = fsocial_uuids;
			JSONObject json = new JSONObject();
			json.put("fsocial_uuid", fsocial_uuid);
			json.put("fsocial_name", fsocial_name);
			json.put("fprovince_code", fprovince_code);
			json.put("fcity_code", fcity_code);
			json.put("fcounty_code", fcounty_code);
			json.put("ftown_code", ftown_code);
			json.put("faddress", faddress);
			json.put("flink_man", flink_man);
			json.put("ftel_no", ftel_no);
			json.put("flongitude", flongitude);
			json.put("flatitude", flatitude);
			json.put("funit_type", funit_type);
			json.put("fis_active", fis_active);
			param = param + json.toString();
			System.out.println(url+param.toString());
			String data = getAddUid(url,param);
//			String data = HttpRequestUtil.get(url);//(url+"?"+param.toString()); //getUrl(url+"?"+param.toString());
			System.out.println(data);
			JSONObject jsonData = new JSONObject(data);
			if(jsonData.getInt("ret_code")==1){
				result = true;
			}else{
				result = false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//刷新伊特若平台对接token
	public static String getFlushToken(String token){
		String result = "";
//		String url = "http://47.104.240.202:8090/tolspp/api";
		String url = "http://120.55.78.242:9058/gzinterface/api";
		String param = "action=token&method=refreshToken&token="+token;
		result = getAccessToKen(url,param);
		try {
			JSONObject json = new JSONObject(result);
			if(json.getInt("code")==1){
				result = token;
			}else{
				result = json.getString("desc");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//获取伊特若平台对接token
	public static String getToken(){
		String result = "";
//		String url = "http://47.104.240.202:8090/tolspp/api";
		String url = "http://120.55.78.242:9058/gzinterface/api";
		String param = "action=token&method=getToken&grant_type=client&appid=iczjetn&secret=23e837c1cc3711e89d7a70106fb7380a";
		result = getAccessToKen(url,param);
		try {
			JSONObject json = new JSONObject(result);
			if(json.getInt("code")==1){
				JSONObject data = new JSONObject(json.getString("data"));
				result = data.getString("token");
			}else{
				result = json.getString("desc");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//get uri请求数据
	public static String getAccessToKen(String url,String param){
		String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            System.out.println(urlNameString);
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
		return result;
	}
	
	//POST uri请求数据
	public static String getAddUid(String url,String param){
		OutputStream out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(5000);
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = conn.getOutputStream();
			// 发送请求参数
			// out.print(param.getBytes("UTF-8"));
			out.write(param.getBytes("UTF-8"));
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
				}
			}
		catch (Exception e) {
				e.printStackTrace();
			}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
					}
				if (in != null) {
					in.close();
					}
				}
			catch (IOException ex) {
				ex.printStackTrace();
				}
			}
		return result;
	}
	
	//unicode 码转换
	public static String string2Unicode(String string) {
		 
	    StringBuffer unicode = new StringBuffer();
	 
	    for (int i = 0; i < string.length(); i++) {
	 
	        // 取出每一个字符
	        char c = string.charAt(i);
	 
	        // 转换为unicode
	        unicode.append("\\u" + Integer.toHexString(c));
	    }
	 
	    return unicode.toString();
	}
	
	public static String unicode2String(String unicode) {
		 
	    StringBuffer string = new StringBuffer();
	 
	    String[] hex = unicode.split("\\\\u");
	 
	    for (int i = 1; i < hex.length; i++) {
	 
	        // 转换出每一个代码点
	        int data = Integer.parseInt(hex[i], 16);
	 
	        // 追加成string
	        string.append((char) data);
	    }
	 
	    return string.toString();
	}
	
	public static String string2Unicode(byte[] bytedata) {
		 
	    StringBuffer unicode = new StringBuffer();
	 
	    for (int i = 0; i < bytedata.length; i++) {
	 
	        // 取出每一个字符
	        String str = Integer.toHexString(bytedata[i]);
	 
	        // 转换为unicode
	        unicode.append("\\u" + str);
	    }
	 
	    return unicode.toString();
	}
	
	public static byte[] unicode2bytes(String unicode) {
		
	    StringBuffer string = new StringBuffer();
	    
	    String[] hex = unicode.split("\\\\u");
	 
	    byte[] bytedata = new byte[hex.length];
	    
	    for (int i = 1; i < hex.length; i++) {
	 
	        // 转换出每一个代码点
	        int data = Integer.parseInt(hex[i], 16);
	 
	        // 追加成string
	        char c = (char)data;
	        string.append(c);
	        bytedata[i-1] = (byte) c;
	    }
	    System.out.println(string.toString());
	    return bytedata;
	}
	
	
	//判断一个字符串source中，有几个字符串src
    private static int getStringCount(String source, String src) {
        int index = 0;
        int count = 0;
        int start = 0;
        while ((index = source.indexOf(src, start)) != -1) {
            count++;
            start = index + 1;
        }
        return count;
    }

    //判断一个字符串source中，从指定的位置开始开始计算，字符串src的游标值
    private static int getStringIndex(String source, String src, int beginIndex) {
        int index = 0;
        int start = 0;
        while ((index = source.indexOf(src, start)) != -1 && index < beginIndex) {
            start = index + 1;
        }
        return index;
    }

    //判断一个byte数值在另外一个byte数组中对应的游标值
    public static int getByteIndexOf(byte[] sources, byte[] src, int startIndex) {
        return getByteIndexOf(sources, src, startIndex, sources.length);
    }


    //判断一个byte数值在另外一个byte数组中对应的游标值，指定开始的游标和结束的游标位置
    public static int getByteIndexOf(byte[] sources, byte[] src, int startIndex, int endIndex) {

        if (sources == null || src == null || sources.length == 0 || src.length == 0) {
            return -1;
        }

        if (endIndex > sources.length) {
            endIndex = sources.length;
        }

        int i, j;
        for (i = startIndex; i < endIndex; i++) {
            if (sources[i] == src[0] && i + src.length <= endIndex) {
                for (j = 1; j < src.length; j++) {
                    if (sources[i + j] != src[j]) {
                        break;
                    }
                }

                if (j == src.length) {
                    return i;
                }
            }
        }
        return -1;
    }

    //判断一个byte数组src，在另一个byte数组source中存在的个数
    public static int getByteCountOf(byte[] sources, byte[] src) {
        if (sources == null || src == null || sources.length == 0 || src.length == 0) {
            return 0;
        }
        int count = 0;
        int start = 0;
        int index = 0;
        while ((index = getByteIndexOf(sources, src, start)) != -1) {
            start = index + 1;
            count++;
        }
        return count;
    }

	//lst 不能为空,将List<String> 转化为‘,’分割的String
	public static String list2String(List<String> lst){
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<lst.size()-1;i++){
			sb.append(lst.get(i));
			sb.append(",");
		}
		sb.append(lst.get(lst.size()-1));
		return sb.toString();
	}
	
	//将String转化为byte[], eg "8946464313"->{(byte)0x89,(byte)0x46,(byte)0x46,(byte)0x43,(byte)0x13}
	public static byte[] str2byteArr(String str){
		int n = str.length();
		if (n%2!=0) {
			System.out.println("传入的字符串有误");
		}
		byte[] byteArr=new byte[n/2];
		for(int i=0;i<n/2;i++){
			byteArr[i] = str2byte(str.substring(i*2, i*2+2));
		}
		return byteArr;
	}
	
	//将字符串转化为byte
	public static byte str2byte(String str) {
		return (byte)(hex2int(str.charAt(0))*16+hex2int(str.charAt(1)));
	}
	
	//读取hex字符为int值
	public static int hex2int(char c) {
		int value = 0;
		if (c >= '0' && c <= '9') {
			value = c - '0';
		} else if ( c >= 'a' && c <= 'f') {
			value = c - 'a' + 10;
		} else if ( c >= 'A' && c < 'F') {
			value = c - 'A' + 10;
		}
		return value;
	}
	//判断List对象是否为空
	public static boolean isNullList(List<?> list) {
		if (list == null || list.size() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static  boolean inzhiminArea(String areaId) {
		List<String> areas = new ArrayList<String>();
		areas.add("2364");
		if (areas.contains(areaId)) {
			return true;
		}
		return false;
	}
	
	public static Indexes getCommons(List<String> A,List<String> B){
		Indexes indexes = new Indexes();
		List<Integer> index1 = new ArrayList<Integer>();
		List<Integer> index2 = new ArrayList<Integer>();
		
		if(A.size() < B.size()) {
			for (int i=0;i<B.size();i++) {
				for (int j=0;j<A.size();j++){
					if (B.get(i).equals(A.get(j))){
						index1.add(j);
						index2.add(i);
						break;
					}
				}
			}
		} else {
			for (int i=0;i<A.size();i++) {
				for (int j=0;j<B.size();j++){
					if (A.get(i).equals(B.get(j))){
						index1.add(i);
						index2.add(j);
						break;
					}
				}
			}
		}
		
		indexes.setLst(index1);
		indexes.setLst2(index2);
		return indexes;
	}
	
	//判断烟感报警是否过滤掉,true为过滤，false不过滤
	public static boolean ifPushAlarmSmoke(String smokeMac){
		boolean result = true;
		long nowTime = System.currentTimeMillis();
		long oldTime = 0l;
		if(smokeAlarmTime.get(smokeMac)==null){
			System.out.println("烟感第一次报警");
			smokeAlarmTime.put(smokeMac, nowTime);
			smokeAlarmCount.put(smokeMac, 1);
		}else{
			int count = smokeAlarmCount.get(smokeMac); //统计次数
			if(count>2){
				System.out.println("烟感报警次数"+count);
				oldTime = smokeAlarmTime.get(smokeMac);
				if((nowTime-oldTime)<20000){
					count++;
					smokeAlarmCount.put(smokeMac, count);
					result = false;
					System.out.println("烟感报警满足条件，进入报警"+result);
				}else{
					System.out.println("烟感报警间隔大于20秒，重新记录");
					smokeAlarmCount.put(smokeMac, 1);
				}
			}else{
				System.out.println("不报警，更新时间，累计次数。");
				oldTime = smokeAlarmTime.get(smokeMac);
				if((nowTime-oldTime)>30000){//如果报警间隔20秒，将从头开始统计
					System.out.println("时间大于20秒");
					smokeAlarmCount.put(smokeMac, 1);
				}else{	//如果在30内连续报警，则更新报警时间，并累加次数。
					System.out.println("时间在20秒内，累加次数："+count);
					count++;
					smokeAlarmCount.put(smokeMac, count);
				}
			}
			smokeAlarmTime.put(smokeMac, nowTime);
		}
		return result;
	}
	//用于U特电气设备电流计算方法
	public static String getEnergyValue(int dataValue){
		String result = "0";
		int b1 = dataValue&0x3FFF;
		int b2 = dataValue>>14;
		double c = Math.pow(10, b2); 
		result = b1/c+"";
		return result;
	}

	public static void main(String[] args) throws Exception {
		
		String token = Authentication.getAccessToken(null, null);
		System.out.println(token);
		
		
		/*byte[] byet = null;
		
		
		
		String str1 = "";
		int abc =StringUtils.isBlank(str1)?-1:(int)Float.parseFloat(str1); 
		System.out.println(abc+"<>");*/
		
		/*byte[] sources = {0x7e,0x03,0x7f};
		byte[] src = {0x7f};
		
		int count = getByteCountOf(sources, src);
		
		System.out.println(count);*/
		
		
		/*Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("a", 1);
		map.put("A", 2);
		System.out.println(map.get("a")+map.get("A"));*/
		
		/*Map<String,Long> map = new HashMap<String,Long>();
		
		if(!map.containsKey("a")){
			System.out.println(22);
			map.put("a", 1L);
		}
		
		System.out.println(map.get("a"));
		
//		RedisOps redis = new RedisOps();
		String key = "";
		String value = "";
		String key1 = "a";
		String value1 = "a";
		String key2 = "b";
		String value2 = "";
		String key3 = "";
		String value3 = "b";*/
		
		/*int a = 4;
		int b = a&0x20;
		System.out.println(a+"="+b);
		System.out.println(b-31);
		System.out.println((a&0x20)-31);*/
		/*String[] C = {"768104FF","76810064","7681002F","7681002D","76800593","7680057E","7680057C","7680057B","7680055F","7680054C","7680053B","76800525","7680051C","7680051A","768004FB","768004F8","768004E3","768004E1","768004DF","768004DD","768004D4","768004D2","768004C5","768004C3","76800494","7680048F","7680047A","76800471","7680046C","76800458","7680043B","7680043A","76800435","7680042B","76800420","7680041F","7680041D","76800418","76800409","76800408","768003FF","7680034B","76800345","76800339","76800338","76800336","7680031B","76800301","768002FF","768002F5","768002F0","768002EE","768002eb","768002E0","768002D7","768002D5","768002D4","768002C7","768002BA","768002B1","768002A6","768002A4","76800289","76800283","7680027C","76800277","76800276","76800274","7680026B","76800267","76800265","7680025D","7680025A","76800258","76800255","7680023E","76800233","76800231","7680022b","76800227","76800222","7680021F","7680021D","76800208","768001F9","768001F2","768001EF","768001ED","768001E9","768001E5","768001E2","768001E0","768001DF","768001DB","768001D8","768001D5","768001d4","768001D0","768001C7","768001bb","768001B2","768001AE","768001A7","768001A6","7680019D","76800197","7680018D","76800175","76800174","76800172","7680016B","76800168","76800162","76800154","7680014E","7680014C","76800148","76800135","7680012D","7680012B","76800129","76800128","76800123","7680011E","7680011C","7680011B","76800110","76800107","76800106","76800102","768000FB","768000F3","768000F1","768000EF","768000EE","768000EC","768000EA","768000E9","768000E8","768000E7","768000E5","768000DE","768000DA","768000D9","768000D2","768000D1","768000CB","768000CA","768000C9","768000C7","768000BF","768000BA","768000B0","768000AF","768000AC","768000ab","768000A5","768000A3","7680009F","7680009E","7680009B","76800099","76800096","76800095","7680008F","7680008E","76800089","76800084","7680007F","7680007E","7680007B","76800075","7680006d","76800069","76800065","76800063","76800061","7680005A","76800055","76800054","7680004F","76800041","76800040","7680003C","76800037","76800036","76800034","7680002D","7680002C","76800027","76800023","76800022","7680001E","7680001C","76800017","76800011","76800010","7680000D","7680000C","7680000A"};
		String[] D = {"768104FF","7681041B","7681041D","76800293","7681005B","7681009E","76800030","76810126","768103BD","76810026","76810048","76800109","76810261","76810062","768101E5","7681016F","7681002B","7681010C","768104C5","7681004A","76810409","7681048A","768100E4","7681048C","768102E1","76810132","7680015F"};
		List<String> A = new ArrayList<String>();
		for (int i=0;i<C.length;i++){
			A.add(C[i]);
		}
		List<String> B = new ArrayList<String>();
		for (int i=0;i<D.length;i++){
			B.add(D[i]);
		}
		Indexes commons = Utils.getCommons(A, B);*/
	}
	
	
	
	public static void  updateOffSmokeList(String repeater,String deviceType,String smokeMac,String oldRepeaterString){
		try{
			//若主机是新加的，添加到redis
			Jedis jedis = RedisConnection.getJedis();
			if (jedis!=null) {
				String requestId = UUID.randomUUID().toString().replace("-", "");
				
				if (!deviceType.equals("5")){
					if (!RedisOps.exist(jedis,"R"+repeater)) {
						Repeater rep  = new Repeater();
						rep.setNetState(0);
						rep.setHeartime(0);
						rep.setRepeaterMac(repeater);
						rep.setPowerChangeTime(0);
						rep.setPowerState(0);
						RedisOps.setObject(jedis,"R"+repeater, rep);
						
						List<String> offMacs = new ArrayList<String>();
						offMacs.add(smokeMac);
						RedisOps.setList(jedis,repeater, offMacs);
					} else {
						//对旧的主机下离线列表进行处理
						if (StringUtils.isNotBlank(oldRepeaterString)&&!repeater.equals(oldRepeaterString)){
							while(!RedisOps.tryGetDistributedLock(jedis, "L"+oldRepeaterString, requestId, 10000)){
								try {
									Thread.currentThread().sleep(300);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							List<String> oldOffMacs = RedisOps.getList(jedis,oldRepeaterString);
							Iterator<String> it = oldOffMacs.iterator();
							boolean changed = false;
							while(it.hasNext()){
								if (it.next().equals(smokeMac)){
									it.remove();
									changed = true;
									break;
								}
							}
							if (changed){
								RedisOps.setList(jedis,oldRepeaterString,oldOffMacs);
							}
							RedisOps.releaseDistributedLock(jedis, "L"+oldRepeaterString, requestId);
						}
						
						//对新主机下离线列表进行处理
						while(!RedisOps.tryGetDistributedLock(jedis, "L"+repeater, requestId, 10000)){
							try {
								Thread.currentThread().sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						List<String> newOffMacs = RedisOps.getList(jedis,repeater);
						Iterator<String> it = newOffMacs.iterator();
						boolean changed = true;
						while(it.hasNext()){
							if (it.next().equals(smokeMac)){
								changed = false;
								break;
							}
						}
						if (changed){
							newOffMacs.add(smokeMac);
							RedisOps.setList(jedis,repeater,newOffMacs);
						}
						RedisOps.releaseDistributedLock(jedis, "L"+repeater, requestId);
						
					}
				}
				
				jedis.close();
			}
				
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	//获取内存里面的设备离线列表
//	@SuppressWarnings("unchecked")
//	public static List<String> getOffMacs(String repeaterMac){
//		
//		Map<String, Long> map = SmokeMap.newInstance().getSmokeMap().get(repeaterMac);
//		if (map==null||map.isEmpty()){
//			return null;
//		}
//		
//		
//		 return new ArrayList<String>(map.keySet());
//	}
	
}
