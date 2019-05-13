package com.cloudfire.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.entity.Repeater;
import com.cloudfire.until.GetTime;

import redis.clients.jedis.Jedis;

public class RedisOps {
	private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    
    private static final Long RELEASE_SUCCESS = 1L;
	
    //获取锁
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {

        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime); //时间单位为ms

        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }
    
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }
	
	public static boolean exist(Jedis jedis,String key){
		return jedis.exists(key);
	}
	
	public static void set(Jedis jedis,String key,String value){
        jedis.set(key, value);
    }
    public static String get(Jedis jedis,String key){
        String value = jedis.get(key);
        return value;
    }
    public static void setObject(Jedis jedis,String key,Object object){
        try {
        	ByteArrayOutputStream bos =  new ByteArrayOutputStream();
			ObjectOutputStream oos =  new ObjectOutputStream(bos);
			oos.writeObject(object);
			byte[] byteArray = bos.toByteArray();
			bos.close();
			oos.close();
			jedis.set(key.getBytes(), byteArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public static Object getObject(Jedis jedis,String key){
        Object object = null;
        byte[] bytes = jedis.get(key.getBytes());
        try {
        	if(bytes == null){
        		return null;
        	}
        	ByteArrayInputStream bis =  new ByteArrayInputStream(bytes);
			ObjectInputStream inputStream =  new ObjectInputStream(bis);
			bis.close();
			inputStream.close();
			object = inputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return object;
    }
    public static void setList(Jedis jedis,String name,List<String> macs){
    	jedis.del(name);
    	if (!macs.isEmpty()){
	    	 for (String mac : macs) {
	    		 jedis.lpush(name, mac);
	 		}
    	}
    }
	public static List<String> getList(Jedis jedis,String name){
    	 List<String> lst = jedis.lrange(name, 0, -1);
    	 return lst;
    }
    public static Set<String> getRepeaters(Jedis jedis) {
    	Set<String> keys = jedis.keys("R*");
    	return keys;
    }
    public static void delKey(Jedis jedis,String key) {
    	jedis.del(key);
    }
    public static void clearRedis(Jedis jedis){
    	Set<String> repeaters = jedis.keys("*");
    	Iterator<String> iterator = repeaters.iterator();
    	while(iterator.hasNext()){
    		jedis.del(iterator.next());
    	}
    }
    
    public static void main(String[] args) {
    	Jedis jedis = RedisConnection.getJedis();
    	Set<String> repeaters = jedis.keys("R*");
    	Iterator<String> iterator = repeaters.iterator();
    	while(iterator.hasNext()){
    		String key = iterator.next();
    		if (key.length() != 9){
    			jedis.del(key);
    			jedis.del(key.substring(1));
    		}
    	}
//    	String requestId = UUID.randomUUID().toString().replace("-", "");
//    	System.out.println(tryGetDistributedLock(jedis, "L8657255596852", requestId, 30000));
//    	System.out.println(tryGetDistributedLock(jedis, "L8657255596852", requestId, 30000));
//    	System.out.println(jedis.get("tag"));
//    	String set = jedis.getSet("lock", "2018-12-25 2");
//    	Long setnx = jedis.setnx("lock", "1");
//    	if (setnx == 1){//获取到了锁
//    		jedis.set("tag", "89");
//    		jedis.del("tag");
//    	} else {
//    		
//    	}
    	
    	
    	
//    	Set<String> repeaters = jedis.keys("*");
//    	Iterator<String> iterator = repeaters.iterator();
//    	while(iterator.hasNext()){
//    		String key = iterator.next();
//    		if (StringUtils.isNotBlank(key)){
//	    		if (key.charAt(0)=='R'){
//	    			Object object = getObject(key);
//	    			if (object!=null){
//	    				Repeater rep = (Repeater) object;
//	    				rep.setNetState(1);
//	    				setObject(key, rep);
//	    			}
//	    		} else {
//	    			jedis.del(key);
//	    		}
//	    	}
//    	}
//    	jedis.close();
    	
//    	
//    	Set<String> repeaters = RedisOps.getRepeaters(jedis);
//		Iterator<String> it = repeaters.iterator();
//		while(it.hasNext()){
//			String repMac = it.next();
//			Object obj  = null;
//			try{
//				obj = RedisOps.getObject(jedis,repMac);
//				Repeater rep  = (Repeater) obj;
//				System.out.println(rep.getRepeaterMac()+"->"+rep.getNetState()+":"+GetTime.getTimeByLong(rep.getHeartime()));
//			}catch( Exception e){
//				System.out.println(repMac);
//			}
//			
//		}
		
//    	Repeater rep = new Repeater();
//    	rep.setRepeaterMac("R02051720");
////    	rep.setNetState(1);
//    	rep.setHeartime(System.currentTimeMillis()-24*60*1000);
//    	setObject(jedis,"R02051720", rep);
    	
    	
//    	SmokeLineDao mSmokeLineDao = new SmokeLineDaoImpl();
//    	Set<String> repeaters = RedisOps.getRepeaters();
//		for(String rep:repeaters){
//			try{
//				Repeater rep2 = (Repeater) RedisOps.getObject(rep);
//				if (rep.equals("R00000000")) {
//					System.out.println("jh");
//				}
			
//				if (rep2.getNetState() == 1 && System.currentTimeMillis() - rep2.getHeartime() >14 * 60 * 1000 ) {
//					//更新设备状态
//					AreaDao areaDaoImpl=new AreaDaoImpl();
//					int areaid=areaDaoImpl.getAreaIdByRepeater(rep.substring(1));
//					if(areaid!=27&&areaid!=34&&areaid!=2052){//(27,34,2052)
//						mSmokeLineDao.RepeaterOffLine(rep.substring(1),0); //更新设备的在线，离线状态
//					}
//					
//					rep2.setNetState(0);
//					RedisOps.setObject(rep,rep2); //存入主机状态
//					RedisOps.setList(rep.substring(1), SmokeLineDaoImpl.getMacs(rep.substring(1))); //存入主机下所有设备到离线列表
//					
//				}
//			}catch (Exception e){
//				System.out.println(rep);
//			}
//		}
//    	
    	
    	//更新主机状态
//    	Object object = getObject(jedis,"R06111620");
//    	Repeater rep2= null;
//    	if (object!=null){
//    		rep2 =(Repeater) object;
//    	}
//    	rep2.setNetState(1);
//    	rep2.setHeartime(GetTime.getTimeByString("2018-12-19 16:00:00"));
//    	RedisOps.setObject(jedis,"R06111620", rep2);
//    	Repeater rep3=(Repeater) RedisOps.getObject("R16061740");
//    	System.out.println(rep3.getRepeaterMac()+"->"+rep3.getNetState()+":"+GetTime.getTimeByLong(rep3.getHeartime()));
    	
//    	String s ="I am superman";
//    	System.out.println(s.substring(1));
    	
    	/*清楚所有的key*/
//    	Jedis jedis = RedisConnection.getJedis();
//    	Set<String> repeaters = jedis.keys("*");
//    	Iterator<String> iterator = repeaters.iterator();
//    	while(iterator.hasNext()){
//    		jedis.del(iterator.next());
//    	}
//    	jedis.close();
    	
//    	Jedis jedis = RedisConnection.getJedis();
//    	String key = "66031720";
//    	String strings ="566";
//    	jedis.lpush(key, strings);
//    	jedis.lpush(key, "966");
//    	System.out.println(jedis.lrange(key,0,-1));
//    	List<String> macs = new ArrayList<String>();
//    	macs.add("45");
//    	macs.add("45");
//    	setList("123", macs);
//    	List<String> s = getList("26061740");
//    	System.out.println(s);
//    	if(str == null)
    	
//    	jedis.del( "listDemo" );
//
//    	System. out .println(jedis.lrange( "listDemo" , 0, -1));
//
//    	jedis.lpush( "listDemo" ,  "A" );
//
//    	jedis.lpush( "listDemo" ,  "B" );
//
//    	jedis.lpush( "listDemo" ,  "C" );
//
//    	System. out .println(jedis.lrange( "listDemo" , 0, -1));
//
//    	System. out .println(jedis.lrange( "listDemo" , 0, 1));
    	
	}
	public static void delKey(byte[] bytes) {
		Jedis jedis = RedisConnection.getJedis();
    	jedis.del(bytes);
    	jedis.close();
	}
}
