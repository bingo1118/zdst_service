package com.cloudfire.db;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnection {
    private static String HOST =SystemConfig.getConfigInfomation("REDIS_HOST");// "139.159.226.232" ;//"139.199.58.208";// "193.112.215.201"; //
    private static int PORT = Integer.parseInt(SystemConfig.getConfigInfomation("REDIS_PORT"));//9379;//  6379;//6971;//
    private static String PASSWORD = SystemConfig.getConfigInfomation("REDIS_PASSWORD");
    private static int MAX_ACTIVE = 5000;
    private static int MAX_IDLE = 100;
    private static int MAX_WAIT = 1000;
    
    private static JedisPool jedisPool = null;
    /*
     * ��ʼ��redis���ӳ�
     * */
    private static void initPool(){
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);//���������
            config.setMaxIdle(MAX_IDLE);//������������
            config.setMaxWaitMillis(MAX_WAIT);//��ȡ�������ӵ����ȴ�ʱ��ms

            jedisPool = new JedisPool(config, HOST, PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * ��ȡjedisʵ��
     * */
    public synchronized static Jedis getJedis() {
    	Jedis jedis = null;
        try {
            if(jedisPool == null){
                initPool();
            }
            jedis = jedisPool.getResource();
            if (StringUtils.isNotBlank(PASSWORD)) {
            	jedis.auth(PASSWORD);//����
            }
            return jedis;
        } catch (Exception e) {
            e.printStackTrace();
            if (jedis != null){
            	jedis.close();
            }
            return null;
        }
    }
}
